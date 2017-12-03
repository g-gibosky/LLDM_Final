package org.xwalk.core.internal;

import android.os.Environment;
import java.io.File;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("xwalk")
public class XWalkPathHelper {
    private static final String TAG = "XWalkPathHelper";

    private static native void nativeSetDirectory(String str, String str2);

    public static void initialize() {
        nativeSetDirectory("EXTERNAL", Environment.getExternalStorageDirectory().getPath());
        String[] names = new String[]{"ALARMS", "DCIM", "DOWNLOADS", "MOVIES", "MUSIC", "NOTIFICATIONS", "PICTURES", "PODCASTS", "RINGTONES"};
        String[] dirs = new String[]{Environment.DIRECTORY_ALARMS, Environment.DIRECTORY_DCIM, Environment.DIRECTORY_DOWNLOADS, Environment.DIRECTORY_MOVIES, Environment.DIRECTORY_MUSIC, Environment.DIRECTORY_NOTIFICATIONS, Environment.DIRECTORY_PICTURES, Environment.DIRECTORY_PODCASTS, Environment.DIRECTORY_RINGTONES};
        for (int i = 0; i < names.length; i++) {
            File dir = Environment.getExternalStoragePublicDirectory(dirs[i]);
            if (dir != null) {
                nativeSetDirectory(names[i], dir.getPath());
            }
        }
    }

    public static void setCacheDirectory(String path) {
        nativeSetDirectory("CACHEDIR", path);
    }

    public static void setExternalCacheDirectory(String path) {
        nativeSetDirectory("EXTERNAL_CACHEDIR", path);
    }
}
