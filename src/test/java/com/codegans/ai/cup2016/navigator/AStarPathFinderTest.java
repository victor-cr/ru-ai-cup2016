package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.model.MockBuilding;
import com.codegans.ai.cup2016.model.MockWizard;
import com.codegans.ai.cup2016.model.MockWorld;
import com.codegans.ai.cup2016.model.Point;
import model.BuildingType;
import model.Faction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 17:54
 */
class AStarPathFinderTest {
    @Test
    void testDirectPath_InCorner() {
        MockWizard self = new MockWizard(32, 32, Faction.ACADEMY, true);

        Point target = new Point(1995, 1995);
        MockWorld world = new MockWorld(4000, 4000).add(self);

        Collection<Point> expected = new ArrayList<>(Arrays.asList(new Point(self), new Point(1968, 1968)));
        Collection<Point> actual = PathFinder.aStar().traverse(GameMap.get(world), new Point(self), target, self.getRadius());

        assertEquals(expected, actual);
    }

    @Test
    void testDirectPath_ProximityCase() {
        MockWizard self = new MockWizard(513.475, 3742.149, Faction.ACADEMY, true);

        Point target = new Point(459.567, 3716.586);
        MockWorld world = new MockWorld(4000, 4000).add(self).add(new MockBuilding(400, 3600, BuildingType.FACTION_BASE, Faction.ACADEMY));

        Collection<Point> expected = new ArrayList<>(Arrays.asList(new Point(512, 3736), new Point(480, 3736)));
        Collection<Point> actual = PathFinder.aStar().traverse(GameMap.get(world), new Point(self), target, self.getRadius());

        assertEquals(expected, actual);
    }

    @Test
    void testNext_ProximityCase() {
        MockWizard self = new MockWizard(513.475, 3742.149, Faction.ACADEMY, true);

        Point target = new Point(459.567, 3716.586);
        MockWorld world = new MockWorld(4000, 4000).add(self).add(new MockBuilding(400, 3600, BuildingType.FACTION_BASE, Faction.ACADEMY));

        Point expected = new Point(480, 3736);
        Point actual = PathFinder.aStar().next(GameMap.get(world), new Point(self), target, self.getRadius());

        assertEquals(expected, actual);
    }
}
