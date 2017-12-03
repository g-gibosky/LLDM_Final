package org.chromium.ui.resources;

import android.graphics.Rect;
import android.graphics.RectF;

public class LayoutResource {
    private final RectF mAperture;
    private final RectF mBitmapSize;
    private final RectF mPadding;

    public LayoutResource(float pxToDp, Resource resource) {
        Rect padding = resource.getPadding();
        Rect bitmapSize = resource.getBitmapSize();
        Rect aperture = resource.getAperture();
        this.mPadding = new RectF(((float) padding.left) * pxToDp, ((float) padding.top) * pxToDp, ((float) padding.right) * pxToDp, ((float) padding.bottom) * pxToDp);
        this.mBitmapSize = new RectF(((float) bitmapSize.left) * pxToDp, ((float) bitmapSize.top) * pxToDp, ((float) bitmapSize.right) * pxToDp, ((float) bitmapSize.bottom) * pxToDp);
        this.mAperture = new RectF(((float) aperture.left) * pxToDp, ((float) aperture.top) * pxToDp, ((float) aperture.right) * pxToDp, ((float) aperture.bottom) * pxToDp);
    }

    public RectF getPadding() {
        return this.mPadding;
    }

    public RectF getBitmapSize() {
        return this.mBitmapSize;
    }

    public RectF getAperture() {
        return this.mAperture;
    }
}
