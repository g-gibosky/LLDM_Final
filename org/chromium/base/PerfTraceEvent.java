package org.chromium.base;

import android.os.Debug;
import android.os.Debug.MemoryInfo;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import org.chromium.base.annotations.SuppressFBWarnings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressFBWarnings({"CHROMIUM_SYNCHRONIZED_METHOD"})
public class PerfTraceEvent {
    private static final int MAX_NAME_LENGTH = 40;
    private static final String MEMORY_TRACE_NAME_SUFFIX = "_BZR_PSS";
    private static long sBeginNanoTime;
    private static boolean sEnabled = false;
    private static List<String> sFilter;
    private static File sOutputFile = null;
    private static JSONArray sPerfTraceStrings;
    private static boolean sTrackMemory = false;
    private static boolean sTrackTiming = true;

    private enum EventType {
        START("S"),
        FINISH("F"),
        INSTANT("I");
        
        private final String mTypeStr;

        private EventType(String typeStr) {
            this.mTypeStr = typeStr;
        }

        public String toString() {
            return this.mTypeStr;
        }
    }

    @VisibleForTesting
    public static synchronized void setFilter(List<String> strings) {
        synchronized (PerfTraceEvent.class) {
            sFilter = new LinkedList(strings);
        }
    }

    @VisibleForTesting
    public static synchronized void setEnabled(boolean enabled) {
        synchronized (PerfTraceEvent.class) {
            if (sEnabled != enabled) {
                if (enabled) {
                    sBeginNanoTime = System.nanoTime();
                    sPerfTraceStrings = new JSONArray();
                } else {
                    dumpPerf();
                    sPerfTraceStrings = null;
                    sFilter = null;
                }
                sEnabled = enabled;
            }
        }
    }

    @VisibleForTesting
    public static synchronized void setMemoryTrackingEnabled(boolean enabled) {
        synchronized (PerfTraceEvent.class) {
            sTrackMemory = enabled;
        }
    }

    @VisibleForTesting
    public static synchronized void setTimingTrackingEnabled(boolean enabled) {
        synchronized (PerfTraceEvent.class) {
            sTrackTiming = enabled;
        }
    }

    @VisibleForTesting
    public static synchronized boolean enabled() {
        boolean z;
        synchronized (PerfTraceEvent.class) {
            z = sEnabled;
        }
        return z;
    }

    public static synchronized void instant(String name) {
        synchronized (PerfTraceEvent.class) {
            long eventId = (long) name.hashCode();
            TraceEvent.instant(name);
            if (sEnabled && matchesFilter(name)) {
                savePerfString(name, eventId, EventType.INSTANT, false);
            }
        }
    }

    @VisibleForTesting
    public static synchronized void begin(String name) {
        synchronized (PerfTraceEvent.class) {
            long eventId = (long) name.hashCode();
            TraceEvent.startAsync(name, eventId);
            if (sEnabled && matchesFilter(name)) {
                if (sTrackMemory) {
                    savePerfString(makeMemoryTraceNameFromTimingName(name), eventId, EventType.START, true);
                }
                if (sTrackTiming) {
                    savePerfString(name, eventId, EventType.START, false);
                }
            }
        }
    }

    @VisibleForTesting
    public static synchronized void end(String name) {
        synchronized (PerfTraceEvent.class) {
            long eventId = (long) name.hashCode();
            TraceEvent.finishAsync(name, eventId);
            if (sEnabled && matchesFilter(name)) {
                if (sTrackTiming) {
                    savePerfString(name, eventId, EventType.FINISH, false);
                }
                if (sTrackMemory) {
                    savePerfString(makeMemoryTraceNameFromTimingName(name), eventId, EventType.FINISH, true);
                }
            }
        }
    }

    @VisibleForTesting
    public static synchronized void begin(String name, MemoryInfo memoryInfo) {
        synchronized (PerfTraceEvent.class) {
            long eventId = (long) name.hashCode();
            TraceEvent.startAsync(name, eventId);
            if (sEnabled && matchesFilter(name)) {
                savePerfString(makeMemoryTraceNameFromTimingName(name), eventId, EventType.START, (System.nanoTime() - sBeginNanoTime) / 1000, memoryInfo);
                if (sTrackTiming) {
                    savePerfString(name, eventId, EventType.START, false);
                }
            }
        }
    }

    @VisibleForTesting
    public static synchronized void end(String name, MemoryInfo memoryInfo) {
        synchronized (PerfTraceEvent.class) {
            long eventId = (long) name.hashCode();
            TraceEvent.finishAsync(name, eventId);
            if (sEnabled && matchesFilter(name)) {
                if (sTrackTiming) {
                    savePerfString(name, eventId, EventType.FINISH, false);
                }
                savePerfString(makeMemoryTraceNameFromTimingName(name), eventId, EventType.FINISH, (System.nanoTime() - sBeginNanoTime) / 1000, memoryInfo);
            }
        }
    }

    private static boolean matchesFilter(String name) {
        return sFilter != null ? sFilter.contains(name) : false;
    }

    private static void savePerfString(String name, long id, EventType type, boolean includeMemory) {
        long timestampUs = (System.nanoTime() - sBeginNanoTime) / 1000;
        MemoryInfo memInfo = null;
        if (includeMemory) {
            memInfo = new MemoryInfo();
            Debug.getMemoryInfo(memInfo);
        }
        savePerfString(name, id, type, timestampUs, memInfo);
    }

    private static void savePerfString(String name, long id, EventType type, long timestampUs, MemoryInfo memoryInfo) {
        try {
            JSONObject traceObj = new JSONObject();
            traceObj.put("cat", "Java");
            traceObj.put("ts", timestampUs);
            traceObj.put("ph", type);
            traceObj.put("name", name);
            traceObj.put("id", id);
            if (memoryInfo != null) {
                traceObj.put("mem", (memoryInfo.nativePss + memoryInfo.dalvikPss) + memoryInfo.otherPss);
            }
            sPerfTraceStrings.put(traceObj);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static String makeMemoryTraceNameFromTimingName(String name) {
        return makeSafeTraceName(name, MEMORY_TRACE_NAME_SUFFIX);
    }

    public static String makeSafeTraceName(String baseName, String suffix) {
        int suffixLength = suffix.length();
        if (baseName.length() + suffixLength > 40) {
            baseName = baseName.substring(0, 40 - suffixLength);
        }
        return baseName + suffix;
    }

    @VisibleForTesting
    public static synchronized void setOutputFile(File file) {
        synchronized (PerfTraceEvent.class) {
            sOutputFile = file;
        }
    }

    private static void dumpPerf() {
        String json = sPerfTraceStrings.toString();
        if (sOutputFile == null) {
            System.out.println(json);
            return;
        }
        PrintStream stream;
        try {
            stream = new PrintStream(new FileOutputStream(sOutputFile, true));
            stream.print(json);
            try {
                stream.close();
            } catch (Exception e) {
                Log.e("PerfTraceEvent", "Unable to close perf trace output file.");
            }
        } catch (FileNotFoundException e2) {
            Log.e("PerfTraceEvent", "Unable to dump perf trace data to output file.");
        } catch (Throwable th) {
            try {
                stream.close();
            } catch (Exception e3) {
                Log.e("PerfTraceEvent", "Unable to close perf trace output file.");
            }
        }
    }
}
