package org.chromium.content.browser;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.RemoteException;
import java.io.IOException;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.TraceEvent;
import org.chromium.base.VisibleForTesting;
import org.chromium.content.app.ChromiumLinkerParams;
import org.chromium.content.browser.ChildProcessConnection.ConnectionCallback;
import org.chromium.content.browser.ChildProcessConnection.DeathCallback;
import org.chromium.content.common.IChildProcessCallback;
import org.chromium.content.common.IChildProcessService;

public class ChildProcessConnectionImpl implements ChildProcessConnection {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String TAG = "ChildProcessConnect";
    private static Boolean[] sNeedsExtrabindFlags = new Boolean[2];
    private final boolean mAlwaysInForeground;
    private ConnectionCallback mConnectionCallback;
    private ConnectionParams mConnectionParams;
    private final Context mContext;
    private final ChildProcessCreationParams mCreationParams;
    private final DeathCallback mDeathCallback;
    private final boolean mInSandbox;
    private ChildServiceConnection mInitialBinding = null;
    private ChromiumLinkerParams mLinkerParams = null;
    private final Object mLock = new Object();
    private ChildServiceConnection mModerateBinding = null;
    private int mPid = 0;
    private IChildProcessService mService = null;
    private boolean mServiceConnectComplete = false;
    private boolean mServiceDisconnected = false;
    private final ComponentName mServiceName;
    private final int mServiceNumber;
    private ChildServiceConnection mStrongBinding = null;
    private int mStrongBindingCount = 0;
    private ChildServiceConnection mWaivedBinding = null;
    private boolean mWasOomProtected = false;

    private class ChildServiceConnection implements ServiceConnection {
        private final int mBindFlags;
        private boolean mBound = false;

        private Intent createServiceBindIntent() {
            Intent intent = new Intent();
            if (ChildProcessConnectionImpl.this.mCreationParams != null) {
                ChildProcessConnectionImpl.this.mCreationParams.addIntentExtras(intent);
            }
            intent.setComponent(ChildProcessConnectionImpl.this.mServiceName);
            return intent;
        }

        public ChildServiceConnection(int bindFlags, boolean needsExtraBindFlags) {
            if (needsExtraBindFlags && ChildProcessConnectionImpl.this.mCreationParams != null) {
                bindFlags = ChildProcessConnectionImpl.this.mCreationParams.addExtraBindFlags(bindFlags);
            }
            this.mBindFlags = bindFlags;
        }

        boolean bind(String[] commandLine) {
            if (!this.mBound) {
                try {
                    TraceEvent.begin("ChildProcessConnectionImpl.ChildServiceConnection.bind");
                    Intent intent = createServiceBindIntent();
                    if (commandLine != null) {
                        intent.putExtra(ChildProcessConstants.EXTRA_COMMAND_LINE, commandLine);
                    }
                    if (ChildProcessConnectionImpl.this.mLinkerParams != null) {
                        ChildProcessConnectionImpl.this.mLinkerParams.addIntentExtras(intent);
                    }
                    this.mBound = ChildProcessConnectionImpl.this.mContext.bindService(intent, this, this.mBindFlags);
                } finally {
                    TraceEvent.end("ChildProcessConnectionImpl.ChildServiceConnection.bind");
                }
            }
            return this.mBound;
        }

        void unbind() {
            if (this.mBound) {
                ChildProcessConnectionImpl.this.mContext.unbindService(this);
                this.mBound = false;
            }
        }

        boolean isBound() {
            return this.mBound;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onServiceConnected(android.content.ComponentName r4, android.os.IBinder r5) {
            /*
            r3 = this;
            r0 = org.chromium.content.browser.ChildProcessConnectionImpl.this;
            r1 = r0.mLock;
            monitor-enter(r1);
            r0 = org.chromium.content.browser.ChildProcessConnectionImpl.this;	 Catch:{ all -> 0x0039 }
            r0 = r0.mServiceConnectComplete;	 Catch:{ all -> 0x0039 }
            if (r0 == 0) goto L_0x0011;
        L_0x000f:
            monitor-exit(r1);	 Catch:{ all -> 0x0039 }
        L_0x0010:
            return;
        L_0x0011:
            r0 = "ChildProcessConnectionImpl.ChildServiceConnection.onServiceConnected";
            org.chromium.base.TraceEvent.begin(r0);	 Catch:{ all -> 0x003c }
            r0 = org.chromium.content.browser.ChildProcessConnectionImpl.this;	 Catch:{ all -> 0x003c }
            r2 = 1;
            r0.mServiceConnectComplete = r2;	 Catch:{ all -> 0x003c }
            r0 = org.chromium.content.browser.ChildProcessConnectionImpl.this;	 Catch:{ all -> 0x003c }
            r2 = org.chromium.content.common.IChildProcessService.Stub.asInterface(r5);	 Catch:{ all -> 0x003c }
            r0.mService = r2;	 Catch:{ all -> 0x003c }
            r0 = org.chromium.content.browser.ChildProcessConnectionImpl.this;	 Catch:{ all -> 0x003c }
            r0 = r0.mConnectionParams;	 Catch:{ all -> 0x003c }
            if (r0 == 0) goto L_0x0032;
        L_0x002d:
            r0 = org.chromium.content.browser.ChildProcessConnectionImpl.this;	 Catch:{ all -> 0x003c }
            r0.doConnectionSetupLocked();	 Catch:{ all -> 0x003c }
        L_0x0032:
            r0 = "ChildProcessConnectionImpl.ChildServiceConnection.onServiceConnected";
            org.chromium.base.TraceEvent.end(r0);	 Catch:{ all -> 0x0039 }
            monitor-exit(r1);	 Catch:{ all -> 0x0039 }
            goto L_0x0010;
        L_0x0039:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x0039 }
            throw r0;
        L_0x003c:
            r0 = move-exception;
            r2 = "ChildProcessConnectionImpl.ChildServiceConnection.onServiceConnected";
            org.chromium.base.TraceEvent.end(r2);	 Catch:{ all -> 0x0039 }
            throw r0;	 Catch:{ all -> 0x0039 }
            */
            throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.browser.ChildProcessConnectionImpl.ChildServiceConnection.onServiceConnected(android.content.ComponentName, android.os.IBinder):void");
        }

        public void onServiceDisconnected(ComponentName className) {
            synchronized (ChildProcessConnectionImpl.this.mLock) {
                if (ChildProcessConnectionImpl.this.mServiceDisconnected) {
                    return;
                }
                ChildProcessConnectionImpl.this.mWasOomProtected = ChildProcessConnectionImpl.this.isCurrentlyOomProtected();
                ChildProcessConnectionImpl.this.mServiceDisconnected = true;
                Log.m38w(ChildProcessConnectionImpl.TAG, "onServiceDisconnected (crash or killed by oom): pid=%d", Integer.valueOf(ChildProcessConnectionImpl.this.mPid));
                ChildProcessConnectionImpl.this.stop();
                ChildProcessConnectionImpl.this.mDeathCallback.onChildProcessDied(ChildProcessConnectionImpl.this);
                if (ChildProcessConnectionImpl.this.mConnectionCallback != null) {
                    ChildProcessConnectionImpl.this.mConnectionCallback.onConnected(0);
                }
                ChildProcessConnectionImpl.this.mConnectionCallback = null;
            }
        }
    }

    private static class ConnectionParams {
        final IChildProcessCallback mCallback;
        final String[] mCommandLine;
        final FileDescriptorInfo[] mFilesToBeMapped;
        final Bundle mSharedRelros;

        ConnectionParams(String[] commandLine, FileDescriptorInfo[] filesToBeMapped, IChildProcessCallback callback, Bundle sharedRelros) {
            this.mCommandLine = commandLine;
            this.mFilesToBeMapped = filesToBeMapped;
            this.mCallback = callback;
            this.mSharedRelros = sharedRelros;
        }
    }

    static {
        boolean z;
        if (ChildProcessConnectionImpl.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
    }

    ChildProcessConnectionImpl(Context context, int number, boolean inSandbox, DeathCallback deathCallback, String serviceClassName, ChromiumLinkerParams chromiumLinkerParams, boolean alwaysInForeground, ChildProcessCreationParams creationParams) {
        this.mContext = context;
        this.mServiceNumber = number;
        this.mInSandbox = inSandbox;
        this.mDeathCallback = deathCallback;
        this.mServiceName = new ComponentName(creationParams != null ? creationParams.getPackageName() : context.getPackageName(), serviceClassName + this.mServiceNumber);
        this.mLinkerParams = chromiumLinkerParams;
        this.mAlwaysInForeground = alwaysInForeground;
        this.mCreationParams = creationParams;
        int initialFlags = 1;
        if (this.mAlwaysInForeground) {
            initialFlags = 1 | 64;
        }
        boolean needsExtraBindFlags = isExportedService(inSandbox, this.mContext, this.mServiceName);
        this.mInitialBinding = new ChildServiceConnection(initialFlags, needsExtraBindFlags);
        this.mStrongBinding = new ChildServiceConnection(65, needsExtraBindFlags);
        this.mWaivedBinding = new ChildServiceConnection(33, needsExtraBindFlags);
        this.mModerateBinding = new ChildServiceConnection(1, needsExtraBindFlags);
    }

    private static boolean isExportedService(boolean inSandbox, Context context, ComponentName serviceName) {
        int arrayIndex;
        if (inSandbox) {
            arrayIndex = 1;
        } else {
            arrayIndex = 0;
        }
        if (sNeedsExtrabindFlags[arrayIndex] != null) {
            return sNeedsExtrabindFlags[arrayIndex].booleanValue();
        }
        boolean result = false;
        try {
            result = context.getPackageManager().getServiceInfo(serviceName, 0).exported;
        } catch (NameNotFoundException e) {
            Log.m28e(TAG, "Could not retrieve info about service %s", serviceName, e);
        }
        sNeedsExtrabindFlags[arrayIndex] = Boolean.valueOf(result);
        return result;
    }

    public int getServiceNumber() {
        return this.mServiceNumber;
    }

    public boolean isInSandbox() {
        return this.mInSandbox;
    }

    public String getPackageName() {
        return this.mCreationParams != null ? this.mCreationParams.getPackageName() : this.mContext.getPackageName();
    }

    public IChildProcessService getService() {
        IChildProcessService iChildProcessService;
        synchronized (this.mLock) {
            iChildProcessService = this.mService;
        }
        return iChildProcessService;
    }

    public int getPid() {
        int i;
        synchronized (this.mLock) {
            i = this.mPid;
        }
        return i;
    }

    public void start(String[] commandLine) {
        try {
            TraceEvent.begin("ChildProcessConnectionImpl.start");
            synchronized (this.mLock) {
                if (!$assertionsDisabled && ThreadUtils.runningOnUiThread()) {
                    throw new AssertionError();
                } else if ($assertionsDisabled || this.mConnectionParams == null) {
                    if (this.mInitialBinding.bind(commandLine)) {
                        this.mWaivedBinding.bind(null);
                    } else {
                        Log.m28e(TAG, "Failed to establish the service connection.", new Object[0]);
                        this.mDeathCallback.onChildProcessDied(this);
                    }
                } else {
                    throw new AssertionError("setupConnection() called before start() in ChildProcessConnectionImpl.");
                }
            }
        } finally {
            TraceEvent.end("ChildProcessConnectionImpl.start");
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setupConnection(java.lang.String[] r5, org.chromium.content.browser.FileDescriptorInfo[] r6, org.chromium.content.common.IChildProcessCallback r7, org.chromium.content.browser.ChildProcessConnection.ConnectionCallback r8, android.os.Bundle r9) {
        /*
        r4 = this;
        r1 = r4.mLock;
        monitor-enter(r1);
        r0 = $assertionsDisabled;	 Catch:{ all -> 0x0011 }
        if (r0 != 0) goto L_0x0014;
    L_0x0007:
        r0 = r4.mConnectionParams;	 Catch:{ all -> 0x0011 }
        if (r0 == 0) goto L_0x0014;
    L_0x000b:
        r0 = new java.lang.AssertionError;	 Catch:{ all -> 0x0011 }
        r0.<init>();	 Catch:{ all -> 0x0011 }
        throw r0;	 Catch:{ all -> 0x0011 }
    L_0x0011:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0011 }
        throw r0;
    L_0x0014:
        r0 = r4.mServiceDisconnected;	 Catch:{ all -> 0x0011 }
        if (r0 == 0) goto L_0x0028;
    L_0x0018:
        r0 = "ChildProcessConnect";
        r2 = "Tried to setup a connection that already disconnected.";
        r3 = 0;
        r3 = new java.lang.Object[r3];	 Catch:{ all -> 0x0011 }
        org.chromium.base.Log.m38w(r0, r2, r3);	 Catch:{ all -> 0x0011 }
        r0 = 0;
        r8.onConnected(r0);	 Catch:{ all -> 0x0011 }
        monitor-exit(r1);	 Catch:{ all -> 0x0011 }
    L_0x0027:
        return;
    L_0x0028:
        r0 = "ChildProcessConnectionImpl.setupConnection";
        org.chromium.base.TraceEvent.begin(r0);	 Catch:{ all -> 0x0044 }
        r4.mConnectionCallback = r8;	 Catch:{ all -> 0x0044 }
        r0 = new org.chromium.content.browser.ChildProcessConnectionImpl$ConnectionParams;	 Catch:{ all -> 0x0044 }
        r0.<init>(r5, r6, r7, r9);	 Catch:{ all -> 0x0044 }
        r4.mConnectionParams = r0;	 Catch:{ all -> 0x0044 }
        r0 = r4.mServiceConnectComplete;	 Catch:{ all -> 0x0044 }
        if (r0 == 0) goto L_0x003d;
    L_0x003a:
        r4.doConnectionSetupLocked();	 Catch:{ all -> 0x0044 }
    L_0x003d:
        r0 = "ChildProcessConnectionImpl.setupConnection";
        org.chromium.base.TraceEvent.end(r0);	 Catch:{ all -> 0x0011 }
        monitor-exit(r1);	 Catch:{ all -> 0x0011 }
        goto L_0x0027;
    L_0x0044:
        r0 = move-exception;
        r2 = "ChildProcessConnectionImpl.setupConnection";
        org.chromium.base.TraceEvent.end(r2);	 Catch:{ all -> 0x0011 }
        throw r0;	 Catch:{ all -> 0x0011 }
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.browser.ChildProcessConnectionImpl.setupConnection(java.lang.String[], org.chromium.content.browser.FileDescriptorInfo[], org.chromium.content.common.IChildProcessCallback, org.chromium.content.browser.ChildProcessConnection$ConnectionCallback, android.os.Bundle):void");
    }

    public void stop() {
        synchronized (this.mLock) {
            this.mInitialBinding.unbind();
            this.mStrongBinding.unbind();
            this.mWaivedBinding.unbind();
            this.mModerateBinding.unbind();
            this.mStrongBindingCount = 0;
            if (this.mService != null) {
                this.mService = null;
            }
            this.mConnectionParams = null;
        }
    }

    private void doConnectionSetupLocked() {
        try {
            TraceEvent.begin("ChildProcessConnectionImpl.doConnectionSetupLocked");
            if (!$assertionsDisabled && (!this.mServiceConnectComplete || this.mService == null)) {
                throw new AssertionError();
            } else if ($assertionsDisabled || this.mConnectionParams != null) {
                this.mPid = this.mService.setupConnection(ChildProcessLauncher.createsServiceBundle(this.mConnectionParams.mCommandLine, this.mConnectionParams.mFilesToBeMapped, this.mConnectionParams.mSharedRelros), this.mConnectionParams.mCallback);
                if (!$assertionsDisabled && this.mPid == 0) {
                    throw new AssertionError("Child service claims to be run by a process of pid=0.");
                }
                try {
                    for (FileDescriptorInfo fileInfo : this.mConnectionParams.mFilesToBeMapped) {
                        fileInfo.mFd.close();
                    }
                } catch (IOException ioe) {
                    Log.m38w(TAG, "Failed to close FD.", ioe);
                }
                this.mConnectionParams = null;
                if (this.mConnectionCallback != null) {
                    this.mConnectionCallback.onConnected(this.mPid);
                }
                this.mConnectionCallback = null;
                TraceEvent.end("ChildProcessConnectionImpl.doConnectionSetupLocked");
            } else {
                throw new AssertionError();
            }
        } catch (RemoteException re) {
            Log.m28e(TAG, "Failed to setup connection.", re);
        } catch (Throwable th) {
            TraceEvent.end("ChildProcessConnectionImpl.doConnectionSetupLocked");
        }
    }

    public boolean isInitialBindingBound() {
        boolean isBound;
        synchronized (this.mLock) {
            isBound = this.mInitialBinding.isBound();
        }
        return isBound;
    }

    public boolean isStrongBindingBound() {
        boolean isBound;
        synchronized (this.mLock) {
            isBound = this.mStrongBinding.isBound();
        }
        return isBound;
    }

    public void removeInitialBinding() {
        synchronized (this.mLock) {
            if ($assertionsDisabled || !this.mAlwaysInForeground) {
                this.mInitialBinding.unbind();
            } else {
                throw new AssertionError();
            }
        }
    }

    public boolean isOomProtectedOrWasWhenDied() {
        boolean z;
        synchronized (this.mLock) {
            if (this.mServiceDisconnected) {
                z = this.mWasOomProtected;
            } else {
                z = isCurrentlyOomProtected();
            }
        }
        return z;
    }

    private boolean isCurrentlyOomProtected() {
        boolean isApplicationInForeground;
        synchronized (this.mLock) {
            if (!$assertionsDisabled && this.mServiceDisconnected) {
                throw new AssertionError();
            } else if (this.mAlwaysInForeground) {
                isApplicationInForeground = ChildProcessLauncher.isApplicationInForeground();
            } else {
                isApplicationInForeground = this.mInitialBinding.isBound() || this.mStrongBinding.isBound();
            }
        }
        return isApplicationInForeground;
    }

    public void dropOomBindings() {
        synchronized (this.mLock) {
            if ($assertionsDisabled || !this.mAlwaysInForeground) {
                this.mInitialBinding.unbind();
                this.mStrongBindingCount = 0;
                this.mStrongBinding.unbind();
                this.mModerateBinding.unbind();
            } else {
                throw new AssertionError();
            }
        }
    }

    public void addStrongBinding() {
        synchronized (this.mLock) {
            if (this.mService == null) {
                Log.m38w(TAG, "The connection is not bound for %d", Integer.valueOf(this.mPid));
                return;
            }
            if (this.mStrongBindingCount == 0) {
                this.mStrongBinding.bind(null);
            }
            this.mStrongBindingCount++;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeStrongBinding() {
        /*
        r6 = this;
        r1 = r6.mLock;
        monitor-enter(r1);
        r0 = r6.mService;	 Catch:{ all -> 0x002a }
        if (r0 != 0) goto L_0x001c;
    L_0x0007:
        r0 = "ChildProcessConnect";
        r2 = "The connection is not bound for %d";
        r3 = 1;
        r3 = new java.lang.Object[r3];	 Catch:{ all -> 0x002a }
        r4 = 0;
        r5 = r6.mPid;	 Catch:{ all -> 0x002a }
        r5 = java.lang.Integer.valueOf(r5);	 Catch:{ all -> 0x002a }
        r3[r4] = r5;	 Catch:{ all -> 0x002a }
        org.chromium.base.Log.m38w(r0, r2, r3);	 Catch:{ all -> 0x002a }
        monitor-exit(r1);	 Catch:{ all -> 0x002a }
    L_0x001b:
        return;
    L_0x001c:
        r0 = $assertionsDisabled;	 Catch:{ all -> 0x002a }
        if (r0 != 0) goto L_0x002d;
    L_0x0020:
        r0 = r6.mStrongBindingCount;	 Catch:{ all -> 0x002a }
        if (r0 > 0) goto L_0x002d;
    L_0x0024:
        r0 = new java.lang.AssertionError;	 Catch:{ all -> 0x002a }
        r0.<init>();	 Catch:{ all -> 0x002a }
        throw r0;	 Catch:{ all -> 0x002a }
    L_0x002a:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x002a }
        throw r0;
    L_0x002d:
        r0 = r6.mStrongBindingCount;	 Catch:{ all -> 0x002a }
        r0 = r0 + -1;
        r6.mStrongBindingCount = r0;	 Catch:{ all -> 0x002a }
        r0 = r6.mStrongBindingCount;	 Catch:{ all -> 0x002a }
        if (r0 != 0) goto L_0x003c;
    L_0x0037:
        r0 = r6.mStrongBinding;	 Catch:{ all -> 0x002a }
        r0.unbind();	 Catch:{ all -> 0x002a }
    L_0x003c:
        monitor-exit(r1);	 Catch:{ all -> 0x002a }
        goto L_0x001b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.browser.ChildProcessConnectionImpl.removeStrongBinding():void");
    }

    public boolean isModerateBindingBound() {
        boolean isBound;
        synchronized (this.mLock) {
            isBound = this.mModerateBinding.isBound();
        }
        return isBound;
    }

    public void addModerateBinding() {
        synchronized (this.mLock) {
            if (this.mService == null) {
                Log.m38w(TAG, "The connection is not bound for %d", Integer.valueOf(this.mPid));
                return;
            }
            this.mModerateBinding.bind(null);
        }
    }

    public void removeModerateBinding() {
        synchronized (this.mLock) {
            if (this.mService == null) {
                Log.m38w(TAG, "The connection is not bound for %d", Integer.valueOf(this.mPid));
                return;
            }
            this.mModerateBinding.unbind();
        }
    }

    @VisibleForTesting
    public boolean crashServiceForTesting() throws RemoteException {
        try {
            this.mService.crashIntentionallyForTesting();
            return false;
        } catch (DeadObjectException e) {
            return true;
        }
    }

    @VisibleForTesting
    public boolean isConnected() {
        return this.mService != null;
    }
}
