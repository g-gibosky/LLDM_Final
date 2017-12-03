package org.chromium.ui.picker;

import android.os.Build.VERSION;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateDialogNormalizer {

    private static class DateAndMillis {
        public final int day;
        public final long millisForPicker;
        public final int month;
        public final int year;

        DateAndMillis(long millisForPicker, int year, int month, int day) {
            this.millisForPicker = millisForPicker;
            this.year = year;
            this.month = month;
            this.day = day;
        }

        static DateAndMillis create(long millisUtc) {
            GregorianCalendar utcCal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            utcCal.setGregorianChange(new Date(Long.MIN_VALUE));
            utcCal.setTimeInMillis(millisUtc);
            return create(utcCal.get(1), utcCal.get(2), utcCal.get(5));
        }

        static DateAndMillis create(int year, int month, int day) {
            Calendar defaultTimeZoneCal = Calendar.getInstance(TimeZone.getDefault());
            defaultTimeZoneCal.clear();
            defaultTimeZoneCal.set(year, month, day);
            return new DateAndMillis(defaultTimeZoneCal.getTimeInMillis(), year, month, day);
        }
    }

    private static void setLimits(DatePicker picker, long currentMillisForPicker, long minMillisForPicker, long maxMillisForPicker) {
        if (VERSION.SDK_INT == 21 || VERSION.SDK_INT == 22) {
            minMillisForPicker = Math.max(minMillisForPicker, currentMillisForPicker - 157680000000000L);
            maxMillisForPicker = Math.min(maxMillisForPicker, currentMillisForPicker + 157680000000000L);
        }
        if (minMillisForPicker > picker.getMaxDate()) {
            picker.setMaxDate(maxMillisForPicker);
            picker.setMinDate(minMillisForPicker);
            return;
        }
        picker.setMinDate(minMillisForPicker);
        picker.setMaxDate(maxMillisForPicker);
    }

    public static void normalize(DatePicker picker, OnDateChangedListener listener, int year, int month, int day, long minMillisUtc, long maxMillisUtc) {
        DateAndMillis currentDate = DateAndMillis.create(year, month, day);
        DateAndMillis minDate = DateAndMillis.create(minMillisUtc);
        DateAndMillis maxDate = DateAndMillis.create(maxMillisUtc);
        if (maxDate.millisForPicker < minDate.millisForPicker) {
            maxDate = minDate;
        }
        if (currentDate.millisForPicker < minDate.millisForPicker) {
            currentDate = minDate;
        } else if (currentDate.millisForPicker > maxDate.millisForPicker) {
            currentDate = maxDate;
        }
        setLimits(picker, currentDate.millisForPicker, minDate.millisForPicker, maxDate.millisForPicker);
        picker.init(currentDate.year, currentDate.month, currentDate.day, listener);
    }
}
