package org.xwalk.core.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

@XWalkAPI(createExternally = true)
public abstract class XWalkExternalExtensionManagerInternal {
    private XWalkViewInternal mXWalkView;

    @XWalkAPI
    public abstract void onNewIntent(Intent intent);

    @XWalkAPI
    public XWalkExternalExtensionManagerInternal(XWalkViewInternal view) {
        view.setExternalExtensionManager(this);
        this.mXWalkView = view;
    }

    @Deprecated
    @XWalkAPI
    public Activity getViewActivity() {
        Context context = getViewContext();
        if (context instanceof Activity) {
            return (Activity) context;
        }
        return null;
    }

    @XWalkAPI
    public Context getViewContext() {
        if (this.mXWalkView != null) {
            return this.mXWalkView.getViewContext();
        }
        return null;
    }

    @XWalkAPI
    public void loadExtension(String extensionPath) {
    }

    @XWalkAPI
    public void onStart() {
    }

    @XWalkAPI
    public void onResume() {
    }

    @XWalkAPI
    public void onPause() {
    }

    @XWalkAPI
    public void onStop() {
    }

    @XWalkAPI
    public void onDestroy() {
        this.mXWalkView.setExternalExtensionManager(null);
        this.mXWalkView = null;
    }

    @Deprecated
    @XWalkAPI
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
