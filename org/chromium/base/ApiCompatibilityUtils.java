package org.chromium.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager.TaskDescription;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.Process;
import android.os.StatFs;
import android.os.UserManager;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.TextView;

@TargetApi(21)
public class ApiCompatibilityUtils {
    static final /* synthetic */ boolean $assertionsDisabled = (!ApiCompatibilityUtils.class.desiredAssertionStatus());

    private static class FinishAndRemoveTaskWithRetry implements Runnable {
        private static final long MAX_TRY_COUNT = 3;
        private static final long RETRY_DELAY_MS = 500;
        private final Activity mActivity;
        private int mTryCount;

        FinishAndRemoveTaskWithRetry(Activity activity) {
            this.mActivity = activity;
        }

        public void run() {
            this.mActivity.finishAndRemoveTask();
            this.mTryCount++;
            if (!this.mActivity.isFinishing()) {
                if (((long) this.mTryCount) < 3) {
                    ThreadUtils.postOnUiThreadDelayed(this, RETRY_DELAY_MS);
                } else {
                    this.mActivity.finish();
                }
            }
        }
    }

    private ApiCompatibilityUtils() {
    }

    public static boolean isLayoutRtl(View view) {
        if (VERSION.SDK_INT < 17) {
            return false;
        }
        if (view.getLayoutDirection() == 1) {
            return true;
        }
        return false;
    }

    public static int getLayoutDirection(Configuration configuration) {
        if (VERSION.SDK_INT >= 17) {
            return configuration.getLayoutDirection();
        }
        return 0;
    }

    public static boolean isPrintingSupported() {
        return VERSION.SDK_INT >= 19;
    }

    public static boolean isElevationSupported() {
        return VERSION.SDK_INT >= 21;
    }

    public static void setLayoutDirection(View view, int layoutDirection) {
        if (VERSION.SDK_INT >= 17) {
            view.setLayoutDirection(layoutDirection);
        }
    }

    public static void setTextAlignment(View view, int textAlignment) {
        if (VERSION.SDK_INT >= 17) {
            view.setTextAlignment(textAlignment);
        }
    }

    public static void setTextDirection(View view, int textDirection) {
        if (VERSION.SDK_INT >= 17) {
            view.setTextDirection(textDirection);
        }
    }

    public static void setLabelFor(View labelView, int id) {
        if (VERSION.SDK_INT >= 17) {
            labelView.setLabelFor(id);
        }
    }

    public static void setMarginEnd(MarginLayoutParams layoutParams, int end) {
        if (VERSION.SDK_INT >= 17) {
            layoutParams.setMarginEnd(end);
        } else {
            layoutParams.rightMargin = end;
        }
    }

    public static int getMarginEnd(MarginLayoutParams layoutParams) {
        if (VERSION.SDK_INT >= 17) {
            return layoutParams.getMarginEnd();
        }
        return layoutParams.rightMargin;
    }

    public static void setMarginStart(MarginLayoutParams layoutParams, int start) {
        if (VERSION.SDK_INT >= 17) {
            layoutParams.setMarginStart(start);
        } else {
            layoutParams.leftMargin = start;
        }
    }

    public static int getMarginStart(MarginLayoutParams layoutParams) {
        if (VERSION.SDK_INT >= 17) {
            return layoutParams.getMarginStart();
        }
        return layoutParams.leftMargin;
    }

    public static void setPaddingRelative(View view, int start, int top, int end, int bottom) {
        if (VERSION.SDK_INT >= 17) {
            view.setPaddingRelative(start, top, end, bottom);
        } else {
            view.setPadding(start, top, end, bottom);
        }
    }

    public static int getPaddingStart(View view) {
        if (VERSION.SDK_INT >= 17) {
            return view.getPaddingStart();
        }
        return view.getPaddingLeft();
    }

    public static int getPaddingEnd(View view) {
        if (VERSION.SDK_INT >= 17) {
            return view.getPaddingEnd();
        }
        return view.getPaddingRight();
    }

    public static void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
        if (VERSION.SDK_INT == 17) {
            Drawable drawable;
            boolean isRtl = isLayoutRtl(textView);
            if (isRtl) {
                drawable = end;
            } else {
                drawable = start;
            }
            if (!isRtl) {
                start = end;
            }
            textView.setCompoundDrawables(drawable, top, start, bottom);
        } else if (VERSION.SDK_INT > 17) {
            textView.setCompoundDrawablesRelative(start, top, end, bottom);
        } else {
            textView.setCompoundDrawables(start, top, end, bottom);
        }
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
        if (VERSION.SDK_INT == 17) {
            Drawable drawable;
            boolean isRtl = isLayoutRtl(textView);
            if (isRtl) {
                drawable = end;
            } else {
                drawable = start;
            }
            if (!isRtl) {
                start = end;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, top, start, bottom);
        } else if (VERSION.SDK_INT > 17) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
        }
    }

    public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView textView, int start, int top, int end, int bottom) {
        if (VERSION.SDK_INT == 17) {
            int i;
            boolean isRtl = isLayoutRtl(textView);
            if (isRtl) {
                i = end;
            } else {
                i = start;
            }
            if (!isRtl) {
                start = end;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(i, top, start, bottom);
        } else if (VERSION.SDK_INT > 17) {
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
        }
    }

    public static String getCreatorPackage(PendingIntent intent) {
        if (VERSION.SDK_INT >= 17) {
            return intent.getCreatorPackage();
        }
        return intent.getTargetPackage();
    }

    @TargetApi(17)
    public static boolean isDeviceProvisioned(Context context) {
        if (VERSION.SDK_INT >= 17 && context != null && context.getContentResolver() != null && Global.getInt(context.getContentResolver(), "device_provisioned", 0) == 0) {
            return false;
        }
        return true;
    }

    public static void finishAndRemoveTask(Activity activity) {
        if (VERSION.SDK_INT > 21) {
            activity.finishAndRemoveTask();
        } else if (VERSION.SDK_INT == 21) {
            new FinishAndRemoveTaskWithRetry(activity).run();
        } else {
            activity.finish();
        }
    }

    @TargetApi(21)
    public static boolean setElevation(View view, float elevationValue) {
        if (!isElevationSupported()) {
            return false;
        }
        view.setElevation(elevationValue);
        return true;
    }

    public static boolean isInteractive(Context context) {
        PowerManager manager = (PowerManager) context.getSystemService("power");
        if (VERSION.SDK_INT >= 20) {
            return manager.isInteractive();
        }
        return manager.isScreenOn();
    }

    public static int getActivityNewDocumentFlag() {
        return VERSION.SDK_INT >= 21 ? 524288 : 524288;
    }

    public static boolean shouldSkipFirstUseHints(ContentResolver contentResolver) {
        if (VERSION.SDK_INT < 21 || Secure.getInt(contentResolver, "skip_first_use_hints", 0) == 0) {
            return false;
        }
        return true;
    }

    public static void setTaskDescription(Activity activity, String title, Bitmap icon, int color) {
        if (!$assertionsDisabled && Color.alpha(color) != 255) {
            throw new AssertionError();
        } else if (VERSION.SDK_INT >= 21) {
            activity.setTaskDescription(new TaskDescription(title, icon, color));
        }
    }

    public static void setStatusBarColor(Window window, int statusBarColor) {
        if (VERSION.SDK_INT >= 21) {
            if (statusBarColor == ViewCompat.MEASURED_STATE_MASK && window.getNavigationBarColor() == ViewCompat.MEASURED_STATE_MASK) {
                window.clearFlags(Integer.MIN_VALUE);
            } else {
                window.addFlags(Integer.MIN_VALUE);
            }
            window.setStatusBarColor(statusBarColor);
        }
    }

    public static Drawable getDrawable(Resources res, int id) throws NotFoundException {
        if (VERSION.SDK_INT >= 21) {
            return res.getDrawable(id, null);
        }
        return res.getDrawable(id);
    }

    public static Drawable getDrawableForDensity(Resources res, int id, int density) {
        if (VERSION.SDK_INT >= 21) {
            return res.getDrawableForDensity(id, density, null);
        }
        return res.getDrawableForDensity(id, density);
    }

    public static void finishAfterTransition(Activity activity) {
        if (VERSION.SDK_INT >= 21) {
            activity.finishAfterTransition();
        } else {
            activity.finish();
        }
    }

    public static Drawable getUserBadgedIcon(Context context, int id) {
        Drawable drawable = getDrawable(context.getResources(), id);
        if (VERSION.SDK_INT >= 21) {
            return context.getPackageManager().getUserBadgedIcon(drawable, Process.myUserHandle());
        }
        return drawable;
    }

    public static Drawable getUserBadgedDrawableForDensity(Context context, Drawable drawable, Rect badgeLocation, int density) {
        if (VERSION.SDK_INT >= 21) {
            return context.getPackageManager().getUserBadgedDrawableForDensity(drawable, Process.myUserHandle(), badgeLocation, density);
        }
        return drawable;
    }

    public static int getColor(Resources res, int id) throws NotFoundException {
        if (VERSION.SDK_INT >= 23) {
            return res.getColor(id, null);
        }
        return res.getColor(id);
    }

    public static ColorFilter getColorFilter(Drawable drawable) {
        if (VERSION.SDK_INT >= 21) {
            return drawable.getColorFilter();
        }
        return null;
    }

    public static ColorStateList getColorStateList(Resources res, int id) throws NotFoundException {
        if (VERSION.SDK_INT >= 23) {
            return res.getColorStateList(id, null);
        }
        return res.getColorStateList(id);
    }

    public static void setTextAppearance(TextView view, int id) {
        if (VERSION.SDK_INT >= 23) {
            view.setTextAppearance(id);
        } else {
            view.setTextAppearance(view.getContext(), id);
        }
    }

    public static long getBlockCount(StatFs statFs) {
        if (VERSION.SDK_INT >= 19) {
            return statFs.getBlockCountLong();
        }
        return (long) statFs.getBlockCount();
    }

    public static long getBlockSize(StatFs statFs) {
        if (VERSION.SDK_INT >= 19) {
            return statFs.getBlockSizeLong();
        }
        return (long) statFs.getBlockSize();
    }

    public static boolean isDemoUser(Context context) {
        boolean z = false;
        if (BuildInfo.isGreaterThanN()) {
            try {
                z = ((Boolean) UserManager.class.getMethod("isDemoUser", new Class[0]).invoke((UserManager) context.getSystemService("user"), new Object[0])).booleanValue();
            } catch (RuntimeException e) {
            } catch (Exception e2) {
            }
        }
        return z;
    }

    public static int checkPermission(Context context, String permission, int pid, int uid) {
        try {
            return context.checkPermission(permission, pid, uid);
        } catch (RuntimeException e) {
            return -1;
        }
    }
}
