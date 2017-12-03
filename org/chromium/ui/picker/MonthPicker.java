package org.chromium.ui.picker;

import android.content.Context;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import org.chromium.ui.C0290R;

public class MonthPicker extends TwoFieldDatePicker {
    private static final int MONTHS_NUMBER = 12;
    private final String[] mShortMonths = DateFormatSymbols.getInstance(Locale.getDefault()).getShortMonths();

    public MonthPicker(Context context, double minValue, double maxValue) {
        super(context, minValue, maxValue);
        getPositionInYearSpinner().setContentDescription(getResources().getString(C0290R.string.accessibility_date_picker_month));
        if (usingNumericMonths()) {
            for (int i = 0; i < this.mShortMonths.length; i++) {
                this.mShortMonths[i] = String.format("%d", new Object[]{Integer.valueOf(i + 1)});
            }
        }
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        init(cal.get(1), cal.get(2), null);
    }

    protected boolean usingNumericMonths() {
        return Character.isDigit(this.mShortMonths[0].charAt(0));
    }

    public static Calendar createDateFromValue(double value) {
        int year = (int) Math.min((value / 12.0d) + 1970.0d, 2.147483647E9d);
        int month = (int) (value % 12.0d);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(year, month, 1);
        return cal;
    }

    protected Calendar getDateForValue(double value) {
        return createDateFromValue(value);
    }

    protected void setCurrentDate(int year, int month) {
        Calendar date = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        date.set(year, month, 1);
        if (date.before(getMinDate())) {
            setCurrentDate(getMinDate());
        } else if (date.after(getMaxDate())) {
            setCurrentDate(getMaxDate());
        } else {
            setCurrentDate(date);
        }
    }

    protected void updateSpinners() {
        super.updateSpinners();
        getPositionInYearSpinner().setDisplayedValues((String[]) Arrays.copyOfRange(this.mShortMonths, getPositionInYearSpinner().getMinValue(), getPositionInYearSpinner().getMaxValue() + 1));
    }

    public int getMonth() {
        return getCurrentDate().get(2);
    }

    public int getPositionInYear() {
        return getMonth();
    }

    protected int getMaxYear() {
        return getMaxDate().get(1);
    }

    protected int getMinYear() {
        return getMinDate().get(1);
    }

    protected int getMaxPositionInYear(int year) {
        if (year == getMaxDate().get(1)) {
            return getMaxDate().get(2);
        }
        return 11;
    }

    protected int getMinPositionInYear(int year) {
        if (year == getMinDate().get(1)) {
            return getMinDate().get(2);
        }
        return 0;
    }
}
