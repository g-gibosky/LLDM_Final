package org.chromium.ui;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.chromium.base.ContentUriUtils;

public class UiUtils {
    public static final String EXTERNAL_IMAGE_FILE_PATH = "browser-images";
    public static final String IMAGE_FILE_PATH = "images";
    private static final float KEYBOARD_DETECT_BOTTOM_THRESHOLD_DP = 100.0f;
    private static final int KEYBOARD_RETRY_ATTEMPTS = 10;
    private static final long KEYBOARD_RETRY_DELAY_MS = 100;
    private static final String TAG = "UiUtils";
    private static KeyboardShowingDelegate sKeyboardShowingDelegate;

    public interface KeyboardShowingDelegate {
        boolean disableKeyboardCheck(Context context, View view);
    }

    public static android.graphics.Bitmap generateScaledScreenshot(android.view.View r22, int r23, android.graphics.Bitmap.Config r24) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0118 in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r16 = 0;
        r4 = r22.isDrawingCacheEnabled();
        r17 = 1;
        r0 = r22;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r1 = r17;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        prepareViewHierarchyForScreenshot(r0, r1);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        if (r4 != 0) goto L_0x001a;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
    L_0x0011:
        r17 = 1;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r22;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r1 = r17;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0.setDrawingCacheEnabled(r1);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
    L_0x001a:
        r8 = r22.getDrawingCache();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        if (r8 == 0) goto L_0x0087;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
    L_0x0020:
        r17 = r8.getHeight();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r17;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r10 = (double) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r17 = r8.getWidth();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r17;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r12 = (double) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r7 = (int) r12;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r6 = (int) r10;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        if (r23 <= 0) goto L_0x004f;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
    L_0x0032:
        r0 = r23;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = (double) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r20 = java.lang.Math.max(r12, r10);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r14 = r18 / r20;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r12 * r14;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = java.lang.Math.round(r18);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r18;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r7 = (int) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r10 * r14;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = java.lang.Math.round(r18);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r18;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r6 = (int) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
    L_0x004f:
        r17 = 1;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r17;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r9 = android.graphics.Bitmap.createScaledBitmap(r8, r7, r6, r0);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r17 = r9.getConfig();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r17;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r1 = r24;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        if (r0 == r1) goto L_0x0084;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
    L_0x0061:
        r17 = 0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r24;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r1 = r17;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r16 = r9.copy(r0, r1);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r9.recycle();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r9 = 0;
    L_0x006f:
        if (r4 != 0) goto L_0x007a;
    L_0x0071:
        r17 = 0;
        r0 = r22;
        r1 = r17;
        r0.setDrawingCacheEnabled(r1);
    L_0x007a:
        r17 = 0;
        r0 = r22;
        r1 = r17;
        prepareViewHierarchyForScreenshot(r0, r1);
    L_0x0083:
        return r16;
    L_0x0084:
        r16 = r9;
        goto L_0x006f;
    L_0x0087:
        r17 = r22.getMeasuredHeight();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        if (r17 <= 0) goto L_0x006f;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
    L_0x008d:
        r17 = r22.getMeasuredWidth();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        if (r17 <= 0) goto L_0x006f;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
    L_0x0093:
        r17 = r22.getMeasuredHeight();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r17;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r10 = (double) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r17 = r22.getMeasuredWidth();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r17;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r12 = (double) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r7 = (int) r12;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r6 = (int) r10;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        if (r23 <= 0) goto L_0x00c2;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
    L_0x00a5:
        r0 = r23;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = (double) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r20 = java.lang.Math.max(r12, r10);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r14 = r18 / r20;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r12 * r14;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = java.lang.Math.round(r18);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r18;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r7 = (int) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r10 * r14;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = java.lang.Math.round(r18);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r18;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r6 = (int) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
    L_0x00c2:
        r0 = r24;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r2 = android.graphics.Bitmap.createBitmap(r7, r6, r0);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r3 = new android.graphics.Canvas;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r3.<init>(r2);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = (double) r7;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r18 / r12;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r18;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = (float) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r17 = r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = (double) r6;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r18 / r10;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r18;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = (float) r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r0;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r17;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r1 = r18;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r3.scale(r0, r1);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0 = r22;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r0.draw(r3);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r16 = r2;
        goto L_0x006f;
    L_0x00f0:
        r5 = move-exception;
        r17 = "UiUtils";	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = new java.lang.StringBuilder;	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18.<init>();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r19 = "Unable to capture screenshot and scale it down.";	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r18.append(r19);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r19 = r5.getMessage();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r18.append(r19);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        r18 = r18.toString();	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        android.util.Log.d(r17, r18);	 Catch:{ OutOfMemoryError -> 0x00f0, all -> 0x0123 }
        if (r4 != 0) goto L_0x0118;
    L_0x010f:
        r17 = 0;
        r0 = r22;
        r1 = r17;
        r0.setDrawingCacheEnabled(r1);
    L_0x0118:
        r17 = 0;
        r0 = r22;
        r1 = r17;
        prepareViewHierarchyForScreenshot(r0, r1);
        goto L_0x0083;
    L_0x0123:
        r17 = move-exception;
        if (r4 != 0) goto L_0x012f;
    L_0x0126:
        r18 = 0;
        r0 = r22;
        r1 = r18;
        r0.setDrawingCacheEnabled(r1);
    L_0x012f:
        r18 = 0;
        r0 = r22;
        r1 = r18;
        prepareViewHierarchyForScreenshot(r0, r1);
        throw r17;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.ui.UiUtils.generateScaledScreenshot(android.view.View, int, android.graphics.Bitmap$Config):android.graphics.Bitmap");
    }

    private UiUtils() {
    }

    public static void setKeyboardShowingDelegate(KeyboardShowingDelegate delegate) {
        sKeyboardShowingDelegate = delegate;
    }

    public static void showKeyboard(final View view) {
        final Handler handler = new Handler();
        final AtomicInteger attempt = new AtomicInteger();
        new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService("input_method");
                ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
                try {
                    imm.showSoftInput(view, 0);
                    StrictMode.setThreadPolicy(oldPolicy);
                } catch (IllegalArgumentException e) {
                    if (attempt.incrementAndGet() <= 10) {
                        handler.postDelayed(this, UiUtils.KEYBOARD_RETRY_DELAY_MS);
                    } else {
                        Log.e(UiUtils.TAG, "Unable to open keyboard.  Giving up.", e);
                    }
                    StrictMode.setThreadPolicy(oldPolicy);
                } catch (Throwable th) {
                    StrictMode.setThreadPolicy(oldPolicy);
                }
            }
        }.run();
    }

    public static boolean hideKeyboard(View view) {
        return ((InputMethodManager) view.getContext().getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isKeyboardShowing(Context context, View view) {
        if (sKeyboardShowingDelegate != null && sKeyboardShowingDelegate.disableKeyboardCheck(context, view)) {
            return false;
        }
        View rootView = view.getRootView();
        if (rootView == null) {
            return false;
        }
        Rect appRect = new Rect();
        rootView.getWindowVisibleDisplayFrame(appRect);
        if (((float) Math.abs(rootView.getHeight() - appRect.height())) / context.getResources().getDisplayMetrics().density > KEYBOARD_DETECT_BOTTOM_THRESHOLD_DP) {
            return true;
        }
        return false;
    }

    public static Set<String> getIMELocales(Context context) {
        LinkedHashSet<String> locales = new LinkedHashSet();
        InputMethodManager imManager = (InputMethodManager) context.getSystemService("input_method");
        List<InputMethodInfo> enabledMethods = imManager.getEnabledInputMethodList();
        for (int i = 0; i < enabledMethods.size(); i++) {
            List<InputMethodSubtype> subtypes = imManager.getEnabledInputMethodSubtypeList((InputMethodInfo) enabledMethods.get(i), true);
            if (subtypes != null) {
                for (int j = 0; j < subtypes.size(); j++) {
                    String locale = ((InputMethodSubtype) subtypes.get(j)).getLocale();
                    if (!TextUtils.isEmpty(locale)) {
                        locales.add(locale);
                    }
                }
            }
        }
        return locales;
    }

    public static int insertBefore(ViewGroup container, View newView, View existingView) {
        return insertView(container, newView, existingView, false);
    }

    public static int insertAfter(ViewGroup container, View newView, View existingView) {
        return insertView(container, newView, existingView, true);
    }

    private static int insertView(ViewGroup container, View newView, View existingView, boolean after) {
        int index = container.indexOfChild(newView);
        if (index >= 0) {
            return index;
        }
        index = container.indexOfChild(existingView);
        if (index < 0) {
            return -1;
        }
        if (after) {
            index++;
        }
        container.addView(newView, index);
        return index;
    }

    private static void prepareViewHierarchyForScreenshot(View view, boolean takingScreenshot) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                prepareViewHierarchyForScreenshot(viewGroup.getChildAt(i), takingScreenshot);
            }
        } else if (view instanceof SurfaceView) {
            view.setWillNotDraw(!takingScreenshot);
        }
    }

    public static File getDirectoryForImageCapture(Context context) throws IOException {
        ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        try {
            File path;
            if (VERSION.SDK_INT >= 18) {
                path = new File(context.getFilesDir(), IMAGE_FILE_PATH);
                if (!(path.exists() || path.mkdir())) {
                    throw new IOException("Folder cannot be created.");
                }
            }
            File externalDataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            path = new File(externalDataDir.getAbsolutePath() + File.separator + EXTERNAL_IMAGE_FILE_PATH);
            if (!(path.exists() || path.mkdirs())) {
                path = externalDataDir;
            }
            StrictMode.setThreadPolicy(oldPolicy);
            return path;
        } catch (Throwable th) {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    public static Uri getUriForImageCaptureFile(Context context, File file) {
        return VERSION.SDK_INT >= 18 ? ContentUriUtils.getContentUriFromFile(context, file) : Uri.fromFile(file);
    }
}
