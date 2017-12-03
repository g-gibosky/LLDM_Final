package org.chromium.ui.picker;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import org.chromium.ui.C0290R;

public abstract class TwoFieldDatePicker extends FrameLayout {
    private Calendar mCurrentDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    private Calendar mMaxDate;
    private Calendar mMinDate;
    private OnMonthOrWeekChangedListener mMonthOrWeekChangedListener;
    private final NumberPicker mPositionInYearSpinner;
    private final NumberPicker mYearSpinner;

    class C03061 implements OnValueChangeListener {
        C03061() {
        }

        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            int year = TwoFieldDatePicker.this.getYear();
            int positionInYear = TwoFieldDatePicker.this.getPositionInYear();
            if (picker == TwoFieldDatePicker.this.mPositionInYearSpinner) {
                positionInYear = newVal;
                if (oldVal == picker.getMaxValue() && newVal == picker.getMinValue()) {
                    year++;
                    positionInYear = TwoFieldDatePicker.this.getMinPositionInYear(year);
                } else if (oldVal == picker.getMinValue() && newVal == picker.getMaxValue()) {
                    year--;
                    positionInYear = TwoFieldDatePicker.this.getMaxPositionInYear(year);
                }
            } else if (picker == TwoFieldDatePicker.this.mYearSpinner) {
                year = newVal;
            } else {
                throw new IllegalArgumentException();
            }
            TwoFieldDatePicker.this.setCurrentDate(year, positionInYear);
            TwoFieldDatePicker.this.updateSpinners();
            TwoFieldDatePicker.this.notifyDateChanged();
        }
    }

    public interface OnMonthOrWeekChangedListener {
        void onMonthOrWeekChanged(TwoFieldDatePicker twoFieldDatePicker, int i, int i2);
    }

    protected abstract Calendar getDateForValue(double d);

    protected abstract int getMaxPositionInYear(int i);

    protected abstract int getMaxYear();

    protected abstract int getMinPositionInYear(int i);

    protected abstract int getMinYear();

    public abstract int getPositionInYear();

    protected abstract void setCurrentDate(int i, int i2);

    public TwoFieldDatePicker(Context context, double minValue, double maxValue) {
        super(context, null, 16843612);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(C0290R.layout.two_field_date_picker, this, true);
        OnValueChangeListener onChangeListener = new C03061();
        if (minValue >= maxValue) {
            this.mMinDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            this.mMinDate.set(0, 0, 1);
            this.mMaxDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            this.mMaxDate.set(9999, 0, 1);
        } else {
            this.mMinDate = getDateForValue(minValue);
            this.mMaxDate = getDateForValue(maxValue);
        }
        this.mPositionInYearSpinner = (NumberPicker) findViewById(C0290R.id.position_in_year);
        this.mPositionInYearSpinner.setOnLongPressUpdateInterval(200);
        this.mPositionInYearSpinner.setOnValueChangedListener(onChangeListener);
        this.mYearSpinner = (NumberPicker) findViewById(C0290R.id.year);
        this.mYearSpinner.setOnLongPressUpdateInterval(100);
        this.mYearSpinner.setOnValueChangedListener(onChangeListener);
        reorderSpinners();
    }

    @TargetApi(18)
    private void reorderSpinners() {
        boolean posInserted = false;
        boolean yearInserted = false;
        LinearLayout pickers = (LinearLayout) findViewById(C0290R.id.pickers);
        pickers.removeView(this.mPositionInYearSpinner);
        pickers.removeView(this.mYearSpinner);
        int i;
        if (VERSION.SDK_INT >= 18) {
            String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "yyyyMMMdd");
            i = 0;
            while (i < pattern.length()) {
                char ch = pattern.charAt(i);
                if (ch == '\'') {
                    i = pattern.indexOf(39, i + 1);
                    if (i == -1) {
                        throw new IllegalArgumentException("Bad quoting in " + pattern);
                    }
                } else if ((ch == 'M' || ch == 'L') && !posInserted) {
                    pickers.addView(this.mPositionInYearSpinner);
                    posInserted = true;
                } else if (ch == 'y' && !yearInserted) {
                    pickers.addView(this.mYearSpinner);
                    yearInserted = true;
                }
                i++;
            }
        } else {
            char[] order = DateFormat.getDateFormatOrder(getContext());
            for (i = 0; i < order.length; i++) {
                if (order[i] == 'M') {
                    pickers.addView(this.mPositionInYearSpinner);
                    posInserted = true;
                } else if (order[i] == 'y') {
                    pickers.addView(this.mYearSpinner);
                    yearInserted = true;
                }
            }
        }
        if (!posInserted) {
            pickers.addView(this.mPositionInYearSpinner);
        }
        if (!yearInserted) {
            pickers.addView(this.mYearSpinner);
        }
    }

    public void init(int year, int positionInYear, OnMonthOrWeekChangedListener onMonthOrWeekChangedListener) {
        setCurrentDate(year, positionInYear);
        updateSpinners();
        this.mMonthOrWeekChangedListener = onMonthOrWeekChangedListener;
    }

    public boolean isNewDate(int year, int positionInYear) {
        return (getYear() == year && getPositionInYear() == positionInYear) ? false : true;
    }

    public void updateDate(int year, int positionInYear) {
        if (isNewDate(year, positionInYear)) {
            setCurrentDate(year, positionInYear);
            updateSpinners();
            notifyDateChanged();
        }
    }

    protected void setCurrentDate(Calendar date) {
        this.mCurrentDate = date;
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
        event.getText().add(DateUtils.formatDateTime(getContext(), this.mCurrentDate.getTimeInMillis(), 20));
    }

    public int getYear() {
        return this.mCurrentDate.get(1);
    }

    protected Calendar getMaxDate() {
        return this.mMaxDate;
    }

    protected Calendar getMinDate() {
        return this.mMinDate;
    }

    protected Calendar getCurrentDate() {
        return this.mCurrentDate;
    }

    protected NumberPicker getPositionInYearSpinner() {
        return this.mPositionInYearSpinner;
    }

    protected NumberPicker getYearSpinner() {
        return this.mYearSpinner;
    }

    protected void updateSpinners() {
        this.mPositionInYearSpinner.setDisplayedValues(null);
        this.mPositionInYearSpinner.setMinValue(getMinPositionInYear(getYear()));
        this.mPositionInYearSpinner.setMaxValue(getMaxPositionInYear(getYear()));
        NumberPicker numberPicker = this.mPositionInYearSpinner;
        boolean z = (this.mCurrentDate.equals(this.mMinDate) || this.mCurrentDate.equals(this.mMaxDate)) ? false : true;
        numberPicker.setWrapSelectorWheel(z);
        this.mYearSpinner.setMinValue(getMinYear());
        this.mYearSpinner.setMaxValue(getMaxYear());
        this.mYearSpinner.setWrapSelectorWheel(false);
        this.mYearSpinner.setValue(getYear());
        this.mPositionInYearSpinner.setValue(getPositionInYear());
    }

    protected void notifyDateChanged() {
        sendAccessibilityEvent(4);
        if (this.mMonthOrWeekChangedListener != null) {
            this.mMonthOrWeekChangedListener.onMonthOrWeekChanged(this, getYear(), getPositionInYear());
        }
    }
}
