package com.codegans.ai.cup2016.model;

import model.Bonus;
import model.Building;
import model.Minion;
import model.Player;
import model.Projectile;
import model.Tree;
import model.Wizard;
import model.World;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 20:55
 */
public class MockWorld extends World {
    public MockWorld(double width, double height) {
        this(Sequence.next(), 0, width, height, new Player[0], new Wizard[0], new Minion[0], new Projectile[0], new Bonus[0], new Building[0], new Tree[0]);
    }

    private MockWorld(int tickIndex, int tickCount, double width, double height, Player[] players, Wizard[] wizards, Minion[] minions, Projectile[] projectiles, Bonus[] bonuses, Building[] buildings, Tree[] trees) {
        super(tickIndex, tickCount, width, height, players, wizards, minions, projectiles, bonuses, buildings, trees);
    }

    public MockWorld add(Wizard wizard) {
        return new MockWorld(getTickIndex(), getTickCount(), getWidth(), getHeight(), getPlayers(), Stream.concat(Arrays.stream(getWizards()), Stream.of(wizard)).toArray(Wizard[]::new), getMinions(), getProjectiles(), getBonuses(), getBuildings(), getTrees());
    }

    public MockWorld add(Tree tree) {
        return new MockWorld(getTickIndex(), getTickCount(), getWidth(), getHeight(), getPlayers(), getWizards(), getMinions(), getProjectiles(), getBonuses(), getBuildings(), Stream.concat(Arrays.stream(getTrees()), Stream.of(tree)).toArray(Tree[]::new));
    }

    public MockWorld add(Building tower) {
        return new MockWorld(getTickIndex(), getTickCount(), getWidth(), getHeight(), getPlayers(), getWizards(), getMinions(), getProjectiles(), getBonuses(), Stream.concat(Arrays.stream(getBuildings()), Stream.of(tower)).toArray(Building[]::new), getTrees());
    }
}
