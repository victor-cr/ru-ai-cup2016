package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 20:27
 */
public interface Decision {
    int MINOR = 1;
    int LOW = MINOR * 10;
    int MEDIUM = LOW * 10;
    int HIGH = MEDIUM * 10;
    int ASAP = HIGH * 10;

    Action decide(Wizard self, World world, Game game, Move move);
}
