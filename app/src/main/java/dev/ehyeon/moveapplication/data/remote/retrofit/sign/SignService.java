package dev.ehyeon.moveapplication.data.remote.retrofit.sign;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface SignService {

    @GET(".")
    Call<String> root();

    @POST("/api/signin")
    Call<SignInResponse> signIn(@Body SignInRequest request);

    @POST("/api/signup")
    Call<Void> signUp(@Body SignUpRequest request);
}
