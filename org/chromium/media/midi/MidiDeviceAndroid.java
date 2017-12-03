package org.chromium.media.midi;

import android.annotation.TargetApi;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@TargetApi(23)
@JNINamespace("media::midi")
class MidiDeviceAndroid {
    private final MidiDevice mDevice;
    private final MidiInputPortAndroid[] mInputPorts;
    private boolean mIsOpen = true;
    private final MidiOutputPortAndroid[] mOutputPorts;

    MidiDeviceAndroid(MidiDevice device) {
        int i;
        this.mDevice = device;
        this.mOutputPorts = new MidiOutputPortAndroid[getInfo().getInputPortCount()];
        for (i = 0; i < this.mOutputPorts.length; i++) {
            this.mOutputPorts[i] = new MidiOutputPortAndroid(device, i);
        }
        this.mInputPorts = new MidiInputPortAndroid[getInfo().getOutputPortCount()];
        for (i = 0; i < this.mInputPorts.length; i++) {
            this.mInputPorts[i] = new MidiInputPortAndroid(device, i);
        }
    }

    boolean isOpen() {
        return this.mIsOpen;
    }

    void close() {
        this.mIsOpen = false;
        for (MidiInputPortAndroid port : this.mInputPorts) {
            port.close();
        }
        for (MidiOutputPortAndroid port2 : this.mOutputPorts) {
            port2.close();
        }
    }

    MidiDevice getDevice() {
        return this.mDevice;
    }

    MidiDeviceInfo getInfo() {
        return this.mDevice.getInfo();
    }

    @CalledByNative
    String getManufacturer() {
        return getProperty("manufacturer");
    }

    @CalledByNative
    String getProduct() {
        return getProperty("product");
    }

    @CalledByNative
    String getVersion() {
        return getProperty("version");
    }

    @CalledByNative
    MidiInputPortAndroid[] getInputPorts() {
        return this.mInputPorts;
    }

    @CalledByNative
    MidiOutputPortAndroid[] getOutputPorts() {
        return this.mOutputPorts;
    }

    private String getProperty(String name) {
        return this.mDevice.getInfo().getProperties().getString(name);
    }
}
