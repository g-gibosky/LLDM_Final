package org.chromium.content.browser.input;

import android.text.Editable;
import android.text.Selection;
import android.util.StringBuilderPrinter;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import java.util.Locale;
import org.chromium.base.ThreadUtils;

public class ImeUtils {
    public static void computeEditorInfo(int inputType, int inputFlags, int initialSelStart, int initialSelEnd, EditorInfo outAttrs) {
        outAttrs.imeOptions = 301989888;
        outAttrs.inputType = 161;
        if ((inputFlags & 2) != 0) {
            outAttrs.inputType |= 524288;
        }
        if (inputType == 1) {
            outAttrs.imeOptions |= 2;
            if ((inputFlags & 8) == 0) {
                outAttrs.inputType |= 32768;
            }
        } else if (inputType == 14 || inputType == 15) {
            outAttrs.inputType |= 131072;
            if ((inputFlags & 8) == 0) {
                outAttrs.inputType |= 32768;
            }
            outAttrs.imeOptions |= 1;
        } else if (inputType == 2) {
            outAttrs.inputType = 225;
            outAttrs.imeOptions |= 2;
        } else if (inputType == 3) {
            outAttrs.imeOptions |= 3;
        } else if (inputType == 7) {
            outAttrs.inputType = 17;
            outAttrs.imeOptions |= 2;
        } else if (inputType == 4) {
            outAttrs.inputType = 209;
            outAttrs.imeOptions |= 2;
        } else if (inputType == 6) {
            outAttrs.inputType = 3;
            outAttrs.imeOptions |= 5;
        } else if (inputType == 5) {
            outAttrs.inputType = 8194;
            outAttrs.imeOptions |= 5;
        }
        if ((inputFlags & 128) != 0) {
            outAttrs.inputType |= 4096;
        } else if ((inputFlags & 256) != 0) {
            outAttrs.inputType |= 8192;
        } else if ((inputFlags & 512) != 0) {
            outAttrs.inputType |= 16384;
        }
        if (inputType == 15) {
            outAttrs.inputType |= 16384;
        }
        outAttrs.initialSelStart = initialSelStart;
        outAttrs.initialSelEnd = initialSelEnd;
    }

    static String getEditorInfoDebugString(EditorInfo editorInfo) {
        StringBuilder builder = new StringBuilder();
        editorInfo.dump(new StringBuilderPrinter(builder), "");
        return builder.toString();
    }

    static String getEditableDebugString(Editable editable) {
        return String.format(Locale.US, "Editable {[%s] SEL[%d %d] COM[%d %d]}", new Object[]{editable.toString(), Integer.valueOf(Selection.getSelectionStart(editable)), Integer.valueOf(Selection.getSelectionEnd(editable)), Integer.valueOf(BaseInputConnection.getComposingSpanStart(editable)), Integer.valueOf(BaseInputConnection.getComposingSpanEnd(editable))});
    }

    static String getCorrectionInfoDebugString(CorrectionInfo correctionInfo) {
        return correctionInfo.toString();
    }

    static void checkCondition(boolean condition) {
        if (!condition) {
            throw new AssertionError();
        }
    }

    static void checkCondition(String msg, boolean condition) {
        if (!condition) {
            throw new AssertionError(msg);
        }
    }

    static void checkOnUiThread() {
        checkCondition("Should be on UI thread.", ThreadUtils.runningOnUiThread());
    }
}
