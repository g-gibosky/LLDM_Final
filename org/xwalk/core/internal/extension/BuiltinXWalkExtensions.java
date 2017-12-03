package org.xwalk.core.internal.extension;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.xwalk.core.internal.XWalkExtensionInternal;
import org.xwalk.core.internal.extension.api.launchscreen.LaunchScreenExtension;
import org.xwalk.core.internal.extension.api.wifidirect.WifiDirect;

public class BuiltinXWalkExtensions {
    private static final String TAG = "BuiltinXWalkExtension";
    private static HashMap<String, XWalkExtensionInternal> sBuiltinExtensions = new HashMap();

    public static void load(Context context) {
        String jsApiContent = "";
        try {
            sBuiltinExtensions.put(LaunchScreenExtension.JS_API_PATH, new LaunchScreenExtension(getExtensionJSFileContent(context, LaunchScreenExtension.JS_API_PATH, true), context.getApplicationContext()));
        } catch (IOException e) {
            Log.w(TAG, "Failed to read JS API file: jsapi/launch_screen_api.js");
        }
        if (context instanceof Activity) {
            jsApiContent = "";
            try {
                sBuiltinExtensions.put(WifiDirect.JS_API_PATH, new WifiDirect(getExtensionJSFileContent(context, WifiDirect.JS_API_PATH, true), (Activity) context));
            } catch (IOException e2) {
                Log.w(TAG, "Failed to read JS API file: jsapi/wifidirect_api.js");
            }
        }
    }

    private static String getExtensionJSFileContent(Context context, String fileName, boolean fromRaw) throws IOException {
        String result = "";
        InputStream inputStream = null;
        if (fromRaw) {
            try {
                Resources resource = context.getResources();
                String resName = new File(fileName).getName().split("\\.")[0];
                int resId = resource.getIdentifier(resName, "raw", context.getPackageName());
                if (resId > 0) {
                    inputStream = resource.openRawResource(resId);
                }
            } catch (NotFoundException e) {
                Log.w(TAG, "Inputstream failed to open for R.raw." + resName + ", try to find it in assets");
            } catch (Throwable th) {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        if (inputStream == null) {
            inputStream = context.getAssets().open(fileName);
        }
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        result = new String(buffer);
        if (inputStream != null) {
            inputStream.close();
        }
        return result;
    }
}
