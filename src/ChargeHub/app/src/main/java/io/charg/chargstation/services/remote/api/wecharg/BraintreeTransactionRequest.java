package io.charg.chargstation.services.remote.api.wecharg;

import com.google.gson.annotations.SerializedName;

public class BraintreeTransactionRequest {

    @SerializedName("parameters")
    public ParametersDto Parameters;

    public static class ParametersDto {

        @SerializedName("order_id")
        public String OrderId;

        @SerializedName("payment_method_nonce")
        public String PaymentNonce;

        @SerializedName("pay_amt")
        public String Amount;

        @SerializedName("token")
        public String Token;
    }
}
