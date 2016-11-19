package com.codegans.ai.cup2016.action;

import com.codegans.ai.cup2016.GameMap;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.model.Point;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 17:15
 */
public class MoveAction implements Action {
    private static final Logger LOG = LoggerFactory.getLogger();

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
        double requiredTurnAngle = self.getAngleTo(checkpoint.x, checkpoint.y);

        if (Double.compare(maxTurnAngle, abs(requiredTurnAngle)) > 0) {
            double angle = requiredTurnAngle;
            double speed = forwardSpeed * (1 - abs(requiredTurnAngle / PI));

            move.setTurn(angle);
            move.setSpeed(speed);

            LOG.printf("Forward: %f <%f> (%f;%f) -> (%f;%f)%n", speed, angle, self.getX(), self.getY(), checkpoint.x, checkpoint.y);
        } else {
            double angle = StrictMath.signum(requiredTurnAngle) * maxTurnAngle;
            double speed = (Double.compare(abs(requiredTurnAngle), PI / 2) < 0 ? forwardSpeed : -game.getWizardBackwardSpeed()) * requiredTurnAngle / PI;

            move.setTurn(angle);
            move.setSpeed(speed);

            LOG.printf("Turn: %f <%f> (%f;%f) -> (%f;%f)%n", speed, angle, self.getX(), self.getY(), checkpoint.x, checkpoint.y);
        }
    }

    @Override
    public String toString() {
        return "MOVE[" + score + "]";
    }
}
