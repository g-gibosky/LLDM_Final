package org.chromium.mojo.bindings;

public final class QueryVersion extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    private static final int STRUCT_SIZE = 8;
    private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(8, 0)};

    private QueryVersion(int version) {
        super(8, version);
    }

    public QueryVersion() {
        this(0);
    }

    public static QueryVersion deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static QueryVersion decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        return new QueryVersion(decoder0.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
    }

    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
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
        return true;
    }

    public int hashCode() {
        return getClass().hashCode() + 31;
    }
}
