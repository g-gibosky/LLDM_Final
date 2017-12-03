package org.chromium.content.app;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("content")
public class ChildProcessService extends Service {
    private final ChildProcessServiceImpl mChildProcessServiceImpl = new ChildProcessServiceImpl();

    public void onCreate() {
        super.onCreate();
        this.mChildProcessServiceImpl.create(getApplicationContext(), getApplicationContext());
    }

    public void onDestroy() {
        super.onDestroy();
        this.mChildProcessServiceImpl.destroy();
    }

    public IBinder onBind(Intent intent) {
        stopSelf();
        return this.mChildProcessServiceImpl.bind(intent);
    }

    protected void initializeParams(Intent intent) {
        this.mChildProcessServiceImpl.initializeParams(intent);
    }

    protected void getServiceInfo(Bundle bundle) {
        this.mChildProcessServiceImpl.getServiceInfo(bundle);
    }
}
