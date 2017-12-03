package org.chromium.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import org.chromium.base.annotations.CalledByNative;

public class BuildInfo {
    private static final int MAX_FINGERPRINT_LENGTH = 128;
    private static final String TAG = "BuildInfo";

    private BuildInfo() {
    }

    @CalledByNative
    public static String getDevice() {
        return Build.DEVICE;
    }

    @CalledByNative
    public static String getBrand() {
        return Build.BRAND;
    }

    @CalledByNative
    public static String getAndroidBuildId() {
        return Build.ID;
    }

    @CalledByNative
    public static String getAndroidBuildFingerprint() {
        return Build.FINGERPRINT.substring(0, Math.min(Build.FINGERPRINT.length(), 128));
    }

    @CalledByNative
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    @CalledByNative
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    @CalledByNative
    public static String getGMSVersionCode(Context context) {
        String msg = "gms versionCode not available.";
        try {
            msg = Integer.toString(context.getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode);
        } catch (NameNotFoundException e) {
            Log.m21d(TAG, "GMS package is not found: %s", e);
        }
        return msg;
    }

    @CalledByNative
    public static String getPackageVersionCode(Context context) {
        String msg = "versionCode not available.";
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            msg = "";
            if (pi.versionCode > 0) {
                msg = Integer.toString(pi.versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.m20d(TAG, msg);
        }
        return msg;
    }

    @CalledByNative
    public static String getPackageVersionName(Context context) {
        String msg = "versionName not available";
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            msg = "";
            if (pi.versionName != null) {
                return pi.versionName;
            }
            return msg;
        } catch (NameNotFoundException e) {
            Log.m20d(TAG, msg);
            return msg;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @org.chromium.base.annotations.CalledByNative
    public static java.lang.String getPackageLabel(android.content.Context r7) {
        /*
        r3 = android.os.StrictMode.allowThreadDiskReads();
        r4 = r7.getPackageManager();	 Catch:{ NameNotFoundException -> 0x0023 }
        r5 = r7.getPackageName();	 Catch:{ NameNotFoundException -> 0x0023 }
        r6 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r0 = r4.getApplicationInfo(r5, r6);	 Catch:{ NameNotFoundException -> 0x0023 }
        r2 = r4.getApplicationLabel(r0);	 Catch:{ NameNotFoundException -> 0x0023 }
        if (r2 == 0) goto L_0x0020;
    L_0x0018:
        r5 = r2.toString();	 Catch:{ NameNotFoundException -> 0x0023 }
    L_0x001c:
        android.os.StrictMode.setThreadPolicy(r3);
    L_0x001f:
        return r5;
    L_0x0020:
        r5 = "";
        goto L_0x001c;
    L_0x0023:
        r1 = move-exception;
        r5 = "";
        android.os.StrictMode.setThreadPolicy(r3);
        goto L_0x001f;
    L_0x002a:
        r5 = move-exception;
        android.os.StrictMode.setThreadPolicy(r3);
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.base.BuildInfo.getPackageLabel(android.content.Context):java.lang.String");
    }

    @CalledByNative
    public static String getPackageName(Context context) {
        String packageName = context != null ? context.getPackageName() : null;
        return packageName != null ? packageName : "";
    }

    @CalledByNative
    public static String getBuildType() {
        return Build.TYPE;
    }

    @CalledByNative
    public static int getSdkInt() {
        return VERSION.SDK_INT;
    }

    public static boolean isGreaterThanN() {
        return VERSION.SDK_INT > 24 || VERSION.CODENAME.equals("NMR1");
    }
}
