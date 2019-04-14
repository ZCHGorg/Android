package io.charg.chargstation.ui.activities.chargingCCActivity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import io.charg.chargstation.services.remote.api.wecharg.PaymentDataResponseDto;
import io.charg.chargstation.services.remote.api.wecharg.PaymentResultResponseDto;
import io.charg.chargstation.services.remote.api.wecharg.WeChargApi;
import io.charg.chargstation.services.remote.api.wecharg.WeChargProvider;
import io.charg.chargstation.ui.dialogs.TxWaitDialog;
import io.charg.chargstation.ui.fragments.BaseNavFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreditCardPaymentFrg extends BaseNavFragment {

    private static final int REQUEST_CODE_BRAINTREE = 1001;

    private WeChargApi mWeChargApi;

    private String mTxHash;

    @BindView(R.id.tv_result)
    TextView mTvResult;

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    public boolean canNext() {
        return true;
    }

    @Override
    public boolean isValid() {
        return !TextUtils.isEmpty(mTxHash);
    }

    @Override
    public void invalidate() {

    }

    @Override
    protected int getResourceId() {
        return R.layout.frg_credit_card_payment;
    }

    @Override
    protected void onShows() {
        initServices();
    }

    private void initServices() {
        mWeChargApi = WeChargProvider.getWeChargApi();
    }

    @Override
    public CharSequence getTitle() {
        return null;
    }

    @OnClick(R.id.btn_pay)
    void btnPay() {
        final TxWaitDialog dialog = new TxWaitDialog(getContext(), "Loading payment data");
        dialog.show();

        mWeChargApi.getPaymentDataAsync().enqueue(new Callback<PaymentDataResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<PaymentDataResponseDto> call, @NonNull Response<PaymentDataResponseDto> response) {
                dialog.dismiss();

                PaymentDataResponseDto dto = response.body();
                if (dto == null) {
                    Toast.makeText(getContext(), R.string.error_loading_payment_token, Toast.LENGTH_SHORT).show();
                    return;
                }

                PaymentDataResponseDto.PaymentDataDto paymentData = dto.PaymentData;
                if (paymentData == null) {
                    Toast.makeText(getContext(), R.string.error_loading_payment_token, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!paymentData.Success) {
                    Toast.makeText(getContext(), R.string.error_loading_payment_token, Toast.LENGTH_SHORT).show();
                    return;
                }

                executeBraintreePayment(paymentData.ClientToken);
            }

            @Override
            public void onFailure(@NonNull Call<PaymentDataResponseDto> call, @NonNull Throwable t) {
                dialog.dismiss();

                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void executeBraintreePayment(String clientToken) {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(clientToken);

        startActivityForResult(dropInRequest.getIntent(getContext()), REQUEST_CODE_BRAINTREE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        handleBraintreeResult(requestCode, resultCode, data);
    }

    private void handleBraintreeResult(int requestCode, int resultCode, Intent data) {
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

            } else if (resultCode == Activity.RESULT_CANCELED) {
                mTvResult.setText(R.string.cancel_user);
            } else {
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                mTvResult.setText(error.getMessage());
            }
        }
    }

    private void exchangeUSDtoCHG(String nonce) {

        final TxWaitDialog dialog = new TxWaitDialog(getContext(), "Making payment");
        dialog.show();

        mWeChargApi.confirmPaymentAsync(
                "0x1aa494ff7a493e0ba002e2d38650d4d21bd5591b",
                "0x3d203f7ad6471c5d11d9ab5e1950130da759c594aa998435d01e36067ac9b7e8",
                5,
                nonce
        ).enqueue(new Callback<PaymentResultResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<PaymentResultResponseDto> call, @NonNull Response<PaymentResultResponseDto> response) {

                dialog.dismiss();

                PaymentResultResponseDto body = response.body();
                if (body == null || body.PaymentResult == null) {
                    Snackbar.make(mTvResult, R.string.error_loading_data, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String txHash = body.PaymentResult.TxHash;
                if (TextUtils.isEmpty(txHash)) {
                    Snackbar.make(mTvResult, R.string.error_loading_data, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                mTxHash = txHash;

                mTvResult.setText("Transaction hash:\r\n" + txHash);

            }

            @Override
            public void onFailure(@NonNull Call<PaymentResultResponseDto> call, @NonNull Throwable t) {
                dialog.dismiss();
                Snackbar.make(mTvResult, t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}
