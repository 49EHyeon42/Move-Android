package dev.ehyeon.moveapplication.data.remote.record;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RecordService {

    @GET("/api/record/")
    Call<List<SearchRecordResponse>> searchRecordByYearAndMonth(@Header("Authorization") String authorization, @Query("from") String from, @Query("to") String to);

    @GET("/api/record/total")
    Call<SearchTotalRecordResponse> searchTotalRecord(@Header("Authorization") String authorization);
}
