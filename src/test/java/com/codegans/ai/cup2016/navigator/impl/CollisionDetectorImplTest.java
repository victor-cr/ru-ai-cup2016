package com.codegans.ai.cup2016.navigator.impl;

import com.codegans.ai.cup2016.model.MockTree;
import com.codegans.ai.cup2016.model.MockWorld;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 20:47
 */
class CollisionDetectorImplTest {
    @Test
    void testCanPass_Empty() {
        MockWorld world = new MockWorld(4000, 4000);

        assertTrue(GameMap.get(world).collisionDetector().full().canPass(new Point(50, 50), new Point(2000, 2000), 35));
    }

    @Test
    void testCanPass_BlockingTree() {
        MockWorld world = new MockWorld(4000, 4000).add(new MockTree(1000, 1000, 100));

        assertFalse(GameMap.get(world).collisionDetector().full().canPass(new Point(50, 50), new Point(2000, 2000), 35));
    }

    @Test
    void testCanPass_AdjacentTree() {
        MockWorld world = new MockWorld(4000, 4000).add(new MockTree(2020, 2020, 19));

        assertFalse(GameMap.get(world).collisionDetector().full().canPass(new Point(50, 50), new Point(2000, 2000), 35));
    }
}
