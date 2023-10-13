package dev.ehyeon.moveapplication.ui.record.calendar_decorator;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Set;

import dev.ehyeon.moveapplication.R;

public class ThreeRecordViewDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private final Set<CalendarDay> calendarDays;

    public ThreeRecordViewDecorator(Context context, Set<CalendarDay> calendarDays) {
        drawable = AppCompatResources.getDrawable(context, R.drawable.three);
        this.calendarDays = calendarDays;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return calendarDays.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}
