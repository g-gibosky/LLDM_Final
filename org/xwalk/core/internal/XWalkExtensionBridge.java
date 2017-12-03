package org.xwalk.core.internal;

public class XWalkExtensionBridge extends XWalkExtensionInternal {
    private ReflectMethod broadcastMessageStringMethod;
    private XWalkCoreBridge coreBridge;
    private ReflectMethod onBinaryMessageintbyteArrayMethod;
    private ReflectMethod onInstanceCreatedintMethod;
    private ReflectMethod onInstanceDestroyedintMethod;
    private ReflectMethod onMessageintStringMethod;
    private ReflectMethod onSyncMessageintStringMethod;
    private ReflectMethod postBinaryMessageintbyteArrayMethod;
    private ReflectMethod postMessageintStringMethod;
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkExtensionBridge(String name, String jsApi, Object wrapper) {
        super(name, jsApi);
        this.postMessageintStringMethod = new ReflectMethod(null, "postMessage", new Class[0]);
        this.postBinaryMessageintbyteArrayMethod = new ReflectMethod(null, "postBinaryMessage", new Class[0]);
        this.broadcastMessageStringMethod = new ReflectMethod(null, "broadcastMessage", new Class[0]);
        this.onInstanceCreatedintMethod = new ReflectMethod(null, "onInstanceCreated", new Class[0]);
        this.onInstanceDestroyedintMethod = new ReflectMethod(null, "onInstanceDestroyed", new Class[0]);
        this.onBinaryMessageintbyteArrayMethod = new ReflectMethod(null, "onBinaryMessage", new Class[0]);
        this.onMessageintStringMethod = new ReflectMethod(null, "onMessage", new Class[0]);
        this.onSyncMessageintStringMethod = new ReflectMethod(null, "onSyncMessage", new Class[0]);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public XWalkExtensionBridge(String name, String jsApi, String[] entryPoints, Object wrapper) {
        super(name, jsApi, entryPoints);
        this.postMessageintStringMethod = new ReflectMethod(null, "postMessage", new Class[0]);
        this.postBinaryMessageintbyteArrayMethod = new ReflectMethod(null, "postBinaryMessage", new Class[0]);
        this.broadcastMessageStringMethod = new ReflectMethod(null, "broadcastMessage", new Class[0]);
        this.onInstanceCreatedintMethod = new ReflectMethod(null, "onInstanceCreated", new Class[0]);
        this.onInstanceDestroyedintMethod = new ReflectMethod(null, "onInstanceDestroyed", new Class[0]);
        this.onBinaryMessageintbyteArrayMethod = new ReflectMethod(null, "onBinaryMessage", new Class[0]);
        this.onMessageintStringMethod = new ReflectMethod(null, "onMessage", new Class[0]);
        this.onSyncMessageintStringMethod = new ReflectMethod(null, "onSyncMessage", new Class[0]);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public void postMessage(int instanceID, String message) {
        if (this.postMessageintStringMethod == null || this.postMessageintStringMethod.isNull()) {
            postMessageSuper(instanceID, message);
            return;
        }
        this.postMessageintStringMethod.invoke(Integer.valueOf(instanceID), message);
    }

    public void postMessageSuper(int instanceID, String message) {
        super.postMessage(instanceID, message);
    }

    public void postBinaryMessage(int instanceID, byte[] message) {
        if (this.postBinaryMessageintbyteArrayMethod == null || this.postBinaryMessageintbyteArrayMethod.isNull()) {
            postBinaryMessageSuper(instanceID, message);
            return;
        }
        this.postBinaryMessageintbyteArrayMethod.invoke(Integer.valueOf(instanceID), message);
    }

    public void postBinaryMessageSuper(int instanceID, byte[] message) {
        super.postBinaryMessage(instanceID, message);
    }

    public void broadcastMessage(String message) {
        if (this.broadcastMessageStringMethod == null || this.broadcastMessageStringMethod.isNull()) {
            broadcastMessageSuper(message);
            return;
        }
        this.broadcastMessageStringMethod.invoke(message);
    }

    public void broadcastMessageSuper(String message) {
        super.broadcastMessage(message);
    }

    public void onInstanceCreated(int instanceID) {
        if (this.onInstanceCreatedintMethod == null || this.onInstanceCreatedintMethod.isNull()) {
            onInstanceCreatedSuper(instanceID);
            return;
        }
        this.onInstanceCreatedintMethod.invoke(Integer.valueOf(instanceID));
    }

    public void onInstanceCreatedSuper(int instanceID) {
        super.onInstanceCreated(instanceID);
    }

    public void onInstanceDestroyed(int instanceID) {
        if (this.onInstanceDestroyedintMethod == null || this.onInstanceDestroyedintMethod.isNull()) {
            onInstanceDestroyedSuper(instanceID);
            return;
        }
        this.onInstanceDestroyedintMethod.invoke(Integer.valueOf(instanceID));
    }

    public void onInstanceDestroyedSuper(int instanceID) {
        super.onInstanceDestroyed(instanceID);
    }

    public void onBinaryMessage(int instanceID, byte[] message) {
        if (this.onBinaryMessageintbyteArrayMethod == null || this.onBinaryMessageintbyteArrayMethod.isNull()) {
            onBinaryMessageSuper(instanceID, message);
            return;
        }
        this.onBinaryMessageintbyteArrayMethod.invoke(Integer.valueOf(instanceID), message);
    }

    public void onBinaryMessageSuper(int instanceID, byte[] message) {
        super.onBinaryMessage(instanceID, message);
    }

    public void onMessage(int instanceID, String message) {
        this.onMessageintStringMethod.invoke(Integer.valueOf(instanceID), message);
    }

    public String onSyncMessage(int instanceID, String message) {
        return (String) this.onSyncMessageintStringMethod.invoke(Integer.valueOf(instanceID), message);
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.postMessageintStringMethod.init(this.wrapper, null, "postMessage", Integer.TYPE, String.class);
            this.postBinaryMessageintbyteArrayMethod.init(this.wrapper, null, "postBinaryMessage", Integer.TYPE, byte[].class);
            this.broadcastMessageStringMethod.init(this.wrapper, null, "broadcastMessage", String.class);
            this.onInstanceCreatedintMethod.init(this.wrapper, null, "onInstanceCreated", Integer.TYPE);
            this.onInstanceDestroyedintMethod.init(this.wrapper, null, "onInstanceDestroyed", Integer.TYPE);
            this.onBinaryMessageintbyteArrayMethod.init(this.wrapper, null, "onBinaryMessage", Integer.TYPE, byte[].class);
            this.onMessageintStringMethod.init(this.wrapper, null, "onMessage", Integer.TYPE, String.class);
            this.onSyncMessageintStringMethod.init(this.wrapper, null, "onSyncMessage", Integer.TYPE, String.class);
        }
    }
}
