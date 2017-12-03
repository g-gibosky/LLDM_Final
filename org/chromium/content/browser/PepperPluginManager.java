package org.chromium.content.browser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import org.chromium.base.Log;

public class PepperPluginManager {
    private static final String DESCRIPTION = "description";
    private static final String FILENAME = "filename";
    private static final String MIMETYPE = "mimetype";
    private static final String NAME = "name";
    public static final String PEPPER_PLUGIN_ACTION = "org.chromium.intent.PEPPERPLUGIN";
    public static final String PEPPER_PLUGIN_ROOT = "/system/lib/pepperplugin/";
    private static final String TAG = "cr.PepperPluginManager";
    private static final String VERSION = "version";

    private static String getPluginDescription(Bundle metaData) {
        String filename = metaData.getString(FILENAME);
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        String mimetype = metaData.getString(MIMETYPE);
        if (mimetype == null || mimetype.isEmpty()) {
            return null;
        }
        StringBuilder plugin = new StringBuilder(PEPPER_PLUGIN_ROOT);
        plugin.append(filename);
        String name = metaData.getString(NAME);
        String description = metaData.getString(DESCRIPTION);
        String version = metaData.getString(VERSION);
        if (!(name == null || name.isEmpty())) {
            plugin.append("#");
            plugin.append(name);
            if (!(description == null || description.isEmpty())) {
                plugin.append("#");
                plugin.append(description);
                if (!(version == null || version.isEmpty())) {
                    plugin.append("#");
                    plugin.append(version);
                }
            }
        }
        plugin.append(';');
        plugin.append(mimetype);
        return plugin.toString();
    }

    public static String getPlugins(Context context) {
        StringBuilder ret = new StringBuilder();
        PackageManager pm = context.getPackageManager();
        for (ResolveInfo info : pm.queryIntentServices(new Intent(PEPPER_PLUGIN_ACTION), 132)) {
            ServiceInfo serviceInfo = info.serviceInfo;
            if (serviceInfo == null || serviceInfo.metaData == null || serviceInfo.packageName == null) {
                Log.m28e(TAG, "Can't get service information from %s", info);
            } else {
                try {
                    PackageInfo pkgInfo = pm.getPackageInfo(serviceInfo.packageName, 0);
                    if (!(pkgInfo == null || (pkgInfo.applicationInfo.flags & 1) == 0)) {
                        Log.m29i(TAG, "The given plugin package is preloaded: %s", serviceInfo.packageName);
                        String plugin = getPluginDescription(serviceInfo.metaData);
                        if (plugin != null) {
                            if (ret.length() > 0) {
                                ret.append(',');
                            }
                            ret.append(plugin);
                        }
                    }
                } catch (NameNotFoundException e) {
                    Log.m28e(TAG, "Can't find plugin: %s", serviceInfo.packageName);
                }
            }
        }
        return ret.toString();
    }
}
