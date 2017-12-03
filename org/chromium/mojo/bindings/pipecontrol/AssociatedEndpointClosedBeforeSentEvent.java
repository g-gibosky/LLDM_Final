package org.chromium.mojo.bindings.pipecontrol;

import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class AssociatedEndpointClosedBeforeSentEvent extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    private static final int STRUCT_SIZE = 16;
    private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(16, 0)};
    public int id;

    private AssociatedEndpointClosedBeforeSentEvent(int version) {
        super(16, version);
    }

    public AssociatedEndpointClosedBeforeSentEvent() {
        this(0);
    }

    public static AssociatedEndpointClosedBeforeSentEvent deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static AssociatedEndpointClosedBeforeSentEvent decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        AssociatedEndpointClosedBeforeSentEvent result = new AssociatedEndpointClosedBeforeSentEvent(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.id = decoder0.readInt(8);
        return result;
    }

    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.id, 8);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        if (this.id != ((AssociatedEndpointClosedBeforeSentEvent) object).id) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.id);
    }
}
