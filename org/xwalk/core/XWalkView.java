package org.xwalk.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Map;
import org.xwalk.core.extension.XWalkExternalExtensionManagerImpl;

public class XWalkView extends FrameLayout {
    static final /* synthetic */ boolean $assertionsDisabled = (!XWalkView.class.desiredAssertionStatus());
    private static final String ANIMATABLE = "animatable";
    public static final int RELOAD_IGNORE_CACHE = 1;
    public static final int RELOAD_NORMAL = 0;
    public static final String SURFACE_VIEW = "SurfaceView";
    public static final String TEXTURE_VIEW = "TextureView";
    private static final String XWALK_ATTRS_NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private ReflectMethod addJavascriptInterfaceObjectStringMethod;
    private Object bridge;
    private ReflectMethod canZoomInMethod;
    private ReflectMethod canZoomOutMethod;
    private ReflectMethod captureBitmapAsyncXWalkGetBitmapCallbackInternalMethod;
    private ReflectMethod clearCacheForSingleFileStringMethod;
    private ReflectMethod clearCachebooleanMethod;
    private ReflectMethod clearClientCertPreferencesRunnableMethod;
    private ReflectMethod clearFormDataMethod;
    private ReflectMethod clearMatchesMethod;
    private ReflectMethod clearSslPreferencesMethod;
    private ReflectMethod computeHorizontalScrollOffsetMethod;
    private ReflectMethod computeHorizontalScrollRangeMethod;
    private ReflectMethod computeVerticalScrollExtentMethod;
    private ReflectMethod computeVerticalScrollOffsetMethod;
    private ReflectMethod computeVerticalScrollRangeMethod;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod evaluateJavascriptStringValueCallbackMethod;
    private ReflectMethod findAllAsyncStringMethod;
    private ReflectMethod findNextbooleanMethod;
    private ReflectMethod getAPIVersionMethod;
    private ReflectMethod getCertificateMethod;
    private ReflectMethod getCompositingSurfaceTypeMethod;
    private ReflectMethod getContentHeightMethod;
    private ReflectMethod getExtensionManagerMethod;
    private ReflectMethod getFaviconMethod;
    private ReflectMethod getHitTestResultMethod;
    private ReflectMethod getNavigationHistoryMethod;
    private ReflectMethod getOriginalUrlMethod;
    private ReflectMethod getRemoteDebuggingUrlMethod;
    private ReflectMethod getSettingsMethod;
    private ReflectMethod getTitleMethod;
    private ReflectMethod getUrlMethod;
    private ReflectMethod getUserAgentStringMethod;
    private ReflectMethod getXWalkVersionMethod;
    private ReflectMethod hasEnteredFullscreenMethod;
    private ReflectMethod leaveFullscreenMethod;
    private ReflectMethod loadAppFromManifestStringStringMethod;
    private ReflectMethod loadDataStringStringStringMethod;
    private ReflectMethod loadDataWithBaseURLStringStringStringStringStringMethod;
    private ReflectMethod loadStringStringMapMethod;
    private ReflectMethod loadStringStringMethod;
    private ReflectMethod loadUrlStringMapMethod;
    private ReflectMethod loadUrlStringMethod;
    private String mAnimatable;
    private ReflectMethod onActivityResultintintIntentMethod;
    private ReflectMethod onCreateInputConnectionEditorInfoMethod;
    private ReflectMethod onDestroyMethod;
    private ReflectMethod onHideMethod;
    private ReflectMethod onNewIntentIntentMethod;
    private ReflectMethod onShowMethod;
    private ReflectMethod onTouchEventMotionEventMethod;
    private ReflectMethod pauseTimersMethod;
    private ReflectMethod postWrapperMethod;
    private ReflectMethod reloadintMethod;
    private ReflectMethod removeJavascriptInterfaceStringMethod;
    private ReflectMethod restoreStateBundleMethod;
    private ReflectMethod resumeTimersMethod;
    private ReflectMethod saveStateBundleMethod;
    private ReflectMethod scrollByintintMethod;
    private ReflectMethod scrollTointintMethod;
    private ReflectMethod setAcceptLanguagesStringMethod;
    private ReflectMethod setBackgroundColorintMethod;
    private ReflectMethod setDownloadListenerXWalkDownloadListenerInternalMethod;
    private ReflectMethod setFindListenerXWalkFindListenerInternalMethod;
    private ReflectMethod setInitialScaleintMethod;
    private ReflectMethod setNetworkAvailablebooleanMethod;
    private ReflectMethod setOnTouchListenerOnTouchListenerMethod;
    private ReflectMethod setOriginAccessWhitelistStringStringArrayMethod;
    private ReflectMethod setResourceClientXWalkResourceClientInternalMethod;
    private ReflectMethod setSurfaceViewVisibilityintMethod;
    private ReflectMethod setUIClientXWalkUIClientInternalMethod;
    private ReflectMethod setUserAgentStringStringMethod;
    private ReflectMethod setXWalkViewInternalVisibilityintMethod;
    private ReflectMethod setZOrderOnTopbooleanMethod;
    private ReflectMethod startActivityForResultIntentintBundleMethod;
    private ReflectMethod stopLoadingMethod;
    private ReflectMethod zoomByfloatMethod;
    private ReflectMethod zoomInMethod;
    private ReflectMethod zoomOutMethod;

    protected Object getBridge() {
        return this.bridge;
    }

    public XWalkView(Context context) {
        super(context, null);
        this.loadStringStringMethod = new ReflectMethod(null, "load", new Class[0]);
        this.loadStringStringMapMethod = new ReflectMethod(null, "load", new Class[0]);
        this.loadDataStringStringStringMethod = new ReflectMethod(null, "loadData", new Class[0]);
        this.loadDataWithBaseURLStringStringStringStringStringMethod = new ReflectMethod(null, "loadDataWithBaseURL", new Class[0]);
        this.loadUrlStringMethod = new ReflectMethod(null, "loadUrl", new Class[0]);
        this.loadUrlStringMapMethod = new ReflectMethod(null, "loadUrl", new Class[0]);
        this.loadAppFromManifestStringStringMethod = new ReflectMethod(null, "loadAppFromManifest", new Class[0]);
        this.reloadintMethod = new ReflectMethod(null, "reload", new Class[0]);
        this.stopLoadingMethod = new ReflectMethod(null, "stopLoading", new Class[0]);
        this.getUrlMethod = new ReflectMethod(null, "getUrl", new Class[0]);
        this.getHitTestResultMethod = new ReflectMethod(null, "getHitTestResult", new Class[0]);
        this.getContentHeightMethod = new ReflectMethod(null, "getContentHeight", new Class[0]);
        this.getTitleMethod = new ReflectMethod(null, "getTitle", new Class[0]);
        this.getOriginalUrlMethod = new ReflectMethod(null, "getOriginalUrl", new Class[0]);
        this.getNavigationHistoryMethod = new ReflectMethod(null, "getNavigationHistory", new Class[0]);
        this.addJavascriptInterfaceObjectStringMethod = new ReflectMethod(null, "addJavascriptInterface", new Class[0]);
        this.removeJavascriptInterfaceStringMethod = new ReflectMethod(null, "removeJavascriptInterface", new Class[0]);
        this.evaluateJavascriptStringValueCallbackMethod = new ReflectMethod(null, "evaluateJavascript", new Class[0]);
        this.clearCachebooleanMethod = new ReflectMethod(null, "clearCache", new Class[0]);
        this.clearCacheForSingleFileStringMethod = new ReflectMethod(null, "clearCacheForSingleFile", new Class[0]);
        this.hasEnteredFullscreenMethod = new ReflectMethod(null, "hasEnteredFullscreen", new Class[0]);
        this.leaveFullscreenMethod = new ReflectMethod(null, "leaveFullscreen", new Class[0]);
        this.pauseTimersMethod = new ReflectMethod(null, "pauseTimers", new Class[0]);
        this.resumeTimersMethod = new ReflectMethod(null, "resumeTimers", new Class[0]);
        this.onHideMethod = new ReflectMethod(null, "onHide", new Class[0]);
        this.onShowMethod = new ReflectMethod(null, "onShow", new Class[0]);
        this.onDestroyMethod = new ReflectMethod(null, "onDestroy", new Class[0]);
        this.startActivityForResultIntentintBundleMethod = new ReflectMethod(null, "startActivityForResult", new Class[0]);
        this.onActivityResultintintIntentMethod = new ReflectMethod(null, "onActivityResult", new Class[0]);
        this.onNewIntentIntentMethod = new ReflectMethod(null, "onNewIntent", new Class[0]);
        this.saveStateBundleMethod = new ReflectMethod(null, "saveState", new Class[0]);
        this.restoreStateBundleMethod = new ReflectMethod(null, "restoreState", new Class[0]);
        this.getAPIVersionMethod = new ReflectMethod(null, "getAPIVersion", new Class[0]);
        this.getXWalkVersionMethod = new ReflectMethod(null, "getXWalkVersion", new Class[0]);
        this.setUIClientXWalkUIClientInternalMethod = new ReflectMethod(null, "setUIClient", new Class[0]);
        this.setResourceClientXWalkResourceClientInternalMethod = new ReflectMethod(null, "setResourceClient", new Class[0]);
        this.setBackgroundColorintMethod = new ReflectMethod(null, "setBackgroundColor", new Class[0]);
        this.setOriginAccessWhitelistStringStringArrayMethod = new ReflectMethod(null, "setOriginAccessWhitelist", new Class[0]);
        this.setUserAgentStringStringMethod = new ReflectMethod(null, "setUserAgentString", new Class[0]);
        this.getUserAgentStringMethod = new ReflectMethod(null, "getUserAgentString", new Class[0]);
        this.setAcceptLanguagesStringMethod = new ReflectMethod(null, "setAcceptLanguages", new Class[0]);
        this.captureBitmapAsyncXWalkGetBitmapCallbackInternalMethod = new ReflectMethod(null, "captureBitmapAsync", new Class[0]);
        this.getSettingsMethod = new ReflectMethod(null, "getSettings", new Class[0]);
        this.setNetworkAvailablebooleanMethod = new ReflectMethod(null, "setNetworkAvailable", new Class[0]);
        this.getRemoteDebuggingUrlMethod = new ReflectMethod(null, "getRemoteDebuggingUrl", new Class[0]);
        this.zoomInMethod = new ReflectMethod(null, "zoomIn", new Class[0]);
        this.zoomOutMethod = new ReflectMethod(null, "zoomOut", new Class[0]);
        this.zoomByfloatMethod = new ReflectMethod(null, "zoomBy", new Class[0]);
        this.canZoomInMethod = new ReflectMethod(null, "canZoomIn", new Class[0]);
        this.canZoomOutMethod = new ReflectMethod(null, "canZoomOut", new Class[0]);
        this.onCreateInputConnectionEditorInfoMethod = new ReflectMethod(null, "onCreateInputConnection", new Class[0]);
        this.setInitialScaleintMethod = new ReflectMethod(null, "setInitialScale", new Class[0]);
        this.getFaviconMethod = new ReflectMethod(null, "getFavicon", new Class[0]);
        this.setZOrderOnTopbooleanMethod = new ReflectMethod(null, "setZOrderOnTop", new Class[0]);
        this.clearFormDataMethod = new ReflectMethod(null, "clearFormData", new Class[0]);
        this.setSurfaceViewVisibilityintMethod = new ReflectMethod(null, "setSurfaceViewVisibility", new Class[0]);
        this.setXWalkViewInternalVisibilityintMethod = new ReflectMethod(null, "setXWalkViewInternalVisibility", new Class[0]);
        this.setDownloadListenerXWalkDownloadListenerInternalMethod = new ReflectMethod(null, "setDownloadListener", new Class[0]);
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        this.setOnTouchListenerOnTouchListenerMethod = new ReflectMethod(null, "setOnTouchListener", new Class[0]);
        this.scrollTointintMethod = new ReflectMethod(null, "scrollTo", new Class[0]);
        this.scrollByintintMethod = new ReflectMethod(null, "scrollBy", new Class[0]);
        this.computeHorizontalScrollRangeMethod = new ReflectMethod(null, "computeHorizontalScrollRange", new Class[0]);
        this.computeHorizontalScrollOffsetMethod = new ReflectMethod(null, "computeHorizontalScrollOffset", new Class[0]);
        this.computeVerticalScrollRangeMethod = new ReflectMethod(null, "computeVerticalScrollRange", new Class[0]);
        this.computeVerticalScrollOffsetMethod = new ReflectMethod(null, "computeVerticalScrollOffset", new Class[0]);
        this.computeVerticalScrollExtentMethod = new ReflectMethod(null, "computeVerticalScrollExtent", new Class[0]);
        this.getExtensionManagerMethod = new ReflectMethod(null, "getExtensionManager", new Class[0]);
        this.clearSslPreferencesMethod = new ReflectMethod(null, "clearSslPreferences", new Class[0]);
        this.clearClientCertPreferencesRunnableMethod = new ReflectMethod(null, "clearClientCertPreferences", new Class[0]);
        this.getCertificateMethod = new ReflectMethod(null, "getCertificate", new Class[0]);
        this.setFindListenerXWalkFindListenerInternalMethod = new ReflectMethod(null, "setFindListener", new Class[0]);
        this.findAllAsyncStringMethod = new ReflectMethod(null, "findAllAsync", new Class[0]);
        this.findNextbooleanMethod = new ReflectMethod(null, "findNext", new Class[0]);
        this.clearMatchesMethod = new ReflectMethod(null, "clearMatches", new Class[0]);
        this.getCompositingSurfaceTypeMethod = new ReflectMethod(null, "getCompositingSurfaceType", new Class[0]);
        SurfaceView surfaceView = new SurfaceView(context);
        surfaceView.setLayoutParams(new LayoutParams(0, 0));
        addView(surfaceView);
        this.constructorTypes = new ArrayList();
        this.constructorTypes.add(Context.class);
        this.constructorParams = new ArrayList();
        this.constructorParams.add(context);
        this.postWrapperMethod = new ReflectMethod((Object) this, "postXWalkViewInternalContextConstructor", new Class[0]);
        reflectionInit();
    }

    public void postXWalkViewInternalContextConstructor() {
        addView((FrameLayout) this.bridge, new FrameLayout.LayoutParams(-1, -1));
        removeViewAt(0);
        XWalkExternalExtensionManagerImpl xWalkExternalExtensionManagerImpl = new XWalkExternalExtensionManagerImpl(this);
    }

    public XWalkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.loadStringStringMethod = new ReflectMethod(null, "load", new Class[0]);
        this.loadStringStringMapMethod = new ReflectMethod(null, "load", new Class[0]);
        this.loadDataStringStringStringMethod = new ReflectMethod(null, "loadData", new Class[0]);
        this.loadDataWithBaseURLStringStringStringStringStringMethod = new ReflectMethod(null, "loadDataWithBaseURL", new Class[0]);
        this.loadUrlStringMethod = new ReflectMethod(null, "loadUrl", new Class[0]);
        this.loadUrlStringMapMethod = new ReflectMethod(null, "loadUrl", new Class[0]);
        this.loadAppFromManifestStringStringMethod = new ReflectMethod(null, "loadAppFromManifest", new Class[0]);
        this.reloadintMethod = new ReflectMethod(null, "reload", new Class[0]);
        this.stopLoadingMethod = new ReflectMethod(null, "stopLoading", new Class[0]);
        this.getUrlMethod = new ReflectMethod(null, "getUrl", new Class[0]);
        this.getHitTestResultMethod = new ReflectMethod(null, "getHitTestResult", new Class[0]);
        this.getContentHeightMethod = new ReflectMethod(null, "getContentHeight", new Class[0]);
        this.getTitleMethod = new ReflectMethod(null, "getTitle", new Class[0]);
        this.getOriginalUrlMethod = new ReflectMethod(null, "getOriginalUrl", new Class[0]);
        this.getNavigationHistoryMethod = new ReflectMethod(null, "getNavigationHistory", new Class[0]);
        this.addJavascriptInterfaceObjectStringMethod = new ReflectMethod(null, "addJavascriptInterface", new Class[0]);
        this.removeJavascriptInterfaceStringMethod = new ReflectMethod(null, "removeJavascriptInterface", new Class[0]);
        this.evaluateJavascriptStringValueCallbackMethod = new ReflectMethod(null, "evaluateJavascript", new Class[0]);
        this.clearCachebooleanMethod = new ReflectMethod(null, "clearCache", new Class[0]);
        this.clearCacheForSingleFileStringMethod = new ReflectMethod(null, "clearCacheForSingleFile", new Class[0]);
        this.hasEnteredFullscreenMethod = new ReflectMethod(null, "hasEnteredFullscreen", new Class[0]);
        this.leaveFullscreenMethod = new ReflectMethod(null, "leaveFullscreen", new Class[0]);
        this.pauseTimersMethod = new ReflectMethod(null, "pauseTimers", new Class[0]);
        this.resumeTimersMethod = new ReflectMethod(null, "resumeTimers", new Class[0]);
        this.onHideMethod = new ReflectMethod(null, "onHide", new Class[0]);
        this.onShowMethod = new ReflectMethod(null, "onShow", new Class[0]);
        this.onDestroyMethod = new ReflectMethod(null, "onDestroy", new Class[0]);
        this.startActivityForResultIntentintBundleMethod = new ReflectMethod(null, "startActivityForResult", new Class[0]);
        this.onActivityResultintintIntentMethod = new ReflectMethod(null, "onActivityResult", new Class[0]);
        this.onNewIntentIntentMethod = new ReflectMethod(null, "onNewIntent", new Class[0]);
        this.saveStateBundleMethod = new ReflectMethod(null, "saveState", new Class[0]);
        this.restoreStateBundleMethod = new ReflectMethod(null, "restoreState", new Class[0]);
        this.getAPIVersionMethod = new ReflectMethod(null, "getAPIVersion", new Class[0]);
        this.getXWalkVersionMethod = new ReflectMethod(null, "getXWalkVersion", new Class[0]);
        this.setUIClientXWalkUIClientInternalMethod = new ReflectMethod(null, "setUIClient", new Class[0]);
        this.setResourceClientXWalkResourceClientInternalMethod = new ReflectMethod(null, "setResourceClient", new Class[0]);
        this.setBackgroundColorintMethod = new ReflectMethod(null, "setBackgroundColor", new Class[0]);
        this.setOriginAccessWhitelistStringStringArrayMethod = new ReflectMethod(null, "setOriginAccessWhitelist", new Class[0]);
        this.setUserAgentStringStringMethod = new ReflectMethod(null, "setUserAgentString", new Class[0]);
        this.getUserAgentStringMethod = new ReflectMethod(null, "getUserAgentString", new Class[0]);
        this.setAcceptLanguagesStringMethod = new ReflectMethod(null, "setAcceptLanguages", new Class[0]);
        this.captureBitmapAsyncXWalkGetBitmapCallbackInternalMethod = new ReflectMethod(null, "captureBitmapAsync", new Class[0]);
        this.getSettingsMethod = new ReflectMethod(null, "getSettings", new Class[0]);
        this.setNetworkAvailablebooleanMethod = new ReflectMethod(null, "setNetworkAvailable", new Class[0]);
        this.getRemoteDebuggingUrlMethod = new ReflectMethod(null, "getRemoteDebuggingUrl", new Class[0]);
        this.zoomInMethod = new ReflectMethod(null, "zoomIn", new Class[0]);
        this.zoomOutMethod = new ReflectMethod(null, "zoomOut", new Class[0]);
        this.zoomByfloatMethod = new ReflectMethod(null, "zoomBy", new Class[0]);
        this.canZoomInMethod = new ReflectMethod(null, "canZoomIn", new Class[0]);
        this.canZoomOutMethod = new ReflectMethod(null, "canZoomOut", new Class[0]);
        this.onCreateInputConnectionEditorInfoMethod = new ReflectMethod(null, "onCreateInputConnection", new Class[0]);
        this.setInitialScaleintMethod = new ReflectMethod(null, "setInitialScale", new Class[0]);
        this.getFaviconMethod = new ReflectMethod(null, "getFavicon", new Class[0]);
        this.setZOrderOnTopbooleanMethod = new ReflectMethod(null, "setZOrderOnTop", new Class[0]);
        this.clearFormDataMethod = new ReflectMethod(null, "clearFormData", new Class[0]);
        this.setSurfaceViewVisibilityintMethod = new ReflectMethod(null, "setSurfaceViewVisibility", new Class[0]);
        this.setXWalkViewInternalVisibilityintMethod = new ReflectMethod(null, "setXWalkViewInternalVisibility", new Class[0]);
        this.setDownloadListenerXWalkDownloadListenerInternalMethod = new ReflectMethod(null, "setDownloadListener", new Class[0]);
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        this.setOnTouchListenerOnTouchListenerMethod = new ReflectMethod(null, "setOnTouchListener", new Class[0]);
        this.scrollTointintMethod = new ReflectMethod(null, "scrollTo", new Class[0]);
        this.scrollByintintMethod = new ReflectMethod(null, "scrollBy", new Class[0]);
        this.computeHorizontalScrollRangeMethod = new ReflectMethod(null, "computeHorizontalScrollRange", new Class[0]);
        this.computeHorizontalScrollOffsetMethod = new ReflectMethod(null, "computeHorizontalScrollOffset", new Class[0]);
        this.computeVerticalScrollRangeMethod = new ReflectMethod(null, "computeVerticalScrollRange", new Class[0]);
        this.computeVerticalScrollOffsetMethod = new ReflectMethod(null, "computeVerticalScrollOffset", new Class[0]);
        this.computeVerticalScrollExtentMethod = new ReflectMethod(null, "computeVerticalScrollExtent", new Class[0]);
        this.getExtensionManagerMethod = new ReflectMethod(null, "getExtensionManager", new Class[0]);
        this.clearSslPreferencesMethod = new ReflectMethod(null, "clearSslPreferences", new Class[0]);
        this.clearClientCertPreferencesRunnableMethod = new ReflectMethod(null, "clearClientCertPreferences", new Class[0]);
        this.getCertificateMethod = new ReflectMethod(null, "getCertificate", new Class[0]);
        this.setFindListenerXWalkFindListenerInternalMethod = new ReflectMethod(null, "setFindListener", new Class[0]);
        this.findAllAsyncStringMethod = new ReflectMethod(null, "findAllAsync", new Class[0]);
        this.findNextbooleanMethod = new ReflectMethod(null, "findNext", new Class[0]);
        this.clearMatchesMethod = new ReflectMethod(null, "clearMatches", new Class[0]);
        this.getCompositingSurfaceTypeMethod = new ReflectMethod(null, "getCompositingSurfaceType", new Class[0]);
        if (!isInEditMode()) {
            if (attrs != null) {
                this.mAnimatable = attrs.getAttributeValue(XWALK_ATTRS_NAMESPACE, ANIMATABLE);
            }
            SurfaceView surfaceView = new SurfaceView(context);
            surfaceView.setLayoutParams(new LayoutParams(0, 0));
            addView(surfaceView);
            this.constructorTypes = new ArrayList();
            this.constructorTypes.add(Context.class);
            this.constructorTypes.add(AttributeSet.class);
            this.constructorParams = new ArrayList();
            this.constructorParams.add(context);
            this.constructorParams.add(attrs);
            this.postWrapperMethod = new ReflectMethod((Object) this, "postXWalkViewInternalContextAttributeSetConstructor", new Class[0]);
            reflectionInit();
        }
    }

    public void postXWalkViewInternalContextAttributeSetConstructor() {
        addView((FrameLayout) this.bridge, new FrameLayout.LayoutParams(-1, -1));
        removeViewAt(0);
        XWalkExternalExtensionManagerImpl xWalkExternalExtensionManagerImpl = new XWalkExternalExtensionManagerImpl(this);
    }

    @Deprecated
    public XWalkView(Context context, Activity activity) {
        super(context, null);
        this.loadStringStringMethod = new ReflectMethod(null, "load", new Class[0]);
        this.loadStringStringMapMethod = new ReflectMethod(null, "load", new Class[0]);
        this.loadDataStringStringStringMethod = new ReflectMethod(null, "loadData", new Class[0]);
        this.loadDataWithBaseURLStringStringStringStringStringMethod = new ReflectMethod(null, "loadDataWithBaseURL", new Class[0]);
        this.loadUrlStringMethod = new ReflectMethod(null, "loadUrl", new Class[0]);
        this.loadUrlStringMapMethod = new ReflectMethod(null, "loadUrl", new Class[0]);
        this.loadAppFromManifestStringStringMethod = new ReflectMethod(null, "loadAppFromManifest", new Class[0]);
        this.reloadintMethod = new ReflectMethod(null, "reload", new Class[0]);
        this.stopLoadingMethod = new ReflectMethod(null, "stopLoading", new Class[0]);
        this.getUrlMethod = new ReflectMethod(null, "getUrl", new Class[0]);
        this.getHitTestResultMethod = new ReflectMethod(null, "getHitTestResult", new Class[0]);
        this.getContentHeightMethod = new ReflectMethod(null, "getContentHeight", new Class[0]);
        this.getTitleMethod = new ReflectMethod(null, "getTitle", new Class[0]);
        this.getOriginalUrlMethod = new ReflectMethod(null, "getOriginalUrl", new Class[0]);
        this.getNavigationHistoryMethod = new ReflectMethod(null, "getNavigationHistory", new Class[0]);
        this.addJavascriptInterfaceObjectStringMethod = new ReflectMethod(null, "addJavascriptInterface", new Class[0]);
        this.removeJavascriptInterfaceStringMethod = new ReflectMethod(null, "removeJavascriptInterface", new Class[0]);
        this.evaluateJavascriptStringValueCallbackMethod = new ReflectMethod(null, "evaluateJavascript", new Class[0]);
        this.clearCachebooleanMethod = new ReflectMethod(null, "clearCache", new Class[0]);
        this.clearCacheForSingleFileStringMethod = new ReflectMethod(null, "clearCacheForSingleFile", new Class[0]);
        this.hasEnteredFullscreenMethod = new ReflectMethod(null, "hasEnteredFullscreen", new Class[0]);
        this.leaveFullscreenMethod = new ReflectMethod(null, "leaveFullscreen", new Class[0]);
        this.pauseTimersMethod = new ReflectMethod(null, "pauseTimers", new Class[0]);
        this.resumeTimersMethod = new ReflectMethod(null, "resumeTimers", new Class[0]);
        this.onHideMethod = new ReflectMethod(null, "onHide", new Class[0]);
        this.onShowMethod = new ReflectMethod(null, "onShow", new Class[0]);
        this.onDestroyMethod = new ReflectMethod(null, "onDestroy", new Class[0]);
        this.startActivityForResultIntentintBundleMethod = new ReflectMethod(null, "startActivityForResult", new Class[0]);
        this.onActivityResultintintIntentMethod = new ReflectMethod(null, "onActivityResult", new Class[0]);
        this.onNewIntentIntentMethod = new ReflectMethod(null, "onNewIntent", new Class[0]);
        this.saveStateBundleMethod = new ReflectMethod(null, "saveState", new Class[0]);
        this.restoreStateBundleMethod = new ReflectMethod(null, "restoreState", new Class[0]);
        this.getAPIVersionMethod = new ReflectMethod(null, "getAPIVersion", new Class[0]);
        this.getXWalkVersionMethod = new ReflectMethod(null, "getXWalkVersion", new Class[0]);
        this.setUIClientXWalkUIClientInternalMethod = new ReflectMethod(null, "setUIClient", new Class[0]);
        this.setResourceClientXWalkResourceClientInternalMethod = new ReflectMethod(null, "setResourceClient", new Class[0]);
        this.setBackgroundColorintMethod = new ReflectMethod(null, "setBackgroundColor", new Class[0]);
        this.setOriginAccessWhitelistStringStringArrayMethod = new ReflectMethod(null, "setOriginAccessWhitelist", new Class[0]);
        this.setUserAgentStringStringMethod = new ReflectMethod(null, "setUserAgentString", new Class[0]);
        this.getUserAgentStringMethod = new ReflectMethod(null, "getUserAgentString", new Class[0]);
        this.setAcceptLanguagesStringMethod = new ReflectMethod(null, "setAcceptLanguages", new Class[0]);
        this.captureBitmapAsyncXWalkGetBitmapCallbackInternalMethod = new ReflectMethod(null, "captureBitmapAsync", new Class[0]);
        this.getSettingsMethod = new ReflectMethod(null, "getSettings", new Class[0]);
        this.setNetworkAvailablebooleanMethod = new ReflectMethod(null, "setNetworkAvailable", new Class[0]);
        this.getRemoteDebuggingUrlMethod = new ReflectMethod(null, "getRemoteDebuggingUrl", new Class[0]);
        this.zoomInMethod = new ReflectMethod(null, "zoomIn", new Class[0]);
        this.zoomOutMethod = new ReflectMethod(null, "zoomOut", new Class[0]);
        this.zoomByfloatMethod = new ReflectMethod(null, "zoomBy", new Class[0]);
        this.canZoomInMethod = new ReflectMethod(null, "canZoomIn", new Class[0]);
        this.canZoomOutMethod = new ReflectMethod(null, "canZoomOut", new Class[0]);
        this.onCreateInputConnectionEditorInfoMethod = new ReflectMethod(null, "onCreateInputConnection", new Class[0]);
        this.setInitialScaleintMethod = new ReflectMethod(null, "setInitialScale", new Class[0]);
        this.getFaviconMethod = new ReflectMethod(null, "getFavicon", new Class[0]);
        this.setZOrderOnTopbooleanMethod = new ReflectMethod(null, "setZOrderOnTop", new Class[0]);
        this.clearFormDataMethod = new ReflectMethod(null, "clearFormData", new Class[0]);
        this.setSurfaceViewVisibilityintMethod = new ReflectMethod(null, "setSurfaceViewVisibility", new Class[0]);
        this.setXWalkViewInternalVisibilityintMethod = new ReflectMethod(null, "setXWalkViewInternalVisibility", new Class[0]);
        this.setDownloadListenerXWalkDownloadListenerInternalMethod = new ReflectMethod(null, "setDownloadListener", new Class[0]);
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        this.setOnTouchListenerOnTouchListenerMethod = new ReflectMethod(null, "setOnTouchListener", new Class[0]);
        this.scrollTointintMethod = new ReflectMethod(null, "scrollTo", new Class[0]);
        this.scrollByintintMethod = new ReflectMethod(null, "scrollBy", new Class[0]);
        this.computeHorizontalScrollRangeMethod = new ReflectMethod(null, "computeHorizontalScrollRange", new Class[0]);
        this.computeHorizontalScrollOffsetMethod = new ReflectMethod(null, "computeHorizontalScrollOffset", new Class[0]);
        this.computeVerticalScrollRangeMethod = new ReflectMethod(null, "computeVerticalScrollRange", new Class[0]);
        this.computeVerticalScrollOffsetMethod = new ReflectMethod(null, "computeVerticalScrollOffset", new Class[0]);
        this.computeVerticalScrollExtentMethod = new ReflectMethod(null, "computeVerticalScrollExtent", new Class[0]);
        this.getExtensionManagerMethod = new ReflectMethod(null, "getExtensionManager", new Class[0]);
        this.clearSslPreferencesMethod = new ReflectMethod(null, "clearSslPreferences", new Class[0]);
        this.clearClientCertPreferencesRunnableMethod = new ReflectMethod(null, "clearClientCertPreferences", new Class[0]);
        this.getCertificateMethod = new ReflectMethod(null, "getCertificate", new Class[0]);
        this.setFindListenerXWalkFindListenerInternalMethod = new ReflectMethod(null, "setFindListener", new Class[0]);
        this.findAllAsyncStringMethod = new ReflectMethod(null, "findAllAsync", new Class[0]);
        this.findNextbooleanMethod = new ReflectMethod(null, "findNext", new Class[0]);
        this.clearMatchesMethod = new ReflectMethod(null, "clearMatches", new Class[0]);
        this.getCompositingSurfaceTypeMethod = new ReflectMethod(null, "getCompositingSurfaceType", new Class[0]);
        SurfaceView surfaceView = new SurfaceView(context);
        surfaceView.setLayoutParams(new LayoutParams(0, 0));
        addView(surfaceView);
        this.constructorTypes = new ArrayList();
        this.constructorTypes.add(Context.class);
        this.constructorTypes.add(Activity.class);
        this.constructorParams = new ArrayList();
        this.constructorParams.add(context);
        this.constructorParams.add(activity);
        this.postWrapperMethod = new ReflectMethod((Object) this, "postXWalkViewInternalContextActivityConstructor", new Class[0]);
        reflectionInit();
    }

    public void postXWalkViewInternalContextActivityConstructor() {
        addView((FrameLayout) this.bridge, new FrameLayout.LayoutParams(-1, -1));
        removeViewAt(0);
        XWalkExternalExtensionManagerImpl xWalkExternalExtensionManagerImpl = new XWalkExternalExtensionManagerImpl(this);
    }

    @Deprecated
    public void load(String url, String content) {
        try {
            this.loadStringStringMethod.invoke(url, content);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    @Deprecated
    public void load(String url, String content, Map<String, String> headers) {
        try {
            this.loadStringStringMapMethod.invoke(url, content, headers);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void loadData(String data, String mimeType, String encoding) {
        try {
            this.loadDataStringStringStringMethod.invoke(data, mimeType, encoding);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        try {
            this.loadDataWithBaseURLStringStringStringStringStringMethod.invoke(baseUrl, data, mimeType, encoding, historyUrl);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void loadUrl(String url) {
        try {
            this.loadUrlStringMethod.invoke(url);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        try {
            this.loadUrlStringMapMethod.invoke(url, additionalHttpHeaders);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void loadAppFromManifest(String url, String content) {
        try {
            this.loadAppFromManifestStringStringMethod.invoke(url, content);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void reload(int mode) {
        try {
            this.reloadintMethod.invoke(Integer.valueOf(mode));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void stopLoading() {
        try {
            this.stopLoadingMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public String getUrl() {
        try {
            return (String) this.getUrlMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public XWalkHitTestResult getHitTestResult() {
        try {
            return (XWalkHitTestResult) this.coreWrapper.getWrapperObject(this.getHitTestResultMethod.invoke(new Object[0]));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public int getContentHeight() {
        try {
            return ((Integer) this.getContentHeightMethod.invoke(new Object[0])).intValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return 0;
        }
    }

    public String getTitle() {
        try {
            return (String) this.getTitleMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public String getOriginalUrl() {
        try {
            return (String) this.getOriginalUrlMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public XWalkNavigationHistory getNavigationHistory() {
        try {
            return (XWalkNavigationHistory) this.coreWrapper.getWrapperObject(this.getNavigationHistoryMethod.invoke(new Object[0]));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public void addJavascriptInterface(Object object, String name) {
        try {
            this.addJavascriptInterfaceObjectStringMethod.invoke(object, name);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                this.addJavascriptInterfaceObjectStringMethod.setArguments(object, name);
                XWalkCoreWrapper.reserveReflectMethod(this.addJavascriptInterfaceObjectStringMethod);
                return;
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void removeJavascriptInterface(String name) {
        try {
            this.removeJavascriptInterfaceStringMethod.invoke(name);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                this.removeJavascriptInterfaceStringMethod.setArguments(name);
                XWalkCoreWrapper.reserveReflectMethod(this.removeJavascriptInterfaceStringMethod);
                return;
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void evaluateJavascript(String script, ValueCallback<String> callback) {
        try {
            this.evaluateJavascriptStringValueCallbackMethod.invoke(script, callback);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void clearCache(boolean includeDiskFiles) {
        try {
            this.clearCachebooleanMethod.invoke(Boolean.valueOf(includeDiskFiles));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void clearCacheForSingleFile(String url) {
        try {
            this.clearCacheForSingleFileStringMethod.invoke(url);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean hasEnteredFullscreen() {
        try {
            return ((Boolean) this.hasEnteredFullscreenMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void leaveFullscreen() {
        try {
            this.leaveFullscreenMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void pauseTimers() {
        try {
            this.pauseTimersMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void resumeTimers() {
        try {
            this.resumeTimersMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void onHide() {
        try {
            this.onHideMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void onShow() {
        try {
            this.onShowMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void onDestroy() {
        try {
            this.onDestroyMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    @Deprecated
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        try {
            this.startActivityForResultIntentintBundleMethod.invoke(intent, Integer.valueOf(requestCode), options);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    @Deprecated
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            this.onActivityResultintintIntentMethod.invoke(Integer.valueOf(requestCode), Integer.valueOf(resultCode), data);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean onNewIntent(Intent intent) {
        try {
            return ((Boolean) this.onNewIntentIntentMethod.invoke(intent)).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public boolean saveState(Bundle outState) {
        try {
            return ((Boolean) this.saveStateBundleMethod.invoke(outState)).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public boolean restoreState(Bundle inState) {
        try {
            return ((Boolean) this.restoreStateBundleMethod.invoke(inState)).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public String getAPIVersion() {
        try {
            return (String) this.getAPIVersionMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public String getXWalkVersion() {
        try {
            return (String) this.getXWalkVersionMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public void setUIClient(XWalkUIClient client) {
        try {
            this.setUIClientXWalkUIClientInternalMethod.invoke(client.getBridge());
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                this.setUIClientXWalkUIClientInternalMethod.setArguments(new ReflectMethod((Object) client, "getBridge", new Class[0]));
                XWalkCoreWrapper.reserveReflectMethod(this.setUIClientXWalkUIClientInternalMethod);
                return;
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void setResourceClient(XWalkResourceClient client) {
        try {
            this.setResourceClientXWalkResourceClientInternalMethod.invoke(client.getBridge());
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                this.setResourceClientXWalkResourceClientInternalMethod.setArguments(new ReflectMethod((Object) client, "getBridge", new Class[0]));
                XWalkCoreWrapper.reserveReflectMethod(this.setResourceClientXWalkResourceClientInternalMethod);
                return;
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void setBackgroundColor(int color) {
        try {
            this.setBackgroundColorintMethod.invoke(Integer.valueOf(color));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void setOriginAccessWhitelist(String url, String[] patterns) {
        try {
            this.setOriginAccessWhitelistStringStringArrayMethod.invoke(url, patterns);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void setLayerType(int layerType, Paint paint) {
    }

    public void setUserAgentString(String userAgent) {
        try {
            this.setUserAgentStringStringMethod.invoke(userAgent);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public String getUserAgentString() {
        try {
            return (String) this.getUserAgentStringMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public void setAcceptLanguages(String acceptLanguages) {
        try {
            this.setAcceptLanguagesStringMethod.invoke(acceptLanguages);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void captureBitmapAsync(XWalkGetBitmapCallback callback) {
        try {
            this.captureBitmapAsyncXWalkGetBitmapCallbackInternalMethod.invoke(callback.getBridge());
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public XWalkSettings getSettings() {
        try {
            return (XWalkSettings) this.coreWrapper.getWrapperObject(this.getSettingsMethod.invoke(new Object[0]));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public void setNetworkAvailable(boolean networkUp) {
        try {
            this.setNetworkAvailablebooleanMethod.invoke(Boolean.valueOf(networkUp));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public Uri getRemoteDebuggingUrl() {
        try {
            return (Uri) this.getRemoteDebuggingUrlMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public boolean zoomIn() {
        try {
            return ((Boolean) this.zoomInMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public boolean zoomOut() {
        try {
            return ((Boolean) this.zoomOutMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void zoomBy(float factor) {
        try {
            this.zoomByfloatMethod.invoke(Float.valueOf(factor));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean canZoomIn() {
        try {
            return ((Boolean) this.canZoomInMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public boolean canZoomOut() {
        try {
            return ((Boolean) this.canZoomOutMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        try {
            return (InputConnection) this.onCreateInputConnectionEditorInfoMethod.invoke(outAttrs);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public void setInitialScale(int scaleInPercent) {
        try {
            this.setInitialScaleintMethod.invoke(Integer.valueOf(scaleInPercent));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public Bitmap getFavicon() {
        try {
            return (Bitmap) this.getFaviconMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public void setZOrderOnTop(boolean onTop) {
        try {
            this.setZOrderOnTopbooleanMethod.invoke(Boolean.valueOf(onTop));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void clearFormData() {
        try {
            this.clearFormDataMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void setVisibility(int visibility) {
        if (visibility == 4) {
            visibility = 8;
        }
        super.setVisibility(visibility);
        setXWalkViewInternalVisibility(visibility);
        setSurfaceViewVisibility(visibility);
    }

    public void setSurfaceViewVisibility(int visibility) {
        try {
            this.setSurfaceViewVisibilityintMethod.invoke(Integer.valueOf(visibility));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                this.setSurfaceViewVisibilityintMethod.setArguments(Integer.valueOf(visibility));
                XWalkCoreWrapper.reserveReflectMethod(this.setSurfaceViewVisibilityintMethod);
                return;
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void setXWalkViewInternalVisibility(int visibility) {
        try {
            this.setXWalkViewInternalVisibilityintMethod.invoke(Integer.valueOf(visibility));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                this.setXWalkViewInternalVisibilityintMethod.setArguments(Integer.valueOf(visibility));
                XWalkCoreWrapper.reserveReflectMethod(this.setXWalkViewInternalVisibilityintMethod);
                return;
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void setDownloadListener(XWalkDownloadListener listener) {
        try {
            this.setDownloadListenerXWalkDownloadListenerInternalMethod.invoke(listener.getBridge());
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                this.setDownloadListenerXWalkDownloadListenerInternalMethod.setArguments(new ReflectMethod((Object) listener, "getBridge", new Class[0]));
                XWalkCoreWrapper.reserveReflectMethod(this.setDownloadListenerXWalkDownloadListenerInternalMethod);
                return;
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    private boolean performLongClickDelegate() {
        return performLongClick();
    }

    private boolean onTouchEventDelegate(MotionEvent event) {
        return onTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        try {
            return ((Boolean) this.onTouchEventMotionEventMethod.invoke(event)).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    private void onScrollChangedDelegate(int l, int t, int oldl, int oldt) {
        onScrollChanged(l, t, oldl, oldt);
    }

    private void onFocusChangedDelegate(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    private void onOverScrolledDelegate(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public void setOnTouchListener(OnTouchListener l) {
        try {
            this.setOnTouchListenerOnTouchListenerMethod.invoke(l);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void scrollTo(int x, int y) {
        try {
            this.scrollTointintMethod.invoke(Integer.valueOf(x), Integer.valueOf(y));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void scrollBy(int x, int y) {
        try {
            this.scrollByintintMethod.invoke(Integer.valueOf(x), Integer.valueOf(y));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public int computeHorizontalScrollRange() {
        try {
            return ((Integer) this.computeHorizontalScrollRangeMethod.invoke(new Object[0])).intValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return 0;
        }
    }

    public int computeHorizontalScrollOffset() {
        try {
            return ((Integer) this.computeHorizontalScrollOffsetMethod.invoke(new Object[0])).intValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return 0;
        }
    }

    public int computeVerticalScrollRange() {
        try {
            return ((Integer) this.computeVerticalScrollRangeMethod.invoke(new Object[0])).intValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return 0;
        }
    }

    public int computeVerticalScrollOffset() {
        try {
            return ((Integer) this.computeVerticalScrollOffsetMethod.invoke(new Object[0])).intValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return 0;
        }
    }

    public int computeVerticalScrollExtent() {
        try {
            return ((Integer) this.computeVerticalScrollExtentMethod.invoke(new Object[0])).intValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return 0;
        }
    }

    public XWalkExternalExtensionManager getExtensionManager() {
        try {
            return (XWalkExternalExtensionManager) this.coreWrapper.getWrapperObject(this.getExtensionManagerMethod.invoke(new Object[0]));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public void clearSslPreferences() {
        try {
            this.clearSslPreferencesMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void clearClientCertPreferences(Runnable callback) {
        try {
            this.clearClientCertPreferencesRunnableMethod.invoke(callback);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public SslCertificate getCertificate() {
        try {
            return (SslCertificate) this.getCertificateMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public void setFindListener(XWalkFindListener listener) {
        try {
            this.setFindListenerXWalkFindListenerInternalMethod.invoke(listener.getBridge());
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                this.setFindListenerXWalkFindListenerInternalMethod.setArguments(new ReflectMethod((Object) listener, "getBridge", new Class[0]));
                XWalkCoreWrapper.reserveReflectMethod(this.setFindListenerXWalkFindListenerInternalMethod);
                return;
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void findAllAsync(String searchString) {
        try {
            this.findAllAsyncStringMethod.invoke(searchString);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void findNext(boolean forward) {
        try {
            this.findNextbooleanMethod.invoke(Boolean.valueOf(forward));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void clearMatches() {
        try {
            this.clearMatchesMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public String getCompositingSurfaceType() {
        try {
            return (String) this.getCompositingSurfaceTypeMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    void reflectionInit() {
        XWalkCoreWrapper.initEmbeddedMode();
        this.coreWrapper = XWalkCoreWrapper.getInstance();
        if (this.coreWrapper == null) {
            XWalkCoreWrapper.reserveReflectObject(this);
            return;
        }
        int length = this.constructorTypes.size();
        Class<?>[] paramTypes = new Class[(length + 1)];
        for (int i = 0; i < length; i++) {
            Object type = this.constructorTypes.get(i);
            if (type instanceof String) {
                paramTypes[i] = this.coreWrapper.getBridgeClass((String) type);
                this.constructorParams.set(i, this.coreWrapper.getBridgeObject(this.constructorParams.get(i)));
            } else if (type instanceof Class) {
                paramTypes[i] = (Class) type;
            } else if (!$assertionsDisabled) {
                throw new AssertionError();
            }
        }
        paramTypes[length] = Object.class;
        this.constructorParams.add(this);
        try {
            this.bridge = new ReflectConstructor(this.coreWrapper.getBridgeClass("XWalkViewBridge"), paramTypes).newInstance(this.constructorParams.toArray());
            if (this.postWrapperMethod != null) {
                this.postWrapperMethod.invoke(new Object[0]);
            }
            this.loadStringStringMethod.init(this.bridge, null, "loadSuper", String.class, String.class);
            this.loadStringStringMapMethod.init(this.bridge, null, "loadSuper", String.class, String.class, Map.class);
            this.loadDataStringStringStringMethod.init(this.bridge, null, "loadDataSuper", String.class, String.class, String.class);
            this.loadDataWithBaseURLStringStringStringStringStringMethod.init(this.bridge, null, "loadDataWithBaseURLSuper", String.class, String.class, String.class, String.class, String.class);
            this.loadUrlStringMethod.init(this.bridge, null, "loadUrlSuper", String.class);
            this.loadUrlStringMapMethod.init(this.bridge, null, "loadUrlSuper", String.class, Map.class);
            this.loadAppFromManifestStringStringMethod.init(this.bridge, null, "loadAppFromManifestSuper", String.class, String.class);
            this.reloadintMethod.init(this.bridge, null, "reloadSuper", Integer.TYPE);
            this.stopLoadingMethod.init(this.bridge, null, "stopLoadingSuper", new Class[0]);
            this.getUrlMethod.init(this.bridge, null, "getUrlSuper", new Class[0]);
            this.getHitTestResultMethod.init(this.bridge, null, "getHitTestResultSuper", new Class[0]);
            this.getContentHeightMethod.init(this.bridge, null, "getContentHeightSuper", new Class[0]);
            this.getTitleMethod.init(this.bridge, null, "getTitleSuper", new Class[0]);
            this.getOriginalUrlMethod.init(this.bridge, null, "getOriginalUrlSuper", new Class[0]);
            this.getNavigationHistoryMethod.init(this.bridge, null, "getNavigationHistorySuper", new Class[0]);
            this.addJavascriptInterfaceObjectStringMethod.init(this.bridge, null, "addJavascriptInterfaceSuper", Object.class, String.class);
            this.removeJavascriptInterfaceStringMethod.init(this.bridge, null, "removeJavascriptInterfaceSuper", String.class);
            this.evaluateJavascriptStringValueCallbackMethod.init(this.bridge, null, "evaluateJavascriptSuper", String.class, ValueCallback.class);
            this.clearCachebooleanMethod.init(this.bridge, null, "clearCacheSuper", Boolean.TYPE);
            this.clearCacheForSingleFileStringMethod.init(this.bridge, null, "clearCacheForSingleFileSuper", String.class);
            this.hasEnteredFullscreenMethod.init(this.bridge, null, "hasEnteredFullscreenSuper", new Class[0]);
            this.leaveFullscreenMethod.init(this.bridge, null, "leaveFullscreenSuper", new Class[0]);
            this.pauseTimersMethod.init(this.bridge, null, "pauseTimersSuper", new Class[0]);
            this.resumeTimersMethod.init(this.bridge, null, "resumeTimersSuper", new Class[0]);
            this.onHideMethod.init(this.bridge, null, "onHideSuper", new Class[0]);
            this.onShowMethod.init(this.bridge, null, "onShowSuper", new Class[0]);
            this.onDestroyMethod.init(this.bridge, null, "onDestroySuper", new Class[0]);
            this.startActivityForResultIntentintBundleMethod.init(this.bridge, null, "startActivityForResultSuper", Intent.class, Integer.TYPE, Bundle.class);
            this.onActivityResultintintIntentMethod.init(this.bridge, null, "onActivityResultSuper", Integer.TYPE, Integer.TYPE, Intent.class);
            this.onNewIntentIntentMethod.init(this.bridge, null, "onNewIntentSuper", Intent.class);
            this.saveStateBundleMethod.init(this.bridge, null, "saveStateSuper", Bundle.class);
            this.restoreStateBundleMethod.init(this.bridge, null, "restoreStateSuper", Bundle.class);
            this.getAPIVersionMethod.init(this.bridge, null, "getAPIVersionSuper", new Class[0]);
            this.getXWalkVersionMethod.init(this.bridge, null, "getXWalkVersionSuper", new Class[0]);
            this.setUIClientXWalkUIClientInternalMethod.init(this.bridge, null, "setUIClientSuper", this.coreWrapper.getBridgeClass("XWalkUIClientBridge"));
            this.setResourceClientXWalkResourceClientInternalMethod.init(this.bridge, null, "setResourceClientSuper", this.coreWrapper.getBridgeClass("XWalkResourceClientBridge"));
            this.setBackgroundColorintMethod.init(this.bridge, null, "setBackgroundColorSuper", Integer.TYPE);
            this.setOriginAccessWhitelistStringStringArrayMethod.init(this.bridge, null, "setOriginAccessWhitelistSuper", String.class, String[].class);
            this.setUserAgentStringStringMethod.init(this.bridge, null, "setUserAgentStringSuper", String.class);
            this.getUserAgentStringMethod.init(this.bridge, null, "getUserAgentStringSuper", new Class[0]);
            this.setAcceptLanguagesStringMethod.init(this.bridge, null, "setAcceptLanguagesSuper", String.class);
            this.captureBitmapAsyncXWalkGetBitmapCallbackInternalMethod.init(this.bridge, null, "captureBitmapAsyncSuper", this.coreWrapper.getBridgeClass("XWalkGetBitmapCallbackBridge"));
            this.getSettingsMethod.init(this.bridge, null, "getSettingsSuper", new Class[0]);
            this.setNetworkAvailablebooleanMethod.init(this.bridge, null, "setNetworkAvailableSuper", Boolean.TYPE);
            this.getRemoteDebuggingUrlMethod.init(this.bridge, null, "getRemoteDebuggingUrlSuper", new Class[0]);
            this.zoomInMethod.init(this.bridge, null, "zoomInSuper", new Class[0]);
            this.zoomOutMethod.init(this.bridge, null, "zoomOutSuper", new Class[0]);
            this.zoomByfloatMethod.init(this.bridge, null, "zoomBySuper", Float.TYPE);
            this.canZoomInMethod.init(this.bridge, null, "canZoomInSuper", new Class[0]);
            this.canZoomOutMethod.init(this.bridge, null, "canZoomOutSuper", new Class[0]);
            this.onCreateInputConnectionEditorInfoMethod.init(this.bridge, null, "onCreateInputConnectionSuper", EditorInfo.class);
            this.setInitialScaleintMethod.init(this.bridge, null, "setInitialScaleSuper", Integer.TYPE);
            this.getFaviconMethod.init(this.bridge, null, "getFaviconSuper", new Class[0]);
            this.setZOrderOnTopbooleanMethod.init(this.bridge, null, "setZOrderOnTopSuper", Boolean.TYPE);
            this.clearFormDataMethod.init(this.bridge, null, "clearFormDataSuper", new Class[0]);
            this.setSurfaceViewVisibilityintMethod.init(this.bridge, null, "setSurfaceViewVisibilitySuper", Integer.TYPE);
            this.setXWalkViewInternalVisibilityintMethod.init(this.bridge, null, "setXWalkViewInternalVisibilitySuper", Integer.TYPE);
            this.setDownloadListenerXWalkDownloadListenerInternalMethod.init(this.bridge, null, "setDownloadListenerSuper", this.coreWrapper.getBridgeClass("XWalkDownloadListenerBridge"));
            this.onTouchEventMotionEventMethod.init(this.bridge, null, "onTouchEventSuper", MotionEvent.class);
            this.setOnTouchListenerOnTouchListenerMethod.init(this.bridge, null, "setOnTouchListenerSuper", OnTouchListener.class);
            this.scrollTointintMethod.init(this.bridge, null, "scrollToSuper", Integer.TYPE, Integer.TYPE);
            this.scrollByintintMethod.init(this.bridge, null, "scrollBySuper", Integer.TYPE, Integer.TYPE);
            this.computeHorizontalScrollRangeMethod.init(this.bridge, null, "computeHorizontalScrollRangeSuper", new Class[0]);
            this.computeHorizontalScrollOffsetMethod.init(this.bridge, null, "computeHorizontalScrollOffsetSuper", new Class[0]);
            this.computeVerticalScrollRangeMethod.init(this.bridge, null, "computeVerticalScrollRangeSuper", new Class[0]);
            this.computeVerticalScrollOffsetMethod.init(this.bridge, null, "computeVerticalScrollOffsetSuper", new Class[0]);
            this.computeVerticalScrollExtentMethod.init(this.bridge, null, "computeVerticalScrollExtentSuper", new Class[0]);
            this.getExtensionManagerMethod.init(this.bridge, null, "getExtensionManagerSuper", new Class[0]);
            this.clearSslPreferencesMethod.init(this.bridge, null, "clearSslPreferencesSuper", new Class[0]);
            this.clearClientCertPreferencesRunnableMethod.init(this.bridge, null, "clearClientCertPreferencesSuper", Runnable.class);
            this.getCertificateMethod.init(this.bridge, null, "getCertificateSuper", new Class[0]);
            this.setFindListenerXWalkFindListenerInternalMethod.init(this.bridge, null, "setFindListenerSuper", this.coreWrapper.getBridgeClass("XWalkFindListenerBridge"));
            this.findAllAsyncStringMethod.init(this.bridge, null, "findAllAsyncSuper", String.class);
            this.findNextbooleanMethod.init(this.bridge, null, "findNextSuper", Boolean.TYPE);
            this.clearMatchesMethod.init(this.bridge, null, "clearMatchesSuper", new Class[0]);
            this.getCompositingSurfaceTypeMethod.init(this.bridge, null, "getCompositingSurfaceTypeSuper", new Class[0]);
        } catch (UnsupportedOperationException e) {
        }
    }
}
