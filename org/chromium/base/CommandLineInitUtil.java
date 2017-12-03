package org.chromium.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import java.io.File;
import org.chromium.base.annotations.SuppressFBWarnings;

public final class CommandLineInitUtil {
    private static final String COMMAND_LINE_FILE_PATH = "/data/local";
    private static final String COMMAND_LINE_FILE_PATH_DEBUG_APP = "/data/local/tmp";
    private static final String TAG = "CommandLineInitUtil";

    private CommandLineInitUtil() {
    }

    @SuppressFBWarnings({"DMI_HARDCODED_ABSOLUTE_FILENAME"})
    public static void initCommandLine(Context context, String fileName) {
        if (!CommandLine.isInitialized()) {
            File commandLineFile = getAlternativeCommandLinePath(context, fileName);
            if (commandLineFile == null) {
                commandLineFile = new File(COMMAND_LINE_FILE_PATH, fileName);
            }
            CommandLine.initFromFile(commandLineFile.getPath());
        }
    }

    @SuppressFBWarnings({"DMI_HARDCODED_ABSOLUTE_FILENAME"})
    private static File getAlternativeCommandLinePath(Context context, String fileName) {
        File alternativeCommandLineFile = new File(COMMAND_LINE_FILE_PATH_DEBUG_APP, fileName);
        if (!alternativeCommandLineFile.exists()) {
            return null;
        }
        try {
            String debugApp = VERSION.SDK_INT < 17 ? getDebugAppPreJBMR1(context) : getDebugAppJBMR1(context);
            if (debugApp != null && debugApp.equals(context.getApplicationContext().getPackageName())) {
                Log.m29i(TAG, "Using alternative command line file in " + alternativeCommandLineFile.getPath(), new Object[0]);
                return alternativeCommandLineFile;
            }
        } catch (RuntimeException e) {
            Log.m28e(TAG, "Unable to detect alternative command line file", new Object[0]);
        }
        return null;
    }

    @SuppressLint({"NewApi"})
    private static String getDebugAppJBMR1(Context context) {
        boolean adbEnabled = true;
        if (Global.getInt(context.getContentResolver(), "adb_enabled", 0) != 1) {
            adbEnabled = false;
        }
        if (adbEnabled) {
            return Global.getString(context.getContentResolver(), "debug_app");
        }
        return null;
    }

    private static String getDebugAppPreJBMR1(Context context) {
        boolean adbEnabled = true;
        if (System.getInt(context.getContentResolver(), "adb_enabled", 0) != 1) {
            adbEnabled = false;
        }
        if (adbEnabled) {
            return System.getString(context.getContentResolver(), "debug_app");
        }
        return null;
    }
}
