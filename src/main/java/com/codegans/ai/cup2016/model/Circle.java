package com.codegans.ai.cup2016.model;

import model.CircularUnit;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 22.11.2015 20:24
 */
public class Circle {
    public final Point center;
    public final double radius;

    public Circle(CircularUnit unit) {
        this(new Point(unit), unit.getRadius());
    }

    public Circle(CircularUnit unit, double radius) {
        this(new Point(unit), radius);
    }

    public Circle(Point center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public double mostX() {
        return center.x + radius;
    }

    public double leastX() {
        return center.x + radius;
    }

    public double mostY() {
        return center.y + radius;
    }

    public double leastY() {
        return center.y + radius;
    }

    public boolean overlaps(CircularUnit unit) {
        return unit.getDistanceTo(center.x, center.y) <= radius + unit.getRadius();
    }

    public boolean overlaps(Circle circle) {
        return circle.center.distanceTo(center) <= radius + circle.radius;
    }

    @Override
    public int hashCode() {
        return center.hashCode() ^ Double.hashCode(radius);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Circle && equals((Circle) obj);
    }

    public boolean equals(Circle that) {
        return that != null && center.equals(that.center) && Double.compare(radius, that.radius) == 0;
    }

    @Override
    public String toString() {
        return String.format("%s[%f]", center, radius);
    }
}
