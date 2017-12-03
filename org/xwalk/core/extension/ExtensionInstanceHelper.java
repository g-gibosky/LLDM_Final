package org.xwalk.core.extension;

public class ExtensionInstanceHelper {
    XWalkExternalExtension mExtension;
    MessageHandler mHandler = new MessageHandler(this.mExtension.getMessageHandler());
    int mId;
    private BindingObjectStore mStore;

    public ExtensionInstanceHelper(XWalkExternalExtension extension, int id) {
        this.mId = id;
        this.mExtension = extension;
        if (this.mExtension.isAutoJS()) {
            ReflectionHelper.registerHandlers(this.mExtension.getReflection(), this.mHandler, this.mExtension);
        }
        this.mStore = new BindingObjectStore(this.mHandler, this);
    }

    public int getId() {
        return this.mId;
    }

    public XWalkExternalExtension getExtension() {
        return this.mExtension;
    }

    public BindingObject getBindingObject(String objectId) {
        return this.mStore.getBindingObject(objectId);
    }

    public boolean addBindingObject(String objectId, BindingObject obj) {
        return this.mStore.addBindingObject(objectId, obj);
    }

    public BindingObject removeBindingObject(String objectId) {
        return this.mStore.removeBindingObject(objectId);
    }

    public Object handleMessage(String message) {
        return this.mHandler.handleMessage(new MessageInfo(this.mExtension, this.mId, message));
    }

    public Object handleMessage(byte[] message) {
        return this.mHandler.handleMessage(new MessageInfo(this.mExtension, this.mId, message));
    }
}
