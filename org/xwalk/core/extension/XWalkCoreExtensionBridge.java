package org.xwalk.core.extension;

import android.content.Intent;
import org.xwalk.core.XWalkExtension;

class XWalkCoreExtensionBridge extends XWalkExtension implements XWalkExternalExtensionBridge {
    private XWalkExternalExtension mExtension;

    public XWalkCoreExtensionBridge(XWalkExternalExtension extension) {
        super(extension.getExtensionName(), extension.getJsApi(), extension.getEntryPoints());
        this.mExtension = extension;
    }

    public void onMessage(int instanceID, String message) {
        this.mExtension.onMessage(instanceID, message);
    }

    public void onBinaryMessage(int instanceID, byte[] message) {
        this.mExtension.onBinaryMessage(instanceID, message);
    }

    public String onSyncMessage(int instanceID, String message) {
        return this.mExtension.onSyncMessage(instanceID, message);
    }

    public void onInstanceCreated(int instanceID) {
        this.mExtension.onInstanceCreated(instanceID);
    }

    public void onInstanceDestroyed(int instanceID) {
        this.mExtension.onInstanceDestroyed(instanceID);
    }

    public void onDestroy() {
        this.mExtension.onDestroy();
    }

    public void onResume() {
        this.mExtension.onResume();
    }

    public void onPause() {
        this.mExtension.onPause();
    }

    public void onStart() {
        this.mExtension.onStart();
    }

    public void onStop() {
        this.mExtension.onStop();
    }

    public void onNewIntent(Intent intent) {
        this.mExtension.onNewIntent(intent);
    }

    public void postMessage(int instanceId, String message) {
        super.postMessage(instanceId, message);
    }

    public void postBinaryMessage(int instanceId, byte[] message) {
        super.postBinaryMessage(instanceId, message);
    }

    public void broadcastMessage(String message) {
        super.broadcastMessage(message);
    }
}
