package org.chromium.media;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.os.Build;
import android.os.Build.VERSION;
import java.util.Locale;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.MainDex;

@JNINamespace("media")
class MediaCodecUtil {
    static final /* synthetic */ boolean $assertionsDisabled = (!MediaCodecUtil.class.desiredAssertionStatus());
    static final int MEDIA_CODEC_DECODER = 0;
    static final int MEDIA_CODEC_ENCODER = 1;
    private static final String TAG = "MediaCodecUtil";

    @MainDex
    public static class CodecCreationInfo {
        public MediaCodec mediaCodec = null;
        public boolean supportsAdaptivePlayback = false;
    }

    @MainDex
    private static class MediaCodecListHelper {
        private MediaCodecInfo[] mCodecList;

        @TargetApi(21)
        public MediaCodecListHelper() {
            if (hasNewMediaCodecList()) {
                this.mCodecList = new MediaCodecList(1).getCodecInfos();
            }
        }

        public int getCodecCount() {
            if (hasNewMediaCodecList()) {
                return this.mCodecList.length;
            }
            return MediaCodecList.getCodecCount();
        }

        public MediaCodecInfo getCodecInfoAt(int index) {
            if (hasNewMediaCodecList()) {
                return this.mCodecList[index];
            }
            return MediaCodecList.getCodecInfoAt(index);
        }

        private boolean hasNewMediaCodecList() {
            return VERSION.SDK_INT >= 21;
        }
    }

    @MainDex
    public static final class MimeTypes {
        public static final String VIDEO_H264 = "video/avc";
        public static final String VIDEO_H265 = "video/hevc";
        public static final String VIDEO_MP4 = "video/mp4";
        public static final String VIDEO_VP8 = "video/x-vnd.on2.vp8";
        public static final String VIDEO_VP9 = "video/x-vnd.on2.vp9";
        public static final String VIDEO_WEBM = "video/webm";
    }

    MediaCodecUtil() {
    }

    public static boolean isSoftwareCodec(String name) {
        if (!name.startsWith("OMX.google.") && name.startsWith("OMX.")) {
            return false;
        }
        return true;
    }

    @CalledByNative
    private static String getDefaultCodecName(String mime, int direction, boolean requireSoftwareCodec) {
        MediaCodecListHelper codecListHelper = new MediaCodecListHelper();
        int codecCount = codecListHelper.getCodecCount();
        for (int i = 0; i < codecCount; i++) {
            int codecDirection;
            MediaCodecInfo info = codecListHelper.getCodecInfoAt(i);
            if (info.isEncoder()) {
                codecDirection = 1;
            } else {
                codecDirection = 0;
            }
            if (codecDirection == direction && (!requireSoftwareCodec || isSoftwareCodec(info.getName()))) {
                String[] supportedTypes = info.getSupportedTypes();
                for (String equalsIgnoreCase : supportedTypes) {
                    if (equalsIgnoreCase.equalsIgnoreCase(mime)) {
                        return info.getName();
                    }
                }
                continue;
            }
        }
        Log.m28e(TAG, "Decoder for type %s is not supported on this device", mime);
        return "";
    }

    @CalledByNative
    private static int[] getEncoderColorFormatsForMime(String mime) {
        MediaCodecListHelper codecListHelper = new MediaCodecListHelper();
        int codecCount = codecListHelper.getCodecCount();
        for (int i = 0; i < codecCount; i++) {
            MediaCodecInfo info = codecListHelper.getCodecInfoAt(i);
            if (info.isEncoder()) {
                String[] supportedTypes = info.getSupportedTypes();
                for (int j = 0; j < supportedTypes.length; j++) {
                    if (supportedTypes[j].equalsIgnoreCase(mime)) {
                        return info.getCapabilitiesForType(supportedTypes[j]).colorFormats;
                    }
                }
                continue;
            }
        }
        return null;
    }

    @CalledByNative
    private static boolean canDecode(String mime, boolean isSecure) {
        CodecCreationInfo info = createDecoder(mime, isSecure, false);
        if (info.mediaCodec == null) {
            return false;
        }
        try {
            info.mediaCodec.release();
        } catch (IllegalStateException e) {
            Log.m28e(TAG, "Cannot release media codec", e);
        }
        return true;
    }

    static CodecCreationInfo createDecoder(String mime, boolean isSecure, boolean requireSoftwareCodec) {
        CodecCreationInfo result = new CodecCreationInfo();
        if (!$assertionsDisabled && result.mediaCodec != null) {
            throw new AssertionError();
        } else if (isSecure && VERSION.SDK_INT < 18) {
            return result;
        } else {
            if (isDecoderSupportedForDevice(mime)) {
                try {
                    if (mime.startsWith("video") && isSecure) {
                        String decoderName = getDefaultCodecName(mime, 0, requireSoftwareCodec);
                        if (decoderName.equals("")) {
                            return null;
                        }
                        if (VERSION.SDK_INT >= 19) {
                            MediaCodec insecureCodec = MediaCodec.createByCodecName(decoderName);
                            result.supportsAdaptivePlayback = codecSupportsAdaptivePlayback(insecureCodec, mime);
                            insecureCodec.release();
                        }
                        result.mediaCodec = MediaCodec.createByCodecName(decoderName + ".secure");
                        return result;
                    }
                    if (requireSoftwareCodec) {
                        result.mediaCodec = MediaCodec.createByCodecName(getDefaultCodecName(mime, 0, requireSoftwareCodec));
                    } else {
                        result.mediaCodec = MediaCodec.createDecoderByType(mime);
                    }
                    result.supportsAdaptivePlayback = codecSupportsAdaptivePlayback(result.mediaCodec, mime);
                    return result;
                } catch (Exception e) {
                    String str = TAG;
                    String str2 = "Failed to create MediaCodec: %s, isSecure: %s, requireSoftwareCodec: %s";
                    Object[] objArr = new Object[4];
                    objArr[0] = mime;
                    objArr[1] = Boolean.valueOf(isSecure);
                    objArr[2] = requireSoftwareCodec ? "yes" : "no";
                    objArr[3] = e;
                    Log.m28e(str, str2, objArr);
                    result.mediaCodec = null;
                    return result;
                }
            }
            Log.m28e(TAG, "Decoder for type %s is not supported on this device", mime);
            return result;
        }
    }

    @CalledByNative
    static boolean isDecoderSupportedForDevice(String mime) {
        if (mime.equals(MimeTypes.VIDEO_VP8)) {
            if (Build.MANUFACTURER.toLowerCase(Locale.getDefault()).equals("samsung")) {
                if ((VERSION.SDK_INT < 21 && (Build.MODEL.startsWith("GT-I9505") || Build.MODEL.startsWith("GT-I9500"))) || Build.MODEL.startsWith("GT-I9190") || Build.MODEL.startsWith("GT-I9195")) {
                    return false;
                }
                if (VERSION.SDK_INT < 19 && (Build.MODEL.startsWith("GT-") || Build.MODEL.startsWith("SCH-") || Build.MODEL.startsWith("SM-T"))) {
                    return false;
                }
            }
            if (Build.HARDWARE.startsWith("mt")) {
                return false;
            }
        } else if (mime.equals(MimeTypes.VIDEO_VP9)) {
            if (VERSION.SDK_INT < 19) {
                return false;
            }
            if (VERSION.SDK_INT < 21 && Build.HARDWARE.startsWith("mt")) {
                return false;
            }
        } else if (mime.equals("audio/opus") && VERSION.SDK_INT < 21) {
            return false;
        }
        return true;
    }

    private static boolean isAdaptivePlaybackBlacklisted(String mime) {
        if ((!mime.equals(MimeTypes.VIDEO_H264) && !mime.equals("video/avc1")) || !VERSION.RELEASE.equals("4.4.2") || !Build.MANUFACTURER.toLowerCase(Locale.getDefault()).equals("samsung")) {
            return false;
        }
        if (Build.MODEL.startsWith("GT-I9300") || Build.MODEL.startsWith("SCH-I535")) {
            return true;
        }
        return false;
    }

    @TargetApi(19)
    private static boolean codecSupportsAdaptivePlayback(MediaCodec mediaCodec, String mime) {
        boolean z = true;
        if (VERSION.SDK_INT < 19 || mediaCodec == null) {
            return false;
        }
        try {
            MediaCodecInfo info = mediaCodec.getCodecInfo();
            if (info.isEncoder() || isAdaptivePlaybackBlacklisted(mime)) {
                return false;
            }
            CodecCapabilities capabilities = info.getCapabilitiesForType(mime);
            if (capabilities == null || !capabilities.isFeatureSupported("adaptive-playback")) {
                z = false;
            }
            return z;
        } catch (IllegalArgumentException e) {
            Log.m28e(TAG, "Cannot retrieve codec information", e);
            return false;
        }
    }
}
