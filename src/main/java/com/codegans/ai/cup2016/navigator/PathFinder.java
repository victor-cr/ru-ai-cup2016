package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.model.Point;
import model.World;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 17:30
 */
public interface PathFinder {
    Collection<Point> traverse(World world, Point start, Point finish, double radius);

    Collection<Point> traverse(World world, Point start, Point finish, double radius, Consumer<Point> logger);

    static AStarPathFinder aStar() {
        return new AStarPathFinder();
    }
}
