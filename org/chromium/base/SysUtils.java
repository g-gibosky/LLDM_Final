package org.chromium.base;

import org.chromium.base.annotations.CalledByNative;

public class SysUtils {
    static final /* synthetic */ boolean $assertionsDisabled = (!SysUtils.class.desiredAssertionStatus());
    private static final int ANDROID_LOW_MEMORY_DEVICE_THRESHOLD_MB = 512;
    private static final String TAG = "SysUtils";
    private static Boolean sLowEndDevice;

    private SysUtils() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int amountOfPhysicalMemoryKB() {
        /*
        r8 = "^MemTotal:\\s+([0-9]+) kB$";
        r5 = java.util.regex.Pattern.compile(r8);
        r4 = android.os.StrictMode.allowThreadDiskReads();
        r1 = new java.io.FileReader;	 Catch:{ Exception -> 0x006d }
        r8 = "/proc/meminfo";
        r1.<init>(r8);	 Catch:{ Exception -> 0x006d }
        r6 = new java.io.BufferedReader;	 Catch:{ all -> 0x0068 }
        r6.<init>(r1);	 Catch:{ all -> 0x0068 }
    L_0x0016:
        r2 = r6.readLine();	 Catch:{ all -> 0x0063 }
        if (r2 != 0) goto L_0x002e;
    L_0x001c:
        r8 = "SysUtils";
        r9 = "/proc/meminfo lacks a MemTotal entry?";
        android.util.Log.w(r8, r9);	 Catch:{ all -> 0x0063 }
    L_0x0023:
        r6.close();	 Catch:{ all -> 0x0068 }
        r1.close();	 Catch:{ Exception -> 0x006d }
        android.os.StrictMode.setThreadPolicy(r4);
    L_0x002c:
        r7 = 0;
    L_0x002d:
        return r7;
    L_0x002e:
        r3 = r5.matcher(r2);	 Catch:{ all -> 0x0063 }
        r8 = r3.find();	 Catch:{ all -> 0x0063 }
        if (r8 == 0) goto L_0x0016;
    L_0x0038:
        r8 = 1;
        r8 = r3.group(r8);	 Catch:{ all -> 0x0063 }
        r7 = java.lang.Integer.parseInt(r8);	 Catch:{ all -> 0x0063 }
        r8 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        if (r7 > r8) goto L_0x0079;
    L_0x0045:
        r8 = "SysUtils";
        r9 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0063 }
        r9.<init>();	 Catch:{ all -> 0x0063 }
        r10 = "Invalid /proc/meminfo total size in kB: ";
        r9 = r9.append(r10);	 Catch:{ all -> 0x0063 }
        r10 = 1;
        r10 = r3.group(r10);	 Catch:{ all -> 0x0063 }
        r9 = r9.append(r10);	 Catch:{ all -> 0x0063 }
        r9 = r9.toString();	 Catch:{ all -> 0x0063 }
        android.util.Log.w(r8, r9);	 Catch:{ all -> 0x0063 }
        goto L_0x0023;
    L_0x0063:
        r8 = move-exception;
        r6.close();	 Catch:{ all -> 0x0068 }
        throw r8;	 Catch:{ all -> 0x0068 }
    L_0x0068:
        r8 = move-exception;
        r1.close();	 Catch:{ Exception -> 0x006d }
        throw r8;	 Catch:{ Exception -> 0x006d }
    L_0x006d:
        r0 = move-exception;
        r8 = "SysUtils";
        r9 = "Cannot get total physical size from /proc/meminfo";
        android.util.Log.w(r8, r9, r0);	 Catch:{ all -> 0x0083 }
        android.os.StrictMode.setThreadPolicy(r4);
        goto L_0x002c;
    L_0x0079:
        r6.close();	 Catch:{ all -> 0x0068 }
        r1.close();	 Catch:{ Exception -> 0x006d }
        android.os.StrictMode.setThreadPolicy(r4);
        goto L_0x002d;
    L_0x0083:
        r8 = move-exception;
        android.os.StrictMode.setThreadPolicy(r4);
        throw r8;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.base.SysUtils.amountOfPhysicalMemoryKB():int");
    }

    @CalledByNative
    public static boolean isLowEndDevice() {
        if (sLowEndDevice == null) {
            sLowEndDevice = Boolean.valueOf(detectLowEndDevice());
        }
        return sLowEndDevice.booleanValue();
    }

    private static boolean detectLowEndDevice() {
        if (!$assertionsDisabled && !CommandLine.isInitialized()) {
            throw new AssertionError();
        } else if (CommandLine.getInstance().hasSwitch(BaseSwitches.ENABLE_LOW_END_DEVICE_MODE)) {
            return true;
        } else {
            if (CommandLine.getInstance().hasSwitch(BaseSwitches.DISABLE_LOW_END_DEVICE_MODE)) {
                return false;
            }
            int ramSizeKB = amountOfPhysicalMemoryKB();
            if (ramSizeKB <= 0 || ramSizeKB / 1024 > 512) {
                return false;
            }
            return true;
        }
    }
}
