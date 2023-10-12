package dev.ehyeon.moveapplication.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import dev.ehyeon.moveapplication.R;
import dev.ehyeon.moveapplication.data.local.record.Record;

public class StatisticCycleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<StatisticCycleFragmentRecyclerViewAdapter.StatisticCycleFragmentRecyclerViewHolder> {

    private List<Record> recordList;

    public StatisticCycleFragmentRecyclerViewAdapter(List<Record> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public StatisticCycleFragmentRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_statistic_recycler_item, parent, false);

        return new StatisticCycleFragmentRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticCycleFragmentRecyclerViewHolder holder, int position) {
        Record record = recordList.get(position);

        int elapsedTime = record.getElapsedTime();
        holder.elapsedTimeTextView.setText(String.format(Locale.getDefault(), "경과 시간 %02d:%02d:%02d", elapsedTime / 3600, (elapsedTime % 3600) / 60, elapsedTime % 60));

        holder.totalTravelDistanceTextView.setText(String.format(Locale.getDefault(), "총 이동 거리 %.0f m", record.getTotalTravelDistance()));

        holder.averageSpeedTextView.setText(String.format(Locale.getDefault(), "평균 속력 %.1f km/h", record.getAverageSpeed()));

        holder.stepTextView.setText(String.format(Locale.getDefault(), "걸음 수 %,d", record.getStep()));

        holder.kiloCalorieTextView.setText(String.format(Locale.getDefault(), "칼로리 소모량 %.1f kcal", record.getCalorieConsumption()));

        holder.imageView.setImageBitmap(record.getImage());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public void updateItem(List<Record> recordList) {
        this.recordList = recordList;
    }

    protected static class StatisticCycleFragmentRecyclerViewHolder extends RecyclerView.ViewHolder {

        private final TextView elapsedTimeTextView;
        private final TextView totalTravelDistanceTextView;
        private final TextView averageSpeedTextView;
        private final TextView stepTextView;
        private final TextView kiloCalorieTextView;
        private final ImageView imageView;

        StatisticCycleFragmentRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            elapsedTimeTextView = itemView.findViewById(R.id.fragmentStatistic_recyclerView_elapsedTimeTextView);
            totalTravelDistanceTextView = itemView.findViewById(R.id.fragmentStatistic_recyclerView_totalTravelDistanceTextView);
            averageSpeedTextView = itemView.findViewById(R.id.fragmentStatistic_recyclerView_averageSpeedTextView);
            stepTextView = itemView.findViewById(R.id.fragmentStatistic_recyclerView_stepTextView);
            kiloCalorieTextView = itemView.findViewById(R.id.fragmentStatistic_recyclerView_calorieConsumptionTextView);
            imageView = itemView.findViewById(R.id.fragmentStatistic_recyclerView_ImageView);
        }
    }
}