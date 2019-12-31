package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import com.google.gson.annotations.SerializedName;

public class ServiceOnResponseDto {

    @SerializedName("error")
    public boolean Error;

    @SerializedName("result")
    public String Result;

}
