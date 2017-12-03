package org.xwalk.core.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.RejectedExecutionException;

class ReflectConstructor {
    private Class<?> mClass;
    private Constructor<?> mConstructor;
    private Class<?>[] mParameterTypes;

    public ReflectConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        init(clazz, parameterTypes);
    }

    public boolean init(Class<?> clazz, Class<?>... parameterTypes) {
        boolean z = true;
        this.mClass = clazz;
        this.mParameterTypes = parameterTypes;
        this.mConstructor = null;
        if (this.mClass == null) {
            return false;
        }
        try {
            this.mConstructor = this.mClass.getConstructor(this.mParameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                this.mConstructor = this.mClass.getDeclaredConstructor(this.mParameterTypes);
                this.mConstructor.setAccessible(true);
            } catch (NoSuchMethodException e2) {
            }
        }
        if (this.mConstructor == null) {
            z = false;
        }
        return z;
    }

    public Object newInstance(Object... args) {
        ReflectiveOperationException e;
        if (this.mConstructor == null) {
            throw new UnsupportedOperationException(toString());
        }
        try {
            return this.mConstructor.newInstance(args);
        } catch (IllegalAccessException e2) {
            e = e2;
            throw new RejectedExecutionException(e);
        } catch (InstantiationException e3) {
            e = e3;
            throw new RejectedExecutionException(e);
        } catch (IllegalArgumentException e4) {
            throw e4;
        } catch (InvocationTargetException e5) {
            throw new RuntimeException(e5.getCause());
        }
    }

    public boolean isNull() {
        return this.mConstructor == null;
    }

    public String toString() {
        if (this.mConstructor != null) {
            return this.mConstructor.toString();
        }
        String ret = "";
        if (this.mClass != null) {
            return ret + this.mClass.toString();
        }
        return ret;
    }
}
