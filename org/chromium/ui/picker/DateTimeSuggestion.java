package org.chromium.ui.picker;

import android.text.TextUtils;

public class DateTimeSuggestion {
    private final String mLabel;
    private final String mLocalizedValue;
    private final double mValue;

    public DateTimeSuggestion(double value, String localizedValue, String label) {
        this.mValue = value;
        this.mLocalizedValue = localizedValue;
        this.mLabel = label;
    }

    double value() {
        return this.mValue;
    }

    String localizedValue() {
        return this.mLocalizedValue;
    }

    String label() {
        return this.mLabel;
    }

    public boolean equals(Object object) {
        if (!(object instanceof DateTimeSuggestion)) {
            return false;
        }
        DateTimeSuggestion other = (DateTimeSuggestion) object;
        if (this.mValue == other.mValue && TextUtils.equals(this.mLocalizedValue, other.mLocalizedValue) && TextUtils.equals(this.mLabel, other.mLabel)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((int) this.mValue) + 1147) * 37) + this.mLocalizedValue.hashCode()) * 37) + this.mLabel.hashCode();
    }
}
