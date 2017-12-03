package org.xwalk.core.internal.extension.api.launchscreen;

import android.content.Context;
import android.content.Intent;
import org.xwalk.core.internal.XWalkExtensionInternal;
import org.xwalk.core.internal.XWalkLaunchScreenManager;

public class LaunchScreenExtension extends XWalkExtensionInternal {
    private static final String CMD_HIDE_LAUNCH_SCREEN = "hideLaunchScreen";
    public static final String JS_API_PATH = "jsapi/launch_screen_api.js";
    private static final String[] JS_ENTRY_POINTS = new String[]{"window.screen.show"};
    private static final String NAME = "xwalk.launchscreen";
    private Context mContext;

    public LaunchScreenExtension(String jsApi, Context context) {
        super(NAME, jsApi, JS_ENTRY_POINTS);
        this.mContext = context;
    }

    public void onMessage(int instanceId, String message) {
        if (message.equals(CMD_HIDE_LAUNCH_SCREEN)) {
            hideLaunchScreen();
        }
    }

    private void hideLaunchScreen() {
        this.mContext.sendBroadcast(new Intent(XWalkLaunchScreenManager.getHideLaunchScreenFilterStr()));
    }

    public String onSyncMessage(int instanceID, String message) {
        return null;
    }
}
