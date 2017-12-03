package org.chromium.net;

import android.content.Context;
import java.util.ArrayList;
import java.util.Iterator;
import org.chromium.base.ObserverList;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.NativeClassQualifiedName;
import org.chromium.net.NetworkChangeNotifierAutoDetect.Observer;
import org.chromium.net.NetworkChangeNotifierAutoDetect.RegistrationPolicy;

@JNINamespace("net")
public class NetworkChangeNotifier {
    static final /* synthetic */ boolean $assertionsDisabled = (!NetworkChangeNotifier.class.desiredAssertionStatus());
    private static NetworkChangeNotifier sInstance;
    private NetworkChangeNotifierAutoDetect mAutoDetector;
    private final ObserverList<ConnectionTypeObserver> mConnectionTypeObservers;
    private final Context mContext;
    private int mCurrentConnectionType = 0;
    private double mCurrentMaxBandwidth = Double.POSITIVE_INFINITY;
    private int mMaxBandwidthConnectionType = this.mCurrentConnectionType;
    private final ArrayList<Long> mNativeChangeNotifiers;

    public interface ConnectionTypeObserver {
        void onConnectionTypeChanged(int i);
    }

    class C04561 implements Observer {
        C04561() {
        }

        public void onConnectionTypeChanged(int newConnectionType) {
            NetworkChangeNotifier.this.updateCurrentConnectionType(newConnectionType);
        }

        public void onMaxBandwidthChanged(double maxBandwidthMbps) {
            NetworkChangeNotifier.this.updateCurrentMaxBandwidth(maxBandwidthMbps);
        }

        public void onNetworkConnect(int netId, int connectionType) {
            NetworkChangeNotifier.this.notifyObserversOfNetworkConnect(netId, connectionType);
        }

        public void onNetworkSoonToDisconnect(int netId) {
            NetworkChangeNotifier.this.notifyObserversOfNetworkSoonToDisconnect(netId);
        }

        public void onNetworkDisconnect(int netId) {
            NetworkChangeNotifier.this.notifyObserversOfNetworkDisconnect(netId);
        }

        public void purgeActiveNetworkList(int[] activeNetIds) {
            NetworkChangeNotifier.this.notifyObserversToPurgeActiveNetworkList(activeNetIds);
        }
    }

    private static native double nativeGetMaxBandwidthForConnectionSubtype(int i);

    @NativeClassQualifiedName("NetworkChangeNotifierDelegateAndroid")
    private native void nativeNotifyConnectionTypeChanged(long j, int i, int i2);

    @NativeClassQualifiedName("NetworkChangeNotifierDelegateAndroid")
    private native void nativeNotifyMaxBandwidthChanged(long j, double d);

    @NativeClassQualifiedName("NetworkChangeNotifierDelegateAndroid")
    private native void nativeNotifyOfNetworkConnect(long j, int i, int i2);

    @NativeClassQualifiedName("NetworkChangeNotifierDelegateAndroid")
    private native void nativeNotifyOfNetworkDisconnect(long j, int i);

    @NativeClassQualifiedName("NetworkChangeNotifierDelegateAndroid")
    private native void nativeNotifyOfNetworkSoonToDisconnect(long j, int i);

    @NativeClassQualifiedName("NetworkChangeNotifierDelegateAndroid")
    private native void nativeNotifyPurgeActiveNetworkList(long j, int[] iArr);

    @VisibleForTesting
    protected NetworkChangeNotifier(Context context) {
        this.mContext = context.getApplicationContext();
        this.mNativeChangeNotifiers = new ArrayList();
        this.mConnectionTypeObservers = new ObserverList();
    }

    @CalledByNative
    public static NetworkChangeNotifier init(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkChangeNotifier(context);
        }
        return sInstance;
    }

    public static boolean isInitialized() {
        return sInstance != null;
    }

    static void resetInstanceForTests(NetworkChangeNotifier notifier) {
        sInstance = notifier;
    }

    @CalledByNative
    public int getCurrentConnectionType() {
        return this.mCurrentConnectionType;
    }

    @CalledByNative
    public int getCurrentConnectionSubtype() {
        return this.mAutoDetector == null ? 0 : this.mAutoDetector.getCurrentConnectionSubtype(this.mAutoDetector.getCurrentNetworkState());
    }

    @CalledByNative
    public double getCurrentMaxBandwidthInMbps() {
        return this.mCurrentMaxBandwidth;
    }

    @CalledByNative
    public int getCurrentDefaultNetId() {
        return this.mAutoDetector == null ? -1 : this.mAutoDetector.getDefaultNetId();
    }

    @CalledByNative
    public int[] getCurrentNetworksAndTypes() {
        return this.mAutoDetector == null ? new int[0] : this.mAutoDetector.getNetworksAndTypes();
    }

    public static double getMaxBandwidthForConnectionSubtype(int subtype) {
        return nativeGetMaxBandwidthForConnectionSubtype(subtype);
    }

    @CalledByNative
    public void addNativeObserver(long nativeChangeNotifier) {
        this.mNativeChangeNotifiers.add(Long.valueOf(nativeChangeNotifier));
    }

    @CalledByNative
    public void removeNativeObserver(long nativeChangeNotifier) {
        this.mNativeChangeNotifiers.remove(Long.valueOf(nativeChangeNotifier));
    }

    public static NetworkChangeNotifier getInstance() {
        if ($assertionsDisabled || sInstance != null) {
            return sInstance;
        }
        throw new AssertionError();
    }

    public static void setAutoDetectConnectivityState(boolean shouldAutoDetect) {
        getInstance().setAutoDetectConnectivityStateInternal(shouldAutoDetect, new RegistrationPolicyApplicationStatus());
    }

    public static void registerToReceiveNotificationsAlways() {
        getInstance().setAutoDetectConnectivityStateInternal(true, new RegistrationPolicyAlwaysRegister());
    }

    public static void setAutoDetectConnectivityState(RegistrationPolicy policy) {
        getInstance().setAutoDetectConnectivityStateInternal(true, policy);
    }

    private void destroyAutoDetector() {
        if (this.mAutoDetector != null) {
            this.mAutoDetector.destroy();
            this.mAutoDetector = null;
        }
    }

    private void setAutoDetectConnectivityStateInternal(boolean shouldAutoDetect, RegistrationPolicy policy) {
        if (!shouldAutoDetect) {
            destroyAutoDetector();
        } else if (this.mAutoDetector == null) {
            this.mAutoDetector = new NetworkChangeNotifierAutoDetect(new C04561(), this.mContext, policy);
            NetworkState networkState = this.mAutoDetector.getCurrentNetworkState();
            updateCurrentConnectionType(this.mAutoDetector.getCurrentConnectionType(networkState));
            updateCurrentMaxBandwidth(this.mAutoDetector.getCurrentMaxBandwidthInMbps(networkState));
        }
    }

    @CalledByNative
    public static void forceConnectivityState(boolean networkAvailable) {
        setAutoDetectConnectivityState(false);
        getInstance().forceConnectivityStateInternal(networkAvailable);
    }

    private void forceConnectivityStateInternal(boolean forceOnline) {
        boolean connectionCurrentlyExists;
        int i = 0;
        if (this.mCurrentConnectionType != 6) {
            connectionCurrentlyExists = true;
        } else {
            connectionCurrentlyExists = false;
        }
        if (connectionCurrentlyExists != forceOnline) {
            if (!forceOnline) {
                i = 6;
            }
            updateCurrentConnectionType(i);
            updateCurrentMaxBandwidth(forceOnline ? Double.POSITIVE_INFINITY : 0.0d);
        }
    }

    @CalledByNative
    public static void fakeNetworkConnected(int netId, int connectionType) {
        setAutoDetectConnectivityState(false);
        getInstance().notifyObserversOfNetworkConnect(netId, connectionType);
    }

    @CalledByNative
    public static void fakeNetworkSoonToBeDisconnected(int netId) {
        setAutoDetectConnectivityState(false);
        getInstance().notifyObserversOfNetworkSoonToDisconnect(netId);
    }

    @CalledByNative
    public static void fakeNetworkDisconnected(int netId) {
        setAutoDetectConnectivityState(false);
        getInstance().notifyObserversOfNetworkDisconnect(netId);
    }

    @CalledByNative
    public static void fakePurgeActiveNetworkList(int[] activeNetIds) {
        setAutoDetectConnectivityState(false);
        getInstance().notifyObserversToPurgeActiveNetworkList(activeNetIds);
    }

    @CalledByNative
    public static void fakeDefaultNetwork(int netId, int connectionType) {
        setAutoDetectConnectivityState(false);
        getInstance().notifyObserversOfConnectionTypeChange(connectionType, netId);
    }

    @CalledByNative
    public static void fakeMaxBandwidthChanged(double maxBandwidthMbps) {
        setAutoDetectConnectivityState(false);
        getInstance().notifyObserversOfMaxBandwidthChange(maxBandwidthMbps);
    }

    private void updateCurrentConnectionType(int newConnectionType) {
        this.mCurrentConnectionType = newConnectionType;
        notifyObserversOfConnectionTypeChange(newConnectionType);
    }

    private void updateCurrentMaxBandwidth(double maxBandwidthMbps) {
        if (maxBandwidthMbps != this.mCurrentMaxBandwidth || this.mCurrentConnectionType != this.mMaxBandwidthConnectionType) {
            this.mCurrentMaxBandwidth = maxBandwidthMbps;
            this.mMaxBandwidthConnectionType = this.mCurrentConnectionType;
            notifyObserversOfMaxBandwidthChange(maxBandwidthMbps);
        }
    }

    void notifyObserversOfConnectionTypeChange(int newConnectionType) {
        notifyObserversOfConnectionTypeChange(newConnectionType, getCurrentDefaultNetId());
    }

    private void notifyObserversOfConnectionTypeChange(int newConnectionType, int defaultNetId) {
        Iterator i$ = this.mNativeChangeNotifiers.iterator();
        while (i$.hasNext()) {
            nativeNotifyConnectionTypeChanged(((Long) i$.next()).longValue(), newConnectionType, defaultNetId);
        }
        i$ = this.mConnectionTypeObservers.iterator();
        while (i$.hasNext()) {
            ((ConnectionTypeObserver) i$.next()).onConnectionTypeChanged(newConnectionType);
        }
    }

    void notifyObserversOfMaxBandwidthChange(double maxBandwidthMbps) {
        Iterator i$ = this.mNativeChangeNotifiers.iterator();
        while (i$.hasNext()) {
            nativeNotifyMaxBandwidthChanged(((Long) i$.next()).longValue(), maxBandwidthMbps);
        }
    }

    void notifyObserversOfNetworkConnect(int netId, int connectionType) {
        Iterator i$ = this.mNativeChangeNotifiers.iterator();
        while (i$.hasNext()) {
            nativeNotifyOfNetworkConnect(((Long) i$.next()).longValue(), netId, connectionType);
        }
    }

    void notifyObserversOfNetworkSoonToDisconnect(int netId) {
        Iterator i$ = this.mNativeChangeNotifiers.iterator();
        while (i$.hasNext()) {
            nativeNotifyOfNetworkSoonToDisconnect(((Long) i$.next()).longValue(), netId);
        }
    }

    void notifyObserversOfNetworkDisconnect(int netId) {
        Iterator i$ = this.mNativeChangeNotifiers.iterator();
        while (i$.hasNext()) {
            nativeNotifyOfNetworkDisconnect(((Long) i$.next()).longValue(), netId);
        }
    }

    void notifyObserversToPurgeActiveNetworkList(int[] activeNetIds) {
        Iterator i$ = this.mNativeChangeNotifiers.iterator();
        while (i$.hasNext()) {
            nativeNotifyPurgeActiveNetworkList(((Long) i$.next()).longValue(), activeNetIds);
        }
    }

    public static void addConnectionTypeObserver(ConnectionTypeObserver observer) {
        getInstance().addConnectionTypeObserverInternal(observer);
    }

    private void addConnectionTypeObserverInternal(ConnectionTypeObserver observer) {
        this.mConnectionTypeObservers.addObserver(observer);
    }

    public static void removeConnectionTypeObserver(ConnectionTypeObserver observer) {
        getInstance().removeConnectionTypeObserverInternal(observer);
    }

    private void removeConnectionTypeObserverInternal(ConnectionTypeObserver observer) {
        this.mConnectionTypeObservers.removeObserver(observer);
    }

    public static NetworkChangeNotifierAutoDetect getAutoDetectorForTest() {
        return getInstance().mAutoDetector;
    }

    public static boolean isOnline() {
        return getInstance().getCurrentConnectionType() != 6;
    }
}
