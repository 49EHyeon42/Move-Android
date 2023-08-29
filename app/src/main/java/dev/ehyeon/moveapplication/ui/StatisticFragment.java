package dev.ehyeon.moveapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.ehyeon.moveapplication.R;

public class StatisticFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        List<String> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add("Hello, world! " + i);
        }

        RecyclerView recyclerView = view.findViewById(R.id.fragmentStatistic_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new StatisticFragmentRecyclerViewAdapter(list));

        return view;
    }
}
