package org.xwalk.core.internal;

public class CustomViewCallbackHandlerBridge extends CustomViewCallbackHandlerInternal {
    private XWalkCoreBridge coreBridge;
    private CustomViewCallbackHandlerInternal internal;
    private ReflectMethod onCustomViewHiddenMethod = new ReflectMethod(null, "onCustomViewHidden", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    CustomViewCallbackHandlerBridge(CustomViewCallbackHandlerInternal internal) {
        this.internal = internal;
        reflectionInit();
    }

    public void onCustomViewHidden() {
        if (this.onCustomViewHiddenMethod == null || this.onCustomViewHiddenMethod.isNull()) {
            onCustomViewHiddenSuper();
        } else {
            this.onCustomViewHiddenMethod.invoke(new Object[0]);
        }
    }

    public void onCustomViewHiddenSuper() {
        if (this.internal == null) {
            super.onCustomViewHidden();
        } else {
            this.internal.onCustomViewHidden();
        }
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            try {
                this.wrapper = new ReflectConstructor(this.coreBridge.getWrapperClass("CustomViewCallbackHandler"), Object.class).newInstance(this);
                this.onCustomViewHiddenMethod.init(this.wrapper, null, "onCustomViewHidden", new Class[0]);
            } catch (UnsupportedOperationException e) {
            }
        }
    }
}
