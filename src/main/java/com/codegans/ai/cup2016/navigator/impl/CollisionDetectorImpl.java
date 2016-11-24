package com.codegans.ai.cup2016.navigator.impl;

import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.CollisionDetector;
import model.Building;
import model.LivingUnit;
import model.Minion;
import model.Tree;
import model.Wizard;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.StrictMath.abs;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2016 10:44
 */
public class CollisionDetectorImpl implements CollisionDetector {
    private final double width;
    private final double height;
    private final Supplier<Stream<Wizard>> wizardStreamSupplier;
    private final Supplier<Stream<Minion>> minionStreamSupplier;
    private final Supplier<Stream<Tree>> treeStreamSupplier;
    private final Supplier<Stream<Building>> buildingStreamSupplier;

    public CollisionDetectorImpl(double width, double height, Supplier<Stream<Tree>> treeStreamSupplier, Supplier<Stream<Building>> buildingStreamSupplier) {
        this(width, height, treeStreamSupplier, buildingStreamSupplier, Stream::empty, Stream::empty);
    }

    public CollisionDetectorImpl(double width, double height, Supplier<Stream<Tree>> treeStreamSupplier, Supplier<Stream<Building>> buildingStreamSupplier, Supplier<Stream<Minion>> minionStreamSupplier, Supplier<Stream<Wizard>> wizardStreamSupplier) {
        this.width = width;
        this.height = height;
        this.treeStreamSupplier = treeStreamSupplier;
        this.buildingStreamSupplier = buildingStreamSupplier;
        this.minionStreamSupplier = minionStreamSupplier;
        this.wizardStreamSupplier = wizardStreamSupplier;
    }

    @Override
    public double width() {
        return width;
    }

    @Override
    public double height() {
        return height;
    }

    @Override
    public boolean isNear(double x, double y, double radius, LivingUnit unit) {
        return circleIntersect(x, y, radius, unit.getX(), unit.getY(), unit.getRadius());
    }

    @Override
    public boolean canPass(Point from, Point to, double radius) {
        return stream().noneMatch(e -> overlaps(from, to, radius, e));
    }

    @Override
    public boolean overlaps(Point from, Point to, double radius, LivingUnit unit) {
        return lineIntersect(radius, from.x, from.y, to.x, to.y, unit.getX(), unit.getY(), unit.getRadius());
    }

    @Override
    public boolean contains(Point from, Point to, double radius, Point target) {
        return lineIntersect(radius, from.x, from.y, to.x, to.y, target.x, target.y, 0);
    }

    @Override
    public boolean available(double x, double y, double radius) {
        return Double.compare(x, 0) >= 0 && Double.compare(y, 0) >= 0
                && Double.compare(x, width) < 0 && Double.compare(y, height) < 0
                && stream().noneMatch(e -> circleIntersect(x, y, radius, e.getX(), e.getY(), e.getRadius()));
    }

    @Override
    public LivingUnit unitAt(double x, double y) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            return stream().filter(e -> circleIntersect(x, y, 0, e.getX(), e.getY(), e.getRadius())).findAny().orElse(null);
        }

        return null;
    }

    @Override
    public Stream<LivingUnit> unitsAt(double x, double y, double radius) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            return stream().filter(e -> circleIntersect(x, y, radius, e.getX(), e.getY(), e.getRadius()));
        }

        return Stream.empty();
    }

    @Override
    public Stream<Minion> minionsAt(double x, double y, double radius) {
        return minionStreamSupplier.get().filter(e -> circleIntersect(x, y, radius, e.getX(), e.getY(), e.getRadius()));
    }

    @Override
    public Stream<Wizard> wizardsAt(double x, double y, double radius) {
        return wizardStreamSupplier.get().filter(e -> circleIntersect(x, y, radius, e.getX(), e.getY(), e.getRadius()));
    }

    @Override
    public Stream<Building> towersAt(double x, double y, double radius) {
        return buildingStreamSupplier.get().filter(e -> circleIntersect(x, y, radius, e.getX(), e.getY(), e.getRadius()));
    }

    @Override
    public Stream<Tree> treesAt(double x, double y, double radius) {
        return treeStreamSupplier.get().filter(e -> circleIntersect(x, y, radius, e.getX(), e.getY(), e.getRadius()));
    }

    private Stream<LivingUnit> stream() {
        return Stream.concat(
                Stream.concat(wizardStreamSupplier.get(), minionStreamSupplier.get()),
                Stream.concat(treeStreamSupplier.get(), buildingStreamSupplier.get())
        );
    }

    private static boolean lineIntersect(double radius, double x1, double y1, double x2, double y2, double x3, double y3, double r3) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0) {
            return Double.compare(y3, Double.min(y1, y2) - radius) >= 0
                    && Double.compare(y3, Double.max(y1, y2) + radius) <= 0
                    && Double.compare(abs(x1 - x3), radius + r3) <= 0;
        }

        if (dy == 0) {
            return Double.compare(x3, Double.min(x1, x2) - radius) >= 0
                    && Double.compare(x3, Double.max(x1, x2) + radius) <= 0
                    && Double.compare(abs(y1 - y3), radius + r3) <= 0;
        }

        double a = dy / dx;
        double b = y1 - a * x1;
        double c = -dx / dy;
        double d = y3 - c * x3;
        double x = (d - b) / (a - c);

        if (Double.compare(x, Double.min(x1, x2) - radius) < 0 || Double.compare(x, Double.max(x1, x2) + radius) > 0) {
            return false;
        }

        double y = a * x + b;

        return Double.compare(pow2(x - x3) + pow2(y - y3), pow2(radius + r3)) < 0;
    }

    private static boolean circleIntersect(double x1, double y1, double r1, double x2, double y2, double r2) {
        return Double.compare(pow2(x1 - x2) + pow2(y1 - y2), pow2(r1 + r2)) < 0;
    }

    private static double pow2(double a) {
        return a * a;
    }
}
