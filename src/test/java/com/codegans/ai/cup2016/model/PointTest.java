package com.codegans.ai.cup2016.model;

import org.junit.jupiter.api.Test;

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
}
