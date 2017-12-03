package org.chromium.ui.picker;

import android.content.Context;
import org.chromium.ui.C0290R;
import org.chromium.ui.picker.TwoFieldDatePickerDialog.OnValueSetListener;

public class MonthPickerDialog extends TwoFieldDatePickerDialog {
    public MonthPickerDialog(Context context, OnValueSetListener callBack, int year, int monthOfYear, double minMonth, double maxMonth) {
        super(context, callBack, year, monthOfYear, minMonth, maxMonth);
        setTitle(C0290R.string.month_picker_dialog_title);
    }

    protected TwoFieldDatePicker createPicker(Context context, double minValue, double maxValue) {
        return new MonthPicker(context, minValue, maxValue);
    }

    public MonthPicker getMonthPicker() {
        return (MonthPicker) this.mPicker;
    }
}
