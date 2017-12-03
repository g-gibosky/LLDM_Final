package org.xwalk.core.extension;

import android.os.Build.VERSION;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.chromium.content.common.ContentSwitches;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageInfo {
    private String TAG = "MessageInfo";
    private JSONArray mArgs;
    private ByteBuffer mBinaryArgs;
    private String mCallbackId;
    private String mCmd;
    private XWalkExternalExtension mExtension;
    private int mInstanceId;
    private String mJsName;
    private String mObjectId;

    private int AlignedWith4Bytes(int length) {
        return (4 - (length % 4)) + length;
    }

    public MessageInfo(MessageInfo info) {
        this.mExtension = info.mExtension;
        this.mInstanceId = info.mInstanceId;
        this.mJsName = info.mJsName;
        this.mCallbackId = info.mCallbackId;
        this.mObjectId = info.mObjectId;
        this.mArgs = info.mArgs;
        this.mCmd = info.mCmd;
    }

    public MessageInfo(XWalkExternalExtension extension, int instanceId, String message) {
        this.mExtension = extension;
        this.mInstanceId = instanceId;
        if (message.trim().charAt(0) == '[') {
            try {
                this.mArgs = new JSONArray(message);
                this.mCmd = "invokeNative";
                this.mJsName = this.mArgs.getString(0);
                this.mCallbackId = this.mArgs.getString(1);
                this.mObjectId = this.mArgs.getString(2);
                if (VERSION.SDK_INT >= 19) {
                    this.mArgs.remove(0);
                    this.mArgs.remove(0);
                    this.mArgs.remove(0);
                    return;
                }
                return;
            } catch (JSONException e) {
                Log.e(this.TAG, e.toString());
                return;
            }
        }
        try {
            JSONObject m = new JSONObject(message);
            String cmd = m.getString("cmd");
            int objectId = m.getInt("objectId");
            this.mCmd = cmd;
            this.mObjectId = Integer.toString(objectId);
            this.mCallbackId = Integer.toString(0);
            String msgType = m.getString(ContentSwitches.SWITCH_PROCESS_TYPE);
            this.mArgs = new JSONArray();
            if (msgType.equals(JsStubGenerator.MSG_TO_EXTENSION)) {
                this.mArgs = m.getJSONArray("args");
                this.mJsName = m.getString("name");
                if (this.mCmd.equals("newInstance")) {
                    this.mObjectId = this.mArgs.getString(0);
                    this.mArgs = this.mArgs.getJSONArray(1);
                    return;
                }
                return;
            }
            this.mJsName = msgType;
            this.mArgs.put(0, m.getString("name"));
            this.mArgs.put(1, m.getJSONArray("args"));
        } catch (JSONException e2) {
            Log.e(this.TAG, e2.toString());
        }
    }

    public MessageInfo(XWalkExternalExtension extension, int instanceId, byte[] message) {
        this.mExtension = extension;
        this.mInstanceId = instanceId;
        this.mCmd = "invokeNative";
        try {
            this.mArgs = null;
            ByteBuffer buf = ByteBuffer.wrap(message);
            if (buf.order() != ByteOrder.LITTLE_ENDIAN) {
                buf.order(ByteOrder.LITTLE_ENDIAN);
            }
            int byteOffset = buf.position();
            int funcNameLen = buf.getInt(byteOffset);
            int alignedFuncNameLen = AlignedWith4Bytes(funcNameLen);
            byteOffset += 4;
            this.mJsName = new String(message, byteOffset, funcNameLen);
            byteOffset += alignedFuncNameLen;
            this.mCallbackId = Integer.toString(buf.getInt(byteOffset));
            byteOffset += 4;
            int objectIdLen = buf.getInt(byteOffset);
            int alignedObjectIdLen = AlignedWith4Bytes(objectIdLen);
            byteOffset += 4;
            this.mObjectId = new String(message, byteOffset, objectIdLen);
            byteOffset += alignedObjectIdLen;
            this.mBinaryArgs = ByteBuffer.wrap(message, byteOffset, message.length - byteOffset);
        } catch (IndexOutOfBoundsException e) {
            Log.e(this.TAG, e.toString());
        } catch (NullPointerException e2) {
            Log.e(this.TAG, e2.toString());
        }
    }

    public String getJsName() {
        return this.mJsName;
    }

    public void setJsName(String JsName) {
        this.mJsName = JsName;
    }

    public JSONArray getArgs() {
        return this.mArgs;
    }

    public void setArgs(JSONArray args) {
        this.mArgs = args;
    }

    public ByteBuffer getBinaryArgs() {
        return this.mBinaryArgs;
    }

    public void setBinaryArgs(ByteBuffer args) {
        this.mBinaryArgs = args;
    }

    public String getObjectId() {
        return this.mObjectId;
    }

    public void setObjectId(String objectId) {
        this.mObjectId = objectId;
    }

    public String getCallbackId() {
        return this.mCallbackId;
    }

    public void setCallbackId(String callbackId) {
        this.mCallbackId = callbackId;
    }

    public String getCmd() {
        return this.mCmd;
    }

    public void postResult(JSONArray args) {
        try {
            JSONArray result = new JSONArray();
            result.put(0, this.mCallbackId);
            for (int i = 0; i < args.length(); i++) {
                result.put(i + 1, args.get(i));
            }
            Log.w(this.TAG, "postResult: " + result.toString());
            this.mExtension.postMessage(this.mInstanceId, result.toString());
        } catch (JSONException e) {
            Log.e(this.TAG, e.toString());
        }
    }

    public XWalkExternalExtension getExtension() {
        return this.mExtension;
    }

    public int getInstanceId() {
        return this.mInstanceId;
    }

    public ExtensionInstanceHelper getInstanceHelper() {
        return this.mExtension.getInstanceHelper(this.mInstanceId);
    }

    public void postResult(byte[] buffer) {
        this.mExtension.postBinaryMessage(this.mInstanceId, buffer);
    }
}
