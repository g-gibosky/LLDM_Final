package org.xwalk.core.internal;

import android.util.Log;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.chromium.base.ThreadUtils;

@XWalkAPI(createInternally = true, impl = ClientCertRequestInternal.class)
public class ClientCertRequestHandlerInternal implements ClientCertRequestInternal {
    private static final String TAG = "ClientCertRequestHandlerInternal";
    private XWalkContentsClientBridge mContentsClient;
    private String mHost;
    private int mId;
    private boolean mIsCalled;
    private String[] mKeyTypes;
    private int mPort;
    private Principal[] mPrincipals;

    class C03292 implements Runnable {
        C03292() {
        }

        public void run() {
            ClientCertRequestHandlerInternal.this.ignoreOnUiThread();
        }
    }

    class C03303 implements Runnable {
        C03303() {
        }

        public void run() {
            ClientCertRequestHandlerInternal.this.cancelOnUiThread();
        }
    }

    ClientCertRequestHandlerInternal(XWalkContentsClientBridge contentsClient, int id, String[] keyTypes, Principal[] principals, String host, int port) {
        this.mKeyTypes = new String[0];
        this.mPrincipals = new Principal[0];
        this.mId = id;
        this.mKeyTypes = keyTypes;
        this.mPrincipals = principals;
        this.mHost = host;
        this.mPort = port;
        this.mContentsClient = contentsClient;
    }

    ClientCertRequestHandlerInternal() {
        this.mKeyTypes = new String[0];
        this.mPrincipals = new Principal[0];
        this.mId = -1;
        this.mHost = "";
        this.mPort = -1;
        this.mContentsClient = null;
    }

    @XWalkAPI
    public void proceed(final PrivateKey privateKey, final List<X509Certificate> chain) {
        ThreadUtils.runOnUiThread(new Runnable() {
            public void run() {
                X509Certificate[] chains = null;
                if (chain != null) {
                    chains = (X509Certificate[]) chain.toArray(new X509Certificate[chain.size()]);
                }
                ClientCertRequestHandlerInternal.this.proceedOnUiThread(privateKey, chains);
            }
        });
    }

    @XWalkAPI
    public void ignore() {
        ThreadUtils.runOnUiThread(new C03292());
    }

    @XWalkAPI
    public void cancel() {
        ThreadUtils.runOnUiThread(new C03303());
    }

    @XWalkAPI
    public String getHost() {
        return this.mHost;
    }

    @XWalkAPI
    public int getPort() {
        return this.mPort;
    }

    @XWalkAPI
    public String[] getKeyTypes() {
        return this.mKeyTypes;
    }

    @XWalkAPI
    public Principal[] getPrincipals() {
        return this.mPrincipals;
    }

    private void proceedOnUiThread(PrivateKey privateKey, X509Certificate[] chain) {
        checkIfCalled();
        if (privateKey == null || chain == null || chain.length == 0) {
            Log.w(TAG, "Empty client certificate chain?");
            provideResponse(null, (byte[][]) null);
            return;
        }
        byte[][] encodedChain = new byte[chain.length][];
        int i = 0;
        while (i < chain.length) {
            try {
                encodedChain[i] = chain[i].getEncoded();
                i++;
            } catch (CertificateEncodingException e) {
                Log.w(TAG, "Could not retrieve encoded certificate chain: " + e);
                provideResponse(null, (byte[][]) null);
                return;
            }
        }
        this.mContentsClient.mLookupTable.allow(this.mHost, this.mPort, privateKey, encodedChain);
        provideResponse(privateKey, encodedChain);
    }

    private void ignoreOnUiThread() {
        checkIfCalled();
        provideResponse(null, (byte[][]) null);
    }

    private void cancelOnUiThread() {
        checkIfCalled();
        this.mContentsClient.mLookupTable.deny(this.mHost, this.mPort);
        provideResponse(null, (byte[][]) null);
    }

    private void checkIfCalled() {
        if (this.mIsCalled) {
            throw new IllegalStateException("The callback was already called.");
        }
        this.mIsCalled = true;
    }

    private void provideResponse(PrivateKey privateKey, byte[][] certChain) {
        this.mContentsClient.provideClientCertificateResponse(this.mId, certChain, privateKey);
    }
}
