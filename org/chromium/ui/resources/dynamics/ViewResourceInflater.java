package org.chromium.ui.resources.dynamics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnDrawListener;
import org.chromium.ui.base.PageTransition;

public class ViewResourceInflater {
    static final /* synthetic */ boolean $assertionsDisabled = (!ViewResourceInflater.class.desiredAssertionStatus());
    private static final int INVALID_ID = -1;
    private ViewGroup mContainer;
    private Context mContext;
    private boolean mIsAttached;
    private boolean mIsInvalidated;
    private int mLayoutId;
    private boolean mNeedsLayoutUpdate;
    private ViewInflaterOnDrawListener mOnDrawListener;
    private ViewResourceAdapter mResourceAdapter;
    private DynamicResourceLoader mResourceLoader;
    private View mView;
    private int mViewId;

    private class ViewInflaterOnDrawListener implements OnDrawListener {
        private ViewInflaterOnDrawListener() {
        }

        public void onDraw() {
            ViewResourceInflater.this.invalidateResource();
        }
    }

    private class ViewInflaterAdapter extends ViewResourceAdapter {
        public ViewInflaterAdapter(View view) {
            super(view);
        }

        protected void onCaptureEnd() {
            ViewResourceInflater.this.onCaptureEnd();
        }
    }

    public ViewResourceInflater(int layoutId, int viewId, Context context, ViewGroup container, DynamicResourceLoader resourceLoader) {
        this.mLayoutId = layoutId;
        this.mViewId = viewId;
        this.mContext = context;
        this.mContainer = container;
        this.mResourceLoader = resourceLoader;
    }

    public void inflate() {
        if (this.mView == null) {
            this.mView = LayoutInflater.from(this.mContext).inflate(this.mLayoutId, this.mContainer, false);
            if ($assertionsDisabled || this.mView.getId() == this.mViewId) {
                onFinishInflate();
                registerResource();
                this.mNeedsLayoutUpdate = true;
                return;
            }
            throw new AssertionError();
        }
    }

    public void invalidate() {
        invalidate(false);
    }

    public void invalidate(boolean didViewSizeChange) {
        if (this.mView == null) {
            inflate();
        }
        this.mIsInvalidated = true;
        if (!this.mIsAttached && shouldAttachView()) {
            attachView();
        }
        if (!this.mIsAttached) {
            layout();
            invalidateResource();
        } else if (didViewSizeChange || this.mNeedsLayoutUpdate) {
            updateLayoutParams();
        }
        this.mNeedsLayoutUpdate = false;
    }

    public void destroy() {
        if (this.mView != null) {
            unregisterResource();
            detachView();
            this.mView = null;
            this.mLayoutId = -1;
            this.mViewId = -1;
            this.mContext = null;
            this.mContainer = null;
            this.mResourceLoader = null;
        }
    }

    public int getMeasuredWidth() {
        if ($assertionsDisabled || this.mView != null) {
            return this.mView.getMeasuredWidth();
        }
        throw new AssertionError();
    }

    public int getMeasuredHeight() {
        if ($assertionsDisabled || this.mView != null) {
            return this.mView.getMeasuredHeight();
        }
        throw new AssertionError();
    }

    public int getViewId() {
        return this.mViewId;
    }

    protected void onFinishInflate() {
    }

    protected boolean shouldAttachView() {
        return true;
    }

    protected boolean shouldDetachViewAfterCapturing() {
        return true;
    }

    protected int getWidthMeasureSpec() {
        return getUnspecifiedMeasureSpec();
    }

    protected int getHeightMeasureSpec() {
        return getUnspecifiedMeasureSpec();
    }

    protected void layout() {
        this.mView.measure(getWidthMeasureSpec(), getHeightMeasureSpec());
        this.mView.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    protected View getView() {
        return this.mView;
    }

    protected Context getContext() {
        return this.mContext;
    }

    private void attachView() {
        if (!this.mIsAttached) {
            if ($assertionsDisabled || this.mView.getParent() == null) {
                this.mContainer.addView(this.mView);
                this.mIsAttached = true;
                if (this.mOnDrawListener == null) {
                    this.mOnDrawListener = new ViewInflaterOnDrawListener();
                    this.mView.getViewTreeObserver().addOnDrawListener(this.mOnDrawListener);
                    return;
                }
                return;
            }
            throw new AssertionError();
        }
    }

    private void detachView() {
        if (this.mIsAttached) {
            if (this.mOnDrawListener != null) {
                this.mView.getViewTreeObserver().removeOnDrawListener(this.mOnDrawListener);
                this.mOnDrawListener = null;
            }
            if ($assertionsDisabled || this.mView.getParent() != null) {
                this.mContainer.removeView(this.mView);
                this.mIsAttached = false;
                return;
            }
            throw new AssertionError();
        }
    }

    private void updateLayoutParams() {
        if ($assertionsDisabled || this.mView != null) {
            int widthMeasureSpec = getWidthMeasureSpec();
            int width = -2;
            if (MeasureSpec.getMode(widthMeasureSpec) == PageTransition.CLIENT_REDIRECT) {
                width = MeasureSpec.getSize(widthMeasureSpec);
            }
            int heightMeasureSpec = getHeightMeasureSpec();
            int height = -2;
            if (MeasureSpec.getMode(heightMeasureSpec) == PageTransition.CLIENT_REDIRECT) {
                height = MeasureSpec.getSize(heightMeasureSpec);
            }
            LayoutParams params = this.mView.getLayoutParams();
            params.width = width;
            params.height = height;
            this.mView.setLayoutParams(params);
            return;
        }
        throw new AssertionError();
    }

    private int getUnspecifiedMeasureSpec() {
        return MeasureSpec.makeMeasureSpec(0, 0);
    }

    private void registerResource() {
        if (this.mResourceAdapter == null) {
            this.mResourceAdapter = new ViewInflaterAdapter(this.mView.findViewById(this.mViewId));
        }
        if (this.mResourceLoader != null) {
            this.mResourceLoader.registerResource(this.mViewId, this.mResourceAdapter);
        }
    }

    private void unregisterResource() {
        if (this.mResourceLoader != null) {
            this.mResourceLoader.unregisterResource(this.mViewId);
        }
        this.mResourceAdapter = null;
    }

    private void invalidateResource() {
        if (this.mIsInvalidated && this.mView != null && this.mResourceAdapter != null) {
            this.mIsInvalidated = false;
            this.mResourceAdapter.invalidate(null);
        }
    }

    protected void onCaptureEnd() {
        if (shouldDetachViewAfterCapturing()) {
            detachView();
        }
    }
}
