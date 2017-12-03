package org.chromium.device.gamepad;

import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("content")
class GamepadMappings {
    @VisibleForTesting
    static final String AMAZON_FIRE_DEVICE_NAME = "Amazon Fire Game Controller";
    @VisibleForTesting
    static final String MICROSOFT_XBOX_PAD_DEVICE_NAME = "Microsoft X-Box 360 pad";
    @VisibleForTesting
    static final String NVIDIA_SHIELD_DEVICE_NAME_PREFIX = "NVIDIA Corporation NVIDIA Controller";
    @VisibleForTesting
    static final String PS3_SIXAXIS_DEVICE_NAME = "Sony PLAYSTATION(R)3 Controller";
    @VisibleForTesting
    static final String SAMSUNG_EI_GP20_DEVICE_NAME = "Samsung Game Pad EI-GP20";

    GamepadMappings() {
    }

    public static boolean mapToStandardGamepad(float[] mappedAxes, float[] mappedButtons, float[] rawAxes, float[] rawButtons, String deviceName) {
        if (deviceName.startsWith(NVIDIA_SHIELD_DEVICE_NAME_PREFIX)) {
            mapShieldGamepad(mappedButtons, rawButtons, mappedAxes, rawAxes);
            return true;
        } else if (deviceName.equals(MICROSOFT_XBOX_PAD_DEVICE_NAME)) {
            mapXBox360Gamepad(mappedButtons, rawButtons, mappedAxes, rawAxes);
            return true;
        } else if (deviceName.equals(PS3_SIXAXIS_DEVICE_NAME)) {
            mapPS3SixAxisGamepad(mappedButtons, rawButtons, mappedAxes, rawAxes);
            return true;
        } else if (deviceName.equals(SAMSUNG_EI_GP20_DEVICE_NAME)) {
            mapSamsungEIGP20Gamepad(mappedButtons, rawButtons, mappedAxes, rawAxes);
            return true;
        } else if (deviceName.equals(AMAZON_FIRE_DEVICE_NAME)) {
            mapAmazonFireGamepad(mappedButtons, rawButtons, mappedAxes, rawAxes);
            return true;
        } else {
            mapUnknownGamepad(mappedButtons, rawButtons, mappedAxes, rawAxes);
            return false;
        }
    }

    private static void mapCommonXYABButtons(float[] mappedButtons, float[] rawButtons) {
        float a = rawButtons[96];
        float b = rawButtons[97];
        float x = rawButtons[99];
        float y = rawButtons[100];
        mappedButtons[0] = a;
        mappedButtons[1] = b;
        mappedButtons[2] = x;
        mappedButtons[3] = y;
    }

    private static void mapCommonStartSelectMetaButtons(float[] mappedButtons, float[] rawButtons) {
        float start = rawButtons[108];
        float select = rawButtons[109];
        float mode = rawButtons[110];
        mappedButtons[9] = start;
        mappedButtons[8] = select;
        mappedButtons[16] = mode;
    }

    private static void mapCommonThumbstickButtons(float[] mappedButtons, float[] rawButtons) {
        float thumbL = rawButtons[106];
        float thumbR = rawButtons[107];
        mappedButtons[10] = thumbL;
        mappedButtons[11] = thumbR;
    }

    private static void mapCommonTriggerButtons(float[] mappedButtons, float[] rawButtons) {
        float l1 = rawButtons[102];
        float r1 = rawButtons[103];
        mappedButtons[6] = l1;
        mappedButtons[7] = r1;
    }

    private static void mapTriggerButtonsToTopShoulder(float[] mappedButtons, float[] rawButtons) {
        float l1 = rawButtons[102];
        float r1 = rawButtons[103];
        mappedButtons[4] = l1;
        mappedButtons[5] = r1;
    }

    private static void mapCommonDpadButtons(float[] mappedButtons, float[] rawButtons) {
        float dpadDown = rawButtons[20];
        float dpadUp = rawButtons[19];
        float dpadLeft = rawButtons[21];
        float dpadRight = rawButtons[22];
        mappedButtons[13] = dpadDown;
        mappedButtons[12] = dpadUp;
        mappedButtons[14] = dpadLeft;
        mappedButtons[15] = dpadRight;
    }

    private static void mapXYAxes(float[] mappedAxes, float[] rawAxes) {
        mappedAxes[0] = rawAxes[0];
        mappedAxes[1] = rawAxes[1];
    }

    private static void mapRXAndRYAxesToRightStick(float[] mappedAxes, float[] rawAxes) {
        mappedAxes[2] = rawAxes[12];
        mappedAxes[3] = rawAxes[13];
    }

    private static void mapZAndRZAxesToRightStick(float[] mappedAxes, float[] rawAxes) {
        mappedAxes[2] = rawAxes[11];
        mappedAxes[3] = rawAxes[14];
    }

    private static void mapTriggerAxexToShoulderButtons(float[] mappedButtons, float[] rawAxes) {
        float lTrigger = rawAxes[17];
        float rTrigger = rawAxes[18];
        mappedButtons[4] = lTrigger;
        mappedButtons[5] = rTrigger;
    }

    private static void mapPedalAxesToBottomShoulder(float[] mappedButtons, float[] rawAxes) {
        float lTrigger = rawAxes[23];
        float rTrigger = rawAxes[22];
        mappedButtons[6] = lTrigger;
        mappedButtons[7] = rTrigger;
    }

    private static void mapTriggerAxesToBottomShoulder(float[] mappedButtons, float[] rawAxes) {
        float lTrigger = rawAxes[17];
        float rTrigger = rawAxes[18];
        mappedButtons[6] = lTrigger;
        mappedButtons[7] = rTrigger;
    }

    @VisibleForTesting
    static float negativeAxisValueAsButton(float input) {
        return input < -0.5f ? 1.0f : 0.0f;
    }

    @VisibleForTesting
    static float positiveAxisValueAsButton(float input) {
        return input > 0.5f ? 1.0f : 0.0f;
    }

    private static void mapHatAxisToDpadButtons(float[] mappedButtons, float[] rawAxes) {
        float hatX = rawAxes[15];
        float hatY = rawAxes[16];
        mappedButtons[14] = negativeAxisValueAsButton(hatX);
        mappedButtons[15] = positiveAxisValueAsButton(hatX);
        mappedButtons[12] = negativeAxisValueAsButton(hatY);
        mappedButtons[13] = positiveAxisValueAsButton(hatY);
    }

    private static void mapAmazonFireGamepad(float[] mappedButtons, float[] rawButtons, float[] mappedAxes, float[] rawAxes) {
        mapCommonXYABButtons(mappedButtons, rawButtons);
        mapTriggerButtonsToTopShoulder(mappedButtons, rawButtons);
        mapCommonThumbstickButtons(mappedButtons, rawButtons);
        mapCommonStartSelectMetaButtons(mappedButtons, rawButtons);
        mapPedalAxesToBottomShoulder(mappedButtons, rawAxes);
        mapHatAxisToDpadButtons(mappedButtons, rawAxes);
        mapXYAxes(mappedAxes, rawAxes);
        mapZAndRZAxesToRightStick(mappedAxes, rawAxes);
    }

    private static void mapShieldGamepad(float[] mappedButtons, float[] rawButtons, float[] mappedAxes, float[] rawAxes) {
        mapCommonXYABButtons(mappedButtons, rawButtons);
        mapTriggerButtonsToTopShoulder(mappedButtons, rawButtons);
        mapCommonThumbstickButtons(mappedButtons, rawButtons);
        mapCommonStartSelectMetaButtons(mappedButtons, rawButtons);
        mapTriggerAxesToBottomShoulder(mappedButtons, rawAxes);
        mapHatAxisToDpadButtons(mappedButtons, rawAxes);
        mapXYAxes(mappedAxes, rawAxes);
        mapZAndRZAxesToRightStick(mappedAxes, rawAxes);
    }

    private static void mapXBox360Gamepad(float[] mappedButtons, float[] rawButtons, float[] mappedAxes, float[] rawAxes) {
        mapShieldGamepad(mappedButtons, rawButtons, mappedAxes, rawAxes);
    }

    private static void mapPS3SixAxisGamepad(float[] mappedButtons, float[] rawButtons, float[] mappedAxes, float[] rawAxes) {
        float a = rawButtons[96];
        float b = rawButtons[97];
        float x = rawButtons[99];
        float y = rawButtons[100];
        mappedButtons[0] = x;
        mappedButtons[1] = y;
        mappedButtons[2] = a;
        mappedButtons[3] = b;
        mapCommonTriggerButtons(mappedButtons, rawButtons);
        mapCommonThumbstickButtons(mappedButtons, rawButtons);
        mapCommonDpadButtons(mappedButtons, rawButtons);
        mapCommonStartSelectMetaButtons(mappedButtons, rawButtons);
        mapTriggerAxexToShoulderButtons(mappedButtons, rawAxes);
        mapXYAxes(mappedAxes, rawAxes);
        mapZAndRZAxesToRightStick(mappedAxes, rawAxes);
    }

    private static void mapSamsungEIGP20Gamepad(float[] mappedButtons, float[] rawButtons, float[] mappedAxes, float[] rawAxes) {
        mapCommonXYABButtons(mappedButtons, rawButtons);
        mapCommonTriggerButtons(mappedButtons, rawButtons);
        mapCommonThumbstickButtons(mappedButtons, rawButtons);
        mapCommonStartSelectMetaButtons(mappedButtons, rawButtons);
        mapHatAxisToDpadButtons(mappedButtons, rawAxes);
        mapXYAxes(mappedAxes, rawAxes);
        mapRXAndRYAxesToRightStick(mappedAxes, rawAxes);
    }

    private static void mapUnknownGamepad(float[] mappedButtons, float[] rawButtons, float[] mappedAxes, float[] rawAxes) {
        mapCommonXYABButtons(mappedButtons, rawButtons);
        mapCommonTriggerButtons(mappedButtons, rawButtons);
        mapCommonThumbstickButtons(mappedButtons, rawButtons);
        mapCommonStartSelectMetaButtons(mappedButtons, rawButtons);
        mapTriggerAxexToShoulderButtons(mappedButtons, rawAxes);
        mapCommonDpadButtons(mappedButtons, rawButtons);
        mapXYAxes(mappedAxes, rawAxes);
        mapRXAndRYAxesToRightStick(mappedAxes, rawAxes);
    }
}
