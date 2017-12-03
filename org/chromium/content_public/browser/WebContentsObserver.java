package org.chromium.content_public.browser;

import java.lang.ref.WeakReference;
import org.chromium.content_public.common.MediaMetadata;

public abstract class WebContentsObserver {
    private WeakReference<WebContents> mWebContents;

    public WebContentsObserver(WebContents webContents) {
        this.mWebContents = new WeakReference(webContents);
        webContents.addObserver(this);
    }

    public void renderViewReady() {
    }

    public void renderProcessGone(boolean wasOomProtected) {
    }

    public void didFinishNavigation(boolean isMainFrame, boolean isErrorPage, boolean hasCommitted) {
    }

    public void didStartLoading(String url) {
    }

    public void didStopLoading(String url) {
    }

    public void didFailLoad(boolean isProvisionalLoad, boolean isMainFrame, int errorCode, String description, String failingUrl, boolean wasIgnoredByHandler) {
    }

    public void didNavigateMainFrame(String url, String baseUrl, boolean isNavigationToDifferentPage, boolean isFragmentNavigation, int statusCode) {
    }

    public void didFirstVisuallyNonEmptyPaint() {
    }

    public void didNavigateAnyFrame(String url, String baseUrl, boolean isReload) {
    }

    public void documentAvailableInMainFrame() {
    }

    public void didStartProvisionalLoadForFrame(long frameId, long parentFrameId, boolean isMainFrame, String validatedUrl, boolean isErrorPage, boolean isIframeSrcdoc) {
    }

    public void didCommitProvisionalLoadForFrame(long frameId, boolean isMainFrame, String url, int transitionType) {
    }

    public void didFinishLoad(long frameId, String validatedUrl, boolean isMainFrame) {
    }

    public void documentLoadedInFrame(long frameId, boolean isMainFrame) {
    }

    public void navigationEntryCommitted() {
    }

    public void didAttachInterstitialPage() {
    }

    public void didDetachInterstitialPage() {
    }

    public void didChangeThemeColor(int color) {
    }

    public void didStartNavigationToPendingEntry(String url) {
    }

    public void mediaSessionStateChanged(boolean isControllable, boolean isSuspended, MediaMetadata metadata) {
    }

    public void destroy() {
        if (this.mWebContents != null) {
            WebContents webContents = (WebContents) this.mWebContents.get();
            this.mWebContents = null;
            if (webContents != null) {
                webContents.removeObserver(this);
            }
        }
    }

    protected WebContentsObserver() {
    }
}
