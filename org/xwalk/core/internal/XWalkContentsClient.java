package org.xwalk.core.internal;

import android.graphics.Picture;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import java.security.PrivateKey;
import java.util.HashMap;
import org.chromium.content.browser.ContentViewClient;
import org.chromium.content_public.browser.WebContents;
import org.chromium.content_public.browser.WebContentsObserver;
import org.xwalk.core.internal.XWalkGeolocationPermissions.Callback;

abstract class XWalkContentsClient extends ContentViewClient {
    private static final String TAG = "XWalkContentsClient";
    private final XWalkContentsClientCallbackHelper mCallbackHelper = new XWalkContentsClientCallbackHelper(this);
    private double mDIPScale;
    private XWalkWebContentsObserver mWebContentsObserver;

    public static class WebResourceRequestInner {
        public boolean hasUserGesture;
        public boolean isMainFrame;
        public String method;
        public HashMap<String, String> requestHeaders;
        public String url;
    }

    public class XWalkWebContentsObserver extends WebContentsObserver {
        public XWalkWebContentsObserver(WebContents webContents) {
            super(webContents);
        }

        public void didChangeThemeColor(int color) {
            if (XWalkPreferencesInternal.getValue("enable-theme-color")) {
                XWalkContentsClient.this.onDidChangeThemeColor(color);
            }
        }

        public void didStopLoading(String url) {
            XWalkContentsClient.this.mCallbackHelper.postOnPageFinished(url);
        }

        public void didFailLoad(boolean isProvisionalLoad, boolean isMainFrame, int errorCode, String description, String failingUrl, boolean wasIgnoredByHandler) {
            if (errorCode != -3 && isMainFrame) {
                XWalkContentsClient.this.onReceivedError(ErrorCodeConversionHelper.convertErrorCode(errorCode), description, failingUrl);
            }
        }

        public void didNavigateAnyFrame(String url, String baseUrl, boolean isReload) {
            XWalkContentsClient.this.doUpdateVisitedHistory(url, isReload);
        }

        public void didFinishLoad(long frameId, String validatedUrl, boolean isMainFrame) {
        }

        public void documentLoadedInFrame(long frameId, boolean isMainFrame) {
            XWalkContentsClient.this.onDocumentLoadedInFrame(frameId);
        }
    }

    public abstract void didFinishLoad(String str);

    public abstract void doUpdateVisitedHistory(String str, boolean z);

    public abstract void getVisitedHistory(ValueCallback<String[]> valueCallback);

    public abstract boolean hasEnteredFullscreen();

    protected abstract void onCloseWindow();

    public abstract boolean onConsoleMessage(ConsoleMessage consoleMessage);

    protected abstract boolean onCreateWindow(boolean z, boolean z2);

    public abstract void onDidChangeThemeColor(int i);

    public abstract void onDocumentLoadedInFrame(long j);

    public abstract void onDownloadStart(String str, String str2, String str3, String str4, long j);

    public abstract void onFindResultReceived(int i, int i2, boolean z);

    public abstract void onFormResubmission(Message message, Message message2);

    public abstract void onGeolocationPermissionsHidePrompt();

    public abstract void onGeolocationPermissionsShowPrompt(String str, Callback callback);

    public abstract void onHideCustomView();

    public abstract void onLoadResource(String str);

    public abstract void onNewPicture(Picture picture);

    public abstract void onPageFinished(String str);

    public abstract void onPageStarted(String str);

    public abstract void onProgressChanged(int i);

    public abstract void onReceivedClientCertRequest(ClientCertRequestInternal clientCertRequestInternal);

    public abstract void onReceivedError(int i, String str, String str2);

    public abstract void onReceivedHttpAuthRequest(XWalkHttpAuthHandlerInternal xWalkHttpAuthHandlerInternal, String str, String str2);

    public abstract void onReceivedLoginRequest(String str, String str2, String str3);

    public abstract void onReceivedResponseHeaders(WebResourceRequestInner webResourceRequestInner, XWalkWebResourceResponseInternal xWalkWebResourceResponseInternal);

    public abstract void onReceivedSslError(ValueCallback<Boolean> valueCallback, SslError sslError);

    public abstract void onRendererResponsive();

    public abstract void onRendererUnresponsive();

    protected abstract void onRequestFocus();

    public abstract void onResourceLoadFinished(String str);

    public abstract void onResourceLoadStarted(String str);

    public abstract void onScaleChangedScaled(float f, float f2);

    public abstract void onShowCustomView(View view, int i, CustomViewCallbackInternal customViewCallbackInternal);

    protected abstract void onStopLoading();

    public abstract void onTitleChanged(String str);

    public abstract void onToggleFullscreen(boolean z);

    public abstract void onUnhandledKeyEvent(KeyEvent keyEvent);

    public abstract void provideClientCertificateResponse(int i, byte[][] bArr, PrivateKey privateKey);

    public abstract boolean shouldCreateWebContents(String str);

    public abstract XWalkWebResourceResponseInternal shouldInterceptRequest(WebResourceRequestInner webResourceRequestInner);

    public abstract boolean shouldOverrideRunFileChooser(int i, int i2, int i3, String str, boolean z);

    public abstract boolean shouldOverrideUrlLoading(String str);

    XWalkContentsClient() {
    }

    public final void onUpdateTitle(String title) {
        onTitleChanged(title);
    }

    public boolean shouldOverrideKeyEvent(KeyEvent event) {
        return super.shouldOverrideKeyEvent(event);
    }

    void installWebContentsObserver(WebContents webContents) {
        if (this.mWebContentsObserver != null) {
            this.mWebContentsObserver.destroy();
        }
        this.mWebContentsObserver = new XWalkWebContentsObserver(webContents);
    }

    void setDIPScale(double dipScale) {
        this.mDIPScale = dipScale;
    }

    final XWalkContentsClientCallbackHelper getCallbackHelper() {
        return this.mCallbackHelper;
    }

    public final void onScaleChanged(float oldScaleFactor, float newScaleFactor) {
        onScaleChangedScaled((float) (((double) oldScaleFactor) * this.mDIPScale), (float) (((double) newScaleFactor) * this.mDIPScale));
    }

    public void onShowCustomView(View view, CustomViewCallbackInternal callback) {
        onShowCustomView(view, -1, callback);
    }
}
