package org.xwalk.core.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.webkit.ValueCallback;
import java.util.HashSet;
import java.util.Set;
import org.chromium.base.ThreadUtils;
import org.chromium.net.GURLUtils;

public final class XWalkGeolocationPermissions {
    private static final String PREF_PREFIX = (XWalkGeolocationPermissions.class.getCanonicalName() + "%");
    private final SharedPreferences mSharedPreferences;

    public interface Callback {
        void invoke(String str, boolean z, boolean z2);
    }

    public XWalkGeolocationPermissions(SharedPreferences sharedPreferences) {
        this.mSharedPreferences = sharedPreferences;
    }

    public void allow(String origin) {
        String key = getOriginKey(origin);
        if (key != null) {
            this.mSharedPreferences.edit().putBoolean(key, true).apply();
        }
    }

    public void deny(String origin) {
        String key = getOriginKey(origin);
        if (key != null) {
            this.mSharedPreferences.edit().putBoolean(key, false).apply();
        }
    }

    public void clear(String origin) {
        String key = getOriginKey(origin);
        if (key != null) {
            this.mSharedPreferences.edit().remove(key).apply();
        }
    }

    public void clearAll() {
        Editor editor = null;
        for (String name : this.mSharedPreferences.getAll().keySet()) {
            if (name.startsWith(PREF_PREFIX)) {
                if (editor == null) {
                    editor = this.mSharedPreferences.edit();
                }
                editor.remove(name);
            }
        }
        if (editor != null) {
            editor.apply();
        }
    }

    public boolean isOriginAllowed(String origin) {
        return this.mSharedPreferences.getBoolean(getOriginKey(origin), false);
    }

    public boolean hasOrigin(String origin) {
        return this.mSharedPreferences.contains(getOriginKey(origin));
    }

    public void getAllowed(String origin, final ValueCallback<Boolean> callback) {
        final boolean finalAllowed = isOriginAllowed(origin);
        ThreadUtils.postOnUiThread(new Runnable() {
            public void run() {
                callback.onReceiveValue(Boolean.valueOf(finalAllowed));
            }
        });
    }

    public void getOrigins(final ValueCallback<Set<String>> callback) {
        final Set<String> origins = new HashSet();
        for (String name : this.mSharedPreferences.getAll().keySet()) {
            if (name.startsWith(PREF_PREFIX)) {
                origins.add(name.substring(PREF_PREFIX.length()));
            }
        }
        ThreadUtils.postOnUiThread(new Runnable() {
            public void run() {
                callback.onReceiveValue(origins);
            }
        });
    }

    private String getOriginKey(String url) {
        String origin = GURLUtils.getOrigin(url);
        if (origin.isEmpty()) {
            return null;
        }
        return PREF_PREFIX + origin;
    }
}
