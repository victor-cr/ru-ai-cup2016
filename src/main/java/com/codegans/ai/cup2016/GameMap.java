package com.codegans.ai.cup2016;

import model.Building;
import model.CircularUnit;
import model.LivingUnit;
import model.Minion;
import model.Tree;
import model.Wizard;
import model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static java.lang.StrictMath.hypot;

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

    public boolean available(int x, int y) {
        return available(x, y, 0);
    }

    public boolean available(double x, double y, double radius) {
        return x >= 0 && y >= 0 && x < width && y < height && check(x, y, radius, buildings) && check(x, y, radius, minions) && check(x, y, radius, wizards) && check(x, y, radius, trees);
    }

    private static boolean check(double x, double y, double radius, Collection<? extends LivingUnit> items) {
        for (LivingUnit item : items) {
            if (intersect(x, y, radius, item.getX(), item.getY(), item.getRadius())) {
                return false;
            }
        }

        return true;
    }

    private static boolean intersect(double x1, double y1, double r1, double x2, double y2, double r2) {
        return pow2(x1 - x2) + pow2(y1 - y2) < pow2(r1 + r2);
    }

    private static double pow2(double a) {
        return a * a;
    }

//    public boolean available(double x, double y, double radius) {
//        return x < 775 - 50 - radius || x > 775 + 50 + radius || y < 148 - 50 - radius || y > 148 + 50 + radius;
//    }

    public CircularUnit find(double x, double y) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            return streamAll().filter(e -> hypot(e.getX() - x, e.getY() - y) < e.getRadius()).findAny().orElse(null);
        }

        return null;
    }

    private Stream<LivingUnit> streamAll() {
        return Stream.concat(Stream.concat(buildings.stream(), minions.stream()), Stream.concat(trees.stream(), wizards.stream()));
    }

    private GameMap update(World world) {
        if (version == world.getTickIndex()) {
            return this;
        }

        version = world.getTickIndex();

        trees.clear();
        wizards.clear();
        minions.clear();
        buildings.clear();


        Arrays.stream(world.getTrees()).filter(e -> e.getLife() > 0).forEach(trees::add);
        Arrays.stream(world.getWizards()).filter(e -> e.getLife() > 0).filter(e -> !e.isMe()).forEach(wizards::add);
        Arrays.stream(world.getMinions()).filter(e -> e.getLife() > 0).forEach(minions::add);
        Arrays.stream(world.getBuildings()).filter(e -> e.getLife() > 0).forEach(buildings::add);

        return this;
    }
}
