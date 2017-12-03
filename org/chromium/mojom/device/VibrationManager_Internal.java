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
import org.chromium.mojom.device.VibrationManager.CancelResponse;
import org.chromium.mojom.device.VibrationManager.VibrateResponse;

class VibrationManager_Internal {
    private static final int CANCEL_ORDINAL = 1;
    public static final Manager<VibrationManager, org.chromium.mojom.device.VibrationManager.Proxy> MANAGER = new C04551();
    private static final int VIBRATE_ORDINAL = 0;

    static class C04551 extends Manager<VibrationManager, org.chromium.mojom.device.VibrationManager.Proxy> {
        C04551() {
        }

        public String getName() {
            return "device::VibrationManager";
        }

        public int getVersion() {
            return 0;
        }

        public Proxy buildProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            return new Proxy(core, messageReceiver);
        }

        public Stub buildStub(Core core, VibrationManager impl) {
            return new Stub(core, impl);
        }

        public VibrationManager[] buildArray(int size) {
            return new VibrationManager[size];
        }
    }

    static final class VibrationManagerCancelParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        private static final int STRUCT_SIZE = 8;
        private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(8, 0)};

        private VibrationManagerCancelParams(int version) {
            super(8, version);
        }

        public VibrationManagerCancelParams() {
            this(0);
        }

        public static VibrationManagerCancelParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static VibrationManagerCancelParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            return new VibrationManagerCancelParams(decoder0.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
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

    static final class VibrationManagerCancelResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        private static final int STRUCT_SIZE = 8;
        private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(8, 0)};

        private VibrationManagerCancelResponseParams(int version) {
            super(8, version);
        }

        public VibrationManagerCancelResponseParams() {
            this(0);
        }

        public static VibrationManagerCancelResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static VibrationManagerCancelResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            return new VibrationManagerCancelResponseParams(decoder0.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
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

    static class VibrationManagerCancelResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final CancelResponse mCallback;

        VibrationManagerCancelResponseParamsForwardToCallback(CancelResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(1, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static final class VibrationManagerVibrateParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        private static final int STRUCT_SIZE = 16;
        private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(16, 0)};
        public long milliseconds;

        private VibrationManagerVibrateParams(int version) {
            super(16, version);
        }

        public VibrationManagerVibrateParams() {
            this(0);
        }

        public static VibrationManagerVibrateParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static VibrationManagerVibrateParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            DataHeader mainDataHeader = decoder0.readAndValidateDataHeader(VERSION_ARRAY);
            VibrationManagerVibrateParams result = new VibrationManagerVibrateParams(mainDataHeader.elementsOrVersion);
            if (mainDataHeader.elementsOrVersion < 0) {
                return result;
            }
            result.milliseconds = decoder0.readLong(8);
            return result;
        }

        protected final void encode(Encoder encoder) {
            encoder.getEncoderAtDataOffset(DEFAULT_STRUCT_INFO).encode(this.milliseconds, 8);
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
            if (this.milliseconds != ((VibrationManagerVibrateParams) object).milliseconds) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return ((getClass().hashCode() + 31) * 31) + BindingsHelper.hashCode(this.milliseconds);
        }
    }

    static final class VibrationManagerVibrateResponseParams extends Struct {
        private static final DataHeader DEFAULT_STRUCT_INFO = VERSION_ARRAY[0];
        private static final int STRUCT_SIZE = 8;
        private static final DataHeader[] VERSION_ARRAY = new DataHeader[]{new DataHeader(8, 0)};

        private VibrationManagerVibrateResponseParams(int version) {
            super(8, version);
        }

        public VibrationManagerVibrateResponseParams() {
            this(0);
        }

        public static VibrationManagerVibrateResponseParams deserialize(Message message) {
            return decode(new Decoder(message));
        }

        public static VibrationManagerVibrateResponseParams decode(Decoder decoder0) {
            if (decoder0 == null) {
                return null;
            }
            return new VibrationManagerVibrateResponseParams(decoder0.readAndValidateDataHeader(VERSION_ARRAY).elementsOrVersion);
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

    static class VibrationManagerVibrateResponseParamsForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final VibrateResponse mCallback;

        VibrationManagerVibrateResponseParamsForwardToCallback(VibrateResponse callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            try {
                if (!message.asServiceMessage().getHeader().validateHeader(0, 2)) {
                    return false;
                }
                this.mCallback.call();
                return true;
            } catch (DeserializationException e) {
                return false;
            }
        }
    }

    static class VibrationManagerCancelResponseParamsProxyToResponder implements CancelResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VibrationManagerCancelResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call() {
            this.mMessageReceiver.accept(new VibrationManagerCancelResponseParams().serializeWithHeader(this.mCore, new MessageHeader(1, 2, this.mRequestId)));
        }
    }

    static class VibrationManagerVibrateResponseParamsProxyToResponder implements VibrateResponse {
        private final Core mCore;
        private final MessageReceiver mMessageReceiver;
        private final long mRequestId;

        VibrationManagerVibrateResponseParamsProxyToResponder(Core core, MessageReceiver messageReceiver, long requestId) {
            this.mCore = core;
            this.mMessageReceiver = messageReceiver;
            this.mRequestId = requestId;
        }

        public void call() {
            this.mMessageReceiver.accept(new VibrationManagerVibrateResponseParams().serializeWithHeader(this.mCore, new MessageHeader(0, 2, this.mRequestId)));
        }
    }

    static final class Stub extends org.chromium.mojo.bindings.Interface.Stub<VibrationManager> {
        Stub(Core core, VibrationManager impl) {
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
                            z = InterfaceControlMessagesHelper.handleRunOrClosePipe(VibrationManager_Internal.MANAGER, messageWithHeader);
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
                        return InterfaceControlMessagesHelper.handleRun(getCore(), VibrationManager_Internal.MANAGER, messageWithHeader, receiver);
                    case 0:
                        ((VibrationManager) getImpl()).vibrate(VibrationManagerVibrateParams.deserialize(messageWithHeader.getPayload()).milliseconds, new VibrationManagerVibrateResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
                        return true;
                    case 1:
                        VibrationManagerCancelParams.deserialize(messageWithHeader.getPayload());
                        ((VibrationManager) getImpl()).cancel(new VibrationManagerCancelResponseParamsProxyToResponder(getCore(), receiver, header.getRequestId()));
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

    static final class Proxy extends AbstractProxy implements org.chromium.mojom.device.VibrationManager.Proxy {
        Proxy(Core core, MessageReceiverWithResponder messageReceiver) {
            super(core, messageReceiver);
        }

        public void vibrate(long milliseconds, VibrateResponse callback) {
            VibrationManagerVibrateParams _message = new VibrationManagerVibrateParams();
            _message.milliseconds = milliseconds;
            getProxyHandler().getMessageReceiver().acceptWithResponder(_message.serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(0, 1, 0)), new VibrationManagerVibrateResponseParamsForwardToCallback(callback));
        }

        public void cancel(CancelResponse callback) {
            getProxyHandler().getMessageReceiver().acceptWithResponder(new VibrationManagerCancelParams().serializeWithHeader(getProxyHandler().getCore(), new MessageHeader(1, 1, 0)), new VibrationManagerCancelResponseParamsForwardToCallback(callback));
        }
    }

    VibrationManager_Internal() {
    }
}
