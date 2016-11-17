package com.codegans.ai.cup2016;

import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Circle;
import model.CircularUnit;
import model.World;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 22.11.2015 20:05
 */
public class ProximityDetector {
    private final Logger log = LoggerFactory.getLogger();
    private final World world;

    public ProximityDetector(World world) {
        this.world = world;
    }

    public boolean hasCollision(Circle circle, Predicate<? super CircularUnit> notCurrent) {
        if (circle.leastX() < 0 || circle.leastY() < 0 || circle.mostX() > world.getWidth() || circle.mostY() > world.getHeight()) {
            log.printf("Collision with border: %s%n", circle);
            return true;
        }

        Predicate<? super CircularUnit> all = e -> true;

        return Arrays.stream(world.getBuildings()).filter(notCurrent).filter(circle::overlaps).peek(e -> log.printf("Collision with building: %s -> %s%n", circle, e)).anyMatch(all) ||
                Arrays.stream(world.getMinions()).filter(notCurrent).filter(circle::overlaps).peek(e -> log.printf("Collision with minion: %s -> %s%n", circle, e)).anyMatch(all) ||
                Arrays.stream(world.getWizards()).filter(notCurrent).filter(circle::overlaps).peek(e -> log.printf("Collision with wizard: %s -> %s%n", circle, e)).anyMatch(all) ||
                Arrays.stream(world.getTrees()).filter(notCurrent).filter(circle::overlaps).peek(e -> log.printf("Collision with tree: %s -> %s%n", circle, e)).anyMatch(all);
    }

    public boolean hasTowerCollision(Circle circle, Predicate<? super CircularUnit> criterion) {
        if (circle.leastX() < 0 || circle.leastY() < 0 || circle.mostX() > world.getWidth() || circle.mostY() > world.getHeight()) {
            log.printf("Collision with border: %s%n", circle);
            return true;
        }

        if (Arrays.stream(world.getBuildings()).filter(criterion).anyMatch(circle::overlaps)) {
            log.printf("Collision with building: %s -> %n", circle);
            return true;
        }

        return false;
    }

    public boolean hasCollision(CircularUnit unit) {
        return hasCollision(unit, unit.getRadius());
    }

    public boolean hasCollision(CircularUnit unit, double radius) {
        return hasCollision(new Circle(unit, radius), e -> e != unit);
    }
}
