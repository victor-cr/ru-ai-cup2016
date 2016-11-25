package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.action.MoveAction;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
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

    @Override
    public Stream<Action> decide(Wizard self, World world, Game game, Move move) {
        GameMap map = GameMap.get(world);

        return doActions(self, world, game, map);
    }

    protected abstract Stream<Action> doActions(Wizard self, World world, Game game, GameMap map);

    protected Stream<Action> retreat(Wizard self, LivingUnit enemy, Game game, GameMap map, int score) {
        Point retreat = map.nearestTower();

        LOG.logTarget(retreat, map.tick());

        if (enemy == null) {
            return go(self, retreat, game, map, score);
        }

        return goWatching(self, retreat, enemy, game, map, score);
    }

    protected Stream<Action> turnAndGo(Wizard self, Point checkpoint, Game game, GameMap map, int score) {
        return Stream.of(new MoveAction(score, map, move -> {
            Point target = map.navigator().next(checkpoint, true);

            double angle = self.getAngleTo(target.x, target.y);
            double distance = self.getDistanceTo(target.x, target.y);

            double dx = cos(angle) * distance;
            double dy = sin(angle) * distance;

            move.setSpeed(dx);
            move.setStrafeSpeed(dy);
            move.setTurn(angle);

            return target;
        }));
    }

    protected Stream<Action> go(Wizard self, Point checkpoint, Game game, GameMap map, int score) {
        return Stream.of(new MoveAction(score, map, move -> {
            Point target = map.navigator().next(checkpoint, false);

            double angle = self.getAngleTo(target.x, target.y);
            double distance = self.getDistanceTo(target.x, target.y);

            double dx = cos(angle) * distance;
            double dy = sin(angle) * distance;

            move.setSpeed(dx);
            move.setStrafeSpeed(dy);
            move.setTurn(angle);

            return target;
        }));
    }

    protected Stream<Action> goWatching(Wizard self, Point checkpoint, LivingUnit unit, Game game, GameMap map, int score) {
        return Stream.of(new MoveAction(score, map, move -> {
            Point target = map.navigator().next(checkpoint, false);

            double unitAngle = self.getAngleTo(unit);
            double checkpointAngle = self.getAngleTo(target.x, target.y);
            double checkpointDistance = self.getDistanceTo(target.x, target.y);

            double angle = checkpointAngle - unitAngle;

            double dx = cos(angle) * checkpointDistance;
            double dy = sin(angle) * checkpointDistance;

            move.setSpeed(dx);
            move.setStrafeSpeed(dy);
            move.setTurn(unitAngle);

            return checkpoint;
        }));
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
