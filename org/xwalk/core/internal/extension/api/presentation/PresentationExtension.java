package org.xwalk.core.internal.extension.api.presentation;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import org.chromium.base.ThreadUtils;
import org.xwalk.core.internal.extension.XWalkExtensionWithActivityStateListener;
import org.xwalk.core.internal.extension.api.XWalkDisplayManager;
import org.xwalk.core.internal.extension.api.XWalkDisplayManager.DisplayListener;
import org.xwalk.core.internal.extension.api.presentation.PresentationView.PresentationListener;
import org.xwalk.core.internal.extension.api.presentation.XWalkPresentationContent.PresentationDelegate;

public class PresentationExtension extends XWalkExtensionWithActivityStateListener {
    private static final String CMD_AVAILABILITY_CHANGE = "AvailabilityChange";
    private static final String CMD_DEFAULT_SESSION_STARTED = "DefaultSessionStarted";
    private static final String CMD_GET_AVAILABILITY = "GetAvailability";
    private static final String CMD_SEND_MESSAGE_TO_HOST_DISPLAY = "SendMessageToHostDisplay";
    private static final String CMD_SEND_MESSAGE_TO_REMOTE_DISPLAY = "SendMessageToRemoteDisplay";
    private static final String CMD_SESSION_MESSAGE_TO_HOST_RECEIVED = "SessionMessageToHostReceived";
    private static final String CMD_SESSION_MESSAGE_TO_REMOTE_RECEIVED = "SessionMessageToRemoteReceived";
    private static final String CMD_SESSION_START_FAILED = "SessionStartFailed";
    private static final String CMD_SESSION_START_SUCCEEDED = "SessionStartSucceeded";
    private static final String CMD_START_SESSION = "StartSession";
    private static final String ERROR_INVALID_ACCESS = "InvalidAccessError";
    private static final String ERROR_INVALID_PARAMETER = "InvalidParameterError";
    private static final String ERROR_INVALID_STATE = "InvalidStateError";
    private static final String ERROR_NOT_FOUND = "NotFoundError";
    private static final String ERROR_NOT_SUPPORTED = "NotSupportedError";
    public static final String JS_API_PATH = "jsapi/presentation_api.js";
    private static final String NAME = "navigator.presentation";
    private static final String TAG = "PresentationExtension";
    private static final String TAG_BASE_URL = "baseUrl";
    private static final String TAG_CMD = "cmd";
    private static final String TAG_DATA = "data";
    private static final String TAG_PRESENTATION_ID = "presentationId";
    private static final String TAG_REQUEST_ID = "requestId";
    private static final String TAG_URL = "url";
    private WeakReference<Activity> mActivity;
    private int mAvailableDisplayCount = 0;
    private Context mContext;
    private final DisplayListener mDisplayListener = new C04801();
    private XWalkDisplayManager mDisplayManager;
    private XWalkPresentationContent mPresentationContent;
    private PresentationDelegate mPresentationDelegate;
    private PresentationView mPresentationView;

    class C04801 implements DisplayListener {
        C04801() {
        }

        public void onDisplayAdded(int displayId) {
            PresentationExtension.access$004(PresentationExtension.this);
            if (PresentationExtension.this.mAvailableDisplayCount == 1) {
                PresentationExtension.this.notifyAvailabilityChanged(true);
            }
        }

        public void onDisplayRemoved(int displayId) {
            PresentationExtension.access$006(PresentationExtension.this);
            if (PresentationExtension.this.mAvailableDisplayCount == 0) {
                PresentationExtension.this.notifyAvailabilityChanged(false);
                PresentationExtension.this.closePresentationContent();
            }
        }

        public void onDisplayChanged(int displayId) {
        }
    }

    class C04823 implements PresentationListener {
        C04823() {
        }

        public void onDismiss(PresentationView view) {
            if (view == PresentationExtension.this.mPresentationView) {
                if (PresentationExtension.this.mPresentationContent != null) {
                    PresentationExtension.this.mPresentationContent.onPause();
                }
                PresentationExtension.this.mPresentationView = null;
            }
        }

        public void onShow(PresentationView view) {
            if (view == PresentationExtension.this.mPresentationView && PresentationExtension.this.mPresentationContent != null) {
                PresentationExtension.this.mPresentationContent.onResume();
            }
        }
    }

    static /* synthetic */ int access$004(PresentationExtension x0) {
        int i = x0.mAvailableDisplayCount + 1;
        x0.mAvailableDisplayCount = i;
        return i;
    }

    static /* synthetic */ int access$006(PresentationExtension x0) {
        int i = x0.mAvailableDisplayCount - 1;
        x0.mAvailableDisplayCount = i;
        return i;
    }

    public PresentationExtension(String jsApi, Activity activity) {
        super(NAME, jsApi, activity);
        this.mContext = activity.getApplicationContext();
        this.mActivity = new WeakReference(activity);
        this.mDisplayManager = XWalkDisplayManager.getInstance(activity.getApplicationContext());
        this.mAvailableDisplayCount = this.mDisplayManager.getPresentationDisplays().length;
    }

    private Display getPreferredDisplay() {
        Display[] displays = this.mDisplayManager.getPresentationDisplays();
        if (displays.length > 0) {
            return displays[0];
        }
        return null;
    }

    private void notifyAvailabilityChanged(boolean isAvailable) {
        StringWriter contents = new StringWriter();
        JsonWriter writer = new JsonWriter(contents);
        try {
            writer.beginObject();
            writer.name(TAG_CMD).value(CMD_AVAILABILITY_CHANGE);
            writer.name(TAG_DATA).value(isAvailable);
            writer.endObject();
            writer.close();
            broadcastMessage(contents.toString());
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.toString());
        }
    }

    private void notifyStartSessionSucceed(int instanceId, int requestId, int presentationId) {
        StringWriter contents = new StringWriter();
        JsonWriter writer = new JsonWriter(contents);
        try {
            writer.beginObject();
            writer.name(TAG_CMD).value(CMD_SESSION_START_SUCCEEDED);
            writer.name(TAG_REQUEST_ID).value((long) requestId);
            writer.name(TAG_DATA).value((long) presentationId);
            writer.endObject();
            writer.close();
            postMessage(instanceId, contents.toString());
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.toString());
        }
    }

    private void notifyStartSessionFail(int instanceId, int requestId, String errorMessage) {
        StringWriter contents = new StringWriter();
        JsonWriter writer = new JsonWriter(contents);
        try {
            writer.beginObject();
            writer.name(TAG_CMD).value(CMD_SESSION_START_FAILED);
            writer.name(TAG_REQUEST_ID).value((long) requestId);
            writer.name(TAG_DATA).value(errorMessage);
            writer.endObject();
            writer.close();
            postMessage(instanceId, contents.toString());
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.toString());
        }
    }

    public void onMessage(int instanceId, String message) {
        JsonReader reader = new JsonReader(new StringReader(message));
        int requestId = -1;
        String cmd = null;
        String url = null;
        String baseUrl = null;
        int presentationId = -1;
        String data = null;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals(TAG_CMD)) {
                    cmd = reader.nextString();
                } else if (name.equals(TAG_REQUEST_ID)) {
                    requestId = reader.nextInt();
                } else if (name.equals(TAG_URL)) {
                    url = reader.nextString();
                } else if (name.equals(TAG_BASE_URL)) {
                    baseUrl = reader.nextString();
                } else if (name.equals(TAG_PRESENTATION_ID)) {
                    presentationId = reader.nextInt();
                } else if (name.equals(TAG_DATA)) {
                    data = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            reader.close();
            if (cmd == null) {
                return;
            }
            if (cmd.equals(CMD_START_SESSION) && requestId >= 0) {
                handleStartSession(instanceId, requestId, url, baseUrl);
            } else if (cmd.equals(CMD_SEND_MESSAGE_TO_REMOTE_DISPLAY)) {
                handleSendMessageToRemoteDisplay(instanceId, presentationId, data);
            } else if (cmd.equals(CMD_SEND_MESSAGE_TO_HOST_DISPLAY)) {
                handleSendMessageToHostDisplay(instanceId, presentationId, data);
            }
        } catch (IOException e) {
            Log.d(TAG, "Error: " + e);
        }
    }

    private void handleStartSession(int instanceId, int requestId, String url, String baseUrl) {
        if (VERSION.SDK_INT < 17) {
            notifyStartSessionFail(instanceId, requestId, ERROR_NOT_SUPPORTED);
        } else if (this.mAvailableDisplayCount == 0) {
            Log.d(TAG, "No available presentation display is found.");
            notifyStartSessionFail(instanceId, requestId, ERROR_NOT_FOUND);
        } else {
            final int i = instanceId;
            final int i2 = requestId;
            final String str = url;
            final String str2 = baseUrl;
            ThreadUtils.runOnUiThread(new Runnable() {

                class C04811 implements PresentationDelegate {
                    C04811() {
                    }

                    public void onContentLoaded(XWalkPresentationContent content) {
                        PresentationExtension.this.notifyStartSessionSucceed(i, i2, content.getPresentationId());
                    }

                    public void onContentClosed(XWalkPresentationContent content) {
                        if (content == PresentationExtension.this.mPresentationContent) {
                            PresentationExtension.this.closePresentationContent();
                            if (PresentationExtension.this.mPresentationView != null) {
                                PresentationExtension.this.mPresentationView.cancel();
                            }
                        }
                    }
                }

                public void run() {
                    Display preferredDisplay = PresentationExtension.this.getPreferredDisplay();
                    if (preferredDisplay == null) {
                        PresentationExtension.this.notifyStartSessionFail(i, i2, PresentationExtension.ERROR_NOT_FOUND);
                    } else if (PresentationExtension.this.mPresentationContent != null) {
                        PresentationExtension.this.notifyStartSessionFail(i, i2, PresentationExtension.ERROR_INVALID_ACCESS);
                    } else {
                        String targetUrl = str;
                        try {
                            URI targetUri = new URI(str);
                            try {
                                if (!targetUri.isAbsolute()) {
                                    targetUrl = new URI(str2).resolve(targetUri).toString();
                                }
                                PresentationExtension.this.mPresentationContent = new XWalkPresentationContent(PresentationExtension.this.mContext, PresentationExtension.this.mActivity, new C04811());
                                PresentationExtension.this.mPresentationContent.load(targetUrl);
                                PresentationExtension.this.updatePresentationView(preferredDisplay);
                            } catch (URISyntaxException e) {
                                URI uri = targetUri;
                                Log.e(PresentationExtension.TAG, "Invalid url passed to requestShow");
                                PresentationExtension.this.notifyStartSessionFail(i, i2, PresentationExtension.ERROR_INVALID_PARAMETER);
                            }
                        } catch (URISyntaxException e2) {
                            Log.e(PresentationExtension.TAG, "Invalid url passed to requestShow");
                            PresentationExtension.this.notifyStartSessionFail(i, i2, PresentationExtension.ERROR_INVALID_PARAMETER);
                        }
                    }
                }
            });
        }
    }

    private void handleSendMessageToRemoteDisplay(int instanceId, int presentationId, String data) {
        notifySessionMessageReceived(false, presentationId, data);
    }

    private void handleSendMessageToHostDisplay(int instanceId, int presentationId, String data) {
        notifySessionMessageReceived(true, presentationId, data);
    }

    private void notifySessionMessageReceived(boolean isToHost, int presentationId, String data) {
        StringWriter contents = new StringWriter();
        JsonWriter writer = new JsonWriter(contents);
        try {
            writer.beginObject();
            if (isToHost) {
                writer.name(TAG_CMD).value(CMD_SESSION_MESSAGE_TO_HOST_RECEIVED);
            } else {
                writer.name(TAG_CMD).value(CMD_SESSION_MESSAGE_TO_REMOTE_RECEIVED);
            }
            writer.name(TAG_PRESENTATION_ID).value((long) presentationId);
            writer.name(TAG_DATA).value(data);
            writer.endObject();
            writer.close();
            broadcastMessage(contents.toString());
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.toString());
        }
    }

    public String onSyncMessage(int instanceId, String message) {
        if (message.equals(CMD_GET_AVAILABILITY)) {
            return this.mAvailableDisplayCount != 0 ? "true" : "false";
        } else {
            Log.e(TAG, "Unexpected sync message received: " + message);
            return "";
        }
    }

    public void onResume() {
        Display[] displays = this.mDisplayManager.getPresentationDisplays();
        if (displays.length == 0 && this.mAvailableDisplayCount > 0) {
            notifyAvailabilityChanged(false);
            this.mAvailableDisplayCount = 0;
            closePresentationContent();
        }
        if (displays.length > 0 && this.mAvailableDisplayCount == 0) {
            notifyAvailabilityChanged(true);
            this.mAvailableDisplayCount = displays.length;
        }
        if (displays.length > 0 && this.mAvailableDisplayCount > 0) {
            this.mAvailableDisplayCount = displays.length;
        }
        if (this.mPresentationContent != null) {
            this.mPresentationContent.onResume();
        }
        updatePresentationView(getPreferredDisplay());
        this.mDisplayManager.registerDisplayListener(this.mDisplayListener);
    }

    private void updatePresentationView(Display preferredDisplay) {
        Activity activity = (Activity) this.mActivity.get();
        if (activity != null && VERSION.SDK_INT >= 17 && preferredDisplay != null) {
            if (this.mPresentationView != null || this.mPresentationContent != null) {
                if (!(this.mPresentationView == null || this.mPresentationView.getDisplay() == preferredDisplay)) {
                    dismissPresentationView();
                }
                if (this.mPresentationView == null && this.mPresentationContent != null) {
                    ViewGroup parent = (ViewGroup) this.mPresentationContent.getContentView().getParent();
                    if (parent != null) {
                        parent.removeView(this.mPresentationContent.getContentView());
                    }
                    this.mPresentationView = PresentationView.createInstance(activity, preferredDisplay);
                    this.mPresentationView.setContentView(this.mPresentationContent.getContentView());
                    this.mPresentationView.setPresentationListener(new C04823());
                }
                this.mPresentationView.show();
            }
        }
    }

    private void dismissPresentationView() {
        if (this.mPresentationView != null) {
            this.mPresentationView.dismiss();
            this.mPresentationView = null;
        }
    }

    private void closePresentationContent() {
        if (this.mPresentationContent != null) {
            this.mPresentationContent.close();
            this.mPresentationContent = null;
        }
    }

    public void onActivityStateChange(Activity activity, int newState) {
        switch (newState) {
            case 3:
                onResume();
                return;
            case 4:
                dismissPresentationView();
                if (this.mPresentationContent != null) {
                    this.mPresentationContent.onPause();
                }
                this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
                return;
            case 6:
                closePresentationContent();
                return;
            default:
                return;
        }
    }
}
