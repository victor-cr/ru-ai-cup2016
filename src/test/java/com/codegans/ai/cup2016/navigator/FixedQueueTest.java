package com.codegans.ai.cup2016.navigator;

import com.codegans.ai.cup2016.model.Point;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2016 14:43
 */
class FixedQueueTest {
    @Test
    void testOffer_Overflow() {
        FixedQueue<Point> queue = new FixedQueue<>(new Point[10]);

        IntStream.range(0, 12).forEach(e -> queue.offer(new Point(e, e)));

        assertAll(
                () -> assertEquals(new Point(2, 2), queue.head(0)),
                () -> assertEquals(new Point(3, 3), queue.head(1)),
                () -> assertEquals(new Point(4, 4), queue.head(2)),
                () -> assertEquals(new Point(5, 5), queue.head(3)),
                () -> assertEquals(new Point(6, 6), queue.head(4)),
                () -> assertEquals(new Point(7, 7), queue.head(5)),
                () -> assertEquals(new Point(8, 8), queue.head(6)),
                () -> assertEquals(new Point(9, 9), queue.head(7)),
                () -> assertEquals(new Point(10, 10), queue.head(8)),
                () -> assertEquals(new Point(11, 11), queue.head(9))
        );
    }
}
