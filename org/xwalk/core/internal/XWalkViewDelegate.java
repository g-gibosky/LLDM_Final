package org.xwalk.core.internal;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.os.Build;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.chromium.base.ApplicationStatusManager;
import org.chromium.base.CommandLine;
import org.chromium.base.PathUtils;
import org.chromium.base.ResourceExtractor;
import org.chromium.base.ResourceExtractor.ResourceEntry;
import org.chromium.base.ResourceExtractor.ResourceInterceptor;
import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.library_loader.LibraryLoader;
import org.chromium.base.library_loader.ProcessInitException;
import org.chromium.content.browser.BrowserStartupController;
import org.chromium.content.browser.DeviceUtils;

@JNINamespace("xwalk")
class XWalkViewDelegate {
    private static final String COMMAND_LINE_FILE = "xwalk-command-line";
    private static final String[] MANDATORY_LIBRARIES = new String[]{PRIVATE_DATA_DIRECTORY_SUFFIX};
    private static final String[] MANDATORY_PAKS = new String[]{XWALK_PAK_NAME, "icudtl.dat", "xwalk_100_percent.pak"};
    private static final String META_XWALK_DOWNLOAD_MODE = "xwalk_download_mode";
    private static final String META_XWALK_ENABLE_DOWNLOAD_MODE = "xwalk_enable_download_mode";
    private static final String PRIVATE_DATA_DIRECTORY_SUFFIX = "xwalkcore";
    private static final String TAG = "XWalkLib";
    private static final String XWALK_CORE_EXTRACTED_DIR = "extracted_xwalkcore";
    private static final String XWALK_PAK_NAME = "xwalk.pak";
    private static final String XWALK_RESOURCES_LIST_RES_NAME = "xwalk_resources_list";
    private static String sDeviceAbi;
    private static boolean sInitialized = false;
    private static boolean sLibraryLoaded = false;
    private static boolean sLoadedByHoudini = false;

    private static native boolean nativeIsLibraryBuiltForIA();

    XWalkViewDelegate() {
    }

    private static String[] readCommandLine(Context context) {
        Throwable th;
        String[] tokenizeQuotedAruments;
        InputStreamReader reader = null;
        try {
            InputStream input = context.getAssets().open(COMMAND_LINE_FILE, 3);
            char[] buffer = new char[1024];
            StringBuilder builder = new StringBuilder();
            InputStreamReader reader2 = new InputStreamReader(input, "UTF-8");
            while (true) {
                try {
                    int length = reader2.read(buffer, 0, 1024);
                    if (length == -1) {
                        break;
                    }
                    builder.append(buffer, 0, length);
                } catch (IOException e) {
                    reader = reader2;
                } catch (Throwable th2) {
                    th = th2;
                    reader = reader2;
                }
            }
            tokenizeQuotedAruments = CommandLine.tokenizeQuotedAruments(builder.toString().toCharArray());
            if (reader2 != null) {
                try {
                    reader2.close();
                } catch (IOException e2) {
                    Log.e(TAG, "Unable to close file reader.", e2);
                }
            }
            reader = reader2;
        } catch (IOException e3) {
            tokenizeQuotedAruments = null;
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e22) {
                    Log.e(TAG, "Unable to close file reader.", e22);
                }
            }
            return tokenizeQuotedAruments;
        } catch (Throwable th3) {
            th = th3;
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e222) {
                    Log.e(TAG, "Unable to close file reader.", e222);
                }
            }
            throw th;
        }
        return tokenizeQuotedAruments;
    }

    public static void init(Context libContext, Context appContext) {
        if (!loadXWalkLibrary(libContext, null)) {
            throw new RuntimeException("Failed to load native library");
        } else if (!sInitialized) {
            Context context;
            if (libContext == null) {
                context = appContext;
            } else {
                context = new MixedContext(libContext, appContext);
            }
            PathUtils.setPrivateDataDirectorySuffix(PRIVATE_DATA_DIRECTORY_SUFFIX, context);
            XWalkInternalResources.resetIds(context);
            if (!CommandLine.isInitialized()) {
                CommandLine.init(readCommandLine(context.getApplicationContext()));
            }
            try {
                setupResourceInterceptor(context);
                ResourceExtractor.get(context);
                startBrowserProcess(context);
                if (appContext instanceof Activity) {
                    ApplicationStatusManager.init(((Activity) appContext).getApplication());
                } else if (appContext instanceof Service) {
                    ApplicationStatusManager.init(((Service) appContext).getApplication());
                }
                XWalkPresentationHost.createInstanceOnce(context);
                sInitialized = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean loadXWalkLibrary(Context context) {
        return loadXWalkLibrary(context, null);
    }

    public static boolean loadXWalkLibrary(Context context, String libDir) throws UnsatisfiedLinkError {
        if (sLibraryLoaded) {
            return true;
        }
        if (libDir == null || sLoadedByHoudini) {
            for (String library : MANDATORY_LIBRARIES) {
                System.loadLibrary(library);
            }
        } else {
            for (String library2 : MANDATORY_LIBRARIES) {
                System.load(libDir + File.separator + "lib" + library2 + ".so");
            }
        }
        try {
            LibraryLoader.get(1).loadNow(context);
        } catch (ProcessInitException e) {
        }
        if (nativeIsLibraryBuiltForIA()) {
            Log.d(TAG, "Native library is built for IA");
        } else {
            Log.d(TAG, "Native library is built for ARM");
            if (isIaDevice()) {
                Log.d(TAG, "Crosswalk's native library does not support Houdini");
                sLoadedByHoudini = true;
                return false;
            }
        }
        sLibraryLoaded = true;
        return true;
    }

    private static void startBrowserProcess(final Context context) {
        ThreadUtils.runOnUiThreadBlocking(new Runnable() {
            public void run() {
                try {
                    LibraryLoader.get(1).ensureInitialized(context);
                    DeviceUtils.addDeviceSpecificUserAgentSwitch(context);
                    CommandLine.getInstance().appendSwitchWithValue("profile-name", XWalkPreferencesInternal.getStringValue("profile-name"));
                    if (XWalkPreferencesInternal.getValue("animatable-xwalk-view") && !CommandLine.getInstance().hasSwitch(XWalkSwitches.DISABLE_GPU_RASTERIZATION)) {
                        CommandLine.getInstance().appendSwitch(XWalkSwitches.DISABLE_GPU_RASTERIZATION);
                    }
                    try {
                        BrowserStartupController.get(context, 1).startBrowserProcessesSync(true);
                    } catch (ProcessInitException e) {
                        throw new RuntimeException("Cannot initialize Crosswalk Core", e);
                    }
                } catch (ProcessInitException e2) {
                    throw new RuntimeException("Cannot initialize Crosswalk Core", e2);
                }
            }
        });
    }

    private static void setupResourceInterceptor(Context context) throws IOException {
        final boolean isSharedMode = !context.getPackageName().equals(context.getApplicationContext().getPackageName());
        String enable = getApplicationMetaData(context, META_XWALK_DOWNLOAD_MODE);
        if (enable == null) {
            enable = getApplicationMetaData(context, META_XWALK_ENABLE_DOWNLOAD_MODE);
        }
        final boolean isDownloadMode = enable != null && (enable.equalsIgnoreCase("enable") || enable.equalsIgnoreCase("true"));
        final boolean isTestApk = !isSharedMode && Arrays.asList(context.getAssets().list("")).contains(XWALK_PAK_NAME);
        HashMap<String, ResourceEntry> resourceList = new HashMap();
        try {
            for (String resource : context.getResources().getStringArray(getResourceId(context, XWALK_RESOURCES_LIST_RES_NAME, "array"))) {
                resourceList.put(resource, new ResourceEntry(0, "", resource));
            }
        } catch (NotFoundException e) {
            for (String resource2 : MANDATORY_PAKS) {
                resourceList.put(resource2, new ResourceEntry(0, "", resource2));
            }
        }
        ResourceExtractor.setResourcesToExtract((ResourceEntry[]) resourceList.values().toArray(new ResourceEntry[resourceList.size()]));
        final HashSet<String> interceptableResources = new HashSet(resourceList.keySet());
        final Context context2 = context;
        ResourceExtractor.setResourceInterceptor(new ResourceInterceptor() {
            public boolean shouldInterceptLoadRequest(String resource) {
                return interceptableResources.contains(resource);
            }

            public InputStream openRawResource(String resource) {
                InputStream open;
                if (isSharedMode || isTestApk) {
                    try {
                        open = context2.getAssets().open(resource);
                    } catch (IOException e) {
                        throw new RuntimeException(resource + " can't be found in assets.");
                    }
                } else if (isDownloadMode) {
                    try {
                        open = new FileInputStream(new File(context2.getApplicationContext().getDir(XWalkViewDelegate.XWALK_CORE_EXTRACTED_DIR, 0).getAbsolutePath(), resource));
                    } catch (FileNotFoundException e2) {
                        throw new RuntimeException(resource + " can't be found.");
                    }
                } else {
                    String resourceName = resource.split("\\.")[0];
                    try {
                        open = context2.getResources().openRawResource(XWalkViewDelegate.getResourceId(context2, resourceName, "raw"));
                    } catch (NotFoundException e3) {
                        throw new RuntimeException("R.raw." + resourceName + " can't be found.");
                    }
                }
                return open;
            }
        });
    }

    private static int getResourceId(Context context, String resourceName, String resourceType) {
        int resourceId = context.getResources().getIdentifier(resourceName, resourceType, context.getClass().getPackage().getName());
        if (resourceId == 0) {
            return context.getResources().getIdentifier(resourceName, resourceType, context.getPackageName());
        }
        return resourceId;
    }

    private static String getApplicationMetaData(Context context, String name) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData.get(name).toString();
        } catch (NameNotFoundException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }

    private static boolean isIaDevice() {
        String abi = getDeviceAbi();
        return abi.equals("x86") || abi.equals("x86_64");
    }

    private static String getDeviceAbi() {
        if (sDeviceAbi == null) {
            try {
                sDeviceAbi = Build.SUPPORTED_ABIS[0].toLowerCase();
            } catch (NoSuchFieldError e) {
                try {
                    InputStreamReader ir = new InputStreamReader(Runtime.getRuntime().exec("getprop ro.product.cpu.abi").getInputStream());
                    BufferedReader input = new BufferedReader(ir);
                    sDeviceAbi = input.readLine().toLowerCase();
                    input.close();
                    ir.close();
                } catch (IOException e2) {
                    throw new RuntimeException("Can not detect device's ABI");
                }
            }
            Log.d(TAG, "Device ABI: " + sDeviceAbi);
        }
        return sDeviceAbi;
    }
}
