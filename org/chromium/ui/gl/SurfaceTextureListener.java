package org.chromium.ui.gl;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("gl")
class SurfaceTextureListener implements OnFrameAvailableListener {
    static final /* synthetic */ boolean $assertionsDisabled = (!SurfaceTextureListener.class.desiredAssertionStatus());
    private final long mNativeSurfaceTextureListener;

    private native void nativeDestroy(long j);

    private native void nativeFrameAvailable(long j);

    SurfaceTextureListener(long nativeSurfaceTextureListener) {
        if ($assertionsDisabled || nativeSurfaceTextureListener != 0) {
            this.mNativeSurfaceTextureListener = nativeSurfaceTextureListener;
            return;
        }
        throw new AssertionError();
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        nativeFrameAvailable(this.mNativeSurfaceTextureListener);
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestroy(this.mNativeSurfaceTextureListener);
        } finally {
            super.finalize();
        }
    }
}
