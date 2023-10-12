package dev.ehyeon.moveapplication.ui.record.calendar_decorator;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class SaturdayViewDecorator implements DayViewDecorator {

    private static final int SATURDAY = 6;

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.getDate().getDayOfWeek().getValue() == SATURDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.BLUE));
    }
}
