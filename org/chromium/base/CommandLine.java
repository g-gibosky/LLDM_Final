package org.chromium.base;

import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.chromium.base.annotations.MainDex;

@MainDex
public abstract class CommandLine {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String SWITCH_PREFIX = "--";
    private static final String SWITCH_TERMINATOR = "--";
    private static final String SWITCH_VALUE_SEPARATOR = "=";
    private static final String TAG = "CommandLine";
    private static final AtomicReference<CommandLine> sCommandLine = new AtomicReference();
    private static final List<ResetListener> sResetListeners = new ArrayList();

    static class C01591 implements Runnable {
        C01591() {
        }

        public void run() {
            for (ResetListener listener : CommandLine.sResetListeners) {
                listener.onCommandLineReset();
            }
        }
    }

    public interface ResetListener {
        void onCommandLineReset();
    }

    private static class JavaCommandLine extends CommandLine {
        static final /* synthetic */ boolean $assertionsDisabled = (!CommandLine.class.desiredAssertionStatus());
        private ArrayList<String> mArgs = new ArrayList();
        private int mArgsBegin = 1;
        private HashMap<String, String> mSwitches = new HashMap();

        JavaCommandLine(String[] args) {
            super();
            if (args == null || args.length == 0 || args[0] == null) {
                this.mArgs.add("");
            } else {
                this.mArgs.add(args[0]);
                appendSwitchesInternal(args, 1);
            }
            if (!$assertionsDisabled && this.mArgs.size() <= 0) {
                throw new AssertionError();
            }
        }

        private String[] getCommandLineArguments() {
            return (String[]) this.mArgs.toArray(new String[this.mArgs.size()]);
        }

        public boolean hasSwitch(String switchString) {
            return this.mSwitches.containsKey(switchString);
        }

        public String getSwitchValue(String switchString) {
            String value = (String) this.mSwitches.get(switchString);
            return (value == null || value.isEmpty()) ? null : value;
        }

        public void appendSwitch(String switchString) {
            appendSwitchWithValue(switchString, null);
        }

        public void appendSwitchWithValue(String switchString, String value) {
            Object obj;
            HashMap hashMap = this.mSwitches;
            if (value == null) {
                obj = "";
            } else {
                String str = value;
            }
            hashMap.put(switchString, obj);
            String combinedSwitchString = "--" + switchString;
            if (!(value == null || value.isEmpty())) {
                combinedSwitchString = combinedSwitchString + CommandLine.SWITCH_VALUE_SEPARATOR + value;
            }
            ArrayList arrayList = this.mArgs;
            int i = this.mArgsBegin;
            this.mArgsBegin = i + 1;
            arrayList.add(i, combinedSwitchString);
        }

        public void appendSwitchesAndArguments(String[] array) {
            appendSwitchesInternal(array, 0);
        }

        private void appendSwitchesInternal(String[] array, int skipCount) {
            boolean parseSwitches = true;
            for (String arg : array) {
                if (skipCount > 0) {
                    skipCount--;
                } else {
                    if (arg.equals("--")) {
                        parseSwitches = false;
                    }
                    if (parseSwitches && arg.startsWith("--")) {
                        String[] parts = arg.split(CommandLine.SWITCH_VALUE_SEPARATOR, 2);
                        appendSwitchWithValue(parts[0].substring("--".length()), parts.length > 1 ? parts[1] : null);
                    } else {
                        this.mArgs.add(arg);
                    }
                }
            }
        }
    }

    private static class NativeCommandLine extends CommandLine {
        private NativeCommandLine() {
            super();
        }

        public boolean hasSwitch(String switchString) {
            return CommandLine.nativeHasSwitch(switchString);
        }

        public String getSwitchValue(String switchString) {
            return CommandLine.nativeGetSwitchValue(switchString);
        }

        public void appendSwitch(String switchString) {
            CommandLine.nativeAppendSwitch(switchString);
        }

        public void appendSwitchWithValue(String switchString, String value) {
            CommandLine.nativeAppendSwitchWithValue(switchString, value);
        }

        public void appendSwitchesAndArguments(String[] array) {
            CommandLine.nativeAppendSwitchesAndArguments(array);
        }

        public boolean isNativeImplementation() {
            return true;
        }
    }

    private static native void nativeAppendSwitch(String str);

    private static native void nativeAppendSwitchWithValue(String str, String str2);

    private static native void nativeAppendSwitchesAndArguments(String[] strArr);

    private static native String nativeGetSwitchValue(String str);

    private static native boolean nativeHasSwitch(String str);

    private static native void nativeReset();

    @VisibleForTesting
    public abstract void appendSwitch(String str);

    public abstract void appendSwitchWithValue(String str, String str2);

    public abstract void appendSwitchesAndArguments(String[] strArr);

    public abstract String getSwitchValue(String str);

    @VisibleForTesting
    public abstract boolean hasSwitch(String str);

    static {
        boolean z;
        if (CommandLine.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
    }

    public String getSwitchValue(String switchString, String defaultValue) {
        String value = getSwitchValue(switchString);
        return TextUtils.isEmpty(value) ? defaultValue : value;
    }

    public boolean isNativeImplementation() {
        return false;
    }

    public static boolean isInitialized() {
        return sCommandLine.get() != null;
    }

    @VisibleForTesting
    public static CommandLine getInstance() {
        CommandLine commandLine = (CommandLine) sCommandLine.get();
        if ($assertionsDisabled || commandLine != null) {
            return commandLine;
        }
        throw new AssertionError();
    }

    public static void init(String[] args) {
        setInstance(new JavaCommandLine(args));
    }

    public static void initFromFile(String file) {
        char[] buffer = readUtf8FileFully(file, 8192);
        init(buffer == null ? null : tokenizeQuotedAruments(buffer));
    }

    @VisibleForTesting
    public static void reset() {
        setInstance(null);
        ThreadUtils.postOnUiThread(new C01591());
    }

    public static void addResetListener(ResetListener listener) {
        sResetListeners.add(listener);
    }

    public static void removeResetListener(ResetListener listener) {
        sResetListeners.remove(listener);
    }

    public static String[] tokenizeQuotedAruments(char[] buffer) {
        ArrayList<String> args = new ArrayList();
        StringBuilder arg = null;
        char currentQuote = '\u0000';
        for (char c : buffer) {
            if ((currentQuote == '\u0000' && (c == '\'' || c == '\"')) || c == currentQuote) {
                if (arg == null || arg.length() <= 0 || arg.charAt(arg.length() - 1) != '\\') {
                    currentQuote = currentQuote == '\u0000' ? c : '\u0000';
                } else {
                    arg.setCharAt(arg.length() - 1, c);
                }
            } else if (currentQuote != '\u0000' || !Character.isWhitespace(c)) {
                if (arg == null) {
                    arg = new StringBuilder();
                }
                arg.append(c);
            } else if (arg != null) {
                args.add(arg.toString());
                arg = null;
            }
        }
        if (arg != null) {
            if (currentQuote != '\u0000') {
                Log.w(TAG, "Unterminated quoted string: " + arg);
            }
            args.add(arg.toString());
        }
        return (String[]) args.toArray(new String[args.size()]);
    }

    public static void enableNativeProxy() {
        sCommandLine.set(new NativeCommandLine());
    }

    public static String[] getJavaSwitchesOrNull() {
        CommandLine commandLine = (CommandLine) sCommandLine.get();
        if (commandLine == null) {
            return null;
        }
        if ($assertionsDisabled || !commandLine.isNativeImplementation()) {
            return ((JavaCommandLine) commandLine).getCommandLineArguments();
        }
        throw new AssertionError();
    }

    private static void setInstance(CommandLine commandLine) {
        CommandLine oldCommandLine = (CommandLine) sCommandLine.getAndSet(commandLine);
        if (oldCommandLine != null && oldCommandLine.isNativeImplementation()) {
            nativeReset();
        }
    }

    private static char[] readUtf8FileFully(String fileName, int sizeLimit) {
        Throwable th;
        Reader reader = null;
        File f = new File(fileName);
        long fileLength = f.length();
        if (fileLength == 0) {
            return null;
        }
        if (fileLength > ((long) sizeLimit)) {
            Log.w(TAG, "File " + fileName + " length " + fileLength + " exceeds limit " + sizeLimit);
            return null;
        }
        try {
            char[] buffer = new char[((int) fileLength)];
            Reader reader2 = new InputStreamReader(new FileInputStream(f), "UTF-8");
            try {
                int charsRead = reader2.read(buffer);
                if ($assertionsDisabled || !reader2.ready()) {
                    if (charsRead < buffer.length) {
                        buffer = Arrays.copyOfRange(buffer, 0, charsRead);
                    }
                    if (reader2 != null) {
                        try {
                            reader2.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Unable to close file reader.", e);
                        }
                    }
                    reader = reader2;
                    return buffer;
                }
                throw new AssertionError();
            } catch (FileNotFoundException e2) {
                reader = reader2;
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e3) {
                        Log.e(TAG, "Unable to close file reader.", e3);
                    }
                }
                return null;
            } catch (IOException e4) {
                reader = reader2;
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e32) {
                        Log.e(TAG, "Unable to close file reader.", e32);
                    }
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                reader = reader2;
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e322) {
                        Log.e(TAG, "Unable to close file reader.", e322);
                    }
                }
                throw th;
            }
        } catch (FileNotFoundException e5) {
            if (reader != null) {
                reader.close();
            }
            return null;
        } catch (IOException e6) {
            if (reader != null) {
                reader.close();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            if (reader != null) {
                reader.close();
            }
            throw th;
        }
    }

    private CommandLine() {
    }
}
