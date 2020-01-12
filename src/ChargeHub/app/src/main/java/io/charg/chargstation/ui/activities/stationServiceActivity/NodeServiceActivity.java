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
import io.charg.chargstation.ui.activities.BaseActivity;
import io.charg.chargstation.ui.activities.BuyChargActivity;
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

    @BindView(R.id.tv_payment_status)
    TextView mTvPaymentStatus;

    private IChargCoinServiceApi mChargCoinServiceApi;

    private String mNodeEthAddress;
    private boolean mPaymentStatus;
    private String mSellOrderHash;

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
        mTvPaymentStatus.setText(mPaymentStatus ? "Confirmed" : "Not confirmed");
    }

    @OnClick(R.id.btn_payment_credit_card)
    void onBtnPayCreditCardClicked() {
        showLoading();

        mChargCoinServiceApi.getPaymentData("USD")
                .enqueue(new Callback<PaymentDataDto>() {
                    @Override
                    public void onResponse(@NonNull Call<PaymentDataDto> call, @NonNull Response<PaymentDataDto> response) {
                        if (response.body() == null) {
                            Snackbar.make(mToolbar, R.string.error_loading_payment_token, Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        PaymentDataDto.PaymentDataContentDto body = response.body().PaymentData;
                        if (body == null) {
                            return;
                        }

                        if (!body.Success) {
                            return;
                        }

                        performPayment(body.ClientToken);
                    }

                    @Override
                    public void onFailure(@NonNull Call<PaymentDataDto> call, @NonNull Throwable t) {
                        Snackbar.make(mToolbar, t.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoading() {

        if (mLoadingDialog == null) {
            mLoadingDialog = new AlertDialog.Builder(this)
                    .setMessage("Loading data...")
                    .create();
        }

        mLoadingDialog.show();
    }

    private void hideLoading() {
        mLoadingDialog.dismiss();
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

                loadBestSellOrder();

                confirmPayment(nonce.getNonce());

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
                mSellOrderHash = orderContent.BestSellOrder.Hash;
            }

            @Override
            public void onFailure(@NonNull Call<BestSellOrderDto> call, @NonNull Throwable t) {
                Toast.makeText(NodeServiceActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmPayment(final String nonce) {
        mChargCoinServiceApi.postConfirmPayment(
                "USD",
                mNodeEthAddress,
                mSellOrderHash,
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
