package org.chromium.content.browser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.Log;
import org.chromium.content.C0174R;

class PopupZoomer extends View {
    private static final long ANIMATION_DURATION = 300;
    private static final String TAG = "cr.PopupZoomer";
    private static final int ZOOM_BOUNDS_MARGIN = 25;
    private static float sOverlayCornerRadius;
    private static Drawable sOverlayDrawable;
    private static Rect sOverlayPadding;
    private boolean mAnimating = false;
    private long mAnimationStartTime = 0;
    private float mBottomExtrusion;
    private RectF mClipRect;
    private RectF mDrawRect;
    private GestureDetector mGestureDetector;
    private final Interpolator mHideInterpolator = new ReverseInterpolator(this.mShowInterpolator);
    private float mLeftExtrusion;
    private float mMaxScrollX;
    private float mMaxScrollY;
    private float mMinScrollX;
    private float mMinScrollY;
    private boolean mNeedsToInitDimensions;
    private OnTapListener mOnTapListener = null;
    private OnVisibilityChangedListener mOnVisibilityChangedListener = null;
    private float mPopupScrollX;
    private float mPopupScrollY;
    private float mRightExtrusion;
    private float mScale = 1.0f;
    private float mShiftX = 0.0f;
    private float mShiftY = 0.0f;
    private final Interpolator mShowInterpolator = new OvershootInterpolator();
    private boolean mShowing = false;
    private Rect mTargetBounds;
    private long mTimeLeft = 0;
    private float mTopExtrusion;
    private final PointF mTouch = new PointF();
    private RectF mViewClipRect;
    private Bitmap mZoomedBitmap;

    class C01991 extends SimpleOnGestureListener {
        C01991() {
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!PopupZoomer.this.mAnimating) {
                if (PopupZoomer.this.isTouchOutsideArea(e1.getX(), e1.getY())) {
                    PopupZoomer.this.hide(true);
                } else {
                    PopupZoomer.this.scroll(distanceX, distanceY);
                }
            }
            return true;
        }

        public boolean onSingleTapUp(MotionEvent e) {
            return handleTapOrPress(e, false);
        }

        public void onLongPress(MotionEvent e) {
            handleTapOrPress(e, true);
        }

        private boolean handleTapOrPress(MotionEvent e, boolean isLongPress) {
            if (!PopupZoomer.this.mAnimating) {
                float x = e.getX();
                float y = e.getY();
                if (PopupZoomer.this.isTouchOutsideArea(x, y)) {
                    PopupZoomer.this.hide(true);
                } else if (PopupZoomer.this.mOnTapListener != null) {
                    PointF converted = PopupZoomer.this.convertTouchPoint(x, y);
                    MotionEvent event = MotionEvent.obtainNoHistory(e);
                    event.setLocation(converted.x, converted.y);
                    if (isLongPress) {
                        PopupZoomer.this.mOnTapListener.onLongPress(PopupZoomer.this, event);
                    } else {
                        PopupZoomer.this.mOnTapListener.onSingleTap(PopupZoomer.this, event);
                    }
                    PopupZoomer.this.hide(true);
                }
            }
            return true;
        }
    }

    public interface OnTapListener {
        boolean onLongPress(View view, MotionEvent motionEvent);

        boolean onSingleTap(View view, MotionEvent motionEvent);
    }

    public interface OnVisibilityChangedListener {
        void onPopupZoomerHidden(PopupZoomer popupZoomer);

        void onPopupZoomerShown(PopupZoomer popupZoomer);
    }

    private static class ReverseInterpolator implements Interpolator {
        private final Interpolator mInterpolator;

        public ReverseInterpolator(Interpolator i) {
            this.mInterpolator = i;
        }

        public float getInterpolation(float input) {
            input = 1.0f - input;
            return this.mInterpolator == null ? input : this.mInterpolator.getInterpolation(input);
        }
    }

    private static float getOverlayCornerRadius(Context context) {
        if (sOverlayCornerRadius == 0.0f) {
            try {
                sOverlayCornerRadius = context.getResources().getDimension(C0174R.dimen.link_preview_overlay_radius);
            } catch (NotFoundException e) {
                Log.m38w(TAG, "No corner radius resource for PopupZoomer overlay found.", new Object[0]);
                sOverlayCornerRadius = 1.0f;
            }
        }
        return sOverlayCornerRadius;
    }

    private static Drawable getOverlayDrawable(Context context) {
        if (sOverlayDrawable == null) {
            try {
                sOverlayDrawable = ApiCompatibilityUtils.getDrawable(context.getResources(), C0174R.drawable.ondemand_overlay);
            } catch (NotFoundException e) {
                Log.m38w(TAG, "No drawable resource for PopupZoomer overlay found.", new Object[0]);
                sOverlayDrawable = new ColorDrawable();
            }
            sOverlayPadding = new Rect();
            sOverlayDrawable.getPadding(sOverlayPadding);
        }
        return sOverlayDrawable;
    }

    private static float constrain(float amount, float low, float high) {
        if (amount < low) {
            return low;
        }
        return amount > high ? high : amount;
    }

    private static int constrain(int amount, int low, int high) {
        if (amount < low) {
            return low;
        }
        return amount > high ? high : amount;
    }

    public PopupZoomer(Context context) {
        super(context);
        setVisibility(4);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.mGestureDetector = new GestureDetector(context, new C01991());
    }

    public void setOnTapListener(OnTapListener listener) {
        this.mOnTapListener = listener;
    }

    public void setOnVisibilityChangedListener(OnVisibilityChangedListener listener) {
        this.mOnVisibilityChangedListener = listener;
    }

    public void setBitmap(Bitmap bitmap) {
        if (this.mZoomedBitmap != null) {
            this.mZoomedBitmap.recycle();
            this.mZoomedBitmap = null;
        }
        this.mZoomedBitmap = bitmap;
        Canvas canvas = new Canvas(this.mZoomedBitmap);
        Path path = new Path();
        RectF canvasRect = new RectF(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight());
        float overlayCornerRadius = getOverlayCornerRadius(getContext());
        path.addRoundRect(canvasRect, overlayCornerRadius, overlayCornerRadius, Direction.CCW);
        canvas.clipPath(path, Op.XOR);
        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
        clearPaint.setColor(0);
        canvas.drawPaint(clearPaint);
    }

    private void scroll(float x, float y) {
        this.mPopupScrollX = constrain(this.mPopupScrollX - x, this.mMinScrollX, this.mMaxScrollX);
        this.mPopupScrollY = constrain(this.mPopupScrollY - y, this.mMinScrollY, this.mMaxScrollY);
        invalidate();
    }

    private void startAnimation(boolean show) {
        this.mAnimating = true;
        this.mShowing = show;
        this.mTimeLeft = 0;
        if (show) {
            setVisibility(0);
            this.mNeedsToInitDimensions = true;
            if (this.mOnVisibilityChangedListener != null) {
                this.mOnVisibilityChangedListener.onPopupZoomerShown(this);
            }
        } else {
            this.mTimeLeft = (this.mAnimationStartTime + ANIMATION_DURATION) - SystemClock.uptimeMillis();
            if (this.mTimeLeft < 0) {
                this.mTimeLeft = 0;
            }
        }
        this.mAnimationStartTime = SystemClock.uptimeMillis();
        invalidate();
    }

    private void hideImmediately() {
        this.mAnimating = false;
        this.mShowing = false;
        this.mTimeLeft = 0;
        if (this.mOnVisibilityChangedListener != null) {
            this.mOnVisibilityChangedListener.onPopupZoomerHidden(this);
        }
        setVisibility(4);
        this.mZoomedBitmap.recycle();
        this.mZoomedBitmap = null;
    }

    public boolean isShowing() {
        return this.mShowing || this.mAnimating;
    }

    public void setLastTouch(float x, float y) {
        this.mTouch.x = x;
        this.mTouch.y = y;
    }

    private void setTargetBounds(Rect rect) {
        this.mTargetBounds = rect;
    }

    private void initDimensions() {
        if (this.mTargetBounds != null && this.mTouch != null) {
            RectF rectF;
            this.mScale = ((float) this.mZoomedBitmap.getWidth()) / ((float) this.mTargetBounds.width());
            float l = this.mTouch.x - (this.mScale * (this.mTouch.x - ((float) this.mTargetBounds.left)));
            float t = this.mTouch.y - (this.mScale * (this.mTouch.y - ((float) this.mTargetBounds.top)));
            this.mClipRect = new RectF(l, t, l + ((float) this.mZoomedBitmap.getWidth()), t + ((float) this.mZoomedBitmap.getHeight()));
            int width = getWidth();
            int height = getHeight();
            this.mViewClipRect = new RectF(25.0f, 25.0f, (float) (width - 25), (float) (height - 25));
            this.mShiftX = 0.0f;
            this.mShiftY = 0.0f;
            if (this.mClipRect.left < 25.0f) {
                this.mShiftX = 25.0f - this.mClipRect.left;
                rectF = this.mClipRect;
                rectF.left += this.mShiftX;
                rectF = this.mClipRect;
                rectF.right += this.mShiftX;
            } else if (this.mClipRect.right > ((float) (width - 25))) {
                this.mShiftX = ((float) (width - 25)) - this.mClipRect.right;
                rectF = this.mClipRect;
                rectF.right += this.mShiftX;
                rectF = this.mClipRect;
                rectF.left += this.mShiftX;
            }
            if (this.mClipRect.top < 25.0f) {
                this.mShiftY = 25.0f - this.mClipRect.top;
                rectF = this.mClipRect;
                rectF.top += this.mShiftY;
                rectF = this.mClipRect;
                rectF.bottom += this.mShiftY;
            } else if (this.mClipRect.bottom > ((float) (height - 25))) {
                this.mShiftY = ((float) (height - 25)) - this.mClipRect.bottom;
                rectF = this.mClipRect;
                rectF.bottom += this.mShiftY;
                rectF = this.mClipRect;
                rectF.top += this.mShiftY;
            }
            this.mMaxScrollY = 0.0f;
            this.mMinScrollY = 0.0f;
            this.mMaxScrollX = 0.0f;
            this.mMinScrollX = 0.0f;
            if (this.mViewClipRect.right + this.mShiftX < this.mClipRect.right) {
                this.mMinScrollX = this.mViewClipRect.right - this.mClipRect.right;
            }
            if (this.mViewClipRect.left + this.mShiftX > this.mClipRect.left) {
                this.mMaxScrollX = this.mViewClipRect.left - this.mClipRect.left;
            }
            if (this.mViewClipRect.top + this.mShiftY > this.mClipRect.top) {
                this.mMaxScrollY = this.mViewClipRect.top - this.mClipRect.top;
            }
            if (this.mViewClipRect.bottom + this.mShiftY < this.mClipRect.bottom) {
                this.mMinScrollY = this.mViewClipRect.bottom - this.mClipRect.bottom;
            }
            this.mClipRect.intersect(this.mViewClipRect);
            this.mLeftExtrusion = this.mTouch.x - this.mClipRect.left;
            this.mRightExtrusion = this.mClipRect.right - this.mTouch.x;
            this.mTopExtrusion = this.mTouch.y - this.mClipRect.top;
            this.mBottomExtrusion = this.mClipRect.bottom - this.mTouch.y;
            float percentY = ((this.mTouch.y - ((float) this.mTargetBounds.centerY())) / (((float) this.mTargetBounds.height()) / 2.0f)) + 0.5f;
            float scrollWidth = this.mMaxScrollX - this.mMinScrollX;
            float scrollHeight = this.mMaxScrollY - this.mMinScrollY;
            this.mPopupScrollX = (scrollWidth * (((this.mTouch.x - ((float) this.mTargetBounds.centerX())) / (((float) this.mTargetBounds.width()) / 2.0f)) + 0.5f)) * -1.0f;
            this.mPopupScrollY = (scrollHeight * percentY) * -1.0f;
            this.mPopupScrollX = constrain(this.mPopupScrollX, this.mMinScrollX, this.mMaxScrollX);
            this.mPopupScrollY = constrain(this.mPopupScrollY, this.mMinScrollY, this.mMaxScrollY);
            this.mDrawRect = new RectF();
        }
    }

    protected boolean acceptZeroSizeView() {
        return false;
    }

    protected void onDraw(Canvas canvas) {
        if (isShowing() && this.mZoomedBitmap != null) {
            if (acceptZeroSizeView() || !(getWidth() == 0 || getHeight() == 0)) {
                float fractionAnimation;
                if (this.mNeedsToInitDimensions) {
                    this.mNeedsToInitDimensions = false;
                    initDimensions();
                }
                canvas.save();
                float time = constrain(((float) ((SystemClock.uptimeMillis() - this.mAnimationStartTime) + this.mTimeLeft)) / 300.0f, 0.0f, 1.0f);
                if (time >= 1.0f) {
                    this.mAnimating = false;
                    if (!isShowing()) {
                        hideImmediately();
                        return;
                    }
                }
                invalidate();
                if (this.mShowing) {
                    fractionAnimation = this.mShowInterpolator.getInterpolation(time);
                } else {
                    fractionAnimation = this.mHideInterpolator.getInterpolation(time);
                }
                canvas.drawARGB((int) (80.0f * fractionAnimation), 0, 0, 0);
                canvas.save();
                float scale = (((this.mScale - 1.0f) * fractionAnimation) / this.mScale) + (1.0f / this.mScale);
                float unshiftX = ((-this.mShiftX) * (1.0f - fractionAnimation)) / this.mScale;
                float unshiftY = ((-this.mShiftY) * (1.0f - fractionAnimation)) / this.mScale;
                this.mDrawRect.left = (this.mTouch.x - (this.mLeftExtrusion * scale)) + unshiftX;
                this.mDrawRect.top = (this.mTouch.y - (this.mTopExtrusion * scale)) + unshiftY;
                this.mDrawRect.right = (this.mTouch.x + (this.mRightExtrusion * scale)) + unshiftX;
                this.mDrawRect.bottom = (this.mTouch.y + (this.mBottomExtrusion * scale)) + unshiftY;
                canvas.clipRect(this.mDrawRect);
                canvas.scale(scale, scale, this.mDrawRect.left, this.mDrawRect.top);
                canvas.translate(this.mPopupScrollX, this.mPopupScrollY);
                canvas.drawBitmap(this.mZoomedBitmap, this.mDrawRect.left, this.mDrawRect.top, null);
                canvas.restore();
                Drawable overlayNineTile = getOverlayDrawable(getContext());
                overlayNineTile.setBounds(((int) this.mDrawRect.left) - sOverlayPadding.left, ((int) this.mDrawRect.top) - sOverlayPadding.top, ((int) this.mDrawRect.right) + sOverlayPadding.right, ((int) this.mDrawRect.bottom) + sOverlayPadding.bottom);
                overlayNineTile.setAlpha(constrain((int) (255.0f * fractionAnimation), 0, 255));
                overlayNineTile.draw(canvas);
                canvas.restore();
            }
        }
    }

    public void show(Rect rect) {
        if (!this.mShowing && this.mZoomedBitmap != null) {
            setTargetBounds(rect);
            startAnimation(true);
        }
    }

    public void hide(boolean animation) {
        if (!this.mShowing) {
            return;
        }
        if (animation) {
            startAnimation(false);
        } else {
            hideImmediately();
        }
    }

    private PointF convertTouchPoint(float x, float y) {
        return new PointF(this.mTouch.x + ((((x - this.mShiftX) - this.mTouch.x) - this.mPopupScrollX) / this.mScale), this.mTouch.y + ((((y - this.mShiftY) - this.mTouch.y) - this.mPopupScrollY) / this.mScale));
    }

    private boolean isTouchOutsideArea(float x, float y) {
        return !this.mClipRect.contains(x, y);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        this.mGestureDetector.onTouchEvent(event);
        return true;
    }
}
