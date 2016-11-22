package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.CollisionDetector;
import com.codegans.ai.cup2016.navigator.GameMap;
import com.codegans.ai.cup2016.navigator.Navigator;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import java.util.Random;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class CollisionMoveDecision extends AbstractMoveDecision {
    private static final double EXPECTED_PADDING = 10.0D;
    private static final double COLLISION_PADDING = 1.0D;

    private final Random random = new Random();

    private CollisionDetector cd;
    private Navigator navigator;

    @Override
    public Stream<Action> decide(Wizard self, World world, Game game, Move move) {
        GameMap map = GameMap.get(world);

        if (cd == null) {
            cd = map.collisionDetector().full();
            navigator = map.navigator().staticOnly();
        }

        double x = self.getX();
        double y = self.getY();
        double r = self.getRadius();

        Point me = new Point(self);

        Point retreat = cd.unitsAt(x, y, r + COLLISION_PADDING)
                .map(Point::new)
                .reduce(Point::merge)
                .map(e -> e.reflectTo(me))
                .orElse(me);

        if (retreat.equals(me)) {
            return Stream.empty();
        }

        retreat = normalize(map, retreat);

        LOG.logTarget(retreat, map.tick());

        return go(self, retreat, game, ASAP);
    }

    private Point normalize(GameMap map, Point point) {
        if (Double.compare(point.x, 0) >= 0 && Double.compare(point.y, 0) >= 0 && Double.compare(point.x, cd.width()) < 0 && Double.compare(point.y, cd.height()) < 0) {
            return point.plusX(random.nextDouble() * EXPECTED_PADDING - EXPECTED_PADDING / 2).plusY(random.nextDouble() * EXPECTED_PADDING - EXPECTED_PADDING / 2);
        }

        return navigator.next(map.home());
    }
}
