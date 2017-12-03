package org.chromium.net;

import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("net::android")
public class AndroidKeyStore {
    private static final String TAG = "AndroidKeyStore";

    @CalledByNative
    private static byte[] getRSAKeyModulus(PrivateKey privateKey) {
        if (privateKey instanceof RSAKey) {
            return ((RSAKey) privateKey).getModulus().toByteArray();
        }
        Log.m38w(TAG, "Not a RSAKey instance!", new Object[0]);
        return null;
    }

    @CalledByNative
    private static byte[] getECKeyOrder(PrivateKey privateKey) {
        if (privateKey instanceof ECKey) {
            return ((ECKey) privateKey).getParams().getOrder().toByteArray();
        }
        Log.m38w(TAG, "Not an ECKey instance!", new Object[0]);
        return null;
    }

    @CalledByNative
    private static byte[] rawSignDigestWithPrivateKey(PrivateKey privateKey, byte[] message) {
        byte[] bArr = null;
        Signature signature = null;
        try {
            String keyAlgorithm = privateKey.getAlgorithm();
            if ("RSA".equalsIgnoreCase(keyAlgorithm)) {
                signature = Signature.getInstance("NONEwithRSA");
            } else if ("EC".equalsIgnoreCase(keyAlgorithm)) {
                signature = Signature.getInstance("NONEwithECDSA");
            }
        } catch (NoSuchAlgorithmException e) {
        }
        if (signature == null) {
            Log.m28e(TAG, "Unsupported private key algorithm: " + privateKey.getAlgorithm(), new Object[0]);
        } else {
            try {
                signature.initSign(privateKey);
                signature.update(message);
                bArr = signature.sign();
            } catch (Exception e2) {
                Log.m28e(TAG, "Exception while signing message with " + privateKey.getAlgorithm() + " private key: " + e2, new Object[0]);
            }
        }
        return bArr;
    }

    @CalledByNative
    private static int getPrivateKeyType(PrivateKey privateKey) {
        String keyAlgorithm = privateKey.getAlgorithm();
        if ("RSA".equalsIgnoreCase(keyAlgorithm)) {
            return 0;
        }
        if ("EC".equalsIgnoreCase(keyAlgorithm)) {
            return 2;
        }
        return 255;
    }

    private static Object getOpenSSLKeyForPrivateKey(PrivateKey privateKey) {
        Method getKey;
        if (privateKey == null) {
            Log.m28e(TAG, "privateKey == null", new Object[0]);
            return null;
        } else if (privateKey instanceof RSAPrivateKey) {
            try {
                Class<?> superClass = Class.forName("org.apache.harmony.xnet.provider.jsse.OpenSSLRSAPrivateKey");
                if (superClass.isInstance(privateKey)) {
                    try {
                        getKey = superClass.getDeclaredMethod("getOpenSSLKey", new Class[0]);
                        getKey.setAccessible(true);
                        Object opensslKey = getKey.invoke(privateKey, new Object[0]);
                        getKey.setAccessible(false);
                        if (opensslKey != null) {
                            return opensslKey;
                        }
                        Log.m28e(TAG, "getOpenSSLKey() returned null", new Object[0]);
                        return null;
                    } catch (Exception e) {
                        Log.m28e(TAG, "Exception while trying to retrieve system EVP_PKEY handle: " + e, new Object[0]);
                        return null;
                    } catch (Throwable th) {
                        getKey.setAccessible(false);
                    }
                }
                Log.m28e(TAG, "Private key is not an OpenSSLRSAPrivateKey instance, its class name is:" + privateKey.getClass().getCanonicalName(), new Object[0]);
                return null;
            } catch (Exception e2) {
                Log.m28e(TAG, "Cannot find system OpenSSLRSAPrivateKey class: " + e2, new Object[0]);
                return null;
            }
        } else {
            Log.m28e(TAG, "does not implement RSAPrivateKey", new Object[0]);
            return null;
        }
    }

    @CalledByNative
    private static long getOpenSSLHandleForPrivateKey(PrivateKey privateKey) {
        Object opensslKey = getOpenSSLKeyForPrivateKey(privateKey);
        if (opensslKey == null) {
            return 0;
        }
        try {
            Method getPkeyContext = opensslKey.getClass().getDeclaredMethod("getPkeyContext", new Class[0]);
            try {
                getPkeyContext.setAccessible(true);
                long evp_pkey = ((Number) getPkeyContext.invoke(opensslKey, new Object[0])).longValue();
                getPkeyContext.setAccessible(false);
                if (evp_pkey != 0) {
                    return evp_pkey;
                }
                Log.m28e(TAG, "getPkeyContext() returned null", new Object[0]);
                return evp_pkey;
            } catch (Exception e) {
                Log.m28e(TAG, "Exception while trying to retrieve system EVP_PKEY handle: " + e, new Object[0]);
                return 0;
            } catch (Throwable th) {
                getPkeyContext.setAccessible(false);
            }
        } catch (Exception e2) {
            Log.m28e(TAG, "No getPkeyContext() method on OpenSSLKey member:" + e2, new Object[0]);
            return 0;
        }
    }

    @CalledByNative
    private static Object getOpenSSLEngineForPrivateKey(PrivateKey privateKey) {
        try {
            Class<?> engineClass = Class.forName("org.apache.harmony.xnet.provider.jsse.OpenSSLEngine");
            Object opensslKey = getOpenSSLKeyForPrivateKey(privateKey);
            if (opensslKey == null) {
                return null;
            }
            try {
                Method getEngine = opensslKey.getClass().getDeclaredMethod("getEngine", new Class[0]);
                try {
                    getEngine.setAccessible(true);
                    Object engine = getEngine.invoke(opensslKey, new Object[0]);
                    getEngine.setAccessible(false);
                    if (engine == null) {
                        Log.m28e(TAG, "getEngine() returned null", new Object[0]);
                    }
                    if (engineClass.isInstance(engine)) {
                        return engine;
                    }
                    Log.m28e(TAG, "Engine is not an OpenSSLEngine instance, its class name is:" + engine.getClass().getCanonicalName(), new Object[0]);
                    return null;
                } catch (Exception e) {
                    Log.m28e(TAG, "Exception while trying to retrieve OpenSSLEngine object: " + e, new Object[0]);
                    return null;
                } catch (Throwable th) {
                    getEngine.setAccessible(false);
                }
            } catch (Exception e2) {
                Log.m28e(TAG, "No getEngine() method on OpenSSLKey member:" + e2, new Object[0]);
                return null;
            }
        } catch (Exception e22) {
            Log.m28e(TAG, "Cannot find system OpenSSLEngine class: " + e22, new Object[0]);
            return null;
        }
    }
}
