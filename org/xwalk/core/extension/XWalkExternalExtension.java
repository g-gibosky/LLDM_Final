package org.xwalk.core.extension;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import org.chromium.content.common.ContentSwitches;
import org.json.JSONObject;

public class XWalkExternalExtension {
    static final /* synthetic */ boolean $assertionsDisabled = (!XWalkExternalExtension.class.desiredAssertionStatus());
    private Map<Integer, ExtensionInstanceHelper> instanceHelpers;
    protected String[] mEntryPoints;
    protected XWalkExtensionContextClient mExtensionContext;
    protected MessageHandler mHandler;
    protected String mJsApi;
    protected String mName;
    protected ReflectionHelper mReflection;
    protected boolean useJsStubGeneration;

    public XWalkExternalExtension(String name, String jsApi, XWalkExtensionContextClient context) {
        this(name, jsApi, null, context);
    }

    public XWalkExternalExtension(String name, String jsApi, String[] entryPoints, XWalkExtensionContextClient context) {
        if ($assertionsDisabled || context != null) {
            this.mName = name;
            this.mJsApi = jsApi;
            this.mEntryPoints = entryPoints;
            this.mExtensionContext = context;
            this.instanceHelpers = new HashMap();
            this.mHandler = new MessageHandler();
            if (this.mJsApi == null || this.mJsApi.length() == 0) {
                this.useJsStubGeneration = true;
                this.mReflection = new ReflectionHelper(getClass());
                this.mJsApi = new JsStubGenerator(this.mReflection).generate();
                if (this.mJsApi == null || this.mJsApi.length() == 0) {
                    Log.e("Extension-" + this.mName, "Can't generate JavaScript stub for this extension.");
                    return;
                }
            }
            this.mReflection = null;
            this.useJsStubGeneration = false;
            this.mExtensionContext.registerExtension(this);
            return;
        }
        throw new AssertionError();
    }

    public final String getExtensionName() {
        return this.mName;
    }

    public final String getJsApi() {
        return this.mJsApi;
    }

    public final String[] getEntryPoints() {
        return this.mEntryPoints;
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

    public void onNewIntent(Intent intent) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onInstanceCreated(int instanceID) {
        this.instanceHelpers.put(Integer.valueOf(instanceID), new ExtensionInstanceHelper(this, instanceID));
    }

    public void onInstanceDestroyed(int instanceID) {
        this.instanceHelpers.remove(Integer.valueOf(instanceID));
    }

    public boolean isAutoJS() {
        return this.useJsStubGeneration;
    }

    public void onMessage(int extensionInstanceID, String message) {
        if (this.useJsStubGeneration) {
            getInstanceHelper(extensionInstanceID).handleMessage(message);
        }
    }

    public void onBinaryMessage(int extensionInstanceID, byte[] message) {
        if (this.useJsStubGeneration) {
            getInstanceHelper(extensionInstanceID).handleMessage(message);
        }
    }

    public String onSyncMessage(int extensionInstanceID, String message) {
        Object result = null;
        if (this.useJsStubGeneration) {
            result = getInstanceHelper(extensionInstanceID).handleMessage(message);
        }
        return result != null ? ReflectionHelper.objToJSON(result) : "";
    }

    public ReflectionHelper getReflection() {
        return this.mReflection;
    }

    public MessageHandler getMessageHandler() {
        return this.mHandler;
    }

    public ReflectionHelper getTargetReflect(String cName) {
        ReflectionHelper targetReflect = this.mReflection.getConstructorReflection(cName);
        return targetReflect != null ? targetReflect : this.mReflection;
    }

    protected ExtensionInstanceHelper getInstanceHelper(int instanceId) {
        return (ExtensionInstanceHelper) this.instanceHelpers.get(Integer.valueOf(instanceId));
    }

    public void sendEvent(String type, Object event) {
        try {
            JSONObject msgOut = new JSONObject();
            msgOut.put("cmd", "onEvent");
            msgOut.put(ContentSwitches.SWITCH_PROCESS_TYPE, type);
            msgOut.put("event", ReflectionHelper.objToJSON(event));
            broadcastMessage(msgOut.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void postMessage(int instanceID, String message) {
        this.mExtensionContext.postMessage(this, instanceID, message);
    }

    public final void postBinaryMessage(int instanceID, byte[] message) {
        this.mExtensionContext.postBinaryMessage(this, instanceID, message);
    }

    public final void broadcastMessage(String message) {
        this.mExtensionContext.broadcastMessage(this, message);
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        throw new ActivityNotFoundException("This method is no longer supported");
    }
}
