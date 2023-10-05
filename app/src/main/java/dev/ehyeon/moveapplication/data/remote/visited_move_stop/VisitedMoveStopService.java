package dev.ehyeon.moveapplication.data.remote.visited_move_stop;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface VisitedMoveStopService {

    @GET("/api/visited_move_stop")
    Call<List<SearchVisitedMoveStopResponse>> searchVisitedMoveStop(
            @Header("Authorization") String token,
            @Query("latitude1") double latitude1,
            @Query("longitude1") double longitude1,
            @Query("latitude2") double latitude2,
            @Query("longitude2") double longitude2);
}
