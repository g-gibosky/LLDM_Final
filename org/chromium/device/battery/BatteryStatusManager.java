package org.chromium.device.battery;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;
import javax.annotation.Nullable;
import org.chromium.base.VisibleForTesting;
import org.chromium.content.browser.MediaSessionDelegate;
import org.chromium.mojom.device.BatteryStatus;

class BatteryStatusManager {
    static final /* synthetic */ boolean $assertionsDisabled = (!BatteryStatusManager.class.desiredAssertionStatus());
    private static final String TAG = "BatteryStatusManager";
    private AndroidBatteryManagerWrapper mAndroidBatteryManager;
    private final Context mAppContext;
    private final BatteryStatusCallback mCallback;
    private boolean mEnabled;
    private final IntentFilter mFilter;
    private final boolean mIgnoreBatteryPresentState;
    private final BroadcastReceiver mReceiver;

    class C02411 extends BroadcastReceiver {
        C02411() {
        }

        public void onReceive(Context context, Intent intent) {
            BatteryStatusManager.this.onReceive(intent);
        }
    }

    @VisibleForTesting
    static class AndroidBatteryManagerWrapper {
        private final BatteryManager mBatteryManager;

        protected AndroidBatteryManagerWrapper(BatteryManager batteryManager) {
            this.mBatteryManager = batteryManager;
        }

        public int getIntProperty(int id) {
            return this.mBatteryManager.getIntProperty(id);
        }
    }

    interface BatteryStatusCallback {
        void onBatteryStatusChanged(BatteryStatus batteryStatus);
    }

    private BatteryStatusManager(Context context, BatteryStatusCallback callback, boolean ignoreBatteryPresentState, @Nullable AndroidBatteryManagerWrapper batteryManager) {
        this.mFilter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        this.mReceiver = new C02411();
        this.mEnabled = false;
        this.mAppContext = context.getApplicationContext();
        this.mCallback = callback;
        this.mIgnoreBatteryPresentState = ignoreBatteryPresentState;
        this.mAndroidBatteryManager = batteryManager;
    }

    BatteryStatusManager(Context context, BatteryStatusCallback callback) {
        this(context, callback, Build.MODEL.equals("Galaxy Nexus"), VERSION.SDK_INT >= 21 ? new AndroidBatteryManagerWrapper((BatteryManager) context.getSystemService("batterymanager")) : null);
    }

    static BatteryStatusManager createBatteryStatusManagerForTesting(Context context, BatteryStatusCallback callback, @Nullable AndroidBatteryManagerWrapper batteryManager) {
        return new BatteryStatusManager(context, callback, false, batteryManager);
    }

    boolean start() {
        if (!(this.mEnabled || this.mAppContext.registerReceiver(this.mReceiver, this.mFilter) == null)) {
            this.mEnabled = true;
        }
        return this.mEnabled;
    }

    void stop() {
        if (this.mEnabled) {
            this.mAppContext.unregisterReceiver(this.mReceiver);
            this.mEnabled = false;
        }
    }

    @VisibleForTesting
    void onReceive(Intent intent) {
        if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
            boolean present;
            if (this.mIgnoreBatteryPresentState) {
                present = true;
            } else {
                present = intent.getBooleanExtra("present", false);
            }
            int pluggedStatus = intent.getIntExtra("plugged", -1);
            if (!present || pluggedStatus == -1) {
                this.mCallback.onBatteryStatusChanged(new BatteryStatus());
                return;
            }
            double level = ((double) intent.getIntExtra("level", -1)) / ((double) intent.getIntExtra("scale", -1));
            if (level < 0.0d || level > MediaSessionDelegate.DEFAULT_VOLUME_MULTIPLIER) {
                level = MediaSessionDelegate.DEFAULT_VOLUME_MULTIPLIER;
            }
            boolean charging = pluggedStatus != 0;
            double chargingTimeSeconds = (charging && (intent.getIntExtra("status", -1) == 5)) ? 0.0d : Double.POSITIVE_INFINITY;
            BatteryStatus batteryStatus = new BatteryStatus();
            batteryStatus.charging = charging;
            batteryStatus.chargingTime = chargingTimeSeconds;
            batteryStatus.dischargingTime = Double.POSITIVE_INFINITY;
            batteryStatus.level = level;
            if (this.mAndroidBatteryManager != null) {
                updateBatteryStatusForLollipop(batteryStatus);
            }
            this.mCallback.onBatteryStatusChanged(batteryStatus);
            return;
        }
        Log.e(TAG, "Unexpected intent.");
    }

    @TargetApi(21)
    private void updateBatteryStatusForLollipop(BatteryStatus batteryStatus) {
        if ($assertionsDisabled || this.mAndroidBatteryManager != null) {
            double remainingCapacityRatio = ((double) this.mAndroidBatteryManager.getIntProperty(4)) / 100.0d;
            double batteryCapacityMicroAh = (double) this.mAndroidBatteryManager.getIntProperty(1);
            double averageCurrentMicroA = (double) this.mAndroidBatteryManager.getIntProperty(3);
            if (batteryStatus.charging) {
                if (batteryStatus.chargingTime == Double.POSITIVE_INFINITY && averageCurrentMicroA > 0.0d) {
                    batteryStatus.chargingTime = Math.ceil(((MediaSessionDelegate.DEFAULT_VOLUME_MULTIPLIER - remainingCapacityRatio) * (batteryCapacityMicroAh / averageCurrentMicroA)) * 3600.0d);
                    return;
                }
                return;
            } else if (averageCurrentMicroA < 0.0d) {
                batteryStatus.dischargingTime = Math.floor((remainingCapacityRatio * (batteryCapacityMicroAh / (-averageCurrentMicroA))) * 3600.0d);
                return;
            } else {
                return;
            }
        }
        throw new AssertionError();
    }
}
