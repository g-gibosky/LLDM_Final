package org.chromium.base.library_loader;

import org.chromium.base.annotations.SuppressFBWarnings;

@SuppressFBWarnings
public class NativeLibraries {
    public static final String[] LIBRARIES = new String[]{"xwalkcore", "xwalkdummy"};
    public static boolean sEnableLinkerTests = false;
    public static boolean sUseLibraryInZipFile = false;
    public static boolean sUseLinker = false;
    static String sVersionNumber = "";
}
