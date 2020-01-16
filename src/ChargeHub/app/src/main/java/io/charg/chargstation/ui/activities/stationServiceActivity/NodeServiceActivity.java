package io.charg.chargstation.ui.activities.stationServiceActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

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
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.ServiceStatusDto;
import io.charg.chargstation.ui.activities.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NodeServiceActivity extends BaseActivity {

    public static final String EXTRA_NODE_ADDRESS = "EXTRA_NODE_ADDRESS";
    private static final int REQUEST_CODE_BRAINTREE = 1001;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_node_eth_address)
    TextView mTvNodeEthAddress;

    @BindView(R.id.tv_payment_hash)
    TextView mTvPaymentStatus;

    @BindView(R.id.tv_sell_order_hash)
    TextView mTvSellOrderStatus;

    @BindView(R.id.tv_service_started_at)
    TextView mTvServiceStartedAt;

    @BindView(R.id.tv_service_stopped_at)
    TextView mTvServiceStoppedAt;

    @BindView(R.id.tv_service_remained)
    TextView mTvServiceRemained;

    @BindView(R.id.tv_service_time_at)
    TextView mTvServiceTime;

    private IChargCoinServiceApi mChargCoinServiceApi;

    private String mPayerId = "uk0505";
    private String mNodeEthAddress;
    private String mSellOrderHash;
    private String mPaymentHash;
    private String mPaymentNonce;

    private AlertDialog mLoadingDialog;

    @Override
    public int getResourceId() {
        return R.layout.activity_node_service;
    }

    @Override
    public void onActivate() {
        initServices();
        readIntent();
        initToolbar();
        refreshUI();
        loadBestSellOrder();
    }

    private void initServices() {
        mChargCoinServiceApi = ApiProvider.getChargCoinServiceApi();
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

    private void readIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mNodeEthAddress = intent.getStringExtra(EXTRA_NODE_ADDRESS);
        }
    }

    private void refreshUI() {
        mTvNodeEthAddress.setText(mNodeEthAddress);
        mTvSellOrderStatus.setText(mSellOrderHash);
        mTvPaymentStatus.setText(mPaymentHash);
    }

    @OnClick(R.id.btn_payment_credit_card)
    void onBtnPayCreditCardClicked() {
        showLoading("Initializing payment...");

        mChargCoinServiceApi.getPaymentData("USD")
                .enqueue(new Callback<PaymentDataDto>() {
                    @Override
                    public void onResponse(@NonNull Call<PaymentDataDto> call, @NonNull Response<PaymentDataDto> response) {
                        if (response.body() == null) {
                            Snackbar.make(mToolbar, R.string.error_loading_payment_token, Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        PaymentDataDto.PaymentDataContentDto content = response.body().PaymentData;
                        if (content == null) {
                            Snackbar.make(mToolbar, R.string.error_loading_payment_token, Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        if (!content.Success) {
                            Snackbar.make(mToolbar, R.string.error_loading_payment_token, Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        performPayment(content.ClientToken);
                    }

                    @Override
                    public void onFailure(@NonNull Call<PaymentDataDto> call, @NonNull Throwable t) {
                        Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick(R.id.btn_start_wifi)
    void onBtnWifiStartClicked() {

        if (TextUtils.isEmpty(mPaymentHash)) {
            Snackbar.make(mToolbar, "Payment is not confirmed. TxHash required", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mPaymentNonce)) {
            Snackbar.make(mToolbar, "Payment is not confirmed. PaymentId required", Snackbar.LENGTH_SHORT).show();
            return;
        }

        mChargCoinServiceApi.postServiceOn(mPayerId, mPaymentHash, mPaymentNonce).enqueue(new Callback<ServiceStatusDto>() {
            @Override
            public void onResponse(@NonNull Call<ServiceStatusDto> call, @NonNull Response<ServiceStatusDto> response) {

                if (response.errorBody() != null) {
                    try {
                        String json = response.errorBody().string();
                        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                        String error = jsonObject.get("error").getAsString();
                        Snackbar.make(mToolbar, error, Snackbar.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (response.body() == null) {
                    Snackbar.make(mToolbar, R.string.error_loading_payment_token, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                LogService.info("Wifi start response: " + response.body());

                ServiceStatusDto body = response.body();
                if (body.Error) {
                    Snackbar.make(mToolbar, R.string.error_service_wifi_on, Snackbar.LENGTH_SHORT).show();
                    Snackbar.make(mToolbar, body.Result.Error, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Snackbar.make(mToolbar, body.toString(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ServiceStatusDto> call, @NonNull Throwable t) {
                Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                refreshUI();
            }
        });
    }

    @OnClick(R.id.btn_stop_wifi)
    void onBtnWifiStopClicked() {
        if (TextUtils.isEmpty(mPaymentHash)) {
            Snackbar.make(mToolbar, "Payment is not confirmed. TxHash required", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mPaymentNonce)) {
            Snackbar.make(mToolbar, "Payment is not confirmed. PaymentId required", Snackbar.LENGTH_SHORT).show();
            return;
        }

        mChargCoinServiceApi.postServiceOff(mPayerId, mPaymentHash, mPaymentNonce).enqueue(new Callback<ServiceStatusDto>() {
            @Override
            public void onResponse(@NonNull Call<ServiceStatusDto> call, @NonNull Response<ServiceStatusDto> response) {

                if (response.errorBody() != null) {
                    try {
                        String json = response.errorBody().string();
                        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                        String error = jsonObject.get("error").getAsString();
                        Snackbar.make(mToolbar, error, Snackbar.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (response.body() == null) {
                    Snackbar.make(mToolbar, R.string.error_loading_payment_token, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                LogService.info("Wifi stop response: " + response.body());

                ServiceStatusDto body = response.body();
                if (body.Error) {
                    Snackbar.make(mToolbar, R.string.error_service_wifi_on, Snackbar.LENGTH_SHORT).show();
                    Snackbar.make(mToolbar, body.Result.Error, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Snackbar.make(mToolbar, body.toString(), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ServiceStatusDto> call, @NonNull Throwable t) {
                Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                refreshUI();
            }
        });
    }

    @OnClick(R.id.btn_service_status)
    void onBtnServiceStatusClicked() {
        if (TextUtils.isEmpty(mPaymentHash)) {
            Snackbar.make(mToolbar, "Payment is not confirmed. TxHash required", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mPaymentNonce)) {
            Snackbar.make(mToolbar, "Payment is not confirmed. PaymentId required", Snackbar.LENGTH_SHORT).show();
            return;
        }

        mChargCoinServiceApi.getServiceStatus(mPayerId, mPaymentHash, mPaymentNonce).enqueue(new Callback<ServiceStatusDto>() {
            @Override
            public void onResponse(@NonNull Call<ServiceStatusDto> call, @NonNull Response<ServiceStatusDto> response) {

                if (response.errorBody() != null) {
                    try {
                        String json = response.errorBody().string();
                        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                        String error = jsonObject.get("error").getAsString();
                        Snackbar.make(mToolbar, error, Snackbar.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (response.body() == null) {
                    Snackbar.make(mToolbar, R.string.error_loading_payment_token, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                LogService.info("Wifi stop response: " + response.body());

                ServiceStatusDto body = response.body();
                if (body.Error) {
                    Snackbar.make(mToolbar, R.string.error_service_wifi_on, Snackbar.LENGTH_SHORT).show();
                    Snackbar.make(mToolbar, body.Result.Error, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                mTvServiceStartedAt.setText(String.valueOf(body.StartedAt));
                mTvServiceStoppedAt.setText(String.valueOf(body.StoppedAt));
                mTvServiceTime.setText(String.valueOf(body.TimeElapsed));
                mTvServiceRemained.setText(String.valueOf(body.Remaind));
            }

            @Override
            public void onFailure(@NonNull Call<ServiceStatusDto> call, @NonNull Throwable t) {
                Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                refreshUI();
            }
        });
    }

    private void showLoading(String message) {

        if (mLoadingDialog == null) {
            mLoadingDialog = new AlertDialog.Builder(this)
                    .setMessage(message)
                    .create();
        }

        mLoadingDialog.show();
    }

    private void hideLoading() {
        mLoadingDialog.dismiss();
        mLoadingDialog = null;
    }

    private void performPayment(String brainTreeClientToken) {
        hideLoading();

        DropInRequest dropInRequest = new DropInRequest()
                .disablePayPal()
                .clientToken(brainTreeClientToken);

        startActivityForResult(dropInRequest.getIntent(NodeServiceActivity.this), REQUEST_CODE_BRAINTREE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        handleBraintree(requestCode, resultCode, data);
    }

    private void handleBraintree(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BRAINTREE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                if (nonce == null) {
                    mTvPaymentStatus.setText(R.string.cancel_user);
                    return;
                }

                Log.i(CommonData.TAG, "nonce: " + nonce.getNonce());
                mPaymentNonce = nonce.getNonce();

                confirmPayment();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                mTvPaymentStatus.setText(R.string.cancel_user);
            } else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                mTvPaymentStatus.setText(error.getMessage());
            }
        }
    }

    private void loadBestSellOrder() {
        ApiProvider.getChargCoinServiceApi().getBestSellOrder(1000).enqueue(new Callback<BestSellOrderDto>() {
            @Override
            public void onResponse(@NonNull Call<BestSellOrderDto> call, @NonNull Response<BestSellOrderDto> response) {
                BestSellOrderDto orderContent = response.body();
                if (orderContent == null || orderContent.BestSellOrder == null) {
                    Toast.makeText(NodeServiceActivity.this, "Best sell order loading error", Toast.LENGTH_SHORT).show();
                    return;
                }

                LogService.info("SellOrder. " + orderContent.toString());
                mSellOrderHash = orderContent.BestSellOrder.Hash;

                refreshUI();
            }

            @Override
            public void onFailure(@NonNull Call<BestSellOrderDto> call, @NonNull Throwable t) {
                Toast.makeText(NodeServiceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                refreshUI();
            }
        });
    }

    private void confirmPayment() {
        showLoading("Confirm payment...");

        mChargCoinServiceApi.postConfirmPayment(
                2,
                "USD",
                mNodeEthAddress,
                mSellOrderHash,
                1,
                mPaymentNonce,
                mPayerId).enqueue(new Callback<ConfirmPaymentResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<ConfirmPaymentResponseDto> call, @NonNull Response<ConfirmPaymentResponseDto> response) {
                hideLoading();

                if (response.errorBody() != null) {
                    try {
                        Snackbar.make(mToolbar, response.errorBody().string(), Snackbar.LENGTH_SHORT).show();
                        mTvPaymentStatus.setText(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

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
                mPaymentHash = txHash;

                refreshUI();
            }

            @Override
            public void onFailure(@NonNull Call<ConfirmPaymentResponseDto> call, @NonNull Throwable t) {
                hideLoading();

                Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                refreshUI();
            }
        });
    }

}
