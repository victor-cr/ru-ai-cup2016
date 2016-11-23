package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.CollisionDetector;
import com.codegans.ai.cup2016.navigator.GameMap;
import com.codegans.ai.cup2016.navigator.Navigator;
import model.Game;
import model.Wizard;
import model.World;

import java.util.stream.Stream;

import static java.lang.StrictMath.*;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class CollisionMoveDecision extends AbstractMoveDecision {
    private static final double EXPECTED_PADDING = 10.0D;
    private static final double COLLISION_PADDING = 1.0D;

    private CollisionDetector cd;

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map, Navigator navigator) {
        if (cd == null) {
            cd = map.collisionDetector().full();
        }

        double x = self.getX();
        double y = self.getY();
        double r = self.getRadius();

        double angle = cd.unitsAt(x, y, r + COLLISION_PADDING)
                .mapToDouble(self::getAngleTo)
                .average().orElse(0.0D);

        if (Double.compare(angle, 0) == 0) {
            return Stream.empty();
        }

        Point target = map.target();

        if (target != null && cd.canPass(new Point(x, y), target, r)) {
            return Stream.empty();
        }

        angle = angle - signum(angle) * PI;

        double dx = self.getRadius() * cos(angle);
        double dy = self.getRadius() * sin(angle);

        Point retreat = normalize(map, new Point(self).plusX(dx).plusY(dy));

        LOG.logTarget(retreat, map.tick());

        return go(self, retreat, game, MEDIUM);
    }

    private Point normalize(GameMap map, Point point) {
        if (Double.compare(point.x, 0) < 0 || Double.compare(point.y, 0) < 0 || Double.compare(point.x, cd.width()) >= 0 || Double.compare(point.y, cd.height()) >= 0) {
            Point me = new Point(map.self());
            int i = 0;

            while (i < safePoints.size() && point.equals(me)) {
                point = safePoints.tail(i);
                i++;
            }

            if (i == safePoints.size()) {
                point = map.home();
            }
        }

        return navigator.next(point);
    }
}
