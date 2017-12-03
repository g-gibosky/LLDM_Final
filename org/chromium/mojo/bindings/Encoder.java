package org.chromium.mojo.bindings;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.Interface.Proxy;
import org.chromium.mojo.bindings.Interface.Proxy.Handler;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.Handle;
import org.chromium.mojo.system.MessagePipeHandle;
import org.chromium.mojo.system.Pair;

public class Encoder {
    private static final int INITIAL_BUFFER_SIZE = 1024;
    private int mBaseOffset;
    private final EncoderState mEncoderState;

    private static class EncoderState {
        static final /* synthetic */ boolean $assertionsDisabled = (!Encoder.class.desiredAssertionStatus());
        public ByteBuffer byteBuffer;
        public final Core core;
        public int dataEnd;
        public final List<Handle> handles;

        private EncoderState(Core core, int bufferSize) {
            this.handles = new ArrayList();
            if ($assertionsDisabled || bufferSize % 8 == 0) {
                this.core = core;
                if (bufferSize <= 0) {
                    bufferSize = 1024;
                }
                this.byteBuffer = ByteBuffer.allocateDirect(bufferSize);
                this.byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                this.dataEnd = 0;
                return;
            }
            throw new AssertionError();
        }

        public void claimMemory(int size) {
            this.dataEnd += size;
            growIfNeeded();
        }

        private void growIfNeeded() {
            if (this.byteBuffer.capacity() < this.dataEnd) {
                int targetSize = this.byteBuffer.capacity() * 2;
                while (targetSize < this.dataEnd) {
                    targetSize *= 2;
                }
                ByteBuffer newBuffer = ByteBuffer.allocateDirect(targetSize);
                newBuffer.order(ByteOrder.nativeOrder());
                this.byteBuffer.position(0);
                this.byteBuffer.limit(this.byteBuffer.capacity());
                newBuffer.put(this.byteBuffer);
                this.byteBuffer = newBuffer;
            }
        }
    }

    public Message getMessage() {
        this.mEncoderState.byteBuffer.position(0);
        this.mEncoderState.byteBuffer.limit(this.mEncoderState.dataEnd);
        return new Message(this.mEncoderState.byteBuffer, this.mEncoderState.handles);
    }

    public Encoder(Core core, int sizeHint) {
        this(new EncoderState(core, sizeHint));
    }

    private Encoder(EncoderState bufferInformation) {
        this.mEncoderState = bufferInformation;
        this.mBaseOffset = bufferInformation.dataEnd;
    }

    public Encoder getEncoderAtDataOffset(DataHeader dataHeader) {
        Encoder result = new Encoder(this.mEncoderState);
        result.encode(dataHeader);
        return result;
    }

    public void encode(DataHeader s) {
        this.mEncoderState.claimMemory(BindingsHelper.align(s.size));
        encode(s.size, 0);
        encode(s.elementsOrVersion, 4);
    }

    public void encode(byte v, int offset) {
        this.mEncoderState.byteBuffer.put(this.mBaseOffset + offset, v);
    }

    public void encode(boolean v, int offset, int bit) {
        if (v) {
            this.mEncoderState.byteBuffer.put(this.mBaseOffset + offset, (byte) (((byte) (1 << bit)) | this.mEncoderState.byteBuffer.get(this.mBaseOffset + offset)));
        }
    }

    public void encode(short v, int offset) {
        this.mEncoderState.byteBuffer.putShort(this.mBaseOffset + offset, v);
    }

    public void encode(int v, int offset) {
        this.mEncoderState.byteBuffer.putInt(this.mBaseOffset + offset, v);
    }

    public void encode(float v, int offset) {
        this.mEncoderState.byteBuffer.putFloat(this.mBaseOffset + offset, v);
    }

    public void encode(long v, int offset) {
        this.mEncoderState.byteBuffer.putLong(this.mBaseOffset + offset, v);
    }

    public void encode(double v, int offset) {
        this.mEncoderState.byteBuffer.putDouble(this.mBaseOffset + offset, v);
    }

    public void encode(Struct v, int offset, boolean nullable) {
        if (v == null) {
            encodeNullPointer(offset, nullable);
            return;
        }
        encodePointerToNextUnclaimedData(offset);
        v.encode(this);
    }

    public void encode(Union v, int offset, boolean nullable) {
        if (v == null && !nullable) {
            throw new SerializationException("Trying to encode a null pointer for a non-nullable type.");
        } else if (v == null) {
            encode(0, offset);
            encode(0, offset + 8);
        } else {
            v.encode(this, offset);
        }
    }

    public void encode(String v, int offset, boolean nullable) {
        if (v == null) {
            encodeNullPointer(offset, nullable);
        } else {
            encode(v.getBytes(Charset.forName("utf8")), offset, nullable ? 1 : 0, -1);
        }
    }

    public void encode(Handle v, int offset, boolean nullable) {
        if (v == null || !v.isValid()) {
            encodeInvalidHandle(offset, nullable);
            return;
        }
        encode(this.mEncoderState.handles.size(), offset);
        this.mEncoderState.handles.add(v);
    }

    public <T extends Interface> void encode(T v, int offset, boolean nullable, Manager<T, ?> manager) {
        if (v == null) {
            encodeInvalidHandle(offset, nullable);
            encode(0, offset + 4);
        } else if (this.mEncoderState.core == null) {
            throw new UnsupportedOperationException("The encoder has been created without a Core. It can't encode an interface.");
        } else if (v instanceof Proxy) {
            Handler handler = ((Proxy) v).getProxyHandler();
            encode(handler.passHandle(), offset, nullable);
            encode(handler.getVersion(), offset + 4);
        } else {
            Pair<MessagePipeHandle, MessagePipeHandle> handles = this.mEncoderState.core.createMessagePipe(null);
            manager.bind((Interface) v, (MessagePipeHandle) handles.first);
            encode((Handle) handles.second, offset, nullable);
            encode(manager.getVersion(), offset + 4);
        }
    }

    public <I extends Interface> void encode(InterfaceRequest<I> v, int offset, boolean nullable) {
        if (v == null) {
            encodeInvalidHandle(offset, nullable);
        } else if (this.mEncoderState.core == null) {
            throw new UnsupportedOperationException("The encoder has been created without a Core. It can't encode an interface.");
        } else {
            encode(v.passHandle(), offset, nullable);
        }
    }

    public void encode(AssociatedInterfaceNotSupported v, int offset, boolean nullable) {
    }

    public void encode(AssociatedInterfaceRequestNotSupported v, int offset, boolean nullable) {
    }

    public Encoder encodePointerArray(int length, int offset, int expectedLength) {
        return encoderForArray(8, length, offset, expectedLength);
    }

    public Encoder encodeUnionArray(int length, int offset, int expectedLength) {
        return encoderForArray(16, length, offset, expectedLength);
    }

    public void encode(boolean[] v, int offset, int arrayNullability, int expectedLength) {
        if (v == null) {
            encodeNullPointer(offset, BindingsHelper.isArrayNullable(arrayNullability));
        } else if (expectedLength == -1 || expectedLength == v.length) {
            byte[] bytes = new byte[((v.length + 7) / 8)];
            for (int i = 0; i < bytes.length; i++) {
                for (int j = 0; j < 8; j++) {
                    int booleanIndex = (i * 8) + j;
                    if (booleanIndex < v.length && v[booleanIndex]) {
                        bytes[i] = (byte) (bytes[i] | ((byte) (1 << j)));
                    }
                }
            }
            encodeByteArray(bytes, v.length, offset);
        } else {
            throw new SerializationException("Trying to encode a fixed array of incorrect length.");
        }
    }

    public void encode(byte[] v, int offset, int arrayNullability, int expectedLength) {
        if (v == null) {
            encodeNullPointer(offset, BindingsHelper.isArrayNullable(arrayNullability));
        } else if (expectedLength == -1 || expectedLength == v.length) {
            encodeByteArray(v, v.length, offset);
        } else {
            throw new SerializationException("Trying to encode a fixed array of incorrect length.");
        }
    }

    public void encode(short[] v, int offset, int arrayNullability, int expectedLength) {
        if (v == null) {
            encodeNullPointer(offset, BindingsHelper.isArrayNullable(arrayNullability));
        } else {
            encoderForArray(2, v.length, offset, expectedLength).append(v);
        }
    }

    public void encode(int[] v, int offset, int arrayNullability, int expectedLength) {
        if (v == null) {
            encodeNullPointer(offset, BindingsHelper.isArrayNullable(arrayNullability));
        } else {
            encoderForArray(4, v.length, offset, expectedLength).append(v);
        }
    }

    public void encode(float[] v, int offset, int arrayNullability, int expectedLength) {
        if (v == null) {
            encodeNullPointer(offset, BindingsHelper.isArrayNullable(arrayNullability));
        } else {
            encoderForArray(4, v.length, offset, expectedLength).append(v);
        }
    }

    public void encode(long[] v, int offset, int arrayNullability, int expectedLength) {
        if (v == null) {
            encodeNullPointer(offset, BindingsHelper.isArrayNullable(arrayNullability));
        } else {
            encoderForArray(8, v.length, offset, expectedLength).append(v);
        }
    }

    public void encode(double[] v, int offset, int arrayNullability, int expectedLength) {
        if (v == null) {
            encodeNullPointer(offset, BindingsHelper.isArrayNullable(arrayNullability));
        } else {
            encoderForArray(8, v.length, offset, expectedLength).append(v);
        }
    }

    public void encode(Handle[] v, int offset, int arrayNullability, int expectedLength) {
        if (v == null) {
            encodeNullPointer(offset, BindingsHelper.isArrayNullable(arrayNullability));
            return;
        }
        Encoder e = encoderForArray(4, v.length, offset, expectedLength);
        for (int i = 0; i < v.length; i++) {
            e.encode(v[i], (i * 4) + 8, BindingsHelper.isElementNullable(arrayNullability));
        }
    }

    public <T extends Interface> void encode(T[] v, int offset, int arrayNullability, int expectedLength, Manager<T, ?> manager) {
        if (v == null) {
            encodeNullPointer(offset, BindingsHelper.isArrayNullable(arrayNullability));
            return;
        }
        Encoder e = encoderForArray(8, v.length, offset, expectedLength);
        for (int i = 0; i < v.length; i++) {
            e.encode(v[i], (i * 8) + 8, BindingsHelper.isElementNullable(arrayNullability), (Manager) manager);
        }
    }

    public Encoder encoderForMap(int offset) {
        encodePointerToNextUnclaimedData(offset);
        return getEncoderAtDataOffset(BindingsHelper.MAP_STRUCT_HEADER);
    }

    public Encoder encoderForUnionPointer(int offset) {
        encodePointerToNextUnclaimedData(offset);
        Encoder result = new Encoder(this.mEncoderState);
        result.mEncoderState.claimMemory(16);
        return result;
    }

    public <I extends Interface> void encode(InterfaceRequest<I>[] v, int offset, int arrayNullability, int expectedLength) {
        if (v == null) {
            encodeNullPointer(offset, BindingsHelper.isArrayNullable(arrayNullability));
            return;
        }
        Encoder e = encoderForArray(4, v.length, offset, expectedLength);
        for (int i = 0; i < v.length; i++) {
            e.encode(v[i], (i * 4) + 8, BindingsHelper.isElementNullable(arrayNullability));
        }
    }

    public void encode(AssociatedInterfaceNotSupported[] v, int offset, int arrayNullability, int expectedLength) {
    }

    public void encode(AssociatedInterfaceRequestNotSupported[] v, int offset, int arrayNullability, int expectedLength) {
    }

    public void encodeNullPointer(int offset, boolean nullable) {
        if (nullable) {
            this.mEncoderState.byteBuffer.putLong(this.mBaseOffset + offset, 0);
            return;
        }
        throw new SerializationException("Trying to encode a null pointer for a non-nullable type.");
    }

    public void encodeInvalidHandle(int offset, boolean nullable) {
        if (nullable) {
            this.mEncoderState.byteBuffer.putInt(this.mBaseOffset + offset, -1);
            return;
        }
        throw new SerializationException("Trying to encode an invalid handle for a non-nullable type.");
    }

    void claimMemory(int size) {
        this.mEncoderState.claimMemory(BindingsHelper.align(size));
    }

    private void encodePointerToNextUnclaimedData(int offset) {
        encode(((long) this.mEncoderState.dataEnd) - ((long) (this.mBaseOffset + offset)), offset);
    }

    private Encoder encoderForArray(int elementSizeInByte, int length, int offset, int expectedLength) {
        if (expectedLength == -1 || expectedLength == length) {
            return encoderForArrayByTotalSize(length * elementSizeInByte, length, offset);
        }
        throw new SerializationException("Trying to encode a fixed array of incorrect length.");
    }

    private Encoder encoderForArrayByTotalSize(int byteSize, int length, int offset) {
        encodePointerToNextUnclaimedData(offset);
        return getEncoderAtDataOffset(new DataHeader(byteSize + 8, length));
    }

    private void encodeByteArray(byte[] bytes, int length, int offset) {
        encoderForArrayByTotalSize(bytes.length, length, offset).append(bytes);
    }

    private void append(byte[] v) {
        this.mEncoderState.byteBuffer.position(this.mBaseOffset + 8);
        this.mEncoderState.byteBuffer.put(v);
    }

    private void append(short[] v) {
        this.mEncoderState.byteBuffer.position(this.mBaseOffset + 8);
        this.mEncoderState.byteBuffer.asShortBuffer().put(v);
    }

    private void append(int[] v) {
        this.mEncoderState.byteBuffer.position(this.mBaseOffset + 8);
        this.mEncoderState.byteBuffer.asIntBuffer().put(v);
    }

    private void append(float[] v) {
        this.mEncoderState.byteBuffer.position(this.mBaseOffset + 8);
        this.mEncoderState.byteBuffer.asFloatBuffer().put(v);
    }

    private void append(double[] v) {
        this.mEncoderState.byteBuffer.position(this.mBaseOffset + 8);
        this.mEncoderState.byteBuffer.asDoubleBuffer().put(v);
    }

    private void append(long[] v) {
        this.mEncoderState.byteBuffer.position(this.mBaseOffset + 8);
        this.mEncoderState.byteBuffer.asLongBuffer().put(v);
    }
}
