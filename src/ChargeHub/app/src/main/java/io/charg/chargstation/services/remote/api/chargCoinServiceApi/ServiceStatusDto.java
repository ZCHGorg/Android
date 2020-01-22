package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ServiceStatusDto {

    @SerializedName("error")
    public boolean Error;

    @SerializedName("result")
    public ResultDto Result;

    @SerializedName("started")
    public long StartedAt;

    @SerializedName("stopped")
    public long StoppedAt;

    @SerializedName("remained")
    public long Remaind;

    @SerializedName("time")
    public long TimeElapsed;

    public class ResultDto {

        @SerializedName("error")
        public String Error;

    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
