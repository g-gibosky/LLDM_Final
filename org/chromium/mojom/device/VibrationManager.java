package org.chromium.mojom.device;

import org.chromium.mojo.bindings.Callbacks.Callback0;
import org.chromium.mojo.bindings.Interface;
import org.chromium.mojo.bindings.Interface.Manager;

public interface VibrationManager extends Interface {
    public static final Manager<VibrationManager, Proxy> MANAGER = VibrationManager_Internal.MANAGER;

    public interface CancelResponse extends Callback0 {
    }

    public interface VibrateResponse extends Callback0 {
    }

    public interface Proxy extends VibrationManager, org.chromium.mojo.bindings.Interface.Proxy {
    }

    void cancel(CancelResponse cancelResponse);

    void vibrate(long j, VibrateResponse vibrateResponse);
}
