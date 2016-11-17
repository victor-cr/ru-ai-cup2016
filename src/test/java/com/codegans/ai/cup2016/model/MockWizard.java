package com.codegans.ai.cup2016.model;

import model.Faction;
import model.Message;
import model.SkillType;
import model.Status;
import model.Wizard;

/**
 * JavaDoc here
 *
 * @author Victor Polischuk
 * @since 16.11.2016 20:55
 */
public class MockWizard extends Wizard {
    public MockWizard(double x, double y, Faction faction, boolean me) {
        this(Sequence.next(), x, y, 0, 0, 0, faction, 35, 100, 100, new Status[0], 0, me, 100, 100, 40, 40, 200, 1, new SkillType[0], 0, new int[0], true, new Message[0]);
    }

    private MockWizard(long id, double x, double y, double speedX, double speedY, double angle, Faction faction, double radius, int life, int maxLife, Status[] statuses, long ownerPlayerId, boolean me, int mana, int maxMana, double visionRange, double castRange, int xp, int level, SkillType[] skills, int remainingActionCooldownTicks, int[] remainingCooldownTicksByAction, boolean master, Message[] messages) {
        super(id, x, y, speedX, speedY, angle, faction, radius, life, maxLife, statuses, ownerPlayerId, me, mana, maxMana, visionRange, castRange, xp, level, skills, remainingActionCooldownTicks, remainingCooldownTicksByAction, master, messages);
    }
}
