package org.xwalk.core.internal;

import android.view.KeyEvent;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.components.web_contents_delegate_android.WebContentsDelegateAndroid;

@JNINamespace("xwalk")
abstract class XWalkWebContentsDelegate extends WebContentsDelegateAndroid {
    @CalledByNative
    public abstract void activateContents();

    @CalledByNative
    public abstract boolean addMessageToConsole(int i, String str, int i2, String str2);

    @CalledByNative
    public abstract boolean addNewContents(boolean z, boolean z2);

    @CalledByNative
    public abstract void closeContents();

    @CalledByNative
    public abstract void handleKeyboardEvent(KeyEvent keyEvent);

    @CalledByNative
    public abstract void rendererResponsive();

    @CalledByNative
    public abstract void rendererUnresponsive();

    @CalledByNative
    public abstract boolean shouldOverrideRunFileChooser(int i, int i2, int i3, String str, boolean z);

    XWalkWebContentsDelegate() {
    }

    @CalledByNative
    public boolean shouldCreateWebContents(String targetUrl) {
        return true;
    }

    @CalledByNative
    public void updatePreferredSize(int widthCss, int heightCss) {
    }

    @CalledByNative
    public void toggleFullscreen(boolean enterFullscreen) {
    }

    @CalledByNative
    public boolean isFullscreen() {
        return false;
    }
}
