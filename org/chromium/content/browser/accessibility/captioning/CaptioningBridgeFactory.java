package org.chromium.content.browser.accessibility.captioning;

import android.content.Context;
import android.os.Build.VERSION;

public class CaptioningBridgeFactory {
    public static SystemCaptioningBridge getSystemCaptioningBridge(Context context) {
        if (VERSION.SDK_INT >= 19) {
            return KitKatCaptioningBridge.getInstance(context);
        }
        return new EmptyCaptioningBridge();
    }
}
