package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.model.Point;
import model.World;

import java.util.BitSet;
import java.util.Collection;
import java.util.PriorityQueue;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 17:30
 */
public class AStarPathFinder implements PathFinder {
    @Override
    public Collection<Point> traverse(World world, Point start, Point finish, double radius) {
        PriorityQueue<Node> opened = new PriorityQueue<>();
        BitSet closed = new BitSet(((int) world.getWidth()) * ((int) world.getHeight()));




        return null;
    }

    private static class Node {
//        private final int x;
//        private final int y;
//        private final Node parent;
    }
}
