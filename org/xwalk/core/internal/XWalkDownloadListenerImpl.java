package org.xwalk.core.internal;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.chromium.content.common.ContentSwitches;

class XWalkDownloadListenerImpl extends XWalkDownloadListenerInternal {
    private Context mContext;
    private XWalkCookieManagerInternal mCookieManager = new XWalkCookieManagerInternal();

    private class FileTransfer extends AsyncTask<Void, Void, String> {
        String fileName;
        String url;

        public FileTransfer(String url, String fileName) {
            this.url = url;
            this.fileName = fileName;
        }

        protected String doInBackground(Void... params) {
            IOException e;
            Throwable th;
            OutputStream dstStream = null;
            InputStream srcStream = null;
            File dst = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), this.fileName);
            if (dst.exists()) {
                return "Existed";
            }
            try {
                OutputStream dstStream2 = new FileOutputStream(dst);
                try {
                    srcStream = AndroidProtocolHandler.open(XWalkDownloadListenerImpl.this.mContext, this.url);
                    if (!(dstStream2 == null || srcStream == null)) {
                        streamTransfer(srcStream, dstStream2);
                    }
                    if (srcStream != null) {
                        try {
                            srcStream.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            dstStream = dstStream2;
                            return "Failed";
                        }
                    }
                    if (dstStream2 != null) {
                        dstStream2.close();
                    }
                    dstStream = dstStream2;
                } catch (IOException e3) {
                    e2 = e3;
                    dstStream = dstStream2;
                    try {
                        e2.printStackTrace();
                        if (srcStream != null) {
                            try {
                                srcStream.close();
                            } catch (IOException e22) {
                                e22.printStackTrace();
                                return "Failed";
                            }
                        }
                        if (dstStream != null) {
                            dstStream.close();
                        }
                        return "Finished";
                    } catch (Throwable th2) {
                        th = th2;
                        if (srcStream != null) {
                            try {
                                srcStream.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                                return "Failed";
                            }
                        }
                        if (dstStream != null) {
                            dstStream.close();
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    dstStream = dstStream2;
                    if (srcStream != null) {
                        srcStream.close();
                    }
                    if (dstStream != null) {
                        dstStream.close();
                    }
                    throw th;
                }
            } catch (IOException e4) {
                e222 = e4;
                e222.printStackTrace();
                if (srcStream != null) {
                    srcStream.close();
                }
                if (dstStream != null) {
                    dstStream.close();
                }
                return "Finished";
            }
            return "Finished";
        }

        protected void onPostExecute(String result) {
            if (result.equals("Failed")) {
                XWalkDownloadListenerImpl.this.popupMessages(XWalkDownloadListenerImpl.this.mContext.getString(C0332R.string.download_failed_toast));
            } else if (result.equals("Existed")) {
                XWalkDownloadListenerImpl.this.popupMessages(XWalkDownloadListenerImpl.this.mContext.getString(C0332R.string.download_already_exists_toast));
            } else if (result.equals("Finished")) {
                XWalkDownloadListenerImpl.this.popupMessages(XWalkDownloadListenerImpl.this.mContext.getString(C0332R.string.download_finished_toast));
            }
        }

        private void streamTransfer(InputStream src, OutputStream dst) throws IOException {
            byte[] buf = new byte[1024];
            while (true) {
                int length = src.read(buf);
                if (length > 0) {
                    dst.write(buf, 0, length);
                } else {
                    return;
                }
            }
        }
    }

    public XWalkDownloadListenerImpl(Context context) {
        super(context);
        this.mContext = context;
    }

    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        String fileName = getFileName(url, contentDisposition, mimetype);
        if (checkWriteExternalPermission()) {
            Uri src = Uri.parse(url);
            if (src.getScheme().equals("http") || src.getScheme().equals("https")) {
                Request request = new Request(Uri.parse(url));
                request.addRequestHeader("Cookie", this.mCookieManager.getCookie(url));
                request.addRequestHeader("User-Agent", userAgent);
                request.setNotificationVisibility(1);
                request.setTitle(fileName);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                getDownloadManager().enqueue(request);
                popupMessages(this.mContext.getString(C0332R.string.download_start_toast) + fileName);
                return;
            }
            new FileTransfer(url, fileName).execute(new Void[0]);
        }
    }

    private String getFileName(String url, String contentDisposition, String mimetype) {
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        int extensionIndex = fileName.lastIndexOf(".");
        String extension = null;
        if (extensionIndex > 1 && extensionIndex < fileName.length()) {
            extension = fileName.substring(extensionIndex + 1);
        }
        if (extension != null) {
            return fileName;
        }
        extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimetype);
        if (extension != null) {
            return fileName + "." + extension;
        }
        return fileName;
    }

    private DownloadManager getDownloadManager() {
        return (DownloadManager) this.mContext.getSystemService(ContentSwitches.SWITCH_DOWNLOAD_PROCESS);
    }

    private boolean checkWriteExternalPermission() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        popupMessages(this.mContext.getString(C0332R.string.download_no_permission_toast));
        return false;
    }

    private void popupMessages(String message) {
        Toast.makeText(this.mContext, message, 0).show();
    }
}
