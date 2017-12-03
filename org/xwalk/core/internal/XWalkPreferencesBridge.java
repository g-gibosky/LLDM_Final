package org.xwalk.core.internal;

public class XWalkPreferencesBridge extends XWalkPreferencesInternal {
    private XWalkCoreBridge coreBridge;
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public static void setValue(String key, boolean enabled) {
        XWalkPreferencesInternal.setValue(key, enabled);
    }

    public static void setValue(String key, int value) {
        XWalkPreferencesInternal.setValue(key, value);
    }

    public static void setValue(String key, String value) {
        XWalkPreferencesInternal.setValue(key, value);
    }

    public static boolean getValue(String key) {
        return XWalkPreferencesInternal.getValue(key);
    }

    public static boolean getBooleanValue(String key) {
        return XWalkPreferencesInternal.getBooleanValue(key);
    }

    public static int getIntegerValue(String key) {
        return XWalkPreferencesInternal.getIntegerValue(key);
    }

    public static String getStringValue(String key) {
        return XWalkPreferencesInternal.getStringValue(key);
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
        }
    }
}
