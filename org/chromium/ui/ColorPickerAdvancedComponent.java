package org.chromium.ui;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import org.chromium.base.ApiCompatibilityUtils;

public class ColorPickerAdvancedComponent {
    private int[] mGradientColors;
    private GradientDrawable mGradientDrawable = new GradientDrawable(Orientation.LEFT_RIGHT, null);
    private final View mGradientView;
    private final SeekBar mSeekBar;
    private final TextView mText;

    ColorPickerAdvancedComponent(View rootView, int textResourceId, int seekBarMax, OnSeekBarChangeListener seekBarListener) {
        this.mGradientView = rootView.findViewById(C0290R.id.gradient);
        this.mText = (TextView) rootView.findViewById(C0290R.id.text);
        this.mText.setText(textResourceId);
        this.mSeekBar = (SeekBar) rootView.findViewById(C0290R.id.seek_bar);
        this.mSeekBar.setOnSeekBarChangeListener(seekBarListener);
        this.mSeekBar.setMax(seekBarMax);
        this.mSeekBar.setThumbOffset(ApiCompatibilityUtils.getDrawable(rootView.getContext().getResources(), C0290R.drawable.color_picker_advanced_select_handle).getIntrinsicWidth() / 2);
    }

    public float getValue() {
        return (float) this.mSeekBar.getProgress();
    }

    public void setValue(float newValue) {
        this.mSeekBar.setProgress((int) newValue);
    }

    public void setGradientColors(int[] newColors) {
        this.mGradientColors = (int[]) newColors.clone();
        this.mGradientDrawable.setColors(this.mGradientColors);
        this.mGradientView.setBackground(this.mGradientDrawable);
    }
}
