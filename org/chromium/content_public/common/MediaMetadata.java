package org.chromium.content_public.common;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("content")
public class MediaMetadata {
    @NonNull
    private String mAlbum;
    @NonNull
    private String mArtist;
    @NonNull
    private String mTitle;

    public String getTitle() {
        return this.mTitle;
    }

    public String getArtist() {
        return this.mArtist;
    }

    public String getAlbum() {
        return this.mAlbum;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public void setAlbum(String album) {
        this.mAlbum = album;
    }

    @CalledByNative
    private static MediaMetadata create(String title, String artist, String album) {
        if (title == null) {
            title = "";
        }
        if (artist == null) {
            artist = "";
        }
        if (album == null) {
            album = "";
        }
        return new MediaMetadata(title, artist, album);
    }

    public MediaMetadata(@NonNull String title, @NonNull String artist, @NonNull String album) {
        this.mTitle = title;
        this.mArtist = artist;
        this.mAlbum = album;
    }

    public MediaMetadata(MediaMetadata other) {
        this(other.mTitle, other.mArtist, other.mAlbum);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MediaMetadata)) {
            return false;
        }
        MediaMetadata other = (MediaMetadata) obj;
        if (TextUtils.equals(this.mTitle, other.mTitle) && TextUtils.equals(this.mArtist, other.mArtist) && TextUtils.equals(this.mAlbum, other.mAlbum)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((this.mTitle.hashCode() * 31) + this.mArtist.hashCode()) * 31) + this.mAlbum.hashCode();
    }
}
