package org.crosswalk.engine;

import android.os.Looper;
import org.apache.cordova.CordovaBridge;
import org.apache.cordova.ExposedJsApi;
import org.json.JSONException;
import org.xwalk.core.JavascriptInterface;

class XWalkExposedJsApi implements ExposedJsApi {
    private final CordovaBridge bridge;

    XWalkExposedJsApi(CordovaBridge bridge) {
        this.bridge = bridge;
    }

    @JavascriptInterface
    public String exec(int bridgeSecret, String service, String action, String callbackId, String arguments) throws JSONException, IllegalAccessException {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        return this.bridge.jsExec(bridgeSecret, service, action, callbackId, arguments);
    }

    @JavascriptInterface
    public void setNativeToJsBridgeMode(int bridgeSecret, int value) throws IllegalAccessException {
        this.bridge.jsSetNativeToJsBridgeMode(bridgeSecret, value);
    }

    @JavascriptInterface
    public String retrieveJsMessages(int bridgeSecret, boolean fromOnlineEvent) throws IllegalAccessException {
        return this.bridge.jsRetrieveJsMessages(bridgeSecret, fromOnlineEvent);
    }
}
