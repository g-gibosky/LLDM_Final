package org.chromium.content.browser.input;

import android.text.TextUtils;
import java.util.Locale;

public class TextInputState {
    private final Range mComposition;
    private final boolean mFromIme;
    private final Range mSelection;
    private final boolean mSingleLine;
    private final CharSequence mText;

    public TextInputState(CharSequence text, Range selection, Range composition, boolean singleLine, boolean fromIme) {
        selection.clamp(0, text.length());
        if (!(composition.start() == -1 && composition.end() == -1)) {
            composition.clamp(0, text.length());
        }
        this.mText = text;
        this.mSelection = selection;
        this.mComposition = composition;
        this.mSingleLine = singleLine;
        this.mFromIme = fromIme;
    }

    public CharSequence text() {
        return this.mText;
    }

    public Range selection() {
        return this.mSelection;
    }

    public Range composition() {
        return this.mComposition;
    }

    public boolean singleLine() {
        return this.mSingleLine;
    }

    public boolean fromIme() {
        return this.mFromIme;
    }

    public CharSequence getSelectedText() {
        if (this.mSelection.start() == this.mSelection.end()) {
            return null;
        }
        return TextUtils.substring(this.mText, this.mSelection.start(), this.mSelection.end());
    }

    public CharSequence getTextAfterSelection(int maxChars) {
        return TextUtils.substring(this.mText, this.mSelection.end(), Math.min(this.mText.length(), this.mSelection.end() + maxChars));
    }

    public CharSequence getTextBeforeSelection(int maxChars) {
        return TextUtils.substring(this.mText, Math.max(0, this.mSelection.start() - maxChars), this.mSelection.start());
    }

    public boolean equals(Object o) {
        if (!(o instanceof TextInputState)) {
            return false;
        }
        TextInputState t = (TextInputState) o;
        if (t == this) {
            return true;
        }
        if (TextUtils.equals(this.mText, t.mText) && this.mSelection.equals(t.mSelection) && this.mComposition.equals(t.mComposition) && this.mSingleLine == t.mSingleLine && this.mFromIme == t.mFromIme) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (this.mSingleLine ? 19 : 0) + ((this.mComposition.hashCode() * 13) + ((this.mText.hashCode() * 7) + (this.mSelection.hashCode() * 11)));
        if (this.mFromIme) {
            i = 23;
        }
        return hashCode + i;
    }

    public boolean shouldUnblock() {
        return false;
    }

    public String toString() {
        Locale locale = Locale.US;
        String str = "TextInputState {[%s] SEL%s COM%s %s %s}";
        Object[] objArr = new Object[5];
        objArr[0] = this.mText;
        objArr[1] = this.mSelection;
        objArr[2] = this.mComposition;
        objArr[3] = this.mSingleLine ? "SIN" : "MUL";
        objArr[4] = this.mFromIme ? "fromIME" : "NOTfromIME";
        return String.format(locale, str, objArr);
    }
}
