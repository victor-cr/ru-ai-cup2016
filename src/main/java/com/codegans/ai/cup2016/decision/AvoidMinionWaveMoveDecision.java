package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Building;
import model.BuildingType;
import model.Game;
import model.LivingUnit;
import model.Wizard;
import model.World;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 26.11.2016 14:04
 */
public class AvoidMinionWaveMoveDecision extends AbstractMoveDecision {
    private static final double THRESHOLD_DISTANCE = 800;
    private static final double THRESHOLD_TIME = 100;

    public AvoidMinionWaveMoveDecision(int priority) {
        super(priority);
    }

    @Override
    protected Stream<Action> doActions(Wizard self, World world, Game game, GameMap map) {
        int interval = game.getFactionMinionAppearanceIntervalTicks();

        int ticksToMinion = interval - (world.getTickIndex() % interval);

        if (ticksToMinion > THRESHOLD_TIME) {
            return Stream.empty();
        }

        Optional<Building> enemyBase = map.towers()
                .filter(GameMap::isEnemy)
                .filter(e -> e.getType() == BuildingType.FACTION_BASE)
                .filter(e -> Double.compare(self.getDistanceTo(e), THRESHOLD_DISTANCE) <= 0)
                .findAny();

        if (enemyBase.isPresent()) {
            LOG.printf("Retreat!!! The minions are coming in: %d tick(s)%n", ticksToMinion);

            LivingUnit enemy = map.cd().unitsAt(self.getX(), self.getY(), self.getCastRange())
                    .filter(GameMap::isEnemy).sorted(Comparator.comparingDouble(LivingUnit::getLife))
                    .findFirst().orElse(null);

            return retreat(self, enemy, game, map, priority);
        }

        return Stream.empty();
    }
}
