package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.model.Point;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:14
 */
public interface Navigator {
    Point next(Point target, boolean passThroughTrees);
}
