package org.xwalk.core.internal;

import android.net.Uri;
import java.util.Map;
import org.xwalk.core.internal.XWalkContentsClient.WebResourceRequestInner;

@XWalkAPI(createInternally = true, impl = XWalkWebResourceRequestInternal.class)
public class XWalkWebResourceRequestHandlerInternal implements XWalkWebResourceRequestInternal {
    private final WebResourceRequestInner mRequest;

    XWalkWebResourceRequestHandlerInternal() {
        this.mRequest = null;
    }

    XWalkWebResourceRequestHandlerInternal(WebResourceRequestInner request) {
        this.mRequest = request;
    }

    @XWalkAPI
    public Uri getUrl() {
        return Uri.parse(this.mRequest.url);
    }

    @XWalkAPI
    public boolean isForMainFrame() {
        return this.mRequest.isMainFrame;
    }

    @XWalkAPI
    public boolean hasGesture() {
        return this.mRequest.hasUserGesture;
    }

    @XWalkAPI
    public String getMethod() {
        return this.mRequest.method;
    }

    @XWalkAPI
    public Map<String, String> getRequestHeaders() {
        return this.mRequest.requestHeaders;
    }
}
