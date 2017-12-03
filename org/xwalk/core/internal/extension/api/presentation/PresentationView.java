package org.xwalk.core.internal.extension.api.presentation;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.Display;
import android.view.View;

public abstract class PresentationView {
    protected PresentationListener mListener;

    public interface PresentationListener {
        void onDismiss(PresentationView presentationView);

        void onShow(PresentationView presentationView);
    }

    public abstract void cancel();

    public abstract void dismiss();

    public abstract Display getDisplay();

    public abstract void setContentView(View view);

    public abstract void show();

    public static PresentationView createInstance(Context context, Display display) {
        if (VERSION.SDK_INT >= 17) {
            return new PresentationViewJBMR1(context, display);
        }
        return new PresentationViewNull();
    }

    public void setPresentationListener(PresentationListener listener) {
        this.mListener = listener;
    }
}
