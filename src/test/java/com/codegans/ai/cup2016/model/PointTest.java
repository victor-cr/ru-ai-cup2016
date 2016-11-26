package com.codegans.ai.cup2016.model;

import org.junit.jupiter.api.Test;

import static java.lang.StrictMath.sqrt;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 22/11/2016 00:18
 */
class PointTest {
    @Test
    void testPoint_SimpleMirror() {
        Point enemy = new Point(45, 45);
        Point base = new Point(100, 100);

        Point expected = new Point(155, 155);
        Point actual = enemy.reflectTo(base);

        assertEquals(expected, actual);
    }

    @Test
    void testPoint_ReverseSimpleMirror() {
        Point enemy = new Point(145, 145);
        Point base = new Point(100, 100);

        Point expected = new Point(55, 55);
        Point actual = enemy.reflectTo(base);

        assertEquals(expected, actual);
    }

    @Test
    void testMerge_One() {
        Point enemy = new Point(145, 145);

        Point expected = new Point(145, 145);
        Point actual = enemy.merge(enemy);

        assertEquals(expected, actual);
    }

    @Test
    void testMerge_Two() {
        Point enemy1 = new Point(145, 145);
        Point enemy2 = new Point(100, 145);

        Point expected = new Point(122.5, 145);
        Point actual = enemy1.merge(enemy2);

        assertEquals(expected, actual);
    }

    @Test
    void testShiftTo_TheSameX() {
        Point enemy = new Point(145, 10);
        Point me = new Point(145, 145);

        assertEquals(new Point(145, 155), me.shiftTo(enemy, 10));
        assertEquals(new Point(145, 135), me.shiftTo(enemy, -10));
    }

    @Test
    void testShiftTo_TheSameY() {
        Point enemy = new Point(10, 145);
        Point me = new Point(145, 145);

        assertEquals(new Point(155, 145), me.shiftTo(enemy, 10));
        assertEquals(new Point(135, 145), me.shiftTo(enemy, -10));
    }

    @Test
    void testShiftTo_45degrees() {
        Point enemy = new Point(10, 10);
        Point me = new Point(145, 145);

        assertEquals(new Point(145 + 10 / sqrt(2), 145 + 10 / sqrt(2)), me.shiftTo(enemy, 10));
        assertEquals(new Point(145 - 10 / sqrt(2), 145 - 10 / sqrt(2)), me.shiftTo(enemy, -10));
    }

    @Test
    void testShiftTo_Random() {
        Point enemy = new Point(123, 10);
        Point me = new Point(145, 321);

        assertEquals(new Point(145.706, 330.975), me.shiftTo(enemy, 10));
        assertEquals(new Point(144.294, 311.025), me.shiftTo(enemy, -10));
    }
}
