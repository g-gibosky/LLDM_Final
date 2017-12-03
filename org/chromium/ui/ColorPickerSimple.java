package org.chromium.ui;

import android.content.Context;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ListView;
import org.chromium.ui.ColorSuggestionListAdapter.OnColorSuggestionClickListener;

public class ColorPickerSimple extends ListView implements OnColorSuggestionClickListener {
    private static final int[] DEFAULT_COLORS = new int[]{SupportMenu.CATEGORY_MASK, -16711681, -16776961, -16711936, -65281, -256, ViewCompat.MEASURED_STATE_MASK, -1};
    private static final int[] DEFAULT_COLOR_LABEL_IDS = new int[]{C0290R.string.color_picker_button_red, C0290R.string.color_picker_button_cyan, C0290R.string.color_picker_button_blue, C0290R.string.color_picker_button_green, C0290R.string.color_picker_button_magenta, C0290R.string.color_picker_button_yellow, C0290R.string.color_picker_button_black, C0290R.string.color_picker_button_white};
    private OnColorChangedListener mOnColorChangedListener;

    public ColorPickerSimple(Context context) {
        super(context);
    }

    public ColorPickerSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPickerSimple(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(ColorSuggestion[] suggestions, OnColorChangedListener onColorChangedListener) {
        this.mOnColorChangedListener = onColorChangedListener;
        if (suggestions == null) {
            suggestions = new ColorSuggestion[DEFAULT_COLORS.length];
            for (int i = 0; i < suggestions.length; i++) {
                suggestions[i] = new ColorSuggestion(DEFAULT_COLORS[i], getContext().getString(DEFAULT_COLOR_LABEL_IDS[i]));
            }
        }
        ColorSuggestionListAdapter adapter = new ColorSuggestionListAdapter(getContext(), suggestions);
        adapter.setOnColorSuggestionClickListener(this);
        setAdapter(adapter);
    }

    public void onColorSuggestionClick(ColorSuggestion suggestion) {
        this.mOnColorChangedListener.onColorChanged(suggestion.mColor);
    }
}
