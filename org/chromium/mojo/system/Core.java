package org.chromium.mojo.system;

import java.util.List;
import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.DataPipe.CreateOptions;
import org.chromium.mojo.system.DataPipe.ProducerHandle;

public interface Core {
    public static final long DEADLINE_INFINITE = -1;

    public static class HandleSignalsState {
        private final HandleSignals mSatisfiableSignals;
        private final HandleSignals mSatisfiedSignals;

        public HandleSignalsState(HandleSignals satisfiedSignals, HandleSignals satisfiableSignals) {
            this.mSatisfiedSignals = satisfiedSignals;
            this.mSatisfiableSignals = satisfiableSignals;
        }

        public HandleSignals getSatisfiedSignals() {
            return this.mSatisfiedSignals;
        }

        public HandleSignals getSatisfiableSignals() {
            return this.mSatisfiableSignals;
        }
    }

    public static class WaitManyResult {
        private int mHandleIndex;
        private int mMojoResult;
        private List<HandleSignalsState> mSignalStates;

        public int getMojoResult() {
            return this.mMojoResult;
        }

        public void setMojoResult(int mojoResult) {
            this.mMojoResult = mojoResult;
        }

        public int getHandleIndex() {
            return this.mHandleIndex;
        }

        public void setHandleIndex(int handleIndex) {
            this.mHandleIndex = handleIndex;
        }

        public List<HandleSignalsState> getSignalStates() {
            return this.mSignalStates;
        }

        public void setSignalStates(List<HandleSignalsState> signalStates) {
            this.mSignalStates = signalStates;
        }
    }

    public static class WaitResult {
        private HandleSignalsState mHandleSignalsState;
        private int mMojoResult;

        public int getMojoResult() {
            return this.mMojoResult;
        }

        public void setMojoResult(int mojoResult) {
            this.mMojoResult = mojoResult;
        }

        public HandleSignalsState getHandleSignalsState() {
            return this.mHandleSignalsState;
        }

        public void setHandleSignalsState(HandleSignalsState handleSignalsState) {
            this.mHandleSignalsState = handleSignalsState;
        }
    }

    public static class HandleSignals extends Flags<HandleSignals> {
        private static final int FLAG_NONE = 0;
        private static final int FLAG_PEER_CLOSED = 4;
        private static final int FLAG_READABLE = 1;
        private static final int FLAG_WRITABLE = 2;
        public static final HandleSignals NONE = ((HandleSignals) none().immutable());
        public static final HandleSignals READABLE = ((HandleSignals) none().setReadable(true).immutable());
        public static final HandleSignals WRITABLE = ((HandleSignals) none().setWritable(true).immutable());

        public HandleSignals(int signals) {
            super(signals);
        }

        public HandleSignals setReadable(boolean readable) {
            return (HandleSignals) setFlag(1, readable);
        }

        public HandleSignals setWritable(boolean writable) {
            return (HandleSignals) setFlag(2, writable);
        }

        public HandleSignals setPeerClosed(boolean peerClosed) {
            return (HandleSignals) setFlag(4, peerClosed);
        }

        public static HandleSignals none() {
            return new HandleSignals(0);
        }
    }

    UntypedHandle acquireNativeHandle(int i);

    Pair<ProducerHandle, ConsumerHandle> createDataPipe(CreateOptions createOptions);

    RunLoop createDefaultRunLoop();

    Pair<MessagePipeHandle, MessagePipeHandle> createMessagePipe(MessagePipeHandle.CreateOptions createOptions);

    SharedBufferHandle createSharedBuffer(SharedBufferHandle.CreateOptions createOptions, long j);

    RunLoop getCurrentRunLoop();

    AsyncWaiter getDefaultAsyncWaiter();

    long getTimeTicksNow();

    WaitResult wait(Handle handle, HandleSignals handleSignals, long j);

    WaitManyResult waitMany(List<Pair<Handle, HandleSignals>> list, long j);
}
