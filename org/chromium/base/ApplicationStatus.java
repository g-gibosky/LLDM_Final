package org.chromium.base;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.chromium.base.ApplicationStatusManager.WindowFocusChangedListener;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.MainDex;

@MainDex
@JNINamespace("base::android")
public class ApplicationStatus {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static Activity sActivity;
    private static final Map<Activity, ActivityInfo> sActivityInfo = new ConcurrentHashMap();
    private static final ObserverList<ApplicationStateListener> sApplicationStateListeners = new ObserverList();
    private static Integer sCachedApplicationState;
    private static Object sCachedApplicationStateLock = new Object();
    private static final ObserverList<ActivityStateListener> sGeneralActivityStateListeners = new ObserverList();
    private static ApplicationStateListener sNativeApplicationStateListener;

    static class C01562 implements ActivityLifecycleCallbacks {
        C01562() {
        }

        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            ApplicationStatus.onStateChange(activity, 1);
        }

        public void onActivityDestroyed(Activity activity) {
            ApplicationStatus.onStateChange(activity, 6);
        }

        public void onActivityPaused(Activity activity) {
            ApplicationStatus.onStateChange(activity, 4);
        }

        public void onActivityResumed(Activity activity) {
            ApplicationStatus.onStateChange(activity, 3);
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        public void onActivityStarted(Activity activity) {
            ApplicationStatus.onStateChange(activity, 2);
        }

        public void onActivityStopped(Activity activity) {
            ApplicationStatus.onStateChange(activity, 5);
        }
    }

    static class C01573 implements Runnable {

        class C04251 implements ApplicationStateListener {
            C04251() {
            }

            public void onApplicationStateChange(int newState) {
                ApplicationStatus.nativeOnApplicationStateChange(newState);
            }
        }

        C01573() {
        }

        public void run() {
            if (ApplicationStatus.sNativeApplicationStateListener == null) {
                ApplicationStatus.sNativeApplicationStateListener = new C04251();
                ApplicationStatus.registerApplicationStateListener(ApplicationStatus.sNativeApplicationStateListener);
            }
        }
    }

    private static class ActivityInfo {
        private ObserverList<ActivityStateListener> mListeners;
        private int mStatus;

        private ActivityInfo() {
            this.mStatus = 6;
            this.mListeners = new ObserverList();
        }

        public int getStatus() {
            return this.mStatus;
        }

        public void setStatus(int status) {
            this.mStatus = status;
        }

        public ObserverList<ActivityStateListener> getListeners() {
            return this.mListeners;
        }
    }

    public interface ActivityStateListener {
        void onActivityStateChange(Activity activity, int i);
    }

    public interface ApplicationStateListener {
        void onApplicationStateChange(int i);
    }

    static class C04241 implements WindowFocusChangedListener {
        C04241() {
        }

        public void onWindowFocusChanged(Activity activity, boolean hasFocus) {
            if (hasFocus && activity != ApplicationStatus.sActivity) {
                int state = ApplicationStatus.getStateForActivity(activity);
                if (state != 6 && state != 5) {
                    ApplicationStatus.sActivity = activity;
                }
            }
        }
    }

    private static native void nativeOnApplicationStateChange(int i);

    static {
        boolean z;
        if (ApplicationStatus.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
    }

    private ApplicationStatus() {
    }

    public static void initialize(Application application) {
        ApplicationStatusManager.registerWindowFocusChangedListener(new C04241());
        application.registerActivityLifecycleCallbacks(new C01562());
    }

    private static void onStateChange(Activity activity, int newState) {
        if (activity == null) {
            throw new IllegalArgumentException("null activity is not supported");
        }
        if (sActivity == null || newState == 1 || newState == 3 || newState == 2) {
            sActivity = activity;
        }
        int oldApplicationState = getStateForApplication();
        if (newState == 1) {
            if ($assertionsDisabled || !sActivityInfo.containsKey(activity)) {
                sActivityInfo.put(activity, new ActivityInfo());
            } else {
                throw new AssertionError();
            }
        }
        synchronized (sCachedApplicationStateLock) {
            sCachedApplicationState = null;
        }
        ActivityInfo info = (ActivityInfo) sActivityInfo.get(activity);
        if (info != null) {
            info.setStatus(newState);
            Iterator i$ = info.getListeners().iterator();
            while (i$.hasNext()) {
                ((ActivityStateListener) i$.next()).onActivityStateChange(activity, newState);
            }
            i$ = sGeneralActivityStateListeners.iterator();
            while (i$.hasNext()) {
                ((ActivityStateListener) i$.next()).onActivityStateChange(activity, newState);
            }
            int applicationState = getStateForApplication();
            if (applicationState != oldApplicationState) {
                i$ = sApplicationStateListeners.iterator();
                while (i$.hasNext()) {
                    ((ApplicationStateListener) i$.next()).onApplicationStateChange(applicationState);
                }
            }
            if (newState == 6) {
                sActivityInfo.remove(activity);
                if (activity == sActivity) {
                    sActivity = null;
                }
            }
        }
    }

    @VisibleForTesting
    public static void onStateChangeForTesting(Activity activity, int newState) {
        onStateChange(activity, newState);
    }

    public static Activity getLastTrackedFocusedActivity() {
        return sActivity;
    }

    public static List<WeakReference<Activity>> getRunningActivities() {
        List<WeakReference<Activity>> activities = new ArrayList();
        for (Activity activity : sActivityInfo.keySet()) {
            activities.add(new WeakReference(activity));
        }
        return activities;
    }

    public static int getStateForActivity(Activity activity) {
        ActivityInfo info = (ActivityInfo) sActivityInfo.get(activity);
        return info != null ? info.getStatus() : 6;
    }

    @CalledByNative
    public static int getStateForApplication() {
        int intValue;
        synchronized (sCachedApplicationStateLock) {
            if (sCachedApplicationState == null) {
                sCachedApplicationState = Integer.valueOf(determineApplicationState());
            }
            intValue = sCachedApplicationState.intValue();
        }
        return intValue;
    }

    public static boolean hasVisibleActivities() {
        int state = getStateForApplication();
        if (state == 1 || state == 2) {
            return true;
        }
        return false;
    }

    public static boolean isEveryActivityDestroyed() {
        return sActivityInfo.isEmpty();
    }

    public static void registerStateListenerForAllActivities(ActivityStateListener listener) {
        sGeneralActivityStateListeners.addObserver(listener);
    }

    public static void registerStateListenerForActivity(ActivityStateListener listener, Activity activity) {
        if ($assertionsDisabled || activity != null) {
            ActivityInfo info = (ActivityInfo) sActivityInfo.get(activity);
            if ($assertionsDisabled || !(info == null || info.getStatus() == 6)) {
                info.getListeners().addObserver(listener);
                return;
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    public static void unregisterActivityStateListener(ActivityStateListener listener) {
        sGeneralActivityStateListeners.removeObserver(listener);
        for (ActivityInfo info : sActivityInfo.values()) {
            info.getListeners().removeObserver(listener);
        }
    }

    public static void registerApplicationStateListener(ApplicationStateListener listener) {
        sApplicationStateListeners.addObserver(listener);
    }

    public static void unregisterApplicationStateListener(ApplicationStateListener listener) {
        sApplicationStateListeners.removeObserver(listener);
    }

    public static void informActivityStarted(Activity activity) {
        onStateChange(activity, 1);
        onStateChange(activity, 2);
        onStateChange(activity, 3);
    }

    public static void destroyForJUnitTests() {
        sApplicationStateListeners.clear();
        sGeneralActivityStateListeners.clear();
        sActivityInfo.clear();
        synchronized (sCachedApplicationStateLock) {
            sCachedApplicationState = null;
        }
        sActivity = null;
        sNativeApplicationStateListener = null;
    }

    @CalledByNative
    private static void registerThreadSafeNativeApplicationStateListener() {
        ThreadUtils.runOnUiThread(new C01573());
    }

    private static int determineApplicationState() {
        boolean hasPausedActivity = false;
        boolean hasStoppedActivity = false;
        for (ActivityInfo info : sActivityInfo.values()) {
            int state = info.getStatus();
            if (state != 4 && state != 5 && state != 6) {
                return 1;
            }
            if (state == 4) {
                hasPausedActivity = true;
            } else if (state == 5) {
                hasStoppedActivity = true;
            }
        }
        if (hasPausedActivity) {
            return 2;
        }
        if (hasStoppedActivity) {
            return 3;
        }
        return 4;
    }
}
