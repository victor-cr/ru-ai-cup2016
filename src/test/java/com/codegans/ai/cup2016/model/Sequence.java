package com.codegans.ai.cup2016.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 21:05
 */
public class Sequence {
    private static final AtomicInteger GENERATOR = new AtomicInteger(0);

    private Sequence() {
    }

    public static int next() {
        return GENERATOR.incrementAndGet();
    }

    public static int current() {
        return GENERATOR.get();
    }
}
