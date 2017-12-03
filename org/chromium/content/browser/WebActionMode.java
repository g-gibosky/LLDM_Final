package org.chromium.content.browser;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.view.ActionMode;
import android.view.View;
import android.view.ViewConfiguration;
import org.chromium.base.Log;

@TargetApi(23)
public class WebActionMode {
    static final /* synthetic */ boolean $assertionsDisabled = (!WebActionMode.class.desiredAssertionStatus());
    private static final int SHOW_DELAY_MS = 300;
    private static final String TAG = "cr.WebActionMode";
    protected final ActionMode mActionMode;
    private boolean mHidden;
    private boolean mPendingInvalidateContentRect;
    private final Runnable mRepeatingHideRunnable;
    private final View mView;

    class C02071 implements Runnable {
        static final /* synthetic */ boolean $assertionsDisabled = (!WebActionMode.class.desiredAssertionStatus());

        C02071() {
        }

        public void run() {
            if ($assertionsDisabled || WebActionMode.this.mHidden) {
                long hideDuration = WebActionMode.this.getDefaultHideDuration();
                WebActionMode.this.mView.postDelayed(WebActionMode.this.mRepeatingHideRunnable, hideDuration - 1);
                WebActionMode.this.hideTemporarily(hideDuration);
                return;
            }
            throw new AssertionError();
        }
    }

    public WebActionMode(ActionMode actionMode, View view) {
        if (!$assertionsDisabled && actionMode == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || view != null) {
            this.mActionMode = actionMode;
            this.mView = view;
            this.mRepeatingHideRunnable = new C02071();
        } else {
            throw new AssertionError();
        }
    }

    public void finish() {
        this.mActionMode.finish();
    }

    public void invalidate() {
        if (this.mHidden) {
            if ($assertionsDisabled || canHide()) {
                this.mHidden = false;
                this.mView.removeCallbacks(this.mRepeatingHideRunnable);
                this.mPendingInvalidateContentRect = false;
            } else {
                throw new AssertionError();
            }
        }
        try {
            this.mActionMode.invalidate();
        } catch (NullPointerException e) {
            Log.m38w(TAG, "Ignoring NPE from ActionMode.invalidate() as workaround for L", e);
        }
    }

    public void invalidateContentRect() {
        if (VERSION.SDK_INT < 23) {
            return;
        }
        if (this.mHidden) {
            this.mPendingInvalidateContentRect = true;
            return;
        }
        this.mPendingInvalidateContentRect = false;
        this.mActionMode.invalidateContentRect();
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (VERSION.SDK_INT >= 23) {
            this.mActionMode.onWindowFocusChanged(hasWindowFocus);
        }
    }

    public void hide(boolean hide) {
        if (canHide() && this.mHidden != hide) {
            this.mHidden = hide;
            if (this.mHidden) {
                this.mRepeatingHideRunnable.run();
                return;
            }
            this.mHidden = false;
            this.mView.removeCallbacks(this.mRepeatingHideRunnable);
            hideTemporarily(300);
            if (this.mPendingInvalidateContentRect) {
                this.mPendingInvalidateContentRect = false;
                invalidateContentRect();
            }
        }
    }

    private void hideTemporarily(long duration) {
        if (!$assertionsDisabled && !canHide()) {
            throw new AssertionError();
        } else if (VERSION.SDK_INT >= 23) {
            this.mActionMode.hide(duration);
        }
    }

    private boolean canHide() {
        if (VERSION.SDK_INT < 23) {
            return false;
        }
        if (this.mActionMode.getType() == 1) {
            return true;
        }
        return false;
    }

    private long getDefaultHideDuration() {
        if (VERSION.SDK_INT >= 23) {
            return ViewConfiguration.getDefaultActionModeHideDuration();
        }
        return 2000;
    }
}
