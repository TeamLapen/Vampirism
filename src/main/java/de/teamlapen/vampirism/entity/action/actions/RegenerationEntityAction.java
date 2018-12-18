package de.teamlapen.vampirism.entity.action.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityVampirism;

public class RegenerationEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends DefaultEntityAction implements ILastingAction<T> {


    @Override
    public int getDuration(int level) {
        return Balance.ea.REGENERATION_DURATION;
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.REGENERATION_COOLDOWN;
    }

    @Override
    public void deactivate(T entity) {
    }

    @Override
    public boolean onUpdate(T entity) {
        System.out.println(entity.getRepresentingEntity().getHealth() + " - "); // TODO remove
        entity.getRepresentingEntity().heal(Balance.ea.REGENERATION_AMOUNT / (getDuration(entity.getLevel()) * 20)); // seconds in ticks
        System.out.print(entity.getRepresentingEntity().getHealth());// TODO remove
        return true;
    }

}
