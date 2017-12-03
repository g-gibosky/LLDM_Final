package org.xwalk.core.extension;

final class XWalkExternalExtensionBridgeFactory {
    XWalkExternalExtensionBridgeFactory() {
    }

    public static XWalkExternalExtensionBridge createInstance(XWalkExternalExtension extension) {
        return new XWalkCoreExtensionBridge(extension);
    }
}
