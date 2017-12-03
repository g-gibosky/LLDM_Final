package org.chromium.content.browser.crypto;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.annotation.concurrent.ThreadSafe;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.chromium.base.Log;
import org.chromium.base.ObserverList;
import org.chromium.base.SecureRandomInitializer;
import org.chromium.base.ThreadUtils;

@ThreadSafe
public class CipherFactory {
    static final String BUNDLE_IV = "org.chromium.content.browser.crypto.CipherFactory.IV";
    static final String BUNDLE_KEY = "org.chromium.content.browser.crypto.CipherFactory.KEY";
    static final int NUM_BYTES = 16;
    private static final String TAG = "cr.CipherFactory";
    private CipherData mData;
    private FutureTask<CipherData> mDataGenerator;
    private final Object mDataLock;
    private final ObserverList<CipherDataObserver> mObservers;
    private ByteArrayGenerator mRandomNumberProvider;

    class C02111 implements Runnable {
        C02111() {
        }

        public void run() {
            CipherFactory.this.notifyCipherDataGenerated();
        }
    }

    class C02122 implements Callable<CipherData> {
        C02122() {
        }

        @SuppressLint({"TrulyRandom"})
        public CipherData call() {
            try {
                byte[] iv = CipherFactory.this.mRandomNumberProvider.getBytes(16);
                try {
                    SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                    SecureRandomInitializer.initialize(random);
                    KeyGenerator generator = KeyGenerator.getInstance("AES");
                    generator.init(128, random);
                    return new CipherData(generator.generateKey(), iv);
                } catch (IOException e) {
                    Log.m28e(CipherFactory.TAG, "Couldn't get generator data.", new Object[0]);
                    return null;
                } catch (GeneralSecurityException e2) {
                    Log.m28e(CipherFactory.TAG, "Couldn't get generator instances.", new Object[0]);
                    return null;
                }
            } catch (IOException e3) {
                Log.m28e(CipherFactory.TAG, "Couldn't get generator data.", new Object[0]);
                return null;
            } catch (GeneralSecurityException e4) {
                Log.m28e(CipherFactory.TAG, "Couldn't get generator data.", new Object[0]);
                return null;
            }
        }
    }

    private static class CipherData {
        public final byte[] iv;
        public final Key key;

        public CipherData(Key key, byte[] iv) {
            this.key = key;
            this.iv = iv;
        }
    }

    public interface CipherDataObserver {
        void onCipherDataGenerated();
    }

    private static class LazyHolder {
        private static CipherFactory sInstance = new CipherFactory();

        private LazyHolder() {
        }
    }

    public static CipherFactory getInstance() {
        return LazyHolder.sInstance;
    }

    public Cipher getCipher(int opmode) {
        CipherData data = getCipherData(true);
        if (data != null) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(opmode, data.key, new IvParameterSpec(data.iv));
                return cipher;
            } catch (GeneralSecurityException e) {
            }
        }
        Log.m28e(TAG, "Error in creating cipher instance.", new Object[0]);
        return null;
    }

    public boolean hasCipher() {
        boolean z;
        synchronized (this.mDataLock) {
            z = this.mData != null;
        }
        return z;
    }

    CipherData getCipherData(boolean generateIfNeeded) {
        if (this.mData == null && generateIfNeeded) {
            triggerKeyGeneration();
            try {
                CipherData data = (CipherData) this.mDataGenerator.get();
                synchronized (this.mDataLock) {
                    if (this.mData == null) {
                        this.mData = data;
                        ThreadUtils.postOnUiThread(new C02111());
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e2) {
                throw new RuntimeException(e2);
            }
        }
        return this.mData;
    }

    private Callable<CipherData> createGeneratorCallable() {
        return new C02122();
    }

    public void triggerKeyGeneration() {
        if (this.mData == null) {
            synchronized (this.mDataLock) {
                if (this.mDataGenerator == null) {
                    this.mDataGenerator = new FutureTask(createGeneratorCallable());
                    AsyncTask.THREAD_POOL_EXECUTOR.execute(this.mDataGenerator);
                }
            }
        }
    }

    public void saveToBundle(Bundle outState) {
        CipherData data = getCipherData(false);
        if (data != null) {
            byte[] wrappedKey = data.key.getEncoded();
            if (wrappedKey != null && data.iv != null) {
                outState.putByteArray(BUNDLE_KEY, wrappedKey);
                outState.putByteArray(BUNDLE_IV, data.iv);
            }
        }
    }

    public boolean restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return false;
        }
        byte[] wrappedKey = savedInstanceState.getByteArray(BUNDLE_KEY);
        byte[] iv = savedInstanceState.getByteArray(BUNDLE_IV);
        if (wrappedKey == null || iv == null) {
            return false;
        }
        try {
            Key bundledKey = new SecretKeySpec(wrappedKey, "AES");
            synchronized (this.mDataLock) {
                if (this.mData == null) {
                    this.mData = new CipherData(bundledKey, iv);
                    return true;
                } else if (this.mData.key.equals(bundledKey) && Arrays.equals(this.mData.iv, iv)) {
                    return true;
                } else {
                    Log.m28e(TAG, "Attempted to restore different cipher data.", new Object[0]);
                    return false;
                }
            }
        } catch (IllegalArgumentException e) {
            Log.m28e(TAG, "Error in restoring the key from the bundle.", new Object[0]);
            return false;
        }
    }

    void setRandomNumberProviderForTests(ByteArrayGenerator mockProvider) {
        this.mRandomNumberProvider = mockProvider;
    }

    public void addCipherDataObserver(CipherDataObserver observer) {
        this.mObservers.addObserver(observer);
    }

    public void removeCipherDataObserver(CipherDataObserver observer) {
        this.mObservers.removeObserver(observer);
    }

    private void notifyCipherDataGenerated() {
        Iterator i$ = this.mObservers.iterator();
        while (i$.hasNext()) {
            ((CipherDataObserver) i$.next()).onCipherDataGenerated();
        }
    }

    private CipherFactory() {
        this.mDataLock = new Object();
        this.mRandomNumberProvider = new ByteArrayGenerator();
        this.mObservers = new ObserverList();
    }
}
