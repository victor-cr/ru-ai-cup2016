package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Game;
import model.LivingUnit;
import model.Wizard;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class FairBattleMoveDecision extends AbstractBattleMoveDecision {
    private static final double PADDING = 5;

    public FairBattleMoveDecision(int priority) {
        super(priority);
    }

    @Override
    protected Stream<Action> moveToKill(LivingUnit enemy, Wizard self, GameMap map, Game game, Collection<LivingUnit> units) {
        Collection<LivingUnit> enemies = units.stream().filter(GameMap::isEnemy)
                .filter(e -> isDanger(game, self, e, PADDING, SAFE_COOL_DOWN))
                .collect(Collectors.toList());

        if (enemies.size() == 1) {
            if (!enemies.contains(enemy) && enemy.getLife() > game.getMagicMissileDirectDamage()) {
                enemy = enemies.iterator().next();
            }

            double delta = self.getCastRange() - self.getDistanceTo(enemy) - PADDING;

            Point shift = new Point(self).shiftTo(new Point(enemy), delta);

            LOG.printf("Keep attack distance: %.3f %s%n", delta, shift);
            LOG.logTarget(shift, map.tick());

            return goWatching(self, shift, enemy, game, map, priority);
        }

        return Stream.empty();
    }
}
