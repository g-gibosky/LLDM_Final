package org.chromium.ui.gfx;

import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.TypedValue;
import android.view.ViewConfiguration;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.ui.C0290R;

@JNINamespace("gfx")
public class ViewConfigurationHelper {
    static final /* synthetic */ boolean $assertionsDisabled = (!ViewConfigurationHelper.class.desiredAssertionStatus());
    private static final float MIN_SCALING_SPAN_MM = 12.0f;
    private final Context mAppContext;
    private float mDensity = this.mAppContext.getResources().getDisplayMetrics().density;
    private ViewConfiguration mViewConfiguration = ViewConfiguration.get(this.mAppContext);

    class C03001 implements ComponentCallbacks {
        C03001() {
        }

        public void onConfigurationChanged(Configuration configuration) {
            ViewConfigurationHelper.this.updateNativeViewConfigurationIfNecessary();
        }

        public void onLowMemory() {
        }
    }

    private native void nativeUpdateSharedViewConfiguration(float f, float f2, float f3, float f4, float f5);

    private ViewConfigurationHelper(Context context) {
        this.mAppContext = context.getApplicationContext();
        if (!$assertionsDisabled && this.mDensity <= 0.0f) {
            throw new AssertionError();
        }
    }

    private void registerListener() {
        this.mAppContext.registerComponentCallbacks(new C03001());
    }

    private void updateNativeViewConfigurationIfNecessary() {
        ViewConfiguration configuration = ViewConfiguration.get(this.mAppContext);
        if (this.mViewConfiguration != configuration) {
            this.mViewConfiguration = configuration;
            this.mDensity = this.mAppContext.getResources().getDisplayMetrics().density;
            if ($assertionsDisabled || this.mDensity > 0.0f) {
                nativeUpdateSharedViewConfiguration(getMaximumFlingVelocity(), getMinimumFlingVelocity(), getTouchSlop(), getDoubleTapSlop(), getMinScalingSpan());
                return;
            }
            throw new AssertionError();
        } else if (!$assertionsDisabled && this.mDensity != this.mAppContext.getResources().getDisplayMetrics().density) {
            throw new AssertionError();
        }
    }

    @CalledByNative
    private static int getDoubleTapTimeout() {
        return ViewConfiguration.getDoubleTapTimeout();
    }

    @CalledByNative
    private static int getLongPressTimeout() {
        return ViewConfiguration.getLongPressTimeout();
    }

    @CalledByNative
    private static int getTapTimeout() {
        return ViewConfiguration.getTapTimeout();
    }

    @CalledByNative
    private static float getScrollFriction() {
        return ViewConfiguration.getScrollFriction();
    }

    @CalledByNative
    private float getMaximumFlingVelocity() {
        return toDips(this.mViewConfiguration.getScaledMaximumFlingVelocity());
    }

    @CalledByNative
    private float getMinimumFlingVelocity() {
        return toDips(this.mViewConfiguration.getScaledMinimumFlingVelocity());
    }

    @CalledByNative
    private float getTouchSlop() {
        return toDips(this.mViewConfiguration.getScaledTouchSlop());
    }

    @CalledByNative
    private float getDoubleTapSlop() {
        return toDips(this.mViewConfiguration.getScaledDoubleTapSlop());
    }

    @CalledByNative
    private float getMinScalingSpan() {
        return toDips(getScaledMinScalingSpan());
    }

    private int getScaledMinScalingSpan() {
        Resources res = this.mAppContext.getResources();
        try {
            return res.getDimensionPixelSize(C0290R.dimen.config_min_scaling_span);
        } catch (NotFoundException e) {
            if ($assertionsDisabled) {
                return (int) TypedValue.applyDimension(5, MIN_SCALING_SPAN_MM, res.getDisplayMetrics());
            }
            throw new AssertionError("MinScalingSpan resource lookup failed.");
        }
    }

    private float toDips(int pixels) {
        return ((float) pixels) / this.mDensity;
    }

    @CalledByNative
    private static ViewConfigurationHelper createWithListener(Context context) {
        ViewConfigurationHelper viewConfigurationHelper = new ViewConfigurationHelper(context);
        viewConfigurationHelper.registerListener();
        return viewConfigurationHelper;
    }
}
