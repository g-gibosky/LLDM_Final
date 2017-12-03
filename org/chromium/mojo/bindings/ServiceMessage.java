package org.chromium.mojo.bindings;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ServiceMessage extends Message {
    static final /* synthetic */ boolean $assertionsDisabled = (!ServiceMessage.class.desiredAssertionStatus());
    private final MessageHeader mHeader;
    private Message mPayload;

    public ServiceMessage(Message baseMessage, MessageHeader header) {
        super(baseMessage.getData(), baseMessage.getHandles());
        if ($assertionsDisabled || header.equals(new MessageHeader(baseMessage))) {
            this.mHeader = header;
            return;
        }
        throw new AssertionError();
    }

    ServiceMessage(Message baseMessage) {
        this(baseMessage, new MessageHeader(baseMessage));
    }

    public ServiceMessage asServiceMessage() {
        return this;
    }

    public MessageHeader getHeader() {
        return this.mHeader;
    }

    public Message getPayload() {
        if (this.mPayload == null) {
            ByteBuffer truncatedBuffer = ((ByteBuffer) getData().position(getHeader().getSize())).slice();
            truncatedBuffer.order(ByteOrder.LITTLE_ENDIAN);
            this.mPayload = new Message(truncatedBuffer, getHandles());
        }
        return this.mPayload;
    }

    void setRequestId(long requestId) {
        this.mHeader.setRequestId(getData(), requestId);
    }
}
