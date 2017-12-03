package org.chromium.media.midi;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("media::midi")
class UsbMidiDeviceFactoryAndroid {
    static final /* synthetic */ boolean $assertionsDisabled = (!UsbMidiDeviceFactoryAndroid.class.desiredAssertionStatus());
    private static final String ACTION_USB_PERMISSION = "org.chromium.media.USB_PERMISSION";
    private final List<UsbMidiDeviceAndroid> mDevices = new ArrayList();
    private boolean mIsEnumeratingDevices;
    private long mNativePointer;
    private BroadcastReceiver mReceiver;
    private Set<UsbDevice> mRequestedDevices;
    private UsbManager mUsbManager;

    class C02691 extends BroadcastReceiver {
        C02691() {
        }

        public void onReceive(Context context, Intent intent) {
            Parcelable extra = intent.getParcelableExtra("device");
            if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(intent.getAction())) {
                UsbMidiDeviceFactoryAndroid.this.requestDevicePermissionIfNecessary(context, (UsbDevice) extra);
            }
            if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(intent.getAction())) {
                UsbMidiDeviceFactoryAndroid.this.onUsbDeviceDetached((UsbDevice) extra);
            }
            if (UsbMidiDeviceFactoryAndroid.ACTION_USB_PERMISSION.equals(intent.getAction())) {
                UsbMidiDeviceFactoryAndroid.this.onUsbDevicePermissionRequestDone(context, intent);
            }
        }
    }

    private static native void nativeOnUsbMidiDeviceAttached(long j, Object obj);

    private static native void nativeOnUsbMidiDeviceDetached(long j, int i);

    private static native void nativeOnUsbMidiDeviceRequestDone(long j, Object[] objArr);

    UsbMidiDeviceFactoryAndroid(Context context, long nativePointer) {
        this.mUsbManager = (UsbManager) context.getSystemService("usb");
        this.mNativePointer = nativePointer;
        this.mReceiver = new C02691();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addAction(ACTION_USB_PERMISSION);
        context.registerReceiver(this.mReceiver, filter);
        this.mRequestedDevices = new HashSet();
    }

    @CalledByNative
    static UsbMidiDeviceFactoryAndroid create(Context context, long nativePointer) {
        return new UsbMidiDeviceFactoryAndroid(context, nativePointer);
    }

    @CalledByNative
    boolean enumerateDevices(Context context) {
        boolean z = true;
        if ($assertionsDisabled || !this.mIsEnumeratingDevices) {
            this.mIsEnumeratingDevices = true;
            Map<String, UsbDevice> devices = this.mUsbManager.getDeviceList();
            if (devices.isEmpty()) {
                this.mIsEnumeratingDevices = false;
                return false;
            }
            for (UsbDevice device : devices.values()) {
                requestDevicePermissionIfNecessary(context, device);
            }
            if (this.mRequestedDevices.isEmpty()) {
                z = false;
            }
            return z;
        }
        throw new AssertionError();
    }

    private void requestDevicePermissionIfNecessary(Context context, UsbDevice device) {
        for (UsbDevice d : this.mRequestedDevices) {
            if (d.getDeviceId() == device.getDeviceId()) {
                return;
            }
        }
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface iface = device.getInterface(i);
            if (iface.getInterfaceClass() == 1 && iface.getInterfaceSubclass() == 3) {
                this.mUsbManager.requestPermission(device, PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0));
                this.mRequestedDevices.add(device);
                return;
            }
        }
    }

    private void onUsbDeviceDetached(UsbDevice device) {
        for (UsbDevice usbDevice : this.mRequestedDevices) {
            if (usbDevice.getDeviceId() == device.getDeviceId()) {
                this.mRequestedDevices.remove(usbDevice);
                break;
            }
        }
        for (int i = 0; i < this.mDevices.size(); i++) {
            UsbMidiDeviceAndroid midiDevice = (UsbMidiDeviceAndroid) this.mDevices.get(i);
            if (!midiDevice.isClosed() && midiDevice.getUsbDevice().getDeviceId() == device.getDeviceId()) {
                midiDevice.close();
                if (this.mIsEnumeratingDevices) {
                    this.mDevices.remove(i);
                    return;
                } else if (this.mNativePointer != 0) {
                    nativeOnUsbMidiDeviceDetached(this.mNativePointer, i);
                    return;
                } else {
                    return;
                }
            }
        }
    }

    private void onUsbDevicePermissionRequestDone(Context context, Intent intent) {
        UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
        UsbMidiDeviceAndroid midiDevice = null;
        if (this.mRequestedDevices.contains(device)) {
            this.mRequestedDevices.remove(device);
            if (!intent.getBooleanExtra("permission", false)) {
                device = null;
            }
        } else {
            device = null;
        }
        if (device != null) {
            for (UsbMidiDeviceAndroid registered : this.mDevices) {
                if (!registered.isClosed() && registered.getUsbDevice().getDeviceId() == device.getDeviceId()) {
                    device = null;
                    break;
                }
            }
        }
        if (device != null) {
            midiDevice = new UsbMidiDeviceAndroid(this.mUsbManager, device);
            this.mDevices.add(midiDevice);
        }
        if (!this.mRequestedDevices.isEmpty() || this.mNativePointer == 0) {
            return;
        }
        if (this.mIsEnumeratingDevices) {
            nativeOnUsbMidiDeviceRequestDone(this.mNativePointer, this.mDevices.toArray());
            this.mIsEnumeratingDevices = false;
        } else if (midiDevice != null) {
            nativeOnUsbMidiDeviceAttached(this.mNativePointer, midiDevice);
        }
    }

    @CalledByNative
    void close(Context context) {
        this.mNativePointer = 0;
        context.unregisterReceiver(this.mReceiver);
    }
}
