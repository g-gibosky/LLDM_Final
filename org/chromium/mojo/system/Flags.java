package org.chromium.mojo.system;

public abstract class Flags<F extends Flags<F>> {
    private int mFlags;
    private boolean mImmutable = false;

    protected Flags(int flags) {
        this.mFlags = flags;
    }

    public int getFlags() {
        return this.mFlags;
    }

    protected F setFlag(int flag, boolean value) {
        if (this.mImmutable) {
            throw new UnsupportedOperationException("Flags is immutable.");
        }
        if (value) {
            this.mFlags |= flag;
        } else {
            this.mFlags &= flag ^ -1;
        }
        return this;
    }

    protected F immutable() {
        this.mImmutable = true;
        return this;
    }

    public int hashCode() {
        return this.mFlags;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (this.mFlags != ((Flags) obj).mFlags) {
            return false;
        }
        return true;
    }
}
