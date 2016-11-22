package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.navigator.GameMap;
import com.codegans.ai.cup2016.navigator.Navigator;
import model.Game;
import model.Wizard;
import model.World;

import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class RetreatMoveDecision extends AbstractMoveDecision {
    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map, Navigator navigator) {
        if (self.getLife() > game.getStaffDamage()) {
            return Stream.empty();
        }

        double x = self.getX();
        double y = self.getY();
        double r = self.getVisionRange() * 2;

        if (navigator.cd().unitsAt(x, y, r).anyMatch(map::isEnemy)) {
            LOG.printf("Retreat!!! The life is in danger: %d%n", self.getLife());

            return retreat(self, game, map, ASAP);
        }

        return Stream.empty();
    }
}
