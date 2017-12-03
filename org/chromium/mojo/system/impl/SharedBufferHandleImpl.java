package org.chromium.mojo.system.impl;

import java.nio.ByteBuffer;
import org.chromium.mojo.system.SharedBufferHandle;
import org.chromium.mojo.system.SharedBufferHandle.DuplicateOptions;
import org.chromium.mojo.system.SharedBufferHandle.MapFlags;

class SharedBufferHandleImpl extends HandleBase implements SharedBufferHandle {
    SharedBufferHandleImpl(CoreImpl core, int mojoHandle) {
        super(core, mojoHandle);
    }

    SharedBufferHandleImpl(HandleBase handle) {
        super(handle);
    }

    public SharedBufferHandle pass() {
        return new SharedBufferHandleImpl(this);
    }

    public SharedBufferHandle duplicate(DuplicateOptions options) {
        return this.mCore.duplicate(this, options);
    }

    public ByteBuffer map(long offset, long numBytes, MapFlags flags) {
        return this.mCore.map(this, offset, numBytes, flags);
    }

    public void unmap(ByteBuffer buffer) {
        this.mCore.unmap(buffer);
    }
}
