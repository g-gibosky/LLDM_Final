package org.xwalk.core.internal;

import android.net.http.SslError;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import java.io.InputStream;
import java.util.Map;

public class XWalkResourceClientBridge extends XWalkResourceClientInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod createXWalkWebResourceResponseStringStringInputStreamMethod = new ReflectMethod(null, "createXWalkWebResourceResponse", new Class[0]);
    private ReflectMethod f20x2cd1027 = new ReflectMethod(null, "createXWalkWebResourceResponse", new Class[0]);
    private ReflectMethod doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod = new ReflectMethod(null, "doUpdateVisitedHistory", new Class[0]);
    private ReflectMethod onDocumentLoadedInFrameXWalkViewInternallongMethod = new ReflectMethod(null, "onDocumentLoadedInFrame", new Class[0]);
    private ReflectMethod onLoadFinishedXWalkViewInternalStringMethod = new ReflectMethod(null, "onLoadFinished", new Class[0]);
    private ReflectMethod onLoadStartedXWalkViewInternalStringMethod = new ReflectMethod(null, "onLoadStarted", new Class[0]);
    private ReflectMethod onProgressChangedXWalkViewInternalintMethod = new ReflectMethod(null, "onProgressChanged", new Class[0]);
    private ReflectMethod f21x4c27f13b = new ReflectMethod(null, "onReceivedClientCertRequest", new Class[0]);
    private ReflectMethod f22xafae7cfd = new ReflectMethod(null, "onReceivedHttpAuthRequest", new Class[0]);
    private ReflectMethod onReceivedLoadErrorXWalkViewInternalintStringStringMethod = new ReflectMethod(null, "onReceivedLoadError", new Class[0]);
    private ReflectMethod f23x503d83ad = new ReflectMethod(null, "onReceivedResponseHeaders", new Class[0]);
    private ReflectMethod onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod = new ReflectMethod(null, "onReceivedSslError", new Class[0]);
    private ReflectMethod shouldInterceptLoadRequestXWalkViewInternalStringMethod = new ReflectMethod(null, "shouldInterceptLoadRequest", new Class[0]);
    private ReflectMethod f24x85899a63 = new ReflectMethod(null, "shouldInterceptLoadRequest", new Class[0]);
    private ReflectMethod shouldOverrideUrlLoadingXWalkViewInternalStringMethod = new ReflectMethod(null, "shouldOverrideUrlLoading", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkResourceClientBridge(XWalkViewBridge view, Object wrapper) {
        super(view);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public void onDocumentLoadedInFrame(XWalkViewInternal view, long frameId) {
        if (view instanceof XWalkViewBridge) {
            onDocumentLoadedInFrame((XWalkViewBridge) view, frameId);
        } else {
            super.onDocumentLoadedInFrame(view, frameId);
        }
    }

    public void onDocumentLoadedInFrame(XWalkViewBridge view, long frameId) {
        if (this.onDocumentLoadedInFrameXWalkViewInternallongMethod == null || this.onDocumentLoadedInFrameXWalkViewInternallongMethod.isNull()) {
            onDocumentLoadedInFrameSuper(view, frameId);
            return;
        }
        ReflectMethod reflectMethod = this.onDocumentLoadedInFrameXWalkViewInternallongMethod;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = Long.valueOf(frameId);
        reflectMethod.invoke(objArr);
    }

    public void onDocumentLoadedInFrameSuper(XWalkViewBridge view, long frameId) {
        super.onDocumentLoadedInFrame(view, frameId);
    }

    public void onLoadStarted(XWalkViewInternal view, String url) {
        if (view instanceof XWalkViewBridge) {
            onLoadStarted((XWalkViewBridge) view, url);
        } else {
            super.onLoadStarted(view, url);
        }
    }

    public void onLoadStarted(XWalkViewBridge view, String url) {
        if (this.onLoadStartedXWalkViewInternalStringMethod == null || this.onLoadStartedXWalkViewInternalStringMethod.isNull()) {
            onLoadStartedSuper(view, url);
            return;
        }
        ReflectMethod reflectMethod = this.onLoadStartedXWalkViewInternalStringMethod;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        reflectMethod.invoke(objArr);
    }

    public void onLoadStartedSuper(XWalkViewBridge view, String url) {
        super.onLoadStarted(view, url);
    }

    public void onLoadFinished(XWalkViewInternal view, String url) {
        if (view instanceof XWalkViewBridge) {
            onLoadFinished((XWalkViewBridge) view, url);
        } else {
            super.onLoadFinished(view, url);
        }
    }

    public void onLoadFinished(XWalkViewBridge view, String url) {
        if (this.onLoadFinishedXWalkViewInternalStringMethod == null || this.onLoadFinishedXWalkViewInternalStringMethod.isNull()) {
            onLoadFinishedSuper(view, url);
            return;
        }
        ReflectMethod reflectMethod = this.onLoadFinishedXWalkViewInternalStringMethod;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        reflectMethod.invoke(objArr);
    }

    public void onLoadFinishedSuper(XWalkViewBridge view, String url) {
        super.onLoadFinished(view, url);
    }

    public void onProgressChanged(XWalkViewInternal view, int progressInPercent) {
        if (view instanceof XWalkViewBridge) {
            onProgressChanged((XWalkViewBridge) view, progressInPercent);
        } else {
            super.onProgressChanged(view, progressInPercent);
        }
    }

    public void onProgressChanged(XWalkViewBridge view, int progressInPercent) {
        if (this.onProgressChangedXWalkViewInternalintMethod == null || this.onProgressChangedXWalkViewInternalintMethod.isNull()) {
            onProgressChangedSuper(view, progressInPercent);
            return;
        }
        ReflectMethod reflectMethod = this.onProgressChangedXWalkViewInternalintMethod;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = Integer.valueOf(progressInPercent);
        reflectMethod.invoke(objArr);
    }

    public void onProgressChangedSuper(XWalkViewBridge view, int progressInPercent) {
        super.onProgressChanged(view, progressInPercent);
    }

    public WebResourceResponse shouldInterceptLoadRequest(XWalkViewInternal view, String url) {
        if (view instanceof XWalkViewBridge) {
            return shouldInterceptLoadRequest((XWalkViewBridge) view, url);
        }
        return super.shouldInterceptLoadRequest(view, url);
    }

    public WebResourceResponse shouldInterceptLoadRequest(XWalkViewBridge view, String url) {
        if (this.shouldInterceptLoadRequestXWalkViewInternalStringMethod == null || this.shouldInterceptLoadRequestXWalkViewInternalStringMethod.isNull()) {
            return shouldInterceptLoadRequestSuper(view, url);
        }
        ReflectMethod reflectMethod = this.shouldInterceptLoadRequestXWalkViewInternalStringMethod;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        return (WebResourceResponse) reflectMethod.invoke(objArr);
    }

    public WebResourceResponse shouldInterceptLoadRequestSuper(XWalkViewBridge view, String url) {
        WebResourceResponse ret = super.shouldInterceptLoadRequest((XWalkViewInternal) view, url);
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public XWalkWebResourceResponseInternal shouldInterceptLoadRequest(XWalkViewInternal view, XWalkWebResourceRequestInternal request) {
        if (!(view instanceof XWalkViewBridge)) {
            return super.shouldInterceptLoadRequest(view, request);
        }
        return shouldInterceptLoadRequest((XWalkViewBridge) view, request instanceof XWalkWebResourceRequestHandlerBridge ? (XWalkWebResourceRequestHandlerBridge) request : new XWalkWebResourceRequestHandlerBridge((XWalkWebResourceRequestHandlerInternal) request));
    }

    public XWalkWebResourceResponseInternal shouldInterceptLoadRequest(XWalkViewBridge view, XWalkWebResourceRequestHandlerBridge request) {
        if (this.f24x85899a63 == null || this.f24x85899a63.isNull()) {
            return shouldInterceptLoadRequestSuper(view, request);
        }
        XWalkCoreBridge xWalkCoreBridge = this.coreBridge;
        ReflectMethod reflectMethod = this.f24x85899a63;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        if (!(request instanceof XWalkWebResourceRequestHandlerBridge)) {
            request = new XWalkWebResourceRequestHandlerBridge(request);
        }
        objArr[1] = request.getWrapper();
        return (XWalkWebResourceResponseBridge) xWalkCoreBridge.getBridgeObject(reflectMethod.invoke(objArr));
    }

    public XWalkWebResourceResponseBridge shouldInterceptLoadRequestSuper(XWalkViewBridge view, XWalkWebResourceRequestHandlerBridge request) {
        XWalkWebResourceResponseInternal ret = super.shouldInterceptLoadRequest((XWalkViewInternal) view, (XWalkWebResourceRequestInternal) request);
        if (ret == null) {
            return null;
        }
        return ret instanceof XWalkWebResourceResponseBridge ? (XWalkWebResourceResponseBridge) ret : new XWalkWebResourceResponseBridge(ret);
    }

    public void onReceivedLoadError(XWalkViewInternal view, int errorCode, String description, String failingUrl) {
        if (view instanceof XWalkViewBridge) {
            onReceivedLoadError((XWalkViewBridge) view, errorCode, description, failingUrl);
        } else {
            super.onReceivedLoadError(view, errorCode, description, failingUrl);
        }
    }

    public void onReceivedLoadError(XWalkViewBridge view, int errorCode, String description, String failingUrl) {
        if (this.onReceivedLoadErrorXWalkViewInternalintStringStringMethod == null || this.onReceivedLoadErrorXWalkViewInternalintStringStringMethod.isNull()) {
            onReceivedLoadErrorSuper(view, errorCode, description, failingUrl);
            return;
        }
        ReflectMethod reflectMethod = this.onReceivedLoadErrorXWalkViewInternalintStringStringMethod;
        Object[] objArr = new Object[4];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = Integer.valueOf(errorCode);
        objArr[2] = description;
        objArr[3] = failingUrl;
        reflectMethod.invoke(objArr);
    }

    public void onReceivedLoadErrorSuper(XWalkViewBridge view, int errorCode, String description, String failingUrl) {
        super.onReceivedLoadError(view, errorCode, description, failingUrl);
    }

    public boolean shouldOverrideUrlLoading(XWalkViewInternal view, String url) {
        if (view instanceof XWalkViewBridge) {
            return shouldOverrideUrlLoading((XWalkViewBridge) view, url);
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    public boolean shouldOverrideUrlLoading(XWalkViewBridge view, String url) {
        if (this.shouldOverrideUrlLoadingXWalkViewInternalStringMethod == null || this.shouldOverrideUrlLoadingXWalkViewInternalStringMethod.isNull()) {
            return shouldOverrideUrlLoadingSuper(view, url);
        }
        ReflectMethod reflectMethod = this.shouldOverrideUrlLoadingXWalkViewInternalStringMethod;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public boolean shouldOverrideUrlLoadingSuper(XWalkViewBridge view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }

    public void onReceivedSslError(XWalkViewInternal view, ValueCallback<Boolean> callback, SslError error) {
        if (view instanceof XWalkViewBridge) {
            onReceivedSslError((XWalkViewBridge) view, (ValueCallback) callback, error);
        } else {
            super.onReceivedSslError(view, callback, error);
        }
    }

    public void onReceivedSslError(XWalkViewBridge view, ValueCallback<Boolean> callback, SslError error) {
        if (this.onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod == null || this.onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod.isNull()) {
            onReceivedSslErrorSuper(view, callback, error);
            return;
        }
        ReflectMethod reflectMethod = this.onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod;
        Object[] objArr = new Object[3];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = callback;
        objArr[2] = error;
        reflectMethod.invoke(objArr);
    }

    public void onReceivedSslErrorSuper(XWalkViewBridge view, ValueCallback<Boolean> callback, SslError error) {
        super.onReceivedSslError(view, callback, error);
    }

    public void onReceivedClientCertRequest(XWalkViewInternal view, ClientCertRequestInternal handler) {
        if (view instanceof XWalkViewBridge) {
            onReceivedClientCertRequest((XWalkViewBridge) view, handler instanceof ClientCertRequestHandlerBridge ? (ClientCertRequestHandlerBridge) handler : new ClientCertRequestHandlerBridge((ClientCertRequestHandlerInternal) handler));
        } else {
            super.onReceivedClientCertRequest(view, handler);
        }
    }

    public void onReceivedClientCertRequest(XWalkViewBridge view, ClientCertRequestHandlerBridge handler) {
        if (this.f21x4c27f13b == null || this.f21x4c27f13b.isNull()) {
            onReceivedClientCertRequestSuper(view, handler);
            return;
        }
        ReflectMethod reflectMethod = this.f21x4c27f13b;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        if (!(handler instanceof ClientCertRequestHandlerBridge)) {
            handler = new ClientCertRequestHandlerBridge(handler);
        }
        objArr[1] = handler.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void onReceivedClientCertRequestSuper(XWalkViewBridge view, ClientCertRequestHandlerBridge handler) {
        super.onReceivedClientCertRequest(view, handler);
    }

    public void onReceivedResponseHeaders(XWalkViewInternal view, XWalkWebResourceRequestInternal request, XWalkWebResourceResponseInternal response) {
        if (view instanceof XWalkViewBridge) {
            XWalkWebResourceResponseBridge response2;
            XWalkViewBridge xWalkViewBridge = (XWalkViewBridge) view;
            XWalkWebResourceRequestHandlerBridge xWalkWebResourceRequestHandlerBridge = request instanceof XWalkWebResourceRequestHandlerBridge ? (XWalkWebResourceRequestHandlerBridge) request : new XWalkWebResourceRequestHandlerBridge((XWalkWebResourceRequestHandlerInternal) request);
            if (response instanceof XWalkWebResourceResponseBridge) {
                response2 = (XWalkWebResourceResponseBridge) response;
            } else {
                response = new XWalkWebResourceResponseBridge(response);
            }
            onReceivedResponseHeaders(xWalkViewBridge, xWalkWebResourceRequestHandlerBridge, response2);
            return;
        }
        super.onReceivedResponseHeaders(view, request, response);
    }

    public void onReceivedResponseHeaders(XWalkViewBridge view, XWalkWebResourceRequestHandlerBridge request, XWalkWebResourceResponseBridge response) {
        if (this.f23x503d83ad == null || this.f23x503d83ad.isNull()) {
            onReceivedResponseHeadersSuper(view, request, response);
            return;
        }
        ReflectMethod reflectMethod = this.f23x503d83ad;
        Object[] objArr = new Object[3];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        if (!(request instanceof XWalkWebResourceRequestHandlerBridge)) {
            request = new XWalkWebResourceRequestHandlerBridge(request);
        }
        objArr[1] = request.getWrapper();
        if (!(response instanceof XWalkWebResourceResponseBridge)) {
            response = new XWalkWebResourceResponseBridge(response);
        }
        objArr[2] = response.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void onReceivedResponseHeadersSuper(XWalkViewBridge view, XWalkWebResourceRequestHandlerBridge request, XWalkWebResourceResponseBridge response) {
        super.onReceivedResponseHeaders(view, request, response);
    }

    public void doUpdateVisitedHistory(XWalkViewInternal view, String url, boolean isReload) {
        if (view instanceof XWalkViewBridge) {
            doUpdateVisitedHistory((XWalkViewBridge) view, url, isReload);
        } else {
            super.doUpdateVisitedHistory(view, url, isReload);
        }
    }

    public void doUpdateVisitedHistory(XWalkViewBridge view, String url, boolean isReload) {
        if (this.doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod == null || this.doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod.isNull()) {
            doUpdateVisitedHistorySuper(view, url, isReload);
            return;
        }
        ReflectMethod reflectMethod = this.doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod;
        Object[] objArr = new Object[3];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        objArr[2] = Boolean.valueOf(isReload);
        reflectMethod.invoke(objArr);
    }

    public void doUpdateVisitedHistorySuper(XWalkViewBridge view, String url, boolean isReload) {
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    public void onReceivedHttpAuthRequest(XWalkViewInternal view, XWalkHttpAuthHandlerInternal handler, String host, String realm) {
        if (view instanceof XWalkViewBridge) {
            XWalkHttpAuthHandlerBridge handler2;
            XWalkViewBridge xWalkViewBridge = (XWalkViewBridge) view;
            if (handler instanceof XWalkHttpAuthHandlerBridge) {
                handler2 = (XWalkHttpAuthHandlerBridge) handler;
            } else {
                handler = new XWalkHttpAuthHandlerBridge(handler);
            }
            onReceivedHttpAuthRequest(xWalkViewBridge, handler2, host, realm);
            return;
        }
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    public void onReceivedHttpAuthRequest(XWalkViewBridge view, XWalkHttpAuthHandlerBridge handler, String host, String realm) {
        if (this.f22xafae7cfd == null || this.f22xafae7cfd.isNull()) {
            onReceivedHttpAuthRequestSuper(view, handler, host, realm);
            return;
        }
        ReflectMethod reflectMethod = this.f22xafae7cfd;
        Object[] objArr = new Object[4];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        if (!(handler instanceof XWalkHttpAuthHandlerBridge)) {
            handler = new XWalkHttpAuthHandlerBridge(handler);
        }
        objArr[1] = handler.getWrapper();
        objArr[2] = host;
        objArr[3] = realm;
        reflectMethod.invoke(objArr);
    }

    public void onReceivedHttpAuthRequestSuper(XWalkViewBridge view, XWalkHttpAuthHandlerBridge handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    public XWalkWebResourceResponseInternal createXWalkWebResourceResponse(String mimeType, String encoding, InputStream data) {
        if (this.createXWalkWebResourceResponseStringStringInputStreamMethod == null || this.createXWalkWebResourceResponseStringStringInputStreamMethod.isNull()) {
            return createXWalkWebResourceResponseSuper(mimeType, encoding, data);
        }
        return (XWalkWebResourceResponseBridge) this.coreBridge.getBridgeObject(this.createXWalkWebResourceResponseStringStringInputStreamMethod.invoke(mimeType, encoding, data));
    }

    public XWalkWebResourceResponseBridge createXWalkWebResourceResponseSuper(String mimeType, String encoding, InputStream data) {
        XWalkWebResourceResponseInternal ret = super.createXWalkWebResourceResponse(mimeType, encoding, data);
        if (ret == null) {
            return null;
        }
        return ret instanceof XWalkWebResourceResponseBridge ? (XWalkWebResourceResponseBridge) ret : new XWalkWebResourceResponseBridge(ret);
    }

    public XWalkWebResourceResponseInternal createXWalkWebResourceResponse(String mimeType, String encoding, InputStream data, int statusCode, String reasonPhrase, Map<String, String> responseHeaders) {
        if (this.f20x2cd1027 == null || this.f20x2cd1027.isNull()) {
            return createXWalkWebResourceResponseSuper(mimeType, encoding, data, statusCode, reasonPhrase, responseHeaders);
        }
        return (XWalkWebResourceResponseBridge) this.coreBridge.getBridgeObject(this.f20x2cd1027.invoke(mimeType, encoding, data, Integer.valueOf(statusCode), reasonPhrase, responseHeaders));
    }

    public XWalkWebResourceResponseBridge createXWalkWebResourceResponseSuper(String mimeType, String encoding, InputStream data, int statusCode, String reasonPhrase, Map<String, String> responseHeaders) {
        XWalkWebResourceResponseInternal ret = super.createXWalkWebResourceResponse(mimeType, encoding, data, statusCode, reasonPhrase, responseHeaders);
        if (ret == null) {
            return null;
        }
        return ret instanceof XWalkWebResourceResponseBridge ? (XWalkWebResourceResponseBridge) ret : new XWalkWebResourceResponseBridge(ret);
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.onDocumentLoadedInFrameXWalkViewInternallongMethod.init(this.wrapper, null, "onDocumentLoadedInFrame", this.coreBridge.getWrapperClass("XWalkView"), Long.TYPE);
            this.onLoadStartedXWalkViewInternalStringMethod.init(this.wrapper, null, "onLoadStarted", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.onLoadFinishedXWalkViewInternalStringMethod.init(this.wrapper, null, "onLoadFinished", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.onProgressChangedXWalkViewInternalintMethod.init(this.wrapper, null, "onProgressChanged", this.coreBridge.getWrapperClass("XWalkView"), Integer.TYPE);
            this.shouldInterceptLoadRequestXWalkViewInternalStringMethod.init(this.wrapper, null, "shouldInterceptLoadRequest", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.f24x85899a63.init(this.wrapper, null, "shouldInterceptLoadRequest", this.coreBridge.getWrapperClass("XWalkView"), this.coreBridge.getWrapperClass("XWalkWebResourceRequest"));
            this.onReceivedLoadErrorXWalkViewInternalintStringStringMethod.init(this.wrapper, null, "onReceivedLoadError", this.coreBridge.getWrapperClass("XWalkView"), Integer.TYPE, String.class, String.class);
            this.shouldOverrideUrlLoadingXWalkViewInternalStringMethod.init(this.wrapper, null, "shouldOverrideUrlLoading", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.onReceivedSslErrorXWalkViewInternalValueCallbackSslErrorMethod.init(this.wrapper, null, "onReceivedSslError", this.coreBridge.getWrapperClass("XWalkView"), ValueCallback.class, SslError.class);
            this.f21x4c27f13b.init(this.wrapper, null, "onReceivedClientCertRequest", this.coreBridge.getWrapperClass("XWalkView"), this.coreBridge.getWrapperClass("ClientCertRequest"));
            this.f23x503d83ad.init(this.wrapper, null, "onReceivedResponseHeaders", this.coreBridge.getWrapperClass("XWalkView"), this.coreBridge.getWrapperClass("XWalkWebResourceRequest"), this.coreBridge.getWrapperClass("XWalkWebResourceResponse"));
            this.doUpdateVisitedHistoryXWalkViewInternalStringbooleanMethod.init(this.wrapper, null, "doUpdateVisitedHistory", this.coreBridge.getWrapperClass("XWalkView"), String.class, Boolean.TYPE);
            this.f22xafae7cfd.init(this.wrapper, null, "onReceivedHttpAuthRequest", this.coreBridge.getWrapperClass("XWalkView"), this.coreBridge.getWrapperClass("XWalkHttpAuthHandler"), String.class, String.class);
            this.createXWalkWebResourceResponseStringStringInputStreamMethod.init(this.wrapper, null, "createXWalkWebResourceResponse", String.class, String.class, InputStream.class);
            this.f20x2cd1027.init(this.wrapper, null, "createXWalkWebResourceResponse", String.class, String.class, InputStream.class, Integer.TYPE, String.class, Map.class);
        }
    }
}
