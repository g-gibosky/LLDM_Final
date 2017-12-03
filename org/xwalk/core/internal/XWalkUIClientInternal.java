package org.xwalk.core.internal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import org.chromium.base.ApiCompatibilityUtils;

@XWalkAPI(createExternally = true)
public class XWalkUIClientInternal {
    static final /* synthetic */ boolean $assertionsDisabled = (!XWalkUIClientInternal.class.desiredAssertionStatus());
    private final int INVALID_ORIENTATION = -2;
    private XWalkContentsClient mContentsClient;
    private Context mContext;
    private CustomViewCallbackInternal mCustomViewCallback;
    private View mCustomXWalkView;
    private AlertDialog mDialog;
    private boolean mIsFullscreen = false;
    private boolean mOriginalForceNotFullscreen;
    private boolean mOriginalFullscreen;
    private int mPreOrientation = -2;
    private EditText mPromptText;
    private int mSystemUiFlag;
    private XWalkViewInternal mXWalkView;

    class C03694 implements OnClickListener {
        C03694() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    class C03727 implements OnClickListener {
        C03727() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    @XWalkAPI
    public enum ConsoleMessageType {
        DEBUG,
        ERROR,
        LOG,
        INFO,
        WARNING
    }

    @XWalkAPI
    public enum InitiateByInternal {
        BY_USER_GESTURE,
        BY_JAVASCRIPT
    }

    @XWalkAPI
    public enum JavascriptMessageTypeInternal {
        JAVASCRIPT_ALERT,
        JAVASCRIPT_CONFIRM,
        JAVASCRIPT_PROMPT,
        JAVASCRIPT_BEFOREUNLOAD
    }

    @XWalkAPI
    public enum LoadStatusInternal {
        FINISHED,
        FAILED,
        CANCELLED
    }

    @XWalkAPI
    public XWalkUIClientInternal(XWalkViewInternal view) {
        this.mContext = view.getContext();
        if (VERSION.SDK_INT >= 19) {
            this.mSystemUiFlag = 1792;
        }
        this.mXWalkView = view;
    }

    @XWalkAPI
    public boolean onCreateWindowRequested(XWalkViewInternal view, InitiateByInternal initiator, ValueCallback<XWalkViewInternal> valueCallback) {
        return false;
    }

    public void onDidChangeThemeColor(XWalkViewInternal view, int color) {
        if (view != null && (this.mContext instanceof Activity)) {
            Activity activity = this.mContext;
            ApiCompatibilityUtils.setStatusBarColor(activity.getWindow(), color);
            ApiCompatibilityUtils.setTaskDescription(activity, null, null, color);
        }
    }

    @XWalkAPI
    public void onIconAvailable(XWalkViewInternal view, String url, Message startDownload) {
    }

    @XWalkAPI
    public void onReceivedIcon(XWalkViewInternal view, String url, Bitmap icon) {
    }

    @XWalkAPI
    public void onRequestFocus(XWalkViewInternal view) {
    }

    @XWalkAPI
    public void onJavascriptCloseWindow(XWalkViewInternal view) {
        if (view != null && (this.mContext instanceof Activity)) {
            this.mContext.finish();
        }
    }

    @XWalkAPI
    public boolean onJavascriptModalDialog(XWalkViewInternal view, JavascriptMessageTypeInternal type, String url, String message, String defaultValue, XWalkJavascriptResultInternal result) {
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

    @XWalkAPI
    public void onFullscreenToggled(XWalkViewInternal view, boolean enterFullscreen) {
        if (this.mContext instanceof Activity) {
            Activity activity = this.mContext;
            if (enterFullscreen) {
                if ((activity.getWindow().getAttributes().flags & 2048) != 0) {
                    this.mOriginalForceNotFullscreen = true;
                    activity.getWindow().clearFlags(2048);
                } else {
                    this.mOriginalForceNotFullscreen = false;
                }
                if (!this.mIsFullscreen) {
                    if (VERSION.SDK_INT >= 19) {
                        View decorView = activity.getWindow().getDecorView();
                        this.mSystemUiFlag = decorView.getSystemUiVisibility();
                        decorView.setSystemUiVisibility(5894);
                    } else if ((activity.getWindow().getAttributes().flags & 1024) != 0) {
                        this.mOriginalFullscreen = true;
                    } else {
                        this.mOriginalFullscreen = false;
                        activity.getWindow().addFlags(1024);
                    }
                    this.mIsFullscreen = true;
                    return;
                }
                return;
            }
            if (this.mOriginalForceNotFullscreen) {
                activity.getWindow().addFlags(2048);
            }
            if (VERSION.SDK_INT >= 19) {
                activity.getWindow().getDecorView().setSystemUiVisibility(this.mSystemUiFlag);
            } else if (!this.mOriginalFullscreen) {
                activity.getWindow().clearFlags(1024);
            }
            this.mIsFullscreen = false;
        }
    }

    @XWalkAPI
    public void openFileChooser(XWalkViewInternal view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        uploadFile.onReceiveValue(null);
    }

    @XWalkAPI
    public void onScaleChanged(XWalkViewInternal view, float oldScale, float newScale) {
    }

    @XWalkAPI
    public boolean shouldOverrideKeyEvent(XWalkViewInternal view, KeyEvent event) {
        return false;
    }

    @XWalkAPI
    public void onUnhandledKeyEvent(XWalkViewInternal view, KeyEvent event) {
    }

    @XWalkAPI
    public boolean onConsoleMessage(XWalkViewInternal view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
        return false;
    }

    @XWalkAPI
    public void onReceivedTitle(XWalkViewInternal view, String title) {
    }

    @XWalkAPI
    public void onPageLoadStarted(XWalkViewInternal view, String url) {
    }

    @XWalkAPI
    public void onPageLoadStopped(XWalkViewInternal view, String url, LoadStatusInternal status) {
    }

    @XWalkAPI
    public boolean onJsAlert(XWalkViewInternal view, String url, String message, XWalkJavascriptResultInternal result) {
        final XWalkJavascriptResultInternal fResult = result;
        Builder dialogBuilder = new Builder(this.mContext);
        dialogBuilder.setTitle(this.mContext.getString(C0332R.string.js_alert_title)).setMessage(message).setCancelable(true).setPositiveButton(this.mContext.getString(17039370), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                fResult.confirm();
                dialog.dismiss();
            }
        }).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                fResult.cancel();
            }
        });
        this.mDialog = dialogBuilder.create();
        this.mDialog.show();
        return false;
    }

    @XWalkAPI
    public boolean onJsConfirm(XWalkViewInternal view, String url, String message, XWalkJavascriptResultInternal result) {
        final XWalkJavascriptResultInternal fResult = result;
        Builder dialogBuilder = new Builder(this.mContext);
        dialogBuilder.setTitle(this.mContext.getString(C0332R.string.js_confirm_title)).setMessage(message).setCancelable(true).setPositiveButton(this.mContext.getString(17039370), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                fResult.confirm();
                dialog.dismiss();
            }
        }).setNegativeButton(this.mContext.getString(17039360), new C03694()).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                fResult.cancel();
            }
        });
        this.mDialog = dialogBuilder.create();
        this.mDialog.show();
        return false;
    }

    @XWalkAPI
    public boolean onJsPrompt(XWalkViewInternal view, String url, String message, String defaultValue, XWalkJavascriptResultInternal result) {
        final XWalkJavascriptResultInternal fResult = result;
        Builder dialogBuilder = new Builder(this.mContext);
        dialogBuilder.setTitle(this.mContext.getString(C0332R.string.js_prompt_title)).setMessage(message).setPositiveButton(this.mContext.getString(17039370), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                fResult.confirmWithResult(XWalkUIClientInternal.this.mPromptText.getText().toString());
                dialog.dismiss();
            }
        }).setNegativeButton(this.mContext.getString(17039360), new C03727()).setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                fResult.cancel();
            }
        });
        this.mPromptText = new EditText(this.mContext);
        this.mPromptText.setVisibility(0);
        this.mPromptText.setText(defaultValue);
        this.mPromptText.selectAll();
        dialogBuilder.setView(this.mPromptText);
        this.mDialog = dialogBuilder.create();
        this.mDialog.show();
        return false;
    }

    void setContentsClient(XWalkContentsClient client) {
        this.mContentsClient = client;
    }

    private Activity addContentView(View view, CustomViewCallbackInternal callback) {
        Activity activity = null;
        try {
            Context context = this.mXWalkView.getContext();
            if (context instanceof Activity) {
                activity = (Activity) context;
            }
        } catch (ClassCastException e) {
        }
        if (this.mCustomXWalkView != null || activity == null) {
            if (callback != null) {
                callback.onCustomViewHidden();
            }
            return null;
        }
        this.mCustomXWalkView = view;
        this.mCustomViewCallback = callback;
        if (this.mContentsClient != null) {
            this.mContentsClient.onToggleFullscreen(true);
        }
        ((FrameLayout) activity.getWindow().getDecorView()).addView(this.mCustomXWalkView, 0, new LayoutParams(-1, -1, 17));
        return activity;
    }

    @XWalkAPI
    public void onShowCustomView(View view, CustomViewCallbackInternal callback) {
        addContentView(view, callback);
    }

    @XWalkAPI
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallbackInternal callback) {
        Activity activity = addContentView(view, callback);
        if (activity != null) {
            int orientation = activity.getResources().getConfiguration().orientation;
            if (requestedOrientation != orientation && requestedOrientation >= -1 && requestedOrientation <= 14) {
                this.mPreOrientation = orientation;
                activity.setRequestedOrientation(requestedOrientation);
            }
        }
    }

    @XWalkAPI
    public void onHideCustomView() {
        if (this.mCustomXWalkView != null && (this.mXWalkView.getContext() instanceof Activity)) {
            if (this.mContentsClient != null) {
                this.mContentsClient.onToggleFullscreen(false);
            }
            Activity activity = (Activity) this.mXWalkView.getContext();
            ((FrameLayout) activity.getWindow().getDecorView()).removeView(this.mCustomXWalkView);
            if (this.mCustomViewCallback != null) {
                this.mCustomViewCallback.onCustomViewHidden();
            }
            if (this.mPreOrientation != -2 && this.mPreOrientation >= -1 && this.mPreOrientation <= 14) {
                activity.setRequestedOrientation(this.mPreOrientation);
                this.mPreOrientation = -2;
            }
            this.mCustomXWalkView = null;
            this.mCustomViewCallback = null;
        }
    }
}
