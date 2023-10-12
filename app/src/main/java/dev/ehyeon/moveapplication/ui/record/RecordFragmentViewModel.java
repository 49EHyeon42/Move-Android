package dev.ehyeon.moveapplication.ui.record;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dev.ehyeon.moveapplication.data.remote.record.RecordService;
import dev.ehyeon.moveapplication.data.remote.record.SearchTotalRecordResponse;
import dev.ehyeon.moveapplication.util.NonNullLiveData;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class RecordFragmentViewModel extends ViewModel {

    private static final String TAG = "RecordFragmentViewModel";

    private final RecordService recordService;
    private final NonNullMutableLiveData<Boolean> refreshStatusNonNullMutableLiveData;

    private final NonNullMutableLiveData<Integer> totalMileageNonNullMutableLiveData;
    private final NonNullMutableLiveData<Double> totalTravelDistanceNonNullMutableLiveData;
    private final NonNullMutableLiveData<Double> totalStepNonNullMutableLiveData;

    @Inject
    public RecordFragmentViewModel(RecordService recordService) {
        this.recordService = recordService;
        refreshStatusNonNullMutableLiveData = new NonNullMutableLiveData<>(false);
        totalMileageNonNullMutableLiveData = new NonNullMutableLiveData<>(0);
        totalTravelDistanceNonNullMutableLiveData = new NonNullMutableLiveData<>(0d);
        totalStepNonNullMutableLiveData = new NonNullMutableLiveData<>(0d);
    }

    public void refreshLayout(Context context) {
        refreshStatusNonNullMutableLiveData.setValue(false);

        String accessToken = context.getSharedPreferences("move", Context.MODE_PRIVATE).getString("access token", "");

        recordService.searchTotalRecord(accessToken).enqueue(new Callback<SearchTotalRecordResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchTotalRecordResponse> call, @NonNull Response<SearchTotalRecordResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        Log.w(TAG, "body is null.");

                        Toast.makeText(context, "잠시 후 다시 시도하십시오.", Toast.LENGTH_SHORT).show();
                    }

                    totalMileageNonNullMutableLiveData.setValue(response.body().getTotalMileage());
                    totalTravelDistanceNonNullMutableLiveData.setValue(response.body().getTotalTravelDistance());
                    totalStepNonNullMutableLiveData.setValue(response.body().getTotalStep());
                } else {
                    Log.w(TAG, "searchTotalRecord failed, response code is " + response.code());

                    Toast.makeText(context, "잠시 후 다시 시도하십시오.", Toast.LENGTH_SHORT).show();
                }

                refreshStatusNonNullMutableLiveData.setValue(true);
            }

            @Override
            public void onFailure(@NonNull Call<SearchTotalRecordResponse> call, @NonNull Throwable t) {
                Log.w(TAG, t.getMessage());

                Toast.makeText(context, "잠시 후 다시 시도하십시오.", Toast.LENGTH_SHORT).show();

                refreshStatusNonNullMutableLiveData.setValue(true);
            }
        });
    }

    public NonNullLiveData<Boolean> getRefreshStatusNonNullLiveData() {
        return refreshStatusNonNullMutableLiveData;
    }

    public NonNullLiveData<Integer> getTotalMileageNonNullLiveData() {
        return totalMileageNonNullMutableLiveData;
    }

    public NonNullLiveData<Double> getTotalTravelDistanceNonNullLiveData() {
        return totalTravelDistanceNonNullMutableLiveData;
    }

    public NonNullLiveData<Double> getTotalStepNonNullLiveData() {
        return totalStepNonNullMutableLiveData;
    }
}
