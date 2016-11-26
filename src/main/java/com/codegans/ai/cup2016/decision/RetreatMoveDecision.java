package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Game;
import model.LivingUnit;
import model.Wizard;
import model.World;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class RetreatMoveDecision extends AbstractMoveDecision {
    private static final int THRESHOLD_ABSOLUTE = 36;
    private static final int THRESHOLD_PERCENT = 20;
    private static final int PADDING = 250;

    public RetreatMoveDecision(int priority) {
        super(priority);
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        int life = self.getLife();

        if (life > THRESHOLD_ABSOLUTE && life * 100 / THRESHOLD_PERCENT > self.getMaxLife()) {
            return Stream.empty();
        }

        double x = self.getX();
        double y = self.getY();
        double r = self.getVisionRange() + PADDING;

        Optional<LivingUnit> target = map.cd().unitsAt(x, y, r)
                .filter(GameMap::isEnemy)
                .filter(e -> isDanger(game, self, e, PADDING, 100000))
                .min(Comparator.comparingDouble(self::getDistanceTo));

        if (target.isPresent()) {
            LOG.printf("Retreat!!! The life is in immanent danger: %d%n", life);

            return retreat(self, target.get(), game, map, priority);
        }

        target = map.cd().unitsAt(x, y, r)
                .filter(GameMap::isEnemy)
                .min(Comparator.comparingDouble(self::getDistanceTo));

        if (target.isPresent()) {
            LOG.printf("Retreat!!! The life is in potential danger: %d%n", life);

            return retreat(self, target.get(), game, map, priority);
        }

        return Stream.empty();
    }
}
