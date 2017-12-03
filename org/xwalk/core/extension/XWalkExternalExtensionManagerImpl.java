package org.xwalk.core.extension;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkExternalExtensionManager;
import org.xwalk.core.XWalkNativeExtensionLoader;
import org.xwalk.core.XWalkView;

public class XWalkExternalExtensionManagerImpl extends XWalkExternalExtensionManager implements XWalkExtensionContextClient {
    private static final String TAG = "XWalkExternalExtensionManagerImpl";
    private final Context mContext;
    private final HashMap<String, XWalkExternalExtensionBridge> mExtensions = new HashMap();
    private boolean mLoadExternalExtensions;
    private final XWalkNativeExtensionLoader mNativeExtensionLoader;
    private final XWalkView mXWalkView;

    public XWalkExternalExtensionManagerImpl(XWalkView view) {
        super(view);
        this.mXWalkView = view;
        if (getBridge() == null) {
            Log.e(TAG, "Cannot load external extensions due to old version of runtime library");
            this.mContext = null;
            this.mLoadExternalExtensions = false;
            this.mNativeExtensionLoader = null;
            return;
        }
        this.mContext = getViewContext();
        this.mLoadExternalExtensions = true;
        this.mNativeExtensionLoader = new XWalkNativeExtensionLoader();
        loadNativeExtensions();
    }

    public void registerExtension(XWalkExternalExtension extension) {
        if (this.mExtensions.get(extension.getExtensionName()) != null) {
            Log.e(TAG, extension.getExtensionName() + "is already registered!");
            return;
        }
        this.mExtensions.put(extension.getExtensionName(), XWalkExternalExtensionBridgeFactory.createInstance(extension));
    }

    public void unregisterExtension(String name) {
        XWalkExternalExtensionBridge bridge = (XWalkExternalExtensionBridge) this.mExtensions.get(name);
        if (bridge != null) {
            this.mExtensions.remove(name);
            bridge.onDestroy();
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public Activity getActivity() {
        if (this.mContext instanceof Activity) {
            return (Activity) this.mContext;
        }
        return null;
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        throw new ActivityNotFoundException("This method is no longer supported");
    }

    public void loadExtension(String extensionPath) {
        if (this.mLoadExternalExtensions) {
            int len = extensionPath.length();
            if (extensionPath.charAt(len - 1) == File.separatorChar) {
                extensionPath = extensionPath.substring(0, len - 1);
            }
            String jsonFile = extensionPath + File.separator + extensionPath.substring(extensionPath.lastIndexOf(File.separatorChar) + 1) + ".json";
            try {
                try {
                    JSONObject jsonObject = new JSONObject(getFileContent(this.mContext, jsonFile, false));
                    String name = jsonObject.getString("name");
                    String className = jsonObject.getString("class");
                    String jsApiFile = jsonObject.optString("jsapi");
                    if (!(jsApiFile == null || jsApiFile.length() == 0)) {
                        jsApiFile = extensionPath + File.separator + jsApiFile;
                    }
                    String jsApi = null;
                    if (!(jsApiFile == null || jsApiFile.length() == 0)) {
                        try {
                            jsApi = getFileContent(this.mContext, jsApiFile, false);
                        } catch (IOException e) {
                            Log.w(TAG, "Failed to read the file " + jsApiFile);
                            return;
                        }
                    }
                    if (name != null && className != null) {
                        Log.i(TAG, "createExternalExtension: name: " + name + " className: " + className);
                        createExternalExtension(name, className, jsApi, this);
                    }
                } catch (JSONException e2) {
                    Log.w(TAG, "Failed to parse json file: " + jsonFile);
                }
            } catch (IOException e3) {
                Log.w(TAG, "Failed to read json file: " + jsonFile);
            }
        }
    }

    public void postMessage(XWalkExternalExtension extension, int instanceID, String message) {
        XWalkExternalExtensionBridge bridge = (XWalkExternalExtensionBridge) this.mExtensions.get(extension.getExtensionName());
        if (bridge != null) {
            bridge.postMessage(instanceID, message);
        }
    }

    public void postBinaryMessage(XWalkExternalExtension extension, int instanceID, byte[] message) {
        XWalkExternalExtensionBridge bridge = (XWalkExternalExtensionBridge) this.mExtensions.get(extension.getExtensionName());
        if (bridge != null) {
            bridge.postBinaryMessage(instanceID, message);
        }
    }

    public void broadcastMessage(XWalkExternalExtension extension, String message) {
        XWalkExternalExtensionBridge bridge = (XWalkExternalExtensionBridge) this.mExtensions.get(extension.getExtensionName());
        if (bridge != null) {
            bridge.broadcastMessage(message);
        }
    }

    public void onStart() {
        for (XWalkExternalExtensionBridge extension : this.mExtensions.values()) {
            extension.onStart();
        }
    }

    public void onResume() {
        for (XWalkExternalExtensionBridge extension : this.mExtensions.values()) {
            extension.onResume();
        }
    }

    public void onPause() {
        for (XWalkExternalExtensionBridge extension : this.mExtensions.values()) {
            extension.onPause();
        }
    }

    public void onStop() {
        for (XWalkExternalExtensionBridge extension : this.mExtensions.values()) {
            extension.onStop();
        }
    }

    public void onDestroy() {
        for (XWalkExternalExtensionBridge extension : this.mExtensions.values()) {
            extension.onDestroy();
        }
        this.mExtensions.clear();
    }

    public void onNewIntent(Intent intent) {
        for (XWalkExternalExtensionBridge extension : this.mExtensions.values()) {
            extension.onNewIntent(intent);
        }
    }

    public void setAllowExternalExtensions(boolean load) {
        this.mLoadExternalExtensions = load;
    }

    private String getFileContent(Context context, String fileName, boolean fromRaw) throws IOException {
        String result = "";
        InputStream inputStream = null;
        if (fromRaw) {
            try {
                Resources resource = context.getResources();
                String resName = new File(fileName).getName().split("\\.")[0];
                int resId = resource.getIdentifier(resName, "raw", context.getPackageName());
                if (resId > 0) {
                    inputStream = resource.openRawResource(resId);
                }
            } catch (NotFoundException e) {
                Log.w(TAG, "Inputstream failed to open for R.raw." + resName + ", try to find it in assets");
            } catch (Throwable th) {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        if (inputStream == null) {
            inputStream = context.getAssets().open(fileName);
        }
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        result = new String(buffer);
        if (inputStream != null) {
            inputStream.close();
        }
        return result;
    }

    private void createExternalExtension(String name, String className, String jsApi, XWalkExtensionContextClient extensionContext) {
        try {
            extensionContext.getContext().getClassLoader().loadClass(className).getConstructor(new Class[]{String.class, String.class, XWalkExtensionContextClient.class}).newInstance(new Object[]{name, jsApi, this});
        } catch (ClassNotFoundException e) {
            handleException(e);
        } catch (IllegalAccessException e2) {
            handleException(e2);
        } catch (InstantiationException e3) {
            handleException(e3);
        } catch (InvocationTargetException e4) {
            handleException(e4);
        } catch (NoSuchMethodException e5) {
            handleException(e5);
        }
    }

    private void loadNativeExtensions() {
        String path = null;
        try {
            path = this.mContext.getPackageManager().getApplicationInfo(this.mContext.getPackageName(), 0).nativeLibraryDir;
        } catch (NameNotFoundException e) {
        }
        if (path != null && new File(path).isDirectory()) {
            this.mNativeExtensionLoader.registerNativeExtensionsInPath(path);
        }
    }

    private static void handleException(Exception e) {
        Log.e(TAG, "Error in calling methods of external extensions. " + e.toString());
        e.printStackTrace();
    }
}
