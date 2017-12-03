package org.chromium.content.browser;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import org.chromium.base.annotations.MainDex;

@MainDex
public final class FileDescriptorInfo implements Parcelable {
    public static final Creator<FileDescriptorInfo> CREATOR = new C01931();
    public final ParcelFileDescriptor mFd;
    public final int mId;
    public final long mOffset;
    public final long mSize;

    static class C01931 implements Creator<FileDescriptorInfo> {
        C01931() {
        }

        public FileDescriptorInfo createFromParcel(Parcel in) {
            return new FileDescriptorInfo(in);
        }

        public FileDescriptorInfo[] newArray(int size) {
            return new FileDescriptorInfo[size];
        }
    }

    FileDescriptorInfo(int id, ParcelFileDescriptor fd, long offset, long size) {
        this.mId = id;
        this.mFd = fd;
        this.mOffset = offset;
        this.mSize = size;
    }

    FileDescriptorInfo(Parcel in) {
        this.mId = in.readInt();
        this.mFd = (ParcelFileDescriptor) in.readParcelable(null);
        this.mOffset = in.readLong();
        this.mSize = in.readLong();
    }

    public int describeContents() {
        return 1;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeParcelable(this.mFd, 1);
        dest.writeLong(this.mOffset);
        dest.writeLong(this.mSize);
    }
}
