package com.codegans.ai.cup2016.navigator.astar;

import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.CollisionDetector;
import com.codegans.ai.cup2016.navigator.PathFinder;
import model.LivingUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 17:30
 */
public class AStarPathFinder implements PathFinder {
    private static final Logger LOG = LoggerFactory.getLogger();

    private static final int STEP = 8;
    private static final int PADDING = STEP * 2;
    private static final int MAX_AREA = 200;
    private static final int MAX_POINTS = 10000;

    @Override
    public Collection<Point> traverse(CollisionDetector cd, Point start, Point finish, double radius, BiConsumer<Point, String> logger) {
        int width = ((int) cd.width());
        int height = ((int) cd.height());

        start = new Point(StrictMath.floor(start.x / STEP) * STEP, StrictMath.floor(start.y / STEP) * STEP);
        finish = new Point(StrictMath.floor(finish.x / STEP) * STEP, StrictMath.floor(finish.y / STEP) * STEP);

        PriorityQueue<AStarNode> opened = new PriorityQueue<>();
        AStarNode[] closed = new AStarNode[width / STEP * height / STEP];
        Direction[] directions = Direction.values();

        StartNode startNode = new StartNode(start, finish);

        LivingUnit unit = cd.unitAt(finish.x, finish.y);

        double emergencyArea = unit == null && cd.unitsAt(finish.x, finish.y, MAX_AREA).count() > 2 ? MAX_AREA : PADDING;

        opened.offer(startNode);
        closed[index(startNode.x, startNode.y, width)] = startNode;

        int i = 0;

        while (!opened.isEmpty()) {
            if (++i > MAX_POINTS) {
                LOG.printf("Terminated by timeout%n");
                break;
            }

            AStarNode node = opened.poll();

            if (node.isTarget(radius) || unit != null && cd.isNear(node.x, node.y, radius + emergencyArea, unit)) {
                return constructPath(cd, startNode, node, radius);
            }

            for (Direction direction : directions) {
                int x = node.x + direction.dx;
                int y = node.y + direction.dy;

                int index = index(x, y, width);

                AStarNode prev = closed[index];

                if (x >= 0 && y >= 0 && x < width && y < height && (prev == null || opened.contains(prev))) {
                    Collection<LivingUnit> units = cd.unitsAt(x, y, radius).collect(Collectors.toList());
                    AStarNode child;

                    if (!units.isEmpty()) {
                        child = new UnitNode(x, y, node, units);
/*
                    } else if (!cd.available(x, y, radius + PADDING)) {
                        child = new BorderNode(x, y, node);
*/
                    } else {
                        child = new EmptyNode(x, y, node);
                    }

                    if (prev == null || Double.compare(prev.cost(), child.cost()) > 0) {
                        if (prev != null) {
                            opened.remove(prev);
                        }

                        closed[index] = child;

                        if (logger != null && (prev == null || prev.getClass() != child.getClass())) {
                            logger.accept(child.toPoint(), child.getClass().getSimpleName());
                        }

                        opened.add(child);
                    }
                }
            }
        }

        LOG.printf("Emergency after %d iterations%n", i);

        AStarNode best = startNode;

        for (AStarNode node : closed) {
            if (node != null && Double.compare(best.estimatedCost(), node.estimatedCost()) >= 0) {
                if (Double.compare(best.estimatedCost(), node.estimatedCost()) > 0 || Double.compare(best.traversedCost(), node.traversedCost()) > 0) {
                    best = node;
                }
            }
        }

        return constructPath(cd, startNode, best, radius);
    }

    private static int index(int x, int y, int width) {
        return (x + y * width / STEP) / STEP;
    }

    private Collection<Point> constructPath(CollisionDetector cd, StartNode start, AStarNode node, double radius) {
        List<Point> result = new ArrayList<>();

        Point startPoint = new Point(start.x, start.y);

        while (node != start) {
            Point point = new Point(node.x, node.y);

            result.add(point);

            if (cd.canPass(point, startPoint, radius + PADDING)) {
                break;
            }

            node = node.previous();
        }

        result.add(startPoint);

        Collections.reverse(result);

        return result;
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
