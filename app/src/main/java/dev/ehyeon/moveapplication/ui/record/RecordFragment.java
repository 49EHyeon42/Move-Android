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
import dev.ehyeon.moveapplication.ui.record.calendar_decorator.ExistsRecordViewDecorator;
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
                totalTravelDistance -> binding.recordFragmentTotalTravelDistanceTextView.setText("총 이동 거리 " + totalTravelDistance + " km"));

        viewModel.getTotalStepNonNullLiveData().observe(getViewLifecycleOwner(),
                totalStep -> binding.recordFragmentTotalStepTextView.setText("총 걸음 수 " + totalStep + " 걸음"));
    }

    private ExistsRecordViewDecorator existsRecordViewDecorator;

    private void initCalendarView() {
        binding.recordFragmentCalendarView.setSelectedDate(LocalDate.now());

        binding.recordFragmentCalendarView.addDecorators(new SaturdayViewDecorator(), new SundayViewDecorator());

        binding.recordFragmentCalendarView.setOnMonthChangedListener((widget, date) ->
                viewModel.searchRecordByDate(requireContext(), date.getYear(), date.getMonth()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        Set<CalendarDay> calendarDays = new HashSet<>();

        viewModel.getSearchRecordResponseNonNullLiveData().observe(getViewLifecycleOwner(), searchRecordResponses -> {
            for (SearchRecordResponse searchRecordResponse : searchRecordResponses) {
                LocalDateTime localDateTime = LocalDateTime.parse(searchRecordResponse.getSavedDate(), formatter);

                calendarDays.add(CalendarDay.from(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth()));
            }

            if (existsRecordViewDecorator != null) {
                binding.recordFragmentCalendarView.removeDecorator(existsRecordViewDecorator);
            }

            existsRecordViewDecorator = new ExistsRecordViewDecorator(requireContext(), calendarDays);

            binding.recordFragmentCalendarView.addDecorator(existsRecordViewDecorator);
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
