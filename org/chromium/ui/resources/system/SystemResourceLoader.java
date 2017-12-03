package org.chromium.ui.resources.system;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import org.chromium.ui.resources.Resource;
import org.chromium.ui.resources.ResourceLoader.ResourceLoaderCallback;
import org.chromium.ui.resources.async.AsyncPreloadResourceLoader;
import org.chromium.ui.resources.async.AsyncPreloadResourceLoader.ResourceCreator;
import org.chromium.ui.resources.statics.StaticResource;

public class SystemResourceLoader extends AsyncPreloadResourceLoader {
    static final /* synthetic */ boolean $assertionsDisabled = (!SystemResourceLoader.class.desiredAssertionStatus());
    private static final float COS_PI_OVER_6 = 0.866f;
    private static final float SIN_PI_OVER_6 = 0.5f;

    class C04591 implements ResourceCreator {
        final /* synthetic */ int val$minScreenSideLengthPx;

        C04591(int i) {
            this.val$minScreenSideLengthPx = i;
        }

        public Resource create(int resId) {
            return SystemResourceLoader.createResource(this.val$minScreenSideLengthPx, resId);
        }
    }

    public SystemResourceLoader(int resourceType, ResourceLoaderCallback callback, int minScreenSideLengthPx) {
        super(resourceType, callback, new C04591(minScreenSideLengthPx));
    }

    private static Resource createResource(int minScreenSideLengthPx, int resId) {
        switch (resId) {
            case 0:
                return StaticResource.create(Resources.getSystem(), getResourceId("android:drawable/overscroll_edge"), 128, 12);
            case 1:
                return StaticResource.create(Resources.getSystem(), getResourceId("android:drawable/overscroll_glow"), 128, 64);
            case 2:
                return createOverscrollGlowLBitmap(minScreenSideLengthPx);
            default:
                if ($assertionsDisabled) {
                    return null;
                }
                throw new AssertionError();
        }
    }

    private static Resource createOverscrollGlowLBitmap(int minScreenSideLengthPx) {
        float arcWidth = (((float) minScreenSideLengthPx) * SIN_PI_OVER_6) / SIN_PI_OVER_6;
        float y = COS_PI_OVER_6 * arcWidth;
        float height = arcWidth - y;
        float arcRectX = (-arcWidth) / 2.0f;
        float arcRectY = (-arcWidth) - y;
        RectF arcRect = new RectF(arcRectX, arcRectY, arcRectX + (arcWidth * 2.0f), arcRectY + (arcWidth * 2.0f));
        Paint arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setAlpha(187);
        arcPaint.setStyle(Style.FILL);
        Bitmap bitmap = Bitmap.createBitmap((int) arcWidth, (int) height, Config.ALPHA_8);
        new Canvas(bitmap).drawArc(arcRect, 45.0f, 90.0f, true, arcPaint);
        return new StaticResource(bitmap);
    }

    private static int getResourceId(String name) {
        return Resources.getSystem().getIdentifier(name, null, null);
    }
}
