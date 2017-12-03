package org.chromium.ui.resources;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.SparseArray;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.MainDex;
import org.chromium.ui.base.WindowAndroid;
import org.chromium.ui.gfx.DeviceDisplayInfo;
import org.chromium.ui.resources.ResourceLoader.ResourceLoaderCallback;
import org.chromium.ui.resources.dynamics.DynamicResourceLoader;
import org.chromium.ui.resources.sprites.CrushedSpriteResource;
import org.chromium.ui.resources.sprites.CrushedSpriteResourceLoader;
import org.chromium.ui.resources.statics.StaticResourceLoader;
import org.chromium.ui.resources.system.SystemResourceLoader;

@MainDex
@JNINamespace("ui")
public class ResourceManager implements ResourceLoaderCallback {
    static final /* synthetic */ boolean $assertionsDisabled = (!ResourceManager.class.desiredAssertionStatus());
    private final CrushedSpriteResourceLoader mCrushedSpriteResourceLoader;
    private final SparseArray<SparseArray<LayoutResource>> mLoadedResources = new SparseArray();
    private long mNativeResourceManagerPtr;
    private final float mPxToDp;
    private final SparseArray<ResourceLoader> mResourceLoaders = new SparseArray();

    private native void nativeOnCrushedSpriteResourceReady(long j, int i, Bitmap bitmap, int[][] iArr, int i2, int i3, float f, float f2);

    private native void nativeOnCrushedSpriteResourceReloaded(long j, int i, Bitmap bitmap);

    private native void nativeOnResourceReady(long j, int i, int i2, Bitmap bitmap, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10);

    private ResourceManager(Resources resources, int minScreenSideLength, long staticResourceManagerPtr) {
        this.mPxToDp = 1.0f / resources.getDisplayMetrics().density;
        registerResourceLoader(new StaticResourceLoader(0, this, resources));
        registerResourceLoader(new DynamicResourceLoader(1, this));
        registerResourceLoader(new DynamicResourceLoader(2, this));
        registerResourceLoader(new SystemResourceLoader(3, this, minScreenSideLength));
        this.mCrushedSpriteResourceLoader = new CrushedSpriteResourceLoader(this, resources);
        this.mNativeResourceManagerPtr = staticResourceManagerPtr;
    }

    @CalledByNative
    private static ResourceManager create(WindowAndroid windowAndroid, long staticResourceManagerPtr) {
        Context context = (Context) windowAndroid.getContext().get();
        if (context == null) {
            throw new IllegalStateException("Context should not be null during initialization.");
        }
        DeviceDisplayInfo displayInfo = DeviceDisplayInfo.create(context);
        return new ResourceManager(context.getResources(), Math.min(displayInfo.getPhysicalDisplayWidth() != 0 ? displayInfo.getPhysicalDisplayWidth() : displayInfo.getDisplayWidth(), displayInfo.getPhysicalDisplayHeight() != 0 ? displayInfo.getPhysicalDisplayHeight() : displayInfo.getDisplayHeight()), staticResourceManagerPtr);
    }

    public DynamicResourceLoader getDynamicResourceLoader() {
        return (DynamicResourceLoader) this.mResourceLoaders.get(1);
    }

    public DynamicResourceLoader getBitmapDynamicResourceLoader() {
        return (DynamicResourceLoader) this.mResourceLoaders.get(2);
    }

    public void preloadResources(int type, int[] syncIds, int[] asyncIds) {
        ResourceLoader loader = (ResourceLoader) this.mResourceLoaders.get(type);
        if (asyncIds != null) {
            for (int valueOf : asyncIds) {
                loader.preloadResource(Integer.valueOf(valueOf).intValue());
            }
        }
        if (syncIds != null) {
            for (int valueOf2 : syncIds) {
                loader.loadResource(Integer.valueOf(valueOf2).intValue());
            }
        }
    }

    public LayoutResource getResource(int resType, int resId) {
        SparseArray<LayoutResource> bucket = (SparseArray) this.mLoadedResources.get(resType);
        return bucket != null ? (LayoutResource) bucket.get(resId) : null;
    }

    public void onResourceLoaded(int resType, int resId, Resource resource) {
        if (resource != null) {
            if (resType != 4) {
                saveMetadataForLoadedResource(resType, resId, resource);
            }
            if (this.mNativeResourceManagerPtr == 0) {
                return;
            }
            if (resType != 4) {
                Rect padding = resource.getPadding();
                Rect aperture = resource.getAperture();
                nativeOnResourceReady(this.mNativeResourceManagerPtr, resType, resId, resource.getBitmap(), padding.left, padding.top, padding.right, padding.bottom, aperture.left, aperture.top, aperture.right, aperture.bottom);
            } else if (resource.getBitmap() != null) {
                CrushedSpriteResource crushedResource = (CrushedSpriteResource) resource;
                nativeOnCrushedSpriteResourceReady(this.mNativeResourceManagerPtr, resId, crushedResource.getBitmap(), crushedResource.getFrameRectangles(), crushedResource.getUnscaledSpriteWidth(), crushedResource.getUnscaledSpriteHeight(), crushedResource.getScaledSpriteWidth(), crushedResource.getScaledSpriteHeight());
            }
        }
    }

    private void saveMetadataForLoadedResource(int resType, int resId, Resource resource) {
        SparseArray<LayoutResource> bucket = (SparseArray) this.mLoadedResources.get(resType);
        if (bucket == null) {
            bucket = new SparseArray();
            this.mLoadedResources.put(resType, bucket);
        }
        bucket.put(resId, new LayoutResource(this.mPxToDp, resource));
    }

    @CalledByNative
    private void destroy() {
        if ($assertionsDisabled || this.mNativeResourceManagerPtr != 0) {
            this.mNativeResourceManagerPtr = 0;
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private void resourceRequested(int resType, int resId) {
        ResourceLoader loader = (ResourceLoader) this.mResourceLoaders.get(resType);
        if (loader != null) {
            loader.loadResource(resId);
        }
    }

    @CalledByNative
    private void preloadResource(int resType, int resId) {
        ResourceLoader loader = (ResourceLoader) this.mResourceLoaders.get(resType);
        if (loader != null) {
            loader.preloadResource(resId);
        }
    }

    @CalledByNative
    private void crushedSpriteResourceRequested(int bitmapResId, int metatadataResId, boolean reloading) {
        if (reloading) {
            Bitmap bitmap = this.mCrushedSpriteResourceLoader.reloadResource(bitmapResId);
            if (bitmap != null) {
                nativeOnCrushedSpriteResourceReloaded(this.mNativeResourceManagerPtr, bitmapResId, bitmap);
                return;
            }
            return;
        }
        this.mCrushedSpriteResourceLoader.loadResource(bitmapResId, metatadataResId);
    }

    @CalledByNative
    private long getNativePtr() {
        return this.mNativeResourceManagerPtr;
    }

    private void registerResourceLoader(ResourceLoader loader) {
        this.mResourceLoaders.put(loader.getResourceType(), loader);
    }
}
