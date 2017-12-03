package org.chromium.ui.base;

import org.chromium.ui.base.WindowAndroid.PermissionCallback;

public interface AndroidPermissionDelegate {
    boolean canRequestPermission(String str);

    boolean hasPermission(String str);

    boolean isPermissionRevokedByPolicy(String str);

    void requestPermissions(String[] strArr, PermissionCallback permissionCallback);
}
