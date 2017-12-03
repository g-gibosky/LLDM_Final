package org.chromium.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.chromium.base.Log;
import org.chromium.base.annotations.JNINamespace;

@TargetApi(21)
@JNINamespace("media")
public class VideoCaptureCamera2 extends VideoCapture {
    private static final String TAG = "VideoCapture";
    private static final double kNanoSecondsToFps = 1.0E-9d;
    private CameraDevice mCameraDevice;
    private CameraState mCameraState = CameraState.STOPPED;
    private final Object mCameraStateLock = new Object();
    private byte[] mCapturedData;
    private CaptureRequest mPreviewRequest;
    private CameraCaptureSession mPreviewSession;

    private enum CameraState {
        OPENING,
        CONFIGURING,
        STARTED,
        STOPPED
    }

    private class CrImageReaderListener implements OnImageAvailableListener {
        private CrImageReaderListener() {
        }

        public void onImageAvailable(ImageReader reader) {
            try {
                Throwable th;
                Throwable th2;
                Image image = reader.acquireLatestImage();
                Throwable th3 = null;
                if (image != null) {
                    try {
                        if (image.getFormat() != 35 || image.getPlanes().length != 3) {
                            VideoCaptureCamera2.this.nativeOnError(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, "Unexpected image format: " + image.getFormat() + " or #planes: " + image.getPlanes().length);
                            throw new IllegalStateException();
                        } else if (reader.getWidth() == image.getWidth() && reader.getHeight() == image.getHeight()) {
                            VideoCaptureCamera2.readImageIntoBuffer(image, VideoCaptureCamera2.this.mCapturedData);
                            VideoCaptureCamera2.this.nativeOnFrameAvailable(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, VideoCaptureCamera2.this.mCapturedData, VideoCaptureCamera2.this.mCapturedData.length, VideoCaptureCamera2.this.getCameraRotation());
                            if (image == null) {
                                return;
                            }
                            if (th3 != null) {
                                try {
                                    image.close();
                                    return;
                                } catch (Throwable x2) {
                                    th3.addSuppressed(x2);
                                    return;
                                }
                            }
                            image.close();
                            return;
                        } else {
                            VideoCaptureCamera2.this.nativeOnError(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, "ImageReader size (" + reader.getWidth() + "x" + reader.getHeight() + ") did not match Image size (" + image.getWidth() + "x" + image.getHeight() + ")");
                            throw new IllegalStateException();
                        }
                    } catch (Throwable th22) {
                        Throwable th4 = th22;
                        th22 = th;
                        th = th4;
                    }
                } else if (image == null) {
                    return;
                } else {
                    if (th3 != null) {
                        try {
                            image.close();
                            return;
                        } catch (Throwable x22) {
                            th3.addSuppressed(x22);
                            return;
                        }
                    }
                    image.close();
                    return;
                }
                if (image != null) {
                    if (th22 != null) {
                        try {
                            image.close();
                        } catch (Throwable x222) {
                            th22.addSuppressed(x222);
                        }
                    } else {
                        image.close();
                    }
                }
                throw th;
                throw th;
            } catch (IllegalStateException ex) {
                Log.m28e(VideoCaptureCamera2.TAG, "acquireLatestImage():", ex);
            }
        }
    }

    private class CrPhotoReaderListener implements OnImageAvailableListener {
        private final long mCallbackId;

        CrPhotoReaderListener(long callbackId) {
            this.mCallbackId = callbackId;
        }

        private byte[] readCapturedData(Image image) {
            byte[] capturedData = null;
            try {
                return image.getPlanes()[0].getBuffer().array();
            } catch (UnsupportedOperationException e) {
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                capturedData = new byte[buffer.remaining()];
                buffer.get(capturedData);
                return capturedData;
            } catch (Throwable th) {
                return capturedData;
            }
        }

        public void onImageAvailable(ImageReader reader) {
            Log.m20d(VideoCaptureCamera2.TAG, "CrPhotoReaderListener.mCallbackId " + this.mCallbackId);
            try {
                Throwable th;
                Throwable th2;
                Image image = reader.acquireLatestImage();
                Throwable th3 = null;
                if (image == null) {
                    try {
                        throw new IllegalStateException();
                    } catch (Throwable th22) {
                        Throwable th4 = th22;
                        th22 = th;
                        th = th4;
                    }
                } else if (image.getFormat() != 256) {
                    Log.m28e(VideoCaptureCamera2.TAG, "Unexpected image format: %d", Integer.valueOf(image.getFormat()));
                    throw new IllegalStateException();
                } else {
                    VideoCaptureCamera2.this.nativeOnPhotoTaken(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, this.mCallbackId, readCapturedData(image));
                    if (image != null) {
                        if (th3 != null) {
                            try {
                                image.close();
                            } catch (Throwable x2) {
                                th3.addSuppressed(x2);
                            }
                        } else {
                            image.close();
                        }
                    }
                    if (!VideoCaptureCamera2.this.createPreviewObjects()) {
                        VideoCaptureCamera2.this.nativeOnError(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, "Error restarting preview");
                        return;
                    }
                    return;
                }
                throw th;
                if (image != null) {
                    if (th22 != null) {
                        try {
                            image.close();
                        } catch (Throwable x22) {
                            th22.addSuppressed(x22);
                        }
                    } else {
                        image.close();
                    }
                }
                throw th;
            } catch (IllegalStateException e) {
                VideoCaptureCamera2.this.notifyTakePhotoError(this.mCallbackId);
            }
        }
    }

    private class CrPhotoSessionListener extends StateCallback {
        private final long mCallbackId;
        private final CaptureRequest mPhotoRequest;

        CrPhotoSessionListener(CaptureRequest photoRequest, long callbackId) {
            this.mPhotoRequest = photoRequest;
            this.mCallbackId = callbackId;
        }

        public void onConfigured(CameraCaptureSession session) {
            Log.m20d(VideoCaptureCamera2.TAG, "onConfigured");
            try {
                session.capture(this.mPhotoRequest, null, null);
            } catch (CameraAccessException e) {
                Log.m28e(VideoCaptureCamera2.TAG, "capture() error", new Object[0]);
                VideoCaptureCamera2.this.notifyTakePhotoError(this.mCallbackId);
            }
        }

        public void onConfigureFailed(CameraCaptureSession session) {
            Log.m28e(VideoCaptureCamera2.TAG, "failed configuring capture session", new Object[0]);
            VideoCaptureCamera2.this.notifyTakePhotoError(this.mCallbackId);
        }
    }

    private class CrPreviewSessionListener extends StateCallback {
        private final CaptureRequest mPreviewRequest;

        CrPreviewSessionListener(CaptureRequest previewRequest) {
            this.mPreviewRequest = previewRequest;
        }

        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            Exception ex;
            Log.m20d(VideoCaptureCamera2.TAG, "onConfigured");
            VideoCaptureCamera2.this.mPreviewSession = cameraCaptureSession;
            try {
                VideoCaptureCamera2.this.mPreviewSession.setRepeatingRequest(this.mPreviewRequest, null, null);
                VideoCaptureCamera2.this.changeCameraStateAndNotify(CameraState.STARTED);
            } catch (CameraAccessException e) {
                ex = e;
                Log.m28e(VideoCaptureCamera2.TAG, "setRepeatingRequest: ", ex);
            } catch (IllegalArgumentException e2) {
                ex = e2;
                Log.m28e(VideoCaptureCamera2.TAG, "setRepeatingRequest: ", ex);
            } catch (SecurityException e3) {
                ex = e3;
                Log.m28e(VideoCaptureCamera2.TAG, "setRepeatingRequest: ", ex);
            }
        }

        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
            VideoCaptureCamera2.this.changeCameraStateAndNotify(CameraState.STOPPED);
            VideoCaptureCamera2.this.nativeOnError(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, "Camera session configuration error");
        }
    }

    private class CrStateListener extends CameraDevice.StateCallback {
        private CrStateListener() {
        }

        public void onOpened(CameraDevice cameraDevice) {
            VideoCaptureCamera2.this.mCameraDevice = cameraDevice;
            VideoCaptureCamera2.this.changeCameraStateAndNotify(CameraState.CONFIGURING);
            if (!VideoCaptureCamera2.this.createPreviewObjects()) {
                VideoCaptureCamera2.this.changeCameraStateAndNotify(CameraState.STOPPED);
                VideoCaptureCamera2.this.nativeOnError(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, "Error configuring camera");
            }
        }

        public void onDisconnected(CameraDevice cameraDevice) {
            cameraDevice.close();
            VideoCaptureCamera2.this.mCameraDevice = null;
            VideoCaptureCamera2.this.changeCameraStateAndNotify(CameraState.STOPPED);
        }

        public void onError(CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            VideoCaptureCamera2.this.mCameraDevice = null;
            VideoCaptureCamera2.this.changeCameraStateAndNotify(CameraState.STOPPED);
            VideoCaptureCamera2.this.nativeOnError(VideoCaptureCamera2.this.mNativeVideoCaptureDeviceAndroid, "Camera device error " + Integer.toString(error));
        }
    }

    private static CameraCharacteristics getCameraCharacteristics(Context appContext, int id) {
        try {
            return ((CameraManager) appContext.getSystemService("camera")).getCameraCharacteristics(Integer.toString(id));
        } catch (CameraAccessException ex) {
            Log.m28e(TAG, "getCameraCharacteristics: ", ex);
            return null;
        }
    }

    private void notifyTakePhotoError(long callbackId) {
        nativeOnPhotoTaken(this.mNativeVideoCaptureDeviceAndroid, callbackId, new byte[0]);
    }

    private boolean createPreviewObjects() {
        Exception ex;
        Log.m20d(TAG, "createPreviewObjects");
        if (this.mCameraDevice == null) {
            return false;
        }
        ImageReader imageReader = ImageReader.newInstance(this.mCaptureFormat.getWidth(), this.mCaptureFormat.getHeight(), this.mCaptureFormat.getPixelFormat(), 2);
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        imageReader.setOnImageAvailableListener(new CrImageReaderListener(), new Handler(thread.getLooper()));
        try {
            Builder previewRequestBuilder = this.mCameraDevice.createCaptureRequest(1);
            if (previewRequestBuilder == null) {
                Log.m28e(TAG, "previewRequestBuilder error", new Object[0]);
                return false;
            }
            previewRequestBuilder.addTarget(imageReader.getSurface());
            previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, Integer.valueOf(1));
            previewRequestBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE, Integer.valueOf(1));
            previewRequestBuilder.set(CaptureRequest.EDGE_MODE, Integer.valueOf(1));
            previewRequestBuilder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, Integer.valueOf(1));
            List<Surface> surfaceList = new ArrayList(1);
            surfaceList.add(imageReader.getSurface());
            this.mPreviewRequest = previewRequestBuilder.build();
            try {
                this.mCameraDevice.createCaptureSession(surfaceList, new CrPreviewSessionListener(this.mPreviewRequest), null);
                return true;
            } catch (CameraAccessException e) {
                ex = e;
                Log.m28e(TAG, "createCaptureSession: ", ex);
                return false;
            } catch (IllegalArgumentException e2) {
                ex = e2;
                Log.m28e(TAG, "createCaptureSession: ", ex);
                return false;
            } catch (SecurityException e3) {
                ex = e3;
                Log.m28e(TAG, "createCaptureSession: ", ex);
                return false;
            }
        } catch (CameraAccessException e4) {
            ex = e4;
            Log.m28e(TAG, "createCaptureRequest: ", ex);
            return false;
        } catch (IllegalArgumentException e5) {
            ex = e5;
            Log.m28e(TAG, "createCaptureRequest: ", ex);
            return false;
        } catch (SecurityException e6) {
            ex = e6;
            Log.m28e(TAG, "createCaptureRequest: ", ex);
            return false;
        }
    }

    private static void readImageIntoBuffer(Image image, byte[] data) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        Plane[] planes = image.getPlanes();
        int offset = 0;
        int plane = 0;
        while (plane < planes.length) {
            ByteBuffer buffer = planes[plane].getBuffer();
            int rowStride = planes[plane].getRowStride();
            int pixelStride = planes[plane].getPixelStride();
            int planeWidth = plane == 0 ? imageWidth : imageWidth / 2;
            int planeHeight = plane == 0 ? imageHeight : imageHeight / 2;
            if (pixelStride == 1 && rowStride == planeWidth) {
                buffer.get(data, offset, planeWidth * planeHeight);
                offset += planeWidth * planeHeight;
            } else {
                int col;
                int offset2;
                byte[] rowData = new byte[rowStride];
                int row = 0;
                while (row < planeHeight - 1) {
                    buffer.get(rowData, 0, rowStride);
                    col = 0;
                    offset2 = offset;
                    while (col < planeWidth) {
                        offset = offset2 + 1;
                        data[offset2] = rowData[col * pixelStride];
                        col++;
                        offset2 = offset;
                    }
                    row++;
                    offset = offset2;
                }
                buffer.get(rowData, 0, Math.min(rowStride, buffer.remaining()));
                col = 0;
                offset2 = offset;
                while (col < planeWidth) {
                    offset = offset2 + 1;
                    data[offset2] = rowData[col * pixelStride];
                    col++;
                    offset2 = offset;
                }
                offset = offset2;
            }
            plane++;
        }
    }

    private void changeCameraStateAndNotify(CameraState state) {
        synchronized (this.mCameraStateLock) {
            this.mCameraState = state;
            this.mCameraStateLock.notifyAll();
        }
    }

    static boolean isLegacyDevice(Context appContext, int id) {
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(appContext, id);
        return cameraCharacteristics != null && ((Integer) cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)).intValue() == 2;
    }

    static int getNumberOfCameras(Context appContext) {
        Exception ex;
        int i = 0;
        try {
            return ((CameraManager) appContext.getSystemService("camera")).getCameraIdList().length;
        } catch (CameraAccessException e) {
            ex = e;
        } catch (SecurityException e2) {
            ex = e2;
        }
        Log.m28e(TAG, "getNumberOfCameras: getCameraIdList(): ", ex);
        return i;
    }

    static int getCaptureApiType(int id, Context appContext) {
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(appContext, id);
        if (cameraCharacteristics == null) {
            return 5;
        }
        switch (((Integer) cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)).intValue()) {
            case 0:
                return 3;
            case 1:
                return 2;
            case 2:
                return 1;
            default:
                return 1;
        }
    }

    static String getName(int id, Context appContext) {
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(appContext, id);
        if (cameraCharacteristics == null) {
            return null;
        }
        return "camera2 " + id + ", facing " + (((Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 0 ? "front" : "back");
    }

    static VideoCaptureFormat[] getDeviceSupportedFormats(Context appContext, int id) {
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(appContext, id);
        if (cameraCharacteristics == null) {
            return null;
        }
        boolean minFrameDurationAvailable = false;
        for (int cap : (int[]) cameraCharacteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)) {
            if (cap == 1) {
                minFrameDurationAvailable = true;
                break;
            }
        }
        ArrayList<VideoCaptureFormat> formatList = new ArrayList();
        StreamConfigurationMap streamMap = (StreamConfigurationMap) cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        for (int format : streamMap.getOutputFormats()) {
            Size[] sizes = streamMap.getOutputSizes(format);
            if (sizes != null) {
                for (Size size : sizes) {
                    double minFrameRate;
                    if (minFrameDurationAvailable) {
                        long minFrameDuration = streamMap.getOutputMinFrameDuration(format, size);
                        minFrameRate = minFrameDuration == 0 ? 0.0d : 9.999999999999999E8d * ((double) minFrameDuration);
                    } else {
                        minFrameRate = 0.0d;
                    }
                    formatList.add(new VideoCaptureFormat(size.getWidth(), size.getHeight(), (int) minFrameRate, 0));
                }
            }
        }
        return (VideoCaptureFormat[]) formatList.toArray(new VideoCaptureFormat[formatList.size()]);
    }

    VideoCaptureCamera2(Context context, int id, long nativeVideoCaptureDeviceAndroid) {
        super(context, id, nativeVideoCaptureDeviceAndroid);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean allocate(int r18, int r19, int r20) {
        /*
        r17 = this;
        r12 = "VideoCapture";
        r13 = "allocate: requested (%d x %d) @%dfps";
        r14 = java.lang.Integer.valueOf(r18);
        r15 = java.lang.Integer.valueOf(r19);
        r16 = java.lang.Integer.valueOf(r20);
        org.chromium.base.Log.m23d(r12, r13, r14, r15, r16);
        r0 = r17;
        r13 = r0.mCameraStateLock;
        monitor-enter(r13);
        r0 = r17;
        r12 = r0.mCameraState;	 Catch:{ all -> 0x0054 }
        r14 = org.chromium.media.VideoCaptureCamera2.CameraState.OPENING;	 Catch:{ all -> 0x0054 }
        if (r12 == r14) goto L_0x0028;
    L_0x0020:
        r0 = r17;
        r12 = r0.mCameraState;	 Catch:{ all -> 0x0054 }
        r14 = org.chromium.media.VideoCaptureCamera2.CameraState.CONFIGURING;	 Catch:{ all -> 0x0054 }
        if (r12 != r14) goto L_0x0035;
    L_0x0028:
        r12 = "VideoCapture";
        r14 = "allocate() invoked while Camera is busy opening/configuring.";
        r15 = 0;
        r15 = new java.lang.Object[r15];	 Catch:{ all -> 0x0054 }
        org.chromium.base.Log.m28e(r12, r14, r15);	 Catch:{ all -> 0x0054 }
        r12 = 0;
        monitor-exit(r13);	 Catch:{ all -> 0x0054 }
    L_0x0034:
        return r12;
    L_0x0035:
        monitor-exit(r13);	 Catch:{ all -> 0x0054 }
        r0 = r17;
        r12 = r0.mContext;
        r0 = r17;
        r13 = r0.mId;
        r2 = getCameraCharacteristics(r12, r13);
        r12 = android.hardware.camera2.CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP;
        r10 = r2.get(r12);
        r10 = (android.hardware.camera2.params.StreamConfigurationMap) r10;
        r12 = 35;
        r11 = r10.getOutputSizes(r12);
        if (r11 != 0) goto L_0x0057;
    L_0x0052:
        r12 = 0;
        goto L_0x0034;
    L_0x0054:
        r12 = move-exception;
        monitor-exit(r13);	 Catch:{ all -> 0x0054 }
        throw r12;
    L_0x0057:
        r3 = 0;
        r8 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r1 = r11;
        r7 = r1.length;
        r6 = 0;
    L_0x005e:
        if (r6 >= r7) goto L_0x007f;
    L_0x0060:
        r9 = r1[r6];
        r12 = r9.getWidth();
        r12 = r12 - r18;
        r12 = java.lang.Math.abs(r12);
        r13 = r9.getHeight();
        r13 = r13 - r19;
        r13 = java.lang.Math.abs(r13);
        r4 = r12 + r13;
        if (r4 >= r8) goto L_0x007c;
    L_0x007a:
        r8 = r4;
        r3 = r9;
    L_0x007c:
        r6 = r6 + 1;
        goto L_0x005e;
    L_0x007f:
        r12 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r8 != r12) goto L_0x0090;
    L_0x0084:
        r12 = "VideoCapture";
        r13 = "No supported resolutions.";
        r14 = 0;
        r14 = new java.lang.Object[r14];
        org.chromium.base.Log.m28e(r12, r13, r14);
        r12 = 0;
        goto L_0x0034;
    L_0x0090:
        r12 = "VideoCapture";
        r13 = "allocate: matched (%d x %d)";
        r14 = r3.getWidth();
        r14 = java.lang.Integer.valueOf(r14);
        r15 = r3.getHeight();
        r15 = java.lang.Integer.valueOf(r15);
        org.chromium.base.Log.m22d(r12, r13, r14, r15);
        r12 = new org.chromium.media.VideoCaptureFormat;
        r13 = r3.getWidth();
        r14 = r3.getHeight();
        r15 = 35;
        r0 = r20;
        r12.<init>(r13, r14, r0, r15);
        r0 = r17;
        r0.mCaptureFormat = r12;
        r0 = r17;
        r12 = r0.mCaptureFormat;
        r12 = r12.mWidth;
        r0 = r17;
        r13 = r0.mCaptureFormat;
        r13 = r13.mHeight;
        r12 = r12 * r13;
        r0 = r17;
        r13 = r0.mCaptureFormat;
        r13 = r13.mPixelFormat;
        r13 = android.graphics.ImageFormat.getBitsPerPixel(r13);
        r12 = r12 * r13;
        r5 = r12 / 8;
        r12 = new byte[r5];
        r0 = r17;
        r0.mCapturedData = r12;
        r12 = android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION;
        r12 = r2.get(r12);
        r12 = (java.lang.Integer) r12;
        r12 = r12.intValue();
        r0 = r17;
        r0.mCameraNativeOrientation = r12;
        r12 = android.hardware.camera2.CameraCharacteristics.LENS_FACING;
        r12 = r2.get(r12);
        r12 = (java.lang.Integer) r12;
        r12 = r12.intValue();
        r13 = 1;
        if (r12 != r13) goto L_0x0103;
    L_0x00fb:
        r12 = 1;
    L_0x00fc:
        r0 = r17;
        r0.mInvertDeviceOrientationReadings = r12;
        r12 = 1;
        goto L_0x0034;
    L_0x0103:
        r12 = 0;
        goto L_0x00fc;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.media.VideoCaptureCamera2.allocate(int, int, int):boolean");
    }

    public boolean startCapture() {
        Exception ex;
        Log.m20d(TAG, "startCapture");
        changeCameraStateAndNotify(CameraState.OPENING);
        CameraManager manager = (CameraManager) this.mContext.getSystemService("camera");
        Handler mainHandler = new Handler(this.mContext.getMainLooper());
        try {
            manager.openCamera(Integer.toString(this.mId), new CrStateListener(), mainHandler);
            return true;
        } catch (CameraAccessException e) {
            ex = e;
        } catch (IllegalArgumentException e2) {
            ex = e2;
        } catch (SecurityException e3) {
            ex = e3;
        }
        Log.m28e(TAG, "allocate: manager.openCamera: ", ex);
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean stopCapture() {
        /*
        r8 = this;
        r1 = 1;
        r2 = 0;
        r3 = "VideoCapture";
        r4 = "stopCapture";
        org.chromium.base.Log.m20d(r3, r4);
        r3 = r8.mCameraStateLock;
        monitor-enter(r3);
    L_0x000c:
        r4 = r8.mCameraState;	 Catch:{ all -> 0x002d }
        r5 = org.chromium.media.VideoCaptureCamera2.CameraState.STARTED;	 Catch:{ all -> 0x002d }
        if (r4 == r5) goto L_0x0030;
    L_0x0012:
        r4 = r8.mCameraState;	 Catch:{ all -> 0x002d }
        r5 = org.chromium.media.VideoCaptureCamera2.CameraState.STOPPED;	 Catch:{ all -> 0x002d }
        if (r4 == r5) goto L_0x0030;
    L_0x0018:
        r4 = r8.mCameraStateLock;	 Catch:{ InterruptedException -> 0x001e }
        r4.wait();	 Catch:{ InterruptedException -> 0x001e }
        goto L_0x000c;
    L_0x001e:
        r0 = move-exception;
        r4 = "VideoCapture";
        r5 = "CaptureStartedEvent: ";
        r6 = 1;
        r6 = new java.lang.Object[r6];	 Catch:{ all -> 0x002d }
        r7 = 0;
        r6[r7] = r0;	 Catch:{ all -> 0x002d }
        org.chromium.base.Log.m28e(r4, r5, r6);	 Catch:{ all -> 0x002d }
        goto L_0x000c;
    L_0x002d:
        r1 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x002d }
        throw r1;
    L_0x0030:
        r4 = r8.mCameraState;	 Catch:{ all -> 0x002d }
        r5 = org.chromium.media.VideoCaptureCamera2.CameraState.STOPPED;	 Catch:{ all -> 0x002d }
        if (r4 != r5) goto L_0x0038;
    L_0x0036:
        monitor-exit(r3);	 Catch:{ all -> 0x002d }
    L_0x0037:
        return r1;
    L_0x0038:
        monitor-exit(r3);	 Catch:{ all -> 0x002d }
        r3 = r8.mPreviewSession;	 Catch:{ CameraAccessException -> 0x005d, IllegalStateException -> 0x0044 }
        r3.abortCaptures();	 Catch:{ CameraAccessException -> 0x005d, IllegalStateException -> 0x0044 }
        r3 = r8.mCameraDevice;
        if (r3 != 0) goto L_0x0052;
    L_0x0042:
        r1 = r2;
        goto L_0x0037;
    L_0x0044:
        r0 = move-exception;
    L_0x0045:
        r3 = "VideoCapture";
        r4 = "abortCaptures: ";
        r1 = new java.lang.Object[r1];
        r1[r2] = r0;
        org.chromium.base.Log.m28e(r3, r4, r1);
        r1 = r2;
        goto L_0x0037;
    L_0x0052:
        r2 = r8.mCameraDevice;
        r2.close();
        r2 = org.chromium.media.VideoCaptureCamera2.CameraState.STOPPED;
        r8.changeCameraStateAndNotify(r2);
        goto L_0x0037;
    L_0x005d:
        r0 = move-exception;
        goto L_0x0045;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.media.VideoCaptureCamera2.stopCapture():boolean");
    }

    public PhotoCapabilities getPhotoCapabilities() {
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(this.mContext, this.mId);
        return new PhotoCapabilities(Math.round(((Float) cameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)).floatValue() * 100.0f), 100, (((Rect) this.mPreviewRequest.get(CaptureRequest.SCALER_CROP_REGION)).width() * 100) / ((Rect) cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)).width());
    }

    public boolean takePhoto(long callbackId) {
        Exception ex;
        Log.m20d(TAG, "takePhoto " + callbackId);
        if (this.mCameraDevice == null || this.mCameraState != CameraState.STARTED) {
            return false;
        }
        ImageReader imageReader = ImageReader.newInstance(this.mCaptureFormat.getWidth(), this.mCaptureFormat.getHeight(), 256, 1);
        HandlerThread thread = new HandlerThread("CameraPicture");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());
        imageReader.setOnImageAvailableListener(new CrPhotoReaderListener(callbackId), backgroundHandler);
        List<Surface> surfaceList = new ArrayList(1);
        surfaceList.add(imageReader.getSurface());
        try {
            Builder photoRequestBuilder = this.mCameraDevice.createCaptureRequest(2);
            if (photoRequestBuilder == null) {
                Log.m28e(TAG, "photoRequestBuilder error", new Object[0]);
                return false;
            }
            photoRequestBuilder.addTarget(imageReader.getSurface());
            photoRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, Integer.valueOf(getCameraRotation()));
            try {
                this.mCameraDevice.createCaptureSession(surfaceList, new CrPhotoSessionListener(photoRequestBuilder.build(), callbackId), backgroundHandler);
                return true;
            } catch (CameraAccessException e) {
                ex = e;
                Log.m28e(TAG, "createCaptureSession: " + ex, new Object[0]);
                return false;
            } catch (IllegalArgumentException e2) {
                ex = e2;
                Log.m28e(TAG, "createCaptureSession: " + ex, new Object[0]);
                return false;
            } catch (SecurityException e3) {
                ex = e3;
                Log.m28e(TAG, "createCaptureSession: " + ex, new Object[0]);
                return false;
            }
        } catch (CameraAccessException e4) {
            Log.m28e(TAG, "mCameraDevice.createCaptureRequest() error", new Object[0]);
            return false;
        }
    }

    public void deallocate() {
        Log.m20d(TAG, "deallocate");
    }
}
