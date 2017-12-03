package org.chromium.ui.base;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Process;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import java.lang.ref.WeakReference;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.ApplicationStatus;
import org.chromium.base.ApplicationStatus.ActivityStateListener;
import org.chromium.base.Callback;
import org.chromium.base.ContextUtils;
import org.chromium.ui.UiUtils;
import org.chromium.ui.base.WindowAndroid.IntentCallback;
import org.chromium.ui.base.WindowAndroid.PermissionCallback;

public class ActivityWindowAndroid extends WindowAndroid implements ActivityStateListener, OnLayoutChangeListener {
    static final /* synthetic */ boolean $assertionsDisabled = (!ActivityWindowAndroid.class.desiredAssertionStatus());
    private static final String PERMISSION_QUERIED_KEY_PREFIX = "HasRequestedAndroidPermission::";
    private static final int REQUEST_CODE_PREFIX = 1000;
    private static final int REQUEST_CODE_RANGE_SIZE = 100;
    private final Handler mHandler;
    private int mNextRequestCode;
    private final SparseArray<PermissionCallback> mOutstandingPermissionRequests;

    private class ActivityAndroidPermissionDelegate implements AndroidPermissionDelegate {
        private ActivityAndroidPermissionDelegate() {
        }

        public boolean hasPermission(String permission) {
            return ApiCompatibilityUtils.checkPermission(ActivityWindowAndroid.this.mApplicationContext, permission, Process.myPid(), Process.myUid()) == 0;
        }

        public boolean canRequestPermission(String permission) {
            if (VERSION.SDK_INT < 23) {
                return false;
            }
            Activity activity = (Activity) ActivityWindowAndroid.this.getActivity().get();
            if (activity == null || isPermissionRevokedByPolicy(permission)) {
                return false;
            }
            if (activity.shouldShowRequestPermissionRationale(permission)) {
                return true;
            }
            if (ContextUtils.getAppSharedPreferences().getBoolean(ActivityWindowAndroid.this.getHasRequestedPermissionKey(permission), false)) {
                return false;
            }
            return true;
        }

        public boolean isPermissionRevokedByPolicy(String permission) {
            if (VERSION.SDK_INT < 23) {
                return false;
            }
            Activity activity = (Activity) ActivityWindowAndroid.this.getActivity().get();
            if (activity != null) {
                return activity.getPackageManager().isPermissionRevokedByPolicy(permission, activity.getPackageName());
            }
            return false;
        }

        public void requestPermissions(final String[] permissions, final PermissionCallback callback) {
            if (!requestPermissionsInternal(permissions, callback)) {
                ActivityWindowAndroid.this.mHandler.post(new Runnable() {
                    public void run() {
                        int[] results = new int[permissions.length];
                        for (int i = 0; i < permissions.length; i++) {
                            results[i] = ActivityAndroidPermissionDelegate.this.hasPermission(permissions[i]) ? 0 : -1;
                        }
                        callback.onRequestPermissionsResult(permissions, results);
                    }
                });
            }
        }

        private boolean requestPermissionsInternal(String[] permissions, PermissionCallback callback) {
            if (VERSION.SDK_INT < 23) {
                return false;
            }
            Activity activity = (Activity) ActivityWindowAndroid.this.getActivity().get();
            if (activity == null) {
                return false;
            }
            int requestCode = ActivityWindowAndroid.this.generateNextRequestCode();
            ActivityWindowAndroid.this.mOutstandingPermissionRequests.put(requestCode, callback);
            activity.requestPermissions(permissions, requestCode);
            return true;
        }
    }

    public ActivityWindowAndroid(Context context) {
        this(context, true);
    }

    public ActivityWindowAndroid(Context context, boolean listenToActivityState) {
        super(context);
        this.mNextRequestCode = 0;
        Activity activity = WindowAndroid.activityFromContext(context);
        if (activity == null) {
            throw new IllegalArgumentException("Context is not and does not wrap an Activity");
        }
        this.mHandler = new Handler();
        this.mOutstandingPermissionRequests = new SparseArray();
        if (listenToActivityState) {
            ApplicationStatus.registerStateListenerForActivity(this, activity);
        }
        setAndroidPermissionDelegate(new ActivityAndroidPermissionDelegate());
    }

    protected void registerKeyboardVisibilityCallbacks() {
        Activity activity = (Activity) getActivity().get();
        if (activity != null) {
            View content = activity.findViewById(16908290);
            this.mIsKeyboardShowing = UiUtils.isKeyboardShowing((Context) getActivity().get(), content);
            content.addOnLayoutChangeListener(this);
        }
    }

    protected void unregisterKeyboardVisibilityCallbacks() {
        Activity activity = (Activity) getActivity().get();
        if (activity != null) {
            activity.findViewById(16908290).removeOnLayoutChangeListener(this);
        }
    }

    public int showCancelableIntent(PendingIntent intent, IntentCallback callback, Integer errorId) {
        Activity activity = (Activity) getActivity().get();
        if (activity == null) {
            return -1;
        }
        int requestCode = generateNextRequestCode();
        try {
            activity.startIntentSenderForResult(intent.getIntentSender(), requestCode, new Intent(), 0, 0, 0);
            storeCallbackData(requestCode, callback, errorId);
            return requestCode;
        } catch (SendIntentException e) {
            return -1;
        }
    }

    public int showCancelableIntent(Intent intent, IntentCallback callback, Integer errorId) {
        Activity activity = (Activity) getActivity().get();
        if (activity == null) {
            return -1;
        }
        int requestCode = generateNextRequestCode();
        try {
            activity.startActivityForResult(intent, requestCode);
            storeCallbackData(requestCode, callback, errorId);
            return requestCode;
        } catch (ActivityNotFoundException e) {
            return -1;
        }
    }

    public int showCancelableIntent(Callback<Integer> intentTrigger, IntentCallback callback, Integer errorId) {
        if (((Activity) getActivity().get()) == null) {
            return -1;
        }
        int requestCode = generateNextRequestCode();
        intentTrigger.onResult(Integer.valueOf(requestCode));
        storeCallbackData(requestCode, callback, errorId);
        return requestCode;
    }

    public void cancelIntent(int requestCode) {
        Activity activity = (Activity) getActivity().get();
        if (activity != null) {
            activity.finishActivity(requestCode);
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentCallback callback = (IntentCallback) this.mOutstandingIntents.get(requestCode);
        this.mOutstandingIntents.delete(requestCode);
        String errorMessage = (String) this.mIntentErrors.remove(Integer.valueOf(requestCode));
        if (callback != null) {
            callback.onIntentCompleted(this, resultCode, this.mApplicationContext.getContentResolver(), data);
            return true;
        } else if (errorMessage == null) {
            return false;
        } else {
            showCallbackNonExistentError(errorMessage);
            return true;
        }
    }

    private String getHasRequestedPermissionKey(String permission) {
        String permissionQueriedKey = permission;
        try {
            PermissionInfo permissionInfo = getApplicationContext().getPackageManager().getPermissionInfo(permission, 128);
            if (!TextUtils.isEmpty(permissionInfo.group)) {
                permissionQueriedKey = permissionInfo.group;
            }
        } catch (NameNotFoundException e) {
        }
        return PERMISSION_QUERIED_KEY_PREFIX + permissionQueriedKey;
    }

    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Activity activity = (Activity) getActivity().get();
        if ($assertionsDisabled || activity != null) {
            Editor editor = ContextUtils.getAppSharedPreferences().edit();
            for (String hasRequestedPermissionKey : permissions) {
                editor.putBoolean(getHasRequestedPermissionKey(hasRequestedPermissionKey), true);
            }
            editor.apply();
            PermissionCallback callback = (PermissionCallback) this.mOutstandingPermissionRequests.get(requestCode);
            this.mOutstandingPermissionRequests.delete(requestCode);
            if (callback == null) {
                return false;
            }
            callback.onRequestPermissionsResult(permissions, grantResults);
            return true;
        }
        throw new AssertionError();
    }

    public WeakReference<Activity> getActivity() {
        return new WeakReference(WindowAndroid.activityFromContext((Context) getContext().get()));
    }

    public void onActivityStateChange(Activity activity, int newState) {
        if (newState == 5) {
            onActivityStopped();
        } else if (newState == 2) {
            onActivityStarted();
        }
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        keyboardVisibilityPossiblyChanged(UiUtils.isKeyboardShowing((Context) getActivity().get(), v));
    }

    private int generateNextRequestCode() {
        int requestCode = this.mNextRequestCode + 1000;
        this.mNextRequestCode = (this.mNextRequestCode + 1) % REQUEST_CODE_RANGE_SIZE;
        return requestCode;
    }

    private void storeCallbackData(int requestCode, IntentCallback callback, Integer errorId) {
        this.mOutstandingIntents.put(requestCode, callback);
        this.mIntentErrors.put(Integer.valueOf(requestCode), errorId == null ? null : this.mApplicationContext.getString(errorId.intValue()));
    }
}
