package org.chromium.content.browser.input;

import android.annotation.TargetApi;
import android.graphics.Matrix;
import android.view.View;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.CursorAnchorInfo.Builder;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.SuppressFBWarnings;
import org.chromium.content.browser.RenderCoordinates;

@TargetApi(21)
final class CursorAnchorInfoController {
    @Nullable
    private final ComposingTextDelegate mComposingTextDelegate;
    @Nullable
    private float[] mCompositionCharacterBounds;
    @Nonnull
    private final Builder mCursorAnchorInfoBuilder = new Builder();
    private boolean mHasCoordinateInfo;
    private boolean mHasInsertionMarker;
    private boolean mHasPendingImmediateRequest;
    @Nullable
    private InputMethodManagerWrapper mInputMethodManagerWrapper;
    private float mInsertionMarkerBottom;
    private float mInsertionMarkerHorizontal;
    private float mInsertionMarkerTop;
    private boolean mIsEditable;
    private boolean mIsInsertionMarkerVisible;
    @Nullable
    private CursorAnchorInfo mLastCursorAnchorInfo;
    @Nonnull
    private final Matrix mMatrix = new Matrix();
    private boolean mMonitorModeEnabled;
    private float mScale;
    private float mTranslationX;
    private float mTranslationY;
    @Nonnull
    private final ViewDelegate mViewDelegate;
    @Nonnull
    private final int[] mViewOrigin = new int[2];

    public interface ComposingTextDelegate {
        int getComposingTextEnd();

        int getComposingTextStart();

        int getSelectionEnd();

        int getSelectionStart();

        CharSequence getText();
    }

    public interface ViewDelegate {
        void getLocationOnScreen(View view, int[] iArr);
    }

    static class C04441 implements ViewDelegate {
        C04441() {
        }

        public void getLocationOnScreen(View view, int[] location) {
            view.getLocationOnScreen(location);
        }
    }

    private CursorAnchorInfoController(InputMethodManagerWrapper inputMethodManagerWrapper, ComposingTextDelegate composingTextDelegate, ViewDelegate viewDelegate) {
        this.mInputMethodManagerWrapper = inputMethodManagerWrapper;
        this.mComposingTextDelegate = composingTextDelegate;
        this.mViewDelegate = viewDelegate;
    }

    public static CursorAnchorInfoController create(InputMethodManagerWrapper inputMethodManagerWrapper, ComposingTextDelegate composingTextDelegate) {
        return new CursorAnchorInfoController(inputMethodManagerWrapper, composingTextDelegate, new C04441());
    }

    @VisibleForTesting
    public void setInputMethodManagerWrapperForTest(InputMethodManagerWrapper inputMethodManagerWrapper) {
        this.mInputMethodManagerWrapper = inputMethodManagerWrapper;
    }

    @VisibleForTesting
    public static CursorAnchorInfoController createForTest(InputMethodManagerWrapper inputMethodManagerWrapper, ComposingTextDelegate composingTextDelegate, ViewDelegate viewDelegate) {
        return new CursorAnchorInfoController(inputMethodManagerWrapper, composingTextDelegate, viewDelegate);
    }

    public void invalidateLastCursorAnchorInfo() {
        if (this.mIsEditable) {
            this.mLastCursorAnchorInfo = null;
        }
    }

    public void setCompositionCharacterBounds(float[] compositionCharacterBounds) {
        if (this.mIsEditable && !Arrays.equals(compositionCharacterBounds, this.mCompositionCharacterBounds)) {
            this.mLastCursorAnchorInfo = null;
            this.mCompositionCharacterBounds = compositionCharacterBounds;
        }
    }

    @SuppressFBWarnings({"FE_FLOATING_POINT_EQUALITY"})
    public void onUpdateFrameInfo(@Nonnull RenderCoordinates renderCoordinates, boolean hasInsertionMarker, boolean isInsertionMarkerVisible, float insertionMarkerHorizontal, float insertionMarkerTop, float insertionMarkerBottom, @Nonnull View view) {
        if (this.mIsEditable) {
            this.mViewDelegate.getLocationOnScreen(view, this.mViewOrigin);
            float scale = renderCoordinates.getDeviceScaleFactor();
            float translationX = (float) this.mViewOrigin[0];
            float translationY = ((float) this.mViewOrigin[1]) + renderCoordinates.getContentOffsetYPix();
            if (!(this.mHasCoordinateInfo && scale == this.mScale && translationX == this.mTranslationX && translationY == this.mTranslationY && hasInsertionMarker == this.mHasInsertionMarker && isInsertionMarkerVisible == this.mIsInsertionMarkerVisible && insertionMarkerHorizontal == this.mInsertionMarkerHorizontal && insertionMarkerTop == this.mInsertionMarkerTop && insertionMarkerBottom == this.mInsertionMarkerBottom)) {
                this.mLastCursorAnchorInfo = null;
                this.mHasCoordinateInfo = true;
                this.mScale = scale;
                this.mTranslationX = translationX;
                this.mTranslationY = translationY;
                this.mHasInsertionMarker = hasInsertionMarker;
                this.mIsInsertionMarkerVisible = isInsertionMarkerVisible;
                this.mInsertionMarkerHorizontal = insertionMarkerHorizontal;
                this.mInsertionMarkerTop = insertionMarkerTop;
                this.mInsertionMarkerBottom = insertionMarkerBottom;
            }
            if (this.mHasPendingImmediateRequest || (this.mMonitorModeEnabled && this.mLastCursorAnchorInfo == null)) {
                updateCursorAnchorInfo(view);
            }
        }
    }

    public void resetMonitoringState() {
        this.mMonitorModeEnabled = false;
        this.mHasPendingImmediateRequest = false;
    }

    public void focusedNodeChanged(boolean isEditable) {
        this.mIsEditable = isEditable;
        this.mCompositionCharacterBounds = null;
        this.mHasCoordinateInfo = false;
        this.mLastCursorAnchorInfo = null;
    }

    public boolean onRequestCursorUpdates(int cursorUpdateMode, View view) {
        boolean z = false;
        if (!this.mIsEditable) {
            return false;
        }
        if ((cursorUpdateMode & 2) != 0) {
            z = true;
        }
        this.mMonitorModeEnabled = z;
        if ((cursorUpdateMode & 1) != 0) {
            this.mHasPendingImmediateRequest = true;
            updateCursorAnchorInfo(view);
        }
        return true;
    }

    private void updateCursorAnchorInfo(View view) {
        if (this.mHasCoordinateInfo) {
            if (this.mLastCursorAnchorInfo == null) {
                this.mCursorAnchorInfoBuilder.reset();
                CharSequence text = this.mComposingTextDelegate.getText();
                int selectionStart = this.mComposingTextDelegate.getSelectionStart();
                int selectionEnd = this.mComposingTextDelegate.getSelectionEnd();
                int composingTextStart = this.mComposingTextDelegate.getComposingTextStart();
                int composingTextEnd = this.mComposingTextDelegate.getComposingTextEnd();
                if (text != null && composingTextStart >= 0 && composingTextEnd <= text.length()) {
                    this.mCursorAnchorInfoBuilder.setComposingText(composingTextStart, text.subSequence(composingTextStart, composingTextEnd));
                    float[] compositionCharacterBounds = this.mCompositionCharacterBounds;
                    if (compositionCharacterBounds != null) {
                        int numCharacter = compositionCharacterBounds.length / 4;
                        for (int i = 0; i < numCharacter; i++) {
                            this.mCursorAnchorInfoBuilder.addCharacterBounds(composingTextStart + i, compositionCharacterBounds[i * 4], compositionCharacterBounds[(i * 4) + 1], compositionCharacterBounds[(i * 4) + 2], compositionCharacterBounds[(i * 4) + 3], 1);
                        }
                    }
                }
                this.mCursorAnchorInfoBuilder.setSelectionRange(selectionStart, selectionEnd);
                this.mMatrix.setScale(this.mScale, this.mScale);
                this.mMatrix.postTranslate(this.mTranslationX, this.mTranslationY);
                this.mCursorAnchorInfoBuilder.setMatrix(this.mMatrix);
                if (this.mHasInsertionMarker) {
                    this.mCursorAnchorInfoBuilder.setInsertionMarkerLocation(this.mInsertionMarkerHorizontal, this.mInsertionMarkerTop, this.mInsertionMarkerBottom, this.mInsertionMarkerBottom, this.mIsInsertionMarkerVisible ? 1 : 2);
                }
                this.mLastCursorAnchorInfo = this.mCursorAnchorInfoBuilder.build();
            }
            if (this.mInputMethodManagerWrapper != null) {
                this.mInputMethodManagerWrapper.updateCursorAnchorInfo(view, this.mLastCursorAnchorInfo);
            }
            this.mHasPendingImmediateRequest = false;
        }
    }
}
