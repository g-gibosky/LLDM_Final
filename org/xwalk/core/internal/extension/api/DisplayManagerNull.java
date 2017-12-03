package org.xwalk.core.internal.extension.api;

import android.view.Display;

public class DisplayManagerNull extends XWalkDisplayManager {
    private static final Display[] NO_DISPLAYS = new Display[0];

    public Display getDisplay(int displayId) {
        return null;
    }

    public Display[] getDisplays() {
        return NO_DISPLAYS;
    }

    public Display[] getDisplays(String category) {
        return NO_DISPLAYS;
    }

    public Display[] getPresentationDisplays() {
        return NO_DISPLAYS;
    }
}
