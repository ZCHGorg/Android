package io.charg.chargstation.services.remote.api.socketio;

import com.google.gson.annotations.SerializedName;

public class SellOrderRequestDto {

    @SerializedName("amountCHG")
    public float AmountChg;

    public SellOrderRequestDto(float amountChg) {
        AmountChg = amountChg;
    }

}
