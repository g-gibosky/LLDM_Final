package org.xwalk.core.extension;

import org.json.JSONObject;

public class JsContextInfo {
    private int extInstanceId;
    private XWalkExternalExtension extensionClient;
    private String objectId;
    private Class<?> targetClass;

    JsContextInfo(int instanceId, XWalkExternalExtension ext, Class<?> tClass, String objId) {
        this.extensionClient = ext;
        this.extInstanceId = instanceId;
        this.objectId = objId;
        this.targetClass = tClass;
    }

    public String getTag() {
        return "Extension-" + this.extensionClient.getExtensionName();
    }

    public ReflectionHelper getTargetReflect() {
        return this.extensionClient.getTargetReflect(this.targetClass.getSimpleName());
    }

    public String getObjectId() {
        return this.objectId;
    }

    public XWalkExternalExtension getExtensionClient() {
        return this.extensionClient;
    }

    public String getConstructorName() {
        return this.targetClass.getSimpleName();
    }

    public void postMessage(JSONObject msg) {
        this.extensionClient.postMessage(this.extInstanceId, msg.toString());
    }

    public void postMessage(byte[] buffer) {
        this.extensionClient.postBinaryMessage(this.extInstanceId, buffer);
    }
}
