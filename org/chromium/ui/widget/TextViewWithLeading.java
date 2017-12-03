package org.chromium.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import org.chromium.ui.C0290R;

public class TextViewWithLeading extends TextView {
    public TextViewWithLeading(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, C0290R.styleable.TextViewWithLeading, 0, 0);
        if (a.hasValue(C0290R.styleable.TextViewWithLeading_leading)) {
            setLineSpacing(a.getDimension(C0290R.styleable.TextViewWithLeading_leading, 0.0f) - getPaint().getFontMetrics(null), 1.0f);
        }
        a.recycle();
    }
}
