package com.codegans.ai.cup2016.log;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 11:46
 */
public interface Logger {
    void print(Object message);

    void printf(String pattern, Object... params);

    void action(Action action);

    void logPath(Collection<Point> path, Point target);

    void logState(Wizard self, World world, Game game, Move move);
}
