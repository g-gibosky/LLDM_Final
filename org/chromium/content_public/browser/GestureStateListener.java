package org.chromium.content_public.browser;

public class GestureStateListener {
    public void onPinchStarted() {
    }

    public void onPinchEnded() {
    }

    public void onFlingStartGesture(int vx, int vy, int scrollOffsetY, int scrollExtentY) {
    }

    public void onFlingEndGesture(int scrollOffsetY, int scrollExtentY) {
    }

    public void onScrollUpdateGestureConsumed() {
    }

    public void onScrollStarted(int scrollOffsetY, int scrollExtentY) {
    }

    public void onScrollEnded(int scrollOffsetY, int scrollExtentY) {
    }

    public void onScrollOffsetOrExtentChanged(int scrollOffsetY, int scrollExtentY) {
    }

    public void onSingleTap(boolean consumed, int x, int y) {
    }

    public void onShowUnhandledTapUIIfNeeded(int x, int y) {
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
    }

    public void onDestroyed() {
    }
}
