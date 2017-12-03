package org.chromium.ui.widget;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.Button;
import org.chromium.ui.C0290R;

@TargetApi(21)
public class ButtonCompat extends Button {
    private static final int DISABLED_COLOR = 1112493903;
    private static final float PRE_L_PRESSED_BRIGHTNESS = 0.85f;
    private int mColor;

    public static Button createBorderlessButton(Context context) {
        return new Button(new ContextThemeWrapper(context, C0290R.style.ButtonCompatBorderlessOverlay));
    }

    public ButtonCompat(Context context, int buttonColor, boolean buttonRaised) {
        this(context, buttonColor, buttonRaised, null);
    }

    public ButtonCompat(Context context, AttributeSet attrs) {
        this(context, getColorFromAttributeSet(context, attrs), getRaisedStatusFromAttributeSet(context, attrs), attrs);
    }

    private ButtonCompat(Context context, int buttonColor, boolean buttonRaised, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, C0290R.style.ButtonCompatOverlay), attrs);
        getBackground().mutate();
        setButtonColor(buttonColor);
        setRaised(buttonRaised);
    }

    public void setButtonColor(int color) {
        if (color != this.mColor) {
            this.mColor = color;
            if (VERSION.SDK_INT >= 21) {
                updateButtonBackgroundL();
            } else {
                updateButtonBackgroundPreL();
            }
        }
    }

    private void setRaised(boolean raised) {
        if (VERSION.SDK_INT >= 21) {
            if (raised) {
                TypedArray a = getContext().obtainStyledAttributes(null, new int[]{16843848}, 0, 16974424);
                int stateListAnimatorId = a.getResourceId(0, 0);
                a.recycle();
                StateListAnimator stateListAnimator = null;
                if (stateListAnimatorId != 0) {
                    stateListAnimator = AnimatorInflater.loadStateListAnimator(getContext(), stateListAnimatorId);
                }
                setStateListAnimator(stateListAnimator);
                return;
            }
            setElevation(0.0f);
            setStateListAnimator(null);
        }
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (VERSION.SDK_INT < 21) {
            updateButtonBackgroundPreL();
        }
    }

    @TargetApi(21)
    private void updateButtonBackgroundL() {
        r2 = new int[2][];
        r2[0] = new int[]{-16842910};
        r2[1] = new int[0];
        ColorStateList csl = new ColorStateList(r2, new int[]{DISABLED_COLOR, this.mColor});
        GradientDrawable shape = (GradientDrawable) ((RippleDrawable) getBackground()).getDrawable(0);
        shape.mutate();
        shape.setColor(csl);
    }

    private void updateButtonBackgroundPreL() {
        ((GradientDrawable) getBackground()).setColor(getBackgroundColorPreL());
    }

    private int getBackgroundColorPreL() {
        for (int state : getDrawableState()) {
            if (state == 16842919 || state == 16842908 || state == 16842913) {
                return Color.rgb(Math.round(((float) Color.red(this.mColor)) * PRE_L_PRESSED_BRIGHTNESS), Math.round(((float) Color.green(this.mColor)) * PRE_L_PRESSED_BRIGHTNESS), Math.round(((float) Color.blue(this.mColor)) * PRE_L_PRESSED_BRIGHTNESS));
            }
        }
        for (int state2 : getDrawableState()) {
            if (state2 == 16842910) {
                return this.mColor;
            }
        }
        return DISABLED_COLOR;
    }

    private static int getColorFromAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, C0290R.styleable.ButtonCompat, 0, 0);
        int color = a.getColor(C0290R.styleable.ButtonCompat_buttonColor, -1);
        a.recycle();
        return color;
    }

    private static boolean getRaisedStatusFromAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, C0290R.styleable.ButtonCompat, 0, 0);
        boolean raised = a.getBoolean(C0290R.styleable.ButtonCompat_buttonRaised, true);
        a.recycle();
        return raised;
    }
}
