package org.chromium.net;

public class CertVerifyStatusAndroid {
    public static final int EXPIRED = -3;
    public static final int FAILED = -1;
    public static final int INCORRECT_KEY_USAGE = -6;
    public static final int NOT_YET_VALID = -4;
    public static final int NO_TRUSTED_ROOT = -2;
    public static final int OK = 0;
    public static final int UNABLE_TO_PARSE = -5;
}
