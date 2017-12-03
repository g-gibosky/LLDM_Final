package org.chromium.mojo.bindings;

import java.io.Closeable;

public interface MessageReceiver extends Closeable {
    boolean accept(Message message);

    void close();
}
