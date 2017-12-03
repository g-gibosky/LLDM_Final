package org.chromium.base;

import android.annotation.SuppressLint;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.MainDex;

@MainDex
class SystemMessageHandler extends Handler {
    private static final int DELAYED_SCHEDULED_WORK = 2;
    private static final int SCHEDULED_WORK = 1;
    private static final String TAG = "cr.SysMessageHandler";
    private long mDelayedScheduledTimeTicks = 0;
    private long mMessagePumpDelegateNative = 0;

    private static class MessageCompat {
        static final MessageWrapperImpl IMPL;

        interface MessageWrapperImpl {
            void setAsynchronous(Message message, boolean z);
        }

        static class LegacyMessageWrapperImpl implements MessageWrapperImpl {
            private Method mMessageMethodSetAsynchronous;

            LegacyMessageWrapperImpl() {
                try {
                    this.mMessageMethodSetAsynchronous = Class.forName("android.os.Message").getMethod("setAsynchronous", new Class[]{Boolean.TYPE});
                } catch (ClassNotFoundException e) {
                    Log.m28e(SystemMessageHandler.TAG, "Failed to find android.os.Message class", e);
                } catch (NoSuchMethodException e2) {
                    Log.m28e(SystemMessageHandler.TAG, "Failed to load Message.setAsynchronous method", e2);
                } catch (RuntimeException e3) {
                    Log.m28e(SystemMessageHandler.TAG, "Exception while loading Message.setAsynchronous method", e3);
                }
            }

            public void setAsynchronous(Message msg, boolean async) {
                if (this.mMessageMethodSetAsynchronous != null) {
                    try {
                        this.mMessageMethodSetAsynchronous.invoke(msg, new Object[]{Boolean.valueOf(async)});
                    } catch (IllegalAccessException e) {
                        Log.m28e(SystemMessageHandler.TAG, "Illegal access to async message creation, disabling.", new Object[0]);
                        this.mMessageMethodSetAsynchronous = null;
                    } catch (IllegalArgumentException e2) {
                        Log.m28e(SystemMessageHandler.TAG, "Illegal argument for async message creation, disabling.", new Object[0]);
                        this.mMessageMethodSetAsynchronous = null;
                    } catch (InvocationTargetException e3) {
                        Log.m28e(SystemMessageHandler.TAG, "Invocation exception during async message creation, disabling.", new Object[0]);
                        this.mMessageMethodSetAsynchronous = null;
                    } catch (RuntimeException e4) {
                        Log.m28e(SystemMessageHandler.TAG, "Runtime exception during async message creation, disabling.", new Object[0]);
                        this.mMessageMethodSetAsynchronous = null;
                    }
                }
            }
        }

        static class LollipopMr1MessageWrapperImpl implements MessageWrapperImpl {
            LollipopMr1MessageWrapperImpl() {
            }

            @SuppressLint({"NewApi"})
            public void setAsynchronous(Message msg, boolean async) {
                msg.setAsynchronous(async);
            }
        }

        private MessageCompat() {
        }

        public static void setAsynchronous(Message message, boolean async) {
            IMPL.setAsynchronous(message, async);
        }

        static {
            if (VERSION.SDK_INT >= 22) {
                IMPL = new LollipopMr1MessageWrapperImpl();
            } else {
                IMPL = new LegacyMessageWrapperImpl();
            }
        }
    }

    private native void nativeDoRunLoopOnce(long j, long j2);

    private SystemMessageHandler(long messagePumpDelegateNative) {
        this.mMessagePumpDelegateNative = messagePumpDelegateNative;
    }

    public void handleMessage(Message msg) {
        if (msg.what == 2) {
            this.mDelayedScheduledTimeTicks = 0;
        }
        nativeDoRunLoopOnce(this.mMessagePumpDelegateNative, this.mDelayedScheduledTimeTicks);
    }

    @CalledByNative
    private void scheduleWork() {
        sendMessage(obtainAsyncMessage(1));
    }

    @CalledByNative
    private void scheduleDelayedWork(long delayedTimeTicks, long millis) {
        if (this.mDelayedScheduledTimeTicks != 0) {
            removeMessages(2);
        }
        this.mDelayedScheduledTimeTicks = delayedTimeTicks;
        sendMessageDelayed(obtainAsyncMessage(2), millis);
    }

    @CalledByNative
    private void removeAllPendingMessages() {
        removeMessages(1);
        removeMessages(2);
    }

    private Message obtainAsyncMessage(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        MessageCompat.setAsynchronous(msg, true);
        return msg;
    }

    @CalledByNative
    private static SystemMessageHandler create(long messagePumpDelegateNative) {
        return new SystemMessageHandler(messagePumpDelegateNative);
    }
}
