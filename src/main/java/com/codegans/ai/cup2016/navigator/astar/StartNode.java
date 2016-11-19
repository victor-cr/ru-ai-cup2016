package com.codegans.ai.cup2016.navigator.astar;

import com.codegans.ai.cup2016.model.Point;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 17.11.2016 8:30
 */
public class StartNode extends AStarNode {
    public StartNode(Point start, Point target, int step) {
        super(
                StrictMath.floorDiv((int) start.x, step) * step,
                StrictMath.floorDiv((int) start.y, step) * step,
                StrictMath.floorDiv((int) target.x, step) * step,
                StrictMath.floorDiv((int) target.y, step) * step,
                null
        );
    }

    @Override
    public double traversedCost() {
        return 0;
    }
}
