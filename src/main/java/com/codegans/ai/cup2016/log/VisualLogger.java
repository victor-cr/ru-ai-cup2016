package com.codegans.ai.cup2016.log;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.model.Point;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javafx.scene.paint.Color.*;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2016 12:20
 */
public class VisualLogger implements Logger {
    private static final AtomicBoolean LAUNCHED = new AtomicBoolean(false);
    private static final CyclicBarrier BARRIER = new CyclicBarrier(2);
    private static final Thread VISUAL_THREAD = new Thread(() -> Application.launch(VisualWindow.class), "visual");
    private static volatile VisualWindow window;

    private final Map<Long, Circle> trees = new HashMap<>();
    private final Map<Long, Circle> towers = new HashMap<>();
    private long time = 0;

    public VisualLogger() {
        if (LAUNCHED.get() && !VISUAL_THREAD.isAlive()) {
            return;
        }

        if (!LAUNCHED.get()) {
            VISUAL_THREAD.setDaemon(true);
            VISUAL_THREAD.start();

            try {
                BARRIER.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }

            LAUNCHED.compareAndSet(false, true);
        }
    }

    @Override
    public void print(Object message) {
        System.out.print(message);
    }

    @Override
    public void printf(String pattern, Object... params) {
        try {
            System.out.printf(pattern, params);
        } catch (RuntimeException e) {
            //ignore
        }
    }

    @Override
    public void action(Action action) {
        System.out.printf("Perform action: %s%n", action);
    }

    @Override
    public void logPath(Collection<Point> path, Point nextTarget) {
        Platform.runLater(() -> {
            Canvas canvas = window.background;

            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.clearRect(10, 10, canvas.getWidth() - 10, canvas.getHeight() - 10);

            gc.setFill(BLACK);
            gc.setLineWidth(5);
            gc.strokePolyline(
                    path.stream().mapToDouble(e -> e.x).toArray(),
                    path.stream().mapToDouble(e -> e.y).toArray(),
                    path.size()
            );

            gc.setFill(BLACK);
            gc.setFont(Font.font(22));
            gc.strokeText("x", nextTarget.x, nextTarget.y);
        });
    }

    @Override
    public void logState(Wizard self, World world, Game game, Move move) {
        long current = System.currentTimeMillis();
        long delta = time == 0 ? 0 : current - time;

        time = current;

        System.out.printf("%n<%5d>---->%5d ms<----[%d]@(%.3f,%.3f)%n", world.getTickIndex(), delta, self.getLife(), self.getX(), self.getY());

        Collection<Circle> data = Stream.concat(
                Stream.concat(
                        Arrays.stream(world.getTrees())
                                .filter(e -> !trees.containsKey(e.getId()))
                                .map(e -> trees.computeIfAbsent(e.getId(), id -> new Circle(e.getX(), e.getY(), e.getRadius(), GREEN))),
                        Arrays.stream(world.getBuildings())
                                .filter(e -> !towers.containsKey(e.getId()))
                                .map(e -> towers.computeIfAbsent(e.getId(), id -> new Circle(e.getX(), e.getY(), e.getRadius(), GRAY)))

                ),
                Stream.concat(
                        Arrays.stream(world.getMinions()).map(e -> new Circle(e.getX(), e.getY(), e.getRadius(), BLUE)),
                        Arrays.stream(world.getWizards()).map(e -> new Circle(e.getX(), e.getY(), e.getRadius(), e.isMe() ? RED : BLACK))
                )
        ).collect(Collectors.toList());

        Platform.runLater(() -> {
            window.root.getChildren().removeAll(window.root.getChildren().stream()
                    .filter(e -> e instanceof Circle)
                    .map(e -> (Circle) e)
                    .filter(e -> e.getFill().equals(BLUE) || e.getFill().equals(RED) || e.getFill().equals(BLACK))
                    .collect(Collectors.toList()));
            window.root.getChildren().addAll(0, data);
        });
    }

    @Override
    public void logTarget(Point target, int tick) {
        trace(target);

        if (Double.compare(target.x, 0) >= 0 && Double.compare(target.y, 0) >= 0 && Double.compare(target.x, window.background.getWidth()) < 0 && Double.compare(target.y, window.background.getHeight()) < 0)
            Platform.runLater(() -> {
                window.root.getChildren().removeIf(e -> (e instanceof MyCircle) && ((MyCircle) e).tick != tick);
                window.root.getChildren().add(new MyCircle(target.x, target.y, 10, BROWN, tick));
            });
    }

    private static void trace(Point target) {
        Arrays.stream(Thread.currentThread().getStackTrace())
                .map(StackTraceElement::getClassName)
                .filter(e -> e.contains("Decision") || e.contains("Navigator"))
                .filter(e -> !e.contains("Abstract"))
                .limit(1).findFirst().filter(e -> e.contains("Decision")).ifPresent(e -> System.out.printf("%s's target: %s%n", e.substring(33), target));
    }

    public static final class VisualWindow extends Application {
        private volatile Group root;
        private volatile Canvas background;

        @Override
        public void start(Stage primaryStage) throws Exception {
            root = new Group();
            background = new Canvas(4000, 4000);

            root.getChildren().add(background);
            root.setScaleX(0.25);
            root.setScaleY(0.25);
            root.setTranslateX(0 - background.getWidth() * (1 - 0.25) / 2);
            root.setTranslateY(0 - background.getHeight() * (1 - 0.25) / 2);

            background.getGraphicsContext2D().setStroke(ROSYBROWN);
            background.getGraphicsContext2D().setLineWidth(10);
            background.getGraphicsContext2D().strokeRect(0, 0, background.getWidth(), background.getHeight());

            primaryStage.setScene(new Scene(root, 1000, 1000, Color.WHITE));
            primaryStage.show();

            window = this;

            BARRIER.await();
        }
    }

    private static class MyCircle extends Circle {
        private final int tick;

        private MyCircle(double centerX, double centerY, double radius, Paint fill, int tick) {
            super(centerX, centerY, radius, fill);
            this.tick = tick;
        }
    }
}
