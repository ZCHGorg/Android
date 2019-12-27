package io.charg.chargstation.services.remote.api.chargCoinServiceApi;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ConfigDto {

    @SerializedName("wsPort")
    public int WsPort;

    @SerializedName("apiPort")
    public int ApiPort;

    @SerializedName("scanUrl")
    public String ScanUrl;

    @SerializedName("chargeContractAddress")
    public String ChargeContractAddress;

    @SerializedName("serviceContractAddress")
    public String ServiceContractAddress;

    @SerializedName("web3Network")
    public String Web3Network;

    @SerializedName("btcExplorer")
    public String BtcExplorer;

    @SerializedName("ltcExplorer")
    public String LtcExplorer;

    @SerializedName("googleMapsKey")
    public String GoogleMapsKey;

    @SerializedName("web3WsProvider")
    public String Web3WsProvider;

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
