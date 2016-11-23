package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.action.MoveAction;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.CollisionDetector;
import com.codegans.ai.cup2016.navigator.GameMap;
import com.codegans.ai.cup2016.navigator.Navigator;
import com.codegans.ai.cup2016.navigator.PointQueue;
import model.ActionType;
import model.Building;
import model.Game;
import model.LivingUnit;
import model.Minion;
import model.MinionType;
import model.Move;
import model.Wizard;
import model.World;

import java.util.stream.Stream;

import static java.lang.StrictMath.*;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 20:09
 */
public abstract class AbstractMoveDecision implements Decision {
    protected static final Logger LOG = LoggerFactory.getLogger();
    private static final double SAFE_POINT_DISTANCE = 50;

    protected final PointQueue safePoints = new PointQueue(10);
    protected GameMap map;
    protected Navigator navigator;
    protected CollisionDetector fullCd;

    @Override
    public Stream<Action> decide(Wizard self, World world, Game game, Move move) {
        if (map == null) {
            map = GameMap.get(world);
        }

        if (navigator == null || fullCd == null) {
            navigator = map.navigator().full();
            fullCd = map.collisionDetector().full();
        }

        double x = self.getX();
        double y = self.getY();
        double r = self.getVisionRange();

        boolean safe = fullCd.unitsAt(x, y, r).noneMatch(map::isEnemy);

        if (safe) {
            Point me = new Point(self);

            if (safePoints.size() == 0 || Double.compare(safePoints.tail(0).distanceTo(me), SAFE_POINT_DISTANCE) > 0) {
                safePoints.offer(me);
            }
        }

        return doActions(self, world, game, map, navigator);
    }

    protected abstract Stream<Action> doActions(Wizard self, World world, Game game, GameMap map, Navigator navigator);

    protected Stream<Action> retreat(Wizard self, LivingUnit enemy, Game game, GameMap map, int score) {
        Point retreat = map.home();

        while (safePoints.size() > 0) {
            retreat = safePoints.tail(0);

            if (Double.compare(self.getDistanceTo(retreat.x, retreat.y), SAFE_POINT_DISTANCE) > 0) {
                break;
            }

            safePoints.remove();
        }

        LOG.logTarget(retreat, map.tick());

        if (enemy == null) {
            return go(self, retreat, game, score);
        }

        return goWatching(self, retreat, enemy, game, score);
    }


    protected Stream<Action> turnAndGo(Wizard self, Point checkpoint, Game game, int score) {
        double angle = self.getAngleTo(checkpoint.x, checkpoint.y);
        double speed = game.getWizardForwardSpeed();

        if (Double.compare(abs(angle), PI / 2) > 0) {
            speed = 0;
        }

        return Stream.of(new MoveAction(score, checkpoint, speed, 0, angle));
//        double forwardSpeed = game.getWizardForwardSpeed();
//        double maxTurnAngle = game.getWizardMaxTurnAngle();
//        double requiredTurnAngle = self.getAngleTo(checkpoint.x, checkpoint.y);
//
//        if (Double.compare(maxTurnAngle, abs(requiredTurnAngle)) > 0) {
//            double angle = requiredTurnAngle;
//            double speed = forwardSpeed * (1 - abs(requiredTurnAngle / PI));
//
//            return Stream.of(new MoveAction(score, checkpoint, map.limitSpeed(speed), 0, angle));
//        }
//
//        double angle = StrictMath.signum(requiredTurnAngle) * maxTurnAngle;
//        double speed = (Double.compare(abs(requiredTurnAngle), PI / 2) < 0 ? forwardSpeed : -game.getWizardBackwardSpeed()) * requiredTurnAngle / PI;
//
//        return Stream.of(new MoveAction(score, checkpoint, map.limitSpeed(speed), 0, angle));
    }

    protected Stream<Action> go(Wizard self, Point checkpoint, Game game, int score) {

        double angle = self.getAngleTo(checkpoint.x, checkpoint.y);
        double distance = self.getDistanceTo(checkpoint.x, checkpoint.y);

        double dx = cos(angle) * distance;
        double dy = sin(angle) * distance;

        return Stream.of(new MoveAction(score, checkpoint, map.limitSpeed(dx), map.limitStrafe(dy), angle));
    }

    protected Stream<Action> goWatching(Wizard self, Point checkpoint, LivingUnit unit, Game game, int score) {
        double unitAngle = self.getAngleTo(unit);
        double checkpointAngle = self.getAngleTo(checkpoint.x, checkpoint.y);
        double checkpointDistance = self.getDistanceTo(checkpoint.x, checkpoint.y);

        double angle = checkpointAngle - unitAngle;

        double dx = cos(angle) * checkpointDistance;
        double dy = sin(angle) * checkpointDistance;

        return Stream.of(new MoveAction(score, checkpoint, map.limitSpeed(dx), map.limitStrafe(dy), unitAngle));
    }

    protected static boolean isDanger(Game game, Wizard self, LivingUnit unit, int safeCoolDown) {
        int coolDown;
        double attackRange;
        double dangerAngle;

        if (unit instanceof Minion) {
            Minion enemy = (Minion) unit;

            coolDown = enemy.getRemainingActionCooldownTicks();

            if (enemy.getType() == MinionType.ORC_WOODCUTTER) {
                attackRange = game.getOrcWoodcutterAttackRange();
                dangerAngle = game.getOrcWoodcutterAttackSector() / 2;
            } else {
                attackRange = game.getFetishBlowdartAttackRange();
                dangerAngle = game.getFetishBlowdartAttackSector() / 2;
            }
        } else if (unit instanceof Building) {
            Building enemy = (Building) unit;

            coolDown = enemy.getRemainingActionCooldownTicks();
            attackRange = enemy.getAttackRange();
            dangerAngle = PI;
        } else {
            Wizard enemy = (Wizard) unit;

            coolDown = max(enemy.getRemainingActionCooldownTicks(), enemy.getRemainingCooldownTicksByAction()[ActionType.MAGIC_MISSILE.ordinal()]);
            attackRange = enemy.getCastRange();
            dangerAngle = game.getStaffSector() / 2;
        }

        double enemyAngle = unit.getAngleTo(self);
        double distance = self.getDistanceTo(unit);

        return Double.compare(abs(enemyAngle), dangerAngle) < 0 && coolDown <= safeCoolDown && Double.compare(distance, attackRange + game.getWizardForwardSpeed()) <= 0;
    }

}
