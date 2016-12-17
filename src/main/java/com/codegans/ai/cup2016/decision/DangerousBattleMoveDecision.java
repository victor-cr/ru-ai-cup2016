package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Building;
import model.Game;
import model.LivingUnit;
import model.Wizard;

import java.util.Collection;
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
public class DangerousBattleMoveDecision extends AbstractBattleMoveDecision {
    private static final double PADDING = 5;

    public DangerousBattleMoveDecision(int priority) {
        super(priority);
    }

    @Override
    protected Stream<Action> moveToKill(LivingUnit enemy, Wizard self, GameMap map, Game game, Collection<LivingUnit> units) {
        Collection<LivingUnit> enemies = units.stream().filter(GameMap::isEnemy)
                .filter(e -> isDanger(game, self, e, PADDING, SAFE_COOL_DOWN))
                .collect(Collectors.toList());
        long friends = units.stream().filter(GameMap::isFriend)
                .filter(e -> Double.compare(self.getDistanceTo(e), self.getCastRange()) <= 0)
                .filter(e -> Double.compare(abs(self.getAngleTo(e)), PI / 2) <= 0)
                .count();

        if (enemies.size() > 1 && friends < enemies.size()) {
            LOG.printf("Retreat!!! Enemies around: %d. Friends around: %d%n", enemies.size(), friends);

            return retreat(self, enemy, game, map, priority);
        }

        Optional<Building> optional = map.towers()
                .filter(GameMap::isEnemy)
                .filter(e -> Double.compare(self.getDistanceTo(e), e.getAttackRange() + self.getRadius() + PADDING) <= 0)
                .findAny();

        if (optional.isPresent()) {
            Building tower = optional.get();

            double distance = tower.getAttackRange() + self.getRadius() + PADDING - self.getDistanceTo(tower);

            double tickRequired = distance / game.getWizardBackwardSpeed();

            if (tower.getRemainingActionCooldownTicks() >= tickRequired && tower.getRemainingActionCooldownTicks() < tickRequired * 2) {
                LOG.printf("Retreat!!! Tower ticks remain: %d%n", tower.getRemainingActionCooldownTicks(), enemies.size(), friends);

                return retreat(self, enemy, game, map, priority);
            }
        }

        return Stream.empty();
    }
}
