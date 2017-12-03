package org.xwalk.core.extension;

import android.util.Log;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

class ReflectionHelper {
    private static final String TAG = "JsStubReflectHelper";
    static Set<Class<?>> primitives = new HashSet();
    private Map<String, String> bindingClasses = new HashMap();
    private Map<String, ReflectionHelper> constructorReflections = new HashMap();
    private MemberInfo entryPoint = null;
    private String[] eventList = null;
    private Map<String, MemberInfo> members = new HashMap();
    private Class<?> myClass;

    public class MemberInfo {
        AccessibleObject accesser;
        boolean isEntryPoint;
        boolean isStatic;
        boolean isWritable;
        String javaName;
        String jsName;
        Class<?> mainClass;
        MemberType type;
        boolean withPromise;
        String wrapArgs = "";
        String wrapReturns = "";
    }

    public enum MemberType {
        JS_METHOD,
        JS_PROPERTY,
        JS_CONSTRUCTOR
    }

    public ReflectionHelper(Class<?> clazz) {
        this.myClass = clazz;
        init();
    }

    void getMemberInfo(AccessibleObject[] accessers, MemberType type) {
        Exception e;
        for (AccessibleObject a : accessers) {
            if (a.isAnnotationPresent(JsApi.class) || a.isAnnotationPresent(JsConstructor.class)) {
                MemberInfo mInfo = new MemberInfo();
                String name = ((Member) a).getName();
                mInfo.javaName = name;
                mInfo.accesser = a;
                mInfo.isStatic = Modifier.isStatic(((Member) a).getModifiers());
                if (a.isAnnotationPresent(JsApi.class)) {
                    JsApi mAnno = (JsApi) a.getAnnotation(JsApi.class);
                    if (type != MemberType.JS_PROPERTY || !mAnno.isEventList()) {
                        mInfo.type = type;
                        mInfo.isWritable = mAnno.isWritable();
                        mInfo.isEntryPoint = mAnno.isEntryPoint();
                        mInfo.withPromise = mAnno.withPromise();
                        mInfo.jsName = name;
                        mInfo.wrapArgs = mAnno.wrapArgs();
                        mInfo.wrapReturns = mAnno.wrapReturns();
                    } else if (((Field) a).getType().equals(String[].class)) {
                        try {
                            this.eventList = (String[]) ((Field) a).get(null);
                        } catch (IllegalArgumentException e2) {
                            e = e2;
                            e.printStackTrace();
                        } catch (IllegalAccessException e3) {
                            e = e3;
                            e.printStackTrace();
                        }
                    } else {
                        Log.w(TAG, "Invalid type for Supported JS event list" + name);
                    }
                } else if (a.isAnnotationPresent(JsConstructor.class)) {
                    if (type != MemberType.JS_METHOD) {
                        Log.w(TAG, "Invalid @JsConstructor on non-function member:" + name);
                    } else {
                        JsConstructor cAnno = (JsConstructor) a.getAnnotation(JsConstructor.class);
                        mInfo.type = MemberType.JS_CONSTRUCTOR;
                        mInfo.isEntryPoint = cAnno.isEntryPoint();
                        mInfo.mainClass = cAnno.mainClass();
                        mInfo.withPromise = false;
                        if (mInfo.mainClass != null) {
                            mInfo.jsName = mInfo.mainClass.getSimpleName();
                            this.bindingClasses.put(mInfo.mainClass.getName(), mInfo.jsName);
                            this.constructorReflections.put(mInfo.jsName, new ReflectionHelper(mInfo.mainClass));
                        }
                    }
                }
                if (mInfo.isEntryPoint) {
                    if (this.entryPoint != null) {
                        Log.w(TAG, "Entry point already exist, try to set another:" + mInfo.jsName);
                    } else if (type != MemberType.JS_PROPERTY || isBindingClass(((Field) mInfo.accesser).getType())) {
                        this.entryPoint = mInfo;
                    } else {
                        Log.w(TAG, "Invalid entry point setting on property:" + name);
                    }
                }
                if (this.members.containsKey(mInfo.jsName)) {
                    Log.w(TAG, "Conflict namespace - " + mInfo.jsName);
                } else {
                    this.members.put(mInfo.jsName, mInfo);
                }
            }
        }
    }

    boolean isBindingClass(Class<?> clz) {
        return BindingObject.class.isAssignableFrom(clz);
    }

    void init() {
        primitives.add(Byte.class);
        primitives.add(Integer.class);
        primitives.add(Long.class);
        primitives.add(Double.class);
        primitives.add(Character.class);
        primitives.add(Float.class);
        primitives.add(Boolean.class);
        primitives.add(Short.class);
        getMemberInfo(this.myClass.getDeclaredMethods(), MemberType.JS_METHOD);
        getMemberInfo(this.myClass.getDeclaredFields(), MemberType.JS_PROPERTY);
    }

    public static void registerHandlers(ReflectionHelper reflection, MessageHandler handler, Object object) {
        if (reflection != null && handler != null) {
            for (String key : reflection.getMembers().keySet()) {
                MemberInfo m = (MemberInfo) reflection.getMembers().get(key);
                handler.register(m.jsName, m.javaName, m.type, object, reflection);
            }
        }
    }

    Map<String, MemberInfo> getMembers() {
        return this.members;
    }

    ReflectionHelper getConstructorReflection(String jsName) {
        if (this.constructorReflections.containsKey(jsName)) {
            return (ReflectionHelper) this.constructorReflections.get(jsName);
        }
        return null;
    }

    ReflectionHelper getReflectionByBindingClass(String className) {
        if (this.bindingClasses.containsKey(className)) {
            return getConstructorReflection((String) this.bindingClasses.get(className));
        }
        return null;
    }

    Boolean hasMethod(String name) {
        boolean z = false;
        if (!this.members.containsKey(name)) {
            return Boolean.valueOf(false);
        }
        MemberInfo m = (MemberInfo) this.members.get(name);
        if (m.type == MemberType.JS_METHOD || m.type == MemberType.JS_CONSTRUCTOR) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    Boolean hasProperty(String name) {
        if (!this.members.containsKey(name)) {
            return Boolean.valueOf(false);
        }
        return Boolean.valueOf(((MemberInfo) this.members.get(name)).type == MemberType.JS_PROPERTY);
    }

    MemberInfo getMemberInfo(String name) {
        return (MemberInfo) this.members.get(name);
    }

    Object[] getArgsFromJson(XWalkExternalExtension ext, int instanceID, Method m, JSONArray args) {
        Exception e;
        Class<?>[] pTypes = m.getParameterTypes();
        Object[] oArgs = new Object[pTypes.length];
        boolean isStatic = Modifier.isStatic(m.getModifiers());
        int i = 0;
        while (i < pTypes.length) {
            try {
                Class<?> p = pTypes[i];
                if (isStatic && p.equals(JsContextInfo.class)) {
                    int i2 = i + 1;
                    try {
                        oArgs[i] = new JsContextInfo(instanceID, ext, m.getClass(), Integer.toString(0));
                        i = i2;
                    } catch (Exception e2) {
                        e = e2;
                        i = i2;
                        e.printStackTrace();
                        i++;
                    }
                    i++;
                } else {
                    oArgs[i] = args.get(i);
                    i++;
                }
            } catch (Exception e3) {
                e = e3;
                e.printStackTrace();
                i++;
            }
        }
        return oArgs;
    }

    public static boolean isSerializable(Object obj) {
        Class<?> clz = obj.getClass();
        return clz.isPrimitive() || primitives.contains(clz) || (obj instanceof String) || (obj instanceof Map) || (obj instanceof JSONArray) || (obj instanceof JSONObject);
    }

    public static Object toSerializableObject(Object obj) {
        if (obj.getClass().isArray()) {
            Object jSONArray = new JSONArray();
            Object[] arr = (Object[]) obj;
            for (int i = 0; i < arr.length; i++) {
                try {
                    jSONArray.put(i, toSerializableObject(arr[i]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return jSONArray;
        } else if (isSerializable(obj)) {
            return obj;
        } else {
            try {
                String jsonStr = (String) obj.getClass().getMethod("toJSONString", new Class[0]).invoke(obj, new Object[0]);
                if (jsonStr.trim().charAt(0) == '[') {
                    return new JSONArray(jsonStr);
                }
                return new JSONObject(jsonStr);
            } catch (Exception e2) {
                Log.w(TAG, "No serialization method: \"toJSONString\", or errors happened.");
                try {
                    Class<?> c = obj.getClass();
                    JSONObject json = new JSONObject();
                    for (Field f : c.getFields()) {
                        json.put(f.getName(), f.get(obj));
                    }
                    return json;
                } catch (Exception e3) {
                    Log.e(TAG, "Field to serialize object to JSON.");
                    e3.printStackTrace();
                    return null;
                }
            }
        }
    }

    public static String objToJSON(Object obj) {
        if (obj == null) {
            return "null";
        }
        Object sObj = toSerializableObject(obj);
        return sObj instanceof String ? JSONObject.quote(sObj.toString()) : sObj.toString();
    }

    Object invokeMethod(XWalkExternalExtension ext, int instanceID, Object obj, String mName, JSONArray args) throws Exception {
        if (!hasMethod(mName).booleanValue()) {
            throw new NoSuchMethodException("No such method:" + mName);
        } else if (getMemberInfo(mName).isStatic || this.myClass.isInstance(obj)) {
            Method m = ((MemberInfo) this.members.get(mName)).accesser;
            if (!m.isAccessible()) {
                m.setAccessible(true);
            }
            return m.invoke(obj, getArgsFromJson(ext, instanceID, m, args));
        } else {
            throw new InvocationTargetException(new Exception("Invalid target to set property:" + mName));
        }
    }

    Object getProperty(Object obj, String pName) throws Exception {
        if (!hasProperty(pName).booleanValue()) {
            throw new NoSuchFieldException("No such property:" + pName);
        } else if (getMemberInfo(pName).isStatic || this.myClass.isInstance(obj)) {
            Field f = ((MemberInfo) this.members.get(pName)).accesser;
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            return f.get(obj);
        } else {
            throw new InvocationTargetException(new Exception("Invalid target to set property:" + pName));
        }
    }

    void setProperty(Object obj, String pName, Object value) throws Exception {
        if (!hasProperty(pName).booleanValue()) {
            throw new NoSuchFieldException("No such property:" + pName);
        } else if (getMemberInfo(pName).isStatic || this.myClass.isInstance(obj)) {
            Field f = ((MemberInfo) this.members.get(pName)).accesser;
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            f.set(obj, value);
        } else {
            throw new InvocationTargetException(new Exception("Invalid target to set property:" + pName));
        }
    }

    String[] getEventList() {
        return this.eventList;
    }

    MemberInfo getEntryPoint() {
        return this.entryPoint;
    }

    boolean isEventSupported(String event) {
        if (this.eventList == null) {
            return false;
        }
        for (String equals : this.eventList) {
            if (equals.equals(event)) {
                return true;
            }
        }
        return false;
    }

    boolean isInstance(Object obj) {
        return this.myClass.isInstance(obj);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object handleMessage(org.xwalk.core.extension.MessageInfo r14, java.lang.Object r15) throws java.lang.Exception {
        /*
        r13 = this;
        r1 = 0;
        r11 = 0;
        r7 = r14.getCmd();	 Catch:{ Exception -> 0x00af }
        r4 = r14.getBinaryArgs();	 Catch:{ Exception -> 0x00af }
        if (r4 == 0) goto L_0x0051;
    L_0x000c:
        r9 = new org.json.JSONArray;	 Catch:{ Exception -> 0x00af }
        r9.<init>();	 Catch:{ Exception -> 0x00af }
        r4 = r14.getBinaryArgs();	 Catch:{ Exception -> 0x00af }
        r9.put(r4);	 Catch:{ Exception -> 0x00af }
        r4 = r14.getCallbackId();	 Catch:{ Exception -> 0x00af }
        r9.put(r4);	 Catch:{ Exception -> 0x00af }
        r6 = r9;
    L_0x0020:
        r5 = r14.getJsName();	 Catch:{ Exception -> 0x00af }
        r2 = r14.getExtension();	 Catch:{ Exception -> 0x00af }
        r3 = r14.getInstanceId();	 Catch:{ Exception -> 0x00af }
        r4 = -1;
        r12 = r7.hashCode();	 Catch:{ Exception -> 0x00af }
        switch(r12) {
            case -633190737: goto L_0x0056;
            case 996179031: goto L_0x0073;
            case 1084758859: goto L_0x0069;
            case 1811874389: goto L_0x005f;
            default: goto L_0x0034;
        };	 Catch:{ Exception -> 0x00af }
    L_0x0034:
        r1 = r4;
    L_0x0035:
        switch(r1) {
            case 0: goto L_0x007d;
            case 1: goto L_0x0084;
            case 2: goto L_0x00a1;
            case 3: goto L_0x00a6;
            default: goto L_0x0038;
        };	 Catch:{ Exception -> 0x00af }
    L_0x0038:
        r1 = "JsStubReflectHelper";
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00af }
        r4.<init>();	 Catch:{ Exception -> 0x00af }
        r12 = "Unsupported cmd: ";
        r4 = r4.append(r12);	 Catch:{ Exception -> 0x00af }
        r4 = r4.append(r7);	 Catch:{ Exception -> 0x00af }
        r4 = r4.toString();	 Catch:{ Exception -> 0x00af }
        android.util.Log.w(r1, r4);	 Catch:{ Exception -> 0x00af }
    L_0x0050:
        return r11;
    L_0x0051:
        r6 = r14.getArgs();	 Catch:{ Exception -> 0x00af }
        goto L_0x0020;
    L_0x0056:
        r12 = "invokeNative";
        r12 = r7.equals(r12);	 Catch:{ Exception -> 0x00af }
        if (r12 == 0) goto L_0x0034;
    L_0x005e:
        goto L_0x0035;
    L_0x005f:
        r1 = "newInstance";
        r1 = r7.equals(r1);	 Catch:{ Exception -> 0x00af }
        if (r1 == 0) goto L_0x0034;
    L_0x0067:
        r1 = 1;
        goto L_0x0035;
    L_0x0069:
        r1 = "getProperty";
        r1 = r7.equals(r1);	 Catch:{ Exception -> 0x00af }
        if (r1 == 0) goto L_0x0034;
    L_0x0071:
        r1 = 2;
        goto L_0x0035;
    L_0x0073:
        r1 = "setProperty";
        r1 = r7.equals(r1);	 Catch:{ Exception -> 0x00af }
        if (r1 == 0) goto L_0x0034;
    L_0x007b:
        r1 = 3;
        goto L_0x0035;
    L_0x007d:
        r1 = r13;
        r4 = r15;
        r11 = r1.invokeMethod(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x00af }
        goto L_0x0050;
    L_0x0084:
        r1 = r13;
        r4 = r15;
        r1 = r1.invokeMethod(r2, r3, r4, r5, r6);	 Catch:{ Exception -> 0x00af }
        r1 = (org.xwalk.core.extension.BindingObject) r1;	 Catch:{ Exception -> 0x00af }
        r0 = r1;
        r0 = (org.xwalk.core.extension.BindingObject) r0;	 Catch:{ Exception -> 0x00af }
        r10 = r0;
        r1 = r14.getInstanceHelper();	 Catch:{ Exception -> 0x00af }
        r4 = r14.getObjectId();	 Catch:{ Exception -> 0x00af }
        r1 = r1.addBindingObject(r4, r10);	 Catch:{ Exception -> 0x00af }
        r11 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x00af }
        goto L_0x0050;
    L_0x00a1:
        r11 = r13.getProperty(r15, r5);	 Catch:{ Exception -> 0x00af }
        goto L_0x0050;
    L_0x00a6:
        r1 = 0;
        r1 = r6.get(r1);	 Catch:{ Exception -> 0x00af }
        r13.setProperty(r15, r5, r1);	 Catch:{ Exception -> 0x00af }
        goto L_0x0050;
    L_0x00af:
        r8 = move-exception;
        r1 = "JsStubReflectHelper";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r12 = "Invalid message, error msg:\n";
        r4 = r4.append(r12);
        r12 = r8.toString();
        r4 = r4.append(r12);
        r4 = r4.toString();
        android.util.Log.w(r1, r4);
        r8.printStackTrace();
        goto L_0x0050;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xwalk.core.extension.ReflectionHelper.handleMessage(org.xwalk.core.extension.MessageInfo, java.lang.Object):java.lang.Object");
    }
}
