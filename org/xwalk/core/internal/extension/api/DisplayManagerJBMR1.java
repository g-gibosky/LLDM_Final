package org.xwalk.core.internal.extension.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.view.Display;

@SuppressLint({"NewApi"})
public class DisplayManagerJBMR1 extends XWalkDisplayManager implements DisplayListener {
    private DisplayManager mDisplayManager;

    public DisplayManagerJBMR1(Context context) {
        this.mDisplayManager = (DisplayManager) context.getSystemService("display");
    }

    public Display getDisplay(int displayId) {
        return this.mDisplayManager.getDisplay(displayId);
    }

    public Display[] getDisplays() {
        return this.mDisplayManager.getDisplays();
    }

    public Display[] getDisplays(String category) {
        return this.mDisplayManager.getDisplays(category);
    }

    public Display[] getPresentationDisplays() {
        return this.mDisplayManager.getDisplays(DisplayManagerCompat.DISPLAY_CATEGORY_PRESENTATION);
    }

    public void registerDisplayListener(XWalkDisplayManager.DisplayListener listener) {
        super.registerDisplayListener(listener);
        if (this.mListeners.size() == 1) {
            this.mDisplayManager.registerDisplayListener(this, null);
        }
    }

    public void unregisterDisplayListener(XWalkDisplayManager.DisplayListener listener) {
        super.unregisterDisplayListener(listener);
        if (this.mListeners.size() == 0) {
            this.mDisplayManager.unregisterDisplayListener(this);
        }
    }

    public void onDisplayAdded(int displayId) {
        notifyDisplayAdded(displayId);
    }

    public void onDisplayRemoved(int displayId) {
        notifyDisplayRemoved(displayId);
    }

    public void onDisplayChanged(int displayId) {
        notifyDisplayChanged(displayId);
    }
}
