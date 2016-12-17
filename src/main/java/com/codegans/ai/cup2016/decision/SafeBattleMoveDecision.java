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
public class SafeBattleMoveDecision extends AbstractBattleMoveDecision {
    private static final int SAFE_COOL_DOWN = 10;

    public SafeBattleMoveDecision(int priority) {
        super(priority);
    }

    @Override
    protected Stream<Action> moveToKill(LivingUnit enemy, Wizard self, GameMap map, Game game, Collection<LivingUnit> units) {
        Collection<LivingUnit> enemies = units.stream().filter(GameMap::isEnemy)
                .filter(e -> isDanger(game, self, e, game.getWizardForwardSpeed(), SAFE_COOL_DOWN))
                .collect(Collectors.toList());

        double distance = self.getDistanceTo(enemy);

        if (Double.compare(distance, self.getCastRange()) < 0 && enemies.isEmpty()) {
            LOG.printf("Stay in safe zone. Kill'em all: (%.3f,%.3f)%n", enemy.getX(), enemy.getY());

            return goWatching(self, new Point(self), enemy, game, map, priority);
        }

        return Stream.empty();
    }
}
