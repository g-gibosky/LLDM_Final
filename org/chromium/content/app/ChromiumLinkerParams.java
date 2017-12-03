package org.chromium.content.app;

import android.content.Intent;
import java.util.Locale;

public class ChromiumLinkerParams {
    private static final String EXTRA_LINKER_PARAMS_BASE_LOAD_ADDRESS = "org.chromium.content.common.linker_params.base_load_address";
    private static final String EXTRA_LINKER_PARAMS_LINKER_IMPLEMENTATION = "org.chromium.content.common.linker_params.linker_implementation";
    private static final String EXTRA_LINKER_PARAMS_TEST_RUNNER_CLASS_NAME = "org.chromium.content.common.linker_params.test_runner_class_name";
    private static final String EXTRA_LINKER_PARAMS_WAIT_FOR_SHARED_RELRO = "org.chromium.content.common.linker_params.wait_for_shared_relro";
    public final long mBaseLoadAddress;
    public final int mLinkerImplementationForTesting;
    public final String mTestRunnerClassNameForTesting;
    public final boolean mWaitForSharedRelro;

    public ChromiumLinkerParams(long baseLoadAddress, boolean waitForSharedRelro) {
        this.mBaseLoadAddress = baseLoadAddress;
        this.mWaitForSharedRelro = waitForSharedRelro;
        this.mTestRunnerClassNameForTesting = null;
        this.mLinkerImplementationForTesting = 0;
    }

    public ChromiumLinkerParams(long baseLoadAddress, boolean waitForSharedRelro, String testRunnerClassName, int linkerImplementation) {
        this.mBaseLoadAddress = baseLoadAddress;
        this.mWaitForSharedRelro = waitForSharedRelro;
        this.mTestRunnerClassNameForTesting = testRunnerClassName;
        this.mLinkerImplementationForTesting = linkerImplementation;
    }

    public ChromiumLinkerParams(Intent intent) {
        this.mBaseLoadAddress = intent.getLongExtra(EXTRA_LINKER_PARAMS_BASE_LOAD_ADDRESS, 0);
        this.mWaitForSharedRelro = intent.getBooleanExtra(EXTRA_LINKER_PARAMS_WAIT_FOR_SHARED_RELRO, false);
        this.mTestRunnerClassNameForTesting = intent.getStringExtra(EXTRA_LINKER_PARAMS_TEST_RUNNER_CLASS_NAME);
        this.mLinkerImplementationForTesting = intent.getIntExtra(EXTRA_LINKER_PARAMS_LINKER_IMPLEMENTATION, 0);
    }

    public void addIntentExtras(Intent intent) {
        intent.putExtra(EXTRA_LINKER_PARAMS_BASE_LOAD_ADDRESS, this.mBaseLoadAddress);
        intent.putExtra(EXTRA_LINKER_PARAMS_WAIT_FOR_SHARED_RELRO, this.mWaitForSharedRelro);
        intent.putExtra(EXTRA_LINKER_PARAMS_TEST_RUNNER_CLASS_NAME, this.mTestRunnerClassNameForTesting);
        intent.putExtra(EXTRA_LINKER_PARAMS_LINKER_IMPLEMENTATION, this.mLinkerImplementationForTesting);
    }

    public String toString() {
        Locale locale = Locale.US;
        String str = "LinkerParams(baseLoadAddress:0x%x, waitForSharedRelro:%s, testRunnerClassName:%s, linkerImplementation:%d";
        Object[] objArr = new Object[4];
        objArr[0] = Long.valueOf(this.mBaseLoadAddress);
        objArr[1] = this.mWaitForSharedRelro ? "true" : "false";
        objArr[2] = this.mTestRunnerClassNameForTesting;
        objArr[3] = Integer.valueOf(this.mLinkerImplementationForTesting);
        return String.format(locale, str, objArr);
    }
}
