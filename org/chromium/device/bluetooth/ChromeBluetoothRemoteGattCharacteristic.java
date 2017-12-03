package org.chromium.device.bluetooth;

import android.annotation.TargetApi;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@TargetApi(21)
@JNINamespace("device")
final class ChromeBluetoothRemoteGattCharacteristic {
    private static final String TAG = "Bluetooth";
    final BluetoothGattCharacteristicWrapper mCharacteristic;
    final ChromeBluetoothDevice mChromeDevice;
    final String mInstanceId;
    private long mNativeBluetoothRemoteGattCharacteristicAndroid;

    private native void nativeCreateGattRemoteDescriptor(long j, String str, Object obj, Object obj2);

    native void nativeOnChanged(long j, byte[] bArr);

    native void nativeOnRead(long j, int i, byte[] bArr);

    native void nativeOnWrite(long j, int i);

    private ChromeBluetoothRemoteGattCharacteristic(long nativeBluetoothRemoteGattCharacteristicAndroid, BluetoothGattCharacteristicWrapper characteristicWrapper, String instanceId, ChromeBluetoothDevice chromeDevice) {
        this.mNativeBluetoothRemoteGattCharacteristicAndroid = nativeBluetoothRemoteGattCharacteristicAndroid;
        this.mCharacteristic = characteristicWrapper;
        this.mInstanceId = instanceId;
        this.mChromeDevice = chromeDevice;
        this.mChromeDevice.mWrapperToChromeCharacteristicsMap.put(characteristicWrapper, this);
        Log.m30v(TAG, "ChromeBluetoothRemoteGattCharacteristic created.");
    }

    @CalledByNative
    private void onBluetoothRemoteGattCharacteristicAndroidDestruction() {
        Log.m30v(TAG, "ChromeBluetoothRemoteGattCharacteristic Destroyed.");
        if (this.mChromeDevice.mBluetoothGatt != null) {
            this.mChromeDevice.mBluetoothGatt.setCharacteristicNotification(this.mCharacteristic, false);
        }
        this.mNativeBluetoothRemoteGattCharacteristicAndroid = 0;
        this.mChromeDevice.mWrapperToChromeCharacteristicsMap.remove(this.mCharacteristic);
    }

    void onCharacteristicChanged() {
        Log.m29i(TAG, "onCharacteristicChanged", new Object[0]);
        if (this.mNativeBluetoothRemoteGattCharacteristicAndroid != 0) {
            nativeOnChanged(this.mNativeBluetoothRemoteGattCharacteristicAndroid, this.mCharacteristic.getValue());
        }
    }

    void onCharacteristicRead(int status) {
        String str = TAG;
        String str2 = "onCharacteristicRead status:%d==%s";
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(status);
        objArr[1] = status == 0 ? "OK" : "Error";
        Log.m29i(str, str2, objArr);
        if (this.mNativeBluetoothRemoteGattCharacteristicAndroid != 0) {
            nativeOnRead(this.mNativeBluetoothRemoteGattCharacteristicAndroid, status, this.mCharacteristic.getValue());
        }
    }

    void onCharacteristicWrite(int status) {
        String str = TAG;
        String str2 = "onCharacteristicWrite status:%d==%s";
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(status);
        objArr[1] = status == 0 ? "OK" : "Error";
        Log.m29i(str, str2, objArr);
        if (this.mNativeBluetoothRemoteGattCharacteristicAndroid != 0) {
            nativeOnWrite(this.mNativeBluetoothRemoteGattCharacteristicAndroid, status);
        }
    }

    @CalledByNative
    private static ChromeBluetoothRemoteGattCharacteristic create(long nativeBluetoothRemoteGattCharacteristicAndroid, Object bluetoothGattCharacteristicWrapper, String instanceId, ChromeBluetoothDevice chromeDevice) {
        return new ChromeBluetoothRemoteGattCharacteristic(nativeBluetoothRemoteGattCharacteristicAndroid, (BluetoothGattCharacteristicWrapper) bluetoothGattCharacteristicWrapper, instanceId, chromeDevice);
    }

    @CalledByNative
    private String getUUID() {
        return this.mCharacteristic.getUuid().toString();
    }

    @CalledByNative
    private int getProperties() {
        return this.mCharacteristic.getProperties();
    }

    @CalledByNative
    private boolean readRemoteCharacteristic() {
        if (this.mChromeDevice.mBluetoothGatt.readCharacteristic(this.mCharacteristic)) {
            return true;
        }
        Log.m29i(TAG, "readRemoteCharacteristic readCharacteristic failed.", new Object[0]);
        return false;
    }

    @CalledByNative
    private boolean writeRemoteCharacteristic(byte[] value) {
        if (!this.mCharacteristic.setValue(value)) {
            Log.m29i(TAG, "writeRemoteCharacteristic setValue failed.", new Object[0]);
            return false;
        } else if (this.mChromeDevice.mBluetoothGatt.writeCharacteristic(this.mCharacteristic)) {
            return true;
        } else {
            Log.m29i(TAG, "writeRemoteCharacteristic writeCharacteristic failed.", new Object[0]);
            return false;
        }
    }

    @CalledByNative
    private boolean setCharacteristicNotification(boolean enabled) {
        return this.mChromeDevice.mBluetoothGatt.setCharacteristicNotification(this.mCharacteristic, enabled);
    }

    @CalledByNative
    private void createDescriptors() {
        int instanceIdCounter = 0;
        for (BluetoothGattDescriptorWrapper descriptor : this.mCharacteristic.getDescriptors()) {
            int instanceIdCounter2 = instanceIdCounter + 1;
            nativeCreateGattRemoteDescriptor(this.mNativeBluetoothRemoteGattCharacteristicAndroid, this.mInstanceId + "/" + descriptor.getUuid().toString() + ";" + instanceIdCounter, descriptor, this.mChromeDevice);
            instanceIdCounter = instanceIdCounter2;
        }
    }
}
