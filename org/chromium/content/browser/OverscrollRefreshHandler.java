package org.chromium.content.browser;

public interface OverscrollRefreshHandler {
    void pull(float f);

    void release(boolean z);

    void reset();

    void setEnabled(boolean z);

    boolean start();
}
