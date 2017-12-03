package org.crosswalk.engine;

import org.apache.cordova.ICordovaHttpAuthHandler;
import org.xwalk.core.XWalkHttpAuthHandler;

public class XWalkCordovaHttpAuthHandler implements ICordovaHttpAuthHandler {
    private final XWalkHttpAuthHandler handler;

    public XWalkCordovaHttpAuthHandler(XWalkHttpAuthHandler handler) {
        this.handler = handler;
    }

    public void cancel() {
        this.handler.cancel();
    }

    public void proceed(String username, String password) {
        this.handler.proceed(username, password);
    }
}
