package dev.ehyeon.moveapplication.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.ehyeon.moveapplication.R;

public class StatisticFragmentRecyclerViewAdapter extends RecyclerView.Adapter<StatisticFragmentRecyclerViewAdapter.StatisticFragmentRecyclerViewHolder> {

    private final List<String> list;

    public StatisticFragmentRecyclerViewAdapter(List<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public StatisticFragmentRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_statistic_recycler_item, parent, false);

        return new StatisticFragmentRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticFragmentRecyclerViewHolder holder, int position) {
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class StatisticFragmentRecyclerViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        StatisticFragmentRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.fragmentStatistic_recyclerView_textView);
        }
    }
}
