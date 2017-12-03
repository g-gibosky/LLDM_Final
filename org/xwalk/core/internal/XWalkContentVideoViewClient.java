package org.xwalk.core.internal;

import android.view.View;
import org.chromium.content.browser.ContentVideoViewEmbedder;

class XWalkContentVideoViewClient implements ContentVideoViewEmbedder {
    private XWalkContentsClient mContentsClient;
    private XWalkViewInternal mView;

    public XWalkContentVideoViewClient(XWalkContentsClient client, XWalkViewInternal view) {
        this.mContentsClient = client;
        this.mView = view;
    }

    public void enterFullscreenVideo(View view) {
        this.mView.setOverlayVideoMode(true);
        this.mContentsClient.onShowCustomView(view, new CustomViewCallbackHandlerInternal());
    }

    public void exitFullscreenVideo() {
        this.mView.setOverlayVideoMode(false);
        this.mContentsClient.onHideCustomView();
    }

    public View getVideoLoadingProgressView() {
        return null;
    }

    public void setSystemUiVisibility(boolean enterFullscreen) {
    }
}
