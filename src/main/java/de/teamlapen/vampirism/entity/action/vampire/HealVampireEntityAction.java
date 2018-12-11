package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.IVampirismEntity;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.config.Balance;

public class HealVampireEntityAction extends DefaultEntityAction implements IInstantAction<IVampireMob> {

    @Override
    public int getCooldown(int level) {
        return Balance.ea.VAMPIRE_HEAL_COOLDOWN * 20; // seconds into ticks
    }

    @Override
    public boolean activate(IVampireMob entity) {
        float healamount = entity.getRepresentingEntity().getMaxHealth() * Balance.ea.VAMPIRE_HEAL_AMOUNT / 100;
        entity.getRepresentingEntity().heal(healamount);
        return true;
    }

    @Override
    public IFaction getFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public void forceDeactivation(IVampirismEntity entity) {
        // TODO Auto-generated method stub

    }
}
