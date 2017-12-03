package org.chromium.device.bluetooth;

import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("device")
final class ChromeBluetoothRemoteGattService {
    private static final String TAG = "Bluetooth";
    ChromeBluetoothDevice mChromeDevice;
    final String mInstanceId;
    private long mNativeBluetoothRemoteGattServiceAndroid;
    final BluetoothGattServiceWrapper mService;

    private native void nativeCreateGattRemoteCharacteristic(long j, String str, Object obj, Object obj2);

    private ChromeBluetoothRemoteGattService(long nativeBluetoothRemoteGattServiceAndroid, BluetoothGattServiceWrapper serviceWrapper, String instanceId, ChromeBluetoothDevice chromeDevice) {
        this.mNativeBluetoothRemoteGattServiceAndroid = nativeBluetoothRemoteGattServiceAndroid;
        this.mService = serviceWrapper;
        this.mInstanceId = instanceId;
        this.mChromeDevice = chromeDevice;
        Log.m30v(TAG, "ChromeBluetoothRemoteGattService created.");
    }

    @CalledByNative
    private void onBluetoothRemoteGattServiceAndroidDestruction() {
        this.mNativeBluetoothRemoteGattServiceAndroid = 0;
    }

    @CalledByNative
    private static ChromeBluetoothRemoteGattService create(long nativeBluetoothRemoteGattServiceAndroid, Object bluetoothGattServiceWrapper, String instanceId, ChromeBluetoothDevice chromeDevice) {
        return new ChromeBluetoothRemoteGattService(nativeBluetoothRemoteGattServiceAndroid, (BluetoothGattServiceWrapper) bluetoothGattServiceWrapper, instanceId, chromeDevice);
    }

    @CalledByNative
    private String getUUID() {
        return this.mService.getUuid().toString();
    }

    @CalledByNative
    private void createCharacteristics() {
        for (BluetoothGattCharacteristicWrapper characteristic : this.mService.getCharacteristics()) {
            nativeCreateGattRemoteCharacteristic(this.mNativeBluetoothRemoteGattServiceAndroid, this.mInstanceId + "/" + characteristic.getUuid().toString() + "," + characteristic.getInstanceId(), characteristic, this.mChromeDevice);
        }
    }
}
