package org.chromium.media;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.DeniedByServerException;
import android.media.MediaCrypto;
import android.media.MediaCryptoException;
import android.media.MediaDrm;
import android.media.MediaDrm.KeyRequest;
import android.media.MediaDrm.MediaDrmStateException;
import android.media.MediaDrm.OnEventListener;
import android.media.MediaDrm.OnExpirationUpdateListener;
import android.media.MediaDrm.OnKeyStatusChangeListener;
import android.media.MediaDrm.ProvisionRequest;
import android.media.MediaDrmException;
import android.media.NotProvisionedException;
import android.media.UnsupportedSchemeException;
import android.os.Build.VERSION;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.MainDex;

@SuppressLint({"WrongConstant"})
@TargetApi(19)
@JNINamespace("media")
public class MediaDrmBridge {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final byte[] DUMMY_KEY_ID = new byte[]{(byte) 0};
    private static final String ENABLE = "enable";
    private static final char[] HEX_CHAR_LOOKUP = "0123456789ABCDEF".toCharArray();
    private static final long INVALID_NATIVE_MEDIA_DRM_BRIDGE = 0;
    private static final String PRIVACY_MODE = "privacyMode";
    private static final String SECURITY_LEVEL = "securityLevel";
    private static final String SERVER_CERTIFICATE = "serviceCertificate";
    private static final String SESSION_SHARING = "sessionSharing";
    private static final String TAG = "cr_media";
    private static final UUID WIDEVINE_UUID = UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed");
    private byte[] mMediaCryptoSession;
    private MediaDrm mMediaDrm;
    private long mNativeMediaDrmBridge;
    private ArrayDeque<PendingCreateSessionData> mPendingCreateSessionDataQueue;
    private boolean mProvisioningPending;
    private boolean mResetDeviceCredentialsPending;
    private UUID mSchemeUUID;
    private HashMap<ByteBuffer, String> mSessionIds;

    @MainDex
    private class EventListener implements OnEventListener {
        static final /* synthetic */ boolean $assertionsDisabled = (!MediaDrmBridge.class.desiredAssertionStatus());

        private EventListener() {
        }

        public void onEvent(MediaDrm mediaDrm, byte[] sessionId, int event, int extra, byte[] data) {
            if (sessionId == null) {
                Log.m28e(MediaDrmBridge.TAG, "EventListener: Null session.", new Object[0]);
            } else if (MediaDrmBridge.this.sessionExists(sessionId)) {
                switch (event) {
                    case 2:
                        Log.m20d(MediaDrmBridge.TAG, "MediaDrm.EVENT_KEY_REQUIRED");
                        if (!MediaDrmBridge.this.mProvisioningPending) {
                            try {
                                KeyRequest request = MediaDrmBridge.this.getKeyRequest(sessionId, data, (String) MediaDrmBridge.this.mSessionIds.get(ByteBuffer.wrap(sessionId)), null);
                                if (request != null) {
                                    MediaDrmBridge.this.onSessionMessage(sessionId, request);
                                    return;
                                }
                                MediaDrmBridge.this.onLegacySessionError(sessionId, "MediaDrm EVENT_KEY_REQUIRED: Failed to generate request.");
                                if (VERSION.SDK_INT < 23) {
                                    MediaDrmBridge.this.onSessionKeysChange(sessionId, MediaDrmBridge.getDummyKeysInfo(4).toArray(), false);
                                }
                                Log.m28e(MediaDrmBridge.TAG, "EventListener: getKeyRequest failed.", new Object[0]);
                                return;
                            } catch (NotProvisionedException e) {
                                Log.m28e(MediaDrmBridge.TAG, "Device not provisioned", e);
                                MediaDrmBridge.this.startProvisioning();
                                return;
                            }
                        }
                        return;
                    case 3:
                        Log.m20d(MediaDrmBridge.TAG, "MediaDrm.EVENT_KEY_EXPIRED");
                        MediaDrmBridge.this.onLegacySessionError(sessionId, "MediaDrm EVENT_KEY_EXPIRED.");
                        if (VERSION.SDK_INT < 23) {
                            MediaDrmBridge.this.onSessionKeysChange(sessionId, MediaDrmBridge.getDummyKeysInfo(1).toArray(), false);
                            return;
                        }
                        return;
                    case 4:
                        Log.m20d(MediaDrmBridge.TAG, "MediaDrm.EVENT_VENDOR_DEFINED");
                        if (!$assertionsDisabled) {
                            throw new AssertionError();
                        }
                        return;
                    default:
                        Log.m28e(MediaDrmBridge.TAG, "Invalid DRM event " + event, new Object[0]);
                        return;
                }
            } else {
                Log.m28e(MediaDrmBridge.TAG, "EventListener: Invalid session %s", MediaDrmBridge.bytesToHexString(sessionId));
            }
        }
    }

    @MainDex
    @TargetApi(23)
    private class ExpirationUpdateListener implements OnExpirationUpdateListener {
        private ExpirationUpdateListener() {
        }

        public void onExpirationUpdate(MediaDrm md, byte[] sessionId, long expirationTime) {
            Log.m20d(MediaDrmBridge.TAG, "ExpirationUpdate: " + MediaDrmBridge.bytesToHexString(sessionId) + ", " + expirationTime);
            MediaDrmBridge.this.onSessionExpirationUpdate(sessionId, expirationTime);
        }
    }

    @MainDex
    private static class KeyStatus {
        private final byte[] mKeyId;
        private final int mStatusCode;

        private KeyStatus(byte[] keyId, int statusCode) {
            this.mKeyId = keyId;
            this.mStatusCode = statusCode;
        }

        @CalledByNative("KeyStatus")
        private byte[] getKeyId() {
            return this.mKeyId;
        }

        @CalledByNative("KeyStatus")
        private int getStatusCode() {
            return this.mStatusCode;
        }
    }

    @MainDex
    @TargetApi(23)
    private class KeyStatusChangeListener implements OnKeyStatusChangeListener {
        private KeyStatusChangeListener() {
        }

        private List<KeyStatus> getKeysInfo(List<android.media.MediaDrm.KeyStatus> keyInformation) {
            List<KeyStatus> keysInfo = new ArrayList();
            for (android.media.MediaDrm.KeyStatus keyStatus : keyInformation) {
                keysInfo.add(new KeyStatus(keyStatus.getKeyId(), keyStatus.getStatusCode()));
            }
            return keysInfo;
        }

        public void onKeyStatusChange(MediaDrm md, byte[] sessionId, List<android.media.MediaDrm.KeyStatus> keyInformation, boolean hasNewUsableKey) {
            Log.m20d(MediaDrmBridge.TAG, "KeysStatusChange: " + MediaDrmBridge.bytesToHexString(sessionId) + ", " + hasNewUsableKey);
            MediaDrmBridge.this.onSessionKeysChange(sessionId, getKeysInfo(keyInformation).toArray(), hasNewUsableKey);
        }
    }

    @MainDex
    private static class PendingCreateSessionData {
        private final byte[] mInitData;
        private final String mMimeType;
        private final HashMap<String, String> mOptionalParameters;
        private final long mPromiseId;

        private PendingCreateSessionData(byte[] initData, String mimeType, HashMap<String, String> optionalParameters, long promiseId) {
            this.mInitData = initData;
            this.mMimeType = mimeType;
            this.mOptionalParameters = optionalParameters;
            this.mPromiseId = promiseId;
        }

        private byte[] initData() {
            return this.mInitData;
        }

        private String mimeType() {
            return this.mMimeType;
        }

        private HashMap<String, String> optionalParameters() {
            return this.mOptionalParameters;
        }

        private long promiseId() {
            return this.mPromiseId;
        }
    }

    private native void nativeOnLegacySessionError(long j, byte[] bArr, String str);

    private native void nativeOnMediaCryptoReady(long j, MediaCrypto mediaCrypto);

    private native void nativeOnPromiseRejected(long j, long j2, String str);

    private native void nativeOnPromiseResolved(long j, long j2);

    private native void nativeOnPromiseResolvedWithSession(long j, long j2, byte[] bArr);

    private native void nativeOnResetDeviceCredentialsCompleted(long j, boolean z);

    private native void nativeOnSessionClosed(long j, byte[] bArr);

    private native void nativeOnSessionExpirationUpdate(long j, byte[] bArr, long j2);

    private native void nativeOnSessionKeysChange(long j, byte[] bArr, Object[] objArr, boolean z);

    private native void nativeOnSessionMessage(long j, byte[] bArr, int i, byte[] bArr2, String str);

    private native void nativeOnStartProvisioning(long j, String str, byte[] bArr);

    static {
        boolean z;
        if (MediaDrmBridge.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
    }

    private static List<KeyStatus> getDummyKeysInfo(int statusCode) {
        List<KeyStatus> keysInfo = new ArrayList();
        keysInfo.add(new KeyStatus(DUMMY_KEY_ID, statusCode));
        return keysInfo;
    }

    private static UUID getUUIDFromBytes(byte[] data) {
        if (data.length != 16) {
            return null;
        }
        int i;
        long mostSigBits = 0;
        long leastSigBits = 0;
        for (i = 0; i < 8; i++) {
            mostSigBits = (mostSigBits << 8) | ((long) (data[i] & 255));
        }
        for (i = 8; i < 16; i++) {
            leastSigBits = (leastSigBits << 8) | ((long) (data[i] & 255));
        }
        return new UUID(mostSigBits, leastSigBits);
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            hexString.append(HEX_CHAR_LOOKUP[bytes[i] >>> 4]);
            hexString.append(HEX_CHAR_LOOKUP[bytes[i] & 15]);
        }
        return hexString.toString();
    }

    private boolean isNativeMediaDrmBridgeValid() {
        return this.mNativeMediaDrmBridge != 0;
    }

    private boolean isWidevine() {
        return this.mSchemeUUID.equals(WIDEVINE_UUID);
    }

    @TargetApi(23)
    private MediaDrmBridge(UUID schemeUUID, long nativeMediaDrmBridge) throws UnsupportedSchemeException {
        this.mSchemeUUID = schemeUUID;
        this.mMediaDrm = new MediaDrm(schemeUUID);
        this.mNativeMediaDrmBridge = nativeMediaDrmBridge;
        if ($assertionsDisabled || isNativeMediaDrmBridgeValid()) {
            this.mSessionIds = new HashMap();
            this.mPendingCreateSessionDataQueue = new ArrayDeque();
            this.mResetDeviceCredentialsPending = false;
            this.mProvisioningPending = false;
            this.mMediaDrm.setOnEventListener(new EventListener());
            if (VERSION.SDK_INT >= 23) {
                this.mMediaDrm.setOnExpirationUpdateListener(new ExpirationUpdateListener(), null);
                this.mMediaDrm.setOnKeyStatusChangeListener(new KeyStatusChangeListener(), null);
            }
            if (isWidevine()) {
                this.mMediaDrm.setPropertyString(PRIVACY_MODE, ENABLE);
                this.mMediaDrm.setPropertyString(SESSION_SHARING, ENABLE);
                return;
            }
            return;
        }
        throw new AssertionError();
    }

    private boolean createMediaCrypto() {
        if (!$assertionsDisabled && this.mMediaDrm == null) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && this.mProvisioningPending) {
            throw new AssertionError();
        } else if ($assertionsDisabled || this.mMediaCryptoSession == null) {
            try {
                this.mMediaCryptoSession = openSession();
                if (this.mMediaCryptoSession == null) {
                    Log.m28e(TAG, "Cannot create MediaCrypto Session.", new Object[0]);
                    return false;
                }
                Log.m21d(TAG, "MediaCrypto Session created: %s", bytesToHexString(this.mMediaCryptoSession));
                try {
                    if (MediaCrypto.isCryptoSchemeSupported(this.mSchemeUUID)) {
                        MediaCrypto mediaCrypto = new MediaCrypto(this.mSchemeUUID, this.mMediaCryptoSession);
                        Log.m20d(TAG, "MediaCrypto successfully created!");
                        onMediaCryptoReady(mediaCrypto);
                        return true;
                    }
                    Log.m28e(TAG, "Cannot create MediaCrypto for unsupported scheme.", new Object[0]);
                    try {
                        this.mMediaDrm.closeSession(this.mMediaCryptoSession);
                    } catch (Exception e) {
                        Log.m28e(TAG, "closeSession failed: ", e);
                    }
                    this.mMediaCryptoSession = null;
                    return false;
                } catch (MediaCryptoException e2) {
                    Log.m28e(TAG, "Cannot create MediaCrypto", e2);
                }
            } catch (NotProvisionedException e3) {
                Log.m21d(TAG, "Device not provisioned", e3);
                startProvisioning();
                return true;
            }
        } else {
            throw new AssertionError();
        }
    }

    private byte[] openSession() throws NotProvisionedException {
        if ($assertionsDisabled || this.mMediaDrm != null) {
            try {
                return (byte[]) this.mMediaDrm.openSession().clone();
            } catch (RuntimeException e) {
                Log.m28e(TAG, "Cannot open a new session", e);
                release();
                return null;
            } catch (NotProvisionedException e2) {
                throw e2;
            } catch (MediaDrmException e3) {
                Log.m28e(TAG, "Cannot open a new session", e3);
                release();
                return null;
            }
        }
        throw new AssertionError();
    }

    @CalledByNative
    private static boolean isCryptoSchemeSupported(byte[] schemeUUID, String containerMimeType) {
        UUID cryptoScheme = getUUIDFromBytes(schemeUUID);
        if (containerMimeType.isEmpty()) {
            return MediaDrm.isCryptoSchemeSupported(cryptoScheme);
        }
        return MediaDrm.isCryptoSchemeSupported(cryptoScheme, containerMimeType);
    }

    @CalledByNative
    private static MediaDrmBridge create(byte[] schemeUUID, String securityLevel, long nativeMediaDrmBridge) {
        UnsupportedSchemeException e;
        MediaDrmBridge mediaDrmBridge;
        IllegalArgumentException e2;
        IllegalStateException e3;
        UUID cryptoScheme = getUUIDFromBytes(schemeUUID);
        if (cryptoScheme == null || !MediaDrm.isCryptoSchemeSupported(cryptoScheme)) {
            return null;
        }
        try {
            MediaDrmBridge mediaDrmBridge2 = new MediaDrmBridge(cryptoScheme, nativeMediaDrmBridge);
            try {
                Log.m20d(TAG, "MediaDrmBridge successfully created.");
                if (!securityLevel.isEmpty() && !mediaDrmBridge2.setSecurityLevel(securityLevel)) {
                    return null;
                }
                if (mediaDrmBridge2.createMediaCrypto()) {
                    return mediaDrmBridge2;
                }
                return null;
            } catch (UnsupportedSchemeException e4) {
                e = e4;
                mediaDrmBridge = mediaDrmBridge2;
                Log.m28e(TAG, "Unsupported DRM scheme", e);
                return null;
            } catch (IllegalArgumentException e5) {
                e2 = e5;
                mediaDrmBridge = mediaDrmBridge2;
                Log.m28e(TAG, "Failed to create MediaDrmBridge", e2);
                return null;
            } catch (IllegalStateException e6) {
                e3 = e6;
                mediaDrmBridge = mediaDrmBridge2;
                Log.m28e(TAG, "Failed to create MediaDrmBridge", e3);
                return null;
            }
        } catch (UnsupportedSchemeException e7) {
            e = e7;
            Log.m28e(TAG, "Unsupported DRM scheme", e);
            return null;
        } catch (IllegalArgumentException e8) {
            e2 = e8;
            Log.m28e(TAG, "Failed to create MediaDrmBridge", e2);
            return null;
        } catch (IllegalStateException e9) {
            e3 = e9;
            Log.m28e(TAG, "Failed to create MediaDrmBridge", e3);
            return null;
        }
    }

    private boolean setSecurityLevel(String securityLevel) {
        if (!isWidevine()) {
            Log.m20d(TAG, "Security level is not supported.");
            return true;
        } else if (!$assertionsDisabled && this.mMediaDrm == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || !securityLevel.isEmpty()) {
            Log.m28e(TAG, "Security level: current %s, new %s", this.mMediaDrm.getPropertyString(SECURITY_LEVEL), securityLevel);
            if (securityLevel.equals(this.mMediaDrm.getPropertyString(SECURITY_LEVEL))) {
                return true;
            }
            try {
                this.mMediaDrm.setPropertyString(SECURITY_LEVEL, securityLevel);
                return true;
            } catch (IllegalArgumentException e) {
                Log.m28e(TAG, "Failed to set security level %s", securityLevel, e);
                Log.m28e(TAG, "Security level %s not supported!", securityLevel);
                return false;
            } catch (IllegalStateException e2) {
                Log.m28e(TAG, "Failed to set security level %s", securityLevel, e2);
                Log.m28e(TAG, "Security level %s not supported!", securityLevel);
                return false;
            }
        } else {
            throw new AssertionError();
        }
    }

    @CalledByNative
    private boolean setServerCertificate(byte[] certificate) {
        if (isWidevine()) {
            try {
                this.mMediaDrm.setPropertyByteArray(SERVER_CERTIFICATE, certificate);
                return true;
            } catch (IllegalArgumentException e) {
                Log.m28e(TAG, "Failed to set server certificate", e);
                return false;
            } catch (IllegalStateException e2) {
                Log.m28e(TAG, "Failed to set server certificate", e2);
                return false;
            }
        }
        Log.m20d(TAG, "Setting server certificate is not supported.");
        return true;
    }

    @CalledByNative
    private void resetDeviceCredentials() {
        if (this.mMediaDrm == null) {
            onResetDeviceCredentialsCompleted(false);
            return;
        }
        this.mResetDeviceCredentialsPending = true;
        startProvisioning();
    }

    @CalledByNative
    private void destroy() {
        this.mNativeMediaDrmBridge = 0;
        if (this.mMediaDrm != null) {
            release();
        }
    }

    private void release() {
        if ($assertionsDisabled || this.mMediaDrm != null) {
            Iterator i$ = this.mPendingCreateSessionDataQueue.iterator();
            while (i$.hasNext()) {
                onPromiseRejected(((PendingCreateSessionData) i$.next()).promiseId(), "Create session aborted.");
            }
            this.mPendingCreateSessionDataQueue.clear();
            this.mPendingCreateSessionDataQueue = null;
            for (ByteBuffer sessionId : this.mSessionIds.keySet()) {
                try {
                    this.mMediaDrm.removeKeys(sessionId.array());
                } catch (Exception e) {
                    Log.m28e(TAG, "removeKeys failed: ", e);
                }
                try {
                    this.mMediaDrm.closeSession(sessionId.array());
                } catch (Exception e2) {
                    Log.m28e(TAG, "closeSession failed: ", e2);
                }
                onSessionClosed(sessionId.array());
            }
            this.mSessionIds.clear();
            this.mSessionIds = null;
            if (this.mMediaCryptoSession == null) {
                onMediaCryptoReady(null);
            } else {
                try {
                    this.mMediaDrm.closeSession(this.mMediaCryptoSession);
                } catch (Exception e22) {
                    Log.m28e(TAG, "closeSession failed: ", e22);
                }
                this.mMediaCryptoSession = null;
            }
            if (this.mResetDeviceCredentialsPending) {
                this.mResetDeviceCredentialsPending = false;
                onResetDeviceCredentialsCompleted(false);
            }
            if (this.mMediaDrm != null) {
                this.mMediaDrm.release();
                this.mMediaDrm = null;
                return;
            }
            return;
        }
        throw new AssertionError();
    }

    private KeyRequest getKeyRequest(byte[] sessionId, byte[] data, String mime, HashMap<String, String> optionalParameters) throws NotProvisionedException {
        if (!$assertionsDisabled && this.mMediaDrm == null) {
            throw new AssertionError();
        } else if (!$assertionsDisabled && this.mMediaCryptoSession == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || !this.mProvisioningPending) {
            if (optionalParameters == null) {
                optionalParameters = new HashMap();
            }
            KeyRequest request = null;
            try {
                request = this.mMediaDrm.getKeyRequest(sessionId, data, mime, 1, optionalParameters);
            } catch (IllegalStateException e) {
                if (VERSION.SDK_INT >= 21 && (e instanceof MediaDrmStateException)) {
                    Log.m28e(TAG, "MediaDrmStateException fired during getKeyRequest().", e);
                }
            }
            Log.m21d(TAG, "getKeyRequest %s!", request != null ? "successed" : "failed");
            return request;
        } else {
            throw new AssertionError();
        }
    }

    private void savePendingCreateSessionData(byte[] initData, String mime, HashMap<String, String> optionalParameters, long promiseId) {
        Log.m20d(TAG, "savePendingCreateSessionData()");
        this.mPendingCreateSessionDataQueue.offer(new PendingCreateSessionData(initData, mime, optionalParameters, promiseId));
    }

    private void processPendingCreateSessionData() {
        Log.m20d(TAG, "processPendingCreateSessionData()");
        if ($assertionsDisabled || this.mMediaDrm != null) {
            while (this.mMediaDrm != null && !this.mProvisioningPending && !this.mPendingCreateSessionDataQueue.isEmpty()) {
                PendingCreateSessionData pendingData = (PendingCreateSessionData) this.mPendingCreateSessionDataQueue.poll();
                createSession(pendingData.initData(), pendingData.mimeType(), pendingData.optionalParameters(), pendingData.promiseId());
            }
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private void createSessionFromNative(byte[] initData, String mime, String[] optionalParamsArray, long promiseId) {
        HashMap<String, String> optionalParameters = new HashMap();
        if (optionalParamsArray != null) {
            if (optionalParamsArray.length % 2 != 0) {
                throw new IllegalArgumentException("Additional data array doesn't have equal keys/values");
            }
            for (int i = 0; i < optionalParamsArray.length; i += 2) {
                optionalParameters.put(optionalParamsArray[i], optionalParamsArray[i + 1]);
            }
        }
        createSession(initData, mime, optionalParameters, promiseId);
    }

    private void createSession(byte[] initData, String mime, HashMap<String, String> optionalParameters, long promiseId) {
        Log.m20d(TAG, "createSession()");
        if (this.mMediaDrm == null) {
            Log.m28e(TAG, "createSession() called when MediaDrm is null.", new Object[0]);
            onPromiseRejected(promiseId, "MediaDrm released previously.");
        } else if (this.mProvisioningPending) {
            savePendingCreateSessionData(initData, mime, optionalParameters, promiseId);
        } else if ($assertionsDisabled || this.mMediaCryptoSession != null) {
            boolean newSessionOpened = false;
            byte[] sessionId = null;
            try {
                sessionId = openSession();
                if (sessionId == null) {
                    onPromiseRejected(promiseId, "Open session failed.");
                    return;
                }
                newSessionOpened = true;
                if ($assertionsDisabled || !sessionExists(sessionId)) {
                    KeyRequest request = getKeyRequest(sessionId, initData, mime, optionalParameters);
                    if (request == null) {
                        try {
                            this.mMediaDrm.closeSession(sessionId);
                        } catch (NotProvisionedException e) {
                            Log.m28e(TAG, "closeSession failed", e);
                        }
                        onPromiseRejected(promiseId, "Generate request failed.");
                        return;
                    }
                    Log.m21d(TAG, "createSession(): Session (%s) created.", bytesToHexString(sessionId));
                    onPromiseResolvedWithSession(promiseId, sessionId);
                    onSessionMessage(sessionId, request);
                    this.mSessionIds.put(ByteBuffer.wrap(sessionId), mime);
                    return;
                }
                throw new AssertionError();
            } catch (NotProvisionedException e2) {
                Log.m28e(TAG, "Device not provisioned", e2);
                if (newSessionOpened) {
                    try {
                        this.mMediaDrm.closeSession(sessionId);
                    } catch (Exception ex) {
                        Log.m28e(TAG, "closeSession failed", ex);
                    }
                }
                savePendingCreateSessionData(initData, mime, optionalParameters, promiseId);
                startProvisioning();
            }
        } else {
            throw new AssertionError();
        }
    }

    private boolean sessionExists(byte[] sessionId) {
        if (this.mMediaCryptoSession == null) {
            if ($assertionsDisabled || this.mSessionIds.isEmpty()) {
                Log.m28e(TAG, "Session doesn't exist because media crypto session is not created.", new Object[0]);
                return false;
            }
            throw new AssertionError();
        } else if (Arrays.equals(sessionId, this.mMediaCryptoSession) || !this.mSessionIds.containsKey(ByteBuffer.wrap(sessionId))) {
            return false;
        } else {
            return true;
        }
    }

    @CalledByNative
    private void closeSession(byte[] sessionId, long promiseId) {
        Log.m20d(TAG, "closeSession()");
        if (this.mMediaDrm == null) {
            onPromiseRejected(promiseId, "closeSession() called when MediaDrm is null.");
        } else if (sessionExists(sessionId)) {
            try {
                this.mMediaDrm.removeKeys(sessionId);
            } catch (Exception e) {
                Log.m28e(TAG, "removeKeys failed: ", e);
            }
            try {
                this.mMediaDrm.closeSession(sessionId);
            } catch (Exception e2) {
                Log.m28e(TAG, "closeSession failed: ", e2);
            }
            this.mSessionIds.remove(ByteBuffer.wrap(sessionId));
            onPromiseResolved(promiseId);
            onSessionClosed(sessionId);
            Log.m21d(TAG, "Session %s closed", bytesToHexString(sessionId));
        } else {
            onPromiseRejected(promiseId, "Invalid sessionId in closeSession(): " + bytesToHexString(sessionId));
        }
    }

    @CalledByNative
    private void updateSession(byte[] sessionId, byte[] response, long promiseId) {
        Log.m20d(TAG, "updateSession()");
        if (this.mMediaDrm == null) {
            onPromiseRejected(promiseId, "updateSession() called when MediaDrm is null.");
        } else if (sessionExists(sessionId)) {
            try {
                this.mMediaDrm.provideKeyResponse(sessionId, response);
            } catch (IllegalStateException e) {
                Log.m28e(TAG, "Exception intentionally caught when calling provideKeyResponse()", e);
            }
            try {
                Log.m21d(TAG, "Key successfully added for session %s", bytesToHexString(sessionId));
                onPromiseResolved(promiseId);
                if (VERSION.SDK_INT < 23) {
                    onSessionKeysChange(sessionId, getDummyKeysInfo(0).toArray(), true);
                }
            } catch (NotProvisionedException e2) {
                Log.m28e(TAG, "failed to provide key response", e2);
                onPromiseRejected(promiseId, "Update session failed.");
                release();
            } catch (DeniedByServerException e3) {
                Log.m28e(TAG, "failed to provide key response", e3);
                onPromiseRejected(promiseId, "Update session failed.");
                release();
            }
        } else {
            onPromiseRejected(promiseId, "Invalid session in updateSession: " + bytesToHexString(sessionId));
        }
    }

    @CalledByNative
    private String getSecurityLevel() {
        if (this.mMediaDrm != null && isWidevine()) {
            return this.mMediaDrm.getPropertyString(SECURITY_LEVEL);
        }
        Log.m28e(TAG, "getSecurityLevel(): MediaDrm is null or security level is not supported.", new Object[0]);
        return null;
    }

    private void startProvisioning() {
        if (this.mProvisioningPending) {
            Log.m20d(TAG, "startProvisioning: another provisioning is in progress, returning");
            return;
        }
        Log.m20d(TAG, "startProvisioning");
        this.mProvisioningPending = true;
        if ($assertionsDisabled || this.mMediaDrm != null) {
            ProvisionRequest request = this.mMediaDrm.getProvisionRequest();
            if (isNativeMediaDrmBridgeValid()) {
                nativeOnStartProvisioning(this.mNativeMediaDrmBridge, request.getDefaultUrl(), request.getData());
                return;
            }
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private void processProvisionResponse(boolean isResponseReceived, byte[] response) {
        Log.m20d(TAG, "processProvisionResponse()");
        if (this.mMediaDrm != null) {
            if ($assertionsDisabled || this.mProvisioningPending) {
                boolean success;
                this.mProvisioningPending = false;
                if (isResponseReceived) {
                    success = provideProvisionResponse(response);
                } else {
                    success = false;
                }
                if (this.mResetDeviceCredentialsPending) {
                    onResetDeviceCredentialsCompleted(success);
                    this.mResetDeviceCredentialsPending = false;
                }
                if (!success || (this.mMediaCryptoSession == null && !createMediaCrypto())) {
                    release();
                    return;
                } else {
                    processPendingCreateSessionData();
                    return;
                }
            }
            throw new AssertionError();
        }
    }

    boolean provideProvisionResponse(byte[] response) {
        if (response == null || response.length == 0) {
            Log.m28e(TAG, "Invalid provision response.", new Object[0]);
            return false;
        }
        try {
            this.mMediaDrm.provideProvisionResponse(response);
            return true;
        } catch (DeniedByServerException e) {
            Log.m28e(TAG, "failed to provide provision response", e);
            return false;
        } catch (IllegalStateException e2) {
            Log.m28e(TAG, "failed to provide provision response", e2);
            return false;
        }
    }

    private void onMediaCryptoReady(MediaCrypto mediaCrypto) {
        if (isNativeMediaDrmBridgeValid()) {
            nativeOnMediaCryptoReady(this.mNativeMediaDrmBridge, mediaCrypto);
        }
    }

    private void onPromiseResolved(long promiseId) {
        if (isNativeMediaDrmBridgeValid()) {
            nativeOnPromiseResolved(this.mNativeMediaDrmBridge, promiseId);
        }
    }

    private void onPromiseResolvedWithSession(long promiseId, byte[] sessionId) {
        if (isNativeMediaDrmBridgeValid()) {
            nativeOnPromiseResolvedWithSession(this.mNativeMediaDrmBridge, promiseId, sessionId);
        }
    }

    private void onPromiseRejected(long promiseId, String errorMessage) {
        Log.m28e(TAG, "onPromiseRejected: %s", errorMessage);
        if (isNativeMediaDrmBridgeValid()) {
            nativeOnPromiseRejected(this.mNativeMediaDrmBridge, promiseId, errorMessage);
        }
    }

    @TargetApi(23)
    private void onSessionMessage(byte[] sessionId, KeyRequest request) {
        if (isNativeMediaDrmBridgeValid()) {
            int requestType;
            if (VERSION.SDK_INT >= 23) {
                requestType = request.getRequestType();
            } else {
                requestType = request.getDefaultUrl().isEmpty() ? 0 : 1;
            }
            nativeOnSessionMessage(this.mNativeMediaDrmBridge, sessionId, requestType, request.getData(), request.getDefaultUrl());
        }
    }

    private void onSessionClosed(byte[] sessionId) {
        if (isNativeMediaDrmBridgeValid()) {
            nativeOnSessionClosed(this.mNativeMediaDrmBridge, sessionId);
        }
    }

    private void onSessionKeysChange(byte[] sessionId, Object[] keysInfo, boolean hasAdditionalUsableKey) {
        if (isNativeMediaDrmBridgeValid()) {
            nativeOnSessionKeysChange(this.mNativeMediaDrmBridge, sessionId, keysInfo, hasAdditionalUsableKey);
        }
    }

    private void onSessionExpirationUpdate(byte[] sessionId, long expirationTime) {
        if (isNativeMediaDrmBridgeValid()) {
            nativeOnSessionExpirationUpdate(this.mNativeMediaDrmBridge, sessionId, expirationTime);
        }
    }

    private void onLegacySessionError(byte[] sessionId, String errorMessage) {
        if (isNativeMediaDrmBridgeValid()) {
            nativeOnLegacySessionError(this.mNativeMediaDrmBridge, sessionId, errorMessage);
        }
    }

    private void onResetDeviceCredentialsCompleted(boolean success) {
        if (isNativeMediaDrmBridgeValid()) {
            nativeOnResetDeviceCredentialsCompleted(this.mNativeMediaDrmBridge, success);
        }
    }
}
