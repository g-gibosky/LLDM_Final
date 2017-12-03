package org.chromium.mojo.bindings.pipecontrol;

import org.chromium.base.annotations.SuppressFBWarnings;
import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.Union;

public final class RunOrClosePipeInput extends Union {
    static final /* synthetic */ boolean $assertionsDisabled = (!RunOrClosePipeInput.class.desiredAssertionStatus());
    private AssociatedEndpointClosedBeforeSentEvent mAssociatedEndpointClosedBeforeSentEvent;
    private PeerAssociatedEndpointClosedEvent mPeerAssociatedEndpointClosedEvent;
    private int mTag_ = -1;

    public static final class Tag {
        public static final int AssociatedEndpointClosedBeforeSentEvent = 1;
        public static final int PeerAssociatedEndpointClosedEvent = 0;
    }

    public int which() {
        return this.mTag_;
    }

    public boolean isUnknown() {
        return this.mTag_ == -1;
    }

    @SuppressFBWarnings({"EI_EXPOSE_REP2"})
    public void setPeerAssociatedEndpointClosedEvent(PeerAssociatedEndpointClosedEvent peerAssociatedEndpointClosedEvent) {
        this.mTag_ = 0;
        this.mPeerAssociatedEndpointClosedEvent = peerAssociatedEndpointClosedEvent;
    }

    @SuppressFBWarnings({"EI_EXPOSE_REP"})
    public PeerAssociatedEndpointClosedEvent getPeerAssociatedEndpointClosedEvent() {
        if ($assertionsDisabled || this.mTag_ == 0) {
            return this.mPeerAssociatedEndpointClosedEvent;
        }
        throw new AssertionError();
    }

    @SuppressFBWarnings({"EI_EXPOSE_REP2"})
    public void setAssociatedEndpointClosedBeforeSentEvent(AssociatedEndpointClosedBeforeSentEvent associatedEndpointClosedBeforeSentEvent) {
        this.mTag_ = 1;
        this.mAssociatedEndpointClosedBeforeSentEvent = associatedEndpointClosedBeforeSentEvent;
    }

    @SuppressFBWarnings({"EI_EXPOSE_REP"})
    public AssociatedEndpointClosedBeforeSentEvent getAssociatedEndpointClosedBeforeSentEvent() {
        if ($assertionsDisabled || this.mTag_ == 1) {
            return this.mAssociatedEndpointClosedBeforeSentEvent;
        }
        throw new AssertionError();
    }

    protected final void encode(Encoder encoder0, int offset) {
        encoder0.encode(16, offset);
        encoder0.encode(this.mTag_, offset + 4);
        switch (this.mTag_) {
            case 0:
                encoder0.encode(this.mPeerAssociatedEndpointClosedEvent, offset + 8, false);
                return;
            case 1:
                encoder0.encode(this.mAssociatedEndpointClosedBeforeSentEvent, offset + 8, false);
                return;
            default:
                return;
        }
    }

    public static RunOrClosePipeInput deserialize(Message message) {
        return decode(new Decoder(message).decoderForSerializedUnion(), 0);
    }

    public static final RunOrClosePipeInput decode(Decoder decoder0, int offset) {
        DataHeader dataHeader = decoder0.readDataHeaderForUnion(offset);
        if (dataHeader.size == 0) {
            return null;
        }
        RunOrClosePipeInput result = new RunOrClosePipeInput();
        switch (dataHeader.elementsOrVersion) {
            case 0:
                result.mPeerAssociatedEndpointClosedEvent = PeerAssociatedEndpointClosedEvent.decode(decoder0.readPointer(offset + 8, false));
                result.mTag_ = 0;
                return result;
            case 1:
                result.mAssociatedEndpointClosedBeforeSentEvent = AssociatedEndpointClosedBeforeSentEvent.decode(decoder0.readPointer(offset + 8, false));
                result.mTag_ = 1;
                return result;
            default:
                return result;
        }
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        RunOrClosePipeInput other = (RunOrClosePipeInput) object;
        if (this.mTag_ != other.mTag_) {
            return false;
        }
        switch (this.mTag_) {
            case 0:
                return BindingsHelper.equals(this.mPeerAssociatedEndpointClosedEvent, other.mPeerAssociatedEndpointClosedEvent);
            case 1:
                return BindingsHelper.equals(this.mAssociatedEndpointClosedBeforeSentEvent, other.mAssociatedEndpointClosedBeforeSentEvent);
            default:
                return false;
        }
    }

    public int hashCode() {
        int result = ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.mTag_);
        switch (this.mTag_) {
            case 0:
                return (result * 31) + BindingsHelper.hashCode(this.mPeerAssociatedEndpointClosedEvent);
            case 1:
                return (result * 31) + BindingsHelper.hashCode(this.mAssociatedEndpointClosedBeforeSentEvent);
            default:
                return result;
        }
    }
}
