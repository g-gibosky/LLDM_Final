package org.xwalk.core;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import org.chromium.content.common.ContentSwitches;

class XWalkLibraryLoader {
    private static final String DEFAULT_DOWNLOAD_FILE_NAME = "xwalk_download.tmp";
    private static final String DOWNLOAD_WITHOUT_NOTIFICATION = "android.permission.DOWNLOAD_WITHOUT_NOTIFICATION";
    private static final String TAG = "XWalkLib";
    private static AsyncTask<Void, Integer, Integer> sActiveTask;

    public interface ActivateListener {
        void onActivateCompleted();

        void onActivateFailed();

        void onActivateStarted();
    }

    private static class ActivateTask extends AsyncTask<Void, Integer, Integer> {
        ActivateListener mListener;

        ActivateTask(ActivateListener listener) {
            this.mListener = listener;
        }

        protected void onPreExecute() {
            Log.d(XWalkLibraryLoader.TAG, "ActivateTask started");
            XWalkLibraryLoader.sActiveTask = this;
            this.mListener.onActivateStarted();
        }

        protected Integer doInBackground(Void... params) {
            if (XWalkCoreWrapper.getInstance() != null) {
                return Integer.valueOf(-1);
            }
            return Integer.valueOf(XWalkCoreWrapper.attachXWalkCore());
        }

        protected void onPostExecute(Integer result) {
            if (result.intValue() == 1) {
                XWalkCoreWrapper.dockXWalkCore();
            }
            Log.d(XWalkLibraryLoader.TAG, "ActivateTask finished, " + result);
            XWalkLibraryLoader.sActiveTask = null;
            if (result.intValue() > 1) {
                this.mListener.onActivateFailed();
            } else {
                this.mListener.onActivateCompleted();
            }
        }
    }

    public interface DecompressListener {
        void onDecompressCancelled();

        void onDecompressCompleted();

        void onDecompressStarted();
    }

    private static class DecompressTask extends AsyncTask<Void, Integer, Integer> {
        boolean mIsCompressed;
        boolean mIsDecompressed;
        DecompressListener mListener;

        DecompressTask(DecompressListener listener) {
            this.mListener = listener;
        }

        protected void onPreExecute() {
            boolean z = false;
            Log.d(XWalkLibraryLoader.TAG, "DecompressTask started");
            XWalkLibraryLoader.sActiveTask = this;
            this.mIsCompressed = XWalkDecompressor.isLibraryCompressed();
            if (this.mIsCompressed) {
                int version = XWalkEnvironment.getSharedPreferences().getInt("version", 0);
                if (version > 0 && version == 8) {
                    z = true;
                }
                this.mIsDecompressed = z;
            }
            if (this.mIsCompressed && !this.mIsDecompressed) {
                this.mListener.onDecompressStarted();
            }
        }

        protected Integer doInBackground(Void... params) {
            if (!this.mIsCompressed || this.mIsDecompressed) {
                return Integer.valueOf(0);
            }
            if (!XWalkDecompressor.decompressLibrary()) {
                return Integer.valueOf(1);
            }
            XWalkEnvironment.getSharedPreferences().edit().putInt("version", 8).apply();
            return Integer.valueOf(0);
        }

        protected void onCancelled(Integer result) {
            Log.d(XWalkLibraryLoader.TAG, "DecompressTask cancelled");
            XWalkLibraryLoader.sActiveTask = null;
            this.mListener.onDecompressCancelled();
        }

        protected void onPostExecute(Integer result) {
            Log.d(XWalkLibraryLoader.TAG, "DecompressTask finished, " + result);
            if (result.intValue() != 0) {
                throw new RuntimeException("Decompression Failed");
            }
            XWalkLibraryLoader.sActiveTask = null;
            this.mListener.onDecompressCompleted();
        }
    }

    public interface DownloadListener {
        void onDownloadCancelled();

        void onDownloadCompleted(Uri uri);

        void onDownloadFailed(int i, int i2);

        void onDownloadStarted();

        void onDownloadUpdated(int i);
    }

    private static class DownloadManagerTask extends AsyncTask<Void, Integer, Integer> {
        private static final int MAX_PAUSED_COUNT = 6000;
        private static final int QUERY_INTERVAL_MS = 100;
        private Context mContext;
        private long mDownloadId;
        private DownloadManager mDownloadManager;
        private String mDownloadUrl;
        private DownloadListener mListener;

        DownloadManagerTask(DownloadListener listener, Context context, String url) {
            this.mListener = listener;
            this.mContext = context;
            this.mDownloadUrl = url;
            this.mDownloadManager = (DownloadManager) context.getSystemService(ContentSwitches.SWITCH_DOWNLOAD_PROCESS);
        }

        protected void onPreExecute() {
            Log.d(XWalkLibraryLoader.TAG, "DownloadManagerTask started, " + this.mDownloadUrl);
            XWalkLibraryLoader.sActiveTask = this;
            String savedFile = XWalkLibraryLoader.DEFAULT_DOWNLOAD_FILE_NAME;
            try {
                String name = new File(new URL(this.mDownloadUrl).getPath()).getName();
                if (!name.isEmpty()) {
                    savedFile = name;
                }
                File downloadFile = new File(this.mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), savedFile);
                if (downloadFile.isFile()) {
                    downloadFile.delete();
                }
                Request request = new Request(Uri.parse(this.mDownloadUrl));
                request.setDestinationInExternalFilesDir(this.mContext, Environment.DIRECTORY_DOWNLOADS, savedFile);
                if (isSilentDownload()) {
                    request.setNotificationVisibility(2);
                }
                this.mDownloadId = this.mDownloadManager.enqueue(request);
                this.mListener.onDownloadStarted();
            } catch (MalformedURLException e) {
                Log.e(XWalkLibraryLoader.TAG, "Invalid download URL " + this.mDownloadUrl);
                this.mDownloadUrl = null;
            } catch (NullPointerException e2) {
                Log.e(XWalkLibraryLoader.TAG, "Invalid download URL " + this.mDownloadUrl);
                this.mDownloadUrl = null;
            }
        }

        protected Integer doInBackground(Void... params) {
            if (this.mDownloadUrl == null) {
                return Integer.valueOf(16);
            }
            Query query = new Query().setFilterById(new long[]{this.mDownloadId});
            int pausedCount = 0;
            while (!isCancelled()) {
                try {
                    Thread.sleep(100);
                    Cursor cursor = this.mDownloadManager.query(query);
                    if (cursor != null && cursor.moveToFirst()) {
                        int totalIdx = cursor.getColumnIndex("total_size");
                        int downloadIdx = cursor.getColumnIndex("bytes_so_far");
                        int totalSize = cursor.getInt(totalIdx);
                        int downloadSize = cursor.getInt(downloadIdx);
                        if (totalSize > 0) {
                            publishProgress(new Integer[]{Integer.valueOf(downloadSize), Integer.valueOf(totalSize)});
                        }
                        int status = cursor.getInt(cursor.getColumnIndex("status"));
                        if (status == 16 || status == 8) {
                            return Integer.valueOf(status);
                        }
                        if (status == 4) {
                            pausedCount++;
                            if (pausedCount == MAX_PAUSED_COUNT) {
                                return Integer.valueOf(status);
                            }
                        } else {
                            continue;
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
            return Integer.valueOf(2);
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d(XWalkLibraryLoader.TAG, "DownloadManagerTask updated: " + progress[0] + "/" + progress[1]);
            int percentage = 0;
            if (progress[1].intValue() > 0) {
                percentage = (int) ((((double) progress[0].intValue()) * 100.0d) / ((double) progress[1].intValue()));
            }
            this.mListener.onDownloadUpdated(percentage);
        }

        protected void onCancelled(Integer result) {
            this.mDownloadManager.remove(new long[]{this.mDownloadId});
            Log.d(XWalkLibraryLoader.TAG, "DownloadManagerTask cancelled");
            XWalkLibraryLoader.sActiveTask = null;
            this.mListener.onDownloadCancelled();
        }

        protected void onPostExecute(Integer result) {
            Log.d(XWalkLibraryLoader.TAG, "DownloadManagerTask finished, " + result);
            XWalkLibraryLoader.sActiveTask = null;
            Cursor cursor;
            if (result.intValue() == 8) {
                Uri uri = this.mDownloadManager.getUriForDownloadedFile(this.mDownloadId);
                Log.d(XWalkLibraryLoader.TAG, "Uri for downloaded file:" + uri.toString());
                if (uri.getScheme().equals("content")) {
                    cursor = this.mDownloadManager.query(new Query().setFilterById(new long[]{this.mDownloadId}));
                    if (cursor != null && cursor.moveToFirst()) {
                        uri = Uri.parse("file://" + cursor.getString(cursor.getColumnIndex("local_filename")));
                    }
                }
                this.mListener.onDownloadCompleted(uri);
                return;
            }
            int error = 1000;
            if (result.intValue() == 16) {
                cursor = this.mDownloadManager.query(new Query().setFilterById(new long[]{this.mDownloadId}));
                if (cursor != null && cursor.moveToFirst()) {
                    error = cursor.getInt(cursor.getColumnIndex("reason"));
                }
            }
            this.mListener.onDownloadFailed(result.intValue(), error);
        }

        private boolean isSilentDownload() {
            try {
                return Arrays.asList(this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 4096).requestedPermissions).contains(XWalkLibraryLoader.DOWNLOAD_WITHOUT_NOTIFICATION);
            } catch (NameNotFoundException e) {
                return false;
            } catch (NullPointerException e2) {
                return false;
            }
        }
    }

    private static class HttpDownloadTask extends AsyncTask<Void, Integer, Integer> {
        private static final int DOWNLOAD_FAILED = -1;
        private static final int DOWNLOAD_SUCCESS = 0;
        private static final int UPDATE_INTERVAL_MS = 500;
        private static final String XWALK_DOWNLOAD_DIR = "xwalk_download";
        private Context mContext;
        private String mDownloadUrl;
        private File mDownloadedFile;
        private DownloadListener mListener;
        private long mProgressUpdateTime;

        HttpDownloadTask(DownloadListener listener, Context context, String url) {
            this.mListener = listener;
            this.mContext = context;
            this.mDownloadUrl = url;
        }

        protected void onPreExecute() {
            Log.d(XWalkLibraryLoader.TAG, "HttpDownloadTask started, " + this.mDownloadUrl);
            XWalkLibraryLoader.sActiveTask = this;
            String savedFile = XWalkLibraryLoader.DEFAULT_DOWNLOAD_FILE_NAME;
            try {
                String name = new File(new URL(this.mDownloadUrl).getPath()).getName();
                if (!name.isEmpty()) {
                    savedFile = name;
                }
                this.mDownloadedFile = new File(this.mContext.getDir(XWALK_DOWNLOAD_DIR, 0), savedFile);
                this.mListener.onDownloadStarted();
            } catch (MalformedURLException e) {
                Log.e(XWalkLibraryLoader.TAG, "Invalid download URL " + this.mDownloadUrl);
                this.mDownloadUrl = null;
            } catch (NullPointerException e2) {
                Log.e(XWalkLibraryLoader.TAG, "Invalid download URL " + this.mDownloadUrl);
                this.mDownloadUrl = null;
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        protected java.lang.Integer doInBackground(java.lang.Void... r21) {
            /*
            r20 = this;
            r0 = r20;
            r15 = r0.mDownloadUrl;
            if (r15 != 0) goto L_0x000c;
        L_0x0006:
            r15 = -1;
            r15 = java.lang.Integer.valueOf(r15);
        L_0x000b:
            return r15;
        L_0x000c:
            r0 = r20;
            r15 = r0.mDownloadedFile;
            r15 = r15.exists();
            if (r15 == 0) goto L_0x001d;
        L_0x0016:
            r0 = r20;
            r15 = r0.mDownloadedFile;
            r15.delete();
        L_0x001d:
            r7 = 0;
            r8 = 0;
            r2 = 0;
            r14 = new java.net.URL;	 Catch:{ Exception -> 0x013b }
            r0 = r20;
            r15 = r0.mDownloadUrl;	 Catch:{ Exception -> 0x013b }
            r14.<init>(r15);	 Catch:{ Exception -> 0x013b }
            r15 = r14.openConnection();	 Catch:{ Exception -> 0x013b }
            r0 = r15;
            r0 = (java.net.HttpURLConnection) r0;	 Catch:{ Exception -> 0x013b }
            r2 = r0;
            r2.connect();	 Catch:{ Exception -> 0x013b }
            r15 = r2.getResponseCode();	 Catch:{ Exception -> 0x013b }
            r16 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
            r0 = r16;
            if (r15 == r0) goto L_0x007d;
        L_0x003e:
            r15 = "XWalkLib";
            r16 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x013b }
            r16.<init>();	 Catch:{ Exception -> 0x013b }
            r17 = "Server returned HTTP ";
            r16 = r16.append(r17);	 Catch:{ Exception -> 0x013b }
            r17 = r2.getResponseCode();	 Catch:{ Exception -> 0x013b }
            r16 = r16.append(r17);	 Catch:{ Exception -> 0x013b }
            r17 = " ";
            r16 = r16.append(r17);	 Catch:{ Exception -> 0x013b }
            r17 = r2.getResponseMessage();	 Catch:{ Exception -> 0x013b }
            r16 = r16.append(r17);	 Catch:{ Exception -> 0x013b }
            r16 = r16.toString();	 Catch:{ Exception -> 0x013b }
            android.util.Log.e(r15, r16);	 Catch:{ Exception -> 0x013b }
            r15 = -1;
            r15 = java.lang.Integer.valueOf(r15);	 Catch:{ Exception -> 0x013b }
            if (r8 == 0) goto L_0x0072;
        L_0x006f:
            r8.close();	 Catch:{ IOException -> 0x0142 }
        L_0x0072:
            if (r7 == 0) goto L_0x0077;
        L_0x0074:
            r7.close();	 Catch:{ IOException -> 0x0142 }
        L_0x0077:
            if (r2 == 0) goto L_0x000b;
        L_0x0079:
            r2.disconnect();
            goto L_0x000b;
        L_0x007d:
            r6 = r2.getContentLength();	 Catch:{ Exception -> 0x013b }
            r7 = r2.getInputStream();	 Catch:{ Exception -> 0x013b }
            r9 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x013b }
            r0 = r20;
            r15 = r0.mDownloadedFile;	 Catch:{ Exception -> 0x013b }
            r9.<init>(r15);	 Catch:{ Exception -> 0x013b }
            r15 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
            r4 = new byte[r15];	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r12 = 0;
        L_0x0094:
            r3 = r7.read(r4);	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r15 = -1;
            if (r3 == r15) goto L_0x010a;
        L_0x009b:
            r15 = r20.isCancelled();	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            if (r15 == 0) goto L_0x00b7;
        L_0x00a1:
            r15 = -1;
            r15 = java.lang.Integer.valueOf(r15);	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            if (r9 == 0) goto L_0x00ab;
        L_0x00a8:
            r9.close();	 Catch:{ IOException -> 0x013f }
        L_0x00ab:
            if (r7 == 0) goto L_0x00b0;
        L_0x00ad:
            r7.close();	 Catch:{ IOException -> 0x013f }
        L_0x00b0:
            if (r2 == 0) goto L_0x000b;
        L_0x00b2:
            r2.disconnect();
            goto L_0x000b;
        L_0x00b7:
            r0 = (long) r3;
            r16 = r0;
            r12 = r12 + r16;
            r15 = 0;
            r9.write(r4, r15, r3);	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r10 = android.os.SystemClock.uptimeMillis();	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r0 = r20;
            r0 = r0.mProgressUpdateTime;	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r16 = r0;
            r16 = r10 - r16;
            r18 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
            r15 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1));
            if (r15 <= 0) goto L_0x0094;
        L_0x00d2:
            r0 = r20;
            r0.mProgressUpdateTime = r10;	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r15 = 2;
            r15 = new java.lang.Integer[r15];	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r16 = 0;
            r0 = (int) r12;	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r17 = r0;
            r17 = java.lang.Integer.valueOf(r17);	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r15[r16] = r17;	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r16 = 1;
            r17 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r15[r16] = r17;	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            r0 = r20;
            r0.publishProgress(r15);	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            goto L_0x0094;
        L_0x00f2:
            r5 = move-exception;
            r8 = r9;
        L_0x00f4:
            r15 = -1;
            r15 = java.lang.Integer.valueOf(r15);	 Catch:{ all -> 0x0123 }
            if (r8 == 0) goto L_0x00fe;
        L_0x00fb:
            r8.close();	 Catch:{ IOException -> 0x0139 }
        L_0x00fe:
            if (r7 == 0) goto L_0x0103;
        L_0x0100:
            r7.close();	 Catch:{ IOException -> 0x0139 }
        L_0x0103:
            if (r2 == 0) goto L_0x000b;
        L_0x0105:
            r2.disconnect();
            goto L_0x000b;
        L_0x010a:
            r9.flush();	 Catch:{ Exception -> 0x00f2, all -> 0x0136 }
            if (r9 == 0) goto L_0x0112;
        L_0x010f:
            r9.close();	 Catch:{ IOException -> 0x013d }
        L_0x0112:
            if (r7 == 0) goto L_0x0117;
        L_0x0114:
            r7.close();	 Catch:{ IOException -> 0x013d }
        L_0x0117:
            if (r2 == 0) goto L_0x011c;
        L_0x0119:
            r2.disconnect();
        L_0x011c:
            r15 = 0;
            r15 = java.lang.Integer.valueOf(r15);
            goto L_0x000b;
        L_0x0123:
            r15 = move-exception;
        L_0x0124:
            if (r8 == 0) goto L_0x0129;
        L_0x0126:
            r8.close();	 Catch:{ IOException -> 0x0134 }
        L_0x0129:
            if (r7 == 0) goto L_0x012e;
        L_0x012b:
            r7.close();	 Catch:{ IOException -> 0x0134 }
        L_0x012e:
            if (r2 == 0) goto L_0x0133;
        L_0x0130:
            r2.disconnect();
        L_0x0133:
            throw r15;
        L_0x0134:
            r16 = move-exception;
            goto L_0x012e;
        L_0x0136:
            r15 = move-exception;
            r8 = r9;
            goto L_0x0124;
        L_0x0139:
            r16 = move-exception;
            goto L_0x0103;
        L_0x013b:
            r5 = move-exception;
            goto L_0x00f4;
        L_0x013d:
            r15 = move-exception;
            goto L_0x0117;
        L_0x013f:
            r16 = move-exception;
            goto L_0x00b0;
        L_0x0142:
            r16 = move-exception;
            goto L_0x0077;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.xwalk.core.XWalkLibraryLoader.HttpDownloadTask.doInBackground(java.lang.Void[]):java.lang.Integer");
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d(XWalkLibraryLoader.TAG, "HttpDownloadTask updated: " + progress[0] + "/" + progress[1]);
            int percentage = 0;
            if (progress[1].intValue() > 0) {
                percentage = (int) ((((double) progress[0].intValue()) * 100.0d) / ((double) progress[1].intValue()));
            }
            this.mListener.onDownloadUpdated(percentage);
        }

        protected void onCancelled(Integer result) {
            Log.d(XWalkLibraryLoader.TAG, "HttpDownloadTask cancelled");
            XWalkLibraryLoader.sActiveTask = null;
            this.mListener.onDownloadCancelled();
        }

        protected void onPostExecute(Integer result) {
            Log.d(XWalkLibraryLoader.TAG, "HttpDownloadTask finished, " + result);
            XWalkLibraryLoader.sActiveTask = null;
            if (result.intValue() == 0) {
                this.mListener.onDownloadCompleted(Uri.fromFile(this.mDownloadedFile));
            } else {
                this.mListener.onDownloadFailed(-1, 0);
            }
        }
    }

    XWalkLibraryLoader() {
    }

    public static boolean isInitializing() {
        return sActiveTask != null && ((sActiveTask instanceof DecompressTask) || (sActiveTask instanceof ActivateTask));
    }

    public static boolean isDownloading() {
        return sActiveTask != null && ((sActiveTask instanceof DownloadManagerTask) || (sActiveTask instanceof HttpDownloadTask));
    }

    public static boolean isSharedLibrary() {
        return XWalkCoreWrapper.getInstance().isSharedMode();
    }

    public static boolean isLibraryReady() {
        return XWalkCoreWrapper.getInstance() != null;
    }

    public static int getLibraryStatus() {
        return XWalkCoreWrapper.getCoreStatus();
    }

    public static void prepareToInit(Context context) {
        XWalkEnvironment.init(context);
        XWalkCoreWrapper.handlePreInit(context.getClass().getName());
    }

    public static void finishInit(Context context) {
        XWalkCoreWrapper.handlePostInit(context.getClass().getName());
    }

    public static void startDecompress(DecompressListener listener) {
        new DecompressTask(listener).execute(new Void[0]);
    }

    public static boolean cancelDecompress() {
        return sActiveTask != null && (sActiveTask instanceof DecompressTask) && sActiveTask.cancel(true);
    }

    public static void startActivate(ActivateListener listener) {
        new ActivateTask(listener).execute(new Void[0]);
    }

    public static void startDownloadManager(DownloadListener listener, Context context, String url) {
        new DownloadManagerTask(listener, context, url).execute(new Void[0]);
    }

    public static boolean cancelDownloadManager() {
        return sActiveTask != null && (sActiveTask instanceof DownloadManagerTask) && sActiveTask.cancel(true);
    }

    public static void startHttpDownload(DownloadListener listener, Context context, String url) {
        new HttpDownloadTask(listener, context, url).execute(new Void[0]);
    }

    public static boolean cancelHttpDownload() {
        return sActiveTask != null && (sActiveTask instanceof HttpDownloadTask) && sActiveTask.cancel(true);
    }
}
