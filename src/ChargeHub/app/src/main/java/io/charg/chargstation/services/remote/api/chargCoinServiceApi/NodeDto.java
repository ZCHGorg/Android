package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class NodeDto {

    @SerializedName("name")
    public String Name;

    @SerializedName("location")
    public String Location;

    @SerializedName("phone")
    public String Phone;

    @SerializedName("connector")
    public String Connector;

    @SerializedName("power")
    public String PowerStr;

    @SerializedName("latitude")
    public double Latitude;

    @SerializedName("longitude")
    public double Longitude;

    @SerializedName("connected")
    public boolean Connected;

    @SerializedName("ip")
    public String Ip;

    @SerializedName("assets")
    public Map<String, AssetDto> Assets = new HashMap<>();

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public class AssetDto {

        @SerializedName("name")
        public String Name;

        @SerializedName("slots")
        public int Slots;

        @SerializedName("free")
        public int Free;

    }
}
