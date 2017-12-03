package org.chromium.base;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.view.Window.Callback;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;

public class ApplicationStatusManager {
    private static final String TOOLBAR_CALLBACK_INTERNAL_WRAPPER_CLASS = "android.support.v7.internal.app.ToolbarActionBar$ToolbarCallbackWrapper";
    private static final String TOOLBAR_CALLBACK_WRAPPER_CLASS = "android.support.v7.app.ToolbarActionBar$ToolbarCallbackWrapper";
    private static ObserverList<WindowFocusChangedListener> sWindowFocusListeners = new ObserverList();

    static class C01581 implements ActivityLifecycleCallbacks {
        C01581() {
        }

        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            ApplicationStatusManager.setWindowFocusChangedCallback(activity);
        }

        public void onActivityDestroyed(Activity activity) {
        }

        public void onActivityPaused(Activity activity) {
        }

        public void onActivityResumed(Activity activity) {
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
        }
    }

    private static class WindowCallbackProxy implements InvocationHandler {
        private final Activity mActivity;
        private final Callback mCallback;

        public WindowCallbackProxy(Activity activity, Callback callback) {
            this.mCallback = callback;
            this.mActivity = activity;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("onWindowFocusChanged") && args.length == 1 && (args[0] instanceof Boolean)) {
                onWindowFocusChanged(((Boolean) args[0]).booleanValue());
                return null;
            }
            try {
                return method.invoke(this.mCallback, args);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof AbstractMethodError) {
                    throw e.getCause();
                }
                throw e;
            }
        }

        public void onWindowFocusChanged(boolean hasFocus) {
            this.mCallback.onWindowFocusChanged(hasFocus);
            Iterator i$ = ApplicationStatusManager.sWindowFocusListeners.iterator();
            while (i$.hasNext()) {
                ((WindowFocusChangedListener) i$.next()).onWindowFocusChanged(this.mActivity, hasFocus);
            }
        }
    }

    public interface WindowFocusChangedListener {
        void onWindowFocusChanged(Activity activity, boolean z);
    }

    public static void init(Application app) {
        ApplicationStatus.initialize(app);
        app.registerActivityLifecycleCallbacks(new C01581());
    }

    public static void registerWindowFocusChangedListener(WindowFocusChangedListener listener) {
        sWindowFocusListeners.addObserver(listener);
    }

    public static void unregisterWindowFocusChangedListener(WindowFocusChangedListener listener) {
        sWindowFocusListeners.removeObserver(listener);
    }

    public static void informActivityStarted(Activity activity) {
        setWindowFocusChangedCallback(activity);
        ApplicationStatus.informActivityStarted(activity);
    }

    private static void setWindowFocusChangedCallback(Activity activity) {
        Callback callback = activity.getWindow().getCallback();
        activity.getWindow().setCallback((Callback) Proxy.newProxyInstance(Callback.class.getClassLoader(), new Class[]{Callback.class}, new WindowCallbackProxy(activity, callback)));
    }
}
