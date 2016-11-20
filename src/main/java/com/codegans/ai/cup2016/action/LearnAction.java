package com.codegans.ai.cup2016.action;

import model.Move;
import model.SkillType;

/**
 * JavaDoc here
 *
 * @author id967092
 * @since 18/11/2016 17:15
 */
public class LearnAction extends BaseAction {
    private final SkillType skillType;

    public LearnAction(int score, SkillType skillType) {
        super(score);

        this.skillType = skillType;
    }

    @Override
    public void apply(Move move) {
        move.setSkillToLearn(skillType);
    }

    @Override
    public String toString() {
        return super.toString() + "[" + skillType + "]";
    }
}
