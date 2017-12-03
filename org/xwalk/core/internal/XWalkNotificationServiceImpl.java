package org.xwalk.core.internal;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.util.AndroidRuntimeException;
import android.util.Log;
import java.util.HashMap;
import org.chromium.ui.base.PageTransition;

public class XWalkNotificationServiceImpl implements XWalkNotificationService {
    private static final String TAG = "XWalkNotificationServiceImpl";
    private static final String XWALK_ACTION_CLICK_NOTIFICATION_SUFFIX = ".notification.click";
    private static final String XWALK_ACTION_CLOSE_NOTIFICATION_SUFFIX = ".notification.close";
    private static final String XWALK_INTENT_CATEGORY_NOTIFICATION_PREFIX = "notification_";
    private static final String XWALK_INTENT_EXTRA_KEY_NOTIFICATION_ID = "xwalk.NOTIFICATION_ID";
    private XWalkContentsClientBridge mBridge;
    private Context mContext;
    private HashMap<Integer, WebNotification> mExistNotificationIds = new HashMap();
    private HashMap<String, WebNotification> mExistReplaceIds = new HashMap();
    private BroadcastReceiver mNotificationCloseReceiver = new C03521();
    private NotificationManager mNotificationManager = ((NotificationManager) this.mContext.getSystemService("notification"));
    private XWalkViewInternal mView;

    class C03521 extends BroadcastReceiver {
        C03521() {
        }

        public void onReceive(Context context, Intent intent) {
            XWalkNotificationServiceImpl.this.mView.onNewIntent(intent);
        }
    }

    private class WebNotification {
        public Builder mBuilder;
        public Integer mMessageNum = Integer.valueOf(1);
        public Integer mNotificationId;
        public String mReplaceId;

        WebNotification() {
        }
    }

    public XWalkNotificationServiceImpl(Context context, XWalkViewInternal view) {
        this.mContext = context;
        this.mView = view;
    }

    private static String getCategoryFromNotificationId(int id) {
        return XWALK_INTENT_CATEGORY_NOTIFICATION_PREFIX + id;
    }

    public void setBridge(XWalkContentsClientBridge bridge) {
        this.mBridge = bridge;
    }

    public void shutdown() {
        if (!this.mExistNotificationIds.isEmpty()) {
            unregisterReceiver();
        }
        this.mBridge = null;
    }

    public boolean maybeHandleIntent(Intent intent) {
        if (intent.getAction() == null) {
            return false;
        }
        int notificationId = intent.getIntExtra(XWALK_INTENT_EXTRA_KEY_NOTIFICATION_ID, -1);
        if (notificationId <= 0) {
            return false;
        }
        if (intent.getAction().equals(this.mView.getContext().getPackageName() + XWALK_ACTION_CLOSE_NOTIFICATION_SUFFIX)) {
            onNotificationClose(notificationId, true);
            return true;
        } else if (!intent.getAction().equals(this.mView.getContext().getPackageName() + XWALK_ACTION_CLICK_NOTIFICATION_SUFFIX)) {
            return false;
        } else {
            onNotificationClick(notificationId);
            return true;
        }
    }

    public Bitmap getNotificationIcon(Bitmap icon) {
        if (icon == null) {
            return null;
        }
        int originalWidth = icon.getWidth();
        int originalHeight = icon.getHeight();
        if (originalWidth == 0 || originalHeight == 0) {
            return icon;
        }
        int targetWidth = this.mContext.getResources().getDimensionPixelSize(17104901);
        int targetHeight = this.mContext.getResources().getDimensionPixelSize(17104902);
        if (originalWidth > targetWidth && originalHeight > targetHeight) {
            if (originalWidth * targetHeight > originalHeight * targetWidth) {
                targetHeight = (originalHeight * targetWidth) / originalWidth;
            } else {
                targetWidth = (originalWidth * targetHeight) / originalHeight;
            }
        }
        return Bitmap.createScaledBitmap(icon, targetWidth, targetHeight, true);
    }

    public void showNotification(String title, String message, String replaceId, Bitmap icon, int notificationId) {
        Builder builder;
        WebNotification webNotification;
        if (replaceId.isEmpty() || !this.mExistReplaceIds.containsKey(replaceId)) {
            builder = new Builder(this.mContext.getApplicationContext()).setAutoCancel(true);
            webNotification = new WebNotification();
            webNotification.mNotificationId = Integer.valueOf(notificationId);
            webNotification.mReplaceId = replaceId;
            webNotification.mBuilder = builder;
            this.mExistNotificationIds.put(Integer.valueOf(notificationId), webNotification);
            if (!replaceId.isEmpty()) {
                this.mExistReplaceIds.put(replaceId, webNotification);
            }
        } else {
            webNotification = (WebNotification) this.mExistReplaceIds.get(replaceId);
            notificationId = webNotification.mNotificationId.intValue();
            builder = webNotification.mBuilder;
            Integer valueOf = Integer.valueOf(webNotification.mMessageNum.intValue() + 1);
            webNotification.mMessageNum = valueOf;
            builder.setNumber(valueOf.intValue());
        }
        builder.setContentTitle(title);
        builder.setContentText(message);
        int iconRes = this.mContext.getApplicationInfo().icon;
        if (iconRes == 0) {
            iconRes = 17301651;
        }
        builder.setSmallIcon(iconRes);
        Bitmap bigIcon = getNotificationIcon(icon);
        if (bigIcon != null) {
            builder.setLargeIcon(bigIcon);
        }
        Context context = this.mView.getContext();
        String category = getCategoryFromNotificationId(notificationId);
        Intent clickIntent = new Intent(context, context.getClass()).setAction(context.getPackageName() + XWALK_ACTION_CLICK_NOTIFICATION_SUFFIX).putExtra(XWALK_INTENT_EXTRA_KEY_NOTIFICATION_ID, notificationId).setFlags(537919488).addCategory(category);
        Intent closeIntent = new Intent(context.getPackageName() + XWALK_ACTION_CLOSE_NOTIFICATION_SUFFIX).putExtra(XWALK_INTENT_EXTRA_KEY_NOTIFICATION_ID, notificationId).addCategory(category);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, clickIntent, PageTransition.FROM_API));
        builder.setDeleteIntent(PendingIntent.getBroadcast(context, 0, closeIntent, PageTransition.FROM_API));
        doShowNotification(notificationId, VERSION.SDK_INT >= 16 ? builder.build() : builder.getNotification());
        notificationChanged();
        onNotificationShown(notificationId);
    }

    public void cancelNotification(int notificationId) {
        this.mNotificationManager.cancel(notificationId);
        onNotificationClose(notificationId, false);
    }

    public void doShowNotification(int notificationId, Notification notification) {
        this.mNotificationManager.notify(notificationId, notification);
    }

    public void onNotificationShown(int notificationId) {
        if (((WebNotification) this.mExistNotificationIds.get(Integer.valueOf(notificationId))) != null && this.mBridge != null) {
            this.mBridge.notificationDisplayed(notificationId);
        }
    }

    public void onNotificationClick(int notificationId) {
        WebNotification webNotification = (WebNotification) this.mExistNotificationIds.get(Integer.valueOf(notificationId));
        if (webNotification != null) {
            this.mExistNotificationIds.remove(Integer.valueOf(notificationId));
            this.mExistReplaceIds.remove(webNotification.mReplaceId);
            notificationChanged();
            if (this.mBridge != null) {
                this.mBridge.notificationClicked(notificationId);
            }
        }
    }

    public void onNotificationClose(int notificationId, boolean byUser) {
        WebNotification webNotification = (WebNotification) this.mExistNotificationIds.get(Integer.valueOf(notificationId));
        if (webNotification != null) {
            this.mExistNotificationIds.remove(Integer.valueOf(notificationId));
            this.mExistReplaceIds.remove(webNotification.mReplaceId);
            notificationChanged();
            if (this.mBridge != null) {
                this.mBridge.notificationClosed(notificationId, byUser);
            }
        }
    }

    private void notificationChanged() {
        if (this.mExistNotificationIds.isEmpty()) {
            Log.i(TAG, "notifications are all cleared,unregister broadcast receiver for close pending intent");
            unregisterReceiver();
            return;
        }
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(this.mView.getContext().getPackageName() + XWALK_ACTION_CLOSE_NOTIFICATION_SUFFIX);
        for (Integer id : this.mExistNotificationIds.keySet()) {
            filter.addCategory(getCategoryFromNotificationId(id.intValue()));
        }
        try {
            this.mView.getContext().registerReceiver(this.mNotificationCloseReceiver, filter);
        } catch (AndroidRuntimeException e) {
            Log.w(TAG, e.getLocalizedMessage());
        }
    }

    private void unregisterReceiver() {
        this.mView.getContext().unregisterReceiver(this.mNotificationCloseReceiver);
    }
}
