package org.chromium.content.browser;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.chromium.base.CollectionUtil;
import org.chromium.base.Log;
import org.chromium.base.ThreadUtils;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("content")
class DeviceSensors implements SensorEventListener {
    static final /* synthetic */ boolean $assertionsDisabled;
    static final Set<Integer> DEVICE_LIGHT_SENSORS = CollectionUtil.newHashSet(Integer.valueOf(5));
    static final Set<Integer> DEVICE_MOTION_SENSORS = CollectionUtil.newHashSet(Integer.valueOf(1), Integer.valueOf(10), Integer.valueOf(4));
    static final Set<Integer> DEVICE_ORIENTATION_ABSOLUTE_SENSORS = CollectionUtil.newHashSet(Integer.valueOf(11));
    static final Set<Integer> DEVICE_ORIENTATION_SENSORS_A = CollectionUtil.newHashSet(Integer.valueOf(15));
    static final Set<Integer> DEVICE_ORIENTATION_SENSORS_B = CollectionUtil.newHashSet(Integer.valueOf(11));
    static final Set<Integer> DEVICE_ORIENTATION_SENSORS_C = CollectionUtil.newHashSet(Integer.valueOf(1), Integer.valueOf(2));
    private static final String EXPERIMENTAL_WEB_PLAFTORM_FEATURES = "enable-experimental-web-platform-features";
    private static final String TAG = "cr.DeviceSensors";
    private static DeviceSensors sSingleton;
    private static Object sSingletonLock = new Object();
    @VisibleForTesting
    final Set<Integer> mActiveSensors = new HashSet();
    private final Context mAppContext;
    boolean mDeviceLightIsActive = false;
    boolean mDeviceMotionIsActive = false;
    boolean mDeviceOrientationAbsoluteIsActive = false;
    boolean mDeviceOrientationIsActive = false;
    boolean mDeviceOrientationIsActiveWithBackupSensors = false;
    Set<Integer> mDeviceOrientationSensors;
    private float[] mDeviceRotationMatrix;
    private Handler mHandler;
    private final Object mHandlerLock = new Object();
    private float[] mMagneticFieldVector;
    private long mNativePtr;
    private final Object mNativePtrLock = new Object();
    boolean mOrientationNotAvailable = false;
    final List<Set<Integer>> mOrientationSensorSets;
    private double[] mRotationAngles;
    private SensorManagerProxy mSensorManagerProxy;
    private Thread mThread;
    private float[] mTruncatedRotationVector;

    interface SensorManagerProxy {
        boolean registerListener(SensorEventListener sensorEventListener, int i, int i2, Handler handler);

        void unregisterListener(SensorEventListener sensorEventListener, int i);
    }

    static class SensorManagerProxyImpl implements SensorManagerProxy {
        private final SensorManager mSensorManager;

        SensorManagerProxyImpl(SensorManager sensorManager) {
            this.mSensorManager = sensorManager;
        }

        public boolean registerListener(SensorEventListener listener, int sensorType, int rate, Handler handler) {
            List<Sensor> sensors = this.mSensorManager.getSensorList(sensorType);
            if (sensors.isEmpty()) {
                return false;
            }
            return this.mSensorManager.registerListener(listener, (Sensor) sensors.get(0), rate, handler);
        }

        public void unregisterListener(SensorEventListener listener, int sensorType) {
            List<Sensor> sensors = this.mSensorManager.getSensorList(sensorType);
            if (!sensors.isEmpty()) {
                try {
                    this.mSensorManager.unregisterListener(listener, (Sensor) sensors.get(0));
                } catch (IllegalArgumentException e) {
                    Log.m38w(DeviceSensors.TAG, "Failed to unregister device sensor " + ((Sensor) sensors.get(0)).getName(), new Object[0]);
                }
            }
        }
    }

    private native void nativeGotAcceleration(long j, double d, double d2, double d3);

    private native void nativeGotAccelerationIncludingGravity(long j, double d, double d2, double d3);

    private native void nativeGotLight(long j, double d);

    private native void nativeGotOrientation(long j, double d, double d2, double d3);

    private native void nativeGotOrientationAbsolute(long j, double d, double d2, double d3);

    private native void nativeGotRotationRate(long j, double d, double d2, double d3);

    static {
        boolean z;
        if (DeviceSensors.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
    }

    protected DeviceSensors(Context context) {
        this.mAppContext = context.getApplicationContext();
        this.mOrientationSensorSets = CollectionUtil.newArrayList(DEVICE_ORIENTATION_SENSORS_A, DEVICE_ORIENTATION_SENSORS_B, DEVICE_ORIENTATION_SENSORS_C);
    }

    @VisibleForTesting
    protected boolean registerOrientationSensorsWithFallback(int rateInMicroseconds) {
        if (this.mOrientationNotAvailable) {
            return false;
        }
        if (this.mDeviceOrientationSensors != null) {
            return registerSensors(this.mDeviceOrientationSensors, rateInMicroseconds, true);
        }
        ensureRotationStructuresAllocated();
        for (Set<Integer> sensors : this.mOrientationSensorSets) {
            this.mDeviceOrientationSensors = sensors;
            if (registerSensors(this.mDeviceOrientationSensors, rateInMicroseconds, true)) {
                return true;
            }
        }
        this.mOrientationNotAvailable = true;
        this.mDeviceOrientationSensors = null;
        this.mDeviceRotationMatrix = null;
        this.mRotationAngles = null;
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @org.chromium.base.annotations.CalledByNative
    public boolean start(long r10, int r12, int r13) {
        /*
        r9 = this;
        r1 = 0;
        r0 = 0;
        r2 = r9.mNativePtrLock;
        monitor-enter(r2);
        switch(r12) {
            case 1: goto L_0x0035;
            case 2: goto L_0x001b;
            case 3: goto L_0x0008;
            case 4: goto L_0x002a;
            case 5: goto L_0x0008;
            case 6: goto L_0x0008;
            case 7: goto L_0x0008;
            case 8: goto L_0x003d;
            default: goto L_0x0008;
        };
    L_0x0008:
        r3 = "cr.DeviceSensors";
        r4 = "Unknown event type: %d";
        r5 = 1;
        r5 = new java.lang.Object[r5];	 Catch:{ all -> 0x0045 }
        r6 = 0;
        r7 = java.lang.Integer.valueOf(r12);	 Catch:{ all -> 0x0045 }
        r5[r6] = r7;	 Catch:{ all -> 0x0045 }
        org.chromium.base.Log.m28e(r3, r4, r5);	 Catch:{ all -> 0x0045 }
        monitor-exit(r2);	 Catch:{ all -> 0x0045 }
    L_0x001a:
        return r1;
    L_0x001b:
        r0 = r9.registerOrientationSensorsWithFallback(r13);	 Catch:{ all -> 0x0045 }
    L_0x001f:
        if (r0 == 0) goto L_0x0027;
    L_0x0021:
        r9.mNativePtr = r10;	 Catch:{ all -> 0x0045 }
        r1 = 1;
        r9.setEventTypeActive(r12, r1);	 Catch:{ all -> 0x0045 }
    L_0x0027:
        monitor-exit(r2);	 Catch:{ all -> 0x0045 }
        r1 = r0;
        goto L_0x001a;
    L_0x002a:
        r9.ensureRotationStructuresAllocated();	 Catch:{ all -> 0x0045 }
        r1 = DEVICE_ORIENTATION_ABSOLUTE_SENSORS;	 Catch:{ all -> 0x0045 }
        r3 = 1;
        r0 = r9.registerSensors(r1, r13, r3);	 Catch:{ all -> 0x0045 }
        goto L_0x001f;
    L_0x0035:
        r1 = DEVICE_MOTION_SENSORS;	 Catch:{ all -> 0x0045 }
        r3 = 0;
        r0 = r9.registerSensors(r1, r13, r3);	 Catch:{ all -> 0x0045 }
        goto L_0x001f;
    L_0x003d:
        r1 = DEVICE_LIGHT_SENSORS;	 Catch:{ all -> 0x0045 }
        r3 = 1;
        r0 = r9.registerSensors(r1, r13, r3);	 Catch:{ all -> 0x0045 }
        goto L_0x001f;
    L_0x0045:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0045 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.chromium.content.browser.DeviceSensors.start(long, int, int):boolean");
    }

    @CalledByNative
    public int getNumberActiveDeviceMotionSensors() {
        Set<Integer> deviceMotionSensors = new HashSet(DEVICE_MOTION_SENSORS);
        deviceMotionSensors.removeAll(this.mActiveSensors);
        return DEVICE_MOTION_SENSORS.size() - deviceMotionSensors.size();
    }

    @CalledByNative
    public int getOrientationSensorTypeUsed() {
        if (this.mOrientationNotAvailable) {
            return 0;
        }
        if (this.mDeviceOrientationSensors == DEVICE_ORIENTATION_SENSORS_A) {
            return 3;
        }
        if (this.mDeviceOrientationSensors == DEVICE_ORIENTATION_SENSORS_B) {
            return 1;
        }
        if (this.mDeviceOrientationSensors == DEVICE_ORIENTATION_SENSORS_C) {
            return 2;
        }
        if ($assertionsDisabled) {
            return 0;
        }
        throw new AssertionError();
    }

    @CalledByNative
    public void stop(int eventType) {
        Set<Integer> sensorsToRemainActive = new HashSet();
        synchronized (this.mNativePtrLock) {
            if (this.mDeviceOrientationIsActive && eventType != 2) {
                sensorsToRemainActive.addAll(this.mDeviceOrientationSensors);
            }
            if (this.mDeviceOrientationAbsoluteIsActive && eventType != 4) {
                sensorsToRemainActive.addAll(DEVICE_ORIENTATION_ABSOLUTE_SENSORS);
            }
            if (this.mDeviceMotionIsActive && eventType != 1) {
                sensorsToRemainActive.addAll(DEVICE_MOTION_SENSORS);
            }
            if (this.mDeviceLightIsActive && eventType != 8) {
                sensorsToRemainActive.addAll(DEVICE_LIGHT_SENSORS);
            }
            Set<Integer> sensorsToDeactivate = new HashSet(this.mActiveSensors);
            sensorsToDeactivate.removeAll(sensorsToRemainActive);
            unregisterSensors(sensorsToDeactivate);
            setEventTypeActive(eventType, false);
            if (this.mActiveSensors.isEmpty()) {
                this.mNativePtr = 0;
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        sensorChanged(event.sensor.getType(), event.values);
    }

    @VisibleForTesting
    void sensorChanged(int type, float[] values) {
        switch (type) {
            case 1:
                if (this.mDeviceMotionIsActive) {
                    gotAccelerationIncludingGravity((double) values[0], (double) values[1], (double) values[2]);
                }
                if (this.mDeviceOrientationIsActiveWithBackupSensors) {
                    getOrientationFromGeomagneticVectors(values, this.mMagneticFieldVector);
                    return;
                }
                return;
            case 2:
                if (this.mDeviceOrientationIsActiveWithBackupSensors) {
                    if (this.mMagneticFieldVector == null) {
                        this.mMagneticFieldVector = new float[3];
                    }
                    System.arraycopy(values, 0, this.mMagneticFieldVector, 0, this.mMagneticFieldVector.length);
                    return;
                }
                return;
            case 4:
                if (this.mDeviceMotionIsActive) {
                    gotRotationRate((double) values[0], (double) values[1], (double) values[2]);
                    return;
                }
                return;
            case 5:
                if (this.mDeviceLightIsActive) {
                    gotLight((double) values[0]);
                    return;
                }
                return;
            case 10:
                if (this.mDeviceMotionIsActive) {
                    gotAcceleration((double) values[0], (double) values[1], (double) values[2]);
                    return;
                }
                return;
            case 11:
                if (this.mDeviceOrientationAbsoluteIsActive) {
                    convertRotationVectorToAngles(values, this.mRotationAngles);
                    gotOrientationAbsolute(this.mRotationAngles[0], this.mRotationAngles[1], this.mRotationAngles[2]);
                }
                if (this.mDeviceOrientationIsActive && this.mDeviceOrientationSensors == DEVICE_ORIENTATION_SENSORS_B) {
                    if (!this.mDeviceOrientationAbsoluteIsActive) {
                        convertRotationVectorToAngles(values, this.mRotationAngles);
                    }
                    gotOrientation(this.mRotationAngles[0], this.mRotationAngles[1], this.mRotationAngles[2]);
                    return;
                }
                return;
            case 15:
                if (this.mDeviceOrientationIsActive) {
                    convertRotationVectorToAngles(values, this.mRotationAngles);
                    gotOrientation(this.mRotationAngles[0], this.mRotationAngles[1], this.mRotationAngles[2]);
                    return;
                }
                return;
            default:
                return;
        }
    }

    @VisibleForTesting
    public static double[] computeDeviceOrientationFromRotationMatrix(float[] matrixR, double[] values) {
        if (matrixR.length == 9) {
            if (matrixR[8] > 0.0f) {
                values[0] = Math.atan2((double) (-matrixR[1]), (double) matrixR[4]);
                values[1] = Math.asin((double) matrixR[7]);
                values[2] = Math.atan2((double) (-matrixR[6]), (double) matrixR[8]);
            } else if (matrixR[8] < 0.0f) {
                values[0] = Math.atan2((double) matrixR[1], (double) (-matrixR[4]));
                values[1] = -Math.asin((double) matrixR[7]);
                values[1] = (values[1] >= 0.0d ? -3.141592653589793d : 3.141592653589793d) + values[1];
                values[2] = Math.atan2((double) matrixR[6], (double) (-matrixR[8]));
            } else if (matrixR[6] > 0.0f) {
                values[0] = Math.atan2((double) (-matrixR[1]), (double) matrixR[4]);
                values[1] = Math.asin((double) matrixR[7]);
                values[2] = -1.5707963267948966d;
            } else if (matrixR[6] < 0.0f) {
                values[0] = Math.atan2((double) matrixR[1], (double) (-matrixR[4]));
                values[1] = -Math.asin((double) matrixR[7]);
                values[1] = (values[1] >= 0.0d ? -3.141592653589793d : 3.141592653589793d) + values[1];
                values[2] = -1.5707963267948966d;
            } else {
                values[0] = Math.atan2((double) matrixR[3], (double) matrixR[0]);
                values[1] = matrixR[7] > 0.0f ? 1.5707963267948966d : -1.5707963267948966d;
                values[2] = 0.0d;
            }
            if (values[0] < 0.0d) {
                values[0] = values[0] + 6.283185307179586d;
            }
        }
        return values;
    }

    public void convertRotationVectorToAngles(float[] rotationVector, double[] angles) {
        if (rotationVector.length > 4) {
            System.arraycopy(rotationVector, 0, this.mTruncatedRotationVector, 0, 4);
            SensorManager.getRotationMatrixFromVector(this.mDeviceRotationMatrix, this.mTruncatedRotationVector);
        } else {
            SensorManager.getRotationMatrixFromVector(this.mDeviceRotationMatrix, rotationVector);
        }
        computeDeviceOrientationFromRotationMatrix(this.mDeviceRotationMatrix, angles);
        for (int i = 0; i < 3; i++) {
            angles[i] = Math.toDegrees(angles[i]);
        }
    }

    private void getOrientationFromGeomagneticVectors(float[] acceleration, float[] magnetic) {
        if (acceleration != null && magnetic != null && SensorManager.getRotationMatrix(this.mDeviceRotationMatrix, null, acceleration, magnetic)) {
            computeDeviceOrientationFromRotationMatrix(this.mDeviceRotationMatrix, this.mRotationAngles);
            gotOrientation(Math.toDegrees(this.mRotationAngles[0]), Math.toDegrees(this.mRotationAngles[1]), Math.toDegrees(this.mRotationAngles[2]));
        }
    }

    private SensorManagerProxy getSensorManagerProxy() {
        if (this.mSensorManagerProxy != null) {
            return this.mSensorManagerProxy;
        }
        ThreadUtils.assertOnUiThread();
        SensorManager sensorManager = (SensorManager) this.mAppContext.getSystemService("sensor");
        if (sensorManager != null) {
            this.mSensorManagerProxy = new SensorManagerProxyImpl(sensorManager);
        }
        return this.mSensorManagerProxy;
    }

    @VisibleForTesting
    void setSensorManagerProxy(SensorManagerProxy sensorManagerProxy) {
        this.mSensorManagerProxy = sensorManagerProxy;
    }

    private void setEventTypeActive(int eventType, boolean active) {
        switch (eventType) {
            case 1:
                this.mDeviceMotionIsActive = active;
                return;
            case 2:
                this.mDeviceOrientationIsActive = active;
                boolean z = active && this.mDeviceOrientationSensors == DEVICE_ORIENTATION_SENSORS_C;
                this.mDeviceOrientationIsActiveWithBackupSensors = z;
                return;
            case 4:
                this.mDeviceOrientationAbsoluteIsActive = active;
                return;
            case 8:
                this.mDeviceLightIsActive = active;
                return;
            default:
                return;
        }
    }

    private void ensureRotationStructuresAllocated() {
        if (this.mDeviceRotationMatrix == null) {
            this.mDeviceRotationMatrix = new float[9];
        }
        if (this.mRotationAngles == null) {
            this.mRotationAngles = new double[3];
        }
        if (this.mTruncatedRotationVector == null) {
            this.mTruncatedRotationVector = new float[4];
        }
    }

    private boolean registerSensors(Set<Integer> sensorTypes, int rateInMicroseconds, boolean failOnMissingSensor) {
        Set<Integer> sensorsToActivate = new HashSet(sensorTypes);
        sensorsToActivate.removeAll(this.mActiveSensors);
        if (sensorsToActivate.isEmpty()) {
            return true;
        }
        boolean success = false;
        for (Integer sensorType : sensorsToActivate) {
            boolean result = registerForSensorType(sensorType.intValue(), rateInMicroseconds);
            if (!result && failOnMissingSensor) {
                unregisterSensors(sensorsToActivate);
                return false;
            } else if (result) {
                this.mActiveSensors.add(sensorType);
                success = true;
            }
        }
        return success;
    }

    private void unregisterSensors(Iterable<Integer> sensorTypes) {
        for (Integer sensorType : sensorTypes) {
            if (this.mActiveSensors.contains(sensorType)) {
                getSensorManagerProxy().unregisterListener(this, sensorType.intValue());
                this.mActiveSensors.remove(sensorType);
            }
        }
    }

    private boolean registerForSensorType(int type, int rateInMicroseconds) {
        SensorManagerProxy sensorManager = getSensorManagerProxy();
        if (sensorManager == null) {
            return false;
        }
        return sensorManager.registerListener(this, type, rateInMicroseconds, getHandler());
    }

    protected void gotOrientation(double alpha, double beta, double gamma) {
        synchronized (this.mNativePtrLock) {
            if (this.mNativePtr != 0) {
                nativeGotOrientation(this.mNativePtr, alpha, beta, gamma);
            }
        }
    }

    protected void gotOrientationAbsolute(double alpha, double beta, double gamma) {
        synchronized (this.mNativePtrLock) {
            if (this.mNativePtr != 0) {
                nativeGotOrientationAbsolute(this.mNativePtr, alpha, beta, gamma);
            }
        }
    }

    protected void gotAcceleration(double x, double y, double z) {
        synchronized (this.mNativePtrLock) {
            if (this.mNativePtr != 0) {
                nativeGotAcceleration(this.mNativePtr, x, y, z);
            }
        }
    }

    protected void gotAccelerationIncludingGravity(double x, double y, double z) {
        synchronized (this.mNativePtrLock) {
            if (this.mNativePtr != 0) {
                nativeGotAccelerationIncludingGravity(this.mNativePtr, x, y, z);
            }
        }
    }

    protected void gotRotationRate(double alpha, double beta, double gamma) {
        synchronized (this.mNativePtrLock) {
            if (this.mNativePtr != 0) {
                nativeGotRotationRate(this.mNativePtr, alpha, beta, gamma);
            }
        }
    }

    protected void gotLight(double value) {
        synchronized (this.mNativePtrLock) {
            if (this.mNativePtr != 0) {
                nativeGotLight(this.mNativePtr, value);
            }
        }
    }

    private Handler getHandler() {
        Handler handler;
        synchronized (this.mHandlerLock) {
            if (this.mHandler == null) {
                HandlerThread thread = new HandlerThread("DeviceMotionAndOrientation");
                thread.start();
                this.mHandler = new Handler(thread.getLooper());
            }
            handler = this.mHandler;
        }
        return handler;
    }

    @CalledByNative
    static DeviceSensors getInstance(Context appContext) {
        DeviceSensors deviceSensors;
        synchronized (sSingletonLock) {
            if (sSingleton == null) {
                sSingleton = new DeviceSensors(appContext);
            }
            deviceSensors = sSingleton;
        }
        return deviceSensors;
    }
}
