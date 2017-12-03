package org.chromium.content.browser.webcontents;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.chromium.base.ObserverList.RewindableIterator;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content_public.browser.AccessibilitySnapshotCallback;
import org.chromium.content_public.browser.AccessibilitySnapshotNode;
import org.chromium.content_public.browser.ContentBitmapCallback;
import org.chromium.content_public.browser.ImageDownloadCallback;
import org.chromium.content_public.browser.JavaScriptCallback;
import org.chromium.content_public.browser.NavigationController;
import org.chromium.content_public.browser.WebContents;
import org.chromium.content_public.browser.WebContentsObserver;

@JNINamespace("content")
class WebContentsImpl implements WebContents {
    static final /* synthetic */ boolean $assertionsDisabled = (!WebContentsImpl.class.desiredAssertionStatus());
    public static final Creator<WebContents> CREATOR = new C02371();
    private static final long PARCELABLE_VERSION_ID = 0;
    private static final String PARCEL_PROCESS_GUARD_KEY = "processguard";
    private static final String PARCEL_VERSION_KEY = "version";
    private static final String PARCEL_WEBCONTENTS_KEY = "webcontents";
    private static UUID sParcelableUUID = UUID.randomUUID();
    private boolean mContextMenuOpened;
    private long mNativeWebContentsAndroid;
    private NavigationController mNavigationController;
    private WebContentsObserverProxy mObserverProxy;

    static class C02371 implements Creator<WebContents> {
        C02371() {
        }

        public WebContents createFromParcel(Parcel source) {
            Bundle bundle = source.readBundle();
            if (bundle.getLong(WebContentsImpl.PARCEL_VERSION_KEY, -1) != 0) {
                return null;
            }
            if (WebContentsImpl.sParcelableUUID.compareTo(((ParcelUuid) bundle.getParcelable(WebContentsImpl.PARCEL_PROCESS_GUARD_KEY)).getUuid()) == 0) {
                return WebContentsImpl.nativeFromNativePtr(bundle.getLong(WebContentsImpl.PARCEL_WEBCONTENTS_KEY));
            }
            return null;
        }

        public WebContents[] newArray(int size) {
            return new WebContents[size];
        }
    }

    private native void nativeAddMessageToDevToolsConsole(long j, int i, String str);

    private native void nativeAdjustSelectionByCharacterOffset(long j, int i, int i2);

    private native void nativeCopy(long j);

    private native void nativeCut(long j);

    private static native void nativeDestroyWebContents(long j);

    private native int nativeDownloadImage(long j, String str, boolean z, int i, boolean z2, ImageDownloadCallback imageDownloadCallback);

    private native void nativeEvaluateJavaScript(long j, String str, JavaScriptCallback javaScriptCallback);

    private native void nativeEvaluateJavaScriptForTests(long j, String str, JavaScriptCallback javaScriptCallback);

    private native void nativeExitFullscreen(long j);

    private native boolean nativeFocusLocationBarByDefault(long j);

    private static native WebContents nativeFromNativePtr(long j);

    private native int nativeGetBackgroundColor(long j);

    private native void nativeGetContentBitmap(long j, ContentBitmapCallback contentBitmapCallback, Config config, float f, float f2, float f3, float f4, float f5);

    private native String nativeGetEncoding(long j);

    private native String nativeGetLastCommittedURL(long j);

    private native int nativeGetThemeColor(long j);

    private native String nativeGetTitle(long j);

    private native String nativeGetURL(long j);

    private native String nativeGetVisibleURL(long j);

    private native boolean nativeHasAccessedInitialDocument(long j);

    private native void nativeInsertCSS(long j, String str);

    private native boolean nativeIsIncognito(long j);

    private native boolean nativeIsLoading(long j);

    private native boolean nativeIsLoadingToDifferentDocument(long j);

    private native boolean nativeIsRenderWidgetHostViewReady(long j);

    private native boolean nativeIsShowingInterstitialPage(long j);

    private native void nativeOnContextMenuClosed(long j);

    private native void nativeOnHide(long j);

    private native void nativeOnShow(long j);

    private native void nativePaste(long j);

    private native void nativeReloadLoFiImages(long j);

    private native void nativeReplace(long j, String str);

    private native void nativeRequestAccessibilitySnapshot(long j, AccessibilitySnapshotCallback accessibilitySnapshotCallback);

    private native void nativeResumeLoadingCreatedWebContents(long j);

    private native void nativeResumeMediaSession(long j);

    private native void nativeScrollFocusedEditableNodeIntoView(long j);

    private native void nativeSelectAll(long j);

    private native void nativeSelectWordAroundCaret(long j);

    private native void nativeSendMessageToFrame(long j, String str, String str2, String str3);

    private native void nativeSetAudioMuted(long j, boolean z);

    private native void nativeShowImeIfNeeded(long j);

    private native void nativeShowInterstitialPage(long j, String str, long j2);

    private native void nativeStop(long j);

    private native void nativeStopMediaSession(long j);

    private native void nativeSuspendAllMediaPlayers(long j);

    private native void nativeSuspendMediaSession(long j);

    private native void nativeUnselect(long j);

    private native void nativeUpdateTopControlsState(long j, boolean z, boolean z2, boolean z3);

    @VisibleForTesting
    public static void invalidateSerializedWebContentsForTesting() {
        sParcelableUUID = UUID.randomUUID();
    }

    private WebContentsImpl(long nativeWebContentsAndroid, NavigationController navigationController) {
        this.mNativeWebContentsAndroid = nativeWebContentsAndroid;
        this.mNavigationController = navigationController;
    }

    @CalledByNative
    private static WebContentsImpl create(long nativeWebContentsAndroid, NavigationController navigationController) {
        return new WebContentsImpl(nativeWebContentsAndroid, navigationController);
    }

    @CalledByNative
    private void clearNativePtr() {
        this.mNativeWebContentsAndroid = 0;
        this.mNavigationController = null;
        if (this.mObserverProxy != null) {
            this.mObserverProxy.destroy();
            this.mObserverProxy = null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        Bundle data = new Bundle();
        data.putLong(PARCEL_VERSION_KEY, 0);
        data.putParcelable(PARCEL_PROCESS_GUARD_KEY, new ParcelUuid(sParcelableUUID));
        data.putLong(PARCEL_WEBCONTENTS_KEY, this.mNativeWebContentsAndroid);
        dest.writeBundle(data);
    }

    @CalledByNative
    private long getNativePointer() {
        return this.mNativeWebContentsAndroid;
    }

    public void destroy() {
        if (!ThreadUtils.runningOnUiThread()) {
            throw new IllegalStateException("Attempting to destroy WebContents on non-UI thread");
        } else if (this.mNativeWebContentsAndroid != 0) {
            nativeDestroyWebContents(this.mNativeWebContentsAndroid);
        }
    }

    public boolean isDestroyed() {
        return this.mNativeWebContentsAndroid == 0;
    }

    public NavigationController getNavigationController() {
        return this.mNavigationController;
    }

    public String getTitle() {
        return nativeGetTitle(this.mNativeWebContentsAndroid);
    }

    public String getVisibleUrl() {
        return nativeGetVisibleURL(this.mNativeWebContentsAndroid);
    }

    public boolean isLoading() {
        return nativeIsLoading(this.mNativeWebContentsAndroid);
    }

    public boolean isLoadingToDifferentDocument() {
        return nativeIsLoadingToDifferentDocument(this.mNativeWebContentsAndroid);
    }

    public void stop() {
        nativeStop(this.mNativeWebContentsAndroid);
    }

    public void cut() {
        nativeCut(this.mNativeWebContentsAndroid);
    }

    public void copy() {
        nativeCopy(this.mNativeWebContentsAndroid);
    }

    public void paste() {
        nativePaste(this.mNativeWebContentsAndroid);
    }

    public void replace(String word) {
        nativeReplace(this.mNativeWebContentsAndroid, word);
    }

    public void selectAll() {
        nativeSelectAll(this.mNativeWebContentsAndroid);
    }

    public void unselect() {
        if (!isDestroyed()) {
            nativeUnselect(this.mNativeWebContentsAndroid);
        }
    }

    public void insertCSS(String css) {
        if (!isDestroyed()) {
            nativeInsertCSS(this.mNativeWebContentsAndroid, css);
        }
    }

    public void onHide() {
        nativeOnHide(this.mNativeWebContentsAndroid);
    }

    public void onShow() {
        nativeOnShow(this.mNativeWebContentsAndroid);
    }

    public void suspendAllMediaPlayers() {
        nativeSuspendAllMediaPlayers(this.mNativeWebContentsAndroid);
    }

    public void setAudioMuted(boolean mute) {
        nativeSetAudioMuted(this.mNativeWebContentsAndroid, mute);
    }

    public int getBackgroundColor() {
        return nativeGetBackgroundColor(this.mNativeWebContentsAndroid);
    }

    public void showInterstitialPage(String url, long interstitialPageDelegateAndroid) {
        nativeShowInterstitialPage(this.mNativeWebContentsAndroid, url, interstitialPageDelegateAndroid);
    }

    public boolean isShowingInterstitialPage() {
        return nativeIsShowingInterstitialPage(this.mNativeWebContentsAndroid);
    }

    public boolean focusLocationBarByDefault() {
        return nativeFocusLocationBarByDefault(this.mNativeWebContentsAndroid);
    }

    public boolean isReady() {
        return nativeIsRenderWidgetHostViewReady(this.mNativeWebContentsAndroid);
    }

    public void exitFullscreen() {
        nativeExitFullscreen(this.mNativeWebContentsAndroid);
    }

    public void updateTopControlsState(boolean enableHiding, boolean enableShowing, boolean animate) {
        nativeUpdateTopControlsState(this.mNativeWebContentsAndroid, enableHiding, enableShowing, animate);
    }

    public void showImeIfNeeded() {
        nativeShowImeIfNeeded(this.mNativeWebContentsAndroid);
    }

    public void scrollFocusedEditableNodeIntoView() {
        nativeScrollFocusedEditableNodeIntoView(this.mNativeWebContentsAndroid);
    }

    public void selectWordAroundCaret() {
        nativeSelectWordAroundCaret(this.mNativeWebContentsAndroid);
    }

    public void adjustSelectionByCharacterOffset(int startAdjust, int endAdjust) {
        nativeAdjustSelectionByCharacterOffset(this.mNativeWebContentsAndroid, startAdjust, endAdjust);
    }

    public String getUrl() {
        if (isDestroyed()) {
            return null;
        }
        return nativeGetURL(this.mNativeWebContentsAndroid);
    }

    public String getLastCommittedUrl() {
        return nativeGetLastCommittedURL(this.mNativeWebContentsAndroid);
    }

    public boolean isIncognito() {
        return nativeIsIncognito(this.mNativeWebContentsAndroid);
    }

    public void resumeLoadingCreatedWebContents() {
        nativeResumeLoadingCreatedWebContents(this.mNativeWebContentsAndroid);
    }

    public void evaluateJavaScript(String script, JavaScriptCallback callback) {
        if (!isDestroyed() && script != null) {
            nativeEvaluateJavaScript(this.mNativeWebContentsAndroid, script, callback);
        }
    }

    @VisibleForTesting
    public void evaluateJavaScriptForTests(String script, JavaScriptCallback callback) {
        if (script != null) {
            nativeEvaluateJavaScriptForTests(this.mNativeWebContentsAndroid, script, callback);
        }
    }

    public void addMessageToDevToolsConsole(int level, String message) {
        nativeAddMessageToDevToolsConsole(this.mNativeWebContentsAndroid, level, message);
    }

    public void sendMessageToFrame(String frameName, String message, String targetOrigin) {
        nativeSendMessageToFrame(this.mNativeWebContentsAndroid, frameName, message, targetOrigin);
    }

    public boolean hasAccessedInitialDocument() {
        return nativeHasAccessedInitialDocument(this.mNativeWebContentsAndroid);
    }

    @CalledByNative
    private static void onEvaluateJavaScriptResult(String jsonResult, JavaScriptCallback callback) {
        callback.handleJavaScriptResult(jsonResult);
    }

    public int getThemeColor() {
        return nativeGetThemeColor(this.mNativeWebContentsAndroid);
    }

    public void requestAccessibilitySnapshot(AccessibilitySnapshotCallback callback) {
        nativeRequestAccessibilitySnapshot(this.mNativeWebContentsAndroid, callback);
    }

    public void resumeMediaSession() {
        nativeResumeMediaSession(this.mNativeWebContentsAndroid);
    }

    public void suspendMediaSession() {
        nativeSuspendMediaSession(this.mNativeWebContentsAndroid);
    }

    public void stopMediaSession() {
        nativeStopMediaSession(this.mNativeWebContentsAndroid);
    }

    public String getEncoding() {
        return nativeGetEncoding(this.mNativeWebContentsAndroid);
    }

    @CalledByNative
    private static void onAccessibilitySnapshot(AccessibilitySnapshotNode root, AccessibilitySnapshotCallback callback) {
        callback.onAccessibilitySnapshot(root);
    }

    @CalledByNative
    private static void addAccessibilityNodeAsChild(AccessibilitySnapshotNode parent, AccessibilitySnapshotNode child) {
        parent.addChild(child);
    }

    @CalledByNative
    private static AccessibilitySnapshotNode createAccessibilitySnapshotNode(int parentRelativeLeft, int parentRelativeTop, int width, int height, boolean isRootNode, String text, int color, int bgcolor, float size, int textStyle, String className) {
        AccessibilitySnapshotNode node = new AccessibilitySnapshotNode(text, className);
        if (((double) size) >= 0.0d) {
            node.setStyle(color, bgcolor, size, (textStyle & 1) > 0, (textStyle & 2) > 0, (textStyle & 4) > 0, (textStyle & 8) > 0);
        }
        node.setLocationInfo(parentRelativeLeft, parentRelativeTop, width, height, isRootNode);
        return node;
    }

    @CalledByNative
    private static void setAccessibilitySnapshotSelection(AccessibilitySnapshotNode node, int start, int end) {
        node.setSelection(start, end);
    }

    public void addObserver(WebContentsObserver observer) {
        if ($assertionsDisabled || this.mNativeWebContentsAndroid != 0) {
            if (this.mObserverProxy == null) {
                this.mObserverProxy = new WebContentsObserverProxy(this);
            }
            this.mObserverProxy.addObserver(observer);
            return;
        }
        throw new AssertionError();
    }

    public void removeObserver(WebContentsObserver observer) {
        if (this.mObserverProxy != null) {
            this.mObserverProxy.removeObserver(observer);
        }
    }

    @VisibleForTesting
    public RewindableIterator<WebContentsObserver> getObserversForTesting() {
        return this.mObserverProxy.getObserversForTesting();
    }

    public void getContentBitmapAsync(Config config, float scale, Rect srcRect, ContentBitmapCallback callback) {
        nativeGetContentBitmap(this.mNativeWebContentsAndroid, callback, config, scale, (float) srcRect.left, (float) srcRect.top, (float) srcRect.width(), (float) srcRect.height());
    }

    public void onContextMenuOpened() {
        this.mContextMenuOpened = true;
    }

    public void onContextMenuClosed() {
        if (this.mContextMenuOpened) {
            this.mContextMenuOpened = false;
            if (this.mNativeWebContentsAndroid != 0) {
                nativeOnContextMenuClosed(this.mNativeWebContentsAndroid);
            }
        }
    }

    @CalledByNative
    private void onGetContentBitmapFinished(ContentBitmapCallback callback, Bitmap bitmap, int response) {
        callback.onFinishGetBitmap(bitmap, response);
    }

    public void reloadLoFiImages() {
        nativeReloadLoFiImages(this.mNativeWebContentsAndroid);
    }

    public int downloadImage(String url, boolean isFavicon, int maxBitmapSize, boolean bypassCache, ImageDownloadCallback callback) {
        return nativeDownloadImage(this.mNativeWebContentsAndroid, url, isFavicon, maxBitmapSize, bypassCache, callback);
    }

    @CalledByNative
    private void onDownloadImageFinished(ImageDownloadCallback callback, int id, int httpStatusCode, String imageUrl, List<Bitmap> bitmaps, List<Rect> sizes) {
        callback.onFinishDownloadImage(id, httpStatusCode, imageUrl, bitmaps, sizes);
    }

    @CalledByNative
    private static List<Bitmap> createBitmapList() {
        return new ArrayList();
    }

    @CalledByNative
    private static void addToBitmapList(List<Bitmap> bitmaps, Bitmap bitmap) {
        bitmaps.add(bitmap);
    }

    @CalledByNative
    private static List<Rect> createSizeList() {
        return new ArrayList();
    }

    @CalledByNative
    private static void createSizeAndAddToList(List<Rect> sizes, int width, int height) {
        sizes.add(new Rect(0, 0, width, height));
    }
}
