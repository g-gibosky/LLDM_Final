package org.chromium.mojo.system;

import java.nio.ByteBuffer;
import java.util.List;
import org.chromium.mojo.system.Core.HandleSignals;
import org.chromium.mojo.system.Core.WaitResult;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.DataPipe.ProducerHandle;
import org.chromium.mojo.system.DataPipe.ReadFlags;
import org.chromium.mojo.system.DataPipe.WriteFlags;
import org.chromium.mojo.system.MessagePipeHandle.ReadMessageResult;
import org.chromium.mojo.system.SharedBufferHandle.DuplicateOptions;
import org.chromium.mojo.system.SharedBufferHandle.MapFlags;

public class InvalidHandle implements UntypedHandle, MessagePipeHandle, ConsumerHandle, ProducerHandle, SharedBufferHandle {
    public static final InvalidHandle INSTANCE = new InvalidHandle();

    private InvalidHandle() {
    }

    public void close() {
    }

    public WaitResult wait(HandleSignals signals, long deadline) {
        throw new MojoException(3);
    }

    public boolean isValid() {
        return false;
    }

    public Core getCore() {
        return null;
    }

    public InvalidHandle pass() {
        return this;
    }

    public UntypedHandle toUntypedHandle() {
        return this;
    }

    public int releaseNativeHandle() {
        return 0;
    }

    public MessagePipeHandle toMessagePipeHandle() {
        return this;
    }

    public ConsumerHandle toDataPipeConsumerHandle() {
        return this;
    }

    public ProducerHandle toDataPipeProducerHandle() {
        return this;
    }

    public SharedBufferHandle toSharedBufferHandle() {
        return this;
    }

    public SharedBufferHandle duplicate(DuplicateOptions options) {
        throw new MojoException(3);
    }

    public ByteBuffer map(long offset, long numBytes, MapFlags flags) {
        throw new MojoException(3);
    }

    public void unmap(ByteBuffer buffer) {
        throw new MojoException(3);
    }

    public ResultAnd<Integer> writeData(ByteBuffer elements, WriteFlags flags) {
        throw new MojoException(3);
    }

    public ByteBuffer beginWriteData(int numBytes, WriteFlags flags) {
        throw new MojoException(3);
    }

    public void endWriteData(int numBytesWritten) {
        throw new MojoException(3);
    }

    public int discardData(int numBytes, ReadFlags flags) {
        throw new MojoException(3);
    }

    public ResultAnd<Integer> readData(ByteBuffer elements, ReadFlags flags) {
        throw new MojoException(3);
    }

    public ByteBuffer beginReadData(int numBytes, ReadFlags flags) {
        throw new MojoException(3);
    }

    public void endReadData(int numBytesRead) {
        throw new MojoException(3);
    }

    public void writeMessage(ByteBuffer bytes, List<? extends Handle> list, MessagePipeHandle.WriteFlags flags) {
        throw new MojoException(3);
    }

    public ResultAnd<ReadMessageResult> readMessage(ByteBuffer bytes, int maxNumberOfHandles, MessagePipeHandle.ReadFlags flags) {
        throw new MojoException(3);
    }
}
