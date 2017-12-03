package org.chromium.content.browser.accessibility.captioning;

import org.chromium.content.browser.accessibility.captioning.SystemCaptioningBridge.SystemCaptioningBridgeListener;

public class EmptyCaptioningBridge implements SystemCaptioningBridge {
    public void syncToListener(SystemCaptioningBridgeListener listener) {
    }

    public void addListener(SystemCaptioningBridgeListener listener) {
    }

    public void removeListener(SystemCaptioningBridgeListener listener) {
    }
}
