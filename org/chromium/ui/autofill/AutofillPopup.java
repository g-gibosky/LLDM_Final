package org.chromium.ui.autofill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.PopupWindow.OnDismissListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.chromium.ui.C0290R;
import org.chromium.ui.DropdownAdapter;
import org.chromium.ui.DropdownItem;
import org.chromium.ui.DropdownPopupWindow;
import org.chromium.ui.base.ViewAndroidDelegate;

public class AutofillPopup extends DropdownPopupWindow implements OnItemClickListener, OnItemLongClickListener, OnDismissListener {
    static final /* synthetic */ boolean $assertionsDisabled = (!AutofillPopup.class.desiredAssertionStatus());
    private static final int ITEM_ID_SEPARATOR_ENTRY = -3;
    private final AutofillDelegate mAutofillDelegate;
    private final Context mContext;
    private List<AutofillSuggestion> mSuggestions;

    public AutofillPopup(Context context, ViewAndroidDelegate viewAndroidDelegate, AutofillDelegate autofillDelegate) {
        super(context, viewAndroidDelegate);
        this.mContext = context;
        this.mAutofillDelegate = autofillDelegate;
        setOnItemClickListener(this);
        setOnDismissListener(this);
        disableHideOnOutsideTap();
        setContentDescriptionForAccessibility(this.mContext.getString(C0290R.string.autofill_popup_content_description));
    }

    @SuppressLint({"InlinedApi"})
    public void filterAndShow(AutofillSuggestion[] suggestions, boolean isRtl) {
        this.mSuggestions = new ArrayList(Arrays.asList(suggestions));
        ArrayList<DropdownItem> cleanedData = new ArrayList();
        HashSet<Integer> separators = new HashSet();
        for (int i = 0; i < suggestions.length; i++) {
            if (suggestions[i].getSuggestionId() == -3) {
                separators.add(Integer.valueOf(cleanedData.size()));
            } else {
                cleanedData.add(suggestions[i]);
            }
        }
        setAdapter(new DropdownAdapter(this.mContext, cleanedData, separators));
        setRtl(isRtl);
        show();
        getListView().setOnItemLongClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int listIndex = this.mSuggestions.indexOf(((DropdownAdapter) parent.getAdapter()).getItem(position));
        if ($assertionsDisabled || listIndex > -1) {
            this.mAutofillDelegate.suggestionSelected(listIndex);
            return;
        }
        throw new AssertionError();
    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AutofillSuggestion suggestion = (AutofillSuggestion) ((DropdownAdapter) parent.getAdapter()).getItem(position);
        if (!suggestion.isDeletable()) {
            return false;
        }
        int listIndex = this.mSuggestions.indexOf(suggestion);
        if ($assertionsDisabled || listIndex > -1) {
            this.mAutofillDelegate.deleteSuggestion(listIndex);
            return true;
        }
        throw new AssertionError();
    }

    public void onDismiss() {
        this.mAutofillDelegate.dismissed();
    }
}
