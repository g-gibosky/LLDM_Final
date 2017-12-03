package org.xwalk.core.internal;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import org.xwalk.core.internal.XWalkUIClientInternal.ConsoleMessageType;
import org.xwalk.core.internal.XWalkUIClientInternal.InitiateByInternal;
import org.xwalk.core.internal.XWalkUIClientInternal.JavascriptMessageTypeInternal;
import org.xwalk.core.internal.XWalkUIClientInternal.LoadStatusInternal;

public class XWalkUIClientBridge extends XWalkUIClientInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod enumConsoleMessageTypeClassValueOfMethod = new ReflectMethod();
    private ReflectMethod enumInitiateByClassValueOfMethod = new ReflectMethod();
    private ReflectMethod enumJavascriptMessageTypeClassValueOfMethod = new ReflectMethod();
    private ReflectMethod enumLoadStatusClassValueOfMethod = new ReflectMethod();
    private ReflectMethod f25xde6ca526 = new ReflectMethod(null, "onConsoleMessage", new Class[0]);
    private ReflectMethod f26xb5cc0caa = new ReflectMethod(null, "onCreateWindowRequested", new Class[0]);
    private ReflectMethod onFullscreenToggledXWalkViewInternalbooleanMethod = new ReflectMethod(null, "onFullscreenToggled", new Class[0]);
    private ReflectMethod onHideCustomViewMethod = new ReflectMethod(null, "onHideCustomView", new Class[0]);
    private ReflectMethod onIconAvailableXWalkViewInternalStringMessageMethod = new ReflectMethod(null, "onIconAvailable", new Class[0]);
    private ReflectMethod onJavascriptCloseWindowXWalkViewInternalMethod = new ReflectMethod(null, "onJavascriptCloseWindow", new Class[0]);
    private ReflectMethod f27x125f119f = new ReflectMethod(null, "onJavascriptModalDialog", new Class[0]);
    private ReflectMethod f28xa4379738 = new ReflectMethod(null, "onJsAlert", new Class[0]);
    private ReflectMethod f29x25dcde9c = new ReflectMethod(null, "onJsConfirm", new Class[0]);
    private ReflectMethod f30x5a70d47f = new ReflectMethod(null, "onJsPrompt", new Class[0]);
    private ReflectMethod onPageLoadStartedXWalkViewInternalStringMethod = new ReflectMethod(null, "onPageLoadStarted", new Class[0]);
    private ReflectMethod onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod = new ReflectMethod(null, "onPageLoadStopped", new Class[0]);
    private ReflectMethod onReceivedIconXWalkViewInternalStringBitmapMethod = new ReflectMethod(null, "onReceivedIcon", new Class[0]);
    private ReflectMethod onReceivedTitleXWalkViewInternalStringMethod = new ReflectMethod(null, "onReceivedTitle", new Class[0]);
    private ReflectMethod onRequestFocusXWalkViewInternalMethod = new ReflectMethod(null, "onRequestFocus", new Class[0]);
    private ReflectMethod onScaleChangedXWalkViewInternalfloatfloatMethod = new ReflectMethod(null, "onScaleChanged", new Class[0]);
    private ReflectMethod onShowCustomViewViewCustomViewCallbackInternalMethod = new ReflectMethod(null, "onShowCustomView", new Class[0]);
    private ReflectMethod onShowCustomViewViewintCustomViewCallbackInternalMethod = new ReflectMethod(null, "onShowCustomView", new Class[0]);
    private ReflectMethod onUnhandledKeyEventXWalkViewInternalKeyEventMethod = new ReflectMethod(null, "onUnhandledKeyEvent", new Class[0]);
    private ReflectMethod openFileChooserXWalkViewInternalValueCallbackStringStringMethod = new ReflectMethod(null, "openFileChooser", new Class[0]);
    private ReflectMethod shouldOverrideKeyEventXWalkViewInternalKeyEventMethod = new ReflectMethod(null, "shouldOverrideKeyEvent", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    private Object ConvertJavascriptMessageTypeInternal(JavascriptMessageTypeInternal type) {
        return this.enumJavascriptMessageTypeClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertConsoleMessageType(ConsoleMessageType type) {
        return this.enumConsoleMessageTypeClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertInitiateByInternal(InitiateByInternal type) {
        return this.enumInitiateByClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertLoadStatusInternal(LoadStatusInternal type) {
        return this.enumLoadStatusClassValueOfMethod.invoke(type.toString());
    }

    public XWalkUIClientBridge(XWalkViewBridge view, Object wrapper) {
        super(view);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public boolean onCreateWindowRequested(XWalkViewInternal view, InitiateByInternal initiator, ValueCallback<XWalkViewInternal> callback) {
        if (view instanceof XWalkViewBridge) {
            return onCreateWindowRequested((XWalkViewBridge) view, initiator, (ValueCallback) callback);
        }
        return super.onCreateWindowRequested(view, initiator, callback);
    }

    public boolean onCreateWindowRequested(XWalkViewBridge view, InitiateByInternal initiator, ValueCallback<XWalkViewInternal> callback) {
        if (this.f26xb5cc0caa == null || this.f26xb5cc0caa.isNull()) {
            return onCreateWindowRequestedSuper(view, initiator, callback);
        }
        final ValueCallback<XWalkViewInternal> callbackFinal = callback;
        ReflectMethod reflectMethod = this.f26xb5cc0caa;
        Object[] objArr = new Object[3];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = ConvertInitiateByInternal(initiator);
        objArr[2] = new ValueCallback<Object>() {
            public void onReceiveValue(Object value) {
                callbackFinal.onReceiveValue((XWalkViewBridge) XWalkUIClientBridge.this.coreBridge.getBridgeObject(value));
            }
        };
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public boolean onCreateWindowRequestedSuper(XWalkViewBridge view, InitiateByInternal initiator, ValueCallback<XWalkViewInternal> callback) {
        return super.onCreateWindowRequested(view, initiator, callback);
    }

    public void onIconAvailable(XWalkViewInternal view, String url, Message startDownload) {
        if (view instanceof XWalkViewBridge) {
            onIconAvailable((XWalkViewBridge) view, url, startDownload);
        } else {
            super.onIconAvailable(view, url, startDownload);
        }
    }

    public void onIconAvailable(XWalkViewBridge view, String url, Message startDownload) {
        if (this.onIconAvailableXWalkViewInternalStringMessageMethod == null || this.onIconAvailableXWalkViewInternalStringMessageMethod.isNull()) {
            onIconAvailableSuper(view, url, startDownload);
            return;
        }
        ReflectMethod reflectMethod = this.onIconAvailableXWalkViewInternalStringMessageMethod;
        Object[] objArr = new Object[3];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        objArr[2] = startDownload;
        reflectMethod.invoke(objArr);
    }

    public void onIconAvailableSuper(XWalkViewBridge view, String url, Message startDownload) {
        super.onIconAvailable(view, url, startDownload);
    }

    public void onReceivedIcon(XWalkViewInternal view, String url, Bitmap icon) {
        if (view instanceof XWalkViewBridge) {
            onReceivedIcon((XWalkViewBridge) view, url, icon);
        } else {
            super.onReceivedIcon(view, url, icon);
        }
    }

    public void onReceivedIcon(XWalkViewBridge view, String url, Bitmap icon) {
        if (this.onReceivedIconXWalkViewInternalStringBitmapMethod == null || this.onReceivedIconXWalkViewInternalStringBitmapMethod.isNull()) {
            onReceivedIconSuper(view, url, icon);
            return;
        }
        ReflectMethod reflectMethod = this.onReceivedIconXWalkViewInternalStringBitmapMethod;
        Object[] objArr = new Object[3];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        objArr[2] = icon;
        reflectMethod.invoke(objArr);
    }

    public void onReceivedIconSuper(XWalkViewBridge view, String url, Bitmap icon) {
        super.onReceivedIcon(view, url, icon);
    }

    public void onRequestFocus(XWalkViewInternal view) {
        if (view instanceof XWalkViewBridge) {
            onRequestFocus((XWalkViewBridge) view);
        } else {
            super.onRequestFocus(view);
        }
    }

    public void onRequestFocus(XWalkViewBridge view) {
        if (this.onRequestFocusXWalkViewInternalMethod == null || this.onRequestFocusXWalkViewInternalMethod.isNull()) {
            onRequestFocusSuper(view);
            return;
        }
        ReflectMethod reflectMethod = this.onRequestFocusXWalkViewInternalMethod;
        Object[] objArr = new Object[1];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void onRequestFocusSuper(XWalkViewBridge view) {
        super.onRequestFocus(view);
    }

    public void onJavascriptCloseWindow(XWalkViewInternal view) {
        if (view instanceof XWalkViewBridge) {
            onJavascriptCloseWindow((XWalkViewBridge) view);
        } else {
            super.onJavascriptCloseWindow(view);
        }
    }

    public void onJavascriptCloseWindow(XWalkViewBridge view) {
        if (this.onJavascriptCloseWindowXWalkViewInternalMethod == null || this.onJavascriptCloseWindowXWalkViewInternalMethod.isNull()) {
            onJavascriptCloseWindowSuper(view);
            return;
        }
        ReflectMethod reflectMethod = this.onJavascriptCloseWindowXWalkViewInternalMethod;
        Object[] objArr = new Object[1];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void onJavascriptCloseWindowSuper(XWalkViewBridge view) {
        super.onJavascriptCloseWindow(view);
    }

    public boolean onJavascriptModalDialog(XWalkViewInternal view, JavascriptMessageTypeInternal type, String url, String message, String defaultValue, XWalkJavascriptResultInternal result) {
        if (!(view instanceof XWalkViewBridge)) {
            return super.onJavascriptModalDialog(view, type, url, message, defaultValue, result);
        }
        return onJavascriptModalDialog((XWalkViewBridge) view, type, url, message, defaultValue, result instanceof XWalkJavascriptResultHandlerBridge ? (XWalkJavascriptResultHandlerBridge) result : new XWalkJavascriptResultHandlerBridge((XWalkJavascriptResultHandlerInternal) result));
    }

    public boolean onJavascriptModalDialog(XWalkViewBridge view, JavascriptMessageTypeInternal type, String url, String message, String defaultValue, XWalkJavascriptResultHandlerBridge result) {
        if (this.f27x125f119f == null || this.f27x125f119f.isNull()) {
            return onJavascriptModalDialogSuper(view, type, url, message, defaultValue, result);
        }
        ReflectMethod reflectMethod = this.f27x125f119f;
        Object[] objArr = new Object[6];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = ConvertJavascriptMessageTypeInternal(type);
        objArr[2] = url;
        objArr[3] = message;
        objArr[4] = defaultValue;
        if (!(result instanceof XWalkJavascriptResultHandlerBridge)) {
            result = new XWalkJavascriptResultHandlerBridge(result);
        }
        objArr[5] = result.getWrapper();
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public boolean onJavascriptModalDialogSuper(XWalkViewBridge view, JavascriptMessageTypeInternal type, String url, String message, String defaultValue, XWalkJavascriptResultHandlerBridge result) {
        return super.onJavascriptModalDialog(view, type, url, message, defaultValue, result);
    }

    public void onFullscreenToggled(XWalkViewInternal view, boolean enterFullscreen) {
        if (view instanceof XWalkViewBridge) {
            onFullscreenToggled((XWalkViewBridge) view, enterFullscreen);
        } else {
            super.onFullscreenToggled(view, enterFullscreen);
        }
    }

    public void onFullscreenToggled(XWalkViewBridge view, boolean enterFullscreen) {
        if (this.onFullscreenToggledXWalkViewInternalbooleanMethod == null || this.onFullscreenToggledXWalkViewInternalbooleanMethod.isNull()) {
            onFullscreenToggledSuper(view, enterFullscreen);
            return;
        }
        ReflectMethod reflectMethod = this.onFullscreenToggledXWalkViewInternalbooleanMethod;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = Boolean.valueOf(enterFullscreen);
        reflectMethod.invoke(objArr);
    }

    public void onFullscreenToggledSuper(XWalkViewBridge view, boolean enterFullscreen) {
        super.onFullscreenToggled(view, enterFullscreen);
    }

    public void openFileChooser(XWalkViewInternal view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        if (view instanceof XWalkViewBridge) {
            openFileChooser((XWalkViewBridge) view, (ValueCallback) uploadFile, acceptType, capture);
        } else {
            super.openFileChooser(view, uploadFile, acceptType, capture);
        }
    }

    public void openFileChooser(XWalkViewBridge view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        if (this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod == null || this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod.isNull()) {
            openFileChooserSuper(view, uploadFile, acceptType, capture);
            return;
        }
        ReflectMethod reflectMethod = this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod;
        Object[] objArr = new Object[4];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = uploadFile;
        objArr[2] = acceptType;
        objArr[3] = capture;
        reflectMethod.invoke(objArr);
    }

    public void openFileChooserSuper(XWalkViewBridge view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        super.openFileChooser(view, uploadFile, acceptType, capture);
    }

    public void onScaleChanged(XWalkViewInternal view, float oldScale, float newScale) {
        if (view instanceof XWalkViewBridge) {
            onScaleChanged((XWalkViewBridge) view, oldScale, newScale);
        } else {
            super.onScaleChanged(view, oldScale, newScale);
        }
    }

    public void onScaleChanged(XWalkViewBridge view, float oldScale, float newScale) {
        if (this.onScaleChangedXWalkViewInternalfloatfloatMethod == null || this.onScaleChangedXWalkViewInternalfloatfloatMethod.isNull()) {
            onScaleChangedSuper(view, oldScale, newScale);
            return;
        }
        ReflectMethod reflectMethod = this.onScaleChangedXWalkViewInternalfloatfloatMethod;
        Object[] objArr = new Object[3];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = Float.valueOf(oldScale);
        objArr[2] = Float.valueOf(newScale);
        reflectMethod.invoke(objArr);
    }

    public void onScaleChangedSuper(XWalkViewBridge view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
    }

    public boolean shouldOverrideKeyEvent(XWalkViewInternal view, KeyEvent event) {
        if (view instanceof XWalkViewBridge) {
            return shouldOverrideKeyEvent((XWalkViewBridge) view, event);
        }
        return super.shouldOverrideKeyEvent(view, event);
    }

    public boolean shouldOverrideKeyEvent(XWalkViewBridge view, KeyEvent event) {
        if (this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod == null || this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod.isNull()) {
            return shouldOverrideKeyEventSuper(view, event);
        }
        ReflectMethod reflectMethod = this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = event;
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public boolean shouldOverrideKeyEventSuper(XWalkViewBridge view, KeyEvent event) {
        return super.shouldOverrideKeyEvent(view, event);
    }

    public void onUnhandledKeyEvent(XWalkViewInternal view, KeyEvent event) {
        if (view instanceof XWalkViewBridge) {
            onUnhandledKeyEvent((XWalkViewBridge) view, event);
        } else {
            super.onUnhandledKeyEvent(view, event);
        }
    }

    public void onUnhandledKeyEvent(XWalkViewBridge view, KeyEvent event) {
        if (this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod == null || this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod.isNull()) {
            onUnhandledKeyEventSuper(view, event);
            return;
        }
        ReflectMethod reflectMethod = this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = event;
        reflectMethod.invoke(objArr);
    }

    public void onUnhandledKeyEventSuper(XWalkViewBridge view, KeyEvent event) {
        super.onUnhandledKeyEvent(view, event);
    }

    public boolean onConsoleMessage(XWalkViewInternal view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
        if (view instanceof XWalkViewBridge) {
            return onConsoleMessage((XWalkViewBridge) view, message, lineNumber, sourceId, messageType);
        }
        return super.onConsoleMessage(view, message, lineNumber, sourceId, messageType);
    }

    public boolean onConsoleMessage(XWalkViewBridge view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
        if (this.f25xde6ca526 == null || this.f25xde6ca526.isNull()) {
            return onConsoleMessageSuper(view, message, lineNumber, sourceId, messageType);
        }
        ReflectMethod reflectMethod = this.f25xde6ca526;
        Object[] objArr = new Object[5];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = message;
        objArr[2] = Integer.valueOf(lineNumber);
        objArr[3] = sourceId;
        objArr[4] = ConvertConsoleMessageType(messageType);
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public boolean onConsoleMessageSuper(XWalkViewBridge view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
        return super.onConsoleMessage(view, message, lineNumber, sourceId, messageType);
    }

    public void onReceivedTitle(XWalkViewInternal view, String title) {
        if (view instanceof XWalkViewBridge) {
            onReceivedTitle((XWalkViewBridge) view, title);
        } else {
            super.onReceivedTitle(view, title);
        }
    }

    public void onReceivedTitle(XWalkViewBridge view, String title) {
        if (this.onReceivedTitleXWalkViewInternalStringMethod == null || this.onReceivedTitleXWalkViewInternalStringMethod.isNull()) {
            onReceivedTitleSuper(view, title);
            return;
        }
        ReflectMethod reflectMethod = this.onReceivedTitleXWalkViewInternalStringMethod;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = title;
        reflectMethod.invoke(objArr);
    }

    public void onReceivedTitleSuper(XWalkViewBridge view, String title) {
        super.onReceivedTitle(view, title);
    }

    public void onPageLoadStarted(XWalkViewInternal view, String url) {
        if (view instanceof XWalkViewBridge) {
            onPageLoadStarted((XWalkViewBridge) view, url);
        } else {
            super.onPageLoadStarted(view, url);
        }
    }

    public void onPageLoadStarted(XWalkViewBridge view, String url) {
        if (this.onPageLoadStartedXWalkViewInternalStringMethod == null || this.onPageLoadStartedXWalkViewInternalStringMethod.isNull()) {
            onPageLoadStartedSuper(view, url);
            return;
        }
        ReflectMethod reflectMethod = this.onPageLoadStartedXWalkViewInternalStringMethod;
        Object[] objArr = new Object[2];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        reflectMethod.invoke(objArr);
    }

    public void onPageLoadStartedSuper(XWalkViewBridge view, String url) {
        super.onPageLoadStarted(view, url);
    }

    public void onPageLoadStopped(XWalkViewInternal view, String url, LoadStatusInternal status) {
        if (view instanceof XWalkViewBridge) {
            onPageLoadStopped((XWalkViewBridge) view, url, status);
        } else {
            super.onPageLoadStopped(view, url, status);
        }
    }

    public void onPageLoadStopped(XWalkViewBridge view, String url, LoadStatusInternal status) {
        if (this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod == null || this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod.isNull()) {
            onPageLoadStoppedSuper(view, url, status);
            return;
        }
        ReflectMethod reflectMethod = this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod;
        Object[] objArr = new Object[3];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        objArr[2] = ConvertLoadStatusInternal(status);
        reflectMethod.invoke(objArr);
    }

    public void onPageLoadStoppedSuper(XWalkViewBridge view, String url, LoadStatusInternal status) {
        super.onPageLoadStopped(view, url, status);
    }

    public boolean onJsAlert(XWalkViewInternal view, String url, String message, XWalkJavascriptResultInternal result) {
        if (!(view instanceof XWalkViewBridge)) {
            return super.onJsAlert(view, url, message, result);
        }
        return onJsAlert((XWalkViewBridge) view, url, message, result instanceof XWalkJavascriptResultHandlerBridge ? (XWalkJavascriptResultHandlerBridge) result : new XWalkJavascriptResultHandlerBridge((XWalkJavascriptResultHandlerInternal) result));
    }

    public boolean onJsAlert(XWalkViewBridge view, String url, String message, XWalkJavascriptResultHandlerBridge result) {
        if (this.f28xa4379738 == null || this.f28xa4379738.isNull()) {
            return onJsAlertSuper(view, url, message, result);
        }
        ReflectMethod reflectMethod = this.f28xa4379738;
        Object[] objArr = new Object[4];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        objArr[2] = message;
        if (!(result instanceof XWalkJavascriptResultHandlerBridge)) {
            result = new XWalkJavascriptResultHandlerBridge(result);
        }
        objArr[3] = result.getWrapper();
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public boolean onJsAlertSuper(XWalkViewBridge view, String url, String message, XWalkJavascriptResultHandlerBridge result) {
        return super.onJsAlert(view, url, message, result);
    }

    public boolean onJsConfirm(XWalkViewInternal view, String url, String message, XWalkJavascriptResultInternal result) {
        if (!(view instanceof XWalkViewBridge)) {
            return super.onJsConfirm(view, url, message, result);
        }
        return onJsConfirm((XWalkViewBridge) view, url, message, result instanceof XWalkJavascriptResultHandlerBridge ? (XWalkJavascriptResultHandlerBridge) result : new XWalkJavascriptResultHandlerBridge((XWalkJavascriptResultHandlerInternal) result));
    }

    public boolean onJsConfirm(XWalkViewBridge view, String url, String message, XWalkJavascriptResultHandlerBridge result) {
        if (this.f29x25dcde9c == null || this.f29x25dcde9c.isNull()) {
            return onJsConfirmSuper(view, url, message, result);
        }
        ReflectMethod reflectMethod = this.f29x25dcde9c;
        Object[] objArr = new Object[4];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        objArr[2] = message;
        if (!(result instanceof XWalkJavascriptResultHandlerBridge)) {
            result = new XWalkJavascriptResultHandlerBridge(result);
        }
        objArr[3] = result.getWrapper();
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public boolean onJsConfirmSuper(XWalkViewBridge view, String url, String message, XWalkJavascriptResultHandlerBridge result) {
        return super.onJsConfirm(view, url, message, result);
    }

    public boolean onJsPrompt(XWalkViewInternal view, String url, String message, String defaultValue, XWalkJavascriptResultInternal result) {
        if (!(view instanceof XWalkViewBridge)) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
        return onJsPrompt((XWalkViewBridge) view, url, message, defaultValue, result instanceof XWalkJavascriptResultHandlerBridge ? (XWalkJavascriptResultHandlerBridge) result : new XWalkJavascriptResultHandlerBridge((XWalkJavascriptResultHandlerInternal) result));
    }

    public boolean onJsPrompt(XWalkViewBridge view, String url, String message, String defaultValue, XWalkJavascriptResultHandlerBridge result) {
        if (this.f30x5a70d47f == null || this.f30x5a70d47f.isNull()) {
            return onJsPromptSuper(view, url, message, defaultValue, result);
        }
        ReflectMethod reflectMethod = this.f30x5a70d47f;
        Object[] objArr = new Object[5];
        if (!(view instanceof XWalkViewBridge)) {
            view = null;
        }
        objArr[0] = view.getWrapper();
        objArr[1] = url;
        objArr[2] = message;
        objArr[3] = defaultValue;
        if (!(result instanceof XWalkJavascriptResultHandlerBridge)) {
            result = new XWalkJavascriptResultHandlerBridge(result);
        }
        objArr[4] = result.getWrapper();
        return ((Boolean) reflectMethod.invoke(objArr)).booleanValue();
    }

    public boolean onJsPromptSuper(XWalkViewBridge view, String url, String message, String defaultValue, XWalkJavascriptResultHandlerBridge result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    public void onShowCustomView(View view, CustomViewCallbackInternal callback) {
        onShowCustomView(view, callback instanceof CustomViewCallbackHandlerBridge ? (CustomViewCallbackHandlerBridge) callback : new CustomViewCallbackHandlerBridge((CustomViewCallbackHandlerInternal) callback));
    }

    public void onShowCustomView(View view, CustomViewCallbackHandlerBridge callback) {
        if (this.onShowCustomViewViewCustomViewCallbackInternalMethod == null || this.onShowCustomViewViewCustomViewCallbackInternalMethod.isNull()) {
            onShowCustomViewSuper(view, callback);
            return;
        }
        ReflectMethod reflectMethod = this.onShowCustomViewViewCustomViewCallbackInternalMethod;
        Object[] objArr = new Object[2];
        objArr[0] = view;
        if (!(callback instanceof CustomViewCallbackHandlerBridge)) {
            callback = new CustomViewCallbackHandlerBridge(callback);
        }
        objArr[1] = callback.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void onShowCustomViewSuper(View view, CustomViewCallbackHandlerBridge callback) {
        super.onShowCustomView(view, callback);
    }

    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallbackInternal callback) {
        onShowCustomView(view, requestedOrientation, callback instanceof CustomViewCallbackHandlerBridge ? (CustomViewCallbackHandlerBridge) callback : new CustomViewCallbackHandlerBridge((CustomViewCallbackHandlerInternal) callback));
    }

    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallbackHandlerBridge callback) {
        if (this.onShowCustomViewViewintCustomViewCallbackInternalMethod == null || this.onShowCustomViewViewintCustomViewCallbackInternalMethod.isNull()) {
            onShowCustomViewSuper(view, requestedOrientation, callback);
            return;
        }
        ReflectMethod reflectMethod = this.onShowCustomViewViewintCustomViewCallbackInternalMethod;
        Object[] objArr = new Object[3];
        objArr[0] = view;
        objArr[1] = Integer.valueOf(requestedOrientation);
        if (!(callback instanceof CustomViewCallbackHandlerBridge)) {
            callback = new CustomViewCallbackHandlerBridge(callback);
        }
        objArr[2] = callback.getWrapper();
        reflectMethod.invoke(objArr);
    }

    public void onShowCustomViewSuper(View view, int requestedOrientation, CustomViewCallbackHandlerBridge callback) {
        super.onShowCustomView(view, requestedOrientation, callback);
    }

    public void onHideCustomView() {
        if (this.onHideCustomViewMethod == null || this.onHideCustomViewMethod.isNull()) {
            onHideCustomViewSuper();
        } else {
            this.onHideCustomViewMethod.invoke(new Object[0]);
        }
    }

    public void onHideCustomViewSuper() {
        super.onHideCustomView();
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.enumJavascriptMessageTypeClassValueOfMethod.init(null, this.coreBridge.getWrapperClass("XWalkUIClient$JavascriptMessageType"), "valueOf", String.class);
            this.enumConsoleMessageTypeClassValueOfMethod.init(null, this.coreBridge.getWrapperClass("XWalkUIClient$ConsoleMessageType"), "valueOf", String.class);
            this.enumInitiateByClassValueOfMethod.init(null, this.coreBridge.getWrapperClass("XWalkUIClient$InitiateBy"), "valueOf", String.class);
            this.enumLoadStatusClassValueOfMethod.init(null, this.coreBridge.getWrapperClass("XWalkUIClient$LoadStatus"), "valueOf", String.class);
            this.f26xb5cc0caa.init(this.wrapper, null, "onCreateWindowRequested", this.coreBridge.getWrapperClass("XWalkView"), this.coreBridge.getWrapperClass("XWalkUIClient$InitiateBy"), ValueCallback.class);
            this.onIconAvailableXWalkViewInternalStringMessageMethod.init(this.wrapper, null, "onIconAvailable", this.coreBridge.getWrapperClass("XWalkView"), String.class, Message.class);
            this.onReceivedIconXWalkViewInternalStringBitmapMethod.init(this.wrapper, null, "onReceivedIcon", this.coreBridge.getWrapperClass("XWalkView"), String.class, Bitmap.class);
            this.onRequestFocusXWalkViewInternalMethod.init(this.wrapper, null, "onRequestFocus", this.coreBridge.getWrapperClass("XWalkView"));
            this.onJavascriptCloseWindowXWalkViewInternalMethod.init(this.wrapper, null, "onJavascriptCloseWindow", this.coreBridge.getWrapperClass("XWalkView"));
            this.f27x125f119f.init(this.wrapper, null, "onJavascriptModalDialog", this.coreBridge.getWrapperClass("XWalkView"), this.coreBridge.getWrapperClass("XWalkUIClient$JavascriptMessageType"), String.class, String.class, String.class, this.coreBridge.getWrapperClass("XWalkJavascriptResult"));
            this.onFullscreenToggledXWalkViewInternalbooleanMethod.init(this.wrapper, null, "onFullscreenToggled", this.coreBridge.getWrapperClass("XWalkView"), Boolean.TYPE);
            this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod.init(this.wrapper, null, "openFileChooser", this.coreBridge.getWrapperClass("XWalkView"), ValueCallback.class, String.class, String.class);
            this.onScaleChangedXWalkViewInternalfloatfloatMethod.init(this.wrapper, null, "onScaleChanged", this.coreBridge.getWrapperClass("XWalkView"), Float.TYPE, Float.TYPE);
            this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod.init(this.wrapper, null, "shouldOverrideKeyEvent", this.coreBridge.getWrapperClass("XWalkView"), KeyEvent.class);
            this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod.init(this.wrapper, null, "onUnhandledKeyEvent", this.coreBridge.getWrapperClass("XWalkView"), KeyEvent.class);
            this.f25xde6ca526.init(this.wrapper, null, "onConsoleMessage", this.coreBridge.getWrapperClass("XWalkView"), String.class, Integer.TYPE, String.class, this.coreBridge.getWrapperClass("XWalkUIClient$ConsoleMessageType"));
            this.onReceivedTitleXWalkViewInternalStringMethod.init(this.wrapper, null, "onReceivedTitle", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.onPageLoadStartedXWalkViewInternalStringMethod.init(this.wrapper, null, "onPageLoadStarted", this.coreBridge.getWrapperClass("XWalkView"), String.class);
            this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod.init(this.wrapper, null, "onPageLoadStopped", this.coreBridge.getWrapperClass("XWalkView"), String.class, this.coreBridge.getWrapperClass("XWalkUIClient$LoadStatus"));
            this.f28xa4379738.init(this.wrapper, null, "onJsAlert", this.coreBridge.getWrapperClass("XWalkView"), String.class, String.class, this.coreBridge.getWrapperClass("XWalkJavascriptResult"));
            this.f29x25dcde9c.init(this.wrapper, null, "onJsConfirm", this.coreBridge.getWrapperClass("XWalkView"), String.class, String.class, this.coreBridge.getWrapperClass("XWalkJavascriptResult"));
            this.f30x5a70d47f.init(this.wrapper, null, "onJsPrompt", this.coreBridge.getWrapperClass("XWalkView"), String.class, String.class, String.class, this.coreBridge.getWrapperClass("XWalkJavascriptResult"));
            this.onShowCustomViewViewCustomViewCallbackInternalMethod.init(this.wrapper, null, "onShowCustomView", View.class, this.coreBridge.getWrapperClass("CustomViewCallback"));
            this.onShowCustomViewViewintCustomViewCallbackInternalMethod.init(this.wrapper, null, "onShowCustomView", View.class, Integer.TYPE, this.coreBridge.getWrapperClass("CustomViewCallback"));
            this.onHideCustomViewMethod.init(this.wrapper, null, "onHideCustomView", new Class[0]);
        }
    }
}
