package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.action.CastAction;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.ActionType;
import model.Game;
import model.LivingUnit;
import model.Wizard;
import model.World;

import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.StrictMath.abs;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class StaffAttackDecision extends AbstractActionDecision {
    private final Predicate<LivingUnit> predicate;

    public StaffAttackDecision(int priority, Predicate<LivingUnit> predicate) {
        super(priority, game -> 0, ActionType.STAFF);

        this.predicate = predicate;
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        double x = self.getX();
        double y = self.getY();
        double r = game.getStaffRange();

        double sector = game.getStaffSector() / 2;

        return map.cd().unitsAt(x, y, r)
                .filter(predicate)
                .filter(e -> Double.compare(abs(self.getAngleTo(e)), sector) <= 0)
                .limit(1)
                .map(e -> CastAction.staff(priority));
    }
}
