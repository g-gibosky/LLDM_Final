package org.chromium.content.browser;

import android.content.Context;
import android.os.Handler;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import java.util.ArrayList;
import java.util.List;
import org.chromium.base.Log;
import org.chromium.base.ResourceExtractor;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.library_loader.LibraryLoader;
import org.chromium.base.library_loader.ProcessInitException;
import org.chromium.content.app.ContentMain;

@JNINamespace("content")
public class BrowserStartupController {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final boolean ALREADY_STARTED = true;
    private static final boolean NOT_ALREADY_STARTED = false;
    @VisibleForTesting
    static final int STARTUP_FAILURE = 1;
    @VisibleForTesting
    static final int STARTUP_SUCCESS = -1;
    private static final String TAG = "cr.BrowserStartup";
    private static boolean sBrowserMayStartAsynchronously = false;
    private static BrowserStartupController sInstance;
    private static boolean sShouldStartGpuProcessOnBrowserStartup = true;
    private final List<StartupCallback> mAsyncStartupCallbacks = new ArrayList();
    private final Context mContext;
    private boolean mHasStartedInitializingBrowserProcess;
    private int mLibraryProcessType;
    private boolean mPostResourceExtractionTasksCompleted;
    private boolean mStartupDone;
    private boolean mStartupSuccess;

    class C01791 implements Runnable {
        C01791() {
        }

        public void run() {
            ThreadUtils.assertOnUiThread();
            if (BrowserStartupController.this.contentStart() > 0) {
                BrowserStartupController.this.enqueueCallbackExecution(1, false);
            }
        }
    }

    public interface StartupCallback {
        void onFailure();

        void onSuccess(boolean z);
    }

    private static native boolean nativeIsOfficialBuild();

    private static native boolean nativeIsPluginEnabled();

    private static native void nativeSetCommandLineFlags(boolean z, String str);

    static {
        boolean z;
        if (BrowserStartupController.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
    }

    private static void setAsynchronousStartup(boolean enable) {
        sBrowserMayStartAsynchronously = enable;
    }

    private static void setShouldStartGpuProcessOnBrowserStartup(boolean enable) {
        sShouldStartGpuProcessOnBrowserStartup = enable;
    }

    @CalledByNative
    @VisibleForTesting
    static boolean browserMayStartAsynchonously() {
        return sBrowserMayStartAsynchronously;
    }

    @CalledByNative
    @VisibleForTesting
    static void browserStartupComplete(int result) {
        if (sInstance != null) {
            sInstance.executeEnqueuedCallbacks(result, false);
        }
    }

    @CalledByNative
    static boolean shouldStartGpuProcessOnBrowserStartup() {
        return sShouldStartGpuProcessOnBrowserStartup;
    }

    BrowserStartupController(Context context, int libraryProcessType) {
        this.mContext = context.getApplicationContext();
        this.mLibraryProcessType = libraryProcessType;
    }

    public static BrowserStartupController get(Context context, int libraryProcessType) {
        if ($assertionsDisabled || ThreadUtils.runningOnUiThread()) {
            ThreadUtils.assertOnUiThread();
            if (sInstance == null) {
                if ($assertionsDisabled || 1 == libraryProcessType || 3 == libraryProcessType) {
                    sInstance = new BrowserStartupController(context, libraryProcessType);
                } else {
                    throw new AssertionError();
                }
            }
            if ($assertionsDisabled || sInstance.mLibraryProcessType == libraryProcessType) {
                return sInstance;
            }
            throw new AssertionError("Wrong process type");
        }
        throw new AssertionError("Tried to start the browser on the wrong thread.");
    }

    @VisibleForTesting
    static BrowserStartupController overrideInstanceForTest(BrowserStartupController controller) {
        if (sInstance == null) {
            sInstance = controller;
        }
        return sInstance;
    }

    public void startBrowserProcessesAsync(boolean startGpuProcess, StartupCallback callback) throws ProcessInitException {
        if (!$assertionsDisabled && !ThreadUtils.runningOnUiThread()) {
            throw new AssertionError("Tried to start the browser on the wrong thread.");
        } else if (this.mStartupDone) {
            postStartupCompleted(callback);
        } else {
            this.mAsyncStartupCallbacks.add(callback);
            if (!this.mHasStartedInitializingBrowserProcess) {
                this.mHasStartedInitializingBrowserProcess = true;
                setAsynchronousStartup(true);
                setShouldStartGpuProcessOnBrowserStartup(startGpuProcess);
                prepareToStartBrowserProcess(false, new C01791());
            }
        }
    }

    public void startBrowserProcessesSync(boolean singleProcess) throws ProcessInitException {
        if (!this.mStartupDone) {
            if (!(this.mHasStartedInitializingBrowserProcess && this.mPostResourceExtractionTasksCompleted)) {
                prepareToStartBrowserProcess(singleProcess, null);
            }
            setAsynchronousStartup(false);
            if (contentStart() > 0) {
                enqueueCallbackExecution(1, false);
            }
        }
        if (!$assertionsDisabled && !this.mStartupDone) {
            throw new AssertionError();
        } else if (!this.mStartupSuccess) {
            throw new ProcessInitException(4);
        }
    }

    @VisibleForTesting
    int contentStart() {
        return ContentMain.start();
    }

    public void addStartupCompletedObserver(StartupCallback callback) {
        ThreadUtils.assertOnUiThread();
        if (this.mStartupDone) {
            postStartupCompleted(callback);
        } else {
            this.mAsyncStartupCallbacks.add(callback);
        }
    }

    private void executeEnqueuedCallbacks(int startupResult, boolean alreadyStarted) {
        boolean z = true;
        if ($assertionsDisabled || ThreadUtils.runningOnUiThread()) {
            this.mStartupDone = true;
            if (startupResult > 0) {
                z = false;
            }
            this.mStartupSuccess = z;
            for (StartupCallback asyncStartupCallback : this.mAsyncStartupCallbacks) {
                if (this.mStartupSuccess) {
                    asyncStartupCallback.onSuccess(alreadyStarted);
                } else {
                    asyncStartupCallback.onFailure();
                }
            }
            this.mAsyncStartupCallbacks.clear();
            return;
        }
        throw new AssertionError("Callback from browser startup from wrong thread.");
    }

    private void enqueueCallbackExecution(final int startupFailure, final boolean alreadyStarted) {
        new Handler().post(new Runnable() {
            public void run() {
                BrowserStartupController.this.executeEnqueuedCallbacks(startupFailure, alreadyStarted);
            }
        });
    }

    private void postStartupCompleted(final StartupCallback callback) {
        new Handler().post(new Runnable() {
            public void run() {
                if (BrowserStartupController.this.mStartupSuccess) {
                    callback.onSuccess(true);
                } else {
                    callback.onFailure();
                }
            }
        });
    }

    @VisibleForTesting
    void prepareToStartBrowserProcess(final boolean singleProcess, final Runnable completionCallback) throws ProcessInitException {
        Log.m29i(TAG, "Initializing chromium process, singleProcess=%b", Boolean.valueOf(singleProcess));
        ResourceExtractor resourceExtractor = ResourceExtractor.get(this.mContext);
        resourceExtractor.startExtractingResources();
        ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        try {
            LibraryLoader.get(this.mLibraryProcessType).ensureInitialized(this.mContext);
            Runnable postResourceExtraction = new Runnable() {
                public void run() {
                    if (!BrowserStartupController.this.mPostResourceExtractionTasksCompleted) {
                        DeviceUtils.addDeviceSpecificUserAgentSwitch(BrowserStartupController.this.mContext);
                        BrowserStartupController.nativeSetCommandLineFlags(singleProcess, BrowserStartupController.nativeIsPluginEnabled() ? BrowserStartupController.this.getPlugins() : null);
                        BrowserStartupController.this.mPostResourceExtractionTasksCompleted = true;
                    }
                    if (completionCallback != null) {
                        completionCallback.run();
                    }
                }
            };
            if (completionCallback == null) {
                resourceExtractor.waitForCompletion();
                postResourceExtraction.run();
                return;
            }
            resourceExtractor.addCompletionCallback(postResourceExtraction);
        } finally {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    public void initChromiumBrowserProcessForTests() {
        ResourceExtractor resourceExtractor = ResourceExtractor.get(this.mContext);
        resourceExtractor.startExtractingResources();
        resourceExtractor.waitForCompletion();
        nativeSetCommandLineFlags(false, null);
    }

    private String getPlugins() {
        return PepperPluginManager.getPlugins(this.mContext);
    }
}
