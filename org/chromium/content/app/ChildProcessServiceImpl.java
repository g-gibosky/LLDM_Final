package org.chromium.content.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.view.Surface;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import org.chromium.base.ContextUtils;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.SuppressFBWarnings;
import org.chromium.base.library_loader.Linker;
import org.chromium.content.browser.ChildProcessConstants;
import org.chromium.content.browser.ChildProcessCreationParams;
import org.chromium.content.browser.FileDescriptorInfo;
import org.chromium.content.common.ContentSwitches;
import org.chromium.content.common.IChildProcessCallback;
import org.chromium.content.common.IChildProcessService.Stub;
import org.chromium.content.common.SurfaceWrapper;

@JNINamespace("content")
public class ChildProcessServiceImpl {
    static final /* synthetic */ boolean $assertionsDisabled = (!ChildProcessServiceImpl.class.desiredAssertionStatus());
    protected static final FileDescriptorInfo[] EMPTY_FILE_DESCRIPTOR_INFO = new FileDescriptorInfo[0];
    private static final String MAIN_THREAD_NAME = "ChildProcessMain";
    private static final String TAG = "ChildProcessService";
    private static AtomicReference<Context> sContext = new AtomicReference(null);
    private final Semaphore mActivitySemaphore = new Semaphore(1);
    private final Stub mBinder = new C04851();
    private IChildProcessCallback mCallback;
    private String[] mCommandLineParams;
    private int mCpuCount;
    private long mCpuFeatures;
    private FileDescriptorInfo[] mFdInfos;
    private ClassLoader mHostClassLoader;
    private boolean mIsBound = false;
    private boolean mLibraryInitialized = false;
    private int mLibraryProcessType;
    private ChromiumLinkerParams mLinkerParams;
    private Thread mMainThread;

    class C04851 extends Stub {
        C04851() {
        }

        public int setupConnection(Bundle args, IChildProcessCallback callback) {
            ChildProcessServiceImpl.this.mCallback = callback;
            ChildProcessServiceImpl.this.getServiceInfo(args);
            return Process.myPid();
        }

        public void crashIntentionallyForTesting() {
            Process.killProcess(Process.myPid());
        }
    }

    @org.chromium.base.annotations.CalledByNative
    private void establishSurfaceTexturePeer(int r8, java.lang.Object r9, int r10, int r11) {
        /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1431)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1453)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:79)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r7 = this;
        r5 = 1;
        r6 = 0;
        r3 = r7.mCallback;
        if (r3 != 0) goto L_0x0010;
    L_0x0006:
        r3 = "ChildProcessService";
        r4 = "No callback interface has been provided.";
        r5 = new java.lang.Object[r6];
        org.chromium.base.Log.m28e(r3, r4, r5);
    L_0x000f:
        return;
    L_0x0010:
        r2 = 0;
        r1 = 0;
        r3 = r9 instanceof android.view.Surface;
        if (r3 == 0) goto L_0x0024;
    L_0x0016:
        r2 = r9;
        r2 = (android.view.Surface) r2;
    L_0x0019:
        r3 = r7.mCallback;	 Catch:{ RemoteException -> 0x003d, all -> 0x0051 }
        r3.establishSurfacePeer(r8, r2, r10, r11);	 Catch:{ RemoteException -> 0x003d, all -> 0x0051 }
        if (r1 == 0) goto L_0x000f;
    L_0x0020:
        r2.release();
        goto L_0x000f;
    L_0x0024:
        r3 = r9 instanceof android.graphics.SurfaceTexture;
        if (r3 == 0) goto L_0x0031;
    L_0x0028:
        r2 = new android.view.Surface;
        r9 = (android.graphics.SurfaceTexture) r9;
        r2.<init>(r9);
        r1 = 1;
        goto L_0x0019;
    L_0x0031:
        r3 = "ChildProcessService";
        r4 = "Not a valid surfaceObject: %s";
        r5 = new java.lang.Object[r5];
        r5[r6] = r9;
        org.chromium.base.Log.m28e(r3, r4, r5);
        goto L_0x000f;
    L_0x003d:
        r0 = move-exception;
        r3 = "ChildProcessService";	 Catch:{ RemoteException -> 0x003d, all -> 0x0051 }
        r4 = "Unable to call establishSurfaceTexturePeer: %s";	 Catch:{ RemoteException -> 0x003d, all -> 0x0051 }
        r5 = 1;	 Catch:{ RemoteException -> 0x003d, all -> 0x0051 }
        r5 = new java.lang.Object[r5];	 Catch:{ RemoteException -> 0x003d, all -> 0x0051 }
        r6 = 0;	 Catch:{ RemoteException -> 0x003d, all -> 0x0051 }
        r5[r6] = r0;	 Catch:{ RemoteException -> 0x003d, all -> 0x0051 }
        org.chromium.base.Log.m28e(r3, r4, r5);	 Catch:{ RemoteException -> 0x003d, all -> 0x0051 }
        if (r1 == 0) goto L_0x000f;
    L_0x004d:
        r2.release();
        goto L_0x000f;
    L_0x0051:
        r3 = move-exception;
        if (r1 == 0) goto L_0x0057;
    L_0x0054:
        r2.release();
    L_0x0057:
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.app.ChildProcessServiceImpl.establishSurfaceTexturePeer(int, java.lang.Object, int, int):void");
    }

    private static native void nativeExitChildProcess();

    private static native void nativeInitChildProcessImpl(ChildProcessServiceImpl childProcessServiceImpl, int i, long j);

    private static native void nativeRegisterGlobalFileDescriptor(int i, int i2, long j, long j2);

    private native void nativeShutdownMainThread();

    private Linker getLinker() {
        if (Linker.areTestsEnabled()) {
            if ($assertionsDisabled || this.mLinkerParams != null) {
                Linker.setupForTesting(this.mLinkerParams.mLinkerImplementationForTesting, this.mLinkerParams.mTestRunnerClassNameForTesting);
            } else {
                throw new AssertionError();
            }
        }
        return Linker.getInstance();
    }

    static Context getContext() {
        return (Context) sContext.get();
    }

    public void create(Context context, final Context hostBrowserContext) {
        this.mHostClassLoader = hostBrowserContext.getClassLoader();
        Log.m29i(TAG, "Creating new ChildProcessService pid=%d", Integer.valueOf(Process.myPid()));
        if (sContext.get() != null) {
            throw new RuntimeException("Illegal child process reuse.");
        }
        sContext.set(context);
        ContextUtils.initApplicationContext(context);
        this.mMainThread = new Thread(new Runnable() {
            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            @org.chromium.base.annotations.SuppressFBWarnings({"DM_EXIT"})
            public void run() {
                /*
                r15 = this;
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r1 = r0.mMainThread;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                monitor-enter(r1);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
            L_0x0007:
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ all -> 0x0019 }
                r0 = r0.mCommandLineParams;	 Catch:{ all -> 0x0019 }
                if (r0 != 0) goto L_0x0030;
            L_0x000f:
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ all -> 0x0019 }
                r0 = r0.mMainThread;	 Catch:{ all -> 0x0019 }
                r0.wait();	 Catch:{ all -> 0x0019 }
                goto L_0x0007;
            L_0x0019:
                r0 = move-exception;
                monitor-exit(r1);	 Catch:{ all -> 0x0019 }
                throw r0;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
            L_0x001c:
                r7 = move-exception;
                r0 = "ChildProcessService";
                r1 = "%s startup failed: %s";
                r2 = 2;
                r2 = new java.lang.Object[r2];
                r3 = 0;
                r4 = "ChildProcessMain";
                r2[r3] = r4;
                r3 = 1;
                r2[r3] = r7;
                org.chromium.base.Log.m38w(r0, r1, r2);
            L_0x002f:
                return;
            L_0x0030:
                monitor-exit(r1);	 Catch:{ all -> 0x0019 }
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = r0.mCommandLineParams;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                org.chromium.base.CommandLine.init(r0);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r12 = 0;
                r14 = 0;
                r0 = org.chromium.base.library_loader.Linker.isUsed();	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                if (r0 == 0) goto L_0x008f;
            L_0x0042:
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r1 = r0.mMainThread;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                monitor-enter(r1);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
            L_0x0049:
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ all -> 0x005b }
                r0 = r0.mIsBound;	 Catch:{ all -> 0x005b }
                if (r0 != 0) goto L_0x0072;
            L_0x0051:
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ all -> 0x005b }
                r0 = r0.mMainThread;	 Catch:{ all -> 0x005b }
                r0.wait();	 Catch:{ all -> 0x005b }
                goto L_0x0049;
            L_0x005b:
                r0 = move-exception;
                monitor-exit(r1);	 Catch:{ all -> 0x005b }
                throw r0;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
            L_0x005e:
                r7 = move-exception;
                r0 = "ChildProcessService";
                r1 = "%s startup failed: %s";
                r2 = 2;
                r2 = new java.lang.Object[r2];
                r3 = 0;
                r4 = "ChildProcessMain";
                r2[r3] = r4;
                r3 = 1;
                r2[r3] = r7;
                org.chromium.base.Log.m38w(r0, r1, r2);
                goto L_0x002f;
            L_0x0072:
                monitor-exit(r1);	 Catch:{ all -> 0x005b }
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r12 = r0.getLinker();	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = r0.mLinkerParams;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = r0.mWaitForSharedRelro;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                if (r0 == 0) goto L_0x0112;
            L_0x0083:
                r14 = 1;
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = r0.mLinkerParams;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = r0.mBaseLoadAddress;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r12.initServiceProcess(r0);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
            L_0x008f:
                r10 = 0;
                r0 = org.chromium.base.CommandLine.getInstance();	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r1 = "renderer-wait-for-java-debugger";
                r0 = r0.hasSwitch(r1);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                if (r0 == 0) goto L_0x009f;
            L_0x009c:
                android.os.Debug.waitForDebugger();	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
            L_0x009f:
                r13 = 0;
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ ProcessInitException -> 0x0117, InterruptedException -> 0x001c }
                r0 = r0.mLibraryProcessType;	 Catch:{ ProcessInitException -> 0x0117, InterruptedException -> 0x001c }
                r0 = org.chromium.base.library_loader.LibraryLoader.get(r0);	 Catch:{ ProcessInitException -> 0x0117, InterruptedException -> 0x001c }
                r1 = r7;	 Catch:{ ProcessInitException -> 0x0117, InterruptedException -> 0x001c }
                r0.loadNow(r1);	 Catch:{ ProcessInitException -> 0x0117, InterruptedException -> 0x001c }
                r10 = 1;
            L_0x00b0:
                if (r10 != 0) goto L_0x00c7;
            L_0x00b2:
                if (r14 == 0) goto L_0x00c7;
            L_0x00b4:
                r12.disableSharedRelros();	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ ProcessInitException -> 0x0135, InterruptedException -> 0x001c }
                r0 = r0.mLibraryProcessType;	 Catch:{ ProcessInitException -> 0x0135, InterruptedException -> 0x001c }
                r0 = org.chromium.base.library_loader.LibraryLoader.get(r0);	 Catch:{ ProcessInitException -> 0x0135, InterruptedException -> 0x001c }
                r1 = r7;	 Catch:{ ProcessInitException -> 0x0135, InterruptedException -> 0x001c }
                r0.loadNow(r1);	 Catch:{ ProcessInitException -> 0x0135, InterruptedException -> 0x001c }
                r10 = 1;
            L_0x00c7:
                if (r10 != 0) goto L_0x00cd;
            L_0x00c9:
                r0 = -1;
                java.lang.System.exit(r0);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
            L_0x00cd:
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = r0.mLibraryProcessType;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = org.chromium.base.library_loader.LibraryLoader.get(r0);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0.registerRendererProcessHistogram(r14, r13);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = r0.mLibraryProcessType;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = org.chromium.base.library_loader.LibraryLoader.get(r0);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0.initialize();	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r1 = r0.mMainThread;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                monitor-enter(r1);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ all -> 0x010f }
                r2 = 1;
                r0.mLibraryInitialized = r2;	 Catch:{ all -> 0x010f }
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ all -> 0x010f }
                r0 = r0.mMainThread;	 Catch:{ all -> 0x010f }
                r0.notifyAll();	 Catch:{ all -> 0x010f }
            L_0x00fd:
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ all -> 0x010f }
                r0 = r0.mFdInfos;	 Catch:{ all -> 0x010f }
                if (r0 != 0) goto L_0x0144;
            L_0x0105:
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ all -> 0x010f }
                r0 = r0.mMainThread;	 Catch:{ all -> 0x010f }
                r0.wait();	 Catch:{ all -> 0x010f }
                goto L_0x00fd;
            L_0x010f:
                r0 = move-exception;
                monitor-exit(r1);	 Catch:{ all -> 0x010f }
                throw r0;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
            L_0x0112:
                r12.disableSharedRelros();	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                goto L_0x008f;
            L_0x0117:
                r7 = move-exception;
                if (r14 == 0) goto L_0x0126;
            L_0x011a:
                r0 = "ChildProcessService";
                r1 = "Failed to load native library with shared RELRO, retrying without";
                r2 = 0;
                r2 = new java.lang.Object[r2];	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                org.chromium.base.Log.m38w(r0, r1, r2);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r13 = 1;
                goto L_0x00b0;
            L_0x0126:
                r0 = "ChildProcessService";
                r1 = "Failed to load native library";
                r2 = 1;
                r2 = new java.lang.Object[r2];	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r3 = 0;
                r2[r3] = r7;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                org.chromium.base.Log.m28e(r0, r1, r2);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                goto L_0x00b0;
            L_0x0135:
                r7 = move-exception;
                r0 = "ChildProcessService";
                r1 = "Failed to load native library on retry";
                r2 = 1;
                r2 = new java.lang.Object[r2];	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r3 = 0;
                r2[r3] = r7;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                org.chromium.base.Log.m28e(r0, r1, r2);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                goto L_0x00c7;
            L_0x0144:
                monitor-exit(r1);	 Catch:{ all -> 0x010f }
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r6 = r0.mFdInfos;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r11 = r6.length;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r9 = 0;
            L_0x014d:
                if (r9 >= r11) goto L_0x0163;
            L_0x014f:
                r8 = r6[r9];	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = r8.mId;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r1 = r8.mFd;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r1 = r1.detachFd();	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r2 = r8.mOffset;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r4 = r8.mSize;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                org.chromium.content.app.ChildProcessServiceImpl.nativeRegisterGlobalFileDescriptor(r0, r1, r2, r4);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r9 = r9 + 1;
                goto L_0x014d;
            L_0x0163:
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r1 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r1 = r1.mCpuCount;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r2 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r2 = r2.mCpuFeatures;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                org.chromium.content.app.ChildProcessServiceImpl.nativeInitChildProcessImpl(r0, r1, r2);	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = org.chromium.content.app.ChildProcessServiceImpl.this;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = r0.mActivitySemaphore;	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                r0 = r0.tryAcquire();	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                if (r0 == 0) goto L_0x002f;
            L_0x0180:
                org.chromium.content.app.ContentMain.start();	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                org.chromium.content.app.ChildProcessServiceImpl.nativeExitChildProcess();	 Catch:{ InterruptedException -> 0x001c, ProcessInitException -> 0x005e }
                goto L_0x002f;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.app.ChildProcessServiceImpl.2.run():void");
            }
        }, MAIN_THREAD_NAME);
        this.mMainThread.start();
    }

    @SuppressFBWarnings({"DM_EXIT"})
    public void destroy() {
        Log.m29i(TAG, "Destroying ChildProcessService pid=%d", Integer.valueOf(Process.myPid()));
        if (this.mActivitySemaphore.tryAcquire()) {
            System.exit(0);
            return;
        }
        synchronized (this.mMainThread) {
            while (!this.mLibraryInitialized) {
                try {
                    this.mMainThread.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        nativeShutdownMainThread();
    }

    public IBinder bind(Intent intent) {
        initializeParams(intent);
        return this.mBinder;
    }

    void initializeParams(Intent intent) {
        synchronized (this.mMainThread) {
            this.mCommandLineParams = intent.getStringArrayExtra(ChildProcessConstants.EXTRA_COMMAND_LINE);
            this.mLinkerParams = new ChromiumLinkerParams(intent);
            this.mLibraryProcessType = ChildProcessCreationParams.getLibraryProcessType(intent);
            this.mIsBound = true;
            this.mMainThread.notifyAll();
        }
    }

    void getServiceInfo(Bundle bundle) {
        bundle.setClassLoader(this.mHostClassLoader);
        synchronized (this.mMainThread) {
            if (this.mCommandLineParams == null) {
                this.mCommandLineParams = bundle.getStringArray(ChildProcessConstants.EXTRA_COMMAND_LINE);
            }
            if ($assertionsDisabled || this.mCommandLineParams != null) {
                this.mCpuCount = bundle.getInt(ChildProcessConstants.EXTRA_CPU_COUNT);
                this.mCpuFeatures = bundle.getLong(ChildProcessConstants.EXTRA_CPU_FEATURES);
                if ($assertionsDisabled || this.mCpuCount > 0) {
                    Parcelable[] fdInfosAsParcelable = bundle.getParcelableArray(ChildProcessConstants.EXTRA_FILES);
                    if (fdInfosAsParcelable != null) {
                        this.mFdInfos = new FileDescriptorInfo[fdInfosAsParcelable.length];
                        System.arraycopy(fdInfosAsParcelable, 0, this.mFdInfos, 0, fdInfosAsParcelable.length);
                    } else {
                        String processType = ContentSwitches.getSwitchValue(this.mCommandLineParams, ContentSwitches.SWITCH_PROCESS_TYPE);
                        if ($assertionsDisabled || ContentSwitches.SWITCH_DOWNLOAD_PROCESS.equals(processType)) {
                            this.mFdInfos = EMPTY_FILE_DESCRIPTOR_INFO;
                        } else {
                            throw new AssertionError();
                        }
                    }
                    Bundle sharedRelros = bundle.getBundle(Linker.EXTRA_LINKER_SHARED_RELROS);
                    if (sharedRelros != null) {
                        getLinker().useSharedRelros(sharedRelros);
                    }
                    this.mMainThread.notifyAll();
                } else {
                    throw new AssertionError();
                }
            }
            throw new AssertionError();
        }
    }

    @CalledByNative
    private Surface getViewSurface(int surfaceId) {
        Surface surface = null;
        if (this.mCallback == null) {
            Log.m28e(TAG, "No callback interface has been provided.", new Object[0]);
        } else {
            try {
                SurfaceWrapper wrapper = this.mCallback.getViewSurface(surfaceId);
                if (wrapper != null) {
                    surface = wrapper.getSurface();
                }
            } catch (RemoteException e) {
                Log.m28e(TAG, "Unable to call getViewSurface: %s", e);
            }
        }
        return surface;
    }

    @CalledByNative
    private void createSurfaceTextureSurface(int surfaceTextureId, int clientId, SurfaceTexture surfaceTexture) {
        if (this.mCallback == null) {
            Log.m28e(TAG, "No callback interface has been provided.", new Object[0]);
            return;
        }
        Surface surface = new Surface(surfaceTexture);
        try {
            this.mCallback.registerSurfaceTextureSurface(surfaceTextureId, clientId, surface);
        } catch (RemoteException e) {
            Log.m28e(TAG, "Unable to call registerSurfaceTextureSurface: %s", e);
        }
        surface.release();
    }

    @CalledByNative
    private void destroySurfaceTextureSurface(int surfaceTextureId, int clientId) {
        if (this.mCallback == null) {
            Log.m28e(TAG, "No callback interface has been provided.", new Object[0]);
            return;
        }
        try {
            this.mCallback.unregisterSurfaceTextureSurface(surfaceTextureId, clientId);
        } catch (RemoteException e) {
            Log.m28e(TAG, "Unable to call unregisterSurfaceTextureSurface: %s", e);
        }
    }

    @CalledByNative
    private Surface getSurfaceTextureSurface(int surfaceTextureId) {
        Surface surface = null;
        if (this.mCallback == null) {
            Log.m28e(TAG, "No callback interface has been provided.", new Object[0]);
        } else {
            try {
                surface = this.mCallback.getSurfaceTextureSurface(surfaceTextureId).getSurface();
            } catch (RemoteException e) {
                Log.m28e(TAG, "Unable to call getSurfaceTextureSurface: %s", e);
            }
        }
        return surface;
    }
}
