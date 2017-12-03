package org.xwalk.core.internal;

import org.xwalk.core.internal.XWalkHitTestResultInternal.type;

public class XWalkHitTestResultBridge extends XWalkHitTestResultInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod enumtypeClassValueOfMethod = new ReflectMethod();
    private ReflectMethod getExtraMethod = new ReflectMethod(null, "getExtra", new Class[0]);
    private ReflectMethod getTypeMethod = new ReflectMethod(null, "getType", new Class[0]);
    private XWalkHitTestResultInternal internal;
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    private Object Converttype(type type) {
        return this.enumtypeClassValueOfMethod.invoke(type.toString());
    }

    XWalkHitTestResultBridge(XWalkHitTestResultInternal internal) {
        this.internal = internal;
        reflectionInit();
    }

    public type getType() {
        if (this.getTypeMethod == null || this.getTypeMethod.isNull()) {
            return getTypeSuper();
        }
        return (type) this.getTypeMethod.invoke(new Object[0]);
    }

    public type getTypeSuper() {
        type ret;
        if (this.internal == null) {
            ret = super.getType();
        } else {
            ret = this.internal.getType();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public String getExtra() {
        if (this.getExtraMethod == null || this.getExtraMethod.isNull()) {
            return getExtraSuper();
        }
        return (String) this.getExtraMethod.invoke(new Object[0]);
    }

    public String getExtraSuper() {
        String ret;
        if (this.internal == null) {
            ret = super.getExtra();
        } else {
            ret = this.internal.getExtra();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            try {
                this.wrapper = new ReflectConstructor(this.coreBridge.getWrapperClass("XWalkHitTestResult"), Object.class).newInstance(this);
                this.enumtypeClassValueOfMethod.init(null, this.coreBridge.getWrapperClass("XWalkHitTestResult$type"), "valueOf", String.class);
                this.getTypeMethod.init(this.wrapper, null, "getType", new Class[0]);
                this.getExtraMethod.init(this.wrapper, null, "getExtra", new Class[0]);
            } catch (UnsupportedOperationException e) {
            }
        }
    }
}
