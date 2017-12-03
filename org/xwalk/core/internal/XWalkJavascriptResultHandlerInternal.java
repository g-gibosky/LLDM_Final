package org.xwalk.core.internal;

import org.chromium.base.ThreadUtils;

@XWalkAPI(createInternally = true, impl = XWalkJavascriptResultInternal.class)
public class XWalkJavascriptResultHandlerInternal implements XWalkJavascriptResultInternal {
    private XWalkContentsClientBridge mBridge;
    private final int mId;

    class C03462 implements Runnable {
        C03462() {
        }

        public void run() {
            if (XWalkJavascriptResultHandlerInternal.this.mBridge != null) {
                XWalkJavascriptResultHandlerInternal.this.mBridge.cancelJsResult(XWalkJavascriptResultHandlerInternal.this.mId);
            }
            XWalkJavascriptResultHandlerInternal.this.mBridge = null;
        }
    }

    XWalkJavascriptResultHandlerInternal(XWalkContentsClientBridge bridge, int id) {
        this.mBridge = bridge;
        this.mId = id;
    }

    XWalkJavascriptResultHandlerInternal() {
        this.mBridge = null;
        this.mId = -1;
    }

    @XWalkAPI
    public void confirm() {
        confirmWithResult(null);
    }

    @XWalkAPI
    public void confirmWithResult(final String promptResult) {
        ThreadUtils.runOnUiThread(new Runnable() {
            public void run() {
                if (XWalkJavascriptResultHandlerInternal.this.mBridge != null) {
                    XWalkJavascriptResultHandlerInternal.this.mBridge.confirmJsResult(XWalkJavascriptResultHandlerInternal.this.mId, promptResult);
                }
                XWalkJavascriptResultHandlerInternal.this.mBridge = null;
            }
        });
    }

    @XWalkAPI
    public void cancel() {
        ThreadUtils.runOnUiThread(new C03462());
    }
}
