package org.chromium.ui.base;

import android.content.Context;
import android.support.v4.view.InputDeviceCompat;
import android.view.InputDevice;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("ui")
public class TouchDevice {
    private TouchDevice() {
    }

    @CalledByNative
    private static int maxTouchPoints(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.touchscreen.multitouch.jazzhand")) {
            return 5;
        }
        if (context.getPackageManager().hasSystemFeature("android.hardware.touchscreen.multitouch.distinct") || context.getPackageManager().hasSystemFeature("android.hardware.touchscreen.multitouch")) {
            return 2;
        }
        if (context.getPackageManager().hasSystemFeature("android.hardware.touchscreen")) {
            return 1;
        }
        return 0;
    }

    @CalledByNative
    private static int availablePointerTypes(Context context) {
        int pointerTypesVal = 0;
        for (int deviceId : InputDevice.getDeviceIds()) {
            InputDevice inputDevice = InputDevice.getDevice(deviceId);
            if (inputDevice != null) {
                int sources = inputDevice.getSources();
                if (hasSource(sources, 8194) || hasSource(sources, InputDeviceCompat.SOURCE_STYLUS) || hasSource(sources, InputDeviceCompat.SOURCE_TOUCHPAD) || hasSource(sources, InputDeviceCompat.SOURCE_TRACKBALL)) {
                    pointerTypesVal |= 4;
                } else if (hasSource(sources, InputDeviceCompat.SOURCE_TOUCHSCREEN)) {
                    pointerTypesVal |= 2;
                }
            }
        }
        if (pointerTypesVal == 0) {
            return 1;
        }
        return pointerTypesVal;
    }

    @CalledByNative
    private static int availableHoverTypes(Context context) {
        int hoverTypesVal = 0;
        for (int deviceId : InputDevice.getDeviceIds()) {
            InputDevice inputDevice = InputDevice.getDevice(deviceId);
            if (inputDevice != null) {
                int sources = inputDevice.getSources();
                if (hasSource(sources, 8194) || hasSource(sources, InputDeviceCompat.SOURCE_TOUCHPAD) || hasSource(sources, InputDeviceCompat.SOURCE_TRACKBALL)) {
                    hoverTypesVal |= 4;
                } else if (hasSource(sources, InputDeviceCompat.SOURCE_STYLUS) || hasSource(sources, InputDeviceCompat.SOURCE_TOUCHSCREEN)) {
                    hoverTypesVal |= 2;
                }
            }
        }
        if (hoverTypesVal == 0) {
            return 1;
        }
        return hoverTypesVal;
    }

    private static boolean hasSource(int sources, int inputDeviceSource) {
        return (sources & inputDeviceSource) == inputDeviceSource;
    }
}
