package org.chromium.content.common;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.view.Surface;
import org.chromium.base.annotations.MainDex;

@MainDex
public class SurfaceWrapper implements Parcelable {
    public static final Creator<SurfaceWrapper> CREATOR = new C02401();
    private final Surface mSurface;

    static class C02401 implements Creator<SurfaceWrapper> {
        C02401() {
        }

        public SurfaceWrapper createFromParcel(Parcel in) {
            return new SurfaceWrapper((Surface) Surface.CREATOR.createFromParcel(in));
        }

        public SurfaceWrapper[] newArray(int size) {
            return new SurfaceWrapper[size];
        }
    }

    public SurfaceWrapper(Surface surface) {
        this.mSurface = surface;
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        this.mSurface.writeToParcel(out, 0);
    }
}
