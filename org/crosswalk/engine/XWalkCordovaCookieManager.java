package org.crosswalk.engine;

import org.apache.cordova.ICordovaCookieManager;
import org.xwalk.core.XWalkCookieManager;

class XWalkCordovaCookieManager implements ICordovaCookieManager {
    protected XWalkCookieManager cookieManager;

    public XWalkCordovaCookieManager() {
        this.cookieManager = null;
        this.cookieManager = new XWalkCookieManager();
    }

    public void setCookiesEnabled(boolean accept) {
        this.cookieManager.setAcceptCookie(accept);
    }

    public void setCookie(String url, String value) {
        this.cookieManager.setCookie(url, value);
    }

    public String getCookie(String url) {
        return this.cookieManager.getCookie(url);
    }

    public void clearCookies() {
        this.cookieManager.removeAllCookie();
    }

    public void flush() {
        this.cookieManager.flushCookieStore();
    }
}
