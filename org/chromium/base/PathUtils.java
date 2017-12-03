package org.chromium.base;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.SystemClock;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.MainDex;
import org.chromium.base.metrics.RecordHistogram;

@MainDex
public abstract class PathUtils {
    static final /* synthetic */ boolean $assertionsDisabled = (!PathUtils.class.desiredAssertionStatus());
    private static final int CACHE_DIRECTORY = 3;
    private static final int DATABASE_DIRECTORY = 2;
    private static final int DATA_DIRECTORY = 0;
    private static final int NUM_DIRECTORIES = 4;
    private static final int THUMBNAIL_DIRECTORY = 1;
    private static final String THUMBNAIL_DIRECTORY_NAME = "textures";
    private static Context sDataDirectoryAppContext;
    private static String sDataDirectorySuffix;
    private static AsyncTask<Void, Void, String[]> sDirPathFetchTask;
    private static final AtomicBoolean sInitializationStarted = new AtomicBoolean();

    static class C01641 extends AsyncTask<Void, Void, String[]> {
        C01641() {
        }

        protected String[] doInBackground(Void... unused) {
            return PathUtils.setPrivateDataDirectorySuffixInternal();
        }
    }

    private static class Holder {
        private static final String[] DIRECTORY_PATHS = PathUtils.getOrComputeDirectoryPaths();

        private Holder() {
        }
    }

    private PathUtils() {
    }

    private static String[] getOrComputeDirectoryPaths() {
        ThreadPolicy oldPolicy;
        try {
            if (!sDirPathFetchTask.cancel(false)) {
                return (String[]) sDirPathFetchTask.get();
            }
            oldPolicy = StrictMode.allowThreadDiskReads();
            StrictMode.allowThreadDiskWrites();
            String[] privateDataDirectorySuffixInternal = setPrivateDataDirectorySuffixInternal();
            StrictMode.setThreadPolicy(oldPolicy);
            return privateDataDirectorySuffixInternal;
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e2) {
            return null;
        } catch (Throwable th) {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    private static String[] setPrivateDataDirectorySuffixInternal() {
        String[] paths = new String[4];
        paths[0] = sDataDirectoryAppContext.getDir(sDataDirectorySuffix, 0).getPath();
        paths[1] = sDataDirectoryAppContext.getDir(THUMBNAIL_DIRECTORY_NAME, 0).getPath();
        paths[2] = sDataDirectoryAppContext.getDatabasePath("foo").getParent();
        if (sDataDirectoryAppContext.getCacheDir() != null) {
            paths[3] = sDataDirectoryAppContext.getCacheDir().getPath();
        }
        return paths;
    }

    public static void setPrivateDataDirectorySuffix(String suffix, Context context) {
        if (!sInitializationStarted.getAndSet(true)) {
            sDataDirectorySuffix = suffix;
            sDataDirectoryAppContext = context.getApplicationContext();
            sDirPathFetchTask = new C01641().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    private static String getDirectoryPath(int index) {
        return Holder.DIRECTORY_PATHS[index];
    }

    @CalledByNative
    public static String getDataDirectory(Context appContext) {
        if ($assertionsDisabled || sDirPathFetchTask != null) {
            return getDirectoryPath(0);
        }
        throw new AssertionError("setDataDirectorySuffix must be called first.");
    }

    @CalledByNative
    public static String getDatabaseDirectory(Context appContext) {
        if ($assertionsDisabled || sDirPathFetchTask != null) {
            return getDirectoryPath(2);
        }
        throw new AssertionError("setDataDirectorySuffix must be called first.");
    }

    @CalledByNative
    public static String getCacheDirectory(Context appContext) {
        if ($assertionsDisabled || sDirPathFetchTask != null) {
            return getDirectoryPath(3);
        }
        throw new AssertionError("setDataDirectorySuffix must be called first.");
    }

    @CalledByNative
    public static String getThumbnailCacheDirectory(Context appContext) {
        if ($assertionsDisabled || sDirPathFetchTask != null) {
            return getDirectoryPath(1);
        }
        throw new AssertionError("setDataDirectorySuffix must be called first.");
    }

    @CalledByNative
    private static String getDownloadsDirectory(Context appContext) {
        ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        try {
            long time = SystemClock.elapsedRealtime();
            String downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            RecordHistogram.recordTimesHistogram("Android.StrictMode.DownloadsDir", SystemClock.elapsedRealtime() - time, TimeUnit.MILLISECONDS);
            return downloadsPath;
        } finally {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    @CalledByNative
    private static String getNativeLibraryDirectory(Context appContext) {
        ApplicationInfo ai = appContext.getApplicationInfo();
        if ((ai.flags & 128) != 0 || (ai.flags & 1) == 0) {
            return ai.nativeLibraryDir;
        }
        return "/system/lib/";
    }

    @CalledByNative
    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
