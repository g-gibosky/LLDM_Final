package org.chromium.ui.base;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import java.io.File;
import java.util.Locale;
import org.chromium.base.ResourceExtractor.ResourceEntry;
import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.SuppressFBWarnings;

@JNINamespace("ui")
public class ResourceBundle {
    static final /* synthetic */ boolean $assertionsDisabled = (!ResourceBundle.class.desiredAssertionStatus());
    private static ResourceEntry[] sActiveLocaleResources;

    private static String toChromeLocaleName(String srcFileName) {
        srcFileName = srcFileName.replace(".lpak", ".pak");
        String[] parts = srcFileName.split("_");
        if (parts.length <= 1) {
            return srcFileName;
        }
        int dotIdx = parts[1].indexOf(46);
        return parts[0] + "-" + parts[1].substring(0, dotIdx).toUpperCase(Locale.ENGLISH) + parts[1].substring(dotIdx);
    }

    @SuppressFBWarnings({"LI_LAZY_INIT_UPDATE_STATIC"})
    public static void initializeLocalePaks(Context context, int localePaksResId) {
        ThreadUtils.assertOnUiThread();
        if ($assertionsDisabled || sActiveLocaleResources == null) {
            Resources resources = context.getResources();
            TypedArray resIds = resources.obtainTypedArray(localePaksResId);
            try {
                int len = resIds.length();
                sActiveLocaleResources = new ResourceEntry[len];
                for (int i = 0; i < len; i++) {
                    int resId = resIds.getResourceId(i, 0);
                    String resPath = resources.getString(resId);
                    sActiveLocaleResources[i] = new ResourceEntry(resId, resPath, toChromeLocaleName(new File(resPath).getName()));
                }
            } finally {
                resIds.recycle();
            }
        } else {
            throw new AssertionError();
        }
    }

    @SuppressFBWarnings({"MS_EXPOSE_REP"})
    public static ResourceEntry[] getActiveLocaleResources() {
        return sActiveLocaleResources;
    }

    @CalledByNative
    private static String getLocalePakResourcePath(String locale) {
        if (sActiveLocaleResources == null) {
            return null;
        }
        String fileName = locale + ".pak";
        for (ResourceEntry entry : sActiveLocaleResources) {
            if (fileName.equals(entry.extractedFileName)) {
                return entry.pathWithinApk;
            }
        }
        return null;
    }
}
