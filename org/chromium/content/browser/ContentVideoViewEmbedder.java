package org.chromium.content.browser;

import android.view.View;

public interface ContentVideoViewEmbedder {
    void enterFullscreenVideo(View view);

    void exitFullscreenVideo();

    View getVideoLoadingProgressView();

    void setSystemUiVisibility(boolean z);
}
