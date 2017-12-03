package org.chromium.device.bluetooth;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import java.util.List;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.components.location.LocationUtils;

@TargetApi(21)
@JNINamespace("device")
final class ChromeBluetoothAdapter extends BroadcastReceiver {
    static final /* synthetic */ boolean $assertionsDisabled = (!ChromeBluetoothAdapter.class.desiredAssertionStatus());
    private static final String TAG = "Bluetooth";
    private final BluetoothAdapterWrapper mAdapter;
    private long mNativeBluetoothAdapterAndroid;
    private ScanCallback mScanCallback;

    private class ScanCallback extends ScanCallbackWrapper {
        private ScanCallback() {
        }

        public void onBatchScanResult(List<ScanResultWrapper> list) {
            Log.m30v(ChromeBluetoothAdapter.TAG, "onBatchScanResults");
        }

        public void onScanResult(int callbackType, ScanResultWrapper result) {
            Log.m33v(ChromeBluetoothAdapter.TAG, "onScanResult %d %s %s", Integer.valueOf(callbackType), result.getDevice().getAddress(), result.getDevice().getName());
            ChromeBluetoothAdapter.this.nativeCreateOrUpdateDeviceOnScan(ChromeBluetoothAdapter.this.mNativeBluetoothAdapterAndroid, result.getDevice().getAddress(), result.getDevice(), result.getScanRecord_getServiceUuids());
        }

        public void onScanFailed(int errorCode) {
            Log.m38w(ChromeBluetoothAdapter.TAG, "onScanFailed: %d", Integer.valueOf(errorCode));
            ChromeBluetoothAdapter.this.nativeOnScanFailed(ChromeBluetoothAdapter.this.mNativeBluetoothAdapterAndroid);
        }
    }

    private native void nativeCreateOrUpdateDeviceOnScan(long j, String str, Object obj, List<ParcelUuid> list);

    private native void nativeOnAdapterStateChanged(long j, boolean z);

    private native void nativeOnScanFailed(long j);

    public ChromeBluetoothAdapter(long nativeBluetoothAdapterAndroid, BluetoothAdapterWrapper adapterWrapper) {
        this.mNativeBluetoothAdapterAndroid = nativeBluetoothAdapterAndroid;
        this.mAdapter = adapterWrapper;
        registerBroadcastReceiver();
        if (adapterWrapper == null) {
            Log.m29i(TAG, "ChromeBluetoothAdapter created with no adapterWrapper.", new Object[0]);
        } else {
            Log.m29i(TAG, "ChromeBluetoothAdapter created with provided adapterWrapper.", new Object[0]);
        }
    }

    @CalledByNative
    private void onBluetoothAdapterAndroidDestruction() {
        stopScan();
        this.mNativeBluetoothAdapterAndroid = 0;
        unregisterBroadcastReceiver();
    }

    @CalledByNative
    private static ChromeBluetoothAdapter create(long nativeBluetoothAdapterAndroid, Object adapterWrapper) {
        return new ChromeBluetoothAdapter(nativeBluetoothAdapterAndroid, (BluetoothAdapterWrapper) adapterWrapper);
    }

    @CalledByNative
    private String getAddress() {
        if (isPresent()) {
            return this.mAdapter.getAddress();
        }
        return "";
    }

    @CalledByNative
    private String getName() {
        if (isPresent()) {
            return this.mAdapter.getName();
        }
        return "";
    }

    @CalledByNative
    private boolean isPresent() {
        return this.mAdapter != null;
    }

    @CalledByNative
    private boolean isPowered() {
        return isPresent() && this.mAdapter.isEnabled();
    }

    @CalledByNative
    private boolean setPowered(boolean powered) {
        if (powered) {
            if (isPresent() && this.mAdapter.enable()) {
                return true;
            }
            return false;
        } else if (isPresent() && this.mAdapter.disable()) {
            return true;
        } else {
            return false;
        }
    }

    @CalledByNative
    private boolean isDiscoverable() {
        return isPresent() && this.mAdapter.getScanMode() == 23;
    }

    @CalledByNative
    private boolean isDiscovering() {
        return isPresent() && (this.mAdapter.isDiscovering() || this.mScanCallback != null);
    }

    @CalledByNative
    private boolean startScan() {
        BluetoothLeScannerWrapper scanner = this.mAdapter.getBluetoothLeScanner();
        if (scanner == null || !canScan()) {
            return false;
        }
        if ($assertionsDisabled || this.mScanCallback == null) {
            this.mScanCallback = new ScanCallback();
            try {
                scanner.startScan(null, 2, this.mScanCallback);
                return true;
            } catch (IllegalArgumentException e) {
                Log.m28e(TAG, "Cannot start scan: " + e, new Object[0]);
                this.mScanCallback = null;
                return false;
            } catch (IllegalStateException e2) {
                Log.m28e(TAG, "Adapter is off. Cannot start scan: " + e2, new Object[0]);
                this.mScanCallback = null;
                return false;
            }
        }
        throw new AssertionError();
    }

    @CalledByNative
    private boolean stopScan() {
        if (this.mScanCallback == null) {
            return false;
        }
        try {
            BluetoothLeScannerWrapper scanner = this.mAdapter.getBluetoothLeScanner();
            if (scanner != null) {
                scanner.stopScan(this.mScanCallback);
            }
        } catch (IllegalArgumentException e) {
            Log.m28e(TAG, "Cannot stop scan: " + e, new Object[0]);
        } catch (IllegalStateException e2) {
            Log.m28e(TAG, "Adapter is off. Cannot stop scan: " + e2, new Object[0]);
        }
        this.mScanCallback = null;
        return true;
    }

    private boolean canScan() {
        Context context = this.mAdapter.getContext();
        return LocationUtils.getInstance().hasAndroidLocationPermission(context) && LocationUtils.getInstance().isSystemLocationSettingEnabled(context);
    }

    private void registerBroadcastReceiver() {
        if (this.mAdapter != null) {
            this.mAdapter.getContext().registerReceiver(this, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
        }
    }

    private void unregisterBroadcastReceiver() {
        if (this.mAdapter != null) {
            this.mAdapter.getContext().unregisterReceiver(this);
        }
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (isPresent() && "android.bluetooth.adapter.action.STATE_CHANGED".equals(action)) {
            Log.m38w(TAG, "onReceive: BluetoothAdapter.ACTION_STATE_CHANGED: %s", getBluetoothStateString(intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE)));
            switch (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE)) {
                case 10:
                    nativeOnAdapterStateChanged(this.mNativeBluetoothAdapterAndroid, false);
                    return;
                case 12:
                    nativeOnAdapterStateChanged(this.mNativeBluetoothAdapterAndroid, true);
                    return;
                default:
                    return;
            }
        }
    }

    private String getBluetoothStateString(int state) {
        switch (state) {
            case 10:
                return "STATE_OFF";
            case 11:
                return "STATE_TURNING_ON";
            case 12:
                return "STATE_ON";
            case 13:
                return "STATE_TURNING_OFF";
            default:
                if ($assertionsDisabled) {
                    return "illegal state: " + state;
                }
                throw new AssertionError();
        }
    }
}
