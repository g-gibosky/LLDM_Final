package org.chromium.net;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.IOException;
import java.util.Arrays;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.metrics.RecordHistogram;

public class NetworkChangeNotifierAutoDetect extends BroadcastReceiver {
    private static final String TAG = "NetworkChangeNotifierAutoDetect";
    private static final int UNKNOWN_LINK_SPEED = -1;
    private int mConnectionType;
    private ConnectivityManagerDelegate mConnectivityManagerDelegate;
    private final Context mContext;
    private final NetworkConnectivityIntentFilter mIntentFilter;
    private int mMaxBandwidthConnectionType;
    private double mMaxBandwidthMbps;
    private final MyNetworkCallback mNetworkCallback;
    private final NetworkRequest mNetworkRequest;
    private final Observer mObserver;
    private boolean mRegistered;
    private final RegistrationPolicy mRegistrationPolicy;
    private WifiManagerDelegate mWifiManagerDelegate;
    private String mWifiSSID;

    static class ConnectivityManagerDelegate {
        static final /* synthetic */ boolean $assertionsDisabled = (!NetworkChangeNotifierAutoDetect.class.desiredAssertionStatus());
        private final ConnectivityManager mConnectivityManager;

        ConnectivityManagerDelegate(Context context) {
            this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        }

        ConnectivityManagerDelegate() {
            this.mConnectivityManager = null;
        }

        NetworkState getNetworkState() {
            return getNetworkState(this.mConnectivityManager.getActiveNetworkInfo());
        }

        private NetworkInfo getNetworkInfo(Network network) {
            NetworkInfo networkInfo;
            try {
                networkInfo = this.mConnectivityManager.getNetworkInfo(network);
                RecordHistogram.recordBooleanHistogram("NCN.getNetInfo1stSuccess", true);
                return networkInfo;
            } catch (NullPointerException e) {
                RecordHistogram.recordBooleanHistogram("NCN.getNetInfo1stSuccess", false);
                try {
                    networkInfo = this.mConnectivityManager.getNetworkInfo(network);
                    RecordHistogram.recordBooleanHistogram("NCN.getNetInfo2ndSuccess", true);
                    return networkInfo;
                } catch (NullPointerException secondException) {
                    RecordHistogram.recordBooleanHistogram("NCN.getNetInfo2ndSuccess", false);
                    throw secondException;
                }
            }
        }

        @TargetApi(21)
        NetworkState getNetworkState(Network network) {
            NetworkInfo networkInfo = getNetworkInfo(network);
            if (networkInfo == null || networkInfo.getType() != 17) {
                return getNetworkState(networkInfo);
            }
            return getNetworkState();
        }

        NetworkState getNetworkState(NetworkInfo networkInfo) {
            if (networkInfo == null || !networkInfo.isConnected()) {
                return new NetworkState(false, -1, -1);
            }
            return new NetworkState(true, networkInfo.getType(), networkInfo.getSubtype());
        }

        @TargetApi(21)
        @VisibleForTesting
        protected Network[] getAllNetworksUnfiltered() {
            return this.mConnectivityManager.getAllNetworks();
        }

        @TargetApi(21)
        @VisibleForTesting
        protected boolean vpnAccessible(Network network) {
            try {
                network.getSocketFactory().createSocket().close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @TargetApi(21)
        @VisibleForTesting
        protected NetworkCapabilities getNetworkCapabilities(Network network) {
            return this.mConnectivityManager.getNetworkCapabilities(network);
        }

        @TargetApi(21)
        void registerNetworkCallback(NetworkRequest networkRequest, NetworkCallback networkCallback) {
            this.mConnectivityManager.registerNetworkCallback(networkRequest, networkCallback);
        }

        @TargetApi(21)
        void unregisterNetworkCallback(NetworkCallback networkCallback) {
            this.mConnectivityManager.unregisterNetworkCallback(networkCallback);
        }

        @TargetApi(21)
        int getDefaultNetId() {
            NetworkInfo defaultNetworkInfo = this.mConnectivityManager.getActiveNetworkInfo();
            if (defaultNetworkInfo == null) {
                return -1;
            }
            int defaultNetId = -1;
            for (Network network : NetworkChangeNotifierAutoDetect.getAllNetworksFiltered(this, null)) {
                NetworkInfo networkInfo = getNetworkInfo(network);
                if (networkInfo != null && (networkInfo.getType() == defaultNetworkInfo.getType() || networkInfo.getType() == 17)) {
                    if ($assertionsDisabled || defaultNetId == -1) {
                        defaultNetId = NetworkChangeNotifierAutoDetect.networkToNetId(network);
                    } else {
                        throw new AssertionError();
                    }
                }
            }
            return defaultNetId;
        }
    }

    @TargetApi(21)
    private class MyNetworkCallback extends NetworkCallback {
        static final /* synthetic */ boolean $assertionsDisabled = (!NetworkChangeNotifierAutoDetect.class.desiredAssertionStatus());
        private Network mVpnInPlace;

        private MyNetworkCallback() {
            this.mVpnInPlace = null;
        }

        void initializeVpnInPlace() {
            Network[] networks = NetworkChangeNotifierAutoDetect.getAllNetworksFiltered(NetworkChangeNotifierAutoDetect.this.mConnectivityManagerDelegate, null);
            this.mVpnInPlace = null;
            if (networks.length == 1) {
                NetworkCapabilities capabilities = NetworkChangeNotifierAutoDetect.this.mConnectivityManagerDelegate.getNetworkCapabilities(networks[0]);
                if (capabilities != null && capabilities.hasTransport(4)) {
                    this.mVpnInPlace = networks[0];
                }
            }
        }

        private boolean ignoreNetworkDueToVpn(Network network) {
            return (this.mVpnInPlace == null || this.mVpnInPlace.equals(network)) ? false : true;
        }

        private boolean ignoreConnectedInaccessibleVpn(Network network, NetworkCapabilities capabilities) {
            if (capabilities == null) {
                capabilities = NetworkChangeNotifierAutoDetect.this.mConnectivityManagerDelegate.getNetworkCapabilities(network);
            }
            return capabilities == null || (capabilities.hasTransport(4) && !NetworkChangeNotifierAutoDetect.this.mConnectivityManagerDelegate.vpnAccessible(network));
        }

        private boolean ignoreConnectedNetwork(Network network, NetworkCapabilities capabilities) {
            return ignoreNetworkDueToVpn(network) || ignoreConnectedInaccessibleVpn(network, capabilities);
        }

        public void onAvailable(Network network) {
            NetworkCapabilities capabilities = NetworkChangeNotifierAutoDetect.this.mConnectivityManagerDelegate.getNetworkCapabilities(network);
            if (!ignoreConnectedNetwork(network, capabilities)) {
                final boolean makeVpnDefault = capabilities.hasTransport(4);
                if (makeVpnDefault) {
                    this.mVpnInPlace = network;
                }
                final int netId = NetworkChangeNotifierAutoDetect.networkToNetId(network);
                final int connectionType = NetworkChangeNotifierAutoDetect.this.getCurrentConnectionType(NetworkChangeNotifierAutoDetect.this.mConnectivityManagerDelegate.getNetworkState(network));
                ThreadUtils.postOnUiThread(new Runnable() {
                    public void run() {
                        NetworkChangeNotifierAutoDetect.this.mObserver.onNetworkConnect(netId, connectionType);
                        if (makeVpnDefault) {
                            NetworkChangeNotifierAutoDetect.this.mObserver.onConnectionTypeChanged(connectionType);
                            NetworkChangeNotifierAutoDetect.this.mObserver.purgeActiveNetworkList(new int[]{netId});
                        }
                    }
                });
            }
        }

        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            if (!ignoreConnectedNetwork(network, networkCapabilities)) {
                final int netId = NetworkChangeNotifierAutoDetect.networkToNetId(network);
                final int connectionType = NetworkChangeNotifierAutoDetect.this.getCurrentConnectionType(NetworkChangeNotifierAutoDetect.this.mConnectivityManagerDelegate.getNetworkState(network));
                ThreadUtils.postOnUiThread(new Runnable() {
                    public void run() {
                        NetworkChangeNotifierAutoDetect.this.mObserver.onNetworkConnect(netId, connectionType);
                    }
                });
            }
        }

        public void onLosing(Network network, int maxMsToLive) {
            if (!ignoreConnectedNetwork(network, null)) {
                final int netId = NetworkChangeNotifierAutoDetect.networkToNetId(network);
                ThreadUtils.postOnUiThread(new Runnable() {
                    public void run() {
                        NetworkChangeNotifierAutoDetect.this.mObserver.onNetworkSoonToDisconnect(netId);
                    }
                });
            }
        }

        public void onLost(final Network network) {
            if (!ignoreNetworkDueToVpn(network)) {
                ThreadUtils.postOnUiThread(new Runnable() {
                    public void run() {
                        NetworkChangeNotifierAutoDetect.this.mObserver.onNetworkDisconnect(NetworkChangeNotifierAutoDetect.networkToNetId(network));
                    }
                });
                if (this.mVpnInPlace == null) {
                    return;
                }
                if ($assertionsDisabled || network.equals(this.mVpnInPlace)) {
                    this.mVpnInPlace = null;
                    for (Network newNetwork : NetworkChangeNotifierAutoDetect.getAllNetworksFiltered(NetworkChangeNotifierAutoDetect.this.mConnectivityManagerDelegate, network)) {
                        onAvailable(newNetwork);
                    }
                    final int newConnectionType = NetworkChangeNotifierAutoDetect.this.getCurrentConnectionType(NetworkChangeNotifierAutoDetect.this.mConnectivityManagerDelegate.getNetworkState());
                    ThreadUtils.postOnUiThread(new Runnable() {
                        public void run() {
                            NetworkChangeNotifierAutoDetect.this.mObserver.onConnectionTypeChanged(newConnectionType);
                        }
                    });
                    return;
                }
                throw new AssertionError();
            }
        }
    }

    private static class NetworkConnectivityIntentFilter extends IntentFilter {
        NetworkConnectivityIntentFilter(boolean monitorRSSI) {
            addAction("android.net.conn.CONNECTIVITY_CHANGE");
            if (monitorRSSI) {
                addAction("android.net.wifi.RSSI_CHANGED");
            }
        }
    }

    static class NetworkState {
        private final boolean mConnected;
        private final int mSubtype;
        private final int mType;

        public NetworkState(boolean connected, int type, int subtype) {
            this.mConnected = connected;
            this.mType = type;
            this.mSubtype = subtype;
        }

        public boolean isConnected() {
            return this.mConnected;
        }

        public int getNetworkType() {
            return this.mType;
        }

        public int getNetworkSubType() {
            return this.mSubtype;
        }
    }

    public interface Observer {
        void onConnectionTypeChanged(int i);

        void onMaxBandwidthChanged(double d);

        void onNetworkConnect(int i, int i2);

        void onNetworkDisconnect(int i);

        void onNetworkSoonToDisconnect(int i);

        void purgeActiveNetworkList(int[] iArr);
    }

    public static abstract class RegistrationPolicy {
        static final /* synthetic */ boolean $assertionsDisabled = (!NetworkChangeNotifierAutoDetect.class.desiredAssertionStatus());
        private NetworkChangeNotifierAutoDetect mNotifier;

        protected abstract void destroy();

        protected final void register() {
            if ($assertionsDisabled || this.mNotifier != null) {
                this.mNotifier.register();
                return;
            }
            throw new AssertionError();
        }

        protected final void unregister() {
            if ($assertionsDisabled || this.mNotifier != null) {
                this.mNotifier.unregister();
                return;
            }
            throw new AssertionError();
        }

        protected void init(NetworkChangeNotifierAutoDetect notifier) {
            this.mNotifier = notifier;
        }
    }

    static class WifiManagerDelegate {
        private final Context mContext;
        private final boolean mHasWifiPermission;
        private final WifiManager mWifiManager;

        WifiManagerDelegate(Context context) {
            this.mContext = context;
            this.mHasWifiPermission = this.mContext.getPackageManager().checkPermission("android.permission.ACCESS_WIFI_STATE", this.mContext.getPackageName()) == 0;
            this.mWifiManager = this.mHasWifiPermission ? (WifiManager) this.mContext.getSystemService("wifi") : null;
        }

        WifiManagerDelegate() {
            this.mContext = null;
            this.mWifiManager = null;
            this.mHasWifiPermission = false;
        }

        String getWifiSSID() {
            Intent intent = this.mContext.registerReceiver(null, new IntentFilter("android.net.wifi.STATE_CHANGE"));
            if (intent != null) {
                WifiInfo wifiInfo = (WifiInfo) intent.getParcelableExtra("wifiInfo");
                if (wifiInfo != null) {
                    String ssid = wifiInfo.getSSID();
                    if (ssid != null) {
                        return ssid;
                    }
                }
            }
            return "";
        }

        private WifiInfo getWifiInfo() {
            WifiInfo wifiInfo;
            try {
                wifiInfo = this.mWifiManager.getConnectionInfo();
                RecordHistogram.recordBooleanHistogram("NCN.getWifiInfo1stSuccess", true);
                return wifiInfo;
            } catch (NullPointerException e) {
                RecordHistogram.recordBooleanHistogram("NCN.getWifiInfo1stSuccess", false);
                try {
                    wifiInfo = this.mWifiManager.getConnectionInfo();
                    RecordHistogram.recordBooleanHistogram("NCN.getWifiInfo2ndSuccess", true);
                    return wifiInfo;
                } catch (NullPointerException secondException) {
                    RecordHistogram.recordBooleanHistogram("NCN.getWifiInfo2ndSuccess", false);
                    throw secondException;
                }
            }
        }

        int getLinkSpeedInMbps() {
            if (!this.mHasWifiPermission || this.mWifiManager == null) {
                return -1;
            }
            WifiInfo wifiInfo = getWifiInfo();
            if (wifiInfo != null) {
                return wifiInfo.getLinkSpeed();
            }
            return -1;
        }

        boolean getHasWifiPermission() {
            return this.mHasWifiPermission;
        }
    }

    @TargetApi(21)
    public NetworkChangeNotifierAutoDetect(Observer observer, Context context, RegistrationPolicy policy) {
        ThreadUtils.assertOnUiThread();
        this.mObserver = observer;
        this.mContext = context.getApplicationContext();
        this.mConnectivityManagerDelegate = new ConnectivityManagerDelegate(context);
        this.mWifiManagerDelegate = new WifiManagerDelegate(context);
        if (VERSION.SDK_INT >= 21) {
            this.mNetworkCallback = new MyNetworkCallback();
            this.mNetworkRequest = new Builder().addCapability(12).removeCapability(15).build();
        } else {
            this.mNetworkCallback = null;
            this.mNetworkRequest = null;
        }
        NetworkState networkState = this.mConnectivityManagerDelegate.getNetworkState();
        this.mConnectionType = getCurrentConnectionType(networkState);
        this.mWifiSSID = getCurrentWifiSSID(networkState);
        this.mMaxBandwidthMbps = getCurrentMaxBandwidthInMbps(networkState);
        this.mMaxBandwidthConnectionType = this.mConnectionType;
        this.mIntentFilter = new NetworkConnectivityIntentFilter(this.mWifiManagerDelegate.getHasWifiPermission());
        this.mRegistrationPolicy = policy;
        this.mRegistrationPolicy.init(this);
    }

    void setConnectivityManagerDelegateForTests(ConnectivityManagerDelegate delegate) {
        this.mConnectivityManagerDelegate = delegate;
    }

    void setWifiManagerDelegateForTests(WifiManagerDelegate delegate) {
        this.mWifiManagerDelegate = delegate;
    }

    @VisibleForTesting
    RegistrationPolicy getRegistrationPolicy() {
        return this.mRegistrationPolicy;
    }

    @VisibleForTesting
    boolean isReceiverRegisteredForTesting() {
        return this.mRegistered;
    }

    public void destroy() {
        this.mRegistrationPolicy.destroy();
        unregister();
    }

    public void register() {
        if (!this.mRegistered) {
            NetworkState networkState = getCurrentNetworkState();
            connectionTypeChanged(networkState);
            maxBandwidthChanged(networkState);
            this.mContext.registerReceiver(this, this.mIntentFilter);
            this.mRegistered = true;
            if (this.mNetworkCallback != null) {
                this.mNetworkCallback.initializeVpnInPlace();
                this.mConnectivityManagerDelegate.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback);
                Network[] networks = getAllNetworksFiltered(this.mConnectivityManagerDelegate, null);
                int[] netIds = new int[networks.length];
                for (int i = 0; i < networks.length; i++) {
                    netIds[i] = networkToNetId(networks[i]);
                }
                this.mObserver.purgeActiveNetworkList(netIds);
            }
        }
    }

    public void unregister() {
        if (this.mRegistered) {
            this.mContext.unregisterReceiver(this);
            this.mRegistered = false;
            if (this.mNetworkCallback != null) {
                this.mConnectivityManagerDelegate.unregisterNetworkCallback(this.mNetworkCallback);
            }
        }
    }

    public NetworkState getCurrentNetworkState() {
        return this.mConnectivityManagerDelegate.getNetworkState();
    }

    @TargetApi(21)
    private static Network[] getAllNetworksFiltered(ConnectivityManagerDelegate connectivityManagerDelegate, Network ignoreNetwork) {
        Network[] networks = connectivityManagerDelegate.getAllNetworksUnfiltered();
        Network[] arr$ = networks;
        int len$ = arr$.length;
        int i$ = 0;
        int filteredIndex = 0;
        while (i$ < len$) {
            int filteredIndex2;
            Network network = arr$[i$];
            if (network.equals(ignoreNetwork)) {
                filteredIndex2 = filteredIndex;
            } else {
                NetworkCapabilities capabilities = connectivityManagerDelegate.getNetworkCapabilities(network);
                if (capabilities != null) {
                    if (!capabilities.hasCapability(12)) {
                        filteredIndex2 = filteredIndex;
                    } else if (!capabilities.hasTransport(4)) {
                        filteredIndex2 = filteredIndex + 1;
                        networks[filteredIndex] = network;
                    } else if (connectivityManagerDelegate.vpnAccessible(network)) {
                        return new Network[]{network};
                    }
                }
                filteredIndex2 = filteredIndex;
            }
            i$++;
            filteredIndex = filteredIndex2;
        }
        return (Network[]) Arrays.copyOf(networks, filteredIndex);
    }

    public int[] getNetworksAndTypes() {
        if (VERSION.SDK_INT < 21) {
            return new int[0];
        }
        Network[] networks = getAllNetworksFiltered(this.mConnectivityManagerDelegate, null);
        int[] networksAndTypes = new int[(networks.length * 2)];
        int index = 0;
        for (Network network : networks) {
            int i = index + 1;
            networksAndTypes[index] = networkToNetId(network);
            index = i + 1;
            networksAndTypes[i] = getCurrentConnectionType(this.mConnectivityManagerDelegate.getNetworkState(network));
        }
        return networksAndTypes;
    }

    public int getDefaultNetId() {
        if (VERSION.SDK_INT < 21) {
            return -1;
        }
        return this.mConnectivityManagerDelegate.getDefaultNetId();
    }

    public int getCurrentConnectionType(NetworkState networkState) {
        if (!networkState.isConnected()) {
            return 6;
        }
        switch (networkState.getNetworkType()) {
            case 0:
                switch (networkState.getNetworkSubType()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return 3;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                        return 4;
                    case 13:
                        return 5;
                    default:
                        return 0;
                }
            case 1:
                return 2;
            case 6:
                return 5;
            case 7:
                return 7;
            case 9:
                return 1;
            default:
                return 0;
        }
    }

    public int getCurrentConnectionSubtype(NetworkState networkState) {
        if (!networkState.isConnected()) {
            return 1;
        }
        switch (networkState.getNetworkType()) {
            case 0:
                switch (networkState.getNetworkSubType()) {
                    case 1:
                        return 7;
                    case 2:
                        return 8;
                    case 3:
                        return 9;
                    case 4:
                        return 5;
                    case 5:
                        return 10;
                    case 6:
                        return 11;
                    case 7:
                        return 6;
                    case 8:
                        return 14;
                    case 9:
                        return 15;
                    case 10:
                        return 12;
                    case 11:
                        return 4;
                    case 12:
                        return 13;
                    case 13:
                        return 18;
                    case 14:
                        return 16;
                    case 15:
                        return 17;
                    default:
                        return 0;
                }
            case 1:
            case 6:
            case 7:
            case 9:
                return 0;
            default:
                return 0;
        }
    }

    public double getCurrentMaxBandwidthInMbps(NetworkState networkState) {
        if (getCurrentConnectionType(networkState) == 2) {
            int link_speed = this.mWifiManagerDelegate.getLinkSpeedInMbps();
            if (link_speed != -1) {
                return (double) link_speed;
            }
        }
        return NetworkChangeNotifier.getMaxBandwidthForConnectionSubtype(getCurrentConnectionSubtype(networkState));
    }

    private String getCurrentWifiSSID(NetworkState networkState) {
        if (getCurrentConnectionType(networkState) != 2) {
            return "";
        }
        return this.mWifiManagerDelegate.getWifiSSID();
    }

    public void onReceive(Context context, Intent intent) {
        NetworkState networkState = getCurrentNetworkState();
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            connectionTypeChanged(networkState);
            maxBandwidthChanged(networkState);
        } else if ("android.net.wifi.RSSI_CHANGED".equals(intent.getAction())) {
            maxBandwidthChanged(networkState);
        }
    }

    private void connectionTypeChanged(NetworkState networkState) {
        int newConnectionType = getCurrentConnectionType(networkState);
        String newWifiSSID = getCurrentWifiSSID(networkState);
        if (newConnectionType != this.mConnectionType || !newWifiSSID.equals(this.mWifiSSID)) {
            this.mConnectionType = newConnectionType;
            this.mWifiSSID = newWifiSSID;
            Log.d(TAG, "Network connectivity changed, type is: " + this.mConnectionType);
            this.mObserver.onConnectionTypeChanged(newConnectionType);
        }
    }

    private void maxBandwidthChanged(NetworkState networkState) {
        double newMaxBandwidthMbps = getCurrentMaxBandwidthInMbps(networkState);
        if (newMaxBandwidthMbps != this.mMaxBandwidthMbps || this.mConnectionType != this.mMaxBandwidthConnectionType) {
            this.mMaxBandwidthMbps = newMaxBandwidthMbps;
            this.mMaxBandwidthConnectionType = this.mConnectionType;
            this.mObserver.onMaxBandwidthChanged(newMaxBandwidthMbps);
        }
    }

    @TargetApi(21)
    @VisibleForTesting
    static int networkToNetId(Network network) {
        return Integer.parseInt(network.toString());
    }
}
