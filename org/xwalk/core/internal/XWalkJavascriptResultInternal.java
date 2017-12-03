package org.xwalk.core.internal;

@XWalkAPI(instance = XWalkJavascriptResultHandlerInternal.class)
public interface XWalkJavascriptResultInternal {
    @XWalkAPI
    void cancel();

    @XWalkAPI
    void confirm();

    @XWalkAPI
    void confirmWithResult(String str);
}
