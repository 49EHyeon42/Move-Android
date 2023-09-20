package dev.ehyeon.moveapplication.data.remote.retrofit.sign;

import com.google.gson.annotations.SerializedName;

public class SignInResponse {

    @SerializedName("access token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}
