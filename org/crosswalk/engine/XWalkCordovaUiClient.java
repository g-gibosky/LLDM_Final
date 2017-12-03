package org.crosswalk.engine;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.ValueCallback;
import org.apache.cordova.CordovaDialogsHelper;
import org.apache.cordova.CordovaDialogsHelper.Result;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.crosswalk.engine.XWalkWebViewEngine.PermissionRequestListener;
import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkUIClient.JavascriptMessageType;
import org.xwalk.core.XWalkUIClient.LoadStatus;
import org.xwalk.core.XWalkView;

public class XWalkCordovaUiClient extends XWalkUIClient {
    static final /* synthetic */ boolean $assertionsDisabled = (!XWalkCordovaUiClient.class.desiredAssertionStatus());
    private static final int FILECHOOSER_RESULTCODE = 5173;
    private static final String TAG = "XWalkCordovaUiClient";
    protected final CordovaDialogsHelper dialogsHelper;
    private XWalkFileChooser mFileChooser;
    private CordovaPlugin mFileChooserResultPlugin;
    protected final XWalkWebViewEngine parentEngine;

    class C04634 extends CordovaPlugin {
        C04634() {
        }

        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            XWalkCordovaUiClient.this.mFileChooser.onActivityResult(requestCode, resultCode, intent);
        }
    }

    public XWalkCordovaUiClient(XWalkWebViewEngine parentEngine) {
        super(parentEngine.webView);
        this.parentEngine = parentEngine;
        this.dialogsHelper = new CordovaDialogsHelper(parentEngine.webView.getContext());
    }

    public boolean onJavascriptModalDialog(XWalkView view, JavascriptMessageType type, String url, String message, String defaultValue, XWalkJavascriptResult result) {
        switch (type) {
            case JAVASCRIPT_ALERT:
                return onJsAlert(view, url, message, result);
            case JAVASCRIPT_CONFIRM:
                return onJsConfirm(view, url, message, result);
            case JAVASCRIPT_PROMPT:
                return onJsPrompt(view, url, message, defaultValue, result);
            case JAVASCRIPT_BEFOREUNLOAD:
                return onJsConfirm(view, url, message, result);
            default:
                if ($assertionsDisabled) {
                    return false;
                }
                throw new AssertionError();
        }
    }

    public boolean onJsAlert(XWalkView view, String url, String message, final XWalkJavascriptResult result) {
        this.dialogsHelper.showAlert(message, new Result() {
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    public boolean onJsConfirm(XWalkView view, String url, String message, final XWalkJavascriptResult result) {
        this.dialogsHelper.showConfirm(message, new Result() {
            public void gotResult(boolean success, String value) {
                if (success) {
                    result.confirm();
                } else {
                    result.cancel();
                }
            }
        });
        return true;
    }

    public boolean onJsPrompt(XWalkView view, String origin, String message, String defaultValue, final XWalkJavascriptResult result) {
        String handledRet = this.parentEngine.bridge.promptOnJsPrompt(origin, message, defaultValue);
        if (handledRet != null) {
            result.confirmWithResult(handledRet);
        } else {
            this.dialogsHelper.showPrompt(message, defaultValue, new Result() {
                public void gotResult(boolean success, String value) {
                    if (success) {
                        result.confirmWithResult(value);
                    } else {
                        result.cancel();
                    }
                }
            });
        }
        return true;
    }

    public void onPageLoadStarted(XWalkView view, String url) {
        LOG.m4d(TAG, "onPageLoadStarted(" + url + ")");
        if (view.getUrl() != null) {
            this.parentEngine.client.onPageStarted(url);
            this.parentEngine.bridge.reset();
        }
    }

    public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
        LOG.m4d(TAG, "onPageLoadStopped(" + url + ")");
        if (status == LoadStatus.FINISHED) {
            this.parentEngine.client.onPageFinishedLoading(url);
        } else if (status != LoadStatus.FAILED) {
        }
    }

    public void openFileChooser(XWalkView view, final ValueCallback<Uri> uploadFile, final String acceptType, final String capture) {
        if (this.mFileChooser == null) {
            this.mFileChooser = new XWalkFileChooser(this.parentEngine.cordova.getActivity());
            this.mFileChooserResultPlugin = new C04634();
        }
        if (!this.parentEngine.requestPermissionsForFileChooser(new PermissionRequestListener() {
            public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
                for (int i = 0; i < permissions.length; i++) {
                    Log.d(XWalkCordovaUiClient.TAG, "permission:" + permissions[i] + " result:" + grantResults[i]);
                }
                XWalkCordovaUiClient.this.parentEngine.cordova.setActivityResultCallback(XWalkCordovaUiClient.this.mFileChooserResultPlugin);
                XWalkCordovaUiClient.this.mFileChooser.showFileChooser(uploadFile, acceptType, capture);
            }
        })) {
            this.parentEngine.cordova.setActivityResultCallback(this.mFileChooserResultPlugin);
            this.mFileChooser.showFileChooser(uploadFile, acceptType, capture);
        }
    }
}
