package org.chromium.media;

import android.annotation.TargetApi;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CryptoException;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.view.PointerIconCompat;
import android.view.Surface;
import java.nio.ByteBuffer;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.MainDex;
import org.chromium.media.MediaCodecUtil.CodecCreationInfo;
import org.chromium.media.MediaCodecUtil.MimeTypes;

@JNINamespace("media")
class MediaCodecBridge {
    static final /* synthetic */ boolean $assertionsDisabled = (!MediaCodecBridge.class.desiredAssertionStatus());
    private static final String KEY_CROP_BOTTOM = "crop-bottom";
    private static final String KEY_CROP_LEFT = "crop-left";
    private static final String KEY_CROP_RIGHT = "crop-right";
    private static final String KEY_CROP_TOP = "crop-top";
    private static final long MAX_PRESENTATION_TIMESTAMP_SHIFT_US = 100000;
    private static final int MEDIA_CODEC_ABORT = 8;
    private static final int MEDIA_CODEC_DEQUEUE_INPUT_AGAIN_LATER = 1;
    private static final int MEDIA_CODEC_DEQUEUE_OUTPUT_AGAIN_LATER = 2;
    private static final int MEDIA_CODEC_ERROR = 9;
    private static final int MEDIA_CODEC_INPUT_END_OF_STREAM = 5;
    private static final int MEDIA_CODEC_NO_KEY = 7;
    private static final int MEDIA_CODEC_OK = 0;
    private static final int MEDIA_CODEC_OUTPUT_BUFFERS_CHANGED = 3;
    private static final int MEDIA_CODEC_OUTPUT_END_OF_STREAM = 6;
    private static final int MEDIA_CODEC_OUTPUT_FORMAT_CHANGED = 4;
    private static final int PCM16_BYTES_PER_SAMPLE = 2;
    private static final String TAG = "cr_media";
    private boolean mAdaptivePlaybackSupported;
    private AudioTrack mAudioTrack;
    private boolean mFlushed;
    private ByteBuffer[] mInputBuffers;
    private long mLastPresentationTimeUs;
    private MediaCodec mMediaCodec;
    private String mMime;
    private ByteBuffer[] mOutputBuffers;
    private byte[] mPendingAudioBuffer;

    @MainDex
    private static class DequeueInputResult {
        private final int mIndex;
        private final int mStatus;

        private DequeueInputResult(int status, int index) {
            this.mStatus = status;
            this.mIndex = index;
        }

        @CalledByNative("DequeueInputResult")
        private int status() {
            return this.mStatus;
        }

        @CalledByNative("DequeueInputResult")
        private int index() {
            return this.mIndex;
        }
    }

    @MainDex
    private static class DequeueOutputResult {
        private final int mFlags;
        private final int mIndex;
        private final int mNumBytes;
        private final int mOffset;
        private final long mPresentationTimeMicroseconds;
        private final int mStatus;

        private DequeueOutputResult(int status, int index, int flags, int offset, long presentationTimeMicroseconds, int numBytes) {
            this.mStatus = status;
            this.mIndex = index;
            this.mFlags = flags;
            this.mOffset = offset;
            this.mPresentationTimeMicroseconds = presentationTimeMicroseconds;
            this.mNumBytes = numBytes;
        }

        @CalledByNative("DequeueOutputResult")
        private int status() {
            return this.mStatus;
        }

        @CalledByNative("DequeueOutputResult")
        private int index() {
            return this.mIndex;
        }

        @CalledByNative("DequeueOutputResult")
        private int flags() {
            return this.mFlags;
        }

        @CalledByNative("DequeueOutputResult")
        private int offset() {
            return this.mOffset;
        }

        @CalledByNative("DequeueOutputResult")
        private long presentationTimeMicroseconds() {
            return this.mPresentationTimeMicroseconds;
        }

        @CalledByNative("DequeueOutputResult")
        private int numBytes() {
            return this.mNumBytes;
        }
    }

    @MainDex
    private static class GetOutputFormatResult {
        private final MediaFormat mFormat;
        private final int mStatus;

        private GetOutputFormatResult(int status, MediaFormat format) {
            this.mStatus = status;
            this.mFormat = format;
        }

        private boolean formatHasCropValues() {
            return this.mFormat.containsKey(MediaCodecBridge.KEY_CROP_RIGHT) && this.mFormat.containsKey(MediaCodecBridge.KEY_CROP_LEFT) && this.mFormat.containsKey(MediaCodecBridge.KEY_CROP_BOTTOM) && this.mFormat.containsKey(MediaCodecBridge.KEY_CROP_TOP);
        }

        @CalledByNative("GetOutputFormatResult")
        private int status() {
            return this.mStatus;
        }

        @CalledByNative("GetOutputFormatResult")
        private int width() {
            return formatHasCropValues() ? (this.mFormat.getInteger(MediaCodecBridge.KEY_CROP_RIGHT) - this.mFormat.getInteger(MediaCodecBridge.KEY_CROP_LEFT)) + 1 : this.mFormat.getInteger("width");
        }

        @CalledByNative("GetOutputFormatResult")
        private int height() {
            return formatHasCropValues() ? (this.mFormat.getInteger(MediaCodecBridge.KEY_CROP_BOTTOM) - this.mFormat.getInteger(MediaCodecBridge.KEY_CROP_TOP)) + 1 : this.mFormat.getInteger("height");
        }

        @CalledByNative("GetOutputFormatResult")
        private int sampleRate() {
            return this.mFormat.getInteger("sample-rate");
        }

        @CalledByNative("GetOutputFormatResult")
        private int channelCount() {
            return this.mFormat.getInteger("channel-count");
        }
    }

    private MediaCodecBridge(MediaCodec mediaCodec, String mime, boolean adaptivePlaybackSupported) {
        if ($assertionsDisabled || mediaCodec != null) {
            this.mMediaCodec = mediaCodec;
            this.mPendingAudioBuffer = null;
            this.mMime = mime;
            this.mLastPresentationTimeUs = 0;
            this.mFlushed = true;
            this.mAdaptivePlaybackSupported = adaptivePlaybackSupported;
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private static MediaCodecBridge create(String mime, boolean isSecure, int direction, boolean requireSoftwareCodec) {
        CodecCreationInfo info = new CodecCreationInfo();
        if (direction == 1) {
            try {
                info.mediaCodec = MediaCodec.createEncoderByType(mime);
                info.supportsAdaptivePlayback = false;
            } catch (Exception e) {
                Log.m28e(TAG, "Failed to create MediaCodec: %s, isSecure: %s, direction: %d", mime, Boolean.valueOf(isSecure), Integer.valueOf(direction), e);
            }
        } else {
            info = MediaCodecUtil.createDecoder(mime, isSecure, requireSoftwareCodec);
        }
        if (info.mediaCodec == null) {
            return null;
        }
        return new MediaCodecBridge(info.mediaCodec, mime, info.supportsAdaptivePlayback);
    }

    @CalledByNative
    private void release() {
        try {
            String codecName = EnvironmentCompat.MEDIA_UNKNOWN;
            if (VERSION.SDK_INT >= 18) {
                codecName = this.mMediaCodec.getName();
            }
            Log.m38w(TAG, "calling MediaCodec.release() on " + codecName, new Object[0]);
            this.mMediaCodec.release();
        } catch (IllegalStateException e) {
            Log.m28e(TAG, "Cannot release media codec", e);
        }
        this.mMediaCodec = null;
        if (this.mAudioTrack != null) {
            this.mAudioTrack.release();
        }
        this.mPendingAudioBuffer = null;
    }

    @CalledByNative
    private boolean start() {
        try {
            this.mMediaCodec.start();
            if (VERSION.SDK_INT > 19) {
                return true;
            }
            this.mInputBuffers = this.mMediaCodec.getInputBuffers();
            this.mOutputBuffers = this.mMediaCodec.getOutputBuffers();
            return true;
        } catch (IllegalStateException e) {
            Log.m28e(TAG, "Cannot start the media codec", e);
            return false;
        } catch (IllegalArgumentException e2) {
            Log.m28e(TAG, "Cannot start the media codec", e2);
            return false;
        }
    }

    @CalledByNative
    private DequeueInputResult dequeueInputBuffer(long timeoutUs) {
        int status = 9;
        int index = -1;
        try {
            int indexOrStatus = this.mMediaCodec.dequeueInputBuffer(timeoutUs);
            if (indexOrStatus >= 0) {
                status = 0;
                index = indexOrStatus;
            } else if (indexOrStatus == -1) {
                status = 1;
            } else {
                Log.m28e(TAG, "Unexpected index_or_status: " + indexOrStatus, new Object[0]);
                if (!$assertionsDisabled) {
                    throw new AssertionError();
                }
            }
        } catch (Exception e) {
            Log.m28e(TAG, "Failed to dequeue input buffer", e);
        }
        return new DequeueInputResult(status, index);
    }

    @CalledByNative
    private int flush() {
        try {
            this.mFlushed = true;
            if (this.mAudioTrack != null) {
                this.mAudioTrack.pause();
                this.mAudioTrack.flush();
                this.mPendingAudioBuffer = null;
            }
            this.mMediaCodec.flush();
            return 0;
        } catch (IllegalStateException e) {
            Log.m28e(TAG, "Failed to flush MediaCodec", e);
            return 9;
        }
    }

    @CalledByNative
    private void stop() {
        this.mMediaCodec.stop();
        if (this.mAudioTrack != null) {
            this.mAudioTrack.pause();
        }
    }

    @CalledByNative
    private GetOutputFormatResult getOutputFormat() {
        MediaFormat format = null;
        int status = 0;
        try {
            format = this.mMediaCodec.getOutputFormat();
        } catch (IllegalStateException e) {
            Log.m28e(TAG, "Failed to get output format", e);
            status = 9;
        }
        return new GetOutputFormatResult(status, format);
    }

    @CalledByNative
    private ByteBuffer getInputBuffer(int index) {
        if (VERSION.SDK_INT <= 19) {
            return this.mInputBuffers[index];
        }
        try {
            return this.mMediaCodec.getInputBuffer(index);
        } catch (IllegalStateException e) {
            Log.m28e(TAG, "Failed to get input buffer", e);
            return null;
        }
    }

    @CalledByNative
    private ByteBuffer getOutputBuffer(int index) {
        if (VERSION.SDK_INT <= 19) {
            return this.mOutputBuffers[index];
        }
        try {
            return this.mMediaCodec.getOutputBuffer(index);
        } catch (IllegalStateException e) {
            Log.m28e(TAG, "Failed to get output buffer", e);
            return null;
        }
    }

    @CalledByNative
    private int queueInputBuffer(int index, int offset, int size, long presentationTimeUs, int flags) {
        resetLastPresentationTimeIfNeeded(presentationTimeUs);
        try {
            this.mMediaCodec.queueInputBuffer(index, offset, size, presentationTimeUs, flags);
            return 0;
        } catch (Exception e) {
            Log.m28e(TAG, "Failed to queue input buffer", e);
            return 9;
        }
    }

    @TargetApi(19)
    @CalledByNative
    private void setVideoBitrate(int bps) {
        Bundle b = new Bundle();
        b.putInt("video-bitrate", bps);
        this.mMediaCodec.setParameters(b);
    }

    @TargetApi(19)
    @CalledByNative
    private void requestKeyFrameSoon() {
        Bundle b = new Bundle();
        b.putInt("request-sync", 0);
        this.mMediaCodec.setParameters(b);
    }

    @CalledByNative
    private int queueSecureInputBuffer(int index, int offset, byte[] iv, byte[] keyId, int[] numBytesOfClearData, int[] numBytesOfEncryptedData, int numSubSamples, long presentationTimeUs) {
        resetLastPresentationTimeIfNeeded(presentationTimeUs);
        try {
            CryptoInfo cryptoInfo = new CryptoInfo();
            cryptoInfo.set(numSubSamples, numBytesOfClearData, numBytesOfEncryptedData, keyId, iv, 1);
            this.mMediaCodec.queueSecureInputBuffer(index, offset, cryptoInfo, presentationTimeUs, 0);
            return 0;
        } catch (CryptoException e) {
            if (e.getErrorCode() == 1) {
                Log.m20d(TAG, "Failed to queue secure input buffer: CryptoException.ERROR_NO_KEY");
                return 7;
            }
            Log.m28e(TAG, "Failed to queue secure input buffer, CryptoException with error code " + e.getErrorCode(), new Object[0]);
            return 9;
        } catch (IllegalStateException e2) {
            Log.m28e(TAG, "Failed to queue secure input buffer, IllegalStateException " + e2, new Object[0]);
            return 9;
        }
    }

    @CalledByNative
    private void releaseOutputBuffer(int index, boolean render) {
        try {
            this.mMediaCodec.releaseOutputBuffer(index, render);
        } catch (IllegalStateException e) {
            Log.m28e(TAG, "Failed to release output buffer", e);
        }
    }

    @CalledByNative
    private DequeueOutputResult dequeueOutputBuffer(long timeoutUs) {
        BufferInfo info = new BufferInfo();
        int status = 9;
        int index = -1;
        try {
            int indexOrStatus = this.mMediaCodec.dequeueOutputBuffer(info, timeoutUs);
            if (info.presentationTimeUs < this.mLastPresentationTimeUs) {
                info.presentationTimeUs = this.mLastPresentationTimeUs;
            }
            this.mLastPresentationTimeUs = info.presentationTimeUs;
            if (indexOrStatus >= 0) {
                status = 0;
                index = indexOrStatus;
            } else if (indexOrStatus == -3) {
                if ($assertionsDisabled || VERSION.SDK_INT <= 19) {
                    this.mOutputBuffers = this.mMediaCodec.getOutputBuffers();
                    status = 3;
                } else {
                    throw new AssertionError();
                }
            } else if (indexOrStatus == -2) {
                status = 4;
                MediaFormat newFormat = this.mMediaCodec.getOutputFormat();
                if (this.mAudioTrack != null && newFormat.containsKey("sample-rate")) {
                    if (this.mAudioTrack.setPlaybackRate(newFormat.getInteger("sample-rate")) != 0) {
                        status = 9;
                    }
                }
            } else if (indexOrStatus == -1) {
                status = 2;
            } else {
                Log.m28e(TAG, "Unexpected index_or_status: " + indexOrStatus, new Object[0]);
                if (!$assertionsDisabled) {
                    throw new AssertionError();
                }
            }
        } catch (IllegalStateException e) {
            status = 9;
            Log.m28e(TAG, "Failed to dequeue output buffer", e);
        }
        return new DequeueOutputResult(status, index, info.flags, info.offset, info.presentationTimeUs, info.size);
    }

    @CalledByNative
    private boolean configureVideo(MediaFormat format, Surface surface, MediaCrypto crypto, int flags, boolean allowAdaptivePlayback) {
        if (!allowAdaptivePlayback) {
            try {
                this.mAdaptivePlaybackSupported = false;
            } catch (IllegalArgumentException e) {
                Log.m28e(TAG, "Cannot configure the video codec, wrong format or surface", e);
                return false;
            } catch (IllegalStateException e2) {
                Log.m28e(TAG, "Cannot configure the video codec", e2);
                return false;
            } catch (CryptoException e3) {
                Log.m28e(TAG, "Cannot configure the video codec: DRM error", e3);
                return false;
            } catch (Exception e4) {
                Log.m28e(TAG, "Cannot configure the video codec", e4);
                return false;
            }
        }
        if (this.mAdaptivePlaybackSupported) {
            format.setInteger("max-width", format.getInteger("width"));
            format.setInteger("max-height", format.getInteger("height"));
        }
        maybeSetMaxInputSize(format);
        this.mMediaCodec.configure(format, surface, crypto, flags);
        return true;
    }

    @CalledByNative
    private static MediaFormat createAudioFormat(String mime, int sampleRate, int channelCount) {
        return MediaFormat.createAudioFormat(mime, sampleRate, channelCount);
    }

    @CalledByNative
    private static MediaFormat createVideoDecoderFormat(String mime, int width, int height) {
        return MediaFormat.createVideoFormat(mime, width, height);
    }

    private void maybeSetMaxInputSize(MediaFormat format) {
        if (!format.containsKey("max-input-size")) {
            int maxPixels;
            int minCompressionRatio;
            int maxHeight = format.getInteger("height");
            if (this.mAdaptivePlaybackSupported && format.containsKey("max-height")) {
                maxHeight = Math.max(maxHeight, format.getInteger("max-height"));
            }
            int maxWidth = format.getInteger("width");
            if (this.mAdaptivePlaybackSupported && format.containsKey("max-width")) {
                maxWidth = Math.max(maxHeight, format.getInteger("max-width"));
            }
            String string = format.getString("mime");
            Object obj = -1;
            switch (string.hashCode()) {
                case -1662541442:
                    if (string.equals(MimeTypes.VIDEO_H265)) {
                        obj = 2;
                        break;
                    }
                    break;
                case 1331836730:
                    if (string.equals(MimeTypes.VIDEO_H264)) {
                        obj = null;
                        break;
                    }
                    break;
                case 1599127256:
                    if (string.equals(MimeTypes.VIDEO_VP8)) {
                        obj = 1;
                        break;
                    }
                    break;
                case 1599127257:
                    if (string.equals(MimeTypes.VIDEO_VP9)) {
                        obj = 3;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case null:
                    if (!"BRAVIA 4K 2015".equals(Build.MODEL)) {
                        maxPixels = ((((maxWidth + 15) / 16) * ((maxHeight + 15) / 16)) * 16) * 16;
                        minCompressionRatio = 2;
                        break;
                    }
                    return;
                case 1:
                    maxPixels = maxWidth * maxHeight;
                    minCompressionRatio = 2;
                    break;
                case 2:
                case 3:
                    maxPixels = maxWidth * maxHeight;
                    minCompressionRatio = 4;
                    break;
                default:
                    return;
            }
            format.setInteger("max-input-size", (maxPixels * 3) / (minCompressionRatio * 2));
        }
    }

    @CalledByNative
    private static MediaFormat createVideoEncoderFormat(String mime, int width, int height, int bitRate, int frameRate, int iFrameInterval, int colorFormat) {
        MediaFormat format = MediaFormat.createVideoFormat(mime, width, height);
        format.setInteger("bitrate", bitRate);
        format.setInteger("frame-rate", frameRate);
        format.setInteger("i-frame-interval", iFrameInterval);
        format.setInteger("color-format", colorFormat);
        return format;
    }

    @CalledByNative
    private boolean isAdaptivePlaybackSupported(int width, int height) {
        return this.mAdaptivePlaybackSupported;
    }

    @CalledByNative
    private static void setCodecSpecificData(MediaFormat format, int index, byte[] bytes) {
        String name;
        switch (index) {
            case 0:
                name = "csd-0";
                break;
            case 1:
                name = "csd-1";
                break;
            case 2:
                name = "csd-2";
                break;
            default:
                name = null;
                break;
        }
        if (name != null) {
            format.setByteBuffer(name, ByteBuffer.wrap(bytes));
        }
    }

    @CalledByNative
    private static void setFrameHasADTSHeader(MediaFormat format) {
        format.setInteger("is-adts", 1);
    }

    @CalledByNative
    private boolean configureAudio(MediaFormat format, MediaCrypto crypto, int flags, boolean playAudio) {
        try {
            this.mMediaCodec.configure(format, null, crypto, flags);
            if (!playAudio || createAudioTrack(format.getInteger("sample-rate"), format.getInteger("channel-count"))) {
                return true;
            }
            return false;
        } catch (IllegalArgumentException e) {
            Log.m28e(TAG, "Cannot configure the audio codec", e);
            return false;
        } catch (IllegalStateException e2) {
            Log.m28e(TAG, "Cannot configure the audio codec", e2);
            return false;
        } catch (CryptoException e3) {
            Log.m28e(TAG, "Cannot configure the audio codec: DRM error", e3);
            return false;
        } catch (Exception e4) {
            Log.m28e(TAG, "Cannot configure the audio codec", e4);
            return false;
        }
    }

    @CalledByNative
    private boolean createAudioTrack(int sampleRate, int channelCount) {
        Log.m30v(TAG, "createAudioTrack: sampleRate:" + sampleRate + " channelCount:" + channelCount);
        int channelConfig = getAudioFormat(channelCount);
        int bufferSize = (((int) (1.5d * ((double) ((AudioTrack.getMinBufferSize(sampleRate, channelConfig, 2) / 2) / channelCount)))) * 2) * channelCount;
        if (this.mAudioTrack != null) {
            this.mAudioTrack.release();
        }
        this.mAudioTrack = new AudioTrack(3, sampleRate, channelConfig, 2, bufferSize, 1);
        if (this.mAudioTrack.getState() != 0) {
            return true;
        }
        Log.m28e(TAG, "Cannot create AudioTrack", new Object[0]);
        this.mAudioTrack = null;
        return false;
    }

    @CalledByNative
    private long playOutputBuffer(byte[] buf, boolean postpone) {
        if (this.mAudioTrack == null) {
            return 0;
        }
        if (!postpone) {
            int size;
            if (3 != this.mAudioTrack.getPlayState()) {
                this.mAudioTrack.play();
            }
            if (this.mPendingAudioBuffer != null) {
                size = this.mAudioTrack.write(this.mPendingAudioBuffer, 0, this.mPendingAudioBuffer.length);
                if (this.mPendingAudioBuffer.length != size) {
                    Log.m29i(TAG, "Failed to send all data to audio output, expected size: " + this.mPendingAudioBuffer.length + ", actual size: " + size, new Object[0]);
                }
                this.mPendingAudioBuffer = null;
            }
            size = this.mAudioTrack.write(buf, 0, buf.length);
            if (buf.length != size) {
                Log.m29i(TAG, "Failed to send all data to audio output, expected size: " + buf.length + ", actual size: " + size, new Object[0]);
            }
            return 4294967295L & ((long) this.mAudioTrack.getPlaybackHeadPosition());
        } else if ($assertionsDisabled || this.mPendingAudioBuffer == null) {
            this.mPendingAudioBuffer = buf;
            return 0;
        } else {
            throw new AssertionError();
        }
    }

    @CalledByNative
    private void setVolume(double volume) {
        if (this.mAudioTrack != null) {
            this.mAudioTrack.setStereoVolume((float) volume, (float) volume);
        }
    }

    private void resetLastPresentationTimeIfNeeded(long presentationTimeUs) {
        if (this.mFlushed) {
            this.mLastPresentationTimeUs = Math.max(presentationTimeUs - MAX_PRESENTATION_TIMESTAMP_SHIFT_US, 0);
            this.mFlushed = false;
        }
    }

    private int getAudioFormat(int channelCount) {
        switch (channelCount) {
            case 1:
                return 4;
            case 2:
                return 12;
            case 4:
                return 204;
            case 6:
                return 252;
            case 8:
                if (VERSION.SDK_INT >= 23) {
                    return 6396;
                }
                return PointerIconCompat.TYPE_GRAB;
            default:
                return 1;
        }
    }
}
