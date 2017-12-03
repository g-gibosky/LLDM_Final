package org.chromium.mojo.bindings;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import org.chromium.mojo.system.AsyncWaiter;
import org.chromium.mojo.system.AsyncWaiter.Callback;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.Core.HandleSignals;
import org.chromium.mojo.system.MessagePipeHandle;
import org.chromium.mojo.system.MessagePipeHandle.CreateOptions;
import org.chromium.mojo.system.MessagePipeHandle.ReadFlags;
import org.chromium.mojo.system.MessagePipeHandle.WriteFlags;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojo.system.Pair;

class ExecutorFactory {
    private static final ThreadLocal<Executor> EXECUTORS = new ThreadLocal();
    private static final ByteBuffer NOTIFY_BUFFER = null;

    private static class PipedExecutor implements Executor, Callback {
        static final /* synthetic */ boolean $assertionsDisabled = (!ExecutorFactory.class.desiredAssertionStatus());
        private final Object mLock;
        private final List<Runnable> mPendingActions;
        private final MessagePipeHandle mReadHandle;
        private final AsyncWaiter mWaiter;
        private final MessagePipeHandle mWriteHandle;

        public PipedExecutor(Core core) {
            this.mWaiter = core.getDefaultAsyncWaiter();
            if ($assertionsDisabled || this.mWaiter != null) {
                this.mLock = new Object();
                Pair<MessagePipeHandle, MessagePipeHandle> handles = core.createMessagePipe(new CreateOptions());
                this.mReadHandle = (MessagePipeHandle) handles.first;
                this.mWriteHandle = (MessagePipeHandle) handles.second;
                this.mPendingActions = new ArrayList();
                asyncWait();
                return;
            }
            throw new AssertionError();
        }

        private void asyncWait() {
            this.mWaiter.asyncWait(this.mReadHandle, HandleSignals.READABLE, -1, this);
        }

        public void onResult(int result) {
            if (result == 0 && readNotifyBufferMessage()) {
                runNextAction();
            } else {
                close();
            }
        }

        public void onError(MojoException exception) {
            close();
        }

        private void close() {
            synchronized (this.mLock) {
                this.mWriteHandle.close();
                this.mPendingActions.clear();
            }
            this.mReadHandle.close();
        }

        private boolean readNotifyBufferMessage() {
            try {
                if (this.mReadHandle.readMessage(ExecutorFactory.NOTIFY_BUFFER, 0, ReadFlags.NONE).getMojoResult() != 0) {
                    return false;
                }
                asyncWait();
                return true;
            } catch (MojoException e) {
                return false;
            }
        }

        private void runNextAction() {
            Runnable toRun;
            synchronized (this.mLock) {
                toRun = (Runnable) this.mPendingActions.remove(0);
            }
            toRun.run();
        }

        public void execute(Runnable command) {
            synchronized (this.mLock) {
                if (this.mWriteHandle.isValid()) {
                    this.mPendingActions.add(command);
                    this.mWriteHandle.writeMessage(ExecutorFactory.NOTIFY_BUFFER, null, WriteFlags.NONE);
                } else {
                    throw new IllegalStateException("Trying to execute an action on a closed executor.");
                }
            }
        }
    }

    ExecutorFactory() {
    }

    public static Executor getExecutorForCurrentThread(Core core) {
        Executor executor = (Executor) EXECUTORS.get();
        if (executor != null) {
            return executor;
        }
        executor = new PipedExecutor(core);
        EXECUTORS.set(executor);
        return executor;
    }
}
