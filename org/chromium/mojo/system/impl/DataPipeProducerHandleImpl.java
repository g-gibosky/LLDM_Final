package org.chromium.mojo.system.impl;

import java.nio.ByteBuffer;
import org.chromium.mojo.system.DataPipe.ProducerHandle;
import org.chromium.mojo.system.DataPipe.WriteFlags;
import org.chromium.mojo.system.ResultAnd;

class DataPipeProducerHandleImpl extends HandleBase implements ProducerHandle {
    DataPipeProducerHandleImpl(CoreImpl core, int mojoHandle) {
        super(core, mojoHandle);
    }

    DataPipeProducerHandleImpl(HandleBase handle) {
        super(handle);
    }

    public ProducerHandle pass() {
        return new DataPipeProducerHandleImpl(this);
    }

    public ResultAnd<Integer> writeData(ByteBuffer elements, WriteFlags flags) {
        return this.mCore.writeData(this, elements, flags);
    }

    public ByteBuffer beginWriteData(int numBytes, WriteFlags flags) {
        return this.mCore.beginWriteData(this, numBytes, flags);
    }

    public void endWriteData(int numBytesWritten) {
        this.mCore.endWriteData(this, numBytesWritten);
    }
}
