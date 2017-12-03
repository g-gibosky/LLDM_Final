package org.xwalk.core;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import java.util.ArrayList;

public class XWalkUIClient {
    static final /* synthetic */ boolean $assertionsDisabled = (!XWalkUIClient.class.desiredAssertionStatus());
    private Object bridge;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes = new ArrayList();
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod enumConsoleMessageTypeClassValueOfMethod = new ReflectMethod();
    private ReflectMethod enumInitiateByClassValueOfMethod = new ReflectMethod();
    private ReflectMethod enumJavascriptMessageTypeClassValueOfMethod = new ReflectMethod();
    private ReflectMethod enumLoadStatusClassValueOfMethod = new ReflectMethod();
    private ReflectMethod f7xde6ca526 = new ReflectMethod(null, "onConsoleMessage", new Class[0]);
    private ReflectMethod f8xb5cc0caa = new ReflectMethod(null, "onCreateWindowRequested", new Class[0]);
    private ReflectMethod onFullscreenToggledXWalkViewInternalbooleanMethod = new ReflectMethod(null, "onFullscreenToggled", new Class[0]);
    private ReflectMethod onHideCustomViewMethod = new ReflectMethod(null, "onHideCustomView", new Class[0]);
    private ReflectMethod onIconAvailableXWalkViewInternalStringMessageMethod = new ReflectMethod(null, "onIconAvailable", new Class[0]);
    private ReflectMethod onJavascriptCloseWindowXWalkViewInternalMethod = new ReflectMethod(null, "onJavascriptCloseWindow", new Class[0]);
    private ReflectMethod f9x125f119f = new ReflectMethod(null, "onJavascriptModalDialog", new Class[0]);
    private ReflectMethod f10xa4379738 = new ReflectMethod(null, "onJsAlert", new Class[0]);
    private ReflectMethod f11x25dcde9c = new ReflectMethod(null, "onJsConfirm", new Class[0]);
    private ReflectMethod f12x5a70d47f = new ReflectMethod(null, "onJsPrompt", new Class[0]);
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
    private ReflectMethod postWrapperMethod;
    private ReflectMethod shouldOverrideKeyEventXWalkViewInternalKeyEventMethod = new ReflectMethod(null, "shouldOverrideKeyEvent", new Class[0]);

    public enum ConsoleMessageType {
        DEBUG,
        ERROR,
        LOG,
        INFO,
        WARNING
    }

    public enum InitiateBy {
        BY_USER_GESTURE,
        BY_JAVASCRIPT
    }

    public enum JavascriptMessageType {
        JAVASCRIPT_ALERT,
        JAVASCRIPT_CONFIRM,
        JAVASCRIPT_PROMPT,
        JAVASCRIPT_BEFOREUNLOAD
    }

    public enum LoadStatus {
        FINISHED,
        FAILED,
        CANCELLED
    }

    private Object ConvertJavascriptMessageType(JavascriptMessageType type) {
        return this.enumJavascriptMessageTypeClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertConsoleMessageType(ConsoleMessageType type) {
        return this.enumConsoleMessageTypeClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertInitiateBy(InitiateBy type) {
        return this.enumInitiateByClassValueOfMethod.invoke(type.toString());
    }

    private Object ConvertLoadStatus(LoadStatus type) {
        return this.enumLoadStatusClassValueOfMethod.invoke(type.toString());
    }

    protected Object getBridge() {
        return this.bridge;
    }

    public XWalkUIClient(XWalkView view) {
        this.constructorTypes.add("XWalkViewBridge");
        this.constructorParams = new ArrayList();
        this.constructorParams.add(view);
        reflectionInit();
    }

    public boolean onCreateWindowRequested(XWalkView view, InitiateBy initiator, ValueCallback<XWalkView> callback) {
        try {
            return ((Boolean) this.f8xb5cc0caa.invoke(view.getBridge(), ConvertInitiateBy(initiator), callback)).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void onIconAvailable(XWalkView view, String url, Message startDownload) {
        try {
            this.onIconAvailableXWalkViewInternalStringMessageMethod.invoke(view.getBridge(), url, startDownload);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void onReceivedIcon(XWalkView view, String url, Bitmap icon) {
        try {
            this.onReceivedIconXWalkViewInternalStringBitmapMethod.invoke(view.getBridge(), url, icon);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void onRequestFocus(XWalkView view) {
        try {
            this.onRequestFocusXWalkViewInternalMethod.invoke(view.getBridge());
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void onJavascriptCloseWindow(XWalkView view) {
        try {
            this.onJavascriptCloseWindowXWalkViewInternalMethod.invoke(view.getBridge());
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean onJavascriptModalDialog(XWalkView view, JavascriptMessageType type, String url, String message, String defaultValue, XWalkJavascriptResult result) {
        try {
            return ((Boolean) this.f9x125f119f.invoke(view.getBridge(), ConvertJavascriptMessageType(type), url, message, defaultValue, ((XWalkJavascriptResultHandler) result).getBridge())).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void onFullscreenToggled(XWalkView view, boolean enterFullscreen) {
        try {
            this.onFullscreenToggledXWalkViewInternalbooleanMethod.invoke(view.getBridge(), Boolean.valueOf(enterFullscreen));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void openFileChooser(XWalkView view, ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        try {
            this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod.invoke(view.getBridge(), uploadFile, acceptType, capture);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void onScaleChanged(XWalkView view, float oldScale, float newScale) {
        try {
            this.onScaleChangedXWalkViewInternalfloatfloatMethod.invoke(view.getBridge(), Float.valueOf(oldScale), Float.valueOf(newScale));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean shouldOverrideKeyEvent(XWalkView view, KeyEvent event) {
        try {
            return ((Boolean) this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod.invoke(view.getBridge(), event)).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void onUnhandledKeyEvent(XWalkView view, KeyEvent event) {
        try {
            this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod.invoke(view.getBridge(), event);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean onConsoleMessage(XWalkView view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
        try {
            return ((Boolean) this.f7xde6ca526.invoke(view.getBridge(), message, Integer.valueOf(lineNumber), sourceId, ConvertConsoleMessageType(messageType))).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void onReceivedTitle(XWalkView view, String title) {
        try {
            this.onReceivedTitleXWalkViewInternalStringMethod.invoke(view.getBridge(), title);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void onPageLoadStarted(XWalkView view, String url) {
        try {
            this.onPageLoadStartedXWalkViewInternalStringMethod.invoke(view.getBridge(), url);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
        try {
            this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod.invoke(view.getBridge(), url, ConvertLoadStatus(status));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean onJsAlert(XWalkView view, String url, String message, XWalkJavascriptResult result) {
        try {
            return ((Boolean) this.f10xa4379738.invoke(view.getBridge(), url, message, ((XWalkJavascriptResultHandler) result).getBridge())).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public boolean onJsConfirm(XWalkView view, String url, String message, XWalkJavascriptResult result) {
        try {
            return ((Boolean) this.f11x25dcde9c.invoke(view.getBridge(), url, message, ((XWalkJavascriptResultHandler) result).getBridge())).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public boolean onJsPrompt(XWalkView view, String url, String message, String defaultValue, XWalkJavascriptResult result) {
        try {
            return ((Boolean) this.f12x5a70d47f.invoke(view.getBridge(), url, message, defaultValue, ((XWalkJavascriptResultHandler) result).getBridge())).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void onShowCustomView(View view, CustomViewCallback callback) {
        try {
            this.onShowCustomViewViewCustomViewCallbackInternalMethod.invoke(view, ((CustomViewCallbackHandler) callback).getBridge());
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        try {
            this.onShowCustomViewViewintCustomViewCallbackInternalMethod.invoke(view, Integer.valueOf(requestedOrientation), ((CustomViewCallbackHandler) callback).getBridge());
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void onHideCustomView() {
        try {
            this.onHideCustomViewMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
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
            this.bridge = new ReflectConstructor(this.coreWrapper.getBridgeClass("XWalkUIClientBridge"), paramTypes).newInstance(this.constructorParams.toArray());
            if (this.postWrapperMethod != null) {
                this.postWrapperMethod.invoke(new Object[0]);
            }
            this.enumJavascriptMessageTypeClassValueOfMethod.init(null, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$JavascriptMessageTypeInternal"), "valueOf", String.class);
            this.enumConsoleMessageTypeClassValueOfMethod.init(null, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$ConsoleMessageType"), "valueOf", String.class);
            this.enumInitiateByClassValueOfMethod.init(null, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$InitiateByInternal"), "valueOf", String.class);
            this.enumLoadStatusClassValueOfMethod.init(null, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$LoadStatusInternal"), "valueOf", String.class);
            this.f8xb5cc0caa.init(this.bridge, null, "onCreateWindowRequestedSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), this.coreWrapper.getBridgeClass("XWalkUIClientInternal$InitiateByInternal"), ValueCallback.class);
            this.onIconAvailableXWalkViewInternalStringMessageMethod.init(this.bridge, null, "onIconAvailableSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, Message.class);
            this.onReceivedIconXWalkViewInternalStringBitmapMethod.init(this.bridge, null, "onReceivedIconSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, Bitmap.class);
            this.onRequestFocusXWalkViewInternalMethod.init(this.bridge, null, "onRequestFocusSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"));
            this.onJavascriptCloseWindowXWalkViewInternalMethod.init(this.bridge, null, "onJavascriptCloseWindowSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"));
            this.f9x125f119f.init(this.bridge, null, "onJavascriptModalDialogSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), this.coreWrapper.getBridgeClass("XWalkUIClientInternal$JavascriptMessageTypeInternal"), String.class, String.class, String.class, this.coreWrapper.getBridgeClass("XWalkJavascriptResultHandlerBridge"));
            this.onFullscreenToggledXWalkViewInternalbooleanMethod.init(this.bridge, null, "onFullscreenToggledSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), Boolean.TYPE);
            this.openFileChooserXWalkViewInternalValueCallbackStringStringMethod.init(this.bridge, null, "openFileChooserSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), ValueCallback.class, String.class, String.class);
            this.onScaleChangedXWalkViewInternalfloatfloatMethod.init(this.bridge, null, "onScaleChangedSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), Float.TYPE, Float.TYPE);
            this.shouldOverrideKeyEventXWalkViewInternalKeyEventMethod.init(this.bridge, null, "shouldOverrideKeyEventSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), KeyEvent.class);
            this.onUnhandledKeyEventXWalkViewInternalKeyEventMethod.init(this.bridge, null, "onUnhandledKeyEventSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), KeyEvent.class);
            this.f7xde6ca526.init(this.bridge, null, "onConsoleMessageSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, Integer.TYPE, String.class, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$ConsoleMessageType"));
            this.onReceivedTitleXWalkViewInternalStringMethod.init(this.bridge, null, "onReceivedTitleSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class);
            this.onPageLoadStartedXWalkViewInternalStringMethod.init(this.bridge, null, "onPageLoadStartedSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class);
            this.onPageLoadStoppedXWalkViewInternalStringLoadStatusInternalMethod.init(this.bridge, null, "onPageLoadStoppedSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, this.coreWrapper.getBridgeClass("XWalkUIClientInternal$LoadStatusInternal"));
            this.f10xa4379738.init(this.bridge, null, "onJsAlertSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, String.class, this.coreWrapper.getBridgeClass("XWalkJavascriptResultHandlerBridge"));
            this.f11x25dcde9c.init(this.bridge, null, "onJsConfirmSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, String.class, this.coreWrapper.getBridgeClass("XWalkJavascriptResultHandlerBridge"));
            this.f12x5a70d47f.init(this.bridge, null, "onJsPromptSuper", this.coreWrapper.getBridgeClass("XWalkViewBridge"), String.class, String.class, String.class, this.coreWrapper.getBridgeClass("XWalkJavascriptResultHandlerBridge"));
            this.onShowCustomViewViewCustomViewCallbackInternalMethod.init(this.bridge, null, "onShowCustomViewSuper", View.class, this.coreWrapper.getBridgeClass("CustomViewCallbackHandlerBridge"));
            this.onShowCustomViewViewintCustomViewCallbackInternalMethod.init(this.bridge, null, "onShowCustomViewSuper", View.class, Integer.TYPE, this.coreWrapper.getBridgeClass("CustomViewCallbackHandlerBridge"));
            this.onHideCustomViewMethod.init(this.bridge, null, "onHideCustomViewSuper", new Class[0]);
        } catch (UnsupportedOperationException e) {
        }
    }
}
