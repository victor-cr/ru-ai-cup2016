package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.action.MoveAction;
import com.codegans.ai.cup2016.model.Point;
import model.Game;
import model.LaneType;
import model.Move;
import model.Wizard;
import model.World;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 20:09
 */
public class MoveCommandDecision implements Decision {
    private final LaneType random = LaneType.values()[StrictMath.toIntExact(System.nanoTime() % 3)];

    @Override
    public Action decide(Wizard self, World world, Game game, Move move) {
//        LaneType target = Arrays.stream(self.getMessages()).map(Message::getLane).filter(e -> e != null).findAny().orElse(random);

        return new MoveAction(MINOR, new Point(0, 0));
    }
}
