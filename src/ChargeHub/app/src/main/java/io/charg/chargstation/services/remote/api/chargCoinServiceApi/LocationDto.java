package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class LocationDto {

    @SerializedName("statusCode")
    public String StatusCode;

    @SerializedName("statusMessage")
    public String StatusMessage;

    @SerializedName("ipAddress")
    public String IpAddress;

    @SerializedName("countryCode")
    public String CountryCode;

    @SerializedName("countryName")
    public String CountryName;

    @SerializedName("regionName")
    public String RegionName;

    @SerializedName("cityName")
    public String CityName;

    @SerializedName("zipCode")
    public String ZipCode;

    @SerializedName("latitude")
    public double Latitude;

    @SerializedName("longitude")
    public double Longitude;

    @SerializedName("timeZone")
    public String TimeZone;

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
