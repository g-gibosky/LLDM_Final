package org.xwalk.core.internal;

import android.content.Context;
import android.net.http.SslError;
import android.os.Message;

public class XWalkClient {
    private Context mContext;
    private XWalkViewInternal mXWalkView;

    public XWalkClient(XWalkViewInternal view) {
        this.mContext = view.getContext();
        this.mXWalkView = view;
    }

    public void onRendererUnresponsive(XWalkViewInternal view) {
    }

    public void onRendererResponsive(XWalkViewInternal view) {
    }

    @Deprecated
    public void onTooManyRedirects(XWalkViewInternal view, Message cancelMsg, Message continueMsg) {
        cancelMsg.sendToTarget();
    }

    public void onFormResubmission(XWalkViewInternal view, Message dontResend, Message resend) {
        dontResend.sendToTarget();
    }

    public void doUpdateVisitedHistory(XWalkViewInternal view, String url, boolean isReload) {
    }

    public void onProceededAfterSslError(XWalkViewInternal view, SslError error) {
    }

    public void onReceivedLoginRequest(XWalkViewInternal view, String realm, String account, String args) {
    }

    public void onLoadResource(XWalkViewInternal view, String url) {
    }
}
