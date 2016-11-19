package com.codegans.ai.cup2016.navigator.astar;

import com.codegans.ai.cup2016.model.Point;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.hypot;
import static java.lang.StrictMath.min;
import static java.lang.StrictMath.sqrt;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 17.11.2016 8:30
 */
public abstract class AStarNode implements Comparable<AStarNode> {
    protected static final double G_WEIGHT = 1;
    protected static final double H_WEIGHT = sqrt(2);
    private static final int SHIFT = 256;

    protected final int x;
    protected final int y;
    protected final int targetX;
    protected final int targetY;
    private final double hCost;
    private double gCost = -1;
    private AStarNode previous;

    protected AStarNode(int x, int y, int targetX, int targetY, AStarNode previous) {
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.previous = previous;
        this.hCost = heuristic(targetX, targetY);
    }

    public double cost() {
        return estimatedCost() + traversedCost();
    }

    public double estimatedCost() {
        return round(hCost);
    }

    public double traversedCost() {
        if (gCost < 0) {
            gCost = previous.traversedCost() + distanceFrom(previous);
        }

        return gCost;
    }

    public AStarNode previous() {
        return previous;
    }

    public void previous(AStarNode previous) {
        this.previous = previous;
        this.gCost = -1;
        traversedCost();
    }

    public boolean isTarget() {
        return x == targetX && y == targetY;
    }

    public Point toPoint() {
        return new Point(x, y);
    }

    protected double distanceFrom(AStarNode target) {
        return round(hypot(x - target.x, y - target.y) * G_WEIGHT);
    }

    private double heuristic(double x, double y) {
        double dx = abs(this.x - x);
        double dy = abs(this.y - y);
        return G_WEIGHT * (dx + dy) + (H_WEIGHT - 2 * G_WEIGHT) * min(dx, dy);
    }

    private static double round(double value) {
        return StrictMath.floor(SHIFT * value) / SHIFT;
    }

    @Override
    public int compareTo(AStarNode o) {
        return Double.compare(cost(), o.cost());
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(x) * 31 + Integer.hashCode(y);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof AStarNode && equals((AStarNode) obj);
    }

    public boolean equals(AStarNode that) {
        return that != null && this.x == that.x && this.y == that.y;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + x + ":" + y + "] (" + hCost + ":" + traversedCost() + ")";
    }
}
