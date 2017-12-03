package org.chromium.device.usb;

import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("device")
final class ChromeUsbInterface {
    private static final String TAG = "Usb";
    final UsbInterface mInterface;

    private ChromeUsbInterface(UsbInterface iface) {
        this.mInterface = iface;
        Log.m30v(TAG, "ChromeUsbInterface created.");
    }

    @CalledByNative
    private static ChromeUsbInterface create(UsbInterface iface) {
        return new ChromeUsbInterface(iface);
    }

    @CalledByNative
    private int getInterfaceNumber() {
        return this.mInterface.getId();
    }

    @CalledByNative
    private int getAlternateSetting() {
        return this.mInterface.getAlternateSetting();
    }

    @CalledByNative
    private int getInterfaceClass() {
        return this.mInterface.getInterfaceClass();
    }

    @CalledByNative
    private int getInterfaceSubclass() {
        return this.mInterface.getInterfaceSubclass();
    }

    @CalledByNative
    private int getInterfaceProtocol() {
        return this.mInterface.getInterfaceProtocol();
    }

    @CalledByNative
    private UsbEndpoint[] getEndpoints() {
        int count = this.mInterface.getEndpointCount();
        UsbEndpoint[] endpoints = new UsbEndpoint[count];
        for (int i = 0; i < count; i++) {
            endpoints[i] = this.mInterface.getEndpoint(i);
        }
        return endpoints;
    }
}
