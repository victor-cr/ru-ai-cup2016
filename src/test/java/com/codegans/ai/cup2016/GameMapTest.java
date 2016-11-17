package com.codegans.ai.cup2016;

import com.codegans.ai.cup2016.model.MockTree;
import com.codegans.ai.cup2016.model.MockWizard;
import com.codegans.ai.cup2016.model.MockWorld;
import model.Faction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 20:47
 */
class GameMapTest {
    @Test
    void testEmptyMap() {
        MockWorld world = new MockWorld(4000, 4000).add(new MockWizard(100, 100, Faction.ACADEMY, true));

        GameMap map = GameMap.get(world);

        assertEquals(0, map.cardinality());
    }

    @Test
    void testSingleTree() {
        MockWorld world = new MockWorld(4000, 4000).add(new MockTree(100, 100, 1));

        GameMap map = GameMap.get(world);

        assertEquals(4, map.cardinality());
    }

    @Test
    void testDoubleTree() {
        MockWorld world = new MockWorld(4000, 4000).add(new MockTree(100, 100, 2));

        GameMap map = GameMap.get(world);

        assertEquals(16, map.cardinality());
    }

    @Test
    void testDoublePlusTree() {
        MockWorld world = new MockWorld(4000, 4000).add(new MockTree(100, 100, 2.1));

        GameMap map = GameMap.get(world);

        assertEquals(24, map.cardinality());
    }

    @Test
    void testDoublePlusPlusTree() {
        MockWorld world = new MockWorld(4000, 4000).add(new MockTree(100, 100, 2.4));

        GameMap map = GameMap.get(world);

        assertEquals(32, map.cardinality());
    }

    @Test
    void testTripleTree() {
        MockWorld world = new MockWorld(4000, 4000).add(new MockTree(100, 100, 3));

        GameMap map = GameMap.get(world);

        assertEquals(36, map.cardinality());
    }
}
