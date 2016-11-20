package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.model.WeightPoint;
import com.codegans.ai.cup2016.navigator.GameMap;
import com.codegans.ai.cup2016.navigator.Navigator;
import model.Game;
import model.MinionType;
import model.Move;
import model.Wizard;
import model.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class RetreatMoveDecision extends AbstractMoveDecision {
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

        Collection<WeightPoint> enemyWizards = navigator.cd().wizardsAt(x, y, r)
                .filter(map::isEnemy)
                .map(e -> new WeightPoint(e, game.getStaffDamage()))
                .collect(Collectors.toList());
        Collection<WeightPoint> enemyMinions = navigator.cd().minionsAt(x, y, r)
                .filter(map::isEnemy)
                .map(e -> new WeightPoint(e, e.getType() == MinionType.ORC_WOODCUTTER ? game.getOrcWoodcutterDamage() : game.getDartDirectDamage()))
                .collect(Collectors.toList());
        Collection<WeightPoint> enemyTowers = navigator.cd().towersAt(x, y, r)
                .filter(map::isEnemy)
                .map(e -> new WeightPoint(e, game.getGuardianTowerDamage()))
                .collect(Collectors.toList());

        int danger = enemyWizards.size() * game.getStaffDamage() + enemyMinions.size() * game.getOrcWoodcutterDamage() + enemyTowers.size() + game.getGuardianTowerDamage();

        if (self.getLife() * 2 >= danger) {
            return Stream.empty();
        }

        Collection<WeightPoint> result = new ArrayList<>();

        result.addAll(enemyWizards);
        result.addAll(enemyMinions);
        result.addAll(enemyTowers);

        WeightPoint center = result.stream().reduce(new WeightPoint(0, 0, 0), WeightPoint::merge);

        Point me = new Point(self);

        Point target = navigator.next(center.point.reflectTo(me));

        return go(self, target, game, HIGH);
    }
}
