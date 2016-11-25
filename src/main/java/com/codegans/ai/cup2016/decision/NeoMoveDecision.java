package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Game;
import model.Projectile;
import model.Unit;
import model.Wizard;
import model.World;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 25/11/2016 18:57
 */
public class NeoMoveDecision extends AbstractMoveDecision {
    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        Map<Long, Wizard> wizards = map.wizards().collect(Collectors.toMap(Unit::getId, e -> e));

        Optional<Projectile> avoidIt = map.projectiles()
                .filter(e -> isDanger(self, e))
                .filter(e -> canAvoid(self, e))
                .sorted((l,r) -> Integer.compare(wizards.get(l.getOwnerUnitId()).getLevel(), wizards.get(r.getOwnerUnitId()).getLevel()))
                .findFirst();

        if (avoidIt.isPresent()) {
            Projectile projectile = avoidIt.get();

            LOG.printf("We could have avoided that: %s%n", new Point(projectile));

            return Stream.empty();
        }

        return Stream.empty();
    }

    private static boolean isDanger(Wizard self, Projectile projectile) {
//        double angle = projectile.getAngleTo(self);
//        double distance = projectile.getDistanceTo(self);
//
//
        return false;
    }

    private static boolean canAvoid(Wizard self, Projectile projectile) {
        return false;
    }
}
