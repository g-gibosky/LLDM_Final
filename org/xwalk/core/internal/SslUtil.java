package org.xwalk.core.internal;

import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.util.Log;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import org.chromium.net.NetError;
import org.chromium.net.X509Util;

class SslUtil {
    static final /* synthetic */ boolean $assertionsDisabled = (!SslUtil.class.desiredAssertionStatus());
    private static final String TAG = "SslUtil";

    SslUtil() {
    }

    public static SslError sslErrorFromNetErrorCode(int error, SslCertificate cert, String url) {
        if ($assertionsDisabled || (error >= NetError.ERR_CERT_END && error <= NetError.ERR_CERT_COMMON_NAME_INVALID)) {
            return new SslError(5, cert, url);
        }
        throw new AssertionError();
    }

    public static SslCertificate getCertificateFromDerBytes(byte[] derBytes) {
        if (derBytes == null) {
            return null;
        }
        try {
            return new SslCertificate(X509Util.createCertificateFromBytes(derBytes));
        } catch (CertificateException e) {
            Log.w(TAG, "Could not read certificate: " + e);
            return null;
        } catch (KeyStoreException e2) {
            Log.w(TAG, "Could not read certificate: " + e2);
            return null;
        } catch (NoSuchAlgorithmException e3) {
            Log.w(TAG, "Could not read certificate: " + e3);
            return null;
        }
    }

    public static boolean shouldDenyRequest(int error) {
        if ($assertionsDisabled || (error >= NetError.ERR_CERT_END && error <= NetError.ERR_CERT_COMMON_NAME_INVALID)) {
            switch (error) {
                case NetError.ERR_CERT_VALIDITY_TOO_LONG /*-213*/:
                case NetError.ERR_CERT_NAME_CONSTRAINT_VIOLATION /*-212*/:
                case NetError.ERR_CERT_WEAK_KEY /*-211*/:
                case NetError.ERR_CERT_WEAK_SIGNATURE_ALGORITHM /*-208*/:
                case NetError.ERR_CERT_INVALID /*-207*/:
                case NetError.ERR_CERT_REVOKED /*-206*/:
                case NetError.ERR_CERT_CONTAINS_ERRORS /*-203*/:
                case NetError.ERR_CERT_AUTHORITY_INVALID /*-202*/:
                case NetError.ERR_CERT_DATE_INVALID /*-201*/:
                case NetError.ERR_CERT_COMMON_NAME_INVALID /*-200*/:
                case NetError.ERR_SSL_PINNED_KEY_NOT_IN_CERT_CHAIN /*-150*/:
                case NetError.ERR_SSL_WEAK_SERVER_EPHEMERAL_DH_KEY /*-129*/:
                    return true;
                default:
                    return false;
            }
        }
        throw new AssertionError();
    }
}
