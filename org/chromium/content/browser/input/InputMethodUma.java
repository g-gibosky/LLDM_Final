package org.chromium.content.browser.input;

import org.chromium.base.metrics.RecordHistogram;

class InputMethodUma {
    private static final int UMA_PROXYVIEW_COUNT = 4;
    private static final int UMA_PROXYVIEW_DETECTION_FAILURE = 2;
    private static final int UMA_PROXYVIEW_FAILURE = 1;
    private static final int UMA_PROXYVIEW_REPLICA_INPUT_CONNECTION = 3;
    private static final int UMA_PROXYVIEW_SUCCESS = 0;
    private static final String UMA_REGISTER_PROXYVIEW = "InputMethod.RegisterProxyView";

    InputMethodUma() {
    }

    void recordProxyViewSuccess() {
        RecordHistogram.recordEnumeratedHistogram(UMA_REGISTER_PROXYVIEW, 0, 4);
    }

    void recordProxyViewFailure() {
        RecordHistogram.recordEnumeratedHistogram(UMA_REGISTER_PROXYVIEW, 1, 4);
    }

    void recordProxyViewDetectionFailure() {
        RecordHistogram.recordEnumeratedHistogram(UMA_REGISTER_PROXYVIEW, 2, 4);
    }

    void recordProxyViewReplicaInputConnection() {
        RecordHistogram.recordEnumeratedHistogram(UMA_REGISTER_PROXYVIEW, 3, 4);
    }
}
