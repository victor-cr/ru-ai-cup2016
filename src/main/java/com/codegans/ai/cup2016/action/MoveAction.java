package com.codegans.ai.cup2016.action;

import com.codegans.ai.cup2016.GameMap;
import com.codegans.ai.cup2016.model.Point;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import static java.lang.StrictMath.*;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 17:15
 */
public class MoveAction implements Action {
    private final int score;
    private final Point target;

    public MoveAction(int score, Point target) {
        this.score = score;
        this.target = target;
    }

    @Override
    public int score() {
        return score;
    }

    @Override
    public void apply(Wizard self, World world, Game game, Move move) {
        GameMap map = GameMap.get(world);

        Point checkpoint = map.navigator(target).next();

        double forwardSpeed = game.getWizardForwardSpeed();
        double maxTurnAngle = game.getWizardMaxTurnAngle();
        double requiredTurnAngle = self.getAngleTo(checkpoint.x, checkpoint.y) - self.getAngle();

        if (Double.compare(maxTurnAngle, abs(requiredTurnAngle)) > 0) {
            move.setSpeed(forwardSpeed);
            move.setTurn(requiredTurnAngle);
        } else {
            double turnAngle = StrictMath.signum(requiredTurnAngle) * maxTurnAngle;
            double turnSpeed = requiredTurnAngle / PI;

            move.setSpeed((signum(turnSpeed) >= 0 ? forwardSpeed : -game.getWizardBackwardSpeed()) * turnSpeed);
            move.setTurn(turnAngle);
        }

    }
}
