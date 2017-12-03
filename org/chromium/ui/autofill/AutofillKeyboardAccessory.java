package org.chromium.ui.autofill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.ui.C0290R;
import org.chromium.ui.UiUtils;
import org.chromium.ui.base.WindowAndroid;
import org.chromium.ui.base.WindowAndroid.KeyboardVisibilityListener;
import org.chromium.ui.gfx.DeviceDisplayInfo;

public class AutofillKeyboardAccessory extends LinearLayout implements KeyboardVisibilityListener, OnClickListener, OnLongClickListener {
    static final /* synthetic */ boolean $assertionsDisabled = (!AutofillKeyboardAccessory.class.desiredAssertionStatus());
    private final AutofillDelegate mAutofillDelegate;
    private final int mMaximumLabelWidthPx;
    private final int mMaximumSublabelWidthPx;
    private final WindowAndroid mWindowAndroid;

    public AutofillKeyboardAccessory(WindowAndroid windowAndroid, AutofillDelegate autofillDelegate) {
        super((Context) windowAndroid.getActivity().get());
        if (!$assertionsDisabled && autofillDelegate == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || windowAndroid.getActivity().get() != null) {
            this.mWindowAndroid = windowAndroid;
            this.mAutofillDelegate = autofillDelegate;
            int deviceWidthPx = DeviceDisplayInfo.create(getContext()).getDisplayWidth();
            this.mMaximumLabelWidthPx = deviceWidthPx / 2;
            this.mMaximumSublabelWidthPx = deviceWidthPx / 4;
            this.mWindowAndroid.addKeyboardVisibilityListener(this);
            int horizontalPaddingPx = getResources().getDimensionPixelSize(C0290R.dimen.keyboard_accessory_half_padding);
            setPadding(horizontalPaddingPx, 0, horizontalPaddingPx, 0);
        } else {
            throw new AssertionError();
        }
    }

    @SuppressLint({"InlinedApi"})
    public void showWithSuggestions(AutofillSuggestion[] suggestions, final boolean isRtl) {
        int i;
        removeAllViews();
        int separatorPosition = -1;
        int i2 = 0;
        while (i2 < suggestions.length) {
            AutofillSuggestion suggestion = suggestions[i2];
            if ($assertionsDisabled || !TextUtils.isEmpty(suggestion.getLabel())) {
                View touchTarget;
                if (suggestion.isFillable() || suggestion.getIconId() == 0) {
                    touchTarget = LayoutInflater.from(getContext()).inflate(C0290R.layout.autofill_keyboard_accessory_item, this, false);
                    TextView label = (TextView) touchTarget.findViewById(C0290R.id.autofill_keyboard_accessory_item_label);
                    if (suggestion.isFillable()) {
                        label.setMaxWidth(this.mMaximumLabelWidthPx);
                    }
                    label.setText(suggestion.getLabel());
                    if (VERSION.SDK_INT < 21) {
                        label.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                    if (suggestion.getIconId() != 0) {
                        ApiCompatibilityUtils.setCompoundDrawablesRelativeWithIntrinsicBounds(label, suggestion.getIconId(), 0, 0, 0);
                    }
                    if (!TextUtils.isEmpty(suggestion.getSublabel())) {
                        if ($assertionsDisabled || suggestion.isFillable()) {
                            TextView sublabel = (TextView) touchTarget.findViewById(C0290R.id.autofill_keyboard_accessory_item_sublabel);
                            sublabel.setText(suggestion.getSublabel());
                            sublabel.setVisibility(0);
                            sublabel.setMaxWidth(this.mMaximumSublabelWidthPx);
                        } else {
                            throw new AssertionError();
                        }
                    }
                }
                touchTarget = LayoutInflater.from(getContext()).inflate(C0290R.layout.autofill_keyboard_accessory_icon, this, false);
                if (separatorPosition == -1) {
                    separatorPosition = i2;
                }
                ImageView icon = (ImageView) touchTarget;
                icon.setImageResource(suggestion.getIconId());
                icon.setContentDescription(suggestion.getLabel());
                touchTarget.setTag(Integer.valueOf(i2));
                touchTarget.setOnClickListener(this);
                if (suggestion.isDeletable()) {
                    touchTarget.setOnLongClickListener(this);
                }
                addView(touchTarget);
                i2++;
            } else {
                throw new AssertionError();
            }
        }
        if (separatorPosition != -1) {
            View separator = new View(getContext());
            separator.setLayoutParams(new LayoutParams(0, 0, 1.0f));
            addView(separator, separatorPosition);
        }
        if (isRtl) {
            i = 1;
        } else {
            i = 0;
        }
        ApiCompatibilityUtils.setLayoutDirection(this, i);
        final HorizontalScrollView container = (HorizontalScrollView) this.mWindowAndroid.getKeyboardAccessoryView();
        if (getParent() == null) {
            container.addView(this);
            container.setVisibility(0);
            container.sendAccessibilityEvent(32);
        }
        container.post(new Runnable() {
            public void run() {
                int right;
                HorizontalScrollView horizontalScrollView = container;
                if (isRtl) {
                    right = AutofillKeyboardAccessory.this.getRight();
                } else {
                    right = 0;
                }
                horizontalScrollView.scrollTo(right, 0);
            }
        });
    }

    public void dismiss() {
        ViewGroup container = this.mWindowAndroid.getKeyboardAccessoryView();
        container.removeView(this);
        container.setVisibility(8);
        this.mWindowAndroid.removeKeyboardVisibilityListener(this);
        ((View) container.getParent()).requestLayout();
    }

    public void keyboardVisibilityChanged(boolean isShowing) {
        if (!isShowing) {
            dismiss();
            this.mAutofillDelegate.dismissed();
        }
    }

    public void onClick(View v) {
        UiUtils.hideKeyboard(this);
        this.mAutofillDelegate.suggestionSelected(((Integer) v.getTag()).intValue());
    }

    public boolean onLongClick(View v) {
        this.mAutofillDelegate.deleteSuggestion(((Integer) v.getTag()).intValue());
        return true;
    }
}
