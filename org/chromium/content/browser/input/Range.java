package org.chromium.content.browser.input;

import org.chromium.base.VisibleForTesting;

public class Range {
    private int mEnd;
    private int mStart;

    public Range(int start, int end) {
        set(start, end);
    }

    public int start() {
        return this.mStart;
    }

    public int end() {
        return this.mEnd;
    }

    @VisibleForTesting
    public void set(int start, int end) {
        this.mStart = Math.min(start, end);
        this.mEnd = Math.max(start, end);
    }

    public void clamp(int start, int end) {
        this.mStart = Math.min(Math.max(this.mStart, start), end);
        this.mEnd = Math.max(Math.min(this.mEnd, end), start);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Range)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        Range r = (Range) o;
        if (this.mStart == r.mStart && this.mEnd == r.mEnd) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.mStart * 11) + (this.mEnd * 31);
    }

    public String toString() {
        return "[ " + this.mStart + ", " + this.mEnd + " ]";
    }
}
