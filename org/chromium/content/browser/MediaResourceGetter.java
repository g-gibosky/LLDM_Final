package org.chromium.content.browser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.chromium.base.Log;
import org.chromium.base.PathUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("content")
class MediaResourceGetter {
    private static final MediaMetadata EMPTY_METADATA = new MediaMetadata(0, 0, 0, false);
    private static final String TAG = "cr_MediaResource";
    private final MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();

    @VisibleForTesting
    static class MediaMetadata {
        private final int mDurationInMilliseconds;
        private final int mHeight;
        private final boolean mSuccess;
        private final int mWidth;

        MediaMetadata(int durationInMilliseconds, int width, int height, boolean success) {
            this.mDurationInMilliseconds = durationInMilliseconds;
            this.mWidth = width;
            this.mHeight = height;
            this.mSuccess = success;
        }

        @CalledByNative("MediaMetadata")
        int getDurationInMilliseconds() {
            return this.mDurationInMilliseconds;
        }

        @CalledByNative("MediaMetadata")
        int getWidth() {
            return this.mWidth;
        }

        @CalledByNative("MediaMetadata")
        int getHeight() {
            return this.mHeight;
        }

        @CalledByNative("MediaMetadata")
        boolean isSuccess() {
            return this.mSuccess;
        }

        public String toString() {
            return "MediaMetadata[durationInMilliseconds=" + this.mDurationInMilliseconds + ", width=" + this.mWidth + ", height=" + this.mHeight + ", success=" + this.mSuccess + "]";
        }

        public int hashCode() {
            return ((((((this.mDurationInMilliseconds + 31) * 31) + this.mHeight) * 31) + (this.mSuccess ? 1231 : 1237)) * 31) + this.mWidth;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MediaMetadata other = (MediaMetadata) obj;
            if (this.mDurationInMilliseconds != other.mDurationInMilliseconds) {
                return false;
            }
            if (this.mHeight != other.mHeight) {
                return false;
            }
            if (this.mSuccess != other.mSuccess) {
                return false;
            }
            if (this.mWidth != other.mWidth) {
                return false;
            }
            return true;
        }
    }

    MediaResourceGetter() {
    }

    @CalledByNative
    private static MediaMetadata extractMediaMetadata(Context context, String url, String cookies, String userAgent) {
        return new MediaResourceGetter().extract(context, url, cookies, userAgent);
    }

    @CalledByNative
    private static MediaMetadata extractMediaMetadataFromFd(int fd, long offset, long length) {
        return new MediaResourceGetter().extract(fd, offset, length);
    }

    @VisibleForTesting
    MediaMetadata extract(int fd, long offset, long length) {
        configure(fd, offset, length);
        return doExtractMetadata();
    }

    @VisibleForTesting
    MediaMetadata extract(Context context, String url, String cookies, String userAgent) {
        if (configure(context, url, cookies, userAgent)) {
            return doExtractMetadata();
        }
        Log.m28e(TAG, "Unable to configure metadata extractor", new Object[0]);
        return EMPTY_METADATA;
    }

    private MediaMetadata doExtractMetadata() {
        try {
            String durationString = extractMetadata(9);
            if (durationString == null) {
                Log.m38w(TAG, "missing duration metadata", new Object[0]);
                return EMPTY_METADATA;
            }
            try {
                int durationMillis = Integer.parseInt(durationString);
                int width = 0;
                int height = 0;
                boolean hasVideo = "yes".equals(extractMetadata(17));
                Log.m20d(TAG, hasVideo ? "resource has video" : "resource doesn't have video");
                if (hasVideo) {
                    String widthString = extractMetadata(18);
                    if (widthString == null) {
                        Log.m38w(TAG, "missing video width metadata", new Object[0]);
                        return EMPTY_METADATA;
                    }
                    try {
                        width = Integer.parseInt(widthString);
                        String heightString = extractMetadata(19);
                        if (heightString == null) {
                            Log.m38w(TAG, "missing video height metadata", new Object[0]);
                            return EMPTY_METADATA;
                        }
                        try {
                            height = Integer.parseInt(heightString);
                        } catch (NumberFormatException e) {
                            Log.m38w(TAG, "non-numeric height: %s", heightString);
                            return EMPTY_METADATA;
                        }
                    } catch (NumberFormatException e2) {
                        Log.m38w(TAG, "non-numeric width: %s", widthString);
                        return EMPTY_METADATA;
                    }
                }
                MediaMetadata result = new MediaMetadata(durationMillis, width, height, true);
                Log.m21d(TAG, "extracted valid metadata: %s", result);
                return result;
            } catch (NumberFormatException e3) {
                Log.m38w(TAG, "non-numeric duration: %s", durationString);
                return EMPTY_METADATA;
            }
        } catch (RuntimeException e4) {
            Log.m28e(TAG, "Unable to extract metadata: %s", e4);
            return EMPTY_METADATA;
        }
    }

    @VisibleForTesting
    boolean configure(Context context, String url, String cookies, String userAgent) {
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            if (scheme == null || scheme.equals(AndroidProtocolHandler.FILE_SCHEME) || scheme.equals(AndroidProtocolHandler.APP_SCHEME)) {
                File file = uriToFile(uri.getPath());
                if (!file.exists()) {
                    Log.m28e(TAG, "File does not exist.", new Object[0]);
                    return false;
                } else if (filePathAcceptable(file, context)) {
                    try {
                        configure(file.getAbsolutePath());
                        return true;
                    } catch (RuntimeException e) {
                        Log.m28e(TAG, "Error configuring data source: %s", e);
                        return false;
                    }
                } else {
                    Log.m28e(TAG, "Refusing to read from unsafe file location.", new Object[0]);
                    return false;
                }
            } else if (scheme.equals("content")) {
                try {
                    configure(context, Uri.parse(uri.toString()));
                    return true;
                } catch (RuntimeException e2) {
                    Log.m28e(TAG, "Error configuring data source: %s", e2);
                    return false;
                }
            } else if (uri.getPath() != null && uri.getPath().endsWith(".m3u8")) {
                return false;
            } else {
                if (isLoopbackAddress(uri.getHost()) || isNetworkReliable(context)) {
                    Map headersMap = new HashMap();
                    if (!TextUtils.isEmpty(cookies)) {
                        headersMap.put("Cookie", cookies);
                    }
                    if (!TextUtils.isEmpty(userAgent)) {
                        headersMap.put("User-Agent", userAgent);
                    }
                    try {
                        configure(url, headersMap);
                        return true;
                    } catch (RuntimeException e22) {
                        Log.m28e(TAG, "Error configuring data source: %s", e22);
                        return false;
                    }
                }
                Log.m38w(TAG, "non-file URI can't be read due to unsuitable network conditions", new Object[0]);
                return false;
            }
        } catch (IllegalArgumentException e3) {
            Log.m28e(TAG, "Cannot parse uri: %s", e3);
            return false;
        }
    }

    @VisibleForTesting
    boolean isNetworkReliable(Context context) {
        if (context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") != 0) {
            Log.m38w(TAG, "permission denied to access network state", new Object[0]);
            return false;
        }
        Integer networkType = getNetworkType(context);
        if (networkType == null) {
            return false;
        }
        switch (networkType.intValue()) {
            case 1:
            case 9:
                Log.m20d(TAG, "ethernet/wifi connection detected");
                return true;
            default:
                Log.m20d(TAG, "no ethernet/wifi connection detected");
                return false;
        }
    }

    private boolean isLoopbackAddress(String host) {
        return host != null && (host.equalsIgnoreCase("localhost") || host.equalsIgnoreCase("localhost.localdomain") || host.equalsIgnoreCase("localhost6") || host.equalsIgnoreCase("localhost6.localdomain6") || host.toLowerCase(Locale.US).endsWith(".localhost") || host.equals("127.0.0.1") || host.equals("[::1]"));
    }

    @VisibleForTesting
    boolean filePathAcceptable(File file, Context context) {
        try {
            String path = file.getCanonicalPath();
            List<String> acceptablePaths = canonicalize(getRawAcceptableDirectories(context));
            acceptablePaths.add(getExternalStorageDirectory());
            Log.m21d(TAG, "canonicalized file path: %s", path);
            for (String acceptablePath : acceptablePaths) {
                if (path.startsWith(acceptablePath)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            Log.m38w(TAG, "canonicalization of file path failed", new Object[0]);
            return false;
        }
    }

    @VisibleForTesting
    File uriToFile(String path) {
        return new File(path);
    }

    @VisibleForTesting
    Integer getNetworkType(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (mConnectivityManager == null) {
            Log.m38w(TAG, "no connectivity manager available", new Object[0]);
            return null;
        }
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info != null) {
            return Integer.valueOf(info.getType());
        }
        Log.m20d(TAG, "no active network");
        return null;
    }

    @SuppressLint({"SdCardPath"})
    private List<String> getRawAcceptableDirectories(Context context) {
        List<String> result = new ArrayList();
        result.add("/mnt/sdcard/");
        result.add("/sdcard/");
        result.add("/data/data/" + context.getPackageName() + "/cache/");
        return result;
    }

    private List<String> canonicalize(List<String> paths) {
        List<String> result = new ArrayList(paths.size());
        try {
            for (String path : paths) {
                result.add(new File(path).getCanonicalPath());
            }
        } catch (IOException e) {
            Log.m38w(TAG, "canonicalization of file path failed", new Object[0]);
        }
        return result;
    }

    @VisibleForTesting
    String getExternalStorageDirectory() {
        return PathUtils.getExternalStorageDirectory();
    }

    @VisibleForTesting
    void configure(int fd, long offset, long length) {
        ParcelFileDescriptor parcelFd = ParcelFileDescriptor.adoptFd(fd);
        try {
            this.mRetriever.setDataSource(parcelFd.getFileDescriptor(), offset, length);
        } finally {
            try {
                parcelFd.close();
            } catch (IOException e) {
                Log.m28e(TAG, "Failed to close file descriptor: %s", e);
            }
        }
    }

    @VisibleForTesting
    void configure(String url, Map<String, String> headers) {
        this.mRetriever.setDataSource(url, headers);
    }

    @VisibleForTesting
    void configure(String path) {
        this.mRetriever.setDataSource(path);
    }

    @VisibleForTesting
    void configure(Context context, Uri uri) {
        this.mRetriever.setDataSource(context, uri);
    }

    @VisibleForTesting
    String extractMetadata(int key) {
        return this.mRetriever.extractMetadata(key);
    }
}
