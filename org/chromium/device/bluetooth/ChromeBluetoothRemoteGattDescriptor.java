package org.chromium.device.bluetooth;

import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("device")
final class ChromeBluetoothRemoteGattDescriptor {
    private static final String TAG = "Bluetooth";
    final ChromeBluetoothDevice mChromeDevice;
    final BluetoothGattDescriptorWrapper mDescriptor;
    private long mNativeBluetoothRemoteGattDescriptorAndroid;

    native void nativeOnRead(long j, int i, byte[] bArr);

    native void nativeOnWrite(long j, int i);

    private ChromeBluetoothRemoteGattDescriptor(long nativeBluetoothRemoteGattDescriptorAndroid, BluetoothGattDescriptorWrapper descriptorWrapper, ChromeBluetoothDevice chromeDevice) {
        this.mNativeBluetoothRemoteGattDescriptorAndroid = nativeBluetoothRemoteGattDescriptorAndroid;
        this.mDescriptor = descriptorWrapper;
        this.mChromeDevice = chromeDevice;
        this.mChromeDevice.mWrapperToChromeDescriptorsMap.put(descriptorWrapper, this);
        Log.m30v(TAG, "ChromeBluetoothRemoteGattDescriptor created.");
    }

    @CalledByNative
    private void onBluetoothRemoteGattDescriptorAndroidDestruction() {
        Log.m30v(TAG, "ChromeBluetoothRemoteGattDescriptor Destroyed.");
        this.mNativeBluetoothRemoteGattDescriptorAndroid = 0;
        this.mChromeDevice.mWrapperToChromeDescriptorsMap.remove(this.mDescriptor);
    }

    void onDescriptorRead(int status) {
        String str = TAG;
        String str2 = "onDescriptorRead status:%d==%s";
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(status);
        objArr[1] = status == 0 ? "OK" : "Error";
        Log.m29i(str, str2, objArr);
        if (this.mNativeBluetoothRemoteGattDescriptorAndroid != 0) {
            nativeOnRead(this.mNativeBluetoothRemoteGattDescriptorAndroid, status, this.mDescriptor.getValue());
        }
    }

    void onDescriptorWrite(int status) {
        String str = TAG;
        String str2 = "onDescriptorWrite status:%d==%s";
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(status);
        objArr[1] = status == 0 ? "OK" : "Error";
        Log.m29i(str, str2, objArr);
        if (this.mNativeBluetoothRemoteGattDescriptorAndroid != 0) {
            nativeOnWrite(this.mNativeBluetoothRemoteGattDescriptorAndroid, status);
        }
    }

    @CalledByNative
    private static ChromeBluetoothRemoteGattDescriptor create(long nativeBluetoothRemoteGattDescriptorAndroid, Object bluetoothGattDescriptorWrapper, ChromeBluetoothDevice chromeDevice) {
        return new ChromeBluetoothRemoteGattDescriptor(nativeBluetoothRemoteGattDescriptorAndroid, (BluetoothGattDescriptorWrapper) bluetoothGattDescriptorWrapper, chromeDevice);
    }

    @CalledByNative
    private String getUUID() {
        return this.mDescriptor.getUuid().toString();
    }

    @CalledByNative
    private boolean readRemoteDescriptor() {
        if (this.mChromeDevice.mBluetoothGatt.readDescriptor(this.mDescriptor)) {
            return true;
        }
        Log.m29i(TAG, "readRemoteDescriptor readDescriptor failed.", new Object[0]);
        return false;
    }

    @CalledByNative
    private boolean writeRemoteDescriptor(byte[] value) {
        if (!this.mDescriptor.setValue(value)) {
            Log.m29i(TAG, "writeRemoteDescriptor setValue failed.", new Object[0]);
            return false;
        } else if (this.mChromeDevice.mBluetoothGatt.writeDescriptor(this.mDescriptor)) {
            return true;
        } else {
            Log.m29i(TAG, "writeRemoteDescriptor writeDescriptor failed.", new Object[0]);
            return false;
        }
    }
}
