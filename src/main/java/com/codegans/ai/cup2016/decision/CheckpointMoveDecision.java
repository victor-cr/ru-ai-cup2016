package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import com.codegans.ai.cup2016.navigator.Navigator;
import model.Game;
import model.LaneType;
import model.Message;
import model.Wizard;
import model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 20:09
 */
public class CheckpointMoveDecision extends AbstractMoveDecision {
    private static final double CHECKPOINT_PADDING = 150;

    private final LaneType random = LaneType.BOTTOM;
//    private final LaneType random = LaneType.values()[StrictMath.toIntExact(System.currentTimeMillis() % 3)];
    private final List<Point> checkpoints = new ArrayList<>();
    private LaneType current = null;
    private Point target;

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map, Navigator navigator) {
        LaneType requested = Arrays.stream(self.getMessages()).map(Message::getLane).filter(e -> e != null).findAny().orElse(random);

        if (map.isResurrected() || current != requested) {
            reassess(map, requested);
        }

        if (target == null || Double.compare(self.getDistanceTo(target.x, target.y), game.getWizardForwardSpeed() * 3) <= 0) {
            target = setupTarget(self);
        }

        LOG.logTarget(target, map.tick());

        return turnAndGo(self, target, game, LOW);
    }

    private void reassess(GameMap map, LaneType requested) {
        current = requested;

        checkpoints.clear();
        target = null;

        map.checkpoints().stream().filter(e -> e.lane == requested).map(e -> e.checkpoint).forEach(checkpoints::add);

        LOG.printf("New requested lane: %s%n", current);
    }

    private Point setupTarget(Wizard self) {
        Point checkpoint = checkpoints.get(0);

        if (checkpoints.size() > 1 && isCheckpointTaken(self, checkpoint)) {
            checkpoints.remove(0);

            checkpoint = checkpoints.get(0);
        }

        return navigator.next(checkpoint);
    }

    private boolean isCheckpointTaken(Wizard self, Point checkpoint) {
        return navigator.cd().isNear(checkpoint.x, checkpoint.y, CHECKPOINT_PADDING, self);
    }
}
