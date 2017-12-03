package org.chromium.base;

import android.os.Handler;
import java.util.LinkedList;
import java.util.List;

public class Promise<T> {
    static final /* synthetic */ boolean $assertionsDisabled = (!Promise.class.desiredAssertionStatus());
    private static final int FULFILLED = 1;
    private static final int REJECTED = 2;
    private static final int UNFULFILLED = 0;
    private final List<Callback<T>> mFulfillCallbacks = new LinkedList();
    private final Handler mHandler = new Handler();
    private final List<Callback<Exception>> mRejectCallbacks = new LinkedList();
    private Exception mRejectReason;
    private T mResult;
    private int mState = 0;
    private final Thread mThread = Thread.currentThread();
    private boolean mThrowingRejectionHandler;

    public interface AsyncFunction<A, R> {
        Promise<R> apply(A a);
    }

    public interface Function<A, R> {
        R apply(A a);
    }

    public static class UnhandledRejectionException extends RuntimeException {
        public UnhandledRejectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    class C04261 extends Callback<Exception> {
        C04261() {
        }

        public void onResult(Exception reason) {
            throw new UnhandledRejectionException("Promise was rejected without a rejection handler.", reason);
        }
    }

    class C04272 extends Callback<T> {
        C04272() {
        }

        public void onResult(T result) {
            Promise.this.fulfill(result);
        }
    }

    public void then(Callback<T> onFulfill) {
        checkThread();
        if (this.mThrowingRejectionHandler) {
            thenInner(onFulfill);
        } else if ($assertionsDisabled || this.mRejectCallbacks.size() == 0) {
            then(onFulfill, new C04261());
            this.mThrowingRejectionHandler = true;
        } else {
            throw new AssertionError("Do not call the single argument Promise.then(Callback) on a Promise that already has a rejection handler.");
        }
    }

    public void then(Callback<T> onFulfill, Callback<Exception> onReject) {
        checkThread();
        thenInner(onFulfill);
        exceptInner(onReject);
    }

    public void except(Callback<Exception> onReject) {
        checkThread();
        exceptInner(onReject);
    }

    public Callback<T> fulfillmentCallback() {
        return new C04272();
    }

    private void thenInner(Callback<T> onFulfill) {
        if (this.mState == 1) {
            postCallbackToLooper(onFulfill, this.mResult);
        } else if (this.mState == 0) {
            this.mFulfillCallbacks.add(onFulfill);
        }
    }

    private void exceptInner(Callback<Exception> onReject) {
        if (!$assertionsDisabled && this.mThrowingRejectionHandler) {
            throw new AssertionError("Do not add an exception handler to a Promise you have called the single argument Promise.then(Callback) on.");
        } else if (this.mState == 2) {
            postCallbackToLooper(onReject, this.mRejectReason);
        } else if (this.mState == 0) {
            this.mRejectCallbacks.add(onReject);
        }
    }

    public <R> Promise<R> then(final Function<T, R> function) {
        checkThread();
        final Promise<R> promise = new Promise();
        thenInner(new Callback<T>() {
            public void onResult(T result) {
                try {
                    promise.fulfill(function.apply(result));
                } catch (Exception e) {
                    promise.reject(e);
                }
            }
        });
        exceptInner(rejectPromiseCallback(promise));
        return promise;
    }

    public <R> Promise<R> then(final AsyncFunction<T, R> function) {
        checkThread();
        final Promise<R> promise = new Promise();
        thenInner(new Callback<T>() {

            class C04291 extends Callback<R> {
                C04291() {
                }

                public void onResult(R result) {
                    promise.fulfill(result);
                }
            }

            public void onResult(T result) {
                try {
                    function.apply(result).then(new C04291(), Promise.rejectPromiseCallback(promise));
                } catch (Exception e) {
                    promise.reject(e);
                }
            }
        });
        exceptInner(rejectPromiseCallback(promise));
        return promise;
    }

    public void fulfill(T result) {
        checkThread();
        if ($assertionsDisabled || this.mState == 0) {
            this.mState = 1;
            this.mResult = result;
            for (Callback<T> callback : this.mFulfillCallbacks) {
                postCallbackToLooper(callback, result);
            }
            this.mFulfillCallbacks.clear();
            return;
        }
        throw new AssertionError();
    }

    public void reject(Exception reason) {
        checkThread();
        if ($assertionsDisabled || this.mState == 0) {
            this.mState = 2;
            this.mRejectReason = reason;
            for (Callback<Exception> callback : this.mRejectCallbacks) {
                postCallbackToLooper(callback, reason);
            }
            this.mRejectCallbacks.clear();
            return;
        }
        throw new AssertionError();
    }

    public void reject() {
        reject(null);
    }

    public boolean isFulfilled() {
        checkThread();
        if (this.mState == 1) {
            return true;
        }
        return false;
    }

    public boolean isRejected() {
        checkThread();
        return this.mState == 2;
    }

    public static <T> Promise<T> fulfilled(T result) {
        Promise<T> promise = new Promise();
        promise.fulfill(result);
        return promise;
    }

    private void checkThread() {
        if (!$assertionsDisabled && this.mThread != Thread.currentThread()) {
            throw new AssertionError("Promise must only be used on a single Thread.");
        }
    }

    private <S> void postCallbackToLooper(final Callback<S> callback, final S result) {
        this.mHandler.post(new Runnable() {
            public void run() {
                callback.onResult(result);
            }
        });
    }

    private static <T> Callback<Exception> rejectPromiseCallback(final Promise<T> promise) {
        return new Callback<Exception>() {
            public void onResult(Exception reason) {
                promise.reject(reason);
            }
        };
    }
}
