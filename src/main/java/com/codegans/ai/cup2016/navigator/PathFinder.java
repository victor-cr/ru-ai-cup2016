package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.astar.AStarPathFinder;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 17:30
 */
public interface PathFinder {
    default Point next(GameMap map, Point start, Point finish, double radius, boolean passThroughTrees) {
        Collection<Point> traverse = traverse(map, start, finish, radius, passThroughTrees, null);

        return traverse.stream().skip(1).findFirst().orElse(start);
    }

    default Collection<Point> traverse(GameMap map, Point start, Point finish, double radius, boolean passThroughTrees) {
        return traverse(map, start, finish, radius, passThroughTrees, null);
    }

    Collection<Point> traverse(GameMap map, Point start, Point finish, double radius, boolean passThroughTrees, BiConsumer<Point, String> logger);

    static PathFinder aStar() {
        return new AStarPathFinder();
    }
}
