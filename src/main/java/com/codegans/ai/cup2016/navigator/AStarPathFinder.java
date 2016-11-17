package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.model.Point;
import model.World;

import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.PriorityQueue;

import static java.lang.StrictMath.hypot;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 17:30
 */
public class AStarPathFinder implements PathFinder {
    @Override
    public Collection<Point> traverse(World world, Point start, Point finish, double radius) {
        int width = (int) world.getWidth();
        int height = (int) world.getHeight();
        PriorityQueue<AStarNode> opened = new PriorityQueue<>();
        BitSet closed = new BitSet(((int) world.getWidth()) * ((int) world.getHeight()));


        LinkedList closedList = new LinkedList();

        AStarNode startNode = new AStarNode((int) start.x, (int) start.y);
        AStarNode goalNode = new AStarNode((int) finish.x, (int) finish.y);

        startNode.update(goalNode);

        opened.offer(startNode);

        while (!opened.isEmpty()) {
            AStarNode node = opened.poll();

            if (node.equals(goalNode)) {
                return constructPath(goalNode);
            }

            node.neighbors()
                    .filter(e -> e.x < 0).filter(e -> e.y < 0).filter(e -> e.x >= width).filter(e -> e.y >= height)
                    .forEach(e-> {
/*
                boolean isOpen = opened.contains(e);
                boolean isClosed = closedList.contains(e);
                float costFromStart = node.costFromStart +
                        node.getCost(neighborNode);

                if ((!isOpen && !isClosed) ||
                        costFromStart < neighborNode.costFromStart) {
                    neighborNode.pathParent = node;
                    neighborNode.costFromStart = costFromStart;
                    neighborNode.estimatedCostToGoal =
                            neighborNode.getEstimatedCost(goalNode);
                    if (isClosed) {
                        closedList.remove(neighborNode);
                    }
                    if (!isOpen) {
                        opened.offer(neighborNode);
                    }
                }
*/
            });

            closedList.add(node);
        }

        // no path found
        return null;

    }

    private Collection<Point> constructPath(AStarNode goalNode) {

    }
}
