package org.chromium.mojo.bindings;

import java.io.Closeable;
import org.chromium.mojo.bindings.Callbacks.Callback1;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.MessagePipeHandle;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojo.system.Pair;

public interface Interface extends ConnectionErrorHandler, Closeable {

    public static abstract class Manager<I extends Interface, P extends Proxy> {
        protected abstract I[] buildArray(int i);

        protected abstract P buildProxy(Core core, MessageReceiverWithResponder messageReceiverWithResponder);

        protected abstract Stub<I> buildStub(Core core, I i);

        public abstract String getName();

        public abstract int getVersion();

        public void bind(I impl, MessagePipeHandle handle) {
            Router router = new RouterImpl(handle);
            bind(handle.getCore(), impl, router);
            router.start();
        }

        public final void bind(I impl, InterfaceRequest<I> request) {
            bind((Interface) impl, request.passHandle());
        }

        public final P attachProxy(MessagePipeHandle handle, int version) {
            Router router = new RouterImpl(handle);
            P proxy = attachProxy(handle.getCore(), router);
            DelegatingConnectionErrorHandler handlers = new DelegatingConnectionErrorHandler();
            handlers.addConnectionErrorHandler(proxy);
            router.setErrorHandler(handlers);
            router.start();
            ((HandlerImpl) proxy.getProxyHandler()).setVersion(version);
            return proxy;
        }

        public final Pair<P, InterfaceRequest<I>> getInterfaceRequest(Core core) {
            Pair<MessagePipeHandle, MessagePipeHandle> handles = core.createMessagePipe(null);
            return Pair.create(attachProxy((MessagePipeHandle) handles.first, 0), new InterfaceRequest((MessagePipeHandle) handles.second));
        }

        public final InterfaceRequest<I> asInterfaceRequest(MessagePipeHandle handle) {
            return new InterfaceRequest(handle);
        }

        final void bind(Core core, I impl, Router router) {
            router.setErrorHandler(impl);
            router.setIncomingMessageReceiver(buildStub(core, impl));
        }

        final P attachProxy(Core core, Router router) {
            return buildProxy(core, new AutoCloseableRouter(core, router));
        }
    }

    public interface Proxy extends Interface {

        public interface Handler extends Closeable {
            int getVersion();

            MessagePipeHandle passHandle();

            void queryVersion(Callback1<Integer> callback1);

            void requireVersion(int i);

            void setErrorHandler(ConnectionErrorHandler connectionErrorHandler);
        }

        Handler getProxyHandler();
    }

    public static abstract class Stub<I extends Interface> implements MessageReceiverWithResponder {
        private final Core mCore;
        private final I mImpl;

        public Stub(Core core, I impl) {
            this.mCore = core;
            this.mImpl = impl;
        }

        protected Core getCore() {
            return this.mCore;
        }

        protected I getImpl() {
            return this.mImpl;
        }

        public void close() {
            this.mImpl.close();
        }
    }

    public static abstract class AbstractProxy implements Proxy {
        private final HandlerImpl mHandler;

        protected static class HandlerImpl implements Handler, ConnectionErrorHandler {
            private final Core mCore;
            private ConnectionErrorHandler mErrorHandler = null;
            private final MessageReceiverWithResponder mMessageReceiver;
            private int mVersion = 0;

            protected HandlerImpl(Core core, MessageReceiverWithResponder messageReceiver) {
                this.mCore = core;
                this.mMessageReceiver = messageReceiver;
            }

            void setVersion(int version) {
                this.mVersion = version;
            }

            public MessageReceiverWithResponder getMessageReceiver() {
                return this.mMessageReceiver;
            }

            public Core getCore() {
                return this.mCore;
            }

            public void setErrorHandler(ConnectionErrorHandler errorHandler) {
                this.mErrorHandler = errorHandler;
            }

            public void onConnectionError(MojoException e) {
                if (this.mErrorHandler != null) {
                    this.mErrorHandler.onConnectionError(e);
                }
            }

            public void close() {
                this.mMessageReceiver.close();
            }

            public MessagePipeHandle passHandle() {
                return (MessagePipeHandle) this.mMessageReceiver.passHandle();
            }

            public int getVersion() {
                return this.mVersion;
            }

            public void queryVersion(final Callback1<Integer> callback) {
                RunMessageParams message = new RunMessageParams();
                message.reserved0 = 16;
                message.reserved1 = 0;
                message.queryVersion = new QueryVersion();
                InterfaceControlMessagesHelper.sendRunMessage(getCore(), this.mMessageReceiver, message, new Callback1<RunResponseMessageParams>() {
                    public void call(RunResponseMessageParams response) {
                        HandlerImpl.this.mVersion = response.queryVersionResult.version;
                        callback.call(Integer.valueOf(HandlerImpl.this.mVersion));
                    }
                });
            }

            public void requireVersion(int version) {
                if (this.mVersion < version) {
                    this.mVersion = version;
                    RunOrClosePipeMessageParams message = new RunOrClosePipeMessageParams();
                    message.reserved0 = 16;
                    message.reserved1 = 0;
                    message.requireVersion = new RequireVersion();
                    message.requireVersion.version = version;
                    InterfaceControlMessagesHelper.sendRunOrClosePipeMessage(getCore(), this.mMessageReceiver, message);
                }
            }
        }

        protected AbstractProxy(Core core, MessageReceiverWithResponder messageReceiver) {
            this.mHandler = new HandlerImpl(core, messageReceiver);
        }

        public void close() {
            this.mHandler.close();
        }

        public HandlerImpl getProxyHandler() {
            return this.mHandler;
        }

        public void onConnectionError(MojoException e) {
            this.mHandler.onConnectionError(e);
        }
    }

    void close();
}
