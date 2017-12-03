package org.chromium.ui;

public interface DropdownItem {
    public static final int NO_ICON = 0;

    int getIconId();

    String getLabel();

    String getSublabel();

    boolean isEnabled();

    boolean isGroupHeader();

    boolean isMultilineLabel();
}
