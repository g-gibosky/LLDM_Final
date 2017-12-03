package org.chromium.content.browser;

import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("content")
public class MotionEventSynthesizer {
    static final /* synthetic */ boolean $assertionsDisabled = (!MotionEventSynthesizer.class.desiredAssertionStatus());
    private static final int ACTION_CANCEL = 2;
    private static final int ACTION_END = 3;
    private static final int ACTION_MOVE = 1;
    private static final int ACTION_SCROLL = 4;
    private static final int ACTION_START = 0;
    private static final int MAX_NUM_POINTERS = 16;
    private final ContentViewCore mContentViewCore;
    private long mDownTimeInMs;
    private final PointerCoords[] mPointerCoords = new PointerCoords[16];
    private final PointerProperties[] mPointerProperties = new PointerProperties[16];

    MotionEventSynthesizer(ContentViewCore contentViewCore) {
        this.mContentViewCore = contentViewCore;
    }

    @CalledByNative
    void setPointer(int index, int x, int y, int id) {
        if ($assertionsDisabled || (index >= 0 && index < 16)) {
            float scaleFactor = this.mContentViewCore.getRenderCoordinates().getDeviceScaleFactor();
            PointerCoords coords = new PointerCoords();
            coords.x = ((float) x) * scaleFactor;
            coords.y = ((float) y) * scaleFactor;
            coords.pressure = 1.0f;
            this.mPointerCoords[index] = coords;
            PointerProperties properties = new PointerProperties();
            properties.id = id;
            this.mPointerProperties[index] = properties;
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    void setScrollDeltas(int x, int y, int dx, int dy) {
        setPointer(0, x, y, 0);
        float scaleFactor = this.mContentViewCore.getRenderCoordinates().getDeviceScaleFactor();
        this.mPointerCoords[0].setAxisValue(10, ((float) dx) * scaleFactor);
        this.mPointerCoords[0].setAxisValue(9, ((float) dy) * scaleFactor);
    }

    @CalledByNative
    void inject(int action, int pointerCount, long timeInMs) {
        MotionEvent event;
        switch (action) {
            case 0:
                this.mDownTimeInMs = timeInMs;
                event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, 0, 1, this.mPointerProperties, this.mPointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 0, 0);
                this.mContentViewCore.onTouchEvent(event);
                event.recycle();
                if (pointerCount > 1) {
                    event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, 5, pointerCount, this.mPointerProperties, this.mPointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 0, 0);
                    this.mContentViewCore.onTouchEvent(event);
                    event.recycle();
                    return;
                }
                return;
            case 1:
                event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, 2, pointerCount, this.mPointerProperties, this.mPointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 0, 0);
                this.mContentViewCore.onTouchEvent(event);
                event.recycle();
                return;
            case 2:
                event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, 3, 1, this.mPointerProperties, this.mPointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 0, 0);
                this.mContentViewCore.onTouchEvent(event);
                event.recycle();
                return;
            case 3:
                if (pointerCount > 1) {
                    event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, 6, pointerCount, this.mPointerProperties, this.mPointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 0, 0);
                    this.mContentViewCore.onTouchEvent(event);
                    event.recycle();
                }
                event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, 1, 1, this.mPointerProperties, this.mPointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 0, 0);
                this.mContentViewCore.onTouchEvent(event);
                event.recycle();
                return;
            case 4:
                if ($assertionsDisabled || pointerCount == 1) {
                    event = MotionEvent.obtain(this.mDownTimeInMs, timeInMs, 8, pointerCount, this.mPointerProperties, this.mPointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 2, 0);
                    this.mContentViewCore.onGenericMotionEvent(event);
                    event.recycle();
                    return;
                }
                throw new AssertionError();
            default:
                if (!$assertionsDisabled) {
                    throw new AssertionError("Unreached");
                }
                return;
        }
    }
}
