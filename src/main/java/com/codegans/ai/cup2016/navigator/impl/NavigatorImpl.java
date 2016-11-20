package com.codegans.ai.cup2016.navigator.impl;

import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.CollisionDetector;
import com.codegans.ai.cup2016.navigator.Navigator;
import com.codegans.ai.cup2016.navigator.PathFinder;
import com.codegans.ai.cup2016.navigator.PointQueue;
import model.Wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2016 12:50
 */
public class NavigatorImpl implements Navigator {
    private static final Logger LOG = LoggerFactory.getLogger();

    private final CollisionDetector collisionDetector;
    private final Supplier<Wizard> selfSupplier;
    private final Supplier<PointQueue> historySupplier;
    private final List<Point> path = new ArrayList<>();
    private Point target;
    private int index = 1;

    public NavigatorImpl(CollisionDetector collisionDetector, Supplier<Wizard> selfSupplier, Supplier<PointQueue> historySupplier) {
        this.collisionDetector = collisionDetector;
        this.selfSupplier = selfSupplier;
        this.historySupplier = historySupplier;
    }

    @Override
    public Collection<Point> path(Point target) {
        if (this.target != null && this.target.equals(target)) {
            return new ArrayList<>(path);
        }

        this.target = target;

        recalculate();

        return path;
    }

    @Override
    public Point next(Point target) {
        Wizard self = selfSupplier.get();

        path(target);

        Point next = findNext();

        if (cd().canPass(new Point(self), next, self.getRadius())) {
            return next;
        }

        recalculate();

        return findNext();
    }

    @Override
    public CollisionDetector cd() {
        return collisionDetector;
    }

    private Point findNext() {
        Wizard self = selfSupplier.get();
        PointQueue history = historySupplier.get();

        Point point = safeGet();
        int size = history.size();

        if (size > 1) {
            Point a = history.tail(0);
            Point b = history.tail(1);

            if (collisionDetector.contains(a, b, self.getRadius(), point)) {
                index++;
                point = safeGet();
            }
        }

        return point;
    }

    private Point safeGet() {
        while (path.size() <= index) {
            index--;
        }

        return path.get(index);
    }

    private void recalculate() {
        Wizard self = selfSupplier.get();

        path.clear();
        path.addAll(PathFinder.aStar().traverse(collisionDetector, new Point(self), target, self.getRadius()));
        index = 1;

        LOG.logPath(path, target);
    }
}
