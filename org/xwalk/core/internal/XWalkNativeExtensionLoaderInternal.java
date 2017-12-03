package org.xwalk.core.internal;

import org.xwalk.core.internal.extensions.XWalkNativeExtensionLoaderAndroid;

@XWalkAPI
public class XWalkNativeExtensionLoaderInternal extends XWalkNativeExtensionLoaderAndroid {
    @XWalkAPI
    public void registerNativeExtensionsInPath(String path) {
        super.registerNativeExtensionsInPath(path);
    }
}
