package org.xwalk.core.internal;

import android.graphics.Bitmap;

@XWalkAPI(createExternally = true)
public abstract class XWalkGetBitmapCallbackInternal {
    @XWalkAPI
    public abstract void onFinishGetBitmap(Bitmap bitmap, int i);
}
