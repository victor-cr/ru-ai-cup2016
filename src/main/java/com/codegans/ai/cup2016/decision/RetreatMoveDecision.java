package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Game;
import model.LivingUnit;
import model.Wizard;
import model.World;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class RetreatMoveDecision extends AbstractMoveDecision {
    private static final int THRESHOLD_ABSOLUTE = 25;
    private static final int THRESHOLD_PERCENT = 10;

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        int life = self.getLife();

        if (life > THRESHOLD_ABSOLUTE && life * 100 / THRESHOLD_PERCENT > self.getMaxLife()) {
            return Stream.empty();
        }

        double x = self.getX();
        double y = self.getY();
        double r = self.getVisionRange() * 2;

        Optional<LivingUnit> target = map.cd().unitsAt(x, y, r)
                .filter(map::isEnemy)
//                .filter(e -> isDanger(game, self, e, 100000))
                .min((a, b) -> Double.compare(self.getDistanceTo(a), self.getDistanceTo(b)));

        if (target.isPresent()) {
            LOG.printf("Retreat!!! The life is in danger: %d%n", life);

            return retreat(self, target.get(), game, map, ASAP);
        }

        return Stream.empty();
    }
}
