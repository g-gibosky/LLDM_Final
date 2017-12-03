package org.chromium.content.browser;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.SurfaceTexture;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Surface;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import org.chromium.base.CommandLine;
import org.chromium.base.CpuFeatures;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.library_loader.Linker;
import org.chromium.content.app.ChromiumLinkerParams;
import org.chromium.content.app.DownloadProcessService;
import org.chromium.content.app.PrivilegedProcessService;
import org.chromium.content.app.SandboxedProcessService;
import org.chromium.content.browser.ChildProcessConnection.ConnectionCallback;
import org.chromium.content.browser.ChildProcessConnection.DeathCallback;
import org.chromium.content.common.ContentSwitches;
import org.chromium.content.common.IChildProcessCallback;
import org.chromium.content.common.IChildProcessCallback.Stub;
import org.chromium.content.common.SurfaceWrapper;

@JNINamespace("content")
public class ChildProcessLauncher {
    static final /* synthetic */ boolean $assertionsDisabled;
    static final int CALLBACK_FOR_DOWNLOAD_PROCESS = 4;
    static final int CALLBACK_FOR_GPU_PROCESS = 1;
    static final int CALLBACK_FOR_RENDERER_PROCESS = 2;
    static final int CALLBACK_FOR_UNKNOWN_PROCESS = 0;
    static final int CALLBACK_FOR_UTILITY_PROCESS = 3;
    private static final long FREE_CONNECTION_DELAY_MILLIS = 1;
    private static final int NULL_PROCESS_HANDLE = 0;
    private static final String NUM_PRIVILEGED_SERVICES_KEY = "org.chromium.content.browser.NUM_PRIVILEGED_SERVICES";
    private static final String NUM_SANDBOXED_SERVICES_KEY = "org.chromium.content.browser.NUM_SANDBOXED_SERVICES";
    private static final String SANDBOXED_SERVICES_NAME_KEY = "org.chromium.content.browser.SANDBOXED_SERVICES_NAME";
    @VisibleForTesting
    public static final String SWITCH_NUM_SANDBOXED_SERVICES_FOR_TESTING = "num-sandboxed-services";
    public static final String SWITCH_SANDBOXED_SERVICES_NAME_FOR_TESTING = "sandboxed-services-name";
    private static final String TAG = "ChildProcLauncher";
    private static boolean sApplicationInForeground = true;
    private static BindingManager sBindingManager = BindingManagerImpl.createBindingManager();
    private static boolean sLinkerInitialized = false;
    private static long sLinkerLoadAddress = 0;
    private static ChildConnectionAllocator sPrivilegedChildConnectionAllocator;
    private static Map<String, ChildConnectionAllocator> sSandboxedChildConnectionAllocatorMap;
    private static Map<Integer, ChildProcessConnection> sServiceMap = new ConcurrentHashMap();
    private static ChildProcessConnection sSpareSandboxedConnection = null;
    private static Map<Pair<Integer, Integer>, Surface> sSurfaceTextureSurfaceMap = new ConcurrentHashMap();
    private static Map<Integer, Surface> sViewSurfaceMap = new ConcurrentHashMap();

    private static class ChildConnectionAllocator {
        static final /* synthetic */ boolean $assertionsDisabled = (!ChildProcessLauncher.class.desiredAssertionStatus());
        private final String mChildClassName;
        private final ChildProcessConnection[] mChildProcessConnections;
        private final Object mConnectionLock = new Object();
        private final ArrayList<Integer> mFreeConnectionIndices;
        private final boolean mInSandbox;
        private final PendingSpawnQueue mPendingSpawnQueue = new PendingSpawnQueue();

        public ChildConnectionAllocator(boolean inSandbox, int numChildServices, String serviceClassName) {
            this.mChildProcessConnections = new ChildProcessConnectionImpl[numChildServices];
            this.mFreeConnectionIndices = new ArrayList(numChildServices);
            for (int i = 0; i < numChildServices; i++) {
                this.mFreeConnectionIndices.add(Integer.valueOf(i));
            }
            this.mChildClassName = serviceClassName;
            this.mInSandbox = inSandbox;
        }

        public ChildProcessConnection allocate(Context context, DeathCallback deathCallback, ChromiumLinkerParams chromiumLinkerParams, boolean alwaysInForeground, ChildProcessCreationParams creationParams) {
            ChildProcessConnection childProcessConnection;
            synchronized (this.mConnectionLock) {
                if (this.mFreeConnectionIndices.isEmpty()) {
                    Log.m20d(ChildProcessLauncher.TAG, "Ran out of services to allocate.");
                    childProcessConnection = null;
                } else {
                    int slot = ((Integer) this.mFreeConnectionIndices.remove(0)).intValue();
                    if ($assertionsDisabled || this.mChildProcessConnections[slot] == null) {
                        this.mChildProcessConnections[slot] = new ChildProcessConnectionImpl(context, slot, this.mInSandbox, deathCallback, this.mChildClassName, chromiumLinkerParams, alwaysInForeground, creationParams);
                        Log.m22d(ChildProcessLauncher.TAG, "Allocator allocated a connection, sandbox: %b, slot: %d", Boolean.valueOf(this.mInSandbox), Integer.valueOf(slot));
                        childProcessConnection = this.mChildProcessConnections[slot];
                    } else {
                        throw new AssertionError();
                    }
                }
            }
            return childProcessConnection;
        }

        public void free(ChildProcessConnection connection) {
            synchronized (this.mConnectionLock) {
                int slot = connection.getServiceNumber();
                if (this.mChildProcessConnections[slot] != connection) {
                    int occupier = this.mChildProcessConnections[slot] == null ? -1 : this.mChildProcessConnections[slot].getServiceNumber();
                    Log.m28e(ChildProcessLauncher.TAG, "Unable to find connection to free in slot: %d already occupied by service: %d", Integer.valueOf(slot), Integer.valueOf(occupier));
                    if (!$assertionsDisabled) {
                        throw new AssertionError();
                    }
                }
                this.mChildProcessConnections[slot] = null;
                if ($assertionsDisabled || !this.mFreeConnectionIndices.contains(Integer.valueOf(slot))) {
                    this.mFreeConnectionIndices.add(Integer.valueOf(slot));
                    Log.m22d(ChildProcessLauncher.TAG, "Allocator freed a connection, sandbox: %b, slot: %d", Boolean.valueOf(this.mInSandbox), Integer.valueOf(slot));
                } else {
                    throw new AssertionError();
                }
            }
        }

        public boolean isFreeConnectionAvailable() {
            boolean z;
            synchronized (this.mConnectionLock) {
                z = !this.mFreeConnectionIndices.isEmpty();
            }
            return z;
        }

        public PendingSpawnQueue getPendingSpawnQueue() {
            return this.mPendingSpawnQueue;
        }

        @VisibleForTesting
        int allocatedConnectionsCountForTesting() {
            return this.mChildProcessConnections.length - this.mFreeConnectionIndices.size();
        }
    }

    private static class PendingSpawnData {
        private final int mCallbackType;
        private final int mChildProcessId;
        private final long mClientContext;
        private final String[] mCommandLine;
        private final Context mContext;
        private final ChildProcessCreationParams mCreationParams;
        private final FileDescriptorInfo[] mFilesToBeMapped;
        private final boolean mInSandbox;

        private PendingSpawnData(Context context, String[] commandLine, int childProcessId, FileDescriptorInfo[] filesToBeMapped, long clientContext, int callbackType, boolean inSandbox, ChildProcessCreationParams creationParams) {
            this.mContext = context;
            this.mCommandLine = commandLine;
            this.mChildProcessId = childProcessId;
            this.mFilesToBeMapped = filesToBeMapped;
            this.mClientContext = clientContext;
            this.mCallbackType = callbackType;
            this.mInSandbox = inSandbox;
            this.mCreationParams = creationParams;
        }

        private Context context() {
            return this.mContext;
        }

        private String[] commandLine() {
            return this.mCommandLine;
        }

        private int childProcessId() {
            return this.mChildProcessId;
        }

        private FileDescriptorInfo[] filesToBeMapped() {
            return this.mFilesToBeMapped;
        }

        private long clientContext() {
            return this.mClientContext;
        }

        private int callbackType() {
            return this.mCallbackType;
        }

        private boolean inSandbox() {
            return this.mInSandbox;
        }

        private ChildProcessCreationParams getCreationParams() {
            return this.mCreationParams;
        }
    }

    private static class PendingSpawnQueue {
        static final /* synthetic */ boolean $assertionsDisabled = (!ChildProcessLauncher.class.desiredAssertionStatus());
        private Queue<PendingSpawnData> mPendingSpawns;
        final Object mPendingSpawnsLock;

        private PendingSpawnQueue() {
            this.mPendingSpawns = new LinkedList();
            this.mPendingSpawnsLock = new Object();
        }

        public void enqueueLocked(PendingSpawnData pendingSpawn) {
            if ($assertionsDisabled || Thread.holdsLock(this.mPendingSpawnsLock)) {
                this.mPendingSpawns.add(pendingSpawn);
                return;
            }
            throw new AssertionError();
        }

        public PendingSpawnData dequeueLocked() {
            if ($assertionsDisabled || Thread.holdsLock(this.mPendingSpawnsLock)) {
                return (PendingSpawnData) this.mPendingSpawns.poll();
            }
            throw new AssertionError();
        }

        public int sizeLocked() {
            if ($assertionsDisabled || Thread.holdsLock(this.mPendingSpawnsLock)) {
                return this.mPendingSpawns.size();
            }
            throw new AssertionError();
        }
    }

    static class C04331 implements DeathCallback {
        C04331() {
        }

        public void onChildProcessDied(ChildProcessConnection connection) {
            if (connection.getPid() != 0) {
                ChildProcessLauncher.stop(connection.getPid());
            } else {
                ChildProcessLauncher.freeConnection(connection);
            }
        }
    }

    private static native void nativeEstablishSurfacePeer(int i, Surface surface, int i2, int i3);

    private static native boolean nativeIsSingleProcess();

    private static native void nativeOnChildProcessStarted(long j, int i);

    static {
        boolean z;
        if (ChildProcessLauncher.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
    }

    private static int getNumberOfServices(Context context, boolean inSandbox, String packageName) {
        int numServices = -1;
        if (inSandbox && CommandLine.getInstance().hasSwitch(SWITCH_NUM_SANDBOXED_SERVICES_FOR_TESTING)) {
            String value = CommandLine.getInstance().getSwitchValue(SWITCH_NUM_SANDBOXED_SERVICES_FOR_TESTING);
            if (!TextUtils.isEmpty(value)) {
                try {
                    numServices = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    Log.m38w(TAG, "The value of --num-sandboxed-services is formatted wrongly: " + value, new Object[0]);
                }
            }
        } else {
            try {
                ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(packageName, 128);
                if (appInfo.metaData != null) {
                    numServices = appInfo.metaData.getInt(inSandbox ? NUM_SANDBOXED_SERVICES_KEY : NUM_PRIVILEGED_SERVICES_KEY, -1);
                }
            } catch (NameNotFoundException e2) {
                throw new RuntimeException("Could not get application info");
            }
        }
        if (numServices >= 0) {
            return numServices;
        }
        throw new RuntimeException("Illegal meta data value for number of child services");
    }

    private static String getClassNameOfService(Context context, boolean inSandbox, String packageName) {
        if (!inSandbox) {
            return PrivilegedProcessService.class.getName();
        }
        if (CommandLine.getInstance().hasSwitch(SWITCH_SANDBOXED_SERVICES_NAME_FOR_TESTING)) {
            return CommandLine.getInstance().getSwitchValue(SWITCH_SANDBOXED_SERVICES_NAME_FOR_TESTING);
        }
        String serviceName = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(packageName, 128);
            if (appInfo.metaData != null) {
                serviceName = appInfo.metaData.getString(SANDBOXED_SERVICES_NAME_KEY);
            }
            if (serviceName == null) {
                return SandboxedProcessService.class.getName();
            }
            try {
                context.getPackageManager().getServiceInfo(new ComponentName(packageName, serviceName + "0"), 0);
                return serviceName;
            } catch (NameNotFoundException e) {
                throw new RuntimeException("Illegal meta data value: the child service doesn't exist");
            }
        } catch (NameNotFoundException e2) {
            throw new RuntimeException("Could not get application info.");
        }
    }

    private static void initConnectionAllocatorsIfNecessary(Context context, boolean inSandbox, String packageName) {
        synchronized (ChildProcessLauncher.class) {
            if (inSandbox) {
                if (sSandboxedChildConnectionAllocatorMap == null) {
                    sSandboxedChildConnectionAllocatorMap = new ConcurrentHashMap();
                }
                if (!sSandboxedChildConnectionAllocatorMap.containsKey(packageName)) {
                    Log.m38w(TAG, "Create a new ChildConnectionAllocator with package name = %s, inSandbox = true", packageName);
                    sSandboxedChildConnectionAllocatorMap.put(packageName, new ChildConnectionAllocator(true, getNumberOfServices(context, true, packageName), getClassNameOfService(context, true, packageName)));
                }
            } else if (sPrivilegedChildConnectionAllocator == null) {
                sPrivilegedChildConnectionAllocator = new ChildConnectionAllocator(false, getNumberOfServices(context, false, packageName), getClassNameOfService(context, false, packageName));
            }
        }
    }

    private static ChildConnectionAllocator getConnectionAllocator(String packageName, boolean inSandbox) {
        if (inSandbox) {
            return (ChildConnectionAllocator) sSandboxedChildConnectionAllocatorMap.get(packageName);
        }
        return sPrivilegedChildConnectionAllocator;
    }

    private static PendingSpawnQueue getPendingSpawnQueue(Context context, String packageName, boolean inSandbox) {
        initConnectionAllocatorsIfNecessary(context, inSandbox, packageName);
        return getConnectionAllocator(packageName, inSandbox).getPendingSpawnQueue();
    }

    private static ChildProcessConnection allocateConnection(Context context, boolean inSandbox, ChromiumLinkerParams chromiumLinkerParams, boolean alwaysInForeground, ChildProcessCreationParams creationParams) {
        DeathCallback deathCallback = new C04331();
        String packageName = creationParams != null ? creationParams.getPackageName() : context.getPackageName();
        initConnectionAllocatorsIfNecessary(context, inSandbox, packageName);
        return getConnectionAllocator(packageName, inSandbox).allocate(context, deathCallback, chromiumLinkerParams, alwaysInForeground, creationParams);
    }

    private static ChromiumLinkerParams getLinkerParamsForNewConnection() {
        if (!sLinkerInitialized) {
            if (Linker.isUsed()) {
                sLinkerLoadAddress = Linker.getInstance().getBaseLoadAddress();
                if (sLinkerLoadAddress == 0) {
                    Log.m29i(TAG, "Shared RELRO support disabled!", new Object[0]);
                }
            }
            sLinkerInitialized = true;
        }
        if (sLinkerLoadAddress == 0) {
            return null;
        }
        if (!Linker.areTestsEnabled()) {
            return new ChromiumLinkerParams(sLinkerLoadAddress, true);
        }
        Linker linker = Linker.getInstance();
        return new ChromiumLinkerParams(sLinkerLoadAddress, true, linker.getTestRunnerClassNameForTesting(), linker.getImplementationForTesting());
    }

    private static ChildProcessConnection allocateBoundConnection(Context context, String[] commandLine, boolean inSandbox, boolean alwaysInForeground, ChildProcessCreationParams creationParams) {
        ChildProcessConnection connection = allocateConnection(context, inSandbox, getLinkerParamsForNewConnection(), alwaysInForeground, creationParams);
        if (connection != null) {
            connection.start(commandLine);
            String packageName = creationParams != null ? creationParams.getPackageName() : context.getPackageName();
            if (inSandbox && !getConnectionAllocator(packageName, inSandbox).isFreeConnectionAvailable()) {
                sBindingManager.releaseAllModerateBindings();
            }
        }
        return connection;
    }

    private static void freeConnection(ChildProcessConnection connection) {
        synchronized (ChildProcessLauncher.class) {
            if (connection.equals(sSpareSandboxedConnection)) {
                sSpareSandboxedConnection = null;
            }
        }
        final ChildProcessConnection conn = connection;
        ThreadUtils.postOnUiThreadDelayed(new Runnable() {
            public void run() {
                final PendingSpawnData pendingSpawn = ChildProcessLauncher.freeConnectionAndDequeuePending(conn);
                if (pendingSpawn != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            ChildProcessLauncher.startInternal(pendingSpawn.context(), pendingSpawn.commandLine(), pendingSpawn.childProcessId(), pendingSpawn.filesToBeMapped(), pendingSpawn.clientContext(), pendingSpawn.callbackType(), pendingSpawn.inSandbox(), pendingSpawn.getCreationParams());
                        }
                    }).start();
                }
            }
        }, 1);
    }

    private static PendingSpawnData freeConnectionAndDequeuePending(ChildProcessConnection conn) {
        ChildConnectionAllocator allocator = getConnectionAllocator(conn.getPackageName(), conn.isInSandbox());
        if ($assertionsDisabled || allocator != null) {
            PendingSpawnData dequeueLocked;
            PendingSpawnQueue pendingSpawnQueue = allocator.getPendingSpawnQueue();
            synchronized (pendingSpawnQueue.mPendingSpawnsLock) {
                allocator.free(conn);
                dequeueLocked = pendingSpawnQueue.dequeueLocked();
            }
            return dequeueLocked;
        }
        throw new AssertionError();
    }

    @VisibleForTesting
    public static void setBindingManagerForTesting(BindingManager manager) {
        sBindingManager = manager;
    }

    @CalledByNative
    private static boolean isOomProtected(int pid) {
        return sBindingManager.isOomProtected(pid);
    }

    @CalledByNative
    private static void registerViewSurface(int surfaceId, Surface surface) {
        if (surface.isValid()) {
            sViewSurfaceMap.put(Integer.valueOf(surfaceId), surface);
            return;
        }
        throw new RuntimeException("Attempting to register invalid Surface.");
    }

    @CalledByNative
    private static void unregisterViewSurface(int surfaceId) {
        sViewSurfaceMap.remove(Integer.valueOf(surfaceId));
    }

    @CalledByNative
    private static Surface getViewSurface(int surfaceId) {
        Surface surface = (Surface) sViewSurfaceMap.get(Integer.valueOf(surfaceId));
        if (surface == null) {
            Log.m28e(TAG, "Invalid surfaceId.", new Object[0]);
            return null;
        } else if (surface.isValid()) {
            return surface;
        } else {
            Log.m28e(TAG, "Requested surface is not valid.", new Object[0]);
            return null;
        }
    }

    private static void registerSurfaceTextureSurface(int surfaceTextureId, int clientId, Surface surface) {
        sSurfaceTextureSurfaceMap.put(new Pair(Integer.valueOf(surfaceTextureId), Integer.valueOf(clientId)), surface);
    }

    private static void unregisterSurfaceTextureSurface(int surfaceTextureId, int clientId) {
        Surface surface = (Surface) sSurfaceTextureSurfaceMap.remove(new Pair(Integer.valueOf(surfaceTextureId), Integer.valueOf(clientId)));
        if (surface != null) {
            if ($assertionsDisabled || surface.isValid()) {
                surface.release();
                return;
            }
            throw new AssertionError();
        }
    }

    @CalledByNative
    private static void createSurfaceTextureSurface(int surfaceTextureId, int clientId, SurfaceTexture surfaceTexture) {
        registerSurfaceTextureSurface(surfaceTextureId, clientId, new Surface(surfaceTexture));
    }

    @CalledByNative
    private static void destroySurfaceTextureSurface(int surfaceTextureId, int clientId) {
        unregisterSurfaceTextureSurface(surfaceTextureId, clientId);
    }

    @CalledByNative
    private static SurfaceWrapper getSurfaceTextureSurface(int surfaceTextureId, int clientId) {
        Surface surface = (Surface) sSurfaceTextureSurfaceMap.get(new Pair(Integer.valueOf(surfaceTextureId), Integer.valueOf(clientId)));
        if (surface == null) {
            Log.m28e(TAG, "Invalid Id for surface texture.", new Object[0]);
            return null;
        } else if ($assertionsDisabled || surface.isValid()) {
            return new SurfaceWrapper(surface);
        } else {
            throw new AssertionError();
        }
    }

    @CalledByNative
    public static void setInForeground(int pid, boolean inForeground) {
        sBindingManager.setInForeground(pid, inForeground);
    }

    public static void determinedVisibility(int pid) {
        sBindingManager.determinedVisibility(pid);
    }

    public static void onSentToBackground() {
        sApplicationInForeground = false;
        sBindingManager.onSentToBackground();
    }

    public static void startModerateBindingManagement(Context context, boolean moderateBindingTillBackgrounded) {
        sBindingManager.startModerateBindingManagement(context, getNumberOfServices(context, true, context.getPackageName()), moderateBindingTillBackgrounded);
    }

    public static void onBroughtToForeground() {
        sApplicationInForeground = true;
        sBindingManager.onBroughtToForeground();
    }

    static boolean isApplicationInForeground() {
        return sApplicationInForeground;
    }

    public static void warmUp(Context context) {
        synchronized (ChildProcessLauncher.class) {
            if ($assertionsDisabled || !ThreadUtils.runningOnUiThread()) {
                if (sSpareSandboxedConnection == null) {
                    ChildProcessCreationParams params = ChildProcessCreationParams.get();
                    if (params != null) {
                        params = params.copy();
                    }
                    sSpareSandboxedConnection = allocateBoundConnection(context, null, true, false, params);
                }
            } else {
                throw new AssertionError();
            }
        }
    }

    @CalledByNative
    private static FileDescriptorInfo makeFdInfo(int id, int fd, boolean autoClose, long offset, long size) {
        ParcelFileDescriptor pFd;
        if (autoClose) {
            pFd = ParcelFileDescriptor.adoptFd(fd);
        } else {
            try {
                pFd = ParcelFileDescriptor.fromFd(fd);
            } catch (IOException e) {
                Log.m28e(TAG, "Invalid FD provided for process connection, aborting connection.", e);
                return null;
            }
        }
        return new FileDescriptorInfo(id, pFd, offset, size);
    }

    @CalledByNative
    private static void start(Context context, String[] commandLine, int childProcessId, FileDescriptorInfo[] filesToBeMapped, long clientContext) {
        if ($assertionsDisabled || clientContext != 0) {
            int callbackType = 0;
            boolean inSandbox = true;
            String processType = ContentSwitches.getSwitchValue(commandLine, ContentSwitches.SWITCH_PROCESS_TYPE);
            ChildProcessCreationParams params = ChildProcessCreationParams.get();
            if (params != null) {
                params = params.copy();
            }
            if (ContentSwitches.SWITCH_RENDERER_PROCESS.equals(processType)) {
                callbackType = 2;
            } else {
                if (!(params == null || params.getPackageName().equals(context.getPackageName()))) {
                    params = new ChildProcessCreationParams(context.getPackageName(), params.getExtraBindFlags(), params.getLibraryProcessType());
                }
                if (ContentSwitches.SWITCH_GPU_PROCESS.equals(processType)) {
                    callbackType = 1;
                    inSandbox = false;
                } else if (ContentSwitches.SWITCH_UTILITY_PROCESS.equals(processType)) {
                    callbackType = 3;
                } else if (!$assertionsDisabled) {
                    throw new AssertionError();
                }
            }
            startInternal(context, commandLine, childProcessId, filesToBeMapped, clientContext, callbackType, inSandbox, params);
            return;
        }
        throw new AssertionError();
    }

    @SuppressLint({"NewApi"})
    @CalledByNative
    private static void startDownloadProcessIfNecessary(Context context, String[] commandLine) {
        if ($assertionsDisabled || VERSION.SDK_INT >= 18) {
            String processType = ContentSwitches.getSwitchValue(commandLine, ContentSwitches.SWITCH_PROCESS_TYPE);
            if ($assertionsDisabled || ContentSwitches.SWITCH_DOWNLOAD_PROCESS.equals(processType)) {
                Intent intent = new Intent();
                intent.setClass(context, DownloadProcessService.class);
                intent.setPackage(context.getPackageName());
                intent.putExtra(ChildProcessConstants.EXTRA_COMMAND_LINE, commandLine);
                Bundle bundle = createsServiceBundle(commandLine, null, Linker.getInstance().getSharedRelros());
                bundle.putBinder(ChildProcessConstants.EXTRA_CHILD_PROCESS_CALLBACK, createCallback(0, 4).asBinder());
                intent.putExtras(bundle);
                ChromiumLinkerParams linkerParams = getLinkerParamsForNewConnection();
                if (linkerParams != null) {
                    linkerParams.addIntentExtras(intent);
                }
                context.startService(intent);
                return;
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void startInternal(android.content.Context r22, java.lang.String[] r23, int r24, org.chromium.content.browser.FileDescriptorInfo[] r25, long r26, int r28, boolean r29, org.chromium.content.browser.ChildProcessCreationParams r30) {
        /*
        r8 = "ChildProcessLauncher.startInternal";
        org.chromium.base.TraceEvent.begin(r8);	 Catch:{ all -> 0x0088 }
        r7 = 0;
        if (r30 == 0) goto L_0x0080;
    L_0x0008:
        r19 = r30.getPackageName();	 Catch:{ all -> 0x0088 }
    L_0x000c:
        r9 = org.chromium.content.browser.ChildProcessLauncher.class;
        monitor-enter(r9);	 Catch:{ all -> 0x0088 }
        if (r29 == 0) goto L_0x0028;
    L_0x0011:
        r8 = sSpareSandboxedConnection;	 Catch:{ all -> 0x0085 }
        if (r8 == 0) goto L_0x0028;
    L_0x0015:
        r8 = sSpareSandboxedConnection;	 Catch:{ all -> 0x0085 }
        r8 = r8.getPackageName();	 Catch:{ all -> 0x0085 }
        r0 = r19;
        r8 = r8.equals(r0);	 Catch:{ all -> 0x0085 }
        if (r8 == 0) goto L_0x0028;
    L_0x0023:
        r7 = sSpareSandboxedConnection;	 Catch:{ all -> 0x0085 }
        r8 = 0;
        sSpareSandboxedConnection = r8;	 Catch:{ all -> 0x0085 }
    L_0x0028:
        monitor-exit(r9);	 Catch:{ all -> 0x0085 }
        if (r7 != 0) goto L_0x0091;
    L_0x002b:
        r18 = 0;
        r8 = 1;
        r0 = r28;
        if (r0 != r8) goto L_0x0034;
    L_0x0032:
        r18 = 1;
    L_0x0034:
        r0 = r22;
        r1 = r19;
        r2 = r29;
        r20 = getPendingSpawnQueue(r0, r1, r2);	 Catch:{ all -> 0x0088 }
        r0 = r20;
        r0 = r0.mPendingSpawnsLock;	 Catch:{ all -> 0x0088 }
        r21 = r0;
        monitor-enter(r21);	 Catch:{ all -> 0x0088 }
        r0 = r22;
        r1 = r23;
        r2 = r29;
        r3 = r18;
        r4 = r30;
        r6 = allocateBoundConnection(r0, r1, r2, r3, r4);	 Catch:{ all -> 0x00b3 }
        if (r6 != 0) goto L_0x008f;
    L_0x0055:
        r8 = "ChildProcLauncher";
        r9 = "Allocation of new service failed. Queuing up pending spawn.";
        org.chromium.base.Log.m20d(r8, r9);	 Catch:{ all -> 0x00b6 }
        r7 = new org.chromium.content.browser.ChildProcessLauncher$PendingSpawnData;	 Catch:{ all -> 0x00b6 }
        r17 = 0;
        r8 = r22;
        r9 = r23;
        r10 = r24;
        r11 = r25;
        r12 = r26;
        r14 = r28;
        r15 = r29;
        r16 = r30;
        r7.<init>(r8, r9, r10, r11, r12, r14, r15, r16);	 Catch:{ all -> 0x00b6 }
        r0 = r20;
        r0.enqueueLocked(r7);	 Catch:{ all -> 0x00b6 }
        monitor-exit(r21);	 Catch:{ all -> 0x00b6 }
        r8 = "ChildProcessLauncher.startInternal";
        org.chromium.base.TraceEvent.end(r8);
        r7 = r6;
    L_0x007f:
        return;
    L_0x0080:
        r19 = r22.getPackageName();	 Catch:{ all -> 0x0088 }
        goto L_0x000c;
    L_0x0085:
        r8 = move-exception;
        monitor-exit(r9);	 Catch:{ all -> 0x0085 }
        throw r8;	 Catch:{ all -> 0x0088 }
    L_0x0088:
        r8 = move-exception;
        r9 = "ChildProcessLauncher.startInternal";
        org.chromium.base.TraceEvent.end(r9);
        throw r8;
    L_0x008f:
        monitor-exit(r21);	 Catch:{ all -> 0x00b6 }
        r7 = r6;
    L_0x0091:
        r8 = "ChildProcLauncher";
        r9 = "Setting up connection to process: slot=%d";
        r10 = r7.getServiceNumber();	 Catch:{ all -> 0x0088 }
        r10 = java.lang.Integer.valueOf(r10);	 Catch:{ all -> 0x0088 }
        org.chromium.base.Log.m21d(r8, r9, r10);	 Catch:{ all -> 0x0088 }
        r8 = r23;
        r9 = r24;
        r10 = r25;
        r11 = r28;
        r12 = r26;
        triggerConnectionSetup(r7, r8, r9, r10, r11, r12);	 Catch:{ all -> 0x0088 }
        r8 = "ChildProcessLauncher.startInternal";
        org.chromium.base.TraceEvent.end(r8);
        goto L_0x007f;
    L_0x00b3:
        r8 = move-exception;
    L_0x00b4:
        monitor-exit(r21);	 Catch:{ all -> 0x00b3 }
        throw r8;	 Catch:{ all -> 0x0088 }
    L_0x00b6:
        r8 = move-exception;
        r7 = r6;
        goto L_0x00b4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.browser.ChildProcessLauncher.startInternal(android.content.Context, java.lang.String[], int, org.chromium.content.browser.FileDescriptorInfo[], long, int, boolean, org.chromium.content.browser.ChildProcessCreationParams):void");
    }

    protected static Bundle createsServiceBundle(String[] commandLine, FileDescriptorInfo[] filesToBeMapped, Bundle sharedRelros) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(ChildProcessConstants.EXTRA_COMMAND_LINE, commandLine);
        bundle.putParcelableArray(ChildProcessConstants.EXTRA_FILES, filesToBeMapped);
        bundle.putInt(ChildProcessConstants.EXTRA_CPU_COUNT, CpuFeatures.getCount());
        bundle.putLong(ChildProcessConstants.EXTRA_CPU_FEATURES, CpuFeatures.getMask());
        bundle.putBundle(Linker.EXTRA_LINKER_SHARED_RELROS, sharedRelros);
        return bundle;
    }

    @VisibleForTesting
    static void triggerConnectionSetup(final ChildProcessConnection connection, String[] commandLine, int childProcessId, FileDescriptorInfo[] filesToBeMapped, final int callbackType, final long clientContext) {
        ConnectionCallback connectionCallback = new ConnectionCallback() {
            public void onConnected(int pid) {
                Log.m23d(ChildProcessLauncher.TAG, "on connect callback, pid=%d context=%d callbackType=%d", Integer.valueOf(pid), Long.valueOf(clientContext), Integer.valueOf(callbackType));
                if (pid != 0) {
                    ChildProcessLauncher.sBindingManager.addNewConnection(pid, connection);
                    ChildProcessLauncher.sServiceMap.put(Integer.valueOf(pid), connection);
                }
                if (clientContext != 0) {
                    ChildProcessLauncher.nativeOnChildProcessStarted(clientContext, pid);
                }
            }
        };
        if ($assertionsDisabled || callbackType != 0) {
            connection.setupConnection(commandLine, filesToBeMapped, createCallback(childProcessId, callbackType), connectionCallback, Linker.getInstance().getSharedRelros());
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    static void stop(int pid) {
        Log.m21d(TAG, "stopping child connection: pid=%d", Integer.valueOf(pid));
        ChildProcessConnection connection = (ChildProcessConnection) sServiceMap.remove(Integer.valueOf(pid));
        if (connection == null) {
            logPidWarning(pid, "Tried to stop non-existent connection");
            return;
        }
        sBindingManager.clearConnection(pid);
        connection.stop();
        freeConnection(connection);
    }

    private static IChildProcessCallback createCallback(final int childProcessId, final int callbackType) {
        return new Stub() {
            public void establishSurfacePeer(int pid, Surface surface, int primaryID, int secondaryID) {
                if (callbackType != 1) {
                    Log.m28e(ChildProcessLauncher.TAG, "Illegal callback for non-GPU process.", new Object[0]);
                } else {
                    ChildProcessLauncher.nativeEstablishSurfacePeer(pid, surface, primaryID, secondaryID);
                }
            }

            public SurfaceWrapper getViewSurface(int surfaceId) {
                if (callbackType != 1) {
                    Log.m28e(ChildProcessLauncher.TAG, "Illegal callback for non-GPU process.", new Object[0]);
                    return null;
                }
                Surface surface = ChildProcessLauncher.getViewSurface(surfaceId);
                if (surface != null) {
                    return new SurfaceWrapper(surface);
                }
                return null;
            }

            public void registerSurfaceTextureSurface(int surfaceTextureId, int clientId, Surface surface) {
                if (callbackType != 1) {
                    Log.m28e(ChildProcessLauncher.TAG, "Illegal callback for non-GPU process.", new Object[0]);
                } else {
                    ChildProcessLauncher.registerSurfaceTextureSurface(surfaceTextureId, clientId, surface);
                }
            }

            public void unregisterSurfaceTextureSurface(int surfaceTextureId, int clientId) {
                if (callbackType != 1) {
                    Log.m28e(ChildProcessLauncher.TAG, "Illegal callback for non-GPU process.", new Object[0]);
                } else {
                    ChildProcessLauncher.unregisterSurfaceTextureSurface(surfaceTextureId, clientId);
                }
            }

            public SurfaceWrapper getSurfaceTextureSurface(int surfaceTextureId) {
                if (callbackType == 2) {
                    return ChildProcessLauncher.getSurfaceTextureSurface(surfaceTextureId, childProcessId);
                }
                Log.m28e(ChildProcessLauncher.TAG, "Illegal callback for non-renderer process.", new Object[0]);
                return null;
            }

            public void onDownloadStarted(boolean started, int downloadId) {
                if (callbackType != 4) {
                    Log.m28e(ChildProcessLauncher.TAG, "Illegal callback for non-download process.", new Object[0]);
                }
            }
        };
    }

    static void logPidWarning(int pid, String message) {
        if (pid > 0 && !nativeIsSingleProcess()) {
            Log.m38w(TAG, "%s, pid=%d", message, Integer.valueOf(pid));
        }
    }

    @VisibleForTesting
    static ChildProcessConnection allocateBoundConnectionForTesting(Context context, ChildProcessCreationParams creationParams) {
        return allocateBoundConnection(context, null, true, false, creationParams);
    }

    @VisibleForTesting
    static ChildProcessConnection allocateConnectionForTesting(Context context, ChildProcessCreationParams creationParams) {
        return allocateConnection(context, true, getLinkerParamsForNewConnection(), false, creationParams);
    }

    @VisibleForTesting
    static void enqueuePendingSpawnForTesting(Context context, String[] commandLine, ChildProcessCreationParams creationParams, boolean inSandbox) {
        PendingSpawnQueue pendingSpawnQueue = getPendingSpawnQueue(context, creationParams != null ? creationParams.getPackageName() : context.getPackageName(), inSandbox);
        synchronized (pendingSpawnQueue.mPendingSpawnsLock) {
            pendingSpawnQueue.enqueueLocked(new PendingSpawnData(context, commandLine, 1, new FileDescriptorInfo[0], 0, 2, true, creationParams));
        }
    }

    @VisibleForTesting
    static int allocatedSandboxedConnectionsCountForTesting(Context context, String packageName) {
        initConnectionAllocatorsIfNecessary(context, true, packageName);
        return ((ChildConnectionAllocator) sSandboxedChildConnectionAllocatorMap.get(packageName)).allocatedConnectionsCountForTesting();
    }

    @VisibleForTesting
    static int connectedServicesCountForTesting() {
        return sServiceMap.size();
    }

    @VisibleForTesting
    static int pendingSpawnsCountForTesting(Context context, String packageName, boolean inSandbox) {
        int sizeLocked;
        PendingSpawnQueue pendingSpawnQueue = getPendingSpawnQueue(context, packageName, inSandbox);
        synchronized (pendingSpawnQueue.mPendingSpawnsLock) {
            sizeLocked = pendingSpawnQueue.sizeLocked();
        }
        return sizeLocked;
    }

    @VisibleForTesting
    public static boolean crashProcessForTesting(int pid) {
        if (sServiceMap.get(Integer.valueOf(pid)) == null) {
            return false;
        }
        try {
            ((ChildProcessConnectionImpl) sServiceMap.get(Integer.valueOf(pid))).crashServiceForTesting();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}
