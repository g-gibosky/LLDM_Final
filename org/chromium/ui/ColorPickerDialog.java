package org.chromium.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ColorPickerDialog extends AlertDialog implements OnColorChangedListener {
    private final ColorPickerAdvanced mAdvancedColorPicker;
    private int mCurrentColor = this.mInitialColor;
    private final View mCurrentColorView;
    private final int mInitialColor;
    private final OnColorChangedListener mListener;
    private final Button mMoreButton;
    private final ColorPickerSimple mSimpleColorPicker;

    class C02841 implements OnClickListener {
        C02841() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ColorPickerDialog.this.tryNotifyColorSet(ColorPickerDialog.this.mCurrentColor);
        }
    }

    class C02852 implements OnClickListener {
        C02852() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ColorPickerDialog.this.tryNotifyColorSet(ColorPickerDialog.this.mInitialColor);
        }
    }

    class C02863 implements OnCancelListener {
        C02863() {
        }

        public void onCancel(DialogInterface arg0) {
            ColorPickerDialog.this.tryNotifyColorSet(ColorPickerDialog.this.mInitialColor);
        }
    }

    class C02874 implements View.OnClickListener {
        C02874() {
        }

        public void onClick(View v) {
            ColorPickerDialog.this.showAdvancedView();
        }
    }

    public ColorPickerDialog(Context context, OnColorChangedListener listener, int color, ColorSuggestion[] suggestions) {
        super(context, 0);
        this.mListener = listener;
        this.mInitialColor = color;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        View title = inflater.inflate(C0290R.layout.color_picker_dialog_title, null);
        setCustomTitle(title);
        this.mCurrentColorView = title.findViewById(C0290R.id.selected_color_view);
        ((TextView) title.findViewById(C0290R.id.title)).setText(C0290R.string.color_picker_dialog_title);
        setButton(-1, context.getString(C0290R.string.color_picker_button_set), new C02841());
        setButton(-2, context.getString(C0290R.string.color_picker_button_cancel), new C02852());
        setOnCancelListener(new C02863());
        View content = inflater.inflate(C0290R.layout.color_picker_dialog_content, null);
        setView(content);
        this.mMoreButton = (Button) content.findViewById(C0290R.id.more_colors_button);
        this.mMoreButton.setOnClickListener(new C02874());
        this.mAdvancedColorPicker = (ColorPickerAdvanced) content.findViewById(C0290R.id.color_picker_advanced);
        this.mAdvancedColorPicker.setVisibility(8);
        this.mSimpleColorPicker = (ColorPickerSimple) content.findViewById(C0290R.id.color_picker_simple);
        this.mSimpleColorPicker.init(suggestions, this);
        updateCurrentColor(this.mInitialColor);
    }

    public void onColorChanged(int color) {
        updateCurrentColor(color);
    }

    private void showAdvancedView() {
        findViewById(C0290R.id.more_colors_button_border).setVisibility(8);
        findViewById(C0290R.id.color_picker_simple).setVisibility(8);
        this.mAdvancedColorPicker.setVisibility(0);
        this.mAdvancedColorPicker.setListener(this);
        this.mAdvancedColorPicker.setColor(this.mCurrentColor);
    }

    private void tryNotifyColorSet(int color) {
        if (this.mListener != null) {
            this.mListener.onColorChanged(color);
        }
    }

    private void updateCurrentColor(int color) {
        this.mCurrentColor = color;
        if (this.mCurrentColorView != null) {
            this.mCurrentColorView.setBackgroundColor(color);
        }
    }
}
