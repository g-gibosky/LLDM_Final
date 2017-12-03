package org.chromium.content.browser.accessibility;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.content.browser.ContentViewCore;

@TargetApi(19)
@JNINamespace("content")
public class KitKatBrowserAccessibilityManager extends BrowserAccessibilityManager {
    private String mSupportedHtmlElementTypes;

    KitKatBrowserAccessibilityManager(long nativeBrowserAccessibilityManagerAndroid, ContentViewCore contentViewCore) {
        super(nativeBrowserAccessibilityManagerAndroid, contentViewCore);
        this.mSupportedHtmlElementTypes = nativeGetSupportedHtmlElementTypes(nativeBrowserAccessibilityManagerAndroid);
    }

    protected void setAccessibilityNodeInfoKitKatAttributes(AccessibilityNodeInfo node, boolean isRoot, boolean isEditableText, String roleDescription) {
        Bundle bundle = node.getExtras();
        bundle.putCharSequence("AccessibilityNodeInfo.roleDescription", roleDescription);
        if (isRoot) {
            bundle.putCharSequence("ACTION_ARGUMENT_HTML_ELEMENT_STRING_VALUES", this.mSupportedHtmlElementTypes);
        }
        if (isEditableText) {
            node.setEditable(true);
        }
    }
}
