package org.xwalk.core;

import java.util.ArrayList;

public class XWalkSettings {
    public static final int LOAD_CACHE_ELSE_NETWORK = 1;
    public static final int LOAD_CACHE_ONLY = 3;
    public static final int LOAD_DEFAULT = -1;
    public static final int LOAD_NO_CACHE = 2;
    private Object bridge;
    private ArrayList<Object> constructorParams;
    private ArrayList<Object> constructorTypes;
    private XWalkCoreWrapper coreWrapper;
    private ReflectMethod enumLayoutAlgorithmClassValueOfMethod = new ReflectMethod();
    private ReflectMethod getAcceptLanguagesMethod = new ReflectMethod(null, "getAcceptLanguages", new Class[0]);
    private ReflectMethod getAllowContentAccessMethod = new ReflectMethod(null, "getAllowContentAccess", new Class[0]);
    private ReflectMethod getAllowFileAccessFromFileURLsMethod = new ReflectMethod(null, "getAllowFileAccessFromFileURLs", new Class[0]);
    private ReflectMethod getAllowFileAccessMethod = new ReflectMethod(null, "getAllowFileAccess", new Class[0]);
    private ReflectMethod getAllowUniversalAccessFromFileURLsMethod = new ReflectMethod(null, "getAllowUniversalAccessFromFileURLs", new Class[0]);
    private ReflectMethod getBlockNetworkImageMethod = new ReflectMethod(null, "getBlockNetworkImage", new Class[0]);
    private ReflectMethod getBlockNetworkLoadsMethod = new ReflectMethod(null, "getBlockNetworkLoads", new Class[0]);
    private ReflectMethod getBuiltInZoomControlsMethod = new ReflectMethod(null, "getBuiltInZoomControls", new Class[0]);
    private ReflectMethod getCacheModeMethod = new ReflectMethod(null, "getCacheMode", new Class[0]);
    private ReflectMethod getDatabaseEnabledMethod = new ReflectMethod(null, "getDatabaseEnabled", new Class[0]);
    private ReflectMethod getDefaultFixedFontSizeMethod = new ReflectMethod(null, "getDefaultFixedFontSize", new Class[0]);
    private ReflectMethod getDefaultFontSizeMethod = new ReflectMethod(null, "getDefaultFontSize", new Class[0]);
    private ReflectMethod getDomStorageEnabledMethod = new ReflectMethod(null, "getDomStorageEnabled", new Class[0]);
    private ReflectMethod getJavaScriptCanOpenWindowsAutomaticallyMethod = new ReflectMethod(null, "getJavaScriptCanOpenWindowsAutomatically", new Class[0]);
    private ReflectMethod getJavaScriptEnabledMethod = new ReflectMethod(null, "getJavaScriptEnabled", new Class[0]);
    private ReflectMethod getLayoutAlgorithmMethod = new ReflectMethod(null, "getLayoutAlgorithm", new Class[0]);
    private ReflectMethod getLoadWithOverviewModeMethod = new ReflectMethod(null, "getLoadWithOverviewMode", new Class[0]);
    private ReflectMethod getLoadsImagesAutomaticallyMethod = new ReflectMethod(null, "getLoadsImagesAutomatically", new Class[0]);
    private ReflectMethod getMediaPlaybackRequiresUserGestureMethod = new ReflectMethod(null, "getMediaPlaybackRequiresUserGesture", new Class[0]);
    private ReflectMethod getSaveFormDataMethod = new ReflectMethod(null, "getSaveFormData", new Class[0]);
    private ReflectMethod getSupportQuirksModeMethod = new ReflectMethod(null, "getSupportQuirksMode", new Class[0]);
    private ReflectMethod getSupportSpatialNavigationMethod = new ReflectMethod(null, "getSupportSpatialNavigation", new Class[0]);
    private ReflectMethod getTextZoomMethod = new ReflectMethod(null, "getTextZoom", new Class[0]);
    private ReflectMethod getUseWideViewPortMethod = new ReflectMethod(null, "getUseWideViewPort", new Class[0]);
    private ReflectMethod getUserAgentStringMethod = new ReflectMethod(null, "getUserAgentString", new Class[0]);
    private ReflectMethod postWrapperMethod;
    private ReflectMethod setAcceptLanguagesStringMethod = new ReflectMethod(null, "setAcceptLanguages", new Class[0]);
    private ReflectMethod setAllowContentAccessbooleanMethod = new ReflectMethod(null, "setAllowContentAccess", new Class[0]);
    private ReflectMethod setAllowFileAccessFromFileURLsbooleanMethod = new ReflectMethod(null, "setAllowFileAccessFromFileURLs", new Class[0]);
    private ReflectMethod setAllowFileAccessbooleanMethod = new ReflectMethod(null, "setAllowFileAccess", new Class[0]);
    private ReflectMethod setAllowUniversalAccessFromFileURLsbooleanMethod = new ReflectMethod(null, "setAllowUniversalAccessFromFileURLs", new Class[0]);
    private ReflectMethod setBlockNetworkImagebooleanMethod = new ReflectMethod(null, "setBlockNetworkImage", new Class[0]);
    private ReflectMethod setBlockNetworkLoadsbooleanMethod = new ReflectMethod(null, "setBlockNetworkLoads", new Class[0]);
    private ReflectMethod setBuiltInZoomControlsbooleanMethod = new ReflectMethod(null, "setBuiltInZoomControls", new Class[0]);
    private ReflectMethod setCacheModeintMethod = new ReflectMethod(null, "setCacheMode", new Class[0]);
    private ReflectMethod setDatabaseEnabledbooleanMethod = new ReflectMethod(null, "setDatabaseEnabled", new Class[0]);
    private ReflectMethod setDefaultFixedFontSizeintMethod = new ReflectMethod(null, "setDefaultFixedFontSize", new Class[0]);
    private ReflectMethod setDefaultFontSizeintMethod = new ReflectMethod(null, "setDefaultFontSize", new Class[0]);
    private ReflectMethod setDomStorageEnabledbooleanMethod = new ReflectMethod(null, "setDomStorageEnabled", new Class[0]);
    private ReflectMethod setInitialPageScalefloatMethod = new ReflectMethod(null, "setInitialPageScale", new Class[0]);
    private ReflectMethod setJavaScriptCanOpenWindowsAutomaticallybooleanMethod = new ReflectMethod(null, "setJavaScriptCanOpenWindowsAutomatically", new Class[0]);
    private ReflectMethod setJavaScriptEnabledbooleanMethod = new ReflectMethod(null, "setJavaScriptEnabled", new Class[0]);
    private ReflectMethod setLayoutAlgorithmLayoutAlgorithmInternalMethod = new ReflectMethod(null, "setLayoutAlgorithm", new Class[0]);
    private ReflectMethod setLoadWithOverviewModebooleanMethod = new ReflectMethod(null, "setLoadWithOverviewMode", new Class[0]);
    private ReflectMethod setLoadsImagesAutomaticallybooleanMethod = new ReflectMethod(null, "setLoadsImagesAutomatically", new Class[0]);
    private ReflectMethod setMediaPlaybackRequiresUserGesturebooleanMethod = new ReflectMethod(null, "setMediaPlaybackRequiresUserGesture", new Class[0]);
    private ReflectMethod setSaveFormDatabooleanMethod = new ReflectMethod(null, "setSaveFormData", new Class[0]);
    private ReflectMethod setSupportMultipleWindowsbooleanMethod = new ReflectMethod(null, "setSupportMultipleWindows", new Class[0]);
    private ReflectMethod setSupportQuirksModebooleanMethod = new ReflectMethod(null, "setSupportQuirksMode", new Class[0]);
    private ReflectMethod setSupportSpatialNavigationbooleanMethod = new ReflectMethod(null, "setSupportSpatialNavigation", new Class[0]);
    private ReflectMethod setSupportZoombooleanMethod = new ReflectMethod(null, "setSupportZoom", new Class[0]);
    private ReflectMethod setTextZoomintMethod = new ReflectMethod(null, "setTextZoom", new Class[0]);
    private ReflectMethod setUseWideViewPortbooleanMethod = new ReflectMethod(null, "setUseWideViewPort", new Class[0]);
    private ReflectMethod setUserAgentStringStringMethod = new ReflectMethod(null, "setUserAgentString", new Class[0]);
    private ReflectMethod supportMultipleWindowsMethod = new ReflectMethod(null, "supportMultipleWindows", new Class[0]);
    private ReflectMethod supportZoomMethod = new ReflectMethod(null, "supportZoom", new Class[0]);
    private ReflectMethod supportsMultiTouchZoomForTestMethod = new ReflectMethod(null, "supportsMultiTouchZoomForTest", new Class[0]);

    public enum LayoutAlgorithm {
        NORMAL,
        SINGLE_COLUMN,
        NARROW_COLUMNS,
        TEXT_AUTOSIZING
    }

    private Object ConvertLayoutAlgorithm(LayoutAlgorithm type) {
        return this.enumLayoutAlgorithmClassValueOfMethod.invoke(type.toString());
    }

    protected Object getBridge() {
        return this.bridge;
    }

    public XWalkSettings(Object bridge) {
        this.bridge = bridge;
        reflectionInit();
    }

    public void setCacheMode(int mode) {
        try {
            this.setCacheModeintMethod.invoke(Integer.valueOf(mode));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public int getCacheMode() {
        try {
            return ((Integer) this.getCacheModeMethod.invoke(new Object[0])).intValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return 0;
        }
    }

    public void setBlockNetworkLoads(boolean flag) {
        try {
            this.setBlockNetworkLoadsbooleanMethod.invoke(Boolean.valueOf(flag));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getBlockNetworkLoads() {
        try {
            return ((Boolean) this.getBlockNetworkLoadsMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setAllowFileAccess(boolean allow) {
        try {
            this.setAllowFileAccessbooleanMethod.invoke(Boolean.valueOf(allow));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getAllowFileAccess() {
        try {
            return ((Boolean) this.getAllowFileAccessMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setAllowContentAccess(boolean allow) {
        try {
            this.setAllowContentAccessbooleanMethod.invoke(Boolean.valueOf(allow));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getAllowContentAccess() {
        try {
            return ((Boolean) this.getAllowContentAccessMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setJavaScriptEnabled(boolean flag) {
        try {
            this.setJavaScriptEnabledbooleanMethod.invoke(Boolean.valueOf(flag));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void setAllowUniversalAccessFromFileURLs(boolean flag) {
        try {
            this.setAllowUniversalAccessFromFileURLsbooleanMethod.invoke(Boolean.valueOf(flag));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void setAllowFileAccessFromFileURLs(boolean flag) {
        try {
            this.setAllowFileAccessFromFileURLsbooleanMethod.invoke(Boolean.valueOf(flag));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void setLoadsImagesAutomatically(boolean flag) {
        try {
            this.setLoadsImagesAutomaticallybooleanMethod.invoke(Boolean.valueOf(flag));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getLoadsImagesAutomatically() {
        try {
            return ((Boolean) this.getLoadsImagesAutomaticallyMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setBlockNetworkImage(boolean flag) {
        try {
            this.setBlockNetworkImagebooleanMethod.invoke(Boolean.valueOf(flag));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getBlockNetworkImage() {
        try {
            return ((Boolean) this.getBlockNetworkImageMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public boolean getJavaScriptEnabled() {
        try {
            return ((Boolean) this.getJavaScriptEnabledMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public boolean getAllowUniversalAccessFromFileURLs() {
        try {
            return ((Boolean) this.getAllowUniversalAccessFromFileURLsMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public boolean getAllowFileAccessFromFileURLs() {
        try {
            return ((Boolean) this.getAllowFileAccessFromFileURLsMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setJavaScriptCanOpenWindowsAutomatically(boolean flag) {
        try {
            this.setJavaScriptCanOpenWindowsAutomaticallybooleanMethod.invoke(Boolean.valueOf(flag));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getJavaScriptCanOpenWindowsAutomatically() {
        try {
            return ((Boolean) this.getJavaScriptCanOpenWindowsAutomaticallyMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setSupportMultipleWindows(boolean support) {
        try {
            this.setSupportMultipleWindowsbooleanMethod.invoke(Boolean.valueOf(support));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean supportMultipleWindows() {
        try {
            return ((Boolean) this.supportMultipleWindowsMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setUseWideViewPort(boolean use) {
        try {
            this.setUseWideViewPortbooleanMethod.invoke(Boolean.valueOf(use));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getUseWideViewPort() {
        try {
            return ((Boolean) this.getUseWideViewPortMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setDomStorageEnabled(boolean flag) {
        try {
            this.setDomStorageEnabledbooleanMethod.invoke(Boolean.valueOf(flag));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getDomStorageEnabled() {
        try {
            return ((Boolean) this.getDomStorageEnabledMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setDatabaseEnabled(boolean flag) {
        try {
            this.setDatabaseEnabledbooleanMethod.invoke(Boolean.valueOf(flag));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getDatabaseEnabled() {
        try {
            return ((Boolean) this.getDatabaseEnabledMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setMediaPlaybackRequiresUserGesture(boolean require) {
        try {
            this.setMediaPlaybackRequiresUserGesturebooleanMethod.invoke(Boolean.valueOf(require));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getMediaPlaybackRequiresUserGesture() {
        try {
            return ((Boolean) this.getMediaPlaybackRequiresUserGestureMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setUserAgentString(String userAgent) {
        try {
            this.setUserAgentStringStringMethod.invoke(userAgent);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public String getUserAgentString() {
        try {
            return (String) this.getUserAgentStringMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public void setAcceptLanguages(String acceptLanguages) {
        try {
            this.setAcceptLanguagesStringMethod.invoke(acceptLanguages);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public String getAcceptLanguages() {
        try {
            return (String) this.getAcceptLanguagesMethod.invoke(new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public void setSaveFormData(boolean enable) {
        try {
            this.setSaveFormDatabooleanMethod.invoke(Boolean.valueOf(enable));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getSaveFormData() {
        try {
            return ((Boolean) this.getSaveFormDataMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setInitialPageScale(float scaleInPercent) {
        try {
            this.setInitialPageScalefloatMethod.invoke(Float.valueOf(scaleInPercent));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public void setTextZoom(int textZoom) {
        try {
            this.setTextZoomintMethod.invoke(Integer.valueOf(textZoom));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public int getTextZoom() {
        try {
            return ((Integer) this.getTextZoomMethod.invoke(new Object[0])).intValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return 0;
        }
    }

    public void setDefaultFontSize(int size) {
        try {
            this.setDefaultFontSizeintMethod.invoke(Integer.valueOf(size));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public int getDefaultFontSize() {
        try {
            return ((Integer) this.getDefaultFontSizeMethod.invoke(new Object[0])).intValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return 0;
        }
    }

    public void setDefaultFixedFontSize(int size) {
        try {
            this.setDefaultFixedFontSizeintMethod.invoke(Integer.valueOf(size));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public int getDefaultFixedFontSize() {
        try {
            return ((Integer) this.getDefaultFixedFontSizeMethod.invoke(new Object[0])).intValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return 0;
        }
    }

    public void setSupportZoom(boolean support) {
        try {
            this.setSupportZoombooleanMethod.invoke(Boolean.valueOf(support));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean supportZoom() {
        try {
            return ((Boolean) this.supportZoomMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setBuiltInZoomControls(boolean enabled) {
        try {
            this.setBuiltInZoomControlsbooleanMethod.invoke(Boolean.valueOf(enabled));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getBuiltInZoomControls() {
        try {
            return ((Boolean) this.getBuiltInZoomControlsMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public boolean supportsMultiTouchZoomForTest() {
        try {
            return ((Boolean) this.supportsMultiTouchZoomForTestMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setSupportSpatialNavigation(boolean enable) {
        try {
            this.setSupportSpatialNavigationbooleanMethod.invoke(Boolean.valueOf(enable));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getSupportSpatialNavigation() {
        try {
            return ((Boolean) this.getSupportSpatialNavigationMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setSupportQuirksMode(boolean enable) {
        try {
            this.setSupportQuirksModebooleanMethod.invoke(Boolean.valueOf(enable));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getSupportQuirksMode() {
        try {
            return ((Boolean) this.getSupportQuirksModeMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    public void setLayoutAlgorithm(LayoutAlgorithm la) {
        try {
            this.setLayoutAlgorithmLayoutAlgorithmInternalMethod.invoke(ConvertLayoutAlgorithm(la));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public LayoutAlgorithm getLayoutAlgorithm() {
        try {
            return LayoutAlgorithm.valueOf(this.getLayoutAlgorithmMethod.invoke(new Object[0]).toString());
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return null;
        }
    }

    public void setLoadWithOverviewMode(boolean overview) {
        try {
            this.setLoadWithOverviewModebooleanMethod.invoke(Boolean.valueOf(overview));
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
        }
    }

    public boolean getLoadWithOverviewMode() {
        try {
            return ((Boolean) this.getLoadWithOverviewModeMethod.invoke(new Object[0])).booleanValue();
        } catch (UnsupportedOperationException e) {
            if (this.coreWrapper == null) {
                throw new RuntimeException("Crosswalk's APIs are not ready yet");
            }
            XWalkCoreWrapper.handleRuntimeError(e);
            return false;
        }
    }

    void reflectionInit() {
        XWalkCoreWrapper.initEmbeddedMode();
        this.coreWrapper = XWalkCoreWrapper.getInstance();
        if (this.coreWrapper == null) {
            XWalkCoreWrapper.reserveReflectObject(this);
            return;
        }
        this.enumLayoutAlgorithmClassValueOfMethod.init(null, this.coreWrapper.getBridgeClass("XWalkSettingsInternal$LayoutAlgorithmInternal"), "valueOf", String.class);
        this.setCacheModeintMethod.init(this.bridge, null, "setCacheModeSuper", Integer.TYPE);
        this.getCacheModeMethod.init(this.bridge, null, "getCacheModeSuper", new Class[0]);
        this.setBlockNetworkLoadsbooleanMethod.init(this.bridge, null, "setBlockNetworkLoadsSuper", Boolean.TYPE);
        this.getBlockNetworkLoadsMethod.init(this.bridge, null, "getBlockNetworkLoadsSuper", new Class[0]);
        this.setAllowFileAccessbooleanMethod.init(this.bridge, null, "setAllowFileAccessSuper", Boolean.TYPE);
        this.getAllowFileAccessMethod.init(this.bridge, null, "getAllowFileAccessSuper", new Class[0]);
        this.setAllowContentAccessbooleanMethod.init(this.bridge, null, "setAllowContentAccessSuper", Boolean.TYPE);
        this.getAllowContentAccessMethod.init(this.bridge, null, "getAllowContentAccessSuper", new Class[0]);
        this.setJavaScriptEnabledbooleanMethod.init(this.bridge, null, "setJavaScriptEnabledSuper", Boolean.TYPE);
        this.setAllowUniversalAccessFromFileURLsbooleanMethod.init(this.bridge, null, "setAllowUniversalAccessFromFileURLsSuper", Boolean.TYPE);
        this.setAllowFileAccessFromFileURLsbooleanMethod.init(this.bridge, null, "setAllowFileAccessFromFileURLsSuper", Boolean.TYPE);
        this.setLoadsImagesAutomaticallybooleanMethod.init(this.bridge, null, "setLoadsImagesAutomaticallySuper", Boolean.TYPE);
        this.getLoadsImagesAutomaticallyMethod.init(this.bridge, null, "getLoadsImagesAutomaticallySuper", new Class[0]);
        this.setBlockNetworkImagebooleanMethod.init(this.bridge, null, "setBlockNetworkImageSuper", Boolean.TYPE);
        this.getBlockNetworkImageMethod.init(this.bridge, null, "getBlockNetworkImageSuper", new Class[0]);
        this.getJavaScriptEnabledMethod.init(this.bridge, null, "getJavaScriptEnabledSuper", new Class[0]);
        this.getAllowUniversalAccessFromFileURLsMethod.init(this.bridge, null, "getAllowUniversalAccessFromFileURLsSuper", new Class[0]);
        this.getAllowFileAccessFromFileURLsMethod.init(this.bridge, null, "getAllowFileAccessFromFileURLsSuper", new Class[0]);
        this.setJavaScriptCanOpenWindowsAutomaticallybooleanMethod.init(this.bridge, null, "setJavaScriptCanOpenWindowsAutomaticallySuper", Boolean.TYPE);
        this.getJavaScriptCanOpenWindowsAutomaticallyMethod.init(this.bridge, null, "getJavaScriptCanOpenWindowsAutomaticallySuper", new Class[0]);
        this.setSupportMultipleWindowsbooleanMethod.init(this.bridge, null, "setSupportMultipleWindowsSuper", Boolean.TYPE);
        this.supportMultipleWindowsMethod.init(this.bridge, null, "supportMultipleWindowsSuper", new Class[0]);
        this.setUseWideViewPortbooleanMethod.init(this.bridge, null, "setUseWideViewPortSuper", Boolean.TYPE);
        this.getUseWideViewPortMethod.init(this.bridge, null, "getUseWideViewPortSuper", new Class[0]);
        this.setDomStorageEnabledbooleanMethod.init(this.bridge, null, "setDomStorageEnabledSuper", Boolean.TYPE);
        this.getDomStorageEnabledMethod.init(this.bridge, null, "getDomStorageEnabledSuper", new Class[0]);
        this.setDatabaseEnabledbooleanMethod.init(this.bridge, null, "setDatabaseEnabledSuper", Boolean.TYPE);
        this.getDatabaseEnabledMethod.init(this.bridge, null, "getDatabaseEnabledSuper", new Class[0]);
        this.setMediaPlaybackRequiresUserGesturebooleanMethod.init(this.bridge, null, "setMediaPlaybackRequiresUserGestureSuper", Boolean.TYPE);
        this.getMediaPlaybackRequiresUserGestureMethod.init(this.bridge, null, "getMediaPlaybackRequiresUserGestureSuper", new Class[0]);
        this.setUserAgentStringStringMethod.init(this.bridge, null, "setUserAgentStringSuper", String.class);
        this.getUserAgentStringMethod.init(this.bridge, null, "getUserAgentStringSuper", new Class[0]);
        this.setAcceptLanguagesStringMethod.init(this.bridge, null, "setAcceptLanguagesSuper", String.class);
        this.getAcceptLanguagesMethod.init(this.bridge, null, "getAcceptLanguagesSuper", new Class[0]);
        this.setSaveFormDatabooleanMethod.init(this.bridge, null, "setSaveFormDataSuper", Boolean.TYPE);
        this.getSaveFormDataMethod.init(this.bridge, null, "getSaveFormDataSuper", new Class[0]);
        this.setInitialPageScalefloatMethod.init(this.bridge, null, "setInitialPageScaleSuper", Float.TYPE);
        this.setTextZoomintMethod.init(this.bridge, null, "setTextZoomSuper", Integer.TYPE);
        this.getTextZoomMethod.init(this.bridge, null, "getTextZoomSuper", new Class[0]);
        this.setDefaultFontSizeintMethod.init(this.bridge, null, "setDefaultFontSizeSuper", Integer.TYPE);
        this.getDefaultFontSizeMethod.init(this.bridge, null, "getDefaultFontSizeSuper", new Class[0]);
        this.setDefaultFixedFontSizeintMethod.init(this.bridge, null, "setDefaultFixedFontSizeSuper", Integer.TYPE);
        this.getDefaultFixedFontSizeMethod.init(this.bridge, null, "getDefaultFixedFontSizeSuper", new Class[0]);
        this.setSupportZoombooleanMethod.init(this.bridge, null, "setSupportZoomSuper", Boolean.TYPE);
        this.supportZoomMethod.init(this.bridge, null, "supportZoomSuper", new Class[0]);
        this.setBuiltInZoomControlsbooleanMethod.init(this.bridge, null, "setBuiltInZoomControlsSuper", Boolean.TYPE);
        this.getBuiltInZoomControlsMethod.init(this.bridge, null, "getBuiltInZoomControlsSuper", new Class[0]);
        this.supportsMultiTouchZoomForTestMethod.init(this.bridge, null, "supportsMultiTouchZoomForTestSuper", new Class[0]);
        this.setSupportSpatialNavigationbooleanMethod.init(this.bridge, null, "setSupportSpatialNavigationSuper", Boolean.TYPE);
        this.getSupportSpatialNavigationMethod.init(this.bridge, null, "getSupportSpatialNavigationSuper", new Class[0]);
        this.setSupportQuirksModebooleanMethod.init(this.bridge, null, "setSupportQuirksModeSuper", Boolean.TYPE);
        this.getSupportQuirksModeMethod.init(this.bridge, null, "getSupportQuirksModeSuper", new Class[0]);
        this.setLayoutAlgorithmLayoutAlgorithmInternalMethod.init(this.bridge, null, "setLayoutAlgorithmSuper", this.coreWrapper.getBridgeClass("XWalkSettingsInternal$LayoutAlgorithmInternal"));
        this.getLayoutAlgorithmMethod.init(this.bridge, null, "getLayoutAlgorithmSuper", new Class[0]);
        this.setLoadWithOverviewModebooleanMethod.init(this.bridge, null, "setLoadWithOverviewModeSuper", Boolean.TYPE);
        this.getLoadWithOverviewModeMethod.init(this.bridge, null, "getLoadWithOverviewModeSuper", new Class[0]);
    }
}
