package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import com.codegans.ai.cup2016.navigator.Navigator;
import model.Game;
import model.LivingUnit;
import model.Move;
import model.Wizard;
import model.World;

import java.util.stream.Stream;

import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.signum;
import static java.lang.StrictMath.sin;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class BattleMoveDecision extends AbstractMoveDecision {
    private Navigator navigator;

    @Override
    public Stream<Action> decide(Wizard self, World world, Game game, Move move) {
        GameMap map = GameMap.get(world);

        if (navigator == null) {
            navigator = map.navigator().full();
        }

        double x = self.getX();
        double y = self.getY();
        double r = self.getCastRange();

        LivingUnit enemy = navigator.cd().unitsAt(x, y, r)
                .filter(map::isEnemy)
                .sorted((a, b) -> Integer.compare(a.getLife(), b.getLife()))
                .limit(1)
                .findAny()
                .orElse(null);

        if (enemy == null) {
            return Stream.empty();
        }

        double enemyAngle = enemy.getAngleTo(self);
        double dangerAngle = game.getStaffSector() / 2 + game.getWizardMaxTurnAngle();

        if (Double.compare(abs(enemyAngle), dangerAngle) >= 0) {
            return goWaitching(self, new Point(self), enemy, game, HIGH);
        }

        double distance = self.getDistanceTo(enemy);

        double safeX = self.getX() + distance * cos(dangerAngle * signum(enemyAngle));
        double safeY = self.getY() + distance * sin(dangerAngle * signum(enemyAngle));

        return goWaitching(self, new Point(safeX, safeY), enemy, game, HIGH);
    }
}
