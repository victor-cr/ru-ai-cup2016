package com.codegans.ai.cup2016.decision;

import com.codegans.ai.cup2016.action.Action;
import com.codegans.ai.cup2016.action.LearnAction;
import com.codegans.ai.cup2016.log.Logger;
import com.codegans.ai.cup2016.log.LoggerFactory;
import com.codegans.ai.cup2016.navigator.GameMap;
import model.Game;
import model.Message;
import model.Move;
import model.SkillType;
import model.Wizard;
import model.World;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Stream;

import static model.SkillType.RANGE_BONUS_PASSIVE_1;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 14.11.2016 21:08
 */
public class LevelUpDecision extends AbstractMoveDecision {
    private static final Logger LOG = LoggerFactory.getLogger();

    private final Collection<SkillType> remaining = EnumSet.allOf(SkillType.class);
    private SkillType learnNext = RANGE_BONUS_PASSIVE_1;
    private int nextLevel = 1;

    @Override
    public Stream<Action> decide(Wizard self, World world, Game game, Move move) {
        int[] values = game.getLevelUpXpValues();

        if (!game.isSkillsEnabled() || values == null || values.length < nextLevel || self.getXp() < Arrays.stream(values).limit(nextLevel).sum()) {
            return Stream.empty();
        }

        GameMap map = GameMap.get(world);

        SkillType learnNow = Arrays.stream(self.getMessages()).map(Message::getSkillToLearn).filter(e -> e != null).findAny().orElse(learnNext);

        while (learnNow != null && !map.learnSkill(learnNow)) {
            learnNow = learnNext;
        }

        learnNext = null;

        if (learnNow == null) {
            LOG.printf("Weird!!! I have nothing to learn!!!%n");

            return Stream.empty();
        }

        remaining.remove(learnNow);

        LOG.printf("Level UP!!! I am %d level now!!! Learn new skill: %s%n", nextLevel, learnNow);

        nextLevel++;

        int i = map.skillBranch(learnNow);
        SkillType[][] skillsToLearn = map.unknownSkills();

        for (int j = 0; j < skillsToLearn[i].length; j++) {
            if (skillsToLearn[i][j] != null) {
                learnNext = skillsToLearn[i][j];
                break;
            }
        }

        if (learnNext == null && !remaining.isEmpty()) {
            learnNext = remaining.iterator().next();
        }

        return Stream.of(new LearnAction(ASAP, learnNow));
    }
}
