package org.xwalk.core.internal;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.util.Log;
import android.view.Display;
import java.util.HashMap;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.xwalk.core.internal.extension.api.XWalkDisplayManager;
import org.xwalk.core.internal.extension.api.XWalkDisplayManager.DisplayListener;

@JNINamespace("xwalk")
class XWalkPresentationHost implements DisplayListener {
    static final /* synthetic */ boolean $assertionsDisabled = (!XWalkPresentationHost.class.desiredAssertionStatus());
    private static String TAG = "XWalkPresentationHost";
    private static XWalkPresentationHost sInstance;
    private Context mApplicationContext;
    private XWalkDisplayManager mDisplayManager = XWalkDisplayManager.getInstance(this.mApplicationContext);
    private HashMap<RenderFrameHostId, PresentationSession> mExistingSessions = new HashMap();
    private long mNativePresentationHost;

    @TargetApi(17)
    private final class PresentationScreen extends Presentation {
        private XWalkViewInternal mContentView;
        private Display mDisplay;
        private PresentationSession mSession;

        public PresentationScreen(PresentationSession session, Display display) {
            super(session.context, display);
            this.mSession = session;
            this.mDisplay = display;
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (this.mContentView == null) {
                this.mContentView = new XWalkViewInternal(getContext());
                this.mContentView.setUIClient(new XWalkUIClientInternal(this.mContentView));
            }
            setContentView(this.mContentView);
        }

        protected void onStop() {
            super.onStop();
            XWalkPresentationHost.onPresentationScreenClose(this.mSession);
        }

        public void loadUrl(String url) {
            this.mContentView.loadUrl(url);
        }
    }

    private final class PresentationSession {
        public Context context;
        public PresentationScreen presentationScreen = null;
        public int renderFrameID;
        public int renderProcessID;

        public PresentationSession(Context context, int renderProcessID, int renderFrameID) {
            this.context = context;
            this.renderProcessID = renderProcessID;
            this.renderFrameID = renderFrameID;
        }
    }

    public static class RenderFrameHostId {
        public int renderFrameID;
        public int renderProcessID;

        public RenderFrameHostId(int renderProcessID, int renderFrameID) {
            this.renderProcessID = renderProcessID;
            this.renderFrameID = renderFrameID;
        }

        public int hashCode() {
            return ((this.renderProcessID + 17) * 31) + this.renderFrameID;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof RenderFrameHostId)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            RenderFrameHostId that = (RenderFrameHostId) obj;
            if (this.renderProcessID == that.renderProcessID && this.renderFrameID == that.renderFrameID) {
                return true;
            }
            return false;
        }
    }

    private static native void nativeDestroy(long j);

    private native long nativeInit();

    private native void nativeOnDisplayAdded(long j, int i);

    private native void nativeOnDisplayChanged(long j, int i);

    private native void nativeOnDisplayRemoved(long j, int i);

    private native void nativeOnPresentationClosed(long j, int i, int i2);

    private native void nativeSetupJavaPeer(long j);

    public static XWalkPresentationHost createInstanceOnce(Context context) {
        if (sInstance == null) {
            sInstance = new XWalkPresentationHost(context);
        }
        return sInstance;
    }

    public static XWalkPresentationHost getInstance() {
        return sInstance;
    }

    private PresentationSession createNewSession(RenderFrameHostId id) {
        PresentationSession session = new PresentationSession(this.mApplicationContext, id.renderProcessID, id.renderFrameID);
        if ($assertionsDisabled || this.mExistingSessions.get(id) == null) {
            this.mExistingSessions.put(id, session);
            return session;
        }
        throw new AssertionError();
    }

    private void removeContextActivity(int renderProcessID, int renderFrameID) {
        this.mExistingSessions.remove(new RenderFrameHostId(renderProcessID, renderFrameID));
    }

    private boolean startNewSession(PresentationSession session, int displayId, String url) {
        if (session != null) {
            Display[] presentationDisplays = new Display[0];
            if (VERSION.SDK_INT >= 17) {
                presentationDisplays = this.mDisplayManager.getDisplays(DisplayManagerCompat.DISPLAY_CATEGORY_PRESENTATION);
            }
            if (presentationDisplays.length > 0) {
                Display display = null;
                for (Display query : presentationDisplays) {
                    if (query.getDisplayId() == displayId) {
                        display = query;
                    }
                }
                if (display == null || VERSION.SDK_INT < 17) {
                    Log.e(TAG, "Can't find specified display with id " + displayId);
                } else {
                    session.presentationScreen = new PresentationScreen(session, display);
                    session.presentationScreen.show();
                    session.presentationScreen.loadUrl(url);
                    return true;
                }
            }
        }
        Log.e(TAG, "startNewSession falied!");
        return false;
    }

    private void closeSession(int renderProcessID, int renderFrameID) {
        PresentationSession session = (PresentationSession) this.mExistingSessions.get(new RenderFrameHostId(renderProcessID, renderFrameID));
        if (session != null && VERSION.SDK_INT >= 17) {
            if (session.presentationScreen != null) {
                session.presentationScreen.dismiss();
                session.presentationScreen = null;
                nativeOnPresentationClosed(this.mNativePresentationHost, renderProcessID, renderFrameID);
            }
            removeContextActivity(renderProcessID, renderFrameID);
        }
    }

    private XWalkPresentationHost(Context context) {
        this.mApplicationContext = context.getApplicationContext();
        setNativeObject(nativeInit());
        listenToSystemDisplayChange();
    }

    public void listenToSystemDisplayChange() {
        this.mDisplayManager.registerDisplayListener(this);
    }

    public void stopListenToSystemDisplayChange() {
        this.mDisplayManager.unregisterDisplayListener(this);
    }

    @CalledByNative
    public Display[] getAndroidDisplayInfo() {
        Display[] emptyDisplay = new Display[0];
        if (VERSION.SDK_INT >= 17) {
            return this.mDisplayManager.getDisplays();
        }
        return emptyDisplay;
    }

    @CalledByNative
    public boolean showPresentation(int renderProcessID, int renderFrameID, int displayId, String url) {
        RenderFrameHostId id = new RenderFrameHostId(renderProcessID, renderFrameID);
        PresentationSession session = (PresentationSession) this.mExistingSessions.get(id);
        if (session == null) {
            session = createNewSession(id);
        }
        return startNewSession(session, displayId, url);
    }

    @CalledByNative
    public void closePresentation(int renderProcessID, int renderFrameID) {
        closeSession(renderProcessID, renderFrameID);
    }

    public static void onPresentationScreenClose(PresentationSession attachedSession) {
        PresentationSession querySession = (PresentationSession) getInstance().mExistingSessions.get(new RenderFrameHostId(attachedSession.renderProcessID, attachedSession.renderFrameID));
        if (querySession != null && VERSION.SDK_INT >= 17) {
            if (querySession.presentationScreen != null) {
                querySession.presentationScreen = null;
            }
            int renderProcessID = querySession.renderProcessID;
            int renderFrameID = querySession.renderFrameID;
            getInstance().nativeOnPresentationClosed(getInstance().mNativePresentationHost, renderProcessID, renderFrameID);
            getInstance().removeContextActivity(renderProcessID, renderFrameID);
        }
    }

    private void setNativeObject(long newNativePresentationAPI) {
        if ($assertionsDisabled || this.mNativePresentationHost == 0) {
            this.mNativePresentationHost = newNativePresentationAPI;
            nativeSetupJavaPeer(this.mNativePresentationHost);
            return;
        }
        throw new AssertionError();
    }

    public void onDisplayAdded(int displayId) {
        nativeOnDisplayAdded(this.mNativePresentationHost, displayId);
    }

    public void onDisplayChanged(int displayId) {
        nativeOnDisplayChanged(this.mNativePresentationHost, displayId);
    }

    public void onDisplayRemoved(int displayId) {
        nativeOnDisplayRemoved(this.mNativePresentationHost, displayId);
    }
}
