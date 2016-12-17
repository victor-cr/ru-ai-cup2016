package com.codegans.ai.cup2016;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.action.MoveAction;
import com.codegans.ai.cup2016.decision.AvoidMinionWaveMoveDecision;
import com.codegans.ai.cup2016.decision.BonusHuntingMoveDecision;
import com.codegans.ai.cup2016.decision.BonusMoveDecision;
import com.codegans.ai.cup2016.decision.CheckpointMoveDecision;
import com.codegans.ai.cup2016.decision.CollisionMoveDecision;
import com.codegans.ai.cup2016.decision.DangerousBattleMoveDecision;
import com.codegans.ai.cup2016.decision.Decision;
import com.codegans.ai.cup2016.decision.FairBattleMoveDecision;
import com.codegans.ai.cup2016.decision.LevelUpDecision;
import com.codegans.ai.cup2016.decision.MissileAttackDecision;
import com.codegans.ai.cup2016.decision.NeoMoveDecision;
import com.codegans.ai.cup2016.decision.RetreatMoveDecision;
import com.codegans.ai.cup2016.decision.SafeBattleMoveDecision;
import com.codegans.ai.cup2016.decision.StaffAttackDecision;
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
import java.util.Map;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 19:04
 */
public class StrategyDelegate {
    private static final Logger LOG = LoggerFactory.getLogger();

    private static final int MOVE_TO_UNBLOCK = 1;
    private static final int MOVE_TO_SAFETY = 10;
    private static final int MOVE_TO_AVOID = 10;
    private static final int MOVE_TO_BONUS = 100;
    private static final int MOVE_TO_FIGHT = 1000;
    private static final int MOVE_TO_CHECKPOINT = 10000;
    private static final int ATTACK_MELEE = 10;
    private static final int ATTACK_MISSILE = 100;
    private static final int ATTACK_FROSTBOLT = 1000;
    private static final int ATTACK_FIREBALL = 1000;
    private static final int CLEAN_MELEE = 1000;
    private static final int CLEAN_MISSILE = 10000;
    private static final int LEVEL_UP = 1;

    private final Collection<Decision> decisions = Arrays.asList(
            new RetreatMoveDecision(MOVE_TO_SAFETY),
            new CollisionMoveDecision(MOVE_TO_UNBLOCK),
            new FairBattleMoveDecision(MOVE_TO_FIGHT),
            new DangerousBattleMoveDecision(MOVE_TO_FIGHT),
            new SafeBattleMoveDecision(MOVE_TO_FIGHT),
            new CheckpointMoveDecision(MOVE_TO_CHECKPOINT),
            new LevelUpDecision(LEVEL_UP),
            new StaffAttackDecision(ATTACK_MELEE, false),
            new StaffAttackDecision(CLEAN_MELEE, true),
            new MissileAttackDecision(ATTACK_MISSILE, false),
            new MissileAttackDecision(CLEAN_MISSILE, true),
            new AvoidMinionWaveMoveDecision(MOVE_TO_AVOID),
            new NeoMoveDecision(MOVE_TO_AVOID),
            new BonusHuntingMoveDecision(MOVE_TO_BONUS),
            new BonusMoveDecision(MOVE_TO_BONUS)
    );

    public void move(Wizard self, World world, Game game, Move move) {
        long time = System.currentTimeMillis();

        GameMap map = GameMap.get(world, game); // update counter and constants

        LOG.logState(self, world, game, move);

//        try {
//            Thread.currentThread().sleep(50);
//        } catch (InterruptedException e) {
//        }

        Map<Class, Action> actions = new HashMap<>();

        decisions.stream()
                .flatMap(e -> e.decide(self, world, game, move))
                .sorted()
                .forEach(e -> actions.putIfAbsent(e.getClass(), e));

        LOG.printf(">====START=ACTIONS====%n");

        for (Action action : actions.values()) {
            action.apply(move);

            if (action instanceof MoveAction) {
                MoveAction moveAction = (MoveAction) action;

                map.target(moveAction.target());
                map.action(moveAction);
            }

            LOG.action(action);
        }

        LOG.printf(">====FINISH=ACTIONS====[%4d ms]%n", System.currentTimeMillis() - time);
    }
}
