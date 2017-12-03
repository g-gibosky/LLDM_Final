package org.chromium.content.browser.accessibility.captioning;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.view.accessibility.CaptioningManager.CaptionStyle;

@TargetApi(19)
public class CaptioningStyle {
    private Integer mBackgroundColor;
    private Integer mEdgeColor;
    private Integer mEdgeType;
    private Integer mForegroundColor;
    private Typeface mTypeface;
    private Integer mWindowColor;

    public CaptioningStyle(Integer backgroundColor, Integer edgeColor, Integer edgeType, Integer foregroundColor, Integer windowColor, Typeface typeface) {
        this.mBackgroundColor = backgroundColor;
        this.mEdgeColor = edgeColor;
        this.mEdgeType = edgeType;
        this.mForegroundColor = foregroundColor;
        this.mWindowColor = windowColor;
        this.mTypeface = typeface;
    }

    public Integer getBackgroundColor() {
        return this.mBackgroundColor;
    }

    public Integer getEdgeColor() {
        return this.mEdgeColor;
    }

    public Integer getEdgeType() {
        return this.mEdgeType;
    }

    public Integer getForegroundColor() {
        return this.mForegroundColor;
    }

    public Integer getWindowColor() {
        return this.mWindowColor;
    }

    public Typeface getTypeface() {
        return this.mTypeface;
    }

    @SuppressLint({"NewApi"})
    public static CaptioningStyle createFrom(CaptionStyle captionStyle) {
        if (captionStyle == null) {
            return new CaptioningStyle(null, null, null, null, null, null);
        }
        Integer backgroundColor = null;
        Integer edgeColor = null;
        Integer edgeType = null;
        Integer foregroundColor = null;
        Integer windowColor = null;
        if (VERSION.SDK_INT >= 21) {
            if (captionStyle.hasBackgroundColor()) {
                backgroundColor = Integer.valueOf(captionStyle.backgroundColor);
            }
            if (captionStyle.hasEdgeColor()) {
                edgeColor = Integer.valueOf(captionStyle.edgeColor);
            }
            if (captionStyle.hasEdgeType()) {
                edgeType = Integer.valueOf(captionStyle.edgeType);
            }
            if (captionStyle.hasForegroundColor()) {
                foregroundColor = Integer.valueOf(captionStyle.foregroundColor);
            }
            if (captionStyle.hasWindowColor()) {
                windowColor = Integer.valueOf(captionStyle.windowColor);
            }
        } else {
            backgroundColor = Integer.valueOf(captionStyle.backgroundColor);
            edgeColor = Integer.valueOf(captionStyle.edgeColor);
            edgeType = Integer.valueOf(captionStyle.edgeType);
            foregroundColor = Integer.valueOf(captionStyle.foregroundColor);
        }
        return new CaptioningStyle(backgroundColor, edgeColor, edgeType, foregroundColor, windowColor, captionStyle.getTypeface());
    }
}
