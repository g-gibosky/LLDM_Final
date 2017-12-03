package org.xwalk.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.RejectedExecutionException;

class ReflectMethod {
    private Object[] mArguments;
    private Class<?> mClass;
    private Object mInstance;
    private Method mMethod;
    private String mName;
    private Class<?>[] mParameterTypes;

    public ReflectMethod(Object instance, String name, Class<?>... parameterTypes) {
        init(instance, null, name, parameterTypes);
    }

    public ReflectMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        init(null, clazz, name, parameterTypes);
    }

    public boolean init(Object instance, Class<?> clazz, String name, Class<?>... parameterTypes) {
        this.mInstance = instance;
        if (clazz == null) {
            clazz = instance != null ? instance.getClass() : null;
        }
        this.mClass = clazz;
        this.mName = name;
        this.mParameterTypes = parameterTypes;
        this.mMethod = null;
        if (this.mClass == null) {
            return false;
        }
        boolean z;
        try {
            this.mMethod = this.mClass.getMethod(this.mName, this.mParameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> parent = this.mClass;
            while (parent != null) {
                try {
                    this.mMethod = parent.getDeclaredMethod(this.mName, this.mParameterTypes);
                    this.mMethod.setAccessible(true);
                    break;
                } catch (NoSuchMethodException e2) {
                    parent = parent.getSuperclass();
                }
            }
        }
        if (this.mMethod != null) {
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    public Object invoke(Object... args) {
        Exception e;
        if (this.mMethod == null) {
            throw new UnsupportedOperationException(toString());
        }
        try {
            return this.mMethod.invoke(this.mInstance, args);
        } catch (IllegalAccessException e2) {
            e = e2;
            throw new RejectedExecutionException(e);
        } catch (NullPointerException e3) {
            e = e3;
            throw new RejectedExecutionException(e);
        } catch (IllegalArgumentException e4) {
            throw e4;
        } catch (InvocationTargetException e5) {
            throw new RuntimeException(e5.getCause());
        }
    }

    public boolean isNull() {
        return this.mMethod == null;
    }

    public String toString() {
        if (this.mMethod != null) {
            return this.mMethod.toString();
        }
        String ret = "";
        if (this.mClass != null) {
            ret = ret + this.mClass.toString() + ".";
        }
        if (this.mName != null) {
            return ret + this.mName;
        }
        return ret;
    }

    public String getName() {
        return this.mName;
    }

    public Object getInstance() {
        return this.mInstance;
    }

    public Object[] getArguments() {
        return this.mArguments;
    }

    public void setArguments(Object... args) {
        this.mArguments = args;
    }

    public Object invokeWithArguments() {
        return invoke(this.mArguments);
    }
}
