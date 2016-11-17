package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.GameMap;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Point;
import model.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 17:30
 */
public class AStarPathFinder implements PathFinder {
    private static final int STEP = 2;
    private static final int TIMEOUT = 3000;
    private static final Logger LOG = LoggerFactory.getLogger();

    @Override
    public Collection<Point> traverse(World world, Point start, Point finish, double radius) {
        return traverse(world, start, finish, radius, e -> {
        });
    }

    @Override
    public Collection<Point> traverse(World world, Point start, Point finish, double radius, Consumer<Point> logger) {
        int width = (int) world.getWidth();
        int height = (int) world.getHeight();
        PriorityQueue<AStarNode> opened = new PriorityQueue<>();
        AStarNode[] closed = new AStarNode[width * height];
        GameMap map = GameMap.get(world);

        AStarNode starNode = new AStarNode(start, finish, STEP);

        opened.offer(starNode);

        int i = 0;
        long startTime = System.currentTimeMillis();

        while (!opened.isEmpty()) {
            if (i++ > 10000) {
                i = 0;

                if (System.currentTimeMillis() > startTime + TIMEOUT) {
                    LOG.printf("Terminated by timeout%n");
                    break;
                }
            }

            AStarNode node = opened.poll();

            if (node.isTarget()) {
                return constructPath(node);
            }

            AStarNode current = closed[node.x / STEP + width * node.y / STEP];

            if (current == null || Double.compare(current.cost(), node.cost()) > 0) {
                closed[node.x / STEP + width * node.y / STEP] = node;

                Stream.<AStarNode>builder()
                        .add(new AStarNode(node.x - STEP, node.y - STEP, node))
                        .add(new AStarNode(node.x - STEP, node.y, node))
                        .add(new AStarNode(node.x - STEP, node.y + STEP, node))
                        .add(new AStarNode(node.x, node.y - STEP, node))
                        .add(new AStarNode(node.x, node.y + STEP, node))
                        .add(new AStarNode(node.x + STEP, node.y - STEP, node))
                        .add(new AStarNode(node.x + STEP, node.y, node))
                        .add(new AStarNode(node.x + STEP, node.y + STEP, node))
                        .build()
                        .filter(e -> map.available(e.x, e.y, radius))
                        .peek(e -> logger.accept(e.toPoint()))
                        .forEach(opened::offer);
            }
        }

        AStarNode best = starNode;

        for (AStarNode node : closed) {
            if (node != null && Double.compare(best.estimatedCost(), node.estimatedCost()) > 0) {
                best = node;
            }
        }

        return constructPath(best);
    }

    private Collection<Point> constructPath(AStarNode node) {
        Collection<Point> result = new ArrayList<>();

        while (node != null) {
            result.add(new Point(node.x, node.y));

            LOG.printf("Path: [%d,%d]%n", node.x, node.y);

            node = node.previous();
        }

        return result;
    }
}
