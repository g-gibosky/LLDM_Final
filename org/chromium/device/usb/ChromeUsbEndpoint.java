package org.chromium.device.usb;

import android.hardware.usb.UsbEndpoint;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("device")
final class ChromeUsbEndpoint {
    private static final String TAG = "Usb";
    final UsbEndpoint mEndpoint;

    private ChromeUsbEndpoint(UsbEndpoint endpoint) {
        this.mEndpoint = endpoint;
        Log.m30v(TAG, "ChromeUsbEndpoint created.");
    }

    @CalledByNative
    private static ChromeUsbEndpoint create(UsbEndpoint endpoint) {
        return new ChromeUsbEndpoint(endpoint);
    }

    @CalledByNative
    private int getAddress() {
        return this.mEndpoint.getAddress();
    }

    @CalledByNative
    private int getMaxPacketSize() {
        return this.mEndpoint.getMaxPacketSize();
    }

    @CalledByNative
    private int getAttributes() {
        return this.mEndpoint.getAttributes();
    }

    @CalledByNative
    private int getInterval() {
        return this.mEndpoint.getInterval();
    }
}
