package com.codegans.ai.cup2016.model;

import model.Unit;

import static java.lang.StrictMath.*;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 22.11.2015 20:18
 */
public final class Point {
    public final double x;
    public final double y;

    public Point(Unit unit) {
        this.x = unit.getX();
        this.y = unit.getY();
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point withX(double x) {
        return new Point(x, y);
    }

    public Point withY(double y) {
        return new Point(x, y);
    }

    public Point plusX(double dx) {
        return new Point(x + dx, y);
    }

    public Point plusY(double dy) {
        return new Point(x, y + dy);
    }

    public Point minusX(double dx) {
        return new Point(x - dx, y);
    }

    public Point minusY(double dy) {
        return new Point(x, y - dy);
    }

    public Point plus(Point base) {
        return new Point(x + base.x, y + base.y);
    }

    public Point minus(Point base) {
        return new Point(x - base.x, y - base.y);
    }

    public Point reflectTo(Point base) {
        double dx = x - base.x;
        double dy = y - base.y;

        return new Point(base.x - dx, base.y - dy);
    }

    public Point merge(Point base) {
        return new Point(base.x - (base.x - x) / 2, base.y - (base.y - y) / 2);
    }

    public Point shiftTo(Point other, double gravity) {
        double val = (1 + max(min(gravity, 1.0D), -1.0D)) / 2;

        return plusX((other.x - x) * val).plusY((other.y - y) * val);
    }

    public double distanceTo(Point point) {
        return StrictMath.hypot(x - point.x, y - point.y);
    }

    @Override
    public int hashCode() {
        return (int) (x * y);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Point && equals((Point) obj);
    }

    public boolean equals(Point other) {
        return other != null && Double.compare(abs(x - other.x), 0.001D) < 0 && Double.compare(abs(y - other.y), 0.001D) < 0;
    }

    @Override
    public String toString() {
        return String.format("(%.3f;%.3f)", x, y);
    }
}
