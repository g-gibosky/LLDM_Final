package org.chromium.ui.resources;

import android.graphics.Bitmap;
import android.graphics.Rect;

public interface Resource {
    Rect getAperture();

    Bitmap getBitmap();

    Rect getBitmapSize();

    Rect getPadding();
}
