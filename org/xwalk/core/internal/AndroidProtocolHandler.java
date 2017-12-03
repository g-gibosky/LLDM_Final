package org.xwalk.core.internal;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;
import android.util.TypedValue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.List;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("xwalk")
class AndroidProtocolHandler {
    static final /* synthetic */ boolean $assertionsDisabled = (!AndroidProtocolHandler.class.desiredAssertionStatus());
    public static final String APP_SCHEME = "app";
    private static final String APP_SRC = "www";
    private static final String CONTENT_SCHEME = "content";
    public static final String FILE_SCHEME = "file";
    private static final String SCHEME_SEPARATOR = "//";
    private static final String TAG = "AndroidProtocolHandler";

    private static native String nativeGetAndroidAssetPath();

    private static native String nativeGetAndroidResourcePath();

    private static native void nativeSetResourceContextForTesting(Context context);

    AndroidProtocolHandler() {
    }

    @CalledByNative
    public static InputStream open(Context context, String url) {
        Uri uri = verifyUrl(url);
        if (uri == null) {
            return null;
        }
        try {
            String path = uri.getPath();
            if (uri.getScheme().equals(FILE_SCHEME)) {
                if (path.startsWith(nativeGetAndroidAssetPath())) {
                    return openAsset(context, uri);
                }
                if (path.startsWith(nativeGetAndroidResourcePath())) {
                    return openResource(context, uri);
                }
                return null;
            } else if (uri.getScheme().equals(CONTENT_SCHEME)) {
                return openContent(context, uri);
            } else {
                if (uri.getScheme().equals(APP_SCHEME) && uri.getHost().equals(context.getPackageName().toLowerCase()) && path.length() > 1) {
                    return openAsset(context, appUriToFileUri(uri));
                }
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening inputstream: " + url);
            return null;
        }
    }

    public static String getAssetPath(Uri uri) {
        if (!$assertionsDisabled && !uri.getScheme().equals(FILE_SCHEME)) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && uri.getPath() == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || uri.getPath().startsWith(nativeGetAndroidAssetPath())) {
            return new File(uri.getPath()).getAbsolutePath().replaceFirst(nativeGetAndroidAssetPath(), "");
        } else {
            throw new AssertionError();
        }
    }

    public static Uri appUriToFileUri(Uri uri) {
        Uri uri2 = null;
        if (!$assertionsDisabled && !uri.getScheme().equals(APP_SCHEME)) {
            throw new AssertionError();
        } else if ($assertionsDisabled || uri.getPath() != null) {
            try {
                uri2 = Uri.parse(new URI(FILE_SCHEME, SCHEME_SEPARATOR + nativeGetAndroidAssetPath() + APP_SRC + uri.getPath(), null).normalize().toString());
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to convert app URI to file URI: " + uri, e);
            }
            return uri2;
        } else {
            throw new AssertionError();
        }
    }

    static String getUrlContent(Context context, String url) throws IOException {
        InputStream stream = open(context, url);
        if (stream == null) {
            throw new RuntimeException("Failed to open the url: " + url);
        }
        String content = "";
        try {
            byte[] buffer = new byte[1024];
            while (true) {
                int actualSize = stream.read(buffer, 0, 1024);
                if (actualSize <= 0) {
                    break;
                }
                content = content + new String(buffer, 0, actualSize);
            }
            return content;
        } finally {
            stream.close();
        }
    }

    private static int getFieldId(Context context, String assetType, String assetName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return context.getClassLoader().loadClass(context.getPackageName() + ".R$" + assetType).getField(assetName).getInt(null);
    }

    private static int getValueType(Context context, int fieldId) {
        TypedValue value = new TypedValue();
        context.getResources().getValue(fieldId, value, true);
        return value.type;
    }

    private static InputStream openResource(Context context, Uri uri) {
        if (!$assertionsDisabled && !uri.getScheme().equals(FILE_SCHEME)) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && uri.getPath() == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || uri.getPath().startsWith(nativeGetAndroidResourcePath())) {
            List<String> pathSegments = uri.getPathSegments();
            if (pathSegments.size() != 3) {
                Log.e(TAG, "Incorrect resource path: " + uri);
                return null;
            }
            String assetType = (String) pathSegments.get(1);
            String assetName = (String) pathSegments.get(2);
            if (("/" + ((String) pathSegments.get(0)) + "/").equals(nativeGetAndroidResourcePath())) {
                assetName = assetName.split("\\.")[0];
                try {
                    if (context.getApplicationContext() != null) {
                        context = context.getApplicationContext();
                    }
                    int fieldId = getFieldId(context, assetType, assetName);
                    if (getValueType(context, fieldId) == 3) {
                        return context.getResources().openRawResource(fieldId);
                    }
                    Log.e(TAG, "Asset not of type string: " + uri);
                    return null;
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "Unable to open resource URL: " + uri, e);
                    return null;
                } catch (NoSuchFieldException e2) {
                    Log.e(TAG, "Unable to open resource URL: " + uri, e2);
                    return null;
                } catch (IllegalAccessException e3) {
                    Log.e(TAG, "Unable to open resource URL: " + uri, e3);
                    return null;
                }
            }
            Log.e(TAG, "Resource path does not start with " + nativeGetAndroidResourcePath() + ": " + uri);
            return null;
        } else {
            throw new AssertionError();
        }
    }

    private static InputStream openAsset(Context context, Uri uri) {
        if (!$assertionsDisabled && !uri.getScheme().equals(FILE_SCHEME)) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && uri.getPath() == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || uri.getPath().startsWith(nativeGetAndroidAssetPath())) {
            try {
                return context.getAssets().open(getAssetPath(uri), 2);
            } catch (IOException e) {
                Log.e(TAG, "Unable to open asset URL: " + uri);
                return null;
            }
        } else {
            throw new AssertionError();
        }
    }

    private static InputStream openContent(Context context, Uri uri) {
        if ($assertionsDisabled || uri.getScheme().equals(CONTENT_SCHEME)) {
            try {
                return context.getContentResolver().openInputStream(stripQueryParameters(uri));
            } catch (Exception e) {
                Log.e(TAG, "Unable to open content URL: " + uri);
                return null;
            }
        }
        throw new AssertionError();
    }

    @CalledByNative
    public static String getMimeType(Context context, InputStream stream, String url) {
        Uri uri = verifyUrl(url);
        if (uri == null) {
            return null;
        }
        try {
            String path = uri.getPath();
            if (uri.getScheme().equals(CONTENT_SCHEME)) {
                return context.getContentResolver().getType(uri);
            }
            if (uri.getScheme().equals(APP_SCHEME) || (uri.getScheme().equals(FILE_SCHEME) && path.startsWith(nativeGetAndroidAssetPath()))) {
                String mimeType = URLConnection.guessContentTypeFromName(path);
                if (mimeType != null) {
                    return mimeType;
                }
            }
            try {
                return URLConnection.guessContentTypeFromStream(stream);
            } catch (IOException e) {
                return null;
            }
        } catch (Exception e2) {
            Log.e(TAG, "Unable to get mime type" + url);
            return null;
        }
    }

    @CalledByNative
    public static String getPackageName(Context context) {
        try {
            return context.getPackageName();
        } catch (Exception e) {
            Log.e(TAG, "Unable to get package name");
            return null;
        }
    }

    private static Uri verifyUrl(String url) {
        if (url == null) {
            return null;
        }
        Uri uri = Uri.parse(url);
        if (uri == null) {
            Log.e(TAG, "Malformed URL: " + url);
            return null;
        }
        String path = uri.getPath();
        if (path != null && path.length() != 0) {
            return uri;
        }
        Log.e(TAG, "URL does not have a path: " + url);
        return null;
    }

    private static Uri stripQueryParameters(Uri uri) {
        if (!$assertionsDisabled && uri.getAuthority() == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || uri.getPath() != null) {
            Builder builder = new Builder();
            builder.scheme(uri.getScheme());
            builder.encodedAuthority(uri.getAuthority());
            builder.encodedPath(uri.getPath());
            return builder.build();
        } else {
            throw new AssertionError();
        }
    }

    public static void setResourceContextForTesting(Context context) {
        nativeSetResourceContextForTesting(context);
    }
}
