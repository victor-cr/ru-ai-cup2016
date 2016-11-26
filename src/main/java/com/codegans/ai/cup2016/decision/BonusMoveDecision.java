package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Bonus;
import model.Game;
import model.Wizard;
import model.World;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 26.11.2016 14:04
 */
public class BonusMoveDecision extends AbstractMoveDecision {
    private static final double THRESHOLD_DISTANCE = 1000;

    public BonusMoveDecision(int priority) {
        super(priority);
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        Optional<Bonus> bonus = map.bonuses().filter(e -> Double.compare(self.getDistanceTo(e), THRESHOLD_DISTANCE) < 0).findAny();

        if (bonus.isPresent()) {
            return go(self, new Point(bonus.get()), game, map, priority);
        }

        return Stream.empty();
    }
}
