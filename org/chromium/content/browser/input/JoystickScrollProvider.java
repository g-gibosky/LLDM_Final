package org.chromium.content.browser.input;

import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import org.chromium.base.Log;
import org.chromium.content.browser.ContentViewCore;

public class JoystickScrollProvider {
    private static final float JOYSTICK_SCROLL_DEADZONE = 0.2f;
    private static final float JOYSTICK_SCROLL_FACTOR_MULTIPLIER = 20.0f;
    private static final float SCROLL_FACTOR_FALLBACK = 128.0f;
    private static final String TAG = "JoystickScroll";
    private boolean mEnabled = true;
    private long mLastAnimateTimeMillis;
    private float mScrollFactor;
    private Runnable mScrollRunnable;
    private float mScrollVelocityX;
    private float mScrollVelocityY;
    private final ContentViewCore mView;

    class C02131 implements Runnable {
        C02131() {
        }

        public void run() {
            JoystickScrollProvider.this.animateScroll();
        }
    }

    public JoystickScrollProvider(ContentViewCore contentView) {
        this.mView = contentView;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
        if (!enabled) {
            stop();
        }
    }

    public boolean onMotion(MotionEvent event) {
        if (!this.mEnabled || (event.getSource() & 16) == 0) {
            return false;
        }
        Log.m20d(TAG, "Joystick left stick axis: " + event.getAxisValue(0) + "," + event.getAxisValue(1));
        computeNewScrollVelocity(event);
        if (this.mScrollVelocityX == 0.0f && this.mScrollVelocityY == 0.0f) {
            stop();
            return false;
        }
        if (this.mScrollRunnable == null) {
            this.mScrollRunnable = new C02131();
        }
        if (this.mLastAnimateTimeMillis == 0) {
            this.mView.getContainerView().postOnAnimation(this.mScrollRunnable);
            this.mLastAnimateTimeMillis = AnimationUtils.currentAnimationTimeMillis();
        }
        return true;
    }

    private void animateScroll() {
        if (this.mLastAnimateTimeMillis != 0) {
            long timeMillis = AnimationUtils.currentAnimationTimeMillis();
            long dt = timeMillis - this.mLastAnimateTimeMillis;
            this.mView.scrollBy((this.mScrollVelocityX * ((float) dt)) / 1000.0f, (this.mScrollVelocityY * ((float) dt)) / 1000.0f, true);
            this.mLastAnimateTimeMillis = timeMillis;
            this.mView.getContainerView().postOnAnimation(this.mScrollRunnable);
        }
    }

    private void stop() {
        this.mLastAnimateTimeMillis = 0;
    }

    private void computeNewScrollVelocity(MotionEvent event) {
        if (this.mScrollFactor == 0.0f) {
            TypedValue outValue = new TypedValue();
            if (this.mView.getContext().getTheme().resolveAttribute(16842829, outValue, true)) {
                Log.m20d(TAG, "Theme attribute listPreferredItemHeight not definedswitching to fallback scroll factor ");
                this.mScrollFactor = SCROLL_FACTOR_FALLBACK * this.mView.getRenderCoordinates().getDeviceScaleFactor();
            } else {
                this.mScrollFactor = outValue.getDimension(this.mView.getContext().getResources().getDisplayMetrics());
            }
        }
        this.mScrollVelocityX = (getFilteredAxisValue(event, 0) * this.mScrollFactor) * JOYSTICK_SCROLL_FACTOR_MULTIPLIER;
        this.mScrollVelocityY = (getFilteredAxisValue(event, 1) * this.mScrollFactor) * JOYSTICK_SCROLL_FACTOR_MULTIPLIER;
    }

    private float getFilteredAxisValue(MotionEvent event, int axis) {
        float axisValWithNoise = event.getAxisValue(axis);
        return (axisValWithNoise > JOYSTICK_SCROLL_DEADZONE || axisValWithNoise < -0.2f) ? axisValWithNoise : 0.0f;
    }
}
