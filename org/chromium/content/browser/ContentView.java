package org.chromium.content.browser;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import org.chromium.base.Log;
import org.chromium.base.TraceEvent;
import org.chromium.content.browser.ContentViewCore.InternalAccessDelegate;
import org.chromium.content.browser.ContentViewCore.SmartClipDataListener;

public class ContentView extends FrameLayout implements InternalAccessDelegate, SmartClipProvider {
    private static final String TAG = "cr.ContentView";
    protected final ContentViewCore mContentViewCore;

    private static class ContentViewApi23 extends ContentView {
        public ContentViewApi23(Context context, ContentViewCore cvc) {
            super(context, cvc);
        }

        public void onProvideVirtualStructure(ViewStructure structure) {
            this.mContentViewCore.onProvideVirtualStructure(structure, false);
        }
    }

    public static ContentView createContentView(Context context, ContentViewCore cvc) {
        if (VERSION.SDK_INT >= 23) {
            return new ContentViewApi23(context, cvc);
        }
        return new ContentView(context, cvc);
    }

    public ContentView(Context context, ContentViewCore cvc) {
        super(context, null, 16842885);
        if (getScrollBarStyle() == 0) {
            setHorizontalScrollBarEnabled(false);
            setVerticalScrollBarEnabled(false);
        }
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.mContentViewCore = cvc;
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

    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    protected void onSizeChanged(int w, int h, int ow, int oh) {
        try {
            TraceEvent.begin("ContentView.onSizeChanged");
            super.onSizeChanged(w, h, ow, oh);
            this.mContentViewCore.onSizeChanged(w, h, ow, oh);
        } finally {
            TraceEvent.end("ContentView.onSizeChanged");
        }
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.mContentViewCore.onCreateInputConnection(outAttrs);
    }

    public boolean onCheckIsTextEditor() {
        return this.mContentViewCore.onCheckIsTextEditor();
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        try {
            TraceEvent.begin("ContentView.onFocusChanged");
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
            this.mContentViewCore.onFocusChanged(gainFocus);
        } finally {
            TraceEvent.end("ContentView.onFocusChanged");
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        this.mContentViewCore.onWindowFocusChanged(hasWindowFocus);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.mContentViewCore.onKeyUp(keyCode, event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (isFocused()) {
            return this.mContentViewCore.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.mContentViewCore.onTouchEvent(event);
    }

    public boolean onHoverEvent(MotionEvent event) {
        boolean consumed = this.mContentViewCore.onHoverEvent(event);
        if (!this.mContentViewCore.isTouchExplorationEnabled()) {
            super.onHoverEvent(event);
        }
        return consumed;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return this.mContentViewCore.onGenericMotionEvent(event);
    }

    public boolean performLongClick() {
        return false;
    }

    protected void onConfigurationChanged(Configuration newConfig) {
        this.mContentViewCore.onConfigurationChanged(newConfig);
    }

    public void scrollBy(int x, int y) {
        this.mContentViewCore.scrollBy((float) x, (float) y, false);
    }

    public void scrollTo(int x, int y) {
        this.mContentViewCore.scrollTo((float) x, (float) y);
    }

    protected int computeHorizontalScrollExtent() {
        return this.mContentViewCore.computeHorizontalScrollExtent();
    }

    protected int computeHorizontalScrollOffset() {
        return this.mContentViewCore.computeHorizontalScrollOffset();
    }

    protected int computeHorizontalScrollRange() {
        return this.mContentViewCore.computeHorizontalScrollRange();
    }

    protected int computeVerticalScrollExtent() {
        return this.mContentViewCore.computeVerticalScrollExtent();
    }

    protected int computeVerticalScrollOffset() {
        return this.mContentViewCore.computeVerticalScrollOffset();
    }

    protected int computeVerticalScrollRange() {
        return this.mContentViewCore.computeVerticalScrollRange();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ContentViewClient client = this.mContentViewCore.getContentViewClient();
        int desiredWidthMeasureSpec = client.getDesiredWidthMeasureSpec();
        if (MeasureSpec.getMode(desiredWidthMeasureSpec) != 0) {
            widthMeasureSpec = desiredWidthMeasureSpec;
        }
        int desiredHeightMeasureSpec = client.getDesiredHeightMeasureSpec();
        if (MeasureSpec.getMode(desiredHeightMeasureSpec) != 0) {
            heightMeasureSpec = desiredHeightMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean awakenScrollBars(int startDelay, boolean invalidate) {
        return this.mContentViewCore.awakenScrollBars(startDelay, invalidate);
    }

    public boolean awakenScrollBars() {
        return super.awakenScrollBars();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mContentViewCore.onAttachedToWindow();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mContentViewCore.onDetachedFromWindow();
    }

    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        this.mContentViewCore.onVisibilityChanged(changedView, visibility);
    }

    public void extractSmartClipData(int x, int y, int width, int height) {
        this.mContentViewCore.extractSmartClipData(x, y, width, height);
    }

    public void setSmartClipResultHandler(final Handler resultHandler) {
        if (resultHandler == null) {
            this.mContentViewCore.setSmartClipDataListener(null);
        } else {
            this.mContentViewCore.setSmartClipDataListener(new SmartClipDataListener() {
                public void onSmartClipDataExtracted(String text, String html, Rect clipRect) {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", ContentView.this.mContentViewCore.getWebContents().getVisibleUrl());
                    bundle.putString("title", ContentView.this.mContentViewCore.getWebContents().getTitle());
                    bundle.putParcelable("rect", clipRect);
                    bundle.putString("text", text);
                    bundle.putString("html", html);
                    try {
                        Message msg = Message.obtain(resultHandler, 0);
                        msg.setData(bundle);
                        msg.sendToTarget();
                    } catch (Exception e) {
                        Log.m28e(ContentView.TAG, "Error calling handler for smart clip data: ", e);
                    }
                }
            });
        }
    }

    public boolean super_onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    public boolean super_dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    public boolean super_onGenericMotionEvent(MotionEvent event) {
        return super.onGenericMotionEvent(event);
    }

    public void super_onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public boolean super_awakenScrollBars(int startDelay, boolean invalidate) {
        return super.awakenScrollBars(startDelay, invalidate);
    }
}
