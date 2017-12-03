package org.chromium.ui.gfx;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("gfx")
public class DeviceDisplayInfo {
    static final /* synthetic */ boolean $assertionsDisabled = (!DeviceDisplayInfo.class.desiredAssertionStatus());
    private final Context mAppContext;
    private DisplayMetrics mTempMetrics = new DisplayMetrics();
    private Point mTempPoint = new Point();
    private final WindowManager mWinManager;

    private native void nativeUpdateSharedDeviceDisplayInfo(int i, int i2, int i3, int i4, int i5, int i6, double d, int i7, int i8);

    private DeviceDisplayInfo(Context context) {
        this.mAppContext = context.getApplicationContext();
        this.mWinManager = (WindowManager) this.mAppContext.getSystemService("window");
    }

    @CalledByNative
    public int getDisplayHeight() {
        getDisplay().getSize(this.mTempPoint);
        return this.mTempPoint.y;
    }

    @CalledByNative
    public int getDisplayWidth() {
        getDisplay().getSize(this.mTempPoint);
        return this.mTempPoint.x;
    }

    @TargetApi(17)
    @CalledByNative
    public int getPhysicalDisplayHeight() {
        if (VERSION.SDK_INT < 17) {
            return 0;
        }
        getDisplay().getRealSize(this.mTempPoint);
        return this.mTempPoint.y;
    }

    @TargetApi(17)
    @CalledByNative
    public int getPhysicalDisplayWidth() {
        if (VERSION.SDK_INT < 17) {
            return 0;
        }
        getDisplay().getRealSize(this.mTempPoint);
        return this.mTempPoint.x;
    }

    private int getPixelFormat() {
        if (VERSION.SDK_INT < 17) {
            return getDisplay().getPixelFormat();
        }
        return 1;
    }

    @CalledByNative
    public int getBitsPerPixel() {
        int format = getPixelFormat();
        PixelFormat info = new PixelFormat();
        PixelFormat.getPixelFormatInfo(format, info);
        return info.bitsPerPixel;
    }

    @CalledByNative
    public int getBitsPerComponent() {
        switch (getPixelFormat()) {
            case 1:
            case 2:
            case 3:
                return 8;
            case 4:
            case 6:
                return 5;
            case 7:
                return 4;
            case 8:
            case 9:
            case 10:
                return 0;
            case 11:
                return 2;
            default:
                return 8;
        }
    }

    @CalledByNative
    public double getDIPScale() {
        getDisplay().getMetrics(this.mTempMetrics);
        return (double) this.mTempMetrics.density;
    }

    @CalledByNative
    private int getSmallestDIPWidth() {
        return this.mAppContext.getResources().getConfiguration().smallestScreenWidthDp;
    }

    @CalledByNative
    public int getRotationDegrees() {
        switch (getDisplay().getRotation()) {
            case 0:
                return 0;
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
                return 270;
            default:
                if ($assertionsDisabled) {
                    return 0;
                }
                throw new AssertionError();
        }
    }

    public void updateNativeSharedDisplayInfo() {
        nativeUpdateSharedDeviceDisplayInfo(getDisplayHeight(), getDisplayWidth(), getPhysicalDisplayHeight(), getPhysicalDisplayWidth(), getBitsPerPixel(), getBitsPerComponent(), getDIPScale(), getSmallestDIPWidth(), getRotationDegrees());
    }

    private Display getDisplay() {
        return this.mWinManager.getDefaultDisplay();
    }

    @CalledByNative
    public static DeviceDisplayInfo create(Context context) {
        return new DeviceDisplayInfo(context);
    }
}
