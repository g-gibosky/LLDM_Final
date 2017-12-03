package org.chromium.mojo.bindings;

import org.chromium.mojo.system.Core;

public abstract class Struct {
    private final int mEncodedBaseSize;
    private final int mVersion;

    protected abstract void encode(Encoder encoder);

    protected Struct(int encodedBaseSize, int version) {
        this.mEncodedBaseSize = encodedBaseSize;
        this.mVersion = version;
    }

    public int getVersion() {
        return this.mVersion;
    }

    public Message serialize(Core core) {
        Encoder encoder = new Encoder(core, this.mEncodedBaseSize);
        encode(encoder);
        return encoder.getMessage();
    }

    public ServiceMessage serializeWithHeader(Core core, MessageHeader header) {
        Encoder encoder = new Encoder(core, this.mEncodedBaseSize + header.getSize());
        header.encode(encoder);
        encode(encoder);
        return new ServiceMessage(encoder.getMessage(), header);
    }
}
