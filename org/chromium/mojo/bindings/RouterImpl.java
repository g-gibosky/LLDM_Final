package org.chromium.mojo.bindings;

import android.annotation.SuppressLint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import org.chromium.mojo.system.AsyncWaiter;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.MessagePipeHandle;

@SuppressLint({"UseSparseArrays"})
public class RouterImpl implements Router {
    static final /* synthetic */ boolean $assertionsDisabled = (!RouterImpl.class.desiredAssertionStatus());
    private final Connector mConnector;
    private final Executor mExecutor;
    private MessageReceiverWithResponder mIncomingMessageReceiver;
    private long mNextRequestId;
    private Map<Long, MessageReceiver> mResponders;

    class C02731 implements Runnable {
        C02731() {
        }

        public void run() {
            RouterImpl.this.close();
        }
    }

    private class HandleIncomingMessageThunk implements MessageReceiver {
        private HandleIncomingMessageThunk() {
        }

        public boolean accept(Message message) {
            return RouterImpl.this.handleIncomingMessage(message);
        }

        public void close() {
            RouterImpl.this.handleConnectorClose();
        }
    }

    class ResponderThunk implements MessageReceiver {
        private boolean mAcceptWasInvoked = false;

        ResponderThunk() {
        }

        public boolean accept(Message message) {
            this.mAcceptWasInvoked = true;
            return RouterImpl.this.accept(message);
        }

        public void close() {
            RouterImpl.this.close();
        }

        protected void finalize() throws Throwable {
            if (!this.mAcceptWasInvoked) {
                RouterImpl.this.closeOnHandleThread();
            }
            super.finalize();
        }
    }

    public RouterImpl(MessagePipeHandle messagePipeHandle) {
        this(messagePipeHandle, BindingsHelper.getDefaultAsyncWaiterForHandle(messagePipeHandle));
    }

    public RouterImpl(MessagePipeHandle messagePipeHandle, AsyncWaiter asyncWaiter) {
        this.mNextRequestId = 1;
        this.mResponders = new HashMap();
        this.mConnector = new Connector(messagePipeHandle, asyncWaiter);
        this.mConnector.setIncomingMessageReceiver(new HandleIncomingMessageThunk());
        Core core = messagePipeHandle.getCore();
        if (core != null) {
            this.mExecutor = ExecutorFactory.getExecutorForCurrentThread(core);
        } else {
            this.mExecutor = null;
        }
    }

    public void start() {
        this.mConnector.start();
    }

    public void setIncomingMessageReceiver(MessageReceiverWithResponder incomingMessageReceiver) {
        this.mIncomingMessageReceiver = incomingMessageReceiver;
    }

    public boolean accept(Message message) {
        return this.mConnector.accept(message);
    }

    public boolean acceptWithResponder(Message message, MessageReceiver responder) {
        ServiceMessage messageWithHeader = message.asServiceMessage();
        if ($assertionsDisabled || messageWithHeader.getHeader().hasFlag(1)) {
            long requestId = this.mNextRequestId;
            this.mNextRequestId = requestId + 1;
            if (requestId == 0) {
                requestId = this.mNextRequestId;
                this.mNextRequestId = requestId + 1;
            }
            if (this.mResponders.containsKey(Long.valueOf(requestId))) {
                throw new IllegalStateException("Unable to find a new request identifier.");
            }
            messageWithHeader.setRequestId(requestId);
            if (!this.mConnector.accept(messageWithHeader)) {
                return false;
            }
            this.mResponders.put(Long.valueOf(requestId), responder);
            return true;
        }
        throw new AssertionError();
    }

    public MessagePipeHandle passHandle() {
        return this.mConnector.passHandle();
    }

    public void close() {
        this.mConnector.close();
    }

    public void setErrorHandler(ConnectionErrorHandler errorHandler) {
        this.mConnector.setErrorHandler(errorHandler);
    }

    private boolean handleIncomingMessage(Message message) {
        MessageHeader header = message.asServiceMessage().getHeader();
        if (header.hasFlag(1)) {
            if (this.mIncomingMessageReceiver != null) {
                return this.mIncomingMessageReceiver.acceptWithResponder(message, new ResponderThunk());
            }
            close();
            return false;
        } else if (header.hasFlag(2)) {
            long requestId = header.getRequestId();
            MessageReceiver responder = (MessageReceiver) this.mResponders.get(Long.valueOf(requestId));
            if (responder == null) {
                return false;
            }
            this.mResponders.remove(Long.valueOf(requestId));
            return responder.accept(message);
        } else if (this.mIncomingMessageReceiver != null) {
            return this.mIncomingMessageReceiver.accept(message);
        } else {
            return false;
        }
    }

    private void handleConnectorClose() {
        if (this.mIncomingMessageReceiver != null) {
            this.mIncomingMessageReceiver.close();
        }
    }

    private void closeOnHandleThread() {
        if (this.mExecutor != null) {
            this.mExecutor.execute(new C02731());
        }
    }
}
