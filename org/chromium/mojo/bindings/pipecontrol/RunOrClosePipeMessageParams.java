package org.chromium.mojo.bindings.pipecontrol;

import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class RunOrClosePipeMessageParams extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    private static final int STRUCT_SIZE = 24;
    private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(24, 0)};
    public RunOrClosePipeInput input;

    private RunOrClosePipeMessageParams(int version) {
        super(24, version);
    }

    public RunOrClosePipeMessageParams() {
        this(0);
    }

    public static RunOrClosePipeMessageParams deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static RunOrClosePipeMessageParams decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        RunOrClosePipeMessageParams result = new RunOrClosePipeMessageParams(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.input = RunOrClosePipeInput.decode(decoder0, 8);
        return result;
    }

    protected final void encode(Encoder encoder) {
        encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.input, 8, false);
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
        if (BindingsHelper.equals(this.input, ((RunOrClosePipeMessageParams) object).input)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.input);
    }
}
