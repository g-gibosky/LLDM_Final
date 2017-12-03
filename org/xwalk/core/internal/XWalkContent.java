package org.xwalk.core.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.net.http.SslCertificate;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ValueCallback;
import android.widget.FrameLayout.LayoutParams;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.components.navigation_interception.InterceptNavigationDelegate;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content.browser.ContentViewRenderView;
import org.chromium.content.browser.ContentViewRenderView.CompositingSurfaceType;
import org.chromium.content.browser.ContentViewStatics;
import org.chromium.content.common.CleanupReference;
import org.chromium.content_public.browser.ContentBitmapCallback;
import org.chromium.content_public.browser.JavaScriptCallback;
import org.chromium.content_public.browser.LoadUrlParams;
import org.chromium.content_public.browser.NavigationController;
import org.chromium.content_public.browser.NavigationHistory;
import org.chromium.content_public.browser.WebContents;
import org.chromium.media.MediaPlayerBridge;
import org.chromium.ui.base.ActivityWindowAndroid;
import org.chromium.ui.base.WindowAndroid;
import org.chromium.ui.gfx.DeviceDisplayInfo;
import org.json.JSONArray;
import org.xwalk.core.internal.XWalkContentsClient.WebResourceRequestInner;
import org.xwalk.core.internal.XWalkDevToolsServer.Security;
import org.xwalk.core.internal.XWalkGeolocationPermissions.Callback;

@JNINamespace("xwalk")
class XWalkContent implements KeyValueChangeListener {
    static final /* synthetic */ boolean $assertionsDisabled = (!XWalkContent.class.desiredAssertionStatus());
    public static final String SAVE_RESTORE_STATE_KEY = "XWALKVIEW_STATE";
    private static String TAG = "XWalkContent";
    private static Class<? extends Annotation> javascriptInterfaceClass = null;
    private static boolean timerPaused = false;
    private boolean mAnimated = false;
    private CleanupReference mCleanupReference;
    private XWalkContentView mContentView;
    private ContentViewCore mContentViewCore;
    private ContentViewRenderView mContentViewRenderView;
    private XWalkContentsClientBridge mContentsClientBridge;
    private double mDIPScale;
    private XWalkDevToolsServer mDevToolsServer;
    private XWalkGeolocationPermissions mGeolocationPermissions;
    private ContentBitmapCallback mGetBitmapCallback;
    private XWalkContentsIoThreadClient mIoThreadClient;
    private boolean mIsLoaded = false;
    private XWalkLaunchScreenManager mLaunchScreenManager;
    long mNativeContent;
    private NavigationController mNavigationController;
    private final HitTestData mPossiblyStaleHitTestData = new HitTestData();
    private XWalkSettingsInternal mSettings;
    private Context mViewContext;
    private WebContents mWebContents;
    private WindowAndroid mWindow;
    private XWalkAutofillClientAndroid mXWalkAutofillClient;
    private XWalkWebContentsDelegateAdapter mXWalkContentsDelegateAdapter;
    private XWalkGetBitmapCallbackInternal mXWalkGetBitmapCallbackInternal;
    private XWalkViewInternal mXWalkView;

    class C03357 implements Runnable {
        C03357() {
        }

        public void run() {
            XWalkContent.this.hideAutofillPopup();
        }
    }

    private static final class DestroyRunnable implements Runnable {
        private final long mNativeContent;

        private DestroyRunnable(long nativeXWalkContent) {
            this.mNativeContent = nativeXWalkContent;
        }

        public void run() {
            XWalkContent.nativeDestroy(this.mNativeContent);
        }
    }

    public static class HitTestData {
        public String anchorText;
        public String hitTestResultExtraData;
        public int hitTestResultType;
        public String href;
        public String imgSrc;
    }

    class C04741 implements ContentBitmapCallback {
        C04741() {
        }

        public void onFinishGetBitmap(Bitmap bitmap, int response) {
            if (XWalkContent.this.mXWalkGetBitmapCallbackInternal != null) {
                XWalkContent.this.mXWalkGetBitmapCallbackInternal.onFinishGetBitmap(bitmap, response);
            }
        }
    }

    class C04763 implements ZoomSupportChangeListener {
        C04763() {
        }

        public void onGestureZoomSupportChanged(boolean supportsDoubleTapZoom, boolean supportsMultiTouchZoom) {
            XWalkContent.this.mContentViewCore.updateDoubleTapSupport(supportsDoubleTapZoom);
            XWalkContent.this.mContentViewCore.updateMultiTouchZoomSupport(supportsMultiTouchZoom);
        }
    }

    private class XWalkGeolocationCallback implements Callback {
        private XWalkGeolocationCallback() {
        }

        public void invoke(final String origin, final boolean allow, final boolean retain) {
            ThreadUtils.runOnUiThread(new Runnable() {
                public void run() {
                    if (retain) {
                        if (allow) {
                            XWalkContent.this.mGeolocationPermissions.allow(origin);
                        } else {
                            XWalkContent.this.mGeolocationPermissions.deny(origin);
                        }
                    }
                    XWalkContent.this.nativeInvokeGeolocationCallback(XWalkContent.this.mNativeContent, allow, origin);
                }
            });
        }
    }

    private class XWalkIoThreadClientImpl extends XWalkContentsIoThreadClient {
        private XWalkIoThreadClientImpl() {
        }

        public int getCacheMode() {
            return XWalkContent.this.mSettings.getCacheMode();
        }

        public XWalkWebResourceResponseInternal shouldInterceptRequest(WebResourceRequestInner request) {
            XWalkContent.this.mContentsClientBridge.getCallbackHelper().postOnResourceLoadStarted(request.url);
            XWalkWebResourceResponseInternal xwalkWebResourceResponse = XWalkContent.this.mContentsClientBridge.shouldInterceptRequest(request);
            if (xwalkWebResourceResponse == null) {
                XWalkContent.this.mContentsClientBridge.getCallbackHelper().postOnLoadResource(request.url);
            } else if (request.isMainFrame && xwalkWebResourceResponse.getData() == null) {
                XWalkContent.this.mContentsClientBridge.getCallbackHelper().postOnReceivedError(-1, null, request.url);
            }
            return xwalkWebResourceResponse;
        }

        public boolean shouldBlockContentUrls() {
            return !XWalkContent.this.mSettings.getAllowContentAccess();
        }

        public boolean shouldBlockFileUrls() {
            return !XWalkContent.this.mSettings.getAllowFileAccess();
        }

        public boolean shouldBlockNetworkLoads() {
            return XWalkContent.this.mSettings.getBlockNetworkLoads();
        }

        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            XWalkContent.this.mContentsClientBridge.getCallbackHelper().postOnDownloadStart(url, userAgent, contentDisposition, mimeType, contentLength);
        }

        public void newLoginRequest(String realm, String account, String args) {
            XWalkContent.this.mContentsClientBridge.getCallbackHelper().postOnReceivedLoginRequest(realm, account, args);
        }

        public void onReceivedResponseHeaders(WebResourceRequestInner request, XWalkWebResourceResponseInternal response) {
            XWalkContent.this.mContentsClientBridge.getCallbackHelper().postOnReceivedResponseHeaders(request, response);
        }
    }

    private native void nativeClearCache(long j, boolean z);

    private native void nativeClearCacheForSingleFile(long j, String str);

    private native void nativeClearMatches(long j);

    private static native void nativeDestroy(long j);

    private native String nativeDevToolsAgentId(long j);

    private native void nativeFindAllAsync(long j, String str);

    private native void nativeFindNext(long j, boolean z);

    private native byte[] nativeGetCertificate(long j);

    private native int nativeGetRoutingID(long j);

    private native byte[] nativeGetState(long j);

    private native String nativeGetVersion(long j);

    private native WebContents nativeGetWebContents(long j);

    private native long nativeInit();

    private native void nativeInvokeGeolocationCallback(long j, boolean z, String str);

    private native long nativeReleasePopupXWalkContent(long j);

    private native void nativeRequestNewHitTestDataAt(long j, float f, float f2, float f3);

    private native void nativeSetBackgroundColor(long j, int i);

    private native void nativeSetJavaPeers(long j, XWalkContent xWalkContent, XWalkWebContentsDelegateAdapter xWalkWebContentsDelegateAdapter, XWalkContentsClientBridge xWalkContentsClientBridge, XWalkContentsIoThreadClient xWalkContentsIoThreadClient, InterceptNavigationDelegate interceptNavigationDelegate);

    private native void nativeSetJsOnlineProperty(long j, boolean z);

    private native boolean nativeSetManifest(long j, String str, String str2);

    private native void nativeSetOriginAccessWhitelist(long j, String str, String str2);

    private native boolean nativeSetState(long j, byte[] bArr);

    private native void nativeUpdateLastHitTestData(long j);

    static void setJavascriptInterfaceClass(Class<? extends Annotation> clazz) {
        if ($assertionsDisabled || javascriptInterfaceClass == null) {
            javascriptInterfaceClass = clazz;
            return;
        }
        throw new AssertionError();
    }

    public XWalkContent(Context context, String animatable, XWalkViewInternal xwView) {
        this.mXWalkView = xwView;
        this.mViewContext = this.mXWalkView.getContext();
        this.mContentsClientBridge = new XWalkContentsClientBridge(this.mXWalkView);
        this.mXWalkContentsDelegateAdapter = new XWalkWebContentsDelegateAdapter(this.mContentsClientBridge);
        this.mIoThreadClient = new XWalkIoThreadClientImpl();
        this.mWindow = WindowAndroid.activityFromContext(context) != null ? new ActivityWindowAndroid(context) : new WindowAndroid(context);
        this.mGeolocationPermissions = new XWalkGeolocationPermissions(new InMemorySharedPreferences());
        MediaPlayerBridge.setResourceLoadingFilter(new XWalkMediaPlayerResourceLoadingFilter());
        setNativeContent(nativeInit(), animatable);
        XWalkPreferencesInternal.load(this);
        initCaptureBitmapAsync();
    }

    private void initCaptureBitmapAsync() {
        this.mGetBitmapCallback = new C04741();
    }

    public void captureBitmapAsync(XWalkGetBitmapCallbackInternal callback) {
        if (this.mNativeContent != 0) {
            this.mXWalkGetBitmapCallbackInternal = callback;
            this.mWebContents.getContentBitmapAsync(Config.ARGB_8888, 1.0f, new Rect(), this.mGetBitmapCallback);
        }
    }

    private void setNativeContent(long newNativeContent, String animatable) {
        if (this.mNativeContent != 0) {
            destroy();
            this.mContentViewCore = null;
        }
        if ($assertionsDisabled || (this.mNativeContent == 0 && this.mCleanupReference == null && this.mContentViewCore == null)) {
            if (animatable == null) {
                this.mAnimated = XWalkPreferencesInternal.getValue("animatable-xwalk-view");
            } else {
                this.mAnimated = animatable.equalsIgnoreCase("true");
            }
            CompositingSurfaceType surfaceType = this.mAnimated ? CompositingSurfaceType.TEXTURE_VIEW : CompositingSurfaceType.SURFACE_VIEW;
            Log.d(TAG, "CompositingSurfaceType is " + (this.mAnimated ? "TextureView" : "SurfaceView"));
            this.mContentViewRenderView = new ContentViewRenderView(this.mViewContext, surfaceType) {
                protected void onReadyToRender() {
                }
            };
            this.mContentViewRenderView.onNativeLibraryLoaded(this.mWindow);
            this.mLaunchScreenManager = new XWalkLaunchScreenManager(this.mViewContext, this.mXWalkView);
            this.mContentViewRenderView.registerFirstRenderedFrameListener(this.mLaunchScreenManager);
            this.mXWalkView.addView(this.mContentViewRenderView, new LayoutParams(-1, -1));
            this.mNativeContent = newNativeContent;
            this.mCleanupReference = new CleanupReference(this, new DestroyRunnable(this.mNativeContent));
            this.mWebContents = nativeGetWebContents(this.mNativeContent);
            this.mContentViewCore = new ContentViewCore(this.mViewContext);
            this.mContentView = XWalkContentView.createContentView(this.mViewContext, this.mContentViewCore, this.mXWalkView);
            this.mContentViewCore.initialize(this.mContentView, this.mContentView, this.mWebContents, this.mWindow);
            this.mNavigationController = this.mWebContents.getNavigationController();
            this.mXWalkView.addView(this.mContentView, new LayoutParams(-1, -1));
            this.mContentViewCore.setContentViewClient(this.mContentsClientBridge);
            this.mContentViewRenderView.setCurrentContentViewCore(this.mContentViewCore);
            this.mContentsClientBridge.installWebContentsObserver(this.mWebContents);
            this.mSettings = new XWalkSettingsInternal(this.mViewContext, this.mWebContents, false);
            this.mSettings.setAllowFileAccessFromFileURLs(true);
            this.mDIPScale = DeviceDisplayInfo.create(this.mViewContext).getDIPScale();
            this.mContentsClientBridge.setDIPScale(this.mDIPScale);
            this.mSettings.setDIPScale(this.mDIPScale);
            String language = Locale.getDefault().toString().replaceAll("_", "-").toLowerCase();
            if (language.isEmpty()) {
                language = "en";
            }
            this.mSettings.setAcceptLanguages(language);
            this.mSettings.setZoomListener(new C04763());
            nativeSetJavaPeers(this.mNativeContent, this, this.mXWalkContentsDelegateAdapter, this.mContentsClientBridge, this.mIoThreadClient, this.mContentsClientBridge.getInterceptNavigationDelegate());
            return;
        }
        throw new AssertionError();
    }

    public void supplyContentsForPopup(XWalkContent newContents) {
        if (this.mNativeContent != 0) {
            long popupNativeXWalkContent = nativeReleasePopupXWalkContent(this.mNativeContent);
            if (popupNativeXWalkContent == 0) {
                Log.w(TAG, "Popup XWalkView bind failed: no pending content.");
                if (newContents != null) {
                    newContents.destroy();
                }
            } else if (newContents == null) {
                nativeDestroy(popupNativeXWalkContent);
            } else {
                newContents.receivePopupContents(popupNativeXWalkContent);
            }
        }
    }

    private void receivePopupContents(long popupNativeXWalkContents) {
        setNativeContent(popupNativeXWalkContents, null);
        this.mContentViewCore.onShow();
    }

    private void doLoadUrl(LoadUrlParams params) {
        params.setOverrideUserAgent(2);
        this.mNavigationController.loadUrl(params);
        this.mContentView.requestFocus();
        this.mIsLoaded = true;
    }

    private static String fixupBase(String url) {
        return TextUtils.isEmpty(url) ? "about:blank" : url;
    }

    private static String fixupData(String data) {
        return TextUtils.isEmpty(data) ? "" : data;
    }

    private static String fixupHistory(String url) {
        return TextUtils.isEmpty(url) ? "about:blank" : url;
    }

    private static String fixupMimeType(String mimeType) {
        return TextUtils.isEmpty(mimeType) ? "text/html" : mimeType;
    }

    private static boolean isBase64Encoded(String encoding) {
        return "base64".equals(encoding);
    }

    public void loadData(String data, String mimeType, String encoding) {
        if (this.mNativeContent != 0) {
            if (TextUtils.isEmpty(data)) {
                data = "";
            }
            if (TextUtils.isEmpty(mimeType)) {
                mimeType = "text/html";
            }
            doLoadUrl(LoadUrlParams.createLoadDataParams(fixupData(data), fixupMimeType(mimeType), isBase64Encoded(encoding)));
        }
    }

    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        if (this.mNativeContent != 0) {
            data = fixupData(data);
            mimeType = fixupMimeType(mimeType);
            baseUrl = fixupBase(baseUrl);
            try {
                doLoadUrl(LoadUrlParams.createLoadDataParamsWithBaseUrl(Base64.encodeToString(data.getBytes("utf-8"), 0), mimeType, true, baseUrl, fixupHistory(historyUrl), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                Log.w(TAG, "Unable to load data string " + data, e);
            }
        }
    }

    public void loadUrl(String url) {
        if (url != null) {
            loadUrl(url, null);
        }
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        if (this.mNativeContent != 0) {
            LoadUrlParams params = new LoadUrlParams(url);
            if (additionalHttpHeaders != null) {
                params.setExtraHeaders(additionalHttpHeaders);
            }
            doLoadUrl(params);
        }
    }

    public void reload(int mode) {
        if (this.mNativeContent != 0) {
            switch (mode) {
                case 1:
                    this.mNavigationController.reloadBypassingCache(true);
                    break;
                default:
                    this.mNavigationController.reload(true);
                    break;
            }
            this.mIsLoaded = true;
        }
    }

    public String getUrl() {
        if (this.mNativeContent == 0) {
            return null;
        }
        String url = this.mWebContents.getUrl();
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        return url;
    }

    public String getTitle() {
        if (this.mNativeContent == 0) {
            return null;
        }
        String title = this.mWebContents.getTitle().trim();
        if (title == null) {
            return "";
        }
        return title;
    }

    public void addJavascriptInterface(Object object, String name) {
        if (this.mNativeContent != 0) {
            this.mContentViewCore.addPossiblyUnsafeJavascriptInterface(object, name, javascriptInterfaceClass);
        }
    }

    public void removeJavascriptInterface(String name) {
        if (this.mNativeContent != 0) {
            this.mContentViewCore.removeJavascriptInterface(name);
        }
    }

    public void evaluateJavascript(String script, ValueCallback<String> callback) {
        if (this.mNativeContent != 0) {
            final ValueCallback<String> fCallback = callback;
            JavaScriptCallback coreCallback = null;
            if (fCallback != null) {
                coreCallback = new JavaScriptCallback() {
                    public void handleJavaScriptResult(String jsonResult) {
                        fCallback.onReceiveValue(jsonResult);
                    }
                };
            }
            this.mContentViewCore.getWebContents().evaluateJavaScript(script, coreCallback);
        }
    }

    public void setUIClient(XWalkUIClientInternal client) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setUIClient(client);
        }
    }

    public void setResourceClient(XWalkResourceClientInternal client) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setResourceClient(client);
        }
    }

    public void setXWalkWebChromeClient(XWalkWebChromeClient client) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setXWalkWebChromeClient(client);
        }
    }

    public XWalkWebChromeClient getXWalkWebChromeClient() {
        if (this.mNativeContent == 0) {
            return null;
        }
        return this.mContentsClientBridge.getXWalkWebChromeClient();
    }

    public int getContentHeight() {
        return (int) Math.ceil((double) this.mContentViewCore.getContentHeightCss());
    }

    public void setXWalkClient(XWalkClient client) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setXWalkClient(client);
        }
    }

    public void setDownloadListener(XWalkDownloadListenerInternal listener) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setDownloadListener(listener);
        }
    }

    public void setNavigationHandler(XWalkNavigationHandler handler) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setNavigationHandler(handler);
        }
    }

    public void setNotificationService(XWalkNotificationService service) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setNotificationService(service);
        }
    }

    public void onPause() {
        if (this.mNativeContent != 0) {
            this.mContentViewCore.onHide();
        }
    }

    public void onResume() {
        if (this.mNativeContent != 0) {
            this.mContentViewCore.onShow();
        }
    }

    public boolean onNewIntent(Intent intent) {
        if (this.mNativeContent == 0) {
            return false;
        }
        return this.mContentsClientBridge.onNewIntent(intent);
    }

    public void clearCache(boolean includeDiskFiles) {
        if (this.mNativeContent != 0) {
            nativeClearCache(this.mNativeContent, includeDiskFiles);
        }
    }

    public void clearCacheForSingleFile(final String url) {
        if (this.mNativeContent != 0) {
            if (this.mIsLoaded) {
                nativeClearCacheForSingleFile(this.mNativeContent, url);
            } else {
                this.mXWalkView.post(new Runnable() {
                    public void run() {
                        XWalkContent.this.clearCacheForSingleFile(url);
                    }
                });
            }
        }
    }

    public void clearHistory() {
        if (this.mNativeContent != 0) {
            this.mNavigationController.clearHistory();
        }
    }

    public boolean canGoBack() {
        return this.mNativeContent == 0 ? false : this.mNavigationController.canGoBack();
    }

    public void goBack() {
        if (this.mNativeContent != 0) {
            this.mNavigationController.goBack();
        }
    }

    public boolean canGoForward() {
        return this.mNativeContent == 0 ? false : this.mNavigationController.canGoForward();
    }

    public void goForward() {
        if (this.mNativeContent != 0) {
            this.mNavigationController.goForward();
        }
    }

    void navigateTo(int offset) {
        this.mNavigationController.goToOffset(offset);
    }

    public void stopLoading() {
        if (this.mNativeContent != 0) {
            this.mWebContents.stop();
            this.mContentsClientBridge.onStopLoading();
        }
    }

    public Bitmap getFavicon() {
        if (this.mNativeContent == 0) {
            return null;
        }
        return this.mContentsClientBridge.getFavicon();
    }

    public void pauseTimers() {
        if (!timerPaused && this.mNativeContent != 0) {
            ContentViewStatics.setWebKitSharedTimersSuspended(true);
            timerPaused = true;
        }
    }

    public void resumeTimers() {
        if (timerPaused && this.mNativeContent != 0) {
            ContentViewStatics.setWebKitSharedTimersSuspended(false);
            timerPaused = false;
        }
    }

    public String getOriginalUrl() {
        if (this.mNativeContent == 0) {
            return null;
        }
        NavigationHistory history = this.mNavigationController.getNavigationHistory();
        int currentIndex = history.getCurrentEntryIndex();
        if (currentIndex < 0 || currentIndex >= history.getEntryCount()) {
            return null;
        }
        return history.getEntryAtIndex(currentIndex).getOriginalUrl();
    }

    public HitTestData getLastHitTestResult() {
        if (this.mNativeContent == 0) {
            return null;
        }
        nativeUpdateLastHitTestData(this.mNativeContent);
        return this.mPossiblyStaleHitTestData;
    }

    public String getXWalkVersion() {
        if (this.mNativeContent == 0) {
            return "";
        }
        return nativeGetVersion(this.mNativeContent);
    }

    private boolean isOpaque(int color) {
        return ((color >> 24) & 255) == 255;
    }

    @CalledByNative
    public void setBackgroundColor(final int color) {
        if (this.mNativeContent != 0) {
            if (this.mIsLoaded) {
                if (isOpaque(color)) {
                    setOverlayVideoMode(false);
                    this.mContentViewCore.setBackgroundOpaque(true);
                } else {
                    setOverlayVideoMode(true);
                    this.mContentViewCore.setBackgroundOpaque(false);
                }
                this.mContentViewCore.setBackgroundColor(color);
                this.mContentViewRenderView.setSurfaceViewBackgroundColor(color);
                nativeSetBackgroundColor(this.mNativeContent, color);
                return;
            }
            this.mXWalkView.post(new Runnable() {
                public void run() {
                    XWalkContent.this.setBackgroundColor(color);
                }
            });
        }
    }

    public void setNetworkAvailable(boolean networkUp) {
        if (this.mNativeContent != 0) {
            nativeSetJsOnlineProperty(this.mNativeContent, networkUp);
        }
    }

    public ContentViewCore getContentViewCoreForTest() {
        return this.mContentViewCore;
    }

    public void installWebContentsObserverForTest(XWalkContentsClient contentClient) {
        if (this.mNativeContent != 0) {
            contentClient.installWebContentsObserver(this.mContentViewCore.getWebContents());
        }
    }

    public String devToolsAgentId() {
        if (this.mNativeContent == 0) {
            return "";
        }
        return nativeDevToolsAgentId(this.mNativeContent);
    }

    public XWalkSettingsInternal getSettings() {
        return this.mSettings;
    }

    public void loadAppFromManifest(String url, String data) {
        if (this.mNativeContent == 0) {
            return;
        }
        if ((url != null && !url.isEmpty()) || (data != null && !data.isEmpty())) {
            String content = data;
            if (data == null || data.isEmpty()) {
                try {
                    content = AndroidProtocolHandler.getUrlContent(this.mXWalkView.getContext(), url);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read the manifest: " + url);
                }
            }
            String baseUrl = url;
            int position = url.lastIndexOf("/");
            if (position != -1) {
                baseUrl = url.substring(0, position + 1);
            } else {
                Log.w(TAG, "The url of manifest.json is probably not set correctly.");
            }
            if (nativeSetManifest(this.mNativeContent, baseUrl, content)) {
                this.mIsLoaded = true;
                return;
            }
            throw new RuntimeException("Failed to parse the manifest file: " + url);
        }
    }

    public void setOriginAccessWhitelist(String url, String[] patterns) {
        if (this.mNativeContent != 0 && !TextUtils.isEmpty(url)) {
            String matchPatterns = "";
            if (patterns != null) {
                matchPatterns = new JSONArray(Arrays.asList(patterns)).toString();
            }
            nativeSetOriginAccessWhitelist(this.mNativeContent, url, matchPatterns);
        }
    }

    public XWalkNavigationHistoryInternal getNavigationHistory() {
        if (this.mNativeContent == 0) {
            return null;
        }
        return new XWalkNavigationHistoryInternal(this.mXWalkView, this.mNavigationController.getNavigationHistory());
    }

    public XWalkNavigationHistoryInternal saveState(Bundle outState) {
        if (this.mNativeContent == 0 || outState == null) {
            return null;
        }
        byte[] state = nativeGetState(this.mNativeContent);
        if (state == null) {
            return null;
        }
        outState.putByteArray(SAVE_RESTORE_STATE_KEY, state);
        return getNavigationHistory();
    }

    public XWalkNavigationHistoryInternal restoreState(Bundle inState) {
        if (this.mNativeContent == 0 || inState == null) {
            return null;
        }
        byte[] state = inState.getByteArray(SAVE_RESTORE_STATE_KEY);
        if (state == null) {
            return null;
        }
        boolean result = nativeSetState(this.mNativeContent, state);
        if (result) {
            this.mContentsClientBridge.onUpdateTitle(this.mWebContents.getTitle());
        }
        if (result) {
            return getNavigationHistory();
        }
        return null;
    }

    boolean hasEnteredFullscreen() {
        return this.mContentsClientBridge.hasEnteredFullscreen();
    }

    void exitFullscreen() {
        if (hasEnteredFullscreen()) {
            this.mWebContents.exitFullscreen();
        }
    }

    @CalledByNative
    public void onGetUrlFromManifest(String url) {
        if (url != null && !url.isEmpty()) {
            loadUrl(url);
        }
    }

    @CalledByNative
    public void onGetUrlAndLaunchScreenFromManifest(String url, String readyWhen, String imageBorder) {
        if (url != null && !url.isEmpty()) {
            this.mLaunchScreenManager.displayLaunchScreen(readyWhen, imageBorder);
            this.mContentsClientBridge.registerPageLoadListener(this.mLaunchScreenManager);
            loadUrl(url);
        }
    }

    @CalledByNative
    public void onGetFullscreenFlagFromManifest(boolean enterFullscreen) {
        if (this.mXWalkView.getContext() instanceof Activity) {
            Activity activity = (Activity) this.mXWalkView.getContext();
            if (!enterFullscreen) {
                return;
            }
            if (VERSION.SDK_INT >= 19) {
                activity.getWindow().getDecorView().setSystemUiVisibility(5894);
            } else {
                activity.getWindow().addFlags(1024);
            }
        }
    }

    public void destroy() {
        if (this.mNativeContent != 0) {
            XWalkPreferencesInternal.unload(this);
            setNotificationService(null);
            this.mXWalkView.removeView(this.mContentView);
            this.mXWalkView.removeView(this.mContentViewRenderView);
            this.mContentViewRenderView.setCurrentContentViewCore(null);
            this.mCleanupReference.cleanupNow();
            this.mContentViewRenderView.destroy();
            this.mContentViewCore.destroy();
            this.mCleanupReference = null;
            this.mNativeContent = 0;
        }
    }

    public int getRoutingID() {
        return nativeGetRoutingID(this.mNativeContent);
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.mContentView.onCreateInputConnectionSuper(outAttrs);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == 0) {
            nativeRequestNewHitTestDataAt(this.mNativeContent, event.getX() / ((float) this.mDIPScale), event.getY() / ((float) this.mDIPScale), event.getTouchMajor() / ((float) this.mDIPScale));
        }
        return this.mContentViewCore.onTouchEvent(event);
    }

    public void setOnTouchListener(OnTouchListener l) {
        this.mContentView.setOnTouchListener(l);
    }

    public void scrollTo(int x, int y) {
        this.mContentView.scrollTo(x, y);
    }

    public void scrollBy(int x, int y) {
        this.mContentView.scrollBy(x, y);
    }

    public int computeHorizontalScrollRange() {
        return this.mContentView.computeHorizontalScrollRangeDelegate();
    }

    public int computeHorizontalScrollOffset() {
        return this.mContentView.computeHorizontalScrollOffsetDelegate();
    }

    public int computeVerticalScrollRange() {
        return this.mContentView.computeVerticalScrollRangeDelegate();
    }

    public int computeVerticalScrollOffset() {
        return this.mContentView.computeVerticalScrollOffsetDelegate();
    }

    public int computeVerticalScrollExtent() {
        return this.mContentView.computeVerticalScrollExtentDelegate();
    }

    @CalledByNative
    private void onGeolocationPermissionsShowPrompt(String origin) {
        if (this.mNativeContent != 0) {
            if (!this.mSettings.getGeolocationEnabled()) {
                nativeInvokeGeolocationCallback(this.mNativeContent, false, origin);
            } else if (this.mGeolocationPermissions.hasOrigin(origin)) {
                nativeInvokeGeolocationCallback(this.mNativeContent, this.mGeolocationPermissions.isOriginAllowed(origin), origin);
            } else {
                this.mContentsClientBridge.onGeolocationPermissionsShowPrompt(origin, new XWalkGeolocationCallback());
            }
        }
    }

    @CalledByNative
    public void onGeolocationPermissionsHidePrompt() {
        this.mContentsClientBridge.onGeolocationPermissionsHidePrompt();
    }

    @CalledByNative
    private void updateHitTestData(int type, String extra, String href, String anchorText, String imgSrc) {
        this.mPossiblyStaleHitTestData.hitTestResultType = type;
        this.mPossiblyStaleHitTestData.hitTestResultExtraData = extra;
        this.mPossiblyStaleHitTestData.href = href;
        this.mPossiblyStaleHitTestData.anchorText = anchorText;
        this.mPossiblyStaleHitTestData.imgSrc = imgSrc;
    }

    public void enableRemoteDebugging() {
        String socketName = this.mViewContext.getApplicationContext().getPackageName() + "_devtools_remote";
        if (this.mDevToolsServer == null) {
            this.mDevToolsServer = new XWalkDevToolsServer(socketName);
            this.mDevToolsServer.setRemoteDebuggingEnabled(true, Security.ALLOW_SOCKET_ACCESS);
        }
    }

    void disableRemoteDebugging() {
        if (this.mDevToolsServer != null) {
            if (this.mDevToolsServer.isRemoteDebuggingEnabled()) {
                this.mDevToolsServer.setRemoteDebuggingEnabled(false);
            }
            this.mDevToolsServer.destroy();
            this.mDevToolsServer = null;
        }
    }

    public String getRemoteDebuggingUrl() {
        if (this.mDevToolsServer == null) {
            return "";
        }
        return "ws://" + this.mDevToolsServer.getSocketName() + "/devtools/page/" + devToolsAgentId();
    }

    public void onKeyValueChanged(String key, PreferenceValue value) {
        if (key != null) {
            if (key.equals("remote-debugging")) {
                if (value.getBooleanValue()) {
                    enableRemoteDebugging();
                } else {
                    disableRemoteDebugging();
                }
            } else if (key.equals("enable-javascript")) {
                if (this.mSettings != null) {
                    this.mSettings.setJavaScriptEnabled(value.getBooleanValue());
                }
            } else if (key.equals("javascript-can-open-window")) {
                if (this.mSettings != null) {
                    this.mSettings.setJavaScriptCanOpenWindowsAutomatically(value.getBooleanValue());
                }
            } else if (key.equals("allow-universal-access-from-file")) {
                if (this.mSettings != null) {
                    this.mSettings.setAllowUniversalAccessFromFileURLs(value.getBooleanValue());
                }
            } else if (key.equals("support-multiple-windows")) {
                if (this.mSettings != null) {
                    this.mSettings.setSupportMultipleWindows(value.getBooleanValue());
                }
            } else if (key.equals("enable-spatial-navigation") && this.mSettings != null) {
                this.mSettings.setSupportSpatialNavigation(value.getBooleanValue());
            }
        }
    }

    public void setOverlayVideoMode(boolean enabled) {
        if (this.mContentViewRenderView != null) {
            this.mContentViewRenderView.setOverlayVideoMode(enabled);
        }
    }

    public void setZOrderOnTop(boolean onTop) {
        if (this.mContentViewRenderView != null) {
            this.mContentViewRenderView.setZOrderOnTop(onTop);
        }
    }

    public boolean zoomIn() {
        if (this.mNativeContent == 0) {
            return false;
        }
        return this.mContentViewCore.zoomIn();
    }

    public boolean zoomOut() {
        if (this.mNativeContent == 0) {
            return false;
        }
        return this.mContentViewCore.zoomOut();
    }

    public void zoomBy(float delta) {
        if (this.mNativeContent != 0) {
            if (delta < 0.01f || delta > 100.0f) {
                throw new IllegalStateException("zoom delta value outside [0.01, 100] range.");
            }
            this.mContentViewCore.pinchByDelta(delta);
        }
    }

    public boolean canZoomIn() {
        if (this.mNativeContent == 0) {
            return false;
        }
        return this.mContentViewCore.canZoomIn();
    }

    public boolean canZoomOut() {
        if (this.mNativeContent == 0) {
            return false;
        }
        return this.mContentViewCore.canZoomOut();
    }

    public void hideAutofillPopup() {
        if (this.mNativeContent != 0) {
            if (!this.mIsLoaded) {
                this.mXWalkView.post(new C03357());
            } else if (this.mXWalkAutofillClient != null) {
                this.mXWalkAutofillClient.hideAutofillPopup();
            }
        }
    }

    public void setVisibility(int visibility) {
        SurfaceView surfaceView = this.mContentViewRenderView.getSurfaceView();
        if (surfaceView != null) {
            surfaceView.setVisibility(visibility);
        }
    }

    @CalledByNative
    private void setXWalkAutofillClient(XWalkAutofillClientAndroid client) {
        this.mXWalkAutofillClient = client;
        client.init(this.mContentViewCore);
    }

    public void clearSslPreferences() {
        if (this.mNativeContent != 0) {
            this.mNavigationController.clearSslPreferences();
        }
    }

    public void clearClientCertPreferences(Runnable callback) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.clearClientCertPreferences(callback);
        }
    }

    public SslCertificate getCertificate() {
        if (this.mNativeContent == 0) {
            return null;
        }
        return SslUtil.getCertificateFromDerBytes(nativeGetCertificate(this.mNativeContent));
    }

    public boolean hasPermission(String permission) {
        if (this.mNativeContent == 0) {
            return false;
        }
        return this.mWindow.hasPermission(permission);
    }

    public void setFindListener(XWalkFindListenerInternal listener) {
        if (this.mNativeContent != 0) {
            this.mContentsClientBridge.setFindListener(listener);
        }
    }

    public void findAllAsync(String searchString) {
        if (this.mNativeContent != 0) {
            nativeFindAllAsync(this.mNativeContent, searchString);
        }
    }

    public void findNext(boolean forward) {
        if (this.mNativeContent != 0) {
            nativeFindNext(this.mNativeContent, forward);
        }
    }

    public void clearMatches() {
        if (this.mNativeContent != 0) {
            nativeClearMatches(this.mNativeContent);
        }
    }

    public String getCompositingSurfaceType() {
        if (this.mNativeContent == 0) {
            return null;
        }
        return this.mAnimated ? "TextureView" : "SurfaceView";
    }

    @CalledByNative
    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
        this.mContentsClientBridge.onFindResultReceived(activeMatchOrdinal, numberOfMatches, isDoneCounting);
    }
}
