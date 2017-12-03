package org.chromium.content.browser;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import org.chromium.base.ApplicationStatus;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content_public.common.ScreenOrientationConstants;
import org.chromium.ui.gfx.DeviceDisplayInfo;

@JNINamespace("content")
public class ScreenOrientationProvider {
    private static final String TAG = "cr.ScreenOrientation";

    static class C02031 implements Runnable {
        C02031() {
        }

        public void run() {
            ScreenOrientationListener.getInstance().startAccurateListening();
        }
    }

    static class C02042 implements Runnable {
        C02042() {
        }

        public void run() {
            ScreenOrientationListener.getInstance().stopAccurateListening();
        }
    }

    private static int getOrientationFromWebScreenOrientations(byte orientation, Activity activity) {
        switch (orientation) {
            case (byte) 0:
                return -1;
            case (byte) 1:
                return 1;
            case (byte) 2:
                return 9;
            case (byte) 3:
                return 0;
            case (byte) 4:
                return 8;
            case (byte) 5:
                return 10;
            case (byte) 6:
                return 6;
            case (byte) 7:
                return 7;
            case (byte) 8:
                DeviceDisplayInfo displayInfo = DeviceDisplayInfo.create(activity);
                int rotation = displayInfo.getRotationDegrees();
                if (rotation == 0 || rotation == 180) {
                    if (displayInfo.getDisplayHeight() >= displayInfo.getDisplayWidth()) {
                        return 1;
                    }
                    return 0;
                } else if (displayInfo.getDisplayHeight() < displayInfo.getDisplayWidth()) {
                    return 1;
                } else {
                    return 0;
                }
            default:
                Log.m38w(TAG, "Trying to lock to unsupported orientation!", new Object[0]);
                return -1;
        }
    }

    @CalledByNative
    static void lockOrientation(byte orientation) {
        lockOrientation(orientation, ApplicationStatus.getLastTrackedFocusedActivity());
    }

    public static void lockOrientation(byte webScreenOrientation, Activity activity) {
        if (activity != null) {
            int orientation = getOrientationFromWebScreenOrientations(webScreenOrientation, activity);
            if (orientation != -1) {
                activity.setRequestedOrientation(orientation);
            }
        }
    }

    @CalledByNative
    static void unlockOrientation() {
        Activity activity = ApplicationStatus.getLastTrackedFocusedActivity();
        if (activity != null) {
            int defaultOrientation = getOrientationFromWebScreenOrientations((byte) activity.getIntent().getIntExtra(ScreenOrientationConstants.EXTRA_ORIENTATION, 0), activity);
            if (defaultOrientation == -1) {
                try {
                    defaultOrientation = activity.getPackageManager().getActivityInfo(activity.getComponentName(), 128).screenOrientation;
                } catch (NameNotFoundException e) {
                    activity.setRequestedOrientation(defaultOrientation);
                    return;
                } catch (Throwable th) {
                    activity.setRequestedOrientation(defaultOrientation);
                }
            }
            activity.setRequestedOrientation(defaultOrientation);
        }
    }

    @CalledByNative
    static void startAccurateListening() {
        ThreadUtils.runOnUiThread(new C02031());
    }

    @CalledByNative
    static void stopAccurateListening() {
        ThreadUtils.runOnUiThread(new C02042());
    }

    private ScreenOrientationProvider() {
    }
}
