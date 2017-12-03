package org.chromium.ui.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.Callback;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.ui.VSyncMonitor;
import org.chromium.ui.VSyncMonitor.Listener;
import org.chromium.ui.widget.Toast;

@JNINamespace("ui")
public class WindowAndroid {
    static final /* synthetic */ boolean $assertionsDisabled = (!WindowAndroid.class.desiredAssertionStatus());
    public static final int START_INTENT_FAILURE = -1;
    private static final String TAG = "WindowAndroid";
    static final String WINDOW_CALLBACK_ERRORS = "window_callback_errors";
    private final AccessibilityManager mAccessibilityManager;
    private View mAnimationPlaceholderView;
    private HashSet<Animator> mAnimationsOverContent = new HashSet();
    protected Context mApplicationContext;
    private WeakReference<Context> mContextRef;
    protected HashMap<Integer, String> mIntentErrors;
    protected boolean mIsKeyboardShowing = false;
    private boolean mIsTouchExplorationEnabled;
    private ViewGroup mKeyboardAccessoryView;
    private LinkedList<KeyboardVisibilityListener> mKeyboardVisibilityListeners = new LinkedList();
    private long mNativeWindowAndroid = 0;
    protected SparseArray<IntentCallback> mOutstandingIntents;
    private AndroidPermissionDelegate mPermissionDelegate;
    private TouchExplorationMonitor mTouchExplorationMonitor;
    private final Listener mVSyncListener = new C04571();
    private final VSyncMonitor mVSyncMonitor;

    class C02972 extends AnimatorListenerAdapter {
        C02972() {
        }

        public void onAnimationEnd(Animator animation) {
            animation.removeListener(this);
            WindowAndroid.this.mAnimationsOverContent.remove(animation);
            WindowAndroid.this.refreshWillNotDraw();
        }
    }

    public interface IntentCallback {
        void onIntentCompleted(WindowAndroid windowAndroid, int i, ContentResolver contentResolver, Intent intent);
    }

    public interface KeyboardVisibilityListener {
        void keyboardVisibilityChanged(boolean z);
    }

    public interface PermissionCallback {
        void onRequestPermissionsResult(String[] strArr, int[] iArr);
    }

    @TargetApi(19)
    private class TouchExplorationMonitor {
        private TouchExplorationStateChangeListener mTouchExplorationListener;

        TouchExplorationMonitor() {
            this.mTouchExplorationListener = new TouchExplorationStateChangeListener(WindowAndroid.this) {
                public void onTouchExplorationStateChanged(boolean enabled) {
                    WindowAndroid.this.mIsTouchExplorationEnabled = WindowAndroid.this.mAccessibilityManager.isTouchExplorationEnabled();
                    WindowAndroid.this.refreshWillNotDraw();
                }
            };
            WindowAndroid.this.mAccessibilityManager.addTouchExplorationStateChangeListener(this.mTouchExplorationListener);
        }

        void destroy() {
            WindowAndroid.this.mAccessibilityManager.removeTouchExplorationStateChangeListener(this.mTouchExplorationListener);
        }
    }

    class C04571 implements Listener {
        C04571() {
        }

        public void onVSync(VSyncMonitor monitor, long vsyncTimeMicros) {
            if (WindowAndroid.this.mNativeWindowAndroid != 0) {
                WindowAndroid.this.nativeOnVSync(WindowAndroid.this.mNativeWindowAndroid, vsyncTimeMicros, WindowAndroid.this.mVSyncMonitor.getVSyncPeriodInMicroseconds());
            }
        }
    }

    private native void nativeDestroy(long j);

    private native long nativeInit();

    private native void nativeOnActivityStarted(long j);

    private native void nativeOnActivityStopped(long j);

    private native void nativeOnVSync(long j, long j2, long j3);

    private native void nativeOnVisibilityChanged(long j, boolean z);

    public static Activity activityFromContext(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return activityFromContext(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public boolean isInsideVSync() {
        return this.mVSyncMonitor.isInsideVSync();
    }

    public long getVsyncPeriodInMillis() {
        return this.mVSyncMonitor.getVSyncPeriodInMicroseconds() / 1000;
    }

    @SuppressLint({"UseSparseArrays"})
    public WindowAndroid(Context context) {
        this.mApplicationContext = context.getApplicationContext();
        this.mContextRef = new WeakReference(context);
        this.mOutstandingIntents = new SparseArray();
        this.mIntentErrors = new HashMap();
        this.mVSyncMonitor = new VSyncMonitor(context, this.mVSyncListener);
        this.mAccessibilityManager = (AccessibilityManager) this.mApplicationContext.getSystemService("accessibility");
    }

    @CalledByNative
    private static WindowAndroid createForTesting(Context context) {
        return new WindowAndroid(context);
    }

    @VisibleForTesting
    public void setAndroidPermissionDelegate(AndroidPermissionDelegate delegate) {
        this.mPermissionDelegate = delegate;
    }

    public boolean showIntent(PendingIntent intent, IntentCallback callback, Integer errorId) {
        return showCancelableIntent(intent, callback, errorId) >= 0;
    }

    public boolean showIntent(Intent intent, IntentCallback callback, Integer errorId) {
        return showCancelableIntent(intent, callback, errorId) >= 0;
    }

    public int showCancelableIntent(PendingIntent intent, IntentCallback callback, Integer errorId) {
        Log.d(TAG, "Can't show intent as context is not an Activity: " + intent);
        return -1;
    }

    public int showCancelableIntent(Intent intent, IntentCallback callback, Integer errorId) {
        Log.d(TAG, "Can't show intent as context is not an Activity: " + intent);
        return -1;
    }

    public int showCancelableIntent(Callback<Integer> callback, IntentCallback callback2, Integer errorId) {
        Log.d(TAG, "Can't show intent as context is not an Activity");
        return -1;
    }

    public void cancelIntent(int requestCode) {
        Log.d(TAG, "Can't cancel intent as context is not an Activity: " + requestCode);
    }

    public boolean removeIntentCallback(IntentCallback callback) {
        int requestCode = this.mOutstandingIntents.indexOfValue(callback);
        if (requestCode < 0) {
            return false;
        }
        this.mOutstandingIntents.remove(requestCode);
        this.mIntentErrors.remove(Integer.valueOf(requestCode));
        return true;
    }

    @CalledByNative
    public final boolean hasPermission(String permission) {
        if (this.mPermissionDelegate != null) {
            return this.mPermissionDelegate.hasPermission(permission);
        }
        return ApiCompatibilityUtils.checkPermission(this.mApplicationContext, permission, Process.myPid(), Process.myUid()) == 0;
    }

    @CalledByNative
    public final boolean canRequestPermission(String permission) {
        if (this.mPermissionDelegate != null) {
            return this.mPermissionDelegate.canRequestPermission(permission);
        }
        Log.w(TAG, "Cannot determine the request permission state as the context is not an Activity");
        if ($assertionsDisabled) {
            return false;
        }
        throw new AssertionError("Failed to determine the request permission state using a WindowAndroid without an Activity");
    }

    public final boolean isPermissionRevokedByPolicy(String permission) {
        if (this.mPermissionDelegate != null) {
            return this.mPermissionDelegate.isPermissionRevokedByPolicy(permission);
        }
        Log.w(TAG, "Cannot determine the policy permission state as the context is not an Activity");
        if ($assertionsDisabled) {
            return false;
        }
        throw new AssertionError("Failed to determine the policy permission state using a WindowAndroid without an Activity");
    }

    public final void requestPermissions(String[] permissions, PermissionCallback callback) {
        if (this.mPermissionDelegate != null) {
            this.mPermissionDelegate.requestPermissions(permissions, callback);
            return;
        }
        Log.w(TAG, "Cannot request permissions as the context is not an Activity");
        if (!$assertionsDisabled) {
            throw new AssertionError("Failed to request permissions using a WindowAndroid without an Activity");
        }
    }

    public void showError(String error) {
        if (error != null) {
            Toast.makeText(this.mApplicationContext, (CharSequence) error, 0).show();
        }
    }

    public void showError(int resId) {
        showError(this.mApplicationContext.getString(resId));
    }

    protected void showCallbackNonExistentError(String error) {
        showError(error);
    }

    public void sendBroadcast(Intent intent) {
        this.mApplicationContext.sendBroadcast(intent);
    }

    public WeakReference<Activity> getActivity() {
        return new WeakReference(null);
    }

    public Context getApplicationContext() {
        return this.mApplicationContext;
    }

    public void saveInstanceState(Bundle bundle) {
        bundle.putSerializable(WINDOW_CALLBACK_ERRORS, this.mIntentErrors);
    }

    public void restoreInstanceState(Bundle bundle) {
        if (bundle != null) {
            Serializable errors = bundle.getSerializable(WINDOW_CALLBACK_ERRORS);
            if (errors instanceof HashMap) {
                this.mIntentErrors = (HashMap) errors;
            }
        }
    }

    public void onVisibilityChanged(boolean visible) {
        if (this.mNativeWindowAndroid != 0) {
            nativeOnVisibilityChanged(this.mNativeWindowAndroid, visible);
        }
    }

    protected void onActivityStopped() {
        if (this.mNativeWindowAndroid != 0) {
            nativeOnActivityStopped(this.mNativeWindowAndroid);
        }
    }

    protected void onActivityStarted() {
        if (this.mNativeWindowAndroid != 0) {
            nativeOnActivityStarted(this.mNativeWindowAndroid);
        }
    }

    @CalledByNative
    private void requestVSyncUpdate() {
        this.mVSyncMonitor.requestUpdate();
    }

    public boolean canResolveActivity(Intent intent) {
        return this.mApplicationContext.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }

    public void destroy() {
        if (this.mNativeWindowAndroid != 0) {
            nativeDestroy(this.mNativeWindowAndroid);
            this.mNativeWindowAndroid = 0;
        }
        if (VERSION.SDK_INT >= 19 && this.mTouchExplorationMonitor != null) {
            this.mTouchExplorationMonitor.destroy();
        }
    }

    public long getNativePointer() {
        if (this.mNativeWindowAndroid == 0) {
            this.mNativeWindowAndroid = nativeInit();
        }
        return this.mNativeWindowAndroid;
    }

    public void setAnimationPlaceholderView(View view) {
        this.mAnimationPlaceholderView = view;
        this.mIsTouchExplorationEnabled = this.mAccessibilityManager.isTouchExplorationEnabled();
        refreshWillNotDraw();
        if (VERSION.SDK_INT >= 19) {
            this.mTouchExplorationMonitor = new TouchExplorationMonitor();
        }
    }

    public void setKeyboardAccessoryView(ViewGroup view) {
        this.mKeyboardAccessoryView = view;
    }

    public ViewGroup getKeyboardAccessoryView() {
        return this.mKeyboardAccessoryView;
    }

    protected void registerKeyboardVisibilityCallbacks() {
    }

    protected void unregisterKeyboardVisibilityCallbacks() {
    }

    public void addKeyboardVisibilityListener(KeyboardVisibilityListener listener) {
        if (this.mKeyboardVisibilityListeners.isEmpty()) {
            registerKeyboardVisibilityCallbacks();
        }
        this.mKeyboardVisibilityListeners.add(listener);
    }

    public void removeKeyboardVisibilityListener(KeyboardVisibilityListener listener) {
        this.mKeyboardVisibilityListeners.remove(listener);
        if (this.mKeyboardVisibilityListeners.isEmpty()) {
            unregisterKeyboardVisibilityCallbacks();
        }
    }

    protected void keyboardVisibilityPossiblyChanged(boolean isShowing) {
        if (this.mIsKeyboardShowing != isShowing) {
            this.mIsKeyboardShowing = isShowing;
            Iterator i$ = new LinkedList(this.mKeyboardVisibilityListeners).iterator();
            while (i$.hasNext()) {
                ((KeyboardVisibilityListener) i$.next()).keyboardVisibilityChanged(isShowing);
            }
        }
    }

    public void startAnimationOverContent(Animator animation) {
        if (this.mAnimationPlaceholderView != null) {
            if (animation.isStarted()) {
                throw new IllegalArgumentException("Already started.");
            } else if (this.mAnimationsOverContent.add(animation)) {
                animation.start();
                refreshWillNotDraw();
                animation.addListener(new C02972());
            } else {
                throw new IllegalArgumentException("Already Added.");
            }
        }
    }

    public WeakReference<Context> getContext() {
        return new WeakReference(this.mContextRef.get());
    }

    private void refreshWillNotDraw() {
        boolean willNotDraw = !this.mIsTouchExplorationEnabled && this.mAnimationsOverContent.isEmpty();
        if (this.mAnimationPlaceholderView.willNotDraw() != willNotDraw) {
            this.mAnimationPlaceholderView.setWillNotDraw(willNotDraw);
        }
    }
}
