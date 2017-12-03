package org.chromium.media.midi;

import android.annotation.TargetApi;
import android.media.midi.MidiDevice;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import java.io.IOException;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@TargetApi(23)
@JNINamespace("media::midi")
class MidiInputPortAndroid {
    private final MidiDevice mDevice;
    private final int mIndex;
    private long mNativeReceiverPointer;
    private MidiOutputPort mPort;

    class C02631 extends MidiReceiver {
        C02631() {
        }

        public void onSend(byte[] bs, int offset, int count, long timestamp) {
            MidiInputPortAndroid.nativeOnData(MidiInputPortAndroid.this.mNativeReceiverPointer, bs, offset, count, timestamp);
        }
    }

    private static native void nativeOnData(long j, byte[] bArr, int i, int i2, long j2);

    MidiInputPortAndroid(MidiDevice device, int index) {
        this.mDevice = device;
        this.mIndex = index;
    }

    @CalledByNative
    boolean open(long nativeReceiverPointer) {
        if (this.mPort != null) {
            return true;
        }
        this.mPort = this.mDevice.openOutputPort(this.mIndex);
        if (this.mPort == null) {
            return false;
        }
        this.mNativeReceiverPointer = nativeReceiverPointer;
        this.mPort.connect(new C02631());
        return true;
    }

    @CalledByNative
    void close() {
        if (this.mPort != null) {
            try {
                this.mPort.close();
            } catch (IOException e) {
            }
            this.mNativeReceiverPointer = 0;
            this.mPort = null;
        }
    }
}
