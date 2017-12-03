package org.chromium.mojo.bindings;

import java.util.concurrent.Executor;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.MessagePipeHandle;

class AutoCloseableRouter implements Router {
    private boolean mClosed;
    private final Executor mExecutor;
    private final Router mRouter;

    class C02701 implements Runnable {
        C02701() {
        }

        public void run() {
            AutoCloseableRouter.this.close();
        }
    }

    public AutoCloseableRouter(Core core, Router router) {
        this.mRouter = router;
        this.mExecutor = ExecutorFactory.getExecutorForCurrentThread(core);
    }

    public void setIncomingMessageReceiver(MessageReceiverWithResponder incomingMessageReceiver) {
        this.mRouter.setIncomingMessageReceiver(incomingMessageReceiver);
    }

    public MessagePipeHandle passHandle() {
        return (MessagePipeHandle) this.mRouter.passHandle();
    }

    public boolean accept(Message message) {
        return this.mRouter.accept(message);
    }

    public boolean acceptWithResponder(Message message, MessageReceiver responder) {
        return this.mRouter.acceptWithResponder(message, responder);
    }

    public void start() {
        this.mRouter.start();
    }

    public void setErrorHandler(ConnectionErrorHandler errorHandler) {
        this.mRouter.setErrorHandler(errorHandler);
    }

    public void close() {
        this.mRouter.close();
        this.mClosed = true;
    }

    protected void finalize() throws Throwable {
        if (this.mClosed) {
            super.finalize();
        } else {
            this.mExecutor.execute(new C02701());
            throw new IllegalStateException("Warning: Router objects should be explicitly closed when no longer required otherwise you may leak handles.");
        }
    }
}
