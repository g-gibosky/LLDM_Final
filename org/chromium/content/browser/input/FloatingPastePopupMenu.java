package org.chromium.content.browser.input;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.View;
import org.chromium.content.browser.FloatingWebActionModeCallback;
import org.chromium.content.browser.WebActionModeCallback;
import org.chromium.content.browser.WebActionModeCallback.ActionHandler;
import org.chromium.content.browser.input.PastePopupMenu.PastePopupMenuDelegate;

@TargetApi(23)
public class FloatingPastePopupMenu implements PastePopupMenu {
    static final /* synthetic */ boolean $assertionsDisabled = (!FloatingPastePopupMenu.class.desiredAssertionStatus());
    private static final int CONTENT_RECT_OFFSET_DIP = 15;
    private static final int SLOP_LENGTH_DIP = 10;
    private ActionHandler mActionHandler;
    private ActionMode mActionMode;
    private final int mContentRectOffset;
    private final Context mContext;
    private final PastePopupMenuDelegate mDelegate;
    private LegacyPastePopupMenu mFallbackPastePopupMenu;
    private final View mParent;
    private int mRawPositionX;
    private int mRawPositionY;
    private final int mSlopLengthSquared;

    class C04461 implements ActionHandler {
        C04461() {
        }

        public void selectAll() {
        }

        public void cut() {
        }

        public void copy() {
        }

        public void paste() {
            FloatingPastePopupMenu.this.mDelegate.paste();
        }

        public void share() {
        }

        public void search() {
        }

        public void processText(Intent intent) {
        }

        public boolean isSelectionPassword() {
            return false;
        }

        public boolean isSelectionEditable() {
            return true;
        }

        public boolean isInsertion() {
            return true;
        }

        public void onDestroyActionMode() {
            FloatingPastePopupMenu.this.mActionMode = null;
            FloatingPastePopupMenu.this.mDelegate.onDismiss();
        }

        public void onGetContentRect(Rect outRect) {
            outRect.set(FloatingPastePopupMenu.this.mRawPositionX - FloatingPastePopupMenu.this.mContentRectOffset, FloatingPastePopupMenu.this.mRawPositionY - FloatingPastePopupMenu.this.mContentRectOffset, FloatingPastePopupMenu.this.mRawPositionX + FloatingPastePopupMenu.this.mContentRectOffset, FloatingPastePopupMenu.this.mRawPositionY + FloatingPastePopupMenu.this.mContentRectOffset);
        }

        public boolean isIncognito() {
            return false;
        }

        public boolean isSelectActionModeAllowed(int actionModeItem) {
            return false;
        }
    }

    public FloatingPastePopupMenu(Context context, View parent, PastePopupMenuDelegate delegate) {
        if ($assertionsDisabled || VERSION.SDK_INT >= 23) {
            this.mParent = parent;
            this.mDelegate = delegate;
            this.mContext = context;
            this.mContentRectOffset = (int) TypedValue.applyDimension(1, 15.0f, this.mContext.getResources().getDisplayMetrics());
            int slopLength = (int) TypedValue.applyDimension(1, 10.0f, this.mContext.getResources().getDisplayMetrics());
            this.mSlopLengthSquared = slopLength * slopLength;
            return;
        }
        throw new AssertionError();
    }

    public void show(int x, int y) {
        if (this.mFallbackPastePopupMenu != null) {
            this.mFallbackPastePopupMenu.show(x, y);
            return;
        }
        if (isShowing()) {
            int dx = this.mRawPositionX - x;
            int dy = this.mRawPositionY - y;
            if ((dx * dx) + (dy * dy) < this.mSlopLengthSquared) {
                return;
            }
        }
        this.mRawPositionX = x;
        this.mRawPositionY = y;
        if (this.mActionMode != null) {
            this.mActionMode.invalidateContentRect();
        } else {
            ensureActionModeOrFallback();
        }
    }

    public void hide() {
        if (this.mFallbackPastePopupMenu != null) {
            this.mFallbackPastePopupMenu.hide();
        } else if (this.mActionMode != null) {
            this.mActionMode.finish();
            this.mActionMode = null;
        }
    }

    public boolean isShowing() {
        if (this.mFallbackPastePopupMenu != null) {
            return this.mFallbackPastePopupMenu.isShowing();
        }
        return this.mActionMode != null;
    }

    private void ensureActionModeOrFallback() {
        if (this.mActionMode == null && this.mFallbackPastePopupMenu == null) {
            ActionMode actionMode = this.mParent.startActionMode(new FloatingWebActionModeCallback(new WebActionModeCallback(this.mParent.getContext(), getActionHandler())), 1);
            if (actionMode == null) {
                this.mFallbackPastePopupMenu = new LegacyPastePopupMenu(this.mContext, this.mParent, this.mDelegate);
                this.mFallbackPastePopupMenu.show(this.mRawPositionX, this.mRawPositionY);
            } else if ($assertionsDisabled || actionMode.getType() == 1) {
                this.mActionMode = actionMode;
            } else {
                throw new AssertionError();
            }
        }
    }

    private ActionHandler getActionHandler() {
        if (this.mActionHandler != null) {
            return this.mActionHandler;
        }
        this.mActionHandler = new C04461();
        return this.mActionHandler;
    }
}
