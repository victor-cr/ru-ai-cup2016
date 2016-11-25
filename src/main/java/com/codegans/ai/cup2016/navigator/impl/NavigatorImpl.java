package com.codegans.ai.cup2016.navigator.impl;

import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import com.codegans.ai.cup2016.navigator.Navigator;
import com.codegans.ai.cup2016.navigator.PathFinder;
import model.Wizard;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2016 12:50
 */
public class NavigatorImpl implements Navigator {
    private static final Logger LOG = LoggerFactory.getLogger();
    private static final int TICKS = 10;

    private final GameMap map;
    private Point next;

    public NavigatorImpl(GameMap map) {
        this.map = map;
    }

    @Override
    public Point next(Point target, boolean passThroughTrees) {
        int tick = map.tick();
        Wizard self = map.self();

        if (next == null || tick % TICKS == 0) {
            next = PathFinder.aStar().next(map, new Point(self), target, self.getRadius(), passThroughTrees);

            LOG.logTarget(next, tick);
        }

        return next;
    }
}
