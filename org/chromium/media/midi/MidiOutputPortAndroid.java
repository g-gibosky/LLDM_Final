package org.chromium.media.midi;

import android.annotation.TargetApi;
import android.media.midi.MidiDevice;
import android.media.midi.MidiInputPort;
import java.io.IOException;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@TargetApi(23)
@JNINamespace("media::midi")
class MidiOutputPortAndroid {
    private static final String TAG = "media_midi";
    private final MidiDevice mDevice;
    private final int mIndex;
    private MidiInputPort mPort;

    MidiOutputPortAndroid(MidiDevice device, int index) {
        this.mDevice = device;
        this.mIndex = index;
    }

    @CalledByNative
    boolean open() {
        if (this.mPort != null) {
            return true;
        }
        this.mPort = this.mDevice.openInputPort(this.mIndex);
        if (this.mPort == null) {
            return false;
        }
        return true;
    }

    @CalledByNative
    void send(byte[] bs) {
        if (this.mPort != null) {
            try {
                this.mPort.send(bs, 0, bs.length);
            } catch (IOException e) {
                Log.m28e(TAG, "MidiOutputPortAndroid.send: " + e, new Object[0]);
            }
        }
    }

    @CalledByNative
    void close() {
        if (this.mPort != null) {
            try {
                this.mPort.close();
            } catch (IOException e) {
            }
            this.mPort = null;
        }
    }
}
