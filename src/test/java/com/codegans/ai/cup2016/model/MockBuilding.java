package com.codegans.ai.cup2016.model;

import model.Building;
import model.BuildingType;
import model.Faction;
import model.Status;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 20:55
 */
public class MockBuilding extends Building {
    public MockBuilding(double x, double y, BuildingType type, Faction faction) {
        this(Sequence.next(), x, y, 0, 0, 0, faction, type == BuildingType.FACTION_BASE ? 100 : 50, type == BuildingType.FACTION_BASE ? 1000 : 500, type == BuildingType.FACTION_BASE ? 1000 : 500, new Status[0], type, type == BuildingType.FACTION_BASE ? 800 : 600, type == BuildingType.FACTION_BASE ? 800 : 600, 35, 0, 0);
    }

    private MockBuilding(long id, double x, double y, double speedX, double speedY, double angle, Faction faction, double radius, int life, int maxLife, Status[] statuses, BuildingType type, double visionRange, double attackRange, int damage, int cooldownTicks, int remainingActionCooldownTicks) {
        super(id, x, y, speedX, speedY, angle, faction, radius, life, maxLife, statuses, type, visionRange, attackRange, damage, cooldownTicks, remainingActionCooldownTicks);
    }
}
