package org.chromium.net;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.security.KeyChain;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLConnection;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.CalledByNativeUnchecked;
import org.chromium.ui.base.PageTransition;

class AndroidNetworkLibrary {
    private static final String TAG = "AndroidNetworkLibrary";

    AndroidNetworkLibrary() {
    }

    @CalledByNative
    public static boolean storeKeyPair(Context context, byte[] publicKey, byte[] privateKey) {
        try {
            Intent intent = KeyChain.createInstallIntent();
            intent.putExtra("PKEY", privateKey);
            intent.putExtra("KEY", publicKey);
            intent.addFlags(PageTransition.CHAIN_START);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "could not store key pair: " + e);
            return false;
        }
    }

    @CalledByNative
    public static boolean storeCertificate(Context context, int certType, byte[] data) {
        try {
            Intent intent = KeyChain.createInstallIntent();
            intent.addFlags(PageTransition.CHAIN_START);
            switch (certType) {
                case 1:
                case 2:
                    intent.putExtra("CERT", data);
                    break;
                case 3:
                    intent.putExtra("PKCS12", data);
                    break;
                default:
                    Log.w(TAG, "invalid certificate type: " + certType);
                    return false;
            }
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "could not store crypto file: " + e);
            return false;
        }
    }

    @CalledByNative
    public static String getMimeTypeFromExtension(String extension) {
        return URLConnection.guessContentTypeFromName("foo." + extension);
    }

    @CalledByNative
    public static boolean haveOnlyLoopbackAddresses() {
        try {
            Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces();
            if (list == null) {
                return false;
            }
            while (list.hasMoreElements()) {
                NetworkInterface netIf = (NetworkInterface) list.nextElement();
                try {
                    if (netIf.isUp() && !netIf.isLoopback()) {
                        return false;
                    }
                } catch (SocketException e) {
                }
            }
            return true;
        } catch (Exception e2) {
            Log.w(TAG, "could not get network interfaces: " + e2);
            return false;
        }
    }

    @CalledByNative
    public static AndroidCertVerifyResult verifyServerCertificates(byte[][] certChain, String authType, String host) {
        try {
            return X509Util.verifyServerCertificates(certChain, authType, host);
        } catch (KeyStoreException e) {
            return new AndroidCertVerifyResult(-1);
        } catch (NoSuchAlgorithmException e2) {
            return new AndroidCertVerifyResult(-1);
        } catch (IllegalArgumentException e3) {
            return new AndroidCertVerifyResult(-1);
        }
    }

    @CalledByNativeUnchecked
    public static void addTestRootCertificate(byte[] rootCert) throws CertificateException, KeyStoreException, NoSuchAlgorithmException {
        X509Util.addTestRootCertificate(rootCert);
    }

    @CalledByNativeUnchecked
    public static void clearTestRootCertificates() throws NoSuchAlgorithmException, CertificateException, KeyStoreException {
        X509Util.clearTestRootCertificates();
    }

    @CalledByNative
    private static String getNetworkCountryIso(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null) {
            return "";
        }
        return telephonyManager.getNetworkCountryIso();
    }

    @CalledByNative
    private static String getNetworkOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null) {
            return "";
        }
        return telephonyManager.getNetworkOperator();
    }

    @CalledByNative
    private static String getSimOperator(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (telephonyManager == null) {
            return "";
        }
        return telephonyManager.getSimOperator();
    }

    @CalledByNative
    private static boolean getIsRoaming(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        return networkInfo.isRoaming();
    }
}
