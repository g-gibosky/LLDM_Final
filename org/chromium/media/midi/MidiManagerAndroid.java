package org.chromium.media.midi;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiManager.DeviceCallback;
import android.media.midi.MidiManager.OnDeviceOpenedListener;
import android.os.Handler;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@TargetApi(23)
@JNINamespace("media::midi")
class MidiManagerAndroid {
    static final /* synthetic */ boolean $assertionsDisabled = (!MidiManagerAndroid.class.desiredAssertionStatus());
    private final List<MidiDeviceAndroid> mDevices = new ArrayList();
    private final Handler mHandler;
    private boolean mIsInitializing = true;
    private final MidiManager mManager;
    private final long mNativeManagerPointer;
    private final Set<MidiDeviceInfo> mPendingDevices = new HashSet();

    class C02641 extends DeviceCallback {
        C02641() {
        }

        public void onDeviceAdded(MidiDeviceInfo device) {
            MidiManagerAndroid.this.onDeviceAdded(device);
        }

        public void onDeviceRemoved(MidiDeviceInfo device) {
            MidiManagerAndroid.this.onDeviceRemoved(device);
        }
    }

    class C02652 implements Runnable {
        C02652() {
        }

        public void run() {
            if (MidiManagerAndroid.this.mPendingDevices.isEmpty() && MidiManagerAndroid.this.mIsInitializing) {
                MidiManagerAndroid.nativeOnInitialized(MidiManagerAndroid.this.mNativeManagerPointer, (MidiDeviceAndroid[]) MidiManagerAndroid.this.mDevices.toArray(new MidiDeviceAndroid[0]));
                MidiManagerAndroid.this.mIsInitializing = false;
            }
        }
    }

    static native void nativeOnAttached(long j, MidiDeviceAndroid midiDeviceAndroid);

    static native void nativeOnDetached(long j, MidiDeviceAndroid midiDeviceAndroid);

    static native void nativeOnInitialized(long j, MidiDeviceAndroid[] midiDeviceAndroidArr);

    @CalledByNative
    static MidiManagerAndroid create(Context context, long nativeManagerPointer) {
        return new MidiManagerAndroid(context, nativeManagerPointer);
    }

    MidiManagerAndroid(Context context, long nativeManagerPointer) {
        if ($assertionsDisabled || ThreadUtils.runningOnUiThread()) {
            this.mManager = (MidiManager) context.getSystemService("midi");
            this.mHandler = new Handler(ThreadUtils.getUiThreadLooper());
            this.mNativeManagerPointer = nativeManagerPointer;
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    void initialize() {
        this.mManager.registerDeviceCallback(new C02641(), this.mHandler);
        for (MidiDeviceInfo info : this.mManager.getDevices()) {
            this.mPendingDevices.add(info);
            openDevice(info);
        }
        this.mHandler.post(new C02652());
    }

    private void openDevice(final MidiDeviceInfo info) {
        this.mManager.openDevice(info, new OnDeviceOpenedListener() {
            public void onDeviceOpened(MidiDevice device) {
                MidiManagerAndroid.this.onDeviceOpened(device, info);
            }
        }, this.mHandler);
    }

    private void onDeviceAdded(MidiDeviceInfo info) {
        if (this.mIsInitializing) {
            this.mPendingDevices.add(info);
        }
        openDevice(info);
    }

    private void onDeviceRemoved(MidiDeviceInfo info) {
        for (MidiDeviceAndroid device : this.mDevices) {
            if (device.isOpen() && device.getInfo().getId() == info.getId()) {
                device.close();
                nativeOnDetached(this.mNativeManagerPointer, device);
            }
        }
    }

    private void onDeviceOpened(MidiDevice device, MidiDeviceInfo info) {
        this.mPendingDevices.remove(info);
        if (device != null) {
            MidiDeviceAndroid xdevice = new MidiDeviceAndroid(device);
            this.mDevices.add(xdevice);
            if (!this.mIsInitializing) {
                nativeOnAttached(this.mNativeManagerPointer, xdevice);
            }
        }
        if (this.mIsInitializing && this.mPendingDevices.isEmpty()) {
            nativeOnInitialized(this.mNativeManagerPointer, (MidiDeviceAndroid[]) this.mDevices.toArray(new MidiDeviceAndroid[0]));
            this.mIsInitializing = false;
        }
    }
}
