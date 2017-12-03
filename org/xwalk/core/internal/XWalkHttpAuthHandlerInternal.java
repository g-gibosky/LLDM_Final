package org.xwalk.core.internal;

import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("xwalk")
@XWalkAPI(createInternally = true, impl = XWalkHttpAuthInternal.class)
public class XWalkHttpAuthHandlerInternal implements XWalkHttpAuthInternal {
    private final boolean mFirstAttempt;
    private long mNativeXWalkHttpAuthHandler;

    private native void nativeCancel(long j);

    private native void nativeProceed(long j, String str, String str2);

    @XWalkAPI
    public void proceed(String username, String password) {
        if (this.mNativeXWalkHttpAuthHandler != 0) {
            nativeProceed(this.mNativeXWalkHttpAuthHandler, username, password);
            this.mNativeXWalkHttpAuthHandler = 0;
        }
    }

    @XWalkAPI
    public void cancel() {
        if (this.mNativeXWalkHttpAuthHandler != 0) {
            nativeCancel(this.mNativeXWalkHttpAuthHandler);
            this.mNativeXWalkHttpAuthHandler = 0;
        }
    }

    @XWalkAPI
    public boolean isFirstAttempt() {
        return this.mFirstAttempt;
    }

    @CalledByNative
    public static XWalkHttpAuthHandlerInternal create(long nativeXWalkAuthHandler, boolean firstAttempt) {
        return new XWalkHttpAuthHandlerInternal(nativeXWalkAuthHandler, firstAttempt);
    }

    public XWalkHttpAuthHandlerInternal(long nativeXWalkHttpAuthHandler, boolean firstAttempt) {
        this.mNativeXWalkHttpAuthHandler = nativeXWalkHttpAuthHandler;
        this.mFirstAttempt = firstAttempt;
    }

    XWalkHttpAuthHandlerInternal() {
        this.mNativeXWalkHttpAuthHandler = 0;
        this.mFirstAttempt = false;
    }

    @CalledByNative
    void handlerDestroyed() {
        this.mNativeXWalkHttpAuthHandler = 0;
    }
}
