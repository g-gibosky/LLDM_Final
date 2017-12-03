package org.chromium.device.usb;

import android.hardware.usb.UsbDeviceConnection;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("device")
class ChromeUsbConnection {
    private static final String TAG = "Usb";
    final UsbDeviceConnection mConnection;

    private ChromeUsbConnection(UsbDeviceConnection connection) {
        this.mConnection = connection;
        Log.m30v(TAG, "ChromeUsbConnection created.");
    }

    @CalledByNative
    private static ChromeUsbConnection create(UsbDeviceConnection connection) {
        return new ChromeUsbConnection(connection);
    }

    @CalledByNative
    private int getFileDescriptor() {
        return this.mConnection.getFileDescriptor();
    }

    @CalledByNative
    private void close() {
        this.mConnection.close();
    }
}
