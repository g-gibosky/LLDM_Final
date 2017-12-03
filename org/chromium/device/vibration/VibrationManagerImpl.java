package org.chromium.device.vibration;

import android.content.Context;
import android.media.AudioManager;
import android.os.Vibrator;
import android.util.Log;
import org.chromium.base.VisibleForTesting;
import org.chromium.mojo.system.MojoException;
import org.chromium.mojom.device.VibrationManager;
import org.chromium.mojom.device.VibrationManager.CancelResponse;
import org.chromium.mojom.device.VibrationManager.VibrateResponse;

public class VibrationManagerImpl implements VibrationManager {
    private static final long MAXIMUM_VIBRATION_DURATION_MS = 10000;
    private static final long MINIMUM_VIBRATION_DURATION_MS = 1;
    private static final String TAG = "VibrationManagerImpl";
    private static AndroidVibratorWrapper sVibratorWrapper;
    private final AudioManager mAudioManager;
    private final boolean mHasVibratePermission;
    private final Vibrator mVibrator;

    @VisibleForTesting
    public static class AndroidVibratorWrapper {
        protected AndroidVibratorWrapper() {
        }

        public void vibrate(Vibrator vibrator, long milliseconds) {
            vibrator.vibrate(milliseconds);
        }

        public void cancel(Vibrator vibrator) {
            vibrator.cancel();
        }
    }

    public static void setVibratorWrapperForTesting(AndroidVibratorWrapper wrapper) {
        sVibratorWrapper = wrapper;
    }

    public VibrationManagerImpl(Context context) {
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        this.mVibrator = (Vibrator) context.getSystemService("vibrator");
        if (sVibratorWrapper == null) {
            sVibratorWrapper = new AndroidVibratorWrapper();
        }
        this.mHasVibratePermission = context.checkCallingOrSelfPermission("android.permission.VIBRATE") == 0;
        if (!this.mHasVibratePermission) {
            Log.w(TAG, "Failed to use vibrate API, requires VIBRATE permission.");
        }
    }

    public void close() {
    }

    public void onConnectionError(MojoException e) {
    }

    public void vibrate(long milliseconds, VibrateResponse callback) {
        long sanitizedMilliseconds = Math.max(1, Math.min(milliseconds, MAXIMUM_VIBRATION_DURATION_MS));
        if (this.mAudioManager.getRingerMode() != 0 && this.mHasVibratePermission) {
            sVibratorWrapper.vibrate(this.mVibrator, sanitizedMilliseconds);
        }
        callback.call();
    }

    public void cancel(CancelResponse callback) {
        if (this.mHasVibratePermission) {
            sVibratorWrapper.cancel(this.mVibrator);
        }
        callback.call();
    }
}
