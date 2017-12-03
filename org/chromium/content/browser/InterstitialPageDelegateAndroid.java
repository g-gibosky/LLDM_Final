package org.chromium.content.browser;

import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("content")
public class InterstitialPageDelegateAndroid {
    private long mNativePtr;

    private native void nativeDontProceed(long j);

    private native long nativeInit(String str);

    private native void nativeProceed(long j);

    @VisibleForTesting
    public InterstitialPageDelegateAndroid(String htmlContent) {
        this.mNativePtr = nativeInit(htmlContent);
    }

    @VisibleForTesting
    public long getNative() {
        return this.mNativePtr;
    }

    @CalledByNative
    protected void onProceed() {
    }

    @CalledByNative
    protected void onDontProceed() {
    }

    @CalledByNative
    protected void commandReceived(String command) {
    }

    @CalledByNative
    private void onNativeDestroyed() {
        this.mNativePtr = 0;
    }

    protected void proceed() {
        if (this.mNativePtr != 0) {
            nativeProceed(this.mNativePtr);
        }
    }

    protected void dontProceed() {
        if (this.mNativePtr != 0) {
            nativeDontProceed(this.mNativePtr);
        }
    }
}
