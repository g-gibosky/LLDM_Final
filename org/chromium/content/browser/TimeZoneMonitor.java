package org.chromium.content.browser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("content")
class TimeZoneMonitor {
    private static final String TAG = "cr.TimeZoneMonitor";
    private final Context mAppContext;
    private final BroadcastReceiver mBroadcastReceiver = new C02051();
    private final IntentFilter mFilter = new IntentFilter("android.intent.action.TIMEZONE_CHANGED");
    private long mNativePtr;

    class C02051 extends BroadcastReceiver {
        C02051() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.TIMEZONE_CHANGED")) {
                TimeZoneMonitor.this.nativeTimeZoneChangedFromJava(TimeZoneMonitor.this.mNativePtr);
            } else {
                Log.m28e(TimeZoneMonitor.TAG, "unexpected intent", new Object[0]);
            }
        }
    }

    private native void nativeTimeZoneChangedFromJava(long j);

    private TimeZoneMonitor(Context context, long nativePtr) {
        this.mAppContext = context.getApplicationContext();
        this.mNativePtr = nativePtr;
        this.mAppContext.registerReceiver(this.mBroadcastReceiver, this.mFilter);
    }

    @CalledByNative
    static TimeZoneMonitor getInstance(Context context, long nativePtr) {
        return new TimeZoneMonitor(context, nativePtr);
    }

    @CalledByNative
    void stop() {
        this.mAppContext.unregisterReceiver(this.mBroadcastReceiver);
        this.mNativePtr = 0;
    }
}
