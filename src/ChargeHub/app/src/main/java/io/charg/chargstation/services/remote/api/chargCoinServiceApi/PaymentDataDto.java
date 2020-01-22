package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import com.google.gson.annotations.SerializedName;

public class PaymentDataDto {

    @SerializedName("paymentData")
    public PaymentDataContentDto PaymentData;

    public class PaymentDataContentDto {

        @SerializedName("clientToken")
        public String ClientToken;

        @SerializedName("success")
        public boolean Success;
    }
}
