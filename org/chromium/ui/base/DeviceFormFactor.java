package org.chromium.ui.base;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import org.chromium.base.annotations.CalledByNative;

public class DeviceFormFactor {
    static final /* synthetic */ boolean $assertionsDisabled = (!DeviceFormFactor.class.desiredAssertionStatus());
    private static final int MINIMUM_LARGE_TABLET_WIDTH_DP = 720;
    public static final int MINIMUM_TABLET_WIDTH_DP = 600;
    private static Float sDensity = null;
    private static Boolean sIsLargeTablet = null;
    private static Boolean sIsTablet = null;
    private static Integer sMinimumTabletWidthPx = null;

    @CalledByNative
    public static boolean isTablet(Context context) {
        if (sIsTablet == null) {
            sIsTablet = Boolean.valueOf(getSmallestDeviceWidthDp(context) >= MINIMUM_TABLET_WIDTH_DP);
        }
        return sIsTablet.booleanValue();
    }

    public static boolean isLargeTablet(Context context) {
        if (sIsLargeTablet == null) {
            sIsLargeTablet = Boolean.valueOf(getSmallestDeviceWidthDp(context) >= MINIMUM_LARGE_TABLET_WIDTH_DP);
        }
        return sIsLargeTablet.booleanValue();
    }

    public static int getSmallestDeviceWidthDp(Context context) {
        if (!$assertionsDisabled && context.getApplicationContext() == null) {
            throw new AssertionError();
        } else if (VERSION.SDK_INT < 17) {
            return context.getResources().getConfiguration().smallestScreenWidthDp;
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            ((WindowManager) context.getApplicationContext().getSystemService("window")).getDefaultDisplay().getRealMetrics(metrics);
            return Math.round(Math.min(((float) metrics.heightPixels) / metrics.density, ((float) metrics.widthPixels) / metrics.density));
        }
    }

    public static int getMinimumTabletWidthPx(Context context) {
        if (sMinimumTabletWidthPx == null) {
            sMinimumTabletWidthPx = Integer.valueOf(Math.round(600.0f * context.getResources().getDisplayMetrics().density));
        }
        return sMinimumTabletWidthPx.intValue();
    }

    public static void resetValuesIfNeeded(Context context) {
        float currentDensity = context.getResources().getDisplayMetrics().density;
        if (!(sDensity == null || sDensity.floatValue() == currentDensity)) {
            sIsTablet = null;
            sIsLargeTablet = null;
            sMinimumTabletWidthPx = null;
        }
        sDensity = Float.valueOf(currentDensity);
    }
}
