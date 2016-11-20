package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.action.SpeedAction;
import com.codegans.ai.cup2016.action.StrafeAction;
import com.codegans.ai.cup2016.action.TurnAction;
import com.codegans.ai.cup2016.model.Point;
import model.Game;
import model.LivingUnit;
import model.Wizard;

import java.util.stream.Stream;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.signum;
import static java.lang.StrictMath.sin;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 20:09
 */
public abstract class AbstractMoveDecision implements Decision {
    protected Stream<Action> turnAndGo(Wizard self, Point checkpoint, Game game, int score) {
        double forwardSpeed = game.getWizardForwardSpeed();
        double maxTurnAngle = game.getWizardMaxTurnAngle();
        double requiredTurnAngle = self.getAngleTo(checkpoint.x, checkpoint.y);

        if (Double.compare(maxTurnAngle, abs(requiredTurnAngle)) > 0) {
            double angle = requiredTurnAngle;
            double speed = forwardSpeed * (1 - abs(requiredTurnAngle / PI));

            return Stream.of(new TurnAction(score, angle), new SpeedAction(score, speed));
        }

        double angle = StrictMath.signum(requiredTurnAngle) * maxTurnAngle;
        double speed = (Double.compare(abs(requiredTurnAngle), PI / 2) < 0 ? forwardSpeed : -game.getWizardBackwardSpeed()) * requiredTurnAngle / PI;

        return Stream.of(new TurnAction(score, angle), new SpeedAction(score, speed));
    }

    protected Stream<Action> go(Wizard self, Point checkpoint, Game game, int score) {
        double angle = self.getAngleTo(checkpoint.x, checkpoint.y);

        if (Double.compare(PI / 2, abs(angle)) >= 0) {
            return Stream.of(new TurnAction(score, angle), new SpeedAction(score, game.getWizardForwardSpeed()));
        }

        return Stream.of(new TurnAction(score, (angle - signum(angle) * PI)), new SpeedAction(score, -game.getWizardBackwardSpeed()));
    }

    protected Stream<Action> goWaitching(Wizard self, Point checkpoint, LivingUnit unit, Game game, int score) {
        double unitAngle = self.getAngleTo(unit);
        double checkpointAngle = self.getAngleTo(checkpoint.x, checkpoint.y);
        double checkpointDistance = self.getDistanceTo(checkpoint.x, checkpoint.y);

        double angle = checkpointAngle - unitAngle;

        double dx = cos(angle) * checkpointDistance;
        double dy = sin(angle) * checkpointDistance;

        return Stream.of(
                new TurnAction(score, unitAngle),
                new SpeedAction(score, dx),
                new StrafeAction(score, dy)
        );
    }
}