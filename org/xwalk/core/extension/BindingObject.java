package org.xwalk.core.extension;

public class BindingObject {
    private String TAG = "BindingObject";
    protected MessageHandler mHandler = new MessageHandler();
    protected ExtensionInstanceHelper mInstanceHelper;
    protected String mObjectId;

    public Object handleMessage(MessageInfo info) {
        return this.mHandler.handleMessage(info);
    }

    public void initBindingInfo(String objectId, ExtensionInstanceHelper instance) {
        this.mObjectId = objectId;
        this.mInstanceHelper = instance;
    }

    public void onJsDestroyed() {
    }

    public void onJsBound() {
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }
}
