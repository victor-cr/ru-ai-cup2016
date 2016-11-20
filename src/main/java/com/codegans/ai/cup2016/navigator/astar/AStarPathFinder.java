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
import java.util.stream.IntStream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 17:30
 */
public class AStarPathFinder implements PathFinder {
    private static final int STEP = 5;
    private static final int PADDING = STEP * 2;
    private static final int MAX_AREA = 200;
    private static final int MAX_POINTS = 2000;
    private static final Logger LOG = LoggerFactory.getLogger();

    @Override
    public Collection<Point> traverse(CollisionDetector cd, Point start, Point finish, double radius, BiConsumer<Point, String> logger) {
        int width = (int) cd.width();
        int height = (int) cd.height();
        PriorityQueue<AStarNode> opened = new PriorityQueue<>();
        AStarNode[] closed = new AStarNode[width * height];
        Direction[] directions = Direction.values();

        AStarNode starNode = new StartNode(start, finish, STEP);

        LivingUnit unit = cd.unitAt(finish.x, finish.y);

        double emergencyArea = unit == null && cd.unitsAt(finish.x, finish.y, MAX_AREA).count() > 2 ? MAX_AREA : PADDING;

        opened.offer(starNode);

        int i = 0;

        while (!opened.isEmpty()) {
            if (++i > MAX_POINTS) {
                LOG.printf("Terminated by timeout%n");
                break;
            }

            AStarNode node = opened.poll();

            if (node.isTarget() || unit != null && cd.isNear(node.x, node.y, radius + emergencyArea, unit)) {
                return constructPath(cd, node, radius);
            }

            for (Direction direction : directions) {
                int x = node.x + direction.dx;
                int y = node.y + direction.dy;

                if (x >= 0 && y >= 0 && x < width && y < height) {
                    Collection<LivingUnit> units = cd.unitsAt(x, y, radius).collect(Collectors.toList());
                    AStarNode child;

                    if (!units.isEmpty()) {
                        child = new UnitNode(x, y, node, units);
                    } else if (!cd.available(x, y, radius + PADDING)) {
                        child = new BorderNode(x, y, node);
                    } else {
                        child = new EmptyNode(x, y, node);
                    }

                    int index = index(x, y, width);

                    AStarNode prev = closed[index];

                    if (prev == null || Double.compare(prev.cost(), child.cost()) > 0) {
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

        AStarNode best = starNode;

        for (AStarNode node : closed) {
            if (node != null && Double.compare(best.estimatedCost(), node.estimatedCost()) >= 0) {
                if (Double.compare(best.estimatedCost(), node.estimatedCost()) > 0 || Double.compare(best.traversedCost(), node.traversedCost()) > 0) {
                    best = node;
                }
            }
        }

        return constructPath(cd, best, radius);
    }

    private static int index(int x, int y, int width) {
        return (x + y * width) / STEP;
    }

    private Collection<Point> constructPath(CollisionDetector cd, AStarNode node, double radius) {
        List<Point> result = new ArrayList<>();

        while (node != null) {
            result.add(new Point(node.x, node.y));

            node = optimizePath(cd, node, radius);
        }

        for (int i = 0; i < result.size(); i++) {
            for (int j = result.size() - 1; j > i + 1; j--) {
                if (cd.canPass(result.get(i), result.get(j), radius + PADDING)) {
                    int from = i + 1;
                    IntStream.range(from, j - 1).forEach(e -> result.remove(from));
                    break;
                }
            }
        }

        Collections.reverse(result);

        return result;
    }

    private AStarNode optimizePath(CollisionDetector cd, AStarNode node, double radius) {
        AStarNode previous = node.previous();

        for (AStarNode temp = previous; temp != null && cd.canPass(node.toPoint(), previous.toPoint(), radius + STEP * 2); temp = previous.previous()) {
            previous = temp;
        }

        return previous;
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
