package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.navigator.GameMap;
import com.codegans.ai.cup2016.navigator.Navigator;
import model.Game;
import model.Move;
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
    private Navigator navigator;

    @Override
    public Stream<Action> decide(Wizard self, World world, Game game, Move move) {
        if (self.getLife() > game.getStaffDamage()) {
            return Stream.empty();
        }

        GameMap map = GameMap.get(world);

        if (navigator == null) {
            navigator = map.navigator().staticOnly();
        }

        double x = self.getX();
        double y = self.getY();
        double r = self.getVisionRange();

        if (navigator.cd().unitsAt(x, y, r).anyMatch(map::isEnemy)) {
            LOG.printf("Retreat!!! The life is in danger: %d%n", self.getLife());

            return retreat(self, game, map, ASAP);
        }

        return Stream.empty();
    }
}
