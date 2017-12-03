package org.xwalk.core.internal;

import android.util.Log;
import java.net.MalformedURLException;
import java.net.URL;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("xwalk")
@XWalkAPI(createExternally = true)
public class XWalkCookieManagerInternal {
    private static final String TAG = "XWalkCookieManager";

    private native boolean nativeAcceptCookie();

    private native boolean nativeAllowFileSchemeCookies();

    private native void nativeFlushCookieStore();

    private native String nativeGetCookie(String str);

    private native boolean nativeHasCookies();

    private native void nativeRemoveAllCookie();

    private native void nativeRemoveExpiredCookie();

    private native void nativeRemoveSessionCookie();

    private native void nativeSetAcceptCookie(boolean z);

    private native void nativeSetAcceptFileSchemeCookies(boolean z);

    private native void nativeSetCookie(String str, String str2);

    @XWalkAPI
    public void setAcceptCookie(boolean accept) {
        nativeSetAcceptCookie(accept);
    }

    @XWalkAPI
    public boolean acceptCookie() {
        return nativeAcceptCookie();
    }

    @XWalkAPI
    public void setCookie(String url, String value) {
        try {
            nativeSetCookie(new URL(url).toString(), value);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Not setting cookie due to invalid URL", e);
        }
    }

    @XWalkAPI
    public String getCookie(String url) {
        try {
            String cookie = nativeGetCookie(new URL(url).toString());
            if (cookie == null || cookie.trim().isEmpty()) {
                return null;
            }
            return cookie;
        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to get cookies due to invalid URL", e);
            return null;
        }
    }

    @XWalkAPI
    public void removeSessionCookie() {
        nativeRemoveSessionCookie();
    }

    @XWalkAPI
    public void removeAllCookie() {
        nativeRemoveAllCookie();
    }

    @XWalkAPI
    public boolean hasCookies() {
        return nativeHasCookies();
    }

    @XWalkAPI
    public void removeExpiredCookie() {
        nativeRemoveExpiredCookie();
    }

    @XWalkAPI
    public void flushCookieStore() {
        nativeFlushCookieStore();
    }

    @XWalkAPI
    public boolean allowFileSchemeCookies() {
        return nativeAllowFileSchemeCookies();
    }

    @XWalkAPI
    public void setAcceptFileSchemeCookies(boolean accept) {
        nativeSetAcceptFileSchemeCookies(accept);
    }
}
