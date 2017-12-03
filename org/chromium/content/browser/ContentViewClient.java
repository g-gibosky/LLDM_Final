package org.chromium.content.browser;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View.MeasureSpec;
import org.chromium.base.Log;
import org.chromium.ui.base.PageTransition;

public class ContentViewClient {
    private static final String TAG = "cr.ContentViewClient";
    private static final int UNSPECIFIED_MEASURE_SPEC = MeasureSpec.makeMeasureSpec(0, 0);

    public void onUpdateTitle(String title) {
    }

    public void onBackgroundColorChanged(int color) {
    }

    public void onOffsetsForFullscreenChanged(float topControlsOffsetYPix, float contentOffsetYPix) {
    }

    public boolean shouldOverrideKeyEvent(KeyEvent event) {
        if (shouldPropagateKey(event.getKeyCode())) {
            return false;
        }
        return true;
    }

    public void onImeEvent() {
    }

    public void onFocusedNodeEditabilityChanged(boolean editable) {
    }

    public void onContextualActionBarShown() {
    }

    public void onContextualActionBarHidden() {
    }

    public void performWebSearch(String searchQuery) {
    }

    public boolean doesPerformWebSearch() {
        return false;
    }

    public boolean doesPerformProcessText() {
        return false;
    }

    public void startProcessTextIntent(Intent intent) {
    }

    public boolean isSelectActionModeAllowed(int actionModeItem) {
        return true;
    }

    public void onStartContentIntent(Context context, String intentUrl, boolean isMainFrame) {
        try {
            Intent intent = Intent.parseUri(intentUrl, 1);
            intent.addFlags(PageTransition.CHAIN_START);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.m38w(TAG, "No application can handle %s", intentUrl);
            }
        } catch (Exception ex) {
            Log.m38w(TAG, "Bad URI %s", intentUrl, ex);
        }
    }

    public ContentVideoViewEmbedder getContentVideoViewEmbedder() {
        return null;
    }

    public boolean shouldBlockMediaRequest(String url) {
        return false;
    }

    public static boolean shouldPropagateKey(int keyCode) {
        if (keyCode == 82 || keyCode == 3 || keyCode == 4 || keyCode == 5 || keyCode == 6 || keyCode == 26 || keyCode == 79 || keyCode == 27 || keyCode == 80 || keyCode == 25 || keyCode == 164 || keyCode == 24) {
            return false;
        }
        return true;
    }

    public int getDesiredWidthMeasureSpec() {
        return UNSPECIFIED_MEASURE_SPEC;
    }

    public int getDesiredHeightMeasureSpec() {
        return UNSPECIFIED_MEASURE_SPEC;
    }

    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
    }

    public int getSystemWindowInsetLeft() {
        return 0;
    }

    public int getSystemWindowInsetTop() {
        return 0;
    }

    public int getSystemWindowInsetRight() {
        return 0;
    }

    public int getSystemWindowInsetBottom() {
        return 0;
    }

    public String getProductVersion() {
        return "";
    }
}
