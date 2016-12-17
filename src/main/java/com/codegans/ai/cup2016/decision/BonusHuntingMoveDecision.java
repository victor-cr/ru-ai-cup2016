package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Game;
import model.Wizard;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 26.11.2016 14:04
 */
public class BonusHuntingMoveDecision extends AbstractMoveDecision {
    private static final double THRESHOLD_DISTANCE = 1600;
    private static final double THRESHOLD_TIME = 200;

    private final Collection<Point> locations = Arrays.asList(new Point(1200, 1200), new Point(2800, 2800));
    private int checkedAt = 0;

    public BonusHuntingMoveDecision(int priority) {
        super(priority);
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        int tickIndex = world.getTickIndex();
        int interval = game.getBonusAppearanceIntervalTicks();

        int ticksToBonus = interval - (tickIndex % interval);

        if (ticksToBonus > THRESHOLD_TIME && tickIndex - checkedAt < interval) {
            return Stream.empty();
        }

        Optional<Point> target = locations.stream()
                .filter(e -> Double.compare(self.getDistanceTo(e.x, e.y), THRESHOLD_DISTANCE) <= 0)
                .sorted(Comparator.comparingDouble(e -> self.getDistanceTo(e.x, e.y)))
                .findFirst();

        if (target.isPresent()) {
            double distance = game.getBonusRadius() + 2;

            LOG.printf("Bonus hunt!!! The bonus is coming in: %d tick(s)%n", ticksToBonus);

            Point bonus = target.get();

            if (map.bonuses().anyMatch(e -> e.getX() == bonus.x && e.getY() == bonus.y) && self.getDistanceTo(bonus.x, bonus.y) < self.getVisionRange()) {
                return Stream.empty();
            }

            if (ticksToBonus > THRESHOLD_TIME && ticksToBonus < interval && map.isVisible(bonus, game.getBonusRadius()) && map.bonuses().noneMatch(e -> e.getX() == bonus.x && e.getY() == bonus.y)) {
                checkedAt = tickIndex;

                LOG.printf("Bonus has been taken: %s%n", bonus);

                return Stream.empty();
            }

            return turnAndGo(self, bonus.shiftTo(new Point(self), distance), game, map, priority);
        }

        return Stream.empty();
    }
}
