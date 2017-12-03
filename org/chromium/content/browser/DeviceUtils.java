package org.chromium.content.browser;

import android.content.Context;
import org.chromium.base.CommandLine;
import org.chromium.content.common.ContentSwitches;
import org.chromium.ui.base.DeviceFormFactor;

public class DeviceUtils {
    public static void addDeviceSpecificUserAgentSwitch(Context context) {
        if (!DeviceFormFactor.isTablet(context)) {
            CommandLine.getInstance().appendSwitch(ContentSwitches.USE_MOBILE_UA);
        }
    }
}
