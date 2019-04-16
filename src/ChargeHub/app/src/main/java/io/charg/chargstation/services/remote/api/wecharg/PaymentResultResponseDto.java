package io.charg.chargstation.services.remote.api.wecharg;

import com.google.gson.annotations.SerializedName;

public class PaymentResultResponseDto {

    @SerializedName("paymentResult")
    public PaymentResultDto PaymentResult;

    public class PaymentResultDto {

        @SerializedName("txHash")
        public String TxHash;

    }
}
