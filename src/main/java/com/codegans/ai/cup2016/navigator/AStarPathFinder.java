package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.GameMap;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Point;
import model.LivingUnit;
import model.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.function.Consumer;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 17:30
 */
public class AStarPathFinder implements PathFinder {
    private static final int STEP = 3;
    private static final int PADDING = 5;
    private static final int TIMEOUT = 300000;
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
        Direction[] directions = Direction.values();

        AStarNode starNode = new AStarNode(start, finish, STEP);

        LivingUnit unit = map.findAt(finish.x, finish.y);

        opened.offer(starNode);

        int i = 0;
        long startTime = System.currentTimeMillis();

        while (!opened.isEmpty()) {
            if ((i++ % 10000) == 0 && System.currentTimeMillis() > startTime + TIMEOUT) {
                LOG.printf("Terminated by timeout%n");
                break;
            }

            AStarNode node = opened.poll();

            if (node.isTarget() || unit != null && map.isNear(node.x, node.y, radius + PADDING, unit)) {
                LOG.printf("Number of iterations: %d%n", i);
                return constructPath(map, node, radius);
            }

            for (Direction direction : directions) {
                int x = node.x + direction.dx;
                int y = node.y + direction.dy;

                if (x >= 0 && y >= 0 && x < width && y < height) {
                    int index = index(x, y, width);

                    AStarNode child = new AStarNode(x, y, node);
                    AStarNode prev = closed[index];

                    if (prev == null || Double.compare(prev.cost(), child.cost()) > 0) {
                        closed[index] = child;

                        if (map.available(x, y, radius)) {
                            if (logger != null && prev == null) {
                                logger.accept(child.toPoint());
                            }

                            opened.add(child);
                        }
                    }
                }
            }
        }

        AStarNode best = starNode;

        for (AStarNode node : closed) {
            if (node != null && Double.compare(best.estimatedCost(), node.estimatedCost()) >= 0) {
                if (Double.compare(best.estimatedCost(), node.estimatedCost()) > 0 || Double.compare(best.traversedCost(), node.traversedCost()) > 0) {
                    best = node;
                }
            }
        }

        LOG.printf("Number of iterations: %d%n", i);

        return constructPath(map, best, radius);
    }

    private static int index(int x, int y, int width) {
        return (x + y * width) / STEP;
    }

    private Collection<Point> constructPath(GameMap map, AStarNode node, double radius) {
        Collection<Point> result = new ArrayList<>();

        while (node != null) {
            result.add(new Point(node.x, node.y));

            LOG.printf("Path: [%d,%d]%n", node.x, node.y);

            node = optimizePath(map, node, radius);
        }

        return result;
    }

    private AStarNode optimizePath(GameMap map, AStarNode node, double radius) {
        AStarNode previous = node.previous();

        for (AStarNode temp = previous; temp != null && !intersect(map, node, previous, radius); temp = previous.previous()) {
            previous = temp;
        }

        return previous;
    }

    private boolean intersect(GameMap map, AStarNode start, AStarNode stop, double radius) {
        int x = start.x;
        int y = start.y;
        int deltaX = start.x - stop.x;
        int deltaY = start.y - stop.y;

        int base = StrictMath.max(StrictMath.abs(deltaX), StrictMath.abs(deltaY));

        double dx = deltaX;
        double dy = deltaY;
        int i = 1;

        while (i < base - 1) {
            if (!map.available(x - dx * i / base, y - dy * i / base, radius)) {
                return true;
            }

            i++;
        }

        return false;
    }

    private enum Direction {
        NORTH(0, -1),
        SOUTH(0, 1),
        WEST(-1, 0),
        EAST(1, 0),
        NORTH_WEST(-1, -1),
        SOUTH_WEST(-1, 1),
        NORTH_EAST(1, -1),
        SOUTH_EAST(1, 1),;

        private final int dx;
        private final int dy;

        Direction(int dx, int dy) {
            this.dx = dx * STEP;
            this.dy = dy * STEP;
        }
    }
}
