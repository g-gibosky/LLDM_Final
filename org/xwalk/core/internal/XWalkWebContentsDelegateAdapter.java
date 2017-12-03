package org.xwalk.core.internal;

import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ConsoleMessage;
import android.webkit.ConsoleMessage.MessageLevel;
import org.chromium.content.browser.ContentVideoView;

class XWalkWebContentsDelegateAdapter extends XWalkWebContentsDelegate {
    private static final String TAG = XWalkWebContentsDelegateAdapter.class.getName();
    private XWalkContentsClient mXWalkContentsClient;

    public XWalkWebContentsDelegateAdapter(XWalkContentsClient client) {
        this.mXWalkContentsClient = client;
    }

    public boolean shouldCreateWebContents(String contentUrl) {
        if (this.mXWalkContentsClient != null) {
            return this.mXWalkContentsClient.shouldCreateWebContents(contentUrl);
        }
        return super.shouldCreateWebContents(contentUrl);
    }

    public void onLoadProgressChanged(int progress) {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onProgressChanged(progress);
        }
    }

    public boolean addNewContents(boolean isDialog, boolean isUserGesture) {
        return this.mXWalkContentsClient.onCreateWindow(isDialog, isUserGesture);
    }

    public void closeContents() {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onCloseWindow();
        }
    }

    public void activateContents() {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onRequestFocus();
        }
    }

    public void rendererUnresponsive() {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onRendererUnresponsive();
        }
    }

    public void rendererResponsive() {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onRendererResponsive();
        }
    }

    public void handleKeyboardEvent(KeyEvent event) {
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onUnhandledKeyEvent(event);
        }
    }

    public boolean addMessageToConsole(int level, String message, int lineNumber, String sourceId) {
        if (this.mXWalkContentsClient == null) {
            return false;
        }
        MessageLevel messageLevel = MessageLevel.DEBUG;
        switch (level) {
            case 0:
                messageLevel = MessageLevel.TIP;
                break;
            case 1:
                messageLevel = MessageLevel.LOG;
                break;
            case 2:
                messageLevel = MessageLevel.WARNING;
                break;
            case 3:
                messageLevel = MessageLevel.ERROR;
                break;
            default:
                Log.w(TAG, "Unknown message level, defaulting to DEBUG");
                break;
        }
        return this.mXWalkContentsClient.onConsoleMessage(new ConsoleMessage(message, sourceId, lineNumber, messageLevel));
    }

    public void toggleFullscreen(boolean enterFullscreen) {
        if (!enterFullscreen) {
            ContentVideoView videoView = ContentVideoView.getContentVideoView();
            if (videoView != null) {
                videoView.exitFullscreen(false);
            }
        }
        if (this.mXWalkContentsClient != null) {
            this.mXWalkContentsClient.onToggleFullscreen(enterFullscreen);
        }
    }

    public boolean isFullscreen() {
        if (this.mXWalkContentsClient != null) {
            return this.mXWalkContentsClient.hasEnteredFullscreen();
        }
        return false;
    }

    public boolean shouldOverrideRunFileChooser(int processId, int renderId, int mode, String acceptTypes, boolean capture) {
        if (this.mXWalkContentsClient != null) {
            return this.mXWalkContentsClient.shouldOverrideRunFileChooser(processId, renderId, mode, acceptTypes, capture);
        }
        return false;
    }
}
