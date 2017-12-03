package org.xwalk.core.internal;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import org.chromium.media.MediaPlayerBridge.ResourceLoadingFilter;

class XWalkMediaPlayerResourceLoadingFilter extends ResourceLoadingFilter {
    XWalkMediaPlayerResourceLoadingFilter() {
    }

    public boolean shouldOverrideResourceLoading(MediaPlayer mediaPlayer, Context context, Uri uri) {
        String scheme = uri.getScheme();
        if (scheme == null) {
            return false;
        }
        if (scheme.equals(AndroidProtocolHandler.APP_SCHEME)) {
            uri = AndroidProtocolHandler.appUriToFileUri(uri);
        }
        if (!uri.getScheme().equals(AndroidProtocolHandler.FILE_SCHEME)) {
            return false;
        }
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(AndroidProtocolHandler.getAssetPath(uri));
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
