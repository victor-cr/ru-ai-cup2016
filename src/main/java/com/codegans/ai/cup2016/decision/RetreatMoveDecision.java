package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.navigator.CollisionDetector;
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
    private static final int THRESHOLD = 15;

    private CollisionDetector cd;

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map, Navigator navigator) {
        if (self.getLife() > THRESHOLD) {
            return Stream.empty();
        }

        if (cd == null) {
            cd = map.collisionDetector().full();
        }

        double x = self.getX();
        double y = self.getY();
        double r = self.getVisionRange() * 2;

        if (cd.unitsAt(x, y, r).anyMatch(map::isEnemy)) {
            LOG.printf("Retreat!!! The life is in danger: %d%n", self.getLife());

            return retreat(self, null, game, map, ASAP);
        }

        return Stream.empty();
    }
}
