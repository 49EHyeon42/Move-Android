package dev.ehyeon.moveapplication.ui.record.calendar_decorator;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Set;

public class ExistsRecordViewDecorator implements DayViewDecorator {

    private final Set<CalendarDay> calendarDays;

    public ExistsRecordViewDecorator(Set<CalendarDay> calendarDays) {
        this.calendarDays = calendarDays;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return calendarDays.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.GREEN));
    }
}
