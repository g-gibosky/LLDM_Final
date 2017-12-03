package org.chromium.mojo.bindings;

import java.nio.ByteBuffer;
import org.chromium.mojo.system.AsyncWaiter;
import org.chromium.mojo.system.AsyncWaiter.Callback;
import org.chromium.mojo.system.AsyncWaiter.Cancellable;
import org.chromium.mojo.system.Core.HandleSignals;
import org.chromium.mojo.system.MessagePipeHandle;
import org.chromium.mojo.system.MessagePipeHandle.ReadFlags;
import org.chromium.mojo.system.MessagePipeHandle.ReadMessageResult;
import org.chromium.mojo.system.MessagePipeHandle.WriteFlags;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojo.system.ResultAnd;

public class Connector implements MessageReceiver, HandleOwner<MessagePipeHandle> {
    static final /* synthetic */ boolean $assertionsDisabled = (!Connector.class.desiredAssertionStatus());
    private final AsyncWaiter mAsyncWaiter;
    private final AsyncWaiterCallback mAsyncWaiterCallback;
    private Cancellable mCancellable;
    private ConnectionErrorHandler mErrorHandler;
    private MessageReceiver mIncomingMessageReceiver;
    private final MessagePipeHandle mMessagePipeHandle;

    private class AsyncWaiterCallback implements Callback {
        private AsyncWaiterCallback() {
        }

        public void onResult(int result) {
            Connector.this.onAsyncWaiterResult(result);
        }

        public void onError(MojoException exception) {
            Connector.this.mCancellable = null;
            Connector.this.onError(exception);
        }
    }

    public Connector(MessagePipeHandle messagePipeHandle) {
        this(messagePipeHandle, BindingsHelper.getDefaultAsyncWaiterForHandle(messagePipeHandle));
    }

    public Connector(MessagePipeHandle messagePipeHandle, AsyncWaiter asyncWaiter) {
        this.mAsyncWaiterCallback = new AsyncWaiterCallback();
        this.mCancellable = null;
        this.mMessagePipeHandle = messagePipeHandle;
        this.mAsyncWaiter = asyncWaiter;
    }

    public void setIncomingMessageReceiver(MessageReceiver incomingMessageReceiver) {
        this.mIncomingMessageReceiver = incomingMessageReceiver;
    }

    public void setErrorHandler(ConnectionErrorHandler errorHandler) {
        this.mErrorHandler = errorHandler;
    }

    public void start() {
        if ($assertionsDisabled || this.mCancellable == null) {
            registerAsyncWaiterForRead();
            return;
        }
        throw new AssertionError();
    }

    public boolean accept(Message message) {
        try {
            this.mMessagePipeHandle.writeMessage(message.getData(), message.getHandles(), WriteFlags.NONE);
            return true;
        } catch (MojoException e) {
            onError(e);
            return false;
        }
    }

    public MessagePipeHandle passHandle() {
        cancelIfActive();
        MessagePipeHandle handle = this.mMessagePipeHandle.pass();
        if (this.mIncomingMessageReceiver != null) {
            this.mIncomingMessageReceiver.close();
        }
        return handle;
    }

    public void close() {
        cancelIfActive();
        this.mMessagePipeHandle.close();
        if (this.mIncomingMessageReceiver != null) {
            MessageReceiver incomingMessageReceiver = this.mIncomingMessageReceiver;
            this.mIncomingMessageReceiver = null;
            incomingMessageReceiver.close();
        }
    }

    private void onAsyncWaiterResult(int result) {
        this.mCancellable = null;
        if (result == 0) {
            readOutstandingMessages();
        } else {
            onError(new MojoException(result));
        }
    }

    private void onError(MojoException exception) {
        close();
        if (!$assertionsDisabled && this.mCancellable != null) {
            throw new AssertionError();
        } else if (this.mErrorHandler != null) {
            this.mErrorHandler.onConnectionError(exception);
        }
    }

    private void registerAsyncWaiterForRead() {
        if (!$assertionsDisabled && this.mCancellable != null) {
            throw new AssertionError();
        } else if (this.mAsyncWaiter != null) {
            this.mCancellable = this.mAsyncWaiter.asyncWait(this.mMessagePipeHandle, HandleSignals.READABLE, -1, this.mAsyncWaiterCallback);
        } else {
            onError(new MojoException(3));
        }
    }

    private void readOutstandingMessages() {
        ResultAnd<Boolean> result;
        do {
            try {
                result = readAndDispatchMessage(this.mMessagePipeHandle, this.mIncomingMessageReceiver);
            } catch (MojoException e) {
                onError(e);
                return;
            }
        } while (((Boolean) result.getValue()).booleanValue());
        if (result.getMojoResult() == 17) {
            registerAsyncWaiterForRead();
        } else {
            onError(new MojoException(result.getMojoResult()));
        }
    }

    private void cancelIfActive() {
        if (this.mCancellable != null) {
            this.mCancellable.cancel();
            this.mCancellable = null;
        }
    }

    static ResultAnd<Boolean> readAndDispatchMessage(MessagePipeHandle handle, MessageReceiver receiver) {
        ResultAnd<ReadMessageResult> result = handle.readMessage(null, 0, ReadFlags.NONE);
        if (result.getMojoResult() != 8) {
            return new ResultAnd(result.getMojoResult(), Boolean.valueOf(false));
        }
        ReadMessageResult readResult = (ReadMessageResult) result.getValue();
        if ($assertionsDisabled || readResult != null) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(readResult.getMessageSize());
            result = handle.readMessage(buffer, readResult.getHandlesCount(), ReadFlags.NONE);
            if (receiver == null || result.getMojoResult() != 0) {
                return new ResultAnd(result.getMojoResult(), Boolean.valueOf(false));
            }
            return new ResultAnd(result.getMojoResult(), Boolean.valueOf(receiver.accept(new Message(buffer, ((ReadMessageResult) result.getValue()).getHandles()))));
        }
        throw new AssertionError();
    }
}
