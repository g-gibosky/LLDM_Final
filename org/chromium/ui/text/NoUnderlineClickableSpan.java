package org.chromium.ui.text;

import android.text.TextPaint;
import android.text.style.ClickableSpan;

public abstract class NoUnderlineClickableSpan extends ClickableSpan {
    public void updateDrawState(TextPaint textPaint) {
        super.updateDrawState(textPaint);
        textPaint.setUnderlineText(false);
    }
}
