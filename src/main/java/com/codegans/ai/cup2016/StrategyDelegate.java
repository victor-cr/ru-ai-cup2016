package com.codegans.ai.cup2016;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.decision.Decision;
import com.codegans.ai.cup2016.decision.MoveCommandDecision;
import com.codegans.ai.cup2016.decision.MoveDecision;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger();

    private final Collection<Decision> decisions = Arrays.asList(
            new MoveDecision(),
            new MoveCommandDecision()
    );

    public void move(Wizard self, World world, Game game, Move move) {
        decisions.stream()
                .map(e -> e.decide(self, world, game, move))
                .sorted()
                .collect(Collectors.toMap(Action::getClass, e -> e, (l, r) -> l, HashMap::new))
                .values().stream()
                .sorted()
                .peek(log::action)
                .forEach(e -> e.apply(self, world, game, move));
    }
}
