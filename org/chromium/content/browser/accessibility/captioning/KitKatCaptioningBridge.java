package org.chromium.content.browser.accessibility.captioning;

import android.annotation.TargetApi;
import android.content.Context;
import android.view.accessibility.CaptioningManager;
import android.view.accessibility.CaptioningManager.CaptionStyle;
import android.view.accessibility.CaptioningManager.CaptioningChangeListener;
import java.util.Locale;
import org.chromium.content.browser.accessibility.captioning.SystemCaptioningBridge.SystemCaptioningBridgeListener;

@TargetApi(19)
public class KitKatCaptioningBridge implements SystemCaptioningBridge {
    private static KitKatCaptioningBridge sKitKatCaptioningBridge;
    private final CaptioningChangeDelegate mCaptioningChangeDelegate = new CaptioningChangeDelegate();
    private final CaptioningChangeListener mCaptioningChangeListener = new KitKatCaptioningChangeListener();
    private final CaptioningManager mCaptioningManager;

    private class KitKatCaptioningChangeListener extends CaptioningChangeListener {
        private KitKatCaptioningChangeListener() {
        }

        public void onEnabledChanged(boolean enabled) {
            KitKatCaptioningBridge.this.mCaptioningChangeDelegate.onEnabledChanged(enabled);
        }

        public void onFontScaleChanged(float fontScale) {
            KitKatCaptioningBridge.this.mCaptioningChangeDelegate.onFontScaleChanged(fontScale);
        }

        public void onLocaleChanged(Locale locale) {
            KitKatCaptioningBridge.this.mCaptioningChangeDelegate.onLocaleChanged(locale);
        }

        public void onUserStyleChanged(CaptionStyle userStyle) {
            KitKatCaptioningBridge.this.mCaptioningChangeDelegate.onUserStyleChanged(KitKatCaptioningBridge.this.getCaptioningStyleFrom(userStyle));
        }
    }

    public static KitKatCaptioningBridge getInstance(Context context) {
        if (sKitKatCaptioningBridge == null) {
            sKitKatCaptioningBridge = new KitKatCaptioningBridge(context);
        }
        return sKitKatCaptioningBridge;
    }

    private KitKatCaptioningBridge(Context context) {
        this.mCaptioningManager = (CaptioningManager) context.getApplicationContext().getSystemService("captioning");
    }

    private void syncToDelegate() {
        this.mCaptioningChangeDelegate.onEnabledChanged(this.mCaptioningManager.isEnabled());
        this.mCaptioningChangeDelegate.onFontScaleChanged(this.mCaptioningManager.getFontScale());
        this.mCaptioningChangeDelegate.onLocaleChanged(this.mCaptioningManager.getLocale());
        this.mCaptioningChangeDelegate.onUserStyleChanged(getCaptioningStyleFrom(this.mCaptioningManager.getUserStyle()));
    }

    public void syncToListener(SystemCaptioningBridgeListener listener) {
        if (!this.mCaptioningChangeDelegate.hasActiveListener()) {
            syncToDelegate();
        }
        this.mCaptioningChangeDelegate.notifyListener(listener);
    }

    public void addListener(SystemCaptioningBridgeListener listener) {
        if (!this.mCaptioningChangeDelegate.hasActiveListener()) {
            this.mCaptioningManager.addCaptioningChangeListener(this.mCaptioningChangeListener);
            syncToDelegate();
        }
        this.mCaptioningChangeDelegate.addListener(listener);
        this.mCaptioningChangeDelegate.notifyListener(listener);
    }

    public void removeListener(SystemCaptioningBridgeListener listener) {
        this.mCaptioningChangeDelegate.removeListener(listener);
        if (!this.mCaptioningChangeDelegate.hasActiveListener()) {
            this.mCaptioningManager.removeCaptioningChangeListener(this.mCaptioningChangeListener);
        }
    }

    private CaptioningStyle getCaptioningStyleFrom(CaptionStyle userStyle) {
        return CaptioningStyle.createFrom(userStyle);
    }
}
