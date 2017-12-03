package org.chromium.content_public.browser;

import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.os.Parcelable;
import org.chromium.base.ObserverList.RewindableIterator;
import org.chromium.base.VisibleForTesting;

public interface WebContents extends Parcelable {
    void addMessageToDevToolsConsole(int i, String str);

    void addObserver(WebContentsObserver webContentsObserver);

    void adjustSelectionByCharacterOffset(int i, int i2);

    void copy();

    void cut();

    void destroy();

    int downloadImage(String str, boolean z, int i, boolean z2, ImageDownloadCallback imageDownloadCallback);

    void evaluateJavaScript(String str, JavaScriptCallback javaScriptCallback);

    @VisibleForTesting
    void evaluateJavaScriptForTests(String str, JavaScriptCallback javaScriptCallback);

    void exitFullscreen();

    boolean focusLocationBarByDefault();

    int getBackgroundColor();

    void getContentBitmapAsync(Config config, float f, Rect rect, ContentBitmapCallback contentBitmapCallback);

    @VisibleForTesting
    String getEncoding();

    String getLastCommittedUrl();

    NavigationController getNavigationController();

    @VisibleForTesting
    RewindableIterator<WebContentsObserver> getObserversForTesting();

    int getThemeColor();

    String getTitle();

    String getUrl();

    String getVisibleUrl();

    boolean hasAccessedInitialDocument();

    void insertCSS(String str);

    boolean isDestroyed();

    boolean isIncognito();

    boolean isLoading();

    boolean isLoadingToDifferentDocument();

    boolean isReady();

    boolean isShowingInterstitialPage();

    void onContextMenuClosed();

    void onContextMenuOpened();

    void onHide();

    void onShow();

    void paste();

    void reloadLoFiImages();

    void removeObserver(WebContentsObserver webContentsObserver);

    void replace(String str);

    void requestAccessibilitySnapshot(AccessibilitySnapshotCallback accessibilitySnapshotCallback);

    void resumeLoadingCreatedWebContents();

    void resumeMediaSession();

    void scrollFocusedEditableNodeIntoView();

    void selectAll();

    void selectWordAroundCaret();

    void sendMessageToFrame(String str, String str2, String str3);

    void setAudioMuted(boolean z);

    void showImeIfNeeded();

    @VisibleForTesting
    void showInterstitialPage(String str, long j);

    void stop();

    void stopMediaSession();

    void suspendAllMediaPlayers();

    void suspendMediaSession();

    void unselect();

    void updateTopControlsState(boolean z, boolean z2, boolean z3);
}
