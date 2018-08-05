package io.charg.chargstation.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.services.remote.api.wecharg.BrainTreeResponseDto;
import io.charg.chargstation.services.remote.api.wecharg.BraintreeTokenResponse;
import io.charg.chargstation.services.remote.api.wecharg.BraintreeTransactionRequest;
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

    private String mClientToken;
    private String mQuoteId;
    private String mBraintreeToken;
    private String mNonce;
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
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        mWeChargApi.getBrainTreeTokenAsync("bearer " + mClientToken).enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                List<Object> body = response.body();
                if (body == null || body.size() < 2) {
                    Snackbar.make(mToolbar, R.string.error_generating_token_braintree, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                mBraintreeToken = body.get(1).toString();
                DropInRequest dropInRequest = new DropInRequest()
                        .clientToken(mBraintreeToken);

                startActivityForResult(dropInRequest.getIntent(BuyChargActivity.this), REQUEST_CODE_BRAINTREE);
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {
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

                mNonce = nonce.getNonce();
                createOrderAsync();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                mTvResult.setText(R.string.cancel_user);
            } else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                mTvResult.setText(error.getMessage());
            }
        }
    }

    private void createOrderAsync() {
        PaymentInformationDto payment = new PaymentInformationDto(mQuoteId);
        payment.PaymentMethod.Method = "braintree";
        payment.PaymentMethod.AdditionalData.Nonce = mNonce;

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
