package org.chromium.content.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build.VERSION;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class ActivityContentVideoViewEmbedder implements ContentVideoViewEmbedder {
    private final Activity mActivity;
    private View mView;

    public ActivityContentVideoViewEmbedder(Activity activity) {
        this.mActivity = activity;
    }

    public void enterFullscreenVideo(View view) {
        ((FrameLayout) this.mActivity.getWindow().getDecorView()).addView(view, 0, new LayoutParams(-1, -1, 17));
        setSystemUiVisibility(true);
        this.mView = view;
    }

    public void exitFullscreenVideo() {
        ((FrameLayout) this.mActivity.getWindow().getDecorView()).removeView(this.mView);
        setSystemUiVisibility(false);
        this.mView = null;
    }

    public View getVideoLoadingProgressView() {
        return null;
    }

    @SuppressLint({"InlinedApi"})
    public void setSystemUiVisibility(boolean enterFullscreen) {
        View decor = this.mActivity.getWindow().getDecorView();
        if (enterFullscreen) {
            this.mActivity.getWindow().setFlags(1024, 1024);
        } else {
            this.mActivity.getWindow().clearFlags(1024);
        }
        if (VERSION.SDK_INT >= 19) {
            int systemUiVisibility = decor.getSystemUiVisibility();
            if (enterFullscreen) {
                systemUiVisibility |= 5638;
            } else {
                systemUiVisibility &= -5639;
            }
            decor.setSystemUiVisibility(systemUiVisibility);
        }
    }
}
