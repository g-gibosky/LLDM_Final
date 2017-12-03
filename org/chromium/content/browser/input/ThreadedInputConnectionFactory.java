package org.chromium.content.browser.input;

import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import org.chromium.base.VisibleForTesting;
import org.chromium.content.browser.input.ChromiumBaseInputConnection.Factory;

public class ThreadedInputConnectionFactory implements Factory {
    private static final int CHECK_REGISTER_RETRY = 1;
    private static final boolean DEBUG_LOGS = false;
    private static final String TAG = "cr_Ime";
    private CheckInvalidator mCheckInvalidator;
    private final Handler mHandler = createHandler();
    private final InputMethodManagerWrapper mInputMethodManagerWrapper;
    private final InputMethodUma mInputMethodUma = createInputMethodUma();
    private ThreadedInputConnectionProxyView mProxyView;
    private boolean mReentrantTriggering;
    private ThreadedInputConnection mThreadedInputConnection;

    private static class CheckInvalidator {
        private boolean mInvalid;

        private CheckInvalidator() {
        }

        public void invalidate() {
            ImeUtils.checkOnUiThread();
            this.mInvalid = true;
        }

        public boolean isInvalid() {
            ImeUtils.checkOnUiThread();
            return this.mInvalid;
        }
    }

    ThreadedInputConnectionFactory(InputMethodManagerWrapper inputMethodManagerWrapper) {
        this.mInputMethodManagerWrapper = inputMethodManagerWrapper;
    }

    @VisibleForTesting
    protected Handler createHandler() {
        HandlerThread thread = new HandlerThread("InputConnectionHandlerThread", 5);
        thread.start();
        return new Handler(thread.getLooper());
    }

    @VisibleForTesting
    protected ThreadedInputConnectionProxyView createProxyView(Handler handler, View containerView) {
        return new ThreadedInputConnectionProxyView(containerView.getContext(), handler, containerView);
    }

    @VisibleForTesting
    protected InputMethodUma createInputMethodUma() {
        return new InputMethodUma();
    }

    private boolean shouldTriggerDelayedOnCreateInputConnection() {
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            String className = ste.getClassName();
            if (className != null && (className.contains(ThreadedInputConnectionProxyView.class.getName()) || className.contains("TestInputMethodManagerWrapper"))) {
                return false;
            }
        }
        return true;
    }

    public ThreadedInputConnection initializeAndGet(View view, ImeAdapter imeAdapter, int inputType, int inputFlags, int selectionStart, int selectionEnd, EditorInfo outAttrs) {
        ImeUtils.checkOnUiThread();
        ImeUtils.computeEditorInfo(inputType, inputFlags, selectionStart, selectionEnd, outAttrs);
        if (this.mCheckInvalidator != null) {
            this.mCheckInvalidator.invalidate();
        }
        if (shouldTriggerDelayedOnCreateInputConnection()) {
            triggerDelayedOnCreateInputConnection(view);
            return null;
        }
        if (this.mThreadedInputConnection == null) {
            this.mThreadedInputConnection = new ThreadedInputConnection(view, imeAdapter, this.mHandler);
        } else {
            this.mThreadedInputConnection.resetOnUiThread();
        }
        return this.mThreadedInputConnection;
    }

    private void triggerDelayedOnCreateInputConnection(final View view) {
        if (!this.mReentrantTriggering && view.hasFocus() && view.hasWindowFocus()) {
            this.mCheckInvalidator = new CheckInvalidator();
            if (this.mProxyView == null) {
                this.mProxyView = createProxyView(this.mHandler, view);
            }
            this.mReentrantTriggering = true;
            this.mProxyView.requestFocus();
            this.mReentrantTriggering = false;
            view.getHandler().post(new Runnable() {

                class C02331 implements Runnable {
                    C02331() {
                    }

                    public void run() {
                        ThreadedInputConnectionFactory.this.postCheckRegisterResultOnUiThread(view, ThreadedInputConnectionFactory.this.mCheckInvalidator, 1);
                    }
                }

                public void run() {
                    if (!ThreadedInputConnectionFactory.this.mCheckInvalidator.isInvalid()) {
                        ThreadedInputConnectionFactory.this.mProxyView.onWindowFocusChanged(true);
                        ThreadedInputConnectionFactory.this.mInputMethodManagerWrapper.isActive(view);
                        ThreadedInputConnectionFactory.this.mHandler.post(new C02331());
                    }
                }
            });
        }
    }

    private void postCheckRegisterResultOnUiThread(final View view, final CheckInvalidator checkInvalidator, final int retry) {
        Handler viewHandler = view.getHandler();
        if (viewHandler != null) {
            viewHandler.post(new Runnable() {
                public void run() {
                    ThreadedInputConnectionFactory.this.checkRegisterResult(view, checkInvalidator, retry);
                }
            });
        }
    }

    private void checkRegisterResult(View view, CheckInvalidator checkInvalidator, int retry) {
        if (this.mInputMethodManagerWrapper.isActive(this.mProxyView)) {
            this.mInputMethodUma.recordProxyViewSuccess();
        } else if (!checkInvalidator.isInvalid()) {
            if (retry > 0) {
                postCheckRegisterResultOnUiThread(view, checkInvalidator, retry - 1);
            } else {
                onRegisterProxyViewFailed();
            }
        }
    }

    @VisibleForTesting
    protected void onRegisterProxyViewFailed() {
        this.mInputMethodUma.recordProxyViewFailure();
        throw new AssertionError("Failed to register proxy view");
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void onWindowFocusChanged(boolean gainFocus) {
        if (!(gainFocus || this.mCheckInvalidator == null)) {
            this.mCheckInvalidator.invalidate();
        }
        if (this.mProxyView != null) {
            this.mProxyView.onOriginalViewWindowFocusChanged(gainFocus);
        }
    }

    public void onViewFocusChanged(boolean gainFocus) {
        if (!(gainFocus || this.mCheckInvalidator == null)) {
            this.mCheckInvalidator.invalidate();
        }
        if (this.mProxyView != null) {
            this.mProxyView.onOriginalViewFocusChanged(gainFocus);
        }
    }

    public void onViewAttachedToWindow() {
        if (this.mProxyView != null) {
            this.mProxyView.onOriginalViewAttachedToWindow();
        }
    }

    public void onViewDetachedFromWindow() {
        if (this.mCheckInvalidator != null) {
            this.mCheckInvalidator.invalidate();
        }
        if (this.mProxyView != null) {
            this.mProxyView.onOriginalViewDetachedFromWindow();
        }
    }
}
