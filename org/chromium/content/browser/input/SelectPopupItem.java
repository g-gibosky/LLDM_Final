package org.chromium.content.browser.input;

import org.chromium.ui.DropdownItem;

public class SelectPopupItem implements DropdownItem {
    private final String mLabel;
    private final int mType;

    public SelectPopupItem(String label, int type) {
        this.mLabel = label;
        this.mType = type;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public String getSublabel() {
        return null;
    }

    public int getIconId() {
        return 0;
    }

    public boolean isEnabled() {
        return this.mType == 2 || this.mType == 0;
    }

    public boolean isGroupHeader() {
        return this.mType == 0;
    }

    public boolean isMultilineLabel() {
        return false;
    }

    public int getType() {
        return this.mType;
    }
}
