package org.xwalk.core.internal;

public class XWalkHttpAuthHandlerBridge extends XWalkHttpAuthHandlerInternal {
    private ReflectMethod cancelMethod = new ReflectMethod(null, "cancel", new Class[0]);
    private XWalkCoreBridge coreBridge;
    private XWalkHttpAuthHandlerInternal internal;
    private ReflectMethod isFirstAttemptMethod = new ReflectMethod(null, "isFirstAttempt", new Class[0]);
    private ReflectMethod proceedStringStringMethod = new ReflectMethod(null, "proceed", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    XWalkHttpAuthHandlerBridge(XWalkHttpAuthHandlerInternal internal) {
        this.internal = internal;
        reflectionInit();
    }

    public void proceed(String username, String password) {
        if (this.proceedStringStringMethod == null || this.proceedStringStringMethod.isNull()) {
            proceedSuper(username, password);
            return;
        }
        this.proceedStringStringMethod.invoke(username, password);
    }

    public void proceedSuper(String username, String password) {
        if (this.internal == null) {
            super.proceed(username, password);
        } else {
            this.internal.proceed(username, password);
        }
    }

    public void cancel() {
        if (this.cancelMethod == null || this.cancelMethod.isNull()) {
            cancelSuper();
        } else {
            this.cancelMethod.invoke(new Object[0]);
        }
    }

    public void cancelSuper() {
        if (this.internal == null) {
            super.cancel();
        } else {
            this.internal.cancel();
        }
    }

    public boolean isFirstAttempt() {
        if (this.isFirstAttemptMethod == null || this.isFirstAttemptMethod.isNull()) {
            return isFirstAttemptSuper();
        }
        return ((Boolean) this.isFirstAttemptMethod.invoke(new Object[0])).booleanValue();
    }

    public boolean isFirstAttemptSuper() {
        if (this.internal == null) {
            return super.isFirstAttempt();
        }
        return this.internal.isFirstAttempt();
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            try {
                this.wrapper = new ReflectConstructor(this.coreBridge.getWrapperClass("XWalkHttpAuthHandler"), Object.class).newInstance(this);
                this.proceedStringStringMethod.init(this.wrapper, null, "proceed", String.class, String.class);
                this.cancelMethod.init(this.wrapper, null, "cancel", new Class[0]);
                this.isFirstAttemptMethod.init(this.wrapper, null, "isFirstAttempt", new Class[0]);
            } catch (UnsupportedOperationException e) {
            }
        }
    }
}
