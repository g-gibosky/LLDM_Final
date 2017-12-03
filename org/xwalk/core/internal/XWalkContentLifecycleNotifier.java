package org.xwalk.core.internal;

import java.util.Iterator;
import org.chromium.base.ObserverList;
import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;

@JNINamespace("xwalk")
public class XWalkContentLifecycleNotifier {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final ObserverList<Observer> sLifecycleObservers = new ObserverList();
    private static int sNumXWalkViews = 0;

    public interface Observer {
        void onFirstXWalkViewCreated();

        void onLastXWalkViewDestroyed();
    }

    static {
        boolean z;
        if (XWalkContentLifecycleNotifier.class.desiredAssertionStatus()) {
            z = false;
        } else {
            z = true;
        }
        $assertionsDisabled = z;
    }

    private XWalkContentLifecycleNotifier() {
    }

    public static void addObserver(Observer observer) {
        sLifecycleObservers.addObserver(observer);
    }

    public static void removeObserver(Observer observer) {
        sLifecycleObservers.removeObserver(observer);
    }

    public static boolean hasXWalkViewInstances() {
        return sNumXWalkViews > 0;
    }

    @CalledByNative
    private static void onXWalkViewCreated() {
        ThreadUtils.assertOnUiThread();
        if ($assertionsDisabled || sNumXWalkViews >= 0) {
            sNumXWalkViews++;
            if (sNumXWalkViews == 1) {
                Iterator i$ = sLifecycleObservers.iterator();
                while (i$.hasNext()) {
                    ((Observer) i$.next()).onFirstXWalkViewCreated();
                }
                return;
            }
            return;
        }
        throw new AssertionError();
    }

    @CalledByNative
    private static void onXWalkViewDestroyed() {
        ThreadUtils.assertOnUiThread();
        if ($assertionsDisabled || sNumXWalkViews > 0) {
            sNumXWalkViews--;
            if (sNumXWalkViews == 0) {
                Iterator i$ = sLifecycleObservers.iterator();
                while (i$.hasNext()) {
                    ((Observer) i$.next()).onLastXWalkViewDestroyed();
                }
                return;
            }
            return;
        }
        throw new AssertionError();
    }
}
