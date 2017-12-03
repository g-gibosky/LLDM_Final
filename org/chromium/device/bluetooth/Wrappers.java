package org.chromium.device.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanSettings.Builder;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.ParcelUuid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@TargetApi(21)
@JNINamespace("device")
class Wrappers {
    public static final int DEVICE_CLASS_UNSPECIFIED = 7936;
    private static final String TAG = "Bluetooth";

    static class BluetoothAdapterWrapper {
        private final BluetoothAdapter mAdapter;
        protected final Context mContext;
        protected BluetoothLeScannerWrapper mScannerWrapper;

        @CalledByNative("BluetoothAdapterWrapper")
        public static BluetoothAdapterWrapper createWithDefaultAdapter(Context context) {
            boolean hasMinAPI;
            boolean hasLowEnergyFeature = true;
            if (VERSION.SDK_INT >= 21) {
                hasMinAPI = true;
            } else {
                hasMinAPI = false;
            }
            if (hasMinAPI) {
                boolean hasPermissions;
                if (context.checkCallingOrSelfPermission("android.permission.BLUETOOTH") == 0 && context.checkCallingOrSelfPermission("android.permission.BLUETOOTH_ADMIN") == 0) {
                    hasPermissions = true;
                } else {
                    hasPermissions = false;
                }
                if (hasPermissions) {
                    if (VERSION.SDK_INT < 18 || !context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
                        hasLowEnergyFeature = false;
                    }
                    if (hasLowEnergyFeature) {
                        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                        if (adapter != null) {
                            return new BluetoothAdapterWrapper(adapter, context);
                        }
                        Log.m29i(Wrappers.TAG, "BluetoothAdapterWrapper.create failed: Default adapter not found.", new Object[0]);
                        return null;
                    }
                    Log.m29i(Wrappers.TAG, "BluetoothAdapterWrapper.create failed: No Low Energy support.", new Object[0]);
                    return null;
                }
                Log.m38w(Wrappers.TAG, "BluetoothAdapterWrapper.create failed: Lacking Bluetooth permissions.", new Object[0]);
                return null;
            }
            Log.m29i(Wrappers.TAG, "BluetoothAdapterWrapper.create failed: SDK version (%d) too low.", Integer.valueOf(VERSION.SDK_INT));
            return null;
        }

        public BluetoothAdapterWrapper(BluetoothAdapter adapter, Context context) {
            this.mAdapter = adapter;
            this.mContext = context;
        }

        public boolean disable() {
            return this.mAdapter.disable();
        }

        public boolean enable() {
            return this.mAdapter.enable();
        }

        public String getAddress() {
            return this.mAdapter.getAddress();
        }

        public BluetoothLeScannerWrapper getBluetoothLeScanner() {
            BluetoothLeScanner scanner = this.mAdapter.getBluetoothLeScanner();
            if (scanner == null) {
                return null;
            }
            if (this.mScannerWrapper == null) {
                this.mScannerWrapper = new BluetoothLeScannerWrapper(scanner);
            }
            return this.mScannerWrapper;
        }

        public Context getContext() {
            return this.mContext;
        }

        public String getName() {
            return this.mAdapter.getName();
        }

        public int getScanMode() {
            return this.mAdapter.getScanMode();
        }

        public boolean isDiscovering() {
            return this.mAdapter.isDiscovering();
        }

        public boolean isEnabled() {
            return this.mAdapter.isEnabled();
        }
    }

    static class BluetoothDeviceWrapper {
        private final HashMap<BluetoothGattCharacteristic, BluetoothGattCharacteristicWrapper> mCharacteristicsToWrappers = new HashMap();
        private final HashMap<BluetoothGattDescriptor, BluetoothGattDescriptorWrapper> mDescriptorsToWrappers = new HashMap();
        private final BluetoothDevice mDevice;

        public BluetoothDeviceWrapper(BluetoothDevice device) {
            this.mDevice = device;
        }

        public BluetoothGattWrapper connectGatt(Context context, boolean autoConnect, BluetoothGattCallbackWrapper callback) {
            return new BluetoothGattWrapper(this.mDevice.connectGatt(context, autoConnect, new ForwardBluetoothGattCallbackToWrapper(callback, this)), this);
        }

        public String getAddress() {
            return this.mDevice.getAddress();
        }

        public int getBluetoothClass_getDeviceClass() {
            if (this.mDevice == null || this.mDevice.getBluetoothClass() == null) {
                return Wrappers.DEVICE_CLASS_UNSPECIFIED;
            }
            return this.mDevice.getBluetoothClass().getDeviceClass();
        }

        public int getBondState() {
            return this.mDevice.getBondState();
        }

        public String getName() {
            return this.mDevice.getName();
        }
    }

    static abstract class BluetoothGattCallbackWrapper {
        public abstract void onCharacteristicChanged(BluetoothGattCharacteristicWrapper bluetoothGattCharacteristicWrapper);

        public abstract void onCharacteristicRead(BluetoothGattCharacteristicWrapper bluetoothGattCharacteristicWrapper, int i);

        public abstract void onCharacteristicWrite(BluetoothGattCharacteristicWrapper bluetoothGattCharacteristicWrapper, int i);

        public abstract void onConnectionStateChange(int i, int i2);

        public abstract void onDescriptorRead(BluetoothGattDescriptorWrapper bluetoothGattDescriptorWrapper, int i);

        public abstract void onDescriptorWrite(BluetoothGattDescriptorWrapper bluetoothGattDescriptorWrapper, int i);

        public abstract void onServicesDiscovered(int i);

        BluetoothGattCallbackWrapper() {
        }
    }

    static class BluetoothGattCharacteristicWrapper {
        final BluetoothGattCharacteristic mCharacteristic;
        final BluetoothDeviceWrapper mDeviceWrapper;

        public BluetoothGattCharacteristicWrapper(BluetoothGattCharacteristic characteristic, BluetoothDeviceWrapper deviceWrapper) {
            this.mCharacteristic = characteristic;
            this.mDeviceWrapper = deviceWrapper;
        }

        public List<BluetoothGattDescriptorWrapper> getDescriptors() {
            List<BluetoothGattDescriptor> descriptors = this.mCharacteristic.getDescriptors();
            ArrayList<BluetoothGattDescriptorWrapper> descriptorsWrapped = new ArrayList(descriptors.size());
            for (BluetoothGattDescriptor descriptor : descriptors) {
                BluetoothGattDescriptorWrapper descriptorWrapper = (BluetoothGattDescriptorWrapper) this.mDeviceWrapper.mDescriptorsToWrappers.get(descriptor);
                if (descriptorWrapper == null) {
                    descriptorWrapper = new BluetoothGattDescriptorWrapper(descriptor, this.mDeviceWrapper);
                    this.mDeviceWrapper.mDescriptorsToWrappers.put(descriptor, descriptorWrapper);
                }
                descriptorsWrapped.add(descriptorWrapper);
            }
            return descriptorsWrapped;
        }

        public int getInstanceId() {
            return this.mCharacteristic.getInstanceId();
        }

        public int getProperties() {
            return this.mCharacteristic.getProperties();
        }

        public UUID getUuid() {
            return this.mCharacteristic.getUuid();
        }

        public byte[] getValue() {
            return this.mCharacteristic.getValue();
        }

        public boolean setValue(byte[] value) {
            return this.mCharacteristic.setValue(value);
        }
    }

    static class BluetoothGattDescriptorWrapper {
        private final BluetoothGattDescriptor mDescriptor;
        final BluetoothDeviceWrapper mDeviceWrapper;

        public BluetoothGattDescriptorWrapper(BluetoothGattDescriptor descriptor, BluetoothDeviceWrapper deviceWrapper) {
            this.mDescriptor = descriptor;
            this.mDeviceWrapper = deviceWrapper;
        }

        public BluetoothGattCharacteristicWrapper getCharacteristic() {
            return (BluetoothGattCharacteristicWrapper) this.mDeviceWrapper.mCharacteristicsToWrappers.get(this.mDescriptor.getCharacteristic());
        }

        public UUID getUuid() {
            return this.mDescriptor.getUuid();
        }

        public byte[] getValue() {
            return this.mDescriptor.getValue();
        }

        public boolean setValue(byte[] value) {
            return this.mDescriptor.setValue(value);
        }
    }

    static class BluetoothGattServiceWrapper {
        private final BluetoothDeviceWrapper mDeviceWrapper;
        private final BluetoothGattService mService;

        public BluetoothGattServiceWrapper(BluetoothGattService service, BluetoothDeviceWrapper deviceWrapper) {
            this.mService = service;
            this.mDeviceWrapper = deviceWrapper;
        }

        public List<BluetoothGattCharacteristicWrapper> getCharacteristics() {
            List<BluetoothGattCharacteristic> characteristics = this.mService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristicWrapper> characteristicsWrapped = new ArrayList(characteristics.size());
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                BluetoothGattCharacteristicWrapper characteristicWrapper = (BluetoothGattCharacteristicWrapper) this.mDeviceWrapper.mCharacteristicsToWrappers.get(characteristic);
                if (characteristicWrapper == null) {
                    characteristicWrapper = new BluetoothGattCharacteristicWrapper(characteristic, this.mDeviceWrapper);
                    this.mDeviceWrapper.mCharacteristicsToWrappers.put(characteristic, characteristicWrapper);
                }
                characteristicsWrapped.add(characteristicWrapper);
            }
            return characteristicsWrapped;
        }

        public int getInstanceId() {
            return this.mService.getInstanceId();
        }

        public UUID getUuid() {
            return this.mService.getUuid();
        }
    }

    static class BluetoothGattWrapper {
        private final BluetoothDeviceWrapper mDeviceWrapper;
        private final BluetoothGatt mGatt;

        BluetoothGattWrapper(BluetoothGatt gatt, BluetoothDeviceWrapper deviceWrapper) {
            this.mGatt = gatt;
            this.mDeviceWrapper = deviceWrapper;
        }

        public void disconnect() {
            this.mGatt.disconnect();
        }

        public void close() {
            this.mGatt.close();
        }

        public void discoverServices() {
            this.mGatt.discoverServices();
        }

        public List<BluetoothGattServiceWrapper> getServices() {
            List<BluetoothGattService> services = this.mGatt.getServices();
            ArrayList<BluetoothGattServiceWrapper> servicesWrapped = new ArrayList(services.size());
            for (BluetoothGattService service : services) {
                servicesWrapped.add(new BluetoothGattServiceWrapper(service, this.mDeviceWrapper));
            }
            return servicesWrapped;
        }

        boolean readCharacteristic(BluetoothGattCharacteristicWrapper characteristic) {
            return this.mGatt.readCharacteristic(characteristic.mCharacteristic);
        }

        boolean setCharacteristicNotification(BluetoothGattCharacteristicWrapper characteristic, boolean enable) {
            return this.mGatt.setCharacteristicNotification(characteristic.mCharacteristic, enable);
        }

        boolean writeCharacteristic(BluetoothGattCharacteristicWrapper characteristic) {
            return this.mGatt.writeCharacteristic(characteristic.mCharacteristic);
        }

        boolean readDescriptor(BluetoothGattDescriptorWrapper descriptor) {
            return this.mGatt.readDescriptor(descriptor.mDescriptor);
        }

        boolean writeDescriptor(BluetoothGattDescriptorWrapper descriptor) {
            return this.mGatt.writeDescriptor(descriptor.mDescriptor);
        }
    }

    static class BluetoothLeScannerWrapper {
        private final HashMap<ScanCallbackWrapper, ForwardScanCallbackToWrapper> mCallbacks = new HashMap();
        protected final BluetoothLeScanner mScanner;

        public BluetoothLeScannerWrapper(BluetoothLeScanner scanner) {
            this.mScanner = scanner;
        }

        public void startScan(List<ScanFilter> filters, int scanSettingsScanMode, ScanCallbackWrapper callback) {
            ScanSettings settings = new Builder().setScanMode(scanSettingsScanMode).build();
            ForwardScanCallbackToWrapper callbackForwarder = new ForwardScanCallbackToWrapper(callback);
            this.mCallbacks.put(callback, callbackForwarder);
            this.mScanner.startScan(filters, settings, callbackForwarder);
        }

        public void stopScan(ScanCallbackWrapper callback) {
            this.mScanner.stopScan((ForwardScanCallbackToWrapper) this.mCallbacks.remove(callback));
        }
    }

    static class ForwardBluetoothGattCallbackToWrapper extends BluetoothGattCallback {
        final BluetoothDeviceWrapper mDeviceWrapper;
        final BluetoothGattCallbackWrapper mWrapperCallback;

        ForwardBluetoothGattCallbackToWrapper(BluetoothGattCallbackWrapper wrapperCallback, BluetoothDeviceWrapper deviceWrapper) {
            this.mWrapperCallback = wrapperCallback;
            this.mDeviceWrapper = deviceWrapper;
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.m29i(Wrappers.TAG, "wrapper onCharacteristicChanged.", new Object[0]);
            this.mWrapperCallback.onCharacteristicChanged((BluetoothGattCharacteristicWrapper) this.mDeviceWrapper.mCharacteristicsToWrappers.get(characteristic));
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            this.mWrapperCallback.onCharacteristicRead((BluetoothGattCharacteristicWrapper) this.mDeviceWrapper.mCharacteristicsToWrappers.get(characteristic), status);
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            this.mWrapperCallback.onCharacteristicWrite((BluetoothGattCharacteristicWrapper) this.mDeviceWrapper.mCharacteristicsToWrappers.get(characteristic), status);
        }

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            this.mWrapperCallback.onDescriptorRead((BluetoothGattDescriptorWrapper) this.mDeviceWrapper.mDescriptorsToWrappers.get(descriptor), status);
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            this.mWrapperCallback.onDescriptorWrite((BluetoothGattDescriptorWrapper) this.mDeviceWrapper.mDescriptorsToWrappers.get(descriptor), status);
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            this.mWrapperCallback.onConnectionStateChange(status, newState);
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            this.mWrapperCallback.onServicesDiscovered(status);
        }
    }

    static class ForwardScanCallbackToWrapper extends ScanCallback {
        final ScanCallbackWrapper mWrapperCallback;

        ForwardScanCallbackToWrapper(ScanCallbackWrapper wrapperCallback) {
            this.mWrapperCallback = wrapperCallback;
        }

        public void onBatchScanResults(List<ScanResult> results) {
            ArrayList<ScanResultWrapper> resultsWrapped = new ArrayList(results.size());
            for (ScanResult result : results) {
                resultsWrapped.add(new ScanResultWrapper(result));
            }
            this.mWrapperCallback.onBatchScanResult(resultsWrapped);
        }

        public void onScanResult(int callbackType, ScanResult result) {
            this.mWrapperCallback.onScanResult(callbackType, new ScanResultWrapper(result));
        }

        public void onScanFailed(int errorCode) {
            this.mWrapperCallback.onScanFailed(errorCode);
        }
    }

    static abstract class ScanCallbackWrapper {
        public abstract void onBatchScanResult(List<ScanResultWrapper> list);

        public abstract void onScanFailed(int i);

        public abstract void onScanResult(int i, ScanResultWrapper scanResultWrapper);

        ScanCallbackWrapper() {
        }
    }

    static class ScanResultWrapper {
        private final ScanResult mScanResult;

        public ScanResultWrapper(ScanResult scanResult) {
            this.mScanResult = scanResult;
        }

        public BluetoothDeviceWrapper getDevice() {
            return new BluetoothDeviceWrapper(this.mScanResult.getDevice());
        }

        public List<ParcelUuid> getScanRecord_getServiceUuids() {
            return this.mScanResult.getScanRecord().getServiceUuids();
        }
    }

    Wrappers() {
    }
}
