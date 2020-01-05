package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import com.google.gson.annotations.SerializedName;

import io.charg.chargstation.services.remote.api.wecharg.PaymentResultResponseDto;

public class ConfirmPaymentResponseDto {

    @SerializedName("paymentResult")
    public PaymentResultResponseDto.PaymentResultDto PaymentResult;

    public class PaymentResultDto {

        @SerializedName("txHash")
        public String TxHash;

    }

}
