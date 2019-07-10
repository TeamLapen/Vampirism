package de.teamlapen.vampirism.entity.minions.commands;

import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.ai.DefendLordMinionGoal;
import net.minecraft.entity.ai.goal.TargetGoal;

public class DefendLordCommand extends DefaultMinionCommand {

    protected final IMinion m;
    protected final TargetGoal defend;
    protected final int aiPrio;

    /**
     * @param id
     * @param m
     * @param targetAiPrio Priority for the AI target task
     */
    public DefendLordCommand(int id, IMinion m, int targetAiPrio) {
        super(id, m);
        this.m = m;
        defend = new DefendLordMinionGoal(m);
        this.aiPrio = targetAiPrio;
    }

    @Override
    public int getMinU() {
        return 64;
    }

    @Override
    public int getMinV() {
        return 0;
    }

    @Override
    public String getUnlocalizedName() {
        return "minioncommand.vampirism.defendlord";
    }

    @Override
    public void onActivated() {
        minionEntity.targetSelector.addGoal(aiPrio, defend);

    }

    @Override
    public void onDeactivated() {
        minionEntity.targetSelector.removeGoal(defend);

    }

}