package org.chromium.content.browser;

import android.content.Context;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content.browser.ServiceRegistry.ImplementationFactory;
import org.chromium.device.battery.BatteryMonitorFactory;
import org.chromium.device.vibration.VibrationManagerImpl;
import org.chromium.mojom.device.BatteryMonitor;
import org.chromium.mojom.device.VibrationManager;

@JNINamespace("content")
class ServiceRegistrar {
    static final /* synthetic */ boolean $assertionsDisabled = (!ServiceRegistrar.class.desiredAssertionStatus());

    private static class BatteryMonitorImplementationFactory implements ImplementationFactory<BatteryMonitor> {
        private final BatteryMonitorFactory mFactory;

        BatteryMonitorImplementationFactory(Context applicationContext) {
            this.mFactory = new BatteryMonitorFactory(applicationContext);
        }

        public BatteryMonitor createImpl() {
            return this.mFactory.createMonitor();
        }
    }

    private static class VibrationManagerImplementationFactory implements ImplementationFactory<VibrationManager> {
        private final Context mApplicationContext;

        VibrationManagerImplementationFactory(Context applicationContext) {
            this.mApplicationContext = applicationContext;
        }

        public VibrationManager createImpl() {
            return new VibrationManagerImpl(this.mApplicationContext);
        }
    }

    ServiceRegistrar() {
    }

    @CalledByNative
    static void registerProcessHostServices(ServiceRegistry registry, Context applicationContext) {
        if ($assertionsDisabled || applicationContext != null) {
            registry.addService(BatteryMonitor.MANAGER, new BatteryMonitorImplementationFactory(applicationContext));
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    static void registerFrameHostServices(ServiceRegistry registry, Context applicationContext) {
        if ($assertionsDisabled || applicationContext != null) {
            registry.addService(VibrationManager.MANAGER, new VibrationManagerImplementationFactory(applicationContext));
            return;
        }
        throw new AssertionError();
    }
}
