package org.chromium.content.browser;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStructure;
import android.view.WindowManager.BadTokenException;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.base.CommandLine;
import org.chromium.base.Log;
import org.chromium.base.ObserverList;
import org.chromium.base.ObserverList.RewindableIterator;
import org.chromium.base.TraceEvent;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.metrics.RecordUserAction;
import org.chromium.content.C0174R;
import org.chromium.content.browser.PopupZoomer.OnTapListener;
import org.chromium.content.browser.PopupZoomer.OnVisibilityChangedListener;
import org.chromium.content.browser.ScreenOrientationListener.ScreenOrientationObserver;
import org.chromium.content.browser.WebActionModeCallback.ActionHandler;
import org.chromium.content.browser.accessibility.BrowserAccessibilityManager;
import org.chromium.content.browser.accessibility.captioning.CaptioningBridgeFactory;
import org.chromium.content.browser.accessibility.captioning.SystemCaptioningBridge;
import org.chromium.content.browser.accessibility.captioning.SystemCaptioningBridge.SystemCaptioningBridgeListener;
import org.chromium.content.browser.accessibility.captioning.TextTrackSettings;
import org.chromium.content.browser.input.AnimationIntervalProvider;
import org.chromium.content.browser.input.FloatingPastePopupMenu;
import org.chromium.content.browser.input.ImeAdapter;
import org.chromium.content.browser.input.ImeAdapter.ImeAdapterDelegate;
import org.chromium.content.browser.input.InputMethodManagerWrapper;
import org.chromium.content.browser.input.JoystickScrollProvider;
import org.chromium.content.browser.input.JoystickZoomProvider;
import org.chromium.content.browser.input.LegacyPastePopupMenu;
import org.chromium.content.browser.input.PastePopupMenu;
import org.chromium.content.browser.input.PastePopupMenu.PastePopupMenuDelegate;
import org.chromium.content.browser.input.SelectPopup;
import org.chromium.content.browser.input.SelectPopupDialog;
import org.chromium.content.browser.input.SelectPopupDropdown;
import org.chromium.content.browser.input.SelectPopupItem;
import org.chromium.content.common.ContentSwitches;
import org.chromium.content_public.browser.AccessibilitySnapshotCallback;
import org.chromium.content_public.browser.AccessibilitySnapshotNode;
import org.chromium.content_public.browser.GestureStateListener;
import org.chromium.content_public.browser.WebContents;
import org.chromium.content_public.browser.WebContentsObserver;
import org.chromium.device.gamepad.GamepadList;
import org.chromium.ui.base.DeviceFormFactor;
import org.chromium.ui.base.PageTransition;
import org.chromium.ui.base.ViewAndroidDelegate;
import org.chromium.ui.base.WindowAndroid;
import org.chromium.ui.base.WindowAndroid.IntentCallback;
import org.chromium.ui.gfx.DeviceDisplayInfo;

@JNINamespace("content")
public class ContentViewCore implements AccessibilityStateChangeListener, ScreenOrientationObserver, SystemCaptioningBridgeListener {
    static final /* synthetic */ boolean $assertionsDisabled = (!ContentViewCore.class.desiredAssertionStatus());
    public static final int INVALID_RENDER_PROCESS_PID = 0;
    private static final ZoomControlsDelegate NO_OP_ZOOM_CONTROLS_DELEGATE = new C04361();
    private static final String TAG = "cr.ContentViewCore";
    private static final float ZOOM_CONTROLS_EPSILON = 0.007f;
    private final AccessibilityManager mAccessibilityManager;
    private ActionHandler mActionHandler;
    private WebActionMode mActionMode;
    private BrowserAccessibilityManager mBrowserAccessibilityManager;
    private ViewGroup mContainerView;
    private InternalAccessDelegate mContainerViewInternals;
    private final ObserverList<ContainerViewObserver> mContainerViewObservers;
    private ContentViewClient mContentViewClient;
    private final Context mContext;
    private ContextualSearchClient mContextualSearchClient;
    private float mCurrentTouchOffsetX;
    private float mCurrentTouchOffsetY;
    private boolean mDraggingSelection;
    private Boolean mEnableTouchHover;
    private Runnable mFakeMouseMoveRunnable = null;
    private boolean mFloatingActionModeCreationFailed;
    private final Rect mFocusPreOSKViewportRect = new Rect();
    private boolean mFocusedNodeEditable;
    private boolean mFocusedNodeIsPassword;
    private boolean mFullscreenRequiredForOrientationLock = true;
    private final ObserverList<GestureStateListener> mGestureStateListeners;
    private final RewindableIterator<GestureStateListener> mGestureStateListenersIterator;
    private boolean mHasInsertion;
    private boolean mHasSelection;
    private ImeAdapter mImeAdapter;
    private boolean mIsMobileOptimizedHint;
    private boolean mIsObscuredByAnotherView;
    private final Map<String, Pair<Object, Class>> mJavaScriptInterfaces = new HashMap();
    private final JoystickScrollProvider mJoystickScrollProvider;
    private JoystickZoomProvider mJoystickZoomProvider;
    private float mLastFocalEventX;
    private float mLastFocalEventY;
    private String mLastSelectedText;
    private boolean mNativeAccessibilityAllowed;
    private boolean mNativeAccessibilityEnabled;
    private long mNativeContentViewCore = 0;
    private long mNativeSelectPopupSourceFrame = 0;
    private OverscrollRefreshHandler mOverscrollRefreshHandler;
    private PastePopupMenu mPastePopupMenu;
    private int mPhysicalBackingHeightPix;
    private int mPhysicalBackingWidthPix;
    private PopupZoomer mPopupZoomer;
    private int mPotentiallyActiveFlingCount;
    private boolean mPreserveSelectionOnNextLossOfFocus;
    private final RenderCoordinates mRenderCoordinates;
    private final HashSet<Object> mRetainedJavaScriptObjects = new HashSet();
    private SelectPopup mSelectPopup;
    private final Rect mSelectionRect = new Rect();
    private boolean mShouldSetAccessibilityFocusOnPageLoad;
    private ShowKeyboardResultReceiver mShowKeyboardResultReceiver;
    private SmartClipDataListener mSmartClipDataListener = null;
    private int mSmartClipOffsetX;
    private int mSmartClipOffsetY;
    private final SystemCaptioningBridge mSystemCaptioningBridge;
    private int mTopControlsHeightPix;
    private boolean mTopControlsShrinkBlinkSize;
    private boolean mTouchExplorationEnabled;
    private boolean mTouchScrollInProgress;
    private boolean mUnselectAllOnActionModeDismiss;
    private ContentViewAndroidDelegate mViewAndroidDelegate;
    private int mViewportHeightPix;
    private int mViewportWidthPix;
    private boolean mWasPastePopupShowingOnInsertionDragStart;
    private WebContents mWebContents;
    private WebContentsObserver mWebContentsObserver;
    private float mWheelScrollFactorInPixels;
    private ZoomControlsDelegate mZoomControlsDelegate;

    public interface InternalAccessDelegate {
        boolean awakenScrollBars();

        void onScrollChanged(int i, int i2, int i3, int i4);

        boolean super_awakenScrollBars(int i, boolean z);

        boolean super_dispatchKeyEvent(KeyEvent keyEvent);

        void super_onConfigurationChanged(Configuration configuration);

        boolean super_onGenericMotionEvent(MotionEvent motionEvent);

        boolean super_onKeyUp(int i, KeyEvent keyEvent);
    }

    private static class ShowKeyboardResultReceiver extends ResultReceiver {
        private final WeakReference<ContentViewCore> mContentViewCore;

        public ShowKeyboardResultReceiver(ContentViewCore contentViewCore, Handler handler) {
            super(handler);
            this.mContentViewCore = new WeakReference(contentViewCore);
        }

        public void onReceiveResult(int resultCode, Bundle resultData) {
            ContentViewCore contentViewCore = (ContentViewCore) this.mContentViewCore.get();
            if (contentViewCore != null) {
                contentViewCore.onShowKeyboardReceiveResult(resultCode);
            }
        }
    }

    public interface SmartClipDataListener {
        void onSmartClipDataExtracted(String str, String str2, Rect rect);
    }

    public interface ZoomControlsDelegate {
        void dismissZoomPicker();

        void invokeZoomPicker();

        void updateZoomControls();
    }

    static class C04361 implements ZoomControlsDelegate {
        C04361() {
        }

        public void invokeZoomPicker() {
        }

        public void dismissZoomPicker() {
        }

        public void updateZoomControls() {
        }
    }

    class C04372 implements ImeAdapterDelegate {
        static final /* synthetic */ boolean $assertionsDisabled = (!ContentViewCore.class.desiredAssertionStatus());

        C04372() {
        }

        public void onImeEvent() {
            ContentViewCore.this.mPopupZoomer.hide(true);
            ContentViewCore.this.getContentViewClient().onImeEvent();
            if (ContentViewCore.this.mFocusedNodeEditable) {
                ContentViewCore.this.dismissTextHandles();
            }
        }

        public void onKeyboardBoundsUnchanged() {
            if ($assertionsDisabled || ContentViewCore.this.mWebContents != null) {
                ContentViewCore.this.mWebContents.scrollFocusedEditableNodeIntoView();
                return;
            }
            throw new AssertionError();
        }

        public boolean performContextMenuAction(int id) {
            if ($assertionsDisabled || ContentViewCore.this.mWebContents != null) {
                switch (id) {
                    case 16908319:
                        ContentViewCore.this.mWebContents.selectAll();
                        return true;
                    case 16908320:
                        ContentViewCore.this.mWebContents.cut();
                        return true;
                    case 16908321:
                        ContentViewCore.this.mWebContents.copy();
                        return true;
                    case 16908322:
                        ContentViewCore.this.mWebContents.paste();
                        return true;
                    default:
                        return false;
                }
            }
            throw new AssertionError();
        }

        public View getAttachedView() {
            return ContentViewCore.this.mContainerView;
        }

        public ResultReceiver getNewShowKeyboardReceiver() {
            return ContentViewCore.this.getNewShowKeyboardReceiver();
        }
    }

    class C04383 implements OnVisibilityChangedListener {
        private final ViewGroup mContainerViewAtCreation = ContentViewCore.this.mContainerView;

        C04383() {
        }

        public void onPopupZoomerShown(final PopupZoomer zoomer) {
            this.mContainerViewAtCreation.post(new Runnable() {
                public void run() {
                    if (C04383.this.mContainerViewAtCreation.indexOfChild(zoomer) == -1) {
                        C04383.this.mContainerViewAtCreation.addView(zoomer);
                    }
                }
            });
        }

        public void onPopupZoomerHidden(final PopupZoomer zoomer) {
            this.mContainerViewAtCreation.post(new Runnable() {
                public void run() {
                    if (C04383.this.mContainerViewAtCreation.indexOfChild(zoomer) != -1) {
                        C04383.this.mContainerViewAtCreation.removeView(zoomer);
                        C04383.this.mContainerViewAtCreation.invalidate();
                    }
                }
            });
        }
    }

    class C04394 implements OnTapListener {
        private final ViewGroup mContainerViewAtCreation = ContentViewCore.this.mContainerView;

        C04394() {
        }

        public boolean onSingleTap(View v, MotionEvent e) {
            this.mContainerViewAtCreation.requestFocus();
            if (ContentViewCore.this.mNativeContentViewCore != 0) {
                ContentViewCore.this.nativeSingleTap(ContentViewCore.this.mNativeContentViewCore, e.getEventTime(), e.getX(), e.getY());
            }
            return true;
        }

        public boolean onLongPress(View v, MotionEvent e) {
            if (ContentViewCore.this.mNativeContentViewCore != 0) {
                ContentViewCore.this.nativeLongPress(ContentViewCore.this.mNativeContentViewCore, e.getEventTime(), e.getX(), e.getY());
            }
            return true;
        }
    }

    class C04416 implements ActionHandler {
        static final /* synthetic */ boolean $assertionsDisabled = (!ContentViewCore.class.desiredAssertionStatus());
        private static final int MAX_SEARCH_QUERY_LENGTH = 1000;
        private static final int MAX_SHARE_QUERY_LENGTH = 100000;

        class C04401 implements IntentCallback {
            C04401() {
            }

            public void onIntentCompleted(WindowAndroid window, int resultCode, ContentResolver contentResolver, Intent data) {
                ContentViewCore.this.onReceivedProcessTextResult(resultCode, data);
            }
        }

        C04416() {
        }

        public void selectAll() {
            ContentViewCore.this.mWebContents.selectAll();
            if (ContentViewCore.this.isFocusedNodeEditable()) {
                RecordUserAction.record("MobileActionMode.SelectAllWasEditable");
            } else {
                RecordUserAction.record("MobileActionMode.SelectAllWasNonEditable");
            }
        }

        public void cut() {
            ContentViewCore.this.mWebContents.cut();
        }

        public void copy() {
            ContentViewCore.this.mWebContents.copy();
        }

        public void paste() {
            ContentViewCore.this.mWebContents.paste();
        }

        public void share() {
            RecordUserAction.record("MobileActionMode.Share");
            String query = sanitizeQuery(ContentViewCore.this.getSelectedText(), MAX_SHARE_QUERY_LENGTH);
            if (!TextUtils.isEmpty(query)) {
                Intent send = new Intent("android.intent.action.SEND");
                send.setType("text/plain");
                send.putExtra("android.intent.extra.TEXT", query);
                try {
                    Intent i = Intent.createChooser(send, ContentViewCore.this.getContext().getString(C0174R.string.actionbar_share));
                    i.setFlags(PageTransition.CHAIN_START);
                    ContentViewCore.this.getContext().startActivity(i);
                } catch (ActivityNotFoundException e) {
                }
            }
        }

        public void processText(Intent intent) {
            RecordUserAction.record("MobileActionMode.ProcessTextIntent");
            if ($assertionsDisabled || VERSION.SDK_INT >= 23) {
                String query = sanitizeQuery(ContentViewCore.this.getSelectedText(), 1000);
                if (!TextUtils.isEmpty(query)) {
                    intent.putExtra("android.intent.extra.PROCESS_TEXT", query);
                    try {
                        if (ContentViewCore.this.getContentViewClient().doesPerformProcessText()) {
                            ContentViewCore.this.getContentViewClient().startProcessTextIntent(intent);
                            return;
                        } else {
                            ContentViewCore.this.getWindowAndroid().showIntent(intent, new C04401(), null);
                            return;
                        }
                    } catch (ActivityNotFoundException e) {
                        return;
                    }
                }
                return;
            }
            throw new AssertionError();
        }

        public void search() {
            RecordUserAction.record("MobileActionMode.WebSearch");
            String query = sanitizeQuery(ContentViewCore.this.getSelectedText(), 1000);
            if (!TextUtils.isEmpty(query)) {
                if (ContentViewCore.this.getContentViewClient().doesPerformWebSearch()) {
                    ContentViewCore.this.getContentViewClient().performWebSearch(query);
                    return;
                }
                Intent i = new Intent("android.intent.action.WEB_SEARCH");
                i.putExtra("new_search", true);
                i.putExtra("query", query);
                i.putExtra("com.android.browser.application_id", ContentViewCore.this.getContext().getPackageName());
                i.addFlags(PageTransition.CHAIN_START);
                try {
                    ContentViewCore.this.getContext().startActivity(i);
                } catch (ActivityNotFoundException e) {
                }
            }
        }

        public boolean isSelectionPassword() {
            return ContentViewCore.this.mFocusedNodeIsPassword;
        }

        public boolean isSelectionEditable() {
            return ContentViewCore.this.mFocusedNodeEditable;
        }

        public boolean isInsertion() {
            return ContentViewCore.this.mHasInsertion;
        }

        public void onDestroyActionMode() {
            ContentViewCore.this.mActionMode = null;
            if (ContentViewCore.this.mUnselectAllOnActionModeDismiss) {
                ContentViewCore.this.dismissTextHandles();
                ContentViewCore.this.clearSelection();
            }
            if (!ContentViewCore.this.supportsFloatingActionMode()) {
                ContentViewCore.this.getContentViewClient().onContextualActionBarHidden();
            }
        }

        public void onGetContentRect(Rect outRect) {
            outRect.set(ContentViewCore.this.mSelectionRect);
            outRect.offset(0, (int) ContentViewCore.this.mRenderCoordinates.getContentOffsetYPix());
        }

        public boolean isIncognito() {
            return ContentViewCore.this.mWebContents.isIncognito();
        }

        public boolean isSelectActionModeAllowed(int actionModeItem) {
            boolean isAllowedByClient = ContentViewCore.this.getContentViewClient().isSelectActionModeAllowed(actionModeItem);
            if (actionModeItem == 1) {
                if (isAllowedByClient && isShareAvailable()) {
                    return true;
                }
                return false;
            } else if (actionModeItem != 2) {
                return isAllowedByClient;
            } else {
                if (isAllowedByClient && isWebSearchAvailable()) {
                    return true;
                }
                return false;
            }
        }

        private boolean isShareAvailable() {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            return ContentViewCore.this.getContext().getPackageManager().queryIntentActivities(intent, 65536).size() > 0;
        }

        private boolean isWebSearchAvailable() {
            if (ContentViewCore.this.getContentViewClient().doesPerformWebSearch()) {
                return true;
            }
            Intent intent = new Intent("android.intent.action.WEB_SEARCH");
            intent.putExtra("new_search", true);
            if (ContentViewCore.this.getContext().getPackageManager().queryIntentActivities(intent, 65536).size() <= 0) {
                return false;
            }
            return true;
        }

        private String sanitizeQuery(String query, int maxLength) {
            if (TextUtils.isEmpty(query) || query.length() < maxLength) {
                return query;
            }
            Log.m38w(ContentViewCore.TAG, "Truncating oversized query (" + query.length() + ").", new Object[0]);
            return query.substring(0, maxLength) + "…";
        }
    }

    class C04427 implements PastePopupMenuDelegate {
        C04427() {
        }

        public void paste() {
            ContentViewCore.this.mWebContents.paste();
            ContentViewCore.this.dismissTextHandles();
        }

        public void onDismiss() {
            if (ContentViewCore.this.mWebContents != null) {
                ContentViewCore.this.mWebContents.onContextMenuClosed();
            }
        }
    }

    private static class ContentViewAndroidDelegate implements ViewAndroidDelegate {
        static final /* synthetic */ boolean $assertionsDisabled = (!ContentViewCore.class.desiredAssertionStatus());
        private final Map<View, Position> mAnchorViews = new LinkedHashMap();
        private WeakReference<ViewGroup> mCurrentContainerView;
        private final RenderCoordinates mRenderCoordinates;

        @VisibleForTesting
        private static class Position {
            private final float mHeight;
            private final float mWidth;
            private final float mX;
            private final float mY;

            public Position(float x, float y, float width, float height) {
                this.mX = x;
                this.mY = y;
                this.mWidth = width;
                this.mHeight = height;
            }
        }

        ContentViewAndroidDelegate(ViewGroup containerView, RenderCoordinates renderCoordinates) {
            this.mRenderCoordinates = renderCoordinates;
            this.mCurrentContainerView = new WeakReference(containerView);
        }

        public View acquireAnchorView() {
            ViewGroup containerView = (ViewGroup) this.mCurrentContainerView.get();
            if (containerView == null) {
                return null;
            }
            View anchorView = new View(containerView.getContext());
            this.mAnchorViews.put(anchorView, null);
            containerView.addView(anchorView);
            return anchorView;
        }

        public void setAnchorViewPosition(View view, float x, float y, float width, float height) {
            this.mAnchorViews.put(view, new Position(x, y, width, height));
            doSetAnchorViewPosition(view, x, y, width, height);
        }

        private void doSetAnchorViewPosition(View view, float x, float y, float width, float height) {
            if (view.getParent() != null) {
                ViewParent containerView = (ViewGroup) this.mCurrentContainerView.get();
                if (containerView == null) {
                    return;
                }
                if ($assertionsDisabled || view.getParent() == containerView) {
                    float scale = (float) DeviceDisplayInfo.create(containerView.getContext()).getDIPScale();
                    int leftMargin = Math.round(x * scale);
                    int topMargin = Math.round(this.mRenderCoordinates.getContentOffsetYPix() + (y * scale));
                    int scaledWidth = Math.round(width * scale);
                    if (containerView instanceof FrameLayout) {
                        int startMargin;
                        if (ApiCompatibilityUtils.isLayoutRtl(containerView)) {
                            startMargin = containerView.getMeasuredWidth() - Math.round((width + x) * scale);
                        } else {
                            startMargin = leftMargin;
                        }
                        if (scaledWidth + startMargin > containerView.getWidth()) {
                            scaledWidth = containerView.getWidth() - startMargin;
                        }
                        LayoutParams lp = new LayoutParams(scaledWidth, Math.round(height * scale));
                        ApiCompatibilityUtils.setMarginStart(lp, startMargin);
                        lp.topMargin = topMargin;
                        view.setLayoutParams(lp);
                        return;
                    } else if (containerView instanceof AbsoluteLayout) {
                        view.setLayoutParams(new AbsoluteLayout.LayoutParams(scaledWidth, (int) (height * scale), leftMargin + this.mRenderCoordinates.getScrollXPixInt(), topMargin + this.mRenderCoordinates.getScrollYPixInt()));
                        return;
                    } else {
                        Log.m28e(ContentViewCore.TAG, "Unknown layout %s", containerView.getClass().getName());
                        return;
                    }
                }
                throw new AssertionError();
            }
        }

        public void releaseAnchorView(View anchorView) {
            this.mAnchorViews.remove(anchorView);
            ViewGroup containerView = (ViewGroup) this.mCurrentContainerView.get();
            if (containerView != null) {
                containerView.removeView(anchorView);
            }
        }

        void updateCurrentContainerView(ViewGroup containerView) {
            ViewGroup oldContainerView = (ViewGroup) this.mCurrentContainerView.get();
            this.mCurrentContainerView = new WeakReference(containerView);
            for (Entry<View, Position> entry : this.mAnchorViews.entrySet()) {
                View anchorView = (View) entry.getKey();
                Position position = (Position) entry.getValue();
                if (oldContainerView != null) {
                    oldContainerView.removeView(anchorView);
                }
                containerView.addView(anchorView);
                if (position != null) {
                    doSetAnchorViewPosition(anchorView, position.mX, position.mY, position.mWidth, position.mHeight);
                }
            }
        }
    }

    private static class ContentViewWebContentsObserver extends WebContentsObserver {
        private final WeakReference<ContentViewCore> mWeakContentViewCore;

        ContentViewWebContentsObserver(ContentViewCore contentViewCore) {
            super(contentViewCore.getWebContents());
            this.mWeakContentViewCore = new WeakReference(contentViewCore);
        }

        public void didFailLoad(boolean isProvisionalLoad, boolean isMainFrame, int errorCode, String description, String failingUrl, boolean wasIgnoredByHandler) {
            if (isProvisionalLoad) {
                determinedProcessVisibility();
            }
        }

        public void didNavigateMainFrame(String url, String baseUrl, boolean isNavigationToDifferentPage, boolean isFragmentNavigation, int statusCode) {
            if (isNavigationToDifferentPage) {
                resetPopupsAndInput();
            }
        }

        public void renderProcessGone(boolean wasOomProtected) {
            resetPopupsAndInput();
            ContentViewCore contentViewCore = (ContentViewCore) this.mWeakContentViewCore.get();
            if (contentViewCore != null) {
                contentViewCore.mImeAdapter.resetAndHideKeyboard();
            }
        }

        public void navigationEntryCommitted() {
            determinedProcessVisibility();
        }

        private void resetPopupsAndInput() {
            ContentViewCore contentViewCore = (ContentViewCore) this.mWeakContentViewCore.get();
            if (contentViewCore != null) {
                contentViewCore.mIsMobileOptimizedHint = false;
                contentViewCore.hidePopupsAndClearSelection();
                contentViewCore.resetScrollInProgress();
            }
        }

        private void determinedProcessVisibility() {
            ContentViewCore contentViewCore = (ContentViewCore) this.mWeakContentViewCore.get();
            if (contentViewCore != null) {
                ChildProcessLauncher.determinedVisibility(contentViewCore.getCurrentRenderProcessId());
            }
        }
    }

    private static class SystemAnimationIntervalProvider implements AnimationIntervalProvider {
        private SystemAnimationIntervalProvider() {
        }

        public long getLastAnimationFrameInterval() {
            return AnimationUtils.currentAnimationTimeMillis();
        }
    }

    private native void nativeAddJavascriptInterface(long j, Object obj, String str, Class cls);

    private native void nativeDismissTextHandles(long j);

    private native void nativeDoubleTap(long j, long j2, float f, float f2);

    private native void nativeExtractSmartClipData(long j, int i, int i2, int i3, int i4);

    private native void nativeFlingCancel(long j, long j2);

    private native void nativeFlingStart(long j, long j2, float f, float f2, float f3, float f4, boolean z);

    private static native ContentViewCore nativeFromWebContentsAndroid(WebContents webContents);

    private native int nativeGetCurrentRenderProcessId(long j);

    private native WindowAndroid nativeGetJavaWindowAndroid(long j);

    private native long nativeGetNativeImeAdapter(long j);

    private native WebContents nativeGetWebContentsAndroid(long j);

    private native long nativeInit(WebContents webContents, ViewAndroidDelegate viewAndroidDelegate, long j, HashSet<Object> hashSet);

    private native void nativeLongPress(long j, long j2, float f, float f2);

    private native void nativeOnJavaContentViewCoreDestroyed(long j);

    private native boolean nativeOnTouchEvent(long j, MotionEvent motionEvent, long j2, int i, int i2, int i3, int i4, float f, float f2, float f3, float f4, int i5, int i6, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, int i7, int i8, int i9, int i10, boolean z);

    private native void nativePinchBegin(long j, long j2, float f, float f2);

    private native void nativePinchBy(long j, long j2, float f, float f2, float f3);

    private native void nativePinchEnd(long j, long j2);

    private native void nativeRemoveJavascriptInterface(long j, String str);

    private native void nativeResetGestureDetection(long j);

    private native void nativeScrollBegin(long j, long j2, float f, float f2, float f3, float f4, boolean z);

    private native void nativeScrollBy(long j, long j2, float f, float f2, float f3, float f4);

    private native void nativeScrollEnd(long j, long j2);

    private native void nativeSelectBetweenCoordinates(long j, float f, float f2, float f3, float f4);

    private native void nativeSelectPopupMenuItems(long j, long j2, int[] iArr);

    private native int nativeSendMouseMoveEvent(long j, long j2, float f, float f2, int i);

    private native int nativeSendMouseWheelEvent(long j, long j2, float f, float f2, float f3, float f4, float f5);

    private native void nativeSendOrientationChangeEvent(long j, int i);

    private native void nativeSetAccessibilityEnabled(long j, boolean z);

    private native void nativeSetAllowJavascriptInterfacesInspection(long j, boolean z);

    private native void nativeSetBackgroundColor(long j, int i);

    private native void nativeSetBackgroundOpaque(long j, boolean z);

    private native void nativeSetDoubleTapSupportEnabled(long j, boolean z);

    private native void nativeSetFocus(long j, boolean z);

    private native void nativeSetMultiTouchZoomSupportEnabled(long j, boolean z);

    private native void nativeSetTextHandlesTemporarilyHidden(long j, boolean z);

    private native void nativeSetTextTrackSettings(long j, boolean z, String str, String str2, String str3, String str4, String str5, String str6, String str7);

    private native void nativeSingleTap(long j, long j2, float f, float f2);

    private native void nativeUpdateWindowAndroid(long j, long j2);

    private native void nativeWasResized(long j);

    public static ContentViewCore fromWebContents(WebContents webContents) {
        return nativeFromWebContentsAndroid(webContents);
    }

    public ContentViewCore(Context context) {
        this.mContext = context;
        this.mRenderCoordinates = new RenderCoordinates();
        this.mJoystickScrollProvider = new JoystickScrollProvider(this);
        float deviceScaleFactor = getContext().getResources().getDisplayMetrics().density;
        String forceScaleFactor = CommandLine.getInstance().getSwitchValue(ContentSwitches.FORCE_DEVICE_SCALE_FACTOR);
        if (forceScaleFactor != null) {
            deviceScaleFactor = Float.valueOf(forceScaleFactor).floatValue();
        }
        this.mRenderCoordinates.setDeviceScaleFactor(deviceScaleFactor);
        this.mAccessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        this.mSystemCaptioningBridge = CaptioningBridgeFactory.getSystemCaptioningBridge(this.mContext);
        this.mGestureStateListeners = new ObserverList();
        this.mGestureStateListenersIterator = this.mGestureStateListeners.rewindableIterator();
        this.mContainerViewObservers = new ObserverList();
    }

    @CalledByNative
    public Context getContext() {
        return this.mContext;
    }

    public ViewGroup getContainerView() {
        return this.mContainerView;
    }

    public WebContents getWebContents() {
        return this.mWebContents;
    }

    public WindowAndroid getWindowAndroid() {
        if (this.mNativeContentViewCore == 0) {
            return null;
        }
        return nativeGetJavaWindowAndroid(this.mNativeContentViewCore);
    }

    public void setTopControlsHeight(int topControlsHeightPix, boolean topControlsShrinkBlinkSize) {
        if (topControlsHeightPix != this.mTopControlsHeightPix || topControlsShrinkBlinkSize != this.mTopControlsShrinkBlinkSize) {
            this.mTopControlsHeightPix = topControlsHeightPix;
            this.mTopControlsShrinkBlinkSize = topControlsShrinkBlinkSize;
            if (this.mNativeContentViewCore != 0) {
                nativeWasResized(this.mNativeContentViewCore);
            }
        }
    }

    public ViewAndroidDelegate getViewAndroidDelegate() {
        return this.mViewAndroidDelegate;
    }

    @VisibleForTesting
    public void setImeAdapterForTest(ImeAdapter imeAdapter) {
        this.mImeAdapter = imeAdapter;
    }

    @VisibleForTesting
    public ImeAdapter getImeAdapterForTest() {
        return this.mImeAdapter;
    }

    private ImeAdapter createImeAdapter() {
        return new ImeAdapter(new InputMethodManagerWrapper(this.mContext), new C04372());
    }

    public void initialize(ViewGroup containerView, InternalAccessDelegate internalDispatcher, WebContents webContents, WindowAndroid windowAndroid) {
        createContentViewAndroidDelegate();
        setContainerView(containerView);
        long windowNativePointer = windowAndroid.getNativePointer();
        if ($assertionsDisabled || windowNativePointer != 0) {
            this.mZoomControlsDelegate = NO_OP_ZOOM_CONTROLS_DELEGATE;
            this.mNativeContentViewCore = nativeInit(webContents, this.mViewAndroidDelegate, windowNativePointer, this.mRetainedJavaScriptObjects);
            this.mWebContents = nativeGetWebContentsAndroid(this.mNativeContentViewCore);
            setContainerViewInternals(internalDispatcher);
            this.mRenderCoordinates.reset();
            initPopupZoomer(this.mContext);
            this.mImeAdapter = createImeAdapter();
            attachImeAdapter();
            this.mWebContentsObserver = new ContentViewWebContentsObserver(this);
            return;
        }
        throw new AssertionError();
    }

    public void updateWindowAndroid(WindowAndroid windowAndroid) {
        nativeUpdateWindowAndroid(this.mNativeContentViewCore, windowAndroid == null ? 0 : windowAndroid.getNativePointer());
        this.mSelectPopup = null;
        this.mPastePopupMenu = null;
    }

    @VisibleForTesting
    public void createContentViewAndroidDelegate() {
        this.mViewAndroidDelegate = new ContentViewAndroidDelegate(this.mContainerView, this.mRenderCoordinates);
    }

    public void setContainerView(ViewGroup containerView) {
        try {
            TraceEvent.begin("ContentViewCore.setContainerView");
            if (this.mContainerView != null) {
                if ($assertionsDisabled || this.mOverscrollRefreshHandler == null) {
                    this.mPastePopupMenu = null;
                    hidePopupsAndClearSelection();
                } else {
                    throw new AssertionError();
                }
            }
            this.mContainerView = containerView;
            this.mContainerView.setClickable(true);
            this.mViewAndroidDelegate.updateCurrentContainerView(this.mContainerView);
            Iterator i$ = this.mContainerViewObservers.iterator();
            while (i$.hasNext()) {
                ((ContainerViewObserver) i$.next()).onContainerViewChanged(this.mContainerView);
            }
        } finally {
            TraceEvent.end("ContentViewCore.setContainerView");
        }
    }

    public void addContainerViewObserver(ContainerViewObserver observer) {
        this.mContainerViewObservers.addObserver(observer);
    }

    public void removeContainerViewObserver(ContainerViewObserver observer) {
        this.mContainerViewObservers.removeObserver(observer);
    }

    @CalledByNative
    private void onNativeContentViewCoreDestroyed(long nativeContentViewCore) {
        if ($assertionsDisabled || nativeContentViewCore == this.mNativeContentViewCore) {
            this.mNativeContentViewCore = 0;
            return;
        }
        throw new AssertionError();
    }

    public void setContainerViewInternals(InternalAccessDelegate internalDispatcher) {
        this.mContainerViewInternals = internalDispatcher;
    }

    @VisibleForTesting
    void initPopupZoomer(Context context) {
        this.mPopupZoomer = new PopupZoomer(context);
        this.mPopupZoomer.setOnVisibilityChangedListener(new C04383());
        this.mPopupZoomer.setOnTapListener(new C04394());
    }

    @VisibleForTesting
    public void setPopupZoomerForTest(PopupZoomer popupZoomer) {
        this.mPopupZoomer = popupZoomer;
    }

    public void destroy() {
        if (this.mNativeContentViewCore != 0) {
            nativeOnJavaContentViewCoreDestroyed(this.mNativeContentViewCore);
        }
        this.mWebContentsObserver.destroy();
        this.mWebContentsObserver = null;
        setSmartClipDataListener(null);
        setZoomControlsDelegate(null);
        this.mImeAdapter.resetAndHideKeyboard();
        this.mContentViewClient = new ContentViewClient();
        this.mWebContents = null;
        this.mOverscrollRefreshHandler = null;
        this.mNativeContentViewCore = 0;
        this.mJavaScriptInterfaces.clear();
        this.mRetainedJavaScriptObjects.clear();
        this.mGestureStateListenersIterator.rewind();
        while (this.mGestureStateListenersIterator.hasNext()) {
            ((GestureStateListener) this.mGestureStateListenersIterator.next()).onDestroyed();
        }
        this.mGestureStateListeners.clear();
        ScreenOrientationListener.getInstance().removeObserver(this);
        this.mContainerViewObservers.clear();
        hidePopupsAndPreserveSelection();
        this.mPastePopupMenu = null;
    }

    public boolean isAlive() {
        return this.mNativeContentViewCore != 0;
    }

    @CalledByNative
    long getNativeContentViewCore() {
        return this.mNativeContentViewCore;
    }

    public void setContentViewClient(ContentViewClient client) {
        if (client == null) {
            throw new IllegalArgumentException("The client can't be null.");
        }
        this.mContentViewClient = client;
    }

    @VisibleForTesting
    public ContentViewClient getContentViewClient() {
        if (this.mContentViewClient == null) {
            this.mContentViewClient = new ContentViewClient();
        }
        return this.mContentViewClient;
    }

    @CalledByNative
    private void onBackgroundColorChanged(int color) {
        getContentViewClient().onBackgroundColorChanged(color);
    }

    @CalledByNative
    public int getViewportWidthPix() {
        return this.mViewportWidthPix;
    }

    @CalledByNative
    public int getViewportHeightPix() {
        return this.mViewportHeightPix;
    }

    @CalledByNative
    public int getViewportHeightWithOSKHiddenPix() {
        return this.mViewportHeightPix + getContentViewClient().getSystemWindowInsetBottom();
    }

    @CalledByNative
    private int getPhysicalBackingWidthPix() {
        return this.mPhysicalBackingWidthPix;
    }

    @CalledByNative
    private int getPhysicalBackingHeightPix() {
        return this.mPhysicalBackingHeightPix;
    }

    @CalledByNative
    public boolean doTopControlsShrinkBlinkSize() {
        return this.mTopControlsShrinkBlinkSize;
    }

    @CalledByNative
    public int getTopControlsHeightPix() {
        return this.mTopControlsHeightPix;
    }

    @VisibleForTesting
    public float getDeviceScaleFactor() {
        return this.mRenderCoordinates.getDeviceScaleFactor();
    }

    @VisibleForTesting
    public float getPageScaleFactor() {
        return this.mRenderCoordinates.getPageScaleFactor();
    }

    public float getContentHeightCss() {
        return this.mRenderCoordinates.getContentHeightCss();
    }

    public float getContentWidthCss() {
        return this.mRenderCoordinates.getContentWidthCss();
    }

    @VisibleForTesting
    public String getSelectedText() {
        return this.mHasSelection ? this.mLastSelectedText : "";
    }

    public boolean isSelectionEditable() {
        return this.mHasSelection ? this.mFocusedNodeEditable : false;
    }

    public boolean isFocusedNodeEditable() {
        return this.mFocusedNodeEditable;
    }

    public boolean isGamepadAPIActive() {
        return GamepadList.isGamepadAPIActive();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return onTouchEventImpl(event, false);
    }

    public boolean onTouchHandleEvent(MotionEvent event) {
        return onTouchEventImpl(event, true);
    }

    private boolean onTouchEventImpl(MotionEvent event, boolean isTouchHandleEvent) {
        TraceEvent.begin("onTouchEvent");
        try {
            int eventAction = event.getActionMasked();
            if (eventAction == 0) {
                cancelRequestToScrollFocusedEditableNodeIntoView();
            }
            if (SPenSupport.isSPenSupported(this.mContext)) {
                eventAction = SPenSupport.convertSPenEventAction(eventAction);
            }
            if (!isValidTouchEventActionForNative(eventAction)) {
                return false;
            }
            if (this.mNativeContentViewCore == 0) {
                TraceEvent.end("onTouchEvent");
                return false;
            }
            float x;
            float y;
            int pointerId;
            float orientation;
            float axisValue;
            int toolType;
            MotionEvent offset = null;
            if (!(this.mCurrentTouchOffsetX == 0.0f && this.mCurrentTouchOffsetY == 0.0f)) {
                offset = createOffsetMotionEvent(event);
                event = offset;
            }
            int pointerCount = event.getPointerCount();
            float[] touchMajor = new float[2];
            touchMajor[0] = event.getTouchMajor();
            touchMajor[1] = pointerCount > 1 ? event.getTouchMajor(1) : 0.0f;
            float[] touchMinor = new float[2];
            touchMinor[0] = event.getTouchMinor();
            touchMinor[1] = pointerCount > 1 ? event.getTouchMinor(1) : 0.0f;
            for (int i = 0; i < 2; i++) {
                if (touchMajor[i] < touchMinor[i]) {
                    float tmp = touchMajor[i];
                    touchMajor[i] = touchMinor[i];
                    touchMinor[i] = tmp;
                }
            }
            long j = this.mNativeContentViewCore;
            long eventTime = event.getEventTime();
            int historySize = event.getHistorySize();
            int actionIndex = event.getActionIndex();
            float x2 = event.getX();
            float y2 = event.getY();
            if (pointerCount > 1) {
                x = event.getX(1);
            } else {
                x = 0.0f;
            }
            if (pointerCount > 1) {
                y = event.getY(1);
            } else {
                y = 0.0f;
            }
            int pointerId2 = event.getPointerId(0);
            if (pointerCount > 1) {
                pointerId = event.getPointerId(1);
            } else {
                pointerId = -1;
            }
            float f = touchMajor[0];
            float f2 = touchMajor[1];
            float f3 = touchMinor[0];
            float f4 = touchMinor[1];
            float orientation2 = event.getOrientation();
            if (pointerCount > 1) {
                orientation = event.getOrientation(1);
            } else {
                orientation = 0.0f;
            }
            float axisValue2 = event.getAxisValue(25);
            if (pointerCount > 1) {
                axisValue = event.getAxisValue(25, 1);
            } else {
                axisValue = 0.0f;
            }
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            int toolType2 = event.getToolType(0);
            if (pointerCount > 1) {
                toolType = event.getToolType(1);
            } else {
                toolType = 0;
            }
            boolean consumed = nativeOnTouchEvent(j, event, eventTime, eventAction, pointerCount, historySize, actionIndex, x2, y2, x, y, pointerId2, pointerId, f, f2, f3, f4, orientation2, orientation, axisValue2, axisValue, rawX, rawY, toolType2, toolType, event.getButtonState(), event.getMetaState(), isTouchHandleEvent);
            if (offset != null) {
                offset.recycle();
            }
            TraceEvent.end("onTouchEvent");
            return consumed;
        } finally {
            TraceEvent.end("onTouchEvent");
        }
    }

    @CalledByNative
    private void requestDisallowInterceptTouchEvent() {
        this.mContainerView.requestDisallowInterceptTouchEvent(true);
    }

    private static boolean isValidTouchEventActionForNative(int eventAction) {
        return eventAction == 0 || eventAction == 1 || eventAction == 3 || eventAction == 2 || eventAction == 5 || eventAction == 6;
    }

    public boolean isScrollInProgress() {
        return this.mTouchScrollInProgress || this.mPotentiallyActiveFlingCount > 0;
    }

    private void setTouchScrollInProgress(boolean inProgress) {
        if (this.mTouchScrollInProgress != inProgress) {
            this.mTouchScrollInProgress = inProgress;
            updateActionModeVisibility();
        }
    }

    @CalledByNative
    private void onFlingStartEventConsumed(int vx, int vy) {
        this.mPotentiallyActiveFlingCount++;
        setTouchScrollInProgress(false);
        this.mGestureStateListenersIterator.rewind();
        while (this.mGestureStateListenersIterator.hasNext()) {
            ((GestureStateListener) this.mGestureStateListenersIterator.next()).onFlingStartGesture(vx, vy, computeVerticalScrollOffset(), computeVerticalScrollExtent());
        }
    }

    @CalledByNative
    private void onFlingCancelEventAck() {
        updateGestureStateListener(10);
    }

    @CalledByNative
    private void onScrollBeginEventAck() {
        setTouchScrollInProgress(true);
        hidePastePopup();
        this.mZoomControlsDelegate.invokeZoomPicker();
        updateGestureStateListener(6);
    }

    @CalledByNative
    private void onScrollUpdateGestureConsumed() {
        this.mZoomControlsDelegate.invokeZoomPicker();
        this.mGestureStateListenersIterator.rewind();
        while (this.mGestureStateListenersIterator.hasNext()) {
            ((GestureStateListener) this.mGestureStateListenersIterator.next()).onScrollUpdateGestureConsumed();
        }
    }

    @CalledByNative
    private void onScrollEndEventAck() {
        setTouchScrollInProgress(false);
        updateGestureStateListener(8);
    }

    @CalledByNative
    private void onPinchBeginEventAck() {
        updateGestureStateListener(12);
    }

    @CalledByNative
    private void onPinchEndEventAck() {
        updateGestureStateListener(14);
    }

    @CalledByNative
    private void onSingleTapEventAck(boolean consumed, int x, int y) {
        this.mGestureStateListenersIterator.rewind();
        while (this.mGestureStateListenersIterator.hasNext()) {
            ((GestureStateListener) this.mGestureStateListenersIterator.next()).onSingleTap(consumed, x, y);
        }
        hidePastePopup();
    }

    @CalledByNative
    private void onShowUnhandledTapUIIfNeeded(int x, int y) {
        if (this.mContextualSearchClient != null) {
            this.mContextualSearchClient.showUnhandledTapUIIfNeeded(x, y);
        }
    }

    @CalledByNative
    private boolean filterTapOrPressEvent(int type, int x, int y) {
        if (type == 5 && offerLongPressToEmbedder()) {
            return true;
        }
        updateForTapOrPress(type, (float) x, (float) y);
        return false;
    }

    @VisibleForTesting
    public void sendDoubleTapForTest(long timeMs, int x, int y) {
        if (this.mNativeContentViewCore != 0) {
            nativeDoubleTap(this.mNativeContentViewCore, timeMs, (float) x, (float) y);
        }
    }

    public void flingViewport(long timeMs, int velocityX, int velocityY) {
        if (this.mNativeContentViewCore != 0) {
            nativeFlingCancel(this.mNativeContentViewCore, timeMs);
            nativeScrollBegin(this.mNativeContentViewCore, timeMs, 0.0f, 0.0f, (float) velocityX, (float) velocityY, true);
            nativeFlingStart(this.mNativeContentViewCore, timeMs, 0.0f, 0.0f, (float) velocityX, (float) velocityY, true);
        }
    }

    public void cancelFling(long timeMs) {
        if (this.mNativeContentViewCore != 0) {
            nativeFlingCancel(this.mNativeContentViewCore, timeMs);
        }
    }

    public void addGestureStateListener(GestureStateListener listener) {
        this.mGestureStateListeners.addObserver(listener);
    }

    public void removeGestureStateListener(GestureStateListener listener) {
        this.mGestureStateListeners.removeObserver(listener);
    }

    void updateGestureStateListener(int gestureType) {
        this.mGestureStateListenersIterator.rewind();
        while (this.mGestureStateListenersIterator.hasNext()) {
            GestureStateListener listener = (GestureStateListener) this.mGestureStateListenersIterator.next();
            switch (gestureType) {
                case 6:
                    listener.onScrollStarted(computeVerticalScrollOffset(), computeVerticalScrollExtent());
                    break;
                case 8:
                    listener.onScrollEnded(computeVerticalScrollOffset(), computeVerticalScrollExtent());
                    break;
                case 11:
                    listener.onFlingEndGesture(computeVerticalScrollOffset(), computeVerticalScrollExtent());
                    break;
                case 12:
                    listener.onPinchStarted();
                    break;
                case 14:
                    listener.onPinchEnded();
                    break;
                default:
                    break;
            }
        }
    }

    public void onShow() {
        if ($assertionsDisabled || this.mWebContents != null) {
            this.mWebContents.onShow();
            setAccessibilityState(this.mAccessibilityManager.isEnabled());
            restoreSelectionPopupsIfNecessary();
            return;
        }
        throw new AssertionError();
    }

    public int getCurrentRenderProcessId() {
        return nativeGetCurrentRenderProcessId(this.mNativeContentViewCore);
    }

    public void onHide() {
        if ($assertionsDisabled || this.mWebContents != null) {
            hidePopupsAndPreserveSelection();
            this.mWebContents.onHide();
            return;
        }
        throw new AssertionError();
    }

    private void hidePopupsAndClearSelection() {
        this.mUnselectAllOnActionModeDismiss = true;
        hidePopups();
    }

    private void hidePopupsAndPreserveSelection() {
        this.mUnselectAllOnActionModeDismiss = false;
        hidePopups();
    }

    private void hidePopups() {
        hideSelectActionMode();
        hidePastePopup();
        hideSelectPopupWithCancelMesage();
        this.mPopupZoomer.hide(false);
        if (this.mUnselectAllOnActionModeDismiss) {
            dismissTextHandles();
        }
    }

    private void restoreSelectionPopupsIfNecessary() {
        if (this.mHasSelection && this.mActionMode == null) {
            showSelectActionMode(true);
        }
    }

    public void hideSelectActionMode() {
        if (this.mActionMode != null) {
            this.mActionMode.finish();
            this.mActionMode = null;
        }
    }

    public boolean isSelectActionBarShowing() {
        return this.mActionMode != null;
    }

    private void resetGestureDetection() {
        if (this.mNativeContentViewCore != 0) {
            nativeResetGestureDetection(this.mNativeContentViewCore);
        }
    }

    public void onAttachedToWindow() {
        setAccessibilityState(this.mAccessibilityManager.isEnabled());
        updateTextSelectionUI(true);
        ScreenOrientationListener.getInstance().addObserver(this, this.mContext);
        GamepadList.onAttachedToWindow(this.mContext);
        this.mAccessibilityManager.addAccessibilityStateChangeListener(this);
        this.mSystemCaptioningBridge.addListener(this);
        this.mImeAdapter.onViewAttachedToWindow();
    }

    public void updateTextSelectionUI(boolean focused) {
        setTextHandlesTemporarilyHidden(!focused);
        if (focused) {
            restoreSelectionPopupsIfNecessary();
        } else {
            hidePopupsAndPreserveSelection();
        }
    }

    @SuppressLint({"MissingSuperCall"})
    public void onDetachedFromWindow() {
        this.mImeAdapter.onViewDetachedFromWindow();
        this.mZoomControlsDelegate.dismissZoomPicker();
        ScreenOrientationListener.getInstance().removeObserver(this);
        GamepadList.onDetachedFromWindow();
        this.mAccessibilityManager.removeAccessibilityStateChangeListener(this);
        updateTextSelectionUI(false);
        this.mSystemCaptioningBridge.removeListener(this);
    }

    public void onVisibilityChanged(View changedView, int visibility) {
        if (visibility != 0) {
            this.mZoomControlsDelegate.dismissZoomPicker();
        }
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.mImeAdapter.onCreateInputConnection(outAttrs);
    }

    public boolean onCheckIsTextEditor() {
        return this.mImeAdapter.hasTextInputType();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        try {
            TraceEvent.begin("ContentViewCore.onConfigurationChanged");
            this.mImeAdapter.onKeyboardConfigurationChanged(newConfig);
            this.mContainerViewInternals.super_onConfigurationChanged(newConfig);
            this.mContainerView.requestLayout();
        } finally {
            TraceEvent.end("ContentViewCore.onConfigurationChanged");
        }
    }

    public void onSizeChanged(int wPix, int hPix, int owPix, int ohPix) {
        if (getViewportWidthPix() != wPix || getViewportHeightPix() != hPix) {
            this.mViewportWidthPix = wPix;
            this.mViewportHeightPix = hPix;
            if (this.mNativeContentViewCore != 0) {
                nativeWasResized(this.mNativeContentViewCore);
            }
            updateAfterSizeChanged();
        }
    }

    public void onPhysicalBackingSizeChanged(int wPix, int hPix) {
        if (this.mPhysicalBackingWidthPix != wPix || this.mPhysicalBackingHeightPix != hPix) {
            this.mPhysicalBackingWidthPix = wPix;
            this.mPhysicalBackingHeightPix = hPix;
            if (this.mNativeContentViewCore != 0) {
                nativeWasResized(this.mNativeContentViewCore);
            }
        }
    }

    private void updateAfterSizeChanged() {
        this.mPopupZoomer.hide(false);
        if (!this.mFocusPreOSKViewportRect.isEmpty()) {
            Rect rect = new Rect();
            getContainerView().getWindowVisibleDisplayFrame(rect);
            if (!rect.equals(this.mFocusPreOSKViewportRect)) {
                if (rect.width() == this.mFocusPreOSKViewportRect.width()) {
                    if ($assertionsDisabled || this.mWebContents != null) {
                        this.mWebContents.scrollFocusedEditableNodeIntoView();
                    } else {
                        throw new AssertionError();
                    }
                }
                cancelRequestToScrollFocusedEditableNodeIntoView();
            }
        }
    }

    private void cancelRequestToScrollFocusedEditableNodeIntoView() {
        this.mFocusPreOSKViewportRect.setEmpty();
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        this.mImeAdapter.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            resetGestureDetection();
        }
        if (this.mActionMode != null) {
            this.mActionMode.onWindowFocusChanged(hasWindowFocus);
        }
        this.mGestureStateListenersIterator.rewind();
        while (this.mGestureStateListenersIterator.hasNext()) {
            ((GestureStateListener) this.mGestureStateListenersIterator.next()).onWindowFocusChanged(hasWindowFocus);
        }
    }

    public void onFocusChanged(boolean gainFocus) {
        this.mImeAdapter.onViewFocusChanged(gainFocus);
        JoystickScrollProvider joystickScrollProvider = this.mJoystickScrollProvider;
        boolean z = gainFocus && !this.mFocusedNodeEditable;
        joystickScrollProvider.setEnabled(z);
        if (gainFocus) {
            restoreSelectionPopupsIfNecessary();
        } else {
            cancelRequestToScrollFocusedEditableNodeIntoView();
            if (this.mPreserveSelectionOnNextLossOfFocus) {
                this.mPreserveSelectionOnNextLossOfFocus = false;
                hidePopupsAndPreserveSelection();
            } else {
                hidePopupsAndClearSelection();
                clearSelection();
            }
        }
        if (this.mNativeContentViewCore != 0) {
            nativeSetFocus(this.mNativeContentViewCore, gainFocus);
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!this.mPopupZoomer.isShowing() || keyCode != 4) {
            return this.mContainerViewInternals.super_onKeyUp(keyCode, event);
        }
        this.mPopupZoomer.hide(true);
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (GamepadList.dispatchKeyEvent(event)) {
            return true;
        }
        if (getContentViewClient().shouldOverrideKeyEvent(event)) {
            return this.mContainerViewInternals.super_dispatchKeyEvent(event);
        }
        if (this.mImeAdapter.dispatchKeyEvent(event)) {
            return true;
        }
        return this.mContainerViewInternals.super_dispatchKeyEvent(event);
    }

    public boolean onHoverEvent(MotionEvent event) {
        TraceEvent.begin("onHoverEvent");
        MotionEvent offset = createOffsetMotionEvent(event);
        if (this.mBrowserAccessibilityManager == null || this.mIsObscuredByAnotherView) {
            try {
                if (this.mTouchExplorationEnabled && offset.getAction() == 10) {
                    offset.recycle();
                    TraceEvent.end("onHoverEvent");
                    return true;
                }
                if (event.getToolType(0) == 1) {
                    if (this.mEnableTouchHover == null) {
                        this.mEnableTouchHover = Boolean.valueOf(CommandLine.getInstance().hasSwitch(ContentSwitches.ENABLE_TOUCH_HOVER));
                    }
                    if (!this.mEnableTouchHover.booleanValue()) {
                        offset.recycle();
                        TraceEvent.end("onHoverEvent");
                        return false;
                    }
                }
                this.mContainerView.removeCallbacks(this.mFakeMouseMoveRunnable);
                if (this.mNativeContentViewCore != 0) {
                    nativeSendMouseMoveEvent(this.mNativeContentViewCore, offset.getEventTime(), offset.getX(), offset.getY(), event.getToolType(0));
                }
                offset.recycle();
                TraceEvent.end("onHoverEvent");
                return true;
            } finally {
                offset.recycle();
                TraceEvent.end("onHoverEvent");
            }
        } else {
            boolean onHoverEvent = this.mBrowserAccessibilityManager.onHoverEvent(offset);
            return onHoverEvent;
        }
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        if (GamepadList.onGenericMotionEvent(event)) {
            return true;
        }
        if ((event.getSource() & 2) != 0) {
            this.mLastFocalEventX = event.getX();
            this.mLastFocalEventY = event.getY();
            switch (event.getAction()) {
                case 8:
                    if (this.mNativeContentViewCore == 0) {
                        return false;
                    }
                    nativeSendMouseWheelEvent(this.mNativeContentViewCore, event.getEventTime(), event.getX(), event.getY(), event.getAxisValue(10), event.getAxisValue(9), getWheelScrollFactorInPixels());
                    this.mContainerView.removeCallbacks(this.mFakeMouseMoveRunnable);
                    final MotionEvent eventFakeMouseMove = MotionEvent.obtain(event);
                    this.mFakeMouseMoveRunnable = new Runnable() {
                        public void run() {
                            ContentViewCore.this.onHoverEvent(eventFakeMouseMove);
                            eventFakeMouseMove.recycle();
                        }
                    };
                    this.mContainerView.postDelayed(this.mFakeMouseMoveRunnable, 250);
                    return true;
            }
        } else if ((event.getSource() & 16) != 0) {
            if (this.mJoystickScrollProvider.onMotion(event)) {
                return true;
            }
            if (this.mJoystickZoomProvider == null) {
                this.mJoystickZoomProvider = new JoystickZoomProvider(this, new SystemAnimationIntervalProvider());
            }
            if (this.mJoystickZoomProvider.onMotion(event)) {
                return true;
            }
        }
        return this.mContainerViewInternals.super_onGenericMotionEvent(event);
    }

    public void setCurrentMotionEventOffsets(float dx, float dy) {
        this.mCurrentTouchOffsetX = dx;
        this.mCurrentTouchOffsetY = dy;
    }

    private MotionEvent createOffsetMotionEvent(MotionEvent src) {
        MotionEvent dst = MotionEvent.obtain(src);
        dst.offsetLocation(this.mCurrentTouchOffsetX, this.mCurrentTouchOffsetY);
        return dst;
    }

    public void scrollBy(float dxPix, float dyPix, boolean useLastFocalEventLocation) {
        float y = 0.0f;
        if (this.mNativeContentViewCore != 0) {
            if (dxPix != 0.0f || dyPix != 0.0f) {
                float x;
                long time = SystemClock.uptimeMillis();
                if (this.mPotentiallyActiveFlingCount > 0) {
                    nativeFlingCancel(this.mNativeContentViewCore, time);
                }
                if (useLastFocalEventLocation) {
                    x = this.mLastFocalEventX;
                } else {
                    x = 0.0f;
                }
                if (useLastFocalEventLocation) {
                    y = this.mLastFocalEventY;
                }
                nativeScrollBegin(this.mNativeContentViewCore, time, x, y, -dxPix, -dyPix, !useLastFocalEventLocation);
                nativeScrollBy(this.mNativeContentViewCore, time, x, y, dxPix, dyPix);
                nativeScrollEnd(this.mNativeContentViewCore, time);
            }
        }
    }

    public void scrollTo(float xPix, float yPix) {
        if (this.mNativeContentViewCore != 0) {
            scrollBy(xPix - this.mRenderCoordinates.getScrollXPix(), yPix - this.mRenderCoordinates.getScrollYPix(), false);
        }
    }

    public int getNativeScrollXForTest() {
        return this.mRenderCoordinates.getScrollXPixInt();
    }

    public int getNativeScrollYForTest() {
        return this.mRenderCoordinates.getScrollYPixInt();
    }

    public int computeHorizontalScrollExtent() {
        return this.mRenderCoordinates.getLastFrameViewportWidthPixInt();
    }

    public int computeHorizontalScrollOffset() {
        return this.mRenderCoordinates.getScrollXPixInt();
    }

    public int computeHorizontalScrollRange() {
        return this.mRenderCoordinates.getContentWidthPixInt();
    }

    public int computeVerticalScrollExtent() {
        return this.mRenderCoordinates.getLastFrameViewportHeightPixInt();
    }

    public int computeVerticalScrollOffset() {
        return this.mRenderCoordinates.getScrollYPixInt();
    }

    public int computeVerticalScrollRange() {
        return this.mRenderCoordinates.getContentHeightPixInt();
    }

    public boolean awakenScrollBars(int startDelay, boolean invalidate) {
        if (this.mContainerView.getScrollBarStyle() == 0) {
            return false;
        }
        return this.mContainerViewInternals.super_awakenScrollBars(startDelay, invalidate);
    }

    private void updateForTapOrPress(int type, float xPix, float yPix) {
        if (type == 3 || type == 2 || type == 5 || type == 16) {
            if (this.mContainerView.isFocusable() && this.mContainerView.isFocusableInTouchMode() && !this.mContainerView.isFocused()) {
                this.mContainerView.requestFocus();
            }
            if (!this.mPopupZoomer.isShowing()) {
                this.mPopupZoomer.setLastTouch(xPix, yPix);
            }
            this.mLastFocalEventX = xPix;
            this.mLastFocalEventY = yPix;
        }
    }

    public void setZoomControlsDelegate(ZoomControlsDelegate zoomControlsDelegate) {
        if (zoomControlsDelegate == null) {
            this.mZoomControlsDelegate = NO_OP_ZOOM_CONTROLS_DELEGATE;
        } else {
            this.mZoomControlsDelegate = zoomControlsDelegate;
        }
    }

    public void updateMultiTouchZoomSupport(boolean supportsMultiTouchZoom) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetMultiTouchZoomSupportEnabled(this.mNativeContentViewCore, supportsMultiTouchZoom);
        }
    }

    public void updateDoubleTapSupport(boolean supportsDoubleTap) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetDoubleTapSupportEnabled(this.mNativeContentViewCore, supportsDoubleTap);
        }
    }

    public void selectPopupMenuItems(int[] indices) {
        if (this.mNativeContentViewCore != 0) {
            nativeSelectPopupMenuItems(this.mNativeContentViewCore, this.mNativeSelectPopupSourceFrame, indices);
        }
        this.mNativeSelectPopupSourceFrame = 0;
        this.mSelectPopup = null;
    }

    @VisibleForTesting
    void sendOrientationChangeEvent(int orientation) {
        if (this.mNativeContentViewCore != 0) {
            nativeSendOrientationChangeEvent(this.mNativeContentViewCore, orientation);
        }
    }

    @VisibleForTesting
    public ActionHandler getSelectActionHandler() {
        return this.mActionHandler;
    }

    private void showSelectActionMode(boolean allowFallbackIfFloatingActionModeCreationFails) {
        if (this.mActionMode != null) {
            this.mActionMode.invalidate();
            return;
        }
        if (this.mActionHandler == null) {
            this.mActionHandler = new C04416();
        }
        this.mActionMode = null;
        if (this.mContainerView.getParent() != null) {
            if ($assertionsDisabled || this.mWebContents != null) {
                ActionMode actionMode = startActionMode(allowFallbackIfFloatingActionModeCreationFails);
                if (actionMode != null) {
                    this.mActionMode = new WebActionMode(actionMode, this.mContainerView);
                }
            } else {
                throw new AssertionError();
            }
        }
        this.mUnselectAllOnActionModeDismiss = true;
        if (this.mActionMode == null) {
            clearSelection();
        } else if (!supportsFloatingActionMode()) {
            getContentViewClient().onContextualActionBarShown();
        }
    }

    private boolean supportsFloatingActionMode() {
        if (VERSION.SDK_INT >= 23 && !this.mFloatingActionModeCreationFailed) {
            return true;
        }
        return false;
    }

    private ActionMode startActionMode(boolean allowFallbackIfFloatingActionModeCreationFails) {
        WebActionModeCallback callback = new WebActionModeCallback(this.mContainerView.getContext(), this.mActionHandler);
        if (supportsFloatingActionMode()) {
            ActionMode actionMode = startFloatingActionMode(callback);
            if (actionMode != null) {
                return actionMode;
            }
            this.mFloatingActionModeCreationFailed = true;
            if (!allowFallbackIfFloatingActionModeCreationFails) {
                return null;
            }
        }
        return startDefaultActionMode(callback);
    }

    private ActionMode startDefaultActionMode(WebActionModeCallback callback) {
        return this.mContainerView.startActionMode(callback);
    }

    @TargetApi(23)
    private ActionMode startFloatingActionMode(WebActionModeCallback callback) {
        return this.mContainerView.startActionMode(new FloatingWebActionModeCallback(callback), 1);
    }

    private void invalidateActionModeContentRect() {
        if (this.mActionMode != null) {
            this.mActionMode.invalidateContentRect();
        }
    }

    private void updateActionModeVisibility() {
        if (this.mActionMode != null) {
            WebActionMode webActionMode = this.mActionMode;
            boolean z = this.mDraggingSelection || this.mTouchScrollInProgress;
            webActionMode.hide(z);
        }
    }

    public void clearSelection() {
        if (this.mFocusedNodeEditable) {
            this.mImeAdapter.moveCursorToSelectionEnd();
        } else if (this.mWebContents != null) {
            this.mWebContents.unselect();
        }
    }

    public void preserveSelectionOnNextLossOfFocus() {
        this.mPreserveSelectionOnNextLossOfFocus = true;
    }

    @VisibleForTesting
    public boolean hasSelection() {
        return this.mHasSelection;
    }

    @VisibleForTesting
    public boolean hasInsertion() {
        return this.mHasInsertion;
    }

    @CalledByNative
    private void onSelectionEvent(int eventType, int xAnchor, int yAnchor, int left, int top, int right, int bottom) {
        if (left == right) {
            right++;
        }
        if (top == bottom) {
            bottom++;
        }
        switch (eventType) {
            case 0:
                this.mSelectionRect.set(left, top, right, bottom);
                this.mHasSelection = true;
                this.mUnselectAllOnActionModeDismiss = true;
                showSelectActionMode(true);
                break;
            case 1:
                this.mSelectionRect.set(left, top, right, bottom);
                invalidateActionModeContentRect();
                break;
            case 2:
                this.mHasSelection = false;
                this.mDraggingSelection = false;
                this.mUnselectAllOnActionModeDismiss = false;
                hideSelectActionMode();
                this.mSelectionRect.setEmpty();
                break;
            case 3:
                this.mDraggingSelection = true;
                updateActionModeVisibility();
                break;
            case 4:
                this.mDraggingSelection = false;
                updateActionModeVisibility();
                break;
            case 5:
                this.mSelectionRect.set(left, top, right, bottom);
                this.mHasInsertion = true;
                break;
            case 6:
                this.mSelectionRect.set(left, top, right, bottom);
                if (!isScrollInProgress() && isPastePopupShowing()) {
                    showPastePopup(xAnchor, yAnchor);
                    break;
                } else {
                    hidePastePopup();
                    break;
                }
                break;
            case 7:
                if (this.mWasPastePopupShowingOnInsertionDragStart) {
                    hidePastePopup();
                } else {
                    showPastePopup(xAnchor, yAnchor);
                }
                this.mWasPastePopupShowingOnInsertionDragStart = false;
                break;
            case 8:
                hidePastePopup();
                this.mHasInsertion = false;
                this.mSelectionRect.setEmpty();
                break;
            case 9:
                this.mWasPastePopupShowingOnInsertionDragStart = isPastePopupShowing();
                hidePastePopup();
                break;
            case 10:
                if (this.mWasPastePopupShowingOnInsertionDragStart) {
                    showPastePopup(xAnchor, yAnchor);
                }
                this.mWasPastePopupShowingOnInsertionDragStart = false;
                break;
            case 11:
            case 12:
                break;
            default:
                if (!$assertionsDisabled) {
                    throw new AssertionError("Invalid selection event type.");
                }
                break;
        }
        if (this.mContextualSearchClient != null) {
            this.mContextualSearchClient.onSelectionEvent(eventType, (float) xAnchor, (float) yAnchor);
        }
    }

    private void dismissTextHandles() {
        if (this.mNativeContentViewCore != 0) {
            nativeDismissTextHandles(this.mNativeContentViewCore);
        }
    }

    private void setTextHandlesTemporarilyHidden(boolean hide) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetTextHandlesTemporarilyHidden(this.mNativeContentViewCore, hide);
        }
    }

    @CalledByNative
    private void updateFrameInfo(float scrollOffsetX, float scrollOffsetY, float pageScaleFactor, float minPageScaleFactor, float maxPageScaleFactor, float contentWidth, float contentHeight, float viewportWidth, float viewportHeight, float controlsOffsetYCss, float contentOffsetYCss, boolean isMobileOptimizedHint, boolean hasInsertionMarker, boolean isInsertionMarkerVisible, float insertionMarkerHorizontal, float insertionMarkerTop, float insertionMarkerBottom) {
        TraceEvent.begin("ContentViewCore:updateFrameInfo");
        this.mIsMobileOptimizedHint = isMobileOptimizedHint;
        float deviceScale = this.mRenderCoordinates.getDeviceScaleFactor();
        contentWidth = Math.max(contentWidth, ((float) this.mViewportWidthPix) / (deviceScale * pageScaleFactor));
        contentHeight = Math.max(contentHeight, ((float) this.mViewportHeightPix) / (deviceScale * pageScaleFactor));
        float contentOffsetYPix = this.mRenderCoordinates.fromDipToPix(contentOffsetYCss);
        boolean contentSizeChanged = (contentWidth == this.mRenderCoordinates.getContentWidthCss() && contentHeight == this.mRenderCoordinates.getContentHeightCss()) ? false : true;
        boolean scaleLimitsChanged = (minPageScaleFactor == this.mRenderCoordinates.getMinPageScaleFactor() && maxPageScaleFactor == this.mRenderCoordinates.getMaxPageScaleFactor()) ? false : true;
        boolean scrollChanged = (!((pageScaleFactor > this.mRenderCoordinates.getPageScaleFactor() ? 1 : (pageScaleFactor == this.mRenderCoordinates.getPageScaleFactor() ? 0 : -1)) != 0) && scrollOffsetX == this.mRenderCoordinates.getScrollX() && scrollOffsetY == this.mRenderCoordinates.getScrollY()) ? false : true;
        boolean contentOffsetChanged = contentOffsetYPix != this.mRenderCoordinates.getContentOffsetYPix();
        boolean needHidePopupZoomer = contentSizeChanged || scrollChanged;
        boolean needUpdateZoomControls = scaleLimitsChanged || scrollChanged;
        if (needHidePopupZoomer) {
            this.mPopupZoomer.hide(true);
        }
        if (scrollChanged) {
            this.mContainerViewInternals.onScrollChanged((int) this.mRenderCoordinates.fromLocalCssToPix(scrollOffsetX), (int) this.mRenderCoordinates.fromLocalCssToPix(scrollOffsetY), (int) this.mRenderCoordinates.getScrollXPix(), (int) this.mRenderCoordinates.getScrollYPix());
        }
        this.mRenderCoordinates.updateFrameInfo(scrollOffsetX, scrollOffsetY, contentWidth, contentHeight, viewportWidth, viewportHeight, pageScaleFactor, minPageScaleFactor, maxPageScaleFactor, contentOffsetYPix);
        if (scrollChanged || contentOffsetChanged) {
            this.mGestureStateListenersIterator.rewind();
            while (this.mGestureStateListenersIterator.hasNext()) {
                ((GestureStateListener) this.mGestureStateListenersIterator.next()).onScrollOffsetOrExtentChanged(computeVerticalScrollOffset(), computeVerticalScrollExtent());
            }
        }
        if (needUpdateZoomControls) {
            this.mZoomControlsDelegate.updateZoomControls();
        }
        getContentViewClient().onOffsetsForFullscreenChanged(controlsOffsetYCss * deviceScale, contentOffsetYPix);
        if (this.mBrowserAccessibilityManager != null) {
            this.mBrowserAccessibilityManager.notifyFrameInfoInitialized();
        }
        this.mImeAdapter.onUpdateFrameInfo(this.mRenderCoordinates, hasInsertionMarker, isInsertionMarkerVisible, insertionMarkerHorizontal, insertionMarkerTop, insertionMarkerBottom);
        TraceEvent.end("ContentViewCore:updateFrameInfo");
    }

    @CalledByNative
    private void updateImeAdapter(long nativeImeAdapterAndroid, int textInputType, int textInputFlags, String text, int selectionStart, int selectionEnd, int compositionStart, int compositionEnd, boolean showImeIfNeeded, boolean isNonImeChange) {
        try {
            TraceEvent.begin("ContentViewCore.updateImeAdapter");
            boolean focusedNodeEditable = textInputType != 0;
            boolean focusedNodeIsPassword = textInputType == 2;
            if (!focusedNodeEditable) {
                hidePastePopup();
            }
            this.mImeAdapter.attach(nativeImeAdapterAndroid);
            this.mImeAdapter.updateKeyboardVisibility(textInputType, textInputFlags, showImeIfNeeded);
            this.mImeAdapter.updateState(text, selectionStart, selectionEnd, compositionStart, compositionEnd, isNonImeChange);
            if (this.mActionMode != null) {
                boolean actionModeConfigurationChanged = (focusedNodeEditable == this.mFocusedNodeEditable && focusedNodeIsPassword == this.mFocusedNodeIsPassword) ? false : true;
                if (actionModeConfigurationChanged) {
                    this.mActionMode.invalidate();
                }
            }
            this.mFocusedNodeIsPassword = focusedNodeIsPassword;
            if (focusedNodeEditable != this.mFocusedNodeEditable) {
                this.mFocusedNodeEditable = focusedNodeEditable;
                this.mJoystickScrollProvider.setEnabled(!this.mFocusedNodeEditable);
                getContentViewClient().onFocusedNodeEditabilityChanged(this.mFocusedNodeEditable);
            }
            TraceEvent.end("ContentViewCore.updateImeAdapter");
        } catch (Throwable th) {
            TraceEvent.end("ContentViewCore.updateImeAdapter");
        }
    }

    @CalledByNative
    private void forceUpdateImeAdapter(long nativeImeAdapterAndroid) {
        this.mImeAdapter.attach(nativeImeAdapterAndroid);
    }

    @CalledByNative
    private void setTitle(String title) {
        getContentViewClient().onUpdateTitle(title);
    }

    @CalledByNative
    private void showSelectPopup(long nativeSelectPopupSourceFrame, Rect bounds, String[] items, int[] enabled, boolean multiple, int[] selectedIndices, boolean rightAligned) {
        if (this.mContainerView.getParent() == null || this.mContainerView.getVisibility() != 0) {
            this.mNativeSelectPopupSourceFrame = nativeSelectPopupSourceFrame;
            selectPopupMenuItems(null);
            return;
        }
        hidePopupsAndClearSelection();
        if (!$assertionsDisabled && this.mNativeSelectPopupSourceFrame != 0) {
            throw new AssertionError("Zombie popup did not clear the frame source");
        } else if ($assertionsDisabled || items.length == enabled.length) {
            List<SelectPopupItem> popupItems = new ArrayList();
            for (int i = 0; i < items.length; i++) {
                popupItems.add(new SelectPopupItem(items[i], enabled[i]));
            }
            if (DeviceFormFactor.isTablet(this.mContext) && !multiple && !isTouchExplorationEnabled()) {
                this.mSelectPopup = new SelectPopupDropdown(this, popupItems, bounds, selectedIndices, rightAligned);
            } else if (getWindowAndroid() != null) {
                Context windowContext = (Context) getWindowAndroid().getContext().get();
                if (windowContext != null) {
                    this.mSelectPopup = new SelectPopupDialog(this, windowContext, popupItems, multiple, selectedIndices);
                } else {
                    return;
                }
            } else {
                return;
            }
            this.mNativeSelectPopupSourceFrame = nativeSelectPopupSourceFrame;
            this.mSelectPopup.show();
        } else {
            throw new AssertionError();
        }
    }

    @CalledByNative
    private void hideSelectPopup() {
        if (this.mSelectPopup != null) {
            this.mSelectPopup.hide(false);
            this.mSelectPopup = null;
            this.mNativeSelectPopupSourceFrame = 0;
        }
    }

    private void hideSelectPopupWithCancelMesage() {
        if (this.mSelectPopup != null) {
            this.mSelectPopup.hide(true);
        }
    }

    @VisibleForTesting
    public SelectPopup getSelectPopupForTest() {
        return this.mSelectPopup;
    }

    @CalledByNative
    private void showDisambiguationPopup(Rect targetRect, Bitmap zoomedBitmap) {
        this.mPopupZoomer.setBitmap(zoomedBitmap);
        this.mPopupZoomer.show(targetRect);
    }

    @CalledByNative
    private MotionEventSynthesizer createMotionEventSynthesizer() {
        return new MotionEventSynthesizer(this);
    }

    public void setOverscrollRefreshHandler(OverscrollRefreshHandler handler) {
        if ($assertionsDisabled || this.mOverscrollRefreshHandler == null || handler == null) {
            this.mOverscrollRefreshHandler = handler;
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private boolean onOverscrollRefreshStart() {
        if (this.mOverscrollRefreshHandler == null) {
            return false;
        }
        return this.mOverscrollRefreshHandler.start();
    }

    @CalledByNative
    private void onOverscrollRefreshUpdate(float delta) {
        if (this.mOverscrollRefreshHandler != null) {
            this.mOverscrollRefreshHandler.pull(delta);
        }
    }

    @CalledByNative
    private void onOverscrollRefreshRelease(boolean allowRefresh) {
        if (this.mOverscrollRefreshHandler != null) {
            this.mOverscrollRefreshHandler.release(allowRefresh);
        }
    }

    @CalledByNative
    private void onOverscrollRefreshReset() {
        if (this.mOverscrollRefreshHandler != null) {
            this.mOverscrollRefreshHandler.reset();
        }
    }

    @CalledByNative
    private void onSelectionChanged(String text) {
        this.mLastSelectedText = text;
        if (this.mContextualSearchClient != null) {
            this.mContextualSearchClient.onSelectionChanged(text);
        }
    }

    @CalledByNative
    private boolean showPastePopupWithFeedback(int x, int y) {
        if (!showPastePopup(x, y)) {
            return false;
        }
        if (this.mWebContents != null) {
            this.mWebContents.onContextMenuOpened();
        }
        return true;
    }

    @CalledByNative
    private void performLongPressHapticFeedback() {
        this.mContainerView.performHapticFeedback(0);
    }

    @VisibleForTesting
    public boolean isPastePopupShowing() {
        if (this.mPastePopupMenu != null) {
            return this.mPastePopupMenu.isShowing();
        }
        return false;
    }

    private boolean showPastePopup(int x, int y) {
        if (this.mContainerView.getParent() == null || this.mContainerView.getVisibility() != 0 || !this.mHasInsertion || !canPaste()) {
            return false;
        }
        float contentOffsetYPix = this.mRenderCoordinates.getContentOffsetYPix();
        PastePopupMenu pastePopupMenu = getPastePopup();
        if (pastePopupMenu == null) {
            return false;
        }
        try {
            pastePopupMenu.show(x, (int) (((float) y) + contentOffsetYPix));
            return true;
        } catch (BadTokenException e) {
            return false;
        }
    }

    private void hidePastePopup() {
        if (this.mPastePopupMenu != null) {
            this.mPastePopupMenu.hide();
        }
    }

    private PastePopupMenu getPastePopup() {
        if (this.mPastePopupMenu == null) {
            PastePopupMenuDelegate delegate = new C04427();
            Context windowContext = (Context) getWindowAndroid().getContext().get();
            if (windowContext == null) {
                return null;
            }
            if (supportsFloatingActionMode()) {
                this.mPastePopupMenu = new FloatingPastePopupMenu(windowContext, getContainerView(), delegate);
            } else {
                this.mPastePopupMenu = new LegacyPastePopupMenu(windowContext, getContainerView(), delegate);
            }
        }
        return this.mPastePopupMenu;
    }

    private boolean canPaste() {
        return ((ClipboardManager) this.mContext.getSystemService("clipboard")).hasPrimaryClip();
    }

    @CalledByNative
    private void onRenderProcessChange() {
        attachImeAdapter();
        this.mSystemCaptioningBridge.syncToListener(this);
    }

    public void attachImeAdapter() {
        if (this.mImeAdapter != null && this.mNativeContentViewCore != 0) {
            this.mImeAdapter.attach(nativeGetNativeImeAdapter(this.mNativeContentViewCore));
        }
    }

    @CalledByNative
    private boolean hasFocus() {
        if (this.mContainerView.isFocusable()) {
            return this.mContainerView.hasFocus();
        }
        return true;
    }

    public boolean canZoomIn() {
        return this.mRenderCoordinates.getMaxPageScaleFactor() - this.mRenderCoordinates.getPageScaleFactor() > ZOOM_CONTROLS_EPSILON;
    }

    public boolean canZoomOut() {
        return this.mRenderCoordinates.getPageScaleFactor() - this.mRenderCoordinates.getMinPageScaleFactor() > ZOOM_CONTROLS_EPSILON;
    }

    public boolean zoomIn() {
        if (canZoomIn()) {
            return pinchByDelta(1.25f);
        }
        return false;
    }

    public boolean zoomOut() {
        if (canZoomOut()) {
            return pinchByDelta(0.8f);
        }
        return false;
    }

    public boolean zoomReset() {
        if (canZoomOut()) {
            return pinchByDelta(this.mRenderCoordinates.getMinPageScaleFactor() / this.mRenderCoordinates.getPageScaleFactor());
        }
        return false;
    }

    public boolean pinchByDelta(float delta) {
        if (this.mNativeContentViewCore == 0) {
            return false;
        }
        long timeMs = SystemClock.uptimeMillis();
        int xPix = getViewportWidthPix() / 2;
        int yPix = getViewportHeightPix() / 2;
        nativePinchBegin(this.mNativeContentViewCore, timeMs, (float) xPix, (float) yPix);
        nativePinchBy(this.mNativeContentViewCore, timeMs, (float) xPix, (float) yPix, delta);
        nativePinchEnd(this.mNativeContentViewCore, timeMs);
        return true;
    }

    public boolean pinchBegin(int xPix, int yPix) {
        if (this.mNativeContentViewCore == 0) {
            return false;
        }
        nativePinchBegin(this.mNativeContentViewCore, SystemClock.uptimeMillis(), (float) xPix, (float) yPix);
        return true;
    }

    public boolean pinchBy(int xPix, int yPix, float delta) {
        if (this.mNativeContentViewCore == 0) {
            return false;
        }
        nativePinchBy(this.mNativeContentViewCore, SystemClock.uptimeMillis(), (float) xPix, (float) yPix, delta);
        return true;
    }

    public boolean pinchEnd() {
        if (this.mNativeContentViewCore == 0) {
            return false;
        }
        nativePinchEnd(this.mNativeContentViewCore, SystemClock.uptimeMillis());
        return true;
    }

    public void invokeZoomPicker() {
        this.mZoomControlsDelegate.invokeZoomPicker();
    }

    public void setAllowJavascriptInterfacesInspection(boolean allow) {
        nativeSetAllowJavascriptInterfacesInspection(this.mNativeContentViewCore, allow);
    }

    public Map<String, Pair<Object, Class>> getJavascriptInterfaces() {
        return this.mJavaScriptInterfaces;
    }

    public void addJavascriptInterface(Object object, String name) {
        addPossiblyUnsafeJavascriptInterface(object, name, JavascriptInterface.class);
    }

    public void addPossiblyUnsafeJavascriptInterface(Object object, String name, Class<? extends Annotation> requiredAnnotation) {
        if (this.mNativeContentViewCore != 0 && object != null) {
            this.mJavaScriptInterfaces.put(name, new Pair(object, requiredAnnotation));
            nativeAddJavascriptInterface(this.mNativeContentViewCore, object, name, requiredAnnotation);
        }
    }

    public void removeJavascriptInterface(String name) {
        this.mJavaScriptInterfaces.remove(name);
        if (this.mNativeContentViewCore != 0) {
            nativeRemoveJavascriptInterface(this.mNativeContentViewCore, name);
        }
    }

    @VisibleForTesting
    public float getScale() {
        return this.mRenderCoordinates.getPageScaleFactor();
    }

    @CalledByNative
    private void startContentIntent(String contentUrl, boolean isMainFrame) {
        getContentViewClient().onStartContentIntent(getContext(), contentUrl, isMainFrame);
    }

    public void onAccessibilityStateChanged(boolean enabled) {
        setAccessibilityState(enabled);
    }

    public boolean supportsAccessibilityAction(int action) {
        return false;
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        return false;
    }

    public void setBrowserAccessibilityManager(BrowserAccessibilityManager manager) {
        this.mBrowserAccessibilityManager = manager;
        if (this.mBrowserAccessibilityManager != null && this.mRenderCoordinates.hasFrameInfo()) {
            this.mBrowserAccessibilityManager.notifyFrameInfoInitialized();
        }
        if (this.mBrowserAccessibilityManager == null) {
            this.mNativeAccessibilityEnabled = false;
        }
    }

    public BrowserAccessibilityManager getBrowserAccessibilityManager() {
        return this.mBrowserAccessibilityManager;
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (this.mIsObscuredByAnotherView) {
            return null;
        }
        if (this.mBrowserAccessibilityManager != null) {
            return this.mBrowserAccessibilityManager.getAccessibilityNodeProvider();
        }
        if (!this.mNativeAccessibilityAllowed || this.mNativeAccessibilityEnabled || this.mNativeContentViewCore == 0) {
            return null;
        }
        this.mNativeAccessibilityEnabled = true;
        nativeSetAccessibilityEnabled(this.mNativeContentViewCore, true);
        return null;
    }

    public void setObscuredByAnotherView(boolean isObscured) {
        if (isObscured != this.mIsObscuredByAnotherView) {
            this.mIsObscuredByAnotherView = isObscured;
            getContainerView().sendAccessibilityEvent(2048);
        }
    }

    @TargetApi(23)
    public void onProvideVirtualStructure(ViewStructure structure, final boolean ignoreScrollOffset) {
        if (getWebContents().isIncognito()) {
            structure.setChildCount(0);
            return;
        }
        structure.setChildCount(1);
        final ViewStructure viewRoot = structure.asyncNewChild(0);
        getWebContents().requestAccessibilitySnapshot(new AccessibilitySnapshotCallback() {
            public void onAccessibilitySnapshot(AccessibilitySnapshotNode root) {
                viewRoot.setClassName("");
                viewRoot.setHint(ContentViewCore.this.mContentViewClient.getProductVersion());
                if (root == null) {
                    viewRoot.asyncCommit();
                } else {
                    ContentViewCore.this.createVirtualStructure(viewRoot, root, ignoreScrollOffset);
                }
            }
        });
    }

    @TargetApi(23)
    private void createVirtualStructure(ViewStructure viewNode, AccessibilitySnapshotNode node, boolean ignoreScrollOffset) {
        viewNode.setClassName(node.className);
        if (node.hasSelection) {
            viewNode.setText(node.text, node.startSelection, node.endSelection);
        } else {
            viewNode.setText(node.text);
        }
        int left = (int) this.mRenderCoordinates.fromLocalCssToPix((float) node.f0x);
        int top = (int) this.mRenderCoordinates.fromLocalCssToPix((float) node.f1y);
        int width = (int) this.mRenderCoordinates.fromLocalCssToPix((float) node.width);
        int height = (int) this.mRenderCoordinates.fromLocalCssToPix((float) node.height);
        Rect boundsInParent = new Rect(left, top, left + width, top + height);
        if (node.isRootNode) {
            boundsInParent.offset(0, (int) this.mRenderCoordinates.getContentOffsetYPix());
            if (!ignoreScrollOffset) {
                boundsInParent.offset(-((int) this.mRenderCoordinates.getScrollXPix()), -((int) this.mRenderCoordinates.getScrollYPix()));
            }
        }
        viewNode.setDimens(boundsInParent.left, boundsInParent.top, 0, 0, width, height);
        viewNode.setChildCount(node.children.size());
        if (node.hasStyle) {
            viewNode.setTextStyle(node.textSize, node.color, node.bgcolor, (((node.italic ? 2 : 0) | (node.bold ? 1 : 0)) | (node.underline ? 4 : 0)) | (node.lineThrough ? 8 : 0));
        }
        for (int i = 0; i < node.children.size(); i++) {
            createVirtualStructure(viewNode.asyncNewChild(i), (AccessibilitySnapshotNode) node.children.get(i), true);
        }
        viewNode.asyncCommit();
    }

    @TargetApi(19)
    public void onSystemCaptioningChanged(TextTrackSettings settings) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetTextTrackSettings(this.mNativeContentViewCore, settings.getTextTracksEnabled(), settings.getTextTrackBackgroundColor(), settings.getTextTrackFontFamily(), settings.getTextTrackFontStyle(), settings.getTextTrackFontVariant(), settings.getTextTrackTextColor(), settings.getTextTrackTextShadow(), settings.getTextTrackTextSize());
        }
    }

    public void onReceivedProcessTextResult(int resultCode, Intent data) {
        if (this.mWebContents != null && isSelectionEditable() && resultCode == -1 && data != null) {
            CharSequence result = data.getCharSequenceExtra("android.intent.extra.PROCESS_TEXT");
            if (result != null) {
                this.mWebContents.replace(result.toString());
            }
        }
    }

    public boolean isTouchExplorationEnabled() {
        return this.mTouchExplorationEnabled;
    }

    public void setAccessibilityState(boolean state) {
        if (state) {
            this.mNativeAccessibilityAllowed = true;
            this.mTouchExplorationEnabled = this.mAccessibilityManager.isTouchExplorationEnabled();
            return;
        }
        this.mNativeAccessibilityAllowed = false;
        this.mTouchExplorationEnabled = false;
    }

    public boolean shouldSetAccessibilityFocusOnPageLoad() {
        return this.mShouldSetAccessibilityFocusOnPageLoad;
    }

    public void setShouldSetAccessibilityFocusOnPageLoad(boolean on) {
        this.mShouldSetAccessibilityFocusOnPageLoad = on;
    }

    public RenderCoordinates getRenderCoordinates() {
        return this.mRenderCoordinates;
    }

    public boolean getIsMobileOptimizedHint() {
        return this.mIsMobileOptimizedHint;
    }

    @CalledByNative
    private static Rect createRect(int x, int y, int right, int bottom) {
        return new Rect(x, y, right, bottom);
    }

    public void extractSmartClipData(int x, int y, int width, int height) {
        if (this.mNativeContentViewCore != 0) {
            nativeExtractSmartClipData(this.mNativeContentViewCore, x + this.mSmartClipOffsetX, y + this.mSmartClipOffsetY, width, height);
        }
    }

    public void setSmartClipOffsets(int offsetX, int offsetY) {
        this.mSmartClipOffsetX = offsetX;
        this.mSmartClipOffsetY = offsetY;
    }

    @CalledByNative
    private void onSmartClipDataExtracted(String text, String html, Rect clipRect) {
        float deviceScale = this.mRenderCoordinates.getDeviceScaleFactor();
        clipRect.offset(-((int) (((float) this.mSmartClipOffsetX) / deviceScale)), -((int) (((float) this.mSmartClipOffsetY) / deviceScale)));
        if (this.mSmartClipDataListener != null) {
            this.mSmartClipDataListener.onSmartClipDataExtracted(text, html, clipRect);
        }
    }

    public void setSmartClipDataListener(SmartClipDataListener listener) {
        this.mSmartClipDataListener = listener;
    }

    public void setBackgroundColor(int color) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetBackgroundColor(this.mNativeContentViewCore, color);
        }
    }

    public void setBackgroundOpaque(boolean opaque) {
        if (this.mNativeContentViewCore != 0) {
            nativeSetBackgroundOpaque(this.mNativeContentViewCore, opaque);
        }
    }

    private boolean offerLongPressToEmbedder() {
        return this.mContainerView.performLongClick();
    }

    private void resetScrollInProgress() {
        if (isScrollInProgress()) {
            boolean touchScrollInProgress = this.mTouchScrollInProgress;
            int potentiallyActiveFlingCount = this.mPotentiallyActiveFlingCount;
            setTouchScrollInProgress(false);
            this.mPotentiallyActiveFlingCount = 0;
            if (touchScrollInProgress) {
                updateGestureStateListener(8);
            }
            if (potentiallyActiveFlingCount > 0) {
                updateGestureStateListener(11);
            }
        }
    }

    private float getWheelScrollFactorInPixels() {
        if (this.mWheelScrollFactorInPixels == 0.0f) {
            TypedValue outValue = new TypedValue();
            if (this.mContext.getTheme().resolveAttribute(16842829, outValue, true)) {
                this.mWheelScrollFactorInPixels = outValue.getDimension(this.mContext.getResources().getDisplayMetrics());
            } else {
                this.mWheelScrollFactorInPixels = 64.0f * this.mRenderCoordinates.getDeviceScaleFactor();
            }
        }
        return this.mWheelScrollFactorInPixels;
    }

    ContentVideoViewEmbedder getContentVideoViewEmbedder() {
        return getContentViewClient().getContentVideoViewEmbedder();
    }

    @CalledByNative
    private boolean shouldBlockMediaRequest(String url) {
        return getContentViewClient().shouldBlockMediaRequest(url);
    }

    @CalledByNative
    private void onNativeFlingStopped() {
        setTouchScrollInProgress(false);
        if (this.mPotentiallyActiveFlingCount > 0) {
            this.mPotentiallyActiveFlingCount--;
            updateGestureStateListener(11);
        }
    }

    public void onScreenOrientationChanged(int orientation) {
        if (VERSION.SDK_INT >= 23 && this.mActionMode != null) {
            hidePopupsAndPreserveSelection();
            showSelectActionMode(true);
        }
        sendOrientationChangeEvent(orientation);
    }

    public void setFullscreenRequiredForOrientationLock(boolean value) {
        this.mFullscreenRequiredForOrientationLock = value;
    }

    @CalledByNative
    private boolean isFullscreenRequiredForOrientationLock() {
        return this.mFullscreenRequiredForOrientationLock;
    }

    public void setContextualSearchClient(ContextualSearchClient contextualSearchClient) {
        this.mContextualSearchClient = contextualSearchClient;
    }

    @CalledByNative
    public void didOverscroll(boolean clampedX, boolean clampedY) {
        this.mContentViewClient.onOverScrolled(this.mRenderCoordinates.getScrollXPixInt(), this.mRenderCoordinates.getScrollYPixInt(), clampedX, clampedY);
    }

    public void onShowKeyboardReceiveResult(int resultCode) {
        if (resultCode == 2) {
            getContainerView().getWindowVisibleDisplayFrame(this.mFocusPreOSKViewportRect);
        } else if (hasFocus() && resultCode == 0 && this.mWebContents != null) {
            this.mWebContents.scrollFocusedEditableNodeIntoView();
        }
    }

    @VisibleForTesting
    public ResultReceiver getNewShowKeyboardReceiver() {
        if (this.mShowKeyboardResultReceiver == null) {
            this.mShowKeyboardResultReceiver = new ShowKeyboardResultReceiver(this, new Handler());
        }
        return this.mShowKeyboardResultReceiver;
    }
}
