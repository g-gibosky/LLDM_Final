package org.chromium.content.browser;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.view.ActionMode;
import android.view.ActionMode.Callback2;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

@TargetApi(23)
public class FloatingWebActionModeCallback extends Callback2 {
    private final WebActionModeCallback mWrappedCallback;

    public FloatingWebActionModeCallback(WebActionModeCallback wrappedCallback) {
        this.mWrappedCallback = wrappedCallback;
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (mode.getType() != 1) {
            return false;
        }
        return this.mWrappedCallback.onCreateActionMode(mode, menu);
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return this.mWrappedCallback.onPrepareActionMode(mode, menu);
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return this.mWrappedCallback.onActionItemClicked(mode, item);
    }

    public void onDestroyActionMode(ActionMode mode) {
        this.mWrappedCallback.onDestroyActionMode(mode);
    }

    public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
        this.mWrappedCallback.onGetContentRect(mode, view, outRect);
    }
}
