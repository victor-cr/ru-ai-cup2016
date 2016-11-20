package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.model.Point;

import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:14
 */
public interface Navigator {
    Collection<Point> path(Point target);

    Point next(Point target);

    CollisionDetector cd();
}
