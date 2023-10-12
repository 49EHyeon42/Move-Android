package dev.ehyeon.moveapplication.data.remote.record;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface RecordService {

    @GET("/api/record/total")
    Call<SearchTotalRecordResponse> searchTotalRecord(@Header("Authorization") String authorization);
}
