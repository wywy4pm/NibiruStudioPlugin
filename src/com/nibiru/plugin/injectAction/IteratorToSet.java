package com.nibiru.plugin.injectAction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IteratorToSet {
    public static <T> Set<T> toSet(Iterator<? extends T> iteration) {
        Set<T> elements = new HashSet<T>(1);
        while (iteration.hasNext()) {
            elements.add(iteration.next());
        }
        return elements;
    }
}
