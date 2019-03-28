package io.charg.chargstation.services.remote.api.socketio;

import com.google.gson.annotations.SerializedName;

public class BraintreeTokenDto {

    @SerializedName("success")
    public boolean Success;

    @SerializedName("clientToken")
    public String Token;

    @Override
    public String toString() {
        return Success + " " + Token;
    }
}
