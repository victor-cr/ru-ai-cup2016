package com.codegans.ai.cup2016;

import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import model.CircularUnit;
import model.Wizard;
import model.World;

import java.util.Arrays;
import java.util.BitSet;
import java.util.function.Predicate;

import static java.lang.StrictMath.hypot;
import static java.util.Arrays.stream;

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
        return x >= 0 && y >= 0 && x < width && y < height && coverage.get(y * width + x);
    }

    private GameMap update(World world) {
        coverage.clear();

        updateUnits(world.getBuildings(), e -> e.getLife() <= 0);
        updateUnits(world.getMinions(), e -> e.getLife() <= 0);
        updateUnits(world.getWizards(), e -> e.getLife() <= 0, Wizard::isMe);
        updateUnits(world.getTrees(), e -> e.getLife() <= 0);

        return this;
    }

    @SafeVarargs
    private final <T extends CircularUnit> void updateUnits(T[] units, Predicate<T>... exclusions) {
        Predicate<T> predicate = Arrays.stream(exclusions).map(Predicate::negate).reduce(e -> true, Predicate::and);

        stream(units).filter(predicate).forEach(this::updateUnit);
    }

    private <T extends CircularUnit> void updateUnit(T unit) {
        double radius = unit.getRadius();
        double x = unit.getX();
        double y = unit.getY();

        int startX = (int) StrictMath.floor(x - radius) / pixelBlock;
        int startY = (int) StrictMath.floor(y - radius) / pixelBlock;
        int finishX = (int) StrictMath.ceil(x + radius) / pixelBlock + 1;
        int finishY = (int) StrictMath.ceil(y + radius) / pixelBlock + 1;

        LOG.printf("Validating %s: (%f,%f)[%f]", unit, x, y, radius);

        for (int i = startX; i < finishX; i++) {
            for (int j = startY; j < finishY; j++) {
                if (overlaps(i, j, pixelBlock, x, y, radius)) {
                    coverage.set(j * width + i);

                    LOG.printf("Marked the block: [(%d,%d)->(%d,%d)]", i * pixelBlock, j * pixelBlock, (i + 1) * pixelBlock, (j + 1) * pixelBlock);
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
