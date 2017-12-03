package org.xwalk.core.internal;

import android.content.Context;

@XWalkAPI(createExternally = true)
public abstract class XWalkDownloadListenerInternal {
    @XWalkAPI
    public abstract void onDownloadStart(String str, String str2, String str3, String str4, long j);

    @XWalkAPI
    public XWalkDownloadListenerInternal(Context context) {
    }
}
