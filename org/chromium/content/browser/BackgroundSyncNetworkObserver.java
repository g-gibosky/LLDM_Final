package org.chromium.content.browser;

import android.content.Context;
import android.os.Process;
import java.util.ArrayList;
import java.util.List;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.NativeClassQualifiedName;
import org.chromium.base.metrics.RecordHistogram;
import org.chromium.net.NetworkChangeNotifierAutoDetect;
import org.chromium.net.NetworkChangeNotifierAutoDetect.Observer;
import org.chromium.net.RegistrationPolicyAlwaysRegister;

@JNINamespace("content")
class BackgroundSyncNetworkObserver implements Observer {
    private static final String TAG = "cr_BgSyncNetObserver";
    private static BackgroundSyncNetworkObserver sInstance = null;
    private Context mContext;
    private List<Long> mNativePtrs = new ArrayList();
    private NetworkChangeNotifierAutoDetect mNotifier;

    @NativeClassQualifiedName("BackgroundSyncNetworkObserverAndroid::Observer")
    private native void nativeNotifyConnectionTypeChanged(long j, int i);

    private BackgroundSyncNetworkObserver(Context ctx) {
        ThreadUtils.assertOnUiThread();
        this.mContext = ctx;
    }

    private static boolean canCreateObserver(Context ctx) {
        return ApiCompatibilityUtils.checkPermission(ctx, "android.permission.ACCESS_NETWORK_STATE", Process.myPid(), Process.myUid()) == 0;
    }

    @CalledByNative
    private static BackgroundSyncNetworkObserver createObserver(Context ctx, long nativePtr) {
        ThreadUtils.assertOnUiThread();
        if (sInstance == null) {
            sInstance = new BackgroundSyncNetworkObserver(ctx);
        }
        sInstance.registerObserver(nativePtr);
        return sInstance;
    }

    private void registerObserver(long nativePtr) {
        ThreadUtils.assertOnUiThread();
        if (canCreateObserver(this.mContext)) {
            if (this.mNotifier == null) {
                this.mNotifier = new NetworkChangeNotifierAutoDetect(this, this.mContext, new RegistrationPolicyAlwaysRegister());
                RecordHistogram.recordBooleanHistogram("BackgroundSync.NetworkObserver.HasPermission", true);
            }
            this.mNativePtrs.add(Long.valueOf(nativePtr));
            nativeNotifyConnectionTypeChanged(nativePtr, this.mNotifier.getCurrentConnectionType(this.mNotifier.getCurrentNetworkState()));
            return;
        }
        RecordHistogram.recordBooleanHistogram("BackgroundSync.NetworkObserver.HasPermission", false);
    }

    @CalledByNative
    private void removeObserver(long nativePtr) {
        ThreadUtils.assertOnUiThread();
        this.mNativePtrs.remove(Long.valueOf(nativePtr));
        if (this.mNativePtrs.size() == 0 && this.mNotifier != null) {
            this.mNotifier.destroy();
            this.mNotifier = null;
        }
    }

    public void onConnectionTypeChanged(int newConnectionType) {
        ThreadUtils.assertOnUiThread();
        for (Long nativePtr : this.mNativePtrs) {
            nativeNotifyConnectionTypeChanged(nativePtr.longValue(), newConnectionType);
        }
    }

    public void onMaxBandwidthChanged(double maxBandwidthMbps) {
    }

    public void onNetworkConnect(int netId, int connectionType) {
    }

    public void onNetworkSoonToDisconnect(int netId) {
    }

    public void onNetworkDisconnect(int netId) {
    }

    public void purgeActiveNetworkList(int[] activeNetIds) {
    }
}
