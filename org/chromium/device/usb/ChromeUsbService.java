package org.chromium.device.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("device")
final class ChromeUsbService {
    private static final String ACTION_USB_PERMISSION = "org.chromium.device.ACTION_USB_PERMISSION";
    private static final String TAG = "Usb";
    Context mContext;
    BroadcastReceiver mUsbDeviceReceiver;
    UsbManager mUsbManager = ((UsbManager) this.mContext.getSystemService("usb"));
    long mUsbServiceAndroid;

    class C02521 extends BroadcastReceiver {
        C02521() {
        }

        public void onReceive(Context context, Intent intent) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
            if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(intent.getAction())) {
                ChromeUsbService.this.nativeDeviceAttached(ChromeUsbService.this.mUsbServiceAndroid, device);
            } else if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(intent.getAction())) {
                ChromeUsbService.this.nativeDeviceDetached(ChromeUsbService.this.mUsbServiceAndroid, device.getDeviceId());
            } else if (ChromeUsbService.ACTION_USB_PERMISSION.equals(intent.getAction())) {
                ChromeUsbService.this.nativeDevicePermissionRequestComplete(ChromeUsbService.this.mUsbServiceAndroid, device.getDeviceId(), intent.getBooleanExtra("permission", false));
            }
        }
    }

    private native void nativeDeviceAttached(long j, UsbDevice usbDevice);

    private native void nativeDeviceDetached(long j, int i);

    private native void nativeDevicePermissionRequestComplete(long j, int i, boolean z);

    private ChromeUsbService(Context context, long usbServiceAndroid) {
        this.mContext = context;
        this.mUsbServiceAndroid = usbServiceAndroid;
        registerForUsbDeviceIntentBroadcast();
        Log.m30v(TAG, "ChromeUsbService created.");
    }

    @CalledByNative
    private static ChromeUsbService create(Context context, long usbServiceAndroid) {
        return new ChromeUsbService(context, usbServiceAndroid);
    }

    @CalledByNative
    private Object[] getDevices() {
        return this.mUsbManager.getDeviceList().values().toArray();
    }

    @CalledByNative
    private UsbDeviceConnection openDevice(ChromeUsbDevice wrapper) {
        return this.mUsbManager.openDevice(wrapper.getDevice());
    }

    @CalledByNative
    private void requestDevicePermission(ChromeUsbDevice wrapper, long nativeCallback) {
        UsbDevice device = wrapper.getDevice();
        if (this.mUsbManager.hasPermission(device)) {
            nativeDevicePermissionRequestComplete(this.mUsbServiceAndroid, device.getDeviceId(), true);
            return;
        }
        this.mUsbManager.requestPermission(wrapper.getDevice(), PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_USB_PERMISSION), 0));
    }

    @CalledByNative
    private void close() {
        unregisterForUsbDeviceIntentBroadcast();
    }

    private void registerForUsbDeviceIntentBroadcast() {
        this.mUsbDeviceReceiver = new C02521();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction(ACTION_USB_PERMISSION);
        this.mContext.registerReceiver(this.mUsbDeviceReceiver, filter);
    }

    private void unregisterForUsbDeviceIntentBroadcast() {
        this.mContext.unregisterReceiver(this.mUsbDeviceReceiver);
        this.mUsbDeviceReceiver = null;
    }
}
