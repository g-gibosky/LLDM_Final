package org.chromium.mojo.system.impl;

import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.mojo.system.RunLoop;

@JNINamespace("mojo::android")
class BaseRunLoop implements RunLoop {
    static final /* synthetic */ boolean $assertionsDisabled = (!BaseRunLoop.class.desiredAssertionStatus());
    private final CoreImpl mCore;
    private long mRunLoopID = nativeCreateBaseRunLoop();

    private native long nativeCreateBaseRunLoop();

    private native void nativeDeleteMessageLoop(long j);

    private native void nativePostDelayedTask(long j, Runnable runnable, long j2);

    private native void nativeQuit(long j);

    private native void nativeRun(long j);

    private native void nativeRunUntilIdle(long j);

    BaseRunLoop(CoreImpl core) {
        this.mCore = core;
    }

    public void run() {
        if ($assertionsDisabled || this.mRunLoopID != 0) {
            nativeRun(this.mRunLoopID);
            return;
        }
        throw new AssertionError("The run loop cannot run once closed");
    }

    public void runUntilIdle() {
        if ($assertionsDisabled || this.mRunLoopID != 0) {
            nativeRunUntilIdle(this.mRunLoopID);
            return;
        }
        throw new AssertionError("The run loop cannot run once closed");
    }

    public void quit() {
        if ($assertionsDisabled || this.mRunLoopID != 0) {
            nativeQuit(this.mRunLoopID);
            return;
        }
        throw new AssertionError("The run loop cannot be quitted run once closed");
    }

    public void postDelayedTask(Runnable runnable, long delay) {
        if ($assertionsDisabled || this.mRunLoopID != 0) {
            nativePostDelayedTask(this.mRunLoopID, runnable, delay);
            return;
        }
        throw new AssertionError("The run loop cannot run tasks once closed");
    }

    public void close() {
        if (this.mRunLoopID != 0) {
            if ($assertionsDisabled || this.mCore.getCurrentRunLoop() == this) {
                this.mCore.clearCurrentRunLoop();
                nativeDeleteMessageLoop(this.mRunLoopID);
                this.mRunLoopID = 0;
                return;
            }
            throw new AssertionError("Only the current run loop can be closed");
        }
    }

    @CalledByNative
    private static void runRunnable(Runnable runnable) {
        runnable.run();
    }
}
