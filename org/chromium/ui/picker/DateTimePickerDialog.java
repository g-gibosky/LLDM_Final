package org.chromium.ui.picker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.chromium.base.VisibleForTesting;
import org.chromium.ui.C0290R;

public class DateTimePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener, OnTimeChangedListener {
    private final OnDateTimeSetListener mCallBack;
    private final DatePicker mDatePicker;
    private final long mMaxTimeMillis;
    private final long mMinTimeMillis;
    private final TimePicker mTimePicker;

    public interface OnDateTimeSetListener {
        void onDateTimeSet(DatePicker datePicker, TimePicker timePicker, int i, int i2, int i3, int i4, int i5);
    }

    public DateTimePickerDialog(Context context, OnDateTimeSetListener callBack, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute, boolean is24HourView, double min, double max) {
        super(context, 0);
        this.mMinTimeMillis = (long) min;
        this.mMaxTimeMillis = (long) max;
        this.mCallBack = callBack;
        setButton(-1, context.getText(C0290R.string.date_picker_dialog_set), this);
        setButton(-2, context.getText(17039360), (OnClickListener) null);
        setIcon(0);
        setTitle(context.getText(C0290R.string.date_time_picker_dialog_title));
        View view = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(C0290R.layout.date_time_picker_dialog, null);
        setView(view);
        this.mDatePicker = (DatePicker) view.findViewById(C0290R.id.date_picker);
        DateDialogNormalizer.normalize(this.mDatePicker, this, year, monthOfYear, dayOfMonth, this.mMinTimeMillis, this.mMaxTimeMillis);
        this.mTimePicker = (TimePicker) view.findViewById(C0290R.id.time_picker);
        this.mTimePicker.setIs24HourView(Boolean.valueOf(is24HourView));
        setHour(this.mTimePicker, hourOfDay);
        setMinute(this.mTimePicker, minute);
        this.mTimePicker.setOnTimeChangedListener(this);
        onTimeChanged(this.mTimePicker, getHour(this.mTimePicker), getMinute(this.mTimePicker));
    }

    public void onClick(DialogInterface dialog, int which) {
        tryNotifyDateTimeSet();
    }

    private void tryNotifyDateTimeSet() {
        if (this.mCallBack != null) {
            this.mDatePicker.clearFocus();
            this.mTimePicker.clearFocus();
            this.mCallBack.onDateTimeSet(this.mDatePicker, this.mTimePicker, this.mDatePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth(), getHour(this.mTimePicker), getMinute(this.mTimePicker));
        }
    }

    public void onDateChanged(DatePicker view, int year, int month, int day) {
        if (this.mTimePicker != null) {
            onTimeChanged(this.mTimePicker, getHour(this.mTimePicker), getMinute(this.mTimePicker));
        }
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        onTimeChangedInternal(this.mDatePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth(), this.mTimePicker, this.mMinTimeMillis, this.mMaxTimeMillis);
    }

    @VisibleForTesting
    public static void onTimeChangedInternal(int year, int month, int day, TimePicker picker, long minTimeMillis, long maxTimeMillis) {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(year, month, day, getHour(picker), getMinute(picker), 0);
        if (calendar.getTimeInMillis() < minTimeMillis) {
            calendar.setTimeInMillis(minTimeMillis);
        } else if (calendar.getTimeInMillis() > maxTimeMillis) {
            calendar.setTimeInMillis(maxTimeMillis);
        }
        setHour(picker, calendar.get(11));
        setMinute(picker, calendar.get(12));
    }

    public void updateDateTime(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minutOfHour) {
        this.mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
        setHour(this.mTimePicker, hourOfDay);
        setMinute(this.mTimePicker, minutOfHour);
    }

    private static void setHour(TimePicker picker, int hour) {
        picker.setCurrentHour(Integer.valueOf(hour));
    }

    private static void setMinute(TimePicker picker, int minute) {
        picker.setCurrentMinute(Integer.valueOf(minute));
    }

    private static int getHour(TimePicker picker) {
        return picker.getCurrentHour().intValue();
    }

    private static int getMinute(TimePicker picker) {
        return picker.getCurrentMinute().intValue();
    }
}
