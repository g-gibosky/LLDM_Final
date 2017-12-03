package org.chromium.content_public.browser;

import org.chromium.base.VisibleForTesting;

public interface NavigationController {
    boolean canCopyStateOver();

    boolean canGoBack();

    boolean canGoForward();

    boolean canGoToOffset(int i);

    boolean canPruneAllButLastCommitted();

    void cancelPendingReload();

    @VisibleForTesting
    void clearHistory();

    void clearSslPreferences();

    void continuePendingReload();

    void copyStateFrom(NavigationController navigationController);

    void copyStateFromAndPrune(NavigationController navigationController, boolean z);

    NavigationHistory getDirectedNavigationHistory(boolean z, int i);

    @VisibleForTesting
    NavigationEntry getEntryAtIndex(int i);

    int getLastCommittedEntryIndex();

    NavigationHistory getNavigationHistory();

    String getOriginalUrlForVisibleNavigationEntry();

    NavigationEntry getPendingEntry();

    boolean getUseDesktopUserAgent();

    void goBack();

    void goForward();

    void goToNavigationIndex(int i);

    void goToOffset(int i);

    boolean isInitialNavigation();

    void loadIfNecessary();

    void loadUrl(LoadUrlParams loadUrlParams);

    void reload(boolean z);

    void reloadBypassingCache(boolean z);

    void reloadDisableLoFi(boolean z);

    void reloadToRefreshContent(boolean z);

    boolean removeEntryAtIndex(int i);

    void requestRestoreLoad();

    void setUseDesktopUserAgent(boolean z, boolean z2);
}
