package com.codegans.ai.cup2016.action;

import model.Game;
import model.Move;
import model.Wizard;
import model.World;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 19:13
 */
public interface Action extends Comparable<Action> {
    int score();

    void apply(Wizard self, World world, Game game, Move move);

    @Override
    default int compareTo(Action o) {
        return Integer.compare(score(), o.score());
    }
}
