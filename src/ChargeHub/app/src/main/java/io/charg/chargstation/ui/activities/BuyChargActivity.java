package io.charg.chargstation.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.GooglePaymentRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.root.ICallbackOnComplete;
import io.charg.chargstation.services.remote.api.socketio.PaymentDataRequestDto;
import io.charg.chargstation.services.remote.api.socketio.SellOrderResponseDto;
import io.charg.chargstation.services.remote.api.socketio.SocketIOProvider;
import io.charg.chargstation.services.remote.api.socketio.BraintreeTokenDto;
import io.charg.chargstation.services.remote.api.wecharg.CartItemRequestDto;
import io.charg.chargstation.services.remote.api.wecharg.CartItemResponseDto;
import io.charg.chargstation.services.remote.api.wecharg.PaymentInformationDto;
import io.charg.chargstation.services.remote.api.wecharg.TokenRequestDto;
import io.charg.chargstation.services.remote.api.wecharg.WeChargApi;
import io.charg.chargstation.services.remote.api.wecharg.WeChargProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyChargActivity extends BaseAuthActivity {

    private static final int REQUEST_CODE_BRAINTREE = 1001;

    @BindView(R.id.et_amount_chg)
    EditText mEtAmountChg;

    @BindView(R.id.tv_final_price)
    TextView mTvFinalPrice;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_result)
    TextView mTvResult;

    private WeChargApi mWeChargApi;
    private SocketIOProvider mSocketsIO;

    private String mClientToken;
    private String mQuoteId;
    private String mBraintreeToken;
    private String mOrderId;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public int getResourceId() {
        return R.layout.activity_buy_charg;
    }

    @Override
    public void onActivate() {
        initServices();
        initToolbar();
        initEtAmountChg();
    }

    private void initServices() {
        mWeChargApi = WeChargProvider.getWeChargApi();
        mSocketsIO = SocketIOProvider.getInstance();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initEtAmountChg() {
        mEtAmountChg.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                refreshFinalPrice();

                if (mEtAmountChg.getText().toString().isEmpty()) {
                    mEtAmountChg.setError("Amount cannot be empty");
                }

                return false;
            }
        });
    }

    private void refreshFinalPrice() {
        try {
            int amount = Integer.parseInt(mEtAmountChg.getText().toString());
            float finalPrice = amount * CommonData.PRICE_CHG_USD;

            mTvFinalPrice.setText(String.valueOf(finalPrice));
        } catch (Exception ex) {
            mTvFinalPrice.setText("");
        }
    }

    @OnClick(R.id.btn_credit)
    void onBtnCreditClicked() {
        executeBuy();

    }

    public void executeBuy() {

        mSocketsIO.getBraintreeTokenAsync(new ICallbackOnComplete<BraintreeTokenDto>() {
            @Override
            public void onComplete(BraintreeTokenDto result) {

                DropInRequest dropInRequest = new DropInRequest()
                        .clientToken(result.Token);

                startActivityForResult(dropInRequest.getIntent(BuyChargActivity.this), REQUEST_CODE_BRAINTREE);
            }
        });

    }

    private void useMagento() {
        mWeChargApi.getTokenAsync(new TokenRequestDto("jdoe@example.com", "Password1"))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        final String clientToken = response.body();
                        System.out.println("token client " + clientToken);

                        if (clientToken == null || clientToken.isEmpty()) {
                            Snackbar.make(mToolbar, R.string.invalid_token, Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        mClientToken = clientToken;
                        createQuoteAsync();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void createQuoteAsync() {
        mWeChargApi.createQuoteAsync("bearer " + mClientToken).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                final String quoteId = response.body();
                System.out.println("quote " + quoteId);

                if (quoteId == null || quoteId.isEmpty()) {
                    Snackbar.make(mToolbar, R.string.eror_create_quote, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                mQuoteId = quoteId;
                addItemToQuoteAsync();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void addItemToQuoteAsync() {
        mWeChargApi.addItemToCart("bearer " + mClientToken, new CartItemRequestDto(mQuoteId)).enqueue(new Callback<CartItemResponseDto>() {
            @Override
            public void onResponse(Call<CartItemResponseDto> call, Response<CartItemResponseDto> response) {
                CartItemResponseDto cart = response.body();
                if (cart == null) {
                    Snackbar.make(mToolbar, R.string.erro_add_item_to_cart, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                System.out.println("cart " + cart.toString());
                generateBraintreeTokenAsync();
            }

            @Override
            public void onFailure(Call<CartItemResponseDto> call, Throwable t) {
                Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void generateBraintreeTokenAsync() {
        mWeChargApi.getBrainTreeTokenAsync("bearer " + mClientToken).enqueue(new Callback<BraintreeTokenDto>() {
            @Override
            public void onResponse(Call<BraintreeTokenDto> call, Response<BraintreeTokenDto> response) {
                BraintreeTokenDto body = response.body();
                if (body == null || !body.Success) {
                    Snackbar.make(mToolbar, R.string.error_generating_token_braintree, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                mBraintreeToken = body.Token;
                DropInRequest dropInRequest = new DropInRequest()
                        .clientToken(mBraintreeToken);

                startActivityForResult(dropInRequest.getIntent(BuyChargActivity.this), REQUEST_CODE_BRAINTREE);
            }

            @Override
            public void onFailure(Call<BraintreeTokenDto> call, Throwable t) {
                Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BRAINTREE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                if (nonce == null) {
                    mTvResult.setText(R.string.cancel_user);
                    return;
                }

                Log.i(CommonData.TAG, "nonce:" + nonce.getNonce());

                exchangeUSDtoCHG(nonce.getNonce());

//                createOrderAsyncMagento();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mTvResult.setText(R.string.cancel_user);
            } else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                mTvResult.setText(error.getMessage());
            }
        }
    }

    private void exchangeUSDtoCHG(final String nonce) {

        mSocketsIO.getBestSellOrderAsync(new ICallbackOnComplete<SellOrderResponseDto>() {
            @Override
            public void onComplete(SellOrderResponseDto sellOrder) {

                PaymentDataRequestDto paymentData = new PaymentDataRequestDto(
                        "0x1aa494ff7a493e0ba002e2d38650d4d21bd5591b",
                        "0x55e93bce34504f89a7966be0aadfefb8cffd5903580b6859b3093c6efdb598d0",
                        1.76f,
                        nonce);

                mSocketsIO.payBraintreeAsync(paymentData);
            }
        });

    }

    private void createOrderAsyncMagento(String nonce) {
        PaymentInformationDto payment = new PaymentInformationDto(mQuoteId);
        payment.PaymentMethod.Method = "braintree";
        payment.PaymentMethod.AdditionalData.Nonce = nonce;

        mWeChargApi.createOrderAsync("bearer " + mClientToken, payment).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                final String orderId = response.body();
                if (orderId == null || orderId.isEmpty()) {
                    Snackbar.make(mToolbar, R.string.error_while_creating_order, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                System.out.println("Order: " + orderId);
                mOrderId = orderId;

                mTvResult.setText("Order: " + mOrderId);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                mTvResult.setText(t.getMessage());
                Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
