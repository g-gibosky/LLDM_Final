package org.chromium.ui.resources.statics;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.chromium.base.ApiCompatibilityUtils;
import org.chromium.ui.resources.Resource;

public class StaticResource implements Resource {
    private final Bitmap mBitmap;
    private final Rect mBitmapSize = new Rect(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
    private final NinePatchData mNinePatchData = NinePatchData.create(this.mBitmap);

    public StaticResource(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public Rect getBitmapSize() {
        return this.mBitmapSize;
    }

    public Rect getPadding() {
        return this.mNinePatchData != null ? this.mNinePatchData.getPadding() : this.mBitmapSize;
    }

    public Rect getAperture() {
        return this.mNinePatchData != null ? this.mNinePatchData.getAperture() : this.mBitmapSize;
    }

    public static StaticResource create(Resources resources, int resId, int fitWidth, int fitHeight) {
        if (resId <= 0) {
            return null;
        }
        Bitmap bitmap = decodeBitmap(resources, resId, fitWidth, fitHeight);
        if (bitmap == null) {
            bitmap = decodeDrawable(resources, resId, fitWidth, fitHeight);
        }
        if (bitmap != null) {
            return new StaticResource(bitmap);
        }
        return null;
    }

    private static Bitmap decodeBitmap(Resources resources, int resId, int fitWidth, int fitHeight) {
        Options options = createOptions(resources, resId, fitWidth, fitHeight);
        options.inPreferredConfig = Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId, options);
        if (bitmap == null) {
            return null;
        }
        if (bitmap.getConfig() == options.inPreferredConfig) {
            return bitmap;
        }
        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), options.inPreferredConfig);
        new Canvas(convertedBitmap).drawBitmap(bitmap, 0.0f, 0.0f, null);
        bitmap.recycle();
        return convertedBitmap;
    }

    private static Bitmap decodeDrawable(Resources resources, int resId, int fitWidth, int fitHeight) {
        try {
            Drawable drawable = ApiCompatibilityUtils.getDrawable(resources, resId);
            int width = Math.max(drawable.getMinimumWidth(), Math.max(fitWidth, 1));
            int height = Math.max(drawable.getMinimumHeight(), Math.max(fitHeight, 1));
            Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            return bitmap;
        } catch (NotFoundException e) {
            return null;
        }
    }

    private static Options createOptions(Resources resources, int resId, int fitWidth, int fitHeight) {
        Options options = new Options();
        options.inPreferredConfig = Config.ARGB_8888;
        if (!(fitWidth == 0 || fitHeight == 0)) {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(resources, resId, options);
            options.inJustDecodeBounds = false;
            if (options.outHeight > fitHeight || options.outWidth > fitWidth) {
                options.inSampleSize = Math.min(Math.round(((float) options.outHeight) / ((float) fitHeight)), Math.round(((float) options.outWidth) / ((float) fitWidth)));
            }
        }
        return options;
    }
}
