package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 20:27
 */
public interface Decision {
    Stream<Action> decide(Wizard self, World world, Game game, Move move);
}
