package org.xwalk.core.internal;

import java.security.PrivateKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientCertLookupTable {
    private final Map<String, Cert> mCerts = new HashMap();
    private final Set<String> mDenieds = new HashSet();

    public static class Cert {
        byte[][] mCertChain;
        PrivateKey mPrivateKey;

        public Cert(PrivateKey privateKey, byte[][] certChain) {
            this.mPrivateKey = privateKey;
            byte[][] newChain = new byte[certChain.length][];
            for (int i = 0; i < certChain.length; i++) {
                newChain[i] = Arrays.copyOf(certChain[i], certChain[i].length);
            }
            this.mCertChain = newChain;
        }
    }

    public void clear() {
        this.mCerts.clear();
        this.mDenieds.clear();
    }

    public void allow(String host, int port, PrivateKey privateKey, byte[][] chain) {
        String host_and_port = hostAndPort(host, port);
        this.mCerts.put(host_and_port, new Cert(privateKey, chain));
        this.mDenieds.remove(host_and_port);
    }

    public void deny(String host, int port) {
        String host_and_port = hostAndPort(host, port);
        this.mCerts.remove(host_and_port);
        this.mDenieds.add(host_and_port);
    }

    public Cert getCertData(String host, int port) {
        return (Cert) this.mCerts.get(hostAndPort(host, port));
    }

    public boolean isDenied(String host, int port) {
        return this.mDenieds.contains(hostAndPort(host, port));
    }

    private static String hostAndPort(String host, int port) {
        return host + ":" + port;
    }
}
