package org.chromium.mojo.system.impl;

import java.nio.ByteBuffer;
import java.util.List;
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.MessagePipeHandle;
import org.chromium.mojo.system.MessagePipeHandle.ReadFlags;
import org.chromium.mojo.system.MessagePipeHandle.ReadMessageResult;
import org.chromium.mojo.system.MessagePipeHandle.WriteFlags;
import org.chromium.mojo.system.ResultAnd;

class MessagePipeHandleImpl extends HandleBase implements MessagePipeHandle {
    MessagePipeHandleImpl(CoreImpl core, int mojoHandle) {
        super(core, mojoHandle);
    }

    MessagePipeHandleImpl(HandleBase handle) {
        super(handle);
    }

    public MessagePipeHandle pass() {
        return new MessagePipeHandleImpl(this);
    }

    public void writeMessage(ByteBuffer bytes, List<? extends Handle> handles, WriteFlags flags) {
        this.mCore.writeMessage(this, bytes, handles, flags);
    }

    public ResultAnd<ReadMessageResult> readMessage(ByteBuffer bytes, int maxNumberOfHandles, ReadFlags flags) {
        return this.mCore.readMessage(this, bytes, maxNumberOfHandles, flags);
    }
}
