package com.codegans.ai.cup2016.navigator.astar;

import com.codegans.ai.cup2016.model.Point;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 17.11.2016 8:30
 */
public class StartNode extends AStarNode {
    public StartNode(Point start, Point target) {
        super((int)start.x, (int)start.y, (int)target.x, (int)target.y, null);
    }

    @Override
    public double traversedCost() {
        return 0;
    }
}
