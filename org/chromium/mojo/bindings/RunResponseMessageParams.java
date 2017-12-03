package org.chromium.mojo.bindings;

public final class RunResponseMessageParams extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    private static final int STRUCT_SIZE = 24;
    private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(24, 0)};
    public QueryVersionResult queryVersionResult;
    public int reserved0;
    public int reserved1;

    private RunResponseMessageParams(int version) {
        super(24, version);
    }

    public RunResponseMessageParams() {
        this(0);
    }

    public static RunResponseMessageParams deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static RunResponseMessageParams decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        RunResponseMessageParams result = new RunResponseMessageParams(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.reserved0 = decoder0.readInt(8);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.reserved1 = decoder0.readInt(12);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.queryVersionResult = QueryVersionResult.decode(decoder0.readPointer(16, false));
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.reserved0, 8);
        encoder0.encode(this.reserved1, 12);
        encoder0.encode(this.queryVersionResult, 16, false);
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
        RunResponseMessageParams other = (RunResponseMessageParams) object;
        if (this.reserved0 != other.reserved0) {
            return false;
        }
        if (this.reserved1 != other.reserved1) {
            return false;
        }
        if (BindingsHelper.equals(this.queryVersionResult, other.queryVersionResult)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.reserved0)) * 31) + BindingsHelper.hashCode(this.reserved1)) * 31) + BindingsHelper.hashCode(this.queryVersionResult);
    }
}
