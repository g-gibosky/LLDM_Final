package org.xwalk.core.internal;

import org.chromium.base.annotations.JNINamespace;

@JNINamespace("xwalk")
public class XWalkFormDatabase {
    private static native void nativeClearFormData();

    private static native boolean nativeHasFormData();

    public static boolean hasFormData() {
        return nativeHasFormData();
    }

    public static void clearFormData() {
        nativeClearFormData();
    }
}
