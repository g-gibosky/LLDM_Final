package org.chromium.device.battery;

import android.util.Log;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojom.device.BatteryMonitor;
import org.chromium.mojom.device.BatteryMonitor.QueryNextStatusResponse;
import org.chromium.mojom.device.BatteryStatus;

public class BatteryMonitorImpl implements BatteryMonitor {
    private static final String TAG = "BatteryMonitorImpl";
    private QueryNextStatusResponse mCallback;
    private final BatteryMonitorFactory mFactory;
    private boolean mHasStatusToReport = false;
    private BatteryStatus mStatus;
    private boolean mSubscribed = true;

    public BatteryMonitorImpl(BatteryMonitorFactory batteryMonitorFactory) {
        this.mFactory = batteryMonitorFactory;
    }

    private void unsubscribe() {
        if (this.mSubscribed) {
            this.mFactory.unsubscribe(this);
            this.mSubscribed = false;
        }
    }

    public void close() {
        unsubscribe();
    }

    public void onConnectionError(MojoException e) {
        unsubscribe();
    }

    public void queryNextStatus(QueryNextStatusResponse callback) {
        if (this.mCallback != null) {
            Log.e(TAG, "Overlapped call to queryNextStatus!");
            unsubscribe();
            return;
        }
        this.mCallback = callback;
        if (this.mHasStatusToReport) {
            reportStatus();
        }
    }

    void didChange(BatteryStatus batteryStatus) {
        this.mStatus = batteryStatus;
        this.mHasStatusToReport = true;
        if (this.mCallback != null) {
            reportStatus();
        }
    }

    void reportStatus() {
        this.mCallback.call(this.mStatus);
        this.mCallback = null;
        this.mHasStatusToReport = false;
    }
}
