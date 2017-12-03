package org.chromium.content.browser;

import org.chromium.net.ProxyChangeListener;

public class ContentViewStatics {
    private static native String nativeFindAddress(String str);

    private static native void nativeSetWebKitSharedTimersSuspended(boolean z);

    public static String findAddress(String addr) {
        if (addr == null) {
            throw new NullPointerException("addr is null");
        }
        String result = nativeFindAddress(addr);
        return (result == null || result.isEmpty()) ? null : result;
    }

    public static void setWebKitSharedTimersSuspended(boolean suspend) {
        nativeSetWebKitSharedTimersSuspended(suspend);
    }

    public static void enablePlatformNotifications() {
        ProxyChangeListener.setEnabled(true);
    }

    public static void disablePlatformNotifications() {
        ProxyChangeListener.setEnabled(false);
    }
}
