package org.chromium.device.bluetooth;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.ParcelUuid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@TargetApi(21)
@JNINamespace("device")
final class ChromeBluetoothDevice {
    private static final String TAG = "Bluetooth";
    BluetoothGattWrapper mBluetoothGatt;
    private final BluetoothGattCallbackImpl mBluetoothGattCallbackImpl = new BluetoothGattCallbackImpl();
    final BluetoothDeviceWrapper mDevice;
    private long mNativeBluetoothDeviceAndroid;
    private HashSet<String> mUuidsFromScan = new HashSet();
    final HashMap<BluetoothGattCharacteristicWrapper, ChromeBluetoothRemoteGattCharacteristic> mWrapperToChromeCharacteristicsMap = new HashMap();
    final HashMap<BluetoothGattDescriptorWrapper, ChromeBluetoothRemoteGattDescriptor> mWrapperToChromeDescriptorsMap = new HashMap();

    private class BluetoothGattCallbackImpl extends BluetoothGattCallbackWrapper {

        class C02452 implements Runnable {
            C02452() {
            }

            public void run() {
                if (ChromeBluetoothDevice.this.mNativeBluetoothDeviceAndroid != 0) {
                    for (BluetoothGattServiceWrapper service : ChromeBluetoothDevice.this.mBluetoothGatt.getServices()) {
                        ChromeBluetoothDevice.this.nativeCreateGattRemoteService(ChromeBluetoothDevice.this.mNativeBluetoothDeviceAndroid, ChromeBluetoothDevice.this.getAddress() + "/" + service.getUuid().toString() + "," + service.getInstanceId(), service);
                    }
                    ChromeBluetoothDevice.this.nativeOnGattServicesDiscovered(ChromeBluetoothDevice.this.mNativeBluetoothDeviceAndroid);
                }
            }
        }

        private BluetoothGattCallbackImpl() {
        }

        public void onConnectionStateChange(final int status, final int newState) {
            String str = ChromeBluetoothDevice.TAG;
            String str2 = "onConnectionStateChange status:%d newState:%s";
            Object[] objArr = new Object[2];
            objArr[0] = Integer.valueOf(status);
            objArr[1] = newState == 2 ? "Connected" : "Disconnected";
            Log.m29i(str, str2, objArr);
            if (newState == 2) {
                ChromeBluetoothDevice.this.mBluetoothGatt.discoverServices();
            } else if (newState == 0 && ChromeBluetoothDevice.this.mBluetoothGatt != null) {
                ChromeBluetoothDevice.this.mBluetoothGatt.close();
                ChromeBluetoothDevice.this.mBluetoothGatt = null;
            }
            ThreadUtils.runOnUiThread(new Runnable() {
                public void run() {
                    if (ChromeBluetoothDevice.this.mNativeBluetoothDeviceAndroid != 0) {
                        ChromeBluetoothDevice.this.nativeOnConnectionStateChange(ChromeBluetoothDevice.this.mNativeBluetoothDeviceAndroid, status, newState == 2);
                    }
                }
            });
        }

        public void onServicesDiscovered(int status) {
            String str = ChromeBluetoothDevice.TAG;
            String str2 = "onServicesDiscovered status:%d==%s";
            Object[] objArr = new Object[2];
            objArr[0] = Integer.valueOf(status);
            objArr[1] = status == 0 ? "OK" : "Error";
            Log.m29i(str, str2, objArr);
            ThreadUtils.runOnUiThread(new C02452());
        }

        public void onCharacteristicChanged(final BluetoothGattCharacteristicWrapper characteristic) {
            Log.m29i(ChromeBluetoothDevice.TAG, "device onCharacteristicChanged.", new Object[0]);
            ThreadUtils.runOnUiThread(new Runnable() {
                public void run() {
                    ChromeBluetoothRemoteGattCharacteristic chromeCharacteristic = (ChromeBluetoothRemoteGattCharacteristic) ChromeBluetoothDevice.this.mWrapperToChromeCharacteristicsMap.get(characteristic);
                    if (chromeCharacteristic == null) {
                        Log.m30v(ChromeBluetoothDevice.TAG, "onCharacteristicChanged when chromeCharacteristic == null.");
                    } else {
                        chromeCharacteristic.onCharacteristicChanged();
                    }
                }
            });
        }

        public void onCharacteristicRead(final BluetoothGattCharacteristicWrapper characteristic, final int status) {
            ThreadUtils.runOnUiThread(new Runnable() {
                public void run() {
                    ChromeBluetoothRemoteGattCharacteristic chromeCharacteristic = (ChromeBluetoothRemoteGattCharacteristic) ChromeBluetoothDevice.this.mWrapperToChromeCharacteristicsMap.get(characteristic);
                    if (chromeCharacteristic == null) {
                        Log.m30v(ChromeBluetoothDevice.TAG, "onCharacteristicRead when chromeCharacteristic == null.");
                    } else {
                        chromeCharacteristic.onCharacteristicRead(status);
                    }
                }
            });
        }

        public void onCharacteristicWrite(final BluetoothGattCharacteristicWrapper characteristic, final int status) {
            ThreadUtils.runOnUiThread(new Runnable() {
                public void run() {
                    ChromeBluetoothRemoteGattCharacteristic chromeCharacteristic = (ChromeBluetoothRemoteGattCharacteristic) ChromeBluetoothDevice.this.mWrapperToChromeCharacteristicsMap.get(characteristic);
                    if (chromeCharacteristic == null) {
                        Log.m30v(ChromeBluetoothDevice.TAG, "onCharacteristicWrite when chromeCharacteristic == null.");
                    } else {
                        chromeCharacteristic.onCharacteristicWrite(status);
                    }
                }
            });
        }

        public void onDescriptorRead(final BluetoothGattDescriptorWrapper descriptor, final int status) {
            ThreadUtils.runOnUiThread(new Runnable() {
                public void run() {
                    ChromeBluetoothRemoteGattDescriptor chromeDescriptor = (ChromeBluetoothRemoteGattDescriptor) ChromeBluetoothDevice.this.mWrapperToChromeDescriptorsMap.get(descriptor);
                    if (chromeDescriptor == null) {
                        Log.m30v(ChromeBluetoothDevice.TAG, "onDescriptorRead when chromeDescriptor == null.");
                    } else {
                        chromeDescriptor.onDescriptorRead(status);
                    }
                }
            });
        }

        public void onDescriptorWrite(final BluetoothGattDescriptorWrapper descriptor, final int status) {
            ThreadUtils.runOnUiThread(new Runnable() {
                public void run() {
                    ChromeBluetoothRemoteGattDescriptor chromeDescriptor = (ChromeBluetoothRemoteGattDescriptor) ChromeBluetoothDevice.this.mWrapperToChromeDescriptorsMap.get(descriptor);
                    if (chromeDescriptor == null) {
                        Log.m30v(ChromeBluetoothDevice.TAG, "onDescriptorWrite when chromeDescriptor == null.");
                    } else {
                        chromeDescriptor.onDescriptorWrite(status);
                    }
                }
            });
        }
    }

    private native void nativeCreateGattRemoteService(long j, String str, Object obj);

    private native void nativeOnConnectionStateChange(long j, int i, boolean z);

    private native void nativeOnGattServicesDiscovered(long j);

    private ChromeBluetoothDevice(long nativeBluetoothDeviceAndroid, BluetoothDeviceWrapper deviceWrapper) {
        this.mNativeBluetoothDeviceAndroid = nativeBluetoothDeviceAndroid;
        this.mDevice = deviceWrapper;
        Log.m30v(TAG, "ChromeBluetoothDevice created.");
    }

    @CalledByNative
    private void onBluetoothDeviceAndroidDestruction() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
        this.mNativeBluetoothDeviceAndroid = 0;
    }

    @CalledByNative
    private static ChromeBluetoothDevice create(long nativeBluetoothDeviceAndroid, Object deviceWrapper) {
        return new ChromeBluetoothDevice(nativeBluetoothDeviceAndroid, (BluetoothDeviceWrapper) deviceWrapper);
    }

    @CalledByNative
    private boolean updateAdvertisedUUIDs(List<ParcelUuid> uuidsFromScan) {
        if (uuidsFromScan == null) {
            return false;
        }
        boolean uuidsUpdated = false;
        for (ParcelUuid uuid : uuidsFromScan) {
            uuidsUpdated |= this.mUuidsFromScan.add(uuid.toString());
        }
        return uuidsUpdated;
    }

    @CalledByNative
    private int getBluetoothClass() {
        return this.mDevice.getBluetoothClass_getDeviceClass();
    }

    @CalledByNative
    private String getAddress() {
        return this.mDevice.getAddress();
    }

    @CalledByNative
    private boolean isPaired() {
        return this.mDevice.getBondState() == 12;
    }

    @CalledByNative
    private String[] getUuids() {
        return (String[]) this.mUuidsFromScan.toArray(new String[this.mUuidsFromScan.size()]);
    }

    @CalledByNative
    private void createGattConnectionImpl(Context context) {
        Log.m29i(TAG, "connectGatt", new Object[0]);
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
        }
        this.mBluetoothGatt = this.mDevice.connectGatt(context, false, this.mBluetoothGattCallbackImpl);
    }

    @CalledByNative
    private void disconnectGatt() {
        Log.m29i(TAG, "BluetoothGatt.disconnect", new Object[0]);
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.disconnect();
        }
    }

    @CalledByNative
    private String getDeviceName() {
        return this.mDevice.getName();
    }
}
