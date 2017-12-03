package org.chromium.content.common;

public abstract class ContentSwitches {
    public static final String ACCESSIBILITY_JAVASCRIPT_URL = "accessibility-js-url";
    public static final String ADD_OFFICIAL_COMMAND_LINE = "add-official-command-line";
    public static final String DISABLE_GESTURE_REQUIREMENT_FOR_MEDIA_PLAYBACK = "disable-gesture-requirement-for-media-playback";
    public static final String DISABLE_GESTURE_REQUIREMENT_FOR_PRESENTATION = "disable-gesture-requirement-for-presentation";
    public static final String DISABLE_POPUP_BLOCKING = "disable-popup-blocking";
    public static final String ENABLE_CREDENTIAL_MANAGER_API = "enable-credential-manager-api";
    public static final String ENABLE_INSTANT_EXTENDED_API = "enable-instant-extended-api";
    public static final String ENABLE_TEST_INTENTS = "enable-test-intents";
    public static final String ENABLE_TOUCH_HOVER = "enable-touch-hover";
    public static final String FORCE_DEVICE_SCALE_FACTOR = "force-device-scale-factor";
    public static final String IN_PROCESS_GPU = "in-process-gpu";
    public static final String LOG_FPS = "log-fps";
    public static final String NETWORK_COUNTRY_ISO = "network-country-iso";
    public static final String RENDER_PROCESS_LIMIT = "renderer-process-limit";
    public static final String RUNNING_PERFORMANCE_BENCHMARK = "running-performance-benchmark";
    public static final String RUN_LAYOUT_TEST = "run-layout-test";
    public static final String SWITCH_DOWNLOAD_PROCESS = "download";
    public static final String SWITCH_GPU_PROCESS = "gpu-process";
    public static final String SWITCH_PROCESS_TYPE = "type";
    public static final String SWITCH_RENDERER_PROCESS = "renderer";
    public static final String SWITCH_UTILITY_PROCESS = "utility";
    public static final String TOP_CONTROLS_HIDE_THRESHOLD = "top-controls-hide-threshold";
    public static final String TOP_CONTROLS_SHOW_THRESHOLD = "top-controls-show-threshold";
    public static final String USE_MOBILE_UA = "use-mobile-user-agent";

    private ContentSwitches() {
    }

    public static String getSwitchValue(String[] commandLine, String switchKey) {
        if (commandLine == null || switchKey == null) {
            return null;
        }
        String switchKeyPrefix = "--" + switchKey + "=";
        for (String command : commandLine) {
            if (command != null && command.startsWith(switchKeyPrefix)) {
                return command.substring(switchKeyPrefix.length());
            }
        }
        return null;
    }
}
