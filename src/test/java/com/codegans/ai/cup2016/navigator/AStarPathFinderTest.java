package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.model.MockWizard;
import com.codegans.ai.cup2016.model.MockWorld;
import com.codegans.ai.cup2016.model.Point;
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
        MockWizard self = new MockWizard(35, 35, Faction.ACADEMY, true);

        Point target = new Point(1995, 1995);
        MockWorld world = new MockWorld(4000, 4000).add(self);

        Collection<Point> expected = new ArrayList<>(Arrays.asList(new Point(self), target));
        Collection<Point> actual = PathFinder.aStar().traverse(GameMap.get(world).collisionDetector().full(), new Point(self), target, self.getRadius());

        assertEquals(expected, actual);
    }
}
