package org.chromium.media;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import org.chromium.base.Log;

public class VideoCaptureTango extends VideoCaptureCamera {
    private static final CamParams[] CAM_PARAMS = new CamParams[]{new CamParams(0, "depth", 320, SF_LINES_FISHEYE), new CamParams(1, "fisheye", 640, 480), new CamParams(2, "4MP", SF_WIDTH, SF_LINES_BIGIMAGE)};
    private static final byte CHROMA_ZERO_LEVEL = Byte.MAX_VALUE;
    private static final int DEPTH_CAMERA_ID = 0;
    private static final int FISHEYE_CAMERA_ID = 1;
    private static final int FOURMP_CAMERA_ID = 2;
    private static final int SF_FULL_HEIGHT = 1752;
    private static final int SF_HEIGHT = 1168;
    private static final int SF_LINES_BIGIMAGE = 720;
    private static final int SF_LINES_DEPTH = 60;
    private static final int SF_LINES_DEPTH_PADDED = 112;
    private static final int SF_LINES_FISHEYE = 240;
    private static final int SF_LINES_HEADER = 16;
    private static final int SF_LINES_RESERVED = 80;
    private static final int SF_OFFSET_4MP_CHROMA = 112;
    private static final int SF_WIDTH = 1280;
    private static final String TAG = "cr.media";
    private ByteBuffer mFrameBuffer = null;
    private final int mTangoCameraId;

    private static class CamParams {
        final int mHeight;
        final int mId;
        final String mName;
        final int mWidth;

        CamParams(int id, String name, int width, int height) {
            this.mId = id;
            this.mName = name;
            this.mWidth = width;
            this.mHeight = height;
        }
    }

    static int numberOfCameras() {
        return CAM_PARAMS.length;
    }

    static int getCaptureApiType(int index) {
        if (index >= CAM_PARAMS.length) {
            return 0;
        }
        return 4;
    }

    static String getName(int index) {
        if (index >= CAM_PARAMS.length) {
            return "";
        }
        return CAM_PARAMS[index].mName;
    }

    static VideoCaptureFormat[] getDeviceSupportedFormats(int id) {
        ArrayList<VideoCaptureFormat> formatList = new ArrayList();
        if (id == 0) {
            formatList.add(new VideoCaptureFormat(320, 180, 5, AndroidImageFormat.YV12));
        } else if (id == 1) {
            formatList.add(new VideoCaptureFormat(640, 480, 30, AndroidImageFormat.YV12));
        } else if (id == 2) {
            formatList.add(new VideoCaptureFormat(SF_WIDTH, SF_LINES_BIGIMAGE, 20, AndroidImageFormat.YV12));
        }
        return (VideoCaptureFormat[]) formatList.toArray(new VideoCaptureFormat[formatList.size()]);
    }

    VideoCaptureTango(Context context, int id, long nativeVideoCaptureDeviceAndroid) {
        super(context, 0, nativeVideoCaptureDeviceAndroid);
        this.mTangoCameraId = id;
    }

    protected void setCaptureParameters(int width, int height, int frameRate, Parameters cameraParameters) {
        this.mCaptureFormat = new VideoCaptureFormat(CAM_PARAMS[this.mTangoCameraId].mWidth, CAM_PARAMS[this.mTangoCameraId].mHeight, frameRate, AndroidImageFormat.YV12);
        cameraParameters.set("sf-mode", "all");
    }

    protected void allocateBuffers() {
        this.mFrameBuffer = ByteBuffer.allocateDirect(((this.mCaptureFormat.mWidth * this.mCaptureFormat.mHeight) * 3) / 2);
        Arrays.fill(this.mFrameBuffer.array(), CHROMA_ZERO_LEVEL);
    }

    protected void setPreviewCallback(PreviewCallback cb) {
        this.mCamera.setPreviewCallback(cb);
    }

    public void onPreviewFrame(byte[] data, Camera camera) {
        this.mPreviewBufferLock.lock();
        try {
            if (this.mIsRunning) {
                if (data.length == 2242560) {
                    if (this.mTangoCameraId == 0) {
                        int j;
                        for (j = 430080; j < 583680; j += 2) {
                            this.mFrameBuffer.put((byte) ((data[j + 1] << 4) | ((data[j] & SF_LINES_FISHEYE) >> 4)));
                        }
                        for (j = 0; j < (this.mCaptureFormat.mWidth * this.mCaptureFormat.mHeight) - 76800; j++) {
                            this.mFrameBuffer.put((byte) 0);
                        }
                    } else if (this.mTangoCameraId == 1) {
                        ByteBuffer.wrap(data, 20480, 307200).get(this.mFrameBuffer.array(), 0, 307200);
                    } else if (this.mTangoCameraId == 2) {
                        ByteBuffer.wrap(data, 573440, 921600).get(this.mFrameBuffer.array(), 0, 921600);
                        ByteBuffer.wrap(data, 1638400, 230400).get(this.mFrameBuffer.array(), 921600, 230400);
                        ByteBuffer.wrap(data, 2012160, 230400).get(this.mFrameBuffer.array(), 1152000, 230400);
                    } else {
                        Log.m28e(TAG, "Unknown camera, #id: %d", Integer.valueOf(this.mTangoCameraId));
                        this.mPreviewBufferLock.unlock();
                        return;
                    }
                    this.mFrameBuffer.rewind();
                    nativeOnFrameAvailable(this.mNativeVideoCaptureDeviceAndroid, this.mFrameBuffer.array(), this.mFrameBuffer.capacity(), getCameraRotation());
                }
                this.mPreviewBufferLock.unlock();
            }
        } finally {
            this.mPreviewBufferLock.unlock();
        }
    }
}
