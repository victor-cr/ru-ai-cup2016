package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Building;
import model.Faction;
import model.Game;
import model.LivingUnit;
import model.Minion;
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
    private static final double PADDING = 5;

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        double x = self.getX();
        double y = self.getY();
        double r = self.getVisionRange();

        List<LivingUnit> units = map.cd().unitsAt(x, y, r).filter(e -> e.getFaction() != Faction.OTHER).collect(Collectors.toList());

        LivingUnit enemy = units.stream().filter(map::isEnemy).sorted((a, b) -> Integer.compare(a.getLife(), b.getLife())).findFirst().orElse(null);

        if (enemy == null) {
            return Stream.empty();
        }

        return kill(enemy, self, map, game, units);
    }

    private Stream<Action> kill(LivingUnit enemy, Wizard self, GameMap map, Game game, Collection<LivingUnit> units) {
        Collection<LivingUnit> enemies = units.stream()
                .filter(map::isEnemy).filter(e -> e instanceof Minion || e instanceof Wizard).filter(e -> isDanger(game, self, e, SAFE_COOL_DOWN * 10)).collect(Collectors.toList());
        long friends = units.stream().filter(map::isFriend).filter(e -> Double.compare(abs(self.getAngleTo(e)), PI / 2) <= 0).count();
        long towerChargeTime = units.stream()
                .filter(map::isEnemy).filter(e -> e instanceof Building).map(e -> (Building) e).mapToInt(Building::getRemainingActionCooldownTicks).findAny().orElse(-1);

        if (enemies.size() > 1 && friends < enemies.size() || towerChargeTime > 0 && towerChargeTime < SAFE_COOL_DOWN * 3) {
            LOG.printf("Retreat!!! Tower remain: %d. Enemies around: %d. Friends around: %d%n", towerChargeTime, enemies.size(), friends);

            return retreat(self, enemy, game, map, HIGH);
        }

        double distance = self.getDistanceTo(enemy);

        if (Double.compare(distance, self.getCastRange()) < 0 && enemies.stream().noneMatch(e -> isDanger(game, self, e, SAFE_COOL_DOWN))) {
            LOG.printf("Stay in safe zone. Kill'em all: (%.3f,%.3f)%n", enemy.getX(), enemy.getY());

            return goWatching(self, new Point(self), enemy, game, map, HIGH);
        }

        double dx = enemy.getX() - self.getX();
        double delta = self.getCastRange() - distance - PADDING;

        if (dx == 0) {
            return goWatching(self, new Point(self).minusY(enemy.getY() - self.getY() + delta), enemy, game, map, HIGH);
        }

        double slope = (enemy.getY() - self.getY()) / dx;

        double x = enemy.getX() - signum(delta) * sqrt(delta * delta / (1 + slope * slope));
        double y = slope * x - signum(delta) * (enemy.getY() - slope * enemy.getX());

        Point shift = new Point(x, y);

        LOG.printf("Keep attack distance: %.3f (%.3f,%.3f)%n", delta, x, y);
        LOG.logTarget(shift, map.tick());

        return goWatching(self, shift, enemy, game, map, HIGH);
    }
}
