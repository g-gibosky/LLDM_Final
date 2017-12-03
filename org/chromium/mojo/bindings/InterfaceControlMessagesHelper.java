package org.chromium.mojo.bindings;

import org.chromium.mojo.bindings.Callbacks.Callback1;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.Interface.Proxy;
import org.chromium.mojo.system.Core;

public class InterfaceControlMessagesHelper {

    private static class RunResponseForwardToCallback extends SideEffectFreeCloseable implements MessageReceiver {
        private final Callback1<RunResponseMessageParams> mCallback;

        RunResponseForwardToCallback(Callback1<RunResponseMessageParams> callback) {
            this.mCallback = callback;
        }

        public boolean accept(Message message) {
            this.mCallback.call(RunResponseMessageParams.deserialize(message.asServiceMessage().getPayload()));
            return true;
        }
    }

    public static void sendRunMessage(Core core, MessageReceiverWithResponder receiver, RunMessageParams params, Callback1<RunResponseMessageParams> callback) {
        receiver.acceptWithResponder(params.serializeWithHeader(core, new MessageHeader(-1, 1, 0)), new RunResponseForwardToCallback(callback));
    }

    public static void sendRunOrClosePipeMessage(Core core, MessageReceiverWithResponder receiver, RunOrClosePipeMessageParams params) {
        receiver.accept(params.serializeWithHeader(core, new MessageHeader(-2)));
    }

    public static <I extends Interface, P extends Proxy> boolean handleRun(Core core, Manager<I, P> manager, ServiceMessage message, MessageReceiver responder) {
        RunResponseMessageParams response = new RunResponseMessageParams();
        response.reserved0 = 16;
        response.reserved1 = 0;
        response.queryVersionResult = new QueryVersionResult();
        response.queryVersionResult.version = manager.getVersion();
        return responder.accept(response.serializeWithHeader(core, new MessageHeader(-1, 2, message.getHeader().getRequestId())));
    }

    public static <I extends Interface, P extends Proxy> boolean handleRunOrClosePipe(Manager<I, P> manager, ServiceMessage message) {
        return RunOrClosePipeMessageParams.deserialize(message.getPayload()).requireVersion.version <= manager.getVersion();
    }
}
