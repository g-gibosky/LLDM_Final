package org.chromium.mojo.bindings;

public final class RunMessageParams extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    private static final int STRUCT_SIZE = 24;
    private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(24, 0)};
    public QueryVersion queryVersion;
    public int reserved0;
    public int reserved1;

    private RunMessageParams(int version) {
        super(24, version);
    }

    public RunMessageParams() {
        this(0);
    }

    public static RunMessageParams deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static RunMessageParams decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        RunMessageParams result = new RunMessageParams(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.reserved0 = decoder0.readInt(8);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.reserved1 = decoder0.readInt(12);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.queryVersion = QueryVersion.decode(decoder0.readPointer(16, false));
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.reserved0, 8);
        encoder0.encode(this.reserved1, 12);
        encoder0.encode(this.queryVersion, 16, false);
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
        RunMessageParams other = (RunMessageParams) object;
        if (this.reserved0 != other.reserved0) {
            return false;
        }
        if (this.reserved1 != other.reserved1) {
            return false;
        }
        if (BindingsHelper.equals(this.queryVersion, other.queryVersion)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.reserved0)) * 31) + BindingsHelper.hashCode(this.reserved1)) * 31) + BindingsHelper.hashCode(this.queryVersion);
    }
}
