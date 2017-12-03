package org.chromium.content.browser.input;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import java.util.List;
import org.chromium.content.C0174R;
import org.chromium.content.browser.ContentViewCore;

public class SelectPopupDialog implements SelectPopup {
    private static final int[] SELECT_DIALOG_ATTRS = new int[]{C0174R.attr.select_dialog_multichoice, C0174R.attr.select_dialog_singlechoice};
    private final ContentViewCore mContentViewCore;
    private final AlertDialog mListBoxPopup;
    private boolean mSelectionNotified;

    class C02202 implements OnClickListener {
        C02202() {
        }

        public void onClick(DialogInterface dialog, int which) {
            SelectPopupDialog.this.notifySelection(null);
        }
    }

    class C02224 implements OnCancelListener {
        C02224() {
        }

        public void onCancel(DialogInterface dialog) {
            SelectPopupDialog.this.notifySelection(null);
        }
    }

    public SelectPopupDialog(ContentViewCore contentViewCore, Context windowContext, List<SelectPopupItem> items, boolean multiple, int[] selected) {
        this.mContentViewCore = contentViewCore;
        final ListView listView = new ListView(windowContext);
        Builder b = new Builder(windowContext).setView(listView).setCancelable(true);
        setInverseBackgroundForced(b);
        if (multiple) {
            b.setPositiveButton(17039370, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    SelectPopupDialog.this.notifySelection(SelectPopupDialog.getSelectedIndices(listView));
                }
            });
            b.setNegativeButton(17039360, new C02202());
        }
        this.mListBoxPopup = b.create();
        listView.setAdapter(new SelectPopupAdapter(this.mListBoxPopup.getContext(), getSelectDialogLayout(multiple), items));
        listView.setFocusableInTouchMode(true);
        if (multiple) {
            listView.setChoiceMode(2);
            for (int itemChecked : selected) {
                listView.setItemChecked(itemChecked, true);
            }
        } else {
            listView.setChoiceMode(1);
            listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                    SelectPopupDialog.this.notifySelection(SelectPopupDialog.getSelectedIndices(listView));
                    SelectPopupDialog.this.mListBoxPopup.dismiss();
                }
            });
            if (selected.length > 0) {
                listView.setSelection(selected[0]);
                listView.setItemChecked(selected[0], true);
            }
        }
        this.mListBoxPopup.setOnCancelListener(new C02224());
    }

    private static void setInverseBackgroundForced(Builder builder) {
        builder.setInverseBackgroundForced(true);
    }

    private int getSelectDialogLayout(boolean isMultiChoice) {
        TypedArray styledAttributes = this.mListBoxPopup.getContext().obtainStyledAttributes(C0174R.style.SelectPopupDialog, SELECT_DIALOG_ATTRS);
        int resourceId = styledAttributes.getResourceId(isMultiChoice ? 0 : 1, 0);
        styledAttributes.recycle();
        return resourceId;
    }

    private static int[] getSelectedIndices(ListView listView) {
        int i;
        SparseBooleanArray sparseArray = listView.getCheckedItemPositions();
        int selectedCount = 0;
        for (i = 0; i < sparseArray.size(); i++) {
            if (sparseArray.valueAt(i)) {
                selectedCount++;
            }
        }
        int[] indices = new int[selectedCount];
        int j = 0;
        for (i = 0; i < sparseArray.size(); i++) {
            if (sparseArray.valueAt(i)) {
                int j2 = j + 1;
                indices[j] = sparseArray.keyAt(i);
                j = j2;
            }
        }
        return indices;
    }

    private void notifySelection(int[] indicies) {
        if (!this.mSelectionNotified) {
            this.mContentViewCore.selectPopupMenuItems(indicies);
            this.mSelectionNotified = true;
        }
    }

    public void show() {
        try {
            this.mListBoxPopup.show();
        } catch (BadTokenException e) {
            notifySelection(null);
        }
    }

    public void hide(boolean sendsCancelMessage) {
        if (sendsCancelMessage) {
            this.mListBoxPopup.cancel();
            notifySelection(null);
            return;
        }
        this.mSelectionNotified = true;
        this.mListBoxPopup.cancel();
    }
}
