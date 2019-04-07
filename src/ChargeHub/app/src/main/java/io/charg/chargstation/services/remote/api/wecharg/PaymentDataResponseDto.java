package io.charg.chargstation.services.remote.api.wecharg;

import com.google.gson.annotations.SerializedName;

public class PaymentDataResponseDto {

    @SerializedName("paymentData")
    public PaymentDataDto PaymentData;

    public class PaymentDataDto {

        @SerializedName("clientToken")
        public String ClientToken;

        @SerializedName("success")
        public boolean Success;

    }
}
