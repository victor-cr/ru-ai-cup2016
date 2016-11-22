package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import com.codegans.ai.cup2016.navigator.Navigator;
import model.ActionType;
import model.Building;
import model.Game;
import model.LivingUnit;
import model.Minion;
import model.MinionType;
import model.Wizard;
import model.World;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.StrictMath.*;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class BattleMoveDecision extends AbstractMoveDecision {
    private static final int SAFE_COOL_DOWN = 10;
    private static final double SAFE_POINT_DISTANCE = 100;
    private static final double PADDING = 5;

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map, Navigator navigator) {
        double x = self.getX();
        double y = self.getY();
        double r = self.getVisionRange();

        List<LivingUnit> units = fullCd.unitsAt(x, y, r).collect(Collectors.toList());

        LivingUnit enemy = units.stream().filter(map::isEnemy).sorted((a, b) -> Integer.compare(a.getLife(), b.getLife())).limit(1).findAny().orElse(null);

        if (enemy == null) {
            return Stream.empty();
        }

        if (enemy instanceof Minion) {
            return killMinion((Minion) enemy, self, map, game, units);
        }

        if (enemy instanceof Building) {
            return killTower((Building) enemy, self, map, game, units);
        }

        return killWizard((Wizard) enemy, self, map, game, units);
    }

    private Stream<Action> killMinion(Minion enemy, Wizard self, GameMap map, Game game, Collection<LivingUnit> units) {
        if (enemy.getType() == MinionType.ORC_WOODCUTTER) {
            return kill(enemy, self, map, game, units, game.getOrcWoodcutterAttackRange());
        }

        return kill(enemy, self, map, game, units, game.getFetishBlowdartAttackRange());
    }

    private Stream<Action> killWizard(Wizard enemy, Wizard self, GameMap map, Game game, Collection<LivingUnit> units) {
        return kill(enemy, self, map, game, units, enemy.getCastRange());
    }

    private Stream<Action> killTower(Building enemy, Wizard self, GameMap map, Game game, Collection<LivingUnit> units) {
        return kill(enemy, self, map, game, units, enemy.getAttackRange());
    }

    private Stream<Action> kill(LivingUnit enemy, Wizard self, GameMap map, Game game, Collection<LivingUnit> units, double castRange) {
        long enemies = units.stream()
                .filter(map::isEnemy).filter(e -> e instanceof Minion || e instanceof Wizard).filter(e -> isDanger(game, self, e, SAFE_COOL_DOWN * 10)).count();
        long friends = units.stream().filter(map::isFriend).filter(e -> Double.compare(abs(self.getAngleTo(e)), PI / 2) <= 0).count();
        long towerChargeTime = units.stream()
                .filter(map::isEnemy).filter(e -> e instanceof Building).map(e -> (Building) e).mapToInt(Building::getRemainingActionCooldownTicks).findAny().orElse(-1);

        if (enemies > 1 && friends < enemies || towerChargeTime > 0 && towerChargeTime < SAFE_COOL_DOWN * 3) {
            LOG.printf("Retreat!!! Tower remain: %d. Enemies around: %d. Friends around: %d%n", towerChargeTime, enemies, friends);

            return retreat(self, game, map, HIGH);
        }

        double enemyAngle = enemy.getAngleTo(self);
        double dangerAngle = game.getStaffSector() / 2;

        double distance = self.getDistanceTo(enemy);

        if (!isDanger(game, self, enemy, SAFE_COOL_DOWN)) {
            double dx = enemy.getX() - self.getX();
            double delta = self.getCastRange() - distance - PADDING;

            double slope = (enemy.getY() - self.getY()) / dx;

            double x = self.getX() - signum(delta) * sqrt(delta * delta / (1 + slope * slope));
            double y = slope * x + (self.getY() - slope * self.getX());

            Point shift = new Point(x, y);

            LOG.printf("Keep attack distance: %.3f (%.3f,%.3f)%n", delta, x, y);
            LOG.logTarget(shift, map.tick());

            return goWaitching(self, shift, enemy, game, HIGH);
        }

        double safeX = self.getX() + distance * cos(dangerAngle * signum(enemyAngle));
        double safeY = self.getY() + distance * sin(dangerAngle * signum(enemyAngle));

        Point point = new Point(safeX, safeY);

        double distanceTurn = self.getDistanceTo(point.x, point.y);
        double dangerRange = castRange - enemy.getDistanceTo(self);

        if (Double.compare(distanceTurn, dangerRange) > 0) {
            point = new Point(enemy).reflectTo(new Point(self)).plus(point);
        }

        LOG.printf("Avoid danger%n");
        LOG.logTarget(point, map.tick());

        return goWaitching(self, point, enemy, game, HIGH);
    }

    private boolean isDanger(Game game, Wizard self, LivingUnit unit, int safeCoolDown) {
        int coolDown;
        double attackRange;

        if (unit instanceof Minion) {
            Minion enemy = (Minion) unit;

            coolDown = enemy.getRemainingActionCooldownTicks();
            attackRange = enemy.getType() == MinionType.ORC_WOODCUTTER ? game.getOrcWoodcutterAttackRange() : game.getFetishBlowdartAttackRange();
        } else if (unit instanceof Building) {
            Building enemy = (Building) unit;

            coolDown = enemy.getRemainingActionCooldownTicks();
            attackRange = enemy.getAttackRange();
        } else {
            Wizard enemy = (Wizard) unit;

            coolDown = max(enemy.getRemainingActionCooldownTicks(), enemy.getRemainingCooldownTicksByAction()[ActionType.MAGIC_MISSILE.ordinal()]);
            attackRange = enemy.getCastRange();
        }

        double enemyAngle = unit.getAngleTo(self);
        double dangerAngle = game.getStaffSector() / 2;
        double distance = self.getDistanceTo(unit);

        return Double.compare(abs(enemyAngle), dangerAngle) < 0 && coolDown <= safeCoolDown && Double.compare(distance, attackRange + PADDING) <= 0;

    }
}
