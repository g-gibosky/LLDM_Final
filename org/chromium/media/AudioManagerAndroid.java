package org.chromium.media;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.provider.Settings.System;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.chromium.base.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.SuppressFBWarnings;

@JNINamespace("media")
class AudioManagerAndroid implements OnAudioFocusChangeListener {
    private static final boolean DEBUG = false;
    private static final int DEFAULT_FRAME_PER_BUFFER = 256;
    private static final int DEFAULT_SAMPLING_RATE = 44100;
    private static final int DEVICE_BLUETOOTH_HEADSET = 3;
    private static final int DEVICE_COUNT = 5;
    private static final int DEVICE_DEFAULT = -2;
    private static final int DEVICE_EARPIECE = 2;
    private static final int DEVICE_INVALID = -1;
    private static final String[] DEVICE_NAMES = new String[]{"Speakerphone", "Wired headset", "Headset earpiece", "Bluetooth headset", "USB audio"};
    private static final int DEVICE_SPEAKERPHONE = 0;
    private static final int DEVICE_USB_AUDIO = 4;
    private static final int DEVICE_WIRED_HEADSET = 1;
    private static final int STATE_BLUETOOTH_SCO_INVALID = -1;
    private static final int STATE_BLUETOOTH_SCO_OFF = 0;
    private static final int STATE_BLUETOOTH_SCO_ON = 1;
    private static final int STATE_BLUETOOTH_SCO_TURNING_OFF = 3;
    private static final int STATE_BLUETOOTH_SCO_TURNING_ON = 2;
    private static final String[] SUPPORTED_AEC_MODELS = new String[]{"GT-I9300", "GT-I9500", "GT-N7105", "Nexus 4", "Nexus 5", "Nexus 7", "SM-N9005", "SM-T310"};
    private static final String TAG = "cr.media";
    private static final Integer[] VALID_DEVICES = new Integer[]{Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4)};
    private boolean[] mAudioDevices = new boolean[5];
    private final AudioManager mAudioManager;
    private BroadcastReceiver mBluetoothHeadsetReceiver;
    private BroadcastReceiver mBluetoothScoReceiver;
    private int mBluetoothScoState = -1;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private int mCurrentVolume;
    private boolean mHasBluetoothPermission = false;
    private boolean mHasModifyAudioSettingsPermission = false;
    private boolean mIsInitialized = false;
    private final Object mLock = new Object();
    private final long mNativeAudioManagerAndroid;
    private final NonThreadSafe mNonThreadSafe = new NonThreadSafe();
    private int mRequestedAudioDevice = -1;
    private int mSavedAudioMode = -2;
    private boolean mSavedIsMicrophoneMute;
    private boolean mSavedIsSpeakerphoneOn;
    private ContentObserver mSettingsObserver = null;
    private HandlerThread mSettingsObserverThread = null;
    private BroadcastReceiver mUsbAudioReceiver;
    private final UsbManager mUsbManager;
    private BroadcastReceiver mWiredHeadsetReceiver;

    class C02531 extends BroadcastReceiver {
        private static final int HAS_MIC = 1;
        private static final int HAS_NO_MIC = 0;
        private static final int STATE_PLUGGED = 1;
        private static final int STATE_UNPLUGGED = 0;

        C02531() {
        }

        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("state", 0)) {
                case 0:
                    synchronized (AudioManagerAndroid.this.mLock) {
                        AudioManagerAndroid.this.mAudioDevices[1] = false;
                        if (AudioManagerAndroid.this.hasUsbAudio()) {
                            AudioManagerAndroid.this.mAudioDevices[4] = true;
                            AudioManagerAndroid.this.mAudioDevices[2] = false;
                        } else if (AudioManagerAndroid.this.hasEarpiece()) {
                            AudioManagerAndroid.this.mAudioDevices[2] = true;
                            AudioManagerAndroid.this.mAudioDevices[4] = false;
                        }
                    }
                    break;
                case 1:
                    synchronized (AudioManagerAndroid.this.mLock) {
                        AudioManagerAndroid.this.mAudioDevices[1] = true;
                        AudioManagerAndroid.this.mAudioDevices[2] = false;
                        AudioManagerAndroid.this.mAudioDevices[4] = false;
                    }
                    break;
                default:
                    AudioManagerAndroid.loge("Invalid state");
                    break;
            }
            if (AudioManagerAndroid.this.deviceHasBeenRequested()) {
                AudioManagerAndroid.this.updateDeviceActivation();
            }
        }
    }

    class C02542 extends BroadcastReceiver {
        C02542() {
        }

        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("android.bluetooth.profile.extra.STATE", 0)) {
                case 0:
                    synchronized (AudioManagerAndroid.this.mLock) {
                        AudioManagerAndroid.this.mAudioDevices[3] = false;
                    }
                    return;
                case 1:
                case 3:
                    return;
                case 2:
                    synchronized (AudioManagerAndroid.this.mLock) {
                        AudioManagerAndroid.this.mAudioDevices[3] = true;
                    }
                    return;
                default:
                    AudioManagerAndroid.loge("Invalid state");
                    return;
            }
        }
    }

    class C02553 extends BroadcastReceiver {
        C02553() {
        }

        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", 0)) {
                case 0:
                    if (AudioManagerAndroid.this.mBluetoothScoState != 3 && AudioManagerAndroid.this.deviceHasBeenRequested()) {
                        AudioManagerAndroid.this.updateDeviceActivation();
                    }
                    AudioManagerAndroid.this.mBluetoothScoState = 0;
                    return;
                case 1:
                    AudioManagerAndroid.this.mBluetoothScoState = 1;
                    return;
                case 2:
                    return;
                default:
                    AudioManagerAndroid.loge("Invalid state");
                    return;
            }
        }
    }

    class C02575 extends BroadcastReceiver {
        C02575() {
        }

        public void onReceive(Context context, Intent intent) {
            if (AudioManagerAndroid.this.hasUsbAudioCommInterface((UsbDevice) intent.getParcelableExtra("device"))) {
                if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(intent.getAction())) {
                    synchronized (AudioManagerAndroid.this.mLock) {
                        if (!AudioManagerAndroid.this.hasWiredHeadset()) {
                            AudioManagerAndroid.this.mAudioDevices[4] = true;
                            AudioManagerAndroid.this.mAudioDevices[2] = false;
                        }
                    }
                } else if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(intent.getAction()) && !AudioManagerAndroid.this.hasUsbAudio()) {
                    synchronized (AudioManagerAndroid.this.mLock) {
                        if (!AudioManagerAndroid.this.hasWiredHeadset()) {
                            AudioManagerAndroid.this.mAudioDevices[4] = false;
                            if (AudioManagerAndroid.this.hasEarpiece()) {
                                AudioManagerAndroid.this.mAudioDevices[2] = true;
                            }
                        }
                    }
                }
                if (AudioManagerAndroid.this.deviceHasBeenRequested()) {
                    AudioManagerAndroid.this.updateDeviceActivation();
                }
            }
        }
    }

    private static class AudioDeviceName {
        private final int mId;
        private final String mName;

        private AudioDeviceName(int id, String name) {
            this.mId = id;
            this.mName = name;
        }

        @CalledByNative("AudioDeviceName")
        private String id() {
            return String.valueOf(this.mId);
        }

        @CalledByNative("AudioDeviceName")
        private String name() {
            return this.mName;
        }
    }

    private static class NonThreadSafe {
        private final Long mThreadId = Long.valueOf(0);

        public boolean calledOnValidThread() {
            return true;
        }
    }

    private native void nativeSetMute(long j, boolean z);

    @CalledByNative
    private static AudioManagerAndroid createAudioManagerAndroid(Context context, long nativeAudioManagerAndroid) {
        return new AudioManagerAndroid(context, nativeAudioManagerAndroid);
    }

    private AudioManagerAndroid(Context context, long nativeAudioManagerAndroid) {
        this.mContext = context;
        this.mNativeAudioManagerAndroid = nativeAudioManagerAndroid;
        this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
        this.mContentResolver = this.mContext.getContentResolver();
        this.mUsbManager = (UsbManager) this.mContext.getSystemService("usb");
    }

    @CalledByNative
    private void init() {
        checkIfCalledOnValidThread();
        if (!this.mIsInitialized) {
            this.mHasModifyAudioSettingsPermission = hasPermission("android.permission.MODIFY_AUDIO_SETTINGS");
            this.mAudioDevices[2] = hasEarpiece();
            this.mAudioDevices[1] = hasWiredHeadset();
            this.mAudioDevices[4] = hasUsbAudio();
            this.mAudioDevices[0] = true;
            registerBluetoothIntentsIfNeeded();
            registerForWiredHeadsetIntentBroadcast();
            registerForUsbAudioIntentBroadcast();
            this.mIsInitialized = true;
        }
    }

    @CalledByNative
    private void close() {
        checkIfCalledOnValidThread();
        if (this.mIsInitialized) {
            stopObservingVolumeChanges();
            unregisterForWiredHeadsetIntentBroadcast();
            unregisterBluetoothIntentsIfNeeded();
            unregisterForUsbAudioIntentBroadcast();
            this.mIsInitialized = false;
        }
    }

    @CalledByNative
    private void setCommunicationAudioModeOn(boolean on) {
        checkIfCalledOnValidThread();
        if (!this.mIsInitialized) {
            return;
        }
        if (this.mHasModifyAudioSettingsPermission) {
            if (on) {
                this.mAudioManager.requestAudioFocus(this, 0, 1);
                this.mSavedIsSpeakerphoneOn = this.mAudioManager.isSpeakerphoneOn();
                this.mSavedIsMicrophoneMute = this.mAudioManager.isMicrophoneMute();
                startObservingVolumeChanges();
            } else {
                this.mAudioManager.abandonAudioFocus(this);
                stopObservingVolumeChanges();
                stopBluetoothSco();
                synchronized (this.mLock) {
                    this.mRequestedAudioDevice = -1;
                }
                setMicrophoneMute(this.mSavedIsMicrophoneMute);
                setSpeakerphoneOn(this.mSavedIsSpeakerphoneOn);
            }
            setCommunicationAudioModeOnInternal(on);
            return;
        }
        Log.m38w(TAG, "MODIFY_AUDIO_SETTINGS is missing => client will run with reduced functionality", new Object[0]);
    }

    private void setCommunicationAudioModeOnInternal(boolean on) {
        if (on) {
            if (this.mSavedAudioMode != -2) {
                Log.m38w(TAG, "Audio mode has already been set", new Object[0]);
                return;
            }
            try {
                this.mSavedAudioMode = this.mAudioManager.getMode();
                try {
                    this.mAudioManager.setMode(3);
                } catch (SecurityException e) {
                    logDeviceInfo();
                    throw e;
                }
            } catch (SecurityException e2) {
                logDeviceInfo();
                throw e2;
            }
        } else if (this.mSavedAudioMode == -2) {
            Log.m38w(TAG, "Audio mode has not yet been set", new Object[0]);
        } else {
            try {
                this.mAudioManager.setMode(this.mSavedAudioMode);
                this.mSavedAudioMode = -2;
            } catch (SecurityException e22) {
                logDeviceInfo();
                throw e22;
            }
        }
    }

    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case 1:
                setCommunicationAudioModeOnInternal(true);
                return;
            default:
                setCommunicationAudioModeOnInternal(false);
                return;
        }
    }

    @CalledByNative
    private boolean setDevice(String deviceId) {
        if (!this.mIsInitialized) {
            return false;
        }
        boolean hasRecordAudioPermission = hasPermission("android.permission.RECORD_AUDIO");
        if (this.mHasModifyAudioSettingsPermission && hasRecordAudioPermission) {
            int intDeviceId = deviceId.isEmpty() ? -2 : Integer.parseInt(deviceId);
            if (intDeviceId == -2) {
                boolean[] devices;
                synchronized (this.mLock) {
                    devices = (boolean[]) this.mAudioDevices.clone();
                    this.mRequestedAudioDevice = -2;
                }
                setAudioDevice(selectDefaultDevice(devices));
                return true;
            } else if (!Arrays.asList(VALID_DEVICES).contains(Integer.valueOf(intDeviceId)) || !this.mAudioDevices[intDeviceId]) {
                return false;
            } else {
                synchronized (this.mLock) {
                    this.mRequestedAudioDevice = intDeviceId;
                }
                setAudioDevice(intDeviceId);
                return true;
            }
        }
        Log.m38w(TAG, "Requires MODIFY_AUDIO_SETTINGS and RECORD_AUDIO. Selected device will not be available for recording", new Object[0]);
        return false;
    }

    @SuppressFBWarnings({"UC_USELESS_OBJECT"})
    @CalledByNative
    private AudioDeviceName[] getAudioInputDeviceNames() {
        if (!this.mIsInitialized) {
            return null;
        }
        boolean hasRecordAudioPermission = hasPermission("android.permission.RECORD_AUDIO");
        if (this.mHasModifyAudioSettingsPermission && hasRecordAudioPermission) {
            boolean[] devices;
            synchronized (this.mLock) {
                devices = (boolean[]) this.mAudioDevices.clone();
            }
            List<String> list = new ArrayList();
            AudioDeviceName[] array = new AudioDeviceName[getNumOfAudioDevices(devices)];
            int i = 0;
            for (int id = 0; id < 5; id++) {
                if (devices[id]) {
                    array[i] = new AudioDeviceName(id, DEVICE_NAMES[id]);
                    list.add(DEVICE_NAMES[id]);
                    i++;
                }
            }
            return array;
        }
        Log.m38w(TAG, "Requires MODIFY_AUDIO_SETTINGS and RECORD_AUDIO. No audio device will be available for recording", new Object[0]);
        return null;
    }

    @TargetApi(17)
    @CalledByNative
    private int getNativeOutputSampleRate() {
        if (VERSION.SDK_INT < 17) {
            return DEFAULT_SAMPLING_RATE;
        }
        String sampleRateString = this.mAudioManager.getProperty("android.media.property.OUTPUT_SAMPLE_RATE");
        if (sampleRateString == null) {
            return DEFAULT_SAMPLING_RATE;
        }
        return Integer.parseInt(sampleRateString);
    }

    @CalledByNative
    private static int getMinInputFrameSize(int sampleRate, int channels) {
        int channelConfig;
        if (channels == 1) {
            channelConfig = 16;
        } else if (channels != 2) {
            return -1;
        } else {
            channelConfig = 12;
        }
        return (AudioRecord.getMinBufferSize(sampleRate, channelConfig, 2) / 2) / channels;
    }

    @CalledByNative
    private static int getMinOutputFrameSize(int sampleRate, int channels) {
        int channelConfig;
        if (channels == 1) {
            channelConfig = 4;
        } else if (channels != 2) {
            return -1;
        } else {
            channelConfig = 12;
        }
        return (AudioTrack.getMinBufferSize(sampleRate, channelConfig, 2) / 2) / channels;
    }

    @CalledByNative
    private boolean isAudioLowLatencySupported() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.audio.low_latency");
    }

    @TargetApi(17)
    @CalledByNative
    private int getAudioLowLatencyOutputFrameSize() {
        if (VERSION.SDK_INT < 17) {
            return 256;
        }
        String framesPerBuffer = this.mAudioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER");
        if (framesPerBuffer != null) {
            return Integer.parseInt(framesPerBuffer);
        }
        return 256;
    }

    @CalledByNative
    private static boolean shouldUseAcousticEchoCanceler() {
        if (Arrays.asList(SUPPORTED_AEC_MODELS).contains(Build.MODEL)) {
            return AcousticEchoCanceler.isAvailable();
        }
        return false;
    }

    private void checkIfCalledOnValidThread() {
    }

    private void registerBluetoothIntentsIfNeeded() {
        this.mHasBluetoothPermission = hasPermission("android.permission.BLUETOOTH");
        if (this.mHasBluetoothPermission) {
            this.mAudioDevices[3] = hasBluetoothHeadset();
            registerForBluetoothHeadsetIntentBroadcast();
            registerForBluetoothScoIntentBroadcast();
            return;
        }
        Log.m38w(TAG, "Requires BLUETOOTH permission", new Object[0]);
    }

    private void unregisterBluetoothIntentsIfNeeded() {
        if (this.mHasBluetoothPermission) {
            this.mAudioManager.stopBluetoothSco();
            unregisterForBluetoothHeadsetIntentBroadcast();
            unregisterForBluetoothScoIntentBroadcast();
        }
    }

    private void setSpeakerphoneOn(boolean on) {
        if (this.mAudioManager.isSpeakerphoneOn() != on) {
            this.mAudioManager.setSpeakerphoneOn(on);
        }
    }

    private void setMicrophoneMute(boolean on) {
        if (this.mAudioManager.isMicrophoneMute() != on) {
            this.mAudioManager.setMicrophoneMute(on);
        }
    }

    private boolean isMicrophoneMute() {
        return this.mAudioManager.isMicrophoneMute();
    }

    private boolean hasEarpiece() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    @Deprecated
    private boolean hasWiredHeadset() {
        return this.mAudioManager.isWiredHeadsetOn();
    }

    private boolean hasPermission(String permission) {
        return this.mContext.checkPermission(permission, Process.myPid(), Process.myUid()) == 0;
    }

    @TargetApi(18)
    private boolean hasBluetoothHeadset() {
        boolean z = true;
        if (this.mHasBluetoothPermission) {
            BluetoothAdapter btAdapter;
            if (VERSION.SDK_INT >= 18) {
                btAdapter = ((BluetoothManager) this.mContext.getSystemService("bluetooth")).getAdapter();
            } else {
                btAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            if (btAdapter == null) {
                return false;
            }
            int profileConnectionState = btAdapter.getProfileConnectionState(1);
            if (!(btAdapter.isEnabled() && profileConnectionState == 2)) {
                z = false;
            }
            return z;
        }
        Log.m38w(TAG, "hasBluetoothHeadset() requires BLUETOOTH permission", new Object[0]);
        return false;
    }

    private boolean hasUsbAudio() {
        if (VERSION.SDK_INT < 21) {
            return false;
        }
        try {
            for (UsbDevice device : this.mUsbManager.getDeviceList().values()) {
                if (hasUsbAudioCommInterface(device)) {
                    return true;
                }
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void registerForWiredHeadsetIntentBroadcast() {
        IntentFilter filter = new IntentFilter("android.intent.action.HEADSET_PLUG");
        this.mWiredHeadsetReceiver = new C02531();
        this.mContext.registerReceiver(this.mWiredHeadsetReceiver, filter);
    }

    private void unregisterForWiredHeadsetIntentBroadcast() {
        this.mContext.unregisterReceiver(this.mWiredHeadsetReceiver);
        this.mWiredHeadsetReceiver = null;
    }

    private void registerForBluetoothHeadsetIntentBroadcast() {
        IntentFilter filter = new IntentFilter("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
        this.mBluetoothHeadsetReceiver = new C02542();
        this.mContext.registerReceiver(this.mBluetoothHeadsetReceiver, filter);
    }

    private void unregisterForBluetoothHeadsetIntentBroadcast() {
        this.mContext.unregisterReceiver(this.mBluetoothHeadsetReceiver);
        this.mBluetoothHeadsetReceiver = null;
    }

    private void registerForBluetoothScoIntentBroadcast() {
        IntentFilter filter = new IntentFilter("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
        this.mBluetoothScoReceiver = new C02553();
        this.mContext.registerReceiver(this.mBluetoothScoReceiver, filter);
    }

    private void unregisterForBluetoothScoIntentBroadcast() {
        this.mContext.unregisterReceiver(this.mBluetoothScoReceiver);
        this.mBluetoothScoReceiver = null;
    }

    private void startBluetoothSco() {
        if (this.mHasBluetoothPermission && this.mBluetoothScoState != 1 && this.mBluetoothScoState != 2) {
            if (this.mAudioManager.isBluetoothScoOn()) {
                this.mBluetoothScoState = 1;
                return;
            }
            this.mBluetoothScoState = 2;
            this.mAudioManager.startBluetoothSco();
        }
    }

    private void stopBluetoothSco() {
        if (!this.mHasBluetoothPermission) {
            return;
        }
        if (this.mBluetoothScoState != 1 && this.mBluetoothScoState != 2) {
            return;
        }
        if (this.mAudioManager.isBluetoothScoOn()) {
            this.mBluetoothScoState = 3;
            this.mAudioManager.stopBluetoothSco();
            return;
        }
        loge("Unable to stop BT SCO since it is already disabled");
        this.mBluetoothScoState = 0;
    }

    private void setAudioDevice(int device) {
        if (device == 3) {
            startBluetoothSco();
        } else {
            stopBluetoothSco();
        }
        switch (device) {
            case 0:
                setSpeakerphoneOn(true);
                break;
            case 1:
                setSpeakerphoneOn(false);
                break;
            case 2:
                setSpeakerphoneOn(false);
                break;
            case 3:
                break;
            case 4:
                setSpeakerphoneOn(false);
                break;
            default:
                loge("Invalid audio device selection");
                break;
        }
        reportUpdate();
    }

    private static int selectDefaultDevice(boolean[] devices) {
        if (devices[1]) {
            return 1;
        }
        if (devices[4]) {
            return 4;
        }
        if (devices[3]) {
            return 3;
        }
        return 0;
    }

    private boolean deviceHasBeenRequested() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mRequestedAudioDevice != -1;
        }
        return z;
    }

    private void updateDeviceActivation() {
        synchronized (this.mLock) {
            int requested = this.mRequestedAudioDevice;
            boolean[] devices = (boolean[]) this.mAudioDevices.clone();
        }
        if (requested == -1) {
            loge("Unable to activate device since no device is selected");
        } else if (requested == -2 || !devices[requested]) {
            setAudioDevice(selectDefaultDevice(devices));
        } else {
            setAudioDevice(requested);
        }
    }

    private static int getNumOfAudioDevices(boolean[] devices) {
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (devices[i]) {
                count++;
            }
        }
        return count;
    }

    private void reportUpdate() {
    }

    private void logDeviceInfo() {
        logd("Android SDK: " + VERSION.SDK_INT + ", " + "Release: " + VERSION.RELEASE + ", " + "Brand: " + Build.BRAND + ", " + "Device: " + Build.DEVICE + ", " + "Id: " + Build.ID + ", " + "Hardware: " + Build.HARDWARE + ", " + "Manufacturer: " + Build.MANUFACTURER + ", " + "Model: " + Build.MODEL + ", " + "Product: " + Build.PRODUCT);
    }

    private static void logd(String msg) {
        Log.m20d(TAG, msg);
    }

    private static void loge(String msg) {
        Log.m28e(TAG, msg, new Object[0]);
    }

    private void startObservingVolumeChanges() {
        if (this.mSettingsObserverThread == null) {
            this.mSettingsObserverThread = new HandlerThread("SettingsObserver");
            this.mSettingsObserverThread.start();
            this.mSettingsObserver = new ContentObserver(new Handler(this.mSettingsObserverThread.getLooper())) {
                public void onChange(boolean selfChange) {
                    boolean z = false;
                    super.onChange(selfChange);
                    int volume = AudioManagerAndroid.this.mAudioManager.getStreamVolume(0);
                    AudioManagerAndroid audioManagerAndroid = AudioManagerAndroid.this;
                    long access$1000 = AudioManagerAndroid.this.mNativeAudioManagerAndroid;
                    if (volume == 0) {
                        z = true;
                    }
                    audioManagerAndroid.nativeSetMute(access$1000, z);
                }
            };
            this.mContentResolver.registerContentObserver(System.CONTENT_URI, true, this.mSettingsObserver);
        }
    }

    private void stopObservingVolumeChanges() {
        if (this.mSettingsObserverThread != null) {
            this.mContentResolver.unregisterContentObserver(this.mSettingsObserver);
            this.mSettingsObserver = null;
            this.mSettingsObserverThread.quit();
            try {
                this.mSettingsObserverThread.join();
            } catch (InterruptedException e) {
                Log.m28e(TAG, "Thread.join() exception: ", e);
            }
            this.mSettingsObserverThread = null;
        }
    }

    private boolean hasUsbAudioCommInterface(UsbDevice device) {
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface iface = device.getInterface(i);
            if (iface.getInterfaceClass() == 1 && iface.getInterfaceSubclass() == 2) {
                return true;
            }
        }
        return false;
    }

    private void registerForUsbAudioIntentBroadcast() {
        this.mUsbAudioReceiver = new C02575();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        this.mContext.registerReceiver(this.mUsbAudioReceiver, filter);
    }

    private void unregisterForUsbAudioIntentBroadcast() {
        this.mContext.unregisterReceiver(this.mUsbAudioReceiver);
        this.mUsbAudioReceiver = null;
    }
}
