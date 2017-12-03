package org.xwalk.core.internal;

import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.ui.autofill.AutofillDelegate;
import org.chromium.ui.autofill.AutofillPopup;
import org.chromium.ui.autofill.AutofillSuggestion;

@JNINamespace("xwalk")
public class XWalkAutofillClientAndroid {
    private AutofillPopup mAutofillPopup;
    private ContentViewCore mContentViewCore;
    private final long mNativeXWalkAutofillClientAndroid;

    class C04731 implements AutofillDelegate {
        C04731() {
        }

        public void dismissed() {
        }

        public void suggestionSelected(int listIndex) {
            XWalkAutofillClientAndroid.this.nativeSuggestionSelected(XWalkAutofillClientAndroid.this.mNativeXWalkAutofillClientAndroid, listIndex);
        }

        public void deleteSuggestion(int listIndex) {
        }
    }

    private native void nativeSuggestionSelected(long j, int i);

    @CalledByNative
    public static XWalkAutofillClientAndroid create(long nativeClient) {
        return new XWalkAutofillClientAndroid(nativeClient);
    }

    private XWalkAutofillClientAndroid(long nativeXWalkAutofillClient) {
        this.mNativeXWalkAutofillClientAndroid = nativeXWalkAutofillClient;
    }

    public void init(ContentViewCore contentViewCore) {
        this.mContentViewCore = contentViewCore;
    }

    @CalledByNative
    private void showAutofillPopup(float x, float y, float width, float height, boolean isRtl, AutofillSuggestion[] suggestions) {
        if (this.mContentViewCore != null) {
            if (this.mAutofillPopup == null) {
                this.mAutofillPopup = new AutofillPopup(this.mContentViewCore.getContext(), this.mContentViewCore.getViewAndroidDelegate(), new C04731());
            }
            this.mAutofillPopup.setAnchorRect(x, y, width, height);
            this.mAutofillPopup.filterAndShow(suggestions, isRtl);
        }
    }

    @CalledByNative
    public void hideAutofillPopup() {
        if (this.mAutofillPopup != null) {
            this.mAutofillPopup.dismiss();
            this.mAutofillPopup = null;
        }
    }

    @CalledByNative
    private static AutofillSuggestion[] createAutofillSuggestionArray(int size) {
        return new AutofillSuggestion[size];
    }

    @CalledByNative
    private static void addToAutofillSuggestionArray(AutofillSuggestion[] array, int index, String name, String label, int uniqueId) {
        array[index] = new AutofillSuggestion(name, label, 0, uniqueId, false, false);
    }
}
