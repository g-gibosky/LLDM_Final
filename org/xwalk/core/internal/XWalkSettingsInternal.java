package org.xwalk.core.internal;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.Settings.System;
import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content.browser.MediaSessionDelegate;
import org.chromium.content_public.browser.WebContents;

@JNINamespace("xwalk")
@XWalkAPI(createInternally = true)
public class XWalkSettingsInternal {
    static final /* synthetic */ boolean $assertionsDisabled;
    @XWalkAPI
    public static final int LOAD_CACHE_ELSE_NETWORK = 1;
    @XWalkAPI
    public static final int LOAD_CACHE_ONLY = 3;
    @XWalkAPI
    public static final int LOAD_DEFAULT = -1;
    @XWalkAPI
    public static final int LOAD_NO_CACHE = 2;
    private static final int MAXIMUM_FONT_SIZE = 72;
    private static final int MINIMUM_FONT_SIZE = 1;
    private static final String TAG = "XWalkSettings";
    private static boolean sAppCachePathIsSet = false;
    private static final Object sGlobalContentSettingsLock = new Object();
    private String mAcceptLanguages;
    private boolean mAllowContentUrlAccess;
    private boolean mAllowFileAccessFromFileURLs;
    private boolean mAllowFileUrlAccess;
    private boolean mAllowScriptsToCloseWindows;
    private boolean mAllowUniversalAccessFromFileURLs;
    private boolean mAppCacheEnabled;
    private boolean mAutoCompleteEnabled;
    private boolean mBlockNetworkLoads;
    private boolean mBuiltInZoomControls;
    private int mCacheMode;
    private final Context mContext;
    private double mDIPScale;
    private boolean mDatabaseEnabled;
    private int mDefaultFixedFontSize;
    private int mDefaultFontSize;
    private String mDefaultVideoPosterURL;
    private boolean mDisplayZoomControls;
    private boolean mDomStorageEnabled;
    private final EventHandler mEventHandler;
    private boolean mGeolocationEnabled;
    private boolean mImagesEnabled;
    private float mInitialPageScalePercent;
    private boolean mIsUpdateWebkitPrefsMessagePending;
    private boolean mJavaScriptCanOpenWindowsAutomatically;
    private boolean mJavaScriptEnabled;
    private LayoutAlgorithmInternal mLayoutAlgorithm;
    private boolean mLoadWithOverviewMode;
    private boolean mLoadsImagesAutomatically;
    private boolean mMediaPlaybackRequiresUserGesture;
    private long mNativeXWalkSettings;
    private final boolean mPasswordEchoEnabled;
    private boolean mQuirksModeEnabled;
    private boolean mShouldFocusFirstNode;
    private boolean mSpatialNavigationEnabled;
    private boolean mSupportMultipleWindows;
    private boolean mSupportZoom;
    private int mTextSizePercent;
    private boolean mUseWideViewport;
    private String mUserAgent;
    private final Object mXWalkSettingsLock;
    private ZoomSupportChangeListener mZoomChangeListener;

    class C03581 implements Runnable {
        C03581() {
        }

        public void run() {
            if (XWalkSettingsInternal.this.mNativeXWalkSettings != 0) {
                XWalkSettingsInternal.this.nativeUpdateUserAgent(XWalkSettingsInternal.this.mNativeXWalkSettings);
            }
        }
    }

    class C03592 implements Runnable {
        C03592() {
        }

        public void run() {
            if (XWalkSettingsInternal.this.mNativeXWalkSettings != 0) {
                XWalkSettingsInternal.this.nativeUpdateAcceptLanguages(XWalkSettingsInternal.this.mNativeXWalkSettings);
            }
        }
    }

    class C03603 implements Runnable {
        C03603() {
        }

        public void run() {
            if (XWalkSettingsInternal.this.mNativeXWalkSettings != 0) {
                XWalkSettingsInternal.this.nativeUpdateFormDataPreferences(XWalkSettingsInternal.this.mNativeXWalkSettings);
            }
        }
    }

    class C03614 implements Runnable {
        C03614() {
        }

        public void run() {
            if (XWalkSettingsInternal.this.mNativeXWalkSettings != 0) {
                XWalkSettingsInternal.this.nativeUpdateInitialPageScale(XWalkSettingsInternal.this.mNativeXWalkSettings);
            }
        }
    }

    class C03636 implements Runnable {
        C03636() {
        }

        public void run() {
            if (XWalkSettingsInternal.this.mNativeXWalkSettings != 0) {
                XWalkSettingsInternal.this.mEventHandler.updateWebkitPreferencesLocked();
                XWalkSettingsInternal.this.nativeResetScrollAndScaleState(XWalkSettingsInternal.this.mNativeXWalkSettings);
            }
        }
    }

    private class EventHandler {
        static final /* synthetic */ boolean $assertionsDisabled = (!XWalkSettingsInternal.class.desiredAssertionStatus());
        private static final int UPDATE_WEBKIT_PREFERENCES = 0;
        private Handler mHandler;

        EventHandler() {
        }

        void bindUiThread() {
            if (this.mHandler == null) {
                this.mHandler = new Handler(ThreadUtils.getUiThreadLooper()) {
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 0:
                                synchronized (XWalkSettingsInternal.this.mXWalkSettingsLock) {
                                    XWalkSettingsInternal.this.updateWebkitPreferencesOnUiThread();
                                    XWalkSettingsInternal.this.mIsUpdateWebkitPrefsMessagePending = false;
                                    XWalkSettingsInternal.this.mXWalkSettingsLock.notifyAll();
                                }
                                return;
                            default:
                                return;
                        }
                    }
                };
            }
        }

        void maybeRunOnUiThreadBlocking(Runnable r) {
            if (this.mHandler != null) {
                ThreadUtils.runOnUiThreadBlocking(r);
            }
        }

        void maybePostOnUiThread(Runnable r) {
            if (this.mHandler != null) {
                this.mHandler.post(r);
            }
        }

        private void updateWebkitPreferencesLocked() {
            if (!$assertionsDisabled && !Thread.holdsLock(XWalkSettingsInternal.this.mXWalkSettingsLock)) {
                throw new AssertionError();
            } else if (XWalkSettingsInternal.this.mNativeXWalkSettings != 0 && this.mHandler != null) {
                if (ThreadUtils.runningOnUiThread()) {
                    XWalkSettingsInternal.this.updateWebkitPreferencesOnUiThread();
                } else if (!XWalkSettingsInternal.this.mIsUpdateWebkitPrefsMessagePending) {
                    XWalkSettingsInternal.this.mIsUpdateWebkitPrefsMessagePending = true;
                    this.mHandler.sendMessage(Message.obtain(null, 0));
                    while (XWalkSettingsInternal.this.mIsUpdateWebkitPrefsMessagePending) {
                        try {
                            XWalkSettingsInternal.this.mXWalkSettingsLock.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            }
        }
    }

    @XWalkAPI
    public enum LayoutAlgorithmInternal {
        NORMAL,
        SINGLE_COLUMN,
        NARROW_COLUMNS,
        TEXT_AUTOSIZING
    }

    static class LazyDefaultUserAgent {
        private static final String sInstance = XWalkSettingsInternal.nativeGetDefaultUserAgent();

        LazyDefaultUserAgent() {
        }
    }

    interface ZoomSupportChangeListener {
        void onGestureZoomSupportChanged(boolean z, boolean z2);
    }

    private native void nativeDestroy(long j);

    private static native String nativeGetDefaultUserAgent();

    private native long nativeInit(WebContents webContents);

    private native void nativeResetScrollAndScaleState(long j);

    private native void nativeUpdateAcceptLanguages(long j);

    private native void nativeUpdateEverythingLocked(long j);

    private native void nativeUpdateFormDataPreferences(long j);

    private native void nativeUpdateInitialPageScale(long j);

    private native void nativeUpdateUserAgent(long j);

    private native void nativeUpdateWebkitPreferences(long j);

    static {
        boolean z;
        if (XWalkSettingsInternal.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
    }

    XWalkSettingsInternal() {
        this.mXWalkSettingsLock = new Object();
        this.mAllowScriptsToCloseWindows = true;
        this.mLoadsImagesAutomatically = true;
        this.mImagesEnabled = true;
        this.mJavaScriptEnabled = true;
        this.mAllowUniversalAccessFromFileURLs = false;
        this.mAllowFileAccessFromFileURLs = false;
        this.mJavaScriptCanOpenWindowsAutomatically = true;
        this.mCacheMode = -1;
        this.mSupportMultipleWindows = false;
        this.mAppCacheEnabled = true;
        this.mDomStorageEnabled = true;
        this.mDatabaseEnabled = true;
        this.mUseWideViewport = false;
        this.mLoadWithOverviewMode = false;
        this.mMediaPlaybackRequiresUserGesture = false;
        this.mAllowContentUrlAccess = true;
        this.mAllowFileUrlAccess = true;
        this.mShouldFocusFirstNode = true;
        this.mGeolocationEnabled = true;
        this.mNativeXWalkSettings = 0;
        this.mIsUpdateWebkitPrefsMessagePending = false;
        this.mDefaultFontSize = 16;
        this.mDefaultFixedFontSize = 13;
        this.mAutoCompleteEnabled = true;
        this.mInitialPageScalePercent = 0.0f;
        this.mDIPScale = MediaSessionDelegate.DEFAULT_VOLUME_MULTIPLIER;
        this.mTextSizePercent = 100;
        this.mSupportZoom = true;
        this.mBuiltInZoomControls = false;
        this.mDisplayZoomControls = true;
        this.mSpatialNavigationEnabled = true;
        this.mQuirksModeEnabled = false;
        this.mLayoutAlgorithm = LayoutAlgorithmInternal.NARROW_COLUMNS;
        this.mContext = null;
        this.mEventHandler = null;
        this.mPasswordEchoEnabled = false;
    }

    XWalkSettingsInternal(Context context, WebContents webContents, boolean isAccessFromFileURLsGrantedByDefault) {
        boolean z = true;
        this.mXWalkSettingsLock = new Object();
        this.mAllowScriptsToCloseWindows = true;
        this.mLoadsImagesAutomatically = true;
        this.mImagesEnabled = true;
        this.mJavaScriptEnabled = true;
        this.mAllowUniversalAccessFromFileURLs = false;
        this.mAllowFileAccessFromFileURLs = false;
        this.mJavaScriptCanOpenWindowsAutomatically = true;
        this.mCacheMode = -1;
        this.mSupportMultipleWindows = false;
        this.mAppCacheEnabled = true;
        this.mDomStorageEnabled = true;
        this.mDatabaseEnabled = true;
        this.mUseWideViewport = false;
        this.mLoadWithOverviewMode = false;
        this.mMediaPlaybackRequiresUserGesture = false;
        this.mAllowContentUrlAccess = true;
        this.mAllowFileUrlAccess = true;
        this.mShouldFocusFirstNode = true;
        this.mGeolocationEnabled = true;
        this.mNativeXWalkSettings = 0;
        this.mIsUpdateWebkitPrefsMessagePending = false;
        this.mDefaultFontSize = 16;
        this.mDefaultFixedFontSize = 13;
        this.mAutoCompleteEnabled = true;
        this.mInitialPageScalePercent = 0.0f;
        this.mDIPScale = MediaSessionDelegate.DEFAULT_VOLUME_MULTIPLIER;
        this.mTextSizePercent = 100;
        this.mSupportZoom = true;
        this.mBuiltInZoomControls = false;
        this.mDisplayZoomControls = true;
        this.mSpatialNavigationEnabled = true;
        this.mQuirksModeEnabled = false;
        this.mLayoutAlgorithm = LayoutAlgorithmInternal.NARROW_COLUMNS;
        ThreadUtils.assertOnUiThread();
        this.mContext = context;
        this.mBlockNetworkLoads = this.mContext.checkPermission("android.permission.INTERNET", Process.myPid(), Process.myUid()) != 0;
        if (isAccessFromFileURLsGrantedByDefault) {
            this.mAllowUniversalAccessFromFileURLs = true;
            this.mAllowFileAccessFromFileURLs = true;
        }
        this.mUserAgent = LazyDefaultUserAgent.sInstance;
        if (System.getInt(context.getContentResolver(), "show_password", 1) != 1) {
            z = false;
        }
        this.mPasswordEchoEnabled = z;
        this.mEventHandler = new EventHandler();
        setWebContents(webContents);
    }

    void setWebContents(WebContents webContents) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mNativeXWalkSettings != 0) {
                nativeDestroy(this.mNativeXWalkSettings);
                if (!($assertionsDisabled || this.mNativeXWalkSettings == 0)) {
                    throw new AssertionError();
                }
            }
            if (webContents != null) {
                this.mEventHandler.bindUiThread();
                this.mNativeXWalkSettings = nativeInit(webContents);
                nativeUpdateEverythingLocked(this.mNativeXWalkSettings);
            }
        }
    }

    @CalledByNative
    private void nativeXWalkSettingsGone(long nativeXWalkSettings) {
        if ($assertionsDisabled || (this.mNativeXWalkSettings != 0 && this.mNativeXWalkSettings == nativeXWalkSettings)) {
            this.mNativeXWalkSettings = 0;
            return;
        }
        throw new AssertionError();
    }

    public void setAllowScriptsToCloseWindows(boolean allow) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAllowScriptsToCloseWindows != allow) {
                this.mAllowScriptsToCloseWindows = allow;
            }
        }
    }

    public boolean getAllowScriptsToCloseWindows() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mAllowScriptsToCloseWindows;
        }
        return z;
    }

    @XWalkAPI
    public void setCacheMode(int mode) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mCacheMode != mode) {
                this.mCacheMode = mode;
            }
        }
    }

    @XWalkAPI
    public int getCacheMode() {
        int i;
        synchronized (this.mXWalkSettingsLock) {
            i = this.mCacheMode;
        }
        return i;
    }

    @XWalkAPI
    public void setBlockNetworkLoads(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (!flag) {
                if (this.mContext.checkPermission("android.permission.INTERNET", Process.myPid(), Process.myUid()) != 0) {
                    throw new SecurityException("Permission denied - application missing INTERNET permission");
                }
            }
            this.mBlockNetworkLoads = flag;
        }
    }

    @XWalkAPI
    public boolean getBlockNetworkLoads() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mBlockNetworkLoads;
        }
        return z;
    }

    @XWalkAPI
    public void setAllowFileAccess(boolean allow) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAllowFileUrlAccess != allow) {
                this.mAllowFileUrlAccess = allow;
            }
        }
    }

    @XWalkAPI
    public boolean getAllowFileAccess() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mAllowFileUrlAccess;
        }
        return z;
    }

    @XWalkAPI
    public void setAllowContentAccess(boolean allow) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAllowContentUrlAccess != allow) {
                this.mAllowContentUrlAccess = allow;
            }
        }
    }

    @XWalkAPI
    public boolean getAllowContentAccess() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mAllowContentUrlAccess;
        }
        return z;
    }

    public void setGeolocationEnabled(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mGeolocationEnabled != flag) {
                this.mGeolocationEnabled = flag;
            }
        }
    }

    boolean getGeolocationEnabled() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mGeolocationEnabled;
        }
        return z;
    }

    @XWalkAPI
    public void setJavaScriptEnabled(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mJavaScriptEnabled != flag) {
                this.mJavaScriptEnabled = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @XWalkAPI
    public void setAllowUniversalAccessFromFileURLs(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAllowUniversalAccessFromFileURLs != flag) {
                this.mAllowUniversalAccessFromFileURLs = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @XWalkAPI
    public void setAllowFileAccessFromFileURLs(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAllowFileAccessFromFileURLs != flag) {
                this.mAllowFileAccessFromFileURLs = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @XWalkAPI
    public void setLoadsImagesAutomatically(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mLoadsImagesAutomatically != flag) {
                this.mLoadsImagesAutomatically = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @XWalkAPI
    public boolean getLoadsImagesAutomatically() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mLoadsImagesAutomatically;
        }
        return z;
    }

    @XWalkAPI
    public void setBlockNetworkImage(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mImagesEnabled == flag) {
                this.mImagesEnabled = !flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @XWalkAPI
    public boolean getBlockNetworkImage() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = !this.mImagesEnabled;
        }
        return z;
    }

    @XWalkAPI
    public boolean getJavaScriptEnabled() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mJavaScriptEnabled;
        }
        return z;
    }

    @XWalkAPI
    public boolean getAllowUniversalAccessFromFileURLs() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mAllowUniversalAccessFromFileURLs;
        }
        return z;
    }

    @XWalkAPI
    public boolean getAllowFileAccessFromFileURLs() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mAllowFileAccessFromFileURLs;
        }
        return z;
    }

    @XWalkAPI
    public void setJavaScriptCanOpenWindowsAutomatically(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mJavaScriptCanOpenWindowsAutomatically != flag) {
                this.mJavaScriptCanOpenWindowsAutomatically = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @XWalkAPI
    public boolean getJavaScriptCanOpenWindowsAutomatically() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mJavaScriptCanOpenWindowsAutomatically;
        }
        return z;
    }

    @XWalkAPI
    public void setSupportMultipleWindows(boolean support) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mSupportMultipleWindows != support) {
                this.mSupportMultipleWindows = support;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @XWalkAPI
    public boolean supportMultipleWindows() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mSupportMultipleWindows;
        }
        return z;
    }

    @XWalkAPI
    public void setUseWideViewPort(boolean use) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mUseWideViewport != use) {
                this.mUseWideViewport = use;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @XWalkAPI
    public boolean getUseWideViewPort() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mUseWideViewport;
        }
        return z;
    }

    public void setAppCacheEnabled(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAppCacheEnabled != flag) {
                this.mAppCacheEnabled = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public void setAppCachePath(String path) {
        boolean needToSync = false;
        synchronized (sGlobalContentSettingsLock) {
            if (!(sAppCachePathIsSet || path == null || path.isEmpty())) {
                sAppCachePathIsSet = true;
                needToSync = true;
            }
        }
        if (needToSync) {
            synchronized (this.mXWalkSettingsLock) {
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @CalledByNative
    private boolean getAppCacheEnabled() {
        return this.mAppCacheEnabled;
    }

    @XWalkAPI
    public void setDomStorageEnabled(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mDomStorageEnabled != flag) {
                this.mDomStorageEnabled = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @XWalkAPI
    public boolean getDomStorageEnabled() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mDomStorageEnabled;
        }
        return z;
    }

    @XWalkAPI
    public void setDatabaseEnabled(boolean flag) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mDatabaseEnabled != flag) {
                this.mDatabaseEnabled = flag;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @XWalkAPI
    public boolean getDatabaseEnabled() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mDatabaseEnabled;
        }
        return z;
    }

    @XWalkAPI
    public void setMediaPlaybackRequiresUserGesture(boolean require) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mMediaPlaybackRequiresUserGesture != require) {
                this.mMediaPlaybackRequiresUserGesture = require;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    @XWalkAPI
    public boolean getMediaPlaybackRequiresUserGesture() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mMediaPlaybackRequiresUserGesture;
        }
        return z;
    }

    public void setDefaultVideoPosterURL(String url) {
        synchronized (this.mXWalkSettingsLock) {
            if (!(this.mDefaultVideoPosterURL == null || this.mDefaultVideoPosterURL.equals(url)) || (this.mDefaultVideoPosterURL == null && url != null)) {
                this.mDefaultVideoPosterURL = url;
                this.mEventHandler.updateWebkitPreferencesLocked();
            }
        }
    }

    public static String getDefaultUserAgent() {
        return LazyDefaultUserAgent.sInstance;
    }

    @XWalkAPI
    public void setUserAgentString(String userAgent) {
        synchronized (this.mXWalkSettingsLock) {
            String oldUserAgent = this.mUserAgent;
            if (userAgent == null || userAgent.length() == 0) {
                this.mUserAgent = LazyDefaultUserAgent.sInstance;
            } else {
                this.mUserAgent = userAgent;
            }
            if (!oldUserAgent.equals(this.mUserAgent)) {
                this.mEventHandler.maybeRunOnUiThreadBlocking(new C03581());
            }
        }
    }

    @XWalkAPI
    public String getUserAgentString() {
        String str;
        synchronized (this.mXWalkSettingsLock) {
            str = this.mUserAgent;
        }
        return str;
    }

    @CalledByNative
    private String getUserAgentLocked() {
        return this.mUserAgent;
    }

    public String getDefaultVideoPosterURL() {
        String str;
        synchronized (this.mXWalkSettingsLock) {
            str = this.mDefaultVideoPosterURL;
        }
        return str;
    }

    @CalledByNative
    private void updateEverything() {
        synchronized (this.mXWalkSettingsLock) {
            nativeUpdateEverythingLocked(this.mNativeXWalkSettings);
        }
    }

    private void updateWebkitPreferencesOnUiThread() {
        if (this.mNativeXWalkSettings != 0) {
            ThreadUtils.assertOnUiThread();
            nativeUpdateWebkitPreferences(this.mNativeXWalkSettings);
        }
    }

    @XWalkAPI
    public void setAcceptLanguages(String acceptLanguages) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAcceptLanguages == acceptLanguages) {
                return;
            }
            this.mAcceptLanguages = acceptLanguages;
            this.mEventHandler.maybeRunOnUiThreadBlocking(new C03592());
        }
    }

    @XWalkAPI
    public String getAcceptLanguages() {
        String str;
        synchronized (this.mXWalkSettingsLock) {
            str = this.mAcceptLanguages;
        }
        return str;
    }

    @XWalkAPI
    public void setSaveFormData(boolean enable) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mAutoCompleteEnabled == enable) {
                return;
            }
            this.mAutoCompleteEnabled = enable;
            this.mEventHandler.maybeRunOnUiThreadBlocking(new C03603());
        }
    }

    @XWalkAPI
    public boolean getSaveFormData() {
        boolean saveFormDataLocked;
        synchronized (this.mXWalkSettingsLock) {
            saveFormDataLocked = getSaveFormDataLocked();
        }
        return saveFormDataLocked;
    }

    @CalledByNative
    private String getAcceptLanguagesLocked() {
        return this.mAcceptLanguages;
    }

    @CalledByNative
    private boolean getSaveFormDataLocked() {
        return this.mAutoCompleteEnabled;
    }

    void setDIPScale(double dipScale) {
        synchronized (this.mXWalkSettingsLock) {
            this.mDIPScale = dipScale;
        }
    }

    @XWalkAPI
    public void setInitialPageScale(float scaleInPercent) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mInitialPageScalePercent == scaleInPercent) {
                return;
            }
            this.mInitialPageScalePercent = scaleInPercent;
            this.mEventHandler.maybeRunOnUiThreadBlocking(new C03614());
        }
    }

    @CalledByNative
    private float getInitialPageScalePercentLocked() {
        if ($assertionsDisabled || Thread.holdsLock(this.mXWalkSettingsLock)) {
            return this.mInitialPageScalePercent;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private double getDIPScaleLocked() {
        if ($assertionsDisabled || Thread.holdsLock(this.mXWalkSettingsLock)) {
            return this.mDIPScale;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private boolean getPasswordEchoEnabledLocked() {
        if ($assertionsDisabled || Thread.holdsLock(this.mXWalkSettingsLock)) {
            return this.mPasswordEchoEnabled;
        }
        throw new AssertionError();
    }

    @XWalkAPI
    public void setTextZoom(int textZoom) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mTextSizePercent == textZoom) {
                return;
            }
            this.mTextSizePercent = textZoom;
            this.mEventHandler.updateWebkitPreferencesLocked();
        }
    }

    @XWalkAPI
    public int getTextZoom() {
        int i;
        synchronized (this.mXWalkSettingsLock) {
            i = this.mTextSizePercent;
        }
        return i;
    }

    private int clipFontSize(int size) {
        if (size < 1) {
            return 1;
        }
        return size > MAXIMUM_FONT_SIZE ? MAXIMUM_FONT_SIZE : size;
    }

    @XWalkAPI
    public void setDefaultFontSize(int size) {
        synchronized (this.mXWalkSettingsLock) {
            size = clipFontSize(size);
            if (this.mDefaultFontSize == size) {
                return;
            }
            this.mDefaultFontSize = size;
            this.mEventHandler.updateWebkitPreferencesLocked();
        }
    }

    @XWalkAPI
    public int getDefaultFontSize() {
        int i;
        synchronized (this.mXWalkSettingsLock) {
            i = this.mDefaultFontSize;
        }
        return i;
    }

    @XWalkAPI
    public void setDefaultFixedFontSize(int size) {
        synchronized (this.mXWalkSettingsLock) {
            size = clipFontSize(size);
            if (this.mDefaultFixedFontSize == size) {
                return;
            }
            this.mDefaultFixedFontSize = size;
            this.mEventHandler.updateWebkitPreferencesLocked();
        }
    }

    @XWalkAPI
    public int getDefaultFixedFontSize() {
        int i;
        synchronized (this.mXWalkSettingsLock) {
            i = this.mDefaultFixedFontSize;
        }
        return i;
    }

    void setZoomListener(ZoomSupportChangeListener zoomChangeListener) {
        synchronized (this.mXWalkSettingsLock) {
            this.mZoomChangeListener = zoomChangeListener;
        }
    }

    private void onGestureZoomSupportChanged(final boolean supportsDoubleTapZoom, final boolean supportsMultiTouchZoom) {
        this.mEventHandler.maybePostOnUiThread(new Runnable() {
            public void run() {
                synchronized (XWalkSettingsInternal.this.mXWalkSettingsLock) {
                    if (XWalkSettingsInternal.this.mZoomChangeListener == null) {
                        return;
                    }
                    XWalkSettingsInternal.this.mZoomChangeListener.onGestureZoomSupportChanged(supportsDoubleTapZoom, supportsMultiTouchZoom);
                }
            }
        });
    }

    @CalledByNative
    private boolean supportsDoubleTapZoomLocked() {
        if ($assertionsDisabled || Thread.holdsLock(this.mXWalkSettingsLock)) {
            return this.mSupportZoom && this.mBuiltInZoomControls && this.mUseWideViewport;
        } else {
            throw new AssertionError();
        }
    }

    private boolean supportsMultiTouchZoomLocked() {
        if ($assertionsDisabled || Thread.holdsLock(this.mXWalkSettingsLock)) {
            return this.mSupportZoom && this.mBuiltInZoomControls;
        } else {
            throw new AssertionError();
        }
    }

    @XWalkAPI
    public void setSupportZoom(boolean support) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mSupportZoom == support) {
                return;
            }
            this.mSupportZoom = support;
            onGestureZoomSupportChanged(supportsDoubleTapZoomLocked(), supportsMultiTouchZoomLocked());
        }
    }

    @XWalkAPI
    public boolean supportZoom() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mSupportZoom;
        }
        return z;
    }

    @XWalkAPI
    public void setBuiltInZoomControls(boolean enabled) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mBuiltInZoomControls == enabled) {
                return;
            }
            this.mBuiltInZoomControls = enabled;
            onGestureZoomSupportChanged(supportsDoubleTapZoomLocked(), supportsMultiTouchZoomLocked());
        }
    }

    @XWalkAPI
    public boolean getBuiltInZoomControls() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mBuiltInZoomControls;
        }
        return z;
    }

    @XWalkAPI
    public boolean supportsMultiTouchZoomForTest() {
        boolean supportsMultiTouchZoomLocked;
        synchronized (this.mXWalkSettingsLock) {
            supportsMultiTouchZoomLocked = supportsMultiTouchZoomLocked();
        }
        return supportsMultiTouchZoomLocked;
    }

    @XWalkAPI
    public void setSupportSpatialNavigation(boolean enable) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mSpatialNavigationEnabled == enable) {
                return;
            }
            this.mSpatialNavigationEnabled = enable;
            this.mEventHandler.updateWebkitPreferencesLocked();
        }
    }

    @XWalkAPI
    public boolean getSupportSpatialNavigation() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mSpatialNavigationEnabled;
        }
        return z;
    }

    @XWalkAPI
    public void setSupportQuirksMode(boolean enable) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mQuirksModeEnabled == enable) {
                return;
            }
            this.mQuirksModeEnabled = enable;
            this.mEventHandler.updateWebkitPreferencesLocked();
        }
    }

    @XWalkAPI
    public boolean getSupportQuirksMode() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mQuirksModeEnabled;
        }
        return z;
    }

    @XWalkAPI
    public void setLayoutAlgorithm(LayoutAlgorithmInternal la) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mLayoutAlgorithm == la) {
                return;
            }
            this.mLayoutAlgorithm = la;
            this.mEventHandler.updateWebkitPreferencesLocked();
        }
    }

    @XWalkAPI
    public LayoutAlgorithmInternal getLayoutAlgorithm() {
        LayoutAlgorithmInternal layoutAlgorithmInternal;
        synchronized (this.mXWalkSettingsLock) {
            layoutAlgorithmInternal = this.mLayoutAlgorithm;
        }
        return layoutAlgorithmInternal;
    }

    @CalledByNative
    private boolean getTextAutosizingEnabledLocked() {
        if ($assertionsDisabled || Thread.holdsLock(this.mXWalkSettingsLock)) {
            return this.mLayoutAlgorithm == LayoutAlgorithmInternal.TEXT_AUTOSIZING;
        } else {
            throw new AssertionError();
        }
    }

    @XWalkAPI
    public void setLoadWithOverviewMode(boolean overview) {
        synchronized (this.mXWalkSettingsLock) {
            if (this.mLoadWithOverviewMode == overview) {
                return;
            }
            this.mLoadWithOverviewMode = overview;
            this.mEventHandler.maybeRunOnUiThreadBlocking(new C03636());
        }
    }

    @XWalkAPI
    public boolean getLoadWithOverviewMode() {
        boolean z;
        synchronized (this.mXWalkSettingsLock) {
            z = this.mLoadWithOverviewMode;
        }
        return z;
    }
}
