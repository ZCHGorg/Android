package io.charg.chargstation.services.remote.api.socketio;

import com.google.gson.annotations.SerializedName;

public class PaymentDataRequestDto {

    @SerializedName("station")
    public String StationEthAddress;

    @SerializedName("hash")
    public String SellOrderHash;

    @SerializedName("amountUSD")
    public float AmountUsd;

    @SerializedName("nonce")
    public String Nonce;

    public PaymentDataRequestDto(String stationEthAddress, String sellOrderHash, float amountUsd, String nonce) {
        StationEthAddress = stationEthAddress;
        SellOrderHash = sellOrderHash;
        AmountUsd = amountUsd;
        Nonce = nonce;
    }

}
