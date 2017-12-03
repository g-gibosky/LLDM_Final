package org.chromium.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.WindowManager;
import org.chromium.base.TraceEvent;

public class VSyncMonitor {
    static final /* synthetic */ boolean $assertionsDisabled = (!VSyncMonitor.class.desiredAssertionStatus());
    private static final long NANOSECONDS_PER_MICROSECOND = 1000;
    private static final long NANOSECONDS_PER_SECOND = 1000000000;
    private final Choreographer mChoreographer;
    private boolean mConsecutiveVSync = false;
    private long mGoodStartingPointNano;
    private final Handler mHandler = new Handler();
    private boolean mHaveRequestInFlight;
    private boolean mInsideVSync = false;
    private Listener mListener;
    private long mRefreshPeriodNano;
    private final FrameCallback mVSyncFrameCallback;

    public interface Listener {
        void onVSync(VSyncMonitor vSyncMonitor, long j);
    }

    static /* synthetic */ long access$214(VSyncMonitor x0, long x1) {
        long j = x0.mRefreshPeriodNano + x1;
        x0.mRefreshPeriodNano = j;
        return j;
    }

    public VSyncMonitor(Context context, Listener listener) {
        boolean useEstimatedRefreshPeriod = false;
        this.mListener = listener;
        float refreshRate = ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRefreshRate();
        if (refreshRate < 30.0f) {
            useEstimatedRefreshPeriod = true;
        }
        if (refreshRate <= 0.0f) {
            refreshRate = 60.0f;
        }
        this.mRefreshPeriodNano = (long) (1.0E9f / refreshRate);
        this.mChoreographer = Choreographer.getInstance();
        this.mVSyncFrameCallback = new FrameCallback() {
            public void doFrame(long frameTimeNanos) {
                TraceEvent.begin("VSync");
                if (useEstimatedRefreshPeriod && VSyncMonitor.this.mConsecutiveVSync) {
                    VSyncMonitor.access$214(VSyncMonitor.this, (long) (((float) ((frameTimeNanos - VSyncMonitor.this.mGoodStartingPointNano) - VSyncMonitor.this.mRefreshPeriodNano)) * 0.1f));
                }
                VSyncMonitor.this.mGoodStartingPointNano = frameTimeNanos;
                VSyncMonitor.this.onVSyncCallback(frameTimeNanos, VSyncMonitor.this.getCurrentNanoTime());
                TraceEvent.end("VSync");
            }
        };
        this.mGoodStartingPointNano = getCurrentNanoTime();
    }

    public long getVSyncPeriodInMicroseconds() {
        return this.mRefreshPeriodNano / NANOSECONDS_PER_MICROSECOND;
    }

    public void requestUpdate() {
        if ($assertionsDisabled || this.mHandler.getLooper() == Looper.myLooper()) {
            postCallback();
            return;
        }
        throw new AssertionError();
    }

    public boolean isInsideVSync() {
        return this.mInsideVSync;
    }

    private long getCurrentNanoTime() {
        return System.nanoTime();
    }

    private void onVSyncCallback(long frameTimeNanos, long currentTimeNanos) {
        if ($assertionsDisabled || this.mHaveRequestInFlight) {
            this.mInsideVSync = true;
            this.mHaveRequestInFlight = false;
            try {
                if (this.mListener != null) {
                    this.mListener.onVSync(this, frameTimeNanos / NANOSECONDS_PER_MICROSECOND);
                }
                this.mInsideVSync = false;
            } catch (Throwable th) {
                this.mInsideVSync = false;
            }
        } else {
            throw new AssertionError();
        }
    }

    private void postCallback() {
        if (!this.mHaveRequestInFlight) {
            this.mHaveRequestInFlight = true;
            this.mConsecutiveVSync = this.mInsideVSync;
            this.mChoreographer.postFrameCallback(this.mVSyncFrameCallback);
        }
    }
}
