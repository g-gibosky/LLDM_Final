package org.chromium.mojo.bindings;

public interface Callbacks {

    public interface Callback0 {
        void call();
    }

    public interface Callback1<T1> {
        void call(T1 t1);
    }

    public interface Callback2<T1, T2> {
        void call(T1 t1, T2 t2);
    }

    public interface Callback3<T1, T2, T3> {
        void call(T1 t1, T2 t2, T3 t3);
    }

    public interface Callback4<T1, T2, T3, T4> {
        void call(T1 t1, T2 t2, T3 t3, T4 t4);
    }

    public interface Callback5<T1, T2, T3, T4, T5> {
        void call(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
    }

    public interface Callback6<T1, T2, T3, T4, T5, T6> {
        void call(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);
    }

    public interface Callback7<T1, T2, T3, T4, T5, T6, T7> {
        void call(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);
    }
}
