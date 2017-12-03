package org.chromium.mojom.device;

import org.chromium.mojo.bindings.BindingsHelper;
import org.chromium.mojo.bindings.DataHeader;
import org.chromium.mojo.bindings.Decoder;
import org.chromium.mojo.bindings.DeserializationException;
import org.chromium.mojo.bindings.Encoder;
import org.chromium.mojo.bindings.Interface.AbstractProxy;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.InterfaceControlMessagesHelper;
import org.chromium.mojo.bindings.Message;
import org.chromium.mojo.bindings.MessageHeader;
import org.chromium.mojo.bindings.MessageReceiver;
import org.chromium.mojo.bindings.MessageReceiverWithResponder;
import org.chromium.mojo.bindings.ServiceMessage;
import org.chromium.mojo.bindings.SideEffectFreeCloseable;
import org.chromium.mojo.bindings.Struct;
import org.chromium.mojo.system.Core;
import org.chromium.mojom.device.BatteryMonitor.QueryNextStatusResponse;

class BatteryMonitor_Internal {
    public static final Manager<BatteryMonitor, org.chromium.mojom.device.BatteryMonitor.Proxy> MANAGER = new C04541();
    private static final int QUERY_NEXT_STATUS_ORDINAL = 0;

    static class C04541 extends Manager<BatteryMonitor, org.chromium.mojom.device.BatteryMonitor.Proxy> {
        C04541() {
        }

        public String getName() {
            return "device::BatteryMonitor";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, BatteryMonitor impl) {
            return new Stub(core, impl);
        }

        public BatteryMonitor[] buildArray(int size) {
            return new BatteryMonitor[size];
        }
    }

    static final class BatteryMonitorQueryNextStatusParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        private static final int STRUCT_SIZE = 8;
        private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(8, 0)};

        private BatteryMonitorQueryNextStatusParams(int version) {
            super(8, version);
        }

        public BatteryMonitorQueryNextStatusParams() {
            this(0);
        }

        public static BatteryMonitorQueryNextStatusParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static BatteryMonitorQueryNextStatusParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            return new BatteryMonitorQueryNextStatusParams(decoder0.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
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

    static final class BatteryMonitorQueryNextStatusResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(16, 0)};
        public BatteryStatus status;

        private BatteryMonitorQueryNextStatusResponseParams(int version) {
            super(16, version);
        }

        public BatteryMonitorQueryNextStatusResponseParams() {
            this(0);
        }

        public static BatteryMonitorQueryNextStatusResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static BatteryMonitorQueryNextStatusResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            BatteryMonitorQueryNextStatusResponseParams result = new BatteryMonitorQueryNextStatusResponseParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.status = BatteryStatus.decode(decoder0.readPointer(8, false));
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.status, 8, false);
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
            if (BindingsHelper.equals(this.status, ((BatteryMonitorQueryNextStatusResponseParams) object).status)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.status);
        }
    }

    static class BatteryMonitorQueryNextStatusResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final QueryNextStatusResponse mCallback;

        BatteryMonitorQueryNextStatusResponseParamsForwardToCallback(QueryNextStatusResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                if (!messageWithHeader.getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call(BatteryMonitorQueryNextStatusResponseParams.deserialize(messageWithHeader.getPayload()).status);
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static class BatteryMonitorQueryNextStatusResponseParamsProxyToResponder implements QueryNextStatusResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        BatteryMonitorQueryNextStatusResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call(BatteryStatus status) {
            BatteryMonitorQueryNextStatusResponseParams _response = new BatteryMonitorQueryNextStatusResponseParams();
            _response.status = status;
            this.mMessageReceiver.accept(_response.serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<BatteryMonitor> {
        Stub(Core core, BatteryMonitor impl) {
            super(core, impl);
        }

        public boolean accept(Message message) {
            boolean z = false;
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (header.validateHeader(0)) {
                    switch (header.getType()) {
                        case -2:
                            z = InterfaceControlMessagesHelper.handleRunOrClosePipe(BatteryMonitor_Internal.MANAGER, messageWithHeader);
                            break;
                        default:
                            break;
                    }
                }
            } catch (DeserializationException e) {
                System.err.println(e.toString());
            }
            return z;
        }

        public boolean acceptWithResponder(Message message, MessageReceiver receiver) {
            try {
                ServiceMessage messageWithHeader = message.asServiceMessage();
                MessageHeader header = messageWithHeader.getHeader();
                if (!header.validateHeader(1)) {
                    return false;
                }
                switch (header.getType()) {
                    case -1:
                        return InterfaceControlMessagesHelper.handleRun(getCore(), BatteryMonitor_Internal.MANAGER, messageWithHeader, receiver);
                    case 0:
                        BatteryMonitorQueryNextStatusParams.deserialize(messageWithHeader.getPayload());
                        ((BatteryMonitor) getImpl()).queryNextStatus(new BatteryMonitorQueryNextStatusResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    default:
                        return false;
                }
            } catch (DeserializationException e) {
                System.err.println(e.toString());
                return false;
            }
        }
    }

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.device.BatteryMonitor.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void queryNextStatus(QueryNextStatusResponse callback) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new BatteryMonitorQueryNextStatusParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0)), new BatteryMonitorQueryNextStatusResponseParamsForwardToCallback(callback));
        }
    }

    BatteryMonitor_Internal() {
    }
}
