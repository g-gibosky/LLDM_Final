package org.xwalk.core;

import android.app.Activity;
import android.os.Bundle;

public abstract class XWalkActivity extends Activity {
    private XWalkActivityDelegate mActivityDelegate;

    class C03161 implements Runnable {
        C03161() {
        }

        public void run() {
            XWalkActivity.this.onXWalkFailed();
        }
    }

    class C03172 implements Runnable {
        C03172() {
        }

        public void run() {
            XWalkActivity.this.onXWalkReady();
        }
    }

    protected abstract void onXWalkReady();

    protected void onXWalkFailed() {
        finish();
    }

    protected XWalkDialogManager getDialogManager() {
        return this.mActivityDelegate.getDialogManager();
    }

    public boolean isXWalkReady() {
        return this.mActivityDelegate.isXWalkReady();
    }

    public boolean isSharedMode() {
        return this.mActivityDelegate.isSharedMode();
    }

    public boolean isDownloadMode() {
        return this.mActivityDelegate.isDownloadMode();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivityDelegate = new XWalkActivityDelegate(this, new C03161(), new C03172());
    }

    protected void onResume() {
        super.onResume();
        this.mActivityDelegate.onResume();
    }
}
