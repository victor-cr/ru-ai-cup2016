package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.action.MoveAction;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.CheckPoint;
import com.codegans.ai.cup2016.model.MoveHistory;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.impl.CollisionDetectorImpl;
import com.codegans.ai.cup2016.navigator.impl.NavigatorImpl;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static model.SkillType.*;

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

    private static GameMap instance;

    private final int[] skills = new int[5];
    private final double width;
    private final double height;
    private final Collection<Tree> trees = new ArrayList<>();
    private final Collection<Wizard> wizards = new ArrayList<>();
    private final Collection<Minion> minions = new ArrayList<>();
    private final Collection<Building> buildings = new ArrayList<>();
    private final Collection<Projectile> projectiles = new ArrayList<>();
    private final Navigator navigator;
    private final CollisionDetector cd;
    private final FixedQueue<MoveHistory> intentions = new FixedQueue<>(new MoveHistory[5]);
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

    public Stream<Projectile> projectiles() {
        return projectiles.stream();
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

    public int tick() {
        return version;
    }

    public CollisionDetector cd() {
        return cd;
    }

    public Point home() {
        return towers().filter(this::isFriend).filter(e -> e.getType() == BuildingType.FACTION_BASE).map(Point::new).findAny().orElse(new Point(0, 0));
    }

    public LaneType lane() {
        return lane;
    }

    public void lane(LaneType lane) {
        this.lane = lane;
    }

    public Collection<CheckPoint> checkpoints() {
        return Arrays.asList(
                new CheckPoint(new Point(200, 3800), LaneType.TOP),
                new CheckPoint(new Point(200, 2700), LaneType.TOP),
                new CheckPoint(new Point(200, 1600), LaneType.TOP),
                new CheckPoint(new Point(350, 350), LaneType.TOP),
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
                new CheckPoint(new Point(3650, 3650), LaneType.BOTTOM),
                new CheckPoint(new Point(3800, 2700), LaneType.BOTTOM),
                new CheckPoint(new Point(3800, 1600), LaneType.BOTTOM),
                new CheckPoint(new Point(3800, 200), LaneType.BOTTOM)
        );
    }

    public Point nearestTower() {
        Wizard self = self();

        return towers().filter(this::isFriend).sorted((l, r) -> Double.compare(self.getDistanceTo(l), self.getDistanceTo(r))).map(Point::new).findFirst().orElse(home());
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

        trees.clear();
        wizards.clear();
        minions.clear();

        stream(world.getWizards()).filter(Wizard::isMe).forEach(e -> self = e);

        stream(world.getTrees()).filter(e -> e.getLife() > 0).forEach(trees::add);
        stream(world.getMinions()).filter(e -> e.getLife() > 0).forEach(minions::add);
        stream(world.getWizards()).filter(e -> e.getLife() > 0).filter(e -> !e.isMe()).forEach(wizards::add);

        List<Building> invisible = towers()
                .filter(e -> minions().filter(this::isFriend).noneMatch(x -> Double.compare(x.getRadius() + e.getVisionRange(), e.getDistanceTo(x)) < 0)
                        || wizards().filter(this::isFriend).noneMatch(x -> Double.compare(x.getRadius() + e.getVisionRange(), e.getDistanceTo(x)) < 0))
                .map(e -> new Building(e.getId(), e.getX(), e.getY(), e.getSpeedX(), e.getSpeedY(), e.getAngle(), e.getFaction(), e.getRadius(), e.getLife(), e.getMaxLife(), e.getStatuses(), e.getType(), e.getVisionRange(), e.getAttackRange(), e.getDamage(), e.getCooldownTicks(), 0))
                .distinct().collect(Collectors.toList());

        List<Building> sure = Arrays.stream(world.getBuildings())
                .filter(e -> e.getLife() > 0)
                .collect(Collectors.toList());

        buildings.clear();
        buildings.addAll(invisible);
        buildings.addAll(sure);

        return this;
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
