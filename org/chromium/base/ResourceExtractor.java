package org.chromium.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Trace;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import org.chromium.base.annotations.SuppressFBWarnings;

public class ResourceExtractor {
    static final /* synthetic */ boolean $assertionsDisabled = (!ResourceExtractor.class.desiredAssertionStatus());
    private static final String APP_VERSION_PREF = "org.chromium.base.ResourceExtractor.Version";
    private static final String ICU_DATA_FILENAME = "icudtl.dat";
    private static final String TAG = "cr.base";
    private static final String V8_NATIVES_DATA_FILENAME = "natives_blob.bin";
    private static final String V8_SNAPSHOT_DATA_FILENAME = "snapshot_blob.bin";
    private static ResourceExtractor sInstance;
    private static ResourceInterceptor sInterceptor = null;
    private static ResourceEntry[] sResourcesToExtract = new ResourceEntry[0];
    private final Context mContext;
    private ExtractTask mExtractTask;

    private class ExtractTask extends AsyncTask<Void, Void, Void> {
        private static final int BUFFER_SIZE = 16384;
        private final List<Runnable> mCompletionCallbacks;

        private void doInBackgroundImpl() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:44:0x00e8 in {7, 10, 13, 17, 20, 21, 22, 28, 37, 39, 40, 42, 45, 46, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r26 = this;
            r0 = r26;
            r0 = org.chromium.base.ResourceExtractor.this;
            r21 = r0;
            r16 = r21.getOutputDir();
            r0 = r26;
            r0 = org.chromium.base.ResourceExtractor.this;
            r21 = r0;
            r4 = r21.getAppDataDir();
            r21 = r16.exists();
            if (r21 != 0) goto L_0x0030;
        L_0x001a:
            r21 = r16.mkdirs();
            if (r21 != 0) goto L_0x0030;
        L_0x0020:
            r21 = "cr.base";
            r22 = "Unable to create pak resources directory!";
            r23 = 0;
            r0 = r23;
            r0 = new java.lang.Object[r0];
            r23 = r0;
            org.chromium.base.Log.m28e(r21, r22, r23);
        L_0x002f:
            return;
        L_0x0030:
            r21 = "checkPakTimeStamp";
            r0 = r26;
            r1 = r21;
            r0.beginTraceSection(r1);
            r8 = r26.getApkVersion();
            r17 = org.chromium.base.ContextUtils.getAppSharedPreferences();
            r21 = "org.chromium.base.ResourceExtractor.Version";
            r22 = 0;
            r0 = r17;
            r1 = r21;
            r2 = r22;
            r18 = r0.getLong(r1, r2);
            r21 = (r8 > r18 ? 1 : (r8 == r18 ? 0 : -1));
            if (r21 == 0) goto L_0x00b0;
        L_0x0053:
            r20 = 1;
        L_0x0055:
            r26.endTraceSection();
            if (r20 == 0) goto L_0x0074;
        L_0x005a:
            r0 = r26;
            r0 = org.chromium.base.ResourceExtractor.this;
            r21 = r0;
            r21.deleteFiles();
            r21 = r17.edit();
            r22 = "org.chromium.base.ResourceExtractor.Version";
            r0 = r21;
            r1 = r22;
            r21 = r0.putLong(r1, r8);
            r21.apply();
        L_0x0074:
            r21 = "WalkAssets";
            r0 = r26;
            r1 = r21;
            r0.beginTraceSection(r1);
            r21 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
            r0 = r21;
            r6 = new byte[r0];
            r5 = org.chromium.base.ResourceExtractor.sResourcesToExtract;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r14 = r5.length;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r12 = 0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x0089:
            if (r12 >= r14) goto L_0x012f;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x008b:
            r11 = r5[r12];	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = r11.extractedFileName;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r21 = r0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r21 = org.chromium.base.ResourceExtractor.isAppDataFile(r21);	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            if (r21 == 0) goto L_0x00b3;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x0097:
            r7 = r4;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x0098:
            r15 = new java.io.File;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = r11.extractedFileName;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r21 = r0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = r21;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r15.<init>(r7, r0);	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r22 = r15.length();	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r24 = 0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r21 = (r22 > r24 ? 1 : (r22 == r24 ? 0 : -1));	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            if (r21 == 0) goto L_0x00b6;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x00ad:
            r12 = r12 + 1;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            goto L_0x0089;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x00b0:
            r20 = 0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            goto L_0x0055;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x00b3:
            r7 = r16;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            goto L_0x0098;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x00b6:
            r21 = "ExtractResource";	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = r26;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r1 = r21;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0.beginTraceSection(r1);	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r21 = org.chromium.base.ResourceExtractor.sInterceptor;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            if (r21 == 0) goto L_0x010e;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x00c5:
            r21 = org.chromium.base.ResourceExtractor.sInterceptor;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = r11.extractedFileName;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r22 = r0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r21 = r21.shouldInterceptLoadRequest(r22);	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            if (r21 == 0) goto L_0x010e;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x00d3:
            r21 = org.chromium.base.ResourceExtractor.sInterceptor;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = r11.extractedFileName;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r22 = r0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r13 = r21.openRawResource(r22);	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x00df:
            r0 = r26;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0.extractResourceHelper(r13, r15, r6);	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r26.endTraceSection();
            goto L_0x00ad;
        L_0x00e8:
            r10 = move-exception;
            r21 = "cr.base";	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r22 = "Exception unpacking required pak resources: %s";	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r23 = 1;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = r23;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = new java.lang.Object[r0];	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r23 = r0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r24 = 0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r25 = r10.getMessage();	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r23[r24] = r25;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            org.chromium.base.Log.m38w(r21, r22, r23);	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = r26;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = org.chromium.base.ResourceExtractor.this;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r21 = r0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r21.deleteFiles();	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r26.endTraceSection();
            goto L_0x002f;
        L_0x010e:
            r0 = r26;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = org.chromium.base.ResourceExtractor.this;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r21 = r0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r21 = r21.mContext;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r21 = r21.getResources();	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r0 = r11.resourceId;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r22 = r0;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r13 = r21.openRawResource(r22);	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            goto L_0x00df;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x0125:
            r21 = move-exception;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
            r26.endTraceSection();
            throw r21;	 Catch:{ all -> 0x0125, IOException -> 0x00e8, all -> 0x012a }
        L_0x012a:
            r21 = move-exception;
            r26.endTraceSection();
            throw r21;
        L_0x012f:
            r26.endTraceSection();
            goto L_0x002f;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.chromium.base.ResourceExtractor.ExtractTask.doInBackgroundImpl():void");
        }

        private ExtractTask() {
            this.mCompletionCallbacks = new ArrayList();
        }

        private void extractResourceHelper(InputStream is, File outFile, byte[] buffer) throws IOException {
            Throwable th;
            OutputStream os = null;
            try {
                OutputStream os2 = new FileOutputStream(outFile);
                try {
                    Log.m29i(ResourceExtractor.TAG, "Extracting resource %s", outFile);
                    while (true) {
                        int count = is.read(buffer, 0, 16384);
                        if (count == -1) {
                            break;
                        }
                        os2.write(buffer, 0, count);
                    }
                    if (os2 != null) {
                        try {
                            os2.close();
                        } catch (Throwable th2) {
                            if (is != null) {
                                is.close();
                            }
                        }
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (Throwable th3) {
                    th = th3;
                    os = os2;
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Throwable th4) {
                            if (is != null) {
                                is.close();
                            }
                        }
                    }
                    if (is != null) {
                        is.close();
                    }
                    throw th;
                }
            } catch (Throwable th5) {
                th = th5;
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
                throw th;
            }
        }

        protected Void doInBackground(Void... unused) {
            beginTraceSection("ResourceExtractor.ExtractTask.doInBackground");
            try {
                doInBackgroundImpl();
                return null;
            } finally {
                endTraceSection();
            }
        }

        private void onPostExecuteImpl() {
            for (int i = 0; i < this.mCompletionCallbacks.size(); i++) {
                ((Runnable) this.mCompletionCallbacks.get(i)).run();
            }
            this.mCompletionCallbacks.clear();
        }

        protected void onPostExecute(Void result) {
            beginTraceSection("ResourceExtractor.ExtractTask.onPostExecute");
            try {
                onPostExecuteImpl();
            } finally {
                endTraceSection();
            }
        }

        private long getApkVersion() {
            try {
                return ResourceExtractor.this.mContext.getPackageManager().getPackageInfo(ResourceExtractor.this.mContext.getPackageName(), 0).lastUpdateTime;
            } catch (NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @TargetApi(18)
        private void beginTraceSection(String section) {
            if (VERSION.SDK_INT >= 18) {
                Trace.beginSection(section);
            }
        }

        @TargetApi(18)
        private void endTraceSection() {
            if (VERSION.SDK_INT >= 18) {
                Trace.endSection();
            }
        }
    }

    public static final class ResourceEntry {
        public final String extractedFileName;
        public final String pathWithinApk;
        public final int resourceId;

        public ResourceEntry(int resourceId, String pathWithinApk, String extractedFileName) {
            this.resourceId = resourceId;
            this.pathWithinApk = pathWithinApk;
            this.extractedFileName = extractedFileName;
        }
    }

    public interface ResourceInterceptor {
        InputStream openRawResource(String str);

        boolean shouldInterceptLoadRequest(String str);
    }

    private static boolean isAppDataFile(String file) {
        return ICU_DATA_FILENAME.equals(file) || V8_NATIVES_DATA_FILENAME.equals(file) || V8_SNAPSHOT_DATA_FILENAME.equals(file);
    }

    public static ResourceExtractor get(Context context) {
        if (sInstance == null) {
            sInstance = new ResourceExtractor(context);
        }
        return sInstance;
    }

    public static void setResourceInterceptor(ResourceInterceptor interceptor) {
        if ($assertionsDisabled || sInstance == null || sInstance.mExtractTask == null) {
            sInterceptor = interceptor;
            return;
        }
        throw new AssertionError("Must be called before startExtractingResources is called");
    }

    @SuppressFBWarnings({"EI_EXPOSE_STATIC_REP2"})
    public static void setResourcesToExtract(ResourceEntry[] entries) {
        if ($assertionsDisabled || sInstance == null || sInstance.mExtractTask == null) {
            sResourcesToExtract = entries;
            return;
        }
        throw new AssertionError("Must be called before startExtractingResources is called");
    }

    private ResourceExtractor(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void waitForCompletion() {
        if (!shouldSkipPakExtraction()) {
            if ($assertionsDisabled || this.mExtractTask != null) {
                try {
                    this.mExtractTask.get();
                    sInterceptor = null;
                    sInstance = null;
                    return;
                } catch (CancellationException e) {
                    deleteFiles();
                    return;
                } catch (ExecutionException e2) {
                    deleteFiles();
                    return;
                } catch (InterruptedException e3) {
                    deleteFiles();
                    return;
                }
            }
            throw new AssertionError();
        }
    }

    public void addCompletionCallback(Runnable callback) {
        ThreadUtils.assertOnUiThread();
        Handler handler = new Handler(Looper.getMainLooper());
        if (shouldSkipPakExtraction()) {
            handler.post(callback);
        } else if (!$assertionsDisabled && this.mExtractTask == null) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && this.mExtractTask.isCancelled()) {
            throw new AssertionError();
        } else if (this.mExtractTask.getStatus() == Status.FINISHED) {
            handler.post(callback);
        } else {
            this.mExtractTask.mCompletionCallbacks.add(callback);
        }
    }

    public void startExtractingResources() {
        if (this.mExtractTask == null && !shouldSkipPakExtraction()) {
            this.mExtractTask = new ExtractTask();
            this.mExtractTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    private File getAppDataDir() {
        return new File(PathUtils.getDataDirectory(this.mContext));
    }

    private File getOutputDir() {
        return new File(getAppDataDir(), "paks");
    }

    private void deleteFiles() {
        File icudata = new File(getAppDataDir(), ICU_DATA_FILENAME);
        if (icudata.exists() && !icudata.delete()) {
            Log.m28e(TAG, "Unable to remove the icudata %s", icudata.getName());
        }
        File v8_natives = new File(getAppDataDir(), V8_NATIVES_DATA_FILENAME);
        if (v8_natives.exists() && !v8_natives.delete()) {
            Log.m28e(TAG, "Unable to remove the v8 data %s", v8_natives.getName());
        }
        File v8_snapshot = new File(getAppDataDir(), V8_SNAPSHOT_DATA_FILENAME);
        if (v8_snapshot.exists() && !v8_snapshot.delete()) {
            Log.m28e(TAG, "Unable to remove the v8 data %s", v8_snapshot.getName());
        }
        File dir = getOutputDir();
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        Log.m28e(TAG, "Unable to remove existing resource %s", arr$[i$].getName());
                    }
                }
            }
        }
    }

    private static boolean shouldSkipPakExtraction() {
        return sResourcesToExtract.length == 0;
    }
}
