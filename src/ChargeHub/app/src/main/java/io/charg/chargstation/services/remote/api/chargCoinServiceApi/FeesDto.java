package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class FeesDto {

    @SerializedName("enabled")
    public boolean Enabled;

    @SerializedName("fee")
    public double Fee;

    @SerializedName("coin")
    public String Coin;

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
