package org.chromium.device.power_save_blocker;

import android.view.View;
import java.lang.ref.WeakReference;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.ui.base.ViewAndroidDelegate;

@JNINamespace("device")
class PowerSaveBlocker {
    static final /* synthetic */ boolean $assertionsDisabled = (!PowerSaveBlocker.class.desiredAssertionStatus());
    private WeakReference<View> mKeepScreenOnView;

    @CalledByNative
    private static PowerSaveBlocker create() {
        return new PowerSaveBlocker();
    }

    private PowerSaveBlocker() {
    }

    @CalledByNative
    private void applyBlock(ViewAndroidDelegate delegate) {
        if ($assertionsDisabled || this.mKeepScreenOnView == null) {
            View anchorView = delegate.acquireAnchorView();
            this.mKeepScreenOnView = new WeakReference(anchorView);
            delegate.setAnchorViewPosition(anchorView, 0.0f, 0.0f, 0.0f, 0.0f);
            anchorView.setKeepScreenOn(true);
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private void removeBlock(ViewAndroidDelegate delegate) {
        if ($assertionsDisabled || this.mKeepScreenOnView != null) {
            View anchorView = (View) this.mKeepScreenOnView.get();
            this.mKeepScreenOnView = null;
            if (anchorView != null) {
                anchorView.setKeepScreenOn(false);
                delegate.releaseAnchorView(anchorView);
                return;
            }
            return;
        }
        throw new AssertionError();
    }
}
