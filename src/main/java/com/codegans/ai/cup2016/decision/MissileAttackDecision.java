package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.action.CastAction;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.ActionType;
import model.Game;
import model.LivingUnit;
import model.Wizard;
import model.World;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.StrictMath.abs;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class MissileAttackDecision extends AbstractActionDecision {
    private final boolean attackNeutrals;
    private final Predicate<LivingUnit> predicate;

    public MissileAttackDecision(int priority, boolean attackNeutrals) {
        super(priority, Game::getMagicMissileManacost, ActionType.MAGIC_MISSILE);

        this.attackNeutrals = attackNeutrals;
        this.predicate = attackNeutrals ? GameMap::isNeutral : GameMap::isEnemy;
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        double x = self.getX();
        double y = self.getY();
        double r = self.getCastRange();

//        if (attackNeutrals && map.cd().unitsAt(x, y, r).anyMatch(GameMap::isEnemy)) {
//            return Stream.empty();
//        }

        Optional<LivingUnit> closest = map.cd().unitsAt(x, y, r / 3)
                .filter(predicate)
                .filter(e -> Double.compare(abs(self.getAngleTo(e)), game.getStaffSector() / 2) <= 0)
                .sorted(Comparator.comparingDouble(self::getDistanceTo))
                .findFirst();

        if (closest.isPresent()) {
            LivingUnit enemy = closest.get();

            return Stream.of(CastAction.missile(priority, self.getAngleTo(enemy), self.getDistanceTo(enemy), self.getDistanceTo(enemy) + enemy.getRadius()));
        }

        return map.cd().unitsAt(x, y, r)
                .filter(predicate)
                .filter(e -> Double.compare(abs(self.getAngleTo(e)), game.getStaffSector() / 2) <= 0)
                .sorted(Comparator.comparingInt(LivingUnit::getLife))
                .limit(1)
                .map(e -> CastAction.missile(priority, self.getAngleTo(e), self.getDistanceTo(e), self.getDistanceTo(e) + e.getRadius()));
    }
}
