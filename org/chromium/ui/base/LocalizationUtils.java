package org.chromium.ui.base;

import java.util.Locale;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.ContextUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("l10n_util")
public class LocalizationUtils {
    static final /* synthetic */ boolean $assertionsDisabled = (!LocalizationUtils.class.desiredAssertionStatus());
    public static final int LEFT_TO_RIGHT = 2;
    public static final int RIGHT_TO_LEFT = 1;
    public static final int UNKNOWN_DIRECTION = 0;
    private static Boolean sIsLayoutRtl;

    private static native int nativeGetFirstStrongCharacterDirection(String str);

    private LocalizationUtils() {
    }

    @CalledByNative
    private static Locale getJavaLocale(String language, String country, String variant) {
        return new Locale(language, country, variant);
    }

    @CalledByNative
    private static String getDisplayNameForLocale(Locale locale, Locale displayLocale) {
        return locale.getDisplayName(displayLocale);
    }

    @CalledByNative
    public static boolean isLayoutRtl() {
        boolean z = true;
        if (sIsLayoutRtl == null) {
            if (ApiCompatibilityUtils.getLayoutDirection(ContextUtils.getApplicationContext().getResources().getConfiguration()) != 1) {
                z = false;
            }
            sIsLayoutRtl = Boolean.valueOf(z);
        }
        return sIsLayoutRtl.booleanValue();
    }

    @VisibleForTesting
    public static void setRtlForTesting(boolean shouldBeRtl) {
        sIsLayoutRtl = Boolean.valueOf(shouldBeRtl);
    }

    public static int getFirstStrongCharacterDirection(String string) {
        if ($assertionsDisabled || string != null) {
            return nativeGetFirstStrongCharacterDirection(string);
        }
        throw new AssertionError();
    }
}
