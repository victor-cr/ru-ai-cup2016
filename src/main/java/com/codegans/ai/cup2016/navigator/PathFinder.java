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
    default Point next(GameMap map, Point start, Point finish, double radius) {
        Collection<Point> traverse = traverse(map, start, finish, radius, null);

        return traverse.stream().skip(1).findFirst().orElse(start);
    }

    default Collection<Point> traverse(GameMap map, Point start, Point finish, double radius) {
        return traverse(map, start, finish, radius, null);
    }

    Collection<Point> traverse(GameMap map, Point start, Point finish, double radius, BiConsumer<Point, String> logger);

    static PathFinder aStar() {
        return new AStarPathFinder();
    }
}
