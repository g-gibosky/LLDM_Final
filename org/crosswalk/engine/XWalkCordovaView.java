package org.crosswalk.engine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine.EngineView;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

public class XWalkCordovaView extends XWalkView implements EngineView {
    public static final String TAG = "XWalkCordovaView";
    private static boolean hasSetStaticPref;
    protected XWalkWebViewEngine parentEngine;
    protected XWalkCordovaResourceClient resourceClient;
    protected XWalkCordovaUiClient uiClient;

    class C04651 extends CordovaPlugin {
        C04651() {
        }

        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            Log.i(XWalkCordovaView.TAG, "Route onActivityResult() to XWalkView");
            XWalkCordovaView.this.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private static Context setGlobalPrefs(Context context, CordovaPreferences preferences) {
        boolean z = false;
        if (!hasSetStaticPref) {
            hasSetStaticPref = true;
            try {
                ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getApplicationContext().getPackageName(), 128);
                boolean prefAnimatable = preferences == null ? false : preferences.getBoolean("CrosswalkAnimatable", false);
                boolean manifestAnimatable = ai.metaData == null ? false : ai.metaData.getBoolean("CrosswalkAnimatable");
                String str = "animatable-xwalk-view";
                if (prefAnimatable || manifestAnimatable) {
                    z = true;
                }
                XWalkPreferences.setValue(str, z);
                if ((ai.flags & 2) != 0) {
                    XWalkPreferences.setValue("remote-debugging", true);
                }
                XWalkPreferences.setValue("javascript-can-open-window", true);
                XWalkPreferences.setValue("allow-universal-access-from-file", true);
            } catch (NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return context;
    }

    public XWalkCordovaView(Context context, CordovaPreferences preferences) {
        super(setGlobalPrefs(context, preferences), (AttributeSet) null);
    }

    public XWalkCordovaView(Context context, AttributeSet attrs) {
        super(setGlobalPrefs(context, null), attrs);
    }

    void init(XWalkWebViewEngine parentEngine) {
        this.parentEngine = parentEngine;
        if (this.resourceClient == null) {
            setResourceClient(new XWalkCordovaResourceClient(parentEngine));
        }
        if (this.uiClient == null) {
            setUIClient(new XWalkCordovaUiClient(parentEngine));
        }
    }

    public void setResourceClient(XWalkResourceClient client) {
        if (client instanceof XWalkCordovaResourceClient) {
            this.resourceClient = (XWalkCordovaResourceClient) client;
        }
        super.setResourceClient(client);
    }

    public void setUIClient(XWalkUIClient client) {
        if (client instanceof XWalkCordovaUiClient) {
            this.uiClient = (XWalkCordovaUiClient) client;
        }
        super.setUIClient(client);
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        this.parentEngine.cordova.startActivityForResult(new C04651(), intent, requestCode);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        Boolean ret = this.parentEngine.client.onDispatchKeyEvent(event);
        if (ret != null) {
            return ret.booleanValue();
        }
        return super.dispatchKeyEvent(event);
    }

    public void pauseTimers() {
    }

    public void pauseTimersForReal() {
        super.pauseTimers();
    }

    public CordovaWebView getCordovaWebView() {
        return this.parentEngine == null ? null : this.parentEngine.getCordovaWebView();
    }

    public void setBackgroundColor(int color) {
        if (this.parentEngine != null && this.parentEngine.isXWalkReady()) {
            super.setBackgroundColor(color);
        }
    }
}
