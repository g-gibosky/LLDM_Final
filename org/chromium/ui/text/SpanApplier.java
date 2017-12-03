package org.chromium.ui.text;

import android.text.SpannableString;
import java.util.Arrays;

public class SpanApplier {

    public static final class SpanInfo implements Comparable<SpanInfo> {
        final String mEndTag;
        int mEndTagIndex;
        final Object mSpan;
        final String mStartTag;
        int mStartTagIndex;

        public SpanInfo(String startTag, String endTag, Object span) {
            this.mStartTag = startTag;
            this.mEndTag = endTag;
            this.mSpan = span;
        }

        public int compareTo(SpanInfo other) {
            if (this.mStartTagIndex < other.mStartTagIndex) {
                return -1;
            }
            return this.mStartTagIndex == other.mStartTagIndex ? 0 : 1;
        }

        public boolean equals(Object other) {
            if ((other instanceof SpanInfo) && compareTo((SpanInfo) other) == 0) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return 0;
        }
    }

    public static SpannableString applySpans(String input, SpanInfo... spans) {
        for (SpanInfo span : spans) {
            span.mStartTagIndex = input.indexOf(span.mStartTag);
            span.mEndTagIndex = input.indexOf(span.mEndTag, span.mStartTagIndex + span.mStartTag.length());
        }
        Arrays.sort(spans);
        int inputIndex = 0;
        StringBuilder output = new StringBuilder(input.length());
        for (SpanInfo span2 : spans) {
            if (span2.mStartTagIndex == -1 || span2.mEndTagIndex == -1 || span2.mStartTagIndex < inputIndex) {
                span2.mStartTagIndex = -1;
                throw new IllegalArgumentException(String.format("Input string is missing tags %s%s: %s", new Object[]{span2.mStartTag, span2.mEndTag, input}));
            }
            output.append(input, inputIndex, span2.mStartTagIndex);
            inputIndex = span2.mStartTagIndex + span2.mStartTag.length();
            span2.mStartTagIndex = output.length();
            output.append(input, inputIndex, span2.mEndTagIndex);
            inputIndex = span2.mEndTagIndex + span2.mEndTag.length();
            span2.mEndTagIndex = output.length();
        }
        output.append(input, inputIndex, input.length());
        SpannableString spannableString = new SpannableString(output);
        for (SpanInfo span22 : spans) {
            if (span22.mStartTagIndex != -1) {
                spannableString.setSpan(span22.mSpan, span22.mStartTagIndex, span22.mEndTagIndex, 0);
            }
        }
        return spannableString;
    }
}
