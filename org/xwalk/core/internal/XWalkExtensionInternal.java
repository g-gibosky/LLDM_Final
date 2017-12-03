package org.xwalk.core.internal;

import org.xwalk.core.internal.extensions.XWalkExtensionAndroid;

@XWalkAPI
public abstract class XWalkExtensionInternal extends XWalkExtensionAndroid {
    @XWalkAPI
    public abstract void onMessage(int i, String str);

    @XWalkAPI
    public abstract String onSyncMessage(int i, String str);

    @XWalkAPI
    public XWalkExtensionInternal(String name, String jsApi) {
        super(name, jsApi);
    }

    @XWalkAPI
    public XWalkExtensionInternal(String name, String jsApi, String[] entryPoints) {
        super(name, jsApi, entryPoints);
    }

    protected void destroyExtension() {
        super.destroyExtension();
    }

    @XWalkAPI
    public void postMessage(int instanceID, String message) {
        super.postMessage(instanceID, message);
    }

    @XWalkAPI
    public void postBinaryMessage(int instanceID, byte[] message) {
        super.postBinaryMessage(instanceID, message);
    }

    @XWalkAPI
    public void broadcastMessage(String message) {
        super.broadcastMessage(message);
    }

    @XWalkAPI
    public void onInstanceCreated(int instanceID) {
    }

    @XWalkAPI
    public void onInstanceDestroyed(int instanceID) {
    }

    @XWalkAPI
    public void onBinaryMessage(int instanceID, byte[] message) {
    }
}
