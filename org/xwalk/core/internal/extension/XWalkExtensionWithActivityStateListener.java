package org.xwalk.core.internal.extension;

import android.app.Activity;
import java.lang.ref.WeakReference;
import org.chromium.base.ApplicationStatus;
import org.chromium.base.ApplicationStatus.ActivityStateListener;
import org.xwalk.core.internal.XWalkExtensionInternal;

public abstract class XWalkExtensionWithActivityStateListener extends XWalkExtensionInternal {
    private XWalkActivityStateListener mActivityStateListener;

    private class XWalkActivityStateListener implements ActivityStateListener {
        WeakReference<XWalkExtensionWithActivityStateListener> mExtensionRef;

        XWalkActivityStateListener(XWalkExtensionWithActivityStateListener extension) {
            this.mExtensionRef = new WeakReference(extension);
        }

        public void onActivityStateChange(Activity activity, int newState) {
            XWalkExtensionWithActivityStateListener extension = (XWalkExtensionWithActivityStateListener) this.mExtensionRef.get();
            if (extension != null) {
                extension.onActivityStateChange(activity, newState);
            }
        }
    }

    public abstract void onActivityStateChange(Activity activity, int i);

    private void initActivityStateListener(Activity activity) {
        this.mActivityStateListener = new XWalkActivityStateListener(this);
        ApplicationStatus.registerStateListenerForActivity(this.mActivityStateListener, activity);
    }

    public XWalkExtensionWithActivityStateListener(String name, String jsApi, Activity activity) {
        super(name, jsApi);
        initActivityStateListener(activity);
    }

    public XWalkExtensionWithActivityStateListener(String name, String jsApi, String[] entryPoints, Activity activity) {
        super(name, jsApi, entryPoints);
        initActivityStateListener(activity);
    }
}
