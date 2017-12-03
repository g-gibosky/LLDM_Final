package org.chromium.content_public.browser;

import android.graphics.Bitmap;

public class NavigationEntry {
    private Bitmap mFavicon;
    private final int mIndex;
    private final String mOriginalUrl;
    private final String mTitle;
    private int mTransition;
    private final String mUrl;
    private final String mVirtualUrl;

    public NavigationEntry(int index, String url, String virtualUrl, String originalUrl, String title, Bitmap favicon, int transition) {
        this.mIndex = index;
        this.mUrl = url;
        this.mVirtualUrl = virtualUrl;
        this.mOriginalUrl = originalUrl;
        this.mTitle = title;
        this.mFavicon = favicon;
        this.mTransition = transition;
    }

    public int getIndex() {
        return this.mIndex;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public String getVirtualUrl() {
        return this.mVirtualUrl;
    }

    public String getOriginalUrl() {
        return this.mOriginalUrl;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public Bitmap getFavicon() {
        return this.mFavicon;
    }

    public void updateFavicon(Bitmap favicon) {
        this.mFavicon = favicon;
    }

    public int getTransition() {
        return this.mTransition;
    }
}
