package org.xwalk.core.internal;

public class XWalkNativeExtensionLoaderBridge extends XWalkNativeExtensionLoaderInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod registerNativeExtensionsInPathStringMethod = new ReflectMethod(null, "registerNativeExtensionsInPath", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkNativeExtensionLoaderBridge(Object wrapper) {
        this.wrapper = wrapper;
        reflectionInit();
    }

    public void registerNativeExtensionsInPath(String path) {
        if (this.registerNativeExtensionsInPathStringMethod == null || this.registerNativeExtensionsInPathStringMethod.isNull()) {
            registerNativeExtensionsInPathSuper(path);
            return;
        }
        this.registerNativeExtensionsInPathStringMethod.invoke(path);
    }

    public void registerNativeExtensionsInPathSuper(String path) {
        super.registerNativeExtensionsInPath(path);
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.registerNativeExtensionsInPathStringMethod.init(this.wrapper, null, "registerNativeExtensionsInPath", String.class);
        }
    }
}
