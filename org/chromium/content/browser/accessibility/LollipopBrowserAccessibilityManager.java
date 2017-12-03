package org.chromium.content.browser.accessibility;

import android.annotation.TargetApi;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo;
import android.view.accessibility.AccessibilityNodeInfo.RangeInfo;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content.browser.ContentViewCore;

@TargetApi(21)
@JNINamespace("content")
public class LollipopBrowserAccessibilityManager extends KitKatBrowserAccessibilityManager {
    LollipopBrowserAccessibilityManager(long nativeBrowserAccessibilityManagerAndroid, ContentViewCore contentViewCore) {
        super(nativeBrowserAccessibilityManagerAndroid, contentViewCore);
    }

    protected void setAccessibilityNodeInfoLollipopAttributes(AccessibilityNodeInfo node, boolean canOpenPopup, boolean contentInvalid, boolean dismissable, boolean multiLine, int inputType, int liveRegion) {
        node.setCanOpenPopup(canOpenPopup);
        node.setContentInvalid(contentInvalid);
        node.setDismissable(contentInvalid);
        node.setMultiLine(multiLine);
        node.setInputType(inputType);
        node.setLiveRegion(liveRegion);
    }

    protected void setAccessibilityNodeInfoCollectionInfo(AccessibilityNodeInfo node, int rowCount, int columnCount, boolean hierarchical) {
        node.setCollectionInfo(CollectionInfo.obtain(rowCount, columnCount, hierarchical));
    }

    protected void setAccessibilityNodeInfoCollectionItemInfo(AccessibilityNodeInfo node, int rowIndex, int rowSpan, int columnIndex, int columnSpan, boolean heading) {
        node.setCollectionItemInfo(CollectionItemInfo.obtain(rowIndex, rowSpan, columnIndex, columnSpan, heading));
    }

    protected void setAccessibilityNodeInfoRangeInfo(AccessibilityNodeInfo node, int rangeType, float min, float max, float current) {
        node.setRangeInfo(RangeInfo.obtain(rangeType, min, max, current));
    }

    protected void setAccessibilityNodeInfoViewIdResourceName(AccessibilityNodeInfo node, String viewIdResourceName) {
        node.setViewIdResourceName(viewIdResourceName);
    }

    protected void setAccessibilityEventLollipopAttributes(AccessibilityEvent event, boolean canOpenPopup, boolean contentInvalid, boolean dismissable, boolean multiLine, int inputType, int liveRegion) {
    }

    protected void setAccessibilityEventCollectionInfo(AccessibilityEvent event, int rowCount, int columnCount, boolean hierarchical) {
    }

    protected void setAccessibilityEventHeadingFlag(AccessibilityEvent event, boolean heading) {
    }

    protected void setAccessibilityEventCollectionItemInfo(AccessibilityEvent event, int rowIndex, int rowSpan, int columnIndex, int columnSpan) {
    }

    protected void setAccessibilityEventRangeInfo(AccessibilityEvent event, int rangeType, float min, float max, float current) {
    }

    @CalledByNative
    protected void addAccessibilityNodeInfoActions(AccessibilityNodeInfo node, int virtualViewId, boolean canScrollForward, boolean canScrollBackward, boolean canScrollUp, boolean canScrollDown, boolean canScrollLeft, boolean canScrollRight, boolean clickable, boolean editableText, boolean enabled, boolean focusable, boolean focused) {
        node.addAction(AccessibilityAction.ACTION_NEXT_HTML_ELEMENT);
        node.addAction(AccessibilityAction.ACTION_PREVIOUS_HTML_ELEMENT);
        node.addAction(AccessibilityAction.ACTION_NEXT_AT_MOVEMENT_GRANULARITY);
        node.addAction(AccessibilityAction.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY);
        if (editableText && enabled) {
            node.addAction(AccessibilityAction.ACTION_SET_TEXT);
            node.addAction(AccessibilityAction.ACTION_SET_SELECTION);
        }
        if (canScrollForward) {
            node.addAction(AccessibilityAction.ACTION_SCROLL_FORWARD);
        }
        if (canScrollBackward) {
            node.addAction(AccessibilityAction.ACTION_SCROLL_BACKWARD);
        }
        if (focusable) {
            if (focused) {
                node.addAction(AccessibilityAction.ACTION_CLEAR_FOCUS);
            } else {
                node.addAction(AccessibilityAction.ACTION_FOCUS);
            }
        }
        if (this.mAccessibilityFocusId == virtualViewId) {
            node.addAction(AccessibilityAction.ACTION_CLEAR_ACCESSIBILITY_FOCUS);
        } else {
            node.addAction(AccessibilityAction.ACTION_ACCESSIBILITY_FOCUS);
        }
        if (clickable) {
            node.addAction(AccessibilityAction.ACTION_CLICK);
        }
    }
}
