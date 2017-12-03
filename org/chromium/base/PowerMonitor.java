package org.chromium.base;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import org.chromium.base.ApplicationStatus.ApplicationStateListener;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("base::android")
public class PowerMonitor implements ApplicationStateListener {
    private static final long SUSPEND_DELAY_MS = 60000;
    private static PowerMonitor sInstance;
    private static final Runnable sSuspendTask = new C01651();
    private final Handler mHandler;
    private boolean mIsBatteryPower;

    static class C01651 implements Runnable {
        C01651() {
        }

        public void run() {
            PowerMonitor.nativeOnMainActivitySuspended();
        }
    }

    private static class LazyHolder {
        private static final PowerMonitor INSTANCE = new PowerMonitor();

        private LazyHolder() {
        }
    }

    private static native void nativeOnBatteryChargingChanged();

    private static native void nativeOnMainActivityResumed();

    private static native void nativeOnMainActivitySuspended();

    public static void createForTests(Context context) {
        sInstance = LazyHolder.INSTANCE;
    }

    public static void create(Context context) {
        context = context.getApplicationContext();
        if (sInstance == null) {
            sInstance = LazyHolder.INSTANCE;
            ApplicationStatus.registerApplicationStateListener(sInstance);
            Intent batteryStatusIntent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            if (batteryStatusIntent != null) {
                onBatteryChargingChanged(batteryStatusIntent);
            }
        }
    }

    private PowerMonitor() {
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    public static void onBatteryChargingChanged(Intent intent) {
        boolean z = true;
        if (sInstance != null) {
            int chargePlug = intent.getIntExtra("plugged", -1);
            PowerMonitor powerMonitor = sInstance;
            if (chargePlug == 2 || chargePlug == 1) {
                z = false;
            }
            powerMonitor.mIsBatteryPower = z;
            nativeOnBatteryChargingChanged();
        }
    }

    public void onApplicationStateChange(int newState) {
        if (newState == 1) {
            this.mHandler.removeCallbacks(sSuspendTask);
            nativeOnMainActivityResumed();
        } else if (newState == 2) {
            this.mHandler.postDelayed(sSuspendTask, SUSPEND_DELAY_MS);
        }
    }

    @CalledByNative
    private static boolean isBatteryPower() {
        return sInstance.mIsBatteryPower;
    }
}
