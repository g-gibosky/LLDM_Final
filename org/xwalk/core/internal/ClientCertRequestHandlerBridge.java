package org.xwalk.core.internal;

import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

public class ClientCertRequestHandlerBridge extends ClientCertRequestHandlerInternal {
    private ReflectMethod cancelMethod = new ReflectMethod(null, "cancel", new Class[0]);
    private XWalkCoreBridge coreBridge;
    private ReflectMethod getHostMethod = new ReflectMethod(null, "getHost", new Class[0]);
    private ReflectMethod getKeyTypesMethod = new ReflectMethod(null, "getKeyTypes", new Class[0]);
    private ReflectMethod getPortMethod = new ReflectMethod(null, "getPort", new Class[0]);
    private ReflectMethod getPrincipalsMethod = new ReflectMethod(null, "getPrincipals", new Class[0]);
    private ReflectMethod ignoreMethod = new ReflectMethod(null, "ignore", new Class[0]);
    private ClientCertRequestHandlerInternal internal;
    private ReflectMethod proceedPrivateKeyListMethod = new ReflectMethod(null, "proceed", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    ClientCertRequestHandlerBridge(ClientCertRequestHandlerInternal internal) {
        this.internal = internal;
        reflectionInit();
    }

    public void proceed(PrivateKey privateKey, List<X509Certificate> chain) {
        if (this.proceedPrivateKeyListMethod == null || this.proceedPrivateKeyListMethod.isNull()) {
            proceedSuper(privateKey, chain);
            return;
        }
        this.proceedPrivateKeyListMethod.invoke(privateKey, chain);
    }

    public void proceedSuper(PrivateKey privateKey, List<X509Certificate> chain) {
        if (this.internal == null) {
            super.proceed(privateKey, chain);
        } else {
            this.internal.proceed(privateKey, chain);
        }
    }

    public void ignore() {
        if (this.ignoreMethod == null || this.ignoreMethod.isNull()) {
            ignoreSuper();
        } else {
            this.ignoreMethod.invoke(new Object[0]);
        }
    }

    public void ignoreSuper() {
        if (this.internal == null) {
            super.ignore();
        } else {
            this.internal.ignore();
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

    public String getHost() {
        if (this.getHostMethod == null || this.getHostMethod.isNull()) {
            return getHostSuper();
        }
        return (String) this.getHostMethod.invoke(new Object[0]);
    }

    public String getHostSuper() {
        String ret;
        if (this.internal == null) {
            ret = super.getHost();
        } else {
            ret = this.internal.getHost();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public int getPort() {
        if (this.getPortMethod == null || this.getPortMethod.isNull()) {
            return getPortSuper();
        }
        return ((Integer) this.getPortMethod.invoke(new Object[0])).intValue();
    }

    public int getPortSuper() {
        if (this.internal == null) {
            return super.getPort();
        }
        return this.internal.getPort();
    }

    public String[] getKeyTypes() {
        if (this.getKeyTypesMethod == null || this.getKeyTypesMethod.isNull()) {
            return getKeyTypesSuper();
        }
        return (String[]) this.getKeyTypesMethod.invoke(new Object[0]);
    }

    public String[] getKeyTypesSuper() {
        String[] ret;
        if (this.internal == null) {
            ret = super.getKeyTypes();
        } else {
            ret = this.internal.getKeyTypes();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public Principal[] getPrincipals() {
        if (this.getPrincipalsMethod == null || this.getPrincipalsMethod.isNull()) {
            return getPrincipalsSuper();
        }
        return (Principal[]) this.getPrincipalsMethod.invoke(new Object[0]);
    }

    public Principal[] getPrincipalsSuper() {
        Principal[] ret;
        if (this.internal == null) {
            ret = super.getPrincipals();
        } else {
            ret = this.internal.getPrincipals();
        }
        if (ret == null) {
            return null;
        }
        return ret;
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            try {
                this.wrapper = new ReflectConstructor(this.coreBridge.getWrapperClass("ClientCertRequestHandler"), Object.class).newInstance(this);
                this.proceedPrivateKeyListMethod.init(this.wrapper, null, "proceed", PrivateKey.class, List.class);
                this.ignoreMethod.init(this.wrapper, null, "ignore", new Class[0]);
                this.cancelMethod.init(this.wrapper, null, "cancel", new Class[0]);
                this.getHostMethod.init(this.wrapper, null, "getHost", new Class[0]);
                this.getPortMethod.init(this.wrapper, null, "getPort", new Class[0]);
                this.getKeyTypesMethod.init(this.wrapper, null, "getKeyTypes", new Class[0]);
                this.getPrincipalsMethod.init(this.wrapper, null, "getPrincipals", new Class[0]);
            } catch (UnsupportedOperationException e) {
            }
        }
    }
}
