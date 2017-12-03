package org.chromium.media;

import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("media")
class PhotoCapabilities {
    public final int currentZoom;
    public final int maxZoom;
    public final int minZoom;

    PhotoCapabilities(int maxZoom, int minZoom, int currentZoom) {
        this.maxZoom = maxZoom;
        this.minZoom = minZoom;
        this.currentZoom = currentZoom;
    }

    @CalledByNative
    public int getMinZoom() {
        return this.minZoom;
    }

    @CalledByNative
    public int getMaxZoom() {
        return this.maxZoom;
    }

    @CalledByNative
    public int getCurrentZoom() {
        return this.currentZoom;
    }
}
