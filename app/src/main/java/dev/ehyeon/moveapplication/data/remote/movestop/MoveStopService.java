package dev.ehyeon.moveapplication.data.remote.movestop;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MoveStopService {

    @GET("/move-stop")
    Call<List<MoveStopResponse>> getMoveStop(@Query("latitude1") double latitude1, @Query("longitude1") double longitude1, @Query("latitude2") double latitude2, @Query("longitude2") double longitude2);
}
