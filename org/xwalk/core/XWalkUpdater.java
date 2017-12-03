package org.xwalk.core;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import java.io.File;
import java.util.List;
import org.chromium.ui.base.PageTransition;
import org.xwalk.core.XWalkLibraryLoader.DownloadListener;

public class XWalkUpdater {
    private static final String ANDROID_MARKET_DETAILS = "market://details?id=";
    private static final String GOOGLE_PLAY_PACKAGE = "com.android.vending";
    private static final String TAG = "XWalkLib";
    private XWalkBackgroundUpdateListener mBackgroundUpdateListener;
    private Runnable mCancelCommand;
    private Context mContext;
    private XWalkDialogManager mDialogManager;
    private Runnable mDownloadCommand;
    private XWalkUpdateListener mUpdateListener;

    class C03221 implements Runnable {
        C03221() {
        }

        public void run() {
            XWalkUpdater.this.downloadXWalkApk();
        }
    }

    class C03232 implements Runnable {
        C03232() {
        }

        public void run() {
            Log.d(XWalkUpdater.TAG, "XWalkUpdater cancelled");
            XWalkUpdater.this.mUpdateListener.onXWalkUpdateCancelled();
        }
    }

    public interface XWalkBackgroundUpdateListener {
        void onXWalkUpdateCancelled();

        void onXWalkUpdateCompleted();

        void onXWalkUpdateFailed();

        void onXWalkUpdateProgress(int i);

        void onXWalkUpdateStarted();
    }

    public interface XWalkUpdateListener {
        void onXWalkUpdateCancelled();
    }

    private class BackgroundListener implements DownloadListener {
        private BackgroundListener() {
        }

        public void onDownloadStarted() {
            XWalkUpdater.this.mBackgroundUpdateListener.onXWalkUpdateStarted();
        }

        public void onDownloadUpdated(int percentage) {
            XWalkUpdater.this.mBackgroundUpdateListener.onXWalkUpdateProgress(percentage);
        }

        public void onDownloadCancelled() {
            XWalkUpdater.this.mBackgroundUpdateListener.onXWalkUpdateCancelled();
        }

        public void onDownloadFailed(int status, int error) {
            XWalkUpdater.this.mBackgroundUpdateListener.onXWalkUpdateFailed();
        }

        public void onDownloadCompleted(Uri uri) {
            final String libFile = uri.getPath();
            final String destDir = XWalkEnvironment.getExtractedCoreDir();
            Log.d(XWalkUpdater.TAG, "Download mode extract dir: " + destDir);
            new AsyncTask<Void, Void, Boolean>() {
                protected Boolean doInBackground(Void... params) {
                    if (XWalkEnvironment.isXWalkVerify() && !XWalkUpdater.this.verifyDownloadedXWalkRuntime(libFile)) {
                        return Boolean.valueOf(false);
                    }
                    if (XWalkDecompressor.isResourceCompressed(libFile)) {
                        if (!XWalkDecompressor.decompressResource(libFile, destDir)) {
                            return Boolean.valueOf(false);
                        }
                    } else if (!XWalkDecompressor.extractResource(libFile, destDir)) {
                        return Boolean.valueOf(false);
                    }
                    return Boolean.valueOf(true);
                }

                protected void onPostExecute(Boolean result) {
                    new File(libFile).delete();
                    if (result.booleanValue()) {
                        XWalkUpdater.this.mBackgroundUpdateListener.onXWalkUpdateCompleted();
                    } else {
                        XWalkUpdater.this.mBackgroundUpdateListener.onXWalkUpdateFailed();
                    }
                }
            }.execute(new Void[0]);
        }
    }

    private class ForegroundListener implements DownloadListener {

        class C03261 implements Runnable {
            C03261() {
            }

            public void run() {
                XWalkLibraryLoader.cancelDownloadManager();
            }
        }

        private ForegroundListener() {
        }

        public void onDownloadStarted() {
            XWalkUpdater.this.mDialogManager.showDownloadProgress(new C03261());
        }

        public void onDownloadUpdated(int percentage) {
            XWalkUpdater.this.mDialogManager.setProgress(percentage, 100);
        }

        public void onDownloadCancelled() {
            XWalkUpdater.this.mUpdateListener.onXWalkUpdateCancelled();
        }

        public void onDownloadFailed(int status, int error) {
            XWalkUpdater.this.mDialogManager.dismissDialog();
            XWalkUpdater.this.mDialogManager.showDownloadError(XWalkUpdater.this.mCancelCommand, XWalkUpdater.this.mDownloadCommand);
        }

        public void onDownloadCompleted(Uri uri) {
            XWalkUpdater.this.mDialogManager.dismissDialog();
            Log.d(XWalkUpdater.TAG, "Install the Crosswalk runtime: " + uri.toString());
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setFlags(PageTransition.CHAIN_START);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            XWalkUpdater.this.mContext.startActivity(intent);
        }
    }

    public XWalkUpdater(XWalkUpdateListener listener, Context context) {
        this.mUpdateListener = listener;
        this.mContext = context;
        this.mDialogManager = new XWalkDialogManager(context);
    }

    public XWalkUpdater(XWalkUpdateListener listener, Context context, XWalkDialogManager dialogManager) {
        this.mUpdateListener = listener;
        this.mContext = context;
        this.mDialogManager = dialogManager;
    }

    public XWalkUpdater(XWalkBackgroundUpdateListener listener, Context context) {
        this.mBackgroundUpdateListener = listener;
        this.mContext = context;
    }

    public boolean updateXWalkRuntime() {
        if (XWalkLibraryLoader.isInitializing() || XWalkLibraryLoader.isDownloading()) {
            Log.d(TAG, "Other initialization or download is proceeding");
            return false;
        } else if (XWalkLibraryLoader.isLibraryReady()) {
            Log.d(TAG, "Initialization has been completed. Do not need to update");
            return false;
        } else {
            int status = XWalkLibraryLoader.getLibraryStatus();
            if (status == 0) {
                throw new RuntimeException("Must invoke XWalkInitializer.initAsync() first");
            }
            if (this.mUpdateListener != null) {
                this.mDownloadCommand = new C03221();
                this.mCancelCommand = new C03232();
                this.mDialogManager.showInitializationError(status, this.mCancelCommand, this.mDownloadCommand);
            } else if (this.mBackgroundUpdateListener != null) {
                XWalkLibraryLoader.startHttpDownload(new BackgroundListener(), this.mContext, XWalkEnvironment.getXWalkApkUrl());
            } else {
                throw new IllegalArgumentException("Update listener is null");
            }
            return true;
        }
    }

    public boolean dismissDialog() {
        if (this.mDialogManager == null || !this.mDialogManager.isShowingDialog()) {
            return false;
        }
        this.mDialogManager.dismissDialog();
        return true;
    }

    public void setXWalkApkUrl(String url) {
        XWalkEnvironment.setXWalkApkUrl(url);
    }

    public boolean cancelBackgroundDownload() {
        return XWalkLibraryLoader.cancelHttpDownload();
    }

    private void downloadXWalkApk() {
        String url = XWalkEnvironment.getXWalkApkUrl();
        if (url.isEmpty()) {
            String storeName;
            String packageName = XWalkLibraryInterface.XWALK_CORE_PACKAGE;
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(ANDROID_MARKET_DETAILS + packageName));
            List<ResolveInfo> infos = this.mContext.getPackageManager().queryIntentActivities(intent, 131072);
            StringBuilder supportedStores = new StringBuilder();
            boolean hasGooglePlay = false;
            Log.d(TAG, "Available Stores:");
            for (ResolveInfo info : infos) {
                Log.d(TAG, info.activityInfo.packageName);
                hasGooglePlay |= info.activityInfo.packageName.equals(GOOGLE_PLAY_PACKAGE);
                storeName = getStoreName(info.activityInfo.packageName);
                if (storeName != null) {
                    if (supportedStores.length() > 0) {
                        supportedStores.append("/");
                    }
                    supportedStores.append(storeName);
                }
            }
            if (supportedStores.length() == 0) {
                this.mDialogManager.showUnsupportedStore(this.mCancelCommand);
                return;
            }
            if (hasGooglePlay || !XWalkEnvironment.isIaDevice()) {
                if (XWalkEnvironment.is64bitApp()) {
                    packageName = XWalkLibraryInterface.XWALK_CORE64_PACKAGE;
                } else {
                    packageName = XWalkLibraryInterface.XWALK_CORE_PACKAGE;
                }
            } else if (XWalkEnvironment.is64bitApp()) {
                packageName = XWalkLibraryInterface.XWALK_CORE64_IA_PACKAGE;
            } else {
                packageName = XWalkLibraryInterface.XWALK_CORE_IA_PACKAGE;
            }
            Log.d(TAG, "Package name of Crosswalk to download: " + packageName);
            intent.setData(Uri.parse(ANDROID_MARKET_DETAILS + packageName));
            final Intent storeIntent = intent;
            storeName = hasGooglePlay ? getStoreName(GOOGLE_PLAY_PACKAGE) : supportedStores.toString();
            Log.d(TAG, "Supported Stores: " + storeName);
            this.mDialogManager.showSelectStore(new Runnable() {
                public void run() {
                    try {
                        XWalkUpdater.this.mContext.startActivity(storeIntent);
                    } catch (ActivityNotFoundException e) {
                        XWalkUpdater.this.mDialogManager.showUnsupportedStore(XWalkUpdater.this.mCancelCommand);
                    }
                }
            }, storeName);
            return;
        }
        XWalkLibraryLoader.startDownloadManager(new ForegroundListener(), this.mContext, url);
    }

    private boolean verifyDownloadedXWalkRuntime(String libFile) {
        PackageInfo runtimePkgInfo = this.mContext.getPackageManager().getPackageArchiveInfo(libFile, 64);
        if (runtimePkgInfo == null) {
            Log.e(TAG, "The downloaded XWalkRuntimeLib.apk is invalid!");
            return false;
        }
        try {
            PackageInfo appPkgInfo = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 64);
            if (runtimePkgInfo.signatures == null || appPkgInfo.signatures == null) {
                Log.e(TAG, "No signature in package info");
                return false;
            } else if (runtimePkgInfo.signatures.length != appPkgInfo.signatures.length) {
                Log.e(TAG, "signatures length not equal");
                return false;
            } else {
                int i = 0;
                while (i < runtimePkgInfo.signatures.length) {
                    Log.d(TAG, "Checking signature " + i);
                    if (appPkgInfo.signatures[i].equals(runtimePkgInfo.signatures[i])) {
                        i++;
                    } else {
                        Log.e(TAG, "signatures do not match");
                        return false;
                    }
                }
                Log.d(TAG, "Signature check passed");
                return true;
            }
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    private String getStoreName(String storePackage) {
        if (storePackage.equals(GOOGLE_PLAY_PACKAGE)) {
            return this.mContext.getString(C0315R.string.google_play_store);
        }
        return null;
    }
}
