package org.chromium.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("base::android")
public class ContextUtils {
    private static final String TAG = "ContextUtils";
    private static Context sApplicationContext;

    private static class Holder {
        private static SharedPreferences sSharedPreferences = ContextUtils.fetchAppSharedPreferences();

        private Holder() {
        }
    }

    private static native void nativeInitNativeSideApplicationContext(Context context);

    public static Context getApplicationContext() {
        return sApplicationContext;
    }

    public static void initApplicationContext(Context appContext) {
        if (sApplicationContext == null || sApplicationContext == appContext) {
            initJavaSideApplicationContext(appContext);
        } else {
            Log.m20d(TAG, "Multiple contexts detected, ignoring new application context.");
        }
    }

    public static void initApplicationContextForNative() {
        if (sApplicationContext == null) {
            throw new RuntimeException("Cannot have native global application context be null.");
        }
        nativeInitNativeSideApplicationContext(sApplicationContext);
    }

    private static SharedPreferences fetchAppSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sApplicationContext);
    }

    public static SharedPreferences getAppSharedPreferences() {
        return Holder.sSharedPreferences;
    }

    @VisibleForTesting
    public static void initApplicationContextForTests(Context appContext) {
        initJavaSideApplicationContext(appContext);
        Holder.sSharedPreferences = fetchAppSharedPreferences();
    }

    private static void initJavaSideApplicationContext(Context appContext) {
        if (appContext == null) {
            throw new RuntimeException("Global application context cannot be set to null.");
        }
        sApplicationContext = appContext;
    }
}
