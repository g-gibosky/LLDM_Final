package org.xwalk.core.internal;

import android.content.Intent;
import android.graphics.Bitmap;

interface XWalkNotificationService {
    void cancelNotification(int i);

    boolean maybeHandleIntent(Intent intent);

    void setBridge(XWalkContentsClientBridge xWalkContentsClientBridge);

    void showNotification(String str, String str2, String str3, Bitmap bitmap, int i);

    void shutdown();
}
