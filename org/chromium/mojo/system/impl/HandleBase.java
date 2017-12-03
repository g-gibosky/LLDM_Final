package org.chromium.mojo.system.impl;

import android.util.Log;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.Core.HandleSignals;
import org.chromium.mojo.system.Core.WaitResult;
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.UntypedHandle;

abstract class HandleBase implements Handle {
    private static final String TAG = "HandleImpl";
    protected CoreImpl mCore;
    private int mMojoHandle;

    HandleBase(CoreImpl core, int mojoHandle) {
        this.mCore = core;
        this.mMojoHandle = mojoHandle;
    }

    protected HandleBase(HandleBase other) {
        this.mCore = other.mCore;
        HandleBase otherAsHandleImpl = other;
        int mojoHandle = otherAsHandleImpl.mMojoHandle;
        otherAsHandleImpl.mMojoHandle = 0;
        this.mMojoHandle = mojoHandle;
    }

    public void close() {
        if (this.mMojoHandle != 0) {
            int handle = this.mMojoHandle;
            this.mMojoHandle = 0;
            this.mCore.close(handle);
        }
    }

    public WaitResult wait(HandleSignals signals, long deadline) {
        return this.mCore.wait(this, signals, deadline);
    }

    public boolean isValid() {
        return this.mMojoHandle != 0;
    }

    public UntypedHandle toUntypedHandle() {
        return new UntypedHandleImpl(this);
    }

    public Core getCore() {
        return this.mCore;
    }

    public int releaseNativeHandle() {
        int result = this.mMojoHandle;
        this.mMojoHandle = 0;
        return result;
    }

    int getMojoHandle() {
        return this.mMojoHandle;
    }

    void invalidateHandle() {
        this.mMojoHandle = 0;
    }

    protected final void finalize() throws Throwable {
        if (isValid()) {
            Log.w(TAG, "Handle was not closed.");
            this.mCore.closeWithResult(this.mMojoHandle);
        }
        super.finalize();
    }
}
