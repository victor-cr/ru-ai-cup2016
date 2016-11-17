package com.codegans.ai.cup2016.action;

import model.Move;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 19:13
 */
public interface Action<A extends Action<A>> extends Comparable<A> {
    int score();

    void apply(Move move);
}
