package dev.ehyeon.moveapplication.ui.record.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.data.remote.record.SearchRecordResponse;

public class SearchRecordActivityRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecordActivityRecyclerViewAdapter.SearchRecordActivityRecyclerViewHolder> {

    private List<SearchRecordResponse> list;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public SearchRecordActivityRecyclerViewAdapter(List<SearchRecordResponse> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SearchRecordActivityRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_record_activity_recycler_view_item, parent, false);

        return new SearchRecordActivityRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecordActivityRecyclerViewHolder holder, int position) {
        SearchRecordResponse searchRecordResponse = list.get(position);


        String elapsedTimeString;
        if (searchRecordResponse.getElapsedTime() >= 60) {
            elapsedTimeString = searchRecordResponse.getElapsedTime() / 60 + "분 " + searchRecordResponse.getElapsedTime() % 60 + "초";
        } else {
            elapsedTimeString = searchRecordResponse.getElapsedTime() + "초";
        }

        holder.savedDateTextView.setText(String.format(Locale.getDefault(), "저장 시간 : " + searchRecordResponse.getSavedDate()));
        holder.elapsedTimeTextView.setText(String.format(Locale.getDefault(), "경과 시간 : " + elapsedTimeString));
        holder.totalTravelDistanceTextView.setText(String.format(Locale.getDefault(), "총 이동 거리 : " + searchRecordResponse.getTotalTravelDistance() + " m"));
        holder.averageSpeedTextView.setText(String.format(Locale.getDefault(), "평균 속력 : " + searchRecordResponse.getAverageSpeed() + " km"));
        holder.stepTextView.setText(String.format(Locale.getDefault(), "걸음 수 : " + searchRecordResponse.getStep() + " 걸음"));
        holder.calorieConsumptionTextView.setText(
                "칼로리 소모량 : " + Math.round(searchRecordResponse.getCalorieConsumption() * 10) / 10.0 + " kcal");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateItem(List<SearchRecordResponse> list) {
        this.list = list;
    }

    protected static class SearchRecordActivityRecyclerViewHolder extends RecyclerView.ViewHolder {

        private final TextView savedDateTextView;
        private final TextView elapsedTimeTextView;
        private final TextView totalTravelDistanceTextView;
        private final TextView averageSpeedTextView;
        private final TextView stepTextView;
        private final TextView calorieConsumptionTextView;

        SearchRecordActivityRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            savedDateTextView = itemView.findViewById(R.id.savedDate_textView);
            elapsedTimeTextView = itemView.findViewById(R.id.elapsedTime_textView);
            totalTravelDistanceTextView = itemView.findViewById(R.id.totalTravelDistance_textView);
            averageSpeedTextView = itemView.findViewById(R.id.averageSpeed_textView);
            stepTextView = itemView.findViewById(R.id.step_textView);
            calorieConsumptionTextView = itemView.findViewById(R.id.calorieConsumption_textView);
        }
    }
}
