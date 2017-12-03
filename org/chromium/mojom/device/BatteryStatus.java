package org.chromium.mojom.device;

import org.chromium.content.browser.MediaSessionDelegate;
import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Struct;

public final class BatteryStatus extends Struct {
    private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
    private static final int STRUCT_SIZE = 40;
    private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(40, 0)};
    public boolean charging;
    public double chargingTime;
    public double dischargingTime;
    public double level;

    private BatteryStatus(int version) {
        super(40, version);
        this.charging = true;
        this.chargingTime = 0.0d;
        this.dischargingTime = Double.POSITIVE_INFINITY;
        this.level = MediaSessionDelegate.DEFAULT_VOLUME_MULTIPLIER;
    }

    public BatteryStatus() {
        this(0);
    }

    public static BatteryStatus deserialize(Message message) {
        return decode(new Decoder(message));
    }

    public static BatteryStatus decode(Decoder decoder0) {
        if (decoder0 == null) {
            return null;
        }
        DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
        BatteryStatus result = new BatteryStatus(mainDataHeader.elementsOrVersion);
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.charging = decoder0.readBoolean(8, 0);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.chargingTime = decoder0.readDouble(16);
        }
        if (mainDataHeader.elementsOrVersion >= 0) {
            result.dischargingTime = decoder0.readDouble(24);
        }
        if (mainDataHeader.elementsOrVersion < 0) {
            return result;
        }
        result.level = decoder0.readDouble(32);
        return result;
    }

    protected final void encode(Encoder encoder) {
        Encoder encoder0 = encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO);
        encoder0.encode(this.charging, 8, 0);
        encoder0.encode(this.chargingTime, 16);
        encoder0.encode(this.dischargingTime, 24);
        encoder0.encode(this.level, 32);
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
        BatteryStatus other = (BatteryStatus) object;
        if (this.charging != other.charging) {
            return false;
        }
        if (this.chargingTime != other.chargingTime) {
            return false;
        }
        if (this.dischargingTime != other.dischargingTime) {
            return false;
        }
        if (this.level != other.level) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ((((((((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.charging)) * 31) + BindingsHelper.hashCode(this.chargingTime)) * 31) + BindingsHelper.hashCode(this.dischargingTime)) * 31) + BindingsHelper.hashCode(this.level);
    }
}
