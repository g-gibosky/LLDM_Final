package org.xwalk.core.internal;

import android.support.v4.media.TransportMediator;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Map;
import java.util.Map.Entry;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("xwalk")
@XWalkAPI(createInternally = true)
public class XWalkWebResourceResponseInternal {
    private InputStream mData;
    private String mEncoding;
    private String mMimeType;
    private String mReasonPhrase;
    private String[] mResponseHeaderNames;
    private String[] mResponseHeaderValues;
    private Map<String, String> mResponseHeaders;
    private int mStatusCode;

    XWalkWebResourceResponseInternal() {
        this.mMimeType = null;
        this.mEncoding = null;
        this.mData = null;
    }

    XWalkWebResourceResponseInternal(String mimeType, String encoding, InputStream data) {
        this.mMimeType = mimeType;
        this.mEncoding = encoding;
        setData(data);
    }

    XWalkWebResourceResponseInternal(String mimeType, String encoding, InputStream data, int statusCode, String reasonPhrase, Map<String, String> responseHeaders) {
        this(mimeType, encoding, data);
        this.mStatusCode = statusCode;
        this.mReasonPhrase = reasonPhrase;
        this.mResponseHeaders = responseHeaders;
    }

    private void fillInResponseHeaderNamesAndValuesIfNeeded() {
        if (this.mResponseHeaders != null && this.mResponseHeaderNames == null) {
            this.mResponseHeaderNames = new String[this.mResponseHeaders.size()];
            this.mResponseHeaderValues = new String[this.mResponseHeaders.size()];
            int i = 0;
            for (Entry<String, String> entry : this.mResponseHeaders.entrySet()) {
                this.mResponseHeaderNames[i] = (String) entry.getKey();
                this.mResponseHeaderValues[i] = (String) entry.getValue();
                i++;
            }
        }
    }

    @XWalkAPI
    public void setMimeType(String mimeType) {
        this.mMimeType = mimeType;
    }

    @XWalkAPI
    public String getMimeType() {
        return this.mMimeType;
    }

    @CalledByNative
    public String getMimeTypeNative() {
        return this.mMimeType;
    }

    @XWalkAPI
    public void setEncoding(String encoding) {
        this.mEncoding = encoding;
    }

    @XWalkAPI
    public String getEncoding() {
        return this.mEncoding;
    }

    @CalledByNative
    public String getEncodingNative() {
        return this.mEncoding;
    }

    @XWalkAPI
    public void setData(InputStream data) {
        if (data == null || !StringBufferInputStream.class.isAssignableFrom(data.getClass())) {
            this.mData = data;
            return;
        }
        throw new IllegalArgumentException("StringBufferInputStream is deprecated and must not be passed to a XWalkWebResourceResponse");
    }

    @XWalkAPI
    public InputStream getData() {
        return this.mData;
    }

    @CalledByNative
    public InputStream getDataNative() {
        return this.mData;
    }

    @XWalkAPI
    public void setStatusCodeAndReasonPhrase(int statusCode, String reasonPhrase) {
        if (statusCode < 100) {
            throw new IllegalArgumentException("statusCode can't be less than 100.");
        } else if (statusCode > 599) {
            throw new IllegalArgumentException("statusCode can't be greater than 599.");
        } else if (statusCode > 299 && statusCode < 400) {
            throw new IllegalArgumentException("statusCode can't be in the [300, 399] range.");
        } else if (reasonPhrase == null) {
            throw new IllegalArgumentException("reasonPhrase can't be null.");
        } else if (reasonPhrase.trim().isEmpty()) {
            throw new IllegalArgumentException("reasonPhrase can't be empty.");
        } else {
            for (int i = 0; i < reasonPhrase.length(); i++) {
                if (reasonPhrase.charAt(i) > TransportMediator.KEYCODE_MEDIA_PAUSE) {
                    throw new IllegalArgumentException("reasonPhrase can't contain non-ASCII characters.");
                }
            }
            this.mStatusCode = statusCode;
            this.mReasonPhrase = reasonPhrase;
        }
    }

    @XWalkAPI
    public int getStatusCode() {
        return this.mStatusCode;
    }

    @CalledByNative
    public int getStatusCodeNative() {
        return this.mStatusCode;
    }

    @XWalkAPI
    public String getReasonPhrase() {
        return this.mReasonPhrase;
    }

    @CalledByNative
    public String getReasonPhraseNative() {
        return this.mReasonPhrase;
    }

    @XWalkAPI
    public void setResponseHeaders(Map<String, String> headers) {
        this.mResponseHeaders = headers;
    }

    @XWalkAPI
    public Map<String, String> getResponseHeaders() {
        return this.mResponseHeaders;
    }

    @CalledByNative
    private String[] getResponseHeaderNames() {
        fillInResponseHeaderNamesAndValuesIfNeeded();
        return this.mResponseHeaderNames;
    }

    @CalledByNative
    private String[] getResponseHeaderValues() {
        fillInResponseHeaderNamesAndValuesIfNeeded();
        return this.mResponseHeaderValues;
    }
}
