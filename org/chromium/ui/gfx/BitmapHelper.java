package org.chromium.ui.gfx;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("gfx")
public class BitmapHelper {

    static /* synthetic */ class C02991 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config = new int[Config.values().length];

        static {
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ALPHA_8.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ARGB_4444.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.ARGB_8888.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$Bitmap$Config[Config.RGB_565.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    @CalledByNative
    private static Bitmap createBitmap(int width, int height, int bitmapFormatValue) {
        return Bitmap.createBitmap(width, height, getBitmapConfigForFormat(bitmapFormatValue));
    }

    @CalledByNative
    private static int getBitmapFormatForConfig(Config bitmapConfig) {
        switch (C02991.$SwitchMap$android$graphics$Bitmap$Config[bitmapConfig.ordinal()]) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            default:
                return 0;
        }
    }

    private static Config getBitmapConfigForFormat(int bitmapFormatValue) {
        switch (bitmapFormatValue) {
            case 1:
                return Config.ALPHA_8;
            case 2:
                return Config.ARGB_4444;
            case 4:
                return Config.RGB_565;
            default:
                return Config.ARGB_8888;
        }
    }

    @CalledByNative
    private static int getByteCount(Bitmap bitmap) {
        return bitmap.getByteCount();
    }
}
