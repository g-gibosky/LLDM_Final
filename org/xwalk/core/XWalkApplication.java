package org.xwalk.core;

import android.app.Application;
import android.content.res.Resources;

public class XWalkApplication extends Application {
    private static XWalkApplication gApp = null;
    private Resources mRes = null;

    public void onCreate() {
        super.onCreate();
        gApp = this;
    }

    public Resources getResources() {
        return this.mRes == null ? super.getResources() : this.mRes;
    }

    void addResource(Resources res) {
        if (this.mRes == null) {
            this.mRes = new XWalkMixedResources(super.getResources(), res);
        }
    }

    static XWalkApplication getApplication() {
        return gApp;
    }
}
