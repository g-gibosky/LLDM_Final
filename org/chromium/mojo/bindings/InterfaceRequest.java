package org.chromium.mojo.bindings;

import org.chromium.mojo.system.MessagePipeHandle;

public class InterfaceRequest<P extends Interface> implements HandleOwner<MessagePipeHandle> {
    private final MessagePipeHandle mHandle;

    InterfaceRequest(MessagePipeHandle handle) {
        this.mHandle = handle;
    }

    public MessagePipeHandle passHandle() {
        return this.mHandle.pass();
    }

    public void close() {
        this.mHandle.close();
    }

    public static InterfaceRequest asInterfaceRequestUnsafe(MessagePipeHandle handle) {
        return new InterfaceRequest(handle);
    }
}
