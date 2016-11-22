package com.codegans.ai.cup2016.log;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import java.io.PrintStream;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 19:56
 */
public class ConsoleLogger implements Logger {
    private volatile PrintStream out = System.out;

    @Override
    public void print(Object message) {
        out.print(message);
    }

    @Override
    public void printf(String pattern, Object... params) {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Collection) {
                Collection<?> c = (Collection) params[i];

                if (!c.isEmpty()) {
                    c = c.stream()
                            .filter(e -> e instanceof Wizard)
                            .map(e -> (Wizard) e)
                            .map(e -> String.format("Wizard@(%.3f;%.3f)", e.getX(), e.getY()))
                            .collect(Collectors.toList());
                }

                if (!c.isEmpty()) {
                    params[i] = c;
                }
            }

            if (params[i] instanceof Wizard) {
                Wizard e = (Wizard) params[i];

                params[i] = String.format("Wizard@(%.3f;%.3f)", e.getX(), e.getY());
            }
        }

        out.printf(pattern, params);
    }

    @Override
    public void action(Action action) {
        printf("Perform action: %s%n", action);
    }

    @Override
    public void logPath(Collection<Point> path, Point target) {
        printf("%s -> %s%n", path, target);
    }

    @Override
    public void logState(Wizard self, World world, Game game, Move move) {
        printf("%n<%d>-------[%d]@(%.3f,%.3f)%n", world.getTickIndex(), self.getLife(), self.getX(), self.getY());
    }

    @Override
    public void logTarget(Point target, int tick) {
        printf("Target: %s%n", target);
    }
}
