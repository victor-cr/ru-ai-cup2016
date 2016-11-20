package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import com.codegans.ai.cup2016.navigator.Navigator;
import model.Game;
import model.LaneType;
import model.Message;
import model.Move;
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
    private static final Logger LOG = LoggerFactory.getLogger();

    //    private final LaneType random = LaneType.BOTTOM;
    private final LaneType random = LaneType.values()[StrictMath.toIntExact(System.currentTimeMillis() % 3)];
    private final List<Point> checkpoints = new ArrayList<>();
    private Navigator global;
    private Navigator local;
    private LaneType current = null;
    private Point target;

    @Override
    public Stream<Action> decide(Wizard self, World world, Game game, Move move) {
        GameMap map = GameMap.get(world);
        LaneType requested = Arrays.stream(self.getMessages()).map(Message::getLane).filter(e -> e != null).findAny().orElse(random);

        if (map.isResurrected() || current != requested) {
            reassess(map, requested);
        }

        target = setupTarget(self);

        Point nearest = local.next(target);

        return turnAndGo(self, nearest, game, LOW);
    }

    private void reassess(GameMap map, LaneType requested) {
        current = requested;

        checkpoints.clear();
        target = null;

        map.checkpoints().stream().filter(e -> e.lane == requested).map(e -> e.checkpoint).forEach(checkpoints::add);

        LOG.printf("New requested lane: %s%n", current);

        if (global == null || local == null) {
            global = map.navigator().staticOnly();
            local = map.navigator().full();
        }
    }

    private Point setupTarget(Wizard self) {
        Point checkpoint = checkpoints.get(0);

        if (checkpoints.size() > 1 && isCheckpointTaken(self, checkpoint)) {
            checkpoints.remove(0);

            checkpoint = checkpoints.get(0);
        }

        return global.next(checkpoint);
    }

    private boolean isCheckpointTaken(Wizard self, Point checkpoint) {
        return global.cd().isNear(checkpoint.x, checkpoint.y, CHECKPOINT_PADDING, self);
    }
}
