package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.action.MoveAction;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Checkpoint;
import com.codegans.ai.cup2016.model.MoveHistory;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.impl.CollisionDetectorImpl;
import com.codegans.ai.cup2016.navigator.impl.NavigatorImpl;
import model.Bonus;
import model.Building;
import model.BuildingType;
import model.Faction;
import model.Game;
import model.LaneType;
import model.LivingUnit;
import model.Minion;
import model.Player;
import model.Projectile;
import model.SkillType;
import model.Tree;
import model.Wizard;
import model.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static model.SkillType.ADVANCED_MAGIC_MISSILE;
import static model.SkillType.FIREBALL;
import static model.SkillType.FROST_BOLT;
import static model.SkillType.HASTE;
import static model.SkillType.MAGICAL_DAMAGE_ABSORPTION_AURA_1;
import static model.SkillType.MAGICAL_DAMAGE_ABSORPTION_AURA_2;
import static model.SkillType.MAGICAL_DAMAGE_ABSORPTION_PASSIVE_1;
import static model.SkillType.MAGICAL_DAMAGE_ABSORPTION_PASSIVE_2;
import static model.SkillType.MAGICAL_DAMAGE_BONUS_AURA_1;
import static model.SkillType.MAGICAL_DAMAGE_BONUS_AURA_2;
import static model.SkillType.MAGICAL_DAMAGE_BONUS_PASSIVE_1;
import static model.SkillType.MAGICAL_DAMAGE_BONUS_PASSIVE_2;
import static model.SkillType.MOVEMENT_BONUS_FACTOR_AURA_1;
import static model.SkillType.MOVEMENT_BONUS_FACTOR_AURA_2;
import static model.SkillType.MOVEMENT_BONUS_FACTOR_PASSIVE_1;
import static model.SkillType.MOVEMENT_BONUS_FACTOR_PASSIVE_2;
import static model.SkillType.RANGE_BONUS_AURA_1;
import static model.SkillType.RANGE_BONUS_AURA_2;
import static model.SkillType.RANGE_BONUS_PASSIVE_1;
import static model.SkillType.RANGE_BONUS_PASSIVE_2;
import static model.SkillType.SHIELD;
import static model.SkillType.STAFF_DAMAGE_BONUS_AURA_1;
import static model.SkillType.STAFF_DAMAGE_BONUS_AURA_2;
import static model.SkillType.STAFF_DAMAGE_BONUS_PASSIVE_1;
import static model.SkillType.STAFF_DAMAGE_BONUS_PASSIVE_2;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 18:55
 */
public final class GameMap {
    private static final Object MUTEX = new Object();
    private static final Logger LOG = LoggerFactory.getLogger();
    private static final SkillType[][] BRANCHES = {
            {RANGE_BONUS_PASSIVE_1, RANGE_BONUS_AURA_1, RANGE_BONUS_PASSIVE_2, RANGE_BONUS_AURA_2, ADVANCED_MAGIC_MISSILE},
            {MAGICAL_DAMAGE_BONUS_PASSIVE_1, MAGICAL_DAMAGE_BONUS_AURA_1, MAGICAL_DAMAGE_BONUS_PASSIVE_2, MAGICAL_DAMAGE_BONUS_AURA_2, FROST_BOLT},
            {STAFF_DAMAGE_BONUS_PASSIVE_1, STAFF_DAMAGE_BONUS_AURA_1, STAFF_DAMAGE_BONUS_PASSIVE_2, STAFF_DAMAGE_BONUS_AURA_2, FIREBALL},
            {MOVEMENT_BONUS_FACTOR_PASSIVE_1, MOVEMENT_BONUS_FACTOR_AURA_1, MOVEMENT_BONUS_FACTOR_PASSIVE_2, MOVEMENT_BONUS_FACTOR_AURA_2, HASTE},
            {MAGICAL_DAMAGE_ABSORPTION_PASSIVE_1, MAGICAL_DAMAGE_ABSORPTION_AURA_1, MAGICAL_DAMAGE_ABSORPTION_PASSIVE_2, MAGICAL_DAMAGE_ABSORPTION_AURA_2, SHIELD}
    };
    // Each element is 500x500 points
    //  ..a.b.c.d.e.f.g.h..
    // 1. T T T T T T T   .1
    // 2. T           M B .2
    // 3. T         M   B .3
    // 4. T       M     B .4
    // 5. T     M       B .5
    // 6. T   M         B .6
    // 7. T M           B .7
    // 8.   B B B B B B B .8
    //  ..a.b.c.d.e.f.g.h..
    private static final Checkpoint[][] LANES = {
            Checkpoint.createLane("a7", "a6", "a5", "a4", "a3", "a2", "a1", "b1", "c1", "d1", "e1", "f1", "g1"),
            Checkpoint.createLane("b7", "c6", "d5", "e4", "f3", "g2"),
            Checkpoint.createLane("b8", "c8", "d8", "e8", "f8", "g8", "h8", "h7", "h6", "h5", "h4", "h3", "h2")
    };

    private static GameMap instance;

    private final int[] skills = new int[5];
    private final double width;
    private final double height;
    private final Collection<Tree> trees = new ArrayList<>();
    private final Collection<Wizard> wizards = new ArrayList<>();
    private final Collection<Minion> minions = new ArrayList<>();
    private final Collection<Building> buildings = new ArrayList<>();
    private final Collection<Bonus> bonuses = new ArrayList<>();
    private final Collection<Projectile> projectiles = new ArrayList<>();
    private final Navigator navigator;
    private final CollisionDetector cd;
    private final FixedQueue<MoveHistory> intentions = new FixedQueue<>(new MoveHistory[2]);
    private final Deque<Point> stack = new ArrayDeque<>();
    private World world;
    private double maxStandardTurnAngle;
    private double maxStandardForwardSpeed;
    private double maxStandardBackwardSpeed;
    private double maxStandardStrafeSpeed;
    private Wizard self;
    private Point target;
    private LaneType lane;
    private int version = -1;
    private boolean resurrected = false;

    private GameMap(int width, int height) {
        this.width = width;
        this.height = height;

        this.cd = new CollisionDetectorImpl(width, height, this::trees, this::towers, this::minions, this::wizards);
        this.navigator = new NavigatorImpl(this);
    }

    public static GameMap get(World world, Game game) {
        GameMap map = get(world);

        map.maxStandardForwardSpeed = game.getWizardForwardSpeed();
        map.maxStandardBackwardSpeed = game.getWizardBackwardSpeed();
        map.maxStandardStrafeSpeed = game.getWizardStrafeSpeed();
        map.maxStandardTurnAngle = game.getWizardMaxTurnAngle();

        return map;
    }

    public static GameMap get(World world) {
        GameMap i = instance;

        if (i == null) {
            synchronized (MUTEX) {
                if (instance == null) {
                    instance = new GameMap((int) world.getWidth(), (int) world.getHeight());

                    try {
                        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                            LOG.print("###############################\n");
                            for (Player player : instance.world.getPlayers()) {
                                LOG.printf("## %19s: %4d ##%n", player.getName(), player.getScore());
                            }
                            LOG.print("###############################\n");
                        }));
                    } catch (SecurityException e) {
                        //Ignore. Running on a target platform
                    }
                }

                i = instance;
            }
        }

        return i.update(world);
    }

    public Collection<MoveHistory> lastActions() {
        return new ArrayList<>(Arrays.asList(intentions.toArray()));
    }

    public void action(MoveAction action) {
        intentions.offer(new MoveHistory(new Point(self), action.speed(), action.strafe(), action.turn()));
    }

    public void savepoint(Point target) {
        stack.push(target);
    }

    public Point savepoint() {
        return stack.pop();
    }

    public double limitAngle(double angle) {
        return limit(angle, maxStandardTurnAngle, -maxStandardTurnAngle);
    }

    public double limitSpeed(double speed) {
        return limit(speed, maxStandardForwardSpeed, -maxStandardBackwardSpeed);
    }

    public double limitStrafe(double speed) {
        return limit(speed, maxStandardStrafeSpeed, -maxStandardStrafeSpeed);
    }

    public int skillBranch(SkillType skill) {
        for (int i = 0; i < BRANCHES.length; i++) {
            for (SkillType s : BRANCHES[i]) {
                if (s == skill) {
                    return i;
                }
            }
        }

        return -1;
    }

    public SkillType[][] unknownSkills() {
        SkillType[][] result = BRANCHES.clone();

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < skills[i]; j++) {
                result[i][j] = null;
            }
        }

        return result;
    }

    public boolean learnSkill(SkillType skill) {
        for (int i = 0; i < BRANCHES.length; i++) {
            for (int j = 0; j < BRANCHES[i].length; j++) {
                if (BRANCHES[i][j] == skill) {
                    if (j == skills[i]) {
                        skills[i] = j + 1;
                        return true;
                    }

                    return false;
                }
            }
        }

        return false;
    }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public Point target() {
        return target;
    }

    public void target(Point point) {
        this.target = point;
    }

    public Wizard self() {
        return self;
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

    public Stream<Bonus> bonuses() {
        return bonuses.stream();
    }

    public Stream<Projectile> projectiles() {
        return projectiles.stream();
    }

    public boolean isResurrected() {
        return resurrected;
    }

    public boolean isVisible(Point point, double radius) {
        return minions().filter(GameMap::isFriend)
                .anyMatch(e -> Double.compare(e.getRadius() + e.getVisionRange(), e.getDistanceTo(point.x, point.y) - radius) < 0)
                || wizards().filter(GameMap::isFriend)
                .anyMatch(e -> Double.compare(e.getRadius() + e.getVisionRange(), e.getDistanceTo(point.x, point.y) - radius) < 0)
                || towers().filter(GameMap::isFriend)
                .anyMatch(e -> Double.compare(e.getRadius() + e.getVisionRange(), e.getDistanceTo(point.x, point.y) - radius) < 0);

    }

    public boolean isNotVisible(Point point, double radius) {
        return minions().filter(GameMap::isFriend)
                .noneMatch(e -> Double.compare(e.getRadius() + e.getVisionRange(), e.getDistanceTo(point.x, point.y) - radius) < 0)
                && wizards().filter(GameMap::isFriend)
                .noneMatch(e -> Double.compare(e.getRadius() + e.getVisionRange(), e.getDistanceTo(point.x, point.y) - radius) < 0)
                && towers().filter(GameMap::isFriend)
                .noneMatch(e -> Double.compare(e.getRadius() + e.getVisionRange(), e.getDistanceTo(point.x, point.y) - radius) < 0);

    }

    public static boolean isEnemy(LivingUnit unit) {
        return !isFriend(unit) && !isNeutral(unit);
    }

    public static boolean isFriend(LivingUnit unit) {
        return unit.getFaction() == instance.self.getFaction();
    }

    public static boolean isNeutral(LivingUnit unit) {
        Faction faction = unit.getFaction();

        return faction == Faction.OTHER || faction == Faction.NEUTRAL && Double.compare(unit.getSpeedX(), 0) == 0 && Double.compare(unit.getSpeedY(), 0) == 0;
    }

    public int tick() {
        return version;
    }

    public CollisionDetector cd() {
        return cd;
    }

    public Point home() {
        return towers().filter(GameMap::isFriend).filter(e -> e.getType() == BuildingType.FACTION_BASE).map(Point::new).findAny().orElse(new Point(0, 0));
    }

    public LaneType lane() {
        return lane;
    }

    public void lane(LaneType lane) {
        this.lane = lane;
    }

    public Collection<Checkpoint> checkpoints(LaneType lane) {
        return new ArrayList<>(Arrays.asList(LANES[lane.ordinal()]));
    }

    public Point nearestTower() {
        Wizard self = self();

        return towers().filter(GameMap::isFriend).sorted(Comparator.comparingDouble(self::getDistanceTo)).map(Point::new).findFirst().orElse(home());
    }

    public Navigator navigator() {
        return navigator;
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
        }

        version = world.getTickIndex();

        wizards.clear();
        minions.clear();
        bonuses.clear();

        stream(world.getWizards()).filter(Wizard::isMe).forEach(e -> self = e);

        stream(world.getMinions()).filter(e -> e.getLife() > 0).forEach(minions::add);
        stream(world.getWizards()).filter(e -> e.getLife() > 0).filter(e -> !e.isMe()).forEach(wizards::add);
        stream(world.getBonuses()).forEach(bonuses::add);

        stream(world.getTrees()).filter(e -> e.getLife() > 0).forEach(trees::add);

        Collection<Building> allTowers = keepInvisible(
                towers(),
                world.getBuildings(),
                e -> new Building(e.getId(), e.getX(), e.getY(), e.getSpeedX(), e.getSpeedY(), e.getAngle(), e.getFaction(), e.getRadius(), e.getLife(), e.getMaxLife(), e.getStatuses(), e.getType(), e.getVisionRange(), e.getAttackRange(), e.getDamage(), e.getCooldownTicks(), 0));

        Collection<Tree> allTrees = keepInvisible(
                trees(),
                world.getTrees(),
                e -> e);

        trees.clear();
        buildings.clear();

        trees.addAll(allTrees);
        buildings.addAll(allTowers);

        return this;
    }

    private <T extends LivingUnit> Collection<T> keepInvisible(Stream<T> stream, T[] items, Function<T, T> mapper) {
        Collection<T> results = new TreeSet<>((l, r) -> {
            int result = Double.compare(l.getX(), r.getX());

            if (result != 0) {
                return result;
            }

            return Double.compare(l.getY(), r.getY());
        });

        results.addAll(Arrays.asList(items));

        stream.filter(e -> isNotVisible(new Point(e), e.getRadius()))
                .map(mapper)
                .distinct()
                .forEach(results::add);

        return results;
    }

    private static double limit(double value, double max, double min) {
        if (Double.compare(value, min) < 0) {
            return min;
        }

        if (Double.compare(value, max) > 0) {
            return max;
        }

        return value;
    }
}
