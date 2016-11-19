package com.codegans.ai.cup2016.navigator;

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
public class AStarNode implements Comparable<AStarNode> {
    private static final int SHIFT = 256;
    private static final double G_WEIGHT = 1;
    private static final double H_WEIGHT = sqrt(2);

    protected final int x;
    protected final int y;
    private final int targetX;
    private final int targetY;
    private final double hCost;
    private final double gCost;
    private AStarNode previous;

    public AStarNode(Point start, Point target, int step) {
        this(
                StrictMath.floorDiv((int) start.x, step) * step,
                StrictMath.floorDiv((int) start.y, step) * step,
                StrictMath.floorDiv((int) target.x, step) * step,
                StrictMath.floorDiv((int) target.y, step) * step,
                null
        );
    }

    public AStarNode(int x, int y, AStarNode previous) {
        this(x, y, previous.targetX, previous.targetY, previous);
    }

    private AStarNode(int x, int y, int targetX, int targetY, AStarNode previous) {
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.previous = previous;
        this.hCost = heuristic();
        this.gCost = previous == null ? 0 : StrictMath.floor(SHIFT * previous.traversedCost()) / SHIFT + previous.distance(this);
    }

    public double cost() {
        return hCost + gCost;
    }

    public double estimatedCost() {
        return hCost;
    }

    public double traversedCost() {
        return gCost;
    }

    public AStarNode previous() {
        return previous;
    }

    public boolean isTarget() {
        return x == targetX && y == targetY;
    }

    private double distance(AStarNode target) {
        return StrictMath.floor(SHIFT * hypot(x - target.x, y - target.y) * G_WEIGHT) / SHIFT;
    }

    private double heuristic() {
        double dx = abs(x - targetX);
        double dy = abs(y - targetY);
        return G_WEIGHT * (dx + dy) + (H_WEIGHT - 2 * G_WEIGHT) * min(dx, dy);
    }

    public Point toPoint() {
        return new Point(x, y);
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
        return "A* node[" + x + ":" + y + "] (" + hCost + ":" + gCost + ")";
    }
}
