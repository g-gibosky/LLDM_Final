package org.chromium.content.app;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.RemoteException;
import org.chromium.base.Log;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content.C0174R;
import org.chromium.content.browser.ChildProcessConstants;
import org.chromium.content.common.IChildProcessCallback;
import org.chromium.content.common.IChildProcessCallback.Stub;

@JNINamespace("content")
public class DownloadProcessService extends ChildProcessService {
    static final /* synthetic */ boolean $assertionsDisabled = (!DownloadProcessService.class.desiredAssertionStatus());
    private static final String TAG = "DownloadProcess";
    private IChildProcessCallback mCallback;
    private long mClientContext;
    private int mDownloadCount;

    public void onCreate() {
        super.onCreate();
        startForeground(C0174R.id.download_service_notification, new Notification());
    }

    @SuppressLint({"NewApi"})
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ($assertionsDisabled || VERSION.SDK_INT >= 18) {
            initializeParams(intent);
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                this.mCallback = Stub.asInterface(bundle.getBinder(ChildProcessConstants.EXTRA_CHILD_PROCESS_CALLBACK));
                getServiceInfo(bundle);
            }
            return 1;
        }
        throw new AssertionError();
    }

    private void onDownloadStarted(boolean started, int downloadId) {
        if (this.mCallback != null) {
            try {
                this.mCallback.onDownloadStarted(started, downloadId);
            } catch (RemoteException e) {
                Log.m28e(TAG, "Unable to callback the browser process.", e);
            }
        }
        if (started) {
            this.mDownloadCount++;
        }
    }

    private void onDownloadCompleted(boolean success) {
        this.mDownloadCount--;
        if (this.mDownloadCount == 0) {
            stopSelf();
        }
    }
}
