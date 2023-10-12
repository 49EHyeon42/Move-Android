package dev.ehyeon.moveapplication.ui.record.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.data.remote.record.RecordService;
import dev.ehyeon.moveapplication.data.remote.record.SearchRecordResponse;
import dev.ehyeon.moveapplication.databinding.SearchRecordActivityBinding;
import dev.ehyeon.moveapplication.util.NonNullMutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class SearchRecordActivity extends AppCompatActivity {

    private static final String TAG = "SearchRecordActivity";

    @Inject
    protected RecordService recordService;

    private SearchRecordActivityBinding binding;

    private NonNullMutableLiveData<List<SearchRecordResponse>> liveData = new NonNullMutableLiveData<>(new ArrayList<>());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.search_record_activity);

        String accessToken = this.getSharedPreferences("move", Context.MODE_PRIVATE).getString("access token", "");

        Intent intent = getIntent();

        int year = intent.getIntExtra("year", 2000);
        int month = intent.getIntExtra("month", 1);
        int day = intent.getIntExtra("day", 1);

        String from = year + "-" + String.format(Locale.getDefault(), "%02d", month) + "-" + String.format(Locale.getDefault(), "%02d", day) + "T00:00:00";
        String to = year + "-" + String.format(Locale.getDefault(), "%02d", month) + "-" + String.format(Locale.getDefault(), "%02d", day) + "T23:59:59";

        SearchRecordActivityRecyclerViewAdapter recyclerViewAdapter = new SearchRecordActivityRecyclerViewAdapter(new ArrayList<>());

        binding.searchRecordActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.searchRecordActivityRecyclerView.setAdapter(recyclerViewAdapter);
        binding.searchRecordActivityRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 10; // 아이템간 간격
            }
        });

        recordService.searchRecordByYearAndMonth(accessToken, from, to).enqueue(new Callback<List<SearchRecordResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<SearchRecordResponse>> call, @NonNull Response<List<SearchRecordResponse>> response) {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        Log.w(TAG, "body is null.");

                        Toast.makeText(getBaseContext(), "잠시 후 다시 시도하십시오.", Toast.LENGTH_SHORT).show();

                        return;
                    }

                    recyclerViewAdapter.updateItem(response.body());
                    recyclerViewAdapter.notifyDataSetChanged();
                } else {
                    Log.w(TAG, "searchRecordByDate failed, response code is " + response.code());

                    Toast.makeText(getBaseContext(), "잠시 후 다시 시도하십시오.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SearchRecordResponse>> call, @NonNull Throwable t) {
                Log.w(TAG, t.getMessage());

                Toast.makeText(getBaseContext(), "잠시 후 다시 시도하십시오.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
