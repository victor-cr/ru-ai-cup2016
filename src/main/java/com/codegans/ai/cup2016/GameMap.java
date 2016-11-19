package com.codegans.ai.cup2016;

import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.PathFinder;
import com.codegans.ai.visualize.VisualLog;
import model.Building;
import model.Faction;
import model.LivingUnit;
import model.Minion;
import model.Tree;
import model.Wizard;
import model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 18:55
 */
public class GameMap {
    private static final Object MUTEX = new Object();

    private static volatile GameMap instance;

    private final double width;
    private final double height;
    private final Collection<Tree> trees = new ArrayList<>();
    private final Collection<Wizard> wizards = new ArrayList<>();
    private final Collection<Minion> minions = new ArrayList<>();
    private final Collection<Building> buildings = new ArrayList<>();
    private final NavigatorImpl navigator = new NavigatorImpl();
    private volatile World world;
    private volatile Wizard self;
    private volatile int version = -1;

    private GameMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static GameMap get(World world) {
        GameMap i = instance;

        if (i == null) {
            synchronized (MUTEX) {
                if (instance == null) {
                    instance = new GameMap((int) world.getWidth(), (int) world.getHeight());
                }

                i = instance;
            }
        }

        return i.update(world);
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public boolean isNear(double x, double y, double radius, LivingUnit unit) {
        return intersects(x, y, radius, unit.getX(), unit.getY(), unit.getRadius());
    }

    public boolean canPass(Point from, Point to, double radius) {
        double x = from.x;
        double y = from.y;
        double targetX = to.x;
        double targetY = to.y;

        double dx = targetX - x;
        double dy = targetY - y;

        double a = dy / dx;
        double b = y - a * x;
        double c = -dx / dy;

        for (LivingUnit item : buildings) {
            if (intersects(a, b, c, radius, x, y, targetX, targetY, item.getX(), item.getY(), item.getRadius())) {
                return false;
            }
        }
        for (LivingUnit item : minions) {
            if (intersects(a, b, c, radius, x, y, targetX, targetY, item.getX(), item.getY(), item.getRadius())) {
                return false;
            }
        }
        for (LivingUnit item : wizards) {
            if (intersects(a, b, c, radius, x, y, targetX, targetY, item.getX(), item.getY(), item.getRadius())) {
                return false;
            }
        }
        for (LivingUnit item : trees) {
            if (intersects(a, b, c, radius, x, y, targetX, targetY, item.getX(), item.getY(), item.getRadius())) {
                return false;
            }
        }

        return true;
    }

    public boolean available(double x, double y) {
        return available(x, y, 0);
    }

    public boolean available(double x, double y, double radius) {
        return x >= 0 && y >= 0 && x < width && y < height
                && get(x, y, radius, buildings).isEmpty()
                && get(x, y, radius, minions).isEmpty()
                && get(x, y, radius, wizards).isEmpty()
                && get(x, y, radius, trees).isEmpty();
    }

    public LivingUnit findAt(double x, double y) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            for (LivingUnit item : buildings) {
                if (intersects(x, y, 0, item.getX(), item.getY(), item.getRadius())) {
                    return item;
                }
            }
            for (LivingUnit item : minions) {
                if (intersects(x, y, 0, item.getX(), item.getY(), item.getRadius())) {
                    return item;
                }
            }
            for (LivingUnit item : wizards) {
                if (intersects(x, y, 0, item.getX(), item.getY(), item.getRadius())) {
                    return item;
                }
            }
            for (LivingUnit item : trees) {
                if (intersects(x, y, 0, item.getX(), item.getY(), item.getRadius())) {
                    return item;
                }
            }
        }

        return null;
    }

    public Collection<LivingUnit> findAll(double x, double y, double radius) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            Collection<LivingUnit> result = new ArrayList<>();

            result.addAll(get(x, y, radius, buildings));
            result.addAll(get(x, y, radius, minions));
            result.addAll(get(x, y, radius, wizards));
            result.addAll(get(x, y, radius, trees));

            return result;
        }

        return Collections.emptySet();
    }

    public Faction negate(Faction faction) {
        return faction == Faction.RENEGADES ? Faction.ACADEMY : Faction.RENEGADES;
    }

    public Stream<Building> buildings() {
        return buildings.stream();
    }

    public Stream<Wizard> wizards() {
        return wizards.stream();
    }

    public Stream<Minion> minions() {
        return minions.stream();
    }

    public Navigator navigator(Point target) {
        return navigator.target(target);
    }

    private static Collection<LivingUnit> get(double x, double y, double radius, Collection<? extends LivingUnit> items) {
        Collection<LivingUnit> result = new ArrayList<>();

        for (LivingUnit item : items) {
            if (intersects(x, y, radius, item.getX(), item.getY(), item.getRadius())) {
                result.add(item);
            }
        }

        return result;
    }

    private static boolean intersects(double a, double b, double c, double radius, double x1, double y1, double x2, double y2, double x3, double y3, double r3) {
        double d = y3 - c * x3;
        double x = (d - b) / (a - c); // a * x + b = c * x + d

        if (Double.compare(x, Double.min(x1, x2) - radius) < 0 || Double.compare(x, Double.max(x1, x2) + radius) > 0) {
            return false;
        }

        double y = a * x + b;

        return Double.compare(pow2(x - x3) + pow2(y - y3), pow2(radius + r3)) < 0;
    }

    private static boolean intersects(double x1, double y1, double r1, double x2, double y2, double r2) {
        return Double.compare(pow2(x1 - x2) + pow2(y1 - y2), pow2(r1 + r2)) < 0;
    }

    private static double pow2(double a) {
        return a * a;
    }

    private GameMap update(World world) {
        if (version == world.getTickIndex()) {
            return this;
        }

        this.world = world;
        version = world.getTickIndex();

        trees.clear();
        wizards.clear();
        minions.clear();
        buildings.clear();

        Arrays.stream(world.getWizards()).filter(Wizard::isMe).forEach(e -> self = e);

        Arrays.stream(world.getTrees()).filter(e -> e.getLife() > 0).forEach(trees::add);
        Arrays.stream(world.getMinions()).filter(e -> e.getLife() > 0).forEach(minions::add);
        Arrays.stream(world.getBuildings()).filter(e -> e.getLife() > 0).forEach(buildings::add);
        Arrays.stream(world.getWizards()).filter(e -> e.getLife() > 0).filter(e -> !e.isMe()).forEach(wizards::add);

        return this;
    }

    private class NavigatorImpl implements Navigator {
        private final List<Point> path = new ArrayList<>();
        private Point target;
        private int index = 1;

        @Override
        public Point next() {
            Point point = path.get(index);

            if (Double.compare(point.x, self.getX()) == 0 && Double.compare(point.y, self.getY()) == 0) {
                point = path.get(++index);
            }

            Point me = new Point(self);

            if (!canPass(me, point, self.getRadius())) {
                recalculate();
                point = path.size() > 1 ? path.get(index) : path.get(0);
            }

            VisualLog.publishPath(path, point);

            return point;
        }

        private NavigatorImpl target(Point target) {
            this.target = target;

            if (path.isEmpty() || !path.contains(target)) {
                recalculate();
            }

            return this;
        }

        private void recalculate() {
            path.clear();
            path.addAll(PathFinder.aStar().traverse(world, new Point(self), target, self.getRadius()));
            index = 1;
        }
    }
}
