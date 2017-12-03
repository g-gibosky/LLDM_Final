package org.chromium.content.browser;

import android.os.Bundle;
import org.chromium.content.common.IChildProcessCallback;
import org.chromium.content.common.IChildProcessService;

public interface ChildProcessConnection {

    public interface ConnectionCallback {
        void onConnected(int i);
    }

    public interface DeathCallback {
        void onChildProcessDied(ChildProcessConnection childProcessConnection);
    }

    void addModerateBinding();

    void addStrongBinding();

    void dropOomBindings();

    String getPackageName();

    int getPid();

    IChildProcessService getService();

    int getServiceNumber();

    boolean isInSandbox();

    boolean isInitialBindingBound();

    boolean isModerateBindingBound();

    boolean isOomProtectedOrWasWhenDied();

    boolean isStrongBindingBound();

    void removeInitialBinding();

    void removeModerateBinding();

    void removeStrongBinding();

    void setupConnection(String[] strArr, FileDescriptorInfo[] fileDescriptorInfoArr, IChildProcessCallback iChildProcessCallback, ConnectionCallback connectionCallback, Bundle bundle);

    void start(String[] strArr);

    void stop();
}
