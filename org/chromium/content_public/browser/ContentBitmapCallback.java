package org.chromium.content_public.browser;

import android.graphics.Bitmap;

public interface ContentBitmapCallback {
    void onFinishGetBitmap(Bitmap bitmap, int i);
}
