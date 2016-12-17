package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Checkpoint;
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
    private final List<Checkpoint> checkpoints = new ArrayList<>();
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

        Checkpoint target = setupTarget(self, map);

        return turnAndGo(self, target.center, game, map, priority);
    }

    private void reassess(GameMap map, LaneType requested) {
        current = requested;

        checkpoints.clear();
        checkpoints.addAll(map.checkpoints(requested));

        map.lane(current);

        LOG.printf("New requested lane: %s%n", current);
    }

    private Checkpoint setupTarget(Wizard self, GameMap map) {
        Checkpoint checkpoint = checkpoints.get(0);

        if (checkpoints.size() > 1 && checkpoint.isTaken(self)) {
            checkpoints.remove(0);

            checkpoint = checkpoints.get(0);
        }

        LOG.logTarget(checkpoint.center, map.tick());

        return checkpoint;
    }
}
