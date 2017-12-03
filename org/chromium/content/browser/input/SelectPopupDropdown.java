package org.chromium.content.browser.input;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import java.util.List;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content.browser.RenderCoordinates;
import org.chromium.ui.DropdownAdapter;
import org.chromium.ui.DropdownPopupWindow;

public class SelectPopupDropdown implements SelectPopup {
    private final ContentViewCore mContentViewCore;
    private final Context mContext = this.mContentViewCore.getContext();
    private final DropdownPopupWindow mDropdownPopupWindow = new DropdownPopupWindow(this.mContext, this.mContentViewCore.getViewAndroidDelegate());
    private boolean mSelectionNotified;

    class C02231 implements OnItemClickListener {
        C02231() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            SelectPopupDropdown.this.notifySelection(new int[]{position});
            SelectPopupDropdown.this.hide(false);
        }
    }

    class C02242 implements OnDismissListener {
        C02242() {
        }

        public void onDismiss() {
            SelectPopupDropdown.this.notifySelection(null);
        }
    }

    public SelectPopupDropdown(ContentViewCore contentViewCore, List<SelectPopupItem> items, Rect bounds, int[] selected, boolean rightAligned) {
        this.mContentViewCore = contentViewCore;
        this.mDropdownPopupWindow.setOnItemClickListener(new C02231());
        int initialSelection = -1;
        if (selected.length > 0) {
            initialSelection = selected[0];
        }
        this.mDropdownPopupWindow.setInitialSelection(initialSelection);
        this.mDropdownPopupWindow.setAdapter(new DropdownAdapter(this.mContext, items, null));
        this.mDropdownPopupWindow.setRtl(rightAligned);
        RenderCoordinates renderCoordinates = this.mContentViewCore.getRenderCoordinates();
        float anchorX = renderCoordinates.fromPixToDip(renderCoordinates.fromLocalCssToPix((float) bounds.left));
        float anchorY = renderCoordinates.fromPixToDip(renderCoordinates.fromLocalCssToPix((float) bounds.top));
        this.mDropdownPopupWindow.setAnchorRect(anchorX, anchorY, renderCoordinates.fromPixToDip(renderCoordinates.fromLocalCssToPix((float) bounds.right)) - anchorX, renderCoordinates.fromPixToDip(renderCoordinates.fromLocalCssToPix((float) bounds.bottom)) - anchorY);
        this.mDropdownPopupWindow.setOnDismissListener(new C02242());
    }

    private void notifySelection(int[] indicies) {
        if (!this.mSelectionNotified) {
            this.mContentViewCore.selectPopupMenuItems(indicies);
            this.mSelectionNotified = true;
        }
    }

    public void show() {
        this.mDropdownPopupWindow.postShow();
    }

    public void hide(boolean sendsCancelMessage) {
        if (sendsCancelMessage) {
            this.mDropdownPopupWindow.dismiss();
            notifySelection(null);
            return;
        }
        this.mSelectionNotified = true;
        this.mDropdownPopupWindow.dismiss();
    }
}
