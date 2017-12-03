package org.chromium.device.battery;

import android.content.Context;
import android.util.Log;
import java.util.HashSet;
import java.util.Iterator;
import org.chromium.base.ThreadUtils;
import org.chromium.mojom.device.BatteryMonitor;
import org.chromium.mojom.device.BatteryStatus;

public class BatteryMonitorFactory {
    static final /* synthetic */ boolean $assertionsDisabled = (!BatteryMonitorFactory.class.desiredAssertionStatus());
    static final String TAG = "BatteryMonitorFactory";
    private final BatteryStatusCallback mCallback = new C04521();
    private final BatteryStatusManager mManager;
    private final HashSet<BatteryMonitorImpl> mSubscribedMonitors = new HashSet();

    class C04521 implements BatteryStatusCallback {
        C04521() {
        }

        public void onBatteryStatusChanged(BatteryStatus batteryStatus) {
            ThreadUtils.assertOnUiThread();
            Iterator i$ = BatteryMonitorFactory.this.mSubscribedMonitors.iterator();
            while (i$.hasNext()) {
                ((BatteryMonitorImpl) i$.next()).didChange(batteryStatus);
            }
        }
    }

    public BatteryMonitorFactory(Context applicationContext) {
        if ($assertionsDisabled || applicationContext != null) {
            this.mManager = new BatteryStatusManager(applicationContext, this.mCallback);
            return;
        }
        throw new AssertionError();
    }

    public BatteryMonitor createMonitor() {
        ThreadUtils.assertOnUiThread();
        if (this.mSubscribedMonitors.isEmpty() && !this.mManager.start()) {
            Log.e(TAG, "BatteryStatusManager failed to start.");
        }
        BatteryMonitorImpl monitor = new BatteryMonitorImpl(this);
        this.mSubscribedMonitors.add(monitor);
        return monitor;
    }

    void unsubscribe(BatteryMonitorImpl monitor) {
        ThreadUtils.assertOnUiThread();
        if ($assertionsDisabled || this.mSubscribedMonitors.contains(monitor)) {
            this.mSubscribedMonitors.remove(monitor);
            if (this.mSubscribedMonitors.isEmpty()) {
                this.mManager.stop();
                return;
            }
            return;
        }
        throw new AssertionError();
    }
}
