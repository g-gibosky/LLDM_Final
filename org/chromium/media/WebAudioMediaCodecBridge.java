package org.chromium.media;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("media")
class WebAudioMediaCodecBridge {
    private static final String TAG = "cr.media";
    static final long TIMEOUT_MICROSECONDS = 500;

    private static native void nativeInitializeDestination(long j, int i, int i2, long j2);

    private static native void nativeOnChunkDecoded(long j, ByteBuffer byteBuffer, int i, int i2, int i3);

    WebAudioMediaCodecBridge() {
    }

    @CalledByNative
    private static String createTempFile(Context ctx) throws IOException {
        return File.createTempFile("webaudio", ".dat", ctx.getCacheDir()).getAbsolutePath();
    }

    @CalledByNative
    private static boolean decodeAudioFile(Context ctx, long nativeMediaCodecBridge, int inputFD, long dataSize) {
        if (dataSize < 0 || dataSize > 2147483647L) {
            return false;
        }
        MediaExtractor extractor = new MediaExtractor();
        ParcelFileDescriptor encodedFD = ParcelFileDescriptor.adoptFd(inputFD);
        try {
            extractor.setDataSource(encodedFD.getFileDescriptor(), 0, dataSize);
            if (extractor.getTrackCount() <= 0) {
                encodedFD.detachFd();
                return false;
            }
            MediaFormat format = extractor.getTrackFormat(0);
            try {
                int inputChannelCount = format.getInteger("channel-count");
                int outputChannelCount = inputChannelCount;
                try {
                    int sampleRate = format.getInteger("sample-rate");
                    try {
                        String mime = format.getString("mime");
                        long durationMicroseconds = 0;
                        if (format.containsKey("durationUs")) {
                            try {
                                durationMicroseconds = format.getLong("durationUs");
                            } catch (Exception e) {
                                Log.m20d(TAG, "Cannot get duration");
                            }
                        }
                        if (durationMicroseconds > 2147483647L) {
                            durationMicroseconds = 0;
                        }
                        Log.m22d(TAG, "Initial: Tracks: %d Format: %s", Integer.valueOf(extractor.getTrackCount()), format);
                        try {
                            MediaCodec codec = MediaCodec.createDecoderByType(mime);
                            try {
                                codec.configure(format, null, null, 0);
                                try {
                                    codec.start();
                                    try {
                                        ByteBuffer[] codecInputBuffers = codec.getInputBuffers();
                                        try {
                                            ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();
                                            extractor.selectTrack(0);
                                            boolean sawInputEOS = false;
                                            boolean sawOutputEOS = false;
                                            boolean destinationInitialized = false;
                                            boolean z = true;
                                            while (!sawOutputEOS) {
                                                if (!sawInputEOS) {
                                                    try {
                                                        int inputBufIndex = codec.dequeueInputBuffer(TIMEOUT_MICROSECONDS);
                                                        if (inputBufIndex >= 0) {
                                                            try {
                                                                int i;
                                                                int sampleSize = extractor.readSampleData(codecInputBuffers[inputBufIndex], 0);
                                                                long presentationTimeMicroSec = 0;
                                                                if (sampleSize < 0) {
                                                                    sawInputEOS = true;
                                                                    sampleSize = 0;
                                                                } else {
                                                                    presentationTimeMicroSec = extractor.getSampleTime();
                                                                }
                                                                if (sawInputEOS) {
                                                                    i = 4;
                                                                } else {
                                                                    i = 0;
                                                                }
                                                                try {
                                                                    codec.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeMicroSec, i);
                                                                    if (!sawInputEOS) {
                                                                        extractor.advance();
                                                                    }
                                                                } catch (Exception e2) {
                                                                    String str = TAG;
                                                                    String str2 = "queueInputBuffer(%d, 0, %d, %d, %d) failed.";
                                                                    Object[] objArr = new Object[5];
                                                                    objArr[0] = Integer.valueOf(inputBufIndex);
                                                                    objArr[1] = Integer.valueOf(sampleSize);
                                                                    objArr[2] = Long.valueOf(presentationTimeMicroSec);
                                                                    objArr[3] = Integer.valueOf(sawInputEOS ? 4 : 0);
                                                                    objArr[4] = e2;
                                                                    Log.m38w(str, str2, objArr);
                                                                    z = false;
                                                                }
                                                            } catch (Exception e3) {
                                                                Log.m38w(TAG, "readSampleData failed.", new Object[0]);
                                                                z = false;
                                                            }
                                                        }
                                                    } catch (Exception e22) {
                                                        Log.m38w(TAG, "dequeueInputBuffer(%d) failed.", Long.valueOf(TIMEOUT_MICROSECONDS), e22);
                                                        z = false;
                                                    }
                                                }
                                                BufferInfo info = new BufferInfo();
                                                try {
                                                    int outputBufIndex = codec.dequeueOutputBuffer(info, TIMEOUT_MICROSECONDS);
                                                    if (outputBufIndex >= 0) {
                                                        ByteBuffer buf = codecOutputBuffers[outputBufIndex];
                                                        if (!destinationInitialized) {
                                                            Log.m24d(TAG, "Final:  Rate: %d Channels: %d Mime: %s Duration: %d microsec", Integer.valueOf(sampleRate), Integer.valueOf(inputChannelCount), mime, Long.valueOf(durationMicroseconds));
                                                            nativeInitializeDestination(nativeMediaCodecBridge, inputChannelCount, sampleRate, durationMicroseconds);
                                                            destinationInitialized = true;
                                                        }
                                                        if (destinationInitialized && info.size > 0) {
                                                            nativeOnChunkDecoded(nativeMediaCodecBridge, buf, info.size, inputChannelCount, outputChannelCount);
                                                        }
                                                        buf.clear();
                                                        codec.releaseOutputBuffer(outputBufIndex, false);
                                                        if ((info.flags & 4) != 0) {
                                                            sawOutputEOS = true;
                                                        }
                                                    } else if (outputBufIndex == -3) {
                                                        codecOutputBuffers = codec.getOutputBuffers();
                                                    } else if (outputBufIndex == -2) {
                                                        MediaFormat newFormat = codec.getOutputFormat();
                                                        outputChannelCount = newFormat.getInteger("channel-count");
                                                        sampleRate = newFormat.getInteger("sample-rate");
                                                        Log.m20d(TAG, "output format changed to " + newFormat);
                                                    }
                                                } catch (Exception e222) {
                                                    Log.m38w(TAG, "dequeueOutputBuffer(%s, %d) failed", info, Long.valueOf(TIMEOUT_MICROSECONDS));
                                                    e222.printStackTrace();
                                                    z = false;
                                                }
                                            }
                                            encodedFD.detachFd();
                                            codec.stop();
                                            codec.release();
                                            return z;
                                        } catch (Exception e2222) {
                                            Log.m38w(TAG, "getOutputBuffers() failed", e2222);
                                            encodedFD.detachFd();
                                            return false;
                                        }
                                    } catch (Exception e22222) {
                                        Log.m38w(TAG, "getInputBuffers() failed", e22222);
                                        encodedFD.detachFd();
                                        return false;
                                    }
                                } catch (Exception e222222) {
                                    Log.m38w(TAG, "Unable to start()", e222222);
                                    encodedFD.detachFd();
                                    return false;
                                }
                            } catch (Exception e2222222) {
                                Log.m38w(TAG, "Unable to configure codec for format " + format, e2222222);
                                encodedFD.detachFd();
                                return false;
                            }
                        } catch (Exception e4) {
                            Log.m38w(TAG, "Failed to create MediaCodec for mime type: %s", mime);
                            encodedFD.detachFd();
                            return false;
                        }
                    } catch (Exception e5) {
                        Log.m38w(TAG, "Unable to determine type of encoding used by the file", new Object[0]);
                        encodedFD.detachFd();
                        return false;
                    }
                } catch (Exception e6) {
                    Log.m38w(TAG, "Unable to determine sample rate", new Object[0]);
                    encodedFD.detachFd();
                    return false;
                }
            } catch (Exception e7) {
                Log.m38w(TAG, "Unable to determine number of channels", new Object[0]);
                encodedFD.detachFd();
                return false;
            }
        } catch (Exception e22222222) {
            e22222222.printStackTrace();
            encodedFD.detachFd();
            return false;
        }
    }
}
