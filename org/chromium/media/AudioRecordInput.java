package org.chromium.media;

import android.annotation.SuppressLint;
import android.media.AudioRecord;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Process;
import java.nio.ByteBuffer;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("media")
class AudioRecordInput {
    private static final boolean DEBUG = false;
    private static final int HARDWARE_DELAY_MS = 100;
    private static final String TAG = "cr.media";
    private AcousticEchoCanceler mAEC;
    private AudioRecord mAudioRecord;
    private AudioRecordThread mAudioRecordThread;
    private final int mBitsPerSample;
    private ByteBuffer mBuffer;
    private final int mChannels;
    private final int mHardwareDelayBytes;
    private final long mNativeAudioRecordInputStream;
    private final int mSampleRate;
    private final boolean mUsePlatformAEC;

    private class AudioRecordThread extends Thread {
        private volatile boolean mKeepAlive;

        private AudioRecordThread() {
            this.mKeepAlive = true;
        }

        public void run() {
            Process.setThreadPriority(-19);
            try {
                AudioRecordInput.this.mAudioRecord.startRecording();
                while (this.mKeepAlive) {
                    int bytesRead = AudioRecordInput.this.mAudioRecord.read(AudioRecordInput.this.mBuffer, AudioRecordInput.this.mBuffer.capacity());
                    if (bytesRead > 0) {
                        AudioRecordInput.this.nativeOnData(AudioRecordInput.this.mNativeAudioRecordInputStream, bytesRead, AudioRecordInput.this.mHardwareDelayBytes);
                    } else {
                        Log.m28e(AudioRecordInput.TAG, "read failed: %d", Integer.valueOf(bytesRead));
                        if (bytesRead == -3) {
                            this.mKeepAlive = false;
                        }
                    }
                }
                try {
                    AudioRecordInput.this.mAudioRecord.stop();
                } catch (IllegalStateException e) {
                    Log.m28e(AudioRecordInput.TAG, "stop failed", e);
                }
            } catch (IllegalStateException e2) {
                Log.m28e(AudioRecordInput.TAG, "startRecording failed", e2);
            }
        }

        public void joinRecordThread() {
            this.mKeepAlive = false;
            while (isAlive()) {
                try {
                    join();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private native void nativeCacheDirectBufferAddress(long j, ByteBuffer byteBuffer);

    private native void nativeOnData(long j, int i, int i2);

    @CalledByNative
    private static AudioRecordInput createAudioRecordInput(long nativeAudioRecordInputStream, int sampleRate, int channels, int bitsPerSample, int bytesPerBuffer, boolean usePlatformAEC) {
        return new AudioRecordInput(nativeAudioRecordInputStream, sampleRate, channels, bitsPerSample, bytesPerBuffer, usePlatformAEC);
    }

    private AudioRecordInput(long nativeAudioRecordInputStream, int sampleRate, int channels, int bitsPerSample, int bytesPerBuffer, boolean usePlatformAEC) {
        this.mNativeAudioRecordInputStream = nativeAudioRecordInputStream;
        this.mSampleRate = sampleRate;
        this.mChannels = channels;
        this.mBitsPerSample = bitsPerSample;
        this.mHardwareDelayBytes = (((sampleRate * HARDWARE_DELAY_MS) / 1000) * bitsPerSample) / 8;
        this.mUsePlatformAEC = usePlatformAEC;
        this.mBuffer = ByteBuffer.allocateDirect(bytesPerBuffer);
        nativeCacheDirectBufferAddress(this.mNativeAudioRecordInputStream, this.mBuffer);
    }

    @SuppressLint({"NewApi"})
    @CalledByNative
    private boolean open() {
        if (this.mAudioRecord != null) {
            Log.m28e(TAG, "open() called twice without a close()", new Object[0]);
            return false;
        }
        int channelConfig;
        int audioFormat;
        if (this.mChannels == 1) {
            channelConfig = 16;
        } else if (this.mChannels == 2) {
            channelConfig = 12;
        } else {
            Log.m28e(TAG, "Unsupported number of channels: %d", Integer.valueOf(this.mChannels));
            return false;
        }
        if (this.mBitsPerSample == 8) {
            audioFormat = 3;
        } else if (this.mBitsPerSample == 16) {
            audioFormat = 2;
        } else {
            Log.m28e(TAG, "Unsupported bits per sample: %d", Integer.valueOf(this.mBitsPerSample));
            return false;
        }
        int minBufferSize = AudioRecord.getMinBufferSize(this.mSampleRate, channelConfig, audioFormat);
        if (minBufferSize < 0) {
            Log.m28e(TAG, "getMinBufferSize error: %d", Integer.valueOf(minBufferSize));
            return false;
        }
        try {
            this.mAudioRecord = new AudioRecord(7, this.mSampleRate, channelConfig, audioFormat, Math.max(this.mBuffer.capacity(), minBufferSize));
            if (AcousticEchoCanceler.isAvailable()) {
                this.mAEC = AcousticEchoCanceler.create(this.mAudioRecord.getAudioSessionId());
                if (this.mAEC == null) {
                    Log.m28e(TAG, "AcousticEchoCanceler.create failed", new Object[0]);
                    return false;
                }
                if (this.mAEC.setEnabled(this.mUsePlatformAEC) != 0) {
                    Log.m28e(TAG, "setEnabled error: %d", Integer.valueOf(this.mAEC.setEnabled(this.mUsePlatformAEC)));
                    return false;
                }
            }
            return true;
        } catch (IllegalArgumentException e) {
            Log.m28e(TAG, "AudioRecord failed", e);
            return false;
        }
    }

    @CalledByNative
    private void start() {
        if (this.mAudioRecord == null) {
            Log.m28e(TAG, "start() called before open().", new Object[0]);
        } else if (this.mAudioRecordThread == null) {
            this.mAudioRecordThread = new AudioRecordThread();
            this.mAudioRecordThread.start();
        }
    }

    @CalledByNative
    private void stop() {
        if (this.mAudioRecordThread != null) {
            this.mAudioRecordThread.joinRecordThread();
            this.mAudioRecordThread = null;
        }
    }

    @SuppressLint({"NewApi"})
    @CalledByNative
    private void close() {
        if (this.mAudioRecordThread != null) {
            Log.m28e(TAG, "close() called before stop().", new Object[0]);
        } else if (this.mAudioRecord != null) {
            if (this.mAEC != null) {
                this.mAEC.release();
                this.mAEC = null;
            }
            this.mAudioRecord.release();
            this.mAudioRecord = null;
        }
    }
}
