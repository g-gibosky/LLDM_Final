package org.chromium.content.browser.input;

import android.view.MotionEvent;
import org.chromium.content.browser.ContentViewCore;

public class JoystickZoomProvider {
    private static final float JOYSTICK_NOISE_THRESHOLD = 0.2f;
    private static final String TAG = "JoystickZoomProvider";
    private static final float ZOOM_SPEED = 1.65f;
    protected final ContentViewCore mContentViewCore;
    protected float mDeviceScaleFactor = this.mContentViewCore.getRenderCoordinates().getDeviceScaleFactor();
    private long mLastAnimateTimeMillis;
    private AnimationIntervalProvider mSystemAnimationIntervalProvider;
    private float mZoomInVelocity;
    private float mZoomOutVelocity;
    protected Runnable mZoomRunnable;
    private int mZoomXcoord = (this.mContentViewCore.getViewportWidthPix() / 2);
    private int mZoomYcoord = (this.mContentViewCore.getViewportHeightPix() / 2);

    class C02141 implements Runnable {
        C02141() {
        }

        public void run() {
            JoystickZoomProvider.this.animateZoom();
        }
    }

    public JoystickZoomProvider(ContentViewCore cvc, AnimationIntervalProvider animationTimeProvider) {
        this.mContentViewCore = cvc;
        this.mSystemAnimationIntervalProvider = animationTimeProvider;
    }

    public boolean onMotion(MotionEvent event) {
        if ((event.getSource() & 16) == 0) {
            return false;
        }
        computeNewZoomVelocity(event);
        if (this.mZoomInVelocity == 0.0f && this.mZoomOutVelocity == 0.0f) {
            stop();
            return false;
        }
        if (this.mZoomRunnable == null) {
            this.mZoomRunnable = new C02141();
        }
        if (this.mLastAnimateTimeMillis == 0) {
            this.mLastAnimateTimeMillis = this.mSystemAnimationIntervalProvider.getLastAnimationFrameInterval();
            this.mContentViewCore.getContainerView().postOnAnimation(this.mZoomRunnable);
            this.mContentViewCore.pinchBegin(this.mZoomXcoord, this.mZoomYcoord);
        }
        return true;
    }

    protected void stop() {
        if (this.mLastAnimateTimeMillis != 0) {
            this.mContentViewCore.pinchEnd();
            this.mLastAnimateTimeMillis = 0;
        }
    }

    private void computeNewZoomVelocity(MotionEvent event) {
        this.mZoomInVelocity = getFilteredAxisValue(event, 18);
        this.mZoomOutVelocity = getFilteredAxisValue(event, 17);
    }

    protected void animateZoom() {
        if (!this.mContentViewCore.getContainerView().hasFocus()) {
            stop();
        } else if (this.mLastAnimateTimeMillis != 0) {
            long timeMillis = this.mSystemAnimationIntervalProvider.getLastAnimationFrameInterval();
            this.mContentViewCore.pinchBy(this.mZoomXcoord, this.mZoomYcoord, (float) Math.pow(1.649999976158142d, (double) (((this.mDeviceScaleFactor * (this.mZoomInVelocity - this.mZoomOutVelocity)) * ((float) (timeMillis - this.mLastAnimateTimeMillis))) / 1000.0f)));
            this.mLastAnimateTimeMillis = timeMillis;
            this.mContentViewCore.getContainerView().postOnAnimation(this.mZoomRunnable);
        }
    }

    private float getFilteredAxisValue(MotionEvent event, int axis) {
        float axisValWithNoise = event.getAxisValue(axis);
        return axisValWithNoise > JOYSTICK_NOISE_THRESHOLD ? axisValWithNoise : 0.0f;
    }
}
