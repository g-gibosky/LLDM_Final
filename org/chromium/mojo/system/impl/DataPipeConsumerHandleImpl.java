package org.chromium.mojo.system.impl;

import java.nio.ByteBuffer;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.DataPipe.ReadFlags;
import org.chromium.mojo.system.ResultAnd;

class DataPipeConsumerHandleImpl extends HandleBase implements ConsumerHandle {
    DataPipeConsumerHandleImpl(CoreImpl core, int mojoHandle) {
        super(core, mojoHandle);
    }

    DataPipeConsumerHandleImpl(HandleBase other) {
        super(other);
    }

    public ConsumerHandle pass() {
        return new DataPipeConsumerHandleImpl(this);
    }

    public int discardData(int numBytes, ReadFlags flags) {
        return this.mCore.discardData(this, numBytes, flags);
    }

    public ResultAnd<Integer> readData(ByteBuffer elements, ReadFlags flags) {
        return this.mCore.readData(this, elements, flags);
    }

    public ByteBuffer beginReadData(int numBytes, ReadFlags flags) {
        return this.mCore.beginReadData(this, numBytes, flags);
    }

    public void endReadData(int numBytesRead) {
        this.mCore.endReadData(this, numBytesRead);
    }
}
