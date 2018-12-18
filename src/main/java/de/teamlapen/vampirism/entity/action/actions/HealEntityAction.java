package de.teamlapen.vampirism.entity.action.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityVampirism;

public class HealEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends DefaultEntityAction implements IInstantAction<T> {

    @Override
    public int getCooldown(int level) {
        return Balance.ea.HEAL_COOLDOWN * 20; // seconds into ticks
    }

    @Override
    public boolean activate(T entity) {
        System.out.println(entity.getRepresentingEntity().getHealth() + " - "); // TODO remove
        entity.getRepresentingEntity().heal(entity.getMaxHealth() / Balance.ea.HEAL_AMOUNT);
        System.out.print(entity.getRepresentingEntity().getHealth());// TODO remove
        return true;
    }

}
