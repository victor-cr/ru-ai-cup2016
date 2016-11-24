package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.action.CastAction;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.ActionType;
import model.Game;
import model.Wizard;
import model.World;

import java.util.stream.Stream;

import static java.lang.StrictMath.abs;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class CleanPathMissileAttackDecision extends AbstractActionDecision {
    public CleanPathMissileAttackDecision() {
        super(game -> 0, ActionType.STAFF);
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        double x = self.getX();
        double y = self.getY();
        double r = self.getCastRange();

        return map.cd().unitsAt(x, y, r)
                .filter(map::isNeutral)
                .filter(e -> Double.compare(abs(self.getAngleTo(e)), game.getStaffSector() / 2) <= 0)
                .sorted((a, b) -> Integer.compare(a.getLife(), b.getLife()))
                .limit(1)
                .map(e -> CastAction.missile(LOW, self.getAngleTo(e), self.getDistanceTo(e), self.getDistanceTo(e) + e.getRadius()));
    }
}