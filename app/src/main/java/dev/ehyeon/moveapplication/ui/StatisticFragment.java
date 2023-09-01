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
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.data.local.record.RecordDao;
import dev.ehyeon.moveapplication.databinding.FragmentStatisticBinding;

@AndroidEntryPoint
public class StatisticFragment extends Fragment {

    @Inject
    protected RecordDao recordDao;

    private FragmentStatisticBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticBinding.inflate(inflater, container, false);

        StatisticFragmentRecyclerViewAdapter recyclerViewAdapter = new StatisticFragmentRecyclerViewAdapter(new ArrayList<>());

        binding.fragmentStatisticRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.fragmentStatisticRecyclerView.setAdapter(recyclerViewAdapter);

        // TODO refactor record -> recordDto
        recordDao.selectAllRecordLiveData().observe(getViewLifecycleOwner(), records -> {
            recyclerViewAdapter.updateItem(records);
            recyclerViewAdapter.notifyDataSetChanged();
        });

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new StatisticCycleFragment());
        fragmentList.add(new StatisticGraphFragment());

        binding.fragmentStatisticViewPager2.setAdapter(new StatisticViewPager2Adapter(this, fragmentList));

        return binding.getRoot();
    }
}
