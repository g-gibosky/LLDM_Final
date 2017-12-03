package org.xwalk.core.internal.extension.api.presentation;

import android.view.Display;
import android.view.View;

public class PresentationViewNull extends PresentationView {
    public void show() {
    }

    public void dismiss() {
    }

    public void cancel() {
    }

    public void setContentView(View contentView) {
    }

    public Display getDisplay() {
        return null;
    }
}
