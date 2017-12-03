package org.xwalk.core;

import android.app.Activity;
import android.util.Log;
import android.view.Window;
import org.xwalk.core.XWalkLibraryLoader.ActivateListener;
import org.xwalk.core.XWalkLibraryLoader.DecompressListener;
import org.xwalk.core.XWalkUpdater.XWalkBackgroundUpdateListener;
import org.xwalk.core.XWalkUpdater.XWalkUpdateListener;

public class XWalkActivityDelegate implements DecompressListener, ActivateListener {
    private static final String TAG = "XWalkLib";
    private Activity mActivity;
    private boolean mBackgroundDecorated;
    private Runnable mCancelCommand;
    private Runnable mCompleteCommand;
    private XWalkDialogManager mDialogManager = new XWalkDialogManager(this.mActivity);
    private boolean mIsXWalkReady;
    private boolean mWillDecompress;
    private XWalkUpdater mXWalkUpdater;

    class C03181 implements Runnable {
        C03181() {
        }

        public void run() {
            Log.d(XWalkActivityDelegate.TAG, "Cancel by XWalkActivity");
            XWalkLibraryLoader.cancelDecompress();
        }
    }

    class C04712 implements XWalkBackgroundUpdateListener {
        C04712() {
        }

        public void onXWalkUpdateStarted() {
        }

        public void onXWalkUpdateProgress(int percentage) {
        }

        public void onXWalkUpdateCancelled() {
            XWalkActivityDelegate.this.mCancelCommand.run();
        }

        public void onXWalkUpdateFailed() {
            XWalkActivityDelegate.this.mCancelCommand.run();
        }

        public void onXWalkUpdateCompleted() {
            XWalkLibraryLoader.startActivate(XWalkActivityDelegate.this);
        }
    }

    class C04723 implements XWalkUpdateListener {
        C04723() {
        }

        public void onXWalkUpdateCancelled() {
            XWalkActivityDelegate.this.mCancelCommand.run();
        }
    }

    public XWalkActivityDelegate(Activity activity, Runnable cancelCommand, Runnable completeCommand) {
        this.mActivity = activity;
        this.mCancelCommand = cancelCommand;
        this.mCompleteCommand = completeCommand;
        XWalkLibraryLoader.prepareToInit(this.mActivity);
    }

    public boolean isXWalkReady() {
        return this.mIsXWalkReady;
    }

    public boolean isSharedMode() {
        return this.mIsXWalkReady && XWalkLibraryLoader.isSharedLibrary();
    }

    public boolean isDownloadMode() {
        return this.mIsXWalkReady && XWalkEnvironment.isDownloadMode();
    }

    public void setXWalkApkUrl(String url) {
        XWalkEnvironment.setXWalkApkUrl(url);
    }

    public XWalkDialogManager getDialogManager() {
        return this.mDialogManager;
    }

    public void onResume() {
        if (!this.mIsXWalkReady) {
            if (XWalkLibraryLoader.isInitializing() || XWalkLibraryLoader.isDownloading()) {
                Log.d(TAG, "Other initialization or download is proceeding");
                return;
            }
            Log.d(TAG, "Initialize by XWalkActivity");
            XWalkLibraryLoader.startDecompress(this);
        }
    }

    public void onDecompressStarted() {
        this.mDialogManager.showDecompressProgress(new C03181());
        this.mWillDecompress = true;
    }

    public void onDecompressCancelled() {
        this.mWillDecompress = false;
        this.mCancelCommand.run();
    }

    public void onDecompressCompleted() {
        if (this.mWillDecompress) {
            this.mDialogManager.dismissDialog();
            this.mWillDecompress = false;
        }
        XWalkLibraryLoader.startActivate(this);
    }

    public void onActivateStarted() {
    }

    public void onActivateFailed() {
        if (this.mXWalkUpdater == null) {
            if (XWalkEnvironment.isDownloadMode()) {
                this.mXWalkUpdater = new XWalkUpdater(new C04712(), this.mActivity);
            } else {
                this.mXWalkUpdater = new XWalkUpdater(new C04723(), this.mActivity, this.mDialogManager);
            }
        }
        if (this.mXWalkUpdater.updateXWalkRuntime() && !XWalkEnvironment.isDownloadMode()) {
            Window window = this.mActivity.getWindow();
            if (window != null && window.getDecorView().getBackground() == null) {
                Log.d(TAG, "Set the background to screen_background_dark");
                window.setBackgroundDrawableResource(17301656);
                this.mBackgroundDecorated = true;
            }
        }
    }

    public void onActivateCompleted() {
        if (this.mDialogManager.isShowingDialog()) {
            this.mDialogManager.dismissDialog();
        }
        if (this.mBackgroundDecorated) {
            Log.d(TAG, "Recover the background");
            this.mActivity.getWindow().setBackgroundDrawable(null);
            this.mBackgroundDecorated = false;
        }
        this.mIsXWalkReady = true;
        XWalkLibraryLoader.finishInit(this.mActivity);
        this.mCompleteCommand.run();
    }
}
