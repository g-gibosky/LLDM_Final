package org.chromium.content.browser.input;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.support.v4.view.PointerIconCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.BadTokenException;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import java.lang.reflect.InvocationTargetException;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content.browser.ContainerViewObserver;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content.browser.PositionObserver;
import org.chromium.content.browser.PositionObserver.Listener;
import org.chromium.content.browser.ViewPositionObserver;
import org.chromium.content_public.browser.GestureStateListener;

@JNINamespace("content")
public class PopupTouchHandleDrawable extends View {
    static final /* synthetic */ boolean $assertionsDisabled = (!PopupTouchHandleDrawable.class.desiredAssertionStatus());
    private static final int FADE_IN_DURATION_MS = 200;
    private static final int MOVING_FADE_IN_DELAY_MS = 300;
    private float mAlpha;
    private boolean mAttachedToWindow;
    private final PopupWindow mContainer;
    private ContentViewCore mContentViewCore;
    private Runnable mDeferredHandleFadeInRunnable;
    private boolean mDelayVisibilityUpdateWAR;
    private Drawable mDrawable;
    private long mFadeStartTime;
    private boolean mFocused;
    private final GestureStateListener mGestureStateListener;
    private boolean mHasPendingInvalidate;
    private Runnable mInvalidationRunnable;
    private boolean mMirrorHorizontal;
    private boolean mMirrorVertical;
    private int mOrientation = 3;
    private final Listener mParentPositionListener;
    private PositionObserver mParentPositionObserver;
    private int mParentPositionX;
    private int mParentPositionY;
    private final ContainerViewObserver mParentViewObserver;
    private int mPositionX;
    private int mPositionY;
    private boolean mScrolling;
    private final int[] mTempScreenCoords = new int[2];
    private boolean mTemporarilyHidden;
    private long mTemporarilyHiddenExpireTime;
    private Runnable mTemporarilyHiddenExpiredRunnable;
    private boolean mVisible;
    private boolean mWasShowingAllowed;

    class C02164 implements Runnable {
        C02164() {
        }

        public void run() {
            PopupTouchHandleDrawable.this.setTemporarilyHidden(false);
        }
    }

    class C02175 implements Runnable {
        C02175() {
        }

        public void run() {
            PopupTouchHandleDrawable.this.beginFadeIn();
        }
    }

    class C02186 implements Runnable {
        C02186() {
        }

        public void run() {
            PopupTouchHandleDrawable.this.mHasPendingInvalidate = false;
            PopupTouchHandleDrawable.this.doInvalidate();
        }
    }

    class C04481 implements Listener {
        C04481() {
        }

        public void onPositionChanged(int x, int y) {
            PopupTouchHandleDrawable.this.updateParentPosition(x, y);
        }
    }

    class C04492 extends ContainerViewObserver {
        C04492() {
        }

        public void onContainerViewChanged(ViewGroup newContainerView) {
            PopupTouchHandleDrawable.this.mParentPositionObserver.clearListener();
            PopupTouchHandleDrawable.this.mParentPositionObserver = new ViewPositionObserver(newContainerView);
            if (PopupTouchHandleDrawable.this.mContainer.isShowing()) {
                PopupTouchHandleDrawable.this.mParentPositionObserver.addListener(PopupTouchHandleDrawable.this.mParentPositionListener);
            }
        }
    }

    class C04503 extends GestureStateListener {
        C04503() {
        }

        public void onScrollStarted(int scrollOffsetX, int scrollOffsetY) {
            PopupTouchHandleDrawable.this.setIsScrolling(true);
        }

        public void onScrollEnded(int scrollOffsetX, int scrollOffsetY) {
            PopupTouchHandleDrawable.this.setIsScrolling(false);
        }

        public void onFlingStartGesture(int vx, int vy, int scrollOffsetY, int scrollExtentY) {
            PopupTouchHandleDrawable.this.setIsScrolling(false);
        }

        public void onScrollOffsetOrExtentChanged(int scrollOffsetY, int scrollExtentY) {
            PopupTouchHandleDrawable.this.temporarilyHide();
        }

        public void onWindowFocusChanged(boolean hasWindowFocus) {
            PopupTouchHandleDrawable.this.setIsFocused(hasWindowFocus);
        }

        public void onDestroyed() {
            PopupTouchHandleDrawable.this.destroy();
        }
    }

    private PopupTouchHandleDrawable(ContentViewCore contentViewCore) {
        super(contentViewCore.getContainerView().getContext());
        this.mContentViewCore = contentViewCore;
        this.mContainer = new PopupWindow((Context) this.mContentViewCore.getWindowAndroid().getContext().get(), null, 16843464);
        this.mContainer.setSplitTouchEnabled(true);
        this.mContainer.setClippingEnabled(false);
        this.mContainer.setAnimationStyle(0);
        setWindowLayoutType(this.mContainer, PointerIconCompat.TYPE_HAND);
        this.mContainer.setWidth(-2);
        this.mContainer.setHeight(-2);
        this.mAlpha = 1.0f;
        this.mVisible = getVisibility() == 0;
        this.mFocused = this.mContentViewCore.getContainerView().hasWindowFocus();
        this.mParentPositionObserver = new ViewPositionObserver(this.mContentViewCore.getContainerView());
        this.mParentPositionListener = new C04481();
        this.mParentViewObserver = new C04492();
        this.mGestureStateListener = new C04503();
        this.mContentViewCore.addGestureStateListener(this.mGestureStateListener);
        this.mContentViewCore.addContainerViewObserver(this.mParentViewObserver);
    }

    private static void setWindowLayoutType(PopupWindow window, int layoutType) {
        if (VERSION.SDK_INT >= 23) {
            window.setWindowLayoutType(layoutType);
            return;
        }
        try {
            PopupWindow.class.getMethod("setWindowLayoutType", new Class[]{Integer.TYPE}).invoke(window, new Object[]{Integer.valueOf(layoutType)});
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e2) {
        } catch (InvocationTargetException e3) {
        } catch (RuntimeException e4) {
        }
    }

    private static Drawable getHandleDrawable(Context context, int orientation) {
        switch (orientation) {
            case 0:
                return HandleViewResources.getLeftHandleDrawable(context);
            case 1:
                return HandleViewResources.getCenterHandleDrawable(context);
            case 2:
                return HandleViewResources.getRightHandleDrawable(context);
            default:
                if ($assertionsDisabled) {
                    return HandleViewResources.getCenterHandleDrawable(context);
                }
                throw new AssertionError();
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mContentViewCore == null) {
            return false;
        }
        this.mContentViewCore.getContainerView().getLocationOnScreen(this.mTempScreenCoords);
        float offsetX = (event.getRawX() - event.getX()) - ((float) this.mTempScreenCoords[0]);
        float offsetY = (event.getRawY() - event.getY()) - ((float) this.mTempScreenCoords[1]);
        MotionEvent offsetEvent = MotionEvent.obtainNoHistory(event);
        offsetEvent.offsetLocation(offsetX, offsetY);
        boolean handled = this.mContentViewCore.onTouchHandleEvent(offsetEvent);
        offsetEvent.recycle();
        return handled;
    }

    @CalledByNative
    private static PopupTouchHandleDrawable create(ContentViewCore contentViewCore) {
        return new PopupTouchHandleDrawable(contentViewCore);
    }

    @CalledByNative
    private void setOrientation(int orientation, boolean mirrorVertical, boolean mirrorHorizontal) {
        if ($assertionsDisabled || (orientation >= 0 && orientation <= 3)) {
            boolean orientationChanged;
            if (this.mOrientation != orientation) {
                orientationChanged = true;
            } else {
                orientationChanged = false;
            }
            boolean mirroringChanged;
            if (this.mMirrorHorizontal == mirrorHorizontal && this.mMirrorVertical == mirrorVertical) {
                mirroringChanged = false;
            } else {
                mirroringChanged = true;
            }
            this.mOrientation = orientation;
            this.mMirrorHorizontal = mirrorHorizontal;
            this.mMirrorVertical = mirrorVertical;
            if (orientationChanged) {
                this.mDrawable = getHandleDrawable(getContext(), this.mOrientation);
            }
            if (this.mDrawable != null) {
                this.mDrawable.setAlpha((int) (255.0f * this.mAlpha));
            }
            if (orientationChanged || mirroringChanged) {
                scheduleInvalidate();
                return;
            }
            return;
        }
        throw new AssertionError();
    }

    private void updateParentPosition(int parentPositionX, int parentPositionY) {
        if (this.mParentPositionX != parentPositionX || this.mParentPositionY != parentPositionY) {
            this.mParentPositionX = parentPositionX;
            this.mParentPositionY = parentPositionY;
            temporarilyHide();
        }
    }

    private int getContainerPositionX() {
        return this.mParentPositionX + this.mPositionX;
    }

    private int getContainerPositionY() {
        return this.mParentPositionY + this.mPositionY;
    }

    private void updatePosition() {
        this.mContainer.update(getContainerPositionX(), getContainerPositionY(), getRight() - getLeft(), getBottom() - getTop());
    }

    private boolean isShowingAllowed() {
        return this.mAttachedToWindow && this.mVisible && this.mFocused && !this.mScrolling && !this.mTemporarilyHidden;
    }

    private void updateVisibility() {
        int newVisibility = isShowingAllowed() ? 0 : 4;
        if (newVisibility != 0 || getVisibility() == 0 || this.mDelayVisibilityUpdateWAR) {
            this.mDelayVisibilityUpdateWAR = false;
            setVisibility(newVisibility);
            return;
        }
        this.mDelayVisibilityUpdateWAR = true;
        scheduleInvalidate();
    }

    private void setIsScrolling(boolean scrolling) {
        if (this.mScrolling != scrolling) {
            this.mScrolling = scrolling;
            onVisibilityInputChanged();
        }
    }

    private void setIsFocused(boolean focused) {
        if (this.mFocused != focused) {
            this.mFocused = focused;
            onVisibilityInputChanged();
        }
    }

    private void setTemporarilyHidden(boolean hidden) {
        if (this.mTemporarilyHidden != hidden) {
            this.mTemporarilyHidden = hidden;
            if (this.mTemporarilyHidden) {
                if (this.mTemporarilyHiddenExpiredRunnable == null) {
                    this.mTemporarilyHiddenExpiredRunnable = new C02164();
                }
                removeCallbacks(this.mTemporarilyHiddenExpiredRunnable);
                postDelayed(this.mTemporarilyHiddenExpiredRunnable, Math.max(0, this.mTemporarilyHiddenExpireTime - SystemClock.uptimeMillis()));
            } else if (this.mTemporarilyHiddenExpiredRunnable != null) {
                removeCallbacks(this.mTemporarilyHiddenExpiredRunnable);
            }
            onVisibilityInputChanged();
        }
    }

    private void onVisibilityInputChanged() {
        if (this.mContainer.isShowing()) {
            boolean allowed = isShowingAllowed();
            if (this.mWasShowingAllowed != allowed) {
                this.mWasShowingAllowed = allowed;
                cancelFadeIn();
                if (allowed) {
                    if (this.mDeferredHandleFadeInRunnable == null) {
                        this.mDeferredHandleFadeInRunnable = new C02175();
                    }
                    postOnAnimation(this.mDeferredHandleFadeInRunnable);
                    return;
                }
                updateVisibility();
            }
        }
    }

    private void updateAlpha() {
        if (this.mAlpha != 1.0f) {
            this.mAlpha = Math.min(1.0f, ((float) (AnimationUtils.currentAnimationTimeMillis() - this.mFadeStartTime)) / 200.0f);
            this.mDrawable.setAlpha((int) (255.0f * this.mAlpha));
            scheduleInvalidate();
        }
    }

    private void temporarilyHide() {
        if (this.mContainer.isShowing()) {
            this.mTemporarilyHiddenExpireTime = SystemClock.uptimeMillis() + 300;
            setTemporarilyHidden(true);
        }
    }

    private void doInvalidate() {
        if (this.mContainer.isShowing()) {
            updateVisibility();
            updatePosition();
            invalidate();
        }
    }

    private void scheduleInvalidate() {
        if (this.mInvalidationRunnable == null) {
            this.mInvalidationRunnable = new C02186();
        }
        if (!this.mHasPendingInvalidate) {
            this.mHasPendingInvalidate = true;
            postOnAnimation(this.mInvalidationRunnable);
        }
    }

    private void cancelFadeIn() {
        if (this.mDeferredHandleFadeInRunnable != null) {
            removeCallbacks(this.mDeferredHandleFadeInRunnable);
        }
    }

    private void beginFadeIn() {
        if (getVisibility() != 0) {
            this.mAlpha = 0.0f;
            this.mFadeStartTime = AnimationUtils.currentAnimationTimeMillis();
            doInvalidate();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mDrawable == null) {
            setMeasuredDimension(0, 0);
        } else {
            setMeasuredDimension(this.mDrawable.getIntrinsicWidth(), this.mDrawable.getIntrinsicHeight());
        }
    }

    protected void onDraw(Canvas c) {
        if (this.mDrawable != null) {
            boolean needsMirror;
            if (this.mMirrorHorizontal || this.mMirrorVertical) {
                needsMirror = true;
            } else {
                needsMirror = false;
            }
            if (needsMirror) {
                float scaleX;
                float scaleY;
                c.save();
                if (this.mMirrorHorizontal) {
                    scaleX = -1.0f;
                } else {
                    scaleX = 1.0f;
                }
                if (this.mMirrorVertical) {
                    scaleY = -1.0f;
                } else {
                    scaleY = 1.0f;
                }
                c.scale(scaleX, scaleY, ((float) getWidth()) / 2.0f, ((float) getHeight()) / 2.0f);
            }
            updateAlpha();
            this.mDrawable.setBounds(0, 0, getRight() - getLeft(), getBottom() - getTop());
            this.mDrawable.draw(c);
            if (needsMirror) {
                c.restore();
            }
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        onVisibilityInputChanged();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAttachedToWindow = false;
        onVisibilityInputChanged();
    }

    @CalledByNative
    private void destroy() {
        if (this.mContentViewCore != null) {
            hide();
            this.mContentViewCore.removeGestureStateListener(this.mGestureStateListener);
            this.mContentViewCore.removeContainerViewObserver(this.mParentViewObserver);
            this.mContentViewCore = null;
        }
    }

    @CalledByNative
    private void show() {
        if (this.mContentViewCore != null && !this.mContainer.isShowing()) {
            updateParentPosition(this.mParentPositionObserver.getPositionX(), this.mParentPositionObserver.getPositionY());
            this.mParentPositionObserver.addListener(this.mParentPositionListener);
            this.mContainer.setContentView(this);
            try {
                this.mContainer.showAtLocation(this.mContentViewCore.getContainerView(), 0, getContainerPositionX(), getContainerPositionY());
            } catch (BadTokenException e) {
                hide();
            }
        }
    }

    @CalledByNative
    private void hide() {
        this.mTemporarilyHiddenExpireTime = 0;
        setTemporarilyHidden(false);
        this.mAlpha = 1.0f;
        if (this.mContainer.isShowing()) {
            try {
                this.mContainer.dismiss();
            } catch (IllegalArgumentException e) {
            }
        }
        this.mParentPositionObserver.clearListener();
    }

    @CalledByNative
    private void setOrigin(float originX, float originY) {
        if (((float) this.mPositionX) != originX || ((float) this.mPositionY) != originY) {
            this.mPositionX = (int) originX;
            this.mPositionY = (int) originY;
            if (getVisibility() == 0) {
                scheduleInvalidate();
            }
        }
    }

    @CalledByNative
    private void setVisible(boolean visible) {
        if (this.mVisible != visible) {
            this.mVisible = visible;
            onVisibilityInputChanged();
        }
    }

    @CalledByNative
    private int getPositionX() {
        return this.mPositionX;
    }

    @CalledByNative
    private float getHandleHorizontalPaddingRatio() {
        return HandleViewResources.getHandleHorizontalPaddingRatio();
    }

    @CalledByNative
    private int getPositionY() {
        return this.mPositionY;
    }

    @CalledByNative
    private int getVisibleWidth() {
        if (this.mDrawable == null) {
            return 0;
        }
        return this.mDrawable.getIntrinsicWidth();
    }

    @CalledByNative
    private int getVisibleHeight() {
        if (this.mDrawable == null) {
            return 0;
        }
        return this.mDrawable.getIntrinsicHeight();
    }
}
