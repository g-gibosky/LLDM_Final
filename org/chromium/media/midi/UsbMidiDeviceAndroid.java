package org.chromium.media.midi;

import android.annotation.TargetApi;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Handler;
import android.util.SparseArray;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("media::midi")
class UsbMidiDeviceAndroid {
    static final int MIDI_SUBCLASS = 3;
    static final int REQUEST_GET_DESCRIPTOR = 6;
    static final int STRING_DESCRIPTOR_TYPE = 3;
    private final UsbDeviceConnection mConnection;
    private final SparseArray<UsbEndpoint> mEndpointMap = new SparseArray();
    private final Handler mHandler = new Handler();
    private boolean mHasInputThread;
    private boolean mIsClosed;
    private long mNativePointer;
    private final Map<UsbEndpoint, UsbRequest> mRequestMap = new HashMap();
    private UsbDevice mUsbDevice;

    private static native void nativeOnData(long j, int i, byte[] bArr);

    UsbMidiDeviceAndroid(UsbManager manager, UsbDevice device) {
        this.mConnection = manager.openDevice(device);
        this.mUsbDevice = device;
        this.mIsClosed = false;
        this.mHasInputThread = false;
        this.mNativePointer = 0;
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface iface = device.getInterface(i);
            if (iface.getInterfaceClass() == 1 && iface.getInterfaceSubclass() == 3) {
                this.mConnection.claimInterface(iface, true);
                for (int j = 0; j < iface.getEndpointCount(); j++) {
                    UsbEndpoint endpoint = iface.getEndpoint(j);
                    if (endpoint.getDirection() == 0) {
                        this.mEndpointMap.put(endpoint.getEndpointNumber(), endpoint);
                    }
                }
            }
        }
        startListen(device);
    }

    private void startListen(UsbDevice device) {
        final Map<UsbEndpoint, ByteBuffer> bufferForEndpoints = new HashMap();
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface iface = device.getInterface(i);
            if (iface.getInterfaceClass() == 1 && iface.getInterfaceSubclass() == 3) {
                for (int j = 0; j < iface.getEndpointCount(); j++) {
                    UsbEndpoint endpoint = iface.getEndpoint(j);
                    if (endpoint.getDirection() == 128) {
                        ByteBuffer buffer = ByteBuffer.allocate(endpoint.getMaxPacketSize());
                        UsbRequest request = new UsbRequest();
                        request.initialize(this.mConnection, endpoint);
                        request.queue(buffer, buffer.remaining());
                        bufferForEndpoints.put(endpoint, buffer);
                    }
                }
            }
        }
        if (!bufferForEndpoints.isEmpty()) {
            this.mHasInputThread = true;
            new Thread() {
                public void run() {
                    while (true) {
                        UsbRequest request = UsbMidiDeviceAndroid.this.mConnection.requestWait();
                        if (request != null) {
                            UsbEndpoint endpoint = request.getEndpoint();
                            if (endpoint.getDirection() == 128) {
                                ByteBuffer buffer = (ByteBuffer) bufferForEndpoints.get(endpoint);
                                int length = UsbMidiDeviceAndroid.getInputDataLength(buffer);
                                if (length > 0) {
                                    buffer.rewind();
                                    byte[] bs = new byte[length];
                                    buffer.get(bs, 0, length);
                                    UsbMidiDeviceAndroid.this.postOnDataEvent(endpoint.getEndpointNumber(), bs);
                                }
                                buffer.rewind();
                                request.queue(buffer, buffer.capacity());
                            }
                        } else {
                            return;
                        }
                    }
                }
            }.start();
        }
    }

    private void postOnDataEvent(final int endpointNumber, final byte[] bs) {
        this.mHandler.post(new Runnable() {
            public void run() {
                if (!UsbMidiDeviceAndroid.this.mIsClosed) {
                    UsbMidiDeviceAndroid.nativeOnData(UsbMidiDeviceAndroid.this.mNativePointer, endpointNumber, bs);
                }
            }
        });
    }

    UsbDevice getUsbDevice() {
        return this.mUsbDevice;
    }

    boolean isClosed() {
        return this.mIsClosed;
    }

    @CalledByNative
    void registerSelf(long nativePointer) {
        this.mNativePointer = nativePointer;
    }

    @TargetApi(18)
    @CalledByNative
    void send(int endpointNumber, byte[] bs) {
        if (!this.mIsClosed) {
            UsbEndpoint endpoint = (UsbEndpoint) this.mEndpointMap.get(endpointNumber);
            if (endpoint == null) {
                return;
            }
            if (shouldUseBulkTransfer()) {
                this.mConnection.bulkTransfer(endpoint, bs, bs.length, 100);
                return;
            }
            UsbRequest request = (UsbRequest) this.mRequestMap.get(endpoint);
            if (request == null) {
                request = new UsbRequest();
                request.initialize(this.mConnection, endpoint);
                this.mRequestMap.put(endpoint, request);
            }
            request.queue(ByteBuffer.wrap(bs), bs.length);
        }
    }

    private boolean shouldUseBulkTransfer() {
        return this.mHasInputThread;
    }

    @CalledByNative
    byte[] getDescriptors() {
        if (this.mConnection == null) {
            return new byte[0];
        }
        return this.mConnection.getRawDescriptors();
    }

    @CalledByNative
    byte[] getStringDescriptor(int index) {
        if (this.mConnection == null) {
            return new byte[0];
        }
        byte[] buffer = new byte[255];
        int read = this.mConnection.controlTransfer(128, 6, index | 768, 0, buffer, buffer.length, 0);
        if (read < 0) {
            return new byte[0];
        }
        return Arrays.copyOf(buffer, read);
    }

    @CalledByNative
    void close() {
        this.mEndpointMap.clear();
        for (UsbRequest request : this.mRequestMap.values()) {
            request.close();
        }
        this.mRequestMap.clear();
        this.mConnection.close();
        this.mNativePointer = 0;
        this.mIsClosed = true;
    }

    private static int getInputDataLength(ByteBuffer buffer) {
        int position = buffer.position();
        for (int i = 0; i < position; i += 4) {
            if (buffer.get(i) == (byte) 0) {
                return i;
            }
        }
        return position;
    }
}
