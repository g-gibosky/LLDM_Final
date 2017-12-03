package org.chromium.content.browser;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content.C0174R;
import org.chromium.ui.base.WindowAndroid;

@JNINamespace("content")
public class ContentVideoView extends FrameLayout implements Callback {
    public static final int MEDIA_ERROR_INVALID_CODE = 3;
    public static final int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 2;
    private static final int STATE_ERROR = -1;
    private static final int STATE_NO_ERROR = 0;
    private static final String TAG = "cr.ContentVideoView";
    private int mCurrentState = 0;
    private final ContentVideoViewEmbedder mEmbedder;
    private String mErrorButton;
    private String mErrorTitle;
    private final Runnable mExitFullscreenRunnable = new C01851();
    private boolean mInitialOrientation;
    private long mNativeContentVideoView;
    private long mOrientationChangedTime;
    private String mPlaybackErrorText;
    private long mPlaybackStartTime;
    private boolean mPossibleAccidentalChange;
    private View mProgressView;
    private SurfaceHolder mSurfaceHolder;
    private boolean mUmaRecorded;
    private String mUnknownErrorText;
    private int mVideoHeight;
    private String mVideoLoadingText;
    private VideoSurfaceView mVideoSurfaceView;
    private int mVideoWidth;

    class C01851 implements Runnable {
        C01851() {
        }

        public void run() {
            ContentVideoView.this.exitFullscreen(true);
        }
    }

    class C01862 implements OnClickListener {
        C01862() {
        }

        public void onClick(DialogInterface dialog, int whichButton) {
        }
    }

    private static class ProgressView extends LinearLayout {
        private final ProgressBar mProgressBar;
        private final TextView mTextView;

        public ProgressView(Context context, String videoLoadingText) {
            super(context);
            setOrientation(1);
            setLayoutParams(new LayoutParams(-2, -2));
            this.mProgressBar = new ProgressBar(context, null, 16842874);
            this.mTextView = new TextView(context);
            this.mTextView.setText(videoLoadingText);
            addView(this.mProgressBar);
            addView(this.mTextView);
        }
    }

    private class VideoSurfaceView extends SurfaceView {
        public VideoSurfaceView(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = 1;
            int height = 1;
            if (ContentVideoView.this.mVideoWidth > 0 && ContentVideoView.this.mVideoHeight > 0) {
                width = getDefaultSize(ContentVideoView.this.mVideoWidth, widthMeasureSpec);
                height = getDefaultSize(ContentVideoView.this.mVideoHeight, heightMeasureSpec);
                if (ContentVideoView.this.mVideoWidth * height > ContentVideoView.this.mVideoHeight * width) {
                    height = (ContentVideoView.this.mVideoHeight * width) / ContentVideoView.this.mVideoWidth;
                } else if (ContentVideoView.this.mVideoWidth * height < ContentVideoView.this.mVideoHeight * width) {
                    width = (ContentVideoView.this.mVideoWidth * height) / ContentVideoView.this.mVideoHeight;
                }
            }
            if (ContentVideoView.this.mUmaRecorded) {
                if (ContentVideoView.this.mPlaybackStartTime == ContentVideoView.this.mOrientationChangedTime) {
                    if (ContentVideoView.this.isOrientationPortrait() != ContentVideoView.this.mInitialOrientation) {
                        ContentVideoView.this.mOrientationChangedTime = System.currentTimeMillis();
                    }
                } else if (!ContentVideoView.this.mPossibleAccidentalChange && ContentVideoView.this.isOrientationPortrait() == ContentVideoView.this.mInitialOrientation && System.currentTimeMillis() - ContentVideoView.this.mOrientationChangedTime < 5000) {
                    ContentVideoView.this.mPossibleAccidentalChange = true;
                }
            }
            setMeasuredDimension(width, height);
        }
    }

    private native void nativeDidExitFullscreen(long j, boolean z);

    private static native ContentVideoView nativeGetSingletonJavaContentVideoView();

    private native void nativeRecordExitFullscreenPlayback(long j, boolean z, long j2, long j3);

    private native void nativeRecordFullscreenPlayback(long j, boolean z, boolean z2);

    private native void nativeSetSurface(long j, Surface surface);

    private ContentVideoView(Context context, long nativeContentVideoView, ContentVideoViewEmbedder embedder) {
        super(context);
        this.mNativeContentVideoView = nativeContentVideoView;
        this.mEmbedder = embedder;
        this.mUmaRecorded = false;
        this.mPossibleAccidentalChange = false;
        initResources(context);
        this.mVideoSurfaceView = new VideoSurfaceView(context);
        showContentVideoView();
        setVisibility(0);
    }

    private ContentVideoViewEmbedder getContentVideoViewEmbedder() {
        return this.mEmbedder;
    }

    private void initResources(Context context) {
        if (this.mPlaybackErrorText == null) {
            this.mPlaybackErrorText = context.getString(C0174R.string.media_player_error_text_invalid_progressive_playback);
            this.mUnknownErrorText = context.getString(C0174R.string.media_player_error_text_unknown);
            this.mErrorButton = context.getString(C0174R.string.media_player_error_button);
            this.mErrorTitle = context.getString(C0174R.string.media_player_error_title);
            this.mVideoLoadingText = context.getString(C0174R.string.media_player_loading_video);
        }
    }

    private void showContentVideoView() {
        this.mVideoSurfaceView.getHolder().addCallback(this);
        addView(this.mVideoSurfaceView, new FrameLayout.LayoutParams(-2, -2, 17));
        this.mProgressView = this.mEmbedder.getVideoLoadingProgressView();
        if (this.mProgressView == null) {
            this.mProgressView = new ProgressView(getContext(), this.mVideoLoadingText);
        }
        addView(this.mProgressView, new FrameLayout.LayoutParams(-2, -2, 17));
    }

    @CalledByNative
    public void onMediaPlayerError(int errorType) {
        Log.m21d(TAG, "OnMediaPlayerError: %d", Integer.valueOf(errorType));
        if (this.mCurrentState != -1 && errorType != 3) {
            this.mCurrentState = -1;
            if (WindowAndroid.activityFromContext(getContext()) == null) {
                Log.m38w(TAG, "Unable to show alert dialog because it requires an activity context", new Object[0]);
            } else if (getWindowToken() != null) {
                String message;
                if (errorType == 2) {
                    message = this.mPlaybackErrorText;
                } else {
                    message = this.mUnknownErrorText;
                }
                try {
                    new Builder(getContext()).setTitle(this.mErrorTitle).setMessage(message).setPositiveButton(this.mErrorButton, new C01862()).setCancelable(false).show();
                } catch (RuntimeException e) {
                    Log.m28e(TAG, "Cannot show the alert dialog, error message: %s", message, e);
                }
            }
        }
    }

    @CalledByNative
    private void onVideoSizeChanged(int width, int height) {
        boolean z = true;
        this.mVideoWidth = width;
        this.mVideoHeight = height;
        this.mVideoSurfaceView.getHolder().setFixedSize(this.mVideoWidth, this.mVideoHeight);
        if (!this.mUmaRecorded) {
            this.mProgressView.setVisibility(8);
            try {
                if (System.getInt(getContext().getContentResolver(), "accelerometer_rotation") != 0) {
                    this.mInitialOrientation = isOrientationPortrait();
                    this.mUmaRecorded = true;
                    this.mPlaybackStartTime = System.currentTimeMillis();
                    this.mOrientationChangedTime = this.mPlaybackStartTime;
                    long j = this.mNativeContentVideoView;
                    if (this.mVideoHeight <= this.mVideoWidth) {
                        z = false;
                    }
                    nativeRecordFullscreenPlayback(j, z, this.mInitialOrientation);
                }
            } catch (SettingNotFoundException e) {
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mSurfaceHolder = holder;
        openVideo();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.mNativeContentVideoView != 0) {
            nativeSetSurface(this.mNativeContentVideoView, null);
        }
        this.mSurfaceHolder = null;
        post(this.mExitFullscreenRunnable);
    }

    @CalledByNative
    private void openVideo() {
        if (this.mSurfaceHolder != null) {
            this.mCurrentState = 0;
            if (this.mNativeContentVideoView != 0) {
                nativeSetSurface(this.mNativeContentVideoView, this.mSurfaceHolder.getSurface());
            }
        }
    }

    @CalledByNative
    private static ContentVideoView createContentVideoView(ContentViewCore contentViewCore, long nativeContentVideoView) {
        ThreadUtils.assertOnUiThread();
        Context context = contentViewCore.getContext();
        ContentVideoViewEmbedder embedder = contentViewCore.getContentVideoViewEmbedder();
        ContentVideoView videoView = new ContentVideoView(context, nativeContentVideoView, embedder);
        embedder.enterFullscreenVideo(videoView);
        return videoView;
    }

    public void removeSurfaceView() {
        removeView(this.mVideoSurfaceView);
        removeView(this.mProgressView);
        this.mVideoSurfaceView = null;
        this.mProgressView = null;
    }

    @CalledByNative
    public void exitFullscreen(boolean releaseMediaPlayer) {
        if (this.mNativeContentVideoView != 0) {
            destroyContentVideoView(false);
            if (this.mUmaRecorded && !this.mPossibleAccidentalChange) {
                long timeBeforeOrientationChange = this.mOrientationChangedTime - this.mPlaybackStartTime;
                long timeAfterOrientationChange = System.currentTimeMillis() - this.mOrientationChangedTime;
                if (timeBeforeOrientationChange == 0) {
                    timeBeforeOrientationChange = timeAfterOrientationChange;
                    timeAfterOrientationChange = 0;
                }
                nativeRecordExitFullscreenPlayback(this.mNativeContentVideoView, this.mInitialOrientation, timeBeforeOrientationChange, timeAfterOrientationChange);
            }
            nativeDidExitFullscreen(this.mNativeContentVideoView, releaseMediaPlayer);
            this.mNativeContentVideoView = 0;
        }
    }

    public void onFullscreenWindowFocused() {
        this.mEmbedder.setSystemUiVisibility(true);
    }

    @CalledByNative
    private void destroyContentVideoView(boolean nativeViewDestroyed) {
        if (this.mVideoSurfaceView != null) {
            removeSurfaceView();
            setVisibility(8);
            this.mEmbedder.exitFullscreenVideo();
        }
        if (nativeViewDestroyed) {
            this.mNativeContentVideoView = 0;
        }
    }

    public static ContentVideoView getContentVideoView() {
        return nativeGetSingletonJavaContentVideoView();
    }

    private boolean isOrientationPortrait() {
        Display display = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay();
        Point outputSize = new Point(0, 0);
        display.getSize(outputSize);
        if (outputSize.x <= outputSize.y) {
            return true;
        }
        return false;
    }
}
