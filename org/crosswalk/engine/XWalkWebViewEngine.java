package org.crosswalk.engine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.cordova.CordovaBridge;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.CordovaWebViewEngine.Client;
import org.apache.cordova.ICordovaCookieManager;
import org.apache.cordova.NativeToJsMessageQueue;
import org.apache.cordova.NativeToJsMessageQueue.EvalBridgeMode;
import org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode;
import org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode.OnlineEventsBridgeModeDelegate;
import org.apache.cordova.PluginEntry;
import org.apache.cordova.PluginManager;
import org.xwalk.core.XWalkActivityDelegate;
import org.xwalk.core.XWalkGetBitmapCallback;
import org.xwalk.core.XWalkNavigationHistory.Direction;
import org.xwalk.core.XWalkView;

public class XWalkWebViewEngine implements CordovaWebViewEngine {
    private static final int PERMISSION_REQUEST_CODE = 100;
    public static final String TAG = "XWalkWebViewEngine";
    private static final String XWALK_EXTENSIONS_FOLDER = "xwalk-extensions";
    public static final String XWALK_USER_AGENT = "xwalkUserAgent";
    public static final String XWALK_Z_ORDER_ON_TOP = "xwalkZOrderOnTop";
    protected XWalkActivityDelegate activityDelegate;
    protected CordovaBridge bridge;
    protected Client client;
    protected XWalkCordovaCookieManager cookieManager;
    protected CordovaInterface cordova;
    protected NativeToJsMessageQueue nativeToJsMessageQueue;
    protected CordovaWebView parentWebView;
    protected PluginManager pluginManager;
    protected CordovaPreferences preferences;
    protected CordovaResourceApi resourceApi;
    protected String startUrl;
    protected final XWalkCordovaView webView;

    class C03121 implements Runnable {
        C03121() {
        }

        public void run() {
            XWalkWebViewEngine.this.cordova.getActivity().finish();
        }
    }

    class C03142 implements Runnable {

        class C04671 extends CordovaPlugin {

            class C03131 implements Runnable {

                class C04661 extends XWalkGetBitmapCallback {
                    C04661() {
                    }

                    public void onFinishGetBitmap(Bitmap bitmap, int response) {
                        XWalkWebViewEngine.this.pluginManager.postMessage("onGotXWalkBitmap", bitmap);
                    }
                }

                C03131() {
                }

                public void run() {
                    XWalkWebViewEngine.this.webView.captureBitmapAsync(new C04661());
                }
            }

            C04671() {
            }

            public void onNewIntent(Intent intent) {
                Log.i(XWalkWebViewEngine.TAG, "notifPlugin route onNewIntent() to XWalkView: " + intent.toString());
                XWalkWebViewEngine.this.webView.onNewIntent(intent);
            }

            public Object onMessage(String id, Object data) {
                if (id.equals("captureXWalkBitmap")) {
                    XWalkWebViewEngine.this.cordova.getActivity().runOnUiThread(new C03131());
                }
                return null;
            }
        }

        C03142() {
        }

        public void run() {
            XWalkWebViewEngine.this.cookieManager = new XWalkCordovaCookieManager();
            XWalkWebViewEngine.this.initWebViewSettings();
            XWalkWebViewEngine.exposeJsInterface(XWalkWebViewEngine.this.webView, XWalkWebViewEngine.this.bridge);
            XWalkWebViewEngine.this.loadExtensions();
            XWalkWebViewEngine.this.pluginManager.addService(new PluginEntry("XWalkNotif", new C04671()));
            if (XWalkWebViewEngine.this.pluginManager != null) {
                XWalkWebViewEngine.this.pluginManager.postMessage("onXWalkReady", this);
            }
            if (XWalkWebViewEngine.this.startUrl != null) {
                XWalkWebViewEngine.this.webView.load(XWalkWebViewEngine.this.startUrl, null);
            }
        }
    }

    public interface PermissionRequestListener {
        void onRequestPermissionResult(int i, String[] strArr, int[] iArr);
    }

    class C04683 extends CordovaPlugin {
        C04683() {
        }

        public void onResume(boolean multitasking) {
            XWalkWebViewEngine.this.activityDelegate.onResume();
        }
    }

    class C04694 implements OnlineEventsBridgeModeDelegate {
        C04694() {
        }

        public void setNetworkAvailable(boolean value) {
            XWalkWebViewEngine.this.webView.setNetworkAvailable(value);
        }

        public void runOnUiThread(Runnable r) {
            XWalkWebViewEngine.this.cordova.getActivity().runOnUiThread(r);
        }
    }

    public XWalkWebViewEngine(Context context, CordovaPreferences preferences) {
        this.preferences = preferences;
        this.activityDelegate = new XWalkActivityDelegate((Activity) context, new C03121(), new C03142());
        this.webView = new XWalkCordovaView(context, preferences);
    }

    public void init(CordovaWebView parentWebView, CordovaInterface cordova, Client client, CordovaResourceApi resourceApi, PluginManager pluginManager, NativeToJsMessageQueue nativeToJsMessageQueue) {
        if (this.cordova != null) {
            throw new IllegalStateException();
        }
        this.parentWebView = parentWebView;
        this.cordova = cordova;
        this.client = client;
        this.resourceApi = resourceApi;
        this.pluginManager = pluginManager;
        this.nativeToJsMessageQueue = nativeToJsMessageQueue;
        pluginManager.addService(new PluginEntry("XWalkActivityDelegate", new C04683()));
        this.webView.init(this);
        nativeToJsMessageQueue.addBridgeMode(new OnlineEventsBridgeMode(new C04694()));
        nativeToJsMessageQueue.addBridgeMode(new EvalBridgeMode(this, cordova));
        this.bridge = new CordovaBridge(pluginManager, nativeToJsMessageQueue);
    }

    public CordovaWebView getCordovaWebView() {
        return this.parentWebView;
    }

    public View getView() {
        return this.webView;
    }

    private void initWebViewSettings() {
        boolean zOrderOnTop = false;
        this.webView.setVerticalScrollBarEnabled(false);
        if (this.preferences != null) {
            zOrderOnTop = this.preferences.getBoolean(XWALK_Z_ORDER_ON_TOP, false);
        }
        this.webView.setZOrderOnTop(zOrderOnTop);
        String xwalkUserAgent = this.preferences == null ? "" : this.preferences.getString(XWALK_USER_AGENT, "");
        if (!xwalkUserAgent.isEmpty()) {
            this.webView.setUserAgentString(xwalkUserAgent);
        }
        String appendUserAgent = this.preferences.getString("AppendUserAgent", "");
        if (!appendUserAgent.isEmpty()) {
            this.webView.setUserAgentString(this.webView.getUserAgentString() + " " + appendUserAgent);
        }
        if (this.preferences.contains("BackgroundColor")) {
            this.webView.setBackgroundColor(this.preferences.getInteger("BackgroundColor", ViewCompat.MEASURED_STATE_MASK));
        }
    }

    private static void exposeJsInterface(XWalkView webView, CordovaBridge bridge) {
        webView.addJavascriptInterface(new XWalkExposedJsApi(bridge), "_cordovaNative");
    }

    private void loadExtensions() {
        AssetManager assetManager = this.cordova.getActivity().getAssets();
        try {
            Log.i(TAG, "Iterate assets/xwalk-extensions folder");
            for (String path : assetManager.list(XWALK_EXTENSIONS_FOLDER)) {
                Log.i(TAG, "Start to load extension: " + path);
                this.webView.getExtensionManager().loadExtension(XWALK_EXTENSIONS_FOLDER + File.separator + path);
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to iterate assets/xwalk-extensions folder");
        }
    }

    public boolean canGoBack() {
        if (this.activityDelegate.isXWalkReady()) {
            return this.webView.getNavigationHistory().canGoBack();
        }
        return false;
    }

    public boolean goBack() {
        if (!this.webView.getNavigationHistory().canGoBack()) {
            return false;
        }
        this.webView.getNavigationHistory().navigate(Direction.BACKWARD, 1);
        return true;
    }

    public void setPaused(boolean value) {
        if (!this.activityDelegate.isXWalkReady()) {
            return;
        }
        if (value) {
            this.webView.pauseTimersForReal();
        } else {
            this.webView.resumeTimers();
        }
    }

    public void destroy() {
        if (this.activityDelegate.isXWalkReady()) {
            this.webView.onDestroy();
        }
    }

    public void clearHistory() {
        if (this.activityDelegate.isXWalkReady()) {
            this.webView.getNavigationHistory().clear();
        }
    }

    public void stopLoading() {
        if (this.activityDelegate.isXWalkReady()) {
            this.webView.stopLoading();
        }
    }

    public void clearCache() {
        if (this.activityDelegate.isXWalkReady()) {
            this.webView.clearCache(true);
        }
    }

    public String getUrl() {
        if (this.activityDelegate.isXWalkReady()) {
            return this.webView.getUrl();
        }
        return null;
    }

    public ICordovaCookieManager getCookieManager() {
        return this.cookieManager;
    }

    public void loadUrl(String url, boolean clearNavigationStack) {
        if (this.activityDelegate.isXWalkReady()) {
            this.webView.load(url, null);
        } else {
            this.startUrl = url;
        }
    }

    public void evaluateJavascript(String js, ValueCallback<String> callback) {
        this.webView.evaluateJavascript(js, callback);
    }

    public boolean isXWalkReady() {
        return this.activityDelegate.isXWalkReady();
    }

    public boolean requestPermissionsForFileChooser(final PermissionRequestListener listener) {
        ArrayList<String> dangerous_permissions = new ArrayList();
        try {
            for (String permission : this.cordova.getActivity().getPackageManager().getPackageInfo(this.cordova.getActivity().getPackageName(), 4096).requestedPermissions) {
                if (permission.equals("android.permission.WRITE_EXTERNAL_STORAGE") || permission.equals("android.permission.CAMERA")) {
                    dangerous_permissions.add(permission);
                }
            }
        } catch (NameNotFoundException e) {
        }
        if (dangerous_permissions.isEmpty()) {
            return false;
        }
        try {
            this.cordova.requestPermissions(new CordovaPlugin() {
                public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
                    if (requestCode == XWalkWebViewEngine.PERMISSION_REQUEST_CODE) {
                        listener.onRequestPermissionResult(requestCode, permissions, grantResults);
                    }
                }
            }, PERMISSION_REQUEST_CODE, (String[]) dangerous_permissions.toArray(new String[dangerous_permissions.size()]));
            return true;
        } catch (NoSuchMethodError e2) {
            return false;
        }
    }
}
