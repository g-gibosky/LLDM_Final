package org.chromium.ui.resources.sprites;

import android.content.res.Resources;
import android.graphics.Bitmap;
import org.chromium.ui.resources.ResourceLoader.ResourceLoaderCallback;

public class CrushedSpriteResourceLoader {
    private final ResourceLoaderCallback mCallback;
    private final Resources mResources;

    public CrushedSpriteResourceLoader(ResourceLoaderCallback callback, Resources resources) {
        this.mCallback = callback;
        this.mResources = resources;
    }

    public void loadResource(int bitmapResId, int metadataResId) {
        this.mCallback.onResourceLoaded(4, bitmapResId, new CrushedSpriteResource(bitmapResId, metadataResId, this.mResources));
    }

    public Bitmap reloadResource(int bitmapResId) {
        return CrushedSpriteResource.loadBitmap(bitmapResId, this.mResources);
    }
}
