package dev.ehyeon.moveapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.databinding.FragmentStatisticViewpager2CalendarBinding;

@AndroidEntryPoint
public class StatisticCalendarFragment extends Fragment {

    private FragmentStatisticViewpager2CalendarBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticViewpager2CalendarBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}
