package com.codegans.ai.cup2016.navigator;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2016 14:22
 */
public final class FixedQueue<T> {
    private final T[] data;
    private final int capacity;
    private int tail;
    private int size;

    public FixedQueue(T[] array) {
        this.data = array.clone();
        this.capacity = array.length;
        this.size = 0;
        this.tail = 0;
    }

    public void clear() {
        size = 0;
        Arrays.setAll(data, i -> null);
    }

    public void offer(T obj) {
        tail = (tail + 1) % capacity;

        if (size < capacity) {
            size++;
        }

        data[tail] = obj;
    }

    public T head(int i) {
        if (size == 0) {
            throw new IndexOutOfBoundsException("Empty queue cannot be queried");
        }

        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("Should be within the interval: [0.." + size + ")");
        }


        return data[(capacity + tail - size + 1 + i) % capacity];
    }

    public T tail(int i) {
        if (size == 0) {
            throw new IndexOutOfBoundsException("Empty queue cannot be queried");
        }

        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("Should be within the interval: [0.." + size + ")");
        }


        return data[(data.length + tail - i) % data.length];
    }

    public void remove() {
        if (size == 0) {
            throw new IndexOutOfBoundsException("Empty queue");
        }

        data[tail] = null;
        tail = (capacity + tail - 1) % capacity;
        size--;
    }

    public int size() {
        return size;
    }

    public T[] toArray() {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(data.getClass().getComponentType(), size);

        for (int i = 0; i < size; i++) {
            result[i] = tail(i);
        }

        return result;
    }
}
