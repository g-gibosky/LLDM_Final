package org.chromium.ui.picker;

import android.content.Context;
import java.util.Calendar;
import java.util.TimeZone;
import org.chromium.ui.C0290R;

public class WeekPicker extends TwoFieldDatePicker {
    public WeekPicker(Context context, double minValue, double maxValue) {
        super(context, minValue, maxValue);
        getPositionInYearSpinner().setContentDescription(getResources().getString(C0290R.string.accessibility_date_picker_week));
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setFirstDayOfWeek(2);
        cal.setMinimalDaysInFirstWeek(4);
        cal.setTimeInMillis(System.currentTimeMillis());
        init(getISOWeekYearForDate(cal), getWeekForDate(cal), null);
    }

    public static Calendar createDateFromWeek(int year, int week) {
        Calendar date = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        date.clear();
        date.setFirstDayOfWeek(2);
        date.setMinimalDaysInFirstWeek(4);
        date.set(7, 2);
        date.set(1, year);
        date.set(3, week);
        return date;
    }

    public static Calendar createDateFromValue(double value) {
        Calendar date = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        date.clear();
        date.setFirstDayOfWeek(2);
        date.setMinimalDaysInFirstWeek(4);
        date.setTimeInMillis((long) value);
        return date;
    }

    protected Calendar getDateForValue(double value) {
        return createDateFromValue(value);
    }

    public static int getISOWeekYearForDate(Calendar date) {
        int year = date.get(1);
        int month = date.get(2);
        int week = date.get(3);
        if (month == 0 && week > 51) {
            return year - 1;
        }
        if (month == 11 && week == 1) {
            return year + 1;
        }
        return year;
    }

    public static int getWeekForDate(Calendar date) {
        return date.get(3);
    }

    protected void setCurrentDate(int year, int week) {
        Calendar date = createDateFromWeek(year, week);
        if (date.before(getMinDate())) {
            setCurrentDate(getMinDate());
        } else if (date.after(getMaxDate())) {
            setCurrentDate(getMaxDate());
        } else {
            setCurrentDate(date);
        }
    }

    private int getNumberOfWeeks(int year) {
        return createDateFromWeek(year, 20).getActualMaximum(3);
    }

    public int getYear() {
        return getISOWeekYearForDate(getCurrentDate());
    }

    public int getWeek() {
        return getWeekForDate(getCurrentDate());
    }

    public int getPositionInYear() {
        return getWeek();
    }

    protected int getMaxYear() {
        return getISOWeekYearForDate(getMaxDate());
    }

    protected int getMinYear() {
        return getISOWeekYearForDate(getMinDate());
    }

    protected int getMaxPositionInYear(int year) {
        if (year == getISOWeekYearForDate(getMaxDate())) {
            return getWeekForDate(getMaxDate());
        }
        return getNumberOfWeeks(year);
    }

    protected int getMinPositionInYear(int year) {
        if (year == getISOWeekYearForDate(getMinDate())) {
            return getWeekForDate(getMinDate());
        }
        return 1;
    }
}
