package org.chromium.content.browser.input;

import android.app.Activity;
import android.content.Context;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.ui.base.WindowAndroid;
import org.chromium.ui.picker.DateTimeSuggestion;
import org.chromium.ui.picker.InputDialogContainer;
import org.chromium.ui.picker.InputDialogContainer.InputActionDelegate;

@JNINamespace("content")
class DateTimeChooserAndroid {
    private final InputDialogContainer mInputDialogContainer;
    private final long mNativeDateTimeChooserAndroid;

    class C04451 implements InputActionDelegate {
        C04451() {
        }

        public void replaceDateTime(double value) {
            DateTimeChooserAndroid.this.nativeReplaceDateTime(DateTimeChooserAndroid.this.mNativeDateTimeChooserAndroid, value);
        }

        public void cancelDateTimeDialog() {
            DateTimeChooserAndroid.this.nativeCancelDialog(DateTimeChooserAndroid.this.mNativeDateTimeChooserAndroid);
        }
    }

    private native void nativeCancelDialog(long j);

    private native void nativeReplaceDateTime(long j, double d);

    private DateTimeChooserAndroid(Context context, long nativeDateTimeChooserAndroid) {
        this.mNativeDateTimeChooserAndroid = nativeDateTimeChooserAndroid;
        this.mInputDialogContainer = new InputDialogContainer(context, new C04451());
    }

    private void showDialog(int dialogType, double dialogValue, double min, double max, double step, DateTimeSuggestion[] suggestions) {
        this.mInputDialogContainer.showDialog(dialogType, dialogValue, min, max, step, suggestions);
    }

    @CalledByNative
    private static DateTimeChooserAndroid createDateTimeChooser(WindowAndroid windowAndroid, long nativeDateTimeChooserAndroid, int dialogType, double dialogValue, double min, double max, double step, DateTimeSuggestion[] suggestions) {
        Activity windowAndroidActivity = (Activity) windowAndroid.getActivity().get();
        if (windowAndroidActivity == null) {
            return null;
        }
        DateTimeChooserAndroid chooser = new DateTimeChooserAndroid(windowAndroidActivity, nativeDateTimeChooserAndroid);
        chooser.showDialog(dialogType, dialogValue, min, max, step, suggestions);
        return chooser;
    }

    @CalledByNative
    private static DateTimeSuggestion[] createSuggestionsArray(int size) {
        return new DateTimeSuggestion[size];
    }

    @CalledByNative
    private static void setDateTimeSuggestionAt(DateTimeSuggestion[] array, int index, double value, String localizedValue, String label) {
        array[index] = new DateTimeSuggestion(value, localizedValue, label);
    }
}
