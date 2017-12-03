package org.xwalk.core;

public class XWalkPreferences {
    public static final String ALLOW_UNIVERSAL_ACCESS_FROM_FILE = "allow-universal-access-from-file";
    public static final String ANIMATABLE_XWALK_VIEW = "animatable-xwalk-view";
    public static final String ENABLE_EXTENSIONS = "enable-extensions";
    public static final String ENABLE_JAVASCRIPT = "enable-javascript";
    public static final String ENABLE_THEME_COLOR = "enable-theme-color";
    public static final String JAVASCRIPT_CAN_OPEN_WINDOW = "javascript-can-open-window";
    public static final String PROFILE_NAME = "profile-name";
    public static final String REMOTE_DEBUGGING = "remote-debugging";
    public static final String SPATIAL_NAVIGATION = "enable-spatial-navigation";
    public static final String SUPPORT_MULTIPLE_WINDOWS = "support-multiple-windows";
    private static XWalkCoreWrapper coreWrapper;
    private static ReflectMethod getBooleanValueStringMethod = new ReflectMethod(null, "getBooleanValue", new Class[0]);
    private static ReflectMethod getIntegerValueStringMethod = new ReflectMethod(null, "getIntegerValue", new Class[0]);
    private static ReflectMethod getStringValueStringMethod = new ReflectMethod(null, "getStringValue", new Class[0]);
    private static ReflectMethod getValueStringMethod = new ReflectMethod(null, "getValue", new Class[0]);
    private static ReflectMethod setValueStringStringMethod = new ReflectMethod(null, "setValue", new Class[0]);
    private static ReflectMethod setValueStringbooleanMethod = new ReflectMethod(null, "setValue", new Class[0]);
    private static ReflectMethod setValueStringintMethod = new ReflectMethod(null, "setValue", new Class[0]);

    public static void setValue(String key, boolean enabled) {
        reflectionInit();
        try {
            setValueStringbooleanMethod.invoke(key, Boolean.valueOf(enabled));
        } catch (UnsupportedOperationException e) {
            if (coreWrapper == null) {
                setValueStringbooleanMethod.setArguments(key, Boolean.valueOf(enabled));
                XWalkCoreWrapper.reserveReflectMethod(setValueStringbooleanMethod);
                return;
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public static void setValue(String key, int value) {
        reflectionInit();
        try {
            setValueStringintMethod.invoke(key, Integer.valueOf(value));
        } catch (UnsupportedOperationException e) {
            if (coreWrapper == null) {
                setValueStringintMethod.setArguments(key, Integer.valueOf(value));
                XWalkCoreWrapper.reserveReflectMethod(setValueStringintMethod);
                return;
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public static void setValue(String key, String value) {
        reflectionInit();
        try {
            setValueStringStringMethod.invoke(key, value);
        } catch (UnsupportedOperationException e) {
            if (coreWrapper == null) {
                setValueStringStringMethod.setArguments(key, value);
                XWalkCoreWrapper.reserveReflectMethod(setValueStringStringMethod);
                return;
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public static boolean getValue(String key) {
        reflectionInit();
        try {
            return ((Boolean) getValueStringMethod.invoke(key)).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public static boolean getBooleanValue(String key) {
        reflectionInit();
        try {
            return ((Boolean) getBooleanValueStringMethod.invoke(key)).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public static int getIntegerValue(String key) {
        reflectionInit();
        try {
            return ((Integer) getIntegerValueStringMethod.invoke(key)).intValue();
        } catch (UnsupportedOperationException e) {
            if (coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return 0;
        }
    }

    public static String getStringValue(String key) {
        reflectionInit();
        try {
            return (String) getStringValueStringMethod.invoke(key);
        } catch (UnsupportedOperationException e) {
            if (coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    static void reflectionInit() {
        if (coreWrapper == null) {
            XWalkCoreWrapper.initEmbeddedMode();
            coreWrapper = XWalkCoreWrapper.getInstance();
            if (coreWrapper == null) {
                XWalkCoreWrapper.reserveReflectClass(XWalkPreferences.class);
                return;
            }
            Class<?> bridgeClass = coreWrapper.getBridgeClass("XWalkPreferencesBridge");
            setValueStringbooleanMethod.init(null, bridgeClass, "setValue", String.class, Boolean.TYPE);
            setValueStringintMethod.init(null, bridgeClass, "setValue", String.class, Integer.TYPE);
            setValueStringStringMethod.init(null, bridgeClass, "setValue", String.class, String.class);
            getValueStringMethod.init(null, bridgeClass, "getValue", String.class);
            getBooleanValueStringMethod.init(null, bridgeClass, "getBooleanValue", String.class);
            getIntegerValueStringMethod.init(null, bridgeClass, "getIntegerValue", String.class);
            getStringValueStringMethod.init(null, bridgeClass, "getStringValue", String.class);
        }
    }
}
