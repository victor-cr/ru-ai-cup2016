package com.codegans.ai.cup2016.log;

import com.codegans.ai.cup2016.Navigator;
import com.codegans.ai.cup2016.action.Action;
import model.Wizard;
import model.World;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 19.11.2015 19:56
 */
public class NullLogger implements Logger {
    @Override
    public void print(Object message) {
    }

    @Override
    public void printf(String pattern, Object... params) {
    }

    @Override
    public void action(Action<?> action) {
    }

    @Override
    public void wizard(Wizard wizard, Navigator navigator) {
    }

    @Override
    public void others(World world) {
    }
}
