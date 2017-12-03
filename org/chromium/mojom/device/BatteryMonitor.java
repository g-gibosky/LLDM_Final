package org.chromium.mojom.device;

import org.chromium.mojo.bindings.Callbacks.Callback1;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;

public interface BatteryMonitor extends Interface {
    public static final Manager<BatteryMonitor, Proxy> MANAGER = BatteryMonitor_Internal.MANAGER;

    public interface QueryNextStatusResponse extends Callback1<BatteryStatus> {
    }

    public interface Proxy extends BatteryMonitor, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void queryNextStatus(QueryNextStatusResponse queryNextStatusResponse);
}
