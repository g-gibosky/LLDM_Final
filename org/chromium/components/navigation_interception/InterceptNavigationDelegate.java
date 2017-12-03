package org.chromium.components.navigation_interception;

import org.chromium.base.annotations.CalledByNative;

public interface InterceptNavigationDelegate {
    @CalledByNative
    boolean shouldIgnoreNavigation(NavigationParams navigationParams);
}
