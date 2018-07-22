package io.charg.chargstation.services.remote.api.wecharg;

import com.google.gson.annotations.SerializedName;

public class TokenRequestDto {
    @SerializedName("username")
    public String UserName;

    @SerializedName("password")
    public String Password;

    public TokenRequestDto(String userName, String password) {
        UserName = userName;
        Password = password;
    }
}
