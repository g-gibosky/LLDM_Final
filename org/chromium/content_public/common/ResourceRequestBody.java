package org.chromium.content_public.common;

import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("content")
public final class ResourceRequestBody {
    private byte[] mEncodedNativeForm;

    private static native byte[] nativeCreateResourceRequestBodyFromBytes(byte[] bArr);

    private ResourceRequestBody(byte[] encodedNativeForm) {
        this.mEncodedNativeForm = encodedNativeForm;
    }

    @CalledByNative
    private static ResourceRequestBody createFromEncodedNativeForm(byte[] encodedNativeForm) {
        return new ResourceRequestBody(encodedNativeForm);
    }

    @CalledByNative
    private byte[] getEncodedNativeForm() {
        return this.mEncodedNativeForm;
    }

    public static ResourceRequestBody createFromBytes(byte[] httpBody) {
        return createFromEncodedNativeForm(nativeCreateResourceRequestBodyFromBytes(httpBody));
    }
}
