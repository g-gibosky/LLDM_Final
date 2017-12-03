package org.chromium.mojo.system;

import java.io.Closeable;
import org.chromium.mojo.system.Core.HandleSignals;
import org.chromium.mojo.system.Core.WaitResult;

public interface Handle extends Closeable {
    void close();

    Core getCore();

    boolean isValid();

    Handle pass();

    int releaseNativeHandle();

    UntypedHandle toUntypedHandle();

    WaitResult wait(HandleSignals handleSignals, long j);
}
