package org.chromium.base.metrics;

import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("base::android")
public class RecordUserAction {
    private static boolean sIsDisabledForTests = false;

    private static native void nativeRecordUserAction(String str);

    @VisibleForTesting
    public static void disableForTests() {
        sIsDisabledForTests = true;
    }

    public static void record(final String action) {
        if (!sIsDisabledForTests) {
            if (ThreadUtils.runningOnUiThread()) {
                nativeRecordUserAction(action);
            } else {
                ThreadUtils.runOnUiThread(new Runnable() {
                    public void run() {
                        RecordUserAction.nativeRecordUserAction(action);
                    }
                });
            }
        }
    }
}
