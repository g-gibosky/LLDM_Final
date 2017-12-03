package org.xwalk.core.internal;

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
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ValueCallback;
import java.util.Map;

public class XWalkViewBridge extends XWalkViewInternal {
    private ReflectMethod addJavascriptInterfaceObjectStringMethod;
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
    private XWalkCoreBridge coreBridge;
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
    private ReflectMethod onActivityResultintintIntentMethod;
    private ReflectMethod onCreateInputConnectionEditorInfoMethod;
    private ReflectMethod onDestroyMethod;
    private ReflectMethod onFocusChangedDelegatebooleanintRectMethod;
    private ReflectMethod onHideMethod;
    private ReflectMethod onNewIntentIntentMethod;
    private ReflectMethod onOverScrolledDelegateintintbooleanbooleanMethod;
    private ReflectMethod onScrollChangedDelegateintintintintMethod;
    private ReflectMethod onShowMethod;
    private ReflectMethod onTouchEventDelegateMotionEventMethod;
    private ReflectMethod onTouchEventMotionEventMethod;
    private ReflectMethod pauseTimersMethod;
    private ReflectMethod performLongClickDelegateMethod;
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
    private ReflectMethod setLayerTypeintPaintMethod;
    private ReflectMethod setNetworkAvailablebooleanMethod;
    private ReflectMethod setOnTouchListenerOnTouchListenerMethod;
    private ReflectMethod setOriginAccessWhitelistStringStringArrayMethod;
    private ReflectMethod setResourceClientXWalkResourceClientInternalMethod;
    private ReflectMethod setSurfaceViewVisibilityintMethod;
    private ReflectMethod setUIClientXWalkUIClientInternalMethod;
    private ReflectMethod setUserAgentStringStringMethod;
    private ReflectMethod setVisibilityintMethod;
    private ReflectMethod setXWalkViewInternalVisibilityintMethod;
    private ReflectMethod setZOrderOnTopbooleanMethod;
    private ReflectMethod startActivityForResultIntentintBundleMethod;
    private ReflectMethod stopLoadingMethod;
    private Object wrapper;
    private ReflectMethod zoomByfloatMethod;
    private ReflectMethod zoomInMethod;
    private ReflectMethod zoomOutMethod;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkViewBridge(Context context, Object wrapper) {
        super(context);
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
        this.setLayerTypeintPaintMethod = new ReflectMethod(null, "setLayerType", new Class[0]);
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
        this.setVisibilityintMethod = new ReflectMethod(null, "setVisibility", new Class[0]);
        this.setSurfaceViewVisibilityintMethod = new ReflectMethod(null, "setSurfaceViewVisibility", new Class[0]);
        this.setXWalkViewInternalVisibilityintMethod = new ReflectMethod(null, "setXWalkViewInternalVisibility", new Class[0]);
        this.setDownloadListenerXWalkDownloadListenerInternalMethod = new ReflectMethod(null, "setDownloadListener", new Class[0]);
        this.performLongClickDelegateMethod = new ReflectMethod(null, "performLongClickDelegate", new Class[0]);
        this.onTouchEventDelegateMotionEventMethod = new ReflectMethod(null, "onTouchEventDelegate", new Class[0]);
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        this.onScrollChangedDelegateintintintintMethod = new ReflectMethod(null, "onScrollChangedDelegate", new Class[0]);
        this.onFocusChangedDelegatebooleanintRectMethod = new ReflectMethod(null, "onFocusChangedDelegate", new Class[0]);
        this.onOverScrolledDelegateintintbooleanbooleanMethod = new ReflectMethod(null, "onOverScrolledDelegate", new Class[0]);
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
        this.wrapper = wrapper;
        reflectionInit();
    }

    public XWalkViewBridge(Context context, AttributeSet attrs, Object wrapper) {
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
        this.setLayerTypeintPaintMethod = new ReflectMethod(null, "setLayerType", new Class[0]);
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
        this.setVisibilityintMethod = new ReflectMethod(null, "setVisibility", new Class[0]);
        this.setSurfaceViewVisibilityintMethod = new ReflectMethod(null, "setSurfaceViewVisibility", new Class[0]);
        this.setXWalkViewInternalVisibilityintMethod = new ReflectMethod(null, "setXWalkViewInternalVisibility", new Class[0]);
        this.setDownloadListenerXWalkDownloadListenerInternalMethod = new ReflectMethod(null, "setDownloadListener", new Class[0]);
        this.performLongClickDelegateMethod = new ReflectMethod(null, "performLongClickDelegate", new Class[0]);
        this.onTouchEventDelegateMotionEventMethod = new ReflectMethod(null, "onTouchEventDelegate", new Class[0]);
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        this.onScrollChangedDelegateintintintintMethod = new ReflectMethod(null, "onScrollChangedDelegate", new Class[0]);
        this.onFocusChangedDelegatebooleanintRectMethod = new ReflectMethod(null, "onFocusChangedDelegate", new Class[0]);
        this.onOverScrolledDelegateintintbooleanbooleanMethod = new ReflectMethod(null, "onOverScrolledDelegate", new Class[0]);
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
        this.wrapper = wrapper;
        reflectionInit();
        String animatable = null;
        try {
            animatable = (String) new ReflectField(wrapper, "mAnimatable").get();
        } catch (RuntimeException e) {
        }
        initXWalkContent(animatable);
    }

    public XWalkViewBridge(Context context, Activity activity, Object wrapper) {
        super(context, activity);
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
        this.setLayerTypeintPaintMethod = new ReflectMethod(null, "setLayerType", new Class[0]);
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
        this.setVisibilityintMethod = new ReflectMethod(null, "setVisibility", new Class[0]);
        this.setSurfaceViewVisibilityintMethod = new ReflectMethod(null, "setSurfaceViewVisibility", new Class[0]);
        this.setXWalkViewInternalVisibilityintMethod = new ReflectMethod(null, "setXWalkViewInternalVisibility", new Class[0]);
        this.setDownloadListenerXWalkDownloadListenerInternalMethod = new ReflectMethod(null, "setDownloadListener", new Class[0]);
        this.performLongClickDelegateMethod = new ReflectMethod(null, "performLongClickDelegate", new Class[0]);
        this.onTouchEventDelegateMotionEventMethod = new ReflectMethod(null, "onTouchEventDelegate", new Class[0]);
        this.onTouchEventMotionEventMethod = new ReflectMethod(null, "onTouchEvent", new Class[0]);
        this.onScrollChangedDelegateintintintintMethod = new ReflectMethod(null, "onScrollChangedDelegate", new Class[0]);
        this.onFocusChangedDelegatebooleanintRectMethod = new ReflectMethod(null, "onFocusChangedDelegate", new Class[0]);
        this.onOverScrolledDelegateintintbooleanbooleanMethod = new ReflectMethod(null, "onOverScrolledDelegate", new Class[0]);
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
        this.wrapper = wrapper;
        reflectionInit();
    }

    public void load(String url, String content) {
        if (this.loadStringStringMethod == null || this.loadStringStringMethod.isNull()) {
            loadSuper(url, content);
            return;
        }
        this.loadStringStringMethod.invoke(url, content);
    }

    public void loadSuper(String url, String content) {
        super.load(url, content);
    }

    public void load(String url, String content, Map<String, String> headers) {
        if (this.loadStringStringMapMethod == null || this.loadStringStringMapMethod.isNull()) {
            loadSuper(url, content, headers);
            return;
        }
        this.loadStringStringMapMethod.invoke(url, content, headers);
    }

    public void loadSuper(String url, String content, Map<String, String> headers) {
        super.load(url, content, headers);
    }

    public void loadData(String data, String mimeType, String encoding) {
        if (this.loadDataStringStringStringMethod == null || this.loadDataStringStringStringMethod.isNull()) {
            loadDataSuper(data, mimeType, encoding);
            return;
        }
        this.loadDataStringStringStringMethod.invoke(data, mimeType, encoding);
    }

    public void loadDataSuper(String data, String mimeType, String encoding) {
        super.loadData(data, mimeType, encoding);
    }

    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        if (this.loadDataWithBaseURLStringStringStringStringStringMethod == null || this.loadDataWithBaseURLStringStringStringStringStringMethod.isNull()) {
            loadDataWithBaseURLSuper(baseUrl, data, mimeType, encoding, historyUrl);
            return;
        }
        this.loadDataWithBaseURLStringStringStringStringStringMethod.invoke(baseUrl, data, mimeType, encoding, historyUrl);
    }

    public void loadDataWithBaseURLSuper(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    public void loadUrl(String url) {
        if (this.loadUrlStringMethod == null || this.loadUrlStringMethod.isNull()) {
            loadUrlSuper(url);
            return;
        }
        this.loadUrlStringMethod.invoke(url);
    }

    public void loadUrlSuper(String url) {
        super.loadUrl(url);
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        if (this.loadUrlStringMapMethod == null || this.loadUrlStringMapMethod.isNull()) {
            loadUrlSuper(url, additionalHttpHeaders);
            return;
        }
        this.loadUrlStringMapMethod.invoke(url, additionalHttpHeaders);
    }

    public void loadUrlSuper(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
    }

    public void loadAppFromManifest(String url, String content) {
        if (this.loadAppFromManifestStringStringMethod == null || this.loadAppFromManifestStringStringMethod.isNull()) {
            loadAppFromManifestSuper(url, content);
            return;
        }
        this.loadAppFromManifestStringStringMethod.invoke(url, content);
    }

    public void loadAppFromManifestSuper(String url, String content) {
        super.loadAppFromManifest(url, content);
    }

    public void reload(int mode) {
        if (this.reloadintMethod == null || this.reloadintMethod.isNull()) {
            reloadSuper(mode);
            return;
        }
        this.reloadintMethod.invoke(Integer.valueOf(mode));
    }

    public void reloadSuper(int mode) {
        super.reload(mode);
    }

    public void stopLoading() {
        if (this.stopLoadingMethod == null || this.stopLoadingMethod.isNull()) {
            stopLoadingSuper();
        } else {
            this.stopLoadingMethod.invoke(new Object[0]);
        }
    }

    public void stopLoadingSuper() {
        super.stopLoading();
    }

    public String getUrl() {
        if (this.getUrlMethod == null || this.getUrlMethod.isNull()) {
            return getUrlSuper();
        }
        return (String) this.getUrlMethod.invoke(new Object[0]);
    }

    public String getUrlSuper() {
        String ret = super.getUrl();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public XWalkHitTestResultInternal getHitTestResult() {
        if (this.getHitTestResultMethod == null || this.getHitTestResultMethod.isNull()) {
            return getHitTestResultSuper();
        }
        return (XWalkHitTestResultBridge) this.coreBridge.getBridgeObject(this.getHitTestResultMethod.invoke(new Object[0]));
    }

    public XWalkHitTestResultBridge getHitTestResultSuper() {
        XWalkHitTestResultInternal ret = super.getHitTestResult();
        if (ret == null) {
            return null;
        }
        return ret instanceof XWalkHitTestResultBridge ? (XWalkHitTestResultBridge) ret : new XWalkHitTestResultBridge(ret);
    }

    public int getContentHeight() {
        if (this.getContentHeightMethod == null || this.getContentHeightMethod.isNull()) {
            return getContentHeightSuper();
        }
        return ((Integer) this.getContentHeightMethod.invoke(new Object[0])).intValue();
    }

    public int getContentHeightSuper() {
        return super.getContentHeight();
    }

    public String getTitle() {
        if (this.getTitleMethod == null || this.getTitleMethod.isNull()) {
            return getTitleSuper();
        }
        return (String) this.getTitleMethod.invoke(new Object[0]);
    }

    public String getTitleSuper() {
        String ret = super.getTitle();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public String getOriginalUrl() {
        if (this.getOriginalUrlMethod == null || this.getOriginalUrlMethod.isNull()) {
            return getOriginalUrlSuper();
        }
        return (String) this.getOriginalUrlMethod.invoke(new Object[0]);
    }

    public String getOriginalUrlSuper() {
        String ret = super.getOriginalUrl();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public XWalkNavigationHistoryInternal getNavigationHistory() {
        if (this.getNavigationHistoryMethod == null || this.getNavigationHistoryMethod.isNull()) {
            return getNavigationHistorySuper();
        }
        return (XWalkNavigationHistoryBridge) this.coreBridge.getBridgeObject(this.getNavigationHistoryMethod.invoke(new Object[0]));
    }

    public XWalkNavigationHistoryBridge getNavigationHistorySuper() {
        XWalkNavigationHistoryInternal ret = super.getNavigationHistory();
        if (ret == null) {
            return null;
        }
        return ret instanceof XWalkNavigationHistoryBridge ? (XWalkNavigationHistoryBridge) ret : new XWalkNavigationHistoryBridge(ret);
    }

    public void addJavascriptInterface(Object object, String name) {
        if (this.addJavascriptInterfaceObjectStringMethod == null || this.addJavascriptInterfaceObjectStringMethod.isNull()) {
            addJavascriptInterfaceSuper(object, name);
            return;
        }
        this.addJavascriptInterfaceObjectStringMethod.invoke(object, name);
    }

    public void addJavascriptInterfaceSuper(Object object, String name) {
        super.addJavascriptInterface(object, name);
    }

    public void removeJavascriptInterface(String name) {
        if (this.removeJavascriptInterfaceStringMethod == null || this.removeJavascriptInterfaceStringMethod.isNull()) {
            removeJavascriptInterfaceSuper(name);
            return;
        }
        this.removeJavascriptInterfaceStringMethod.invoke(name);
    }

    public void removeJavascriptInterfaceSuper(String name) {
        super.removeJavascriptInterface(name);
    }

    public void evaluateJavascript(String script, ValueCallback<String> callback) {
        if (this.evaluateJavascriptStringValueCallbackMethod == null || this.evaluateJavascriptStringValueCallbackMethod.isNull()) {
            evaluateJavascriptSuper(script, callback);
            return;
        }
        this.evaluateJavascriptStringValueCallbackMethod.invoke(script, callback);
    }

    public void evaluateJavascriptSuper(String script, ValueCallback<String> callback) {
        super.evaluateJavascript(script, callback);
    }

    public void clearCache(boolean includeDiskFiles) {
        if (this.clearCachebooleanMethod == null || this.clearCachebooleanMethod.isNull()) {
            clearCacheSuper(includeDiskFiles);
            return;
        }
        this.clearCachebooleanMethod.invoke(Boolean.valueOf(includeDiskFiles));
    }

    public void clearCacheSuper(boolean includeDiskFiles) {
        super.clearCache(includeDiskFiles);
    }

    public void clearCacheForSingleFile(String url) {
        if (this.clearCacheForSingleFileStringMethod == null || this.clearCacheForSingleFileStringMethod.isNull()) {
            clearCacheForSingleFileSuper(url);
            return;
        }
        this.clearCacheForSingleFileStringMethod.invoke(url);
    }

    public void clearCacheForSingleFileSuper(String url) {
        super.clearCacheForSingleFile(url);
    }

    public boolean hasEnteredFullscreen() {
        if (this.hasEnteredFullscreenMethod == null || this.hasEnteredFullscreenMethod.isNull()) {
            return hasEnteredFullscreenSuper();
        }
        return ((Boolean) this.hasEnteredFullscreenMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean hasEnteredFullscreenSuper() {
        return super.hasEnteredFullscreen();
    }

    public void leaveFullscreen() {
        if (this.leaveFullscreenMethod == null || this.leaveFullscreenMethod.isNull()) {
            leaveFullscreenSuper();
        } else {
            this.leaveFullscreenMethod.invoke(new Object[0]);
        }
    }

    public void leaveFullscreenSuper() {
        super.leaveFullscreen();
    }

    public void pauseTimers() {
        if (this.pauseTimersMethod == null || this.pauseTimersMethod.isNull()) {
            pauseTimersSuper();
        } else {
            this.pauseTimersMethod.invoke(new Object[0]);
        }
    }

    public void pauseTimersSuper() {
        super.pauseTimers();
    }

    public void resumeTimers() {
        if (this.resumeTimersMethod == null || this.resumeTimersMethod.isNull()) {
            resumeTimersSuper();
        } else {
            this.resumeTimersMethod.invoke(new Object[0]);
        }
    }

    public void resumeTimersSuper() {
        super.resumeTimers();
    }

    public void onHide() {
        if (this.onHideMethod == null || this.onHideMethod.isNull()) {
            onHideSuper();
        } else {
            this.onHideMethod.invoke(new Object[0]);
        }
    }

    public void onHideSuper() {
        super.onHide();
    }

    public void onShow() {
        if (this.onShowMethod == null || this.onShowMethod.isNull()) {
            onShowSuper();
        } else {
            this.onShowMethod.invoke(new Object[0]);
        }
    }

    public void onShowSuper() {
        super.onShow();
    }

    public void onDestroy() {
        if (this.onDestroyMethod == null || this.onDestroyMethod.isNull()) {
            onDestroySuper();
        } else {
            this.onDestroyMethod.invoke(new Object[0]);
        }
    }

    public void onDestroySuper() {
        super.onDestroy();
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (this.startActivityForResultIntentintBundleMethod == null || this.startActivityForResultIntentintBundleMethod.isNull()) {
            startActivityForResultSuper(intent, requestCode, options);
            return;
        }
        this.startActivityForResultIntentintBundleMethod.invoke(intent, Integer.valueOf(requestCode), options);
    }

    public void startActivityForResultSuper(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.onActivityResultintintIntentMethod == null || this.onActivityResultintintIntentMethod.isNull()) {
            onActivityResultSuper(requestCode, resultCode, data);
            return;
        }
        this.onActivityResultintintIntentMethod.invoke(Integer.valueOf(requestCode), Integer.valueOf(resultCode), data);
    }

    public void onActivityResultSuper(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onNewIntent(Intent intent) {
        if (this.onNewIntentIntentMethod == null || this.onNewIntentIntentMethod.isNull()) {
            return onNewIntentSuper(intent);
        }
        return ((Boolean) this.onNewIntentIntentMethod.invoke(intent)).booleanValue();
    }

    public boolean onNewIntentSuper(Intent intent) {
        return super.onNewIntent(intent);
    }

    public boolean saveState(Bundle outState) {
        if (this.saveStateBundleMethod == null || this.saveStateBundleMethod.isNull()) {
            return saveStateSuper(outState);
        }
        return ((Boolean) this.saveStateBundleMethod.invoke(outState)).booleanValue();
    }

    public boolean saveStateSuper(Bundle outState) {
        return super.saveState(outState);
    }

    public boolean restoreState(Bundle inState) {
        if (this.restoreStateBundleMethod == null || this.restoreStateBundleMethod.isNull()) {
            return restoreStateSuper(inState);
        }
        return ((Boolean) this.restoreStateBundleMethod.invoke(inState)).booleanValue();
    }

    public boolean restoreStateSuper(Bundle inState) {
        return super.restoreState(inState);
    }

    public String getAPIVersion() {
        if (this.getAPIVersionMethod == null || this.getAPIVersionMethod.isNull()) {
            return getAPIVersionSuper();
        }
        return (String) this.getAPIVersionMethod.invoke(new Object[0]);
    }

    public String getAPIVersionSuper() {
        String ret = super.getAPIVersion();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public String getXWalkVersion() {
        if (this.getXWalkVersionMethod == null || this.getXWalkVersionMethod.isNull()) {
            return getXWalkVersionSuper();
        }
        return (String) this.getXWalkVersionMethod.invoke(new Object[0]);
    }

    public String getXWalkVersionSuper() {
        String ret = super.getXWalkVersion();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setUIClient(XWalkUIClientInternal client) {
        if (client instanceof XWalkUIClientBridge) {
            setUIClient((XWalkUIClientBridge) client);
        } else {
            super.setUIClient(client);
        }
    }

    public void setUIClient(XWalkUIClientBridge client) {
        if (this.setUIClientXWalkUIClientInternalMethod == null || this.setUIClientXWalkUIClientInternalMethod.isNull()) {
            setUIClientSuper(client);
            return;
        }
        ReflectMethod reflectMethod = this.setUIClientXWalkUIClientInternalMethod;
        Object[] objArr = new Object[1];
        if (!(client instanceof XWalkUIClientBridge)) {
            client = null;
        }
        objArr[0] = client.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void setUIClientSuper(XWalkUIClientBridge client) {
        super.setUIClient(client);
    }

    public void setResourceClient(XWalkResourceClientInternal client) {
        if (client instanceof XWalkResourceClientBridge) {
            setResourceClient((XWalkResourceClientBridge) client);
        } else {
            super.setResourceClient(client);
        }
    }

    public void setResourceClient(XWalkResourceClientBridge client) {
        if (this.setResourceClientXWalkResourceClientInternalMethod == null || this.setResourceClientXWalkResourceClientInternalMethod.isNull()) {
            setResourceClientSuper(client);
            return;
        }
        ReflectMethod reflectMethod = this.setResourceClientXWalkResourceClientInternalMethod;
        Object[] objArr = new Object[1];
        if (!(client instanceof XWalkResourceClientBridge)) {
            client = null;
        }
        objArr[0] = client.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void setResourceClientSuper(XWalkResourceClientBridge client) {
        super.setResourceClient(client);
    }

    public void setBackgroundColor(int color) {
        if (this.setBackgroundColorintMethod == null || this.setBackgroundColorintMethod.isNull()) {
            setBackgroundColorSuper(color);
            return;
        }
        this.setBackgroundColorintMethod.invoke(Integer.valueOf(color));
    }

    public void setBackgroundColorSuper(int color) {
        super.setBackgroundColor(color);
    }

    public void setOriginAccessWhitelist(String url, String[] patterns) {
        if (this.setOriginAccessWhitelistStringStringArrayMethod == null || this.setOriginAccessWhitelistStringStringArrayMethod.isNull()) {
            setOriginAccessWhitelistSuper(url, patterns);
            return;
        }
        this.setOriginAccessWhitelistStringStringArrayMethod.invoke(url, patterns);
    }

    public void setOriginAccessWhitelistSuper(String url, String[] patterns) {
        super.setOriginAccessWhitelist(url, patterns);
    }

    public void setLayerType(int layerType, Paint paint) {
        if (this.setLayerTypeintPaintMethod == null || this.setLayerTypeintPaintMethod.isNull()) {
            setLayerTypeSuper(layerType, paint);
            return;
        }
        this.setLayerTypeintPaintMethod.invoke(Integer.valueOf(layerType), paint);
    }

    public void setLayerTypeSuper(int layerType, Paint paint) {
        super.setLayerType(layerType, paint);
    }

    public void setUserAgentString(String userAgent) {
        if (this.setUserAgentStringStringMethod == null || this.setUserAgentStringStringMethod.isNull()) {
            setUserAgentStringSuper(userAgent);
            return;
        }
        this.setUserAgentStringStringMethod.invoke(userAgent);
    }

    public void setUserAgentStringSuper(String userAgent) {
        super.setUserAgentString(userAgent);
    }

    public String getUserAgentString() {
        if (this.getUserAgentStringMethod == null || this.getUserAgentStringMethod.isNull()) {
            return getUserAgentStringSuper();
        }
        return (String) this.getUserAgentStringMethod.invoke(new Object[0]);
    }

    public String getUserAgentStringSuper() {
        String ret = super.getUserAgentString();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setAcceptLanguages(String acceptLanguages) {
        if (this.setAcceptLanguagesStringMethod == null || this.setAcceptLanguagesStringMethod.isNull()) {
            setAcceptLanguagesSuper(acceptLanguages);
            return;
        }
        this.setAcceptLanguagesStringMethod.invoke(acceptLanguages);
    }

    public void setAcceptLanguagesSuper(String acceptLanguages) {
        super.setAcceptLanguages(acceptLanguages);
    }

    public void captureBitmapAsync(XWalkGetBitmapCallbackInternal callback) {
        if (callback instanceof XWalkGetBitmapCallbackBridge) {
            captureBitmapAsync((XWalkGetBitmapCallbackBridge) callback);
        } else {
            super.captureBitmapAsync(callback);
        }
    }

    public void captureBitmapAsync(XWalkGetBitmapCallbackBridge callback) {
        if (this.captureBitmapAsyncXWalkGetBitmapCallbackInternalMethod == null || this.captureBitmapAsyncXWalkGetBitmapCallbackInternalMethod.isNull()) {
            captureBitmapAsyncSuper(callback);
            return;
        }
        ReflectMethod reflectMethod = this.captureBitmapAsyncXWalkGetBitmapCallbackInternalMethod;
        Object[] objArr = new Object[1];
        if (!(callback instanceof XWalkGetBitmapCallbackBridge)) {
            callback = null;
        }
        objArr[0] = callback.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void captureBitmapAsyncSuper(XWalkGetBitmapCallbackBridge callback) {
        super.captureBitmapAsync(callback);
    }

    public XWalkSettingsInternal getSettings() {
        if (this.getSettingsMethod == null || this.getSettingsMethod.isNull()) {
            return getSettingsSuper();
        }
        return (XWalkSettingsBridge) this.coreBridge.getBridgeObject(this.getSettingsMethod.invoke(new Object[0]));
    }

    public XWalkSettingsBridge getSettingsSuper() {
        XWalkSettingsInternal ret = super.getSettings();
        if (ret == null) {
            return null;
        }
        return ret instanceof XWalkSettingsBridge ? (XWalkSettingsBridge) ret : new XWalkSettingsBridge(ret);
    }

    public void setNetworkAvailable(boolean networkUp) {
        if (this.setNetworkAvailablebooleanMethod == null || this.setNetworkAvailablebooleanMethod.isNull()) {
            setNetworkAvailableSuper(networkUp);
            return;
        }
        this.setNetworkAvailablebooleanMethod.invoke(Boolean.valueOf(networkUp));
    }

    public void setNetworkAvailableSuper(boolean networkUp) {
        super.setNetworkAvailable(networkUp);
    }

    public Uri getRemoteDebuggingUrl() {
        if (this.getRemoteDebuggingUrlMethod == null || this.getRemoteDebuggingUrlMethod.isNull()) {
            return getRemoteDebuggingUrlSuper();
        }
        return (Uri) this.getRemoteDebuggingUrlMethod.invoke(new Object[0]);
    }

    public Uri getRemoteDebuggingUrlSuper() {
        Uri ret = super.getRemoteDebuggingUrl();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public boolean zoomIn() {
        if (this.zoomInMethod == null || this.zoomInMethod.isNull()) {
            return zoomInSuper();
        }
        return ((Boolean) this.zoomInMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean zoomInSuper() {
        return super.zoomIn();
    }

    public boolean zoomOut() {
        if (this.zoomOutMethod == null || this.zoomOutMethod.isNull()) {
            return zoomOutSuper();
        }
        return ((Boolean) this.zoomOutMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean zoomOutSuper() {
        return super.zoomOut();
    }

    public void zoomBy(float factor) {
        if (this.zoomByfloatMethod == null || this.zoomByfloatMethod.isNull()) {
            zoomBySuper(factor);
            return;
        }
        this.zoomByfloatMethod.invoke(Float.valueOf(factor));
    }

    public void zoomBySuper(float factor) {
        super.zoomBy(factor);
    }

    public boolean canZoomIn() {
        if (this.canZoomInMethod == null || this.canZoomInMethod.isNull()) {
            return canZoomInSuper();
        }
        return ((Boolean) this.canZoomInMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean canZoomInSuper() {
        return super.canZoomIn();
    }

    public boolean canZoomOut() {
        if (this.canZoomOutMethod == null || this.canZoomOutMethod.isNull()) {
            return canZoomOutSuper();
        }
        return ((Boolean) this.canZoomOutMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean canZoomOutSuper() {
        return super.canZoomOut();
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (this.onCreateInputConnectionEditorInfoMethod == null || this.onCreateInputConnectionEditorInfoMethod.isNull()) {
            return onCreateInputConnectionSuper(outAttrs);
        }
        return (InputConnection) this.onCreateInputConnectionEditorInfoMethod.invoke(outAttrs);
    }

    public InputConnection onCreateInputConnectionSuper(EditorInfo outAttrs) {
        InputConnection ret = super.onCreateInputConnection(outAttrs);
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setInitialScale(int scaleInPercent) {
        if (this.setInitialScaleintMethod == null || this.setInitialScaleintMethod.isNull()) {
            setInitialScaleSuper(scaleInPercent);
            return;
        }
        this.setInitialScaleintMethod.invoke(Integer.valueOf(scaleInPercent));
    }

    public void setInitialScaleSuper(int scaleInPercent) {
        super.setInitialScale(scaleInPercent);
    }

    public Bitmap getFavicon() {
        if (this.getFaviconMethod == null || this.getFaviconMethod.isNull()) {
            return getFaviconSuper();
        }
        return (Bitmap) this.getFaviconMethod.invoke(new Object[0]);
    }

    public Bitmap getFaviconSuper() {
        Bitmap ret = super.getFavicon();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setZOrderOnTop(boolean onTop) {
        if (this.setZOrderOnTopbooleanMethod == null || this.setZOrderOnTopbooleanMethod.isNull()) {
            setZOrderOnTopSuper(onTop);
            return;
        }
        this.setZOrderOnTopbooleanMethod.invoke(Boolean.valueOf(onTop));
    }

    public void setZOrderOnTopSuper(boolean onTop) {
        super.setZOrderOnTop(onTop);
    }

    public void clearFormData() {
        if (this.clearFormDataMethod == null || this.clearFormDataMethod.isNull()) {
            clearFormDataSuper();
        } else {
            this.clearFormDataMethod.invoke(new Object[0]);
        }
    }

    public void clearFormDataSuper() {
        super.clearFormData();
    }

    public void setVisibility(int visibility) {
        if (this.setVisibilityintMethod == null || this.setVisibilityintMethod.isNull()) {
            setVisibilitySuper(visibility);
            return;
        }
        this.setVisibilityintMethod.invoke(Integer.valueOf(visibility));
    }

    public void setVisibilitySuper(int visibility) {
        super.setVisibility(visibility);
    }

    public void setSurfaceViewVisibility(int visibility) {
        if (this.setSurfaceViewVisibilityintMethod == null || this.setSurfaceViewVisibilityintMethod.isNull()) {
            setSurfaceViewVisibilitySuper(visibility);
            return;
        }
        this.setSurfaceViewVisibilityintMethod.invoke(Integer.valueOf(visibility));
    }

    public void setSurfaceViewVisibilitySuper(int visibility) {
        super.setSurfaceViewVisibility(visibility);
    }

    public void setXWalkViewInternalVisibility(int visibility) {
        if (this.setXWalkViewInternalVisibilityintMethod == null || this.setXWalkViewInternalVisibilityintMethod.isNull()) {
            setXWalkViewInternalVisibilitySuper(visibility);
            return;
        }
        this.setXWalkViewInternalVisibilityintMethod.invoke(Integer.valueOf(visibility));
    }

    public void setXWalkViewInternalVisibilitySuper(int visibility) {
        super.setXWalkViewInternalVisibility(visibility);
    }

    public void setDownloadListener(XWalkDownloadListenerInternal listener) {
        if (listener instanceof XWalkDownloadListenerBridge) {
            setDownloadListener((XWalkDownloadListenerBridge) listener);
        } else {
            super.setDownloadListener(listener);
        }
    }

    public void setDownloadListener(XWalkDownloadListenerBridge listener) {
        if (this.setDownloadListenerXWalkDownloadListenerInternalMethod == null || this.setDownloadListenerXWalkDownloadListenerInternalMethod.isNull()) {
            setDownloadListenerSuper(listener);
            return;
        }
        ReflectMethod reflectMethod = this.setDownloadListenerXWalkDownloadListenerInternalMethod;
        Object[] objArr = new Object[1];
        if (!(listener instanceof XWalkDownloadListenerBridge)) {
            listener = null;
        }
        objArr[0] = listener.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void setDownloadListenerSuper(XWalkDownloadListenerBridge listener) {
        super.setDownloadListener(listener);
    }

    public boolean performLongClickDelegate() {
        if (this.performLongClickDelegateMethod == null || this.performLongClickDelegateMethod.isNull()) {
            return performLongClickDelegateSuper();
        }
        return ((Boolean) this.performLongClickDelegateMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean performLongClickDelegateSuper() {
        return super.performLongClickDelegate();
    }

    public boolean onTouchEventDelegate(MotionEvent event) {
        if (this.onTouchEventDelegateMotionEventMethod == null || this.onTouchEventDelegateMotionEventMethod.isNull()) {
            return onTouchEventDelegateSuper(event);
        }
        return ((Boolean) this.onTouchEventDelegateMotionEventMethod.invoke(event)).booleanValue();
    }

    public boolean onTouchEventDelegateSuper(MotionEvent event) {
        return super.onTouchEventDelegate(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.onTouchEventMotionEventMethod == null || this.onTouchEventMotionEventMethod.isNull()) {
            return onTouchEventSuper(event);
        }
        return ((Boolean) this.onTouchEventMotionEventMethod.invoke(event)).booleanValue();
    }

    public boolean onTouchEventSuper(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void onScrollChangedDelegate(int l, int t, int oldl, int oldt) {
        if (this.onScrollChangedDelegateintintintintMethod == null || this.onScrollChangedDelegateintintintintMethod.isNull()) {
            onScrollChangedDelegateSuper(l, t, oldl, oldt);
            return;
        }
        this.onScrollChangedDelegateintintintintMethod.invoke(Integer.valueOf(l), Integer.valueOf(t), Integer.valueOf(oldl), Integer.valueOf(oldt));
    }

    public void onScrollChangedDelegateSuper(int l, int t, int oldl, int oldt) {
        super.onScrollChangedDelegate(l, t, oldl, oldt);
    }

    public void onFocusChangedDelegate(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (this.onFocusChangedDelegatebooleanintRectMethod == null || this.onFocusChangedDelegatebooleanintRectMethod.isNull()) {
            onFocusChangedDelegateSuper(gainFocus, direction, previouslyFocusedRect);
            return;
        }
        this.onFocusChangedDelegatebooleanintRectMethod.invoke(Boolean.valueOf(gainFocus), Integer.valueOf(direction), previouslyFocusedRect);
    }

    public void onFocusChangedDelegateSuper(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChangedDelegate(gainFocus, direction, previouslyFocusedRect);
    }

    public void onOverScrolledDelegate(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (this.onOverScrolledDelegateintintbooleanbooleanMethod == null || this.onOverScrolledDelegateintintbooleanbooleanMethod.isNull()) {
            onOverScrolledDelegateSuper(scrollX, scrollY, clampedX, clampedY);
            return;
        }
        this.onOverScrolledDelegateintintbooleanbooleanMethod.invoke(Integer.valueOf(scrollX), Integer.valueOf(scrollY), Boolean.valueOf(clampedX), Boolean.valueOf(clampedY));
    }

    public void onOverScrolledDelegateSuper(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolledDelegate(scrollX, scrollY, clampedX, clampedY);
    }

    public void setOnTouchListener(OnTouchListener l) {
        if (this.setOnTouchListenerOnTouchListenerMethod == null || this.setOnTouchListenerOnTouchListenerMethod.isNull()) {
            setOnTouchListenerSuper(l);
            return;
        }
        this.setOnTouchListenerOnTouchListenerMethod.invoke(l);
    }

    public void setOnTouchListenerSuper(OnTouchListener l) {
        super.setOnTouchListener(l);
    }

    public void scrollTo(int x, int y) {
        if (this.scrollTointintMethod == null || this.scrollTointintMethod.isNull()) {
            scrollToSuper(x, y);
            return;
        }
        this.scrollTointintMethod.invoke(Integer.valueOf(x), Integer.valueOf(y));
    }

    public void scrollToSuper(int x, int y) {
        super.scrollTo(x, y);
    }

    public void scrollBy(int x, int y) {
        if (this.scrollByintintMethod == null || this.scrollByintintMethod.isNull()) {
            scrollBySuper(x, y);
            return;
        }
        this.scrollByintintMethod.invoke(Integer.valueOf(x), Integer.valueOf(y));
    }

    public void scrollBySuper(int x, int y) {
        super.scrollBy(x, y);
    }

    public int computeHorizontalScrollRange() {
        if (this.computeHorizontalScrollRangeMethod == null || this.computeHorizontalScrollRangeMethod.isNull()) {
            return computeHorizontalScrollRangeSuper();
        }
        return ((Integer) this.computeHorizontalScrollRangeMethod.invoke(new Object[0])).intValue();
    }

    public int computeHorizontalScrollRangeSuper() {
        return super.computeHorizontalScrollRange();
    }

    public int computeHorizontalScrollOffset() {
        if (this.computeHorizontalScrollOffsetMethod == null || this.computeHorizontalScrollOffsetMethod.isNull()) {
            return computeHorizontalScrollOffsetSuper();
        }
        return ((Integer) this.computeHorizontalScrollOffsetMethod.invoke(new Object[0])).intValue();
    }

    public int computeHorizontalScrollOffsetSuper() {
        return super.computeHorizontalScrollOffset();
    }

    public int computeVerticalScrollRange() {
        if (this.computeVerticalScrollRangeMethod == null || this.computeVerticalScrollRangeMethod.isNull()) {
            return computeVerticalScrollRangeSuper();
        }
        return ((Integer) this.computeVerticalScrollRangeMethod.invoke(new Object[0])).intValue();
    }

    public int computeVerticalScrollRangeSuper() {
        return super.computeVerticalScrollRange();
    }

    public int computeVerticalScrollOffset() {
        if (this.computeVerticalScrollOffsetMethod == null || this.computeVerticalScrollOffsetMethod.isNull()) {
            return computeVerticalScrollOffsetSuper();
        }
        return ((Integer) this.computeVerticalScrollOffsetMethod.invoke(new Object[0])).intValue();
    }

    public int computeVerticalScrollOffsetSuper() {
        return super.computeVerticalScrollOffset();
    }

    public int computeVerticalScrollExtent() {
        if (this.computeVerticalScrollExtentMethod == null || this.computeVerticalScrollExtentMethod.isNull()) {
            return computeVerticalScrollExtentSuper();
        }
        return ((Integer) this.computeVerticalScrollExtentMethod.invoke(new Object[0])).intValue();
    }

    public int computeVerticalScrollExtentSuper() {
        return super.computeVerticalScrollExtent();
    }

    public XWalkExternalExtensionManagerInternal getExtensionManager() {
        if (this.getExtensionManagerMethod == null || this.getExtensionManagerMethod.isNull()) {
            return getExtensionManagerSuper();
        }
        return (XWalkExternalExtensionManagerBridge) this.coreBridge.getBridgeObject(this.getExtensionManagerMethod.invoke(new Object[0]));
    }

    public XWalkExternalExtensionManagerBridge getExtensionManagerSuper() {
        XWalkExternalExtensionManagerInternal ret = super.getExtensionManager();
        if (ret == null) {
            return null;
        }
        return ret instanceof XWalkExternalExtensionManagerBridge ? (XWalkExternalExtensionManagerBridge) ret : null;
    }

    public void clearSslPreferences() {
        if (this.clearSslPreferencesMethod == null || this.clearSslPreferencesMethod.isNull()) {
            clearSslPreferencesSuper();
        } else {
            this.clearSslPreferencesMethod.invoke(new Object[0]);
        }
    }

    public void clearSslPreferencesSuper() {
        super.clearSslPreferences();
    }

    public void clearClientCertPreferences(Runnable callback) {
        if (this.clearClientCertPreferencesRunnableMethod == null || this.clearClientCertPreferencesRunnableMethod.isNull()) {
            clearClientCertPreferencesSuper(callback);
            return;
        }
        this.clearClientCertPreferencesRunnableMethod.invoke(callback);
    }

    public void clearClientCertPreferencesSuper(Runnable callback) {
        super.clearClientCertPreferences(callback);
    }

    public SslCertificate getCertificate() {
        if (this.getCertificateMethod == null || this.getCertificateMethod.isNull()) {
            return getCertificateSuper();
        }
        return (SslCertificate) this.getCertificateMethod.invoke(new Object[0]);
    }

    public SslCertificate getCertificateSuper() {
        SslCertificate ret = super.getCertificate();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setFindListener(XWalkFindListenerInternal listener) {
        if (listener instanceof XWalkFindListenerBridge) {
            setFindListener((XWalkFindListenerBridge) listener);
        } else {
            super.setFindListener(listener);
        }
    }

    public void setFindListener(XWalkFindListenerBridge listener) {
        if (this.setFindListenerXWalkFindListenerInternalMethod == null || this.setFindListenerXWalkFindListenerInternalMethod.isNull()) {
            setFindListenerSuper(listener);
            return;
        }
        ReflectMethod reflectMethod = this.setFindListenerXWalkFindListenerInternalMethod;
        Object[] objArr = new Object[1];
        if (!(listener instanceof XWalkFindListenerBridge)) {
            listener = null;
        }
        objArr[0] = listener.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void setFindListenerSuper(XWalkFindListenerBridge listener) {
        super.setFindListener(listener);
    }

    public void findAllAsync(String searchString) {
        if (this.findAllAsyncStringMethod == null || this.findAllAsyncStringMethod.isNull()) {
            findAllAsyncSuper(searchString);
            return;
        }
        this.findAllAsyncStringMethod.invoke(searchString);
    }

    public void findAllAsyncSuper(String searchString) {
        super.findAllAsync(searchString);
    }

    public void findNext(boolean forward) {
        if (this.findNextbooleanMethod == null || this.findNextbooleanMethod.isNull()) {
            findNextSuper(forward);
            return;
        }
        this.findNextbooleanMethod.invoke(Boolean.valueOf(forward));
    }

    public void findNextSuper(boolean forward) {
        super.findNext(forward);
    }

    public void clearMatches() {
        if (this.clearMatchesMethod == null || this.clearMatchesMethod.isNull()) {
            clearMatchesSuper();
        } else {
            this.clearMatchesMethod.invoke(new Object[0]);
        }
    }

    public void clearMatchesSuper() {
        super.clearMatches();
    }

    public String getCompositingSurfaceType() {
        if (this.getCompositingSurfaceTypeMethod == null || this.getCompositingSurfaceTypeMethod.isNull()) {
            return getCompositingSurfaceTypeSuper();
        }
        return (String) this.getCompositingSurfaceTypeMethod.invoke(new Object[0]);
    }

    public String getCompositingSurfaceTypeSuper() {
        String ret = super.getCompositingSurfaceType();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.loadStringStringMethod.init(this.wrapper, null, "load", String.class, String.class);
            this.loadStringStringMapMethod.init(this.wrapper, null, "load", String.class, String.class, Map.class);
            this.loadDataStringStringStringMethod.init(this.wrapper, null, "loadData", String.class, String.class, String.class);
            this.loadDataWithBaseURLStringStringStringStringStringMethod.init(this.wrapper, null, "loadDataWithBaseURL", String.class, String.class, String.class, String.class, String.class);
            this.loadUrlStringMethod.init(this.wrapper, null, "loadUrl", String.class);
            this.loadUrlStringMapMethod.init(this.wrapper, null, "loadUrl", String.class, Map.class);
            this.loadAppFromManifestStringStringMethod.init(this.wrapper, null, "loadAppFromManifest", String.class, String.class);
            this.reloadintMethod.init(this.wrapper, null, "reload", Integer.TYPE);
            this.stopLoadingMethod.init(this.wrapper, null, "stopLoading", new Class[0]);
            this.getUrlMethod.init(this.wrapper, null, "getUrl", new Class[0]);
            this.getHitTestResultMethod.init(this.wrapper, null, "getHitTestResult", new Class[0]);
            this.getContentHeightMethod.init(this.wrapper, null, "getContentHeight", new Class[0]);
            this.getTitleMethod.init(this.wrapper, null, "getTitle", new Class[0]);
            this.getOriginalUrlMethod.init(this.wrapper, null, "getOriginalUrl", new Class[0]);
            this.getNavigationHistoryMethod.init(this.wrapper, null, "getNavigationHistory", new Class[0]);
            this.addJavascriptInterfaceObjectStringMethod.init(this.wrapper, null, "addJavascriptInterface", Object.class, String.class);
            this.removeJavascriptInterfaceStringMethod.init(this.wrapper, null, "removeJavascriptInterface", String.class);
            this.evaluateJavascriptStringValueCallbackMethod.init(this.wrapper, null, "evaluateJavascript", String.class, ValueCallback.class);
            this.clearCachebooleanMethod.init(this.wrapper, null, "clearCache", Boolean.TYPE);
            this.clearCacheForSingleFileStringMethod.init(this.wrapper, null, "clearCacheForSingleFile", String.class);
            this.hasEnteredFullscreenMethod.init(this.wrapper, null, "hasEnteredFullscreen", new Class[0]);
            this.leaveFullscreenMethod.init(this.wrapper, null, "leaveFullscreen", new Class[0]);
            this.pauseTimersMethod.init(this.wrapper, null, "pauseTimers", new Class[0]);
            this.resumeTimersMethod.init(this.wrapper, null, "resumeTimers", new Class[0]);
            this.onHideMethod.init(this.wrapper, null, "onHide", new Class[0]);
            this.onShowMethod.init(this.wrapper, null, "onShow", new Class[0]);
            this.onDestroyMethod.init(this.wrapper, null, "onDestroy", new Class[0]);
            this.startActivityForResultIntentintBundleMethod.init(this.wrapper, null, "startActivityForResult", Intent.class, Integer.TYPE, Bundle.class);
            this.onActivityResultintintIntentMethod.init(this.wrapper, null, "onActivityResult", Integer.TYPE, Integer.TYPE, Intent.class);
            this.onNewIntentIntentMethod.init(this.wrapper, null, "onNewIntent", Intent.class);
            this.saveStateBundleMethod.init(this.wrapper, null, "saveState", Bundle.class);
            this.restoreStateBundleMethod.init(this.wrapper, null, "restoreState", Bundle.class);
            this.getAPIVersionMethod.init(this.wrapper, null, "getAPIVersion", new Class[0]);
            this.getXWalkVersionMethod.init(this.wrapper, null, "getXWalkVersion", new Class[0]);
            this.setUIClientXWalkUIClientInternalMethod.init(this.wrapper, null, "setUIClient", this.coreBridge.getWrapperClass("XWalkUIClient"));
            this.setResourceClientXWalkResourceClientInternalMethod.init(this.wrapper, null, "setResourceClient", this.coreBridge.getWrapperClass("XWalkResourceClient"));
            this.setBackgroundColorintMethod.init(this.wrapper, null, "setBackgroundColor", Integer.TYPE);
            this.setOriginAccessWhitelistStringStringArrayMethod.init(this.wrapper, null, "setOriginAccessWhitelist", String.class, String[].class);
            this.setLayerTypeintPaintMethod.init(this.wrapper, null, "setLayerType", Integer.TYPE, Paint.class);
            this.setUserAgentStringStringMethod.init(this.wrapper, null, "setUserAgentString", String.class);
            this.getUserAgentStringMethod.init(this.wrapper, null, "getUserAgentString", new Class[0]);
            this.setAcceptLanguagesStringMethod.init(this.wrapper, null, "setAcceptLanguages", String.class);
            this.captureBitmapAsyncXWalkGetBitmapCallbackInternalMethod.init(this.wrapper, null, "captureBitmapAsync", this.coreBridge.getWrapperClass("XWalkGetBitmapCallback"));
            this.getSettingsMethod.init(this.wrapper, null, "getSettings", new Class[0]);
            this.setNetworkAvailablebooleanMethod.init(this.wrapper, null, "setNetworkAvailable", Boolean.TYPE);
            this.getRemoteDebuggingUrlMethod.init(this.wrapper, null, "getRemoteDebuggingUrl", new Class[0]);
            this.zoomInMethod.init(this.wrapper, null, "zoomIn", new Class[0]);
            this.zoomOutMethod.init(this.wrapper, null, "zoomOut", new Class[0]);
            this.zoomByfloatMethod.init(this.wrapper, null, "zoomBy", Float.TYPE);
            this.canZoomInMethod.init(this.wrapper, null, "canZoomIn", new Class[0]);
            this.canZoomOutMethod.init(this.wrapper, null, "canZoomOut", new Class[0]);
            this.onCreateInputConnectionEditorInfoMethod.init(this.wrapper, null, "onCreateInputConnection", EditorInfo.class);
            this.setInitialScaleintMethod.init(this.wrapper, null, "setInitialScale", Integer.TYPE);
            this.getFaviconMethod.init(this.wrapper, null, "getFavicon", new Class[0]);
            this.setZOrderOnTopbooleanMethod.init(this.wrapper, null, "setZOrderOnTop", Boolean.TYPE);
            this.clearFormDataMethod.init(this.wrapper, null, "clearFormData", new Class[0]);
            this.setVisibilityintMethod.init(this.wrapper, null, "setVisibility", Integer.TYPE);
            this.setSurfaceViewVisibilityintMethod.init(this.wrapper, null, "setSurfaceViewVisibility", Integer.TYPE);
            this.setXWalkViewInternalVisibilityintMethod.init(this.wrapper, null, "setXWalkViewInternalVisibility", Integer.TYPE);
            this.setDownloadListenerXWalkDownloadListenerInternalMethod.init(this.wrapper, null, "setDownloadListener", this.coreBridge.getWrapperClass("XWalkDownloadListener"));
            this.performLongClickDelegateMethod.init(this.wrapper, null, "performLongClickDelegate", new Class[0]);
            this.onTouchEventDelegateMotionEventMethod.init(this.wrapper, null, "onTouchEventDelegate", MotionEvent.class);
            this.onTouchEventMotionEventMethod.init(this.wrapper, null, "onTouchEvent", MotionEvent.class);
            this.onScrollChangedDelegateintintintintMethod.init(this.wrapper, null, "onScrollChangedDelegate", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            this.onFocusChangedDelegatebooleanintRectMethod.init(this.wrapper, null, "onFocusChangedDelegate", Boolean.TYPE, Integer.TYPE, Rect.class);
            this.onOverScrolledDelegateintintbooleanbooleanMethod.init(this.wrapper, null, "onOverScrolledDelegate", Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE);
            this.setOnTouchListenerOnTouchListenerMethod.init(this.wrapper, null, "setOnTouchListener", OnTouchListener.class);
            this.scrollTointintMethod.init(this.wrapper, null, "scrollTo", Integer.TYPE, Integer.TYPE);
            this.scrollByintintMethod.init(this.wrapper, null, "scrollBy", Integer.TYPE, Integer.TYPE);
            this.computeHorizontalScrollRangeMethod.init(this.wrapper, null, "computeHorizontalScrollRange", new Class[0]);
            this.computeHorizontalScrollOffsetMethod.init(this.wrapper, null, "computeHorizontalScrollOffset", new Class[0]);
            this.computeVerticalScrollRangeMethod.init(this.wrapper, null, "computeVerticalScrollRange", new Class[0]);
            this.computeVerticalScrollOffsetMethod.init(this.wrapper, null, "computeVerticalScrollOffset", new Class[0]);
            this.computeVerticalScrollExtentMethod.init(this.wrapper, null, "computeVerticalScrollExtent", new Class[0]);
            this.getExtensionManagerMethod.init(this.wrapper, null, "getExtensionManager", new Class[0]);
            this.clearSslPreferencesMethod.init(this.wrapper, null, "clearSslPreferences", new Class[0]);
            this.clearClientCertPreferencesRunnableMethod.init(this.wrapper, null, "clearClientCertPreferences", Runnable.class);
            this.getCertificateMethod.init(this.wrapper, null, "getCertificate", new Class[0]);
            this.setFindListenerXWalkFindListenerInternalMethod.init(this.wrapper, null, "setFindListener", this.coreBridge.getWrapperClass("XWalkFindListener"));
            this.findAllAsyncStringMethod.init(this.wrapper, null, "findAllAsync", String.class);
            this.findNextbooleanMethod.init(this.wrapper, null, "findNext", Boolean.TYPE);
            this.clearMatchesMethod.init(this.wrapper, null, "clearMatches", new Class[0]);
            this.getCompositingSurfaceTypeMethod.init(this.wrapper, null, "getCompositingSurfaceType", new Class[0]);
        }
    }
}
