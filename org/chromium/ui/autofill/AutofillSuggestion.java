package org.chromium.ui.autofill;

import org.chromium.ui.DropdownItem;

public class AutofillSuggestion implements DropdownItem {
    private final boolean mDeletable;
    private final int mIconId;
    private final boolean mIsMultilineLabel;
    private final String mLabel;
    private final String mSublabel;
    private final int mSuggestionId;

    public AutofillSuggestion(String label, String sublabel, int iconId, int suggestionId, boolean deletable, boolean multilineLabel) {
        this.mLabel = label;
        this.mSublabel = sublabel;
        this.mIconId = iconId;
        this.mSuggestionId = suggestionId;
        this.mDeletable = deletable;
        this.mIsMultilineLabel = multilineLabel;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public String getSublabel() {
        return this.mSublabel;
    }

    public int getIconId() {
        return this.mIconId;
    }

    public boolean isEnabled() {
        return true;
    }

    public boolean isGroupHeader() {
        return false;
    }

    public boolean isMultilineLabel() {
        return this.mIsMultilineLabel;
    }

    public int getSuggestionId() {
        return this.mSuggestionId;
    }

    public boolean isDeletable() {
        return this.mDeletable;
    }

    public boolean isFillable() {
        return this.mSuggestionId >= 0;
    }
}
