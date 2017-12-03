package org.chromium.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.media.MediaPlayer.TrackInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Base64InputStream;
import android.view.Surface;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("media")
public class MediaPlayerBridge {
    private static final String TAG = "MediaPlayerBridge";
    private static ResourceLoadingFilter sResourceLoadFilter = null;
    private LoadDataUriTask mLoadDataUriTask;
    private long mNativeMediaPlayerBridge;
    private MediaPlayer mPlayer;

    protected static class AllowedOperations {
        private final boolean mCanPause;
        private final boolean mCanSeekBackward;
        private final boolean mCanSeekForward;

        public AllowedOperations(boolean canPause, boolean canSeekForward, boolean canSeekBackward) {
            this.mCanPause = canPause;
            this.mCanSeekForward = canSeekForward;
            this.mCanSeekBackward = canSeekBackward;
        }

        @CalledByNative("AllowedOperations")
        private boolean canPause() {
            return this.mCanPause;
        }

        @CalledByNative("AllowedOperations")
        private boolean canSeekForward() {
            return this.mCanSeekForward;
        }

        @CalledByNative("AllowedOperations")
        private boolean canSeekBackward() {
            return this.mCanSeekBackward;
        }
    }

    private class LoadDataUriTask extends AsyncTask<Void, Void, Boolean> {
        static final /* synthetic */ boolean $assertionsDisabled = (!MediaPlayerBridge.class.desiredAssertionStatus());
        private final Context mContext;
        private final String mData;
        private File mTempFile;

        public LoadDataUriTask(Context context, String data) {
            this.mData = data;
            this.mContext = context;
        }

        protected Boolean doInBackground(Void... params) {
            Boolean valueOf;
            Throwable th;
            FileOutputStream fileOutputStream = null;
            try {
                this.mTempFile = File.createTempFile("decoded", "mediadata");
                FileOutputStream fos = new FileOutputStream(this.mTempFile);
                try {
                    Base64InputStream decoder = new Base64InputStream(new ByteArrayInputStream(this.mData.getBytes()), 0);
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int len = decoder.read(buffer);
                        if (len == -1) {
                            break;
                        }
                        fos.write(buffer, 0, len);
                    }
                    decoder.close();
                    valueOf = Boolean.valueOf(true);
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                    fileOutputStream = fos;
                } catch (IOException e2) {
                    fileOutputStream = fos;
                    try {
                        valueOf = Boolean.valueOf(false);
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e3) {
                            }
                        }
                        return valueOf;
                    } catch (Throwable th2) {
                        th = th2;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e4) {
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    fileOutputStream = fos;
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    throw th;
                }
            } catch (IOException e5) {
                valueOf = Boolean.valueOf(false);
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                return valueOf;
            }
            return valueOf;
        }

        protected void onPostExecute(Boolean result) {
            if (isCancelled()) {
                deleteFile();
                return;
            }
            try {
                MediaPlayerBridge.this.getLocalPlayer().setDataSource(this.mContext, Uri.fromFile(this.mTempFile));
            } catch (IOException e) {
                result = Boolean.valueOf(false);
            }
            deleteFile();
            if ($assertionsDisabled || MediaPlayerBridge.this.mNativeMediaPlayerBridge != 0) {
                MediaPlayerBridge.this.nativeOnDidSetDataUriDataSource(MediaPlayerBridge.this.mNativeMediaPlayerBridge, result.booleanValue());
                return;
            }
            throw new AssertionError();
        }

        private void deleteFile() {
            if (this.mTempFile != null && !this.mTempFile.delete()) {
                Log.m28e(MediaPlayerBridge.TAG, "Failed to delete temporary file: " + this.mTempFile, new Object[0]);
                if (!$assertionsDisabled) {
                    throw new AssertionError();
                }
            }
        }
    }

    public static class ResourceLoadingFilter {
        public boolean shouldOverrideResourceLoading(MediaPlayer mediaPlayer, Context context, Uri uri) {
            return false;
        }
    }

    private native void nativeOnDidSetDataUriDataSource(long j, boolean z);

    public static void setResourceLoadingFilter(ResourceLoadingFilter filter) {
        sResourceLoadFilter = filter;
    }

    @CalledByNative
    private static MediaPlayerBridge create(long nativeMediaPlayerBridge) {
        return new MediaPlayerBridge(nativeMediaPlayerBridge);
    }

    protected MediaPlayerBridge(long nativeMediaPlayerBridge) {
        this.mNativeMediaPlayerBridge = nativeMediaPlayerBridge;
    }

    protected MediaPlayerBridge() {
    }

    @CalledByNative
    protected void destroy() {
        if (this.mLoadDataUriTask != null) {
            this.mLoadDataUriTask.cancel(true);
            this.mLoadDataUriTask = null;
        }
        this.mNativeMediaPlayerBridge = 0;
    }

    protected MediaPlayer getLocalPlayer() {
        if (this.mPlayer == null) {
            this.mPlayer = new MediaPlayer();
        }
        return this.mPlayer;
    }

    @CalledByNative
    protected void setSurface(Surface surface) {
        getLocalPlayer().setSurface(surface);
    }

    @CalledByNative
    protected boolean prepareAsync() {
        try {
            getLocalPlayer().prepareAsync();
            return true;
        } catch (IllegalStateException ise) {
            Log.m28e(TAG, "Unable to prepare MediaPlayer.", ise);
            return false;
        } catch (Exception e) {
            Log.m28e(TAG, "Unable to prepare MediaPlayer.", e);
            return false;
        }
    }

    @CalledByNative
    protected boolean isPlaying() {
        return getLocalPlayer().isPlaying();
    }

    @CalledByNative
    protected boolean hasVideo() {
        return hasTrack(1);
    }

    @CalledByNative
    protected boolean hasAudio() {
        return hasTrack(2);
    }

    private boolean hasTrack(int trackType) {
        try {
            TrackInfo[] trackInfo = getLocalPlayer().getTrackInfo();
            if (trackInfo.length == 0) {
                return true;
            }
            for (TrackInfo info : trackInfo) {
                if (trackType == info.getTrackType() || info.getTrackType() == 0) {
                    return true;
                }
            }
            return false;
        } catch (RuntimeException e) {
            return true;
        }
    }

    @CalledByNative
    protected int getVideoWidth() {
        return getLocalPlayer().getVideoWidth();
    }

    @CalledByNative
    protected int getVideoHeight() {
        return getLocalPlayer().getVideoHeight();
    }

    @CalledByNative
    protected int getCurrentPosition() {
        return getLocalPlayer().getCurrentPosition();
    }

    @CalledByNative
    protected int getDuration() {
        return getLocalPlayer().getDuration();
    }

    @CalledByNative
    protected void release() {
        getLocalPlayer().release();
    }

    @CalledByNative
    protected void setVolume(double volume) {
        getLocalPlayer().setVolume((float) volume, (float) volume);
    }

    @CalledByNative
    protected void start() {
        getLocalPlayer().start();
    }

    @CalledByNative
    protected void pause() {
        getLocalPlayer().pause();
    }

    @CalledByNative
    protected void seekTo(int msec) throws IllegalStateException {
        getLocalPlayer().seekTo(msec);
    }

    @CalledByNative
    protected boolean setDataSource(Context context, String url, String cookies, String userAgent, boolean hideUrlLog) {
        Uri uri = Uri.parse(url);
        HashMap<String, String> headersMap = new HashMap();
        if (hideUrlLog) {
            headersMap.put("x-hide-urls-from-log", "true");
        }
        if (!TextUtils.isEmpty(cookies)) {
            headersMap.put("Cookie", cookies);
        }
        if (!TextUtils.isEmpty(userAgent)) {
            headersMap.put("User-Agent", userAgent);
        }
        if (VERSION.SDK_INT > 19) {
            headersMap.put("allow-cross-domain-redirect", "false");
        }
        try {
            if (sResourceLoadFilter != null && sResourceLoadFilter.shouldOverrideResourceLoading(getLocalPlayer(), context, uri)) {
                return true;
            }
            getLocalPlayer().setDataSource(context, uri, headersMap);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @CalledByNative
    protected boolean setDataSourceFromFd(int fd, long offset, long length) {
        try {
            ParcelFileDescriptor parcelFd = ParcelFileDescriptor.adoptFd(fd);
            getLocalPlayer().setDataSource(parcelFd.getFileDescriptor(), offset, length);
            parcelFd.close();
            return true;
        } catch (IOException e) {
            Log.m28e(TAG, "Failed to set data source from file descriptor: " + e, new Object[0]);
            return false;
        }
    }

    @CalledByNative
    protected boolean setDataUriDataSource(Context context, String url) {
        if (this.mLoadDataUriTask != null) {
            this.mLoadDataUriTask.cancel(true);
            this.mLoadDataUriTask = null;
        }
        if (!url.startsWith("data:")) {
            return false;
        }
        int headerStop = url.indexOf(44);
        if (headerStop == -1) {
            return false;
        }
        String header = url.substring(0, headerStop);
        String data = url.substring(headerStop + 1);
        String[] headerInfo = header.substring(5).split(";");
        if (headerInfo.length != 2 || !"base64".equals(headerInfo[1])) {
            return false;
        }
        this.mLoadDataUriTask = new LoadDataUriTask(context, data);
        this.mLoadDataUriTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        return true;
    }

    protected void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {
        getLocalPlayer().setOnBufferingUpdateListener(listener);
    }

    protected void setOnCompletionListener(OnCompletionListener listener) {
        getLocalPlayer().setOnCompletionListener(listener);
    }

    protected void setOnErrorListener(OnErrorListener listener) {
        getLocalPlayer().setOnErrorListener(listener);
    }

    protected void setOnPreparedListener(OnPreparedListener listener) {
        getLocalPlayer().setOnPreparedListener(listener);
    }

    protected void setOnSeekCompleteListener(OnSeekCompleteListener listener) {
        getLocalPlayer().setOnSeekCompleteListener(listener);
    }

    protected void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
        getLocalPlayer().setOnVideoSizeChangedListener(listener);
    }

    @CalledByNative
    protected AllowedOperations getAllowedOperations() {
        MediaPlayer player = getLocalPlayer();
        boolean canPause = true;
        boolean canSeekForward = true;
        boolean canSeekBackward = true;
        try {
            Method getMetadata = player.getClass().getDeclaredMethod("getMetadata", new Class[]{Boolean.TYPE, Boolean.TYPE});
            getMetadata.setAccessible(true);
            Object data = getMetadata.invoke(player, new Object[]{Boolean.valueOf(false), Boolean.valueOf(false)});
            if (data != null) {
                Class<?> metadataClass = data.getClass();
                Method hasMethod = metadataClass.getDeclaredMethod("has", new Class[]{Integer.TYPE});
                Method getBooleanMethod = metadataClass.getDeclaredMethod("getBoolean", new Class[]{Integer.TYPE});
                int pause = ((Integer) metadataClass.getField("PAUSE_AVAILABLE").get(null)).intValue();
                int seekForward = ((Integer) metadataClass.getField("SEEK_FORWARD_AVAILABLE").get(null)).intValue();
                int seekBackward = ((Integer) metadataClass.getField("SEEK_BACKWARD_AVAILABLE").get(null)).intValue();
                hasMethod.setAccessible(true);
                getBooleanMethod.setAccessible(true);
                if (((Boolean) hasMethod.invoke(data, new Object[]{Integer.valueOf(pause)})).booleanValue()) {
                    if (!((Boolean) getBooleanMethod.invoke(data, new Object[]{Integer.valueOf(pause)})).booleanValue()) {
                        canPause = false;
                        if (((Boolean) hasMethod.invoke(data, new Object[]{Integer.valueOf(seekForward)})).booleanValue()) {
                            if (!((Boolean) getBooleanMethod.invoke(data, new Object[]{Integer.valueOf(seekForward)})).booleanValue()) {
                                canSeekForward = false;
                                if (((Boolean) hasMethod.invoke(data, new Object[]{Integer.valueOf(seekBackward)})).booleanValue()) {
                                    if (!((Boolean) getBooleanMethod.invoke(data, new Object[]{Integer.valueOf(seekBackward)})).booleanValue()) {
                                        canSeekBackward = false;
                                    }
                                }
                                canSeekBackward = true;
                            }
                        }
                        canSeekForward = true;
                        if (((Boolean) hasMethod.invoke(data, new Object[]{Integer.valueOf(seekBackward)})).booleanValue()) {
                            if (((Boolean) getBooleanMethod.invoke(data, new Object[]{Integer.valueOf(seekBackward)})).booleanValue()) {
                                canSeekBackward = false;
                            }
                        }
                        canSeekBackward = true;
                    }
                }
                canPause = true;
                if (((Boolean) hasMethod.invoke(data, new Object[]{Integer.valueOf(seekForward)})).booleanValue()) {
                    if (((Boolean) getBooleanMethod.invoke(data, new Object[]{Integer.valueOf(seekForward)})).booleanValue()) {
                        canSeekForward = false;
                        if (((Boolean) hasMethod.invoke(data, new Object[]{Integer.valueOf(seekBackward)})).booleanValue()) {
                            if (((Boolean) getBooleanMethod.invoke(data, new Object[]{Integer.valueOf(seekBackward)})).booleanValue()) {
                                canSeekBackward = false;
                            }
                        }
                        canSeekBackward = true;
                    }
                }
                canSeekForward = true;
                if (((Boolean) hasMethod.invoke(data, new Object[]{Integer.valueOf(seekBackward)})).booleanValue()) {
                    if (((Boolean) getBooleanMethod.invoke(data, new Object[]{Integer.valueOf(seekBackward)})).booleanValue()) {
                        canSeekBackward = false;
                    }
                }
                canSeekBackward = true;
            }
        } catch (NoSuchMethodException e) {
            Log.m28e(TAG, "Cannot find getMetadata() method: " + e, new Object[0]);
        } catch (InvocationTargetException e2) {
            Log.m28e(TAG, "Cannot invoke MediaPlayer.getMetadata() method: " + e2, new Object[0]);
        } catch (IllegalAccessException e3) {
            Log.m28e(TAG, "Cannot access metadata: " + e3, new Object[0]);
        } catch (NoSuchFieldException e4) {
            Log.m28e(TAG, "Cannot find matching fields in Metadata class: " + e4, new Object[0]);
        }
        return new AllowedOperations(canPause, canSeekForward, canSeekBackward);
    }
}
