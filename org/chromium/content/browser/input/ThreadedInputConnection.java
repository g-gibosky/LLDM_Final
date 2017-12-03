package org.chromium.content.browser.input;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;

public class ThreadedInputConnection extends BaseInputConnection implements ChromiumBaseInputConnection {
    private static final boolean DEBUG_LOGS = false;
    private static final String TAG = "cr_Ime";
    private static final TextInputState UNBLOCKER = new TextInputState("", new Range(0, 0), new Range(-1, -1), false, false) {
        public boolean shouldUnblock() {
            return true;
        }
    };
    private final Runnable mFinishComposingTextRunnable = new C02296();
    private final Handler mHandler;
    private final ImeAdapter mImeAdapter;
    private final Runnable mMoveCursorSelectionEndRunnable = new C02263();
    private final Runnable mNotifyUserActionRunnable = new C02285();
    private int mNumNestedBatchEdits;
    private int mPendingAccent;
    private final Runnable mProcessPendingInputStatesRunnable = new C02252();
    private final BlockingQueue<TextInputState> mQueue = new LinkedBlockingQueue();
    private final Runnable mRequestTextInputStateUpdate = new C02274();

    class C02252 implements Runnable {
        C02252() {
        }

        public void run() {
            ThreadedInputConnection.this.processPendingInputStates();
        }
    }

    class C02263 implements Runnable {
        C02263() {
        }

        public void run() {
            TextInputState textInputState = ThreadedInputConnection.this.requestAndWaitForTextInputState();
            if (textInputState != null) {
                Range selection = textInputState.selection();
                ThreadedInputConnection.this.setSelection(selection.end(), selection.end());
            }
        }
    }

    class C02274 implements Runnable {
        C02274() {
        }

        public void run() {
            if (!ThreadedInputConnection.this.mImeAdapter.requestTextInputStateUpdate()) {
                ThreadedInputConnection.this.unblockOnUiThread();
            }
        }
    }

    class C02285 implements Runnable {
        C02285() {
        }

        public void run() {
            ThreadedInputConnection.this.mImeAdapter.notifyUserAction();
        }
    }

    class C02296 implements Runnable {
        C02296() {
        }

        public void run() {
            ThreadedInputConnection.this.mImeAdapter.finishComposingText();
        }
    }

    ThreadedInputConnection(View view, ImeAdapter imeAdapter, Handler handler) {
        super(view, true);
        ImeUtils.checkOnUiThread();
        this.mImeAdapter = imeAdapter;
        this.mHandler = handler;
    }

    void resetOnUiThread() {
        ImeUtils.checkOnUiThread();
        this.mNumNestedBatchEdits = 0;
        this.mPendingAccent = 0;
    }

    public void updateStateOnUiThread(String text, int selectionStart, int selectionEnd, int compositionStart, int compositionEnd, boolean singleLine, boolean isNonImeChange) {
        ImeUtils.checkOnUiThread();
        addToQueueOnUiThread(new TextInputState(text, new Range(selectionStart, selectionEnd), new Range(compositionStart, compositionEnd), singleLine, !isNonImeChange));
        if (isNonImeChange) {
            this.mHandler.post(this.mProcessPendingInputStatesRunnable);
        }
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void onRestartInputOnUiThread() {
    }

    public boolean sendKeyEventOnUiThread(final KeyEvent event) {
        ImeUtils.checkOnUiThread();
        this.mHandler.post(new Runnable() {
            public void run() {
                ThreadedInputConnection.this.sendKeyEvent(event);
            }
        });
        return true;
    }

    public void moveCursorToSelectionEndOnUiThread() {
        this.mHandler.post(this.mMoveCursorSelectionEndRunnable);
    }

    @VisibleForTesting
    public void unblockOnUiThread() {
        ImeUtils.checkOnUiThread();
        addToQueueOnUiThread(UNBLOCKER);
        this.mHandler.post(this.mProcessPendingInputStatesRunnable);
    }

    private void processPendingInputStates() {
        assertOnImeThread();
        while (true) {
            TextInputState state = (TextInputState) this.mQueue.poll();
            if (state != null) {
                if (!state.shouldUnblock()) {
                    updateSelection(state);
                }
            } else {
                return;
            }
        }
    }

    private void updateSelection(TextInputState textInputState) {
        if (textInputState != null) {
            assertOnImeThread();
            if (this.mNumNestedBatchEdits == 0) {
                Range selection = textInputState.selection();
                Range composition = textInputState.composition();
                this.mImeAdapter.updateSelection(selection.start(), selection.end(), composition.start(), composition.end());
            }
        }
    }

    private TextInputState requestAndWaitForTextInputState() {
        ThreadUtils.postOnUiThread(this.mRequestTextInputStateUpdate);
        return blockAndGetStateUpdate();
    }

    private void addToQueueOnUiThread(TextInputState textInputState) {
        ImeUtils.checkOnUiThread();
        try {
            this.mQueue.put(textInputState);
        } catch (InterruptedException e) {
            Log.m28e(TAG, "addToQueueOnUiThread interrupted", e);
        }
    }

    BlockingQueue<TextInputState> getQueueForTest() {
        return this.mQueue;
    }

    private void assertOnImeThread() {
        ImeUtils.checkCondition(this.mHandler.getLooper() == Looper.myLooper());
    }

    private TextInputState blockAndGetStateUpdate() {
        TextInputState state;
        assertOnImeThread();
        boolean shouldUpdateSelection = false;
        while (true) {
            try {
                state = (TextInputState) this.mQueue.take();
                if (state.shouldUnblock()) {
                    return null;
                }
                if (state.fromIme()) {
                    break;
                }
                shouldUpdateSelection = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                ImeUtils.checkCondition(false);
                return null;
            }
        }
        if (!shouldUpdateSelection) {
            return state;
        }
        updateSelection(state);
        return state;
    }

    private void notifyUserAction() {
        ThreadUtils.postOnUiThread(this.mNotifyUserActionRunnable);
    }

    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        return updateComposingText(text, newCursorPosition, false);
    }

    @VisibleForTesting
    public boolean updateComposingText(final CharSequence text, final int newCursorPosition, boolean isPendingAccent) {
        final int accentToSend = isPendingAccent ? this.mPendingAccent | Integer.MIN_VALUE : 0;
        assertOnImeThread();
        cancelCombiningAccent();
        ThreadUtils.postOnUiThread(new Runnable() {
            public void run() {
                ThreadedInputConnection.this.mImeAdapter.sendCompositionToNative(text, newCursorPosition, false, accentToSend);
            }
        });
        notifyUserAction();
        return true;
    }

    public boolean commitText(final CharSequence text, final int newCursorPosition) {
        assertOnImeThread();
        cancelCombiningAccent();
        ThreadUtils.postOnUiThread(new Runnable() {
            public void run() {
                boolean z;
                ImeAdapter access$200 = ThreadedInputConnection.this.mImeAdapter;
                CharSequence charSequence = text;
                int i = newCursorPosition;
                if (text.length() > 0) {
                    z = true;
                } else {
                    z = false;
                }
                access$200.sendCompositionToNative(charSequence, i, z, 0);
            }
        });
        notifyUserAction();
        return true;
    }

    public boolean performEditorAction(final int actionCode) {
        assertOnImeThread();
        ThreadUtils.postOnUiThread(new Runnable() {
            public void run() {
                ThreadedInputConnection.this.mImeAdapter.performEditorAction(actionCode);
            }
        });
        return true;
    }

    public boolean performContextMenuAction(final int id) {
        assertOnImeThread();
        ThreadUtils.postOnUiThread(new Runnable() {
            public void run() {
                ThreadedInputConnection.this.mImeAdapter.performContextMenuAction(id);
            }
        });
        return true;
    }

    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        assertOnImeThread();
        TextInputState textInputState = requestAndWaitForTextInputState();
        if (textInputState == null) {
            return null;
        }
        ExtractedText extractedText = new ExtractedText();
        extractedText.text = textInputState.text();
        extractedText.partialEndOffset = textInputState.text().length();
        extractedText.selectionStart = textInputState.selection().start();
        extractedText.selectionEnd = textInputState.selection().end();
        extractedText.flags = textInputState.singleLine() ? 1 : 0;
        return extractedText;
    }

    public boolean beginBatchEdit() {
        assertOnImeThread();
        this.mNumNestedBatchEdits++;
        return true;
    }

    public boolean endBatchEdit() {
        assertOnImeThread();
        if (this.mNumNestedBatchEdits == 0) {
            return false;
        }
        this.mNumNestedBatchEdits--;
        if (this.mNumNestedBatchEdits == 0) {
            updateSelection(requestAndWaitForTextInputState());
        }
        if (this.mNumNestedBatchEdits != 0) {
            return true;
        }
        return false;
    }

    public boolean deleteSurroundingText(final int beforeLength, final int afterLength) {
        assertOnImeThread();
        if (this.mPendingAccent != 0) {
            finishComposingText();
        }
        ThreadUtils.postOnUiThread(new Runnable() {
            public void run() {
                ThreadedInputConnection.this.mImeAdapter.deleteSurroundingText(beforeLength, afterLength);
            }
        });
        return true;
    }

    public boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) {
        return false;
    }

    public boolean sendKeyEvent(final KeyEvent event) {
        assertOnImeThread();
        if (!handleCombiningAccent(event)) {
            ThreadUtils.postOnUiThread(new Runnable() {
                public void run() {
                    ThreadedInputConnection.this.mImeAdapter.sendKeyEvent(event);
                }
            });
            notifyUserAction();
        }
        return true;
    }

    private boolean handleCombiningAccent(KeyEvent event) {
        int action = event.getAction();
        int unicodeChar = event.getUnicodeChar();
        if (action != 0) {
            return false;
        }
        StringBuilder builder;
        if ((Integer.MIN_VALUE & unicodeChar) != 0) {
            int pendingAccent = unicodeChar & Integer.MAX_VALUE;
            builder = new StringBuilder();
            builder.appendCodePoint(pendingAccent);
            updateComposingText(builder.toString(), 1, true);
            setCombiningAccent(pendingAccent);
            return true;
        } else if (this.mPendingAccent == 0 || unicodeChar == 0) {
            return false;
        } else {
            int combined = KeyEvent.getDeadChar(this.mPendingAccent, unicodeChar);
            if (combined != 0) {
                builder = new StringBuilder();
                builder.appendCodePoint(combined);
                commitText(builder.toString(), 1);
                return true;
            }
            finishComposingText();
            return false;
        }
    }

    @VisibleForTesting
    public void setCombiningAccent(int pendingAccent) {
        this.mPendingAccent = pendingAccent;
    }

    private void cancelCombiningAccent() {
        this.mPendingAccent = 0;
    }

    public boolean finishComposingText() {
        cancelCombiningAccent();
        ThreadUtils.postOnUiThread(this.mFinishComposingTextRunnable);
        return true;
    }

    public boolean setSelection(final int start, final int end) {
        assertOnImeThread();
        ThreadUtils.postOnUiThread(new Runnable() {
            public void run() {
                ThreadedInputConnection.this.mImeAdapter.setEditableSelectionOffsets(start, end);
            }
        });
        return true;
    }

    public boolean setComposingRegion(final int start, final int end) {
        assertOnImeThread();
        ThreadUtils.postOnUiThread(new Runnable() {
            public void run() {
                ThreadedInputConnection.this.mImeAdapter.setComposingRegion(start, end);
            }
        });
        return true;
    }

    public CharSequence getTextBeforeCursor(int maxChars, int flags) {
        assertOnImeThread();
        TextInputState textInputState = requestAndWaitForTextInputState();
        if (textInputState == null) {
            return null;
        }
        return textInputState.getTextBeforeSelection(maxChars);
    }

    public CharSequence getTextAfterCursor(int maxChars, int flags) {
        assertOnImeThread();
        TextInputState textInputState = requestAndWaitForTextInputState();
        if (textInputState == null) {
            return null;
        }
        return textInputState.getTextAfterSelection(maxChars);
    }

    public CharSequence getSelectedText(int flags) {
        assertOnImeThread();
        TextInputState textInputState = requestAndWaitForTextInputState();
        if (textInputState == null) {
            return null;
        }
        return textInputState.getSelectedText();
    }

    public int getCursorCapsMode(int reqModes) {
        assertOnImeThread();
        return 0;
    }

    public boolean commitCompletion(CompletionInfo text) {
        assertOnImeThread();
        return false;
    }

    public boolean commitCorrection(CorrectionInfo correctionInfo) {
        assertOnImeThread();
        return false;
    }

    public boolean clearMetaKeyStates(int states) {
        assertOnImeThread();
        return false;
    }

    public boolean reportFullscreenMode(boolean enabled) {
        return false;
    }

    public boolean performPrivateCommand(String action, Bundle data) {
        assertOnImeThread();
        return false;
    }

    public boolean requestCursorUpdates(final int cursorUpdateMode) {
        assertOnImeThread();
        ThreadUtils.postOnUiThread(new Runnable() {
            public void run() {
                ThreadedInputConnection.this.mImeAdapter.onRequestCursorUpdates(cursorUpdateMode);
            }
        });
        return true;
    }

    public void closeConnection() {
    }
}
