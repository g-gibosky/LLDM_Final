package org.chromium.components.web_contents_delegate_android;

import android.content.Context;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.ui.ColorPickerDialog;
import org.chromium.ui.ColorSuggestion;
import org.chromium.ui.OnColorChangedListener;
import org.chromium.ui.base.WindowAndroid;

@JNINamespace("web_contents_delegate_android")
public class ColorChooserAndroid {
    private final ColorPickerDialog mDialog;
    private final long mNativeColorChooserAndroid;

    class C04321 implements OnColorChangedListener {
        C04321() {
        }

        public void onColorChanged(int color) {
            ColorChooserAndroid.this.mDialog.dismiss();
            ColorChooserAndroid.this.nativeOnColorChosen(ColorChooserAndroid.this.mNativeColorChooserAndroid, color);
        }
    }

    private native void nativeOnColorChosen(long j, int i);

    private ColorChooserAndroid(long nativeColorChooserAndroid, Context context, int initialColor, ColorSuggestion[] suggestions) {
        OnColorChangedListener listener = new C04321();
        this.mNativeColorChooserAndroid = nativeColorChooserAndroid;
        this.mDialog = new ColorPickerDialog(context, listener, initialColor, suggestions);
    }

    private void openColorChooser() {
        this.mDialog.show();
    }

    @CalledByNative
    public void closeColorChooser() {
        this.mDialog.dismiss();
    }

    @CalledByNative
    public static ColorChooserAndroid createColorChooserAndroid(long nativeColorChooserAndroid, ContentViewCore contentViewCore, int initialColor, ColorSuggestion[] suggestions) {
        if (contentViewCore.getWindowAndroid() == null) {
            return null;
        }
        Context windowContext = (Context) contentViewCore.getWindowAndroid().getContext().get();
        if (WindowAndroid.activityFromContext(windowContext) == null) {
            return null;
        }
        ColorChooserAndroid chooser = new ColorChooserAndroid(nativeColorChooserAndroid, windowContext, initialColor, suggestions);
        chooser.openColorChooser();
        return chooser;
    }

    @CalledByNative
    private static ColorSuggestion[] createColorSuggestionArray(int size) {
        return new ColorSuggestion[size];
    }

    @CalledByNative
    private static void addToColorSuggestionArray(ColorSuggestion[] array, int index, int color, String label) {
        array[index] = new ColorSuggestion(color, label);
    }
}
