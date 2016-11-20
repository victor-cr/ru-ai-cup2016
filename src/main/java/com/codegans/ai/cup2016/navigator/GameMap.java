package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.CheckPoint;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.impl.CollisionDetectorImpl;
import com.codegans.ai.cup2016.navigator.impl.NavigatorImpl;
import model.Building;
import model.Faction;
import model.LaneType;
import model.LivingUnit;
import model.Minion;
import model.Tree;
import model.Wizard;
import model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 18:55
 */
public class GameMap {
    private static final Object MUTEX = new Object();
    private static final Logger LOG = LoggerFactory.getLogger();
    private static final int HISTORY_SIZE = 10;

    private static volatile GameMap instance;

    private final double width;
    private final double height;
    private final Collection<Tree> trees = new ArrayList<>();
    private final Collection<Wizard> wizards = new ArrayList<>();
    private final Collection<Minion> minions = new ArrayList<>();
    private final Collection<Building> buildings = new ArrayList<>();
    private final PointQueue history = new PointQueue(HISTORY_SIZE);
    private final NavigatorFactory navigatorFactory;
    private final CollisionDetectorFactory collisionDetectorFactory;
    private volatile World world;
    private volatile Wizard self;
    private volatile int version = -1;
    private volatile boolean resurrected = false;

    private GameMap(int width, int height) {
        this.width = width;
        this.height = height;

        this.collisionDetectorFactory = new CollisionDetectorFactory() {
            @Override
            public CollisionDetector full() {
                return new CollisionDetectorImpl(width, height, GameMap.this::trees, GameMap.this::towers, GameMap.this::minions, GameMap.this::wizards);
            }

            @Override
            public CollisionDetector staticOnly() {
                return new CollisionDetectorImpl(width, height, GameMap.this::trees, GameMap.this::towers);
            }
        };

        this.navigatorFactory = new NavigatorFactory() {
            @Override
            public Navigator full() {
                return new NavigatorImpl(collisionDetector().full(), () -> self, () -> history);
            }

            @Override
            public Navigator staticOnly() {
                return new NavigatorImpl(collisionDetector().staticOnly(), () -> self, () -> history);
            }
        };
    }

    public static GameMap get(World world) {
        GameMap i = instance;

        if (i == null) {
            synchronized (MUTEX) {
                if (instance == null) {
                    instance = new GameMap((int) world.getWidth(), (int) world.getHeight());
                }

                i = instance;
            }
        }

        return i.update(world);
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public Stream<Tree> trees() {
        return trees.stream();
    }

    public Stream<Building> towers() {
        return buildings.stream();
    }

    public Stream<Minion> minions() {
        return minions.stream();
    }

    public Stream<Wizard> wizards() {
        return wizards.stream();
    }

    public boolean isResurrected() {
        return resurrected;
    }

    public boolean isEnemy(LivingUnit unit) {
        return !isFriend(unit) && !isNeutral(unit);
    }

    public boolean isFriend(LivingUnit unit) {
        return unit.getFaction() == self.getFaction();
    }

    public boolean isNeutral(LivingUnit unit) {
        return unit.getFaction() == Faction.NEUTRAL || unit.getFaction() == Faction.OTHER;
    }

    public CollisionDetectorFactory collisionDetector() {
        return collisionDetectorFactory;
    }

    public Collection<CheckPoint> checkpoints() {
        return Arrays.asList(
                new CheckPoint(new Point(200, 3800), LaneType.TOP),
                new CheckPoint(new Point(200, 2700), LaneType.TOP),
                new CheckPoint(new Point(200, 1600), LaneType.TOP),
                new CheckPoint(new Point(200, 200), LaneType.TOP),
                new CheckPoint(new Point(1600, 200), LaneType.TOP),
                new CheckPoint(new Point(2700, 200), LaneType.TOP),
                new CheckPoint(new Point(3800, 200), LaneType.TOP),

                new CheckPoint(new Point(200, 3800), LaneType.MIDDLE),
                new CheckPoint(new Point(1200, 2800), LaneType.MIDDLE),
                new CheckPoint(new Point(2000, 2000), LaneType.MIDDLE),
                new CheckPoint(new Point(2800, 1200), LaneType.MIDDLE),
                new CheckPoint(new Point(3800, 200), LaneType.MIDDLE),

                new CheckPoint(new Point(200, 3800), LaneType.BOTTOM),
                new CheckPoint(new Point(1600, 3800), LaneType.BOTTOM),
                new CheckPoint(new Point(2700, 3800), LaneType.BOTTOM),
                new CheckPoint(new Point(3800, 3800), LaneType.BOTTOM),
                new CheckPoint(new Point(3800, 2700), LaneType.BOTTOM),
                new CheckPoint(new Point(3800, 1600), LaneType.BOTTOM),
                new CheckPoint(new Point(3800, 200), LaneType.BOTTOM)
        );
    }

    public NavigatorFactory navigator() {
        return navigatorFactory;
    }

    private GameMap update(World world) {
        if (version == world.getTickIndex()) {
            return this;
        }

        this.world = world;
        this.resurrected = world.getTickIndex() - version > 1;

        if (resurrected) {
            LOG.printf("#########################################%n");
            LOG.printf("## We have been dead for a while: %4d ##%n", world.getTickIndex() - version);
            LOG.printf("#########################################%n");

            history.clear();
        }

        version = world.getTickIndex();

        trees.clear();
        wizards.clear();
        minions.clear();
        buildings.clear();

        Arrays.stream(world.getWizards()).filter(Wizard::isMe).forEach(e -> self = e);

        Arrays.stream(world.getTrees()).filter(e -> e.getLife() > 0).forEach(trees::add);
        Arrays.stream(world.getMinions()).filter(e -> e.getLife() > 0).forEach(minions::add);
        Arrays.stream(world.getBuildings()).filter(e -> e.getLife() > 0).forEach(buildings::add);
        Arrays.stream(world.getWizards()).filter(e -> e.getLife() > 0).filter(e -> !e.isMe()).forEach(wizards::add);

        history.offer(new Point(self));

        return this;
    }
}
