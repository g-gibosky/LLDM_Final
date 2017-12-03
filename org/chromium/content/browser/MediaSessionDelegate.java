package org.chromium.content.browser;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("content")
public class MediaSessionDelegate implements OnAudioFocusChangeListener {
    public static final double DEFAULT_VOLUME_MULTIPLIER = 1.0d;
    public static final double DUCKING_VOLUME_MULTIPLIER = 0.20000000298023224d;
    private static final String TAG = "MediaSession";
    private Context mContext;
    private int mFocusType;
    private boolean mIsDucking = false;
    private long mNativeMediaSessionDelegateAndroid;

    private native void nativeOnResume(long j);

    private native void nativeOnSetVolumeMultiplier(long j, double d);

    private native void nativeOnSuspend(long j, boolean z);

    private native void nativeRecordSessionDuck(long j);

    private MediaSessionDelegate(Context context, long nativeMediaSessionDelegateAndroid) {
        this.mContext = context;
        this.mNativeMediaSessionDelegateAndroid = nativeMediaSessionDelegateAndroid;
    }

    @CalledByNative
    private static MediaSessionDelegate create(Context context, long nativeMediaSessionDelegateAndroid) {
        return new MediaSessionDelegate(context, nativeMediaSessionDelegateAndroid);
    }

    @CalledByNative
    private void tearDown() {
        abandonAudioFocus();
        this.mNativeMediaSessionDelegateAndroid = 0;
    }

    @CalledByNative
    private boolean requestAudioFocus(boolean transientFocus) {
        this.mFocusType = transientFocus ? 3 : 1;
        return requestAudioFocusInternal();
    }

    @CalledByNative
    private void abandonAudioFocus() {
        ((AudioManager) this.mContext.getSystemService("audio")).abandonAudioFocus(this);
    }

    private boolean requestAudioFocusInternal() {
        if (((AudioManager) this.mContext.getSystemService("audio")).requestAudioFocus(this, 3, this.mFocusType) == 1) {
            return true;
        }
        return false;
    }

    public void onAudioFocusChange(int focusChange) {
        if (this.mNativeMediaSessionDelegateAndroid != 0) {
            switch (focusChange) {
                case -3:
                    this.mIsDucking = true;
                    nativeRecordSessionDuck(this.mNativeMediaSessionDelegateAndroid);
                    nativeOnSetVolumeMultiplier(this.mNativeMediaSessionDelegateAndroid, DUCKING_VOLUME_MULTIPLIER);
                    return;
                case -2:
                    nativeOnSuspend(this.mNativeMediaSessionDelegateAndroid, true);
                    return;
                case -1:
                    abandonAudioFocus();
                    nativeOnSuspend(this.mNativeMediaSessionDelegateAndroid, false);
                    return;
                case 1:
                    if (this.mIsDucking) {
                        nativeOnSetVolumeMultiplier(this.mNativeMediaSessionDelegateAndroid, DEFAULT_VOLUME_MULTIPLIER);
                        this.mIsDucking = false;
                        return;
                    }
                    nativeOnResume(this.mNativeMediaSessionDelegateAndroid);
                    return;
                default:
                    Log.m38w(TAG, "onAudioFocusChange called with unexpected value %d", Integer.valueOf(focusChange));
                    return;
            }
        }
    }
}
