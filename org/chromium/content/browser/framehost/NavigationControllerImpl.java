package org.chromium.content.browser.framehost;

import android.graphics.Bitmap;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content_public.browser.LoadUrlParams;
import org.chromium.content_public.browser.NavigationController;
import org.chromium.content_public.browser.NavigationEntry;
import org.chromium.content_public.browser.NavigationHistory;
import org.chromium.content_public.common.ResourceRequestBody;

@JNINamespace("content")
class NavigationControllerImpl implements NavigationController {
    private long mNativeNavigationControllerAndroid;

    private native boolean nativeCanCopyStateOver(long j);

    private native boolean nativeCanGoBack(long j);

    private native boolean nativeCanGoForward(long j);

    private native boolean nativeCanGoToOffset(long j, int i);

    private native boolean nativeCanPruneAllButLastCommitted(long j);

    private native void nativeCancelPendingReload(long j);

    private native void nativeClearHistory(long j);

    private native void nativeClearSslPreferences(long j);

    private native void nativeContinuePendingReload(long j);

    private native void nativeCopyStateFrom(long j, long j2);

    private native void nativeCopyStateFromAndPrune(long j, long j2, boolean z);

    private native void nativeGetDirectedNavigationHistory(long j, NavigationHistory navigationHistory, boolean z, int i);

    private native NavigationEntry nativeGetEntryAtIndex(long j, int i);

    private native int nativeGetLastCommittedEntryIndex(long j);

    private native int nativeGetNavigationHistory(long j, Object obj);

    private native String nativeGetOriginalUrlForVisibleNavigationEntry(long j);

    private native NavigationEntry nativeGetPendingEntry(long j);

    private native boolean nativeGetUseDesktopUserAgent(long j);

    private native void nativeGoBack(long j);

    private native void nativeGoForward(long j);

    private native void nativeGoToNavigationIndex(long j, int i);

    private native void nativeGoToOffset(long j, int i);

    private native boolean nativeIsInitialNavigation(long j);

    private native void nativeLoadIfNecessary(long j);

    private native void nativeLoadUrl(long j, String str, int i, int i2, String str2, int i3, int i4, String str3, ResourceRequestBody resourceRequestBody, String str4, String str5, String str6, boolean z, boolean z2, boolean z3);

    private native void nativeReload(long j, boolean z);

    private native void nativeReloadBypassingCache(long j, boolean z);

    private native void nativeReloadDisableLoFi(long j, boolean z);

    private native void nativeReloadToRefreshContent(long j, boolean z);

    private native boolean nativeRemoveEntryAtIndex(long j, int i);

    private native void nativeRequestRestoreLoad(long j);

    private native void nativeSetUseDesktopUserAgent(long j, boolean z, boolean z2);

    private NavigationControllerImpl(long nativeNavigationControllerAndroid) {
        this.mNativeNavigationControllerAndroid = nativeNavigationControllerAndroid;
    }

    @CalledByNative
    private static NavigationControllerImpl create(long nativeNavigationControllerAndroid) {
        return new NavigationControllerImpl(nativeNavigationControllerAndroid);
    }

    @CalledByNative
    private void destroy() {
        this.mNativeNavigationControllerAndroid = 0;
    }

    public boolean canGoBack() {
        return this.mNativeNavigationControllerAndroid != 0 && nativeCanGoBack(this.mNativeNavigationControllerAndroid);
    }

    public boolean canGoForward() {
        return this.mNativeNavigationControllerAndroid != 0 && nativeCanGoForward(this.mNativeNavigationControllerAndroid);
    }

    @VisibleForTesting
    public boolean canGoToOffset(int offset) {
        return this.mNativeNavigationControllerAndroid != 0 && nativeCanGoToOffset(this.mNativeNavigationControllerAndroid, offset);
    }

    public void goToOffset(int offset) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeGoToOffset(this.mNativeNavigationControllerAndroid, offset);
        }
    }

    public void goToNavigationIndex(int index) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeGoToNavigationIndex(this.mNativeNavigationControllerAndroid, index);
        }
    }

    public void goBack() {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeGoBack(this.mNativeNavigationControllerAndroid);
        }
    }

    public void goForward() {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeGoForward(this.mNativeNavigationControllerAndroid);
        }
    }

    public boolean isInitialNavigation() {
        return this.mNativeNavigationControllerAndroid != 0 && nativeIsInitialNavigation(this.mNativeNavigationControllerAndroid);
    }

    public void loadIfNecessary() {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeLoadIfNecessary(this.mNativeNavigationControllerAndroid);
        }
    }

    public void requestRestoreLoad() {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeRequestRestoreLoad(this.mNativeNavigationControllerAndroid);
        }
    }

    public void reload(boolean checkForRepost) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeReload(this.mNativeNavigationControllerAndroid, checkForRepost);
        }
    }

    public void reloadToRefreshContent(boolean checkForRepost) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeReloadToRefreshContent(this.mNativeNavigationControllerAndroid, checkForRepost);
        }
    }

    public void reloadBypassingCache(boolean checkForRepost) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeReloadBypassingCache(this.mNativeNavigationControllerAndroid, checkForRepost);
        }
    }

    public void reloadDisableLoFi(boolean checkForRepost) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeReloadDisableLoFi(this.mNativeNavigationControllerAndroid, checkForRepost);
        }
    }

    public void cancelPendingReload() {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeCancelPendingReload(this.mNativeNavigationControllerAndroid);
        }
    }

    public void continuePendingReload() {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeContinuePendingReload(this.mNativeNavigationControllerAndroid);
        }
    }

    public void loadUrl(LoadUrlParams params) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeLoadUrl(this.mNativeNavigationControllerAndroid, params.getUrl(), params.getLoadUrlType(), params.getTransitionType(), params.getReferrer() != null ? params.getReferrer().getUrl() : null, params.getReferrer() != null ? params.getReferrer().getPolicy() : 0, params.getUserAgentOverrideOption(), params.getExtraHeadersString(), params.getPostData(), params.getBaseUrl(), params.getVirtualUrlForDataUrl(), params.getDataUrlAsString(), params.getCanLoadLocalResources(), params.getIsRendererInitiated(), params.getShouldReplaceCurrentEntry());
        }
    }

    @VisibleForTesting
    public void clearHistory() {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeClearHistory(this.mNativeNavigationControllerAndroid);
        }
    }

    public NavigationHistory getNavigationHistory() {
        if (this.mNativeNavigationControllerAndroid == 0) {
            return null;
        }
        NavigationHistory history = new NavigationHistory();
        history.setCurrentEntryIndex(nativeGetNavigationHistory(this.mNativeNavigationControllerAndroid, history));
        return history;
    }

    public NavigationHistory getDirectedNavigationHistory(boolean isForward, int itemLimit) {
        if (this.mNativeNavigationControllerAndroid == 0) {
            return null;
        }
        NavigationHistory history = new NavigationHistory();
        nativeGetDirectedNavigationHistory(this.mNativeNavigationControllerAndroid, history, isForward, itemLimit);
        return history;
    }

    public String getOriginalUrlForVisibleNavigationEntry() {
        if (this.mNativeNavigationControllerAndroid == 0) {
            return null;
        }
        return nativeGetOriginalUrlForVisibleNavigationEntry(this.mNativeNavigationControllerAndroid);
    }

    public void clearSslPreferences() {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeClearSslPreferences(this.mNativeNavigationControllerAndroid);
        }
    }

    public boolean getUseDesktopUserAgent() {
        if (this.mNativeNavigationControllerAndroid == 0) {
            return false;
        }
        return nativeGetUseDesktopUserAgent(this.mNativeNavigationControllerAndroid);
    }

    public void setUseDesktopUserAgent(boolean override, boolean reloadOnChange) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            nativeSetUseDesktopUserAgent(this.mNativeNavigationControllerAndroid, override, reloadOnChange);
        }
    }

    public NavigationEntry getEntryAtIndex(int index) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            return nativeGetEntryAtIndex(this.mNativeNavigationControllerAndroid, index);
        }
        return null;
    }

    public NavigationEntry getPendingEntry() {
        if (this.mNativeNavigationControllerAndroid != 0) {
            return nativeGetPendingEntry(this.mNativeNavigationControllerAndroid);
        }
        return null;
    }

    public int getLastCommittedEntryIndex() {
        if (this.mNativeNavigationControllerAndroid != 0) {
            return nativeGetLastCommittedEntryIndex(this.mNativeNavigationControllerAndroid);
        }
        return -1;
    }

    public boolean removeEntryAtIndex(int index) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            return nativeRemoveEntryAtIndex(this.mNativeNavigationControllerAndroid, index);
        }
        return false;
    }

    public boolean canCopyStateOver() {
        return this.mNativeNavigationControllerAndroid != 0 && nativeCanCopyStateOver(this.mNativeNavigationControllerAndroid);
    }

    public boolean canPruneAllButLastCommitted() {
        return this.mNativeNavigationControllerAndroid != 0 && nativeCanPruneAllButLastCommitted(this.mNativeNavigationControllerAndroid);
    }

    public void copyStateFrom(NavigationController source) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            NavigationControllerImpl sourceImpl = (NavigationControllerImpl) source;
            if (sourceImpl.mNativeNavigationControllerAndroid != 0) {
                nativeCopyStateFrom(this.mNativeNavigationControllerAndroid, sourceImpl.mNativeNavigationControllerAndroid);
            }
        }
    }

    public void copyStateFromAndPrune(NavigationController source, boolean replaceEntry) {
        if (this.mNativeNavigationControllerAndroid != 0) {
            NavigationControllerImpl sourceImpl = (NavigationControllerImpl) source;
            if (sourceImpl.mNativeNavigationControllerAndroid != 0) {
                nativeCopyStateFromAndPrune(this.mNativeNavigationControllerAndroid, sourceImpl.mNativeNavigationControllerAndroid, replaceEntry);
            }
        }
    }

    @CalledByNative
    private static void addToNavigationHistory(Object history, Object navigationEntry) {
        ((NavigationHistory) history).addEntry((NavigationEntry) navigationEntry);
    }

    @CalledByNative
    private static NavigationEntry createNavigationEntry(int index, String url, String virtualUrl, String originalUrl, String title, Bitmap favicon, int transition) {
        return new NavigationEntry(index, url, virtualUrl, originalUrl, title, favicon, transition);
    }
}
