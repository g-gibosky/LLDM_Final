package org.xwalk.core.internal;

import java.io.InputStream;
import java.util.Map;

public class XWalkWebResourceResponseBridge extends XWalkWebResourceResponseInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod getDataMethod = new ReflectMethod(null, "getData", new Class[0]);
    private ReflectMethod getEncodingMethod = new ReflectMethod(null, "getEncoding", new Class[0]);
    private ReflectMethod getMimeTypeMethod = new ReflectMethod(null, "getMimeType", new Class[0]);
    private ReflectMethod getReasonPhraseMethod = new ReflectMethod(null, "getReasonPhrase", new Class[0]);
    private ReflectMethod getResponseHeadersMethod = new ReflectMethod(null, "getResponseHeaders", new Class[0]);
    private ReflectMethod getStatusCodeMethod = new ReflectMethod(null, "getStatusCode", new Class[0]);
    private XWalkWebResourceResponseInternal internal;
    private ReflectMethod setDataInputStreamMethod = new ReflectMethod(null, "setData", new Class[0]);
    private ReflectMethod setEncodingStringMethod = new ReflectMethod(null, "setEncoding", new Class[0]);
    private ReflectMethod setMimeTypeStringMethod = new ReflectMethod(null, "setMimeType", new Class[0]);
    private ReflectMethod setResponseHeadersMapMethod = new ReflectMethod(null, "setResponseHeaders", new Class[0]);
    private ReflectMethod setStatusCodeAndReasonPhraseintStringMethod = new ReflectMethod(null, "setStatusCodeAndReasonPhrase", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    XWalkWebResourceResponseBridge(XWalkWebResourceResponseInternal internal) {
        this.internal = internal;
        reflectionInit();
    }

    public void setMimeType(String mimeType) {
        if (this.setMimeTypeStringMethod == null || this.setMimeTypeStringMethod.isNull()) {
            setMimeTypeSuper(mimeType);
            return;
        }
        this.setMimeTypeStringMethod.invoke(mimeType);
    }

    public void setMimeTypeSuper(String mimeType) {
        if (this.internal == null) {
            super.setMimeType(mimeType);
        } else {
            this.internal.setMimeType(mimeType);
        }
    }

    public String getMimeType() {
        if (this.getMimeTypeMethod == null || this.getMimeTypeMethod.isNull()) {
            return getMimeTypeSuper();
        }
        return (String) this.getMimeTypeMethod.invoke(new Object[0]);
    }

    public String getMimeTypeSuper() {
        String ret;
        if (this.internal == null) {
            ret = super.getMimeType();
        } else {
            ret = this.internal.getMimeType();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setEncoding(String encoding) {
        if (this.setEncodingStringMethod == null || this.setEncodingStringMethod.isNull()) {
            setEncodingSuper(encoding);
            return;
        }
        this.setEncodingStringMethod.invoke(encoding);
    }

    public void setEncodingSuper(String encoding) {
        if (this.internal == null) {
            super.setEncoding(encoding);
        } else {
            this.internal.setEncoding(encoding);
        }
    }

    public String getEncoding() {
        if (this.getEncodingMethod == null || this.getEncodingMethod.isNull()) {
            return getEncodingSuper();
        }
        return (String) this.getEncodingMethod.invoke(new Object[0]);
    }

    public String getEncodingSuper() {
        String ret;
        if (this.internal == null) {
            ret = super.getEncoding();
        } else {
            ret = this.internal.getEncoding();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setData(InputStream data) {
        if (this.setDataInputStreamMethod == null || this.setDataInputStreamMethod.isNull()) {
            setDataSuper(data);
            return;
        }
        this.setDataInputStreamMethod.invoke(data);
    }

    public void setDataSuper(InputStream data) {
        if (this.internal == null) {
            super.setData(data);
        } else {
            this.internal.setData(data);
        }
    }

    public InputStream getData() {
        if (this.getDataMethod == null || this.getDataMethod.isNull()) {
            return getDataSuper();
        }
        return (InputStream) this.getDataMethod.invoke(new Object[0]);
    }

    public InputStream getDataSuper() {
        InputStream ret;
        if (this.internal == null) {
            ret = super.getData();
        } else {
            ret = this.internal.getData();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setStatusCodeAndReasonPhrase(int statusCode, String reasonPhrase) {
        if (this.setStatusCodeAndReasonPhraseintStringMethod == null || this.setStatusCodeAndReasonPhraseintStringMethod.isNull()) {
            setStatusCodeAndReasonPhraseSuper(statusCode, reasonPhrase);
            return;
        }
        this.setStatusCodeAndReasonPhraseintStringMethod.invoke(Integer.valueOf(statusCode), reasonPhrase);
    }

    public void setStatusCodeAndReasonPhraseSuper(int statusCode, String reasonPhrase) {
        if (this.internal == null) {
            super.setStatusCodeAndReasonPhrase(statusCode, reasonPhrase);
        } else {
            this.internal.setStatusCodeAndReasonPhrase(statusCode, reasonPhrase);
        }
    }

    public int getStatusCode() {
        if (this.getStatusCodeMethod == null || this.getStatusCodeMethod.isNull()) {
            return getStatusCodeSuper();
        }
        return ((Integer) this.getStatusCodeMethod.invoke(new Object[0])).intValue();
    }

    public int getStatusCodeSuper() {
        if (this.internal == null) {
            return super.getStatusCode();
        }
        return this.internal.getStatusCode();
    }

    public String getReasonPhrase() {
        if (this.getReasonPhraseMethod == null || this.getReasonPhraseMethod.isNull()) {
            return getReasonPhraseSuper();
        }
        return (String) this.getReasonPhraseMethod.invoke(new Object[0]);
    }

    public String getReasonPhraseSuper() {
        String ret;
        if (this.internal == null) {
            ret = super.getReasonPhrase();
        } else {
            ret = this.internal.getReasonPhrase();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setResponseHeaders(Map<String, String> headers) {
        if (this.setResponseHeadersMapMethod == null || this.setResponseHeadersMapMethod.isNull()) {
            setResponseHeadersSuper(headers);
            return;
        }
        this.setResponseHeadersMapMethod.invoke(headers);
    }

    public void setResponseHeadersSuper(Map<String, String> headers) {
        if (this.internal == null) {
            super.setResponseHeaders(headers);
        } else {
            this.internal.setResponseHeaders(headers);
        }
    }

    public Map<String, String> getResponseHeaders() {
        if (this.getResponseHeadersMethod == null || this.getResponseHeadersMethod.isNull()) {
            return getResponseHeadersSuper();
        }
        return (Map) this.getResponseHeadersMethod.invoke(new Object[0]);
    }

    public Map<String, String> getResponseHeadersSuper() {
        Map<String, String> ret;
        if (this.internal == null) {
            ret = super.getResponseHeaders();
        } else {
            ret = this.internal.getResponseHeaders();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            try {
                this.wrapper = new ReflectConstructor(this.coreBridge.getWrapperClass("XWalkWebResourceResponse"), Object.class).newInstance(this);
                this.setMimeTypeStringMethod.init(this.wrapper, null, "setMimeType", String.class);
                this.getMimeTypeMethod.init(this.wrapper, null, "getMimeType", new Class[0]);
                this.setEncodingStringMethod.init(this.wrapper, null, "setEncoding", String.class);
                this.getEncodingMethod.init(this.wrapper, null, "getEncoding", new Class[0]);
                this.setDataInputStreamMethod.init(this.wrapper, null, "setData", InputStream.class);
                this.getDataMethod.init(this.wrapper, null, "getData", new Class[0]);
                this.setStatusCodeAndReasonPhraseintStringMethod.init(this.wrapper, null, "setStatusCodeAndReasonPhrase", Integer.TYPE, String.class);
                this.getStatusCodeMethod.init(this.wrapper, null, "getStatusCode", new Class[0]);
                this.getReasonPhraseMethod.init(this.wrapper, null, "getReasonPhrase", new Class[0]);
                this.setResponseHeadersMapMethod.init(this.wrapper, null, "setResponseHeaders", Map.class);
                this.getResponseHeadersMethod.init(this.wrapper, null, "getResponseHeaders", new Class[0]);
            } catch (UnsupportedOperationException e) {
            }
        }
    }
}
