package io.charg.chargstation.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;

import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.services.local.LogService;
import io.charg.chargstation.services.remote.api.ApiProvider;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.BestSellOrderDto;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.ConfirmPaymentResponseDto;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.IChargCoinServiceApi;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.PaymentDataDto;
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

    private IChargCoinServiceApi mApi;

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initServices() {
        mApi = ApiProvider.getChargCoinServiceApi(this);
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

        LogService.info("Starting payment");

        mApi.getPaymentData("USD").enqueue(new Callback<PaymentDataDto>() {
            @Override
            public void onResponse(@NonNull Call<PaymentDataDto> call, @NonNull Response<PaymentDataDto> response) {
                if (!response.isSuccessful()) {
                    Snackbar.make(mToolbar, "Response is not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                PaymentDataDto body = response.body();
                if (body == null || body.PaymentData == null) {
                    Snackbar.make(mToolbar, R.string.error_loading_data, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!body.PaymentData.Success) {
                    Snackbar.make(mToolbar, "Reading payment data is not successful", Toast.LENGTH_SHORT).show();
                    return;
                }

                DropInRequest dropInRequest = new DropInRequest()
                        .clientToken(body.PaymentData.ClientToken);

                startActivityForResult(dropInRequest.getIntent(BuyChargActivity.this), REQUEST_CODE_BRAINTREE);
            }

            @Override
            public void onFailure(@NonNull Call<PaymentDataDto> call, @NonNull Throwable t) {
                Snackbar.make(mToolbar, t.getMessage(), Toast.LENGTH_SHORT).show();
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

                Log.i(CommonData.TAG, "nonce: " + nonce.getNonce());

                exchangeUSDtoCHG(nonce.getNonce());

            } else if (resultCode == Activity.RESULT_CANCELED) {
                mTvResult.setText(R.string.cancel_user);
            } else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                mTvResult.setText(error.getMessage());
            }
        }
    }

    private void exchangeUSDtoCHG(final String nonce) {

        ApiProvider.getChargCoinServiceApi(this).getBestSellOrder(1000).enqueue(new Callback<BestSellOrderDto>() {
            @Override
            public void onResponse(Call<BestSellOrderDto> call, Response<BestSellOrderDto> response) {
                BestSellOrderDto orderContent = response.body();
                if (orderContent == null || orderContent.BestSellOrder == null) {
                    Toast.makeText(BuyChargActivity.this, "Best sell order loading error", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(BuyChargActivity.this, orderContent.BestSellOrder.Hash, Toast.LENGTH_SHORT).show();

                confirmPayment(orderContent.BestSellOrder.Hash, nonce);
            }

            @Override
            public void onFailure(Call<BestSellOrderDto> call, Throwable t) {
                Toast.makeText(BuyChargActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmPayment(String hash, String nonce) {
        mApi.postConfirmPayment(
                2,
                "USD",
                "0x1aa494ff7a493e0ba002e2d38650d4d21bd5591b",
                hash,
                5,
                nonce,
                "uk0505")/*
                "0x1aa494ff7a493e0ba002e2d38650d4d21bd5591b",
                "0x3d203f7ad6471c5d11d9ab5e1950130da759c594aa998435d01e36067ac9b7e8",
                5,
                nonce*/
                .enqueue(new Callback<ConfirmPaymentResponseDto>() {
                    @Override
                    public void onResponse(@NonNull Call<ConfirmPaymentResponseDto> call, @NonNull Response<ConfirmPaymentResponseDto> response) {
                        ConfirmPaymentResponseDto body = response.body();
                        if (body == null || body.PaymentResult == null) {
                            Snackbar.make(mToolbar, R.string.error_loading_data, Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        String txHash = body.PaymentResult.TxHash;
                        if (TextUtils.isEmpty(txHash)) {
                            Snackbar.make(mToolbar, R.string.error_loading_data, Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        LogService.info("Payment hash: " + txHash);

                        Snackbar.make(mToolbar, txHash, Snackbar.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(@NonNull Call<ConfirmPaymentResponseDto> call, @NonNull Throwable t) {
                        Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

}
