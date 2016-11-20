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
 * @since 19.11.2015 19:56
 */
public class NullLogger implements Logger {
    @Override
    public void print(Object message) {
    }

    @Override
    public void printf(String pattern, Object... params) {
    }

    @Override
    public void action(Action action) {
    }

    @Override
    public void logPath(Collection<Point> path, Point target) {
    }

    @Override
    public void logState(Wizard self, World world, Game game, Move move) {
    }
}
