package org.xwalk.core.extension;

import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.xwalk.core.extension.ReflectionHelper.MemberType;

public class MessageHandler {
    private String TAG = "MessageHandler";
    private Map<String, Handler> mHandlers = new HashMap();

    public class Handler {
        String javaName;
        ReflectionHelper reflection;
        Object targetObject;
        MemberType type;

        public Handler(String javaName, MemberType type, Object object, ReflectionHelper reflection) {
            this.type = type;
            this.javaName = javaName;
            this.targetObject = object;
            this.reflection = reflection;
        }

        public Handler(MessageHandler messageHandler, String javaName, MemberType type, Object object) {
            this(javaName, type, object, null);
        }
    }

    public MessageHandler(MessageHandler sourceHandler) {
        this.mHandlers.putAll(sourceHandler.mHandlers);
    }

    public void register(String jsName, String javaName, MemberType type, Object obj, ReflectionHelper reflection) {
        if (this.mHandlers.containsKey(jsName)) {
            Log.w(this.TAG, "Existing handler for " + jsName);
            return;
        }
        this.mHandlers.put(jsName, new Handler(javaName, type, obj, reflection));
    }

    public void register(String jsName, String javaName, MemberType type, Object obj) {
        register(jsName, javaName, type, obj, null);
    }

    public void register(String jsName, String javaName, Object obj) {
        register(jsName, javaName, MemberType.JS_METHOD, obj, null);
    }

    public void register(String jsName, Object obj) {
        register(jsName, jsName, MemberType.JS_METHOD, obj, null);
    }

    public Object handleMessage(MessageInfo info) {
        Exception e;
        Object result = null;
        String jsName = info.getJsName();
        Handler handler = (Handler) this.mHandlers.get(jsName);
        if (handler == null || handler.targetObject == null) {
            Log.w(this.TAG, "Cannot find handler for method " + jsName);
        } else {
            Object obj = handler.targetObject;
            if (!info.getExtension().isAutoJS() || handler.reflection == null) {
                try {
                    result = obj.getClass().getMethod(handler.javaName, new Class[]{MessageInfo.class}).invoke(obj, new Object[]{info});
                } catch (SecurityException e2) {
                    e = e2;
                    Log.e(this.TAG, e.toString());
                    return result;
                } catch (InvocationTargetException e3) {
                    e = e3;
                    Log.e(this.TAG, e.toString());
                    return result;
                } catch (NoSuchMethodException e4) {
                    e = e4;
                    Log.e(this.TAG, e.toString());
                    return result;
                } catch (IllegalArgumentException e5) {
                    e = e5;
                    Log.e(this.TAG, e.toString());
                    return result;
                } catch (IllegalAccessException e6) {
                    e = e6;
                    Log.e(this.TAG, e.toString());
                    return result;
                }
            }
            try {
                result = handler.reflection.handleMessage(info, obj);
            } catch (Exception e7) {
                Log.e(this.TAG, e7.toString());
            }
        }
        return result;
    }
}
