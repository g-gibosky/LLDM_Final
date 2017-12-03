package org.chromium.ui.resources.dynamics;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import org.chromium.base.TraceEvent;

public class ViewResourceAdapter implements DynamicResource, OnLayoutChangeListener {
    static final /* synthetic */ boolean $assertionsDisabled = (!ViewResourceAdapter.class.desiredAssertionStatus());
    private Bitmap mBitmap;
    private Rect mBitmapSize = new Rect();
    private final Rect mContentAperture = new Rect();
    private final Rect mContentPadding = new Rect();
    private final Rect mDirtyRect = new Rect();
    private final View mView;

    public ViewResourceAdapter(View view) {
        this.mView = view;
        this.mView.addOnLayoutChangeListener(this);
    }

    public Bitmap getBitmap() {
        if (!isDirty()) {
            return this.mBitmap;
        }
        TraceEvent.begin("ViewResourceAdapter:getBitmap");
        if (validateBitmap()) {
            Canvas canvas = new Canvas(this.mBitmap);
            onCaptureStart(canvas, this.mDirtyRect.isEmpty() ? null : this.mDirtyRect);
            if (!this.mDirtyRect.isEmpty()) {
                canvas.clipRect(this.mDirtyRect);
            }
            capture(canvas);
            onCaptureEnd();
        } else if ($assertionsDisabled || (this.mBitmap.getWidth() == 1 && this.mBitmap.getHeight() == 1)) {
            this.mBitmap.setPixel(0, 0, 0);
        } else {
            throw new AssertionError();
        }
        this.mDirtyRect.setEmpty();
        TraceEvent.end("ViewResourceAdapter:getBitmap");
        return this.mBitmap;
    }

    public Rect getBitmapSize() {
        return this.mBitmapSize;
    }

    public Rect getPadding() {
        computeContentPadding(this.mContentPadding);
        return this.mContentPadding;
    }

    public Rect getAperture() {
        computeContentAperture(this.mContentAperture);
        return this.mContentAperture;
    }

    public boolean isDirty() {
        if (this.mBitmap == null) {
            this.mDirtyRect.set(0, 0, this.mView.getWidth(), this.mView.getHeight());
        }
        if (this.mDirtyRect.isEmpty()) {
            return false;
        }
        return true;
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        int width = right - left;
        int height = bottom - top;
        int oldHeight = oldBottom - oldTop;
        if (width != oldRight - oldLeft || height != oldHeight) {
            this.mDirtyRect.set(0, 0, width, height);
        }
    }

    public void invalidate(Rect dirtyRect) {
        if (dirtyRect == null) {
            this.mDirtyRect.set(0, 0, this.mView.getWidth(), this.mView.getHeight());
        } else {
            this.mDirtyRect.union(dirtyRect);
        }
    }

    protected Rect getDirtyRect() {
        return this.mDirtyRect;
    }

    protected void onCaptureStart(Canvas canvas, Rect dirtyRect) {
    }

    protected void capture(Canvas canvas) {
        this.mView.draw(canvas);
    }

    protected void onCaptureEnd() {
    }

    protected void computeContentPadding(Rect outContentPadding) {
        outContentPadding.set(0, 0, this.mView.getWidth(), this.mView.getHeight());
    }

    protected void computeContentAperture(Rect outContentAperture) {
        outContentAperture.set(0, 0, this.mView.getWidth(), this.mView.getHeight());
    }

    private boolean validateBitmap() {
        boolean isEmpty;
        int viewWidth = this.mView.getWidth();
        int viewHeight = this.mView.getHeight();
        if (viewWidth == 0 || viewHeight == 0) {
            isEmpty = true;
        } else {
            isEmpty = false;
        }
        if (isEmpty) {
            viewWidth = 1;
            viewHeight = 1;
        }
        if (!(this.mBitmap == null || (this.mBitmap.getWidth() == viewWidth && this.mBitmap.getHeight() == viewHeight))) {
            this.mBitmap.recycle();
            this.mBitmap = null;
        }
        if (this.mBitmap == null) {
            this.mBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Config.ARGB_8888);
            this.mBitmap.setHasAlpha(true);
            this.mDirtyRect.set(0, 0, viewWidth, viewHeight);
            this.mBitmapSize.set(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
        }
        if (isEmpty) {
            return false;
        }
        return true;
    }
}
