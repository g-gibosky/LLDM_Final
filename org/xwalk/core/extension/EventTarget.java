package org.xwalk.core.extension;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventTarget extends BindingObject {
    private String TAG = "EventTarget";
    private Map<String, MessageInfo> mEvents = new HashMap();

    public EventTarget() {
        this.mHandler.register("addEventListener", this);
        this.mHandler.register("removeEventListener", this);
    }

    public void startEvent(String type) {
    }

    public void stopEvent(String type) {
    }

    public boolean isEventActive(String type) {
        return this.mEvents.containsKey(type);
    }

    public void dispatchEvent(String type) {
        dispatchEvent(type, null);
    }

    public void dispatchEvent(String type, JSONObject data) {
        try {
            if (this.mEvents.containsKey(type)) {
                MessageInfo info = (MessageInfo) this.mEvents.get(type);
                JSONArray args = new JSONArray();
                if (data != null) {
                    args.put(0, data);
                }
                info.postResult(args);
                return;
            }
            Log.w(this.TAG, "Attempt to dispatch to non-existing event :" + type);
        } catch (JSONException e) {
            Log.e(this.TAG, e.toString());
        }
    }

    public void onAddEventListener(MessageInfo info) {
        try {
            String type = info.getArgs().getString(0);
            if (this.mEvents.containsKey(type)) {
                Log.w(this.TAG, "Trying to re-add the event :" + type);
                return;
            }
            this.mEvents.put(type, info);
            startEvent(type);
        } catch (JSONException e) {
            Log.e(this.TAG, e.toString());
        }
    }

    public void onRemoveEventListener(MessageInfo info) {
        try {
            String type = info.getArgs().getString(0);
            if (this.mEvents.containsKey(type)) {
                stopEvent(type);
                this.mEvents.remove(type);
                return;
            }
            Log.w(this.TAG, "Attempt to remove non-existing event :" + type);
        } catch (JSONException e) {
            Log.e(this.TAG, e.toString());
        }
    }
}
