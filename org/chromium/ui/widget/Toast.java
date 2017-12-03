package org.chromium.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.chromium.base.SysUtils;

public class Toast {
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    private ViewGroup mSWLayout;
    private android.widget.Toast mToast;

    public Toast(Context context) {
        this(context, new android.widget.Toast(context));
    }

    private Toast(Context context, android.widget.Toast toast) {
        this.mToast = toast;
        if (SysUtils.isLowEndDevice() && VERSION.SDK_INT >= 21 && context.getApplicationInfo().targetSdkVersion >= 21 && isHWAccelerationDisabled(context)) {
            this.mSWLayout = new FrameLayout(new ContextWrapper(context) {
                public ApplicationInfo getApplicationInfo() {
                    ApplicationInfo info = new ApplicationInfo(super.getApplicationInfo());
                    info.targetSdkVersion = 19;
                    return info;
                }
            });
            setView(toast.getView());
        }
    }

    public android.widget.Toast getAndroidToast() {
        return this.mToast;
    }

    public void show() {
        this.mToast.show();
    }

    public void cancel() {
        this.mToast.cancel();
    }

    public void setView(View view) {
        if (this.mSWLayout != null) {
            this.mSWLayout.removeAllViews();
            if (view != null) {
                this.mSWLayout.addView(view, -2, -2);
                this.mToast.setView(this.mSWLayout);
                return;
            }
            this.mToast.setView(null);
            return;
        }
        this.mToast.setView(view);
    }

    public View getView() {
        if (this.mToast.getView() == null) {
            return null;
        }
        if (this.mSWLayout != null) {
            return this.mSWLayout.getChildAt(0);
        }
        return this.mToast.getView();
    }

    public void setDuration(int duration) {
        this.mToast.setDuration(duration);
    }

    public int getDuration() {
        return this.mToast.getDuration();
    }

    public void setMargin(float horizontalMargin, float verticalMargin) {
        this.mToast.setMargin(horizontalMargin, verticalMargin);
    }

    public float getHorizontalMargin() {
        return this.mToast.getHorizontalMargin();
    }

    public float getVerticalMargin() {
        return this.mToast.getVerticalMargin();
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        this.mToast.setGravity(gravity, xOffset, yOffset);
    }

    public int getGravity() {
        return this.mToast.getGravity();
    }

    public int getXOffset() {
        return this.mToast.getXOffset();
    }

    public int getYOffset() {
        return this.mToast.getYOffset();
    }

    public void setText(int resId) {
        this.mToast.setText(resId);
    }

    public void setText(CharSequence s) {
        this.mToast.setText(s);
    }

    @SuppressLint({"ShowToast"})
    public static Toast makeText(Context context, CharSequence text, int duration) {
        return new Toast(context, android.widget.Toast.makeText(context, text, duration));
    }

    @SuppressLint({"ShowToast"})
    public static Toast makeText(Context context, int resId, int duration) throws NotFoundException {
        return new Toast(context, android.widget.Toast.makeText(context, resId, duration));
    }

    private static Activity getActivity(Context context) {
        while (context != null) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            if (!(context instanceof ContextWrapper)) {
                break;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private static boolean isHWAccelerationDisabled(Context context) {
        Activity activity = getActivity(context);
        if (activity == null) {
            return false;
        }
        try {
            if ((activity.getPackageManager().getActivityInfo(activity.getComponentName(), 0).flags & 512) == 0) {
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
