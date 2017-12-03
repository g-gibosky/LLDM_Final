package org.xwalk.core;

import java.lang.reflect.Field;
import java.util.concurrent.RejectedExecutionException;

class ReflectField {
    private Class<?> mClass;
    private Field mField;
    private Object mInstance;
    private String mName;

    public ReflectField(Object instance, String name) {
        init(instance, null, name);
    }

    public ReflectField(Class<?> clazz, String name) {
        init(null, clazz, name);
    }

    public boolean init(Object instance, Class<?> clazz, String name) {
        this.mInstance = instance;
        if (clazz == null) {
            clazz = instance != null ? instance.getClass() : null;
        }
        this.mClass = clazz;
        this.mName = name;
        this.mField = null;
        if (this.mClass == null) {
            return false;
        }
        boolean z;
        try {
            this.mField = this.mClass.getField(this.mName);
        } catch (NoSuchFieldException e) {
            Class<?> parent = this.mClass;
            while (parent != null) {
                try {
                    this.mField = parent.getDeclaredField(this.mName);
                    this.mField.setAccessible(true);
                    break;
                } catch (NoSuchFieldException e2) {
                    parent = parent.getSuperclass();
                }
            }
        }
        if (this.mField != null) {
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    public Object get() {
        Exception e;
        if (this.mField == null) {
            throw new UnsupportedOperationException(toString());
        }
        try {
            return this.mField.get(this.mInstance);
        } catch (IllegalAccessException e2) {
            e = e2;
            throw new RejectedExecutionException(e);
        } catch (NullPointerException e3) {
            e = e3;
            throw new RejectedExecutionException(e);
        } catch (IllegalArgumentException e4) {
            throw e4;
        } catch (ExceptionInInitializerError e5) {
            throw new RuntimeException(e5);
        }
    }

    public boolean isNull() {
        return this.mField == null;
    }

    public String toString() {
        if (this.mField != null) {
            return this.mField.toString();
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
}
