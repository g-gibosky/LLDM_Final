package org.xwalk.core.internal;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import org.chromium.content.browser.ContentView;
import org.chromium.content.browser.ContentViewCore;

public class XWalkContentView extends ContentView {
    private static final String TAG = "XWalkContentView";
    private XWalkViewInternal mXWalkView;

    private static class XWalkContentViewApi16 extends XWalkContentView {
        public XWalkContentViewApi16(Context context, ContentViewCore cvc, XWalkViewInternal xwView) {
            super(context, cvc, xwView);
        }

        public boolean performAccessibilityAction(int action, Bundle arguments) {
            if (this.mContentViewCore.supportsAccessibilityAction(action)) {
                return this.mContentViewCore.performAccessibilityAction(action, arguments);
            }
            return super.performAccessibilityAction(action, arguments);
        }

        public AccessibilityNodeProvider getAccessibilityNodeProvider() {
            AccessibilityNodeProvider provider = this.mContentViewCore.getAccessibilityNodeProvider();
            return provider != null ? provider : super.getAccessibilityNodeProvider();
        }
    }

    private static class XWalkContentViewApi23 extends XWalkContentViewApi16 {
        public XWalkContentViewApi23(Context context, ContentViewCore cvc, XWalkViewInternal xwView) {
            super(context, cvc, xwView);
        }

        public void onProvideVirtualStructure(ViewStructure structure) {
            this.mContentViewCore.onProvideVirtualStructure(structure, false);
        }
    }

    public static XWalkContentView createContentView(Context context, ContentViewCore cvc, XWalkViewInternal xwView) {
        if (VERSION.SDK_INT >= 23) {
            return new XWalkContentViewApi23(context, cvc, xwView);
        }
        if (VERSION.SDK_INT >= 16) {
            return new XWalkContentViewApi16(context, cvc, xwView);
        }
        return new XWalkContentView(context, cvc, xwView);
    }

    private XWalkContentView(Context context, ContentViewCore cvc, XWalkViewInternal xwView) {
        super(context, cvc);
        this.mXWalkView = xwView;
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.mXWalkView.onCreateInputConnection(outAttrs);
    }

    public InputConnection onCreateInputConnectionSuper(EditorInfo outAttrs) {
        return super.onCreateInputConnection(outAttrs);
    }

    public boolean performLongClick() {
        return this.mXWalkView.performLongClickDelegate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mXWalkView.onTouchEventDelegate(event)) {
            return true;
        }
        return this.mContentViewCore.onTouchEvent(event);
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        this.mXWalkView.onScrollChangedDelegate(l, t, oldl, oldt);
        this.mXWalkView.onOverScrolledDelegate(l, t, false, false);
    }

    public int computeHorizontalScrollRangeDelegate() {
        return computeHorizontalScrollRange();
    }

    public int computeHorizontalScrollOffsetDelegate() {
        return computeHorizontalScrollOffset();
    }

    public int computeVerticalScrollRangeDelegate() {
        return computeVerticalScrollRange();
    }

    public int computeVerticalScrollOffsetDelegate() {
        return computeVerticalScrollOffset();
    }

    public int computeVerticalScrollExtentDelegate() {
        return computeVerticalScrollExtent();
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        this.mXWalkView.onFocusChangedDelegate(gainFocus, direction, previouslyFocusedRect);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }
}
