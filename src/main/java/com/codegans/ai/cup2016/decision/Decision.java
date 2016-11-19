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
    int ASAP = 1;
    int HIGH = ASAP * 10;
    int MEDIUM = HIGH * 10;
    int LOW = MEDIUM * 10;
    int MINOR = 10 * LOW;

    Action decide(Wizard self, World world, Game game, Move move);
}
