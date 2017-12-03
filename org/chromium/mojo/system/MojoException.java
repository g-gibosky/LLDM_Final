package org.chromium.mojo.system;

public class MojoException extends RuntimeException {
    private final int mCode;

    public MojoException(int code) {
        this.mCode = code;
    }

    public MojoException(Throwable cause) {
        super(cause);
        this.mCode = 2;
    }

    public int getMojoResult() {
        return this.mCode;
    }

    public String toString() {
        return "MojoResult(" + this.mCode + "): " + MojoResult.describe(this.mCode);
    }
}
