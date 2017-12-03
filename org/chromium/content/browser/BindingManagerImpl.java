package org.chromium.content.browser;

import android.annotation.TargetApi;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Handler;
import android.util.LruCache;
import android.util.SparseArray;
import java.util.Map.Entry;
import org.chromium.base.Log;
import org.chromium.base.SysUtils;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;

class BindingManagerImpl implements BindingManager {
    static final /* synthetic */ boolean $assertionsDisabled = (!BindingManagerImpl.class.desiredAssertionStatus());
    private static final long DETACH_AS_ACTIVE_HIGH_END_DELAY_MILLIS = 1000;
    private static final float MODERATE_BINDING_HIGH_REDUCE_RATIO = 0.5f;
    private static final float MODERATE_BINDING_LOW_REDUCE_RATIO = 0.25f;
    private static final long MODERATE_BINDING_POOL_CLEARER_DELAY_MILLIS = 10000;
    private static final String TAG = "cr.BindingManager";
    private ManagedConnection mBoundForBackgroundPeriod;
    private final boolean mIsLowMemoryDevice;
    private ManagedConnection mLastInForeground;
    private final Object mLastInForegroundLock = new Object();
    private final SparseArray<ManagedConnection> mManagedConnections = new SparseArray();
    private ModerateBindingPool mModerateBindingPool;
    private final Object mModerateBindingPoolLock = new Object();
    private boolean mModerateBindingTillBackgrounded;
    private final boolean mOnTesting;

    private class ManagedConnection {
        static final /* synthetic */ boolean $assertionsDisabled = (!BindingManagerImpl.class.desiredAssertionStatus());
        private boolean mBoundForBackgroundPeriod;
        private ChildProcessConnection mConnection;
        private boolean mInForeground;
        private boolean mWasOomProtected;

        private boolean removeInitialBinding() {
            if (this.mConnection == null || !this.mConnection.isInitialBindingBound()) {
                return false;
            }
            this.mConnection.removeInitialBinding();
            return true;
        }

        private void addStrongBinding() {
            ChildProcessConnection connection = this.mConnection;
            if (connection != null) {
                ModerateBindingPool moderateBindingPool;
                connection.addStrongBinding();
                synchronized (BindingManagerImpl.this.mModerateBindingPoolLock) {
                    moderateBindingPool = BindingManagerImpl.this.mModerateBindingPool;
                }
                if (moderateBindingPool != null) {
                    moderateBindingPool.removeConnection(this);
                }
            }
        }

        private void removeStrongBinding(final boolean keepAsModerate) {
            final ChildProcessConnection connection = this.mConnection;
            if (connection != null && connection.isStrongBindingBound()) {
                Runnable doUnbind = new Runnable() {
                    public void run() {
                        if (connection.isStrongBindingBound()) {
                            connection.removeStrongBinding();
                            if (keepAsModerate) {
                                ManagedConnection.this.addConnectionToModerateBindingPool(connection);
                            }
                        }
                    }
                };
                if (BindingManagerImpl.this.mIsLowMemoryDevice) {
                    doUnbind.run();
                } else {
                    ThreadUtils.postOnUiThreadDelayed(doUnbind, BindingManagerImpl.DETACH_AS_ACTIVE_HIGH_END_DELAY_MILLIS);
                }
            }
        }

        private void addConnectionToModerateBindingPool(ChildProcessConnection connection) {
            synchronized (BindingManagerImpl.this.mModerateBindingPoolLock) {
                ModerateBindingPool moderateBindingPool = BindingManagerImpl.this.mModerateBindingPool;
            }
            if (moderateBindingPool != null && !connection.isStrongBindingBound()) {
                moderateBindingPool.addConnection(this);
            }
        }

        private void removeModerateBinding() {
            if (this.mConnection != null && this.mConnection.isModerateBindingBound()) {
                this.mConnection.removeModerateBinding();
            }
        }

        private void addModerateBinding() {
            ChildProcessConnection connection = this.mConnection;
            if (connection != null) {
                connection.addModerateBinding();
            }
        }

        private void dropBindings() {
            if ($assertionsDisabled || BindingManagerImpl.this.mIsLowMemoryDevice) {
                ChildProcessConnection connection = this.mConnection;
                if (connection != null) {
                    connection.dropOomBindings();
                    return;
                }
                return;
            }
            throw new AssertionError();
        }

        ManagedConnection(ChildProcessConnection connection) {
            this.mConnection = connection;
        }

        void setInForeground(boolean nextInForeground) {
            if (!this.mInForeground && nextInForeground) {
                addStrongBinding();
            } else if (this.mInForeground && !nextInForeground) {
                removeStrongBinding(true);
            }
            this.mInForeground = nextInForeground;
        }

        void determinedVisibility() {
            if (removeInitialBinding() && BindingManagerImpl.this.mModerateBindingTillBackgrounded) {
                addConnectionToModerateBindingPool(this.mConnection);
            }
        }

        void setBoundForBackgroundPeriod(boolean nextBound) {
            if (!this.mBoundForBackgroundPeriod && nextBound) {
                addStrongBinding();
            } else if (this.mBoundForBackgroundPeriod && !nextBound) {
                removeStrongBinding(false);
            }
            this.mBoundForBackgroundPeriod = nextBound;
        }

        boolean isOomProtected() {
            return this.mConnection != null ? this.mConnection.isOomProtectedOrWasWhenDied() : this.mWasOomProtected;
        }

        void clearConnection() {
            this.mWasOomProtected = this.mConnection.isOomProtectedOrWasWhenDied();
            synchronized (BindingManagerImpl.this.mModerateBindingPoolLock) {
                ModerateBindingPool moderateBindingPool = BindingManagerImpl.this.mModerateBindingPool;
            }
            if (moderateBindingPool != null) {
                moderateBindingPool.removeConnection(this);
            }
            this.mConnection = null;
        }

        @VisibleForTesting
        boolean isConnectionCleared() {
            return this.mConnection == null;
        }
    }

    private static class ModerateBindingPool extends LruCache<Integer, ManagedConnection> implements ComponentCallbacks2 {
        private Runnable mDelayedClearer;
        private final Object mDelayedClearerLock = new Object();
        private final Handler mHandler = new Handler(ThreadUtils.getUiThreadLooper());

        public ModerateBindingPool(int maxSize) {
            super(maxSize);
        }

        public void onTrimMemory(int level) {
            Log.m29i(BindingManagerImpl.TAG, "onTrimMemory: level=%d, size=%d", Integer.valueOf(level), Integer.valueOf(size()));
            if (size() <= 0) {
                return;
            }
            if (level <= 5) {
                reduce(BindingManagerImpl.MODERATE_BINDING_LOW_REDUCE_RATIO);
            } else if (level <= 10) {
                reduce(BindingManagerImpl.MODERATE_BINDING_HIGH_REDUCE_RATIO);
            } else if (level != 20) {
                evictAll();
            }
        }

        public void onLowMemory() {
            Log.m29i(BindingManagerImpl.TAG, "onLowMemory: evict %d bindings", Integer.valueOf(size()));
            evictAll();
        }

        public void onConfigurationChanged(Configuration configuration) {
        }

        @TargetApi(17)
        private void reduce(float reduceRatio) {
            int oldSize = size();
            int newSize = (int) (((float) oldSize) * (1.0f - reduceRatio));
            Log.m29i(BindingManagerImpl.TAG, "Reduce connections from %d to %d", Integer.valueOf(oldSize), Integer.valueOf(newSize));
            if (newSize == 0) {
                evictAll();
            } else if (VERSION.SDK_INT >= 17) {
                trimToSize(newSize);
            } else {
                int count = 0;
                for (Entry<Integer, ManagedConnection> entry : snapshot().entrySet()) {
                    remove(entry.getKey());
                    count++;
                    if (count == oldSize - newSize) {
                        return;
                    }
                }
            }
        }

        void addConnection(ManagedConnection managedConnection) {
            ChildProcessConnection connection = managedConnection.mConnection;
            if (connection != null && connection.isInSandbox()) {
                managedConnection.addModerateBinding();
                if (connection.isModerateBindingBound()) {
                    put(Integer.valueOf(connection.getServiceNumber()), managedConnection);
                } else {
                    remove(Integer.valueOf(connection.getServiceNumber()));
                }
            }
        }

        void removeConnection(ManagedConnection managedConnection) {
            ChildProcessConnection connection = managedConnection.mConnection;
            if (connection != null && connection.isInSandbox()) {
                remove(Integer.valueOf(connection.getServiceNumber()));
            }
        }

        protected void entryRemoved(boolean evicted, Integer key, ManagedConnection oldValue, ManagedConnection newValue) {
            if (oldValue != newValue) {
                oldValue.removeModerateBinding();
            }
        }

        void onSentToBackground(final boolean onTesting) {
            if (size() != 0) {
                synchronized (this.mDelayedClearerLock) {
                    this.mDelayedClearer = new Runnable() {
                        /* JADX WARNING: inconsistent code. */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public void run() {
                            /*
                            r5 = this;
                            r0 = org.chromium.content.browser.BindingManagerImpl.ModerateBindingPool.this;
                            r1 = r0.mDelayedClearerLock;
                            monitor-enter(r1);
                            r0 = org.chromium.content.browser.BindingManagerImpl.ModerateBindingPool.this;	 Catch:{ all -> 0x0044 }
                            r0 = r0.mDelayedClearer;	 Catch:{ all -> 0x0044 }
                            if (r0 != 0) goto L_0x0011;
                        L_0x000f:
                            monitor-exit(r1);	 Catch:{ all -> 0x0044 }
                        L_0x0010:
                            return;
                        L_0x0011:
                            r0 = org.chromium.content.browser.BindingManagerImpl.ModerateBindingPool.this;	 Catch:{ all -> 0x0044 }
                            r2 = 0;
                            r0.mDelayedClearer = r2;	 Catch:{ all -> 0x0044 }
                            monitor-exit(r1);	 Catch:{ all -> 0x0044 }
                            r0 = "cr.BindingManager";
                            r1 = "Release moderate connections: %d";
                            r2 = 1;
                            r2 = new java.lang.Object[r2];
                            r3 = 0;
                            r4 = org.chromium.content.browser.BindingManagerImpl.ModerateBindingPool.this;
                            r4 = r4.size();
                            r4 = java.lang.Integer.valueOf(r4);
                            r2[r3] = r4;
                            org.chromium.base.Log.m29i(r0, r1, r2);
                            r0 = r7;
                            if (r0 != 0) goto L_0x003e;
                        L_0x0033:
                            r0 = "Android.ModerateBindingCount";
                            r1 = org.chromium.content.browser.BindingManagerImpl.ModerateBindingPool.this;
                            r1 = r1.size();
                            org.chromium.base.metrics.RecordHistogram.recordCountHistogram(r0, r1);
                        L_0x003e:
                            r0 = org.chromium.content.browser.BindingManagerImpl.ModerateBindingPool.this;
                            r0.evictAll();
                            goto L_0x0010;
                        L_0x0044:
                            r0 = move-exception;
                            monitor-exit(r1);	 Catch:{ all -> 0x0044 }
                            throw r0;
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.browser.BindingManagerImpl.ModerateBindingPool.1.run():void");
                        }
                    };
                    this.mHandler.postDelayed(this.mDelayedClearer, BindingManagerImpl.MODERATE_BINDING_POOL_CLEARER_DELAY_MILLIS);
                }
            }
        }

        void onBroughtToForeground() {
            synchronized (this.mDelayedClearerLock) {
                if (this.mDelayedClearer == null) {
                    return;
                }
                this.mHandler.removeCallbacks(this.mDelayedClearer);
                this.mDelayedClearer = null;
            }
        }
    }

    private BindingManagerImpl(boolean isLowMemoryDevice, boolean onTesting) {
        this.mIsLowMemoryDevice = isLowMemoryDevice;
        this.mOnTesting = onTesting;
    }

    public static BindingManagerImpl createBindingManager() {
        return new BindingManagerImpl(SysUtils.isLowEndDevice(), false);
    }

    public static BindingManagerImpl createBindingManagerForTesting(boolean isLowEndDevice) {
        return new BindingManagerImpl(isLowEndDevice, true);
    }

    public void addNewConnection(int pid, ChildProcessConnection connection) {
        synchronized (this.mManagedConnections) {
            this.mManagedConnections.put(pid, new ManagedConnection(connection));
        }
    }

    public void setInForeground(int pid, boolean inForeground) {
        synchronized (this.mManagedConnections) {
            ManagedConnection managedConnection = (ManagedConnection) this.mManagedConnections.get(pid);
        }
        if (managedConnection == null) {
            Log.m38w(TAG, "Cannot setInForeground() - never saw a connection for the pid: %d", Integer.valueOf(pid));
            return;
        }
        synchronized (this.mLastInForegroundLock) {
            if (inForeground) {
                if (!(!this.mIsLowMemoryDevice || this.mLastInForeground == null || this.mLastInForeground == managedConnection)) {
                    this.mLastInForeground.dropBindings();
                }
            }
            managedConnection.setInForeground(inForeground);
            if (inForeground) {
                this.mLastInForeground = managedConnection;
            }
        }
    }

    public void determinedVisibility(int pid) {
        synchronized (this.mManagedConnections) {
            ManagedConnection managedConnection = (ManagedConnection) this.mManagedConnections.get(pid);
        }
        if (managedConnection == null) {
            Log.m38w(TAG, "Cannot call determinedVisibility() - never saw a connection for the pid: %d", Integer.valueOf(pid));
            return;
        }
        managedConnection.determinedVisibility();
    }

    public void onSentToBackground() {
        if ($assertionsDisabled || this.mBoundForBackgroundPeriod == null) {
            ModerateBindingPool moderateBindingPool;
            synchronized (this.mLastInForegroundLock) {
                if (this.mLastInForeground != null) {
                    this.mLastInForeground.setBoundForBackgroundPeriod(true);
                    this.mBoundForBackgroundPeriod = this.mLastInForeground;
                }
            }
            synchronized (this.mModerateBindingPoolLock) {
                moderateBindingPool = this.mModerateBindingPool;
            }
            if (moderateBindingPool != null) {
                moderateBindingPool.onSentToBackground(this.mOnTesting);
                return;
            }
            return;
        }
        throw new AssertionError();
    }

    public void onBroughtToForeground() {
        if (this.mBoundForBackgroundPeriod != null) {
            this.mBoundForBackgroundPeriod.setBoundForBackgroundPeriod(false);
            this.mBoundForBackgroundPeriod = null;
        }
        synchronized (this.mModerateBindingPoolLock) {
            ModerateBindingPool moderateBindingPool = this.mModerateBindingPool;
        }
        if (moderateBindingPool != null) {
            moderateBindingPool.onBroughtToForeground();
        }
    }

    public boolean isOomProtected(int pid) {
        ManagedConnection managedConnection;
        synchronized (this.mManagedConnections) {
            managedConnection = (ManagedConnection) this.mManagedConnections.get(pid);
        }
        return managedConnection != null ? managedConnection.isOomProtected() : false;
    }

    public void clearConnection(int pid) {
        synchronized (this.mManagedConnections) {
            ManagedConnection managedConnection = (ManagedConnection) this.mManagedConnections.get(pid);
        }
        if (managedConnection != null) {
            managedConnection.clearConnection();
        }
    }

    @VisibleForTesting
    public boolean isConnectionCleared(int pid) {
        boolean isConnectionCleared;
        synchronized (this.mManagedConnections) {
            isConnectionCleared = ((ManagedConnection) this.mManagedConnections.get(pid)).isConnectionCleared();
        }
        return isConnectionCleared;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startModerateBindingManagement(android.content.Context r7, int r8, boolean r9) {
        /*
        r6 = this;
        r1 = r6.mModerateBindingPoolLock;
        monitor-enter(r1);
        r0 = r6.mIsLowMemoryDevice;	 Catch:{ all -> 0x0030 }
        if (r0 != 0) goto L_0x000b;
    L_0x0007:
        r0 = r6.mModerateBindingPool;	 Catch:{ all -> 0x0030 }
        if (r0 == 0) goto L_0x000d;
    L_0x000b:
        monitor-exit(r1);	 Catch:{ all -> 0x0030 }
    L_0x000c:
        return;
    L_0x000d:
        r6.mModerateBindingTillBackgrounded = r9;	 Catch:{ all -> 0x0030 }
        r0 = "cr.BindingManager";
        r2 = "Moderate binding enabled: maxSize=%d";
        r3 = 1;
        r3 = new java.lang.Object[r3];	 Catch:{ all -> 0x0030 }
        r4 = 0;
        r5 = java.lang.Integer.valueOf(r8);	 Catch:{ all -> 0x0030 }
        r3[r4] = r5;	 Catch:{ all -> 0x0030 }
        org.chromium.base.Log.m29i(r0, r2, r3);	 Catch:{ all -> 0x0030 }
        r0 = new org.chromium.content.browser.BindingManagerImpl$ModerateBindingPool;	 Catch:{ all -> 0x0030 }
        r0.<init>(r8);	 Catch:{ all -> 0x0030 }
        r6.mModerateBindingPool = r0;	 Catch:{ all -> 0x0030 }
        if (r7 == 0) goto L_0x002e;
    L_0x0029:
        r0 = r6.mModerateBindingPool;	 Catch:{ all -> 0x0030 }
        r7.registerComponentCallbacks(r0);	 Catch:{ all -> 0x0030 }
    L_0x002e:
        monitor-exit(r1);	 Catch:{ all -> 0x0030 }
        goto L_0x000c;
    L_0x0030:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0030 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.browser.BindingManagerImpl.startModerateBindingManagement(android.content.Context, int, boolean):void");
    }

    public void releaseAllModerateBindings() {
        synchronized (this.mModerateBindingPoolLock) {
            ModerateBindingPool moderateBindingPool = this.mModerateBindingPool;
        }
        if (moderateBindingPool != null) {
            Log.m29i(TAG, "Release all moderate bindings: %d", Integer.valueOf(moderateBindingPool.size()));
            moderateBindingPool.evictAll();
        }
    }
}
