package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.model.Point;

import java.util.stream.Stream;

import static java.lang.StrictMath.hypot;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 17.11.2016 8:30
 */
public class AStarNode implements Comparable<AStarNode> {
    protected final int x;
    protected final int y;
    private final AStarNode previous;
    private double hCost;
    private double gCost;

    public AStarNode(int x, int y) {
        this(x, y, null);
    }

    public AStarNode(int x, int y, AStarNode previous) {
        this.x = x;
        this.y = y;
        this.previous = previous;
        this.gCost = adjacent(previous);
    }

    public double cost() {
        return hCost + gCost;
    }

    public AStarNode previous() {
        return previous;
    }

    public Stream<AStarNode> neighbors() {
        Stream<AStarNode> stream = Stream.<AStarNode>builder()
                .add(new AStarNode(x - 1, y - 1, this))
                .add(new AStarNode(x - 1, y, this))
                .add(new AStarNode(x, y - 1, this))
                .add(new AStarNode(x, y + 1, this))
                .add(new AStarNode(x + 1, y, this))
                .add(new AStarNode(x + 1, y + 1, this))
                .build();

        if (previous != null) {
            return stream.filter(previous::equals);
        }

        return stream;
    }

    protected double adjacent(AStarNode target) {
        return target == null ? 0 : hypot(x - target.x, y - target.y);
    }

    protected double estimate(AStarNode target) {
        return target == null ? 0 : hypot(x - target.x, y - target.y);
    }

    public void update(AStarNode target) {
        hCost = estimate(target);
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
        return "AStarNode: " + toInternalString();
    }

    private String toInternalString() {
        if (previous == null) {
            return "[" + x + ":" + y + "]";
        }

        return previous.toInternalString() + " -> [" + x + ":" + y + "]";
    }

}
