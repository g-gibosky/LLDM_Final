package org.chromium.media;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;

public class VideoCaptureAndroid extends VideoCaptureCamera {
    private static final int NUM_CAPTURE_BUFFERS = 3;
    private static final String TAG = "cr.media";
    private int mExpectedFrameSize;

    private static class BuggyDeviceHack {
        private static final String[] COLORSPACE_BUGGY_DEVICE_LIST = new String[]{"SAMSUNG-SGH-I747", "ODROID-U2", "XT1092", "XT1095", "XT1096", "XT1097"};

        private BuggyDeviceHack() {
        }

        static int getImageFormat() {
            for (String buggyDevice : COLORSPACE_BUGGY_DEVICE_LIST) {
                if (buggyDevice.contentEquals(Build.MODEL)) {
                    return 17;
                }
            }
            return AndroidImageFormat.YV12;
        }
    }

    static int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

    static int getCaptureApiType(int id) {
        if (VideoCaptureCamera.getCameraInfo(id) == null) {
            return 5;
        }
        return 0;
    }

    static String getName(int id) {
        CameraInfo cameraInfo = VideoCaptureCamera.getCameraInfo(id);
        if (cameraInfo == null) {
            return null;
        }
        return "camera " + id + ", facing " + (cameraInfo.facing == 1 ? "front" : "back");
    }

    static org.chromium.media.VideoCaptureFormat[] getDeviceSupportedFormats(int r22) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.ModVisitor.makeFilledArrayInsn(ModVisitor.java:363)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:132)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r4 = android.hardware.Camera.open(r22);	 Catch:{ RuntimeException -> 0x000d }
        r12 = org.chromium.media.VideoCaptureCamera.getCameraParameters(r4);
        if (r12 != 0) goto L_0x0024;
    L_0x000a:
        r18 = 0;
    L_0x000c:
        return r18;
    L_0x000d:
        r5 = move-exception;
        r18 = "cr.media";
        r19 = "Camera.open: ";
        r20 = 1;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r21 = 0;
        r20[r21] = r5;
        org.chromium.base.Log.m28e(r18, r19, r20);
        r18 = 0;
        goto L_0x000c;
    L_0x0024:
        r6 = new java.util.ArrayList;
        r6.<init>();
        r14 = r12.getSupportedPreviewFormats();
        if (r14 != 0) goto L_0x0034;
    L_0x002f:
        r14 = new java.util.ArrayList;
        r14.<init>();
    L_0x0034:
        r18 = r14.size();
        if (r18 != 0) goto L_0x0045;
    L_0x003a:
        r18 = 0;
        r18 = java.lang.Integer.valueOf(r18);
        r0 = r18;
        r14.add(r0);
    L_0x0045:
        r8 = r14.iterator();
    L_0x0049:
        r18 = r8.hasNext();
        if (r18 == 0) goto L_0x010a;
    L_0x004f:
        r15 = r8.next();
        r15 = (java.lang.Integer) r15;
        r13 = 0;
        r18 = r15.intValue();
        r19 = 842094169; // 0x32315659 float:1.0322389E-8 double:4.160497995E-315;
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x00fc;
    L_0x0063:
        r13 = 842094169; // 0x32315659 float:1.0322389E-8 double:4.160497995E-315;
    L_0x0066:
        r11 = r12.getSupportedPreviewFpsRange();
        if (r11 != 0) goto L_0x0071;
    L_0x006c:
        r11 = new java.util.ArrayList;
        r11.<init>();
    L_0x0071:
        r18 = r11.size();
        if (r18 != 0) goto L_0x0087;
    L_0x0077:
        r18 = 2;
        r0 = r18;
        r0 = new int[r0];
        r18 = r0;
        r18 = {0, 0};
        r0 = r18;
        r11.add(r0);
    L_0x0087:
        r9 = r11.iterator();
    L_0x008b:
        r18 = r9.hasNext();
        if (r18 == 0) goto L_0x0049;
    L_0x0091:
        r7 = r9.next();
        r7 = (int[]) r7;
        r17 = r12.getSupportedPreviewSizes();
        if (r17 != 0) goto L_0x00a2;
    L_0x009d:
        r17 = new java.util.ArrayList;
        r17.<init>();
    L_0x00a2:
        r18 = r17.size();
        if (r18 != 0) goto L_0x00bd;
    L_0x00a8:
        r18 = new android.hardware.Camera$Size;
        r4.getClass();
        r19 = 0;
        r20 = 0;
        r0 = r18;
        r1 = r19;
        r2 = r20;
        r0.<init>(r4, r1, r2);
        r17.add(r18);
    L_0x00bd:
        r10 = r17.iterator();
    L_0x00c1:
        r18 = r10.hasNext();
        if (r18 == 0) goto L_0x008b;
    L_0x00c7:
        r16 = r10.next();
        r16 = (android.hardware.Camera.Size) r16;
        r18 = new org.chromium.media.VideoCaptureFormat;
        r0 = r16;
        r0 = r0.width;
        r19 = r0;
        r0 = r16;
        r0 = r0.height;
        r20 = r0;
        r21 = 1;
        r21 = r7[r21];
        r0 = r21;
        r0 = r0 + 999;
        r21 = r0;
        r0 = r21;
        r0 = r0 / 1000;
        r21 = r0;
        r0 = r18;
        r1 = r19;
        r2 = r20;
        r3 = r21;
        r0.<init>(r1, r2, r3, r13);
        r0 = r18;
        r6.add(r0);
        goto L_0x00c1;
    L_0x00fc:
        r18 = r15.intValue();
        r19 = 17;
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x0066;
    L_0x0108:
        goto L_0x0049;
    L_0x010a:
        r4.release();
        r18 = r6.size();
        r0 = r18;
        r0 = new org.chromium.media.VideoCaptureFormat[r0];
        r18 = r0;
        r0 = r18;
        r18 = r6.toArray(r0);
        r18 = (org.chromium.media.VideoCaptureFormat[]) r18;
        goto L_0x000c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.media.VideoCaptureAndroid.getDeviceSupportedFormats(int):org.chromium.media.VideoCaptureFormat[]");
    }

    VideoCaptureAndroid(Context context, int id, long nativeVideoCaptureDeviceAndroid) {
        super(context, id, nativeVideoCaptureDeviceAndroid);
    }

    protected void setCaptureParameters(int width, int height, int frameRate, Parameters cameraParameters) {
        this.mCaptureFormat = new VideoCaptureFormat(width, height, frameRate, BuggyDeviceHack.getImageFormat());
    }

    protected void allocateBuffers() {
        this.mExpectedFrameSize = ((this.mCaptureFormat.mWidth * this.mCaptureFormat.mHeight) * ImageFormat.getBitsPerPixel(this.mCaptureFormat.mPixelFormat)) / 8;
        for (int i = 0; i < 3; i++) {
            this.mCamera.addCallbackBuffer(new byte[this.mExpectedFrameSize]);
        }
    }

    protected void setPreviewCallback(PreviewCallback cb) {
        this.mCamera.setPreviewCallbackWithBuffer(cb);
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        this.mPreviewBufferLock.lock();
        try {
            if (this.mIsRunning) {
                if (data.length == this.mExpectedFrameSize) {
                    nativeOnFrameAvailable(this.mNativeVideoCaptureDeviceAndroid, data, this.mExpectedFrameSize, getCameraRotation());
                }
                this.mPreviewBufferLock.unlock();
                if (camera != null) {
                    camera.addCallbackBuffer(data);
                }
            }
        } finally {
            this.mPreviewBufferLock.unlock();
            if (camera != null) {
                camera.addCallbackBuffer(data);
            }
        }
    }
}
