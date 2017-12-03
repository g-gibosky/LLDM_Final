package org.xwalk.core;

import android.content.Context;
import android.util.Log;
import org.xwalk.core.XWalkLibraryLoader.ActivateListener;
import org.xwalk.core.XWalkLibraryLoader.DecompressListener;

public class XWalkInitializer {
    private static final String TAG = "XWalkLib";
    private Context mContext;
    private XWalkInitListener mInitListener;
    private boolean mIsXWalkReady;

    public interface XWalkInitListener {
        void onXWalkInitCancelled();

        void onXWalkInitCompleted();

        void onXWalkInitFailed();

        void onXWalkInitStarted();
    }

    private class XWalkLibraryListener implements DecompressListener, ActivateListener {
        private XWalkLibraryListener() {
        }

        public void onDecompressStarted() {
        }

        public void onDecompressCancelled() {
            XWalkInitializer.this.mInitListener.onXWalkInitCancelled();
        }

        public void onDecompressCompleted() {
            XWalkLibraryLoader.startActivate(this);
        }

        public void onActivateStarted() {
        }

        public void onActivateFailed() {
            XWalkInitializer.this.mInitListener.onXWalkInitFailed();
        }

        public void onActivateCompleted() {
            XWalkInitializer.this.mIsXWalkReady = true;
            XWalkLibraryLoader.finishInit(XWalkInitializer.this.mContext);
            XWalkInitializer.this.mInitListener.onXWalkInitCompleted();
        }
    }

    public XWalkInitializer(XWalkInitListener listener, Context context) {
        this.mInitListener = listener;
        this.mContext = context;
        XWalkLibraryLoader.prepareToInit(this.mContext);
    }

    public boolean initAsync() {
        if (this.mIsXWalkReady) {
            return false;
        }
        if (XWalkLibraryLoader.isInitializing() || XWalkLibraryLoader.isDownloading()) {
            Log.d(TAG, "Other initialization or download is proceeding");
            return false;
        }
        Log.d(TAG, "Initialized by XWalkInitializer");
        XWalkLibraryLoader.startDecompress(new XWalkLibraryListener());
        this.mInitListener.onXWalkInitStarted();
        return true;
    }

    public boolean cancelInit() {
        Log.d(TAG, "Cancel by XWalkInitializer");
        return XWalkLibraryLoader.cancelDecompress();
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
}
