package io.charg.chargstation.services.remote.api.wecharg;

import com.google.gson.annotations.SerializedName;

public class BrainTreeResponseDto {

    @SerializedName("success")
    public boolean Success;

    @SerializedName("message")
    public String Message;
}
