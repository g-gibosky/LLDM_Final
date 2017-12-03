package org.chromium.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLayoutChangeListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.ui.base.ViewAndroidDelegate;

public class DropdownPopupWindow extends ListPopupWindow {
    static final /* synthetic */ boolean $assertionsDisabled = (!DropdownPopupWindow.class.desiredAssertionStatus());
    ListAdapter mAdapter;
    private float mAnchorHeight;
    private final View mAnchorView;
    private float mAnchorWidth;
    private float mAnchorX;
    private float mAnchorY;
    private final Context mContext;
    private CharSequence mDescription;
    private int mInitialSelection = -1;
    private OnLayoutChangeListener mLayoutChangeListener;
    private OnDismissListener mOnDismissListener;
    private boolean mRtl;
    private final ViewAndroidDelegate mViewAndroidDelegate;

    class C02881 implements OnLayoutChangeListener {
        C02881() {
        }

        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            if (v == DropdownPopupWindow.this.mAnchorView) {
                DropdownPopupWindow.this.show();
            }
        }
    }

    class C02892 implements OnDismissListener {
        C02892() {
        }

        public void onDismiss() {
            if (DropdownPopupWindow.this.mOnDismissListener != null) {
                DropdownPopupWindow.this.mOnDismissListener.onDismiss();
            }
            DropdownPopupWindow.this.mAnchorView.removeOnLayoutChangeListener(DropdownPopupWindow.this.mLayoutChangeListener);
            DropdownPopupWindow.this.mAnchorView.setTag(null);
            DropdownPopupWindow.this.mViewAndroidDelegate.releaseAnchorView(DropdownPopupWindow.this.mAnchorView);
        }
    }

    public DropdownPopupWindow(Context context, ViewAndroidDelegate viewAndroidDelegate) {
        super(context, null, 0, C0290R.style.DropdownPopupWindow);
        this.mContext = context;
        this.mViewAndroidDelegate = viewAndroidDelegate;
        this.mAnchorView = this.mViewAndroidDelegate.acquireAnchorView();
        this.mAnchorView.setId(C0290R.id.dropdown_popup_window);
        this.mAnchorView.setTag(this);
        this.mLayoutChangeListener = new C02881();
        this.mAnchorView.addOnLayoutChangeListener(this.mLayoutChangeListener);
        super.setOnDismissListener(new C02892());
        setAnchorView(this.mAnchorView);
        Rect originalPadding = new Rect();
        getBackground().getPadding(originalPadding);
        setVerticalOffset(-originalPadding.top);
    }

    public void setAnchorRect(float x, float y, float width, float height) {
        this.mAnchorWidth = width;
        this.mAnchorHeight = height;
        this.mAnchorX = x;
        this.mAnchorY = y;
        if (this.mAnchorView != null) {
            this.mViewAndroidDelegate.setAnchorViewPosition(this.mAnchorView, this.mAnchorX, this.mAnchorY, this.mAnchorWidth, this.mAnchorHeight);
        }
    }

    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;
        super.setAdapter(adapter);
    }

    public void setInitialSelection(int initialSelection) {
        this.mInitialSelection = initialSelection;
    }

    public void show() {
        int i;
        setInputMethodMode(1);
        int contentWidth = measureContentWidth();
        float contentWidthInDip = ((float) contentWidth) / this.mContext.getResources().getDisplayMetrics().density;
        Rect padding = new Rect();
        getBackground().getPadding(padding);
        if ((((float) padding.left) + contentWidthInDip) + ((float) padding.right) > this.mAnchorWidth) {
            setContentWidth(contentWidth);
            Rect displayFrame = new Rect();
            this.mAnchorView.getWindowVisibleDisplayFrame(displayFrame);
            if (getWidth() > displayFrame.width()) {
                setWidth(displayFrame.width());
            }
        } else {
            setWidth(-2);
        }
        this.mViewAndroidDelegate.setAnchorViewPosition(this.mAnchorView, this.mAnchorX, this.mAnchorY, this.mAnchorWidth, this.mAnchorHeight);
        boolean wasShowing = isShowing();
        super.show();
        getListView().setDividerHeight(0);
        View listView = getListView();
        if (this.mRtl) {
            i = 1;
        } else {
            i = 0;
        }
        ApiCompatibilityUtils.setLayoutDirection(listView, i);
        if (!wasShowing) {
            getListView().setContentDescription(this.mDescription);
            getListView().sendAccessibilityEvent(32);
        }
        if (this.mInitialSelection >= 0) {
            getListView().setSelection(this.mInitialSelection);
            this.mInitialSelection = -1;
        }
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.mOnDismissListener = listener;
    }

    public void setRtl(boolean isRtl) {
        this.mRtl = isRtl;
    }

    public void disableHideOnOutsideTap() {
        try {
            ListPopupWindow.class.getMethod("setForceIgnoreOutsideTouch", new Class[]{Boolean.TYPE}).invoke(this, new Object[]{Boolean.valueOf(true)});
        } catch (Exception e) {
            Log.e("AutofillPopup", "ListPopupWindow.setForceIgnoreOutsideTouch not found", e);
        }
    }

    public void setContentDescriptionForAccessibility(CharSequence description) {
        this.mDescription = description;
    }

    private int measureContentWidth() {
        if ($assertionsDisabled || this.mAdapter != null) {
            int maxWidth = 0;
            View[] itemViews = new View[this.mAdapter.getViewTypeCount()];
            int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
            int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, 0);
            for (int i = 0; i < this.mAdapter.getCount(); i++) {
                int type = this.mAdapter.getItemViewType(i);
                itemViews[type] = this.mAdapter.getView(i, itemViews[type], null);
                View itemView = itemViews[type];
                itemView.setLayoutParams(new LayoutParams(-2, -2));
                itemView.measure(widthMeasureSpec, heightMeasureSpec);
                maxWidth = Math.max(maxWidth, itemView.getMeasuredWidth());
            }
            return maxWidth;
        }
        throw new AssertionError("Set the adapter before showing the popup.");
    }
}
