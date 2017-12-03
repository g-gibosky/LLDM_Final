package org.chromium.ui.base;

import android.view.View;

public interface ViewAndroidDelegate {
    View acquireAnchorView();

    void releaseAnchorView(View view);

    void setAnchorViewPosition(View view, float f, float f2, float f3, float f4);
}
