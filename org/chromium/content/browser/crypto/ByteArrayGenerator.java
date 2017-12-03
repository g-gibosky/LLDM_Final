package org.chromium.content.browser.crypto;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class ByteArrayGenerator {
    public byte[] getBytes(int numBytes) throws IOException, GeneralSecurityException {
        Throwable th;
        FileInputStream fis = null;
        try {
            FileInputStream fis2 = new FileInputStream("/dev/urandom");
            try {
                byte[] bytes = new byte[numBytes];
                if (bytes.length != fis2.read(bytes)) {
                    throw new GeneralSecurityException("Not enough random data available");
                }
                if (fis2 != null) {
                    fis2.close();
                }
                return bytes;
            } catch (Throwable th2) {
                th = th2;
                fis = fis2;
                if (fis != null) {
                    fis.close();
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
