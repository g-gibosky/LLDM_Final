package org.chromium.mojo.system;

import java.nio.ByteBuffer;
import java.util.List;

public interface MessagePipeHandle extends Handle {

    public static class CreateOptions {
        private CreateFlags mFlags = CreateFlags.NONE;

        public CreateFlags getFlags() {
            return this.mFlags;
        }
    }

    public static class ReadMessageResult {
        private List<UntypedHandle> mHandles;
        private int mHandlesCount;
        private int mMessageSize;

        public int getMessageSize() {
            return this.mMessageSize;
        }

        public void setMessageSize(int messageSize) {
            this.mMessageSize = messageSize;
        }

        public int getHandlesCount() {
            return this.mHandlesCount;
        }

        public void setHandlesCount(int handlesCount) {
            this.mHandlesCount = handlesCount;
        }

        public List<UntypedHandle> getHandles() {
            return this.mHandles;
        }

        public void setHandles(List<UntypedHandle> handles) {
            this.mHandles = handles;
        }
    }

    public static class CreateFlags extends Flags<CreateFlags> {
        private static final int FLAG_NONE = 0;
        public static final CreateFlags NONE = ((CreateFlags) none().immutable());

        protected CreateFlags(int flags) {
            super(flags);
        }

        public static CreateFlags none() {
            return new CreateFlags(0);
        }
    }

    public static class ReadFlags extends Flags<ReadFlags> {
        private static final int FLAG_MAY_DISCARD = 1;
        private static final int FLAG_NONE = 0;
        public static final ReadFlags NONE = ((ReadFlags) none().immutable());

        private ReadFlags(int flags) {
            super(flags);
        }

        public ReadFlags setMayDiscard(boolean mayDiscard) {
            return (ReadFlags) setFlag(1, mayDiscard);
        }

        public static ReadFlags none() {
            return new ReadFlags(0);
        }
    }

    public static class WriteFlags extends Flags<WriteFlags> {
        private static final int FLAG_NONE = 0;
        public static final WriteFlags NONE = ((WriteFlags) none().immutable());

        private WriteFlags(int flags) {
            super(flags);
        }

        public static WriteFlags none() {
            return new WriteFlags(0);
        }
    }

    MessagePipeHandle pass();

    ResultAnd<ReadMessageResult> readMessage(ByteBuffer byteBuffer, int i, ReadFlags readFlags);

    void writeMessage(ByteBuffer byteBuffer, List<? extends Handle> list, WriteFlags writeFlags);
}
