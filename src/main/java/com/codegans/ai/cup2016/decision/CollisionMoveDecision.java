package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Game;
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
public class CollisionMoveDecision extends AbstractMoveDecision {
    private static final double PADDING = 2.0D;

    public CollisionMoveDecision(int priority) {
        super(priority);
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        Point me = new Point(self);

        boolean stuck = map.lastActions().stream().allMatch(e -> me.equals(e.target()) && (e.speed() != 0 || e.strafe() != 0));

        if (stuck) {
            LOG.printf("Stuck%n");

            Optional<Point> target = map.cd().unitsAt(self.getX(), self.getY(), self.getRadius() + PADDING).map(Point::new).map(e -> me.shiftTo(e, self.getDistanceTo(e.x, e.y))).reduce(Point::merge);

            if (target.isPresent()) {
                return go(self, target.get(), game, map, priority);
            }
        }

        return Stream.empty();
    }
}
