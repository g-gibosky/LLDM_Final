package org.xwalk.core.extension;

import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONException;

public class BindingObjectStore {
    private String TAG = "BindingObjectStore";
    private Map<String, BindingObject> mBindingObjects = new HashMap();
    private ExtensionInstanceHelper mInstance;

    public BindingObjectStore(MessageHandler handler, ExtensionInstanceHelper instance) {
        this.mInstance = instance;
        handler.register("JSObjectCollected", "onJSObjectCollected", this);
        handler.register(JsStubGenerator.MSG_TO_OBJECT, "onPostMessageToObject", this);
        handler.register(JsStubGenerator.MSG_TO_CLASS, "onPostMessageToClass", this);
    }

    public boolean addBindingObject(String objectId, BindingObject obj) {
        if (this.mBindingObjects.containsKey(objectId)) {
            Log.w(this.TAG, "Existing binding object:\n" + objectId);
            return false;
        }
        obj.initBindingInfo(objectId, this.mInstance);
        this.mBindingObjects.put(objectId, obj);
        obj.onJsBound();
        return true;
    }

    public BindingObject getBindingObject(String objectId) {
        return (BindingObject) this.mBindingObjects.get(objectId);
    }

    public BindingObject removeBindingObject(String objectId) {
        BindingObject obj = (BindingObject) this.mBindingObjects.remove(objectId);
        if (obj != null) {
            obj.onJsDestroyed();
        }
        return obj;
    }

    public void onJSObjectCollected(MessageInfo info) {
        removeBindingObject(info.getObjectId());
    }

    public Object onPostMessageToClass(MessageInfo info) {
        Object result = null;
        JSONArray args = info.getArgs();
        try {
            MessageInfo newInfo = new MessageInfo(info);
            String memberName = args.getString(0);
            JSONArray originArgs = args.getJSONArray(1);
            String ctorName = originArgs.getString(0);
            JSONArray memberArgs = originArgs.getJSONArray(1);
            newInfo.setJsName(memberName);
            newInfo.setArgs(memberArgs);
            result = info.getExtension().getTargetReflect(ctorName).handleMessage(newInfo, null);
        } catch (JSONException e) {
            Log.e(this.TAG, e.toString());
        } catch (Exception e2) {
            Log.e(this.TAG, e2.toString());
        }
        return result;
    }

    public Object onPostMessageToObject(MessageInfo info) {
        Exception e;
        try {
            BindingObject obj = getBindingObject(info.getObjectId());
            MessageInfo newInfo = new MessageInfo(info);
            String objectMethodName;
            if (info.getArgs() != null) {
                JSONArray args = info.getArgs();
                objectMethodName = args.getString(0);
                JSONArray objectMethodArgs = args.getJSONArray(1);
                newInfo.setJsName(objectMethodName);
                newInfo.setArgs(objectMethodArgs);
            } else {
                ByteBuffer args2 = info.getBinaryArgs();
                args2.order(ByteOrder.LITTLE_ENDIAN);
                int byteOffset = args2.position();
                int methodNameLen = args2.getInt(byteOffset);
                byteOffset += 4;
                int alignedMethodNameLen = methodNameLen + (4 - (methodNameLen % 4));
                objectMethodName = new String(args2.array(), byteOffset, methodNameLen);
                byteOffset += alignedMethodNameLen;
                ByteBuffer objectMethodArgs2 = ByteBuffer.wrap(args2.array(), byteOffset, args2.array().length - byteOffset);
                newInfo.setJsName(objectMethodName);
                newInfo.setBinaryArgs(objectMethodArgs2);
            }
            if (obj != null) {
                return obj.handleMessage(newInfo);
            }
            return null;
        } catch (JSONException e2) {
            e = e2;
            Log.e(this.TAG, e.toString());
            return null;
        } catch (IndexOutOfBoundsException e3) {
            e = e3;
            Log.e(this.TAG, e.toString());
            return null;
        } catch (NullPointerException e4) {
            e = e4;
            Log.e(this.TAG, e.toString());
            return null;
        }
    }

    public void onStart() {
        for (Entry<String, BindingObject> entry : this.mBindingObjects.entrySet()) {
            ((BindingObject) entry.getValue()).onStart();
        }
    }

    public void onResume() {
        for (Entry<String, BindingObject> entry : this.mBindingObjects.entrySet()) {
            ((BindingObject) entry.getValue()).onResume();
        }
    }

    public void onPause() {
        for (Entry<String, BindingObject> entry : this.mBindingObjects.entrySet()) {
            ((BindingObject) entry.getValue()).onPause();
        }
    }

    public void onStop() {
        for (Entry<String, BindingObject> entry : this.mBindingObjects.entrySet()) {
            ((BindingObject) entry.getValue()).onStop();
        }
    }

    public void onDestroy() {
        for (Entry<String, BindingObject> entry : this.mBindingObjects.entrySet()) {
            ((BindingObject) entry.getValue()).onDestroy();
        }
    }
}
