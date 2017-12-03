package org.xwalk.core.internal;

import android.content.Context;

class XWalkCoreBridge {
    private static final String BRIDGE_PACKAGE = "org.xwalk.core.internal";
    private static final String WRAPPER_PACKAGE = "org.xwalk.core";
    private static XWalkCoreBridge sInstance;
    private Context mBridgeContext;
    private ClassLoader mWrapperLoader;

    public static XWalkCoreBridge getInstance() {
        return sInstance;
    }

    public static void init(Context context, Object wrapper) {
        sInstance = new XWalkCoreBridge(context, wrapper);
    }

    private XWalkCoreBridge(Context context, Object wrapper) {
        this.mBridgeContext = context;
        this.mWrapperLoader = wrapper.getClass().getClassLoader();
        Class xwalkContent = getBridgeClass("XWalkContent");
        Class<?> javascriptInterface = getWrapperClass("JavascriptInterface");
        new ReflectMethod(xwalkContent, "setJavascriptInterfaceClass", javascriptInterface.getClass()).invoke(javascriptInterface);
    }

    public Context getContext() {
        return this.mBridgeContext;
    }

    public Object getBridgeObject(Object object) {
        try {
            return new ReflectMethod(object, "getBridge", new Class[0]).invoke(new Object[0]);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public Class<?> getWrapperClass(String name) {
        try {
            return this.mWrapperLoader.loadClass("org.xwalk.core." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public Class<?> getBridgeClass(String name) {
        try {
            return XWalkCoreBridge.class.getClassLoader().loadClass("org.xwalk.core.internal." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
