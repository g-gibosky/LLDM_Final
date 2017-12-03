package org.chromium.components.location;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Process;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.SuppressFBWarnings;
import org.chromium.ui.base.PageTransition;

public class LocationUtils {
    private static Factory sFactory;
    private static LocationUtils sInstance;

    public interface Factory {
        LocationUtils create();
    }

    protected LocationUtils() {
    }

    @SuppressFBWarnings({"LI_LAZY_INIT_STATIC"})
    public static LocationUtils getInstance() {
        ThreadUtils.assertOnUiThread();
        if (sInstance == null) {
            if (sFactory == null) {
                sInstance = new LocationUtils();
            } else {
                sInstance = sFactory.create();
            }
        }
        return sInstance;
    }

    private boolean hasPermission(Context context, String name) {
        return ApiCompatibilityUtils.checkPermission(context, name, Process.myPid(), Process.myUid()) == 0;
    }

    public boolean hasAndroidLocationPermission(Context context) {
        return hasPermission(context, "android.permission.ACCESS_COARSE_LOCATION") || hasPermission(context, "android.permission.ACCESS_FINE_LOCATION");
    }

    public boolean isSystemLocationSettingEnabled(Context context) {
        if (VERSION.SDK_INT >= 19) {
            if (Secure.getInt(context.getContentResolver(), "location_mode", 0) != 0) {
                return true;
            }
            return false;
        } else if (TextUtils.isEmpty(Secure.getString(context.getContentResolver(), "location_providers_allowed"))) {
            return false;
        } else {
            return true;
        }
    }

    public Intent getSystemLocationSettingsIntent() {
        Intent i = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
        i.setFlags(PageTransition.CHAIN_START);
        return i;
    }

    @VisibleForTesting
    public static void setFactory(Factory factory) {
        sFactory = factory;
        sInstance = null;
    }
}
