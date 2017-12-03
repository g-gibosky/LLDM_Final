package org.chromium.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import org.chromium.base.ApiCompatibilityUtils;

public class ColorSuggestionListAdapter extends BaseAdapter implements OnClickListener {
    private static final int COLORS_PER_ROW = 4;
    private Context mContext;
    private OnColorSuggestionClickListener mListener;
    private ColorSuggestion[] mSuggestions;

    public interface OnColorSuggestionClickListener {
        void onColorSuggestionClick(ColorSuggestion colorSuggestion);
    }

    ColorSuggestionListAdapter(Context context, ColorSuggestion[] suggestions) {
        this.mContext = context;
        this.mSuggestions = suggestions;
    }

    public void setOnColorSuggestionClickListener(OnColorSuggestionClickListener listener) {
        this.mListener = listener;
    }

    private void setUpColorButton(View button, int index) {
        if (index >= this.mSuggestions.length) {
            button.setTag(null);
            button.setContentDescription(null);
            button.setVisibility(4);
            return;
        }
        button.setTag(this.mSuggestions[index]);
        button.setVisibility(0);
        ColorSuggestion suggestion = this.mSuggestions[index];
        ((GradientDrawable) ((LayerDrawable) button.getBackground()).findDrawableByLayerId(C0290R.id.color_button_swatch)).setColor(suggestion.mColor);
        String description = suggestion.mLabel;
        if (TextUtils.isEmpty(description)) {
            description = String.format("#%06X", new Object[]{Integer.valueOf(ViewCompat.MEASURED_SIZE_MASK & suggestion.mColor)});
        }
        button.setContentDescription(description);
        button.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (this.mListener != null) {
            ColorSuggestion suggestion = (ColorSuggestion) v.getTag();
            if (suggestion != null) {
                this.mListener.onColorSuggestionClick(suggestion);
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout;
        int i;
        if (convertView instanceof LinearLayout) {
            layout = (LinearLayout) convertView;
        } else {
            layout = new LinearLayout(this.mContext);
            layout.setLayoutParams(new LayoutParams(-1, -2));
            layout.setOrientation(0);
            layout.setBackgroundColor(-1);
            int buttonHeight = this.mContext.getResources().getDimensionPixelOffset(C0290R.dimen.color_button_height);
            for (i = 0; i < 4; i++) {
                View button = new View(this.mContext);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, buttonHeight, 1.0f);
                ApiCompatibilityUtils.setMarginStart(layoutParams, -1);
                if (i == 3) {
                    ApiCompatibilityUtils.setMarginEnd(layoutParams, -1);
                }
                button.setLayoutParams(layoutParams);
                button.setBackgroundResource(C0290R.drawable.color_button_background);
                layout.addView(button);
            }
        }
        for (i = 0; i < 4; i++) {
            setUpColorButton(layout.getChildAt(i), (position * 4) + i);
        }
        return layout;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public Object getItem(int position) {
        return null;
    }

    public int getCount() {
        return ((this.mSuggestions.length + 4) - 1) / 4;
    }
}
