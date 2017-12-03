package org.xwalk.core.internal;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.InputStream;
import java.util.Map;

@XWalkAPI(createExternally = true)
public class XWalkResourceClientInternal {
    @XWalkAPI
    public static final int ERROR_AUTHENTICATION = -4;
    @XWalkAPI
    public static final int ERROR_BAD_URL = -12;
    @XWalkAPI
    public static final int ERROR_CONNECT = -6;
    @XWalkAPI
    public static final int ERROR_FAILED_SSL_HANDSHAKE = -11;
    @XWalkAPI
    public static final int ERROR_FILE = -13;
    @XWalkAPI
    public static final int ERROR_FILE_NOT_FOUND = -14;
    @XWalkAPI
    public static final int ERROR_HOST_LOOKUP = -2;
    @XWalkAPI
    public static final int ERROR_IO = -7;
    @XWalkAPI
    public static final int ERROR_OK = 0;
    @XWalkAPI
    public static final int ERROR_PROXY_AUTHENTICATION = -5;
    @XWalkAPI
    public static final int ERROR_REDIRECT_LOOP = -9;
    @XWalkAPI
    public static final int ERROR_TIMEOUT = -8;
    @XWalkAPI
    public static final int ERROR_TOO_MANY_REQUESTS = -15;
    @XWalkAPI
    public static final int ERROR_UNKNOWN = -1;
    @XWalkAPI
    public static final int ERROR_UNSUPPORTED_AUTH_SCHEME = -3;
    @XWalkAPI
    public static final int ERROR_UNSUPPORTED_SCHEME = -10;

    @XWalkAPI
    public XWalkResourceClientInternal(XWalkViewInternal view) {
    }

    @XWalkAPI
    public void onDocumentLoadedInFrame(XWalkViewInternal view, long frameId) {
    }

    @XWalkAPI
    public void onLoadStarted(XWalkViewInternal view, String url) {
    }

    @XWalkAPI
    public void onLoadFinished(XWalkViewInternal view, String url) {
    }

    @XWalkAPI
    public void onProgressChanged(XWalkViewInternal view, int progressInPercent) {
    }

    @XWalkAPI
    public WebResourceResponse shouldInterceptLoadRequest(XWalkViewInternal view, String url) {
        return null;
    }

    @XWalkAPI
    public XWalkWebResourceResponseInternal shouldInterceptLoadRequest(XWalkViewInternal view, XWalkWebResourceRequestInternal request) {
        return null;
    }

    @XWalkAPI
    public void onReceivedLoadError(XWalkViewInternal view, int errorCode, String description, String failingUrl) {
        Toast.makeText(view.getContext(), description, 0).show();
    }

    @XWalkAPI
    public boolean shouldOverrideUrlLoading(XWalkViewInternal view, String url) {
        return false;
    }

    @XWalkAPI
    public void onReceivedSslError(XWalkViewInternal view, ValueCallback<Boolean> callback, SslError error) {
        final ValueCallback<Boolean> valueCallback = callback;
        Builder dialogBuilder = new Builder(view.getContext());
        dialogBuilder.setTitle(C0332R.string.ssl_alert_title).setPositiveButton(17039370, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                valueCallback.onReceiveValue(Boolean.valueOf(true));
                dialog.dismiss();
            }
        }).setNegativeButton(17039360, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                valueCallback.onReceiveValue(Boolean.valueOf(false));
                dialog.dismiss();
            }
        }).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                valueCallback.onReceiveValue(Boolean.valueOf(false));
            }
        });
        dialogBuilder.create().show();
    }

    @XWalkAPI
    public void onReceivedClientCertRequest(XWalkViewInternal view, ClientCertRequestInternal handler) {
        handler.cancel();
    }

    @XWalkAPI
    public void onReceivedResponseHeaders(XWalkViewInternal view, XWalkWebResourceRequestInternal request, XWalkWebResourceResponseInternal response) {
    }

    @XWalkAPI
    public void doUpdateVisitedHistory(XWalkViewInternal view, String url, boolean isReload) {
    }

    @XWalkAPI
    public void onReceivedHttpAuthRequest(XWalkViewInternal view, XWalkHttpAuthHandlerInternal handler, String host, String realm) {
        if (view != null) {
            final XWalkHttpAuthHandlerInternal haHandler = handler;
            Context context = view.getContext();
            LinearLayout layout = new LinearLayout(context);
            final EditText userNameEditText = new EditText(context);
            final EditText passwordEditText = new EditText(context);
            layout.setOrientation(1);
            if (VERSION.SDK_INT >= 17) {
                layout.setPaddingRelative(10, 0, 10, 20);
            } else {
                layout.setPadding(10, 0, 10, 20);
            }
            userNameEditText.setHint(C0332R.string.http_auth_user_name);
            passwordEditText.setHint(C0332R.string.http_auth_password);
            passwordEditText.setInputType(129);
            layout.addView(userNameEditText);
            layout.addView(passwordEditText);
            new Builder(view.getContext()).setTitle(C0332R.string.http_auth_title).setView(layout).setCancelable(false).setPositiveButton(C0332R.string.http_auth_log_in, new OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    haHandler.proceed(userNameEditText.getText().toString(), passwordEditText.getText().toString());
                    dialog.dismiss();
                }
            }).setNegativeButton(17039360, null).setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    haHandler.cancel();
                }
            }).create().show();
        }
    }

    @XWalkAPI
    public XWalkWebResourceResponseInternal createXWalkWebResourceResponse(String mimeType, String encoding, InputStream data) {
        return new XWalkWebResourceResponseInternal(mimeType, encoding, data);
    }

    @XWalkAPI
    public XWalkWebResourceResponseInternal createXWalkWebResourceResponse(String mimeType, String encoding, InputStream data, int statusCode, String reasonPhrase, Map<String, String> responseHeaders) {
        return new XWalkWebResourceResponseInternal(mimeType, encoding, data, statusCode, reasonPhrase, responseHeaders);
    }
}
