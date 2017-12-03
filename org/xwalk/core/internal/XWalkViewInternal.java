package org.xwalk.core.internal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;
import java.io.File;
import java.util.Map;
import org.chromium.base.ApplicationStatusManager;
import org.chromium.base.CommandLine;
import org.chromium.content.browser.ContentViewCore;
import org.xwalk.core.internal.XWalkContent.HitTestData;
import org.xwalk.core.internal.extension.BuiltinXWalkExtensions;

@XWalkAPI(createExternally = true, extendClass = FrameLayout.class)
public class XWalkViewInternal extends FrameLayout {
    static final String PLAYSTORE_DETAIL_URI = "market://details?id=";
    @XWalkAPI
    public static final int RELOAD_IGNORE_CACHE = 1;
    @XWalkAPI
    public static final int RELOAD_NORMAL = 0;
    @XWalkAPI
    public static final String SURFACE_VIEW = "SurfaceView";
    private static final String TAG = XWalkViewInternal.class.getSimpleName();
    @XWalkAPI
    public static final String TEXTURE_VIEW = "TextureView";
    private XWalkContent mContent;
    private Context mContext = getContext();
    private XWalkExternalExtensionManagerInternal mExternalExtensionManager;
    private boolean mIsHidden;
    private final XWalkHitTestResultInternal mXWalkHitTestResult = new XWalkHitTestResultInternal();

    @XWalkAPI(postWrapperLines = {"        addView((FrameLayout)bridge, new FrameLayout.LayoutParams(", "                FrameLayout.LayoutParams.MATCH_PARENT,", "                FrameLayout.LayoutParams.MATCH_PARENT));", "        removeViewAt(0);", "        new org.xwalk.core.extension.XWalkExternalExtensionManagerImpl(this);"}, preWrapperLines = {"        super(${param1}, null);", "        SurfaceView surfaceView = new SurfaceView(${param1});", "        surfaceView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));", "        addView(surfaceView);"})
    public XWalkViewInternal(Context context) {
        super(context, null);
        checkThreadSafety();
        initXWalkContent(null);
    }

    @XWalkAPI(postBridgeLines = {"        String animatable = null;", "        try {", "            animatable = (String) new ReflectField(wrapper, \"mAnimatable\").get();", "        } catch (RuntimeException e) {", "        }", "        initXWalkContent(animatable);"}, postWrapperLines = {"        addView((FrameLayout)bridge, new FrameLayout.LayoutParams(", "                FrameLayout.LayoutParams.MATCH_PARENT,", "                FrameLayout.LayoutParams.MATCH_PARENT));", "        removeViewAt(0);", "        new org.xwalk.core.extension.XWalkExternalExtensionManagerImpl(this);"}, preWrapperLines = {"        super(${param1}, ${param2});", "        if (isInEditMode()) return;", "        if (${param2} != null)", "            mAnimatable = ${param2}.getAttributeValue(", "                    XWALK_ATTRS_NAMESPACE, ANIMATABLE);", "        SurfaceView surfaceView = new SurfaceView(${param1});", "        surfaceView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));", "        addView(surfaceView);"})
    public XWalkViewInternal(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkThreadSafety();
    }

    @Deprecated
    @XWalkAPI(postWrapperLines = {"        addView((FrameLayout)bridge, new FrameLayout.LayoutParams(", "                FrameLayout.LayoutParams.MATCH_PARENT,", "                FrameLayout.LayoutParams.MATCH_PARENT));", "        removeViewAt(0);", "        new org.xwalk.core.extension.XWalkExternalExtensionManagerImpl(this);"}, preWrapperLines = {"        super(${param1}, null);", "        SurfaceView surfaceView = new SurfaceView(${param1});", "        surfaceView.setLayoutParams(new ViewGroup.LayoutParams(0, 0));", "        addView(surfaceView);"})
    public XWalkViewInternal(Context context, Activity activity) {
        super(context, null);
        checkThreadSafety();
        initXWalkContent(null);
    }

    public Context getViewContext() {
        return this.mContext;
    }

    public void completeWindowCreation(XWalkViewInternal newXWalkView) {
        this.mContent.supplyContentsForPopup(newXWalkView == null ? null : newXWalkView.mContent);
    }

    protected void initXWalkContent(String animatable) {
        XWalkViewDelegate.init(null, this.mContext);
        if (this.mContext instanceof Activity) {
            ApplicationStatusManager.informActivityStarted((Activity) this.mContext);
        }
        if (CommandLine.getInstance().hasSwitch("disable-xwalk-extensions")) {
            XWalkPreferencesInternal.setValue("enable-extensions", false);
        } else {
            BuiltinXWalkExtensions.load(this.mContext);
        }
        this.mIsHidden = false;
        this.mContent = new XWalkContent(this.mContext, animatable, this);
        this.mContent.resumeTimers();
        setXWalkClient(new XWalkClient(this));
        setXWalkWebChromeClient(new XWalkWebChromeClient());
        setUIClient(new XWalkUIClientInternal(this));
        setResourceClient(new XWalkResourceClientInternal(this));
        setDownloadListener(new XWalkDownloadListenerImpl(this.mContext));
        setNavigationHandler(new XWalkNavigationHandlerImpl(this.mContext));
        setNotificationService(new XWalkNotificationServiceImpl(this.mContext, this));
        XWalkPathHelper.initialize();
        XWalkPathHelper.setCacheDirectory(this.mContext.getApplicationContext().getCacheDir().getPath());
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state) || "mounted_ro".equals(state)) {
            File extCacheDir = this.mContext.getApplicationContext().getExternalCacheDir();
            if (extCacheDir != null) {
                XWalkPathHelper.setExternalCacheDirectory(extCacheDir.getPath());
            }
        }
    }

    @Deprecated
    @XWalkAPI
    public void load(String url, String content) {
        load(url, content, null);
    }

    @Deprecated
    @XWalkAPI
    public void load(String url, String content, Map<String, String> headers) {
        if (this.mContent != null) {
            checkThreadSafety();
            if ((url != null && !url.isEmpty()) || (content != null && !content.isEmpty())) {
                if (url != null && !url.isEmpty() && TextUtils.equals(url, getUrl())) {
                    reload(0);
                } else if (content == null || content.isEmpty()) {
                    this.mContent.loadUrl(url, headers);
                } else {
                    this.mContent.loadDataWithBaseURL(url, content, "text/html", null, null);
                }
            }
        }
    }

    @XWalkAPI
    public void loadData(String data, String mimeType, String encoding) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.loadData(data, mimeType, encoding);
        }
    }

    @XWalkAPI
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
        }
    }

    @XWalkAPI
    public void loadUrl(String url) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.loadUrl(url);
        }
    }

    @XWalkAPI
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.loadUrl(url, additionalHttpHeaders);
        }
    }

    @XWalkAPI
    public void loadAppFromManifest(String url, String content) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.loadAppFromManifest(url, content);
        }
    }

    @XWalkAPI
    public void reload(int mode) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.reload(mode);
        }
    }

    @XWalkAPI
    public void stopLoading() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.stopLoading();
        }
    }

    @XWalkAPI
    public String getUrl() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getUrl();
    }

    @XWalkAPI
    public XWalkHitTestResultInternal getHitTestResult() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        HitTestData data = this.mContent.getLastHitTestResult();
        this.mXWalkHitTestResult.setType(data.hitTestResultType);
        this.mXWalkHitTestResult.setExtra(data.hitTestResultExtraData);
        return this.mXWalkHitTestResult;
    }

    @XWalkAPI
    public int getContentHeight() {
        return this.mContent.getContentHeight();
    }

    @XWalkAPI
    public String getTitle() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getTitle();
    }

    @XWalkAPI
    public String getOriginalUrl() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getOriginalUrl();
    }

    @XWalkAPI
    public XWalkNavigationHistoryInternal getNavigationHistory() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getNavigationHistory();
    }

    @XWalkAPI(reservable = true)
    public void addJavascriptInterface(Object object, String name) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.addJavascriptInterface(object, name);
        }
    }

    @XWalkAPI(reservable = true)
    public void removeJavascriptInterface(String name) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.removeJavascriptInterface(name);
        }
    }

    @XWalkAPI
    public void evaluateJavascript(String script, ValueCallback<String> callback) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.evaluateJavascript(script, callback);
        }
    }

    @XWalkAPI
    public void clearCache(boolean includeDiskFiles) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.clearCache(includeDiskFiles);
        }
    }

    @XWalkAPI
    public void clearCacheForSingleFile(String url) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.clearCacheForSingleFile(url);
        }
    }

    @XWalkAPI
    public boolean hasEnteredFullscreen() {
        if (this.mContent == null) {
            return false;
        }
        checkThreadSafety();
        return this.mContent.hasEnteredFullscreen();
    }

    @XWalkAPI
    public void leaveFullscreen() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.exitFullscreen();
        }
    }

    @XWalkAPI
    public void pauseTimers() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.pauseTimers();
        }
    }

    @XWalkAPI
    public void resumeTimers() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.resumeTimers();
        }
    }

    @XWalkAPI
    public void onHide() {
        if (this.mContent != null && !this.mIsHidden) {
            this.mContent.onPause();
            this.mIsHidden = true;
        }
    }

    @XWalkAPI
    public void onShow() {
        if (this.mContent != null && this.mIsHidden) {
            this.mContent.onResume();
            this.mIsHidden = false;
        }
    }

    @XWalkAPI
    public void onDestroy() {
        destroy();
    }

    @Deprecated
    @XWalkAPI
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        throw new ActivityNotFoundException("This method is no longer supported");
    }

    @Deprecated
    @XWalkAPI
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @XWalkAPI
    public boolean onNewIntent(Intent intent) {
        if (this.mContent == null) {
            return false;
        }
        if (this.mExternalExtensionManager != null) {
            this.mExternalExtensionManager.onNewIntent(intent);
        }
        return this.mContent.onNewIntent(intent);
    }

    @XWalkAPI
    public boolean saveState(Bundle outState) {
        if (this.mContent == null) {
            return false;
        }
        this.mContent.saveState(outState);
        return true;
    }

    @XWalkAPI
    public boolean restoreState(Bundle inState) {
        if (this.mContent == null || this.mContent.restoreState(inState) == null) {
            return false;
        }
        return true;
    }

    @XWalkAPI
    public String getAPIVersion() {
        return String.valueOf(8) + ".0";
    }

    @XWalkAPI
    public String getXWalkVersion() {
        if (this.mContent == null) {
            return null;
        }
        return this.mContent.getXWalkVersion();
    }

    @XWalkAPI(reservable = true)
    public void setUIClient(XWalkUIClientInternal client) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setUIClient(client);
        }
    }

    @XWalkAPI(reservable = true)
    public void setResourceClient(XWalkResourceClientInternal client) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setResourceClient(client);
        }
    }

    @XWalkAPI
    public void setBackgroundColor(int color) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setBackgroundColor(color);
        }
    }

    @XWalkAPI
    public void setOriginAccessWhitelist(String url, String[] patterns) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setOriginAccessWhitelist(url, patterns);
        }
    }

    @XWalkAPI(disableReflectMethod = true, preWrapperLines = {"        return;"})
    public void setLayerType(int layerType, Paint paint) {
    }

    @XWalkAPI
    public void setUserAgentString(String userAgent) {
        XWalkSettingsInternal settings = getSettings();
        if (settings != null) {
            checkThreadSafety();
            settings.setUserAgentString(userAgent);
        }
    }

    @XWalkAPI
    public String getUserAgentString() {
        XWalkSettingsInternal settings = getSettings();
        if (settings == null) {
            return null;
        }
        checkThreadSafety();
        return settings.getUserAgentString();
    }

    @XWalkAPI
    public void setAcceptLanguages(String acceptLanguages) {
        XWalkSettingsInternal settings = getSettings();
        if (settings != null) {
            checkThreadSafety();
            settings.setAcceptLanguages(acceptLanguages);
        }
    }

    @XWalkAPI
    public void captureBitmapAsync(XWalkGetBitmapCallbackInternal callback) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.captureBitmapAsync(callback);
        }
    }

    @XWalkAPI
    public XWalkSettingsInternal getSettings() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getSettings();
    }

    @XWalkAPI
    public void setNetworkAvailable(boolean networkUp) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setNetworkAvailable(networkUp);
        }
    }

    public void enableRemoteDebugging() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.enableRemoteDebugging();
        }
    }

    @XWalkAPI
    public Uri getRemoteDebuggingUrl() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        String wsUrl = this.mContent.getRemoteDebuggingUrl();
        if (wsUrl == null || wsUrl.isEmpty()) {
            return null;
        }
        return Uri.parse(wsUrl);
    }

    @XWalkAPI
    public boolean zoomIn() {
        if (this.mContent == null) {
            return false;
        }
        checkThreadSafety();
        return this.mContent.zoomIn();
    }

    @XWalkAPI
    public boolean zoomOut() {
        if (this.mContent == null) {
            return false;
        }
        checkThreadSafety();
        return this.mContent.zoomOut();
    }

    @XWalkAPI
    public void zoomBy(float factor) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.zoomBy(factor);
        }
    }

    @XWalkAPI
    public boolean canZoomIn() {
        if (this.mContent == null) {
            return false;
        }
        checkThreadSafety();
        return this.mContent.canZoomIn();
    }

    @XWalkAPI
    public boolean canZoomOut() {
        if (this.mContent == null) {
            return false;
        }
        checkThreadSafety();
        return this.mContent.canZoomOut();
    }

    @XWalkAPI
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.mContent.onCreateInputConnection(outAttrs);
    }

    @XWalkAPI
    public void setInitialScale(int scaleInPercent) {
        checkThreadSafety();
        XWalkSettingsInternal settings = getSettings();
        if (settings != null) {
            settings.setInitialPageScale((float) scaleInPercent);
        }
    }

    public int getContentID() {
        if (this.mContent == null) {
            return -1;
        }
        return this.mContent.getRoutingID();
    }

    boolean canGoBack() {
        if (this.mContent == null) {
            return false;
        }
        checkThreadSafety();
        return this.mContent.canGoBack();
    }

    void goBack() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.goBack();
        }
    }

    boolean canGoForward() {
        if (this.mContent == null) {
            return false;
        }
        checkThreadSafety();
        return this.mContent.canGoForward();
    }

    void goForward() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.goForward();
        }
    }

    void clearHistory() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.clearHistory();
        }
    }

    void destroy() {
        if (this.mContent != null) {
            this.mContent.destroy();
            disableRemoteDebugging();
        }
    }

    void disableRemoteDebugging() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.disableRemoteDebugging();
        }
    }

    private static void checkThreadSafety() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new RuntimeException(new Throwable("Warning: A XWalkViewInternal method was called on thread '" + Thread.currentThread().getName() + "'. " + "All XWalkViewInternal methods must be called on the UI thread. "));
        }
    }

    void navigateTo(int offset) {
        if (this.mContent != null) {
            this.mContent.navigateTo(offset);
        }
    }

    void setOverlayVideoMode(boolean enabled) {
        this.mContent.setOverlayVideoMode(enabled);
    }

    @XWalkAPI
    public Bitmap getFavicon() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getFavicon();
    }

    @XWalkAPI
    public void setZOrderOnTop(boolean onTop) {
        if (this.mContent != null) {
            this.mContent.setZOrderOnTop(onTop);
        }
    }

    @XWalkAPI
    public void clearFormData() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.hideAutofillPopup();
        }
    }

    @XWalkAPI(disableReflectMethod = true, preWrapperLines = {"        if (visibility == View.INVISIBLE) visibility = View.GONE;", "        super.setVisibility(visibility);", "        setXWalkViewInternalVisibility(visibility);", "        setSurfaceViewVisibility(visibility);"})
    public void setVisibility(int visibility) {
    }

    @XWalkAPI(reservable = true)
    public void setSurfaceViewVisibility(int visibility) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setVisibility(visibility);
        }
    }

    @XWalkAPI(reservable = true)
    public void setXWalkViewInternalVisibility(int visibility) {
        if (this.mContent != null) {
            checkThreadSafety();
            super.setVisibility(visibility);
        }
    }

    public void setXWalkClient(XWalkClient client) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setXWalkClient(client);
        }
    }

    public void setXWalkWebChromeClient(XWalkWebChromeClient client) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setXWalkWebChromeClient(client);
        }
    }

    @XWalkAPI(reservable = true)
    public void setDownloadListener(XWalkDownloadListenerInternal listener) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setDownloadListener(listener);
        }
    }

    public void setNavigationHandler(XWalkNavigationHandler handler) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setNavigationHandler(handler);
        }
    }

    public void setNotificationService(XWalkNotificationService service) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setNotificationService(service);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 1 && event.getKeyCode() == 4) {
            if (hasEnteredFullscreen()) {
                leaveFullscreen();
                return true;
            } else if (canGoBack()) {
                goBack();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public ContentViewCore getXWalkContentForTest() {
        return this.mContent.getContentViewCoreForTest();
    }

    @XWalkAPI(delegate = true, preWrapperLines = {"return performLongClick();"})
    public boolean performLongClickDelegate() {
        return false;
    }

    @XWalkAPI(delegate = true, preWrapperLines = {"return onTouchEvent(event);"})
    public boolean onTouchEventDelegate(MotionEvent event) {
        return false;
    }

    @XWalkAPI
    public boolean onTouchEvent(MotionEvent event) {
        return this.mContent.onTouchEvent(event);
    }

    @XWalkAPI(delegate = true, preWrapperLines = {"onScrollChanged(l, t, oldl, oldt);"})
    public void onScrollChangedDelegate(int l, int t, int oldl, int oldt) {
    }

    @XWalkAPI(delegate = true, preWrapperLines = {"onFocusChanged(gainFocus, direction, previouslyFocusedRect);"})
    public void onFocusChangedDelegate(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
    }

    @XWalkAPI(delegate = true, preWrapperLines = {"onOverScrolled(scrollX, scrollY, clampedX, clampedY);"})
    public void onOverScrolledDelegate(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
    }

    @XWalkAPI
    public void setOnTouchListener(OnTouchListener l) {
        this.mContent.setOnTouchListener(l);
    }

    @XWalkAPI
    public void scrollTo(int x, int y) {
        this.mContent.scrollTo(x, y);
    }

    @XWalkAPI
    public void scrollBy(int x, int y) {
        this.mContent.scrollBy(x, y);
    }

    @XWalkAPI
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int overScrollMode = super.getOverScrollMode();
        boolean canScrollHorizontal = computeHorizontalScrollRange() > computeHorizontalScrollExtent();
        boolean canScrollVertical = computeVerticalScrollRange() > computeVerticalScrollExtent();
        boolean overScrollHorizontal = overScrollMode == 0 || (overScrollMode == 1 && canScrollHorizontal);
        boolean overScrollVertical = overScrollMode == 0 || (overScrollMode == 1 && canScrollVertical);
        int newScrollX = scrollX + deltaX;
        if (!overScrollHorizontal) {
            maxOverScrollX = 0;
        }
        int newScrollY = scrollY + deltaY;
        if (!overScrollVertical) {
            maxOverScrollY = 0;
        }
        int left = -maxOverScrollX;
        int right = maxOverScrollX + scrollRangeX;
        int top = -maxOverScrollY;
        int bottom = maxOverScrollY + scrollRangeY;
        boolean clampedX = false;
        if (newScrollX > right) {
            newScrollX = right;
            clampedX = true;
        } else if (newScrollX < left) {
            newScrollX = left;
            clampedX = true;
        }
        boolean clampedY = false;
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < top) {
            newScrollY = top;
            clampedY = true;
        }
        scrollTo(newScrollX, newScrollY);
        if (clampedX || clampedY) {
            return true;
        }
        return false;
    }

    @XWalkAPI
    public int computeHorizontalScrollRange() {
        return this.mContent.computeHorizontalScrollRange();
    }

    @XWalkAPI
    public int computeHorizontalScrollOffset() {
        return this.mContent.computeHorizontalScrollOffset();
    }

    @XWalkAPI
    public int computeVerticalScrollRange() {
        return this.mContent.computeVerticalScrollRange();
    }

    @XWalkAPI
    public int computeVerticalScrollOffset() {
        return this.mContent.computeVerticalScrollOffset();
    }

    @XWalkAPI
    public int computeVerticalScrollExtent() {
        return this.mContent.computeVerticalScrollExtent();
    }

    @XWalkAPI
    public XWalkExternalExtensionManagerInternal getExtensionManager() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mExternalExtensionManager;
    }

    public void setExternalExtensionManager(XWalkExternalExtensionManagerInternal manager) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mExternalExtensionManager = manager;
        }
    }

    @XWalkAPI
    public void clearSslPreferences() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.clearSslPreferences();
        }
    }

    @XWalkAPI
    public void clearClientCertPreferences(Runnable callback) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.clearClientCertPreferences(callback);
        }
    }

    @XWalkAPI
    public SslCertificate getCertificate() {
        if (this.mContent == null) {
            return null;
        }
        checkThreadSafety();
        return this.mContent.getCertificate();
    }

    @XWalkAPI(reservable = true)
    public void setFindListener(XWalkFindListenerInternal listener) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.setFindListener(listener);
        }
    }

    @XWalkAPI
    public void findAllAsync(String searchString) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.findAllAsync(searchString);
        }
    }

    @XWalkAPI
    public void findNext(boolean forward) {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.findNext(forward);
        }
    }

    @XWalkAPI
    public void clearMatches() {
        if (this.mContent != null) {
            checkThreadSafety();
            this.mContent.clearMatches();
        }
    }

    @XWalkAPI
    public String getCompositingSurfaceType() {
        checkThreadSafety();
        if (this.mContent == null) {
            return null;
        }
        return this.mContent.getCompositingSurfaceType();
    }
}
