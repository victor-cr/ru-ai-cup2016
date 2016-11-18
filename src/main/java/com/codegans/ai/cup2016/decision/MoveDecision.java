package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.action.MoveAction;
import com.codegans.ai.cup2016.model.Point;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class MoveDecision implements Decision {
    @Override
    public Action decide(Wizard self, World world, Game game, Move move) {
        return new MoveAction(MEDIUM, new Point(2000, 2000));
    }
}
