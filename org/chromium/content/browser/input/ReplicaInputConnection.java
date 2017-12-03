package org.chromium.content.browser.input;

import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import org.chromium.base.VisibleForTesting;

public class ReplicaInputConnection extends BaseInputConnection implements ChromiumBaseInputConnection {
    private static final boolean DEBUG_LOGS = false;
    public static final int INVALID_COMPOSITION = -1;
    public static final int INVALID_SELECTION = -1;
    private static final String TAG = "cr_Ime";
    private final Editable mEditable;
    private final Handler mHandler;
    private final ImeAdapter mImeAdapter;
    private int mNumNestedBatchEdits = 0;
    private int mPendingAccent;
    private boolean mSingleLine;

    @VisibleForTesting
    static class ImeState {
        public final int compositionEnd;
        public final int compositionStart;
        public final int selectionEnd;
        public final int selectionStart;
        public final String text;

        public ImeState(String text, int selectionStart, int selectionEnd, int compositionStart, int compositionEnd) {
            this.text = text;
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
            this.compositionStart = compositionStart;
            this.compositionEnd = compositionEnd;
        }
    }

    static class Factory implements org.chromium.content.browser.input.ChromiumBaseInputConnection.Factory {
        private final Editable mEditable = android.text.Editable.Factory.getInstance().newEditable("");
        private final Handler mHandler = new Handler(Looper.getMainLooper());

        Factory() {
            Selection.setSelection(this.mEditable, 0);
        }

        public ReplicaInputConnection initializeAndGet(View view, ImeAdapter imeAdapter, int inputType, int inputFlags, int selectionStart, int selectionEnd, EditorInfo outAttrs) {
            new InputMethodUma().recordProxyViewReplicaInputConnection();
            return new ReplicaInputConnection(view, imeAdapter, this.mHandler, this.mEditable, inputType, inputFlags, outAttrs);
        }

        public Handler getHandler() {
            return this.mHandler;
        }

        public void onWindowFocusChanged(boolean gainFocus) {
        }

        public void onViewFocusChanged(boolean gainFocus) {
        }

        public void onViewAttachedToWindow() {
        }

        public void onViewDetachedFromWindow() {
        }
    }

    @VisibleForTesting
    ReplicaInputConnection(View view, ImeAdapter imeAdapter, Handler handler, Editable editable, int inputType, int inputFlags, EditorInfo outAttrs) {
        super(view, true);
        this.mImeAdapter = imeAdapter;
        this.mEditable = editable;
        this.mHandler = handler;
        ImeUtils.computeEditorInfo(inputType, inputFlags, Selection.getSelectionStart(editable), Selection.getSelectionEnd(editable), outAttrs);
    }

    public void updateStateOnUiThread(String text, int selectionStart, int selectionEnd, int compositionStart, int compositionEnd, boolean singleLine, boolean isNonImeChange) {
        this.mSingleLine = singleLine;
        if (isNonImeChange) {
            text = text.replace(' ', ' ');
            selectionStart = Math.min(selectionStart, text.length());
            selectionEnd = Math.min(selectionEnd, text.length());
            compositionStart = Math.min(compositionStart, text.length());
            compositionEnd = Math.min(compositionEnd, text.length());
            if (!this.mEditable.toString().equals(text)) {
                this.mEditable.replace(0, this.mEditable.length(), text);
            }
            Selection.setSelection(this.mEditable, selectionStart, selectionEnd);
            if (compositionStart == compositionEnd) {
                removeComposingSpans(this.mEditable);
            } else {
                super.setComposingRegion(compositionStart, compositionEnd);
            }
            updateSelectionIfRequired();
        }
    }

    public Editable getEditable() {
        return this.mEditable;
    }

    private void updateSelectionIfRequired() {
        if (this.mNumNestedBatchEdits == 0) {
            this.mImeAdapter.updateSelection(Selection.getSelectionStart(this.mEditable), Selection.getSelectionEnd(this.mEditable), getComposingSpanStart(this.mEditable), getComposingSpanEnd(this.mEditable));
        }
    }

    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        return updateComposingText(text, newCursorPosition, false);
    }

    private boolean updateComposingText(CharSequence text, int newCursorPosition, boolean isPendingAccent) {
        int accentToSend;
        if (isPendingAccent) {
            accentToSend = this.mPendingAccent;
        } else {
            accentToSend = 0;
        }
        this.mPendingAccent = 0;
        super.setComposingText(text, newCursorPosition);
        updateSelectionIfRequired();
        return this.mImeAdapter.sendCompositionToNative(text, newCursorPosition, false, accentToSend);
    }

    public boolean commitText(CharSequence text, int newCursorPosition) {
        this.mPendingAccent = 0;
        super.commitText(text, newCursorPosition);
        updateSelectionIfRequired();
        return this.mImeAdapter.sendCompositionToNative(text, newCursorPosition, text.length() > 0, 0);
    }

    public boolean performEditorAction(int actionCode) {
        return this.mImeAdapter.performEditorAction(actionCode);
    }

    public boolean performContextMenuAction(int id) {
        return this.mImeAdapter.performContextMenuAction(id);
    }

    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        ExtractedText et = new ExtractedText();
        et.text = this.mEditable.toString();
        et.partialEndOffset = this.mEditable.length();
        et.selectionStart = Selection.getSelectionStart(this.mEditable);
        et.selectionEnd = Selection.getSelectionEnd(this.mEditable);
        et.flags = this.mSingleLine ? 1 : 0;
        return et;
    }

    public boolean beginBatchEdit() {
        this.mNumNestedBatchEdits++;
        return true;
    }

    public boolean endBatchEdit() {
        if (this.mNumNestedBatchEdits == 0) {
            return false;
        }
        this.mNumNestedBatchEdits--;
        if (this.mNumNestedBatchEdits == 0) {
            updateSelectionIfRequired();
        }
        if (this.mNumNestedBatchEdits != 0) {
            return true;
        }
        return false;
    }

    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        return deleteSurroundingTextImpl(beforeLength, afterLength, false);
    }

    @VisibleForTesting
    static boolean isIndexBetweenUtf16SurrogatePair(CharSequence str, int index) {
        return index > 0 && index < str.length() && Character.isHighSurrogate(str.charAt(index - 1)) && Character.isLowSurrogate(str.charAt(index));
    }

    private boolean deleteSurroundingTextImpl(int beforeLength, int afterLength, boolean fromPhysicalKey) {
        if (this.mPendingAccent != 0) {
            finishComposingText();
        }
        int selectionStart = Selection.getSelectionStart(this.mEditable);
        int selectionEnd = Selection.getSelectionEnd(this.mEditable);
        int availableAfter = this.mEditable.length() - selectionEnd;
        beforeLength = Math.min(beforeLength, selectionStart);
        afterLength = Math.min(afterLength, availableAfter);
        if (isIndexBetweenUtf16SurrogatePair(this.mEditable, selectionStart - beforeLength)) {
            beforeLength++;
        }
        if (isIndexBetweenUtf16SurrogatePair(this.mEditable, selectionEnd + afterLength)) {
            afterLength++;
        }
        super.deleteSurroundingText(beforeLength, afterLength);
        updateSelectionIfRequired();
        if (fromPhysicalKey) {
            return true;
        }
        return this.mImeAdapter.deleteSurroundingText(beforeLength, afterLength);
    }

    public boolean sendKeyEventOnUiThread(KeyEvent event) {
        return sendKeyEvent(event);
    }

    public boolean sendKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keycode = event.getKeyCode();
        int unicodeChar = event.getUnicodeChar();
        if (action != 0) {
            this.mImeAdapter.sendKeyEvent(event);
        } else {
            if (keycode == 67) {
                deleteSurroundingTextImpl(1, 0, true);
            } else if (keycode == 112) {
                deleteSurroundingTextImpl(0, 1, true);
            } else if (keycode == 66) {
                finishComposingText();
            } else if ((Integer.MIN_VALUE & unicodeChar) != 0) {
                int pendingAccent = unicodeChar & Integer.MAX_VALUE;
                builder = new StringBuilder();
                builder.appendCodePoint(pendingAccent);
                updateComposingText(builder.toString(), 1, true);
                this.mPendingAccent = pendingAccent;
            } else if (!(this.mPendingAccent == 0 || unicodeChar == 0)) {
                int combined = KeyEvent.getDeadChar(this.mPendingAccent, unicodeChar);
                if (combined != 0) {
                    builder = new StringBuilder();
                    builder.appendCodePoint(combined);
                    commitText(builder.toString(), 1);
                } else {
                    finishComposingText();
                }
            }
            replaceSelectionWithUnicodeChar(unicodeChar);
            this.mImeAdapter.sendKeyEvent(event);
        }
        return true;
    }

    private void replaceSelectionWithUnicodeChar(int unicodeChar) {
        if (unicodeChar != 0) {
            int selectionStart = Selection.getSelectionStart(this.mEditable);
            int selectionEnd = Selection.getSelectionEnd(this.mEditable);
            if (selectionStart > selectionEnd) {
                int temp = selectionStart;
                selectionStart = selectionEnd;
                selectionEnd = temp;
            }
            this.mEditable.replace(selectionStart, selectionEnd, Character.toString((char) unicodeChar));
            updateSelectionIfRequired();
        }
    }

    public boolean finishComposingText() {
        this.mPendingAccent = 0;
        if (getComposingSpanStart(this.mEditable) != getComposingSpanEnd(this.mEditable)) {
            super.finishComposingText();
            updateSelectionIfRequired();
            this.mImeAdapter.finishComposingText();
        }
        return true;
    }

    public boolean setSelection(int start, int end) {
        int textLength = this.mEditable.length();
        if (start < 0 || end < 0 || start > textLength || end > textLength) {
            return true;
        }
        super.setSelection(start, end);
        updateSelectionIfRequired();
        return this.mImeAdapter.setEditableSelectionOffsets(start, end);
    }

    public boolean setComposingRegion(int start, int end) {
        int textLength = this.mEditable.length();
        int a = Math.min(start, end);
        int b = Math.max(start, end);
        if (a < 0) {
            a = 0;
        }
        if (b < 0) {
            b = 0;
        }
        if (a > textLength) {
            a = textLength;
        }
        if (b > textLength) {
            b = textLength;
        }
        if (a == b) {
            removeComposingSpans(this.mEditable);
        } else {
            super.setComposingRegion(a, b);
        }
        updateSelectionIfRequired();
        return this.mImeAdapter.setComposingRegion(a, b);
    }

    public void onRestartInputOnUiThread() {
        this.mNumNestedBatchEdits = 0;
        this.mPendingAccent = 0;
    }

    public void moveCursorToSelectionEndOnUiThread() {
        int selectionEnd = Selection.getSelectionEnd(this.mEditable);
        setSelection(selectionEnd, selectionEnd);
    }

    public void unblockOnUiThread() {
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    @TargetApi(21)
    public boolean requestCursorUpdates(int cursorUpdateMode) {
        return this.mImeAdapter.onRequestCursorUpdates(cursorUpdateMode);
    }

    @VisibleForTesting
    ImeState getImeStateForTesting() {
        return new ImeState(this.mEditable.toString(), Selection.getSelectionStart(this.mEditable), Selection.getSelectionEnd(this.mEditable), getComposingSpanStart(this.mEditable), getComposingSpanEnd(this.mEditable));
    }
}
