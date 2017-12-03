package org.chromium.content.browser;

import android.content.Context;

public interface BindingManager {
    void addNewConnection(int i, ChildProcessConnection childProcessConnection);

    void clearConnection(int i);

    void determinedVisibility(int i);

    boolean isOomProtected(int i);

    void onBroughtToForeground();

    void onSentToBackground();

    void releaseAllModerateBindings();

    void setInForeground(int i, boolean z);

    void startModerateBindingManagement(Context context, int i, boolean z);
}
