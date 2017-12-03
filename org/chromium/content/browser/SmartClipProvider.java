package org.chromium.content.browser;

import android.os.Handler;
import org.chromium.base.annotations.UsedByReflection;

@UsedByReflection("ExternalOemSupport")
public interface SmartClipProvider {
    @UsedByReflection("ExternalOemSupport")
    void extractSmartClipData(int i, int i2, int i3, int i4);

    @UsedByReflection("ExternalOemSupport")
    void setSmartClipResultHandler(Handler handler);
}
