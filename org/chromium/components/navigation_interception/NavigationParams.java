package org.chromium.components.navigation_interception;

import android.text.TextUtils;
import org.chromium.base.annotations.CalledByNative;

public class NavigationParams {
    public final boolean hasUserGesture;
    public final boolean hasUserGestureCarryover;
    public final boolean isExternalProtocol;
    public final boolean isMainFrame;
    public final boolean isPost;
    public final boolean isRedirect;
    public final int pageTransitionType;
    public final String referrer;
    public final String url;

    public NavigationParams(String url, String referrer, boolean isPost, boolean hasUserGesture, int pageTransitionType, boolean isRedirect, boolean isExternalProtocol, boolean isMainFrame, boolean hasUserGestureCarryover) {
        this.url = url;
        if (TextUtils.isEmpty(referrer)) {
            referrer = null;
        }
        this.referrer = referrer;
        this.isPost = isPost;
        this.hasUserGesture = hasUserGesture;
        this.pageTransitionType = pageTransitionType;
        this.isRedirect = isRedirect;
        this.isExternalProtocol = isExternalProtocol;
        this.isMainFrame = isMainFrame;
        this.hasUserGestureCarryover = hasUserGestureCarryover;
    }

    @CalledByNative
    public static NavigationParams create(String url, String referrer, boolean isPost, boolean hasUserGesture, int pageTransitionType, boolean isRedirect, boolean isExternalProtocol, boolean isMainFrame, boolean hasUserGestureCarryover) {
        return new NavigationParams(url, referrer, isPost, hasUserGesture, pageTransitionType, isRedirect, isExternalProtocol, isMainFrame, hasUserGestureCarryover);
    }
}
