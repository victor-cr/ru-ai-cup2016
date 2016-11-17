package com.codegans.ai.cup2016;

import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import model.Building;
import model.CircularUnit;
import model.LivingUnit;
import model.Minion;
import model.Tree;
import model.Wizard;
import model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.StrictMath.hypot;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 18:55
 */
public class GameMap {
    private static final int DEFAULT_PIXEL_BLOCK = 1;
    private static final Object MUTEX = new Object();
    private static final Logger LOG = LoggerFactory.getLogger();

    private static volatile GameMap instance;

    private final int pixelBlock;
    private final int width;
    private final int height;
    private final BitSet coverage;
    private final Collection<Building> buildings = new ArrayList<>();
    private final Collection<Minion> minions = new ArrayList<>();
    private final Collection<Wizard> wizards = new ArrayList<>();
    private final Collection<Tree> trees = new ArrayList<>();

    private GameMap(int pixelBlock, int width, int height) {
        this.pixelBlock = pixelBlock;
        this.width = width / pixelBlock;
        this.height = height / pixelBlock;

        this.coverage = new BitSet(this.width * this.height);
    }

    public static GameMap get(World world) {
        return get(world, DEFAULT_PIXEL_BLOCK);
    }

    public static GameMap get(World world, int pixelBlock) {
        GameMap i = instance;

        if (i == null) {
            synchronized (MUTEX) {
                if (instance == null) {
                    instance = new GameMap(pixelBlock, (int) world.getWidth(), (int) world.getHeight());

                    LOG.printf("Constructed the game map: %f x %f [%dx%d] = %d%n", world.getWidth(), world.getHeight(), DEFAULT_PIXEL_BLOCK, DEFAULT_PIXEL_BLOCK, instance.coverage.size());
                }

                i = instance;
            }
        }

        if (pixelBlock != instance.pixelBlock) {
            throw new IllegalArgumentException("You cannot change pixel block size after GameMap creation");
        }

        return i.update(world);
    }

    public int cardinality() {
        return coverage.cardinality();
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public boolean available(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height && !coverage.get(y * width + x);
    }

    public boolean available(int x, int y, double radius) {
        if (!available(x, y)) {
            return false;
        }

        int startX = (int) StrictMath.floor(x - radius) / pixelBlock;
        int startY = (int) StrictMath.floor(y - radius) / pixelBlock;
        int finishX = (int) StrictMath.ceil(x + radius) / pixelBlock + 1;
        int finishY = (int) StrictMath.ceil(y + radius) / pixelBlock + 1;

        for (int i = startX; i < finishX; i++) {
            for (int j = startY; j < finishY; j++) {
                if (!available(i * pixelBlock, j * pixelBlock) && overlaps(i, j, pixelBlock, x, y, radius)) {
                    return false;
                }
            }
        }

        return true;
    }

    public CircularUnit find(int x, int y) {
        Predicate<? super CircularUnit> predicate = e -> overlaps(x, y, 1, e.getX(), e.getY(), e.getRadius());

        Optional<LivingUnit> optional = Stream.concat(Stream.concat(buildings.stream(), minions.stream()), Stream.concat(trees.stream(), wizards.stream())).filter(predicate).findAny();

        return optional.isPresent() ? optional.get() : null;
    }

    private GameMap update(World world) {
        coverage.clear();
        buildings.clear();
        minions.clear();
        wizards.clear();
        trees.clear();

        Arrays.stream(world.getBuildings())
                .filter(e -> e.getLife() > 0)
                .peek(this::updateUnit).forEach(buildings::add);
        Arrays.stream(world.getMinions())
                .filter(e -> e.getLife() > 0)
                .peek(this::updateUnit).forEach(minions::add);
        Arrays.stream(world.getWizards())
                .filter(e -> e.getLife() > 0)
                .filter(e -> !e.isMe())
                .peek(this::updateUnit).forEach(wizards::add);
        Arrays.stream(world.getTrees())
                .filter(e -> e.getLife() > 0)
                .peek(this::updateUnit).forEach(trees::add);

        return this;
    }

    private <T extends CircularUnit> void updateUnit(T unit) {
        double radius = unit.getRadius();
        double x = unit.getX();
        double y = unit.getY();

        int startX = (int) StrictMath.floor(x - radius) / pixelBlock;
        int startY = (int) StrictMath.floor(y - radius) / pixelBlock;
        int finishX = (int) StrictMath.ceil(x + radius) / pixelBlock + 1;
        int finishY = (int) StrictMath.ceil(y + radius) / pixelBlock + 1;

        LOG.printf("Validating %s: (%f,%f)[%f]%n", unit, x, y, radius);

        for (int i = startX; i < finishX; i++) {
            for (int j = startY; j < finishY; j++) {
                if (overlaps(i, j, pixelBlock, x, y, radius)) {
                    coverage.set(j * width + i);

                    LOG.printf("Marked the block: [(%d,%d)->(%d,%d)]%n", i * pixelBlock, j * pixelBlock, (i + 1) * pixelBlock, (j + 1) * pixelBlock);
                }
            }
        }
    }

    private static boolean overlaps(int i, int j, int pixelBlock, double x, double y, double radius) {
        double leftX = i * pixelBlock;
        double topY = j * pixelBlock;
        double rightX = leftX + pixelBlock;
        double bottomY = topY + pixelBlock;

        return leftX <= x - radius && rightX >= x + radius && topY <= y - radius && bottomY >= y + radius
                || hypot(x - leftX, y - topY) < radius
                || hypot(x - rightX, y - topY) < radius
                || hypot(x - leftX, y - bottomY) < radius
                || hypot(x - rightX, y - bottomY) < radius
                || leftX < x && rightX > x && (topY >= y - radius || bottomY >= y + radius)
                || topY < y && bottomY > y && (leftX >= x - radius || rightX >= x + radius);
    }
}
