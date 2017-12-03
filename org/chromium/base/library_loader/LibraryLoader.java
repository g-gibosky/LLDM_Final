package org.chromium.base.library_loader;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import org.chromium.base.CommandLine;
import org.chromium.base.ContextUtils;
import org.chromium.base.Log;
import org.chromium.base.TraceEvent;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.metrics.RecordHistogram;

@JNINamespace("base::android")
public class LibraryLoader {
    static final /* synthetic */ boolean $assertionsDisabled = (!LibraryLoader.class.desiredAssertionStatus());
    private static final boolean DEBUG = false;
    private static final String TAG = "LibraryLoader";
    private static volatile LibraryLoader sInstance;
    private static NativeLibraryPreloader sLibraryPreloader;
    private static final Object sLock = new Object();
    private boolean mCommandLineSwitched;
    private volatile boolean mInitialized;
    private boolean mIsUsingBrowserSharedRelros;
    private long mLibraryLoadTimeMs;
    private int mLibraryPreloaderStatus = -1;
    private final int mLibraryProcessType;
    private boolean mLibraryWasLoadedFromApk;
    private boolean mLoadAtFixedAddressFailed;
    private boolean mLoaded;
    private final AtomicBoolean mPrefetchLibraryHasBeenCalled;

    private static native boolean nativeForkAndPrefetchNativeLibrary();

    private native String nativeGetVersionNumber();

    private native void nativeInitCommandLine(String[] strArr);

    private native boolean nativeLibraryLoaded();

    private static native int nativePercentageOfResidentNativeLibraryCode();

    private native void nativeRecordChromiumAndroidLinkerBrowserHistogram(boolean z, boolean z2, int i, long j);

    private native void nativeRecordLibraryPreloaderBrowserHistogram(int i);

    private native void nativeRegisterChromiumAndroidLinkerRendererHistogram(boolean z, boolean z2, long j);

    private native void nativeRegisterLibraryPreloaderRendererHistogram(int i);

    public static void setNativeLibraryPreloader(NativeLibraryPreloader loader) {
        synchronized (sLock) {
            if ($assertionsDisabled || (sLibraryPreloader == null && (sInstance == null || !sInstance.mLoaded))) {
                sLibraryPreloader = loader;
            } else {
                throw new AssertionError();
            }
        }
    }

    public static LibraryLoader get(int libraryProcessType) throws ProcessInitException {
        LibraryLoader libraryLoader;
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new LibraryLoader(libraryProcessType);
                libraryLoader = sInstance;
            } else if (sInstance.mLibraryProcessType == libraryProcessType) {
                libraryLoader = sInstance;
            } else {
                throw new ProcessInitException(2);
            }
        }
        return libraryLoader;
    }

    private LibraryLoader(int libraryProcessType) {
        this.mLibraryProcessType = libraryProcessType;
        this.mPrefetchLibraryHasBeenCalled = new AtomicBoolean();
    }

    public void ensureInitialized(Context context) throws ProcessInitException {
        ContextUtils.initApplicationContext(context.getApplicationContext());
        synchronized (sLock) {
            if (this.mInitialized) {
                return;
            }
            loadAlreadyLocked(context);
            initializeAlreadyLocked();
        }
    }

    public static boolean isInitialized() {
        return sInstance != null && sInstance.mInitialized;
    }

    public void loadNow(Context context) throws ProcessInitException {
        synchronized (sLock) {
            loadAlreadyLocked(context);
        }
    }

    public void initialize() throws ProcessInitException {
        synchronized (sLock) {
            initializeAlreadyLocked();
        }
    }

    public void asyncPrefetchLibrariesToMemory() {
        final boolean coldStart = this.mPrefetchLibraryHasBeenCalled.compareAndSet(false, true);
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                TraceEvent.begin("LibraryLoader.asyncPrefetchLibrariesToMemory");
                int percentage = LibraryLoader.nativePercentageOfResidentNativeLibraryCode();
                boolean success = false;
                if (coldStart) {
                    success = LibraryLoader.nativeForkAndPrefetchNativeLibrary();
                    if (!success) {
                        Log.m38w(LibraryLoader.TAG, "Forking a process to prefetch the native library failed.", new Object[0]);
                    }
                }
                RecordHistogram.initialize();
                if (coldStart) {
                    RecordHistogram.recordBooleanHistogram("LibraryLoader.PrefetchStatus", success);
                }
                if (percentage != -1) {
                    RecordHistogram.recordPercentageHistogram("LibraryLoader.PercentageOfResidentCodeBeforePrefetch" + (coldStart ? ".ColdStartup" : ".WarmStartup"), percentage);
                }
                TraceEvent.end("LibraryLoader.asyncPrefetchLibrariesToMemory");
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void loadLibrary(Linker linker, @Nullable String zipFilePath, String libFilePath) {
        if (linker.isUsingBrowserSharedRelros()) {
            this.mIsUsingBrowserSharedRelros = true;
            try {
                linker.loadLibrary(zipFilePath, libFilePath);
            } catch (UnsatisfiedLinkError e) {
                Log.m38w(TAG, "Failed to load native library with shared RELRO, retrying without", new Object[0]);
                this.mLoadAtFixedAddressFailed = true;
                linker.loadLibraryNoFixedAddress(zipFilePath, libFilePath);
            }
        } else {
            linker.loadLibrary(zipFilePath, libFilePath);
        }
        if (zipFilePath != null) {
            this.mLibraryWasLoadedFromApk = true;
        }
    }

    private void loadAlreadyLocked(Context context) throws ProcessInitException {
        try {
            if (!this.mLoaded) {
                if ($assertionsDisabled || !this.mInitialized) {
                    long startTime = SystemClock.uptimeMillis();
                    if (Linker.isUsed()) {
                        Linker linker = Linker.getInstance();
                        linker.prepareLibraryLoad();
                        for (String library : NativeLibraries.LIBRARIES) {
                            if (!linker.isChromiumLinkerLibrary(library)) {
                                String zipFilePath = null;
                                String libFilePath = System.mapLibraryName(library);
                                if (Linker.isInZipFile()) {
                                    zipFilePath = context.getApplicationInfo().sourceDir;
                                    Log.m29i(TAG, "Loading " + library + " from within " + zipFilePath, new Object[0]);
                                } else {
                                    Log.m29i(TAG, "Loading " + library, new Object[0]);
                                }
                                loadLibrary(linker, zipFilePath, libFilePath);
                            }
                        }
                        linker.finishLibraryLoad();
                    } else {
                        if (sLibraryPreloader != null) {
                            this.mLibraryPreloaderStatus = sLibraryPreloader.loadLibrary(context);
                        }
                        for (String library2 : NativeLibraries.LIBRARIES) {
                            System.loadLibrary(library2);
                        }
                    }
                    this.mLibraryLoadTimeMs = SystemClock.uptimeMillis() - startTime;
                    Log.m29i(TAG, String.format("Time to load native libraries: %d ms (timestamps %d-%d)", new Object[]{Long.valueOf(this.mLibraryLoadTimeMs), Long.valueOf(startTime % 10000), Long.valueOf(stopTime % 10000)}), new Object[0]);
                    this.mLoaded = true;
                } else {
                    throw new AssertionError();
                }
            }
            Log.m29i(TAG, String.format("Expected native library version number \"%s\", actual native library version number \"%s\"", new Object[]{NativeLibraries.sVersionNumber, nativeGetVersionNumber()}), new Object[0]);
            if (!NativeLibraries.sVersionNumber.equals(nativeGetVersionNumber())) {
                throw new ProcessInitException(3);
            }
        } catch (UnsatisfiedLinkError e) {
            throw new ProcessInitException(2, e);
        }
    }

    private static boolean isAbiSplit(String splitName) {
        return splitName.startsWith("abi_");
    }

    public void switchCommandLineForWebView() {
        synchronized (sLock) {
            ensureCommandLineSwitchedAlreadyLocked();
        }
    }

    private void ensureCommandLineSwitchedAlreadyLocked() {
        if (!$assertionsDisabled && !this.mLoaded) {
            throw new AssertionError();
        } else if (!this.mCommandLineSwitched) {
            nativeInitCommandLine(CommandLine.getJavaSwitchesOrNull());
            CommandLine.enableNativeProxy();
            this.mCommandLineSwitched = true;
            ContextUtils.initApplicationContextForNative();
        }
    }

    private void initializeAlreadyLocked() throws ProcessInitException {
        if (!this.mInitialized) {
            ensureCommandLineSwitchedAlreadyLocked();
            if (nativeLibraryLoaded()) {
                TraceEvent.registerNativeEnabledObserver();
                this.mInitialized = true;
                return;
            }
            Log.m28e(TAG, "error calling nativeLibraryLoaded", new Object[0]);
            throw new ProcessInitException(1);
        }
    }

    public void onNativeInitializationComplete(Context context) {
        recordBrowserProcessHistogram(context);
    }

    private void recordBrowserProcessHistogram(Context context) {
        Linker.getInstance();
        if (Linker.isUsed()) {
            nativeRecordChromiumAndroidLinkerBrowserHistogram(this.mIsUsingBrowserSharedRelros, this.mLoadAtFixedAddressFailed, getLibraryLoadFromApkStatus(context), this.mLibraryLoadTimeMs);
        }
        if (sLibraryPreloader != null) {
            nativeRecordLibraryPreloaderBrowserHistogram(this.mLibraryPreloaderStatus);
        }
    }

    private int getLibraryLoadFromApkStatus(Context context) {
        if (!$assertionsDisabled) {
            Linker.getInstance();
            if (!Linker.isUsed()) {
                throw new AssertionError();
            }
        }
        if (this.mLibraryWasLoadedFromApk) {
            return 3;
        }
        return 0;
    }

    public void registerRendererProcessHistogram(boolean requestedSharedRelro, boolean loadAtFixedAddressFailed) {
        Linker.getInstance();
        if (Linker.isUsed()) {
            nativeRegisterChromiumAndroidLinkerRendererHistogram(requestedSharedRelro, loadAtFixedAddressFailed, this.mLibraryLoadTimeMs);
        }
        if (sLibraryPreloader != null) {
            nativeRegisterLibraryPreloaderRendererHistogram(this.mLibraryPreloaderStatus);
        }
    }

    @CalledByNative
    public static int getLibraryProcessType() {
        if (sInstance == null) {
            return 0;
        }
        return sInstance.mLibraryProcessType;
    }
}
