package io.charg.chargstation.ui.activities.nodeServiceActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;
import io.charg.chargstation.R;
import io.charg.chargstation.root.CommonData;
import io.charg.chargstation.services.local.FavouriteStationsRepository;
import io.charg.chargstation.services.local.LogService;
import io.charg.chargstation.services.remote.api.ApiProvider;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.BestSellOrderDto;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.ConfirmPaymentResponseDto;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.IChargCoinServiceApi;
import io.charg.chargstation.services.remote.api.chargCoinServiceApi.NodeDto;
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

    @BindView(R.id.et_amount)
    EditText mEtAmount;

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

    @BindView(R.id.rgrp_service_type)
    RadioGroup mRgrpServiceType;

    @BindView(R.id.rbtn_charging)
    RadioButton mRbtnCharging;

    @BindView(R.id.rbtn_parking)
    RadioButton mRbtnParking;

    @BindView(R.id.rbtn_wifi)
    RadioButton mRbtnWifi;

    @BindView(R.id.tv_charging_asset)
    TextView mTvChargingAsset;

    @BindView(R.id.tv_parking_asset)
    TextView mTvParkingAsset;

    @BindView(R.id.tv_wifi_asset)
    TextView mTvWifiAsset;

    @BindView(R.id.tv_calc_time)
    TextView mTvCalcTime;

    private IChargCoinServiceApi mChargCoinServiceApi;
    private FavouriteStationsRepository mFavouriteService;

    private int mUsdAmount;
    private String mPayerId = "uk0505";
    private String mNodeEthAddress;
    private String mSellOrderHash;
    private String mPaymentHash;
    private String mPaymentNonce;

    private AlertDialog mLoadingDialog;
    private MenuItem mMenuFavourite;

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
        loadNodeStatus();
        loadBestSellOrder();
    }

    private void loadNodeStatus() {
        mChargCoinServiceApi.getNodeStatus(mNodeEthAddress).enqueue(new Callback<NodeDto>() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onResponse(@NonNull Call<NodeDto> call, @NonNull Response<NodeDto> response) {
                LogService.info(new Gson().toJson(response.body()));

                NodeDto body = response.body();
                if (body == null) {
                    Snackbar.make(mToolbar, R.string.error_loading_data, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                mTvChargingAsset.setText(String.format(
                        "Free: %d of %d\r\n%.3f CHG/sec",
                        body.getChargingAsset().Free,
                        body.getChargingAsset().Slots,
                        body.getChargingAsset().Rate / 1E18
                ));

                mTvParkingAsset.setText(String.format(
                        "Free: %d of %d\r\n%.3f CHG/sec",
                        body.getParkingAsset().Free,
                        body.getParkingAsset().Slots,
                        body.getParkingAsset().Rate / 1E18
                ));

                mTvWifiAsset.setText(String.format(
                        "Free: %d of %d\r\n%.3f CHG/sec",
                        body.getWifiAsset().Free,
                        body.getWifiAsset().Slots,
                        body.getWifiAsset().Rate / 1E18
                ));

            }

            @Override
            public void onFailure(@NonNull Call<NodeDto> call, @NonNull Throwable t) {
                Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void initServices() {
        mChargCoinServiceApi = ApiProvider.getChargCoinServiceApi(this);
        mFavouriteService = new FavouriteStationsRepository(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenuFavourite = menu.add(null);
        mMenuFavourite.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        mMenuFavourite.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (mFavouriteService.isFavourite(mNodeEthAddress)) {
                    mFavouriteService.removeFromFavorites(mNodeEthAddress);
                } else {
                    mFavouriteService.addToFavorites(mNodeEthAddress);
                }
                refreshMenu();
                return false;
            }
        });
        refreshMenu();

        return super.onCreateOptionsMenu(menu);
    }

    private void refreshMenu() {
        if (mMenuFavourite == null) {
            return;
        }
        mMenuFavourite.setIcon(mFavouriteService.isFavourite(mNodeEthAddress)
                ? R.drawable.ic_favorite
                : R.drawable.ic_favorite_border);
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

    @OnClick(R.id.btn_calc_time)
    public void onBtnCalcTimeClicked() {
        int amount = Integer.parseInt(mEtAmount.getText().toString());

        int time = (int) (amount / (0.004 * 231 * 0.00115));
        mTvCalcTime.setText(time + " sec");

    }

    @OnClick(R.id.btn_payment_credit_card)
    public void onBtnPayCreditCardClicked() {
        showLoading("Initializing payment...");

        mUsdAmount = Integer.parseInt(mEtAmount.getText().toString());

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

    @OnClick(R.id.btn_service_start)
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

                ServiceStatusDto serviceStatus = response.body();
                showServiceStatus(serviceStatus);
            }

            @Override
            public void onFailure(@NonNull Call<ServiceStatusDto> call, @NonNull Throwable t) {
                Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                refreshUI();
            }
        });
    }

    @OnClick(R.id.btn_service_stop)
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

                ServiceStatusDto serviceStatus = response.body();
                showServiceStatus(serviceStatus);
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

                ServiceStatusDto serviceStatus = response.body();
                showServiceStatus(serviceStatus);
            }

            @Override
            public void onFailure(@NonNull Call<ServiceStatusDto> call, @NonNull Throwable t) {
                Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                refreshUI();
            }
        });
    }

    private void showServiceStatus(ServiceStatusDto serviceStatus) {

        loadNodeStatus();

        if (serviceStatus.Error) {
            Snackbar.make(mToolbar, R.string.error_service_wifi_on, Snackbar.LENGTH_SHORT).show();
            Snackbar.make(mToolbar, serviceStatus.Result.Error, Snackbar.LENGTH_SHORT).show();
            return;
        }

        mTvServiceStartedAt.setText(String.valueOf(serviceStatus.StartedAt));
        mTvServiceStoppedAt.setText(String.valueOf(serviceStatus.StoppedAt));
        mTvServiceTime.setText(String.valueOf(serviceStatus.TimeElapsed));
        mTvServiceRemained.setText(String.valueOf(serviceStatus.Remaind));
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
        ApiProvider.getChargCoinServiceApi(this).getBestSellOrder(1000).enqueue(new Callback<BestSellOrderDto>() {
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

        int serviceId = -1;

        if (mRgrpServiceType.getCheckedRadioButtonId() == mRbtnCharging.getId()) {
            serviceId = 0;
        } else if (mRgrpServiceType.getCheckedRadioButtonId() == mRbtnParking.getId()) {
            serviceId = 1;
        } else if (mRgrpServiceType.getCheckedRadioButtonId() == mRbtnWifi.getId()) {
            serviceId = 2;
        }

        mChargCoinServiceApi.postConfirmPayment(
                serviceId,
                "USD",
                mNodeEthAddress,
                mSellOrderHash,
                mUsdAmount,
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
