package org.chromium.content.browser.input;

import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.UnderlineSpan;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import org.chromium.base.Log;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content.browser.RenderCoordinates;
import org.chromium.content.browser.input.ChromiumBaseInputConnection.Factory;
import org.chromium.content.browser.input.CursorAnchorInfoController.ComposingTextDelegate;
import org.chromium.ui.picker.InputDialogContainer;

@JNINamespace("content")
public class ImeAdapter {
    private static final int COMPOSITION_KEY_CODE = 229;
    private static final boolean DEBUG_LOGS = false;
    private static final String TAG = "cr_Ime";
    static KeyCharacterMap sKeyCharacterMap;
    static char[] sSingleCharArray = new char[1];
    private Configuration mCurrentConfig;
    private final CursorAnchorInfoController mCursorAnchorInfoController;
    private ChromiumBaseInputConnection mInputConnection;
    private Factory mInputConnectionFactory;
    private InputMethodManagerWrapper mInputMethodManagerWrapper;
    private int mLastCompositionEnd;
    private int mLastCompositionStart;
    private int mLastSelectionEnd;
    private int mLastSelectionStart;
    private String mLastText;
    private long mNativeImeAdapterAndroid;
    private int mTextInputFlags;
    private int mTextInputType = 0;
    private final ImeAdapterDelegate mViewEmbedder;

    public interface ImeAdapterDelegate {
        View getAttachedView();

        ResultReceiver getNewShowKeyboardReceiver();

        void onImeEvent();

        void onKeyboardBoundsUnchanged();

        boolean performContextMenuAction(int i);
    }

    class C04471 implements ComposingTextDelegate {
        C04471() {
        }

        public CharSequence getText() {
            return ImeAdapter.this.mLastText;
        }

        public int getSelectionStart() {
            return ImeAdapter.this.mLastSelectionStart;
        }

        public int getSelectionEnd() {
            return ImeAdapter.this.mLastSelectionEnd;
        }

        public int getComposingTextStart() {
            return ImeAdapter.this.mLastCompositionStart;
        }

        public int getComposingTextEnd() {
            return ImeAdapter.this.mLastCompositionEnd;
        }
    }

    private static native void nativeAppendBackgroundColorSpan(long j, int i, int i2, int i3);

    private static native void nativeAppendUnderlineSpan(long j, int i, int i2);

    private native void nativeAttachImeAdapter(long j);

    private native void nativeCommitText(long j, String str);

    private native void nativeDeleteSurroundingText(long j, int i, int i2);

    private native void nativeFinishComposingText(long j);

    private native boolean nativeIsImeThreadEnabled(long j);

    private native boolean nativeRequestTextInputStateUpdate(long j);

    private native void nativeResetImeAdapter(long j);

    private native boolean nativeSendKeyEvent(long j, KeyEvent keyEvent, int i, int i2, long j2, int i3, int i4, boolean z, int i5);

    private native boolean nativeSendSyntheticKeyEvent(long j, int i, long j2, int i2, int i3, int i4);

    private native void nativeSetComposingRegion(long j, int i, int i2);

    private native void nativeSetComposingText(long j, CharSequence charSequence, String str, int i);

    private native void nativeSetEditableSelectionOffsets(long j, int i, int i2);

    public ImeAdapter(InputMethodManagerWrapper wrapper, ImeAdapterDelegate embedder) {
        this.mInputMethodManagerWrapper = wrapper;
        this.mViewEmbedder = embedder;
        this.mCurrentConfig = new Configuration(this.mViewEmbedder.getAttachedView().getResources().getConfiguration());
        if (VERSION.SDK_INT >= 21) {
            this.mCursorAnchorInfoController = CursorAnchorInfoController.create(wrapper, new C04471());
        } else {
            this.mCursorAnchorInfoController = null;
        }
    }

    private boolean isImeThreadEnabled() {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        return nativeIsImeThreadEnabled(this.mNativeImeAdapterAndroid);
    }

    private void createInputConnectionFactory() {
        if (this.mInputConnectionFactory == null) {
            if (isImeThreadEnabled()) {
                Log.m29i(TAG, "ImeThread is enabled.", new Object[0]);
                this.mInputConnectionFactory = new ThreadedInputConnectionFactory(this.mInputMethodManagerWrapper);
                return;
            }
            Log.m29i(TAG, "ImeThread is not enabled.", new Object[0]);
            this.mInputConnectionFactory = new Factory();
        }
    }

    public ChromiumBaseInputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.imeOptions = 301989888;
        if (this.mTextInputType == 0) {
            setInputConnection(null);
            return null;
        } else if (this.mInputConnectionFactory == null) {
            return null;
        } else {
            setInputConnection(this.mInputConnectionFactory.initializeAndGet(this.mViewEmbedder.getAttachedView(), this, this.mTextInputType, this.mTextInputFlags, this.mLastSelectionStart, this.mLastSelectionEnd, outAttrs));
            if (this.mCursorAnchorInfoController != null) {
                this.mCursorAnchorInfoController.resetMonitoringState();
            }
            return this.mInputConnection;
        }
    }

    private void setInputConnection(ChromiumBaseInputConnection inputConnection) {
        if (this.mInputConnection != inputConnection) {
            if (this.mInputConnection != null) {
                this.mInputConnection.unblockOnUiThread();
            }
            this.mInputConnection = inputConnection;
        }
    }

    @VisibleForTesting
    public void setInputMethodManagerWrapperForTest(InputMethodManagerWrapper immw) {
        this.mInputMethodManagerWrapper = immw;
        if (this.mCursorAnchorInfoController != null) {
            this.mCursorAnchorInfoController.setInputMethodManagerWrapperForTest(immw);
        }
    }

    @VisibleForTesting
    void setInputConnectionFactory(Factory factory) {
        this.mInputConnectionFactory = factory;
    }

    @VisibleForTesting
    Factory getInputConnectionFactoryForTest() {
        return this.mInputConnectionFactory;
    }

    @VisibleForTesting
    public ChromiumBaseInputConnection getInputConnectionForTest() {
        return this.mInputConnection;
    }

    private static int getModifiers(int metaState) {
        int modifiers = 0;
        if ((metaState & 1) != 0) {
            modifiers = 0 | 1;
        }
        if ((metaState & 2) != 0) {
            modifiers |= 4;
        }
        if ((metaState & 4096) != 0) {
            modifiers |= 2;
        }
        if ((1048576 & metaState) != 0) {
            modifiers |= 512;
        }
        if ((2097152 & metaState) != 0) {
            return modifiers | 1024;
        }
        return modifiers;
    }

    public void updateKeyboardVisibility(int textInputType, int textInputFlags, boolean showIfNeeded) {
        this.mTextInputFlags = textInputFlags;
        if (this.mTextInputType != textInputType) {
            this.mTextInputType = textInputType;
            if (textInputType != 0) {
                restartInput();
            }
        }
        if (textInputType == 0) {
            hideKeyboard();
        } else if (showIfNeeded) {
            showSoftKeyboard();
        }
    }

    public void updateState(String text, int selectionStart, int selectionEnd, int compositionStart, int compositionEnd, boolean isNonImeChange) {
        if (!(this.mCursorAnchorInfoController == null || (TextUtils.equals(this.mLastText, text) && this.mLastSelectionStart == selectionStart && this.mLastSelectionEnd == selectionEnd && this.mLastCompositionStart == compositionStart && this.mLastCompositionEnd == compositionEnd))) {
            this.mCursorAnchorInfoController.invalidateLastCursorAnchorInfo();
        }
        this.mLastText = text;
        this.mLastSelectionStart = selectionStart;
        this.mLastSelectionEnd = selectionEnd;
        this.mLastCompositionStart = compositionStart;
        this.mLastCompositionEnd = compositionEnd;
        if (this.mInputConnection != null) {
            boolean singleLine = (this.mTextInputType == 14 || this.mTextInputType == 15) ? false : true;
            this.mInputConnection.updateStateOnUiThread(text, selectionStart, selectionEnd, compositionStart, compositionEnd, singleLine, isNonImeChange);
        }
    }

    public void attach(long nativeImeAdapter) {
        if (this.mNativeImeAdapterAndroid != nativeImeAdapter) {
            if (this.mNativeImeAdapterAndroid != 0) {
                nativeResetImeAdapter(this.mNativeImeAdapterAndroid);
            }
            if (nativeImeAdapter != 0) {
                nativeAttachImeAdapter(nativeImeAdapter);
            }
            this.mNativeImeAdapterAndroid = nativeImeAdapter;
            if (nativeImeAdapter != 0) {
                createInputConnectionFactory();
            }
        }
    }

    private void showSoftKeyboard() {
        this.mInputMethodManagerWrapper.showSoftInput(this.mViewEmbedder.getAttachedView(), 0, this.mViewEmbedder.getNewShowKeyboardReceiver());
        if (this.mViewEmbedder.getAttachedView().getResources().getConfiguration().keyboard != 1) {
            this.mViewEmbedder.onKeyboardBoundsUnchanged();
        }
    }

    private void hideKeyboard() {
        View view = this.mViewEmbedder.getAttachedView();
        if (this.mInputMethodManagerWrapper.isActive(view)) {
            this.mInputMethodManagerWrapper.hideSoftInputFromWindow(view.getWindowToken(), 0, null);
        }
        if (this.mTextInputType == 0 && this.mInputConnection != null) {
            restartInput();
        }
    }

    public void onKeyboardConfigurationChanged(Configuration newConfig) {
        if (this.mCurrentConfig.keyboard != newConfig.keyboard || this.mCurrentConfig.keyboardHidden != newConfig.keyboardHidden || this.mCurrentConfig.hardKeyboardHidden != newConfig.hardKeyboardHidden) {
            this.mCurrentConfig = new Configuration(newConfig);
            if (this.mTextInputType != 0) {
                restartInput();
                showSoftKeyboard();
            }
        }
    }

    public void onWindowFocusChanged(boolean gainFocus) {
        if (this.mInputConnectionFactory != null) {
            this.mInputConnectionFactory.onWindowFocusChanged(gainFocus);
        }
    }

    public void onViewAttachedToWindow() {
        if (this.mInputConnectionFactory != null) {
            this.mInputConnectionFactory.onViewAttachedToWindow();
        }
    }

    public void onViewDetachedFromWindow() {
        if (this.mInputConnectionFactory != null) {
            this.mInputConnectionFactory.onViewDetachedFromWindow();
        }
    }

    public void onViewFocusChanged(boolean gainFocus) {
        if (!gainFocus) {
            resetAndHideKeyboard();
        }
        if (this.mInputConnectionFactory != null) {
            this.mInputConnectionFactory.onViewFocusChanged(gainFocus);
        }
    }

    public void moveCursorToSelectionEnd() {
        if (this.mInputConnection != null) {
            this.mInputConnection.moveCursorToSelectionEndOnUiThread();
        }
    }

    @VisibleForTesting
    void setInputTypeForTest(int textInputType) {
        this.mTextInputType = textInputType;
    }

    private static boolean isTextInputType(int type) {
        return (type == 0 || InputDialogContainer.isDialogInputType(type)) ? false : true;
    }

    public boolean hasTextInputType() {
        return isTextInputType(this.mTextInputType);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.mInputConnection != null) {
            return this.mInputConnection.sendKeyEventOnUiThread(event);
        }
        return sendKeyEvent(event);
    }

    public void resetAndHideKeyboard() {
        this.mTextInputType = 0;
        this.mTextInputFlags = 0;
        hideKeyboard();
    }

    void updateSelection(int selectionStart, int selectionEnd, int compositionStart, int compositionEnd) {
        this.mInputMethodManagerWrapper.updateSelection(this.mViewEmbedder.getAttachedView(), selectionStart, selectionEnd, compositionStart, compositionEnd);
    }

    void restartInput() {
        this.mInputMethodManagerWrapper.restartInput(this.mViewEmbedder.getAttachedView());
        if (this.mInputConnection != null) {
            this.mInputConnection.onRestartInputOnUiThread();
        }
    }

    boolean performContextMenuAction(int id) {
        return this.mViewEmbedder.performContextMenuAction(id);
    }

    boolean performEditorAction(int actionCode) {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        if (actionCode == 5) {
            sendSyntheticKeyPress(61, 22);
        } else {
            sendSyntheticKeyPress(66, 22);
        }
        return true;
    }

    void notifyUserAction() {
        this.mInputMethodManagerWrapper.notifyUserAction();
    }

    @VisibleForTesting
    protected void sendSyntheticKeyPress(int keyCode, int flags) {
        long eventTime = SystemClock.uptimeMillis();
        sendKeyEvent(new KeyEvent(eventTime, eventTime, 0, keyCode, 0, 0, -1, 0, flags));
        sendKeyEvent(new KeyEvent(SystemClock.uptimeMillis(), eventTime, 1, keyCode, 0, 0, -1, 0, flags));
    }

    boolean sendCompositionToNative(CharSequence text, int newCursorPosition, boolean isCommit, int unicodeFromKeyEvent) {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        if (TextUtils.equals(text, "\n")) {
            sendSyntheticKeyPress(66, 6);
            return true;
        }
        this.mViewEmbedder.onImeEvent();
        long timestampMs = SystemClock.uptimeMillis();
        nativeSendSyntheticKeyEvent(this.mNativeImeAdapterAndroid, 7, timestampMs, COMPOSITION_KEY_CODE, 0, unicodeFromKeyEvent);
        if (isCommit) {
            nativeCommitText(this.mNativeImeAdapterAndroid, text.toString());
        } else {
            nativeSetComposingText(this.mNativeImeAdapterAndroid, text, text.toString(), newCursorPosition);
        }
        nativeSendSyntheticKeyEvent(this.mNativeImeAdapterAndroid, 9, timestampMs, COMPOSITION_KEY_CODE, 0, unicodeFromKeyEvent);
        return true;
    }

    @VisibleForTesting
    boolean finishComposingText() {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        nativeFinishComposingText(this.mNativeImeAdapterAndroid);
        return true;
    }

    boolean sendKeyEvent(KeyEvent event) {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        int action = event.getAction();
        if (action != 0 && action != 1) {
            return false;
        }
        this.mViewEmbedder.onImeEvent();
        return nativeSendKeyEvent(this.mNativeImeAdapterAndroid, event, event.getAction(), getModifiers(event.getMetaState()), event.getEventTime(), event.getKeyCode(), event.getScanCode(), false, event.getUnicodeChar());
    }

    boolean deleteSurroundingText(int beforeLength, int afterLength) {
        this.mViewEmbedder.onImeEvent();
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        nativeSendSyntheticKeyEvent(this.mNativeImeAdapterAndroid, 7, SystemClock.uptimeMillis(), COMPOSITION_KEY_CODE, 0, 0);
        nativeDeleteSurroundingText(this.mNativeImeAdapterAndroid, beforeLength, afterLength);
        nativeSendSyntheticKeyEvent(this.mNativeImeAdapterAndroid, 9, SystemClock.uptimeMillis(), COMPOSITION_KEY_CODE, 0, 0);
        return true;
    }

    boolean setEditableSelectionOffsets(int start, int end) {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        nativeSetEditableSelectionOffsets(this.mNativeImeAdapterAndroid, start, end);
        return true;
    }

    boolean setComposingRegion(int start, int end) {
        if (this.mNativeImeAdapterAndroid == 0) {
            return false;
        }
        nativeSetComposingRegion(this.mNativeImeAdapterAndroid, start, end);
        return true;
    }

    @CalledByNative
    private void focusedNodeChanged(boolean isEditable) {
        if (this.mCursorAnchorInfoController != null) {
            this.mCursorAnchorInfoController.focusedNodeChanged(isEditable);
        }
        if (this.mTextInputType != 0 && this.mInputConnection != null && isEditable) {
            restartInput();
        }
    }

    boolean requestTextInputStateUpdate() {
        if (this.mNativeImeAdapterAndroid == 0 || this.mInputConnection == null) {
            return false;
        }
        return nativeRequestTextInputStateUpdate(this.mNativeImeAdapterAndroid);
    }

    public boolean onRequestCursorUpdates(int cursorUpdateMode) {
        if (this.mCursorAnchorInfoController == null) {
            return false;
        }
        return this.mCursorAnchorInfoController.onRequestCursorUpdates(cursorUpdateMode, this.mViewEmbedder.getAttachedView());
    }

    public void onUpdateFrameInfo(RenderCoordinates renderCoordinates, boolean hasInsertionMarker, boolean isInsertionMarkerVisible, float insertionMarkerHorizontal, float insertionMarkerTop, float insertionMarkerBottom) {
        if (this.mCursorAnchorInfoController != null) {
            this.mCursorAnchorInfoController.onUpdateFrameInfo(renderCoordinates, hasInsertionMarker, isInsertionMarkerVisible, insertionMarkerHorizontal, insertionMarkerTop, insertionMarkerBottom, this.mViewEmbedder.getAttachedView());
        }
    }

    @CalledByNative
    private void populateUnderlinesFromSpans(CharSequence text, long underlines) {
        if (text instanceof SpannableString) {
            SpannableString spannableString = (SpannableString) text;
            for (CharacterStyle span : (CharacterStyle[]) spannableString.getSpans(0, text.length(), CharacterStyle.class)) {
                if (span instanceof BackgroundColorSpan) {
                    nativeAppendBackgroundColorSpan(underlines, spannableString.getSpanStart(span), spannableString.getSpanEnd(span), ((BackgroundColorSpan) span).getBackgroundColor());
                } else if (span instanceof UnderlineSpan) {
                    nativeAppendUnderlineSpan(underlines, spannableString.getSpanStart(span), spannableString.getSpanEnd(span));
                }
            }
        }
    }

    @CalledByNative
    private void cancelComposition() {
        if (this.mInputConnection != null) {
            restartInput();
        }
    }

    @CalledByNative
    private void setCharacterBounds(float[] characterBounds) {
        if (this.mCursorAnchorInfoController != null) {
            this.mCursorAnchorInfoController.setCompositionCharacterBounds(characterBounds);
        }
    }

    @CalledByNative
    private void detach() {
        this.mNativeImeAdapterAndroid = 0;
        if (this.mCursorAnchorInfoController != null) {
            this.mCursorAnchorInfoController.focusedNodeChanged(false);
        }
    }
}
