package com.codegans.ai.cup2016;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.decision.BattleMoveDecision;
import com.codegans.ai.cup2016.decision.MissileAttackDecision;
import com.codegans.ai.cup2016.decision.StaffAttackDecision;
import com.codegans.ai.cup2016.decision.Decision;
import com.codegans.ai.cup2016.decision.CheckpointMoveDecision;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 19:04
 */
public class StrategyDelegate {
    private static final Logger LOG = LoggerFactory.getLogger();

    private final Collection<Decision> decisions = Arrays.asList(
//            new MoveToSafetyDecision(),
            new BattleMoveDecision(),
            new CheckpointMoveDecision(),
            new StaffAttackDecision(),
            new MissileAttackDecision()
    );

    public void move(Wizard self, World world, Game game, Move move) {
        GameMap.get(world); // update counter

        LOG.logState(self, world, game, move);

//        try {
//            Thread.currentThread().sleep(100);
//        } catch (InterruptedException e) {
//        }

        Collection<Action> actions = decisions.stream()
                .flatMap(e -> e.decide(self, world, game, move))
                .sorted()
                .collect(Collectors.toMap(Action::getClass, e -> e, (l, r) -> l, HashMap::new))
                .values();

        LOG.printf(">====START=ACTIONS====%n");

        actions.stream()
                .sorted()
                .peek(LOG::action)
                .forEach(e -> e.apply(move));

        LOG.printf(">====FINISH=ACTIONS====%n");
    }
}
