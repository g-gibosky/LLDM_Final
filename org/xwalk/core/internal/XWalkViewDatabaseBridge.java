package org.xwalk.core.internal;

public class XWalkViewDatabaseBridge extends XWalkViewDatabaseInternal {
    private XWalkCoreBridge coreBridge;
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public static boolean hasFormData() {
        return XWalkViewDatabaseInternal.hasFormData();
    }

    public static void clearFormData() {
        XWalkViewDatabaseInternal.clearFormData();
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
        }
    }
}
