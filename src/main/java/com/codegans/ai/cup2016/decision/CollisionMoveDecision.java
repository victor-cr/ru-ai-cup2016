package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
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
public class CollisionMoveDecision extends AbstractMoveDecision {
    private static final double PADDING = 2.0D;

    public CollisionMoveDecision(int priority) {
        super(priority);
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        Point me = new Point(self);

        boolean stuck = map.lastActions().stream().allMatch(e -> me.equals(e.target()) && (Double.compare(e.speed(), 0) != 0 || Double.compare(e.strafe(), 0) != 0));

        if (stuck) {
            Optional<LivingUnit> target = map.cd().unitsAt(self.getX(), self.getY(), self.getRadius() + PADDING).filter(GameMap::isNeutral).sorted(Comparator.comparingDouble(LivingUnit::getLife)).findFirst();

            if (target.isPresent()) {
                LOG.printf("Try to destroy the barrier: %s%n", target.get());

                return goWatching(self, me, target.get(), game, map, priority);
            }


            Optional<Point> moveTo = map.cd().unitsAt(self.getX(), self.getY(), self.getRadius() + PADDING).map(Point::new).map(e -> me.shiftTo(e, self.getDistanceTo(e.x, e.y))).reduce(Point::merge);

            if (moveTo.isPresent()) {
                LOG.printf("Try to move out: %s%n", moveTo.get());

                return go(self, moveTo.get(), game, map, priority);
            }
        }

        return Stream.empty();
    }
}
