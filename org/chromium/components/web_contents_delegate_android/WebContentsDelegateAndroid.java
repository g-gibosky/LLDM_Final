package org.chromium.components.web_contents_delegate_android;

import android.view.KeyEvent;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content_public.browser.WebContents;
import org.chromium.content_public.common.ResourceRequestBody;

@JNINamespace("web_contents_delegate_android")
public class WebContentsDelegateAndroid {
    public static final int LOG_LEVEL_ERROR = 3;
    public static final int LOG_LEVEL_LOG = 1;
    public static final int LOG_LEVEL_TIP = 0;
    public static final int LOG_LEVEL_WARNING = 2;
    private int mMostRecentProgress = 100;

    public int getMostRecentProgress() {
        return this.mMostRecentProgress;
    }

    @CalledByNative
    public void openNewTab(String url, String extraHeaders, ResourceRequestBody postData, int disposition, boolean isRendererInitiated) {
    }

    @CalledByNative
    public void activateContents() {
    }

    @CalledByNative
    public void closeContents() {
    }

    @CalledByNative
    public void onLoadStarted(boolean toDifferentDocument) {
    }

    @CalledByNative
    public void onLoadStopped() {
    }

    @CalledByNative
    public void navigationStateChanged(int flags) {
    }

    @CalledByNative
    public void visibleSSLStateChanged() {
    }

    @CalledByNative
    private final void notifyLoadProgressChanged(double progress) {
        this.mMostRecentProgress = (int) (100.0d * progress);
        onLoadProgressChanged(this.mMostRecentProgress);
    }

    public void onLoadProgressChanged(int progress) {
    }

    @CalledByNative
    public void rendererUnresponsive() {
    }

    @CalledByNative
    public void rendererResponsive() {
    }

    @CalledByNative
    public void webContentsCreated(WebContents sourceWebContents, long openerRenderFrameId, String frameName, String targetUrl, WebContents newWebContents) {
    }

    @CalledByNative
    public boolean shouldCreateWebContents(String targetUrl) {
        return true;
    }

    @CalledByNative
    public boolean onGoToEntryOffset(int offset) {
        return true;
    }

    @CalledByNative
    public void onUpdateUrl(String url) {
    }

    @CalledByNative
    public boolean takeFocus(boolean reverse) {
        return false;
    }

    @CalledByNative
    public void handleKeyboardEvent(KeyEvent event) {
    }

    @CalledByNative
    public boolean addMessageToConsole(int level, String message, int lineNumber, String sourceId) {
        return false;
    }

    @CalledByNative
    public void showRepostFormWarningDialog() {
    }

    @CalledByNative
    public void toggleFullscreenModeForTab(boolean enterFullscreen) {
    }

    @CalledByNative
    public boolean isFullscreenForTabOrPending() {
        return false;
    }
}
