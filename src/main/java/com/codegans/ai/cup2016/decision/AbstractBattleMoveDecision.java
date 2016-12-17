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

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public abstract class AbstractBattleMoveDecision extends AbstractMoveDecision {
    protected static final int SAFE_COOL_DOWN = 10;
    private static final int DAMAGE_MULTIPLICAND = 5;

    public AbstractBattleMoveDecision(int priority) {
        super(priority);
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        double x = self.getX();
        double y = self.getY();
        double r = self.getVisionRange() * 2;

        List<LivingUnit> units = map.cd().unitsAt(x, y, r).filter(e -> !GameMap.isNeutral(e)).collect(Collectors.toList());

        Point me = new Point(self);

        Optional<LivingUnit> enemy = units.stream()
                .filter(GameMap::isEnemy)
                .filter(e -> map.cd().canShoot(me, new Point(e), game.getMagicMissileRadius()))
                .sorted(Comparator.comparingInt(e -> estimate(e, self, game)))
                .findFirst();

        if (enemy.isPresent()) {
            return moveToKill(enemy.get(), self, map, game, units);
        }

        return Stream.empty();
    }

    protected abstract Stream<Action> moveToKill(LivingUnit enemy, Wizard self, GameMap map, Game game, Collection<LivingUnit> units);

    private static int estimate(LivingUnit enemy, Wizard self, Game game) {
        int score = enemy.getLife();

        if (isDanger(game, self, enemy, 0, SAFE_COOL_DOWN)) {
            if (enemy instanceof Minion) {
                score -= ((Minion) enemy).getDamage() * DAMAGE_MULTIPLICAND;
            } else if (enemy instanceof Wizard) {
                score -= ((Wizard) enemy).getLevel() + game.getMagicMissileDirectDamage() * DAMAGE_MULTIPLICAND;
            } else if (enemy instanceof Building) {
                score -= ((Building) enemy).getDamage() * DAMAGE_MULTIPLICAND;
            }
        }

        return score;
    }
}
