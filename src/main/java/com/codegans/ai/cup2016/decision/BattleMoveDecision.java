package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Building;
import model.Game;
import model.LivingUnit;
import model.Minion;
import model.Wizard;
import model.World;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class BattleMoveDecision extends AbstractMoveDecision {
    private static final int DAMAGE_MULTIPLICATOR = 5;
    private static final int SAFE_COOL_DOWN = 10;
    private static final double PADDING = 5;

    public BattleMoveDecision(int priority) {
        super(priority);
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        double x = self.getX();
        double y = self.getY();
        double r = self.getVisionRange();

        List<LivingUnit> units = map.cd().unitsAt(x, y, r).filter(e -> !GameMap.isNeutral(e)).collect(Collectors.toList());

        Optional<LivingUnit> enemy = units.stream()
                .filter(GameMap::isEnemy)
                .sorted(Comparator.comparingInt(e -> estimate(e, self, world, game, map)))
                .findFirst();

        if (enemy.isPresent()) {
            return kill(enemy.get(), self, map, game, units);
        }

        return Stream.empty();
    }

    private Stream<Action> kill(LivingUnit enemy, Wizard self, GameMap map, Game game, Collection<LivingUnit> units) {
        Collection<LivingUnit> enemies = units.stream()
                .filter(GameMap::isEnemy)
                .filter(e -> e instanceof Minion || e instanceof Wizard)
                .filter(e -> isDanger(game, self, e, game.getWizardForwardSpeed(), SAFE_COOL_DOWN)).collect(Collectors.toList());
        long friends = units.stream().filter(GameMap::isFriend).filter(e -> Double.compare(abs(self.getAngleTo(e)), PI / 2) <= 0).count();
        long towerChargeTime = units.stream()
                .filter(GameMap::isEnemy)
                .filter(e -> e instanceof Building)
                .map(e -> (Building) e).mapToInt(Building::getRemainingActionCooldownTicks).findAny().orElse(-1);

        if (enemies.size() > 1 && friends < enemies.size() || towerChargeTime > 0 && towerChargeTime < SAFE_COOL_DOWN * 3) {
            LOG.printf("Retreat!!! Tower remain: %d. Enemies around: %d. Friends around: %d%n", towerChargeTime, enemies.size(), friends);

            return retreat(self, enemy, game, map, priority);
        }

        double distance = self.getDistanceTo(enemy);

        if (Double.compare(distance, self.getCastRange()) < 0 && enemies.isEmpty()) {
            LOG.printf("Stay in safe zone. Kill'em all: (%.3f,%.3f)%n", enemy.getX(), enemy.getY());

            return goWatching(self, new Point(self), enemy, game, map, priority);
        }

        double delta = self.getCastRange() - distance - PADDING;

        Point shift = new Point(self).shiftTo(new Point(enemy), delta);

        LOG.printf("Keep attack distance: %.3f %s%n", delta, shift);
        LOG.logTarget(shift, map.tick());

        return goWatching(self, shift, enemy, game, map, priority);
    }

    private static int estimate(LivingUnit enemy, Wizard self, World world, Game game, GameMap map) {
        int score = enemy.getLife();

        if (isDanger(game, self, enemy, 0, SAFE_COOL_DOWN)) {
            if (enemy instanceof Minion) {
                score -= ((Minion) enemy).getDamage() * DAMAGE_MULTIPLICATOR;
            } else if (enemy instanceof Wizard) {
                score -= ((Wizard) enemy).getLevel() + game.getMagicMissileDirectDamage() * DAMAGE_MULTIPLICATOR;
            } else if (enemy instanceof Building) {
                score -= ((Building) enemy).getDamage() * DAMAGE_MULTIPLICATOR;
            }
        }

        return score;
    }
}
