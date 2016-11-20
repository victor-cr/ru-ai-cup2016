package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.model.Point;

import java.util.Arrays;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2016 14:22
 */
public final class PointQueue {
    private final Point[] data;
    private final int capacity;
    private int tail;
    private int size;

    public PointQueue(int capacity) {
        this.data = new Point[capacity];
        this.capacity = capacity;
        this.size = 0;
        this.tail = 0;
    }

    public void clear() {
        size = 0;
        Arrays.setAll(data, i -> null);
    }

    public void offer(Point location) {
        tail = (tail + 1) % capacity;

        if (size < capacity) {
            size++;
        }

        data[tail] = location;
    }

    public Point head(int i) {
        if (size == 0) {
            throw new IndexOutOfBoundsException("Empty queue cannot be queried");
        }

        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("Should be within the interval: [0.." + size + ")");
        }


        return data[(capacity + tail - size + 1 + i) % capacity];
    }

    public Point tail(int i) {
        if (size == 0) {
            throw new IndexOutOfBoundsException("Empty queue cannot be queried");
        }

        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("Should be within the interval: [0.." + size + ")");
        }


        return data[(data.length + tail - i) % data.length];
    }

    public int size() {
        return size;
    }
}
