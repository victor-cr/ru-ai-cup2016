package com.codegans.ai.cup2016.navigator.astar;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 17.11.2016 8:30
 */
public class BorderNode extends AStarNode {
    public BorderNode(int x, int y, AStarNode previous) {
        super(x, y, previous.targetX, previous.targetY, previous);
    }

    @Override
    protected double distanceFrom(AStarNode target) {
        return G_WEIGHT * 20;
    }
}
