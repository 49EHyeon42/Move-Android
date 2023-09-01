package dev.ehyeon.moveapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.data.local.record.RecordDao;
import dev.ehyeon.moveapplication.databinding.FragmentStatisticViewpager2CycleBinding;

@AndroidEntryPoint
public class StatisticCycleFragment extends Fragment {

    @Inject
    protected RecordDao recordDao;

    private FragmentStatisticViewpager2CycleBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticViewpager2CycleBinding.inflate(inflater, container, false);

        StatisticCycleFragmentRecyclerViewAdapter recyclerViewAdapter = new StatisticCycleFragmentRecyclerViewAdapter(new ArrayList<>());

        binding.fragmentStatisticCycleRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.fragmentStatisticCycleRecyclerView.setAdapter(recyclerViewAdapter);

        recordDao.selectAllRecordLiveData().observe(getViewLifecycleOwner(), recordList -> {
            recyclerViewAdapter.updateItem(recordList);
            recyclerViewAdapter.notifyDataSetChanged();
        });

        return binding.getRoot();
    }
}
