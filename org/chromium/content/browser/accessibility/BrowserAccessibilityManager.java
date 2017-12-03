package org.chromium.content.browser.accessibility;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content.browser.ContentViewCore;
import org.chromium.content.browser.RenderCoordinates;

@JNINamespace("content")
public class BrowserAccessibilityManager {
    private static final String ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE = "ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE";
    private static final int ACTION_SET_TEXT = 2097152;
    private static final String TAG = "BrowserAccessibilityManager";
    private static final int WINDOW_CONTENT_CHANGED_DELAY_MS = 500;
    protected int mAccessibilityFocusId;
    private Rect mAccessibilityFocusRect;
    private final AccessibilityManager mAccessibilityManager;
    private final AccessibilityNodeProvider mAccessibilityNodeProvider;
    private ContentViewCore mContentViewCore;
    protected int mCurrentRootId;
    private boolean mIsHovering;
    private int mLastHoverId = -1;
    private long mNativeObj;
    private boolean mNotifyFrameInfoInitializedCalled;
    private boolean mPendingScrollToMakeNodeVisible;
    private final RenderCoordinates mRenderCoordinates;
    private int mSelectionEndIndex;
    private int mSelectionGranularity;
    private int mSelectionStartIndex;
    private Runnable mSendWindowContentChangedRunnable;
    private final int[] mTempLocation = new int[2];
    private boolean mUserHasTouchExplored;
    private final ViewGroup mView;

    class C02092 implements Runnable {
        C02092() {
        }

        public void run() {
            BrowserAccessibilityManager.this.sendWindowContentChangedOnView();
        }
    }

    private native boolean nativeAdjustSlider(long j, int i, boolean z);

    private native void nativeBlur(long j);

    private native void nativeClick(long j, int i);

    private native int nativeFindElementType(long j, int i, String str, boolean z);

    private native void nativeFocus(long j, int i);

    private native int nativeGetEditableTextSelectionEnd(long j, int i);

    private native int nativeGetEditableTextSelectionStart(long j, int i);

    private native int nativeGetRootId(long j);

    private native void nativeHitTest(long j, int i, int i2);

    private native boolean nativeIsEditableText(long j, int i);

    private native boolean nativeIsNodeValid(long j, int i);

    private native boolean nativeIsSlider(long j, int i);

    private native boolean nativeNextAtGranularity(long j, int i, boolean z, int i2, int i3);

    private native boolean nativePopulateAccessibilityEvent(long j, AccessibilityEvent accessibilityEvent, int i, int i2);

    private native boolean nativePopulateAccessibilityNodeInfo(long j, AccessibilityNodeInfo accessibilityNodeInfo, int i);

    private native boolean nativePreviousAtGranularity(long j, int i, boolean z, int i2, int i3);

    private native boolean nativeScroll(long j, int i, int i2);

    private native void nativeScrollToMakeNodeVisible(long j, int i);

    private native void nativeSetAccessibilityFocus(long j, int i);

    private native void nativeSetSelection(long j, int i, int i2, int i3);

    private native void nativeSetTextFieldValue(long j, int i, String str);

    protected native String nativeGetSupportedHtmlElementTypes(long j);

    @CalledByNative
    private static BrowserAccessibilityManager create(long nativeBrowserAccessibilityManagerAndroid, ContentViewCore contentViewCore) {
        if (VERSION.SDK_INT >= 21) {
            return new LollipopBrowserAccessibilityManager(nativeBrowserAccessibilityManagerAndroid, contentViewCore);
        }
        if (VERSION.SDK_INT >= 19) {
            return new KitKatBrowserAccessibilityManager(nativeBrowserAccessibilityManagerAndroid, contentViewCore);
        }
        return new BrowserAccessibilityManager(nativeBrowserAccessibilityManagerAndroid, contentViewCore);
    }

    protected BrowserAccessibilityManager(long nativeBrowserAccessibilityManagerAndroid, ContentViewCore contentViewCore) {
        this.mNativeObj = nativeBrowserAccessibilityManagerAndroid;
        this.mContentViewCore = contentViewCore;
        this.mAccessibilityFocusId = -1;
        this.mIsHovering = false;
        this.mCurrentRootId = -1;
        this.mView = this.mContentViewCore.getContainerView();
        this.mRenderCoordinates = this.mContentViewCore.getRenderCoordinates();
        this.mAccessibilityManager = (AccessibilityManager) this.mContentViewCore.getContext().getSystemService("accessibility");
        final BrowserAccessibilityManager delegate = this;
        this.mAccessibilityNodeProvider = new AccessibilityNodeProvider() {
            public AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
                return delegate.createAccessibilityNodeInfo(virtualViewId);
            }

            public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String text, int virtualViewId) {
                return delegate.findAccessibilityNodeInfosByText(text, virtualViewId);
            }

            public boolean performAction(int virtualViewId, int action, Bundle arguments) {
                return delegate.performAction(virtualViewId, action, arguments);
            }
        };
        this.mContentViewCore.setBrowserAccessibilityManager(this);
    }

    @CalledByNative
    private void onNativeObjectDestroyed(long nativeBrowserAccessibilityManagerAndroid) {
        if (nativeBrowserAccessibilityManagerAndroid == this.mNativeObj) {
            if (this.mContentViewCore != null && this.mContentViewCore.getBrowserAccessibilityManager() == this) {
                this.mContentViewCore.setBrowserAccessibilityManager(null);
            }
            this.mNativeObj = 0;
            this.mContentViewCore = null;
        }
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        return this.mAccessibilityNodeProvider;
    }

    protected AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
        if (!this.mAccessibilityManager.isEnabled() || this.mNativeObj == 0) {
            return null;
        }
        int rootId = nativeGetRootId(this.mNativeObj);
        if (virtualViewId == -1) {
            return createNodeForHost(rootId);
        }
        if (!isFrameInfoInitialized()) {
            return null;
        }
        AccessibilityNodeInfo info = AccessibilityNodeInfo.obtain(this.mView);
        info.setPackageName(this.mContentViewCore.getContext().getPackageName());
        info.setSource(this.mView, virtualViewId);
        if (virtualViewId == rootId) {
            info.setParent(this.mView);
        }
        if (nativePopulateAccessibilityNodeInfo(this.mNativeObj, info, virtualViewId)) {
            return info;
        }
        info.recycle();
        return null;
    }

    protected List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String text, int virtualViewId) {
        return new ArrayList();
    }

    protected static boolean isValidMovementGranularity(int granularity) {
        switch (granularity) {
            case 1:
            case 2:
            case 4:
                return true;
            default:
                return false;
        }
    }

    protected boolean performAction(int virtualViewId, int action, Bundle arguments) {
        if (!this.mAccessibilityManager.isEnabled() || this.mNativeObj == 0 || !nativeIsNodeValid(this.mNativeObj, virtualViewId)) {
            return false;
        }
        int granularity;
        boolean extend;
        String elementType;
        switch (action) {
            case 1:
                nativeFocus(this.mNativeObj, virtualViewId);
                return true;
            case 2:
                nativeBlur(this.mNativeObj);
                return true;
            case 16:
                nativeClick(this.mNativeObj, virtualViewId);
                return true;
            case 64:
                if (!moveAccessibilityFocusToId(virtualViewId)) {
                    return true;
                }
                if (this.mIsHovering) {
                    this.mPendingScrollToMakeNodeVisible = true;
                } else {
                    nativeScrollToMakeNodeVisible(this.mNativeObj, this.mAccessibilityFocusId);
                }
                return true;
            case 128:
                sendAccessibilityEvent(virtualViewId, 65536);
                if (this.mAccessibilityFocusId == virtualViewId) {
                    this.mAccessibilityFocusId = -1;
                    this.mAccessibilityFocusRect = null;
                }
                return true;
            case 256:
                if (arguments == null) {
                    return false;
                }
                granularity = arguments.getInt(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT);
                extend = arguments.getBoolean(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN);
                if (isValidMovementGranularity(granularity)) {
                    return nextAtGranularity(granularity, extend);
                }
                return false;
            case 512:
                if (arguments == null) {
                    return false;
                }
                granularity = arguments.getInt(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT);
                extend = arguments.getBoolean(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN);
                if (isValidMovementGranularity(granularity)) {
                    return previousAtGranularity(granularity, extend);
                }
                return false;
            case 1024:
                if (arguments == null) {
                    return false;
                }
                elementType = arguments.getString(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_HTML_ELEMENT_STRING);
                if (elementType == null) {
                    return false;
                }
                return jumpToElementType(elementType.toUpperCase(Locale.US), true);
            case 2048:
                if (arguments == null) {
                    return false;
                }
                elementType = arguments.getString(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_HTML_ELEMENT_STRING);
                if (elementType == null) {
                    return false;
                }
                return jumpToElementType(elementType.toUpperCase(Locale.US), false);
            case 4096:
                return scrollForward(virtualViewId);
            case 8192:
                return scrollBackward(virtualViewId);
            case 131072:
                if (!nativeIsEditableText(this.mNativeObj, virtualViewId)) {
                    return false;
                }
                int selectionStart = 0;
                int selectionEnd = 0;
                if (arguments != null) {
                    selectionStart = arguments.getInt(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_START_INT);
                    selectionEnd = arguments.getInt(AccessibilityNodeInfoCompat.ACTION_ARGUMENT_SELECTION_END_INT);
                }
                nativeSetSelection(this.mNativeObj, virtualViewId, selectionStart, selectionEnd);
                return true;
            case 2097152:
                if (!nativeIsEditableText(this.mNativeObj, virtualViewId)) {
                    return false;
                }
                if (arguments == null) {
                    return false;
                }
                String newText = arguments.getString("ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE");
                if (newText == null) {
                    return false;
                }
                nativeSetTextFieldValue(this.mNativeObj, virtualViewId, newText);
                nativeSetSelection(this.mNativeObj, virtualViewId, newText.length(), newText.length());
                return true;
            default:
                return false;
        }
    }

    public boolean onHoverEvent(MotionEvent event) {
        if (!this.mAccessibilityManager.isEnabled() || this.mNativeObj == 0) {
            return false;
        }
        if (event.getAction() == 10) {
            this.mIsHovering = false;
            if (this.mLastHoverId != -1) {
                sendAccessibilityEvent(this.mLastHoverId, 256);
                this.mLastHoverId = -1;
            }
            if (this.mPendingScrollToMakeNodeVisible) {
                nativeScrollToMakeNodeVisible(this.mNativeObj, this.mAccessibilityFocusId);
            }
            this.mPendingScrollToMakeNodeVisible = false;
            return true;
        }
        this.mIsHovering = true;
        this.mUserHasTouchExplored = true;
        float x = event.getX();
        nativeHitTest(this.mNativeObj, (int) this.mRenderCoordinates.fromPixToLocalCss(x), (int) this.mRenderCoordinates.fromPixToLocalCss(event.getY()));
        return true;
    }

    public void notifyFrameInfoInitialized() {
        if (!this.mNotifyFrameInfoInitializedCalled) {
            this.mNotifyFrameInfoInitializedCalled = true;
            sendWindowContentChangedOnView();
            if (this.mAccessibilityFocusId != -1) {
                moveAccessibilityFocusToIdAndRefocusIfNeeded(this.mAccessibilityFocusId);
            }
        }
    }

    private boolean jumpToElementType(String elementType, boolean forwards) {
        int id = nativeFindElementType(this.mNativeObj, this.mAccessibilityFocusId, elementType, forwards);
        if (id == 0) {
            return false;
        }
        moveAccessibilityFocusToId(id);
        nativeScrollToMakeNodeVisible(this.mNativeObj, this.mAccessibilityFocusId);
        return true;
    }

    private void setGranularityAndUpdateSelection(int granularity) {
        if (this.mSelectionGranularity == 0) {
            this.mSelectionStartIndex = -1;
            this.mSelectionEndIndex = -1;
        }
        this.mSelectionGranularity = granularity;
        if (nativeIsEditableText(this.mNativeObj, this.mAccessibilityFocusId)) {
            this.mSelectionStartIndex = nativeGetEditableTextSelectionStart(this.mNativeObj, this.mAccessibilityFocusId);
            this.mSelectionEndIndex = nativeGetEditableTextSelectionEnd(this.mNativeObj, this.mAccessibilityFocusId);
        }
    }

    private boolean nextAtGranularity(int granularity, boolean extendSelection) {
        setGranularityAndUpdateSelection(granularity);
        return nativeNextAtGranularity(this.mNativeObj, this.mSelectionGranularity, extendSelection, this.mAccessibilityFocusId, this.mSelectionEndIndex);
    }

    private boolean previousAtGranularity(int granularity, boolean extendSelection) {
        setGranularityAndUpdateSelection(granularity);
        return nativePreviousAtGranularity(this.mNativeObj, this.mSelectionGranularity, extendSelection, this.mAccessibilityFocusId, this.mSelectionEndIndex);
    }

    @CalledByNative
    private void finishGranularityMove(String text, boolean extendSelection, int itemStartIndex, int itemEndIndex, boolean forwards) {
        if (this.mNativeObj != 0) {
            AccessibilityEvent selectionEvent = buildAccessibilityEvent(this.mAccessibilityFocusId, 8192);
            if (selectionEvent != null) {
                AccessibilityEvent traverseEvent = buildAccessibilityEvent(this.mAccessibilityFocusId, 131072);
                if (traverseEvent == null) {
                    selectionEvent.recycle();
                    return;
                }
                if (forwards) {
                    this.mSelectionEndIndex = itemEndIndex;
                } else {
                    this.mSelectionEndIndex = itemStartIndex;
                }
                if (!extendSelection) {
                    this.mSelectionStartIndex = this.mSelectionEndIndex;
                }
                if (nativeIsEditableText(this.mNativeObj, this.mAccessibilityFocusId)) {
                    nativeSetSelection(this.mNativeObj, this.mAccessibilityFocusId, this.mSelectionStartIndex, this.mSelectionEndIndex);
                }
                selectionEvent.setFromIndex(this.mSelectionStartIndex);
                selectionEvent.setToIndex(this.mSelectionStartIndex);
                selectionEvent.setItemCount(text.length());
                traverseEvent.setFromIndex(itemStartIndex);
                traverseEvent.setToIndex(itemEndIndex);
                traverseEvent.setItemCount(text.length());
                traverseEvent.setMovementGranularity(this.mSelectionGranularity);
                traverseEvent.setContentDescription(text);
                if (forwards) {
                    traverseEvent.setAction(256);
                } else {
                    traverseEvent.setAction(512);
                }
                this.mView.requestSendAccessibilityEvent(this.mView, selectionEvent);
                this.mView.requestSendAccessibilityEvent(this.mView, traverseEvent);
            }
        }
    }

    private boolean scrollForward(int virtualViewId) {
        if (nativeIsSlider(this.mNativeObj, virtualViewId)) {
            return nativeAdjustSlider(this.mNativeObj, virtualViewId, true);
        }
        return nativeScroll(this.mNativeObj, virtualViewId, 0);
    }

    private boolean scrollBackward(int virtualViewId) {
        if (nativeIsSlider(this.mNativeObj, virtualViewId)) {
            return nativeAdjustSlider(this.mNativeObj, virtualViewId, false);
        }
        return nativeScroll(this.mNativeObj, virtualViewId, 1);
    }

    private boolean moveAccessibilityFocusToId(int newAccessibilityFocusId) {
        if (newAccessibilityFocusId == this.mAccessibilityFocusId) {
            return false;
        }
        this.mAccessibilityFocusId = newAccessibilityFocusId;
        this.mAccessibilityFocusRect = null;
        this.mSelectionGranularity = 0;
        this.mSelectionStartIndex = 0;
        this.mSelectionEndIndex = 0;
        if (this.mAccessibilityFocusId == this.mCurrentRootId) {
            nativeSetAccessibilityFocus(this.mNativeObj, -1);
        } else {
            nativeSetAccessibilityFocus(this.mNativeObj, this.mAccessibilityFocusId);
        }
        sendAccessibilityEvent(this.mAccessibilityFocusId, 32768);
        return true;
    }

    private void moveAccessibilityFocusToIdAndRefocusIfNeeded(int newAccessibilityFocusId) {
        if (newAccessibilityFocusId == this.mAccessibilityFocusId) {
            sendAccessibilityEvent(newAccessibilityFocusId, 65536);
            this.mAccessibilityFocusId = -1;
        }
        moveAccessibilityFocusToId(newAccessibilityFocusId);
    }

    @CalledByNative
    private void sendDelayedWindowContentChangedEvent() {
        if (this.mNativeObj != 0 && this.mSendWindowContentChangedRunnable == null) {
            this.mSendWindowContentChangedRunnable = new C02092();
            this.mView.postDelayed(this.mSendWindowContentChangedRunnable, 500);
        }
    }

    private void sendWindowContentChangedOnView() {
        if (this.mNativeObj != 0 && this.mContentViewCore != null && this.mView != null) {
            if (this.mSendWindowContentChangedRunnable != null) {
                this.mView.removeCallbacks(this.mSendWindowContentChangedRunnable);
                this.mSendWindowContentChangedRunnable = null;
            }
            this.mView.sendAccessibilityEvent(2048);
        }
    }

    private void sendWindowContentChangedOnVirtualView(int virtualViewId) {
        sendAccessibilityEvent(virtualViewId, 2048);
    }

    private void sendAccessibilityEvent(int virtualViewId, int eventType) {
        if (virtualViewId == -1) {
            this.mView.sendAccessibilityEvent(eventType);
            return;
        }
        AccessibilityEvent event = buildAccessibilityEvent(virtualViewId, eventType);
        if (event != null) {
            this.mView.requestSendAccessibilityEvent(this.mView, event);
        }
    }

    private AccessibilityEvent buildAccessibilityEvent(int virtualViewId, int eventType) {
        if (!this.mAccessibilityManager.isEnabled() || this.mNativeObj == 0 || !isFrameInfoInitialized()) {
            return null;
        }
        this.mView.postInvalidate();
        AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
        event.setPackageName(this.mContentViewCore.getContext().getPackageName());
        event.setSource(this.mView, virtualViewId);
        if (nativePopulateAccessibilityEvent(this.mNativeObj, event, virtualViewId, eventType)) {
            return event;
        }
        event.recycle();
        return null;
    }

    private Bundle getOrCreateBundleForAccessibilityEvent(AccessibilityEvent event) {
        Bundle bundle = (Bundle) event.getParcelableData();
        if (bundle != null) {
            return bundle;
        }
        bundle = new Bundle();
        event.setParcelableData(bundle);
        return bundle;
    }

    private AccessibilityNodeInfo createNodeForHost(int rootId) {
        AccessibilityNodeInfo result = AccessibilityNodeInfo.obtain(this.mView);
        AccessibilityNodeInfo source = AccessibilityNodeInfo.obtain(this.mView);
        this.mView.onInitializeAccessibilityNodeInfo(source);
        Rect rect = new Rect();
        source.getBoundsInParent(rect);
        result.setBoundsInParent(rect);
        source.getBoundsInScreen(rect);
        result.setBoundsInScreen(rect);
        ViewParent parent = this.mView.getParentForAccessibility();
        if (parent instanceof View) {
            result.setParent((View) parent);
        }
        result.setVisibleToUser(source.isVisibleToUser());
        result.setEnabled(source.isEnabled());
        result.setPackageName(source.getPackageName());
        result.setClassName(source.getClassName());
        if (isFrameInfoInitialized()) {
            result.addChild(this.mView, rootId);
        }
        return result;
    }

    private boolean isFrameInfoInitialized() {
        return (((double) this.mRenderCoordinates.getContentWidthCss()) == 0.0d && ((double) this.mRenderCoordinates.getContentHeightCss()) == 0.0d) ? false : true;
    }

    @CalledByNative
    private void handlePageLoaded(int id) {
        if (this.mNativeObj != 0 && !this.mUserHasTouchExplored && this.mContentViewCore.shouldSetAccessibilityFocusOnPageLoad()) {
            moveAccessibilityFocusToIdAndRefocusIfNeeded(id);
        }
    }

    @CalledByNative
    private void handleFocusChanged(int id) {
        if (this.mNativeObj != 0) {
            sendAccessibilityEvent(id, 8);
            moveAccessibilityFocusToId(id);
        }
    }

    @CalledByNative
    private void handleCheckStateChanged(int id) {
        if (this.mNativeObj != 0) {
            sendAccessibilityEvent(id, 1);
        }
    }

    @CalledByNative
    private void handleClicked(int id) {
        if (this.mNativeObj != 0) {
            sendAccessibilityEvent(id, 1);
        }
    }

    @CalledByNative
    private void handleTextSelectionChanged(int id) {
        if (this.mNativeObj != 0) {
            sendAccessibilityEvent(id, 8192);
        }
    }

    @CalledByNative
    private void handleEditableTextChanged(int id) {
        if (this.mNativeObj != 0) {
            sendAccessibilityEvent(id, 16);
        }
    }

    @CalledByNative
    private void handleSliderChanged(int id) {
        if (this.mNativeObj != 0) {
            sendAccessibilityEvent(id, 4096);
        }
    }

    @CalledByNative
    private void handleContentChanged(int id) {
        if (this.mNativeObj != 0) {
            int rootId = nativeGetRootId(this.mNativeObj);
            if (rootId != this.mCurrentRootId) {
                this.mCurrentRootId = rootId;
                sendWindowContentChangedOnView();
                return;
            }
            sendWindowContentChangedOnVirtualView(id);
        }
    }

    @CalledByNative
    private void handleNavigate() {
        if (this.mNativeObj != 0) {
            this.mAccessibilityFocusId = -1;
            this.mAccessibilityFocusRect = null;
            this.mUserHasTouchExplored = false;
            sendWindowContentChangedOnView();
        }
    }

    @CalledByNative
    private void handleScrollPositionChanged(int id) {
        if (this.mNativeObj != 0) {
            sendAccessibilityEvent(id, 4096);
        }
    }

    @CalledByNative
    private void handleScrolledToAnchor(int id) {
        if (this.mNativeObj != 0) {
            moveAccessibilityFocusToId(id);
        }
    }

    @CalledByNative
    private void handleHover(int id) {
        if (this.mNativeObj != 0 && this.mLastHoverId != id && this.mIsHovering) {
            sendAccessibilityEvent(id, 128);
            if (this.mLastHoverId != -1) {
                sendAccessibilityEvent(this.mLastHoverId, 256);
            }
            this.mLastHoverId = id;
        }
    }

    @CalledByNative
    private void announceLiveRegionText(String text) {
        this.mView.announceForAccessibility(text);
    }

    @CalledByNative
    private void setAccessibilityNodeInfoParent(AccessibilityNodeInfo node, int parentId) {
        node.setParent(this.mView, parentId);
    }

    @CalledByNative
    private void addAccessibilityNodeInfoChild(AccessibilityNodeInfo node, int childId) {
        node.addChild(this.mView, childId);
    }

    @CalledByNative
    private void setAccessibilityNodeInfoBooleanAttributes(AccessibilityNodeInfo node, int virtualViewId, boolean checkable, boolean checked, boolean clickable, boolean enabled, boolean focusable, boolean focused, boolean password, boolean scrollable, boolean selected, boolean visibleToUser) {
        node.setCheckable(checkable);
        node.setChecked(checked);
        node.setClickable(clickable);
        node.setEnabled(enabled);
        node.setFocusable(focusable);
        node.setFocused(focused);
        node.setPassword(password);
        node.setScrollable(scrollable);
        node.setSelected(selected);
        node.setVisibleToUser(visibleToUser);
        node.setMovementGranularities(7);
        if (this.mAccessibilityFocusId == virtualViewId) {
            node.setAccessibilityFocused(true);
        } else {
            node.setAccessibilityFocused(false);
        }
    }

    @CalledByNative
    protected void addAccessibilityNodeInfoActions(AccessibilityNodeInfo node, int virtualViewId, boolean canScrollForward, boolean canScrollBackward, boolean canScrollUp, boolean canScrollDown, boolean canScrollLeft, boolean canScrollRight, boolean clickable, boolean editableText, boolean enabled, boolean focusable, boolean focused) {
        node.addAction(1024);
        node.addAction(2048);
        node.addAction(256);
        node.addAction(512);
        if (editableText && enabled) {
            node.addAction(2097152);
            node.addAction(131072);
        }
        if (canScrollForward) {
            node.addAction(4096);
        }
        if (canScrollBackward) {
            node.addAction(8192);
        }
        if (focusable) {
            if (focused) {
                node.addAction(2);
            } else {
                node.addAction(1);
            }
        }
        if (this.mAccessibilityFocusId == virtualViewId) {
            node.addAction(128);
        } else {
            node.addAction(64);
        }
        if (clickable) {
            node.addAction(16);
        }
    }

    @CalledByNative
    private void setAccessibilityNodeInfoClassName(AccessibilityNodeInfo node, String className) {
        node.setClassName(className);
    }

    @SuppressLint({"NewApi"})
    @CalledByNative
    private void setAccessibilityNodeInfoText(AccessibilityNodeInfo node, String text, boolean annotateAsLink, boolean isEditableText) {
        CharSequence charSequence = text;
        if (annotateAsLink) {
            CharSequence spannable = new SpannableString(text);
            spannable.setSpan(new URLSpan(""), 0, spannable.length(), 0);
            charSequence = spannable;
        }
        if (isEditableText) {
            node.setText(charSequence);
        } else {
            node.setContentDescription(charSequence);
        }
    }

    @CalledByNative
    private void setAccessibilityNodeInfoLocation(AccessibilityNodeInfo node, int virtualViewId, int absoluteLeft, int absoluteTop, int parentRelativeLeft, int parentRelativeTop, int width, int height, boolean isRootNode) {
        Rect boundsInParent = new Rect(parentRelativeLeft, parentRelativeTop, parentRelativeLeft + width, parentRelativeTop + height);
        if (isRootNode) {
            boundsInParent.offset(0, (int) this.mRenderCoordinates.getContentOffsetYPix());
        }
        node.setBoundsInParent(boundsInParent);
        Rect rect = new Rect(absoluteLeft, absoluteTop, absoluteLeft + width, absoluteTop + height);
        rect.offset(-((int) this.mRenderCoordinates.getScrollX()), -((int) this.mRenderCoordinates.getScrollY()));
        rect.left = (int) this.mRenderCoordinates.fromLocalCssToPix((float) rect.left);
        rect.top = (int) this.mRenderCoordinates.fromLocalCssToPix((float) rect.top);
        rect.bottom = (int) this.mRenderCoordinates.fromLocalCssToPix((float) rect.bottom);
        rect.right = (int) this.mRenderCoordinates.fromLocalCssToPix((float) rect.right);
        rect.offset(0, (int) this.mRenderCoordinates.getContentOffsetYPix());
        int[] viewLocation = new int[2];
        this.mView.getLocationOnScreen(viewLocation);
        rect.offset(viewLocation[0], viewLocation[1]);
        int viewportRectTop = viewLocation[1] + ((int) this.mRenderCoordinates.getContentOffsetYPix());
        int viewportRectBottom = viewportRectTop + this.mContentViewCore.getViewportHeightPix();
        if (rect.top < viewportRectTop) {
            rect.top = viewportRectTop;
        }
        if (rect.bottom > viewportRectBottom) {
            rect.bottom = viewportRectBottom;
        }
        node.setBoundsInScreen(rect);
        if (virtualViewId == this.mAccessibilityFocusId && virtualViewId != this.mCurrentRootId) {
            if (this.mAccessibilityFocusRect == null) {
                this.mAccessibilityFocusRect = rect;
            } else if (!this.mAccessibilityFocusRect.equals(rect)) {
                this.mAccessibilityFocusRect = rect;
                moveAccessibilityFocusToIdAndRefocusIfNeeded(virtualViewId);
            }
        }
    }

    @CalledByNative
    protected void setAccessibilityNodeInfoKitKatAttributes(AccessibilityNodeInfo node, boolean isRoot, boolean isEditableText, String roleDescription) {
    }

    @CalledByNative
    protected void setAccessibilityNodeInfoLollipopAttributes(AccessibilityNodeInfo node, boolean canOpenPopup, boolean contentInvalid, boolean dismissable, boolean multiLine, int inputType, int liveRegion) {
    }

    @CalledByNative
    protected void setAccessibilityNodeInfoCollectionInfo(AccessibilityNodeInfo node, int rowCount, int columnCount, boolean hierarchical) {
    }

    @CalledByNative
    protected void setAccessibilityNodeInfoCollectionItemInfo(AccessibilityNodeInfo node, int rowIndex, int rowSpan, int columnIndex, int columnSpan, boolean heading) {
    }

    @CalledByNative
    protected void setAccessibilityNodeInfoRangeInfo(AccessibilityNodeInfo node, int rangeType, float min, float max, float current) {
    }

    @CalledByNative
    protected void setAccessibilityNodeInfoViewIdResourceName(AccessibilityNodeInfo node, String viewIdResourceName) {
    }

    @CalledByNative
    private void setAccessibilityEventBooleanAttributes(AccessibilityEvent event, boolean checked, boolean enabled, boolean password, boolean scrollable) {
        event.setChecked(checked);
        event.setEnabled(enabled);
        event.setPassword(password);
        event.setScrollable(scrollable);
    }

    @CalledByNative
    private void setAccessibilityEventClassName(AccessibilityEvent event, String className) {
        event.setClassName(className);
    }

    @CalledByNative
    private void setAccessibilityEventListAttributes(AccessibilityEvent event, int currentItemIndex, int itemCount) {
        event.setCurrentItemIndex(currentItemIndex);
        event.setItemCount(itemCount);
    }

    @CalledByNative
    private void setAccessibilityEventScrollAttributes(AccessibilityEvent event, int scrollX, int scrollY, int maxScrollX, int maxScrollY) {
        event.setScrollX(scrollX);
        event.setScrollY(scrollY);
        event.setMaxScrollX(maxScrollX);
        event.setMaxScrollY(maxScrollY);
    }

    @CalledByNative
    private void setAccessibilityEventTextChangedAttrs(AccessibilityEvent event, int fromIndex, int addedCount, int removedCount, String beforeText, String text) {
        event.setFromIndex(fromIndex);
        event.setAddedCount(addedCount);
        event.setRemovedCount(removedCount);
        event.setBeforeText(beforeText);
        event.getText().add(text);
    }

    @CalledByNative
    private void setAccessibilityEventSelectionAttrs(AccessibilityEvent event, int fromIndex, int toIndex, int itemCount, String text) {
        event.setFromIndex(fromIndex);
        event.setToIndex(toIndex);
        event.setItemCount(itemCount);
        event.getText().add(text);
    }

    @CalledByNative
    protected void setAccessibilityEventLollipopAttributes(AccessibilityEvent event, boolean canOpenPopup, boolean contentInvalid, boolean dismissable, boolean multiLine, int inputType, int liveRegion) {
        Bundle bundle = getOrCreateBundleForAccessibilityEvent(event);
        bundle.putBoolean("AccessibilityNodeInfo.canOpenPopup", canOpenPopup);
        bundle.putBoolean("AccessibilityNodeInfo.contentInvalid", contentInvalid);
        bundle.putBoolean("AccessibilityNodeInfo.dismissable", dismissable);
        bundle.putBoolean("AccessibilityNodeInfo.multiLine", multiLine);
        bundle.putInt("AccessibilityNodeInfo.inputType", inputType);
        bundle.putInt("AccessibilityNodeInfo.liveRegion", liveRegion);
    }

    @CalledByNative
    protected void setAccessibilityEventCollectionInfo(AccessibilityEvent event, int rowCount, int columnCount, boolean hierarchical) {
        Bundle bundle = getOrCreateBundleForAccessibilityEvent(event);
        bundle.putInt("AccessibilityNodeInfo.CollectionInfo.rowCount", rowCount);
        bundle.putInt("AccessibilityNodeInfo.CollectionInfo.columnCount", columnCount);
        bundle.putBoolean("AccessibilityNodeInfo.CollectionInfo.hierarchical", hierarchical);
    }

    @CalledByNative
    protected void setAccessibilityEventHeadingFlag(AccessibilityEvent event, boolean heading) {
        getOrCreateBundleForAccessibilityEvent(event).putBoolean("AccessibilityNodeInfo.CollectionItemInfo.heading", heading);
    }

    @CalledByNative
    protected void setAccessibilityEventCollectionItemInfo(AccessibilityEvent event, int rowIndex, int rowSpan, int columnIndex, int columnSpan) {
        Bundle bundle = getOrCreateBundleForAccessibilityEvent(event);
        bundle.putInt("AccessibilityNodeInfo.CollectionItemInfo.rowIndex", rowIndex);
        bundle.putInt("AccessibilityNodeInfo.CollectionItemInfo.rowSpan", rowSpan);
        bundle.putInt("AccessibilityNodeInfo.CollectionItemInfo.columnIndex", columnIndex);
        bundle.putInt("AccessibilityNodeInfo.CollectionItemInfo.columnSpan", columnSpan);
    }

    @CalledByNative
    protected void setAccessibilityEventRangeInfo(AccessibilityEvent event, int rangeType, float min, float max, float current) {
        Bundle bundle = getOrCreateBundleForAccessibilityEvent(event);
        bundle.putInt("AccessibilityNodeInfo.RangeInfo.type", rangeType);
        bundle.putFloat("AccessibilityNodeInfo.RangeInfo.min", min);
        bundle.putFloat("AccessibilityNodeInfo.RangeInfo.max", max);
        bundle.putFloat("AccessibilityNodeInfo.RangeInfo.current", current);
    }

    @CalledByNative
    boolean shouldExposePasswordText() {
        return Secure.getInt(this.mContentViewCore.getContext().getContentResolver(), "speak_password", 0) == 1;
    }
}
