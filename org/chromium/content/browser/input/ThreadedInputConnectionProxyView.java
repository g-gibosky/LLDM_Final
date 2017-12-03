package org.chromium.content.browser.input;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.chromium.base.ThreadUtils;

public class ThreadedInputConnectionProxyView extends View {
    private static final boolean DEBUG_LOGS = false;
    private static final String TAG = "cr_Ime";
    private final View mContainerView;
    private final AtomicBoolean mFocused = new AtomicBoolean();
    private final Handler mImeThreadHandler;
    private final AtomicReference<View> mRootView = new AtomicReference();
    private final AtomicBoolean mWindowFocused = new AtomicBoolean();
    private final AtomicReference<IBinder> mWindowToken = new AtomicReference();

    ThreadedInputConnectionProxyView(Context context, Handler imeThreadHandler, View containerView) {
        super(context);
        this.mImeThreadHandler = imeThreadHandler;
        this.mContainerView = containerView;
        setFocusable(true);
        setFocusableInTouchMode(true);
        setVisibility(0);
        this.mFocused.set(this.mContainerView.hasFocus());
        this.mWindowFocused.set(this.mContainerView.hasWindowFocus());
        this.mWindowToken.set(this.mContainerView.getWindowToken());
        this.mRootView.set(this.mContainerView.getRootView());
    }

    public void onOriginalViewFocusChanged(boolean gainFocus) {
        this.mFocused.set(gainFocus);
    }

    public void onOriginalViewWindowFocusChanged(boolean gainFocus) {
        this.mWindowFocused.set(gainFocus);
    }

    public void onOriginalViewAttachedToWindow() {
        this.mWindowToken.set(this.mContainerView.getWindowToken());
        this.mRootView.set(this.mContainerView.getRootView());
    }

    public void onOriginalViewDetachedFromWindow() {
        this.mWindowToken.set(null);
        this.mRootView.set(null);
    }

    public Handler getHandler() {
        return this.mImeThreadHandler;
    }

    public boolean checkInputConnectionProxy(View view) {
        return this.mContainerView == view;
    }

    public InputConnection onCreateInputConnection(final EditorInfo outAttrs) {
        return (InputConnection) ThreadUtils.runOnUiThreadBlockingNoException(new Callable<InputConnection>() {
            public InputConnection call() throws Exception {
                return ThreadedInputConnectionProxyView.this.mContainerView.onCreateInputConnection(outAttrs);
            }
        });
    }

    public boolean hasWindowFocus() {
        return this.mWindowFocused.get();
    }

    public View getRootView() {
        return (View) this.mRootView.get();
    }

    public boolean onCheckIsTextEditor() {
        return true;
    }

    public boolean isFocused() {
        return this.mFocused.get();
    }

    public IBinder getWindowToken() {
        return (IBinder) this.mWindowToken.get();
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }
}
