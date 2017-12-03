package org.xwalk.core.internal.extensions;

import org.chromium.base.annotations.JNINamespace;

@JNINamespace("xwalk::extensions")
public abstract class XWalkNativeExtensionLoaderAndroid {
    private static native void nativeRegisterExtensionInPath(String str);

    public void registerNativeExtensionsInPath(String path) {
        nativeRegisterExtensionInPath(path);
    }
}
