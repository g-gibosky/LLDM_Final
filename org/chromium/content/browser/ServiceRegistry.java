package org.chromium.content.browser;

import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;
import org.chromium.mojo.bindings.Interface.Proxy;
import org.chromium.mojo.bindings.InterfaceRequest;
import org.chromium.mojo.system.Core;
import org.chromium.mojo.system.impl.CoreImpl;

@JNINamespace("content")
public class ServiceRegistry {
    private final Core mCore;
    private long mNativeServiceRegistryAndroid;

    public interface ImplementationFactory<I extends Interface> {
        I createImpl();
    }

    private native void nativeAddService(long j, Manager manager, ImplementationFactory implementationFactory, String str);

    private native void nativeConnectToRemoteService(long j, String str, int i);

    private native void nativeRemoveService(long j, String str);

    public <I extends Interface, P extends Proxy> void addService(Manager<I, P> manager, ImplementationFactory<I> factory) {
        nativeAddService(this.mNativeServiceRegistryAndroid, manager, factory, manager.getName());
    }

    <I extends Interface, P extends Proxy> void removeService(Manager<I, P> manager) {
        nativeRemoveService(this.mNativeServiceRegistryAndroid, manager.getName());
    }

    <I extends Interface, P extends Proxy> void connectToRemoteService(Manager<I, P> manager, InterfaceRequest<I> request) {
        nativeConnectToRemoteService(this.mNativeServiceRegistryAndroid, manager.getName(), request.passHandle().releaseNativeHandle());
    }

    private ServiceRegistry(long nativeServiceRegistryAndroid, Core core) {
        this.mNativeServiceRegistryAndroid = nativeServiceRegistryAndroid;
        this.mCore = core;
    }

    @CalledByNative
    private static ServiceRegistry create(long nativeServiceRegistryAndroid) {
        return new ServiceRegistry(nativeServiceRegistryAndroid, CoreImpl.getInstance());
    }

    @CalledByNative
    private void destroy() {
        this.mNativeServiceRegistryAndroid = 0;
    }

    @CalledByNative
    private void createImplAndAttach(int nativeHandle, Manager manager, ImplementationFactory factory) {
        manager.bind(factory.createImpl(), this.mCore.acquireNativeHandle(nativeHandle).toMessagePipeHandle());
    }
}
