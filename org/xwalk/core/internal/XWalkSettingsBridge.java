package org.xwalk.core.internal;

import org.xwalk.core.internal.XWalkSettingsInternal.LayoutAlgorithmInternal;

public class XWalkSettingsBridge extends XWalkSettingsInternal {
    private XWalkCoreBridge coreBridge;
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
    private XWalkSettingsInternal internal;
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
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    private Object ConvertLayoutAlgorithmInternal(LayoutAlgorithmInternal type) {
        return this.enumLayoutAlgorithmClassValueOfMethod.invoke(type.toString());
    }

    XWalkSettingsBridge(XWalkSettingsInternal internal) {
        this.internal = internal;
        reflectionInit();
    }

    public void setCacheMode(int mode) {
        if (this.setCacheModeintMethod == null || this.setCacheModeintMethod.isNull()) {
            setCacheModeSuper(mode);
            return;
        }
        this.setCacheModeintMethod.invoke(Integer.valueOf(mode));
    }

    public void setCacheModeSuper(int mode) {
        if (this.internal == null) {
            super.setCacheMode(mode);
        } else {
            this.internal.setCacheMode(mode);
        }
    }

    public int getCacheMode() {
        if (this.getCacheModeMethod == null || this.getCacheModeMethod.isNull()) {
            return getCacheModeSuper();
        }
        return ((Integer) this.getCacheModeMethod.invoke(new Object[0])).intValue();
    }

    public int getCacheModeSuper() {
        if (this.internal == null) {
            return super.getCacheMode();
        }
        return this.internal.getCacheMode();
    }

    public void setBlockNetworkLoads(boolean flag) {
        if (this.setBlockNetworkLoadsbooleanMethod == null || this.setBlockNetworkLoadsbooleanMethod.isNull()) {
            setBlockNetworkLoadsSuper(flag);
            return;
        }
        this.setBlockNetworkLoadsbooleanMethod.invoke(Boolean.valueOf(flag));
    }

    public void setBlockNetworkLoadsSuper(boolean flag) {
        if (this.internal == null) {
            super.setBlockNetworkLoads(flag);
        } else {
            this.internal.setBlockNetworkLoads(flag);
        }
    }

    public boolean getBlockNetworkLoads() {
        if (this.getBlockNetworkLoadsMethod == null || this.getBlockNetworkLoadsMethod.isNull()) {
            return getBlockNetworkLoadsSuper();
        }
        return ((Boolean) this.getBlockNetworkLoadsMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getBlockNetworkLoadsSuper() {
        if (this.internal == null) {
            return super.getBlockNetworkLoads();
        }
        return this.internal.getBlockNetworkLoads();
    }

    public void setAllowFileAccess(boolean allow) {
        if (this.setAllowFileAccessbooleanMethod == null || this.setAllowFileAccessbooleanMethod.isNull()) {
            setAllowFileAccessSuper(allow);
            return;
        }
        this.setAllowFileAccessbooleanMethod.invoke(Boolean.valueOf(allow));
    }

    public void setAllowFileAccessSuper(boolean allow) {
        if (this.internal == null) {
            super.setAllowFileAccess(allow);
        } else {
            this.internal.setAllowFileAccess(allow);
        }
    }

    public boolean getAllowFileAccess() {
        if (this.getAllowFileAccessMethod == null || this.getAllowFileAccessMethod.isNull()) {
            return getAllowFileAccessSuper();
        }
        return ((Boolean) this.getAllowFileAccessMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getAllowFileAccessSuper() {
        if (this.internal == null) {
            return super.getAllowFileAccess();
        }
        return this.internal.getAllowFileAccess();
    }

    public void setAllowContentAccess(boolean allow) {
        if (this.setAllowContentAccessbooleanMethod == null || this.setAllowContentAccessbooleanMethod.isNull()) {
            setAllowContentAccessSuper(allow);
            return;
        }
        this.setAllowContentAccessbooleanMethod.invoke(Boolean.valueOf(allow));
    }

    public void setAllowContentAccessSuper(boolean allow) {
        if (this.internal == null) {
            super.setAllowContentAccess(allow);
        } else {
            this.internal.setAllowContentAccess(allow);
        }
    }

    public boolean getAllowContentAccess() {
        if (this.getAllowContentAccessMethod == null || this.getAllowContentAccessMethod.isNull()) {
            return getAllowContentAccessSuper();
        }
        return ((Boolean) this.getAllowContentAccessMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getAllowContentAccessSuper() {
        if (this.internal == null) {
            return super.getAllowContentAccess();
        }
        return this.internal.getAllowContentAccess();
    }

    public void setJavaScriptEnabled(boolean flag) {
        if (this.setJavaScriptEnabledbooleanMethod == null || this.setJavaScriptEnabledbooleanMethod.isNull()) {
            setJavaScriptEnabledSuper(flag);
            return;
        }
        this.setJavaScriptEnabledbooleanMethod.invoke(Boolean.valueOf(flag));
    }

    public void setJavaScriptEnabledSuper(boolean flag) {
        if (this.internal == null) {
            super.setJavaScriptEnabled(flag);
        } else {
            this.internal.setJavaScriptEnabled(flag);
        }
    }

    public void setAllowUniversalAccessFromFileURLs(boolean flag) {
        if (this.setAllowUniversalAccessFromFileURLsbooleanMethod == null || this.setAllowUniversalAccessFromFileURLsbooleanMethod.isNull()) {
            setAllowUniversalAccessFromFileURLsSuper(flag);
            return;
        }
        this.setAllowUniversalAccessFromFileURLsbooleanMethod.invoke(Boolean.valueOf(flag));
    }

    public void setAllowUniversalAccessFromFileURLsSuper(boolean flag) {
        if (this.internal == null) {
            super.setAllowUniversalAccessFromFileURLs(flag);
        } else {
            this.internal.setAllowUniversalAccessFromFileURLs(flag);
        }
    }

    public void setAllowFileAccessFromFileURLs(boolean flag) {
        if (this.setAllowFileAccessFromFileURLsbooleanMethod == null || this.setAllowFileAccessFromFileURLsbooleanMethod.isNull()) {
            setAllowFileAccessFromFileURLsSuper(flag);
            return;
        }
        this.setAllowFileAccessFromFileURLsbooleanMethod.invoke(Boolean.valueOf(flag));
    }

    public void setAllowFileAccessFromFileURLsSuper(boolean flag) {
        if (this.internal == null) {
            super.setAllowFileAccessFromFileURLs(flag);
        } else {
            this.internal.setAllowFileAccessFromFileURLs(flag);
        }
    }

    public void setLoadsImagesAutomatically(boolean flag) {
        if (this.setLoadsImagesAutomaticallybooleanMethod == null || this.setLoadsImagesAutomaticallybooleanMethod.isNull()) {
            setLoadsImagesAutomaticallySuper(flag);
            return;
        }
        this.setLoadsImagesAutomaticallybooleanMethod.invoke(Boolean.valueOf(flag));
    }

    public void setLoadsImagesAutomaticallySuper(boolean flag) {
        if (this.internal == null) {
            super.setLoadsImagesAutomatically(flag);
        } else {
            this.internal.setLoadsImagesAutomatically(flag);
        }
    }

    public boolean getLoadsImagesAutomatically() {
        if (this.getLoadsImagesAutomaticallyMethod == null || this.getLoadsImagesAutomaticallyMethod.isNull()) {
            return getLoadsImagesAutomaticallySuper();
        }
        return ((Boolean) this.getLoadsImagesAutomaticallyMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getLoadsImagesAutomaticallySuper() {
        if (this.internal == null) {
            return super.getLoadsImagesAutomatically();
        }
        return this.internal.getLoadsImagesAutomatically();
    }

    public void setBlockNetworkImage(boolean flag) {
        if (this.setBlockNetworkImagebooleanMethod == null || this.setBlockNetworkImagebooleanMethod.isNull()) {
            setBlockNetworkImageSuper(flag);
            return;
        }
        this.setBlockNetworkImagebooleanMethod.invoke(Boolean.valueOf(flag));
    }

    public void setBlockNetworkImageSuper(boolean flag) {
        if (this.internal == null) {
            super.setBlockNetworkImage(flag);
        } else {
            this.internal.setBlockNetworkImage(flag);
        }
    }

    public boolean getBlockNetworkImage() {
        if (this.getBlockNetworkImageMethod == null || this.getBlockNetworkImageMethod.isNull()) {
            return getBlockNetworkImageSuper();
        }
        return ((Boolean) this.getBlockNetworkImageMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getBlockNetworkImageSuper() {
        if (this.internal == null) {
            return super.getBlockNetworkImage();
        }
        return this.internal.getBlockNetworkImage();
    }

    public boolean getJavaScriptEnabled() {
        if (this.getJavaScriptEnabledMethod == null || this.getJavaScriptEnabledMethod.isNull()) {
            return getJavaScriptEnabledSuper();
        }
        return ((Boolean) this.getJavaScriptEnabledMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getJavaScriptEnabledSuper() {
        if (this.internal == null) {
            return super.getJavaScriptEnabled();
        }
        return this.internal.getJavaScriptEnabled();
    }

    public boolean getAllowUniversalAccessFromFileURLs() {
        if (this.getAllowUniversalAccessFromFileURLsMethod == null || this.getAllowUniversalAccessFromFileURLsMethod.isNull()) {
            return getAllowUniversalAccessFromFileURLsSuper();
        }
        return ((Boolean) this.getAllowUniversalAccessFromFileURLsMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getAllowUniversalAccessFromFileURLsSuper() {
        if (this.internal == null) {
            return super.getAllowUniversalAccessFromFileURLs();
        }
        return this.internal.getAllowUniversalAccessFromFileURLs();
    }

    public boolean getAllowFileAccessFromFileURLs() {
        if (this.getAllowFileAccessFromFileURLsMethod == null || this.getAllowFileAccessFromFileURLsMethod.isNull()) {
            return getAllowFileAccessFromFileURLsSuper();
        }
        return ((Boolean) this.getAllowFileAccessFromFileURLsMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getAllowFileAccessFromFileURLsSuper() {
        if (this.internal == null) {
            return super.getAllowFileAccessFromFileURLs();
        }
        return this.internal.getAllowFileAccessFromFileURLs();
    }

    public void setJavaScriptCanOpenWindowsAutomatically(boolean flag) {
        if (this.setJavaScriptCanOpenWindowsAutomaticallybooleanMethod == null || this.setJavaScriptCanOpenWindowsAutomaticallybooleanMethod.isNull()) {
            setJavaScriptCanOpenWindowsAutomaticallySuper(flag);
            return;
        }
        this.setJavaScriptCanOpenWindowsAutomaticallybooleanMethod.invoke(Boolean.valueOf(flag));
    }

    public void setJavaScriptCanOpenWindowsAutomaticallySuper(boolean flag) {
        if (this.internal == null) {
            super.setJavaScriptCanOpenWindowsAutomatically(flag);
        } else {
            this.internal.setJavaScriptCanOpenWindowsAutomatically(flag);
        }
    }

    public boolean getJavaScriptCanOpenWindowsAutomatically() {
        if (this.getJavaScriptCanOpenWindowsAutomaticallyMethod == null || this.getJavaScriptCanOpenWindowsAutomaticallyMethod.isNull()) {
            return getJavaScriptCanOpenWindowsAutomaticallySuper();
        }
        return ((Boolean) this.getJavaScriptCanOpenWindowsAutomaticallyMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getJavaScriptCanOpenWindowsAutomaticallySuper() {
        if (this.internal == null) {
            return super.getJavaScriptCanOpenWindowsAutomatically();
        }
        return this.internal.getJavaScriptCanOpenWindowsAutomatically();
    }

    public void setSupportMultipleWindows(boolean support) {
        if (this.setSupportMultipleWindowsbooleanMethod == null || this.setSupportMultipleWindowsbooleanMethod.isNull()) {
            setSupportMultipleWindowsSuper(support);
            return;
        }
        this.setSupportMultipleWindowsbooleanMethod.invoke(Boolean.valueOf(support));
    }

    public void setSupportMultipleWindowsSuper(boolean support) {
        if (this.internal == null) {
            super.setSupportMultipleWindows(support);
        } else {
            this.internal.setSupportMultipleWindows(support);
        }
    }

    public boolean supportMultipleWindows() {
        if (this.supportMultipleWindowsMethod == null || this.supportMultipleWindowsMethod.isNull()) {
            return supportMultipleWindowsSuper();
        }
        return ((Boolean) this.supportMultipleWindowsMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean supportMultipleWindowsSuper() {
        if (this.internal == null) {
            return super.supportMultipleWindows();
        }
        return this.internal.supportMultipleWindows();
    }

    public void setUseWideViewPort(boolean use) {
        if (this.setUseWideViewPortbooleanMethod == null || this.setUseWideViewPortbooleanMethod.isNull()) {
            setUseWideViewPortSuper(use);
            return;
        }
        this.setUseWideViewPortbooleanMethod.invoke(Boolean.valueOf(use));
    }

    public void setUseWideViewPortSuper(boolean use) {
        if (this.internal == null) {
            super.setUseWideViewPort(use);
        } else {
            this.internal.setUseWideViewPort(use);
        }
    }

    public boolean getUseWideViewPort() {
        if (this.getUseWideViewPortMethod == null || this.getUseWideViewPortMethod.isNull()) {
            return getUseWideViewPortSuper();
        }
        return ((Boolean) this.getUseWideViewPortMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getUseWideViewPortSuper() {
        if (this.internal == null) {
            return super.getUseWideViewPort();
        }
        return this.internal.getUseWideViewPort();
    }

    public void setDomStorageEnabled(boolean flag) {
        if (this.setDomStorageEnabledbooleanMethod == null || this.setDomStorageEnabledbooleanMethod.isNull()) {
            setDomStorageEnabledSuper(flag);
            return;
        }
        this.setDomStorageEnabledbooleanMethod.invoke(Boolean.valueOf(flag));
    }

    public void setDomStorageEnabledSuper(boolean flag) {
        if (this.internal == null) {
            super.setDomStorageEnabled(flag);
        } else {
            this.internal.setDomStorageEnabled(flag);
        }
    }

    public boolean getDomStorageEnabled() {
        if (this.getDomStorageEnabledMethod == null || this.getDomStorageEnabledMethod.isNull()) {
            return getDomStorageEnabledSuper();
        }
        return ((Boolean) this.getDomStorageEnabledMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getDomStorageEnabledSuper() {
        if (this.internal == null) {
            return super.getDomStorageEnabled();
        }
        return this.internal.getDomStorageEnabled();
    }

    public void setDatabaseEnabled(boolean flag) {
        if (this.setDatabaseEnabledbooleanMethod == null || this.setDatabaseEnabledbooleanMethod.isNull()) {
            setDatabaseEnabledSuper(flag);
            return;
        }
        this.setDatabaseEnabledbooleanMethod.invoke(Boolean.valueOf(flag));
    }

    public void setDatabaseEnabledSuper(boolean flag) {
        if (this.internal == null) {
            super.setDatabaseEnabled(flag);
        } else {
            this.internal.setDatabaseEnabled(flag);
        }
    }

    public boolean getDatabaseEnabled() {
        if (this.getDatabaseEnabledMethod == null || this.getDatabaseEnabledMethod.isNull()) {
            return getDatabaseEnabledSuper();
        }
        return ((Boolean) this.getDatabaseEnabledMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getDatabaseEnabledSuper() {
        if (this.internal == null) {
            return super.getDatabaseEnabled();
        }
        return this.internal.getDatabaseEnabled();
    }

    public void setMediaPlaybackRequiresUserGesture(boolean require) {
        if (this.setMediaPlaybackRequiresUserGesturebooleanMethod == null || this.setMediaPlaybackRequiresUserGesturebooleanMethod.isNull()) {
            setMediaPlaybackRequiresUserGestureSuper(require);
            return;
        }
        this.setMediaPlaybackRequiresUserGesturebooleanMethod.invoke(Boolean.valueOf(require));
    }

    public void setMediaPlaybackRequiresUserGestureSuper(boolean require) {
        if (this.internal == null) {
            super.setMediaPlaybackRequiresUserGesture(require);
        } else {
            this.internal.setMediaPlaybackRequiresUserGesture(require);
        }
    }

    public boolean getMediaPlaybackRequiresUserGesture() {
        if (this.getMediaPlaybackRequiresUserGestureMethod == null || this.getMediaPlaybackRequiresUserGestureMethod.isNull()) {
            return getMediaPlaybackRequiresUserGestureSuper();
        }
        return ((Boolean) this.getMediaPlaybackRequiresUserGestureMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getMediaPlaybackRequiresUserGestureSuper() {
        if (this.internal == null) {
            return super.getMediaPlaybackRequiresUserGesture();
        }
        return this.internal.getMediaPlaybackRequiresUserGesture();
    }

    public void setUserAgentString(String userAgent) {
        if (this.setUserAgentStringStringMethod == null || this.setUserAgentStringStringMethod.isNull()) {
            setUserAgentStringSuper(userAgent);
            return;
        }
        this.setUserAgentStringStringMethod.invoke(userAgent);
    }

    public void setUserAgentStringSuper(String userAgent) {
        if (this.internal == null) {
            super.setUserAgentString(userAgent);
        } else {
            this.internal.setUserAgentString(userAgent);
        }
    }

    public String getUserAgentString() {
        if (this.getUserAgentStringMethod == null || this.getUserAgentStringMethod.isNull()) {
            return getUserAgentStringSuper();
        }
        return (String) this.getUserAgentStringMethod.invoke(new Object[0]);
    }

    public String getUserAgentStringSuper() {
        String ret;
        if (this.internal == null) {
            ret = super.getUserAgentString();
        } else {
            ret = this.internal.getUserAgentString();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setAcceptLanguages(String acceptLanguages) {
        if (this.setAcceptLanguagesStringMethod == null || this.setAcceptLanguagesStringMethod.isNull()) {
            setAcceptLanguagesSuper(acceptLanguages);
            return;
        }
        this.setAcceptLanguagesStringMethod.invoke(acceptLanguages);
    }

    public void setAcceptLanguagesSuper(String acceptLanguages) {
        if (this.internal == null) {
            super.setAcceptLanguages(acceptLanguages);
        } else {
            this.internal.setAcceptLanguages(acceptLanguages);
        }
    }

    public String getAcceptLanguages() {
        if (this.getAcceptLanguagesMethod == null || this.getAcceptLanguagesMethod.isNull()) {
            return getAcceptLanguagesSuper();
        }
        return (String) this.getAcceptLanguagesMethod.invoke(new Object[0]);
    }

    public String getAcceptLanguagesSuper() {
        String ret;
        if (this.internal == null) {
            ret = super.getAcceptLanguages();
        } else {
            ret = this.internal.getAcceptLanguages();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setSaveFormData(boolean enable) {
        if (this.setSaveFormDatabooleanMethod == null || this.setSaveFormDatabooleanMethod.isNull()) {
            setSaveFormDataSuper(enable);
            return;
        }
        this.setSaveFormDatabooleanMethod.invoke(Boolean.valueOf(enable));
    }

    public void setSaveFormDataSuper(boolean enable) {
        if (this.internal == null) {
            super.setSaveFormData(enable);
        } else {
            this.internal.setSaveFormData(enable);
        }
    }

    public boolean getSaveFormData() {
        if (this.getSaveFormDataMethod == null || this.getSaveFormDataMethod.isNull()) {
            return getSaveFormDataSuper();
        }
        return ((Boolean) this.getSaveFormDataMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getSaveFormDataSuper() {
        if (this.internal == null) {
            return super.getSaveFormData();
        }
        return this.internal.getSaveFormData();
    }

    public void setInitialPageScale(float scaleInPercent) {
        if (this.setInitialPageScalefloatMethod == null || this.setInitialPageScalefloatMethod.isNull()) {
            setInitialPageScaleSuper(scaleInPercent);
            return;
        }
        this.setInitialPageScalefloatMethod.invoke(Float.valueOf(scaleInPercent));
    }

    public void setInitialPageScaleSuper(float scaleInPercent) {
        if (this.internal == null) {
            super.setInitialPageScale(scaleInPercent);
        } else {
            this.internal.setInitialPageScale(scaleInPercent);
        }
    }

    public void setTextZoom(int textZoom) {
        if (this.setTextZoomintMethod == null || this.setTextZoomintMethod.isNull()) {
            setTextZoomSuper(textZoom);
            return;
        }
        this.setTextZoomintMethod.invoke(Integer.valueOf(textZoom));
    }

    public void setTextZoomSuper(int textZoom) {
        if (this.internal == null) {
            super.setTextZoom(textZoom);
        } else {
            this.internal.setTextZoom(textZoom);
        }
    }

    public int getTextZoom() {
        if (this.getTextZoomMethod == null || this.getTextZoomMethod.isNull()) {
            return getTextZoomSuper();
        }
        return ((Integer) this.getTextZoomMethod.invoke(new Object[0])).intValue();
    }

    public int getTextZoomSuper() {
        if (this.internal == null) {
            return super.getTextZoom();
        }
        return this.internal.getTextZoom();
    }

    public void setDefaultFontSize(int size) {
        if (this.setDefaultFontSizeintMethod == null || this.setDefaultFontSizeintMethod.isNull()) {
            setDefaultFontSizeSuper(size);
            return;
        }
        this.setDefaultFontSizeintMethod.invoke(Integer.valueOf(size));
    }

    public void setDefaultFontSizeSuper(int size) {
        if (this.internal == null) {
            super.setDefaultFontSize(size);
        } else {
            this.internal.setDefaultFontSize(size);
        }
    }

    public int getDefaultFontSize() {
        if (this.getDefaultFontSizeMethod == null || this.getDefaultFontSizeMethod.isNull()) {
            return getDefaultFontSizeSuper();
        }
        return ((Integer) this.getDefaultFontSizeMethod.invoke(new Object[0])).intValue();
    }

    public int getDefaultFontSizeSuper() {
        if (this.internal == null) {
            return super.getDefaultFontSize();
        }
        return this.internal.getDefaultFontSize();
    }

    public void setDefaultFixedFontSize(int size) {
        if (this.setDefaultFixedFontSizeintMethod == null || this.setDefaultFixedFontSizeintMethod.isNull()) {
            setDefaultFixedFontSizeSuper(size);
            return;
        }
        this.setDefaultFixedFontSizeintMethod.invoke(Integer.valueOf(size));
    }

    public void setDefaultFixedFontSizeSuper(int size) {
        if (this.internal == null) {
            super.setDefaultFixedFontSize(size);
        } else {
            this.internal.setDefaultFixedFontSize(size);
        }
    }

    public int getDefaultFixedFontSize() {
        if (this.getDefaultFixedFontSizeMethod == null || this.getDefaultFixedFontSizeMethod.isNull()) {
            return getDefaultFixedFontSizeSuper();
        }
        return ((Integer) this.getDefaultFixedFontSizeMethod.invoke(new Object[0])).intValue();
    }

    public int getDefaultFixedFontSizeSuper() {
        if (this.internal == null) {
            return super.getDefaultFixedFontSize();
        }
        return this.internal.getDefaultFixedFontSize();
    }

    public void setSupportZoom(boolean support) {
        if (this.setSupportZoombooleanMethod == null || this.setSupportZoombooleanMethod.isNull()) {
            setSupportZoomSuper(support);
            return;
        }
        this.setSupportZoombooleanMethod.invoke(Boolean.valueOf(support));
    }

    public void setSupportZoomSuper(boolean support) {
        if (this.internal == null) {
            super.setSupportZoom(support);
        } else {
            this.internal.setSupportZoom(support);
        }
    }

    public boolean supportZoom() {
        if (this.supportZoomMethod == null || this.supportZoomMethod.isNull()) {
            return supportZoomSuper();
        }
        return ((Boolean) this.supportZoomMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean supportZoomSuper() {
        if (this.internal == null) {
            return super.supportZoom();
        }
        return this.internal.supportZoom();
    }

    public void setBuiltInZoomControls(boolean enabled) {
        if (this.setBuiltInZoomControlsbooleanMethod == null || this.setBuiltInZoomControlsbooleanMethod.isNull()) {
            setBuiltInZoomControlsSuper(enabled);
            return;
        }
        this.setBuiltInZoomControlsbooleanMethod.invoke(Boolean.valueOf(enabled));
    }

    public void setBuiltInZoomControlsSuper(boolean enabled) {
        if (this.internal == null) {
            super.setBuiltInZoomControls(enabled);
        } else {
            this.internal.setBuiltInZoomControls(enabled);
        }
    }

    public boolean getBuiltInZoomControls() {
        if (this.getBuiltInZoomControlsMethod == null || this.getBuiltInZoomControlsMethod.isNull()) {
            return getBuiltInZoomControlsSuper();
        }
        return ((Boolean) this.getBuiltInZoomControlsMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getBuiltInZoomControlsSuper() {
        if (this.internal == null) {
            return super.getBuiltInZoomControls();
        }
        return this.internal.getBuiltInZoomControls();
    }

    public boolean supportsMultiTouchZoomForTest() {
        if (this.supportsMultiTouchZoomForTestMethod == null || this.supportsMultiTouchZoomForTestMethod.isNull()) {
            return supportsMultiTouchZoomForTestSuper();
        }
        return ((Boolean) this.supportsMultiTouchZoomForTestMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean supportsMultiTouchZoomForTestSuper() {
        if (this.internal == null) {
            return super.supportsMultiTouchZoomForTest();
        }
        return this.internal.supportsMultiTouchZoomForTest();
    }

    public void setSupportSpatialNavigation(boolean enable) {
        if (this.setSupportSpatialNavigationbooleanMethod == null || this.setSupportSpatialNavigationbooleanMethod.isNull()) {
            setSupportSpatialNavigationSuper(enable);
            return;
        }
        this.setSupportSpatialNavigationbooleanMethod.invoke(Boolean.valueOf(enable));
    }

    public void setSupportSpatialNavigationSuper(boolean enable) {
        if (this.internal == null) {
            super.setSupportSpatialNavigation(enable);
        } else {
            this.internal.setSupportSpatialNavigation(enable);
        }
    }

    public boolean getSupportSpatialNavigation() {
        if (this.getSupportSpatialNavigationMethod == null || this.getSupportSpatialNavigationMethod.isNull()) {
            return getSupportSpatialNavigationSuper();
        }
        return ((Boolean) this.getSupportSpatialNavigationMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getSupportSpatialNavigationSuper() {
        if (this.internal == null) {
            return super.getSupportSpatialNavigation();
        }
        return this.internal.getSupportSpatialNavigation();
    }

    public void setSupportQuirksMode(boolean enable) {
        if (this.setSupportQuirksModebooleanMethod == null || this.setSupportQuirksModebooleanMethod.isNull()) {
            setSupportQuirksModeSuper(enable);
            return;
        }
        this.setSupportQuirksModebooleanMethod.invoke(Boolean.valueOf(enable));
    }

    public void setSupportQuirksModeSuper(boolean enable) {
        if (this.internal == null) {
            super.setSupportQuirksMode(enable);
        } else {
            this.internal.setSupportQuirksMode(enable);
        }
    }

    public boolean getSupportQuirksMode() {
        if (this.getSupportQuirksModeMethod == null || this.getSupportQuirksModeMethod.isNull()) {
            return getSupportQuirksModeSuper();
        }
        return ((Boolean) this.getSupportQuirksModeMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getSupportQuirksModeSuper() {
        if (this.internal == null) {
            return super.getSupportQuirksMode();
        }
        return this.internal.getSupportQuirksMode();
    }

    public void setLayoutAlgorithm(LayoutAlgorithmInternal la) {
        if (this.setLayoutAlgorithmLayoutAlgorithmInternalMethod == null || this.setLayoutAlgorithmLayoutAlgorithmInternalMethod.isNull()) {
            setLayoutAlgorithmSuper(la);
            return;
        }
        this.setLayoutAlgorithmLayoutAlgorithmInternalMethod.invoke(ConvertLayoutAlgorithmInternal(la));
    }

    public void setLayoutAlgorithmSuper(LayoutAlgorithmInternal la) {
        if (this.internal == null) {
            super.setLayoutAlgorithm(la);
        } else {
            this.internal.setLayoutAlgorithm(la);
        }
    }

    public LayoutAlgorithmInternal getLayoutAlgorithm() {
        if (this.getLayoutAlgorithmMethod == null || this.getLayoutAlgorithmMethod.isNull()) {
            return getLayoutAlgorithmSuper();
        }
        return (LayoutAlgorithmInternal) this.getLayoutAlgorithmMethod.invoke(new Object[0]);
    }

    public LayoutAlgorithmInternal getLayoutAlgorithmSuper() {
        LayoutAlgorithmInternal ret;
        if (this.internal == null) {
            ret = super.getLayoutAlgorithm();
        } else {
            ret = this.internal.getLayoutAlgorithm();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void setLoadWithOverviewMode(boolean overview) {
        if (this.setLoadWithOverviewModebooleanMethod == null || this.setLoadWithOverviewModebooleanMethod.isNull()) {
            setLoadWithOverviewModeSuper(overview);
            return;
        }
        this.setLoadWithOverviewModebooleanMethod.invoke(Boolean.valueOf(overview));
    }

    public void setLoadWithOverviewModeSuper(boolean overview) {
        if (this.internal == null) {
            super.setLoadWithOverviewMode(overview);
        } else {
            this.internal.setLoadWithOverviewMode(overview);
        }
    }

    public boolean getLoadWithOverviewMode() {
        if (this.getLoadWithOverviewModeMethod == null || this.getLoadWithOverviewModeMethod.isNull()) {
            return getLoadWithOverviewModeSuper();
        }
        return ((Boolean) this.getLoadWithOverviewModeMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean getLoadWithOverviewModeSuper() {
        if (this.internal == null) {
            return super.getLoadWithOverviewMode();
        }
        return this.internal.getLoadWithOverviewMode();
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            try {
                this.wrapper = new ReflectConstructor(this.coreBridge.getWrapperClass("XWalkSettings"), Object.class).newInstance(this);
                this.enumLayoutAlgorithmClassValueOfMethod.init(null, this.coreBridge.getWrapperClass("XWalkSettings$LayoutAlgorithm"), "valueOf", String.class);
                this.setCacheModeintMethod.init(this.wrapper, null, "setCacheMode", Integer.TYPE);
                this.getCacheModeMethod.init(this.wrapper, null, "getCacheMode", new Class[0]);
                this.setBlockNetworkLoadsbooleanMethod.init(this.wrapper, null, "setBlockNetworkLoads", Boolean.TYPE);
                this.getBlockNetworkLoadsMethod.init(this.wrapper, null, "getBlockNetworkLoads", new Class[0]);
                this.setAllowFileAccessbooleanMethod.init(this.wrapper, null, "setAllowFileAccess", Boolean.TYPE);
                this.getAllowFileAccessMethod.init(this.wrapper, null, "getAllowFileAccess", new Class[0]);
                this.setAllowContentAccessbooleanMethod.init(this.wrapper, null, "setAllowContentAccess", Boolean.TYPE);
                this.getAllowContentAccessMethod.init(this.wrapper, null, "getAllowContentAccess", new Class[0]);
                this.setJavaScriptEnabledbooleanMethod.init(this.wrapper, null, "setJavaScriptEnabled", Boolean.TYPE);
                this.setAllowUniversalAccessFromFileURLsbooleanMethod.init(this.wrapper, null, "setAllowUniversalAccessFromFileURLs", Boolean.TYPE);
                this.setAllowFileAccessFromFileURLsbooleanMethod.init(this.wrapper, null, "setAllowFileAccessFromFileURLs", Boolean.TYPE);
                this.setLoadsImagesAutomaticallybooleanMethod.init(this.wrapper, null, "setLoadsImagesAutomatically", Boolean.TYPE);
                this.getLoadsImagesAutomaticallyMethod.init(this.wrapper, null, "getLoadsImagesAutomatically", new Class[0]);
                this.setBlockNetworkImagebooleanMethod.init(this.wrapper, null, "setBlockNetworkImage", Boolean.TYPE);
                this.getBlockNetworkImageMethod.init(this.wrapper, null, "getBlockNetworkImage", new Class[0]);
                this.getJavaScriptEnabledMethod.init(this.wrapper, null, "getJavaScriptEnabled", new Class[0]);
                this.getAllowUniversalAccessFromFileURLsMethod.init(this.wrapper, null, "getAllowUniversalAccessFromFileURLs", new Class[0]);
                this.getAllowFileAccessFromFileURLsMethod.init(this.wrapper, null, "getAllowFileAccessFromFileURLs", new Class[0]);
                this.setJavaScriptCanOpenWindowsAutomaticallybooleanMethod.init(this.wrapper, null, "setJavaScriptCanOpenWindowsAutomatically", Boolean.TYPE);
                this.getJavaScriptCanOpenWindowsAutomaticallyMethod.init(this.wrapper, null, "getJavaScriptCanOpenWindowsAutomatically", new Class[0]);
                this.setSupportMultipleWindowsbooleanMethod.init(this.wrapper, null, "setSupportMultipleWindows", Boolean.TYPE);
                this.supportMultipleWindowsMethod.init(this.wrapper, null, "supportMultipleWindows", new Class[0]);
                this.setUseWideViewPortbooleanMethod.init(this.wrapper, null, "setUseWideViewPort", Boolean.TYPE);
                this.getUseWideViewPortMethod.init(this.wrapper, null, "getUseWideViewPort", new Class[0]);
                this.setDomStorageEnabledbooleanMethod.init(this.wrapper, null, "setDomStorageEnabled", Boolean.TYPE);
                this.getDomStorageEnabledMethod.init(this.wrapper, null, "getDomStorageEnabled", new Class[0]);
                this.setDatabaseEnabledbooleanMethod.init(this.wrapper, null, "setDatabaseEnabled", Boolean.TYPE);
                this.getDatabaseEnabledMethod.init(this.wrapper, null, "getDatabaseEnabled", new Class[0]);
                this.setMediaPlaybackRequiresUserGesturebooleanMethod.init(this.wrapper, null, "setMediaPlaybackRequiresUserGesture", Boolean.TYPE);
                this.getMediaPlaybackRequiresUserGestureMethod.init(this.wrapper, null, "getMediaPlaybackRequiresUserGesture", new Class[0]);
                this.setUserAgentStringStringMethod.init(this.wrapper, null, "setUserAgentString", String.class);
                this.getUserAgentStringMethod.init(this.wrapper, null, "getUserAgentString", new Class[0]);
                this.setAcceptLanguagesStringMethod.init(this.wrapper, null, "setAcceptLanguages", String.class);
                this.getAcceptLanguagesMethod.init(this.wrapper, null, "getAcceptLanguages", new Class[0]);
                this.setSaveFormDatabooleanMethod.init(this.wrapper, null, "setSaveFormData", Boolean.TYPE);
                this.getSaveFormDataMethod.init(this.wrapper, null, "getSaveFormData", new Class[0]);
                this.setInitialPageScalefloatMethod.init(this.wrapper, null, "setInitialPageScale", Float.TYPE);
                this.setTextZoomintMethod.init(this.wrapper, null, "setTextZoom", Integer.TYPE);
                this.getTextZoomMethod.init(this.wrapper, null, "getTextZoom", new Class[0]);
                this.setDefaultFontSizeintMethod.init(this.wrapper, null, "setDefaultFontSize", Integer.TYPE);
                this.getDefaultFontSizeMethod.init(this.wrapper, null, "getDefaultFontSize", new Class[0]);
                this.setDefaultFixedFontSizeintMethod.init(this.wrapper, null, "setDefaultFixedFontSize", Integer.TYPE);
                this.getDefaultFixedFontSizeMethod.init(this.wrapper, null, "getDefaultFixedFontSize", new Class[0]);
                this.setSupportZoombooleanMethod.init(this.wrapper, null, "setSupportZoom", Boolean.TYPE);
                this.supportZoomMethod.init(this.wrapper, null, "supportZoom", new Class[0]);
                this.setBuiltInZoomControlsbooleanMethod.init(this.wrapper, null, "setBuiltInZoomControls", Boolean.TYPE);
                this.getBuiltInZoomControlsMethod.init(this.wrapper, null, "getBuiltInZoomControls", new Class[0]);
                this.supportsMultiTouchZoomForTestMethod.init(this.wrapper, null, "supportsMultiTouchZoomForTest", new Class[0]);
                this.setSupportSpatialNavigationbooleanMethod.init(this.wrapper, null, "setSupportSpatialNavigation", Boolean.TYPE);
                this.getSupportSpatialNavigationMethod.init(this.wrapper, null, "getSupportSpatialNavigation", new Class[0]);
                this.setSupportQuirksModebooleanMethod.init(this.wrapper, null, "setSupportQuirksMode", Boolean.TYPE);
                this.getSupportQuirksModeMethod.init(this.wrapper, null, "getSupportQuirksMode", new Class[0]);
                this.setLayoutAlgorithmLayoutAlgorithmInternalMethod.init(this.wrapper, null, "setLayoutAlgorithm", this.coreBridge.getWrapperClass("XWalkSettings$LayoutAlgorithm"));
                this.getLayoutAlgorithmMethod.init(this.wrapper, null, "getLayoutAlgorithm", new Class[0]);
                this.setLoadWithOverviewModebooleanMethod.init(this.wrapper, null, "setLoadWithOverviewMode", Boolean.TYPE);
                this.getLoadWithOverviewModeMethod.init(this.wrapper, null, "getLoadWithOverviewMode", new Class[0]);
            } catch (UnsupportedOperationException e) {
            }
        }
    }
}
