package org.chromium.content.browser;

import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.util.ArrayList;
import org.chromium.content.browser.PositionObserver.Listener;

public class ViewPositionObserver implements PositionObserver {
    private final ArrayList<Listener> mListeners;
    private final int[] mPosition = new int[2];
    private OnPreDrawListener mPreDrawListener;
    private View mView;

    class C02061 implements OnPreDrawListener {
        C02061() {
        }

        public boolean onPreDraw() {
            ViewPositionObserver.this.updatePosition();
            return true;
        }
    }

    public ViewPositionObserver(View view) {
        this.mView = view;
        this.mListeners = new ArrayList();
        updatePosition();
        this.mPreDrawListener = new C02061();
    }

    public int getPositionX() {
        updatePosition();
        return this.mPosition[0];
    }

    public int getPositionY() {
        updatePosition();
        return this.mPosition[1];
    }

    public void addListener(Listener listener) {
        if (!this.mListeners.contains(listener)) {
            if (this.mListeners.isEmpty()) {
                this.mView.getViewTreeObserver().addOnPreDrawListener(this.mPreDrawListener);
                updatePosition();
            }
            this.mListeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        if (this.mListeners.contains(listener)) {
            this.mListeners.remove(listener);
            if (this.mListeners.isEmpty()) {
                this.mView.getViewTreeObserver().removeOnPreDrawListener(this.mPreDrawListener);
            }
        }
    }

    private void notifyListeners() {
        for (int i = 0; i < this.mListeners.size(); i++) {
            ((Listener) this.mListeners.get(i)).onPositionChanged(this.mPosition[0], this.mPosition[1]);
        }
    }

    private void updatePosition() {
        int previousPositionX = this.mPosition[0];
        int previousPositionY = this.mPosition[1];
        this.mView.getLocationInWindow(this.mPosition);
        if (this.mPosition[0] != previousPositionX || this.mPosition[1] != previousPositionY) {
            notifyListeners();
        }
    }

    public void clearListener() {
        this.mListeners.clear();
    }
}
