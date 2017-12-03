package org.chromium.content.browser.input;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import org.chromium.base.VisibleForTesting;

public interface ChromiumBaseInputConnection extends InputConnection {

    public interface Factory {
        @VisibleForTesting
        Handler getHandler();

        ChromiumBaseInputConnection initializeAndGet(View view, ImeAdapter imeAdapter, int i, int i2, int i3, int i4, EditorInfo editorInfo);

        void onViewAttachedToWindow();

        void onViewDetachedFromWindow();

        void onViewFocusChanged(boolean z);

        void onWindowFocusChanged(boolean z);
    }

    @VisibleForTesting
    Handler getHandler();

    void moveCursorToSelectionEndOnUiThread();

    void onRestartInputOnUiThread();

    boolean sendKeyEventOnUiThread(KeyEvent keyEvent);

    void unblockOnUiThread();

    void updateStateOnUiThread(String str, int i, int i2, int i3, int i4, boolean z, boolean z2);
}
