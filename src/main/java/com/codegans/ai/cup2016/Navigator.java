package com.codegans.ai.cup2016;

import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Circle;
import com.codegans.ai.cup2016.model.Point;
import model.Faction;
import model.Wizard;
import model.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 10:14
 */
public class Navigator {
    private final Logger log = LoggerFactory.getLogger();

    public Collection<Point> checkpoints(World world) {
        Supplier<IllegalStateException> errorSupplier = () -> new IllegalStateException("Impossible situation");

        Faction allies = world.getMyPlayer().getFaction();
        Wizard me = Arrays.stream(world.getWizards()).filter(Wizard::isMe).findAny().orElseThrow(errorSupplier);

        Faction enemies = Stream.of(Faction.ACADEMY, Faction.RENEGADES).filter(e -> e == allies).findAny().orElseThrow(errorSupplier);

        return Arrays.stream(world.getBuildings())
                .filter(e -> e.getFaction() == enemies)
                .sorted((x, y) -> Double.compare(x.getDistanceTo(me), y.getDistanceTo(me)))
                .map(Point::new)
                .collect(Collectors.toList());
    }

    public Collection<Point> path(Wizard wizard, World world) {
        double radius = wizard.getRadius();
        double halfRadius = radius / 2;
        int x = (int) (wizard.getX() / radius);
        int y = (int) (wizard.getY() / radius);
        int width = (int) (world.getWidth() / radius);
        int height = (int) (world.getHeight() / radius);

        boolean[][] visited = new boolean[width][height];

        Queue<Node> queue = new ArrayDeque<>();
        queue.add(new Node(null, x, y));
        visited[x][y] = true;
        ProximityDetector detector = new ProximityDetector(world);

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            Node child;

            while ((child = nextNode(node, visited)) != null) {
                Point center = new Point(x * radius + halfRadius, y * radius + halfRadius);

                if (!detector.hasCollision(new Circle(center, halfRadius), e -> e != wizard)) {
                    log.printf("Go deeper%n");
                    queue.offer(child);
                } else if (detector.hasTowerCollision(new Circle(center, radius), e -> true)) {
                    log.print("Result%n");
                    return resolve(child, radius);
                }

                visited[node.x][node.y] = true;
            }
        }

        return Collections.emptySet();
    }

    private static Collection<Point> resolve(Node node, double radius) {
        Collection<Point> result = new ArrayList<>();

        while (node != null) {
            result.add(new Point(node.x * radius + radius / 2, node.y * radius + radius / 2));

            node = node.parent;
        }

        return result;
    }

    private static Node nextNode(Node node, boolean[][] visited) {
        int x = node.x;
        int y = node.y;

        if (x > 0 && !visited[x - 1][y]) {
            return new Node(node, x - 1, y);
        }

        if (y > 0 && !visited[x][y - 1]) {
            return new Node(node, x, y - 1);
        }

        if (x + 1 < visited.length && !visited[x + 1][y]) {
            return new Node(node, x + 1, y);
        }

        if (y + 1 < visited[0].length && !visited[x][y + 1]) {
            return new Node(node, x, y + 1);
        }

        return null;
    }

    private static class Node {
        private final Node parent;
        private final int x, y;

        public Node(Node parent, int x, int y) {
            this.parent = parent;
            this.x = x;
            this.y = y;
        }
    }
}
