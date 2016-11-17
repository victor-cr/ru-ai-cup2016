package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.Navigator;
import com.codegans.ai.cup2016.action.Action;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 20:27
 */
public interface Decision {
    Collection<Action<?>> decide(Wizard self, World world, Game game, Move move, Navigator navigator);
}
