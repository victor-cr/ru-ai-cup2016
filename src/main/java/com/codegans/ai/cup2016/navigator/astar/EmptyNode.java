package com.codegans.ai.cup2016.navigator.astar;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 17.11.2016 8:30
 */
public class EmptyNode extends AStarNode {
    public EmptyNode(int x, int y, AStarNode previous) {
        super(x, y, previous.targetX, previous.targetY, previous);
    }
}
