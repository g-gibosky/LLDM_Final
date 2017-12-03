package org.chromium.media;

import android.content.Context;
import android.view.WindowManager;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("media")
public abstract class VideoCapture {
    protected int mCameraNativeOrientation;
    protected VideoCaptureFormat mCaptureFormat = null;
    protected final Context mContext;
    protected final int mId;
    protected boolean mInvertDeviceOrientationReadings;
    protected final long mNativeVideoCaptureDeviceAndroid;

    @CalledByNative
    public abstract boolean allocate(int i, int i2, int i3);

    @CalledByNative
    public abstract void deallocate();

    @CalledByNative
    public abstract PhotoCapabilities getPhotoCapabilities();

    public native void nativeOnError(long j, String str);

    public native void nativeOnFrameAvailable(long j, byte[] bArr, int i, int i2);

    public native void nativeOnPhotoTaken(long j, long j2, byte[] bArr);

    @CalledByNative
    public abstract boolean startCapture();

    @CalledByNative
    public abstract boolean stopCapture();

    @CalledByNative
    public abstract boolean takePhoto(long j);

    VideoCapture(Context context, int id, long nativeVideoCaptureDeviceAndroid) {
        this.mContext = context;
        this.mId = id;
        this.mNativeVideoCaptureDeviceAndroid = nativeVideoCaptureDeviceAndroid;
    }

    @CalledByNative
    public final int queryWidth() {
        return this.mCaptureFormat.mWidth;
    }

    @CalledByNative
    public final int queryHeight() {
        return this.mCaptureFormat.mHeight;
    }

    @CalledByNative
    public final int queryFrameRate() {
        return this.mCaptureFormat.mFramerate;
    }

    @CalledByNative
    public final int getColorspace() {
        switch (this.mCaptureFormat.mPixelFormat) {
            case 17:
                return 17;
            case 35:
                return 35;
            case AndroidImageFormat.YV12 /*842094169*/:
                return AndroidImageFormat.YV12;
            default:
                return 0;
        }
    }

    protected final int getCameraRotation() {
        return (this.mCameraNativeOrientation + (this.mInvertDeviceOrientationReadings ? 360 - getDeviceRotation() : getDeviceRotation())) % 360;
    }

    protected final int getDeviceRotation() {
        if (this.mContext == null) {
            return 0;
        }
        switch (((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRotation()) {
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
                return 270;
            default:
                return 0;
        }
    }
}
