package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.ActionType;
import model.Game;
import model.Move;
import model.Wizard;
import model.World;

import java.util.function.ToIntFunction;
import java.util.stream.Stream;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 20.11.2016 21:40
 */
public abstract class AbstractActionDecision extends AbstractDecision {
    private final ToIntFunction<Game> manaCost;
    protected final ActionType type;

    public AbstractActionDecision(int priority, ToIntFunction<Game> manaCost, ActionType type) {
        super(priority);

        this.manaCost = manaCost;
        this.type = type;
    }

    @Override
    public Stream<Action> decide(Wizard self, World world, Game game, Move move) {
        if (self.getRemainingActionCooldownTicks() != 0) {
            return Stream.empty();
        }

        if (self.getRemainingCooldownTicksByAction()[type.ordinal()] != 0) {
            return Stream.empty();
        }

        if (self.getMana() < manaCost.applyAsInt(game)) {
            return Stream.empty();
        }

        GameMap map = GameMap.get(world);

        return doActions(self, world, game, map);
    }

    protected abstract Stream<Action> doActions(Wizard self, World world, Game game, GameMap map);
}
