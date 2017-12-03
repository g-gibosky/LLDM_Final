package org.chromium.ui;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

class DropdownDividerDrawable extends Drawable {
    private Rect mDividerRect = new Rect();
    private Paint mPaint = new Paint();

    public void draw(Canvas canvas) {
        canvas.drawRect(this.mDividerRect, this.mPaint);
    }

    public void onBoundsChange(Rect bounds) {
        this.mDividerRect.set(0, 0, bounds.width(), this.mDividerRect.height());
    }

    public void setHeight(int height) {
        this.mDividerRect.set(0, 0, this.mDividerRect.right, height);
    }

    public void setColor(int color) {
        this.mPaint.setColor(color);
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return -2;
    }
}
