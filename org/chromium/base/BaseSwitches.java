package org.chromium.base;

public abstract class BaseSwitches {
    public static final String DEFAULT_COUNTRY_CODE_AT_INSTALL = "default-country-code";
    public static final String DISABLE_LOW_END_DEVICE_MODE = "disable-low-end-device-mode";
    public static final String ENABLE_IDLE_TRACING = "enable-idle-tracing";
    public static final String ENABLE_LOW_END_DEVICE_MODE = "enable-low-end-device-mode";
    public static final String RENDERER_WAIT_FOR_JAVA_DEBUGGER = "renderer-wait-for-java-debugger";
    public static final String WAIT_FOR_JAVA_DEBUGGER = "wait-for-java-debugger";

    private BaseSwitches() {
    }
}