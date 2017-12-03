package org.chromium.ui.resources.async;

import android.os.AsyncTask;
import android.util.SparseArray;
import java.util.concurrent.ExecutionException;
import org.chromium.base.TraceEvent;
import org.chromium.ui.resources.Resource;
import org.chromium.ui.resources.ResourceLoader;
import org.chromium.ui.resources.ResourceLoader.ResourceLoaderCallback;

public class AsyncPreloadResourceLoader extends ResourceLoader {
    private final ResourceCreator mCreator;
    private final SparseArray<AsyncLoadTask> mOutstandingLoads = new SparseArray();

    private class AsyncLoadTask extends AsyncTask<Void, Void, Resource> {
        private final int mResourceId;

        public AsyncLoadTask(int resourceId) {
            this.mResourceId = resourceId;
        }

        protected Resource doInBackground(Void... params) {
            return AsyncPreloadResourceLoader.this.createResource(this.mResourceId);
        }

        protected void onPostExecute(Resource resource) {
            if (AsyncPreloadResourceLoader.this.mOutstandingLoads.get(this.mResourceId) != null) {
                AsyncPreloadResourceLoader.this.registerResource(resource, this.mResourceId);
            }
        }
    }

    public interface ResourceCreator {
        Resource create(int i);
    }

    public AsyncPreloadResourceLoader(int resourceType, ResourceLoaderCallback callback, ResourceCreator creator) {
        super(resourceType, callback);
        this.mCreator = creator;
    }

    public void loadResource(int resId) {
        AsyncLoadTask task = (AsyncLoadTask) this.mOutstandingLoads.get(resId);
        if (task == null || task.cancel(false)) {
            registerResource(createResource(resId), resId);
            return;
        }
        try {
            registerResource((Resource) task.get(), resId);
        } catch (InterruptedException e) {
            notifyLoadFinished(resId, null);
        } catch (ExecutionException e2) {
            notifyLoadFinished(resId, null);
        }
    }

    public void preloadResource(int resId) {
        if (this.mOutstandingLoads.get(resId) == null) {
            AsyncLoadTask task = new AsyncLoadTask(resId);
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, (Void[]) null);
            this.mOutstandingLoads.put(resId, task);
        }
    }

    private Resource createResource(int resId) {
        try {
            TraceEvent.begin("AsyncPreloadResourceLoader.createResource");
            Resource create = this.mCreator.create(resId);
            return create;
        } finally {
            TraceEvent.end("AsyncPreloadResourceLoader.createResource");
        }
    }

    private void registerResource(Resource resource, int resourceId) {
        notifyLoadFinished(resourceId, resource);
        if (resource != null) {
            resource.getBitmap().recycle();
        }
        this.mOutstandingLoads.remove(resourceId);
    }
}
