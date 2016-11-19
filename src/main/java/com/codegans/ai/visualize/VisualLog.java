package com.codegans.ai.visualize;

import com.codegans.ai.cup2016.model.Point;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.BLUE;
import static javafx.scene.paint.Color.GRAY;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.ROSYBROWN;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2016 12:20
 */
public class VisualLog extends Application {
    private static final AtomicBoolean LAUNCHED = new AtomicBoolean(false);
    private static final CyclicBarrier BARRIER = new CyclicBarrier(2);
    private static final Thread VISUAL_THREAD = new Thread(() -> {
        launch(VisualLog.class);
        System.out.println("Bye, bye");
    }, "visual");
    private static volatile VisualLog instance;

    private final Map<Long, Circle> trees = new HashMap<>();
    private final Map<Long, Circle> towers = new HashMap<>();

    private volatile Group root;
    private volatile Canvas foreground;


    public static void intercept(Wizard self, World world, Game game, Move move) {
        initialize();

        instance.listen(self, world, game, move);
    }

    public static void publishPath(Collection<Point> path, Point nextTarget) {
        initialize();

        Platform.runLater(() -> {
            Canvas canvas = instance.foreground;

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
            gc.strokeText("x", nextTarget.x, nextTarget.y);
        });
    }

    private static void initialize() {
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
    public void start(Stage primaryStage) throws Exception {
        root = new Group();
        foreground = new Canvas(4000, 4000);

        root.getChildren().add(foreground);
        root.setScaleX(0.25);
        root.setScaleY(0.25);
        root.setTranslateX(0 - foreground.getWidth() * (1 - 0.25) / 2);
        root.setTranslateY(0 - foreground.getHeight() * (1 - 0.25) / 2);

        foreground.getGraphicsContext2D().setStroke(ROSYBROWN);
        foreground.getGraphicsContext2D().setLineWidth(10);
        foreground.getGraphicsContext2D().strokeRect(0, 0, foreground.getWidth(), foreground.getHeight());

        primaryStage.setScene(new Scene(root, 1000, 1000, Color.WHITE));
        primaryStage.show();

        instance = this;

        BARRIER.await();
    }

    private void listen(Wizard self, World world, Game game, Move move) {
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
            root.getChildren().removeAll(root.getChildren().stream()
                    .filter(e -> e instanceof Circle)
                    .map(e -> (Circle) e)
                    .filter(e -> e.getFill().equals(BLUE) || e.getFill().equals(RED) || e.getFill().equals(BLACK))
                    .collect(Collectors.toList()));
            root.getChildren().addAll(0, data);
        });
    }
}
