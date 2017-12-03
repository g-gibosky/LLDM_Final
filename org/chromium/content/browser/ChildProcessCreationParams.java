package org.chromium.content.browser;

import android.content.Intent;

public class ChildProcessCreationParams {
    private static final String EXTRA_LIBRARY_PROCESS_TYPE = "org.chromium.content.common.child_service_params.library_process_type";
    private static volatile ChildProcessCreationParams sChildProcessCreationParams;
    private final int mExtraBindFlags;
    private final int mLibraryProcessType;
    private final String mPackageName;

    public static void set(ChildProcessCreationParams params) {
        sChildProcessCreationParams = params;
    }

    public static ChildProcessCreationParams get() {
        return sChildProcessCreationParams;
    }

    public ChildProcessCreationParams(String packageName, int extraBindFlags, int libraryProcessType) {
        this.mPackageName = packageName;
        this.mExtraBindFlags = extraBindFlags;
        this.mLibraryProcessType = libraryProcessType;
    }

    public ChildProcessCreationParams copy() {
        return new ChildProcessCreationParams(this.mPackageName, this.mExtraBindFlags, this.mLibraryProcessType);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getExtraBindFlags() {
        return this.mExtraBindFlags;
    }

    public int getLibraryProcessType() {
        return this.mLibraryProcessType;
    }

    public int addExtraBindFlags(int bindFlags) {
        return this.mExtraBindFlags | bindFlags;
    }

    public void addIntentExtras(Intent intent) {
        intent.putExtra(EXTRA_LIBRARY_PROCESS_TYPE, this.mLibraryProcessType);
    }

    public static int getLibraryProcessType(Intent intent) {
        return intent.getIntExtra(EXTRA_LIBRARY_PROCESS_TYPE, 2);
    }
}
