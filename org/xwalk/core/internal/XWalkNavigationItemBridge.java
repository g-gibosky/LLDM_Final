package org.xwalk.core.internal;

public class XWalkNavigationItemBridge extends XWalkNavigationItemInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod getOriginalUrlMethod = new ReflectMethod(null, "getOriginalUrl", new Class[0]);
    private ReflectMethod getTitleMethod = new ReflectMethod(null, "getTitle", new Class[0]);
    private ReflectMethod getUrlMethod = new ReflectMethod(null, "getUrl", new Class[0]);
    private XWalkNavigationItemInternal internal;
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    XWalkNavigationItemBridge(XWalkNavigationItemInternal internal) {
        this.internal = internal;
        reflectionInit();
    }

    public String getUrl() {
        if (this.getUrlMethod == null || this.getUrlMethod.isNull()) {
            return getUrlSuper();
        }
        return (String) this.getUrlMethod.invoke(new Object[0]);
    }

    public String getUrlSuper() {
        String ret;
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

    public String getOriginalUrl() {
        if (this.getOriginalUrlMethod == null || this.getOriginalUrlMethod.isNull()) {
            return getOriginalUrlSuper();
        }
        return (String) this.getOriginalUrlMethod.invoke(new Object[0]);
    }

    public String getOriginalUrlSuper() {
        String ret;
        if (this.internal == null) {
            ret = super.getOriginalUrl();
        } else {
            ret = this.internal.getOriginalUrl();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public String getTitle() {
        if (this.getTitleMethod == null || this.getTitleMethod.isNull()) {
            return getTitleSuper();
        }
        return (String) this.getTitleMethod.invoke(new Object[0]);
    }

    public String getTitleSuper() {
        String ret;
        if (this.internal == null) {
            ret = super.getTitle();
        } else {
            ret = this.internal.getTitle();
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
                this.wrapper = new ReflectConstructor(this.coreBridge.getWrapperClass("XWalkNavigationItem"), Object.class).newInstance(this);
                this.getUrlMethod.init(this.wrapper, null, "getUrl", new Class[0]);
                this.getOriginalUrlMethod.init(this.wrapper, null, "getOriginalUrl", new Class[0]);
                this.getTitleMethod.init(this.wrapper, null, "getTitle", new Class[0]);
            } catch (UnsupportedOperationException e) {
            }
        }
    }
}
