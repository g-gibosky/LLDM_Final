package org.chromium.mojo.system;

import java.io.Closeable;

public interface RunLoop extends Closeable {
    void close();

    void postDelayedTask(Runnable runnable, long j);

    void quit();

    void run();

    void runUntilIdle();
}
