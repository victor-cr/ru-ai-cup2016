package com.codegans.ai.cup2016.navigator.astar;

import model.Faction;
import model.LivingUnit;

import java.util.ArrayList;
import java.util.Collection;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 17.11.2016 8:30
 */
public class UnitNode extends AStarNode {
    private static final int HIT_WEIGHT = 12;
    private static final double LIFE_WEIGHT = HIT_WEIGHT;
    private static final double IMPOSSIBLE = 100000;

    private final boolean passThroughTrees;
    private final Collection<LivingUnit> units;

    public UnitNode(int x, int y, AStarNode previous, boolean passThroughTrees, Collection<LivingUnit> units) {
        super(x, y, previous.targetX, previous.targetY, previous);

        this.units = units;
        this.passThroughTrees = passThroughTrees;
    }

    @Override
    protected double distanceFrom(AStarNode target) {
        Collection<LivingUnit> units = new ArrayList<>(this.units);

        if (target instanceof UnitNode) {
            UnitNode node = (UnitNode) target;

            units.removeAll(node.units);
        }

        boolean treesOnly = passThroughTrees && units.stream().filter(e -> e.getFaction() != Faction.OTHER).count() == 0;
        double life = units.stream().mapToInt(LivingUnit::getLife).sum();

        return super.distanceFrom(target) + (treesOnly ? StrictMath.floor(life / HIT_WEIGHT) * LIFE_WEIGHT : IMPOSSIBLE);
    }
}
