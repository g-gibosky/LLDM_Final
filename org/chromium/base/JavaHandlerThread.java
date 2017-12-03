package org.chromium.base;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("base::android")
class JavaHandlerThread {
    final HandlerThread mThread;

    private native void nativeInitializeThread(long j, long j2);

    private native void nativeStopThread(long j, long j2);

    private JavaHandlerThread(String name) {
        this.mThread = new HandlerThread(name);
    }

    @CalledByNative
    private static JavaHandlerThread create(String name) {
        return new JavaHandlerThread(name);
    }

    @CalledByNative
    private void start(long nativeThread, long nativeEvent) {
        this.mThread.start();
        final long j = nativeThread;
        final long j2 = nativeEvent;
        new Handler(this.mThread.getLooper()).post(new Runnable() {
            public void run() {
                JavaHandlerThread.this.nativeInitializeThread(j, j2);
            }
        });
    }

    @TargetApi(18)
    @CalledByNative
    private void stop(long nativeThread, long nativeEvent) {
        final boolean quitSafely = VERSION.SDK_INT >= 18;
        final long j = nativeThread;
        final long j2 = nativeEvent;
        new Handler(this.mThread.getLooper()).post(new Runnable() {
            public void run() {
                JavaHandlerThread.this.nativeStopThread(j, j2);
                if (!quitSafely) {
                    JavaHandlerThread.this.mThread.quit();
                }
            }
        });
        if (quitSafely) {
            this.mThread.quitSafely();
        }
    }
}
