package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class RatesDto {

    @SerializedName("BTC")
    public double Btc;

    @SerializedName("USD")
    public double Usd;

    @SerializedName("LTC")
    public double Ltc;

    @SerializedName("EOS")
    public double Eos;

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
