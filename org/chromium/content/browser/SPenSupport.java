package org.chromium.content.browser;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.os.Build;

public final class SPenSupport {
    private static final int SPEN_ACTION_CANCEL = 214;
    private static final int SPEN_ACTION_DOWN = 211;
    private static final int SPEN_ACTION_MOVE = 213;
    private static final int SPEN_ACTION_UP = 212;
    private static Boolean sIsSPenSupported;

    public static boolean isSPenSupported(Context context) {
        if (sIsSPenSupported == null) {
            sIsSPenSupported = Boolean.valueOf(detectSPenSupport(context));
        }
        return sIsSPenSupported.booleanValue();
    }

    private static boolean detectSPenSupport(Context context) {
        if (!"SAMSUNG".equalsIgnoreCase(Build.MANUFACTURER)) {
            return false;
        }
        for (FeatureInfo info : context.getPackageManager().getSystemAvailableFeatures()) {
            if ("com.sec.feature.spen_usp".equalsIgnoreCase(info.name)) {
                return true;
            }
        }
        return false;
    }

    public static int convertSPenEventAction(int eventActionMasked) {
        switch (eventActionMasked) {
            case SPEN_ACTION_DOWN /*211*/:
                return 0;
            case SPEN_ACTION_UP /*212*/:
                return 1;
            case SPEN_ACTION_MOVE /*213*/:
                return 2;
            case SPEN_ACTION_CANCEL /*214*/:
                return 3;
            default:
                return eventActionMasked;
        }
    }
}
