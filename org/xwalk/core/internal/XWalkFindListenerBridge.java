package org.xwalk.core.internal;

public class XWalkFindListenerBridge extends XWalkFindListenerInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod onFindResultReceivedintintbooleanMethod = new ReflectMethod(null, "onFindResultReceived", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkFindListenerBridge(Object wrapper) {
        this.wrapper = wrapper;
        reflectionInit();
    }

    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
        this.onFindResultReceivedintintbooleanMethod.invoke(Integer.valueOf(activeMatchOrdinal), Integer.valueOf(numberOfMatches), Boolean.valueOf(isDoneCounting));
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.onFindResultReceivedintintbooleanMethod.init(this.wrapper, null, "onFindResultReceived", Integer.TYPE, Integer.TYPE, Boolean.TYPE);
        }
    }
}
