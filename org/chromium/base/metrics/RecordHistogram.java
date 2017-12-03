package org.chromium.base.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.chromium.base.VisibleForTesting;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("base::android")
public class RecordHistogram {
    private static Map<String, Long> sCache = Collections.synchronizedMap(new HashMap());
    private static boolean sIsDisabledForTests = false;

    private static native int nativeGetHistogramValueCountForTesting(String str, int i);

    private static native void nativeInitialize();

    private static native long nativeRecordBooleanHistogram(String str, long j, boolean z);

    private static native long nativeRecordCustomCountHistogram(String str, long j, int i, int i2, int i3, int i4);

    private static native long nativeRecordCustomTimesHistogramMilliseconds(String str, long j, int i, int i2, int i3, int i4);

    private static native long nativeRecordEnumeratedHistogram(String str, long j, int i, int i2);

    private static native long nativeRecordLinearCountHistogram(String str, long j, int i, int i2, int i3, int i4);

    private static native long nativeRecordSparseHistogram(String str, long j, int i);

    @VisibleForTesting
    public static void disableForTests() {
        sIsDisabledForTests = true;
    }

    private static long getCachedHistogramKey(String name) {
        Long key = (Long) sCache.get(name);
        return key == null ? 0 : key.longValue();
    }

    public static void recordBooleanHistogram(String name, boolean sample) {
        if (!sIsDisabledForTests) {
            long key = getCachedHistogramKey(name);
            long result = nativeRecordBooleanHistogram(name, key, sample);
            if (result != key) {
                sCache.put(name, Long.valueOf(result));
            }
        }
    }

    public static void recordEnumeratedHistogram(String name, int sample, int boundary) {
        if (!sIsDisabledForTests) {
            long key = getCachedHistogramKey(name);
            long result = nativeRecordEnumeratedHistogram(name, key, sample, boundary);
            if (result != key) {
                sCache.put(name, Long.valueOf(result));
            }
        }
    }

    public static void recordCountHistogram(String name, int sample) {
        recordCustomCountHistogram(name, sample, 1, 1000000, 50);
    }

    public static void recordCount100Histogram(String name, int sample) {
        recordCustomCountHistogram(name, sample, 1, 100, 50);
    }

    public static void recordCount1000Histogram(String name, int sample) {
        recordCustomCountHistogram(name, sample, 1, 1000, 50);
    }

    public static void recordCustomCountHistogram(String name, int sample, int min, int max, int numBuckets) {
        if (!sIsDisabledForTests) {
            long key = getCachedHistogramKey(name);
            long result = nativeRecordCustomCountHistogram(name, key, sample, min, max, numBuckets);
            if (result != key) {
                sCache.put(name, Long.valueOf(result));
            }
        }
    }

    public static void recordLinearCountHistogram(String name, int sample, int min, int max, int numBuckets) {
        if (!sIsDisabledForTests) {
            long key = getCachedHistogramKey(name);
            long result = nativeRecordLinearCountHistogram(name, key, sample, min, max, numBuckets);
            if (result != key) {
                sCache.put(name, Long.valueOf(result));
            }
        }
    }

    public static void recordPercentageHistogram(String name, int sample) {
        if (!sIsDisabledForTests) {
            long key = getCachedHistogramKey(name);
            long result = nativeRecordEnumeratedHistogram(name, key, sample, 101);
            if (result != key) {
                sCache.put(name, Long.valueOf(result));
            }
        }
    }

    public static void recordSparseSlowlyHistogram(String name, int sample) {
        if (!sIsDisabledForTests) {
            long key = getCachedHistogramKey(name);
            long result = nativeRecordSparseHistogram(name, key, sample);
            if (result != key) {
                sCache.put(name, Long.valueOf(result));
            }
        }
    }

    public static void recordTimesHistogram(String name, long duration, TimeUnit timeUnit) {
        recordCustomTimesHistogramMilliseconds(name, timeUnit.toMillis(duration), 1, TimeUnit.SECONDS.toMillis(10), 50);
    }

    public static void recordMediumTimesHistogram(String name, long duration, TimeUnit timeUnit) {
        recordCustomTimesHistogramMilliseconds(name, timeUnit.toMillis(duration), 10, TimeUnit.MINUTES.toMillis(3), 50);
    }

    public static void recordLongTimesHistogram(String name, long duration, TimeUnit timeUnit) {
        recordCustomTimesHistogramMilliseconds(name, timeUnit.toMillis(duration), 1, TimeUnit.HOURS.toMillis(1), 50);
    }

    public static void recordCustomTimesHistogram(String name, long duration, long min, long max, TimeUnit timeUnit, int numBuckets) {
        recordCustomTimesHistogramMilliseconds(name, timeUnit.toMillis(duration), timeUnit.toMillis(min), timeUnit.toMillis(max), numBuckets);
    }

    private static int clampToInt(long value) {
        if (value > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        if (value < -2147483648L) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    private static void recordCustomTimesHistogramMilliseconds(String name, long duration, long min, long max, int numBuckets) {
        if (!sIsDisabledForTests) {
            long key = getCachedHistogramKey(name);
            long result = nativeRecordCustomTimesHistogramMilliseconds(name, key, clampToInt(duration), clampToInt(min), clampToInt(max), numBuckets);
            if (result != key) {
                sCache.put(name, Long.valueOf(result));
            }
        }
    }

    @VisibleForTesting
    public static int getHistogramValueCountForTesting(String name, int sample) {
        return nativeGetHistogramValueCountForTesting(name, sample);
    }

    public static void initialize() {
        if (!sIsDisabledForTests) {
            nativeInitialize();
        }
    }
}
