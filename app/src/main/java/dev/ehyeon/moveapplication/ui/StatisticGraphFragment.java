package dev.ehyeon.moveapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.data.local.record.HourlyRecord;
import dev.ehyeon.moveapplication.data.local.record.HourlyRecordDao;
import dev.ehyeon.moveapplication.databinding.FragmentStatisticViewpager2GraphBinding;

@AndroidEntryPoint
public class StatisticGraphFragment extends Fragment {

    @Inject
    protected HourlyRecordDao hourlyRecordDao;

    private FragmentStatisticViewpager2GraphBinding binding;

    // TODO fix
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStatisticViewpager2GraphBinding.inflate(inflater, container, false);

        hourlyRecordDao.selectAllHourlyRecordLiveData().observe(getViewLifecycleOwner(), hourlyRecordList -> {
            List<String> dateList = new ArrayList<>();
            List<Entry> stepList = new ArrayList<>();

            int i = 0;

            for (HourlyRecord hourlyRecord : hourlyRecordList) {
                Date date = new Date(hourlyRecord.getId());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH", Locale.KOREA);

                dateList.add(simpleDateFormat.format(date));

                stepList.add(new Entry(i++, hourlyRecord.getStep()));
            }

            LineDataSet lineDataSet = new LineDataSet(stepList, "step");

            binding.fragmentStatisticGraphLineChart.setData(new LineData(lineDataSet));

            ValueFormatter formatter = new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return dateList.get((int) value);
                }
            };

            binding.fragmentStatisticGraphLineChart.getXAxis().setValueFormatter(formatter);

            binding.fragmentStatisticGraphLineChart.invalidate();
        });

        return binding.getRoot();
    }
}
