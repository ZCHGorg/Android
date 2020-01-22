package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import com.google.gson.annotations.SerializedName;

public class ConfirmPaymentResponseDto {

    @SerializedName("error")
    public boolean ErrorStatus;

    @SerializedName("paymentResult")
    public PaymentResultDto PaymentResult;

    public class PaymentResultDto {

        @SerializedName("txHash")
        public String TxHash;

    }

}
