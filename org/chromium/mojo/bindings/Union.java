package org.chromium.mojo.bindings;

import org.chromium.mojo.system.Core;

public abstract class Union {
    protected abstract void encode(Encoder encoder, int i);

    public Message serialize(Core core) {
        Encoder encoder = new Encoder(core, 16);
        encoder.claimMemory(16);
        encode(encoder, 0);
        return encoder.getMessage();
    }
}
