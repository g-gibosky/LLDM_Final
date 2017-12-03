package org.xwalk.core.internal;

import java.util.HashMap;
import java.util.Map;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.xwalk.core.internal.XWalkContentsClient.WebResourceRequestInner;

@JNINamespace("xwalk")
public abstract class XWalkContentsIoThreadClient {
    @CalledByNative
    public abstract int getCacheMode();

    @CalledByNative
    public abstract void newLoginRequest(String str, String str2, String str3);

    @CalledByNative
    public abstract void onDownloadStart(String str, String str2, String str3, String str4, long j);

    public abstract void onReceivedResponseHeaders(WebResourceRequestInner webResourceRequestInner, XWalkWebResourceResponseInternal xWalkWebResourceResponseInternal);

    @CalledByNative
    public abstract boolean shouldBlockContentUrls();

    @CalledByNative
    public abstract boolean shouldBlockFileUrls();

    @CalledByNative
    public abstract boolean shouldBlockNetworkLoads();

    public abstract XWalkWebResourceResponseInternal shouldInterceptRequest(WebResourceRequestInner webResourceRequestInner);

    @CalledByNative
    protected XWalkWebResourceResponseInternal shouldInterceptRequest(String url, boolean isMainFrame, boolean hasUserGesture, String method, String[] requestHeaderNames, String[] requestHeaderValues) {
        WebResourceRequestInner request = new WebResourceRequestInner();
        request.url = url;
        request.isMainFrame = isMainFrame;
        request.hasUserGesture = hasUserGesture;
        request.method = method;
        request.requestHeaders = new HashMap(requestHeaderNames.length);
        for (int i = 0; i < requestHeaderNames.length; i++) {
            request.requestHeaders.put(requestHeaderNames[i], requestHeaderValues[i]);
        }
        return shouldInterceptRequest(request);
    }

    @CalledByNative
    protected void onReceivedResponseHeaders(String url, boolean isMainFrame, boolean hasUserGesture, String method, String[] requestHeaderNames, String[] requestHeaderValues, String mimeType, String encoding, int statusCode, String reasonPhrase, String[] responseHeaderNames, String[] responseHeaderValues) {
        int i;
        WebResourceRequestInner request = new WebResourceRequestInner();
        request.url = url;
        request.isMainFrame = isMainFrame;
        request.hasUserGesture = hasUserGesture;
        request.method = method;
        request.requestHeaders = new HashMap(requestHeaderNames.length);
        for (i = 0; i < requestHeaderNames.length; i++) {
            request.requestHeaders.put(requestHeaderNames[i], requestHeaderValues[i]);
        }
        Map<String, String> responseHeaders = new HashMap(responseHeaderNames.length);
        for (i = 0; i < responseHeaderNames.length; i++) {
            if (!responseHeaders.containsKey(responseHeaderNames[i])) {
                responseHeaders.put(responseHeaderNames[i], responseHeaderValues[i]);
            } else if (!responseHeaderValues[i].isEmpty()) {
                String currentValue = (String) responseHeaders.get(responseHeaderNames[i]);
                if (!currentValue.isEmpty()) {
                    currentValue = currentValue + ", ";
                }
                responseHeaders.put(responseHeaderNames[i], currentValue + responseHeaderValues[i]);
            }
        }
        onReceivedResponseHeaders(request, new XWalkWebResourceResponseInternal(mimeType, encoding, null, statusCode, reasonPhrase, responseHeaders));
    }
}
