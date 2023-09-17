package dev.ehyeon.moveapplication.data.remote.retrofit.sign;

import retrofit2.Call;
import retrofit2.http.GET;

public interface SignService {

    @GET(".")
    Call<String> root();
}
