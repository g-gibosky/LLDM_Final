package org.chromium.ui.resources;

public abstract class ResourceLoader {
    private final ResourceLoaderCallback mCallback;
    private final int mResourceType;

    public interface ResourceLoaderCallback {
        void onResourceLoaded(int i, int i2, Resource resource);
    }

    public abstract void loadResource(int i);

    public abstract void preloadResource(int i);

    public ResourceLoader(int resourceType, ResourceLoaderCallback callback) {
        this.mResourceType = resourceType;
        this.mCallback = callback;
    }

    public int getResourceType() {
        return this.mResourceType;
    }

    protected void notifyLoadFinished(int resId, Resource resource) {
        if (this.mCallback != null) {
            this.mCallback.onResourceLoaded(getResourceType(), resId, resource);
        }
    }
}
