package io.charg.chargstation.services.remote.api.socketio;

import com.google.gson.annotations.SerializedName;

public class SellOrderResponseDto {

    @SerializedName("give")
    public String GiveChg;

    @SerializedName("get")
    public String GetEth;

    @SerializedName("rate")
    public String Rate;

    @SerializedName("expire")
    public long Expire;

    @SerializedName("hash")
    public String Hash;

    @SerializedName("seller")
    public String Seller;

}
