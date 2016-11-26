package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Game;
import model.LaneType;
import model.Message;
import model.Wizard;
import model.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 20:09
 */
public class CheckpointMoveDecision extends AbstractMoveDecision {
    private static final double CHECKPOINT_PADDING = 250;

    private final List<Point> checkpoints = new ArrayList<>();
    private final LaneType random;
    private LaneType current = null;

    public CheckpointMoveDecision(int priority) {
        this(priority, LaneType.values()[StrictMath.toIntExact(System.currentTimeMillis() % 3)]);
    }

    public CheckpointMoveDecision(int priority, LaneType lane) {
        super(priority);

        this.random = lane;
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        LaneType requested = Arrays.stream(self.getMessages()).map(Message::getLane).filter(Objects::nonNull).findAny().orElse(random);

        if (map.isResurrected() || current != requested) {
            reassess(map, requested);
        }

        Point target = setupTarget(self, map);

        return turnAndGo(self, target, game, map, priority);
    }

    private void reassess(GameMap map, LaneType requested) {
        current = requested;

        checkpoints.clear();

        map.checkpoints().stream().filter(e -> e.lane == requested).map(e -> e.checkpoint).forEach(checkpoints::add);

        map.lane(current);

        LOG.printf("New requested lane: %s%n", current);
    }

    private Point setupTarget(Wizard self, GameMap map) {
        Point checkpoint = checkpoints.get(0);

        if (checkpoints.size() > 1 && isCheckpointTaken(self, checkpoint, map)) {
            checkpoints.remove(0);

            checkpoint = checkpoints.get(0);
        }

        LOG.logTarget(checkpoint, map.tick());

        return checkpoint;
    }

    private boolean isCheckpointTaken(Wizard self, Point checkpoint, GameMap map) {
        return map.cd().isNear(checkpoint.x, checkpoint.y, CHECKPOINT_PADDING, self);
    }
}
