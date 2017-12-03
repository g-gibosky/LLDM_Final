package org.xwalk.core.internal;

import android.net.Uri;
import java.util.Map;

public class XWalkWebResourceRequestHandlerBridge extends XWalkWebResourceRequestHandlerInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod getMethodMethod = new ReflectMethod(null, "getMethod", new Class[0]);
    private ReflectMethod getRequestHeadersMethod = new ReflectMethod(null, "getRequestHeaders", new Class[0]);
    private ReflectMethod getUrlMethod = new ReflectMethod(null, "getUrl", new Class[0]);
    private ReflectMethod hasGestureMethod = new ReflectMethod(null, "hasGesture", new Class[0]);
    private XWalkWebResourceRequestHandlerInternal internal;
    private ReflectMethod isForMainFrameMethod = new ReflectMethod(null, "isForMainFrame", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    XWalkWebResourceRequestHandlerBridge(XWalkWebResourceRequestHandlerInternal internal) {
        this.internal = internal;
        reflectionInit();
    }

    public Uri getUrl() {
        if (this.getUrlMethod == null || this.getUrlMethod.isNull()) {
            return getUrlSuper();
        }
        return (Uri) this.getUrlMethod.invoke(new Object[0]);
    }

    public Uri getUrlSuper() {
        Uri ret;
        if (this.internal == null) {
            ret = super.getUrl();
        } else {
            ret = this.internal.getUrl();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public boolean isForMainFrame() {
        if (this.isForMainFrameMethod == null || this.isForMainFrameMethod.isNull()) {
            return isForMainFrameSuper();
        }
        return ((Boolean) this.isForMainFrameMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean isForMainFrameSuper() {
        if (this.internal == null) {
            return super.isForMainFrame();
        }
        return this.internal.isForMainFrame();
    }

    public boolean hasGesture() {
        if (this.hasGestureMethod == null || this.hasGestureMethod.isNull()) {
            return hasGestureSuper();
        }
        return ((Boolean) this.hasGestureMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean hasGestureSuper() {
        if (this.internal == null) {
            return super.hasGesture();
        }
        return this.internal.hasGesture();
    }

    public String getMethod() {
        if (this.getMethodMethod == null || this.getMethodMethod.isNull()) {
            return getMethodSuper();
        }
        return (String) this.getMethodMethod.invoke(new Object[0]);
    }

    public String getMethodSuper() {
        String ret;
        if (this.internal == null) {
            ret = super.getMethod();
        } else {
            ret = this.internal.getMethod();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public Map<String, String> getRequestHeaders() {
        if (this.getRequestHeadersMethod == null || this.getRequestHeadersMethod.isNull()) {
            return getRequestHeadersSuper();
        }
        return (Map) this.getRequestHeadersMethod.invoke(new Object[0]);
    }

    public Map<String, String> getRequestHeadersSuper() {
        Map<String, String> ret;
        if (this.internal == null) {
            ret = super.getRequestHeaders();
        } else {
            ret = this.internal.getRequestHeaders();
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
                this.wrapper = new ReflectConstructor(this.coreBridge.getWrapperClass("XWalkWebResourceRequestHandler"), Object.class).newInstance(this);
                this.getUrlMethod.init(this.wrapper, null, "getUrl", new Class[0]);
                this.isForMainFrameMethod.init(this.wrapper, null, "isForMainFrame", new Class[0]);
                this.hasGestureMethod.init(this.wrapper, null, "hasGesture", new Class[0]);
                this.getMethodMethod.init(this.wrapper, null, "getMethod", new Class[0]);
                this.getRequestHeadersMethod.init(this.wrapper, null, "getRequestHeaders", new Class[0]);
            } catch (UnsupportedOperationException e) {
            }
        }
    }
}
