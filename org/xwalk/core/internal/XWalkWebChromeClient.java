package org.xwalk.core.internal;

import android.graphics.Bitmap;
import android.os.Message;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebStorage.QuotaUpdater;
import org.xwalk.core.internal.XWalkGeolocationPermissions.Callback;

public class XWalkWebChromeClient {
    private long XWALK_MAX_QUOTA = 104857600;

    public void onReceivedIcon(XWalkViewInternal view, Bitmap icon) {
    }

    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, QuotaUpdater quotaUpdater) {
        quotaUpdater.updateQuota(this.XWALK_MAX_QUOTA);
    }

    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, QuotaUpdater quotaUpdater) {
        quotaUpdater.updateQuota(this.XWALK_MAX_QUOTA);
    }

    public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
        callback.invoke(origin, true, false);
    }

    public void onGeolocationPermissionsHidePrompt() {
    }

    public boolean onJsTimeout() {
        return true;
    }

    @Deprecated
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        onConsoleMessage(consoleMessage.message(), consoleMessage.lineNumber(), consoleMessage.sourceId());
        return false;
    }

    public void getVisitedHistory(ValueCallback<String[]> valueCallback) {
    }

    public void setInstallableWebApp() {
    }

    public void setupAutoFill(Message msg) {
    }
}
