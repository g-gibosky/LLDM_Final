package org.xwalk.core.internal;

public class XWalkCookieManagerBridge extends XWalkCookieManagerInternal {
    private ReflectMethod acceptCookieMethod = new ReflectMethod(null, "acceptCookie", new Class[0]);
    private ReflectMethod allowFileSchemeCookiesMethod = new ReflectMethod(null, "allowFileSchemeCookies", new Class[0]);
    private XWalkCoreBridge coreBridge;
    private ReflectMethod flushCookieStoreMethod = new ReflectMethod(null, "flushCookieStore", new Class[0]);
    private ReflectMethod getCookieStringMethod = new ReflectMethod(null, "getCookie", new Class[0]);
    private ReflectMethod hasCookiesMethod = new ReflectMethod(null, "hasCookies", new Class[0]);
    private ReflectMethod removeAllCookieMethod = new ReflectMethod(null, "removeAllCookie", new Class[0]);
    private ReflectMethod removeExpiredCookieMethod = new ReflectMethod(null, "removeExpiredCookie", new Class[0]);
    private ReflectMethod removeSessionCookieMethod = new ReflectMethod(null, "removeSessionCookie", new Class[0]);
    private ReflectMethod setAcceptCookiebooleanMethod = new ReflectMethod(null, "setAcceptCookie", new Class[0]);
    private ReflectMethod setAcceptFileSchemeCookiesbooleanMethod = new ReflectMethod(null, "setAcceptFileSchemeCookies", new Class[0]);
    private ReflectMethod setCookieStringStringMethod = new ReflectMethod(null, "setCookie", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkCookieManagerBridge(Object wrapper) {
        this.wrapper = wrapper;
        reflectionInit();
    }

    public void setAcceptCookie(boolean accept) {
        if (this.setAcceptCookiebooleanMethod == null || this.setAcceptCookiebooleanMethod.isNull()) {
            setAcceptCookieSuper(accept);
            return;
        }
        this.setAcceptCookiebooleanMethod.invoke(Boolean.valueOf(accept));
    }

    public void setAcceptCookieSuper(boolean accept) {
        super.setAcceptCookie(accept);
    }

    public boolean acceptCookie() {
        if (this.acceptCookieMethod == null || this.acceptCookieMethod.isNull()) {
            return acceptCookieSuper();
        }
        return ((Boolean) this.acceptCookieMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean acceptCookieSuper() {
        return super.acceptCookie();
    }

    public void setCookie(String url, String value) {
        if (this.setCookieStringStringMethod == null || this.setCookieStringStringMethod.isNull()) {
            setCookieSuper(url, value);
            return;
        }
        this.setCookieStringStringMethod.invoke(url, value);
    }

    public void setCookieSuper(String url, String value) {
        super.setCookie(url, value);
    }

    public String getCookie(String url) {
        if (this.getCookieStringMethod == null || this.getCookieStringMethod.isNull()) {
            return getCookieSuper(url);
        }
        return (String) this.getCookieStringMethod.invoke(url);
    }

    public String getCookieSuper(String url) {
        String ret = super.getCookie(url);
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void removeSessionCookie() {
        if (this.removeSessionCookieMethod == null || this.removeSessionCookieMethod.isNull()) {
            removeSessionCookieSuper();
        } else {
            this.removeSessionCookieMethod.invoke(new Object[0]);
        }
    }

    public void removeSessionCookieSuper() {
        super.removeSessionCookie();
    }

    public void removeAllCookie() {
        if (this.removeAllCookieMethod == null || this.removeAllCookieMethod.isNull()) {
            removeAllCookieSuper();
        } else {
            this.removeAllCookieMethod.invoke(new Object[0]);
        }
    }

    public void removeAllCookieSuper() {
        super.removeAllCookie();
    }

    public boolean hasCookies() {
        if (this.hasCookiesMethod == null || this.hasCookiesMethod.isNull()) {
            return hasCookiesSuper();
        }
        return ((Boolean) this.hasCookiesMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean hasCookiesSuper() {
        return super.hasCookies();
    }

    public void removeExpiredCookie() {
        if (this.removeExpiredCookieMethod == null || this.removeExpiredCookieMethod.isNull()) {
            removeExpiredCookieSuper();
        } else {
            this.removeExpiredCookieMethod.invoke(new Object[0]);
        }
    }

    public void removeExpiredCookieSuper() {
        super.removeExpiredCookie();
    }

    public void flushCookieStore() {
        if (this.flushCookieStoreMethod == null || this.flushCookieStoreMethod.isNull()) {
            flushCookieStoreSuper();
        } else {
            this.flushCookieStoreMethod.invoke(new Object[0]);
        }
    }

    public void flushCookieStoreSuper() {
        super.flushCookieStore();
    }

    public boolean allowFileSchemeCookies() {
        if (this.allowFileSchemeCookiesMethod == null || this.allowFileSchemeCookiesMethod.isNull()) {
            return allowFileSchemeCookiesSuper();
        }
        return ((Boolean) this.allowFileSchemeCookiesMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean allowFileSchemeCookiesSuper() {
        return super.allowFileSchemeCookies();
    }

    public void setAcceptFileSchemeCookies(boolean accept) {
        if (this.setAcceptFileSchemeCookiesbooleanMethod == null || this.setAcceptFileSchemeCookiesbooleanMethod.isNull()) {
            setAcceptFileSchemeCookiesSuper(accept);
            return;
        }
        this.setAcceptFileSchemeCookiesbooleanMethod.invoke(Boolean.valueOf(accept));
    }

    public void setAcceptFileSchemeCookiesSuper(boolean accept) {
        super.setAcceptFileSchemeCookies(accept);
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.setAcceptCookiebooleanMethod.init(this.wrapper, null, "setAcceptCookie", Boolean.TYPE);
            this.acceptCookieMethod.init(this.wrapper, null, "acceptCookie", new Class[0]);
            this.setCookieStringStringMethod.init(this.wrapper, null, "setCookie", String.class, String.class);
            this.getCookieStringMethod.init(this.wrapper, null, "getCookie", String.class);
            this.removeSessionCookieMethod.init(this.wrapper, null, "removeSessionCookie", new Class[0]);
            this.removeAllCookieMethod.init(this.wrapper, null, "removeAllCookie", new Class[0]);
            this.hasCookiesMethod.init(this.wrapper, null, "hasCookies", new Class[0]);
            this.removeExpiredCookieMethod.init(this.wrapper, null, "removeExpiredCookie", new Class[0]);
            this.flushCookieStoreMethod.init(this.wrapper, null, "flushCookieStore", new Class[0]);
            this.allowFileSchemeCookiesMethod.init(this.wrapper, null, "allowFileSchemeCookies", new Class[0]);
            this.setAcceptFileSchemeCookiesbooleanMethod.init(this.wrapper, null, "setAcceptFileSchemeCookies", Boolean.TYPE);
        }
    }
}
