package org.xwalk.core.internal;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import org.xwalk.core.internal.XWalkContentsClient.WebResourceRequestInner;

class XWalkContentsClientCallbackHelper {
    private static final int MSG_ON_DOWNLOAD_START = 3;
    private static final int MSG_ON_LOAD_RESOURCE = 1;
    private static final int MSG_ON_PAGE_FINISHED = 7;
    private static final int MSG_ON_PAGE_STARTED = 2;
    private static final int MSG_ON_RECEIVED_ERROR = 5;
    private static final int MSG_ON_RECEIVED_LOGIN_REQUEST = 4;
    private static final int MSG_ON_RECEIVED_RESPONSE_HEADERS = 8;
    private static final int MSG_ON_RESOURCE_LOAD_STARTED = 6;
    private final XWalkContentsClient mContentsClient;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onLoadResource(msg.obj);
                    return;
                case 2:
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onPageStarted((String) msg.obj);
                    return;
                case 3:
                    DownloadInfo info = msg.obj;
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onDownloadStart(info.mUrl, info.mUserAgent, info.mContentDisposition, info.mMimeType, info.mContentLength);
                    return;
                case 4:
                    LoginRequestInfo info2 = msg.obj;
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onReceivedLoginRequest(info2.mRealm, info2.mAccount, info2.mArgs);
                    return;
                case 5:
                    OnReceivedErrorInfo info3 = msg.obj;
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onReceivedError(info3.mErrorCode, info3.mDescription, info3.mFailingUrl);
                    return;
                case 6:
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onResourceLoadStarted((String) msg.obj);
                    return;
                case 7:
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onPageFinished((String) msg.obj);
                    return;
                case 8:
                    OnReceivedResponseHeadersInfo info4 = msg.obj;
                    XWalkContentsClientCallbackHelper.this.mContentsClient.onReceivedResponseHeaders(info4.mRequest, info4.mResponse);
                    return;
                default:
                    throw new IllegalStateException("XWalkContentsClientCallbackHelper: unhandled message " + msg.what);
            }
        }
    };

    private static class DownloadInfo {
        final String mContentDisposition;
        final long mContentLength;
        final String mMimeType;
        final String mUrl;
        final String mUserAgent;

        DownloadInfo(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            this.mUrl = url;
            this.mUserAgent = userAgent;
            this.mContentDisposition = contentDisposition;
            this.mMimeType = mimeType;
            this.mContentLength = contentLength;
        }
    }

    private static class LoginRequestInfo {
        final String mAccount;
        final String mArgs;
        final String mRealm;

        LoginRequestInfo(String realm, String account, String args) {
            this.mRealm = realm;
            this.mAccount = account;
            this.mArgs = args;
        }
    }

    private static class OnReceivedErrorInfo {
        final String mDescription;
        final int mErrorCode;
        final String mFailingUrl;

        OnReceivedErrorInfo(int errorCode, String description, String failingUrl) {
            this.mErrorCode = errorCode;
            this.mDescription = description;
            this.mFailingUrl = failingUrl;
        }
    }

    private static class OnReceivedResponseHeadersInfo {
        final WebResourceRequestInner mRequest;
        final XWalkWebResourceResponseInternal mResponse;

        OnReceivedResponseHeadersInfo(WebResourceRequestInner request, XWalkWebResourceResponseInternal response) {
            this.mRequest = request;
            this.mResponse = response;
        }
    }

    public XWalkContentsClientCallbackHelper(XWalkContentsClient contentsClient) {
        this.mContentsClient = contentsClient;
    }

    public void postOnLoadResource(String url) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(1, url));
    }

    public void postOnPageStarted(String url) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(2, url));
    }

    public void postOnDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, new DownloadInfo(url, userAgent, contentDisposition, mimeType, contentLength)));
    }

    public void postOnReceivedLoginRequest(String realm, String account, String args) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(4, new LoginRequestInfo(realm, account, args)));
    }

    public void postOnReceivedError(int errorCode, String description, String failingUrl) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(5, new OnReceivedErrorInfo(errorCode, description, failingUrl)));
    }

    public void postOnResourceLoadStarted(String url) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(6, url));
    }

    public void postOnPageFinished(String url) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(7, url));
    }

    public void postOnReceivedResponseHeaders(WebResourceRequestInner request, XWalkWebResourceResponseInternal response) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(8, new OnReceivedResponseHeadersInfo(request, response)));
    }
}
