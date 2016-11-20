package com.codegans.ai.cup2016.model;

import model.Unit;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 22.11.2015 20:18
 */
public final class WeightPoint {
    public final Point point;
    public final double weight;

    public WeightPoint(Unit unit, double weight) {
        this.point = new Point(unit);
        this.weight = weight;
    }

    public WeightPoint(double x, double y, double weight) {
        this.point = new Point(x, y);
        this.weight = weight;
    }

    public WeightPoint merge(Point other, double weight) {
        double mass = this.weight + weight;

        double val = this.weight / mass;

        return new WeightPoint((other.x - point.x) * val, (other.y - point.y) * val, mass);
    }

    public WeightPoint merge(WeightPoint other) {
        return merge(other.point, other.weight);
    }
}
