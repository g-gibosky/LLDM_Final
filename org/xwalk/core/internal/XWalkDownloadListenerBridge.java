package org.xwalk.core.internal;

import android.content.Context;

public class XWalkDownloadListenerBridge extends XWalkDownloadListenerInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod onDownloadStartStringStringStringStringlongMethod = new ReflectMethod(null, "onDownloadStart", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkDownloadListenerBridge(Context context, Object wrapper) {
        super(context);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        this.onDownloadStartStringStringStringStringlongMethod.invoke(url, userAgent, contentDisposition, mimetype, Long.valueOf(contentLength));
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.onDownloadStartStringStringStringStringlongMethod.init(this.wrapper, null, "onDownloadStart", String.class, String.class, String.class, String.class, Long.TYPE);
        }
    }
}
