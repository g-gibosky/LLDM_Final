package org.xwalk.core.internal;

import android.net.Uri;
import java.util.Map;

@XWalkAPI(instance = XWalkWebResourceRequestHandlerInternal.class)
public interface XWalkWebResourceRequestInternal {
    @XWalkAPI
    String getMethod();

    @XWalkAPI
    Map<String, String> getRequestHeaders();

    @XWalkAPI
    Uri getUrl();

    @XWalkAPI
    boolean hasGesture();

    @XWalkAPI
    boolean isForMainFrame();
}
