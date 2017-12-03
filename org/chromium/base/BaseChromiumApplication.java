package org.chromium.base;

import android.app.Application;
import android.content.Context;
import org.chromium.base.multidex.ChromiumMultiDexInstaller;

public class BaseChromiumApplication extends Application {
    private static final String TAG = "cr.base";
    private final boolean mShouldInitializeApplicationStatusTracking;

    public BaseChromiumApplication() {
        this(true);
    }

    protected BaseChromiumApplication(boolean shouldInitializeApplicationStatusTracking) {
        this.mShouldInitializeApplicationStatusTracking = shouldInitializeApplicationStatusTracking;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ChromiumMultiDexInstaller.install(this);
    }

    public void onCreate() {
        super.onCreate();
        ApplicationStatusManager.init(this);
    }

    public void initCommandLine() {
    }

    @VisibleForTesting
    public static void initCommandLine(Context context) {
        ((BaseChromiumApplication) context.getApplicationContext()).initCommandLine();
    }
}
