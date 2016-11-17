package com.codegans.ai.cup2016.log;

import com.codegans.ai.cup2016.Navigator;
import com.codegans.ai.cup2016.action.Action;
import model.Wizard;
import model.World;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 19:56
 */
public class ConsoleLogger implements Logger {
    private volatile PrintStream out = System.out;
    private final PrintStream nullOut = new PrintStream(NullOutputStream.INSTANCE);

    @Override
    public void print(Object message) {
        out.print(message);
    }

    @Override
    public void printf(String pattern, Object... params) {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof Collection) {
                Collection<?> c = (Collection) params[i];

                if (!c.isEmpty()) {
                    c = c.stream()
                            .filter(e -> e instanceof Wizard)
                            .map(e -> (Wizard) e)
                            .map(e -> String.format("Wizard@(%.3f;%.3f)", e.getX(), e.getY()))
                            .collect(Collectors.toList());
                }

                if (!c.isEmpty()) {
                    params[i] = c;
                }
            }

            if (params[i] instanceof Wizard) {
                Wizard e = (Wizard) params[i];

                params[i] = String.format("Wizard@(%.3f;%.3f)", e.getX(), e.getY());
            }
        }

        out.printf(pattern, params);
    }

    @Override
    public void action(Action<?> action) {
        printf("Perform action: %s%n", action);
    }

    @Override
    public void wizard(Wizard wizard, Navigator navigator) {
        printf("%s%n", wizard);
    }

    @Override
    public void others(World world) {
        printf("All wizards: %s%n", Arrays.asList(world.getWizards()));
    }

    private static class NullOutputStream extends OutputStream {
        private static final NullOutputStream INSTANCE = new NullOutputStream();

        private NullOutputStream() {
        }

        public void write(int b) throws IOException {
        }
    }
}
