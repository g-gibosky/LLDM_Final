package org.chromium.base;

public interface ActivityState {
    public static final int CREATED = 1;
    public static final int DESTROYED = 6;
    public static final int PAUSED = 4;
    public static final int RESUMED = 3;
    public static final int STARTED = 2;
    public static final int STOPPED = 5;
}
