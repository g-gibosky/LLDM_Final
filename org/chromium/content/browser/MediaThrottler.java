package org.chromium.content.browser;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.metrics.RecordHistogram;
import org.chromium.content.C0174R;

@JNINamespace("content")
class MediaThrottler implements OnErrorListener {
    static final /* synthetic */ boolean $assertionsDisabled = (!MediaThrottler.class.desiredAssertionStatus());
    private static final int RELEASE_WATCH_DOG_PLAYER_DELAY_IN_MILLIS = 5000;
    private static final int SERVER_CRASH_COUNT_THRESHOLD_FOR_THROTTLING = 4;
    private static final long SERVER_CRASH_INTERVAL_THRESHOLD_IN_MILLIS = 60000;
    private static final String TAG = "cr_MediaThrottler";
    private static final long UNKNOWN_LAST_SERVER_CRASH_TIME = -1;
    private final Context mContext;
    private final Runnable mDelayedReleaseRunnable = new C01971();
    private Handler mHandler;
    private long mLastCrashTime = -1;
    private final Object mLock = new Object();
    private MediaPlayer mPlayer;
    private int mRequestCount;
    private int mServerCrashCount;

    class C01971 implements Runnable {
        C01971() {
        }

        public void run() {
            new ReleaseWatchDogTask().execute(new Void[0]);
        }
    }

    class C01982 implements Runnable {
        C01982() {
        }

        public void run() {
            new StartWatchDogTask().execute(new Void[0]);
        }
    }

    private class ReleaseWatchDogTask extends AsyncTask<Void, Void, Void> {
        private ReleaseWatchDogTask() {
        }

        protected Void doInBackground(Void... voids) {
            synchronized (MediaThrottler.this.mLock) {
                if (MediaThrottler.this.mRequestCount == 0 && MediaThrottler.this.mPlayer != null) {
                    MediaThrottler.this.mPlayer.release();
                    MediaThrottler.this.mPlayer = null;
                }
            }
            return null;
        }
    }

    private class StartWatchDogTask extends AsyncTask<Void, Void, Void> {
        private StartWatchDogTask() {
        }

        protected Void doInBackground(Void... voids) {
            synchronized (MediaThrottler.this.mLock) {
                if (MediaThrottler.this.mPlayer != null || MediaThrottler.this.mRequestCount == 0) {
                } else {
                    try {
                        MediaThrottler.this.mPlayer = MediaPlayer.create(MediaThrottler.this.mContext, C0174R.raw.empty);
                    } catch (IllegalStateException e) {
                        Log.m28e(MediaThrottler.TAG, "Exception happens while creating the watch dog player.", e);
                    } catch (RuntimeException e2) {
                        Log.m28e(MediaThrottler.TAG, "Exception happens while creating the watch dog player.", e2);
                    }
                    if (MediaThrottler.this.mPlayer == null) {
                        Log.m28e(MediaThrottler.TAG, "Unable to create watch dog player, treat it as server crash.", new Object[0]);
                        MediaThrottler.this.onMediaServerCrash();
                    } else {
                        MediaThrottler.this.mPlayer.setOnErrorListener(MediaThrottler.this);
                    }
                }
            }
            return null;
        }
    }

    @CalledByNative
    private static MediaThrottler create(Context context) {
        return new MediaThrottler(context);
    }

    private MediaThrottler(Context context) {
        this.mContext = context;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @org.chromium.base.annotations.CalledByNative
    private boolean requestDecoderResources() {
        /*
        r10 = this;
        r3 = 1;
        r2 = 0;
        r4 = r10.mLock;
        monitor-enter(r4);
        r0 = android.os.SystemClock.elapsedRealtime();	 Catch:{ all -> 0x004f }
        r6 = r10.mLastCrashTime;	 Catch:{ all -> 0x004f }
        r8 = -1;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 == 0) goto L_0x002d;
    L_0x0011:
        r6 = r10.mLastCrashTime;	 Catch:{ all -> 0x004f }
        r6 = r0 - r6;
        r8 = 60000; // 0xea60 float:8.4078E-41 double:2.9644E-319;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 >= 0) goto L_0x002d;
    L_0x001c:
        r5 = r10.mServerCrashCount;	 Catch:{ all -> 0x004f }
        r6 = 4;
        if (r5 < r6) goto L_0x002d;
    L_0x0021:
        r3 = "cr_MediaThrottler";
        r5 = "Request to decode media data denied due to throttling.";
        r6 = 0;
        r6 = new java.lang.Object[r6];	 Catch:{ all -> 0x004f }
        org.chromium.base.Log.m28e(r3, r5, r6);	 Catch:{ all -> 0x004f }
        monitor-exit(r4);	 Catch:{ all -> 0x004f }
    L_0x002c:
        return r2;
    L_0x002d:
        r2 = r10.mRequestCount;	 Catch:{ all -> 0x004f }
        r2 = r2 + 1;
        r10.mRequestCount = r2;	 Catch:{ all -> 0x004f }
        r2 = r10.mRequestCount;	 Catch:{ all -> 0x004f }
        if (r2 == r3) goto L_0x003b;
    L_0x0037:
        r2 = r10.mPlayer;	 Catch:{ all -> 0x004f }
        if (r2 != 0) goto L_0x004c;
    L_0x003b:
        r2 = r10.mHandler;	 Catch:{ all -> 0x004f }
        r5 = r10.mDelayedReleaseRunnable;	 Catch:{ all -> 0x004f }
        r2.removeCallbacks(r5);	 Catch:{ all -> 0x004f }
        r2 = r10.mHandler;	 Catch:{ all -> 0x004f }
        r5 = new org.chromium.content.browser.MediaThrottler$2;	 Catch:{ all -> 0x004f }
        r5.<init>();	 Catch:{ all -> 0x004f }
        r2.post(r5);	 Catch:{ all -> 0x004f }
    L_0x004c:
        monitor-exit(r4);	 Catch:{ all -> 0x004f }
        r2 = r3;
        goto L_0x002c;
    L_0x004f:
        r2 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x004f }
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.browser.MediaThrottler.requestDecoderResources():boolean");
    }

    @CalledByNative
    private void onDecodeRequestFinished() {
        synchronized (this.mLock) {
            this.mRequestCount--;
            if (this.mRequestCount == 0) {
                prepareToStopWatchDog();
            }
        }
    }

    private void prepareToStopWatchDog() {
        this.mHandler.postDelayed(this.mDelayedReleaseRunnable, 5000);
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (what == 100) {
            synchronized (this.mLock) {
                onMediaServerCrash();
            }
        }
        return true;
    }

    private void onMediaServerCrash() {
        if ($assertionsDisabled || Thread.holdsLock(this.mLock)) {
            long currentTime = SystemClock.elapsedRealtime();
            if (this.mLastCrashTime == -1 || currentTime - this.mLastCrashTime >= SERVER_CRASH_INTERVAL_THRESHOLD_IN_MILLIS) {
                recordNumMediaServerCrashes();
                this.mServerCrashCount = 1;
            } else {
                this.mServerCrashCount++;
            }
            this.mLastCrashTime = currentTime;
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private void reset() {
        synchronized (this.mLock) {
            recordNumMediaServerCrashes();
            this.mServerCrashCount = 0;
            this.mLastCrashTime = -1;
        }
    }

    private void recordNumMediaServerCrashes() {
        if (!$assertionsDisabled && !Thread.holdsLock(this.mLock)) {
            throw new AssertionError();
        } else if (this.mServerCrashCount > 0) {
            RecordHistogram.recordCountHistogram("Media.Android.NumMediaServerCrashes", this.mServerCrashCount);
        }
    }
}
