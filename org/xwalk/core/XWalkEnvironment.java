package org.xwalk.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

class XWalkEnvironment {
    private static final String META_XWALK_APK_URL = "xwalk_apk_url";
    private static final String META_XWALK_DOWNLOAD_MODE = "xwalk_download_mode";
    private static final String META_XWALK_DOWNLOAD_MODE_UPDATE = "xwalk_download_mode_update";
    private static final String META_XWALK_ENABLE_DOWNLOAD_MODE = "xwalk_enable_download_mode";
    private static final String META_XWALK_VERIFY = "xwalk_verify";
    private static final String OPTIMIZED_DEX_DIR = "dex";
    private static final String PACKAGE_RE = "[a-z]+\\.[a-z0-9]+\\.[a-z0-9]+.*";
    private static final String PRIVATE_DATA_DIRECTORY_SUFFIX = "xwalkcore";
    private static final String TAG = "XWalkLib";
    private static final String XWALK_CORE_EXTRACTED_DIR = "extracted_xwalkcore";
    private static Context sApplicationContext;
    private static String sApplicationName;
    private static String sDeviceAbi;
    private static Boolean sIsDownloadMode;
    private static Boolean sIsDownloadModeUpdate;
    private static Boolean sIsXWalkVerify;
    private static String sRuntimeAbi;
    private static String sXWalkApkUrl;

    XWalkEnvironment() {
    }

    public static void init(Context context) {
        sApplicationContext = context.getApplicationContext();
    }

    public static Context getApplicationContext() {
        return sApplicationContext;
    }

    public static SharedPreferences getSharedPreferences() {
        return sApplicationContext.getSharedPreferences("libxwalkcore", 0);
    }

    public static String getPrivateDataDir() {
        return sApplicationContext.getDir(PRIVATE_DATA_DIRECTORY_SUFFIX, 0).getAbsolutePath();
    }

    public static String getExtractedCoreDir() {
        return sApplicationContext.getDir(XWALK_CORE_EXTRACTED_DIR, 0).getAbsolutePath();
    }

    public static String getOptimizedDexDir() {
        return sApplicationContext.getDir(OPTIMIZED_DEX_DIR, 0).getAbsolutePath();
    }

    public static void setXWalkApkUrl(String url) {
        sXWalkApkUrl = url;
        Log.d(TAG, "Crosswalk APK download URL: " + sXWalkApkUrl);
    }

    public static String getXWalkApkUrl() {
        if (sXWalkApkUrl == null) {
            String url = getApplicationMetaData(META_XWALK_APK_URL);
            if (url == null) {
                sXWalkApkUrl = "";
            } else {
                String archQuery = "arch=" + getRuntimeAbi();
                try {
                    URI uri = new URI(url);
                    String query = uri.getQuery();
                    if (query == null) {
                        query = archQuery;
                    } else {
                        query = query + "&" + archQuery;
                    }
                    sXWalkApkUrl = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment()).toString();
                } catch (URISyntaxException e) {
                    throw new RuntimeException("Invalid xwalk_apk_url", e);
                }
            }
            Log.d(TAG, "Crosswalk APK download URL: " + sXWalkApkUrl);
        }
        return sXWalkApkUrl;
    }

    public static String getApplicationName() {
        if (sApplicationName == null) {
            try {
                PackageManager packageManager = sApplicationContext.getPackageManager();
                sApplicationName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(sApplicationContext.getPackageName(), 0));
            } catch (NameNotFoundException e) {
            }
            if (sApplicationName == null || sApplicationName.isEmpty() || sApplicationName.matches(PACKAGE_RE)) {
                sApplicationName = "this application";
            }
            Log.d(TAG, "Crosswalk application name: " + sApplicationName);
        }
        return sApplicationName;
    }

    public static boolean isDownloadMode() {
        if (sIsDownloadMode == null) {
            String enable = getApplicationMetaData(META_XWALK_DOWNLOAD_MODE);
            if (enable == null) {
                enable = getApplicationMetaData(META_XWALK_ENABLE_DOWNLOAD_MODE);
            }
            boolean z = enable != null && (enable.equalsIgnoreCase("enable") || enable.equalsIgnoreCase("true"));
            sIsDownloadMode = Boolean.valueOf(z);
            Log.d(TAG, "Crosswalk download mode: " + sIsDownloadMode);
        }
        return sIsDownloadMode.booleanValue();
    }

    public static boolean isDownloadModeUpdate() {
        if (sIsDownloadModeUpdate == null) {
            String enable = getApplicationMetaData(META_XWALK_DOWNLOAD_MODE_UPDATE);
            boolean z = enable != null && (enable.equalsIgnoreCase("enable") || enable.equalsIgnoreCase("true"));
            sIsDownloadModeUpdate = Boolean.valueOf(z);
            Log.d(TAG, "Crosswalk download mode update: " + sIsDownloadModeUpdate);
        }
        return sIsDownloadModeUpdate.booleanValue();
    }

    public static boolean isXWalkVerify() {
        if (sIsXWalkVerify == null) {
            String verify = getApplicationMetaData(META_XWALK_VERIFY);
            boolean z = verify == null || !(verify.equalsIgnoreCase("disable") || verify.equalsIgnoreCase("false"));
            sIsXWalkVerify = Boolean.valueOf(z);
            Log.d(TAG, "Crosswalk verify: " + sIsXWalkVerify);
        }
        return sIsXWalkVerify.booleanValue();
    }

    public static boolean isIaDevice() {
        String abi = getDeviceAbi();
        return abi.equals("x86") || abi.equals("x86_64");
    }

    public static boolean is64bitDevice() {
        String abi = getDeviceAbi();
        return abi.equals("arm64-v8a") || abi.equals("x86_64");
    }

    public static boolean is64bitApp() {
        String abi = getRuntimeAbi();
        return abi.equals("arm64-v8a") || abi.equals("x86_64");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getRuntimeAbi() {
        /*
        r7 = 3;
        r6 = 2;
        r5 = 1;
        r3 = 0;
        r4 = -1;
        r8 = sRuntimeAbi;
        if (r8 != 0) goto L_0x00d2;
    L_0x0009:
        r8 = android.os.Build.VERSION.SDK_INT;	 Catch:{ NoSuchFieldError -> 0x0015 }
        r9 = 21;
        if (r8 < r9) goto L_0x0044;
    L_0x000f:
        r8 = new java.lang.NoSuchFieldError;	 Catch:{ NoSuchFieldError -> 0x0015 }
        r8.<init>();	 Catch:{ NoSuchFieldError -> 0x0015 }
        throw r8;	 Catch:{ NoSuchFieldError -> 0x0015 }
    L_0x0015:
        r2 = move-exception;
        r8 = "os.arch";
        r8 = java.lang.System.getProperty(r8);
        r1 = r8.toLowerCase();
        r8 = r1.hashCode();
        switch(r8) {
            case -1409295825: goto L_0x0125;
            case -1221096139: goto L_0x0147;
            case -806050265: goto L_0x011a;
            case -738963905: goto L_0x0130;
            case 117046: goto L_0x010f;
            case 117110: goto L_0x00e4;
            case 3178856: goto L_0x00f9;
            case 3181739: goto L_0x00ee;
            case 3222903: goto L_0x0104;
            case 93084186: goto L_0x015f;
            case 93086174: goto L_0x0153;
            case 145444210: goto L_0x013b;
            default: goto L_0x0027;
        };
    L_0x0027:
        r3 = r4;
    L_0x0028:
        switch(r3) {
            case 0: goto L_0x016b;
            case 1: goto L_0x016b;
            case 2: goto L_0x016b;
            case 3: goto L_0x016b;
            case 4: goto L_0x0171;
            case 5: goto L_0x0171;
            case 6: goto L_0x0183;
            case 7: goto L_0x0183;
            case 8: goto L_0x0183;
            case 9: goto L_0x0189;
            case 10: goto L_0x0189;
            case 11: goto L_0x0189;
            default: goto L_0x002b;
        };
    L_0x002b:
        r3 = new java.lang.RuntimeException;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Unexpected os.arch: ";
        r4 = r4.append(r5);
        r4 = r4.append(r1);
        r4 = r4.toString();
        r3.<init>(r4);
        throw r3;
    L_0x0044:
        r8 = android.os.Build.CPU_ABI;	 Catch:{ NoSuchFieldError -> 0x0015 }
        r0 = r8.toLowerCase();	 Catch:{ NoSuchFieldError -> 0x0015 }
        r8 = r0.hashCode();	 Catch:{ NoSuchFieldError -> 0x0015 }
        switch(r8) {
            case -806050265: goto L_0x0096;
            case -738963905: goto L_0x006e;
            case 117110: goto L_0x008c;
            case 145444210: goto L_0x0078;
            case 1431565292: goto L_0x0082;
            default: goto L_0x0051;
        };	 Catch:{ NoSuchFieldError -> 0x0015 }
    L_0x0051:
        r8 = r4;
    L_0x0052:
        switch(r8) {
            case 0: goto L_0x00a0;
            case 1: goto L_0x00a0;
            case 2: goto L_0x00d5;
            case 3: goto L_0x00da;
            case 4: goto L_0x00df;
            default: goto L_0x0055;
        };	 Catch:{ NoSuchFieldError -> 0x0015 }
    L_0x0055:
        r8 = new java.lang.RuntimeException;	 Catch:{ NoSuchFieldError -> 0x0015 }
        r9 = new java.lang.StringBuilder;	 Catch:{ NoSuchFieldError -> 0x0015 }
        r9.<init>();	 Catch:{ NoSuchFieldError -> 0x0015 }
        r10 = "Unexpected CPU_ABI: ";
        r9 = r9.append(r10);	 Catch:{ NoSuchFieldError -> 0x0015 }
        r9 = r9.append(r0);	 Catch:{ NoSuchFieldError -> 0x0015 }
        r9 = r9.toString();	 Catch:{ NoSuchFieldError -> 0x0015 }
        r8.<init>(r9);	 Catch:{ NoSuchFieldError -> 0x0015 }
        throw r8;	 Catch:{ NoSuchFieldError -> 0x0015 }
    L_0x006e:
        r8 = "armeabi";
        r8 = r0.equals(r8);	 Catch:{ NoSuchFieldError -> 0x0015 }
        if (r8 == 0) goto L_0x0051;
    L_0x0076:
        r8 = r3;
        goto L_0x0052;
    L_0x0078:
        r8 = "armeabi-v7a";
        r8 = r0.equals(r8);	 Catch:{ NoSuchFieldError -> 0x0015 }
        if (r8 == 0) goto L_0x0051;
    L_0x0080:
        r8 = r5;
        goto L_0x0052;
    L_0x0082:
        r8 = "arm64-v8a";
        r8 = r0.equals(r8);	 Catch:{ NoSuchFieldError -> 0x0015 }
        if (r8 == 0) goto L_0x0051;
    L_0x008a:
        r8 = r6;
        goto L_0x0052;
    L_0x008c:
        r8 = "x86";
        r8 = r0.equals(r8);	 Catch:{ NoSuchFieldError -> 0x0015 }
        if (r8 == 0) goto L_0x0051;
    L_0x0094:
        r8 = r7;
        goto L_0x0052;
    L_0x0096:
        r8 = "x86_64";
        r8 = r0.equals(r8);	 Catch:{ NoSuchFieldError -> 0x0015 }
        if (r8 == 0) goto L_0x0051;
    L_0x009e:
        r8 = 4;
        goto L_0x0052;
    L_0x00a0:
        r8 = "armeabi-v7a";
        sRuntimeAbi = r8;	 Catch:{ NoSuchFieldError -> 0x0015 }
    L_0x00a4:
        r3 = sRuntimeAbi;
        r4 = "armeabi-v7a";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x019b;
    L_0x00ae:
        r3 = isIaDevice();
        if (r3 == 0) goto L_0x00b8;
    L_0x00b4:
        r3 = "x86";
        sRuntimeAbi = r3;
    L_0x00b8:
        r3 = "XWalkLib";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Runtime ABI: ";
        r4 = r4.append(r5);
        r5 = sRuntimeAbi;
        r4 = r4.append(r5);
        r4 = r4.toString();
        android.util.Log.d(r3, r4);
    L_0x00d2:
        r3 = sRuntimeAbi;
        return r3;
    L_0x00d5:
        r8 = "arm64-v8a";
        sRuntimeAbi = r8;	 Catch:{ NoSuchFieldError -> 0x0015 }
        goto L_0x00a4;
    L_0x00da:
        r8 = "x86";
        sRuntimeAbi = r8;	 Catch:{ NoSuchFieldError -> 0x0015 }
        goto L_0x00a4;
    L_0x00df:
        r8 = "x86_64";
        sRuntimeAbi = r8;	 Catch:{ NoSuchFieldError -> 0x0015 }
        goto L_0x00a4;
    L_0x00e4:
        r5 = "x86";
        r5 = r1.equals(r5);
        if (r5 == 0) goto L_0x0027;
    L_0x00ec:
        goto L_0x0028;
    L_0x00ee:
        r3 = "i686";
        r3 = r1.equals(r3);
        if (r3 == 0) goto L_0x0027;
    L_0x00f6:
        r3 = r5;
        goto L_0x0028;
    L_0x00f9:
        r3 = "i386";
        r3 = r1.equals(r3);
        if (r3 == 0) goto L_0x0027;
    L_0x0101:
        r3 = r6;
        goto L_0x0028;
    L_0x0104:
        r3 = "ia32";
        r3 = r1.equals(r3);
        if (r3 == 0) goto L_0x0027;
    L_0x010c:
        r3 = r7;
        goto L_0x0028;
    L_0x010f:
        r3 = "x64";
        r3 = r1.equals(r3);
        if (r3 == 0) goto L_0x0027;
    L_0x0117:
        r3 = 4;
        goto L_0x0028;
    L_0x011a:
        r3 = "x86_64";
        r3 = r1.equals(r3);
        if (r3 == 0) goto L_0x0027;
    L_0x0122:
        r3 = 5;
        goto L_0x0028;
    L_0x0125:
        r3 = "armv7l";
        r3 = r1.equals(r3);
        if (r3 == 0) goto L_0x0027;
    L_0x012d:
        r3 = 6;
        goto L_0x0028;
    L_0x0130:
        r3 = "armeabi";
        r3 = r1.equals(r3);
        if (r3 == 0) goto L_0x0027;
    L_0x0138:
        r3 = 7;
        goto L_0x0028;
    L_0x013b:
        r3 = "armeabi-v7a";
        r3 = r1.equals(r3);
        if (r3 == 0) goto L_0x0027;
    L_0x0143:
        r3 = 8;
        goto L_0x0028;
    L_0x0147:
        r3 = "aarch64";
        r3 = r1.equals(r3);
        if (r3 == 0) goto L_0x0027;
    L_0x014f:
        r3 = 9;
        goto L_0x0028;
    L_0x0153:
        r3 = "armv8";
        r3 = r1.equals(r3);
        if (r3 == 0) goto L_0x0027;
    L_0x015b:
        r3 = 10;
        goto L_0x0028;
    L_0x015f:
        r3 = "arm64";
        r3 = r1.equals(r3);
        if (r3 == 0) goto L_0x0027;
    L_0x0167:
        r3 = 11;
        goto L_0x0028;
    L_0x016b:
        r3 = "x86";
        sRuntimeAbi = r3;
        goto L_0x00a4;
    L_0x0171:
        r3 = is64bitDevice();
        if (r3 == 0) goto L_0x017d;
    L_0x0177:
        r3 = "x86_64";
        sRuntimeAbi = r3;
        goto L_0x00a4;
    L_0x017d:
        r3 = "x86";
        sRuntimeAbi = r3;
        goto L_0x00a4;
    L_0x0183:
        r3 = "armeabi-v7a";
        sRuntimeAbi = r3;
        goto L_0x00a4;
    L_0x0189:
        r3 = is64bitDevice();
        if (r3 == 0) goto L_0x0195;
    L_0x018f:
        r3 = "arm64-v8a";
        sRuntimeAbi = r3;
        goto L_0x00a4;
    L_0x0195:
        r3 = "armeabi-v7a";
        sRuntimeAbi = r3;
        goto L_0x00a4;
    L_0x019b:
        r3 = sRuntimeAbi;
        r4 = "arm64-v8a";
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x00b8;
    L_0x01a5:
        r3 = isIaDevice();
        if (r3 == 0) goto L_0x00b8;
    L_0x01ab:
        r3 = "x86_64";
        sRuntimeAbi = r3;
        goto L_0x00b8;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.xwalk.core.XWalkEnvironment.getRuntimeAbi():java.lang.String");
    }

    public static String getDeviceAbi() {
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

    private static String getApplicationMetaData(String name) {
        try {
            return sApplicationContext.getPackageManager().getApplicationInfo(sApplicationContext.getPackageName(), 128).metaData.get(name).toString();
        } catch (NameNotFoundException e) {
            return null;
        } catch (NullPointerException e2) {
            return null;
        }
    }
}
