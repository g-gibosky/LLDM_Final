package org.chromium.mojo.system.impl;

import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.DataPipe.ProducerHandle;
import org.chromium.mojo.system.MessagePipeHandle;
import org.chromium.mojo.system.SharedBufferHandle;
import org.chromium.mojo.system.UntypedHandle;

class UntypedHandleImpl extends HandleBase implements UntypedHandle {
    UntypedHandleImpl(CoreImpl core, int mojoHandle) {
        super(core, mojoHandle);
    }

    UntypedHandleImpl(HandleBase handle) {
        super(handle);
    }

    public UntypedHandle pass() {
        return new UntypedHandleImpl(this);
    }

    public MessagePipeHandle toMessagePipeHandle() {
        return new MessagePipeHandleImpl(this);
    }

    public ConsumerHandle toDataPipeConsumerHandle() {
        return new DataPipeConsumerHandleImpl(this);
    }

    public ProducerHandle toDataPipeProducerHandle() {
        return new DataPipeProducerHandleImpl(this);
    }

    public SharedBufferHandle toSharedBufferHandle() {
        return new SharedBufferHandleImpl(this);
    }
}
