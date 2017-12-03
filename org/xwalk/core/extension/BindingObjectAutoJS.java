package org.xwalk.core.extension;

import android.util.Log;
import org.chromium.content.common.ContentSwitches;
import org.json.JSONArray;
import org.json.JSONObject;

public class BindingObjectAutoJS extends BindingObject {
    public Object handleMessage(MessageInfo info) {
        Object result = null;
        try {
            result = this.mInstanceHelper.getExtension().getReflection().getReflectionByBindingClass(getClass().getName()).handleMessage(info, this);
        } catch (Exception e) {
            Log.e("BindingObjectAutoJs", e.toString());
        }
        return result;
    }

    public JsContextInfo getJsContextInfo() {
        return new JsContextInfo(this.mInstanceHelper.getId(), this.mInstanceHelper.getExtension(), getClass(), this.mObjectId);
    }

    public static void invokeJsCallback(JsContextInfo mInfo, String callbackId, Object... args) {
        JSONArray jsArgs;
        Object[] arr = args;
        if (arr.length == 1 && (arr[0] instanceof JSONArray)) {
            jsArgs = (JSONArray) arr[0];
        } else {
            jsArgs = (JSONArray) ReflectionHelper.toSerializableObject(args);
        }
        try {
            JSONObject msgOut = new JSONObject();
            msgOut.put("cmd", "invokeCallback");
            msgOut.put("callbackId", callbackId);
            msgOut.put("args", jsArgs);
            mInfo.postMessage(msgOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void invokeJsCallback(String callbackId, Object... args) {
        invokeJsCallback(getJsContextInfo(), callbackId, args);
    }

    public static void invokeJsCallback(JsContextInfo mInfo, byte[] buffer) {
        mInfo.postMessage(buffer);
    }

    public void invokeJsCallback(byte[] buffer) {
        getJsContextInfo().postMessage(buffer);
    }

    public static void dispatchEvent(JsContextInfo mInfo, String type, Object event) {
        if (mInfo.getTargetReflect().isEventSupported(type)) {
            try {
                JSONObject msgOut = new JSONObject();
                msgOut.put("cmd", "dispatchEvent");
                msgOut.put("constructorName", mInfo.getConstructorName());
                msgOut.put("objectId", mInfo.getObjectId());
                msgOut.put(ContentSwitches.SWITCH_PROCESS_TYPE, type);
                msgOut.put("event", ReflectionHelper.toSerializableObject(event));
                mInfo.postMessage(msgOut);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        Log.w(mInfo.getTag(), "Unsupport event in extension: " + type);
    }

    public void dispatchEvent(String type, Object event) {
        dispatchEvent(getJsContextInfo(), type, event);
    }

    public static void sendEvent(JsContextInfo mInfo, String type, Object event) {
        mInfo.getExtensionClient().sendEvent(type, event);
    }

    public void sendEvent(String type, Object event) {
        sendEvent(getJsContextInfo(), type, event);
    }

    public static void updateProperty(JsContextInfo mInfo, String pName) {
        ReflectionHelper targetReflect = mInfo.getTargetReflect();
        if (targetReflect.hasProperty(pName).booleanValue()) {
            boolean isStatic = targetReflect.getMemberInfo(pName).isStatic;
            try {
                JSONObject msgOut = new JSONObject();
                msgOut.put("cmd", "updateProperty");
                msgOut.put("objectId", isStatic ? "0" : mInfo.getObjectId());
                msgOut.put("constructorName", mInfo.getConstructorName());
                msgOut.put("name", pName);
                mInfo.postMessage(msgOut);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        Log.w(mInfo.getTag(), "Unexposed property in extension: " + pName);
    }

    public void updateProperty(String pName) {
        updateProperty(getJsContextInfo(), pName);
    }
}
