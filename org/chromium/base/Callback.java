package org.chromium.base;

import org.chromium.base.annotations.CalledByNative;

public abstract class Callback<T> {
    public abstract void onResult(T t);

    @CalledByNative
    private void onResultFromNative(Object result) {
        onResult(result);
    }

    @CalledByNative
    private void onResultFromNative(boolean result) {
        onResult(Boolean.valueOf(result));
    }

    @CalledByNative
    private void onResultFromNative(int result) {
        onResult(Integer.valueOf(result));
    }
}
