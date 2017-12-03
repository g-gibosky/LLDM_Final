package org.xwalk.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.util.Log;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import junit.framework.Assert;

class XWalkCoreWrapper {
    private static final String BRIDGE_PACKAGE = "org.xwalk.core.internal";
    private static final String TAG = "XWalkLib";
    private static final String WRAPPER_PACKAGE = "org.xwalk.core";
    private static final String XWALK_CORE_CLASSES_DEX = "classes.dex";
    private static XWalkCoreWrapper sInstance;
    private static XWalkCoreWrapper sProvisionalInstance;
    private static HashMap<String, LinkedList<ReservedAction>> sReservedActions = new HashMap();
    private static LinkedList<String> sReservedActivities = new LinkedList();
    private int mApiVersion = 8;
    private Context mBridgeContext;
    private ClassLoader mBridgeLoader;
    private int mCoreStatus;
    private int mMinApiVersion;
    private Context mWrapperContext;

    private static class ReservedAction {
        Object[] mArguments;
        Class<?> mClass;
        ReflectMethod mMethod;
        Object mObject;

        ReservedAction(Object object) {
            this.mObject = object;
        }

        ReservedAction(Class<?> clazz) {
            this.mClass = clazz;
        }

        ReservedAction(ReflectMethod method) {
            this.mMethod = method;
            if (method.getArguments() != null) {
                this.mArguments = Arrays.copyOf(method.getArguments(), method.getArguments().length);
            }
        }
    }

    public static XWalkCoreWrapper getInstance() {
        return sInstance;
    }

    public static int getCoreStatus() {
        if (sInstance != null) {
            return sInstance.mCoreStatus;
        }
        if (sProvisionalInstance == null) {
            return 0;
        }
        return sProvisionalInstance.mCoreStatus;
    }

    public static void handlePreInit(String tag) {
        if (sInstance == null) {
            Log.d(TAG, "Pre init xwalk core in " + tag);
            if (sReservedActions.containsKey(tag)) {
                sReservedActions.remove(tag);
            } else {
                sReservedActivities.add(tag);
            }
            sReservedActions.put(tag, new LinkedList());
        }
    }

    public static void reserveReflectObject(Object object) {
        String tag = (String) sReservedActivities.getLast();
        Log.d(TAG, "Reserve object " + object.getClass() + " to " + tag);
        ((LinkedList) sReservedActions.get(tag)).add(new ReservedAction(object));
    }

    public static void reserveReflectClass(Class<?> clazz) {
        String tag = (String) sReservedActivities.getLast();
        Log.d(TAG, "Reserve class " + clazz.toString() + " to " + tag);
        ((LinkedList) sReservedActions.get(tag)).add(new ReservedAction((Class) clazz));
    }

    public static void reserveReflectMethod(ReflectMethod method) {
        String tag = (String) sReservedActivities.getLast();
        Log.d(TAG, "Reserve method " + method.toString() + " to " + tag);
        ((LinkedList) sReservedActions.get(tag)).add(new ReservedAction(method));
    }

    public static void handlePostInit(String tag) {
        Log.d(TAG, "Post init xwalk core in " + tag);
        if (sReservedActions.containsKey(tag)) {
            Iterator i$ = ((LinkedList) sReservedActions.get(tag)).iterator();
            while (i$.hasNext()) {
                ReservedAction action = (ReservedAction) i$.next();
                if (action.mObject != null) {
                    Log.d(TAG, "Init reserved object: " + action.mObject.getClass());
                    new ReflectMethod(action.mObject, "reflectionInit", new Class[0]).invoke(new Object[0]);
                } else if (action.mClass != null) {
                    Log.d(TAG, "Init reserved class: " + action.mClass.toString());
                    new ReflectMethod(action.mClass, "reflectionInit", new Class[0]).invoke(new Object[0]);
                } else {
                    Log.d(TAG, "Call reserved method: " + action.mMethod.toString());
                    Object[] args = action.mArguments;
                    if (args != null) {
                        for (int i = 0; i < args.length; i++) {
                            if (args[i] instanceof ReflectMethod) {
                                args[i] = ((ReflectMethod) args[i]).invokeWithArguments();
                            }
                        }
                    }
                    action.mMethod.invoke(args);
                }
            }
            sReservedActions.remove(tag);
            sReservedActivities.remove(tag);
        }
    }

    public static void handleRuntimeError(RuntimeException e) {
        Log.e(TAG, "This API is incompatible with the Crosswalk runtime library");
        e.printStackTrace();
    }

    public static int attachXWalkCore() {
        Assert.assertFalse(sReservedActivities.isEmpty());
        Assert.assertNull(sInstance);
        Log.d(TAG, "Attach xwalk core");
        sProvisionalInstance = new XWalkCoreWrapper(XWalkEnvironment.getApplicationContext(), 1);
        if (sProvisionalInstance.findEmbeddedCore()) {
            return sProvisionalInstance.mCoreStatus;
        }
        if (XWalkEnvironment.isDownloadMode()) {
            sProvisionalInstance.findDownloadedCore();
            return sProvisionalInstance.mCoreStatus;
        }
        if (XWalkEnvironment.is64bitDevice()) {
            if (!(sProvisionalInstance.findSharedCore("org.xwalk.core") || sProvisionalInstance.findSharedCore(XWalkLibraryInterface.XWALK_CORE64_PACKAGE) || !XWalkEnvironment.isIaDevice())) {
                sProvisionalInstance.findSharedCore(XWalkLibraryInterface.XWALK_CORE64_IA_PACKAGE);
            }
        } else if (!sProvisionalInstance.findSharedCore("org.xwalk.core") && XWalkEnvironment.isIaDevice()) {
            sProvisionalInstance.findSharedCore(XWalkLibraryInterface.XWALK_CORE_IA_PACKAGE);
        }
        return sProvisionalInstance.mCoreStatus;
    }

    public static void dockXWalkCore() {
        Assert.assertNotNull(sProvisionalInstance);
        Assert.assertNull(sInstance);
        Log.d(TAG, "Dock xwalk core");
        sInstance = sProvisionalInstance;
        sProvisionalInstance = null;
        sInstance.initCoreBridge();
        sInstance.initXWalkView();
    }

    public static void initEmbeddedMode() {
        if (sInstance == null && sReservedActivities.isEmpty()) {
            Log.d(TAG, "Init embedded mode");
            XWalkCoreWrapper provisionalInstance = new XWalkCoreWrapper(null, -1);
            if (provisionalInstance.findEmbeddedCore()) {
                sInstance = provisionalInstance;
                sInstance.initCoreBridge();
                return;
            }
            throw new RuntimeException("Please have your activity extend XWalkActivity for shared mode");
        }
    }

    private XWalkCoreWrapper(Context context, int minApiVersion) {
        if (minApiVersion <= 0 || minApiVersion > this.mApiVersion) {
            minApiVersion = this.mApiVersion;
        }
        this.mMinApiVersion = minApiVersion;
        this.mCoreStatus = 0;
        this.mWrapperContext = context;
    }

    private void initCoreBridge() {
        Log.d(TAG, "Init core bridge");
        new ReflectMethod(getBridgeClass("XWalkCoreBridge"), "init", Context.class, Object.class).invoke(this.mBridgeContext, this);
    }

    private void initXWalkView() {
        Log.d(TAG, "Init xwalk view");
        new ReflectMethod(getBridgeClass("XWalkViewDelegate"), "init", Context.class, Context.class).invoke(this.mBridgeContext, this.mWrapperContext);
    }

    private boolean findEmbeddedCore() {
        this.mBridgeContext = null;
        this.mBridgeLoader = XWalkCoreWrapper.class.getClassLoader();
        if (checkCoreVersion() && checkCoreArchitecture()) {
            Log.d(TAG, "Running in embedded mode");
            this.mCoreStatus = 1;
            return true;
        }
        this.mBridgeLoader = null;
        return false;
    }

    private boolean findSharedCore(String packageName) {
        if (!checkCorePackage(packageName)) {
            return false;
        }
        this.mBridgeLoader = this.mBridgeContext.getClassLoader();
        if (checkCoreVersion() && checkCoreArchitecture()) {
            Log.d(TAG, "Running in shared mode");
            this.mCoreStatus = 1;
            return true;
        }
        this.mBridgeContext = null;
        this.mBridgeLoader = null;
        return false;
    }

    private boolean findDownloadedCore() {
        String libDir = XWalkEnvironment.getExtractedCoreDir();
        this.mBridgeLoader = new DexClassLoader(libDir + File.separator + XWALK_CORE_CLASSES_DEX, XWalkEnvironment.getOptimizedDexDir(), libDir, ClassLoader.getSystemClassLoader());
        if (checkCoreVersion() && checkCoreArchitecture()) {
            Log.d(TAG, "Running in downloaded mode");
            this.mCoreStatus = 1;
            return true;
        }
        this.mBridgeLoader = null;
        return false;
    }

    private boolean checkCoreVersion() {
        Log.d(TAG, "[Environment] SDK:" + VERSION.SDK_INT);
        Log.d(TAG, "[App Version] build:23.53.589.4, api:" + this.mApiVersion + ", min_api:" + this.mMinApiVersion);
        try {
            Class clazz = getBridgeClass("XWalkCoreVersion");
            String buildVersion = "";
            try {
                buildVersion = (String) new ReflectField(clazz, "XWALK_BUILD_VERSION").get();
            } catch (RuntimeException e) {
            }
            int libVersion = ((Integer) new ReflectField(clazz, "API_VERSION").get()).intValue();
            int minLibVersion = ((Integer) new ReflectField(clazz, "MIN_API_VERSION").get()).intValue();
            Log.d(TAG, "[Lib Version] build:" + buildVersion + ", api:" + libVersion + ", min_api:" + minLibVersion);
            if (XWalkEnvironment.isDownloadMode() && XWalkEnvironment.isDownloadModeUpdate() && !buildVersion.isEmpty() && !buildVersion.equals("23.53.589.4")) {
                this.mCoreStatus = 8;
                return false;
            } else if (this.mMinApiVersion > libVersion) {
                this.mCoreStatus = 3;
                return false;
            } else if (this.mApiVersion < minLibVersion) {
                this.mCoreStatus = 4;
                return false;
            } else {
                Log.d(TAG, "XWalk core version matched");
                return true;
            }
        } catch (RuntimeException e2) {
            Log.d(TAG, "XWalk core not found");
            this.mCoreStatus = 2;
            return false;
        }
    }

    private boolean checkCoreArchitecture() {
        try {
            ReflectMethod method = new ReflectMethod(getBridgeClass("XWalkViewDelegate"), "loadXWalkLibrary", Context.class, String.class);
            boolean architectureMatched = false;
            String libDir = null;
            if (this.mBridgeContext != null) {
                if (VERSION.SDK_INT < 17) {
                    libDir = "/data/data/" + this.mBridgeContext.getPackageName() + "/lib";
                }
                architectureMatched = ((Boolean) method.invoke(this.mBridgeContext, libDir)).booleanValue();
            } else {
                try {
                    architectureMatched = ((Boolean) method.invoke(this.mBridgeContext, null)).booleanValue();
                } catch (RuntimeException ex) {
                    Log.d(TAG, ex.getLocalizedMessage());
                }
                if (!architectureMatched) {
                    if (this.mWrapperContext != null) {
                        libDir = XWalkEnvironment.getPrivateDataDir();
                        architectureMatched = ((Boolean) method.invoke(this.mBridgeContext, libDir)).booleanValue();
                    }
                }
            }
            if (architectureMatched) {
                Log.d(TAG, "XWalk core architecture matched");
                return true;
            }
            Log.d(TAG, "Mismatch of CPU architecture");
            this.mCoreStatus = 6;
            return false;
        } catch (RuntimeException e) {
            Log.d(TAG, e.getLocalizedMessage());
            if (e.getCause() instanceof UnsatisfiedLinkError) {
                this.mCoreStatus = 6;
                return false;
            }
            this.mCoreStatus = 5;
            return false;
        }
    }

    private boolean checkCorePackage(String packageName) {
        try {
            if (verifyPackageInfo(this.mWrapperContext.getPackageManager().getPackageInfo(packageName, 64), XWalkAppVersion.XWALK_APK_HASH_ALGORITHM, XWalkAppVersion.XWALK_APK_HASH_CODE)) {
                try {
                    this.mBridgeContext = this.mWrapperContext.createPackageContext(packageName, 3);
                    Log.d(TAG, "Created package context for " + packageName);
                    return true;
                } catch (NameNotFoundException e) {
                    Log.d(TAG, packageName + " not found");
                    return false;
                }
            }
            Log.d(TAG, packageName + " signature verification failed");
            this.mCoreStatus = 7;
            return false;
        } catch (NameNotFoundException e2) {
            Log.d(TAG, packageName + " not found");
            return false;
        }
    }

    private boolean verifyPackageInfo(PackageInfo packageInfo, String hashAlgorithm, String hashCode) {
        if (packageInfo.signatures == null) {
            Log.e(TAG, "No signature in package info");
            return false;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
            byte[] hashArray = hexStringToByteArray(hashCode);
            if (hashArray == null) {
                throw new IllegalArgumentException("Invalid hash code");
            }
            for (int i = 0; i < packageInfo.signatures.length; i++) {
                Log.d(TAG, "Checking signature " + i);
                if (MessageDigest.isEqual(md.digest(packageInfo.signatures[i].toByteArray()), hashArray)) {
                    Log.d(TAG, "Signature passed verification");
                    return true;
                }
                Log.e(TAG, "Hash code does not match");
            }
            return false;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Invalid hash algorithm");
        } catch (NullPointerException e2) {
            throw new IllegalArgumentException("Invalid hash algorithm");
        }
    }

    private byte[] hexStringToByteArray(String str) {
        if (str == null || str.isEmpty() || str.length() % 2 != 0) {
            return null;
        }
        byte[] result = new byte[(str.length() / 2)];
        for (int i = 0; i < str.length(); i += 2) {
            result[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return result;
    }

    public boolean isSharedMode() {
        return this.mBridgeContext != null;
    }

    public Object getBridgeObject(Object object) {
        try {
            return new ReflectMethod(object, "getBridge", new Class[0]).invoke(new Object[0]);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public Object getWrapperObject(Object object) {
        try {
            return new ReflectMethod(object, "getWrapper", new Class[0]).invoke(new Object[0]);
        } catch (RuntimeException e) {
            return null;
        }
    }

    public Class<?> getBridgeClass(String name) {
        try {
            return this.mBridgeLoader.loadClass("org.xwalk.core.internal." + name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
