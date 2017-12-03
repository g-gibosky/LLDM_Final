package org.chromium.base;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import org.chromium.base.annotations.CalledByNative;

public abstract class ContentUriUtils {
    private static final String TAG = "ContentUriUtils";
    private static FileProviderUtil sFileProviderUtil;
    private static final Object sLock = new Object();

    public interface FileProviderUtil {
        Uri getContentUriFromFile(Context context, File file);
    }

    public static java.lang.String getDisplayName(android.net.Uri r9, android.content.ContentResolver r10, java.lang.String r11) {
        /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1431)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1453)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:79)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        if (r10 == 0) goto L_0x0004;
    L_0x0002:
        if (r9 != 0) goto L_0x0007;
    L_0x0004:
        r0 = "";
    L_0x0006:
        return r0;
    L_0x0007:
        r6 = 0;
        r2 = 0;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r0 = r10;
        r1 = r9;
        r6 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ NullPointerException -> 0x0037, all -> 0x0040 }
        if (r6 == 0) goto L_0x002f;	 Catch:{ NullPointerException -> 0x0037, all -> 0x0040 }
    L_0x0014:
        r0 = r6.getCount();	 Catch:{ NullPointerException -> 0x0037, all -> 0x0040 }
        r1 = 1;	 Catch:{ NullPointerException -> 0x0037, all -> 0x0040 }
        if (r0 < r1) goto L_0x002f;	 Catch:{ NullPointerException -> 0x0037, all -> 0x0040 }
    L_0x001b:
        r6.moveToFirst();	 Catch:{ NullPointerException -> 0x0037, all -> 0x0040 }
        r8 = r6.getColumnIndex(r11);	 Catch:{ NullPointerException -> 0x0037, all -> 0x0040 }
        r0 = -1;	 Catch:{ NullPointerException -> 0x0037, all -> 0x0040 }
        if (r8 <= r0) goto L_0x002f;	 Catch:{ NullPointerException -> 0x0037, all -> 0x0040 }
    L_0x0025:
        r0 = r6.getString(r8);	 Catch:{ NullPointerException -> 0x0037, all -> 0x0040 }
        if (r6 == 0) goto L_0x0006;
    L_0x002b:
        r6.close();
        goto L_0x0006;
    L_0x002f:
        if (r6 == 0) goto L_0x0034;
    L_0x0031:
        r6.close();
    L_0x0034:
        r0 = "";
        goto L_0x0006;
    L_0x0037:
        r7 = move-exception;
        r0 = "";	 Catch:{ NullPointerException -> 0x0037, all -> 0x0040 }
        if (r6 == 0) goto L_0x0006;
    L_0x003c:
        r6.close();
        goto L_0x0006;
    L_0x0040:
        r0 = move-exception;
        if (r6 == 0) goto L_0x0046;
    L_0x0043:
        r6.close();
    L_0x0046:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.base.ContentUriUtils.getDisplayName(android.net.Uri, android.content.ContentResolver, java.lang.String):java.lang.String");
    }

    private ContentUriUtils() {
    }

    public static void setFileProviderUtil(FileProviderUtil util) {
        synchronized (sLock) {
            sFileProviderUtil = util;
        }
    }

    public static Uri getContentUriFromFile(Context context, File file) {
        synchronized (sLock) {
            if (sFileProviderUtil != null) {
                Uri contentUriFromFile = sFileProviderUtil.getContentUriFromFile(context, file);
                return contentUriFromFile;
            }
            return null;
        }
    }

    @CalledByNative
    public static int openContentUriForRead(Context context, String uriString) {
        ParcelFileDescriptor pfd = getParcelFileDescriptor(context, uriString);
        if (pfd != null) {
            return pfd.detachFd();
        }
        return -1;
    }

    @CalledByNative
    public static boolean contentUriExists(Context context, String uriString) {
        return getParcelFileDescriptor(context, uriString) != null;
    }

    @CalledByNative
    public static String getMimeType(Context context, String uriString) {
        ContentResolver resolver = context.getContentResolver();
        if (resolver == null) {
            return null;
        }
        return resolver.getType(Uri.parse(uriString));
    }

    private static ParcelFileDescriptor getParcelFileDescriptor(Context context, String uriString) {
        ParcelFileDescriptor pfd = null;
        try {
            pfd = context.getContentResolver().openFileDescriptor(Uri.parse(uriString), "r");
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Cannot find content uri: " + uriString, e);
        } catch (SecurityException e2) {
            Log.w(TAG, "Cannot open content uri: " + uriString, e2);
        } catch (IllegalArgumentException e3) {
            Log.w(TAG, "Unknown content uri: " + uriString, e3);
        } catch (IllegalStateException e4) {
            Log.w(TAG, "Unknown content uri: " + uriString, e4);
        }
        return pfd;
    }
}
