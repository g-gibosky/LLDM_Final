package org.chromium.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public final class CollectionUtil {
    private CollectionUtil() {
    }

    @SafeVarargs
    public static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = new HashSet(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    @SafeVarargs
    public static <E> ArrayList<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList(elements.length);
        Collections.addAll(list, elements);
        return list;
    }

    @VisibleForTesting
    public static <E> ArrayList<E> newArrayList(Iterable<E> iterable) {
        ArrayList<E> list = new ArrayList();
        for (E element : iterable) {
            list.add(element);
        }
        return list;
    }
}
