package org.xwalk.core.internal;

import android.content.Context;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("xwalk")
class XWalkDevToolsServer {
    private static final String DEBUG_PERMISSION_SIFFIX = ".permission.DEBUG";
    private long mNativeDevToolsServer = 0;
    private String mSocketName = null;

    public enum Security {
        DEFAULT,
        ALLOW_DEBUG_PERMISSION,
        ALLOW_SOCKET_ACCESS
    }

    private native void nativeDestroyRemoteDebugging(long j);

    private native long nativeInitRemoteDebugging(String str);

    private native boolean nativeIsRemoteDebuggingEnabled(long j);

    private native void nativeSetRemoteDebuggingEnabled(long j, boolean z, boolean z2, boolean z3);

    public XWalkDevToolsServer(String socketName) {
        this.mNativeDevToolsServer = nativeInitRemoteDebugging(socketName);
        this.mSocketName = socketName;
    }

    public void destroy() {
        nativeDestroyRemoteDebugging(this.mNativeDevToolsServer);
        this.mNativeDevToolsServer = 0;
    }

    public boolean isRemoteDebuggingEnabled() {
        return nativeIsRemoteDebuggingEnabled(this.mNativeDevToolsServer);
    }

    public void setRemoteDebuggingEnabled(boolean enabled, Security security) {
        boolean allowDebugPermission;
        boolean allowSocketAccess;
        if (security == Security.ALLOW_DEBUG_PERMISSION) {
            allowDebugPermission = true;
        } else {
            allowDebugPermission = false;
        }
        if (security == Security.ALLOW_SOCKET_ACCESS) {
            allowSocketAccess = true;
        } else {
            allowSocketAccess = false;
        }
        nativeSetRemoteDebuggingEnabled(this.mNativeDevToolsServer, enabled, allowDebugPermission, allowSocketAccess);
    }

    public void setRemoteDebuggingEnabled(boolean enabled) {
        setRemoteDebuggingEnabled(enabled, Security.DEFAULT);
    }

    public String getSocketName() {
        return this.mSocketName;
    }

    @CalledByNative
    private static boolean checkDebugPermission(Context context, int pid, int uid) {
        return context.checkPermission(new StringBuilder().append(context.getPackageName()).append(DEBUG_PERMISSION_SIFFIX).toString(), pid, uid) == 0;
    }
}
