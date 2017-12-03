package org.chromium.ui.picker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.chromium.ui.C0290R;
import org.chromium.ui.picker.TwoFieldDatePicker.OnMonthOrWeekChangedListener;

public abstract class TwoFieldDatePickerDialog extends AlertDialog implements OnClickListener, OnMonthOrWeekChangedListener {
    private static final String POSITION_IN_YEAR = "position_in_year";
    private static final String YEAR = "year";
    protected final OnValueSetListener mCallBack;
    protected final TwoFieldDatePicker mPicker;

    public interface OnValueSetListener {
        void onValueSet(int i, int i2);
    }

    public TwoFieldDatePickerDialog(Context context, OnValueSetListener callBack, int year, int positionInYear, double minValue, double maxValue) {
        this(context, 0, callBack, year, positionInYear, minValue, maxValue);
    }

    public TwoFieldDatePickerDialog(Context context, int theme, OnValueSetListener callBack, int year, int positionInYear, double minValue, double maxValue) {
        super(context, theme);
        this.mCallBack = callBack;
        setButton(-1, context.getText(C0290R.string.date_picker_dialog_set), this);
        setButton(-2, context.getText(17039360), (OnClickListener) null);
        setIcon(0);
        this.mPicker = createPicker(context, minValue, maxValue);
        setView(this.mPicker);
        this.mPicker.init(year, positionInYear, this);
    }

    protected TwoFieldDatePicker createPicker(Context context, double minValue, double maxValue) {
        return null;
    }

    public void onClick(DialogInterface dialog, int which) {
        tryNotifyDateSet();
    }

    protected void tryNotifyDateSet() {
        if (this.mCallBack != null) {
            this.mPicker.clearFocus();
            this.mCallBack.onValueSet(this.mPicker.getYear(), this.mPicker.getPositionInYear());
        }
    }

    public void onMonthOrWeekChanged(TwoFieldDatePicker view, int year, int positionInYear) {
        this.mPicker.init(year, positionInYear, null);
    }

    public void updateDate(int year, int weekOfYear) {
        this.mPicker.updateDate(year, weekOfYear);
    }
}
