package dev.ehyeon.moveapplication.ui.record.calendar_decorator;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class SundayViewDecorator implements DayViewDecorator {

    private static final int SUNDAY = 7;

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.getDate().getDayOfWeek().getValue() == SUNDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED));
    }
}
