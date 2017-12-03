package org.chromium.content_public.browser;

import android.graphics.Bitmap;
import android.graphics.Rect;
import java.util.List;

public interface ImageDownloadCallback {
    void onFinishDownloadImage(int i, int i2, String str, List<Bitmap> list, List<Rect> list2);
}
