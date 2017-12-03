package org.xwalk.core.internal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ConsoleMessage.MessageLevel;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.widget.Toast;
import java.security.Principal;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.x500.X500Principal;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.components.navigation_interception.InterceptNavigationDelegate;
import org.chromium.components.navigation_interception.NavigationParams;
import org.chromium.content.browser.ContentVideoViewEmbedder;
import org.xwalk.core.internal.ClientCertLookupTable.Cert;
import org.xwalk.core.internal.XWalkContentsClient.WebResourceRequestInner;
import org.xwalk.core.internal.XWalkGeolocationPermissions.Callback;
import org.xwalk.core.internal.XWalkUIClientInternal.ConsoleMessageType;
import org.xwalk.core.internal.XWalkUIClientInternal.InitiateByInternal;
import org.xwalk.core.internal.XWalkUIClientInternal.JavascriptMessageTypeInternal;
import org.xwalk.core.internal.XWalkUIClientInternal.LoadStatusInternal;

@JNINamespace("xwalk")
class XWalkContentsClientBridge extends XWalkContentsClient {
    static final /* synthetic */ boolean $assertionsDisabled = (!XWalkContentsClientBridge.class.desiredAssertionStatus());
    private static final int NEW_ICON_DOWNLOAD = 101;
    private static final int NEW_XWALKVIEW_CREATED = 100;
    private static final String TAG = XWalkContentsClientBridge.class.getName();
    private XWalkDownloadListenerInternal mDownloadListener;
    private Bitmap mFavicon;
    private XWalkFindListenerInternal mFindListener;
    private InterceptNavigationDelegate mInterceptNavigationDelegate;
    private boolean mIsFullscreen = false;
    private LoadStatusInternal mLoadStatus = LoadStatusInternal.FINISHED;
    private String mLoadingUrl = null;
    protected ClientCertLookupTable mLookupTable;
    protected long mNativeContentsClientBridge;
    private XWalkNavigationHandler mNavigationHandler;
    private XWalkNotificationService mNotificationService;
    private PageLoadListener mPageLoadListener;
    private float mPageScaleFactor;
    private Handler mUiThreadHandler;
    private XWalkClient mXWalkClient;
    private XWalkResourceClientInternal mXWalkResourceClient;
    private XWalkUIClientInternal mXWalkUIClient;
    private XWalkViewInternal mXWalkView;
    private XWalkWebChromeClient mXWalkWebChromeClient;

    class C03381 extends Handler {
        C03381() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case XWalkContentsClientBridge.NEW_XWALKVIEW_CREATED /*100*/:
                    XWalkViewInternal newXWalkView = msg.obj;
                    if (newXWalkView == XWalkContentsClientBridge.this.mXWalkView) {
                        throw new IllegalArgumentException("Parent XWalkView cannot host it's own popup window");
                    } else if (newXWalkView == null || newXWalkView.getNavigationHistory().size() == 0) {
                        XWalkContentsClientBridge.this.mXWalkView.completeWindowCreation(newXWalkView);
                        return;
                    } else {
                        throw new IllegalArgumentException("New WebView for popup window must not have been previously navigated.");
                    }
                case XWalkContentsClientBridge.NEW_ICON_DOWNLOAD /*101*/:
                    XWalkContentsClientBridge.this.nativeDownloadIcon(XWalkContentsClientBridge.this.mNativeContentsClientBridge, msg.obj);
                    return;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    class C03392 implements ValueCallback<XWalkViewInternal> {
        C03392() {
        }

        public void onReceiveValue(XWalkViewInternal newXWalkView) {
            XWalkContentsClientBridge.this.mUiThreadHandler.obtainMessage(XWalkContentsClientBridge.NEW_XWALKVIEW_CREATED, newXWalkView).sendToTarget();
        }
    }

    static /* synthetic */ class C03415 {
        static final /* synthetic */ int[] $SwitchMap$android$webkit$ConsoleMessage$MessageLevel = new int[MessageLevel.values().length];

        static {
            try {
                $SwitchMap$android$webkit$ConsoleMessage$MessageLevel[MessageLevel.TIP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$webkit$ConsoleMessage$MessageLevel[MessageLevel.LOG.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$webkit$ConsoleMessage$MessageLevel[MessageLevel.WARNING.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$webkit$ConsoleMessage$MessageLevel[MessageLevel.ERROR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private class InterceptNavigationDelegateImpl implements InterceptNavigationDelegate {
        private XWalkContentsClient mContentsClient;

        public InterceptNavigationDelegateImpl(XWalkContentsClient client) {
            this.mContentsClient = client;
        }

        public boolean shouldIgnoreNavigation(NavigationParams navigationParams) {
            String url = navigationParams.url;
            boolean ignoreNavigation = XWalkContentsClientBridge.this.mNavigationHandler != null && XWalkContentsClientBridge.this.mNavigationHandler.handleNavigation(navigationParams);
            if (!ignoreNavigation) {
                String fallbackUrl = XWalkContentsClientBridge.this.mNavigationHandler.getFallbackUrl();
                if (fallbackUrl != null) {
                    XWalkContentsClientBridge.this.mNavigationHandler.resetFallbackUrl();
                    XWalkContentsClientBridge.this.mXWalkView.loadUrl(fallbackUrl);
                } else {
                    this.mContentsClient.getCallbackHelper().postOnPageStarted(url);
                }
            }
            return ignoreNavigation;
        }
    }

    private native void nativeCancelJsResult(long j, int i);

    private native void nativeClearClientCertPreferences(long j, Runnable runnable);

    private native void nativeConfirmJsResult(long j, int i, String str);

    private native void nativeDownloadIcon(long j, String str);

    private native void nativeNotificationClicked(long j, int i);

    private native void nativeNotificationClosed(long j, int i, boolean z);

    private native void nativeNotificationDisplayed(long j, int i);

    private native void nativeOnFilesNotSelected(long j, int i, int i2, int i3);

    private native void nativeOnFilesSelected(long j, int i, int i2, int i3, String str, String str2);

    private native void nativeProceedSslError(long j, boolean z, int i);

    private native void nativeProvideClientCertificateResponse(long j, int i, byte[][] bArr, PrivateKey privateKey);

    public XWalkContentsClientBridge(XWalkViewInternal xwView) {
        this.mXWalkView = xwView;
        this.mLookupTable = new ClientCertLookupTable();
        this.mInterceptNavigationDelegate = new InterceptNavigationDelegateImpl(this);
        this.mUiThreadHandler = new C03381();
    }

    public void setUIClient(XWalkUIClientInternal client) {
        if (client == null) {
            this.mXWalkUIClient = new XWalkUIClientInternal(this.mXWalkView);
        } else {
            this.mXWalkUIClient = client;
        }
        this.mXWalkUIClient.setContentsClient(this);
    }

    public void setResourceClient(XWalkResourceClientInternal client) {
        if (client != null) {
            this.mXWalkResourceClient = client;
        } else {
            this.mXWalkResourceClient = new XWalkResourceClientInternal(this.mXWalkView);
        }
    }

    public void setXWalkWebChromeClient(XWalkWebChromeClient client) {
        if (client != null) {
            this.mXWalkWebChromeClient = client;
        }
    }

    public XWalkWebChromeClient getXWalkWebChromeClient() {
        return this.mXWalkWebChromeClient;
    }

    public void setXWalkClient(XWalkClient client) {
        this.mXWalkClient = client;
    }

    public void setNavigationHandler(XWalkNavigationHandler handler) {
        this.mNavigationHandler = handler;
    }

    void registerPageLoadListener(PageLoadListener listener) {
        this.mPageLoadListener = listener;
    }

    public void setNotificationService(XWalkNotificationService service) {
        if (this.mNotificationService != null) {
            this.mNotificationService.shutdown();
        }
        this.mNotificationService = service;
        if (this.mNotificationService != null) {
            this.mNotificationService.setBridge(this);
        }
    }

    public boolean onNewIntent(Intent intent) {
        return this.mNotificationService.maybeHandleIntent(intent);
    }

    public InterceptNavigationDelegate getInterceptNavigationDelegate() {
        return this.mInterceptNavigationDelegate;
    }

    public boolean shouldOverrideUrlLoading(String url) {
        if (this.mXWalkResourceClient == null || this.mXWalkView == null) {
            return false;
        }
        return this.mXWalkResourceClient.shouldOverrideUrlLoading(this.mXWalkView, url);
    }

    public boolean shouldOverrideKeyEvent(KeyEvent event) {
        boolean overridden = false;
        if (!(this.mXWalkUIClient == null || this.mXWalkView == null)) {
            overridden = this.mXWalkUIClient.shouldOverrideKeyEvent(this.mXWalkView, event);
        }
        if (overridden) {
            return overridden;
        }
        return super.shouldOverrideKeyEvent(event);
    }

    public void onUnhandledKeyEvent(KeyEvent event) {
        if (this.mXWalkUIClient != null && this.mXWalkView != null) {
            this.mXWalkUIClient.onUnhandledKeyEvent(this.mXWalkView, event);
        }
    }

    public void getVisitedHistory(ValueCallback<String[]> valueCallback) {
    }

    public void doUpdateVisitedHistory(String url, boolean isReload) {
        this.mXWalkResourceClient.doUpdateVisitedHistory(this.mXWalkView, url, isReload);
    }

    public void onProgressChanged(int progress) {
        this.mXWalkResourceClient.onProgressChanged(this.mXWalkView, progress);
    }

    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        this.mXWalkView.onOverScrolledDelegate(scrollX, scrollY, clampedX, clampedY);
    }

    public XWalkWebResourceResponseInternal shouldInterceptRequest(WebResourceRequestInner request) {
        WebResourceResponse response = this.mXWalkResourceClient.shouldInterceptLoadRequest(this.mXWalkView, request.url);
        if (response != null) {
            return new XWalkWebResourceResponseInternal(response.getMimeType(), response.getEncoding(), response.getData());
        }
        XWalkWebResourceResponseInternal xwalkResponse = this.mXWalkResourceClient.shouldInterceptLoadRequest(this.mXWalkView, new XWalkWebResourceRequestHandlerInternal(request));
        if (xwalkResponse == null) {
            return null;
        }
        Map<String, String> responseHeaders = xwalkResponse.getResponseHeaders();
        if (responseHeaders == null) {
            responseHeaders = new HashMap();
        }
        return new XWalkWebResourceResponseInternal(xwalkResponse.getMimeType(), xwalkResponse.getEncoding(), xwalkResponse.getData(), xwalkResponse.getStatusCode(), xwalkResponse.getReasonPhrase(), responseHeaders);
    }

    public void onDidChangeThemeColor(int color) {
        this.mXWalkUIClient.onDidChangeThemeColor(this.mXWalkView, color);
    }

    public void onDocumentLoadedInFrame(long frameId) {
        this.mXWalkResourceClient.onDocumentLoadedInFrame(this.mXWalkView, frameId);
    }

    public void onResourceLoadStarted(String url) {
        this.mXWalkResourceClient.onLoadStarted(this.mXWalkView, url);
    }

    public void onResourceLoadFinished(String url) {
        this.mXWalkResourceClient.onLoadFinished(this.mXWalkView, url);
    }

    public void onLoadResource(String url) {
        if (this.mXWalkClient != null) {
            this.mXWalkClient.onLoadResource(this.mXWalkView, url);
        }
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (this.mXWalkClient == null || this.mXWalkView == null) {
            return false;
        }
        ConsoleMessageType consoleMessageType = ConsoleMessageType.DEBUG;
        switch (C03415.$SwitchMap$android$webkit$ConsoleMessage$MessageLevel[consoleMessage.messageLevel().ordinal()]) {
            case 1:
                consoleMessageType = ConsoleMessageType.INFO;
                break;
            case 2:
                consoleMessageType = ConsoleMessageType.LOG;
                break;
            case 3:
                consoleMessageType = ConsoleMessageType.WARNING;
                break;
            case 4:
                consoleMessageType = ConsoleMessageType.ERROR;
                break;
            default:
                Log.w(TAG, "Unknown message level, defaulting to DEBUG");
                break;
        }
        return this.mXWalkUIClient.onConsoleMessage(this.mXWalkView, consoleMessage.message(), consoleMessage.lineNumber(), consoleMessage.sourceId(), consoleMessageType);
    }

    @CalledByNative
    public void onReceivedHttpAuthRequest(XWalkHttpAuthHandlerInternal handler, String host, String realm) {
        if (this.mXWalkResourceClient != null) {
            this.mXWalkResourceClient.onReceivedHttpAuthRequest(this.mXWalkView, handler, host, realm);
        }
    }

    public void onReceivedSslError(ValueCallback<Boolean> callback, SslError error) {
        if (this.mXWalkResourceClient != null) {
            this.mXWalkResourceClient.onReceivedSslError(this.mXWalkView, callback, error);
        }
    }

    public void onReceivedLoginRequest(String realm, String account, String args) {
    }

    public void onReceivedClientCertRequest(ClientCertRequestInternal handler) {
        if (this.mXWalkResourceClient != null) {
            this.mXWalkResourceClient.onReceivedClientCertRequest(this.mXWalkView, handler);
        }
    }

    public void onReceivedResponseHeaders(WebResourceRequestInner request, XWalkWebResourceResponseInternal response) {
        if (this.mXWalkResourceClient != null) {
            this.mXWalkResourceClient.onReceivedResponseHeaders(this.mXWalkView, new XWalkWebResourceRequestHandlerInternal(request), response);
        }
    }

    public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
        if (this.mXWalkWebChromeClient != null) {
            this.mXWalkWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }

    public void onGeolocationPermissionsHidePrompt() {
        if (this.mXWalkWebChromeClient != null) {
            this.mXWalkWebChromeClient.onGeolocationPermissionsHidePrompt();
        }
    }

    public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
        if (this.mFindListener != null) {
            this.mFindListener.onFindResultReceived(activeMatchOrdinal, numberOfMatches, isDoneCounting);
        }
    }

    public void onNewPicture(Picture picture) {
    }

    public void onPageStarted(String url) {
        if (this.mXWalkUIClient != null) {
            this.mLoadingUrl = url;
            this.mLoadStatus = LoadStatusInternal.FINISHED;
            this.mXWalkUIClient.onPageLoadStarted(this.mXWalkView, url);
        }
    }

    public void onPageFinished(String url) {
        if (this.mPageLoadListener != null) {
            this.mPageLoadListener.onPageFinished(url);
        }
        if (this.mXWalkUIClient != null) {
            if (this.mLoadStatus != LoadStatusInternal.CANCELLED || this.mLoadingUrl == null) {
                this.mXWalkUIClient.onPageLoadStopped(this.mXWalkView, url, this.mLoadStatus);
            } else {
                this.mXWalkUIClient.onPageLoadStopped(this.mXWalkView, this.mLoadingUrl, this.mLoadStatus);
            }
            this.mLoadingUrl = null;
        }
        onResourceLoadFinished(url);
    }

    protected void onStopLoading() {
        this.mLoadStatus = LoadStatusInternal.CANCELLED;
    }

    public void onReceivedError(int errorCode, String description, String failingUrl) {
        if (this.mLoadingUrl != null && this.mLoadingUrl.equals(failingUrl)) {
            this.mLoadStatus = LoadStatusInternal.FAILED;
        }
        this.mXWalkResourceClient.onReceivedLoadError(this.mXWalkView, errorCode, description, failingUrl);
    }

    public void onRendererUnresponsive() {
        if (this.mXWalkClient != null) {
            this.mXWalkClient.onRendererUnresponsive(this.mXWalkView);
        }
    }

    public void onRendererResponsive() {
        if (this.mXWalkClient != null) {
            this.mXWalkClient.onRendererResponsive(this.mXWalkView);
        }
    }

    public void onFormResubmission(Message dontResend, Message resend) {
        dontResend.sendToTarget();
    }

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        if (this.mDownloadListener != null) {
            this.mDownloadListener.onDownloadStart(url, userAgent, contentDisposition, mimeType, contentLength);
        }
    }

    public boolean onCreateWindow(boolean isDialog, boolean isUserGesture) {
        if (isDialog) {
            return false;
        }
        InitiateByInternal initiator = InitiateByInternal.BY_JAVASCRIPT;
        if (isUserGesture) {
            initiator = InitiateByInternal.BY_USER_GESTURE;
        }
        return this.mXWalkUIClient.onCreateWindowRequested(this.mXWalkView, initiator, new C03392());
    }

    public void onRequestFocus() {
        this.mXWalkUIClient.onRequestFocus(this.mXWalkView);
    }

    public void onCloseWindow() {
        this.mXWalkUIClient.onJavascriptCloseWindow(this.mXWalkView);
    }

    public void onShowCustomView(View view, CustomViewCallbackInternal callback) {
        if (this.mXWalkUIClient != null) {
            this.mXWalkUIClient.onShowCustomView(view, callback);
        }
    }

    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallbackInternal callback) {
        if (this.mXWalkUIClient != null) {
            this.mXWalkUIClient.onShowCustomView(view, requestedOrientation, callback);
        }
    }

    public void onHideCustomView() {
        if (this.mXWalkUIClient != null) {
            this.mXWalkUIClient.onHideCustomView();
        }
    }

    public void onScaleChangedScaled(float oldScale, float newScale) {
        this.mXWalkUIClient.onScaleChanged(this.mXWalkView, oldScale, newScale);
    }

    public void didFinishLoad(String url) {
    }

    public void onTitleChanged(String title) {
        if (this.mXWalkUIClient != null) {
            this.mXWalkUIClient.onReceivedTitle(this.mXWalkView, title);
        }
    }

    public void onToggleFullscreen(boolean enterFullscreen) {
        this.mIsFullscreen = enterFullscreen;
        this.mXWalkUIClient.onFullscreenToggled(this.mXWalkView, enterFullscreen);
    }

    public boolean hasEnteredFullscreen() {
        return this.mIsFullscreen;
    }

    public boolean shouldCreateWebContents(String contentUrl) {
        return true;
    }

    public boolean shouldOverrideRunFileChooser(final int processId, final int renderId, final int modeFlags, String acceptTypes, boolean capture) {
        AnonymousClass1UriCallback uploadFile = new AnonymousClass1UriCallback() {
            boolean completed = false;

            public void onReceiveValue(Uri value) {
                if (this.completed) {
                    throw new IllegalStateException("Duplicate openFileChooser result");
                } else if (value != null || this.syncCallFinished) {
                    this.completed = true;
                    if (value == null) {
                        XWalkContentsClientBridge.this.nativeOnFilesNotSelected(XWalkContentsClientBridge.this.mNativeContentsClientBridge, processId, renderId, modeFlags);
                        return;
                    }
                    String displayName;
                    String result = "";
                    if (AndroidProtocolHandler.FILE_SCHEME.equals(value.getScheme())) {
                        result = value.getSchemeSpecificPart();
                        displayName = value.getLastPathSegment();
                    } else if ("content".equals(value.getScheme())) {
                        result = value.toString();
                        displayName = resolveFileName(value, XWalkContentsClientBridge.this.mXWalkView.getContext().getContentResolver());
                    } else {
                        result = value.getPath();
                        displayName = value.getLastPathSegment();
                    }
                    if (displayName == null || displayName.isEmpty()) {
                        displayName = result;
                    }
                    XWalkContentsClientBridge.this.nativeOnFilesSelected(XWalkContentsClientBridge.this.mNativeContentsClientBridge, processId, renderId, modeFlags, result, displayName);
                } else {
                    this.syncNullReceived = true;
                }
            }
        };
        this.mXWalkUIClient.openFileChooser(this.mXWalkView, uploadFile, acceptTypes, Boolean.toString(capture));
        uploadFile.syncCallFinished = true;
        if (uploadFile.syncNullReceived) {
            return false;
        }
        return true;
    }

    public ContentVideoViewEmbedder getContentVideoViewEmbedder() {
        return new XWalkContentVideoViewClient(this, this.mXWalkView);
    }

    public void provideClientCertificateResponse(int id, byte[][] certChain, PrivateKey privateKey) {
        nativeProvideClientCertificateResponse(this.mNativeContentsClientBridge, id, certChain, privateKey);
    }

    public Bitmap getFavicon() {
        return this.mFavicon;
    }

    @CalledByNative
    private void setNativeContentsClientBridge(long nativeContentsClientBridge) {
        this.mNativeContentsClientBridge = nativeContentsClientBridge;
    }

    @CalledByNative
    private boolean allowCertificateError(int certError, byte[] derBytes, String url, final int id) {
        if (SslUtil.shouldDenyRequest(certError)) {
            Toast.makeText(this.mXWalkView.getContext(), C0332R.string.ssl_error_deny_request, 0).show();
            return false;
        }
        SslCertificate cert = SslUtil.getCertificateFromDerBytes(derBytes);
        if (cert == null) {
            return false;
        }
        onReceivedSslError(new ValueCallback<Boolean>() {
            public void onReceiveValue(Boolean value) {
                XWalkContentsClientBridge.this.proceedSslError(value.booleanValue(), id);
            }
        }, SslUtil.sslErrorFromNetErrorCode(certError, cert, url));
        return true;
    }

    @CalledByNative
    private void selectClientCertificate(int id, String[] keyTypes, byte[][] encodedPrincipals, String host, int port) {
        if (this.mXWalkResourceClient == null) {
            return;
        }
        if ($assertionsDisabled || this.mNativeContentsClientBridge != 0) {
            Cert cert = this.mLookupTable.getCertData(host, port);
            if (this.mLookupTable.isDenied(host, port)) {
                nativeProvideClientCertificateResponse(this.mNativeContentsClientBridge, id, (byte[][]) null, null);
                return;
            } else if (cert != null) {
                nativeProvideClientCertificateResponse(this.mNativeContentsClientBridge, id, cert.mCertChain, cert.mPrivateKey);
                return;
            } else {
                Principal[] principals = null;
                if (encodedPrincipals.length > 0) {
                    principals = new X500Principal[encodedPrincipals.length];
                    int n = 0;
                    while (n < encodedPrincipals.length) {
                        try {
                            principals[n] = new X500Principal(encodedPrincipals[n]);
                            n++;
                        } catch (IllegalArgumentException e) {
                            Log.w(TAG, "Exception while decoding issuers list: " + e);
                            nativeProvideClientCertificateResponse(this.mNativeContentsClientBridge, id, (byte[][]) null, null);
                            return;
                        }
                    }
                }
                onReceivedClientCertRequest(new ClientCertRequestHandlerInternal(this, id, keyTypes, principals, host, port));
                return;
            }
        }
        throw new AssertionError();
    }

    public void clearClientCertPreferences(Runnable callback) {
        this.mLookupTable.clear();
        if (this.mNativeContentsClientBridge != 0) {
            nativeClearClientCertPreferences(this.mNativeContentsClientBridge, callback);
        } else if (callback != null) {
            callback.run();
        }
    }

    @CalledByNative
    private void clientCertificatesCleared(Runnable callback) {
        if (callback != null) {
            callback.run();
        }
    }

    private void proceedSslError(boolean proceed, int id) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeProceedSslError(this.mNativeContentsClientBridge, proceed, id);
        }
    }

    @CalledByNative
    private void handleJsAlert(String url, String message, int id) {
        String str = url;
        String str2 = message;
        this.mXWalkUIClient.onJavascriptModalDialog(this.mXWalkView, JavascriptMessageTypeInternal.JAVASCRIPT_ALERT, str, str2, "", new XWalkJavascriptResultHandlerInternal(this, id));
    }

    @CalledByNative
    private void handleJsConfirm(String url, String message, int id) {
        String str = url;
        String str2 = message;
        this.mXWalkUIClient.onJavascriptModalDialog(this.mXWalkView, JavascriptMessageTypeInternal.JAVASCRIPT_CONFIRM, str, str2, "", new XWalkJavascriptResultHandlerInternal(this, id));
    }

    @CalledByNative
    private void handleJsPrompt(String url, String message, String defaultValue, int id) {
        this.mXWalkUIClient.onJavascriptModalDialog(this.mXWalkView, JavascriptMessageTypeInternal.JAVASCRIPT_PROMPT, url, message, defaultValue, new XWalkJavascriptResultHandlerInternal(this, id));
    }

    @CalledByNative
    private void handleJsBeforeUnload(String url, String message, int id) {
        String str = url;
        String str2 = message;
        this.mXWalkUIClient.onJavascriptModalDialog(this.mXWalkView, JavascriptMessageTypeInternal.JAVASCRIPT_BEFOREUNLOAD, str, str2, "", new XWalkJavascriptResultHandlerInternal(this, id));
    }

    @CalledByNative
    private boolean shouldOverrideUrlLoading(String url, boolean hasUserGesture, boolean isRedirect, boolean isMainFrame) {
        return shouldOverrideUrlLoading(url);
    }

    @CalledByNative
    private void showNotification(String title, String message, String replaceId, Bitmap icon, int notificationId) {
        this.mNotificationService.showNotification(title, message, replaceId, icon, notificationId);
    }

    @CalledByNative
    private void cancelNotification(int notificationId) {
        this.mNotificationService.cancelNotification(notificationId);
    }

    void confirmJsResult(int id, String prompt) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeConfirmJsResult(this.mNativeContentsClientBridge, id, prompt);
        }
    }

    void cancelJsResult(int id) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeCancelJsResult(this.mNativeContentsClientBridge, id);
        }
    }

    public void notificationDisplayed(int id) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeNotificationDisplayed(this.mNativeContentsClientBridge, id);
        }
    }

    public void notificationClicked(int id) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeNotificationClicked(this.mNativeContentsClientBridge, id);
        }
    }

    public void notificationClosed(int id, boolean byUser) {
        if (this.mNativeContentsClientBridge != 0) {
            nativeNotificationClosed(this.mNativeContentsClientBridge, id, byUser);
        }
    }

    void setDownloadListener(XWalkDownloadListenerInternal listener) {
        this.mDownloadListener = listener;
    }

    void setFindListener(XWalkFindListenerInternal listener) {
        this.mFindListener = listener;
    }

    @CalledByNative
    public void onWebLayoutPageScaleFactorChanged(float pageScaleFactor) {
        if (this.mPageScaleFactor != pageScaleFactor) {
            float oldPageScaleFactor = this.mPageScaleFactor;
            this.mPageScaleFactor = pageScaleFactor;
            onScaleChanged(oldPageScaleFactor, this.mPageScaleFactor);
        }
    }

    @CalledByNative
    public void onIconAvailable(String url) {
        this.mXWalkUIClient.onIconAvailable(this.mXWalkView, url, this.mUiThreadHandler.obtainMessage(NEW_ICON_DOWNLOAD, url));
    }

    @CalledByNative
    public void onReceivedIcon(String url, Bitmap icon) {
        this.mXWalkUIClient.onReceivedIcon(this.mXWalkView, url, icon);
        this.mFavicon = icon;
    }
}
