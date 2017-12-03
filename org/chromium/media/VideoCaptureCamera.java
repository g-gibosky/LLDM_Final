package org.chromium.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.chromium.base.Log;
import org.chromium.base.annotations.JNINamespace;

@TargetApi(15)
@JNINamespace("media")
public abstract class VideoCaptureCamera extends VideoCapture implements PreviewCallback {
    protected static final int GL_TEXTURE_EXTERNAL_OES = 36197;
    private static final String TAG = "VideoCapture";
    protected Camera mCamera;
    protected int[] mGlTextures = null;
    protected boolean mIsRunning = false;
    private long mPhotoTakenCallbackId = 0;
    private final Object mPhotoTakenCallbackLock = new Object();
    protected ReentrantLock mPreviewBufferLock = new ReentrantLock();
    protected SurfaceTexture mSurfaceTexture = null;

    private class CrErrorCallback implements ErrorCallback {
        private CrErrorCallback() {
        }

        public void onError(int error, Camera camera) {
            VideoCaptureCamera.this.nativeOnError(VideoCaptureCamera.this.mNativeVideoCaptureDeviceAndroid, "Error id: " + error);
            synchronized (VideoCaptureCamera.this.mPhotoTakenCallbackLock) {
                if (VideoCaptureCamera.this.mPhotoTakenCallbackId == 0) {
                    return;
                }
                VideoCaptureCamera.this.nativeOnPhotoTaken(VideoCaptureCamera.this.mNativeVideoCaptureDeviceAndroid, VideoCaptureCamera.this.mPhotoTakenCallbackId, new byte[0]);
                VideoCaptureCamera.this.mPhotoTakenCallbackId = 0;
            }
        }
    }

    private class CrPictureCallback implements PictureCallback {
        private CrPictureCallback() {
        }

        public void onPictureTaken(byte[] data, Camera camera) {
            synchronized (VideoCaptureCamera.this.mPhotoTakenCallbackLock) {
                if (VideoCaptureCamera.this.mPhotoTakenCallbackId != 0) {
                    VideoCaptureCamera.this.nativeOnPhotoTaken(VideoCaptureCamera.this.mNativeVideoCaptureDeviceAndroid, VideoCaptureCamera.this.mPhotoTakenCallbackId, data);
                }
                VideoCaptureCamera.this.mPhotoTakenCallbackId = 0;
            }
            Parameters parameters = VideoCaptureCamera.getCameraParameters(VideoCaptureCamera.this.mCamera);
            parameters.setRotation(0);
            VideoCaptureCamera.this.mCamera.setParameters(parameters);
            camera.startPreview();
        }
    }

    abstract void allocateBuffers();

    abstract void setCaptureParameters(int i, int i2, int i3, Parameters parameters);

    abstract void setPreviewCallback(PreviewCallback previewCallback);

    protected static CameraInfo getCameraInfo(int id) {
        CameraInfo cameraInfo = new CameraInfo();
        try {
            Camera.getCameraInfo(id, cameraInfo);
            return cameraInfo;
        } catch (RuntimeException ex) {
            Log.m28e(TAG, "getCameraInfo: Camera.getCameraInfo: " + ex, new Object[0]);
            return null;
        }
    }

    protected static Parameters getCameraParameters(Camera camera) {
        try {
            return camera.getParameters();
        } catch (RuntimeException ex) {
            Log.m28e(TAG, "getCameraParameters: android.hardware.Camera.getParameters: " + ex, new Object[0]);
            if (camera != null) {
                camera.release();
            }
            return null;
        }
    }

    VideoCaptureCamera(Context context, int id, long nativeVideoCaptureDeviceAndroid) {
        super(context, id, nativeVideoCaptureDeviceAndroid);
    }

    public boolean allocate(int width, int height, int frameRate) {
        Log.m23d(TAG, "allocate: requested (%d x %d) @%dfps", Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(frameRate));
        try {
            this.mCamera = Camera.open(this.mId);
            CameraInfo cameraInfo = getCameraInfo(this.mId);
            if (cameraInfo == null) {
                this.mCamera.release();
                this.mCamera = null;
                return false;
            }
            this.mCameraNativeOrientation = cameraInfo.orientation;
            this.mInvertDeviceOrientationReadings = cameraInfo.facing == 0;
            Log.m23d(TAG, "allocate: Rotation dev=%d, cam=%d, facing back? %s", Integer.valueOf(getDeviceRotation()), Integer.valueOf(this.mCameraNativeOrientation), Boolean.valueOf(this.mInvertDeviceOrientationReadings));
            Parameters parameters = getCameraParameters(this.mCamera);
            if (parameters == null) {
                this.mCamera = null;
                return false;
            }
            List<int[]> listFpsRange = parameters.getSupportedPreviewFpsRange();
            if (listFpsRange == null || listFpsRange.size() == 0) {
                Log.m28e(TAG, "allocate: no fps range found", new Object[0]);
                return false;
            }
            int frameRateNearest;
            int frameRateScaled = frameRate * 1000;
            int[] chosenFpsRange = (int[]) listFpsRange.get(0);
            if (Math.abs(frameRateScaled - chosenFpsRange[0]) < Math.abs(frameRateScaled - chosenFpsRange[1])) {
                frameRateNearest = chosenFpsRange[0];
            } else {
                frameRateNearest = chosenFpsRange[1];
            }
            int chosenFrameRate = (frameRateNearest + 999) / 1000;
            int fpsRangeSize = Integer.MAX_VALUE;
            for (int[] fpsRange : listFpsRange) {
                if (fpsRange[0] <= frameRateScaled && frameRateScaled <= fpsRange[1] && fpsRange[1] - fpsRange[0] <= fpsRangeSize) {
                    chosenFpsRange = fpsRange;
                    chosenFrameRate = frameRate;
                    fpsRangeSize = fpsRange[1] - fpsRange[0];
                }
            }
            Log.m23d(TAG, "allocate: fps set to %d, [%d-%d]", Integer.valueOf(chosenFrameRate), Integer.valueOf(chosenFpsRange[0]), Integer.valueOf(chosenFpsRange[1]));
            int minDiff = Integer.MAX_VALUE;
            int matchedWidth = width;
            int matchedHeight = height;
            for (Size size : parameters.getSupportedPreviewSizes()) {
                int diff = Math.abs(size.width - width) + Math.abs(size.height - height);
                Log.m23d(TAG, "allocate: supported (%d, %d), diff=%d", Integer.valueOf(size.width), Integer.valueOf(size.height), Integer.valueOf(diff));
                if (diff < minDiff && size.width % 32 == 0) {
                    minDiff = diff;
                    matchedWidth = size.width;
                    matchedHeight = size.height;
                }
            }
            if (minDiff == Integer.MAX_VALUE) {
                Log.m28e(TAG, "allocate: can not find a multiple-of-32 resolution", new Object[0]);
                return false;
            }
            Log.m22d(TAG, "allocate: matched (%d x %d)", Integer.valueOf(matchedWidth), Integer.valueOf(matchedHeight));
            if (parameters.isVideoStabilizationSupported()) {
                Log.m20d(TAG, "Image stabilization supported, currently: " + parameters.getVideoStabilization() + ", setting it.");
                parameters.setVideoStabilization(true);
            } else {
                Log.m20d(TAG, "Image stabilization not supported.");
            }
            if (parameters.getSupportedFocusModes().contains("continuous-video")) {
                parameters.setFocusMode("continuous-video");
            } else {
                Log.m20d(TAG, "Continuous focus mode not supported.");
            }
            setCaptureParameters(matchedWidth, matchedHeight, chosenFrameRate, parameters);
            parameters.setPictureSize(matchedWidth, matchedHeight);
            parameters.setPreviewSize(matchedWidth, matchedHeight);
            parameters.setPreviewFpsRange(chosenFpsRange[0], chosenFpsRange[1]);
            parameters.setPreviewFormat(this.mCaptureFormat.mPixelFormat);
            try {
                this.mCamera.setParameters(parameters);
                this.mGlTextures = new int[1];
                GLES20.glGenTextures(1, this.mGlTextures, 0);
                GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, this.mGlTextures[0]);
                GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, 10241, 9729.0f);
                GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, 10240, 9729.0f);
                GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, 10242, 33071);
                GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, 10243, 33071);
                this.mSurfaceTexture = new SurfaceTexture(this.mGlTextures[0]);
                this.mSurfaceTexture.setOnFrameAvailableListener(null);
                try {
                    this.mCamera.setPreviewTexture(this.mSurfaceTexture);
                    this.mCamera.setErrorCallback(new CrErrorCallback());
                    allocateBuffers();
                    return true;
                } catch (IOException ex) {
                    Log.m28e(TAG, "allocate: " + ex, new Object[0]);
                    return false;
                }
            } catch (RuntimeException ex2) {
                Log.m28e(TAG, "setParameters: " + ex2, new Object[0]);
                return false;
            }
        } catch (RuntimeException ex22) {
            Log.m28e(TAG, "allocate: Camera.open: " + ex22, new Object[0]);
            return false;
        }
    }

    public boolean startCapture() {
        ReentrantLock reentrantLock = true;
        if (this.mCamera == null) {
            Log.m28e(TAG, "startCapture: mCamera is null", new Object[0]);
            return false;
        }
        this.mPreviewBufferLock.lock();
        try {
            if (this.mIsRunning) {
                return reentrantLock;
            }
            this.mIsRunning = true;
            this.mPreviewBufferLock.unlock();
            setPreviewCallback(this);
            try {
                this.mCamera.startPreview();
                return true;
            } catch (RuntimeException ex) {
                Log.m28e(TAG, "startCapture: Camera.startPreview: " + ex, new Object[0]);
                return false;
            }
        } finally {
            reentrantLock = this.mPreviewBufferLock;
            reentrantLock.unlock();
        }
    }

    public boolean stopCapture() {
        if (this.mCamera == null) {
            Log.m28e(TAG, "stopCapture: mCamera is null", new Object[0]);
        } else {
            this.mPreviewBufferLock.lock();
            try {
                if (this.mIsRunning) {
                    this.mIsRunning = false;
                    this.mPreviewBufferLock.unlock();
                    this.mCamera.stopPreview();
                    setPreviewCallback(null);
                }
            } finally {
                this.mPreviewBufferLock.unlock();
            }
        }
        return true;
    }

    public PhotoCapabilities getPhotoCapabilities() {
        Parameters parameters = getCameraParameters(this.mCamera);
        if (!parameters.isZoomSupported()) {
            return new PhotoCapabilities(0, 0, 0);
        }
        Log.m20d(TAG, "parameters.getZoomRatios(): " + parameters.getZoomRatios().toString());
        return new PhotoCapabilities(((Integer) parameters.getZoomRatios().get(parameters.getMaxZoom())).intValue(), 100, (parameters.getZoom() * 100) + 100);
    }

    public boolean takePhoto(long callbackId) {
        if (this.mCamera == null || !this.mIsRunning) {
            Log.m28e(TAG, "takePhoto: mCamera is null or is not running", new Object[0]);
            return false;
        }
        synchronized (this.mPhotoTakenCallbackLock) {
            if (this.mPhotoTakenCallbackId != 0) {
                return false;
            }
            this.mPhotoTakenCallbackId = callbackId;
            Parameters parameters = getCameraParameters(this.mCamera);
            parameters.setRotation(getCameraRotation());
            this.mCamera.setParameters(parameters);
            try {
                this.mCamera.takePicture(null, null, null, new CrPictureCallback());
                return true;
            } catch (RuntimeException ex) {
                Log.m28e(TAG, "takePicture ", ex);
                return false;
            }
        }
    }

    public void deallocate() {
        if (this.mCamera != null) {
            stopCapture();
            try {
                this.mCamera.setPreviewTexture(null);
                if (this.mGlTextures != null) {
                    GLES20.glDeleteTextures(1, this.mGlTextures, 0);
                }
                this.mCaptureFormat = null;
                this.mCamera.release();
                this.mCamera = null;
            } catch (IOException ex) {
                Log.m28e(TAG, "deallocate: failed to deallocate camera, " + ex, new Object[0]);
            }
        }
    }
}
