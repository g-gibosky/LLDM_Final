package org.chromium.content.browser;

import org.chromium.base.VisibleForTesting;

public class RenderCoordinates {
    private float mContentHeightCss;
    private float mContentOffsetYPix;
    private float mContentWidthCss;
    private float mDeviceScaleFactor;
    private boolean mHasFrameInfo;
    private float mLastFrameViewportHeightCss;
    private float mLastFrameViewportWidthCss;
    private float mMaxPageScaleFactor = 1.0f;
    private float mMinPageScaleFactor = 1.0f;
    private float mPageScaleFactor = 1.0f;
    private float mScrollXCss;
    private float mScrollYCss;

    public class NormalizedPoint {
        private float mXAbsoluteCss;
        private float mYAbsoluteCss;

        private NormalizedPoint() {
        }

        public float getXAbsoluteCss() {
            return this.mXAbsoluteCss;
        }

        public float getYAbsoluteCss() {
            return this.mYAbsoluteCss;
        }

        public float getXLocalDip() {
            return (this.mXAbsoluteCss - RenderCoordinates.this.mScrollXCss) * RenderCoordinates.this.mPageScaleFactor;
        }

        public float getYLocalDip() {
            return (this.mYAbsoluteCss - RenderCoordinates.this.mScrollYCss) * RenderCoordinates.this.mPageScaleFactor;
        }

        public float getXPix() {
            return getXLocalDip() * RenderCoordinates.this.mDeviceScaleFactor;
        }

        public float getYPix() {
            return (getYLocalDip() * RenderCoordinates.this.mDeviceScaleFactor) + RenderCoordinates.this.mContentOffsetYPix;
        }

        public void setAbsoluteCss(float xCss, float yCss) {
            this.mXAbsoluteCss = xCss;
            this.mYAbsoluteCss = yCss;
        }

        public void setLocalDip(float xDip, float yDip) {
            setAbsoluteCss((xDip / RenderCoordinates.this.mPageScaleFactor) + RenderCoordinates.this.mScrollXCss, (yDip / RenderCoordinates.this.mPageScaleFactor) + RenderCoordinates.this.mScrollYCss);
        }

        public void setScreen(float xPix, float yPix) {
            setLocalDip(xPix / RenderCoordinates.this.mDeviceScaleFactor, yPix / RenderCoordinates.this.mDeviceScaleFactor);
        }
    }

    void reset() {
        this.mScrollYCss = 0.0f;
        this.mScrollXCss = 0.0f;
        this.mPageScaleFactor = 1.0f;
        this.mHasFrameInfo = false;
    }

    void updateContentSizeCss(float contentWidthCss, float contentHeightCss) {
        this.mContentWidthCss = contentWidthCss;
        this.mContentHeightCss = contentHeightCss;
    }

    void setDeviceScaleFactor(float deviceScaleFactor) {
        this.mDeviceScaleFactor = deviceScaleFactor;
    }

    void updateFrameInfo(float scrollXCss, float scrollYCss, float contentWidthCss, float contentHeightCss, float viewportWidthCss, float viewportHeightCss, float pageScaleFactor, float minPageScaleFactor, float maxPageScaleFactor, float contentOffsetYPix) {
        this.mScrollXCss = scrollXCss;
        this.mScrollYCss = scrollYCss;
        this.mPageScaleFactor = pageScaleFactor;
        this.mMinPageScaleFactor = minPageScaleFactor;
        this.mMaxPageScaleFactor = maxPageScaleFactor;
        this.mContentOffsetYPix = contentOffsetYPix;
        updateContentSizeCss(contentWidthCss, contentHeightCss);
        this.mLastFrameViewportWidthCss = viewportWidthCss;
        this.mLastFrameViewportHeightCss = viewportHeightCss;
        this.mHasFrameInfo = true;
    }

    @VisibleForTesting
    public void setFrameInfoForTest(float deviceScaleFactor, float contentOffsetYPix) {
        reset();
        this.mDeviceScaleFactor = deviceScaleFactor;
        this.mContentOffsetYPix = contentOffsetYPix;
    }

    public NormalizedPoint createNormalizedPoint() {
        return new NormalizedPoint();
    }

    public float getScrollX() {
        return this.mScrollXCss;
    }

    public float getScrollY() {
        return this.mScrollYCss;
    }

    public float getScrollXPix() {
        return fromLocalCssToPix(this.mScrollXCss);
    }

    public float getScrollYPix() {
        return fromLocalCssToPix(this.mScrollYCss);
    }

    public int getScrollXPixInt() {
        return (int) Math.floor((double) getScrollXPix());
    }

    public int getScrollYPixInt() {
        return (int) Math.floor((double) getScrollYPix());
    }

    public float getContentWidthCss() {
        return this.mContentWidthCss;
    }

    public float getContentHeightCss() {
        return this.mContentHeightCss;
    }

    public float getContentWidthPix() {
        return fromLocalCssToPix(this.mContentWidthCss);
    }

    public float getContentHeightPix() {
        return fromLocalCssToPix(this.mContentHeightCss);
    }

    public int getContentWidthPixInt() {
        return (int) Math.ceil((double) getContentWidthPix());
    }

    public int getContentHeightPixInt() {
        return (int) Math.ceil((double) getContentHeightPix());
    }

    public float getLastFrameViewportWidthCss() {
        return this.mLastFrameViewportWidthCss;
    }

    public float getLastFrameViewportHeightCss() {
        return this.mLastFrameViewportHeightCss;
    }

    public float getLastFrameViewportWidthPix() {
        return fromLocalCssToPix(this.mLastFrameViewportWidthCss);
    }

    public float getLastFrameViewportHeightPix() {
        return fromLocalCssToPix(this.mLastFrameViewportHeightCss);
    }

    public int getLastFrameViewportWidthPixInt() {
        return (int) Math.ceil((double) getLastFrameViewportWidthPix());
    }

    public int getLastFrameViewportHeightPixInt() {
        return (int) Math.ceil((double) getLastFrameViewportHeightPix());
    }

    public float getContentOffsetYPix() {
        return this.mContentOffsetYPix;
    }

    public float getPageScaleFactor() {
        return this.mPageScaleFactor;
    }

    public float getMinPageScaleFactor() {
        return this.mMinPageScaleFactor;
    }

    public float getMaxPageScaleFactor() {
        return this.mMaxPageScaleFactor;
    }

    public float getDeviceScaleFactor() {
        return this.mDeviceScaleFactor;
    }

    public float getMaxHorizontalScrollPix() {
        return getContentWidthPix() - getLastFrameViewportWidthPix();
    }

    public float getMaxVerticalScrollPix() {
        return getContentHeightPix() - getLastFrameViewportHeightPix();
    }

    public int getMaxHorizontalScrollPixInt() {
        return (int) Math.floor((double) getMaxHorizontalScrollPix());
    }

    public int getMaxVerticalScrollPixInt() {
        return (int) Math.floor((double) getMaxVerticalScrollPix());
    }

    public boolean hasFrameInfo() {
        return this.mHasFrameInfo;
    }

    public float fromPixToDip(float pix) {
        return pix / this.mDeviceScaleFactor;
    }

    public float fromDipToPix(float dip) {
        return this.mDeviceScaleFactor * dip;
    }

    public float fromPixToLocalCss(float pix) {
        return pix / (this.mDeviceScaleFactor * this.mPageScaleFactor);
    }

    public float fromLocalCssToPix(float css) {
        return (this.mPageScaleFactor * css) * this.mDeviceScaleFactor;
    }
}
