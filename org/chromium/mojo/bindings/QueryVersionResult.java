package org.chromium.mojo.bindings;

public final class QueryVersionResult extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    private static final int STRUCT_SIZE = 16;
    private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(16, 0)};
    public int version;

    private QueryVersionResult(int version) {
        super(16, version);
    }

    public QueryVersionResult() {
        this(0);
    }

    public static QueryVersionResult deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static QueryVersionResult decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        QueryVersionResult result = new QueryVersionResult(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.version = decoder0.readInt(8);
        return result;
    }

    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.version, 8);
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
        if (this.version != ((QueryVersionResult) object).version) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.version);
    }
}
