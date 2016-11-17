package com.codegans.ai.cup2016.model;

import model.Faction;
import model.Status;
import model.Tree;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 20:55
 */
public class MockTree extends Tree {
    public MockTree(double x, double y, double radius) {
        this(Sequence.next(), x, y, 0, 0, 0, Faction.OTHER, radius, 100, 100, new Status[0]);
    }

    private MockTree(long id, double x, double y, double speedX, double speedY, double angle, Faction faction, double radius, int life, int maxLife, Status[] statuses) {
        super(id, x, y, speedX, speedY, angle, faction, radius, life, maxLife, statuses);
    }
}
