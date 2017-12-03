package org.chromium.content.browser.input;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.view.View;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.InputMethodManager;
import java.lang.reflect.InvocationTargetException;

public class InputMethodManagerWrapper {
    private static final boolean DEBUG_LOGS = false;
    private static final String TAG = "cr_Ime";
    private final Context mContext;

    public InputMethodManagerWrapper(Context context) {
        this.mContext = context;
    }

    private InputMethodManager getInputMethodManager() {
        return (InputMethodManager) this.mContext.getSystemService("input_method");
    }

    public void restartInput(View view) {
        getInputMethodManager().restartInput(view);
    }

    public void showSoftInput(View view, int flags, ResultReceiver resultReceiver) {
        ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        try {
            getInputMethodManager().showSoftInput(view, flags, resultReceiver);
        } finally {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    public boolean isActive(View view) {
        return getInputMethodManager().isActive(view);
    }

    public boolean hideSoftInputFromWindow(IBinder windowToken, int flags, ResultReceiver resultReceiver) {
        ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        try {
            boolean hideSoftInputFromWindow = getInputMethodManager().hideSoftInputFromWindow(windowToken, flags, resultReceiver);
            return hideSoftInputFromWindow;
        } finally {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    public void updateSelection(View view, int selStart, int selEnd, int candidatesStart, int candidatesEnd) {
        getInputMethodManager().updateSelection(view, selStart, selEnd, candidatesStart, candidatesEnd);
    }

    @TargetApi(21)
    public void updateCursorAnchorInfo(View view, CursorAnchorInfo cursorAnchorInfo) {
        if (VERSION.SDK_INT >= 21) {
            getInputMethodManager().updateCursorAnchorInfo(view, cursorAnchorInfo);
        }
    }

    public void notifyUserAction() {
        if (VERSION.SDK_INT <= 23) {
            try {
                InputMethodManager.class.getMethod("notifyUserAction", new Class[0]).invoke(getInputMethodManager(), new Object[0]);
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException e2) {
            } catch (IllegalArgumentException e3) {
            } catch (InvocationTargetException e4) {
            }
        }
    }
}
