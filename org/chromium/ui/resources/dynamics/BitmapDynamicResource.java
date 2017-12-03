package org.chromium.ui.resources.dynamics;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class BitmapDynamicResource implements DynamicResource {
    private static final Rect EMPTY_RECT = new Rect();
    private Bitmap mBitmap;
    private boolean mIsDirty = true;
    private final int mResId;
    private final Rect mSize = new Rect();

    public BitmapDynamicResource(int resourceId) {
        this.mResId = resourceId;
    }

    public int getResId() {
        return this.mResId;
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.mIsDirty = true;
            if (this.mBitmap != null) {
                this.mBitmap.recycle();
            }
            this.mBitmap = bitmap;
            this.mSize.set(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
        }
    }

    public Bitmap getBitmap() {
        this.mIsDirty = false;
        return this.mBitmap;
    }

    public Rect getBitmapSize() {
        return this.mSize;
    }

    public Rect getPadding() {
        return EMPTY_RECT;
    }

    public Rect getAperture() {
        return EMPTY_RECT;
    }

    public boolean isDirty() {
        return this.mIsDirty;
    }
}
