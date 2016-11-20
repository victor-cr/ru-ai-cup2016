package com.codegans.ai.cup2016.navigator.astar;

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
    private static final int HIT_WEIGHT = 10;
    private static final double LIFE_WEIGHT = G_WEIGHT + HIT_WEIGHT;

    private final Collection<LivingUnit> units;

    public UnitNode(int x, int y, AStarNode previous, Collection<LivingUnit> units) {
        super(x, y, previous.targetX, previous.targetY, previous);

        this.units = units;
    }

    @Override
    protected double distanceFrom(AStarNode target) {
        Collection<LivingUnit> units = new ArrayList<>(this.units);

        if (target instanceof UnitNode) {
            UnitNode node = (UnitNode) target;

            units.removeAll(node.units);
        }

        double life = units.stream().mapToInt(LivingUnit::getLife).average().orElse(0);


        return super.distanceFrom(target) + StrictMath.floor(life / HIT_WEIGHT) * LIFE_WEIGHT;
    }
}
