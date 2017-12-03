package org.xwalk.core.internal;

import android.graphics.Bitmap;

public class XWalkGetBitmapCallbackBridge extends XWalkGetBitmapCallbackInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod onFinishGetBitmapBitmapintMethod = new ReflectMethod(null, "onFinishGetBitmap", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkGetBitmapCallbackBridge(Object wrapper) {
        this.wrapper = wrapper;
        reflectionInit();
    }

    public void onFinishGetBitmap(Bitmap bitmap, int response) {
        this.onFinishGetBitmapBitmapintMethod.invoke(bitmap, Integer.valueOf(response));
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.onFinishGetBitmapBitmapintMethod.init(this.wrapper, null, "onFinishGetBitmap", Bitmap.class, Integer.TYPE);
        }
    }
}
