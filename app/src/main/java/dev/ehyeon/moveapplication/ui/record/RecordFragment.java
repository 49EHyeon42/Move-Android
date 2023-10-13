package dev.ehyeon.moveapplication.ui.record;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.threeten.bp.LocalDate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.data.remote.record.SearchRecordResponse;
import dev.ehyeon.moveapplication.databinding.RecordFragmentBinding;
import dev.ehyeon.moveapplication.ui.record.calendar_decorator.FiveRecordViewDecorator;
import dev.ehyeon.moveapplication.ui.record.calendar_decorator.OneRecordViewDecorator;
import dev.ehyeon.moveapplication.ui.record.calendar_decorator.ThreeRecordViewDecorator;
import dev.ehyeon.moveapplication.ui.record.calendar_decorator.ZeroRecordViewDecorator;
import dev.ehyeon.moveapplication.ui.record.calendar_decorator.SaturdayViewDecorator;
import dev.ehyeon.moveapplication.ui.record.calendar_decorator.SundayViewDecorator;
import dev.ehyeon.moveapplication.ui.record.search.SearchRecordActivity;

@AndroidEntryPoint
public class RecordFragment extends Fragment {

    private RecordFragmentViewModel viewModel;

    private RecordFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RecordFragmentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RecordFragmentBinding.inflate(inflater, container, false);

        binding.recordFragmentSwipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshLayout(requireContext()));

        viewModel.getRefreshStatusNonNullLiveData().observe(getViewLifecycleOwner(), refreshStatus -> {
            if (refreshStatus) {
                binding.recordFragmentSwipeRefreshLayout.setRefreshing(false);

                CalendarDay currentCalendarDay = binding.recordFragmentCalendarView.getSelectedDate();

                viewModel.searchRecordByDate(requireContext(), currentCalendarDay.getYear(), currentCalendarDay.getMonth());
            }
        });

        initTextView();
        initCalendarView();
        initButton();

        viewModel.refreshLayout(requireContext());

        return binding.getRoot();
    }

    private void initTextView() {
        viewModel.getTotalMileageNonNullLiveData().observe(getViewLifecycleOwner(),
                totalMileage -> binding.recordFragmentTotalMileageTextView.setText("마일리지 " + totalMileage + " 적립"));

        viewModel.getTotalTravelDistanceNonNullLiveData().observe(getViewLifecycleOwner(),
                totalTravelDistance -> binding.recordFragmentTotalTravelDistanceTextView.setText("총 이동 거리 " + totalTravelDistance + " m"));

        viewModel.getTotalStepNonNullLiveData().observe(getViewLifecycleOwner(),
                totalStep -> binding.recordFragmentTotalStepTextView.setText("총 걸음 수 " + totalStep + " 걸음"));
    }

    private ZeroRecordViewDecorator zeroRecordViewDecorator;
    private OneRecordViewDecorator oneRecordViewDecorator;
    private ThreeRecordViewDecorator threeRecordViewDecorator;
    private FiveRecordViewDecorator fiveRecordViewDecorator;

    private void initCalendarView() {
        binding.recordFragmentCalendarView.setSelectedDate(LocalDate.now());

        binding.recordFragmentCalendarView.addDecorators(new SaturdayViewDecorator(), new SundayViewDecorator());

        binding.recordFragmentCalendarView.setOnMonthChangedListener((widget, date) ->
                viewModel.searchRecordByDate(requireContext(), date.getYear(), date.getMonth()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        Set<CalendarDay> zero = new HashSet<>();
        Set<CalendarDay> one = new HashSet<>();
        Set<CalendarDay> three = new HashSet<>();
        Set<CalendarDay> five = new HashSet<>();

        viewModel.getSearchRecordResponseNonNullLiveData().observe(getViewLifecycleOwner(), searchRecordResponses -> {
            zero.clear();
            one.clear();
            three.clear();
            five.clear();

            for (SearchRecordResponse searchRecordResponse : searchRecordResponses) {
                LocalDateTime localDateTime = LocalDateTime.parse(searchRecordResponse.getSavedDate(), formatter);

                if (searchRecordResponse.getTotalTravelDistance() < 1000) {
                    zero.add(CalendarDay.from(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth()));
                } else if (searchRecordResponse.getTotalTravelDistance() < 3000) {
                    one.add(CalendarDay.from(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth()));
                } else if (searchRecordResponse.getTotalTravelDistance() < 5000) {
                    three.add(CalendarDay.from(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth()));
                } else {
                    five.add(CalendarDay.from(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth()));
                }
            }

            if (zeroRecordViewDecorator != null) {
                binding.recordFragmentCalendarView.removeDecorator(zeroRecordViewDecorator);
            }

            if (oneRecordViewDecorator != null) {
                binding.recordFragmentCalendarView.removeDecorator(oneRecordViewDecorator);
            }

            if (threeRecordViewDecorator != null) {
                binding.recordFragmentCalendarView.removeDecorator(threeRecordViewDecorator);
            }

            if (fiveRecordViewDecorator != null) {
                binding.recordFragmentCalendarView.removeDecorator(fiveRecordViewDecorator);
            }

            zeroRecordViewDecorator = new ZeroRecordViewDecorator(requireContext(), zero);
            oneRecordViewDecorator = new OneRecordViewDecorator(requireContext(), one);
            threeRecordViewDecorator = new ThreeRecordViewDecorator(requireContext(), three);
            fiveRecordViewDecorator = new FiveRecordViewDecorator(requireContext(), five);

            binding.recordFragmentCalendarView.addDecorators(zeroRecordViewDecorator, oneRecordViewDecorator, threeRecordViewDecorator, fiveRecordViewDecorator);
        });
    }

    private void initButton() {
        binding.searchRecordButton.setOnClickListener(view -> {
            CalendarDay currentCalendarDay = binding.recordFragmentCalendarView.getSelectedDate();

            Intent intent = new Intent(requireContext(), SearchRecordActivity.class);
            intent.putExtra("year", currentCalendarDay.getYear());
            intent.putExtra("month", currentCalendarDay.getMonth());
            intent.putExtra("day", currentCalendarDay.getDay());

            requireContext().startActivity(intent);
        });
    }
}
