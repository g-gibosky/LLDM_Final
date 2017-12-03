package org.xwalk.core.internal;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;

class MixedContext extends ContextWrapper {
    private Context mActivityCtx;

    public MixedContext(Context base, Context activity) {
        super(base);
        this.mActivityCtx = activity;
    }

    public Context getApplicationContext() {
        return this.mActivityCtx.getApplicationContext();
    }

    public boolean bindService(Intent in, ServiceConnection conn, int flags) {
        return getApplicationContext().bindService(in, conn, flags);
    }

    public void unbindService(ServiceConnection conn) {
        getApplicationContext().unbindService(conn);
    }

    public Object getSystemService(String name) {
        if (name.equals("layout_inflater")) {
            return super.getSystemService(name);
        }
        return this.mActivityCtx.getSystemService(name);
    }
}
