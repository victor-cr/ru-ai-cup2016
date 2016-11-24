package com.codegans.ai.cup2016.navigator.impl;

import com.codegans.ai.cup2016.model.MockTree;
import com.codegans.ai.cup2016.model.MockWizard;
import com.codegans.ai.cup2016.model.MockWorld;
import com.codegans.ai.cup2016.model.Point;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Faction;
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

        assertTrue(GameMap.get(world).cd().canPass(new Point(50, 50), new Point(2000, 2000), 35));
    }

    @Test
    void testCanPass_NonBlockingTree() {
        MockWorld world = new MockWorld(4000, 4000).add(new MockTree(100 + (10 + 35) / StrictMath.sqrt(2), 100 - (10 + 35) / StrictMath.sqrt(2), 10));

        assertTrue(GameMap.get(world).cd().canPass(new Point(50, 50), new Point(2000, 2000), 35));
    }

    @Test
    void testCanPass_BlockingTree() {
        MockWorld world = new MockWorld(4000, 4000).add(new MockTree(100 + (10 + 34) / StrictMath.sqrt(2), 100 - (10 + 34) / StrictMath.sqrt(2), 10));

        assertFalse(GameMap.get(world).cd().canPass(new Point(50, 50), new Point(2000, 2000), 35));
    }

    @Test
    void testCanPass_AdjacentTree() {
        MockWorld world = new MockWorld(4000, 4000).add(new MockTree(2020, 2020, 19));

        assertFalse(GameMap.get(world).cd().canPass(new Point(50, 50), new Point(2000, 2000), 35));
    }

    @Test
    void testCanPass_Strange() {
        MockWorld world = new MockWorld(4000, 4000)
                .add(new MockWizard(786.0723948539099D, 3830.732383159212D, Faction.ACADEMY, false))
                .add(new MockWizard(803.1142797131D, 3762.0003986017596D, Faction.ACADEMY, false))
                .add(new MockWizard(189.2048868708348D, 3113.3570810929978D, Faction.ACADEMY, false))
                .add(new MockWizard(607.4976800404273D, 3599.5265739546326D, Faction.ACADEMY, false));

        assertFalse(GameMap.get(world).cd().canPass(new Point(712, 3816), new Point(1288, 3816), 35));
    }
}
