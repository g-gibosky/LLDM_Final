package org.xwalk.core.internal.extensions;

import android.util.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("xwalk::extensions")
public abstract class XWalkExtensionAndroid {
    private static final String TAG = "XWalkExtensionAndroid";
    private long mXWalkExtension;

    private native void nativeBroadcastMessage(long j, String str);

    private native void nativeDestroyExtension(long j);

    private native long nativeGetOrCreateExtension(String str, String str2, String[] strArr);

    private native void nativePostBinaryMessage(long j, int i, byte[] bArr);

    private native void nativePostMessage(long j, int i, String str);

    @CalledByNative
    public abstract void onMessage(int i, String str);

    @CalledByNative
    public abstract String onSyncMessage(int i, String str);

    public XWalkExtensionAndroid(String name, String jsApi) {
        this.mXWalkExtension = nativeGetOrCreateExtension(name, jsApi, null);
    }

    public XWalkExtensionAndroid(String name, String jsApi, String[] entryPoints) {
        this.mXWalkExtension = nativeGetOrCreateExtension(name, jsApi, entryPoints);
    }

    protected void destroyExtension() {
        if (this.mXWalkExtension == 0) {
            Log.e(TAG, "The extension to be destroyed is invalid!");
            return;
        }
        nativeDestroyExtension(this.mXWalkExtension);
        this.mXWalkExtension = 0;
    }

    public void postMessage(int instanceID, String message) {
        if (this.mXWalkExtension == 0) {
            Log.e(TAG, "Can not post a message to an invalid extension!");
        } else {
            nativePostMessage(this.mXWalkExtension, instanceID, message);
        }
    }

    public void postBinaryMessage(int instanceID, byte[] message) {
        if (this.mXWalkExtension == 0) {
            Log.e(TAG, "Can not post a binary message to an invalid extension!");
        } else {
            nativePostBinaryMessage(this.mXWalkExtension, instanceID, message);
        }
    }

    public void broadcastMessage(String message) {
        if (this.mXWalkExtension == 0) {
            Log.e(TAG, "Can not broadcast message to an invalid extension!");
        } else {
            nativeBroadcastMessage(this.mXWalkExtension, message);
        }
    }

    @CalledByNative
    public void onInstanceCreated(int instanceID) {
    }

    @CalledByNative
    public void onInstanceDestroyed(int instanceID) {
    }

    @CalledByNative
    public void onBinaryMessage(int instanceID, byte[] message) {
    }
}
