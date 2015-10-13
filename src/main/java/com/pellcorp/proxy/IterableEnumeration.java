package com.pellcorp.proxy;

import java.util.Enumeration;
import java.util.Iterator;

public class IterableEnumeration<T> implements Iterable<T> {
    private final Enumeration<T> enumeration;

    private IterableEnumeration(Enumeration<T> enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return IterableEnumeration.this.enumeration.hasMoreElements();
            }

            @Override
            public T next() {
                return IterableEnumeration.this.enumeration.nextElement();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }
    
    static <T> Iterable<T> iterable(Enumeration<T> enumeration) {
        return new IterableEnumeration<T>(enumeration);
    }
}