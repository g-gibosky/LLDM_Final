package org.chromium.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class SecureRandomInitializer {
    private static final int NUM_RANDOM_BYTES = 16;
    private static byte[] sSeedBytes = new byte[16];

    public static void initialize(SecureRandom generator) throws IOException {
        Throwable th;
        FileInputStream fis = null;
        try {
            FileInputStream fis2 = new FileInputStream("/dev/urandom");
            try {
                if (fis2.read(sSeedBytes) != sSeedBytes.length) {
                    throw new IOException("Failed to get enough random data.");
                }
                generator.setSeed(sSeedBytes);
                if (fis2 != null) {
                    try {
                        fis2.close();
                    } catch (IOException e) {
                    }
                }
            } catch (Throwable th2) {
                th = th2;
                fis = fis2;
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (fis != null) {
                fis.close();
            }
            throw th;
        }
    }
}
