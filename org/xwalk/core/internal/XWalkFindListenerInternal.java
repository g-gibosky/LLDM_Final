package org.xwalk.core.internal;

@XWalkAPI(createExternally = true)
public abstract class XWalkFindListenerInternal {
    @XWalkAPI
    public abstract void onFindResultReceived(int i, int i2, boolean z);
}
