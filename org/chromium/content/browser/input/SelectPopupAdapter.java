package org.chromium.content.browser.input;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class SelectPopupAdapter extends ArrayAdapter<SelectPopupItem> {
    private boolean mAreAllItemsEnabled = true;
    private List<SelectPopupItem> mItems;

    public SelectPopupAdapter(Context context, int layoutResource, List<SelectPopupItem> items) {
        super(context, layoutResource, items);
        this.mItems = new ArrayList(items);
        for (int i = 0; i < this.mItems.size(); i++) {
            if (((SelectPopupItem) this.mItems.get(i)).getType() != 2) {
                this.mAreAllItemsEnabled = false;
                return;
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (position < 0 || position >= getCount()) {
            return null;
        }
        convertView = super.getView(position, null, parent);
        ((TextView) convertView).setText(((SelectPopupItem) this.mItems.get(position)).getLabel());
        if (((SelectPopupItem) this.mItems.get(position)).getType() != 2) {
            if (((SelectPopupItem) this.mItems.get(position)).getType() != 0) {
                convertView.setEnabled(false);
            } else if (convertView instanceof CheckedTextView) {
                ((CheckedTextView) convertView).setCheckMarkDrawable(null);
            }
        }
        return convertView;
    }

    public boolean areAllItemsEnabled() {
        return this.mAreAllItemsEnabled;
    }

    public boolean isEnabled(int position) {
        if (position < 0 || position >= getCount()) {
            return false;
        }
        return ((SelectPopupItem) this.mItems.get(position)).getType() == 2;
    }
}
