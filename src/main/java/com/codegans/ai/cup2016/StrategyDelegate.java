package com.codegans.ai.cup2016;

import com.codegans.ai.cup2016.decision.Decision;
import com.codegans.ai.cup2016.decision.MoveDecision;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import java.util.Arrays;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 19:04
 */
public class StrategyDelegate {
    private final Logger log = LoggerFactory.getLogger();
    private final Collection<Decision> decisions = Arrays.asList(
            new MoveDecision()
    );

    public void move(Wizard self, World world, Game game, Move move) {

    }
}
