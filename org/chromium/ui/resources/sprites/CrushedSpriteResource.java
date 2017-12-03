package org.chromium.ui.resources.sprites;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.util.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.chromium.base.TraceEvent;
import org.chromium.base.VisibleForTesting;
import org.chromium.ui.resources.Resource;

public class CrushedSpriteResource implements Resource {
    static final /* synthetic */ boolean $assertionsDisabled = (!CrushedSpriteResource.class.desiredAssertionStatus());
    private static final Rect EMPTY_RECT = new Rect();
    private Bitmap mBitmap;
    private final Rect mBitmapSize = new Rect();
    private int[][] mRectangles;
    private float mScaledSpriteHeight;
    private float mScaledSpriteWidth;
    private int mUnscaledSpriteHeight;
    private int mUnscaledSpriteWidth;

    public CrushedSpriteResource(int bitmapResId, int metadataResId, Resources resources) {
        this.mBitmap = loadBitmap(bitmapResId, resources);
        if (this.mBitmap != null) {
            this.mBitmapSize.set(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
            try {
                TraceEvent.begin("CrushedSpriteResource.parseMetadata");
                parseMetadata(metadataResId, this.mBitmap.getDensity(), resources);
                TraceEvent.end("CrushedSpriteResource.parseMetadata");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap loadBitmap(int bitmapResId, Resources resources) {
        TraceEvent.begin("CrushedSpriteResource.loadBitmap");
        Options opts = new Options();
        opts.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, bitmapResId, opts);
        TraceEvent.end("CrushedSpriteResource.loadBitmap");
        return bitmap;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    public Rect getBitmapSize() {
        return this.mBitmapSize;
    }

    public Rect getPadding() {
        return EMPTY_RECT;
    }

    public Rect getAperture() {
        return EMPTY_RECT;
    }

    public float getScaledSpriteWidth() {
        return this.mScaledSpriteWidth;
    }

    public float getScaledSpriteHeight() {
        return this.mScaledSpriteHeight;
    }

    public int getUnscaledSpriteWidth() {
        return this.mUnscaledSpriteWidth;
    }

    public int getUnscaledSpriteHeight() {
        return this.mUnscaledSpriteHeight;
    }

    public int[][] getFrameRectangles() {
        return (int[][]) this.mRectangles.clone();
    }

    @VisibleForTesting
    void parseMetadata(int metadataResId, int bitmapDensity, Resources resources) throws IOException {
        InputStream inputStream = resources.openRawResource(metadataResId);
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        try {
            reader.beginObject();
            String name = reader.nextName();
            if ($assertionsDisabled || name.equals("apiVersion")) {
                String version = reader.nextString();
                if ($assertionsDisabled || version.equals("1.0")) {
                    float dpToPx = resources.getDisplayMetrics().density;
                    name = reader.nextName();
                    if ($assertionsDisabled || name.equals("scaledSpriteWidthDp")) {
                        this.mScaledSpriteWidth = ((float) reader.nextInt()) * dpToPx;
                        name = reader.nextName();
                        if ($assertionsDisabled || name.equals("scaledSpriteHeightDp")) {
                            this.mScaledSpriteHeight = ((float) reader.nextInt()) * dpToPx;
                            name = reader.nextName();
                            if ($assertionsDisabled || name.equals("densities")) {
                                reader.beginArray();
                                while (reader.hasNext()) {
                                    reader.beginObject();
                                    boolean foundDensity = parseMetadataForDensity(reader, bitmapDensity);
                                    reader.endObject();
                                    if (foundDensity) {
                                        break;
                                    }
                                }
                                reader.close();
                                inputStream.close();
                                return;
                            }
                            throw new AssertionError();
                        }
                        throw new AssertionError();
                    }
                    throw new AssertionError();
                }
                throw new AssertionError();
            }
            throw new AssertionError();
        } catch (Throwable th) {
            reader.close();
            inputStream.close();
        }
    }

    private boolean parseMetadataForDensity(JsonReader reader, int bitmapDensity) throws IOException {
        String name = reader.nextName();
        if (!$assertionsDisabled && !name.equals("density")) {
            throw new AssertionError();
        } else if (reader.nextInt() != bitmapDensity) {
            reader.skipValue();
            reader.skipValue();
            reader.skipValue();
            reader.skipValue();
            reader.skipValue();
            reader.skipValue();
            return false;
        } else {
            name = reader.nextName();
            if ($assertionsDisabled || name.equals("width")) {
                this.mUnscaledSpriteWidth = reader.nextInt();
                name = reader.nextName();
                if ($assertionsDisabled || name.equals("height")) {
                    this.mUnscaledSpriteHeight = reader.nextInt();
                    name = reader.nextName();
                    if ($assertionsDisabled || name.equals("rectangles")) {
                        parseFrameRectangles(reader);
                        return true;
                    }
                    throw new AssertionError();
                }
                throw new AssertionError();
            }
            throw new AssertionError();
        }
    }

    private void parseFrameRectangles(JsonReader reader) throws IOException {
        ArrayList<ArrayList<Integer>> allFrameRectangles = new ArrayList();
        int frameCount = 0;
        reader.beginArray();
        while (reader.hasNext()) {
            ArrayList<Integer> frameRectangles = new ArrayList();
            reader.beginArray();
            while (reader.hasNext()) {
                frameRectangles.add(Integer.valueOf(reader.nextInt()));
            }
            reader.endArray();
            allFrameRectangles.add(frameRectangles);
            frameCount++;
        }
        reader.endArray();
        this.mRectangles = new int[frameCount][];
        for (int i = 0; i < frameCount; i++) {
            frameRectangles = (ArrayList) allFrameRectangles.get(i);
            int[] frameRectanglesArray = new int[frameRectangles.size()];
            for (int j = 0; j < frameRectangles.size(); j++) {
                frameRectanglesArray[j] = ((Integer) frameRectangles.get(j)).intValue();
            }
            this.mRectangles[i] = frameRectanglesArray;
        }
    }
}
